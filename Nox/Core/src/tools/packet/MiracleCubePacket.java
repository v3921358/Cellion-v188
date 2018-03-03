package tools.packet;

import client.inventory.Equip;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;

/**
 *
 * @author Lloyd Korn
 */
public class MiracleCubePacket {

    /**
     * KMST PDB: http://pastebin.com/MctEdFJk NOT IN GMS v170
     *
     * @param newstateIdentificationID
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return * public static Packet onWhiteAdditionalCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int cubeid,
     * Equip equip) { OutPacket oPacket = new OutPacket();
     *
     * oPacket.encodeShort(SendPacketOpcode.BLACK_CUBE_RESULT.getValue()); oPacket.encodeLong(newstateIdentificationID); //
     * CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079
     * anyway
     *
     * final boolean unk = true; oPacket.encode(unk); if (unk) { PacketHelper.addItemInfo(oPacket, equip); // v5 =
     * GW_ItemSlotBase::Decode(&result, iPacket); oPacket.encodeInteger(cubeid); oPacket.encodeInteger(eqSlotId); }
     * oPacket.encodeInteger(upgrade ? 1 : 0);
     *
     * return oPacket.createPacket(); }
     */
    /**
     * KMST PDB: http://pastebin.com/qT4yG3TX GMS PDB: http://pastebin.com/ZGZL8JSw
     *
     * @param newstateIdentificationID
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet onMemorialCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MemorialCubeResult.getValue());
        oPacket.EncodeLong(newstateIdentificationID); // CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079 anyway

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        oPacket.EncodeInteger(upgrade ? 1 : 0);

        return oPacket.ToPacket();
    }

    /**
     *
     * @param newstateIdentificationID
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet memorialCubeModified(boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MemorialCubeModified.getValue());

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        return oPacket.ToPacket();
    }

    /**
     * Encodes the memorial cube information for the client
     *
     * @param oPacket
     * @param eqSlotId
     * @param cubeid
     * @param equip
     */
    public static void memorialCubeInfo_Decode(OutPacket oPacket, int eqSlotId, int cubeid, Equip equip) {
        final boolean unk = true;
        oPacket.Encode(unk);
        if (unk) {
            PacketHelper.addItemInfo(oPacket, equip); // v5 = GW_ItemSlotBase::Decode(&result, iPacket);
            oPacket.EncodeInteger(cubeid);
            oPacket.EncodeInteger(eqSlotId);
        }
    }

    /**
     * Displays the black cube result.
     *
     * KMST PDB: http://pastebin.com/2nkBXevK Packet log: http://pastebin.com/uPdDq18p
     *
     * @param newstateIdentificationID
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet onBlackCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BlackCubeResult.getValue());
        oPacket.EncodeLong(newstateIdentificationID); // CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079 anyway

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        oPacket.EncodeInteger(upgrade ? 1 : 0);

        return oPacket.ToPacket();
    }

    /**
     * http://pastebin.com/2P2jP55t
     *
     * @param charid
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet onInGameCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemInGameCubeResult.getValue());
        oPacket.EncodeInteger(charid);
        oPacket.Encode(upgrade);
        oPacket.EncodeInteger(cubeid);
        oPacket.EncodeInteger(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);

        return oPacket.ToPacket();
    }

    /**
     * http://pastebin.com/TnZcWB01
     *
     * @param charid
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet onRedCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemRedCubeResult.getValue());
        oPacket.EncodeInteger(charid);
        oPacket.Encode(upgrade);
        oPacket.EncodeInteger(cubeid);//(5062009); // CubeID
        oPacket.EncodeInteger(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);
        PacketHelper.addItemPosition(oPacket, equip, false, false);

        return oPacket.ToPacket();
    }

    /**
     * http://pastebin.com/TnZcWB01
     *
     * @param charid
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static Packet onBonusCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemBonusCubeResult.getValue());
        oPacket.EncodeInteger(charid);
        oPacket.Encode(upgrade);
        oPacket.EncodeInteger(cubeid);//(5062009); // CubeID
        oPacket.EncodeInteger(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);
        PacketHelper.addItemPosition(oPacket, equip, false, false);

        return oPacket.ToPacket();
    }
}
