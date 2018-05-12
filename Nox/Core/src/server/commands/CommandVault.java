/*
 * Cellion Development
 */
package server.commands;

import client.ClientSocket;
import client.MapleDisease;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.CheaterData;
import handling.world.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import provider.data.HexTool;
import scripting.EventManager;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.MaplePortal;
import server.MapleStringInformationProvider;
import server.life.LifeFactory;
import server.life.Mob;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.quest.Quest;
import server.shops.MapleShopFactory;
import service.ChannelServer;
import service.RecvPacketOpcode;
import service.SendPacketOpcode;
import tools.Pair;
import tools.StringUtil;
import tools.Tuple;
import tools.packet.CField;
import tools.packet.WvsContext;

/**
 * Additional Command Vault
 * @author Mazen Massoud
 * 
 * @note These commands are accessible only to developers upon toggling the command vault with the '!commandvault' command.
 * The commands found here are mostly unused and have been placed behind this toggle in order to keep the primary commands more organized.
 */
public class CommandVault {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.COMMAND_VAULT_ACCESS;
    }

    public static class CharInfo extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
            builder.append(ClientSocket.getLogMessage(other, ""));
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
        public int execute(ClientSocket c, String[] splitted) {
            List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
    }
    
    public static class Map extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
    
    public static class OpenNpc extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]), splitted.length > 2 ? StringUtil.joinStringFrom(splitted, 2) : splitted[1]);
            return 1;
        }
    }

    public static class OpenShop extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }
    
    public static class ActiveBomberman extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
                    chrs.getClient().SendPacket(WvsContext.broadcastMsg(GameConstants.isEventMap(chrs.getMapId()) ? 0 : 22, c.getChannel(), "Event : Bomberman event has started!"));
                }
                player.getMap().broadcastMessage(CField.getClock(60));
            }
            return 1;
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().broadcastMessage(CField.musicChange(splitted[1]));
            return 1;
        }
    }

    public static class DeactiveBomberman extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
                    chrs.getClient().SendPacket(WvsContext.broadcastMsg(GameConstants.isEventMap(chrs.getMapId()) ? 0 : 22, c.getChannel(), "Event : Bomberman event has ended! The winners are: " + winner));
                }
            }
            return 1;
        }
    }

    public static class Bob extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            Mob mob = LifeFactory.getMonster(9400551);
            for (int i = 0; i < 10; i++) {
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
    }

    public static class StartAutoEvent extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
    
    public static class CutScene extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            //c.write(NPCPacket.getCutSceneSkip());
            return 1;
        }
    }

    public static class DemonJob extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.SendPacket(CField.NPCPacket.getDemonSelection());
            return 1;
        }
    }
    
    public static class Find extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
                            for (Quest questPair : Quest.getAllInstances()) {
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
                        case "HEADER":
                        case "OPCODE":
                            List<String> headers = new ArrayList<>();
                            headers.add("\r\nSend Opcodes:");
                            for (SendPacketOpcode send : SendPacketOpcode.values()) {
                                if (send.name() != null && send.name().toLowerCase().contains(search.toLowerCase())) {
                                    headers.add("\r\n" + send.name() + " Value: " + send.getValue() + " Hex: " + HexTool.getOpcodeToString(send.getValue()));
                                }
                            }
                            headers.add("\r\nRecv Opcodes:");
                            for (RecvPacketOpcode recv : RecvPacketOpcode.values()) {
                                if (recv.name() != null && recv.name().toLowerCase().contains(search.toLowerCase())) {
                                    headers.add("\r\n" + recv.name() + " Value: " + recv.getValue() + " Hex: " + HexTool.getOpcodeToString(recv.getValue()));
                                }
                            }
                            for (String header : headers) {
                                if (sb.length() > 10000) {
                                    sb.append("\r\nThere were too many results, and could not display all of them.");
                                    break;
                                }
                                sb.append(header);
                                //c.write(NPCPacket.getNPCTalk(9010000, (byte) 0, headers.toString(), "00 00", (byte) 0, 9010000));
                                //c.getPlayer().dropMessage(6, header);
                            }
                            break;
                        default:
                            c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
                            break;
                    }
                    c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sb.toString(), NPCChatByType.NPC_Cancellable));
                    break;
            }
            return 0;
        }
    }
    
    public static class KillAllDrops extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "Map does not exist");
                return 0;
            }
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                if (!mob.getStats().isBoss() || mob.getStats().isPartyBonus() || c.getPlayer().isGM()) {
                    map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
                }
            }
            return 1;
        }
    }
}
