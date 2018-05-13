/*
 * Cellion Development
 */
package server.commands;

import client.ClientSocket;
import client.MapleDisease;
import client.Stat;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.CheaterData;
import handling.world.World;
import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.OutPacket;
import provider.data.HexTool;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStringInformationProvider;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.LifeFactory;
import server.life.Mob;
import server.life.MobSkillFactory;
import server.life.MonsterInformationProvider;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import server.maps.objects.Pet;
import server.maps.objects.User;
import server.quest.Quest;
import server.shops.ShopFactory;
import service.ChannelServer;
import service.RecvPacketOpcode;
import service.SendPacketOpcode;
import tools.LogHelper;
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

    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = Stat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = Stat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = Stat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = Stat.LUK;
        }
    }

    public static class Hair extends DistributeStatCommands {

        public Hair() {
            stat = Stat.Hair;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected Stat stat = null;
        private static final int statLim = 5000;
        private static final int hpMpLim = 500000;

        private void setStat(User player, int current, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) (current + amount), player);
                    player.updateSingleStat(Stat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) (current + amount), player);
                    player.updateSingleStat(Stat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) (current + amount), player);
                    player.updateSingleStat(Stat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) (current + amount), player);
                    player.updateSingleStat(Stat.LUK, player.getStat().getLuk());
                    break;
                case MaxHP:
                    long maxhp = Math.min(500000, Math.abs(current + amount * 30));
                    player.getStat().setMaxHp((short) (current + amount * 30), player);
                    player.getStat().setMaxHp((short) maxhp, player);
                    player.updateSingleStat(Stat.MaxHP, player.getStat().getHp());
                    break;
                case MaxMP:
                    long maxmp = Math.min(500000, Math.abs(current + amount));
                    player.getStat().setMaxMp((short) maxmp, player);
                    player.updateSingleStat(Stat.MaxMP, player.getStat().getMp());
                    break;
                case Hair:
                    int hair = amount;
                    player.setZeroBetaHair(hair);
                    player.updateSingleStat(Stat.Hair, player.getZeroBetaHair());
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
                case MaxHP:
                    return player.getStat().getMaxHp();
                case MaxMP:
                    return player.getStat().getMaxMp();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
            if (stat == Stat.MaxMP) {
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
            if (getStat(c.getPlayer()) + change > hpMpLim && (stat == Stat.MaxHP || stat == Stat.MaxMP)) {
                c.getPlayer().dropMessage(5, "The stat limit is " + hpMpLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()), change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + hpUsed));
            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + mpUsed));
            c.getPlayer().updateSingleStat(Stat.AP, c.getPlayer().getRemainingAp());
            if (stat == Stat.MaxHP) {
                c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change * 30 + ".");
                c.getPlayer().reloadUser();
            } else {
                c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            }
            return 1;
        }
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
            ShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            ShopFactory shop = ShopFactory.getInstance();
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
                player.getMap().broadcastPacket(CField.getClock(60));
            }
            return 1;
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().broadcastPacket(CField.musicChange(splitted[1]));
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
    
    public static class Position extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            Point position = c.getPlayer().getPosition();

            c.getPlayer().dropMessage(6, "Your position is: " + position + ".");
            return 1;
        }
    }
    
    public static class GivePet extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 6) {
                c.getPlayer().dropMessage(0, splitted[0] + " <character name> <petid> <petname> <petlevel> <petcloseness> <petfullness>");
                return 0;
            }
            User petowner = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int id = Integer.parseInt(splitted[2]);
            String name = splitted[3];
            int level = Integer.parseInt(splitted[4]);
            int closeness = Integer.parseInt(splitted[5]);
            int fullness = Integer.parseInt(splitted[6]);
            long period = 20000;
            short flags = 0;
            if (id >= 5001000 || id < 5000000) {
                c.getPlayer().dropMessage(0, "Invalid pet id.");
                return 0;
            }
            if (level > 30) {
                level = 30;
            }
            if (closeness > 30000) {
                closeness = 30000;
            }
            if (fullness > 100) {
                fullness = 100;
            }
            if (level < 1) {
                level = 1;
            }
            if (closeness < 0) {
                closeness = 0;
            }
            if (fullness < 0) {
                fullness = 0;
            }
            try {
                MapleInventoryManipulator.addById(petowner.getClient(), id, (short) 1, "", Pet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags), 45, false, null);
            } catch (NullPointerException ex) {
            }
            return 1;
        }
    }
    
    public static class AutoEvent extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleEvent.onStartEvent(c.getPlayer());
            return 1;
        }
    }

    public static class StartEvent extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "Started the event and closed off");
                return 1;
            } else {
                c.getPlayer().dropMessage(5, "!event must've been done first, and you must be in the event map.");
                return 0;
            }
        }
    }

    public static class Event extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                final StringBuilder sb = new StringBuilder("Wrong syntax: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return 0;
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return 0;
            }
            return 1;
        }
    }
    
    public static class SpeakMega extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(0, "The person isn't login, or doesn't exists.");
                return 0;
            }
            World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(3, victim.getClient().getChannel(), victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            return 1;
        }
    }
    
    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            Mob monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
    }

    public static class GMChatNotice extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.dropMessage(-6, StringUtil.joinStringFrom(splitted, 1));
            }
            return 1;
        }
    }
    
    public static class Yellow extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            int range = -1;
            switch (splitted[1]) {
                case "m":
                    range = 0;
                    break;
                case "c":
                    range = 1;
                    break;
                case "w":
                    range = 2;
                    break;
            }
            if (range == -1) {
                range = 2;
            }
            OutPacket packet = WvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastPacket(packet);
                    break;
                case 1:
                    ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
                    break;
                case 2:
                    World.Broadcast.broadcastMessage(packet);
                    break;
                default:
                    break;
            }
            return 1;
        }
    }
    
    public static class Letter extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "syntax: !letter <color (green/red)> <word>");
                return 0;
            }
            int start;
            int nstart;
            if (splitted[1].equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted[1].equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "Unknown color!");
                return 0;
            }
            String splitString = StringUtil.joinStringFrom(splitted, 2);
            List<Integer> chars = new ArrayList<>();
            splitString = splitString.toUpperCase();

            for (int i = 0; i < splitString.length(); ++i) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(Integer.valueOf(-1));
                } else if ((chr >= 'A') && (chr <= 'Z')) {
                    chars.add(Integer.valueOf(chr));
                } else if ((chr >= '0') && (chr <= '9')) {
                    chars.add(Integer.valueOf(chr + 200));
                }
            }
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * 32);
            for (Integer i : chars) {
                if (i.intValue() == -1) {
                    dStart += 32;
                } else {
                    int val;
                    Item item;
                    if (i.intValue() < 200) {
                        val = start + i.intValue() - 65;
                        item = new Item(val, (byte) 0, (short) 1);
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false, false);
                        dStart += 32;
                    } else if ((i.intValue() >= 200) && (i.intValue() <= 300)) {
                        val = nstart + i.intValue() - 48 - 200;
                        item = new Item(val, (byte) 0, (short) 1);
                        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false, false);
                        dStart += 32;
                    }
                }
            }
            return 1;
        }
    }
    
    public static class SpawnMob extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final String mname = splitted[2];
            final int num = Integer.parseInt(splitted[1]);
            int mid = 0;
            for (Pair<Integer, String> mob : MonsterInformationProvider.getInstance().getAllMonsters()) {
                if (mob.getRight().toLowerCase().equals(mname.toLowerCase())) {
                    mid = mob.getLeft();
                    break;
                }
            }

            Mob onemob;
            try {
                onemob = LifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }
            if (onemob == null) {
                c.getPlayer().dropMessage(5, "Mob does not exist");
                return 0;
            }
            for (int i = 0; i < num; i++) {
                Mob mob = LifeFactory.getMonster(mid);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
    }
    
    /*public static class SpawnMist extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            int clock = 1;
            MapleMonster mob = LifeFactory.getMonster(891000);
            c.write(CField.spawnClockMist(this));
            return 0;
        }
    }*/
    
    public static class ItemSearch extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            String search = StringUtil.joinStringFrom(splitted, 1);
            String result = "";

            List<String> retItems = new ArrayList<>();
            int selection = 0;
            for (java.util.Map.Entry<Integer, Pair<String, String>> itemValue : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                if (itemValue.getValue().getLeft().toLowerCase().contains(search.toLowerCase())
                        || search.toLowerCase().contains(itemValue.getValue().getLeft().toLowerCase())) {
                    retItems.add("\r\n#L" + selection + "##b" + itemValue.getKey() + " " + " #k- " + " #r#z" + itemValue.getValue().getLeft() + "##k");
                    selection++;
                }
            }

            if (retItems.size() > 0) {
                for (String singleRetItem : retItems) {
                    if (result.length() < 10000) {
                        result += singleRetItem;
                    } else {
                        result += "\r\n#bCouldn't load all items, there are too many results.#k";
                        c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, result, NPCChatByType.NPC_Cancellable));
                        return 1;
                    }
                }
            } else {
                result = "No Items Found";
            }
            c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OnAskMenu, result, NPCChatByType.NPC_Cancellable));
            return 1;
        }
    }
    
    public static class DropItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final String itemName = StringUtil.joinStringFrom(splitted, 2);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1);
            int itemId = 0;

            for (java.util.Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                final String name = item.getValue().getLeft();

                if (name.toLowerCase().equals(itemName.toLowerCase())) {
                    itemId = item.getKey();
                    break;
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemName + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = (Equip) ii.getEquipById(itemId);
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
            }
            return 1;
        }
    }
    
    /*public static class Drop extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
            }
            return 1;
        }
    }

    public static class DropItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final String itemName = StringUtil.joinStringFrom(splitted, 2);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1);
            int itemId = 0;

            for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                final String name = item.getValue().getLeft();

                if (name.toLowerCase().equals(itemName.toLowerCase())) {
                    itemId = item.getKey();
                    break;
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemName + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = (Equip) ii.getEquipById(itemId);
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
            }
            return 1;
        }
    }*/
    
    public static class Marry extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Need <name> <itemid>");
                return 0;
            }
            int itemId = Integer.parseInt(splitted[2]);
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "Invalid itemID.");
            } else {
                User fff = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "Player must be online");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        User[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId, ringID[i]);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "Invalid itemID.");
                                return 0;
                            }
                            MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                            chrz[i].dropMessage(6, "Successfully married with " + chrz[i == 0 ? 1 : 0].getName());
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                        LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                    }
                }
            }
            return 1;
        }
    }
    
    public static class PinkZak extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().setExp(c.getPlayer().getExp() - GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) >= 0 ? GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) : 0);
            return 1;
        }
    }

    public static class ResetExp extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().setExp(0);
            return 1;
        }
    }
    
    public static class Menu extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.SendPacket(WvsContext.enableActions());

            if (c.getPlayer().isInBlockedMap()) {
                c.getPlayer().dropMessage(5, "This command may not be used here.");
                return 0;
            } else {
                NPCScriptManager.getInstance().start(c, 9001001, null);
                return 1;
            }
        }
    }
    
    public static class Online extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
            if (c.getPlayer().getMapId() == ServerConstants.UNIVERSAL_START_MAP) {
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
    
    public static class SpawnBomb extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (c.getPlayer().getMapId() != 109010100) {
                c.getPlayer().dropMessage(5, "You may only spawn bomb in the event map.");
                return 0;
            }
            if (!c.getChannelServer().bombermanActive()) {
                c.getPlayer().dropMessage(5, "You may not spawn bombs yet.");
                return 0;
            }
            c.getPlayer().getMap().spawnMonsterOnGroudBelow(LifeFactory.getMonster(9300166), c.getPlayer().getPosition());
            return 1;
        }
    }
    
    /*public static class TempBanIP extends TempBan {

        public TempBanIP() {
            ipBan = true;
        }
    }*/
    
    public static class Memory extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            long currentUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;

            c.getPlayer().dropMessage(6, "REXION Memory Usage (" + currentUsage + " MB)");
            return 1;
        }
    }
}
