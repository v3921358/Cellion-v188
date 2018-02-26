package net;

import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.Charset;

/**
 * Artifact from Invictus. Modified by Zygon for Desu, modified by Novak for
 * ResistanceMS
 *
 * @author Zygon
 * @author Novak (reworked)
 */
public final class InPacket {

    private int nOffset;
    private byte[] aData;
    private static Charset ASCII = Charset.forName("US-ASCII");

    public InPacket() {
        nOffset = -1;
        aData = null;
    }

    public InPacket Next(byte[] d) {
        nOffset = 0;
        aData = d;
        return this;
    }

    public InPacket Next(Packet p) {
        return InPacket.this.Next(p.GetData());
    }

    public int Decode() {
        try {
            return 0xFF & aData[nOffset++];
        } catch (Exception e) {
            return -1;
        }
    }

    public void Decode(byte[] in) {
        InPacket.this.Decode(in, 0, in.length);
    }

    public void Decode(byte[] in, int nOffset, int nLen) {
        for (int i = nOffset; i < nLen; i++) {
            in[i] = DecodeByte();
        }
    }

    public byte[] Decode(int nLen) {
        byte[] ret = new byte[nLen];
        for (int i = 0; i < nLen; i++) {
            ret[i] = DecodeByte();
        }
        return ret;
    }

    public boolean DecodeBoolean() {
        return InPacket.this.Decode() > 0;
    }

    public byte DecodeByte() {
        return (byte) InPacket.this.Decode();
    }

    public short DecodeShort() {
        return (short) (InPacket.this.Decode() + (InPacket.this.Decode() << 8));
    }

    public char DecodeChar() {
        return (char) (InPacket.this.Decode() + (InPacket.this.Decode() << 8));
    }

    public int DecodeInteger() {
        return InPacket.this.Decode() + (InPacket.this.Decode() << 8) + (InPacket.this.Decode() << 16)
                + (InPacket.this.Decode() << 24);
    }

    public float DecodeFloat() {
        return Float.intBitsToFloat(DecodeInteger());
    }

    public long DecodeLong() {
        return InPacket.this.Decode() + (InPacket.this.Decode() << 8) + (InPacket.this.Decode() << 16)
                + (InPacket.this.Decode() << 24) + (InPacket.this.Decode() << 32)
                + (InPacket.this.Decode() << 40) + (InPacket.this.Decode() << 48)
                + (InPacket.this.Decode() << 56);
    }

    public double DecodeDouble() {
        return Double.longBitsToDouble(DecodeLong());
    }

    public String DecodeString(int len) {
        byte[] sd = new byte[len];
        for (int i = 0; i < len; i++) {
            sd[i] = DecodeByte();
        }
        return new String(sd, ASCII);
    }

    public String DecodeString() {
        return InPacket.this.DecodeString(DecodeShort());
    }

    public String DecodeNullTerminatedString() {
        int c = 0;
        while (InPacket.this.Decode() != 0) {
            c++;
        }
        nOffset -= (c + 1);
        return InPacket.this.DecodeString(c);
    }

    public Point DecodePosition() {
        return new Point(this.DecodeShort(), this.DecodeShort());
    }
    
    public Rectangle DecodeRectanlge() {
        return new Rectangle(this.DecodeShort(), this.DecodeShort());
    }

    public InPacket Skip(int nLen) {
        nOffset += nLen;
        return this;
    }

    public int Available() {
        return aData.length - nOffset;
    }

    public int GetOffset() {
        return nOffset;
    }

    public byte[] GetData() {
        return aData;
    }

    public void Clear() {
        nOffset = -1;
        aData = null;
    }

    public byte[] GetRemainder() {
        byte[] remainder = new byte[Available()];
        System.arraycopy(aData, nOffset, remainder, 0, Available());
        return remainder;
    }

    public void Reverse(int nLength) {
        nOffset -= nLength;
    }
}
