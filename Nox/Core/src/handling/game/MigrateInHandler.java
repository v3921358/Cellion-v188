package handling.game;

import java.util.List;

import client.ClientSocket;
import client.ClientSocket.MapleClientLoginState;
import client.QuestStatus;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import handling.cashshop.CashShopOperation;
import handling.farm.FarmOperation;
import client.jobs.Resistance.BlasterHandler;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.PlayerBuffStorage;
import handling.world.World;
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
import server.quest.Quest;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.WvsContext.GuildPacket;
import tools.packet.JobPacket.AvengerPacket;
import client.jobs.Hero.PhantomHandler;
import net.ProcessPacket;

/**
 * MigrateIn 
 * @author Mazen Massoud
 * @author Novak
 */
public final class MigrateInHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.DecodeInt();
        final int nPlayerID = iPacket.DecodeInt();
        iPacket.Skip(18); // Reference: 00 00 00 00 00 00 45 9A 4E C8 00 00 00 00 C0 17 00 00 00 00 00 00 00 00 00 00
        final long nLoginAuthCookie = iPacket.DecodeLong();
        boolean bFromTransfer = false;
        User pPlayer;

        CharacterTransfer pCashShopTransfer = CashShopServer.getPlayerStorage().getPendingCharacter(nPlayerID);
        if (pCashShopTransfer != null) {
            CashShopOperation.EnterCS(pCashShopTransfer, c);
            return;
        }
        CharacterTransfer pFarmTransfer = FarmServer.getPlayerStorage().getPendingCharacter(nPlayerID);
        if (pFarmTransfer != null) {
            FarmOperation.EnterFarm(pFarmTransfer, c);
            return;
        }
        for (ChannelServer pChannels : ChannelServer.getAllInstances()) {
            pCashShopTransfer = pChannels.getPlayerStorage().getPendingCharacter(nPlayerID);
            if (pCashShopTransfer != null) {
                c.setChannel(pChannels.getChannel());
                break;
            }
        }

        if (pCashShopTransfer == null) { // Player isn't in storage, probably isn't CC.
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
            pPlayer = User.reconstructCharacter(pCashShopTransfer, c, true);
        }
        
        final ChannelServer pChannelServer = c.getChannelServer();
        c.setPlayer(pPlayer);
        c.setAccID(pPlayer.getAccountID());

        if (!c.CheckIPAddress()) { // Remote Hack
            c.Close();
            return;
        }
        
        final MapleClientLoginState pState = c.getLoginState();
        boolean bAllowLogin = true;

        switch (pState) {
            case Login_ServerTransition:
            case ChangeChannel:
            case Login_NotLoggedIn: {
                bAllowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
                break;
            }
        }
        
        if (!bAllowLogin) {
            c.setPlayer(null);
            c.Close();
            return;
        }

        c.updateLoginState(ClientSocket.MapleClientLoginState.Login_LoggedIn, c.getSessionIPAddress());
        pChannelServer.addPlayer(pPlayer);

        // Writes the wrap to map packet first.
        // otherwise certain packets specific to the map will not work if it is being sent late.
        c.SendPacket(CField.getWarpToMap(pPlayer, null, 0, true));

        // Add the player to the map.
        pPlayer.getMap().addPlayer(pPlayer);

        // Others. The orders of how these are placed should be based on its priority
        // Such as clones, pets which may not really affect the player if should an exception occur should be placed last.
        // Or stuff that may not possibility cause exception could be placed on top with higher priority too
        try {
            // Buddy List
            pPlayer.OnlineBuddyListRequest();
            
            // Messenger
            final MapleMessenger messenger = pPlayer.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }

            // Guild and Alliance
            if (pPlayer.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(pPlayer.getMGC(), true, c.getChannel());
                c.SendPacket(GuildPacket.loadGuild_Done(pPlayer));
                final MapleGuild pGuild = World.Guild.getGuild(pPlayer.getGuildId());
                if (pGuild != null) {
                    final List<OutPacket> aPacketList = World.Alliance.getAllianceInfo(pGuild.getAllianceId(), true);
                    if (aPacketList != null) {
                        for (OutPacket oPacket : aPacketList) {
                            if (oPacket != null) {
                                c.SendPacket(oPacket);
                            }
                        }
                    }
                } else { // Guild Not Found -> Change Guild ID
                    pPlayer.setGuildId(0);
                    pPlayer.setGuildRank((byte) 5);
                    pPlayer.setAllianceRank((byte) 5);
                    pPlayer.saveGuildStatus();
                }
            }
            if (pPlayer.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(pPlayer.getMFC(), true, c.getChannel());
            }
            
            //c.SendPacket(FamilyPacket.getFamilyData());
            //c.SendPacket(FamilyPacket.getFamilyInfo(player));

            // Buffs
            pPlayer.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(pPlayer.getId()));
            pPlayer.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(pPlayer.getId()));
            pPlayer.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(pPlayer.getId()));
        } catch (Exception e) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an exception with loading a player:\n{}", e);
        }

        // Key Maps
        pPlayer.sendMacros();
        c.SendPacket(CField.getKeymap(pPlayer.getKeyLayout(), pPlayer.getJob()));

        // Server Message
        pPlayer.getClient().SendPacket(WvsContext.broadcastMsg(pChannelServer.getServerMessage()));

        // Skills
        pPlayer.baseSkills(); //fix people who've lost skills.
        pPlayer.updateHyperSPAmount();
        c.SendPacket(WvsContext.updateSkills(c.getPlayer().getSkills(), false));//skill to 0 "fix"
        //c.write(JobPacket.addStolenSkill());

        // Pendant Slot Expansion
        QuestStatus pPendantSlot = pPlayer.getQuestNoAdd(Quest.getInstance(GameConstants.PENDANT_SLOT));
        c.SendPacket(WvsContext.pendantExpansionAvailable(pPendantSlot != null && pPendantSlot.getCustomData() != null && Long.parseLong(pPendantSlot.getCustomData()) > System.currentTimeMillis()));

        pPlayer.expirationTask(true, pCashShopTransfer == null);

        // Job Specifics
        if (GameConstants.isWarriorDarkKnight(pPlayer.getJob())) {
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

        // Quick Slots
        QuestStatus pQuickSlot = pPlayer.getQuestNoAdd(Quest.getInstance(GameConstants.QUICK_SLOT));
        c.SendPacket(CField.quickSlot(pQuickSlot != null && pQuickSlot.getCustomData() != null ? pQuickSlot.getCustomData() : null));

        // Pets
        pPlayer.updatePetAuto();
        pPlayer.spawnSavedPets();
        pPlayer.sendImp();

        // Miscellaneous
        pPlayer.showNote();
        pPlayer.updateReward();
        pPlayer.updatePartyMemberHP();
        pPlayer.startFairySchedule(false);
        c.SendPacket(WvsContext.updateCrowns(new int[]{-1, -1, -1, -1, -1}));
        c.SendPacket(WvsContext.getFamiliarInfo(pPlayer));
        c.SendPacket(WvsContext.shopDiscount(ServerConstants.SHOP_DISCOUNT));
        
        // Developer Skill Cooldown Toggle
        if (ServerConstants.DEV_DEFAULT_CD && pPlayer.isDeveloper()) {
            pPlayer.toggleCooldown();
            pPlayer.yellowMessage("[Debug] Skill cooldowns are toggled off by default due to developer status.");
            pPlayer.dropMessage(6, "[Reminder] You can type !togglecooldown to enable skill cooldowns.");
        }

        // Game Master Quality of Life Features
        if (pPlayer.isGM()) {
            if (pPlayer.getGMLevel() > 5) pPlayer.setGM((byte) 5); // Reset Command Vault & Keep GM Levels Organized
            pPlayer.gainMeso((9999999999L - pPlayer.getMeso()), false); // Maximum Mesos
            pPlayer.setNX(2100000000); // Maximum NX
            pPlayer.toggleGodMode(true); // Enabled God Mode
            pPlayer.dropMessage(6, "[Reminder] God mode has been enabled by default.");
            pPlayer.dropMessage(5, "[Visibility] Your character is currently hidden.");
            SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer()); // Enabled Hide Mode
        }

        if (pPlayer.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(pPlayer.getStat().equippedSummon + (GameConstants.getBeginnerJob(pPlayer.getJob()) * 1000)).getEffect(1).applyTo(pPlayer);
        }

        if (c.getPlayer().getLevel() < 11 && ServerConstants.RED_EVENT_10) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginTot");
        } else if (c.getPlayer().getLevel() > 10 && ServerConstants.RED_EVENT) {
            NPCScriptManager.getInstance().start(c, 9000108, "LoginRed");
        }
    }
}
