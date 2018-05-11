package server.commands;

import client.CharacterTemporaryStat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import client.Client;
import client.MapleCharacterUtil;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import database.Database;
import handling.world.World;
import java.awt.Point;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import net.OutPacket;
import provider.data.HexTool;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.AbstractScriptManager;
import service.ChannelServer;

import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import scripting.provider.NPCScriptManager;
import scripting.provider.ReactorScriptManager;
import scripting.provider.ScriptType;
import server.CashItemFactory;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleSquad;
import server.MapleStringInformationProvider;
import server.Randomizer;
import server.ShutdownServer;
import server.Timer;
import server.Timer.EventTimer;
import server.Timer.WorldTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.Mob;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactorFactory;
import server.maps.objects.MapleNPC;
import server.maps.objects.MapleReactor;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.quest.Quest;
import server.shops.MapleShopFactory;
import service.RecvPacketOpcode;
import service.SendPacketOpcode;
import tools.LogHelper;
import tools.Pair;
import tools.StringUtil;
import tools.Tuple;
import tools.Utility;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class GiveDP extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Need playername and amount.");
                return 0;
            }
            User chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "Make sure they are in the correct channel.");
            } else {
                chrs.setDPoints(chrs.getDPoints() + Integer.parseInt(splitted[2]));
                c.getPlayer().dropMessage(6, splitted[1] + " has " + chrs.getDPoints() + " Donor Points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }
    
    public static class GiveVP extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Need playername and amount.");
                return 0;
            }
            User chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "Make sure they are in the correct channel");
            } else {
                chrs.setVPoints(chrs.getVPoints() + Integer.parseInt(splitted[2]));
                c.getPlayer().dropMessage(6, splitted[1] + " has " + chrs.getVPoints() + " VotePoints, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }
    
    public static class GiveMeso extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User pTarget = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "Syntax: !givemeso <character> <amount>");
                return 0;
            }
            pTarget.gainMeso(Integer.parseInt(splitted[2]), true);
            c.getPlayer().dropMessage(5, "Character (" + pTarget.getName() + ") has been given " + Integer.parseInt(splitted[2]) + " Mesos.");
            return 1;
        }
    }
    
    public static class GiveNX extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User pTarget = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "!givenx <player> <amount>");
                return 0;
            }
            pTarget.gainNX(Integer.parseInt(splitted[2]), true);
            c.getPlayer().dropMessage(5, "Character (" + pTarget.getName() + ") has been given " + Integer.parseInt(splitted[2]) + " NX.");
            return 1;
        }
    }
    
    public static class SetLevel extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            switch (splitted.length) {
                case 2:
                    c.getPlayer().setLevel((short) Integer.parseInt(splitted[1]));
                    c.getPlayer().setExp(0);
                    c.getPlayer().updateSingleStat(MapleStat.EXP, 0);
                    c.getPlayer().updateSingleStat(MapleStat.LEVEL, c.getPlayer().getLevel());
                    break;
                case 1:
                    User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                    victim.setLevel((short) Integer.parseInt(splitted[1]));
                    victim.setExp(0);
                    victim.updateSingleStat(MapleStat.EXP, 0);
                    victim.updateSingleStat(MapleStat.LEVEL, c.getPlayer().getLevel());
                    break;
                default:
                    c.getPlayer().dropMessage(5, "Syntax: !setlevel <level> or !setlevel <character> <level>");
                    break;
            }
            return 1;
        }
    }
    
    public static class LevelPersonTo extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User pUser = Utility.requestCharacter(splitted[1]);
            if (pUser == null) {
                c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") could not be found.");
                return 0;
            }
            while (pUser.getLevel() < Integer.parseInt(splitted[1])) {
                if (pUser.getLevel() < 255) {
                    pUser.levelUp();
                }
            }
            c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") is now level " + pUser.getLevel() + ".");
            return 1;
        }
    }

    public static class UpdatePet extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Pet pet = c.getPlayer().getPet(0);
            if (pet == null) {
                return 0;
            }
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.petColor(c.getPlayer().getId(), (byte) 0, Color.yellow.getAlpha()), true);
            return 1;
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
    }

    public static class ReloadCS extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            CashItemFactory.reload();
            return 1;
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + " clones disposed.");
            c.getPlayer().disposeClones();
            return 1;
        }
    }

    public static class DamageBuff extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            SkillFactory.getSkill(9101003).getEffect(1).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class MagicWheel extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            List<Integer> items = new LinkedList<>();
            for (int i = 1; i <= 10; i++) {
                try {
                    items.add(Integer.parseInt(splitted[i]));
                } catch (NumberFormatException ex) {
                    items.add(GameConstants.eventRareReward[GameConstants.eventRareReward.length]);
                }
            }
            int end = Randomizer.nextInt(10);
            String data = "Magic Wheel";
            c.getPlayer().setWheelItem(items.get(end));
            c.SendPacket(CWvsContext.magicWheel((byte) 3, items, data, end));
            return 1;
        }
    }

    public static class UnsealItem extends CommandExecute {

        @Override
        public int execute(final Client c, String[] splitted) {
            short slot = Short.parseShort(splitted[1]);
            Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
            if (item == null) {
                return 0;
            }
            final int itemId = item.getItemId();
            Integer[] itemArray = {1002140, 1302000, 1302001,
                1302002, 1302003, 1302004, 1302005, 1302006,
                1302007};
            final List<Integer> items = Arrays.asList(itemArray);
            //c.write(CField.sendSealedBox(slot, 2028162, items)); //sealed box // UserItemLuckyItemEffect??
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            WorldTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                    Item item = ii.getEquipById(items.get(Randomizer.nextInt(items.size())));
                    MapleInventoryManipulator.addbyItem(c, item);
                    c.SendPacket(CField.unsealBox(item.getItemId()));
                    c.SendPacket(CField.EffectPacket.showRewardItemAnimation(2028162, "")); //sealed box
                }
            }, 10000);
            return 1;
        }
    }

    public static class CutScene extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            //c.write(NPCPacket.getCutSceneSkip());
            return 1;
        }
    }

    public static class DemonJob extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.SendPacket(NPCPacket.getDemonSelection());
            return 1;
        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return 1;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class Reward extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            chr.addReward(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]), Integer.parseInt(splitted[6]), StringUtil.joinStringFrom(splitted, 7));
            chr.updateReward();
            return 1;
        }
    }

    public static class GMPerson extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]).setGM(Byte.parseByte(splitted[2]));
            return 1;
        }
    }

    public static class ToggleMultiLevel extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            ServerConstants.MULTI_LEVEL = !ServerConstants.MULTI_LEVEL;
            return 1;
        }
    }

    public static class DoubleTime extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            ServerConstants.DoubleTime = !ServerConstants.DoubleTime;
            //if (ServerConstants.DoubleMiracleTime) {
            World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(4, "It's Miracle Time!  Between 2:00 PM and 4:00 PM (Pacific) today, Miracle, Premium, Revolutionary Miracle, Super Miracle, Enlightening Miracle and Carved Slot Miracle Cubes have increased chances to raise your item to the next potential tier!"));
            //}
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Time Event has " + (ServerConstants.DoubleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class DoubleMiracleTime extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            ServerConstants.DoubleMiracleTime = !ServerConstants.DoubleMiracleTime;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Miracle Time Event has " + (ServerConstants.DoubleMiracleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class TestDirection extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.SendPacket(CField.UIPacket.UserInGameDirectionEvent(StringUtil.joinStringFrom(splitted, 5), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5])));
            return 1;
        }
    }

    public static class MakePacket extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            byte[] aData = StringUtil.joinStringFrom(splitted, 1).getBytes();
            byte[] aDataTrim = new byte[aData.length - 2];
            System.arraycopy(aData, 2, aDataTrim, 0, aDataTrim.length);
            c.SendPacket(new OutPacket((short) ((aData[0] & 0xFF) + (aData[1] >>> 8))).Encode(aDataTrim));
            return 1;
        }
    }

    public static class StripEveryone extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            ChannelServer cs = c.getChannelServer();
            for (User mchr : cs.getPlayerStorage().getAllCharacters()) {
                if (c.getPlayer().isGM()) {
                    continue;
                }
                MapleInventory equipped = mchr.getInventory(MapleInventoryType.EQUIPPED);
                MapleInventory equip = mchr.getInventory(MapleInventoryType.EQUIP);
                List<Short> ids = new ArrayList<>();
                for (Item item : equipped.newList()) {
                    ids.add(item.getPosition());
                }
                for (short id : ids) {
                    MapleInventoryManipulator.unequip(mchr.getClient(), id, equip.getNextFreeSlot());
                }
            }
            return 1;
        }
    }

    public static class Strip extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            MapleInventory equipped = victim.getInventory(MapleInventoryType.EQUIPPED);
            MapleInventory equip = victim.getInventory(MapleInventoryType.EQUIP);
            List<Short> ids = new ArrayList<>();
            for (Item item : equipped.newList()) {
                ids.add(item.getPosition());
            }
            for (short id : ids) {
                MapleInventoryManipulator.unequip(victim.getClient(), id, equip.getNextFreeSlot());
            }
            boolean notice = false;
            if (splitted.length > 1) {
                notice = true;
            }
            if (notice) {
                World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0, victim.getName() + " has been stripped by " + c.getPlayer().getName()));
            }
            return 1;
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Long.parseLong(splitted[1]), true);
                }
            }
            return 1;
        }
    }

    public static class ScheduleHotTime extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(0, "!ScheduleHotTime <Item Id>");
                return 0;
            }
            if (!MapleItemInformationProvider.getInstance().itemExists(Integer.parseInt(splitted[1]))) {
                c.getPlayer().dropMessage(0, "Item does not exists.");
                return 0;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    if (c.canClickNPC()) {
                        mch.gainItem(Integer.parseInt(splitted[1]), 1);
                        mch.getClient().SendPacket(CField.NPCPacket.getNPCTalk(9010010, NPCChatType.OK, "You got the #t" + Integer.parseInt(splitted[1]) + "#, right? Click it to see what's inside. Go ahead and check your item inventory now, if you're curious.", NPCChatByType.NPC_UnCancellable, 9010010));
                    }
                }
            }
            c.getPlayer().dropMessage(0, "Hot Time has been scheduled successfully.");
            return 1;
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() != c.getPlayer().getMapId()) {
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                }
            }
            return 1;
        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
                range = 1;
            }
            switch (range) {
                case 0:
                    c.getPlayer().getMap().disconnectAll();
                    break;
                case 1:
                    c.getChannelServer().getPlayerStorage().disconnectAll(true);
                    break;
                case 2:
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.getPlayerStorage().disconnectAll(true);
                    }
                    break;
                default:
                    break;
            }
            return 1;
        }
    }

    public static class Shutdown extends CommandExecute {

        protected static Thread t = null;

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Shutting down...");
            if (t == null || !t.isAlive()) {
                t = new Thread(ShutdownServer.getInstance());
                ShutdownServer.getInstance().shutdown();
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }

    public static class ShutdownTime extends Shutdown {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public int execute(Client c, String[] splitted) {
            minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "Shutting down... in " + minutesLeft + " minutes");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0, "The server will shutdown in " + minutesLeft + " minutes. Please log off safely."));
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, "A shutdown thread is already in progress or shutdown has not been done. Please wait.");
            }
            return 1;
        }
    }
    
    public static class Morph extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User pPlayer = c.getPlayer();
            int nMorphID = Integer.valueOf(splitted[1]);

            if (nMorphID == 0) {
                pPlayer.cancelMorphs();
                c.getPlayer().dropMessage(6, "You have been demorphed.");
            } else {
                final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.Morph, nMorphID);
                c.SendPacket(BuffPacket.giveBuff(pPlayer, 0, 1, stat, null));
                c.getPlayer().dropMessage(6, "You have morphed into " + nMorphID + ".");
            }
            return 1;
        }
    }

    public static class Mount extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User pPlayer = c.getPlayer();
            int nMountID = Integer.valueOf(splitted[1]);

            if (nMountID == 0) {
                pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                c.getPlayer().dropMessage(6, "You have been unmounted.");
            } else {
                final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.RideVehicle, nMountID);
                c.SendPacket(BuffPacket.giveBuff(pPlayer, 0, 1, stat, null));
                c.getPlayer().dropMessage(6, "You have mounted " + nMountID + ".");
            }
            return 1;
        }
    }
    
    public static class ItemVac extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            final List<MapleMapObject> items = c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), GameConstants.maxViewRangeSq(), Arrays.asList(MapleMapObjectType.ITEM));
            MapleMapItem mapitem;
            for (MapleMapObject item : items) {
                mapitem = (MapleMapItem) item;
                if (mapitem.getMeso() > 0) {
                    c.getPlayer().gainMeso(mapitem.getMeso(), true);
                } else if (mapitem.getItem() == null || !MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true)) {
                    continue;
                }
                mapitem.setPickedUp(true);
                c.getPlayer().getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()), mapitem.getPosition());
                c.getPlayer().getMap().removeMapObject(item);

            }
            return 1;
        }
    }
    
    public static class Find extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
                    c.SendPacket(NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sb.toString(), NPCChatByType.NPC_Cancellable));
                    break;
            }
            return 0;
        }
    }
    
    public static class KillAllDrops extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
            Point position = c.getPlayer().getPosition();

            c.getPlayer().dropMessage(6, "Your position is: " + position + ".");
            return 1;
        }
    }
    
    public static class GivePet extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
    
    public static class OpenNpc extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, Integer.parseInt(splitted[1]), splitted.length > 2 ? StringUtil.joinStringFrom(splitted, 2) : splitted[1]);
            return 1;
        }
    }

    public static class OpenShop extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleShopFactory.getInstance().getShop(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class GetSkill extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }
    
    public static class Shop extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }

    public static class StartAutoEvent extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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

    public static class AutoEvent extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleEvent.onStartEvent(c.getPlayer());
            return 1;
        }
    }

    public static class StartEvent extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(0, "The person isn't login, or doesn't exists.");
                return 0;
            }
            World.Broadcast.broadcastSmega(CWvsContext.broadcastMsg(3, victim.getClient().getChannel(), victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            return 1;
        }
    }

    public static class SpeakAll extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch == null) {
                    return 0;
                } else {
                    mch.getMap().broadcastMessage(CField.getChatText(mch.getId(), StringUtil.joinStringFrom(splitted, 1), mch.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "unable to find '" + splitted[1]);
                return 0;
            } else {
                victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), true));
            }
            return 1;
        }
    }
    
    public static class DiseaseMap extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "!disease <type> <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL");
                return 0;
            }
            int type;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) { //24, 289 and 29 are cool.
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else if (splitted[1].equalsIgnoreCase("POTENTIAL")) {
                type = 138;
            } else if (splitted[1].equalsIgnoreCase("SLOW2")) {
                type = 172;
            } else if (splitted[1].equalsIgnoreCase("TORNADO")) {
                type = 173;
            } else if (splitted[1].equalsIgnoreCase("FLAG")) {
                type = 799;
            } else {
                c.getPlayer().dropMessage(6, "!disease <type> <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL/SLOW2/TORNADO/FLAG");
                return 0;
            }
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() == c.getPlayer().getMapId()) {
                    if (splitted.length == 4) {
                        mch.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1));
                    } else {
                        mch.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1));
                    }
                }
            }
            return 1;
        }
    }

    public static class Disease extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL");
                return 0;
            }
            int type;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else if (splitted[1].equalsIgnoreCase("POTENTIAL")) {
                type = 138;
            } else if (splitted[1].equalsIgnoreCase("SLOW2")) {
                type = 172;
            } else if (splitted[1].equalsIgnoreCase("TORNADO")) {
                type = 173;
            } else if (splitted[1].equalsIgnoreCase("FLAG")) {
                type = 799;
            } else {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL/SLOW2/TORNADO/FLAG");
                return 0;
            }
            if (splitted.length == 4) {
                User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "Not found.");
                    return 0;
                }
                victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
            } else {
                for (User victim : c.getPlayer().getMap().getCharacters()) {
                    victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
                }
            }
            return 1;
        }
    }

    public static class SetInstanceProperty extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                em.setProperty(splitted[2], splitted[3]);
                for (EventInstanceManager eim : em.getInstances()) {
                    eim.setProperty(splitted[2], splitted[3]);
                }
            }
            return 1;
        }
    }

    public static class ListInstanceProperty extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", eventManager: " + em.getName() + " iprops: " + eim.getProperty(splitted[2]) + ", eprops: " + em.getProperty(splitted[2]));
                }
            }
            return 0;
        }
    }

    public static class LeaveInstance extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "You are not in one");
            } else {
                c.getPlayer().getEventInstance().unregisterPlayer(c.getPlayer());
            }
            return 1;
        }
    }
    
    public static class StartInstance extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (c.getPlayer().getEventInstance() != null) {
                c.getPlayer().dropMessage(5, "You are in one");
            } else if (splitted.length > 2) {
                EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
                if (em == null || em.getInstance(splitted[2]) == null) {
                    c.getPlayer().dropMessage(5, "Not exist");
                } else {
                    em.getInstance(splitted[2]).registerPlayer(c.getPlayer());
                }
            } else {
                c.getPlayer().dropMessage(5, "!startinstance [eventmanager] [eventinstance]");
            }
            return 1;

        }
    }
    
    public static class ResetMobs extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
    }

    public static class GMChatNotice extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.dropMessage(-6, StringUtil.joinStringFrom(splitted, 1));
            }
            return 1;
        }
    }
    
    public static class Notice extends CommandExecute {

        protected static int getNoticeType(String typestring) {
            switch (typestring) {
                case "1":
                    return -1;
                case "2":
                    return -2;
                case "3":
                    return -3;
                case "4":
                    return -4;
                case "5":
                    return -5;
                case "6":
                    return -6;
                case "7":
                    return -7;
                case "8":
                    return -8;
                case "n":
                    return 0;
                case "p":
                    return 1;
                case "l":
                    return 2;
                case "nv":
                    return 5;
                case "v":
                    return 5;
                case "b":
                    return 6;
            }
            return -1;
        }

        @Override
        public int execute(Client c, String[] splitted) {
            int joinmod = 1;
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
            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            OutPacket packet = CWvsContext.broadcastMsg(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
            OutPacket packet = CWvsContext.yellowChat((splitted[0].equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            switch (range) {
                case 0:
                    c.getPlayer().getMap().broadcastMessage(packet);
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
    
    public static class LookNPC extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.NPC)) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;

                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class LookReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.REACTOR)) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return 0;
        }
    }

    public static class MyNPCPos extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFh());
            return 1;
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
            final String mname = splitted[2];
            final int num = Integer.parseInt(splitted[1]);
            int mid = 0;
            for (Pair<Integer, String> mob : MapleMonsterInformationProvider.getInstance().getAllMonsters()) {
                if (mob.getRight().toLowerCase().equals(mname.toLowerCase())) {
                    mid = mob.getLeft();
                    break;
                }
            }

            Mob onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }
            if (onemob == null) {
                c.getPlayer().dropMessage(5, "Mob does not exist");
                return 0;
            }
            for (int i = 0; i < num; i++) {
                Mob mob = MapleLifeFactory.getMonster(mid);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
    }

    public static class VacMob extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (c.getPlayer().getLastGMMovement() != null) {

                for (MapleMapObject mmo : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.MONSTER)) {
                    final Mob monster = (Mob) mmo;
                    c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(
                            monster, false, -1, 0, 0, (short) 0, monster.getObjectId(), c.getPlayer().getLastGMMovement(), null, null));
                    monster.setPosition(c.getPlayer().getPosition());
                }

            }
            return 1;
        }
    }

    /*public static class SpawnMist extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            int clock = 1;
            MapleMonster mob = MapleLifeFactory.getMonster(891000);
            c.write(CField.spawnClockMist(this));
            return 0;
        }
    }*/
    
    public static class ItemSearch extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            String search = StringUtil.joinStringFrom(splitted, 1);
            String result = "";

            List<String> retItems = new ArrayList<>();
            int selection = 0;
            for (Map.Entry<Integer, Pair<String, String>> itemValue : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
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
                        c.SendPacket(NPCPacket.getNPCTalk(9010000, NPCChatType.OK, result, NPCChatByType.NPC_Cancellable));
                        return 1;
                    }
                }
            } else {
                result = "No Items Found";
            }
            c.SendPacket(NPCPacket.getNPCTalk(9010000, NPCChatType.OnAskMenu, result, NPCChatByType.NPC_Cancellable));
            return 1;
        }
    }

    public static class ServerNotice extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(CWvsContext.broadcastMsg(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), StringUtil.joinStringFrom(splitted, 3)));
            }
            return 1;
        }
    }

    public static class SpecialMessage extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            String message = StringUtil.joinStringFrom(splitted, 2);
            int type = Integer.parseInt(splitted[1]);

            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(CWvsContext.getSpecialMsg(message, type, true, 10000));
            }
            return 1;
        }
    }

    public static class HideSpecialMessage extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(CWvsContext.getSpecialMsg("", 0, false, 0));
            }
            return 1;
        }
    }
    
    public static class SetName extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: setname <player> <new name>");
                return 0;
            }
            if (victim == null) {
                c.getPlayer().dropMessage(0, "Could not find the player.");
                return 0;
            }
            if (c.getPlayer().getGMLevel() < 6 && !victim.isGM()) {
                c.getPlayer().dropMessage(6, "Only an Admin can change player's name.");
                return 0;
            }
            victim.getClient().Close();
            victim.getClient().disconnect(true, false);
            victim.setName(splitted[2]);
            return 1;
        }
    }

    public static class Popup extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (splitted.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(StringUtil.joinStringFrom(splitted, 1));
                    mch.dropMessage(1, sb.toString());
                } else {
                    c.getPlayer().dropMessage(6, "Syntax: popup <message>");
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class SaveAndroids extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                mch.getAndroid().saveToDb();
                mch.dropMessage(0, "Androids successfully saved!");
            }
            return 1;
        }
    }
    
    public static class GiveSkill extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[2]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 4, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            victim.changeSingleSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }
    
    public static class Drop extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
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
    }
    
    /*public static class Drop extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
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
        public int execute(Client c, String[] splitted) {
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
    
    public static class SpeakMap extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User victim : c.getPlayer().getMap().getCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class SpeakChn extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                    }
                }
            }
            return 1;
        }
    }
    
    public static class ResetOther extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[2])).forfeit(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]));
            return 1;
        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[2])).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]), splitted.length > 4 ? splitted[4] : null);
            return 1;
        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[2])).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1) {
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return 1;
        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 2) {
                throw new IllegalArgumentException();
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return 1;
        }
    }

    public static class ToggleOffense extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted[1] + " not found");
            }
            return 1;
        }
    }

    public static class TMegaphone extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "Megaphone state : " + (c.getChannelServer().getMegaphoneMuteState() ? "Enabled" : "Disabled"));
            return 1;
        }
    }

    public static class SReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(splitted[1])), Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            c.getPlayer().getMap().spawnReactorOnGroundBelow(reactor, new Point(c.getPlayer().getTruePosition().x, c.getPlayer().getTruePosition().y - 20));
            return 1;
        }
    }

    public static class ClearSquads extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            final Collection<MapleSquad> squadz = new ArrayList<>(c.getChannelServer().getAllSquads().values());
            for (MapleSquad squads : squadz) {
                squads.clear();
            }
            return 1;
        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            int damage = Integer.parseInt(splitted[2]);
            Mob monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class HitAll extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
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
            int damage = Integer.parseInt(splitted[1]);
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                mob.damage(c.getPlayer(), damage, false);
            }
            return 1;
        }
    }

    public static class HitMonster extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            int damage = Integer.parseInt(splitted[1]);
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[2])) {
                    map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                    mob.damage(c.getPlayer(), damage, false);
                }
            }
            return 1;
        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                if (mob.getId() == Integer.parseInt(splitted[1])) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return 1;
        }
    }
    
    public static class NPC extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getSharedMapResources().footholds.findBelow(c.getPlayer().getPosition()).getId());
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "You have entered an invalid Npc-Id");
                return 0;
            }
            return 1;
        }
    }

    public static class PNPC extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(6, "!pnpc <npcid>");
                return 0;
            }
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getSharedMapResources().footholds.findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);

                try (Connection con = Database.GetConnection()) {

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wz_customlife (mid, idd, x, y, fh, cy, rx0, rx1, mobtime, f, team, type, hide) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, c.getPlayer().getMapId());
                        ps.setInt(2, npcId);
                        ps.setInt(3, xpos);
                        ps.setInt(4, ypos);
                        ps.setInt(5, fh);
                        ps.setInt(6, ypos);
                        ps.setInt(7, xpos);
                        ps.setInt(8, xpos);
                        ps.setInt(9, 0); // mobTime
                        ps.setInt(10, 0); // f
                        ps.setInt(11, -1); // carnival team
                        ps.setString(12, "n");
                        ps.setInt(13, 0);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                    c.getPlayer().dropMessage(6, "Failed to save NPC to the database.");
                }

                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the NPC will disappear untill the next restart.");
            } else {
                c.getPlayer().dropMessage(6, "You have entered an invalid npc id.");
                return 0;
            }
            return 1;
        }
    }

    public static class PMOB extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!pmob <mobid> <mob respawn time in seconds>");
                return 0;
            }
            int mobid = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            Mob npc;
            try {
                npc = MapleLifeFactory.getMonster(mobid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }
            if (npc != null) {
                final int xpos = c.getPlayer().getPosition().x;
                final int ypos = c.getPlayer().getPosition().y;
                final int fh = c.getPlayer().getMap().getSharedMapResources().footholds.findBelow(c.getPlayer().getPosition()).getId();
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos);
                npc.setRx1(xpos);
                npc.setFh(fh);
                System.out.printf("The current map (%s) \r\n", c.getPlayer().getMapId());

                try (Connection con = Database.GetConnection()) {

                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wz_customlife (mid, idd, x, y, fh, cy, rx0, rx1, mobtime, f, team, type, hide) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, c.getPlayer().getMapId());
                        ps.setInt(2, mobid);
                        ps.setInt(3, xpos);
                        ps.setInt(4, ypos);
                        ps.setInt(5, fh);
                        ps.setInt(6, ypos);
                        ps.setInt(7, xpos);
                        ps.setInt(8, xpos);
                        ps.setInt(9, mobTime);
                        ps.setInt(10, 0); // f
                        ps.setInt(11, -1); // carnival team
                        ps.setString(12, "m");
                        ps.setInt(13, 0);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                    c.getPlayer().dropMessage(6, "Failed to save monster to the database.");
                }

                c.getPlayer().getMap().addMonsterSpawn(npc, mobTime, null);
                c.getPlayer().dropMessage(6, "Please do not reload this map or else the monster will disappear till the next restart.");
            } else {
                c.getPlayer().dropMessage(6, "You have entered an invalid monster id.");
                return 0;
            }
            return 1;
        }
    }

    public static class PlayerNpc extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                Client cs = new Client(null, 0, 0);
                User chhr = User.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[1]), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " does not exist");
                    return 0;
                }
                PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "Done");
            } catch (NumberFormatException e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
    }

    public static class DestroyPlayerNPC extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC) npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                } else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            } catch (NumberFormatException e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
            }
            return 1;
        }
    }

    public static class ServerMessage extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.setServerMessage(outputMessage);
            }
            return 1;
        }
    }

    public static class ReloadMap extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            final int mapId = Integer.parseInt(splitted[1]);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                    c.getPlayer().dropMessage(5, "There are players in the selected map on Channel " + cserv.getChannel() + ".");
                    return 0;
                }
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId)) {
                    cserv.getMapFactory().removeMap(mapId);
                    c.getPlayer().dropMessage(5, "The selected map has been reloaded.");
                }
            }
            return 1;
        }
    }

    public static class Respawn extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().respawn(true, System.currentTimeMillis());
            c.getPlayer().dropMessage(5, "Monster on the current map have been respawned.");
            return 1;
        }
    }

    public abstract static class TestTimer extends CommandExecute {

        protected Timer toTest = null;

        @Override
        public int execute(final Client c, String[] splitted) {
            final int sec = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(5, "Message will pop up in " + sec + " seconds.");
            c.getPlayer().dropMessage(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Max: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
            final long oldMillis = System.currentTimeMillis();
            toTest.schedule(new Runnable() {
                @Override
                public void run() {
                    c.getPlayer().dropMessage(5, "Message has popped up in " + ((System.currentTimeMillis() - oldMillis) / 1000) + " seconds, expected was " + sec + " seconds");
                    c.getPlayer().dropMessage(5, "Active: " + toTest.getSES().getActiveCount() + " Core: " + toTest.getSES().getCorePoolSize() + " Largest: " + toTest.getSES().getLargestPoolSize() + " Max: " + toTest.getSES().getMaximumPoolSize() + " Current: " + toTest.getSES().getPoolSize() + " Status: " + toTest.getSES().isShutdown() + toTest.getSES().isTerminated() + toTest.getSES().isTerminating());
                }
            }, sec * 1000);
            return 1;
        }
    }

    public static class TestEventTimer extends TestTimer {

        public TestEventTimer() {
            toTest = EventTimer.getInstance();
        }
    }

    public static class TestCloneTimer extends TestTimer {

        public TestCloneTimer() {
            toTest = Timer.CloneTimer.getInstance();
        }
    }

    public static class TestEtcTimer extends TestTimer {

        public TestEtcTimer() {
            toTest = Timer.EtcTimer.getInstance();
        }
    }

    public static class TestMapTimer extends TestTimer {

        public TestMapTimer() {
            toTest = Timer.MapTimer.getInstance();
        }
    }

    public static class TestWorldTimer extends TestTimer {

        public TestWorldTimer() {
            toTest = WorldTimer.getInstance();
        }
    }

    public static class TestBuffTimer extends TestTimer {

        public TestBuffTimer() {
            toTest = Timer.BuffTimer.getInstance();
        }
    }

    public static class Crash extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().Close();
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "The victim does not exist.");
                return 0;
            }
        }
    }
    
    public static class Rev extends CommandExecute {

        private static int revision = -1;

        public static int getRevision() {
            if (revision != -1) {
                return revision;
            } else {
                InputStream svninfo = AdminCommand.class.getResourceAsStream("/all-wcprops");
                if (svninfo == null) {
                    return revision;
                }
                try (Scanner sc = new Scanner(svninfo)) {
                    while (sc.hasNext()) {
                        String[] s = sc.next().split("/");
                        if (s.length > 1 && s[1].equals("svn")) {
                            revision = Integer.parseInt(s[5]);
                            break;
                        }
                    }
                }
            }
            return revision;
        }

        @Override
        public int execute(Client c, String[] splitted) {
            User player = c.getPlayer();
            if (getRevision() != -1) {
                c.getPlayer().dropMessage(5, "Revision: " + ServerConstants.SOURCE_REVISION);
            } else {
                c.getPlayer().dropMessage(5, "Could not find revision.");
            }
            return 1;
        }
    }

    public static class FillBook extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (int e : MapleItemInformationProvider.getInstance().getMonsterBook().keySet()) {
                c.getPlayer().getMonsterBook().getCards().put(e, 2);
            }
            c.getPlayer().getMonsterBook().changed();
            c.getPlayer().dropMessage(5, "Done.");
            return 1;
        }
    }

    public static class ListBook extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            final List<Map.Entry<Integer, Integer>> mbList = new ArrayList<>(MapleItemInformationProvider.getInstance().getMonsterBook().entrySet());
            Collections.sort(mbList, new BookComparator());
            final int page = Integer.parseInt(splitted[1]);
            for (int e = (page * 8); e < Math.min(mbList.size(), (page + 1) * 8); e++) {
                c.getPlayer().dropMessage(6, e + ": " + mbList.get(e).getKey() + " - " + mbList.get(e).getValue());
            }

            return 0;
        }

        public static class BookComparator implements Comparator<Map.Entry<Integer, Integer>>, Serializable {

            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return 1;
                } else if (Objects.equals(o1.getValue(), o2.getValue())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
    }

    public static class Subcategory extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().setSubcategory(Byte.parseByte(splitted[1]));
            return 1;
        }
    }
    
    public static class GainMP extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Need amount.");
                return 0;
            }
            c.getPlayer().modifyCSPoints(2, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class GainP extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Need amount.");
                return 0;
            }
            c.getPlayer().setPoints(c.getPlayer().getPoints() + Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class GainVP extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Need amount.");
                return 0;
            }
            c.getPlayer().setVPoints(c.getPlayer().getVPoints() + Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class SetSendOp extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            SendPacketOpcode.valueOf(splitted[1]).setValue(Short.parseShort(splitted[2]));
            return 1;
        }
    }

    public static class ReloadSkills extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            SkillFactory.reload();
            c.getPlayer().dropMessage(5, "Skills have been reloaded.");
            return 1;
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(5, "Drops have been reloaded.");
            return 1;
        }
    }

    public static class ReloadMapScript extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Maps);
            c.getPlayer().dropMessage(5, "Map scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Reactor);
            c.getPlayer().dropMessage(5, "Reactor scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Quest);
            c.getPlayer().dropMessage(5, "Quest scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadNPC extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.NPC);
            c.getPlayer().dropMessage(5, "NPC scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadPortal extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Portal);
            return 1;
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleShopFactory.getInstance().clear();
            return 1;
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Event);
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[1])).forfeit(c.getPlayer());
            return 1;
        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[1])).start(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[1])).complete(c.getPlayer(), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]));
            return 1;
        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[1])).forceStart(c.getPlayer(), Integer.parseInt(splitted[2]), splitted.length >= 4 ? splitted[3] : null);
            return 1;
        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            Quest.getInstance(Integer.parseInt(splitted[1])).forceComplete(c.getPlayer(), Integer.parseInt(splitted[2]));
            return 1;
        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
            return 1;
        }
    }

    public static class FHReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted[1])).forceHitReactor(Byte.parseByte(splitted[2]));
            return 1;
        }
    }

    public static class DReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
            return 1;
        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class ResetReactor extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().resetReactors();
            return 1;
        }
    }

    public static class SendNote extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (splitted.length >= 2) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                c.getPlayer().sendNote(victim.getName(), text);
            } else {
                c.getPlayer().dropMessage(6, "Use it like this, !sendnote <victim> <text>");
                return 0;
            }
            return 1;
        }
    }

    public static class SendAllNote extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {

            if (splitted.length >= 1) {
                String text = StringUtil.joinStringFrom(splitted, 1);
                for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                c.getPlayer().dropMessage(6, "Use it like this, !sendallnote <text>");
                return 0;
            }
            return 1;
        }
    }

    public static class BuffSkill extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItem extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItemEX extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class cancelSkill extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dispelBuff(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class MapBuffSkill extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItem extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItemEX extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapItemSize extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Number of items: " + MapleItemInformationProvider.getInstance().getAllItems().size());
            return 0;
        }
    }

    public static class openUIOption extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.SendPacket(CField.UIPacket.openUIOption(Integer.parseInt(splitted[1]), 9010000));
            return 1;
        }
    }

    public static class openUIWindow extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.SendPacket(CField.UIPacket.openUI(Integer.parseInt(splitted[1])));
            return 1;
        }
    }
}
