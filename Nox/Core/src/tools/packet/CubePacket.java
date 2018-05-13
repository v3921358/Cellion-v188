package tools.packet;

import client.inventory.Equip;
import service.SendPacketOpcode;
import net.OutPacket;

/**
 * Cube Packets
 * @author Mazen Massoud
 */
public class CubePacket {

    /**
     * Cube Result Packets
     * 
     * @param nCharID
     * @param bUpgrade
     * @param nPOS
     * @param nCubeID
     * @param pEquip
     * @return 
     */
    public static OutPacket OnInGameCubeResult(int nCharID, boolean bUpgrade, int nPOS, int nCubeID, Equip pEquip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemInGameCubeResult.getValue());
        
        oPacket.EncodeInt(nCharID);
        oPacket.EncodeBool(bUpgrade);
        oPacket.EncodeInt(nCubeID);
        oPacket.EncodeInt(nPOS);
        PacketHelper.addItemInfo(oPacket, pEquip);

        return oPacket;
    }
    
    public static OutPacket OnRedCubeResult(int nCharID, boolean bUpgrade, int nPOS, int nCubeID, Equip pEquip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemRedCubeResult.getValue());
        oPacket.EncodeInt(nCharID);
        oPacket.EncodeBool(bUpgrade);
        oPacket.EncodeInt(nCubeID); // nRedCubeID = 5062009
        oPacket.EncodeInt(nPOS);
        PacketHelper.addItemInfo(oPacket, pEquip);

        return oPacket;
    }
    
    public static OutPacket OnPlatinumCubeResult(int nCharID, boolean bUpgrade, short nPOS, int nCubeID, Equip pEquip) {
        
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MemorialCubeResult.getValue());
        
        oPacket.EncodeInt(nCharID);
        oPacket.EncodeBool(bUpgrade);
        oPacket.EncodeInt(nCubeID); 
        oPacket.EncodeInt(nPOS);
        PacketHelper.addItemInfo(oPacket, pEquip);
        
        return oPacket;
    }
    
    public static OutPacket OnBonusCubeResult(int nCharID, boolean bUpgrade, int nPOS, int nCubeID, Equip pEquip) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemBonusCubeResult.getValue());
        
        oPacket.EncodeInt(nCharID);
        oPacket.EncodeBool(bUpgrade);
        oPacket.EncodeInt(nCubeID); 
        oPacket.EncodeInt(nPOS);
        PacketHelper.addItemInfo(oPacket, pEquip);

        return oPacket;
    }
    
    
    /**
     * onMemorialCubeResult
     * @author Lloyd Korn
     * 
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
     * memorialCubeModified
     * @author Lloyd Korn
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
     * memorialCubeInfo_Decode
     * Encodes the memorial cube information for the client
     * @author Lloyd Korn
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
     * onBlackCubeResult
     * Displays the black cube result.
     * @author Lloyd Korn
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
}
