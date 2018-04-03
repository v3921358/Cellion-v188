package tools.packet;

import java.util.LinkedList;
import java.util.List;

import client.MapleClient;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.farm.MapleFarm;
import server.maps.objects.User;
import tools.Pair;

/**
 *
 * @author Itzik
 */
public class FarmPacket {

    public static Packet enterFarm(MapleClient c) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetFarmField.getValue());
        PacketHelper.addCharacterInfo(oPacket, c.getPlayer());
        MapleFarm f = c.getFarm();
        long time = System.currentTimeMillis();
        /* Farm House positions:
         * 000 001 002 003 004
         * 025 026 027 028 029
         * 050 051 052 053 054
         * 075 076 077 078 079
         * 100 101 102 103 104 //104 is base position
         */
        List<Integer> house = new LinkedList<>();
        int houseBase = 104;
        int houseId = 4150001; //15x15 house need to code better houses
        for (int i = 0; i < 5; i++) { //5x5
            for (int j = 0; j < 5; j++) { //5x5
                house.add(houseBase - j - i); //104 base position
            }
        }
        for (int i = 0; i < 25 * 25; i++) { //2D building at every position
            boolean housePosition = house.contains(i);
            oPacket.EncodeInteger(housePosition ? houseId : 0); //building that the position contains
            oPacket.EncodeInteger(i == houseBase ? houseId : 0); //building that the position bases
            oPacket.Fill(0, 5);
            oPacket.EncodeLong(PacketHelper.getTime(time));
        }
        oPacket.EncodeInteger(14);
        oPacket.EncodeInteger(14);
        oPacket.EncodeInteger(0);
        oPacket.EncodeLong(PacketHelper.getTime(time + 180000));

        return oPacket.ToPacket();
    }

    public static Packet farmQuestData(List<Pair<Integer, String>> canStart, List<Pair<Integer, String>> completed) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_QUEST_DATA.getValue());
        oPacket.EncodeInteger(canStart.size());
        for (Pair<Integer, String> i : canStart) {
            oPacket.EncodeInteger(i.getLeft());
            oPacket.EncodeString(i.getRight());
        }
        oPacket.EncodeInteger(completed.size());
        for (Pair<Integer, String> i : completed) {
            oPacket.EncodeInteger(i.getLeft());
            oPacket.EncodeString(i.getRight());
        }

        return oPacket.ToPacket();
    }

    public static Packet alertQuest(int questId, int status) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.QUEST_ALERT.getValue());
        oPacket.EncodeInteger(questId);
        oPacket.Encode((byte) status);

        return oPacket.ToPacket();
    }

    public static Packet updateMonsterInfo(List<Pair<Integer, Integer>> monsters) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_MONSTER_INFO.getValue());
        oPacket.EncodeInteger(monsters.size());
        for (Pair<Integer, Integer> i : monsters) {
            oPacket.EncodeInteger(i.getLeft());
            oPacket.EncodeInteger(i.getRight());
        }

        return oPacket.ToPacket();
    }

    public static Packet updateAesthetic(int quantity) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AESTHETIC_POINT.getValue());
        oPacket.EncodeInteger(quantity);

        return oPacket.ToPacket();
    }

    public static Packet spawnFarmMonster1() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SPAWN_FARM_MONSTER1.getValue());
        oPacket.EncodeInteger(0);
        oPacket.Encode(1);
        oPacket.EncodeInteger(0); //if 1 then same as spawnmonster2 but last byte is 1

        return oPacket.ToPacket();
    }

    public static Packet farmPacket1() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_PACKET1.getValue());
        oPacket.Fill(0, 4);

        return oPacket.ToPacket();
    }

    public static Packet farmPacket4() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_PACKET4.getValue());
        oPacket.Fill(0, 4);

        return oPacket.ToPacket();
    }

    public static Packet updateQuestInfo(int id, int mode, String data) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_QUEST_INFO.getValue());
        oPacket.EncodeInteger(id);
        oPacket.Encode((byte) mode);
        oPacket.EncodeString(data);

        return oPacket.ToPacket();
    }

    public static Packet farmMessage(String msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_MESSAGE.getValue());
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet updateItemQuantity(int id, int quantity) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_ITEM_GAIN.getValue());
        oPacket.EncodeInteger(id);
        oPacket.EncodeInteger(quantity);

        return oPacket.ToPacket();
    }

    public static Packet itemPurchased(int id) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_ITEM_PURCHASED.getValue());
        oPacket.EncodeInteger(id);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet showExpGain(int quantity, int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_EXP.getValue());
        oPacket.EncodeInteger(quantity);
        oPacket.EncodeInteger(mode);

        return oPacket.ToPacket();
    }

    public static Packet updateWaru(int quantity) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UPDATE_WARU.getValue());
        oPacket.EncodeInteger(quantity);

        return oPacket.ToPacket();
    }

    public static Packet showWaruHarvest(int slot, int quantity) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.HARVEST_WARU.getValue());
        oPacket.Encode(0);
        oPacket.EncodeInteger(slot);
        oPacket.EncodeInteger(quantity);

        return oPacket.ToPacket();
    }

    public static Packet spawnFarmMonster(MapleClient c, int id) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SPAWN_FARM_MONSTER2.getValue());
        oPacket.EncodeInteger(0);
        oPacket.Encode(1);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(c.getFarm().getId());
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(id);
        oPacket.EncodeString(""); //monster.getName()
        oPacket.EncodeInteger(1); //level?
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(15);
        oPacket.EncodeInteger(3); //monster.getNurturesLeft()
        oPacket.EncodeInteger(20); //monster.getPlaysLeft()
        oPacket.EncodeInteger(0);
        long time = System.currentTimeMillis(); //should be server time
        oPacket.EncodeLong(PacketHelper.getTime(time));
        oPacket.EncodeLong(PacketHelper.getTime(time + 25920000000000L));
        oPacket.EncodeLong(PacketHelper.getTime(time + 25920000000000L));
        for (int i = 0; i < 4; i++) {
            oPacket.EncodeLong(PacketHelper.getTime(time));
        }
        oPacket.EncodeInteger(-1);
        oPacket.EncodeInteger(-1);
        oPacket.Fill(0, 12);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet updateMonster(List<Pair<Integer, Long>> monsters) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UPDATE_MONSTER.getValue());
        oPacket.Encode(monsters.size());
        for (Pair<Integer, Long> monster : monsters) {
            oPacket.EncodeInteger(monster.getLeft()); //mob id as regular monster
            oPacket.EncodeLong(PacketHelper.getTime(monster.getRight())); //expire
        }

        return oPacket.ToPacket();
    }

    public static Packet updateMonsterQuantity(int itemId, int monsterId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_MONSTER_GAIN.getValue());
        oPacket.Encode(0);
        oPacket.EncodeInteger(itemId);
        oPacket.Encode(1);
        oPacket.EncodeInteger(monsterId);
        oPacket.EncodeInteger(1); //quantity?

        return oPacket.ToPacket();
    }

    public static Packet renameMonster(int index, String name) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.RENAME_MONSTER.getValue());
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(index);
        oPacket.EncodeString(name);

        return oPacket.ToPacket();
    }

    public static Packet updateFarmFriends(List<MapleFarm> friends) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_FRIENDS.getValue());
        oPacket.EncodeInteger(friends.size());
        for (MapleFarm f : friends) {
            oPacket.EncodeInteger(f.getId());
            oPacket.EncodeString(f.getName());
            oPacket.Fill(0, 5);
        }
        oPacket.EncodeInteger(0); //blocked?
        oPacket.EncodeInteger(0); //follower

        return oPacket.ToPacket();
    }

    public static Packet updateFarmInfo(MapleClient c) {
        return updateFarmInfo(c, false);
    }

    public static Packet updateFarmInfo(MapleClient c, boolean newname) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_INFO.getValue());
        oPacket.EncodeInteger(c.getFarm().getId()); //Farm ID
        oPacket.EncodeInteger(0);
        oPacket.EncodeLong(0); //decodeMoney ._.

        //first real farm info
        PacketHelper.addFarmInfo(oPacket, c, 2);
        oPacket.Encode(0);

        //then fake farm info
        if (newname) {
            oPacket.EncodeString("Creating...");
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);

            oPacket.Encode(2);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(1);
        } else { //or real info again incase name wasn't chosen this time
            PacketHelper.addFarmInfo(oPacket, c, 2);
        }
        oPacket.Encode(0);

        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(-1);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet updateUserFarmInfo(User chr, boolean update) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_USER_INFO.getValue());
        oPacket.Encode(update);
        if (update) {
            oPacket.EncodeInteger(chr.getWorld());
            oPacket.EncodeString(WorldConstants.getNameById(chr.getWorld()));
            oPacket.EncodeInteger(chr.getId()); //Not sure if character id or farm id
            oPacket.EncodeString(chr.getName());
        }

        return oPacket.ToPacket();
    }

    public static Packet sendFarmRanking(User chr, List<Pair<MapleFarm, Integer>> rankings) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_RANKING.getValue());
        oPacket.EncodeInteger(0); //Visitors
        oPacket.EncodeInteger(0); //Playtime
        oPacket.EncodeInteger(0); //Combinations
        oPacket.EncodeInteger(rankings.size());
        int i = 0;
        for (Pair<MapleFarm, Integer> best : rankings) {
            oPacket.EncodeInteger(i); //Type; 0 = visitors 1 = playtime 2 = combinations
            oPacket.EncodeInteger(best.getLeft().getId());
            oPacket.EncodeString(best.getLeft().getName());
            oPacket.EncodeInteger(best.getRight()); //Value of type
            if (i < 2) {
                i++;
            }
        }
        oPacket.Encode(0); //Boolean; enable or disable entry reward button

        return oPacket.ToPacket();
    }

    public static Packet updateAvatar(Pair<WorldOption, User> from, Pair<WorldOption, User> to, boolean change) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FARM_AVATAR.getValue());
        oPacket.Encode(change);
        oPacket.EncodeInteger(from.getLeft().getWorld());
        oPacket.EncodeString(WorldConstants.getNameById(from.getLeft().getWorld()));
        oPacket.EncodeInteger(from.getRight().getId());
        oPacket.EncodeString(from.getRight().getName());
        if (change) {
            oPacket.EncodeInteger(to.getLeft().getWorld());
            oPacket.EncodeString(WorldConstants.getNameById(to.getLeft().getWorld()));
            oPacket.EncodeInteger(to.getRight().getId());
            oPacket.EncodeString(to.getRight().getName());
        }

        return oPacket.ToPacket();
    }
}
