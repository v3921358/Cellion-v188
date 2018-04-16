package server.commands;

import java.util.Arrays;

import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import database.Database;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import service.ChannelServer;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import server.maps.objects.User;
import tools.LogHelper;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

/*
 * Cellion Player Commands
 *
 * @author Mazen
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class Dispose extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.SendPacket(CWvsContext.enableActions());
            c.getPlayer().dropMessage(5, "Your characters actions have been enabled.");
            return 1;
        }
    }

    public static class PinkZak extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final EventManager eem = c.getChannelServer().getEventSM().getEventManager("PinkZakumEntrance");
            final EventInstanceManager eim = eem.getInstance(("PinkZakumEntrance"));
            if (eem.getProperty("entryPossible").equals("true")) {
                NPCScriptManager.getInstance().start(c, 9201160, null);
            }
            if (eem.getProperty("entryPossible").equals("false")) {
                c.getPlayer().dropMessage(5, "Entry to the [Pink Zakum] Event is currently closed please wait patiently for the next entrance!");
            }
            return 1;
        }
    }

    public static class ExpFix extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
            return 1;
        }
    }

    public static class ResetExp extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            return 1;
        }
    }

    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public static class Hair extends DistributeStatCommands {

        public Hair() {
            stat = MapleStat.HAIR;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static final int statLim = 5000;
        private static final int hpMpLim = 500000;

        private void setStat(User player, int current, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) (current + amount), player);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) (current + amount), player);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) (current + amount), player);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) (current + amount), player);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
                case MAXHP:
                    long maxhp = Math.min(500000, Math.abs(current + amount * 30));
                    player.getStat().setMaxHp((short) (current + amount * 30), player);
                    player.getStat().setMaxHp((short) maxhp, player);
                    player.updateSingleStat(MapleStat.HP, player.getStat().getHp());
                    break;
                case IndieMMP:
                    long maxmp = Math.min(500000, Math.abs(current + amount));
                    player.getStat().setMaxMp((short) maxmp, player);
                    player.updateSingleStat(MapleStat.MP, player.getStat().getMp());
                    break;
                case HAIR:
                    int hair = amount;
                    player.setZeroBetaHair(hair);
                    player.updateSingleStat(MapleStat.HAIR, player.getZeroBetaHair());
                    break;
            }
        }

        private int getStat(User player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                case MAXHP:
                    return player.getStat().getMaxHp();
                case IndieMMP:
                    return player.getStat().getMaxMp();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            int change;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            int hpUsed = 0;
            int mpUsed = 0;
            if (stat == MapleStat.IndieMMP) {
                mpUsed = change;
                short job = c.getPlayer().getJob();
                if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
                    c.getPlayer().dropMessage(5, "You cannot raise MP.");
                    return 0;
                }
                change *= GameConstants.getMpApByJob(job);
            }

            if (change <= 0) {
                c.getPlayer().dropMessage(5, "You don't have enough AP Resets for that.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "You don't have enough AP for that.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > hpMpLim && (stat == MapleStat.MAXHP || stat == MapleStat.IndieMMP)) {
                c.getPlayer().dropMessage(5, "The stat limit is " + hpMpLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()), change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + hpUsed));
            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + mpUsed));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            if (stat == MapleStat.MAXHP) {
                c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change * 30 + ".");
                c.getPlayer().fakeRelog();
            } else {
                c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            }
            return 1;
        }
    }

    public static class Monster extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Mob pMob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                pMob = (Mob) monstermo;
                if (pMob.isAlive()) {
                    c.getPlayer().dropMessage(6, "Monster " + pMob.toString());
                    break; //only one
                }
            }
            if (pMob == null) {
                c.getPlayer().dropMessage(6, "Sorry, no monster was found nearby.");
            }
            return 1;
        }
    }

    public static class Save extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
            c.getPlayer().saveToDB(false, false);
            return 1;
        }
    }

    public static class Online extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int nSize = 0;
            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                nSize += ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size();
            }

            String sMessage = "#d" + ServerConstants.SERVER_NAME + " MapleStory Server#k\r\n"
                    + "There are currently #e#r" + nSize + "#k#n user(s) online.\r\n\r\n#r";

            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                sMessage += ChannelServer.getInstance(i).getPlayerStorage().formatOnlinePlayers(true);
            }

            c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sMessage, NPCChatByType.NPC_Cancellable));
            return 1;
        }
    }

    public static class FM extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }

            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }

            if (c.getPlayer().getMapId() == 910000000) {
                c.getPlayer().dropMessage(5, "You are already in the Free Market.");
            }

            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);

            c.getPlayer().changeMap(map, map.getPortal(0));

            return 1;
        }
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- Cellion PLAYER COMMANDS -----------------");
            c.getPlayer().yellowMessage("@str, @dex, @int, @luk <amount> : Place ability points in specified statistic.");
            c.getPlayer().yellowMessage("@support <message> : Send a message to availible staff members.");
            c.getPlayer().yellowMessage("@dispose : Enables your character's actions when stuck.");
            c.getPlayer().yellowMessage("@event : Quick travel to the current event, if available.");
            c.getPlayer().yellowMessage("@alpha : Open the ALPHA debuf menu.");
            c.getPlayer().yellowMessage("@online : Display the current online players.");
            c.getPlayer().yellowMessage("@mob : Display information on closest monster.");
            c.getPlayer().yellowMessage("@fm : Quick travel to the Free Market.");
            c.getPlayer().yellowMessage("@help : Display the general player commands.");

            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.SendPacket(CWvsContext.enableActions());
            return 1;
        }
    }

    public static class Cellion extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.SendPacket(CWvsContext.enableActions());

            if (c.getPlayer().isInBlockedMap()) {
                c.getPlayer().dropMessage(5, "This command may not be used here.");
                return 0;
            } else {
                NPCScriptManager.getInstance().start(c, 9001001, null);
                return 1;
            }
        }
    }

    public static class Event extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getClient().getChannelServer().eventOn) {
                c.getPlayer().changeMap(c.getPlayer().getClient().getChannelServer().eventMap, 0);
                c.getPlayer().dropMessage(5, "Welcome to the " + ServerConstants.SERVER_NAME + " event, have fun!");
            } else {
                c.getPlayer().dropMessage(5, "Sorry, there is currently no event being hosted.");
            }
            return 1;
        }
    }

    public static class SpawnBomb extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMapId() != 109010100) {
                c.getPlayer().dropMessage(5, "You may only spawn bomb in the event map.");
                return 0;
            }
            if (!c.getChannelServer().bombermanActive()) {
                c.getPlayer().dropMessage(5, "You may not spawn bombs yet.");
                return 0;
            }
            c.getPlayer().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9300166), c.getPlayer().getPosition());
            return 1;
        }
    }

    public static class Support extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, "[Cellion Support] " + c.getPlayer().getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
            c.getPlayer().dropMessage(5, "Your message has been sent successfully.");
            return 1;
        }
    }
}
