/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net;

import crypto.AESCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.Charset;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class InPacket {

    private final ByteBuf pRecvBuff;
    private static Charset ASCII = Charset.forName("US-ASCII");

    public InPacket(byte[] aData, int uSeqKey, boolean bEncrypt) {
        this.pRecvBuff = Unpooled.buffer();
        if (bEncrypt) {
            AESCipher.Crypt(aData, uSeqKey);
        }
        this.pRecvBuff.writeBytes(aData);
    }

    public void Decode(byte[] aData) {
        Decode(aData, 0, aData.length);
    }

    public void Decode(byte[] aData, int nOffset, int nLen) {
        for (int i = nOffset; i < nLen; i++) {
            aData[i] = DecodeByte();
        }
    }

    public byte[] Decode(int nLen) {
        byte[] aRet = new byte[nLen];
        for (int i = 0; i < nLen; i++) {
            aRet[i] = DecodeByte();
        }
        return aRet;
    }

    public boolean DecodeBool() {
        return pRecvBuff.readBoolean();
    }

    public byte DecodeByte() {
        return pRecvBuff.readByte();
    }

    public short DecodeShort() {
        return pRecvBuff.readShortLE();
    }

    public char DecodeChar() {
        return pRecvBuff.readChar();
    }

    public int DecodeInt() {
        return pRecvBuff.readIntLE();
    }

    public float DecodeFloat() {
        return Float.intBitsToFloat(DecodeInt());
    }

    public long DecodeLong() {
        return pRecvBuff.readLongLE();
    }

    public double DecodeDouble() {
        return Double.longBitsToDouble(DecodeLong());
    }

    public String DecodeString(int nLen) {
        byte[] aData = new byte[nLen];
        for (int i = 0; i < nLen; i++) {
            aData[i] = DecodeByte();
        }
        return new String(aData, ASCII);
    }

    public String DecodeString() {
        return DecodeString(DecodeShort());
    }

    public String DecodeNullTerminatedString() {
        int nOffset = pRecvBuff.readerIndex();
        int nLen = 0;
        while (DecodeByte() != 0) {
            nLen++;
        }
        pRecvBuff.readerIndex(nOffset);
        return DecodeString(nLen);
    }

    public Point DecodePosition() {
        return new Point(DecodeShort(), DecodeShort());
    }

    public Rectangle DecodeRectanlge() {
        return new Rectangle(DecodeShort(), DecodeShort());
    }

    public InPacket Skip(int nLen) {
        pRecvBuff.readerIndex(pRecvBuff.readerIndex() + nLen);
        return this;
    }

    public int GetRemainder() {
        return pRecvBuff.readableBytes();
    }

    @Override
    public final String toString() {
        if (pRecvBuff.readableBytes() > 0) {
            byte[] aData = new byte[pRecvBuff.readableBytes()];
            int nReaderIndex = pRecvBuff.readerIndex();
            pRecvBuff.getBytes(nReaderIndex, aData);
            return HexUtils.ToHex(aData);
        } else {
            return "";
        }
    }
}
