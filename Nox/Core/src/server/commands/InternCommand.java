package server.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.CheaterData;
import handling.world.World;
import java.awt.Point;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import service.ChannelServer;
import service.RecvPacketOpcode;
import service.SendPacketOpcode;
import provider.data.HexTool;
import scripting.EventManager;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MaplePortal;
import server.MapleSquad.MapleSquadType;
import server.MapleStringInformationProvider;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.quest.MapleQuest;
import server.shops.MapleShopFactory;
import tools.Pair;
import tools.StringUtil;
import tools.Tuple;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class InternCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.INTERN;
    }

    public static class Hide extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().isHidden()) {
                c.getPlayer().dropMessage(5, "[" + ServerConstants.SERVER_NAME + " Stealth] Your character is no longer hidden.");
                c.getPlayer().dispelBuff(9101004);
                //MapleItemInformationProvider.getInstance().getItemEffect(2100069).applyTo(c.getPlayer());
                //c.write(CWvsContext.InfoPacket.getStatusMsg(2100069));
            } else {
                c.getPlayer().dropMessage(5, "[" + ServerConstants.SERVER_NAME + " Stealth] Your character is now hidden.");
                SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getPlayer());
            }
            return 0;
        }
    }

    public static class ToggleChatType extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            User oPlayer = c.getPlayer();

            if (oPlayer.usingStaffChat()) {
                oPlayer.toggleStaffChat(true);
            } else {
                oPlayer.toggleStaffChat(false);
            }

            if (oPlayer.usingStaffChat()) {
                oPlayer.dropMessage(5, "You are now using the special chat type.");
            } else {
                oPlayer.dropMessage(5, "You are now using the default chat type.");
            }

            return 0;
        }
    }

    public static class WhereAmI extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Point pos = c.getPlayer().getPosition();

            c.getPlayer().dropMessage(6, "[" + ServerConstants.SERVER_NAME + "] Character Position: X (" + pos.x + "), Y (" + pos.y + "), RX0 (" + (pos.x + 50) + "), RX1 (" + (pos.x - 50) + "), FH (" + c.getPlayer().getFh() + ")");
            c.getPlayer().dropMessage(6, "[" + ServerConstants.SERVER_NAME + "] Current Map ID (" + c.getPlayer().getMap().getId() + ")");
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
                    + "There are currently #e#r" + nSize + "#k#n user(s) online.\r\n\r\n";

            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                if (ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size() > 0) {
                    sMessage += "#b[Channel " + i + "] Players Connected (#b#e" + ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size() + "#n#b)\r\n#k";
                    sMessage += ChannelServer.getInstance(i).getPlayerStorage().formatOnlinePlayers(true);
                    sMessage += "\r\n\r\n";
                }
            }

            c.write(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sMessage, NPCChatByType.NPC_Cancellable));
            return 1;
        }
    }

    public static class Seal extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !seal opcode");
                return 1;
            } else {
                short opcode = 0;
                String text = splitted[1];
                opcode = Short.parseShort(text);
                List<Integer> items;
                Integer[] itemArray = {1002140, 1302000, 1302001,
                    1302002, 1302003, 1302004, 1302005, 1302006,
                    1302007};
                items = Arrays.asList(itemArray);
                c.write(CField.sendBoxDebug(opcode, 2028162, items)); //sealed box
                return 1;
            }
        }
    }

    public static class CharInfo extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final StringBuilder builder = new StringBuilder();
            final User other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (other == null) {
                builder.append("...does not exist");
                c.getPlayer().dropMessage(6, builder.toString());
                return 0;
            }
            //if (other.getClient().getLastPing() <= 0) {
            //    other.getClient().sendPing();
            //}
            builder.append(MapleClient.getLogMessage(other, ""));
            builder.append(" at (").append(other.getPosition().x);
            builder.append(", ").append(other.getPosition().y);
            builder.append(")");

            builder.append("\r\nHP : ");
            builder.append(other.getStat().getHp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxHp());

            builder.append(" || MP : ");
            builder.append(other.getStat().getMp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

            builder.append(" || BattleshipHP : ");
            builder.append(other.currentBattleshipHP());

            builder.append("\r\n WATK : ");
            builder.append(other.getStat().getTotalWatk());
            builder.append(" || MATK : ");
            builder.append(other.getStat().getTotalMagic());
            builder.append(" || MAXDAMAGE : ");
            builder.append(other.getStat().getCurrentMaxBaseDamage());
            builder.append(" || DAMAGE % : ");
            builder.append(other.getStat().dam_r);
            builder.append(" || BOSSDAMAGE % : ");
            builder.append(other.getStat().bossdam_r);
            builder.append(" || CRIT CHANCE : ");
            builder.append(other.getStat().passive_sharpeye_rate());
            builder.append(" || CRIT DAMAGE : ");
            builder.append(other.getStat().passive_sharpeye_percent());

            builder.append("\r\n STR : ");
            builder.append(other.getStat().getStr()).append(" + (").append(other.getStat().getTotalStr() - other.getStat().getStr()).append(")");
            builder.append(" || DEX : ");
            builder.append(other.getStat().getDex()).append(" + (").append(other.getStat().getTotalDex() - other.getStat().getDex()).append(")");
            builder.append(" || INT : ");
            builder.append(other.getStat().getInt()).append(" + (").append(other.getStat().getTotalInt() - other.getStat().getInt()).append(")");
            builder.append(" || LUK : ");
            builder.append(other.getStat().getLuk()).append(" + (").append(other.getStat().getTotalLuk() - other.getStat().getLuk()).append(")");

            builder.append(" || Weapon avoidability : ");
            builder.append(other.getStat().avoidability_weapon);

            builder.append(" || Magic avoidability : ");
            builder.append(other.getStat().avoidability_magic);

            builder.append(" || Avoidability Rate : ");
            builder.append(other.getStat().avoidabilityRate);

            builder.append("\r\n EXP : ");
            builder.append(other.getExp());
            builder.append(" || MESO : ");
            builder.append(other.getMeso());

            builder.append("\r\n Vote Points : ");
            builder.append(other.getVPoints());
            builder.append(" || Event Points : ");
            builder.append(other.getPoints());
            builder.append(" || NX Prepaid : ");
            builder.append(other.getCSPoints(1));

            builder.append("\r\n Party : ");
            builder.append(other.getParty() == null ? -1 : other.getParty().getId());

            builder.append(" || hasTrade: ");
            builder.append(other.getTrade() != null);
            builder.append(" || Latency: ");
            builder.append(other.getClient().getLatency());
            //builder.append(" || PING: ");
            //builder.append(other.getClient().getLastPing());
            //builder.append(" || PONG: ");
            //builder.append(other.getClient().getLastPong());
            builder.append(" || remoteAddress: ");
            other.getClient().DebugMessage(builder);

            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }

    public static class Cheaters extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
    }

    public static class GoTo extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<>();

        static {
            gotomaps.put("ardent", 910001000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("aqua", 860000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("edelstein", 310000000);
            gotomaps.put("ellin", 300000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("ellinel", 101071300);
            gotomaps.put("elluel", 101050000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("florina", 120000300);
            gotomaps.put("fm", 910000000);
            gotomaps.put("future", 271000000);

            for (int i = 1; i < 11; i++) {
                gotomaps.put("gmmap" + i, 180000000 + i);
            }
            gotomaps.put("gmmap100", 180000100);
            gotomaps.put("gmmap105", 180000105);
            
            gotomaps.put("guild", 200000301);
            
            gotomaps.put("happy", 209000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("harbor", 104000000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("kerningsquare", 103040000);
            gotomaps.put("korean", 222000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("ludi", 220000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("nlc", 600000000);
            gotomaps.put("omega", 221000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("pantheon", 400000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("rien", 140000000);
            gotomaps.put("rienastrait", 141000000);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("sixpath", 104020000);
            gotomaps.put("sleepywood", 105000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("tot", 270000000);
            gotomaps.put("twilight", 273000000);
            gotomaps.put("tynerum", 301000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("pinkzakum", 689010000);
            gotomaps.put("pinkzak", 689010000);
            gotomaps.put("pap", 220080001);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("mapleleafhighschool", 744000000);
            gotomaps.put("oldmaple", 690000000);
            gotomaps.put("oldmaplelith", 690000029);
            gotomaps.put("oldmapledungeon", 690000030);

            gotomaps.put("shinsoo", 330000000);
            gotomaps.put("friendsstory", 330000000);

            gotomaps.put("foxridge", 410000000);
            gotomaps.put("rootabyss", 105200000);
            gotomaps.put("whitehaven", 693000070);
            //gotomaps.put("blackhaven", 693000070);
            gotomaps.put("haven", 310070000);
            gotomaps.put("worldtree", 105300000); // called 'Fallen world Tree' in SEA, and 'Dark World Tree' in GMS. Better match both.
            gotomaps.put("damien", 105300303);

            gotomaps.put("twistaquaroad", 860000000);
            gotomaps.put("twistedaquaroad", 860000000);

            gotomaps.put("alishan", 749080900);
            gotomaps.put("blackgate", 610050000);
            gotomaps.put("desertedisland", 620100052);

            gotomaps.put("stumptown", 866000000);
            gotomaps.put("stump", 866000000);
            gotomaps.put("arboren", 866000000);

            gotomaps.put("magnus_e", 401060300);
            gotomaps.put("tyrant_e", 401060300);
            gotomaps.put("magnus_n", 401060200);
            gotomaps.put("tyrant_n", 401060200);
            gotomaps.put("magnus_h", 401060100);
            gotomaps.put("tyrant_h", 401060100);
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
                return 0;
            }

            MapleMap target = null;
            final String searchQuery = splitted[1].toLowerCase();

            if (gotomaps.containsKey(searchQuery)) {
                target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(searchQuery));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "Map does not exist");
                    return 0;
                }
            } else {
                for (Entry<String, Integer> e : gotomaps.entrySet()) {
                    if (e.getKey().contains(searchQuery) || searchQuery.contains(e.getKey())) {
                        target = c.getChannelServer().getMapFactory().getMap(e.getValue());
                        if (target != null) {
                            break;
                        }
                    }
                }
            }
            if (target != null) {
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else {
                // Show the list of possible locations availanle
                c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            }
            return 1;
        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().fakeRelog2();
            return 1;
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return 1;
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[splitted.length - 1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().close();
                victim.getClient().disconnect(true, false);
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "The victim does not exist.");
                return 0;
            }
        }
    }

    public static class TempBan extends CommandExecute {

        protected boolean ipBan = false;
        private final String[] types = {"HACK", "BOT", "AD", "HARASS", "CURSE", "SCAM", "MISCONDUCT", "SELL", "ICASH", "TEMP", "GM", "IPROGRAM", "MEGAPHONE"};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) {
                c.getPlayer().dropMessage(6, "Tempban [name] [REASON] [hours]");
                StringBuilder s = new StringBuilder("Tempban reasons: ");
                for (int i = 0; i < types.length; i++) {
                    s.append(i + 1).append(" - ").append(types[i]).append(", ");
                }
                c.getPlayer().dropMessage(6, s.toString());
                return 0;
            }
            final User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int reason = Integer.parseInt(splitted[2]);
            final int numHour = Integer.parseInt(splitted[3]);

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, numHour);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null || reason < 0 || reason >= types.length) {
                c.getPlayer().dropMessage(6, "Unable to find character or reason was not valid, type tempban to see reasons");
                return 0;
            }
            victim.tempban("Temp banned by " + c.getPlayer().getName() + " for " + types[reason] + " reason", cal, reason, ipBan);
            c.getPlayer().dropMessage(6, "The character " + splitted[1] + " has been successfully tempbanned till " + df.format(cal.getTime()));
            return 1;
        }
    }

    public static class Jail extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Syntax: !jail <name> <minutes, 0 = forever>");
                return 0;
            }
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            final int minutes = Math.max(0, Integer.parseInt(splitted[2]));
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(GameConstants.JAIL);
                victim.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData(String.valueOf(minutes * 60));
                victim.changeMap(target, target.getPortal(0));
            } else {
                c.getPlayer().dropMessage(6, "Please go to the same channel as the targeted character.");
                return 0;
            }
            return 1;
        }
    }

    public static class TempBanIP extends TempBan {

        public TempBanIP() {
            ipBan = true;
        }
    }

    public static class Map extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                User victim;
                int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "Map does not exist");
                        return 0;
                    }
                    MaplePortal targetPortal = null;
                    if (splitted.length > 2) {
                        try {
                            targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                        } catch (IndexOutOfBoundsException e) {
                            // noop, assume the gm didn't know how many portals there are
                            c.getPlayer().dropMessage(5, "Invalid portal selected.");
                        } catch (NumberFormatException a) {
                            // noop, assume that the gm is drunk
                        }
                    }
                    if (targetPortal == null) {
                        targetPortal = target.getPortal(0);
                    }
                    c.getPlayer().changeMap(target, targetPortal);
                } else {
                    victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                    c.getPlayer().dropMessage(6, "Cross changing channel. Please wait.");
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                        c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                    }
                    c.getPlayer().changeChannel(ch);
                }
            } catch (NumberFormatException e) {
                c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
                return 0;
            }
            return 1;
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                if (c.getPlayer().isDeveloper()) {
                    sb.append("DEV ");
                } else if (c.getPlayer().isAdmin()) {
                    sb.append("GM ");
                } else if (c.getPlayer().isGM()) {
                    sb.append("GM ");
                } else if (c.getPlayer().isIntern()) {
                    sb.append("Intern ");
                }
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, sb.toString()));
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !say <message>");
                return 0;
            }
            return 1;
        }
    }

    public static class Find extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            switch (splitted.length) {
                case 1:
                    c.getPlayer().dropMessage(6, splitted[0] + ": <NPC> <MOB> <ITEM> <MAP> <SKILL> <QUEST> <HEADER/OPCODE>");
                    break;
                case 2:
                    c.getPlayer().dropMessage(6, "Provide something to search.");
                    break;
                default:
                    String type = splitted[1].toUpperCase();
                    String search = StringUtil.joinStringFrom(splitted, 2);

                    StringBuilder sb = new StringBuilder();
                    sb.append("<<" + "Type: ").append(type).append(" | " + "Search: ").append(search).append(">>");

                    switch (type) {
                        case "NPC":
                            List<String> retNpcs = new ArrayList<>();
                            for (java.util.Map.Entry<Integer, String> npcPair : MapleStringInformationProvider.getNPCStringCache().entrySet()) {
                                if (npcPair.getValue().toLowerCase().contains(search.toLowerCase())
                                        || search.toLowerCase().contains(npcPair.getValue().toLowerCase())) {
                                    retNpcs.add("\r\n" + npcPair.getKey() + " - " + npcPair.getValue());
                                }
                            }
                            if (retNpcs.size() > 0) {
                                for (String singleRetNpc : retNpcs) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetNpc);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retNpcs.toString(), "00 00", (byte) 0, 9010000));
                                    //c.getPlayer().dropMessage(6, singleRetNpc);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No NPC's Found");
                            }
                            break;
                        case "MAP":
                            List<String> retMaps = new ArrayList<>();

                            for (java.util.Map.Entry<Integer, Pair<String, String>> mapPair : MapleStringInformationProvider.getMapStringCache().entrySet()) {
                                if (mapPair.getValue().getRight().toLowerCase().contains(search.toLowerCase())
                                        || search.toLowerCase().contains(mapPair.getValue().getRight())) {
                                    final String fullMapName = mapPair.getValue().getLeft() + " - " + mapPair.getValue().getRight();
                                    retMaps.add("\r\n" + mapPair.getKey() + " - " + fullMapName);
                                }
                            }
                            if (retMaps.size() > 0) {
                                for (String singleRetMap : retMaps) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetMap);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retMaps.toString(), "00 00", (byte) 0, 9010000));
                                    //c.getPlayer().dropMessage(6, singleRetMap);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No Maps Found");
                            }
                            break;
                        case "MOB":
                            List<String> retMobs = new ArrayList<>();

                            for (java.util.Map.Entry<Integer, String> mobPair : MapleStringInformationProvider.getMobStringCache().entrySet()) {
                                if (mobPair.getValue().toLowerCase().contains(search.toLowerCase())) {
                                    retMobs.add("\r\n" + mobPair.getKey() + " - " + mobPair.getValue());
                                }
                            }
                            if (retMobs.size() > 0) {
                                for (String singleRetMob : retMobs) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetMob);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retMobs.toString(), "00 00", (byte) 0, 9010000));
                                    //c.getPlayer().dropMessage(6, singleRetMob);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No Mobs Found");
                            }
                            break;
                        case "ITEM":
                            List<String> retItems = new ArrayList<>();

                            for (java.util.Map.Entry<Integer, Pair<String, String>> itemValue : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                                if (itemValue.getValue().getLeft().toLowerCase().contains(search.toLowerCase())
                                        || search.toLowerCase().contains(itemValue.getValue().getLeft().toLowerCase())) {
                                    retItems.add("\r\n" + itemValue.getKey() + " - " + itemValue.getValue().getLeft());
                                }
                            }
                            if (retItems.size() > 0) {
                                for (String singleRetItem : retItems) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetItem);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retItems.toString(), "00 00", (byte) 0, 9010000));
                                    //c.getPlayer().dropMessage(6, singleRetItem);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No Items Found");
                            }
                            break;
                        case "QUEST":
                            List<String> retQuests = new ArrayList<>();
                            for (MapleQuest questPair : MapleQuest.getAllInstances()) {
                                if (questPair.getName().length() > 0 && questPair.getName().toLowerCase().contains(search.toLowerCase())) {
                                    retQuests.add("\r\n" + questPair.getId() + " - " + questPair.getName());
                                }
                            }
                            if (retQuests.size() > 0) {
                                for (String singleRetQuest : retQuests) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetQuest);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retQuests.toString(), "00 00", (byte) 0, 9010000));
                                    //    c.getPlayer().dropMessage(6, singleRetItem);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No Quests Found");
                            }
                            break;
                        case "SKILL":
                            List<String> retSkills = new ArrayList<>();
                            for (java.util.Map.Entry<Integer, Tuple<String, String, String>> itemValue : MapleStringInformationProvider.getSkillStringCache().entrySet()) {
                                final String skillName = itemValue.getValue().get_2();

                                if (skillName.toLowerCase().contains(search.toLowerCase()) || search.toLowerCase().contains(skillName)) {
                                    retSkills.add("\r\n" + itemValue.getKey() + " - " + skillName);
                                }
                            }
                            if (retSkills.size() > 0) {
                                for (String singleRetSkill : retSkills) {
                                    if (sb.length() > 10000) {
                                        sb.append("\r\nThere were too many results, and could not display all of them.");
                                        break;
                                    }
                                    sb.append(singleRetSkill);
                                    //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, retSkills.toString(), "00 00", (byte) 0, 9010000));
                                    //    c.getPlayer().dropMessage(6, singleRetSkill);
                                }
                            } else {
                                c.getPlayer().dropMessage(6, "No Skills Found");
                            }
                            break;
                        default:
                            c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
                            break;
                    }
                    c.write(NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sb.toString(), NPCChatByType.NPC_Cancellable));
                    break;
            }
            return 0;
        }
    }

    public static class ID extends Find {
    }

    public static class LookUp extends Find {
    }

    public static class Search extends Find {
    }

    public static class WhosFirst extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //probably bad way to do it
            final long currentTime = System.currentTimeMillis();
            List<Pair<String, Long>> players = new ArrayList<>();
            for (User chr : c.getPlayer().getMap().getCharacters()) {
                if (!chr.isIntern()) {
                    players.add(new Pair<>(MapleCharacterUtil.makeMapleReadable(chr.getName()) + (currentTime - chr.getCheatTracker().getLastAttack() > 600000 ? " (AFK)" : ""), chr.getChangeTime()));
                }
            }
            Collections.sort(players, new WhoComparator());
            StringBuilder sb = new StringBuilder("List of people in this map in order, counting AFK (10 minutes):  ");
            for (Pair<String, Long> z : players) {
                sb.append(z.left).append(", ");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }

        public static class WhoComparator implements Comparator<Pair<String, Long>>, Serializable {

            @Override
            public int compare(Pair<String, Long> o1, Pair<String, Long> o2) {
                if (o1.right > o2.right) {
                    return 1;
                } else if (Objects.equals(o1.right, o2.right)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    }

    public static class WhosLast extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
                for (MapleSquadType t : MapleSquadType.values()) {
                    sb.append(t.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
            if (t == null) {
                StringBuilder sb = new StringBuilder("whoslast [type] where type can be:  ");
                for (MapleSquadType z : MapleSquadType.values()) {
                    sb.append(z.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            if (t.queuedPlayers.get(c.getChannel()) == null) {
                c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "Queued players: " + t.queuedPlayers.get(c.getChannel()).size());
            StringBuilder sb = new StringBuilder("List of participants:  ");
            for (Pair<String, String> z : t.queuedPlayers.get(c.getChannel())) {
                sb.append(z.left).append('(').append(z.right).append(')').append(", ");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }
    }

    public static class WhosNext extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
                for (MapleSquadType t : MapleSquadType.values()) {
                    sb.append(t.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            final MapleSquadType t = MapleSquadType.valueOf(splitted[1].toLowerCase());
            if (t == null) {
                StringBuilder sb = new StringBuilder("whosnext [type] where type can be:  ");
                for (MapleSquadType z : MapleSquadType.values()) {
                    sb.append(z.name()).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
                return 0;
            }
            if (t.queue.get(c.getChannel()) == null) {
                c.getPlayer().dropMessage(6, "The queue has not been initialized in this channel yet.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "Queued players: " + t.queue.get(c.getChannel()).size());
            StringBuilder sb = new StringBuilder("List of participants:  ");
            final long now = System.currentTimeMillis();
            for (Pair<String, Long> z : t.queue.get(c.getChannel())) {
                sb.append(z.left).append('(').append(StringUtil.getReadableMillis(z.right, now)).append(" ago),");
            }
            c.getPlayer().dropMessage(6, sb.toString().substring(0, sb.length() - 2));
            return 0;
        }
    }

    public static class CancelBuffs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return 1;
        }
    }

    public static class CC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().changeChannel(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class FakeRelog2 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().fakeRelog();
            return 1;
        }
    }

    public static class OpenNpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]), splitted.length > 2 ? StringUtil.joinStringFrom(splitted, 2) : splitted[1]);
            return 1;
        }
    }

    public static class OpenShop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }

    public static class SaveAll extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    mch.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage(0, "All characters have been succesfully saved.");
            return 1;
        }
    }

    public static class ActiveBomberman extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            User player = c.getPlayer();
            if (player.getMapId() != 109010100) {
                player.dropMessage(5, "This command is only usable in map 109010100.");
            } else {
                c.getChannelServer().toggleBomberman(c.getPlayer());
                for (User chr : player.getMap().getCharacters()) {
                    if (!chr.isIntern()) {
                        chr.cancelAllBuffs();
                        chr.giveDebuff(MapleDisease.SEAL, MobSkillFactory.getMobSkill(120, 1));
                        //MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, 2100067, chr.getItemQuantity(2100067, false), true, true);
                        //chr.gainItem(2100067, 30);
                        //MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.ETC, 4031868, chr.getItemQuantity(4031868, false), true, true);
                        //chr.gainItem(4031868, (short) 5);
                        //chr.dropMessage(0, "You have been granted 5 jewels(lifes) and 30 bombs.");
                        //chr.dropMessage(0, "Pick up as many bombs and jewels as you can!");
                        //chr.dropMessage(0, "Check inventory for Bomb under use");
                    }
                }
                for (User chrs : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    chrs.getClient().write(CWvsContext.broadcastMsg(GameConstants.isEventMap(chrs.getMapId()) ? 0 : 22, c.getChannel(), "Event : Bomberman event has started!"));
                }
                player.getMap().broadcastMessage(CField.getClock(60));
            }
            return 1;
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
            return 1;
        }
    }

    public static class DeactiveBomberman extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            User player = c.getPlayer();
            if (player.getMapId() != 109010100) {
                player.dropMessage(5, "This command is only usable in map 109010100.");
            } else {
                c.getChannelServer().toggleBomberman(c.getPlayer());
                int count = 0;
                String winner = "";
                for (User chr : player.getMap().getCharacters()) {
                    if (!chr.isGM()) {
                        if (count == 0) {
                            winner = chr.getName();
                            count++;
                        } else {
                            winner += " , " + chr.getName();
                        }
                    }
                }
                for (User chrs : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    chrs.getClient().write(CWvsContext.broadcastMsg(GameConstants.isEventMap(chrs.getMapId()) ? 0 : 22, c.getChannel(), "Event : Bomberman event has ended! The winners are: " + winner));
                }
            }
            return 1;
        }
    }

    public static class Bob extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = MapleLifeFactory.getMonster(9400551);
            for (int i = 0; i < 10; i++) {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
    }

    public static class StartAutoEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.setWorldEvent();
                em.scheduleRandomEvent();
                System.out.println("Scheduling Random Automated Event.");
            } else {
                System.out.println("Could not locate Automated Event script.");
            }
            return 1;
        }
    }
}
