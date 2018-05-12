package server.commands;

import client.CharacterTemporaryStat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import client.ClientSocket;
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
import server.life.LifeFactory;
import server.life.MonsterInformationProvider;
import server.life.Mob;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactorFactory;
import server.life.NPCLife;
import server.maps.objects.Reactor;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.quest.Quest;
import server.shops.ShopFactory;
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
import tools.packet.WvsContext;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

/**
 * Administrator Commands
 * @author Mazen Massoud
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class GiveDP extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
            Pet pet = c.getPlayer().getPet(0);
            if (pet == null) {
                return 0;
            }
            c.getPlayer().getMap().broadcastPacket(c.getPlayer(), PetPacket.petColor(c.getPlayer().getId(), (byte) 0, Color.yellow.getAlpha()), true);
            return 1;
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
    }

    public static class ReloadCS extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            CashItemFactory.reload();
            return 1;
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + " clones disposed.");
            c.getPlayer().disposeClones();
            return 1;
        }
    }

    public static class DamageBuff extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            SkillFactory.getSkill(9101003).getEffect(1).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class MagicWheel extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
            c.SendPacket(WvsContext.magicWheel((byte) 3, items, data, end));
            return 1;
        }
    }

    public static class UnsealItem extends CommandExecute {

        @Override
        public int execute(final ClientSocket c, String[] splitted) {
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

    public static class NearestPortal extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return 1;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class Reward extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            chr.addReward(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]), Integer.parseInt(splitted[6]), StringUtil.joinStringFrom(splitted, 7));
            chr.updateReward();
            return 1;
        }
    }

    public static class ToggleMultiLevel extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            ServerConstants.MULTI_LEVEL = !ServerConstants.MULTI_LEVEL;
            return 1;
        }
    }

    public static class DoubleTime extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            ServerConstants.DOUBLE_TIME = !ServerConstants.DOUBLE_TIME;
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(4, "It's Miracle Time!  Between 2:00 PM and 4:00 PM (Pacific) today, Miracle, Premium, Revolutionary Miracle, Super Miracle, Enlightening Miracle and Carved Slot Miracle Cubes have increased chances to raise your item to the next potential tier!"));
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Time Event has " + (ServerConstants.DOUBLE_TIME ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class DoubleMiracleTime extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            ServerConstants.DOUBLE_MIRACLE_TIME = !ServerConstants.DOUBLE_MIRACLE_TIME;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Miracle Time Event has " + (ServerConstants.DOUBLE_MIRACLE_TIME ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    

    public static class StripEveryone extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
                World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(0, victim.getName() + " has been stripped by " + c.getPlayer().getName()));
            }
            return 1;
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
                        World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(0, "The server will shutdown in " + minutesLeft + " minutes. Please log off safely."));
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
                c.getPlayer().getMap().broadcastPacket(CField.removeItemFromMap(mapitem.getObjectId(), 2, c.getPlayer().getId()), mapitem.getPosition());
                c.getPlayer().getMap().removeMapObject(item);

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

    public static class GetSkill extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
    
    public static class SpeakAll extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch == null) {
                    return 0;
                } else {
                    mch.getMap().broadcastPacket(CField.getChatText(mch.getId(), StringUtil.joinStringFrom(splitted, 1), mch.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "unable to find '" + splitted[1]);
                return 0;
            } else {
                victim.getMap().broadcastPacket(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), true));
            }
            return 1;
        }
    }
    
    public static class DiseaseMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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

    public static class ResetMobs extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
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
        public int execute(ClientSocket c, String[] splitted) {
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

            OutPacket packet = WvsContext.broadcastMsg(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastPacket(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class LookNPC extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.NPC)) {
                NPCLife reactor2l = (NPCLife) reactor1l;

                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class LookReactor extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.REACTOR)) {
                Reactor reactor2l = (Reactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return 0;
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return 0;
        }
    }

    public static class NPCPos extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFh());
            return 1;
        }
    }

    public static class VacMob extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (c.getPlayer().getLastGMMovement() != null) {

                for (MapleMapObject mmo : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.MONSTER)) {
                    final Mob monster = (Mob) mmo;
                    c.getPlayer().getMap().broadcastPacket(MobPacket.moveMonster(
                            monster, false, -1, 0, 0, (short) 0, monster.getObjectId(), c.getPlayer().getLastGMMovement(), null, null));
                    monster.setPosition(c.getPlayer().getPosition());
                }

            }
            return 1;
        }
    }

    public static class ServerNotice extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(WvsContext.broadcastMsg(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), StringUtil.joinStringFrom(splitted, 3)));
            }
            return 1;
        }
    }

    public static class SpecialMessage extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            String message = StringUtil.joinStringFrom(splitted, 2);
            int type = Integer.parseInt(splitted[1]);

            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(WvsContext.getSpecialMsg(message, type, true, 10000));
            }
            return 1;
        }
    }

    public static class HideSpecialMessage extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User all : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                all.getClient().getChannelServer().broadcastMessage(WvsContext.getSpecialMsg("", 0, false, 0));
            }
            return 1;
        }
    }
    
    public static class SetName extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
            for (User mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                mch.getAndroid().saveToDb();
                mch.dropMessage(0, "Androids successfully saved!");
            }
            return 1;
        }
    }
    
    public static class GiveSkill extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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

    public static class SpeakMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User victim : c.getPlayer().getMap().getCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastPacket(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class SpeakChn extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastPacket(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                }
            }
            return 1;
        }
    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastPacket(CField.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 1), victim.isGM(), true));
                    }
                }
            }
            return 1;
        }
    }
    
    public static class NPC extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            int npcId = Integer.parseInt(splitted[1]);
            NPCLife npc = LifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getSharedMapResources().footholds.findBelow(c.getPlayer().getPosition()).getId());
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastPacket(NPCPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "You have entered an invalid Npc-Id");
                return 0;
            }
            return 1;
        }
    }

    public static class PNPC extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 1) {
                c.getPlayer().dropMessage(6, "!pnpc <npcid>");
                return 0;
            }
            int npcId = Integer.parseInt(splitted[1]);
            NPCLife npc = LifeFactory.getNPC(npcId);
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
                c.getPlayer().getMap().broadcastPacket(NPCPacket.spawnNPC(npc, true));
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
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!pmob <mobid> <mob respawn time in seconds>");
                return 0;
            }
            int mobid = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            Mob npc;
            try {
                npc = LifeFactory.getMonster(mobid);
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
        public int execute(ClientSocket c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                ClientSocket cs = new ClientSocket(null, 0, 0);
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
        public int execute(ClientSocket c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final NPCLife npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted[1]));
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
        public int execute(ClientSocket c, String[] splitted) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                cserv.setServerMessage(outputMessage);
            }
            return 1;
        }
    }

    public static class ReloadMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().respawn(true, System.currentTimeMillis());
            c.getPlayer().dropMessage(5, "Monster on the current map have been respawned.");
            return 1;
        }
    }

    public static class ReloadSkills extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            SkillFactory.reload();
            c.getPlayer().dropMessage(5, "Skills have been reloaded.");
            return 1;
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(5, "Drops have been reloaded.");
            return 1;
        }
    }

    public static class ReloadMapScript extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Maps);
            c.getPlayer().dropMessage(5, "Map scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadReactor extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Reactor);
            c.getPlayer().dropMessage(5, "Reactor scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadQuest extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Quest);
            c.getPlayer().dropMessage(5, "Quest scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadNPC extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.NPC);
            c.getPlayer().dropMessage(5, "NPC scripts have been reloaded.");
            return 1;
        }
    }

    public static class ReloadPortal extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Portal);
            return 1;
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            ShopFactory.getInstance().clear();
            return 1;
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            AbstractScriptManager.reloadCachedScript(ScriptType.Event);
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return 1;
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().resetFully();
            return 1;
        }
    }

    public static class SendNote extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
        public int execute(ClientSocket c, String[] splitted) {

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
        public int execute(ClientSocket c, String[] splitted) {
            SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class BuffItemEX extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(c.getPlayer());
            return 0;
        }
    }

    public static class cancelSkill extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dispelBuff(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class MapBuffSkill extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffect(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapBuffItemEX extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User mch : c.getPlayer().getMap().getCharacters()) {
                MapleItemInformationProvider.getInstance().getItemEffectEX(Integer.parseInt(splitted[1])).applyTo(mch);
            }
            return 0;
        }
    }

    public static class MapItemSize extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Number of items: " + MapleItemInformationProvider.getInstance().getAllItems().size());
            return 0;
        }
    }
}
