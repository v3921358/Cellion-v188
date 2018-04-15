package tools.packet;

import service.SendPacketOpcode;
import net.OutPacket;

import server.maps.MapleMap;
import server.maps.objects.User;

/**
 *
 * @author LEL
 */
public class EvolvingPacket {

    public static OutPacket showEvolvingMessage(int action) {

        //24 00 1B 01 00
        OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
        oPacket.EncodeShort(284);
        oPacket.EncodeByte(action);
        return oPacket;
    }

    public static OutPacket partyCoreInfo(int[] core) {

        //AF 00 /00 /48 EF 36 00 /D3 FB 36 00 /00 00 00 00 /00 00 00 00/ 00 00 00 00/ 00 00 00 00 /00 00 00 00 /32 F3 36 00 /00 00 00 00 /00 00 00 00
        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeByte(0);
        for (int i = 0; i < 10; i++) {
            oPacket.EncodeInt(core[i]);
        }
        return oPacket;
    }

    public static OutPacket showPartyConnect(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(chr.getParty().getLeader().getId() == chr.getId() ? 1 : 0);
        return oPacket;
    }

    public static OutPacket connectCancel() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeShort(1);
        return oPacket;
    }

    public static OutPacket rewardCore(int itemid, int position) {
        //AF 00 02 01 00 00 00 00 00 D0 F2 36 00 01 00 00 00

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeByte(2); //슬롯?
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(position);
        oPacket.EncodeInt(itemid);
        oPacket.EncodeInt(1);
        return oPacket;
    }

    public static OutPacket showRewardCore(int itemid) {
        //24 00 1D 16 D0 F2 36 00 01 00 00 00

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
        oPacket.EncodeShort(5662);
        oPacket.EncodeInt(itemid);
        oPacket.EncodeInt(1);
        return oPacket;
    }

    public static OutPacket moveCore(byte equip, byte slot, byte move, byte to) {
        //AF 00 03 00 01 02 01 03
        //AF 00 03 00 01 03 01 04

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeByte(3);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(equip);//무브, 장착해제 : 1, 장착 : 0
        oPacket.EncodeByte(slot);
        oPacket.EncodeByte(move);//장착, 무브 : 1, 장착해제 : 0
        oPacket.EncodeByte(to);
        return oPacket;
    }

    public static OutPacket dropCore(byte position, short quantity) {
        //AF 00 04 01 /00 /01 00 /00 00

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeByte(4);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(position);
        oPacket.EncodeShort(quantity);//1
        oPacket.EncodeShort(0);
        return oPacket;
    }
}
