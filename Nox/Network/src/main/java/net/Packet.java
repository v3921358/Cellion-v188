package net;

import util.HexTool;

/**
 * Represents a packet to be sent over a TCP socket for MapleStory. 
 * Very simply, it is an abstraction of raw data that applies some extra 
 * functionality because it is a MapleStory packet.
 * 
 * @author Zygon
 * @author Novak (reworked)
 */
public class Packet {

    private byte[] aData;

    public Packet() {
    }

    public Packet(byte[] aData) {
        this.aData = aData;
    }

    public int GetLength() {
        if (aData != null) {
            return aData.length;
        }
        return 0;
    }

    public int GetHeader() {
        if (aData.length < 2) {
            return 0xFFFF;
        }
        return (aData[0] + (aData[1] << 8));
    }

    public void SetData(byte[] aData) {
        this.aData = aData;
    }

    public byte[] GetData() {
        return aData;
    }
    
    @Override
    public String toString() {
        if (aData == null) return "";
        return HexTool.toHex(aData);
    }
    
    public Packet Clone() {
        byte[] aClone = new byte[aData.length];
        System.arraycopy(aData, 0, aClone, 0, aData.length);
        return new Packet(aClone);
    }
}
