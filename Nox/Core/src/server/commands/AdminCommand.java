package server.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.EventConstants;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import tools.packet.LoadPacket;
import handling.world.World;
import service.ChannelServer;
import net.Packet;
import provider.data.HexTool;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Randomizer;
import server.ShutdownServer;
import server.Timer.EventTimer;
import server.Timer.WorldTimer;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.PetPacket;

public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class GiveDP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
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

    public static class UpdatePet extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + " clones disposed.");
            c.getPlayer().disposeClones();
            return 1;
        }
    }

    public static class DamageBuff extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9101003).getEffect(1).applyTo(c.getPlayer());
            return 1;
        }
    }

    public static class MagicWheel extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
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
            c.write(CWvsContext.magicWheel((byte) 3, items, data, end));
            return 1;
        }
    }

    public static class UnsealItem extends CommandExecute {

        @Override
        public int execute(final MapleClient c, String[] splitted) {
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
                    c.write(CField.unsealBox(item.getItemId()));
                    c.write(CField.EffectPacket.showRewardItemAnimation(2028162, "")); //sealed box
                }
            }, 10000);
            return 1;
        }
    }

    public static class CutScene extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //c.write(NPCPacket.getCutSceneSkip());
            return 1;
        }
    }

    public static class DemonJob extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.write(NPCPacket.getDemonSelection());
            return 1;
        }
    }

    public static class CPacket extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.write(LoadPacket.createPacket());
            return 1;
        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return 1;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return 1;
        }
    }

    public static class Reward extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            User chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            chr.addReward(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]), Integer.parseInt(splitted[6]), StringUtil.joinStringFrom(splitted, 7));
            chr.updateReward();
            return 1;
        }
    }

    public static class GMPerson extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]).setGM(Byte.parseByte(splitted[2]));
            return 1;
        }
    }

    public static class ToggleMultiLevel extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            ServerConstants.MULTI_LEVEL = !ServerConstants.MULTI_LEVEL;
            return 1;
        }
    }

    public static class DoubleTime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventConstants.DoubleTime = !EventConstants.DoubleTime;
            //if (EventConstants.DoubleMiracleTime) {
            World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(4, "It's Miracle Time!  Between 2:00 PM and 4:00 PM (Pacific) today, Miracle, Premium, Revolutionary Miracle, Super Miracle, Enlightening Miracle and Carved Slot Miracle Cubes have increased chances to raise your item to the next potential tier!"));
            //}
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Time Event has " + (EventConstants.DoubleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class DoubleMiracleTime extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventConstants.DoubleMiracleTime = !EventConstants.DoubleMiracleTime;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.dropMessage(0, "Double Miracle Time Event has " + (EventConstants.DoubleMiracleTime ? "began!" : "ended"));
                }
            }
            return 1;
        }
    }

    public static class TestDirection extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.write(CField.UIPacket.UserInGameDirectionEvent(StringUtil.joinStringFrom(splitted, 5), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5])));
            return 1;
        }
    }

    public static class MakePacket extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.write(new Packet(StringUtil.joinStringFrom(splitted, 1).getBytes()));
            return 1;
        }
    }

    public static class StripEveryone extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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
                        mch.getClient().write(CField.NPCPacket.getNPCTalk(9010010, NPCChatType.OK, "You got the #t" + Integer.parseInt(splitted[1]) + "#, right? Click it to see what's inside. Go ahead and check your item inventory now, if you're curious.", NPCChatByType.NPC_UnCancellable, 9010010));
                    }
                }
            }
            c.getPlayer().dropMessage(0, "Hot Time has been scheduled successfully.");
            return 1;
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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
        public int execute(MapleClient c, String[] splitted) {
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

}
