package net;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.Charset;
import util.HexTool;
import util.IntegerValue;

/**
 * Artifact from Invictus. Modified by Zygon for Desu, modified by Novak for
 * ResistanceMS
 *
 * @author Zygon
 * @author Novak (reworked)
 */
public final class OutPacket {

    private int nOffset;
    private byte[] aData;
    private static final Charset ASCII = Charset.forName("US-ASCII");

    public OutPacket(int nLength) {
        nOffset = 0;
        aData = new byte[nLength];
    }

    private void Expand(int nLength) {
        byte[] aExpanded = new byte[nLength];
        System.arraycopy(aData, 0, aExpanded, 0, nOffset);
        aData = aExpanded;
    }

    private void Trim() {
        Expand(nOffset);
    }

    public final OutPacket Encode(int nValue) {
        if (nOffset + 1 >= aData.length) {
            Expand(aData.length * 2);
        }
        aData[nOffset++] = (byte) nValue;
        return this;
    }

    private OutPacket Encode(long nValue) {
        return Encode((int) nValue);
    }

    public final OutPacket Encode(byte[] aData) {
        return Encode(aData, 0, aData.length);
    }

    public final OutPacket Encode(byte[] aData, int nOffset, int nLength) {
        for (int i = nOffset; i < nLength; i++) {
            Encode(aData[i]);
        }
        return this;
    }

    public final OutPacket Encode(int... aValue) {
        for (int i = 0; i < aValue.length; i++) {
            Encode(aValue[i]);
        }
        return this;
    }

    public final OutPacket EncodeByte(byte nValue) {
        return Encode(nValue);
    }

    public final OutPacket EncodeHeader(IntegerValue nValue) {
        return EncodeShort(nValue.getValue());
    }

    public final OutPacket EncodeShort(int nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8);
    }

    public final OutPacket EncodeShort(short nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8);
    }

    public final OutPacket EncodeChar(char cValue) {
        return EncodeShort(cValue);
    }

    public final OutPacket EncodeInteger(int nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8).Encode(nValue >>> 16).Encode(nValue >>> 24);
    }

    public final OutPacket EncodeFloat(float nValue) {
        return EncodeInteger(Float.floatToIntBits(nValue));
    }

    public final OutPacket EncodeLong(long nValue) {
        return Encode(nValue & 0xFF).Encode(nValue >>> 8).Encode(nValue >>> 16).
                Encode(nValue >>> 24).Encode(nValue >>> 32).Encode(nValue >>> 40).
                Encode(nValue >>> 48).Encode(nValue >>> 56);
    }

    public final OutPacket EncodeDouble(double nValue) {
        return EncodeLong(Double.doubleToLongBits(nValue));
    }

    public final OutPacket EncodeString(String sData, int nLen) {
        byte[] string = sData.getBytes(ASCII);
        byte[] fill = new byte[nLen - string.length];
        return Encode(string).Encode(fill);
    }

    public final OutPacket EncodeString(String sData) {
        return EncodeShort(sData.length()).Encode(sData.getBytes(ASCII));
    }
    
    public final OutPacket encodeString(String sData, boolean bEncodeLen) {
        if (bEncodeLen) {
            Encode((short) (sData.length()));
        }
        Encode(sData.getBytes(ASCII));
        return this;
    }

    public final OutPacket EncodeNullTerminatedString(String sData) {
        return Encode(sData.getBytes(ASCII)).Encode(0);
    }

    public final OutPacket EncodeHex(String sData) {
        return Encode(HexTool.toBytes(sData));
    }

    public final OutPacket EncodeBool(boolean bData) {
        return Encode(bData ? 1 : 0);
    }
    
    public final OutPacket Encode(boolean bData) {
        return EncodeBool(bData);
    }

    public final OutPacket Fill(int nValue, int nLenth) {
        for (int i = 0; i < nLenth; i++) {
            Encode(nValue);
        }
        return this;
    }

    public final OutPacket EncodePosition(Point pData) {
        return EncodeShort(pData.x).EncodeShort(pData.y);
    }

    public final OutPacket EncodeRectangle(Rectangle rData) {
        return EncodeInteger(rData.x).EncodeInteger(rData.y)
                .EncodeInteger(rData.x + rData.width).EncodeInteger(rData.y + rData.height);
    }

    public final int GetOffset() {
        return nOffset;
    }

    public final byte[] GetData() {
        return aData;
    }

    public final void Clear() {
        nOffset = -1;
        aData = null;
    }

    @Override
    public final String toString() {
        return HexTool.toHex(aData);
    }

    public final byte[] Data() {
        if (aData != null) {
            if (aData.length > nOffset) {
                Trim();
            }
            return aData;
        }
        return null;
    }

    public final Packet ToPacket() {
        if (aData != null) {
            if (aData.length > nOffset) {
                Trim();
            }
            return new Packet(aData);
        }
        return null;
    }
}
