package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import client.MapleClient.MapleClientLoginState;
import client.MapleQuestStatus;
import client.MapleSpecialStats;
import client.MapleSpecialStats.MapleSpecialStatUpdateType;
import client.SkillFactory;
import client.buddy.Buddy;
import client.buddy.BuddyResult;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import constants.GameConstants;
import constants.ServerConstants;
import handling.cashshop.CashShopOperation;
import handling.farm.FarmOperation;
import client.jobs.Hero;
import client.jobs.Hero.PhantomHandler;
import client.jobs.Resistance;
import client.jobs.Resistance.BlasterHandler;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.MapleExpedition;
import handling.world.MapleGuild;
import service.CashShopServer;
import service.ChannelServer;
import service.FarmServer;
import service.LoginServer;
import net.InPacket;
import net.OutPacket;

import scripting.provider.NPCScriptManager;
import server.LoginAuthorization;
import server.maps.objects.User;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.JobPacket.AvengerPacket;
import net.ProcessPacket;

public final class MigrateInHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.DecodeInt();
        final int nPlayerID = iPacket.DecodeInt();
        iPacket.Skip(18); // 00 00 00 00 00 00 45 9A 4E C8 00 00 00 00 C0 17 00 00 00 00 00 00 00 00 00 00
        final long nLoginAuthCookie = iPacket.DecodeLong();

        boolean bFromTransfer = false;
        User pPlayer;

        CharacterTransfer CashShopTransition = CashShopServer.getPlayerStorage().getPendingCharacter(nPlayerID);
        if (CashShopTransition != null) {
            //c.write(BuffPacket.cancelBuff());
            CashShopOperation.EnterCS(CashShopTransition, c);
            return;
        }
        CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(nPlayerID);
        if (farmtransfer != null) {
            FarmOperation.EnterFarm(farmtransfer, c);
            return;
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            CashShopTransition = cserv.getPlayerStorage().getPendingCharacter(nPlayerID);
            if (CashShopTransition != null) {
                c.setChannel(cserv.getChannel());
                break;
            }
        }

        if (CashShopTransition == null) { // Player isn't in storage, probably isn't CC
            LoginAuthorization ip = LoginServer.getLoginAuth(nPlayerID);
            String s = c.getSessionIPAddress();
            if (ip == null || !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.getIPAddress())) {
                if (ip != null) {
                    LoginServer.putLoginAuth(nPlayerID, ip.getIPAddress(), ip.getTempIP(), ip.getChannel(), 0);
                }
                c.Close();
                return;
            }
            c.setTempIP(ip.getIPAddress());
            c.setChannel(ip.getChannel());
            pPlayer = User.loadCharFromDB(nPlayerID, c, true);
        } else {
            bFromTransfer = true;
            pPlayer = User.reconstructCharacter(CashShopTransition, c, true);
        }
        final ChannelServer channelServer = c.getChannelServer();
        c.setPlayer(pPlayer);
        c.setAccID(pPlayer.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.Close();
            return;
        }
        final MapleClientLoginState state = c.getLoginState();
        boolean allowLogin = true;

        switch (state) {
            case LOGIN_SERVER_TRANSITION:
            case CHANGE_CHANNEL:
            case LOGIN_NOTLOGGEDIN: {
                allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
                break;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.Close();
            return;
        }

        c.updateLoginState(MapleClient.MapleClientLoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(pPlayer);

        // Writes the wrap to map packet first.
        // otherwise certain packets specific to the map will not work if it is being sent late.
        c.SendPacket(CField.getWarpToMap(pPlayer, null, 0, true));

        // Add the player to the map.
        pPlayer.getMap().addPlayer(pPlayer);

        // Others. The orders of how these are placed should be based on its priority
        // Such as clones, pets which may not really affect the player if should an exception occur should be placed last.
        // Or stuff that may not possibility cause exception could be placed on top with higher priority too
        try {
            World.WorldBuddy.loggedOn(pPlayer.getName(), pPlayer.getId(), c.getChannel(), pPlayer.getBuddylist().getBuddyIds());

            final MapleParty party = pPlayer.getParty();
            if (party != null) {
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(pPlayer));

                if (party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.SendPacket(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(pPlayer.getId(), pPlayer.getBuddylist().getBuddyIds());
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                pPlayer.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            Buddy buddy = new Buddy(BuddyResult.LOAD_FRIENDS);
            buddy.setEntries(new ArrayList<>(pPlayer.getBuddylist().getBuddies()));
            c.SendPacket(CWvsContext.buddylistMessage(buddy));
            c.SendPacket(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_MESSENGER_MODE)));
            // Start of Messenger
            final MapleMessenger messenger = pPlayer.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (pPlayer.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(pPlayer.getMGC(), true, c.getChannel());
                c.SendPacket(GuildPacket.loadGuild_Done(pPlayer));
                final MapleGuild gs = World.Guild.getGuild(pPlayer.getGuildId());
                if (gs != null) {
                    final List<OutPacket> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (OutPacket pack : packetList) {
                            if (pack != null) {
                                c.SendPacket(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    pPlayer.setGuildId(0);
                    pPlayer.setGuildRank((byte) 5);
                    pPlayer.setAllianceRank((byte) 5);
                    pPlayer.saveGuildStatus();
                }
            }
            if (pPlayer.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(pPlayer.getMFC(), true, c.getChannel());
            }
            //c.write(FamilyPacket.getFamilyData());
            //c.write(FamilyPacket.getFamilyInfo(player));

            // Buffs
            pPlayer.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(pPlayer.getId()));
            pPlayer.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(pPlayer.getId()));
            pPlayer.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(pPlayer.getId()));
        } catch (Exception e) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an exception with loading a player:\n{}", e);
        }

        // Keymaps
        pPlayer.sendMacros();
        c.SendPacket(CField.getKeymap(pPlayer.getKeyLayout(), pPlayer.getJob()));

        // Server message
        pPlayer.getClient().SendPacket(CWvsContext.broadcastMsg(channelServer.getServerMessage()));

        // Skills
        pPlayer.baseSkills(); //fix people who've lost skills.
        pPlayer.updateHyperSPAmount();
        c.SendPacket(CWvsContext.updateSkills(c.getPlayer().getSkills(), false));//skill to 0 "fix"
        //c.write(JobPacket.addStolenSkill());

        // Pendant expansion
        MapleQuestStatus quest_pendant = pPlayer.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        c.SendPacket(CWvsContext.pendantExpansionAvailable(
                quest_pendant != null && quest_pendant.getCustomData() != null && Long.parseLong(quest_pendant.getCustomData()) > System.currentTimeMillis()));

        // Zero items, equipments
        if (!GameConstants.isZero(pPlayer.getJob())) { //tell all players 2 login so u can remove this from ther
            Equip a = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (a != null) {
                if (GameConstants.getWeaponType(a.getItemId()) == MapleWeaponType.LAZULI) {
                    pPlayer.getInventory(MapleInventoryType.EQUIPPED).removeItem((short) -11);
                }
            }
            Equip b = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
            if (b != null) {
                if (GameConstants.getWeaponType(b.getItemId()) == MapleWeaponType.LAPIS) {
                    pPlayer.getInventory(MapleInventoryType.EQUIPPED).removeItem((short) -10);
                }
            }
        }
        pPlayer.expirationTask(true, CashShopTransition == null);

        // Job specific stuff.
        if (pPlayer.getJob() == 132) { // DARKKNIGHT
            pPlayer.checkBerserk();
        } else if (GameConstants.isXenon(pPlayer.getJob())) {
            pPlayer.startXenonSupply();
        } else if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
            c.SendPacket(AvengerPacket.giveAvengerHpBuff(pPlayer.getStat().getHp()));
        } else if (GameConstants.isLuminous(pPlayer.getJob())) {
            pPlayer.applyLifeTidal();
        } else if (GameConstants.isBlaster(pPlayer.getJob())) {
            BlasterHandler.enterCylinderState(pPlayer);
        } else if (GameConstants.isPhantom(pPlayer.getJob())) {
            PhantomHandler.updateDeckRequest(pPlayer, 0);
        }

        // Quickslots
        MapleQuestStatus quest_quickSlot = pPlayer.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));
        c.SendPacket(CField.quickSlot(quest_quickSlot != null && quest_quickSlot.getCustomData() != null ? quest_quickSlot.getCustomData() : null));

        // Pets
        pPlayer.updatePetAuto();
        pPlayer.spawnSavedPets();
        pPlayer.sendImp();

        // Etc
        pPlayer.showNote();
        pPlayer.updateReward();
        pPlayer.updatePartyMemberHP();
        pPlayer.startFairySchedule(false);

        // Bloodless Channel Notification
        if (ServerConstants.BUFFED_SYSTEM && ServerConstants.BLOODLESS_EVENT && (pPlayer.getClient().getChannel() >= ServerConstants.START_RANGE && pPlayer.getClient().getChannel() <= ServerConstants.END_RANGE)) {
            pPlayer.dropMessage(-1, "You have entered a Bloodless Channel (Hard Mode)!");
        }

        // Developer Skill Cooldown Toggle
        if (ServerConstants.DEV_DEFAULT_CD && pPlayer.isDeveloper()) {
            pPlayer.toggleCooldown();
            pPlayer.yellowMessage("[Debug] Skill cooldowns are toggled off by default due to developer status.");
            pPlayer.dropMessage(6, "[Reminder] You can type !togglecooldown to enable skill cooldowns.");
        }

        // Game Master Quality of Life Features
        if (pPlayer.isGM()) {
            pPlayer.gainMeso(2100000000, true);
            pPlayer.toggleGodMode(true);
            pPlayer.dropMessage(6, "[Reminder] God mode has been enabled by default.");
            pPlayer.dropMessage(5, "[" + ServerConstants.SERVER_NAME + " Stealth] Your character is currently hidden.");
            SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer());
        }

        if (pPlayer.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(pPlayer.getStat().equippedSummon + (GameConstants.getBeginnerJob(pPlayer.getJob()) * 1000)).getEffect(1).applyTo(pPlayer);
        }

        if (c.getPlayer().getLevel() < 11 && ServerConstants.RED_EVENT_10) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginTot");
        } else if (c.getPlayer().getLevel() > 10 && ServerConstants.RED_EVENT) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginRed");
        }
        c.SendPacket(CWvsContext.updateCrowns(new int[]{-1, -1, -1, -1, -1}));

        c.SendPacket(CWvsContext.getFamiliarInfo(pPlayer));
        c.SendPacket(CWvsContext.shopDiscount(ServerConstants.SHOP_DISCOUNT));
    }
}
