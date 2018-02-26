package tools.packet;

import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.maps.MapleMap;
import server.maps.objects.MapleCharacter;

/**
 *
 * @author LEL
 */
public class EvolvingPacket {

    public static Packet showEvolvingMessage(int action) {
        OutPacket oPacket = new OutPacket(80);
        //24 00 1B 01 00
        oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
        oPacket.EncodeShort(284);
        oPacket.Encode(action);
        return oPacket.ToPacket();
    }

    public static Packet partyCoreInfo(int[] core) {
        OutPacket oPacket = new OutPacket(80);
        //AF 00 /00 /48 EF 36 00 /D3 FB 36 00 /00 00 00 00 /00 00 00 00/ 00 00 00 00/ 00 00 00 00 /00 00 00 00 /32 F3 36 00 /00 00 00 00 /00 00 00 00
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.Encode(0);
        for (int i = 0; i < 10; i++) {
            oPacket.EncodeInteger(core[i]);
        }
        return oPacket.ToPacket();
    }

    public static Packet showPartyConnect(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.Encode(1);
        oPacket.Encode(1);
        oPacket.Encode(chr.getParty().getLeader().getId() == chr.getId() ? 1 : 0);
        return oPacket.ToPacket();
    }

    public static Packet connectCancel() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.EncodeShort(1);
        return oPacket.ToPacket();
    }

    public static Packet rewardCore(int itemid, int position) {
        //AF 00 02 01 00 00 00 00 00 D0 F2 36 00 01 00 00 00
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.Encode(2); //슬롯?
        oPacket.Encode(1);
        oPacket.EncodeInteger(0);
        oPacket.Encode(position);
        oPacket.EncodeInteger(itemid);
        oPacket.EncodeInteger(1);
        return oPacket.ToPacket();
    }

    public static Packet showRewardCore(int itemid) {
        //24 00 1D 16 D0 F2 36 00 01 00 00 00
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
        oPacket.EncodeShort(5662);
        oPacket.EncodeInteger(itemid);
        oPacket.EncodeInteger(1);
        return oPacket.ToPacket();
    }

    public static Packet moveCore(byte equip, byte slot, byte move, byte to) {
        //AF 00 03 00 01 02 01 03
        //AF 00 03 00 01 03 01 04
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.Encode(3);
        oPacket.Encode(0);
        oPacket.Encode(equip);//무브, 장착해제 : 1, 장착 : 0
        oPacket.Encode(slot);
        oPacket.Encode(move);//장착, 무브 : 1, 장착해제 : 0
        oPacket.Encode(to);
        return oPacket.ToPacket();
    }

    public static Packet dropCore(byte position, short quantity) {
        //AF 00 04 01 /00 /01 00 /00 00
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EvolvingResult.getValue());//
        oPacket.Encode(4);
        oPacket.Encode(1);
        oPacket.Encode(position);
        oPacket.EncodeShort(quantity);//1
        oPacket.EncodeShort(0);
        return oPacket.ToPacket();
    }
}
