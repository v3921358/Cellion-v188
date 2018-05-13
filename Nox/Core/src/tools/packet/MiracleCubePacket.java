package tools.packet;

import client.inventory.Equip;
import service.SendPacketOpcode;
import net.OutPacket;

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
     * @return * public static OutPacket onWhiteAdditionalCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int
     * cubeid, Equip equip) { OutPacket oPacket = new OutPacket();
     *
     * OutPacket oPacket = new OutPacket(SendPacketOpcode.BLACK_CUBE_RESULT.getValue()); oPacket.encodeLong(newstateIdentificationID); //
     * CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079
     * anyway
     *
     * final boolean unk = true; oPacket.encode(unk); if (unk) { PacketHelper.addItemInfo(oPacket, equip); // v5 =
     * GW_ItemSlotBase::Decode(&result, iPacket); oPacket.EncodeInt(cubeid); oPacket.EncodeInt(eqSlotId); } oPacket.EncodeInt(upgrade ? 1 :
     * 0);
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
    public static OutPacket onMemorialCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MemorialCubeResult.getValue());
        oPacket.EncodeLong(newstateIdentificationID); // CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079 anyway

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        oPacket.EncodeInt(upgrade ? 1 : 0);

        return oPacket;
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
    public static OutPacket memorialCubeModified(boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MemorialCubeModified.getValue());

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        return oPacket;
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
        oPacket.EncodeBool(unk);
        if (unk) {
            PacketHelper.addItemInfo(oPacket, equip); // v5 = GW_ItemSlotBase::Decode(&result, iPacket);
            oPacket.EncodeInt(cubeid);
            oPacket.EncodeInt(eqSlotId);
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
    public static OutPacket onBlackCubeResult(long newstateIdentificationID, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BlackCubeResult.getValue());
        oPacket.EncodeLong(newstateIdentificationID); // CInPacket::DecodeBuffer(iPacket, &liSN, 8u); // supposed to be uniqueid, odin always write it as 00 80 05 BB 46 E6 17 02, 1/1/2079 anyway

        memorialCubeInfo_Decode(oPacket, eqSlotId, cubeid, equip);

        oPacket.EncodeInt(upgrade ? 1 : 0);

        return oPacket;
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
    public static OutPacket onInGameCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemInGameCubeResult.getValue());
        oPacket.EncodeInt(charid);
        oPacket.EncodeBool(upgrade);
        oPacket.EncodeInt(cubeid);
        oPacket.EncodeInt(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);

        return oPacket;
    }

    /**
     * Red Cube Packet
     * http://pastebin.com/TnZcWB01
     *
     * @param charid
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static OutPacket onRedCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemRedCubeResult.getValue());
        oPacket.EncodeInt(charid);
        oPacket.EncodeBool(upgrade);
        oPacket.EncodeInt(cubeid); // CubeID = 5062009
        oPacket.EncodeInt(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);
        //PacketHelper.addItemPosition(oPacket, equip, false, false);

        return oPacket;
    }

    /**
     * Bonus Cube Packet
     *
     * @param charid
     * @param upgrade
     * @param eqSlotId
     * @param cubeid
     * @param equip
     * @return
     */
    public static OutPacket onBonusCubeResult(int charid, boolean upgrade, int eqSlotId, int cubeid, Equip equip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemBonusCubeResult.getValue());
        oPacket.EncodeInt(charid);
        oPacket.EncodeBool(upgrade);
        oPacket.EncodeInt(cubeid); 
        oPacket.EncodeInt(eqSlotId);
        PacketHelper.addItemInfo(oPacket, equip);

        return oPacket;
    }
}
