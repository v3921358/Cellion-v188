/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.skills;

import net.InPacket;
import net.OutPacket;
import tools.packet.PacketHelper;

/**
 *
 * @author Five
 */
public class VMatrixRecord {

    public int nState, nCoreID, nSkillID, nSkillID2, nSkillID3, nSLV = 1, nMasterLev, nExp;
    public long ftExpirationDate = PacketHelper.getTime(-1);
    public static int Disassembled = 0, Inactive = 1, Active = 2; // nState
    public static int Enable = 0, Disable = 1, Enhance = 3, DisassembleSingle = 4, DisassembleMultiple = 5, CraftNode = 6, CraftNodestone = 8; // MatrixUpdate Type

    public VMatrixRecord() {
        this.nState = Inactive;
    }

    public void Decode(InPacket iPacket) {
        iPacket.DecodeLong(); // Unknown
        this.nCoreID = iPacket.DecodeInt();
        this.nSLV = iPacket.DecodeInt();
        this.nExp = iPacket.DecodeInt();
        this.nState = iPacket.DecodeInt();
        this.nSkillID = iPacket.DecodeInt();
        this.nSkillID2 = iPacket.DecodeInt();
        this.nSkillID3 = iPacket.DecodeInt();
        this.ftExpirationDate = iPacket.DecodeLong();
    }

    public void Encode(OutPacket oPacket) {
        oPacket.EncodeLong((long) Math.random()); // Some kind of counter...
        oPacket.EncodeInt(this.nCoreID);
        oPacket.EncodeInt(this.nSLV);
        oPacket.EncodeInt(this.nExp);
        oPacket.EncodeInt(this.nState);
        oPacket.EncodeInt(this.nSkillID);
        oPacket.EncodeInt(this.nSkillID2);
        oPacket.EncodeInt(this.nSkillID3);
        oPacket.EncodeLong(this.ftExpirationDate);
    }
}
