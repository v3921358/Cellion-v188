package tools.packet;

import java.util.LinkedList;
import java.util.List;

import client.Client;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import service.SendPacketOpcode;
import net.OutPacket;

import server.farm.MapleFarm;
import server.maps.objects.User;
import tools.Pair;

/**
 *
 * @author Itzik
 */
public class FarmPacket {

    public static OutPacket enterFarm(Client c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetFarmField.getValue());
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
            oPacket.EncodeInt(housePosition ? houseId : 0); //building that the position contains
            oPacket.EncodeInt(i == houseBase ? houseId : 0); //building that the position bases
            oPacket.Fill(0, 5);
            oPacket.EncodeLong(PacketHelper.getTime(time));
        }
        oPacket.EncodeInt(14);
        oPacket.EncodeInt(14);
        oPacket.EncodeInt(0);
        oPacket.EncodeLong(PacketHelper.getTime(time + 180000));

        return oPacket;
    }

    public static OutPacket farmQuestData(List<Pair<Integer, String>> canStart, List<Pair<Integer, String>> completed) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_QUEST_DATA.getValue());
        oPacket.EncodeInt(canStart.size());
        for (Pair<Integer, String> i : canStart) {
            oPacket.EncodeInt(i.getLeft());
            oPacket.EncodeString(i.getRight());
        }
        oPacket.EncodeInt(completed.size());
        for (Pair<Integer, String> i : completed) {
            oPacket.EncodeInt(i.getLeft());
            oPacket.EncodeString(i.getRight());
        }

        return oPacket;
    }

    public static OutPacket alertQuest(int questId, int status) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.QUEST_ALERT.getValue());
        oPacket.EncodeInt(questId);
        oPacket.EncodeByte((byte) status);

        return oPacket;
    }

    public static OutPacket updateMonsterInfo(List<Pair<Integer, Integer>> monsters) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_MONSTER_INFO.getValue());
        oPacket.EncodeInt(monsters.size());
        for (Pair<Integer, Integer> i : monsters) {
            oPacket.EncodeInt(i.getLeft());
            oPacket.EncodeInt(i.getRight());
        }

        return oPacket;
    }

    public static OutPacket updateAesthetic(int quantity) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AESTHETIC_POINT.getValue());
        oPacket.EncodeInt(quantity);

        return oPacket;
    }

    public static OutPacket spawnFarmMonster1() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SPAWN_FARM_MONSTER1.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(0); //if 1 then same as spawnmonster2 but last byte is 1

        return oPacket;
    }

    public static OutPacket farmPacket1() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_PACKET1.getValue());
        oPacket.Fill(0, 4);

        return oPacket;
    }

    public static OutPacket farmPacket4() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_PACKET4.getValue());
        oPacket.Fill(0, 4);

        return oPacket;
    }

    public static OutPacket updateQuestInfo(int id, int mode, String data) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_QUEST_INFO.getValue());
        oPacket.EncodeInt(id);
        oPacket.EncodeByte((byte) mode);
        oPacket.EncodeString(data);

        return oPacket;
    }

    public static OutPacket farmMessage(String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_MESSAGE.getValue());
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket updateItemQuantity(int id, int quantity) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_ITEM_GAIN.getValue());
        oPacket.EncodeInt(id);
        oPacket.EncodeInt(quantity);

        return oPacket;
    }

    public static OutPacket itemPurchased(int id) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_ITEM_PURCHASED.getValue());
        oPacket.EncodeInt(id);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket showExpGain(int quantity, int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_EXP.getValue());
        oPacket.EncodeInt(quantity);
        oPacket.EncodeInt(mode);

        return oPacket;
    }

    public static OutPacket updateWaru(int quantity) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UPDATE_WARU.getValue());
        oPacket.EncodeInt(quantity);

        return oPacket;
    }

    public static OutPacket showWaruHarvest(int slot, int quantity) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.HARVEST_WARU.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(slot);
        oPacket.EncodeInt(quantity);

        return oPacket;
    }

    public static OutPacket spawnFarmMonster(Client c, int id) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SPAWN_FARM_MONSTER2.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(c.getFarm().getId());
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(id);
        oPacket.EncodeString(""); //monster.getName()
        oPacket.EncodeInt(1); //level?
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(15);
        oPacket.EncodeInt(3); //monster.getNurturesLeft()
        oPacket.EncodeInt(20); //monster.getPlaysLeft()
        oPacket.EncodeInt(0);
        long time = System.currentTimeMillis(); //should be server time
        oPacket.EncodeLong(PacketHelper.getTime(time));
        oPacket.EncodeLong(PacketHelper.getTime(time + 25920000000000L));
        oPacket.EncodeLong(PacketHelper.getTime(time + 25920000000000L));
        for (int i = 0; i < 4; i++) {
            oPacket.EncodeLong(PacketHelper.getTime(time));
        }
        oPacket.EncodeInt(-1);
        oPacket.EncodeInt(-1);
        oPacket.Fill(0, 12);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket updateMonster(List<Pair<Integer, Long>> monsters) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UPDATE_MONSTER.getValue());
        oPacket.EncodeByte(monsters.size());
        for (Pair<Integer, Long> monster : monsters) {
            oPacket.EncodeInt(monster.getLeft()); //mob id as regular monster
            oPacket.EncodeLong(PacketHelper.getTime(monster.getRight())); //expire
        }

        return oPacket;
    }

    public static OutPacket updateMonsterQuantity(int itemId, int monsterId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_MONSTER_GAIN.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(itemId);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(monsterId);
        oPacket.EncodeInt(1); //quantity?

        return oPacket;
    }

    public static OutPacket renameMonster(int index, String name) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RENAME_MONSTER.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(index);
        oPacket.EncodeString(name);

        return oPacket;
    }

    public static OutPacket updateFarmFriends(List<MapleFarm> friends) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_FRIENDS.getValue());
        oPacket.EncodeInt(friends.size());
        for (MapleFarm f : friends) {
            oPacket.EncodeInt(f.getId());
            oPacket.EncodeString(f.getName());
            oPacket.Fill(0, 5);
        }
        oPacket.EncodeInt(0); //blocked?
        oPacket.EncodeInt(0); //follower

        return oPacket;
    }

    public static OutPacket updateFarmInfo(Client c) {
        return updateFarmInfo(c, false);
    }

    public static OutPacket updateFarmInfo(Client c, boolean newname) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_INFO.getValue());
        oPacket.EncodeInt(c.getFarm().getId()); //Farm ID
        oPacket.EncodeInt(0);
        oPacket.EncodeLong(0); //decodeMoney ._.

        //first real farm info
        PacketHelper.addFarmInfo(oPacket, c, 2);
        oPacket.EncodeByte(0);

        //then fake farm info
        if (newname) {
            oPacket.EncodeString("Creating...");
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);

            oPacket.EncodeByte(2);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(1);
        } else { //or real info again incase name wasn't chosen this time
            PacketHelper.addFarmInfo(oPacket, c, 2);
        }
        oPacket.EncodeByte(0);

        oPacket.EncodeInt(0);
        oPacket.EncodeInt(-1);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket updateUserFarmInfo(User chr, boolean update) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_USER_INFO.getValue());
        oPacket.EncodeBool(update);
        if (update) {
            oPacket.EncodeInt(chr.getWorld());
            oPacket.EncodeString(WorldConstants.getNameById(chr.getWorld()));
            oPacket.EncodeInt(chr.getId()); //Not sure if character id or farm id
            oPacket.EncodeString(chr.getName());
        }

        return oPacket;
    }

    public static OutPacket sendFarmRanking(User chr, List<Pair<MapleFarm, Integer>> rankings) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_RANKING.getValue());
        oPacket.EncodeInt(0); //Visitors
        oPacket.EncodeInt(0); //Playtime
        oPacket.EncodeInt(0); //Combinations
        oPacket.EncodeInt(rankings.size());
        int i = 0;
        for (Pair<MapleFarm, Integer> best : rankings) {
            oPacket.EncodeInt(i); //Type; 0 = visitors 1 = playtime 2 = combinations
            oPacket.EncodeInt(best.getLeft().getId());
            oPacket.EncodeString(best.getLeft().getName());
            oPacket.EncodeInt(best.getRight()); //Value of type
            if (i < 2) {
                i++;
            }
        }
        oPacket.EncodeByte(0); //Boolean; enable or disable entry reward button

        return oPacket;
    }

    public static OutPacket updateAvatar(Pair<WorldOption, User> from, Pair<WorldOption, User> to, boolean change) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FARM_AVATAR.getValue());
        oPacket.EncodeBool(change);
        oPacket.EncodeInt(from.getLeft().getWorld());
        oPacket.EncodeString(WorldConstants.getNameById(from.getLeft().getWorld()));
        oPacket.EncodeInt(from.getRight().getId());
        oPacket.EncodeString(from.getRight().getName());
        if (change) {
            oPacket.EncodeInt(to.getLeft().getWorld());
            oPacket.EncodeString(WorldConstants.getNameById(to.getLeft().getWorld()));
            oPacket.EncodeInt(to.getRight().getId());
            oPacket.EncodeString(to.getRight().getName());
        }

        return oPacket;
    }
}
