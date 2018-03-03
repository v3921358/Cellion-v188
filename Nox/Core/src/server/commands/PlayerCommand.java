package server.commands;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleStat;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import constants.skills.DanceMoves;
import database.DatabaseConnection;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import service.ChannelServer;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.RankingWorker;
import server.RankingWorker.RankingInformation;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import server.maps.objects.MapleCharacter;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/*
 * REXION Player Commands
 *
 * @author Mazen
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class Sell extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 3 || player.hasBlockedInventory()) {
                c.getPlayer().dropMessage(6, "Syntax: @sell <eq/use/setup/etc> <start slot> <end slot>");
                return 0;
            } else {
                MapleInventoryType type;
                if (splitted[1].equalsIgnoreCase("eq")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = MapleInventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = MapleInventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = MapleInventoryType.ETC;
                } else {
                    c.getPlayer().dropMessage(5, "Invalid Syntax. @sell <eq/use/setup/etc>");
                    return 0;
                }
                MapleInventory inv = c.getPlayer().getInventory(type);
                byte start = Byte.parseByte(splitted[2]);
                byte end = Byte.parseByte(splitted[3]);
                int totalMesosGained = 0;
                for (byte i = start; i <= end; i++) {
                    if (inv.getItem(i) != null) {
                        MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                        int itemPrice = (int) iii.getPrice(inv.getItem(i).getItemId());
                        totalMesosGained += itemPrice;
                        player.gainMeso(itemPrice < 0 ? 0 : itemPrice, true);
                        MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                    }
                }
                c.getPlayer().dropMessage(5, "You sold items in slots " + start + " to " + end + ", and gained " + totalMesosGained + " mesos.");
            }
            return 1;
        }
    }

    public static class Dispose extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());

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

    /*public static class HP extends DistributeStatCommands {

        public HP() {
            stat = MapleStat.MAXHP;
        }
    }

    public static class MP extends DistributeStatCommands {

        public MP() {
            stat = MapleStat.IndieMMP;
        }
    }*/
    public static class Hair extends DistributeStatCommands {

        public Hair() {
            stat = MapleStat.HAIR;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static final int statLim = 5000;
        private static final int hpMpLim = 500000;

        private void setStat(MapleCharacter player, int current, int amount) {
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

        private int getStat(MapleCharacter player) {
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
            //   if (stat == MapleStat.MAXHP) {
            //        hpUsed = change;
            //       short job = c.getPlayer().getJob();
            //       change *= GameConstants.getHpApByJob(job);
            //   }
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

    public static class Mob extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "Monster " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
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

            c.write(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sMessage, NPCChatByType.NPC_Cancellable));
            return 1;
            /*String online = "";
            int size = 0;

            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                online += ChannelServer.getInstance(i).getPlayerStorage().getOnlinePlayers(true);
                size += ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size();
            }

            c.getPlayer().dropMessage(6, "[REXION Realm: " + size + " Connected] " + online);
            if (size > 1) {
                c.getPlayer().dropMessage(6, "There are currently " + size + " people online.");
            } else {
                c.getPlayer().dropMessage(6, "There is currently one person online.");
            }
            return 1;*/
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

    public static class Profile extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().yellowMessage("REXION Character Profile");
            c.getPlayer().dropMessage(6, "- Maple Points (" + c.getPlayer().getCSPoints(2) + ")");
            c.getPlayer().dropMessage(6, "- Donor Points (" + c.getPlayer().getDPoints() + ")");
            c.getPlayer().dropMessage(6, "- Vote Points (" + c.getPlayer().getVPoints() + ")");
            c.getPlayer().dropMessage(6, "- Event Points (" + c.getPlayer().getEPoints() + ")");
            c.getPlayer().dropMessage(6, "- Boss PQ Points (" + c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ) + ")");
            if (c.getPlayer().getLevel() < GameConstants.maxLevel) { // Normal Exp Statistics
                float calcExpPercent = ((c.getPlayer().getExp() * 100) / c.getPlayer().getNeededExp());
                c.getPlayer().yellowMessage("- Experience (" + c.getPlayer().getExp() + " / " + c.getPlayer().getNeededExp() + ") (" + calcExpPercent + "%)");
            } else if (ServerConstants.PARAGON_SYSTEM) { // Paragon Exp Statistics
                if (c.getPlayer().getReborns() >= ServerConstants.MAX_PARAGON) {
                    c.getPlayer().yellowMessage("- Maximum Paragon (SS)");
                } else {
                    String displayRank[] = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
                    if (c.getPlayer().getReborns() > 0) {
                        c.getPlayer().yellowMessage("- Paragon Rank (" + displayRank[c.getPlayer().getReborns() - 1] + ")");
                    } else {
                        c.getPlayer().yellowMessage("- Paragon Rank (0)");
                    }
                    float calcExpPercent = ((c.getPlayer().getExp() * 100) / ServerConstants.PARAGON_NEEDED_EXP[c.getPlayer().getReborns()]);
                    c.getPlayer().yellowMessage("- Paragon Experience (" + c.getPlayer().getExp() + " / " + ServerConstants.PARAGON_NEEDED_EXP[c.getPlayer().getReborns()] + ") (" + calcExpPercent + "%)");
                }
            }
            c.getPlayer().dropMessage(6, "Your character profile has been loaded successfully.");

            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());
            return 1;
        }
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- REXION PLAYER COMMANDS -----------------");
            //c.getPlayer().yellowMessage("@w <name> <message> : Send a whisper to the specified player.");
            //c.getPlayer().yellowMessage("@f <name> : Find a specified character's location and information.");
            c.getPlayer().yellowMessage("@str, @dex, @int, @luk <amount> : Place ability points in specified statistic.");
            c.getPlayer().yellowMessage("@support <message> : Send a message to availible staff members.");
            c.getPlayer().yellowMessage("@sell <from slot> <to slot> : Sell specific item slots instantly.");
            c.getPlayer().yellowMessage("@vote : Claim vote points after voting at (https://playrexion.net/vote).");
            c.getPlayer().yellowMessage("@dispose : Enables your character's actions when stuck.");
            c.getPlayer().yellowMessage("@event : Quick travel to the current event, if available.");
            c.getPlayer().yellowMessage("@rexion : Open the REXION Quick Access system.");
            c.getPlayer().yellowMessage("@profile : View your account and character information.");
            c.getPlayer().yellowMessage("@online : Display the current online players.");
            c.getPlayer().yellowMessage("@mob : Display information on closest monster.");
            c.getPlayer().yellowMessage("@fm : Quick travel to the Free Market.");
            c.getPlayer().yellowMessage("@help : Display the general player commands.");
            c.getPlayer().yellowMessage("@home : Quick travel to the Rexion Courtyard.");

            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());
            return 1;
        }
    }

    public static class Rexion extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());

            if (c.getPlayer().isInBlockedMap()) {
                c.getPlayer().dropMessage(5, "This command may not be used here.");
                return 0;
            } else {
                NPCScriptManager.getInstance().start(c, 9001001, null);
                return 1;
            }
        }
    }

    public static class Home extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());

            if (c.getPlayer().isInBlockedMap()) {
                c.getPlayer().dropMessage(5, "This command may not be used here.");
                return 0;
            } else {
                MapleMap map = c.getChannelServer().getMapFactory().getMap(101071300);
                c.getPlayer().changeMap(map, map.getPortal(0));
                c.getPlayer().yellowMessage("Welcome to the Rexion Courtyard!");
                return 1;
            }
        }
    }

    /*
    *   Point Claim Method from Database
    *   @purpose Allow players to claim points from the database without needing to log out.
    *
    *   @author Mazen
    *   @author Poppy
     */
    public static class Vote extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int nAmount = 0;
            boolean bSuccess = false;

            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cms_votes WHERE accountid = " + c.getPlayer().getAccountID() + " AND collected = 0")) {

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        nAmount += rs.getInt(5);
                        PreparedStatement ps_2 = (PreparedStatement) con.prepareStatement("UPDATE cms_votes SET collected = 1 WHERE id = " + rs.getInt(1));
                        ps_2.executeUpdate();
                        ps_2.close();

                        bSuccess = true;
                    }
                    c.getPlayer().setVPoints(c.getPlayer().getVPoints() + nAmount);
                }

                if (bSuccess) {
                    c.getPlayer().dropMessage(6, "You have claimed " + nAmount + " vote points, and now have a total of " + c.getPlayer().getVPoints() + ".");
                } else {
                    c.getPlayer().dropMessage(5, "Sorry, looks like you don't have any unclaimed vote points.");
                }
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Vote Claim] Error retrieving last character creation time\n", e);
            }
            return 1;
        }
    }

    public static class Memory extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            long currentUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;

            c.getPlayer().dropMessage(6, "REXION Memory Usage (" + currentUsage + " MB)");
            return 1;
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

    /*public static class W extends CommandExecute { // Whisper Command

        @Override
        public int execute(MapleClient c, String[] splitted) {
            String charName = splitted[1];
            int ch = World.Find.findChannel(charName);
            MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "Please use the following syntax to whisper: @w <character> <message>.");
                return 0;
            } else if (charName.equals(c.getPlayer().getName())) {
                c.getPlayer().dropMessage(5, "Sorry, you are unable whisper yourself.");
                return 0;
            } else if (target == null) {
                c.getPlayer().dropMessage(5, "The specified character is either offline or does not exist.");
            }
            
            World.Broadcast.broadcastWhisper(CField.getGameMessage("<< (Whisper Received) " + c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 2), (short) 1), charName); // Send Whisper
            c.write(CField.getGameMessage(">> (Whisper Sent) " + charName + " : " + StringUtil.joinStringFrom(splitted, 2), (short) 1)); // Local Message
            return 1;
        }
    }
    
    public static class F extends CommandExecute { // Find Command

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int ch = World.Find.findChannel(splitted[1]);
            MapleCharacter target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            
            if (target == null) {
                c.getPlayer().dropMessage(5, "Unable to find " + splitted[1] + ".");
                return 0;
            }
             
            c.write(CField.getGameMessage(target.getName() + " is level " + target.getLevel() + " playing in " + target.getMap().getMapName() + " (Channel " + target.getClient().getChannel() + ").", (short) 1));
            return 1;
        }
    }*/
    public static class Support extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(CWvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, "[REXION Support] " + c.getPlayer().getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
            c.getPlayer().dropMessage(5, "Your message has been sent successfully.");
            return 1;
        }
    }

    // Unused Commands
    /*
    public static class beastmodes extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (GameConstants.isBeastTamer(c.getPlayer().getJob())) {
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110001510), (byte) 1, (byte) 1);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110001501), (byte) 1, (byte) 1);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110001502), (byte) 1, (byte) 1);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110001503), (byte) 1, (byte) 1);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(110001504), (byte) 1, (byte) 1);
                HashMap<Skill, SkillEntry> sa = new HashMap<>();
                for (Skill skil : SkillFactory.getAllSkills()) {
                    if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(c.getPlayer().getJob()) && !skil.isInvisible()) { //no db/additionals/resistance skills
                        sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
                    }
                }
                c.getPlayer().changeSkillsLevel(sa);
                c.getPlayer().dropMessage(6, "Beast Tamer modes have been added successfully.");
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "Sorry, this is only available for Beast Tamers.");
                return 0;
            }
        }
    }
    
    public static class EventNPC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().isInBlockedMap() || c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            NPCScriptManager.getInstance().start(c, 9000000, null);
            return 1;
        }
    }
    
    public static class CashDrop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010017, "CashDrop");
            return 1;
        }
    }
    
    public static class T extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getPlayer().getName());
                sb.append(": ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, sb.toString()));
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !t <message>");
                return 0;
            }
            return 1;
        }
    }
    
     */
}
