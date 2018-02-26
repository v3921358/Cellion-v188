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
import net.Packet;
import scripting.provider.NPCScriptManager;
import server.LoginAuthorization;
import server.maps.objects.MapleCharacter;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.JobPacket.AvengerPacket;
import netty.ProcessPacket;

public final class MigrateInHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.DecodeInteger();
        final int playerid = iPacket.DecodeInteger();
        iPacket.Skip(18); // 00 00 00 00 00 00 45 9A 4E C8 00 00 00 00 C0 17 00 00 00 00 00 00 00 00 00 00
        final long loginAuthCookie = iPacket.DecodeLong();

        boolean fromTransfer = false;
        MapleCharacter player;

        CharacterTransfer CashShopTransition = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        if (CashShopTransition != null) {
            //c.write(BuffPacket.cancelBuff());
            CashShopOperation.EnterCS(CashShopTransition, c);
            return;
        }
        CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(playerid);
        if (farmtransfer != null) {
            FarmOperation.EnterFarm(farmtransfer, c);
            return;
        }
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            CashShopTransition = cserv.getPlayerStorage().getPendingCharacter(playerid);
            if (CashShopTransition != null) {
                c.setChannel(cserv.getChannel());
                break;
            }
        }

        if (CashShopTransition == null) { // Player isn't in storage, probably isn't CC
            LoginAuthorization ip = LoginServer.getLoginAuth(playerid);
            String s = c.getSessionIPAddress();
            if (ip == null || !s.substring(s.indexOf('/') + 1, s.length()).equals(ip.getIPAddress())) {
                if (ip != null) {
                    LoginServer.putLoginAuth(playerid, ip.getIPAddress(), ip.getTempIP(), ip.getChannel(), 0);
                }
                c.close();
                return;
            }
            c.setTempIP(ip.getIPAddress());
            c.setChannel(ip.getChannel());
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        } else {
            fromTransfer = true;
            player = MapleCharacter.reconstructCharacter(CashShopTransition, c, true);
        }
        final ChannelServer channelServer = c.getChannelServer();
        c.setPlayer(player);
        c.setAccID(player.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.close();
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
            c.close();
            return;
        }

        c.updateLoginState(MapleClient.MapleClientLoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);

        // Writes the wrap to map packet first.
        // otherwise certain packets specific to the map will not work if it is being sent late.
        c.write(CField.getWarpToMap(player, null, 0, true));

        // Add the player to the map.
        player.getMap().addPlayer(player);

        // Others. The orders of how these are placed should be based on its priority
        // Such as clones, pets which may not really affect the player if should an exception occur should be placed last.
        // Or stuff that may not possibility cause exception could be placed on top with higher priority too
        try {
            World.WorldBuddy.loggedOn(player.getName(), player.getId(), c.getChannel(), player.getBuddylist().getBuddyIds());

            final MapleParty party = player.getParty();
            if (party != null) {
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

                if (party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        c.write(CWvsContext.ExpeditionPacket.expeditionStatus(me, false, true));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), player.getBuddylist().getBuddyIds());
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            Buddy buddy = new Buddy(BuddyResult.LOAD_FRIENDS);
            buddy.setEntries(new ArrayList<>(player.getBuddylist().getBuddies()));
            c.write(CWvsContext.buddylistMessage(buddy));
            c.write(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_MESSENGER_MODE)));
            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.write(GuildPacket.loadGuild_Done(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<Packet> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (Packet pack : packetList) {
                            if (pack != null) {
                                c.write(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            //c.write(FamilyPacket.getFamilyData());
            //c.write(FamilyPacket.getFamilyInfo(player));

            // Buffs
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
        } catch (Exception e) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an exception with loading a player:\n{}", e);
        }

        // Keymaps
        player.sendMacros();
        c.write(CField.getKeymap(player.getKeyLayout(), player.getJob()));

        // Server message
        player.getClient().write(CWvsContext.broadcastMsg(channelServer.getServerMessage()));

        // Skills
        player.baseSkills(); //fix people who've lost skills.
        player.updateHyperSPAmount();
        c.write(CWvsContext.updateSkills(c.getPlayer().getSkills(), false));//skill to 0 "fix"
        //c.write(JobPacket.addStolenSkill());

        // Pendant expansion
        MapleQuestStatus quest_pendant = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        c.write(CWvsContext.pendantExpansionAvailable(
                quest_pendant != null && quest_pendant.getCustomData() != null && Long.parseLong(quest_pendant.getCustomData()) > System.currentTimeMillis()));

        // Zero items, equipments
        if (!GameConstants.isZero(player.getJob())) { //tell all players 2 login so u can remove this from ther
            Equip a = (Equip) player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (a != null) {
                if (GameConstants.getWeaponType(a.getItemId()) == MapleWeaponType.LAZULI) {
                    player.getInventory(MapleInventoryType.EQUIPPED).removeItem((short) -11);
                }
            }
            Equip b = (Equip) player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
            if (b != null) {
                if (GameConstants.getWeaponType(b.getItemId()) == MapleWeaponType.LAPIS) {
                    player.getInventory(MapleInventoryType.EQUIPPED).removeItem((short) -10);
                }
            }
        }
        player.expirationTask(true, CashShopTransition == null);

        // Job specific stuff.
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        } else if (GameConstants.isXenon(player.getJob())) {
            player.startXenonSupply();
        } else if (GameConstants.isDemonAvenger(player.getJob())) {
            c.write(AvengerPacket.giveAvengerHpBuff(player.getStat().getHp()));
        } else if (GameConstants.isLuminous(player.getJob())) {
            player.applyLifeTidal();
        }

        // Quickslots
        MapleQuestStatus quest_quickSlot = player.getQuestNoAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT));
        c.write(CField.quickSlot(quest_quickSlot != null && quest_quickSlot.getCustomData() != null ? quest_quickSlot.getCustomData() : null));

        // Pets
        player.updatePetAuto();
        player.spawnSavedPets();
        player.sendImp();

        // Etc
        player.showNote();
        player.updateReward();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);

        // Bloodless Channel Notification
        if (ServerConstants.BUFFED_SYSTEM && ServerConstants.BLOODLESS_EVENT && (player.getClient().getChannel() >= ServerConstants.START_RANGE && player.getClient().getChannel() <= ServerConstants.END_RANGE)) {
            player.dropMessage(-1, "You have entered a Bloodless Channel (Hard Mode)!");
        }
        
        // Developer Skill Cooldown Toggle
        if (ServerConstants.DEV_DEFAULT_CD && player.isDeveloper()) {
            player.toggleCooldown();
            player.yellowMessage("[Debug] Skill cooldowns are toggled off by default due to your developer account status.");
            player.dropMessage(6, "[Reminder] You can type !togglecooldown to enable skill cooldowns.");
        }
        
        // Game Master Quality of Life Features
        if (player.isGM()) {
            c.getPlayer().dropMessage(5, "[" + ServerConstants.SERVER_NAME + " Stealth] Your character is currently hidden.");
            SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer());
        }

        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon + (GameConstants.getBeginnerJob(player.getJob()) * 1000)).getEffect(1).applyTo(player);
        }

        if (c.getPlayer().getLevel() < 11 && ServerConstants.RED_EVENT_10) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginTot");
        } else if (c.getPlayer().getLevel() > 10 && ServerConstants.RED_EVENT) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginRed");
        }
        c.write(CWvsContext.updateCrowns(new int[]{-1, -1, -1, -1, -1}));

        c.write(CWvsContext.getFamiliarInfo(player));
        c.write(CWvsContext.shopDiscount(ServerConstants.SHOP_DISCOUNT));
    }
}
