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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import util.HexUtils;

/**
 *
 * @author Kaz Voeten
 */
public class OutPacket {

    public int nPacketID;
    private final ByteBuf pSendBuff;
    private static final Charset ASCII = Charset.forName("US-ASCII");

    public OutPacket(short nPacketID) {
        this.pSendBuff = Unpooled.buffer().order(ByteOrder.LITTLE_ENDIAN);
        this.nPacketID = nPacketID;
        pSendBuff.writeShort(nPacketID);
    }
    
    public final OutPacket EncodeByte(int nValue) {
        pSendBuff.writeByte(nValue);
        return this;
    }

    public final OutPacket EncodeByte(long nValue) {
        return EncodeByte((int) nValue);
    }

    public final OutPacket Encode(byte[] aData) {
        return Encode(aData, 0, aData.length);
    }

    public final OutPacket EncodeBool(boolean bData) {
        return EncodeByte(bData ? 1 : 0);
    }

    public final OutPacket Encode(byte[] aData, int nOffset, int nLength) {
        for (int i = nOffset; i < nLength; i++) {
            EncodeByte(aData[i]);
        }
        return this;
    }

    public final OutPacket EncodeShort(int nValue) {
        pSendBuff.writeShort(nValue);
        return this;
    }

    public final OutPacket EncodeShort(short nValue) {
        pSendBuff.writeShort(nValue);
        return this;
    }

    public final OutPacket EncodeChar(char cValue) {
        pSendBuff.writeChar(cValue);
        return this;
    }

    public final OutPacket EncodeInt(int nValue) {
        pSendBuff.writeInt(nValue);
        return this;
    }

    public final OutPacket EncodeFloat(float nValue) {
        pSendBuff.writeFloat(nValue);
        return this;
    }

    public final OutPacket EncodeLong(long nValue) {
        pSendBuff.writeLong(nValue);
        return this;
    }

    public final OutPacket EncodeDouble(double nValue) {
        pSendBuff.writeDouble(nValue);
        return this;
    }

    public final OutPacket EncodeString(String sData, int nLen) {
        byte[] string = sData.getBytes(ASCII);
        byte[] fill = new byte[nLen - string.length];
        return Encode(string).Encode(fill);
    }

    public final OutPacket EncodeString(String sData) {
        return EncodeShort(sData.length()).Encode(sData.getBytes(ASCII));
    }

    public final OutPacket EncodeBuffer(String sData) {
        Encode(sData.getBytes(ASCII));
        return this;
    }

    public final OutPacket EncodeHex(String sData) {
        return Encode(HexUtils.ToBytes(sData));
    }

    public final OutPacket Fill(int nValue, int nLenth) {
        for (int i = 0; i < nLenth; i++) {
            EncodeByte(nValue);
        }
        return this;
    }

    public final OutPacket EncodePosition(Point pData) {
        return EncodeShort(pData.x).EncodeShort(pData.y);
    }

    public final OutPacket EncodeRectangle(Rectangle rData) {
        return EncodeInt(rData.x).EncodeInt(rData.y)
                .EncodeInt(rData.x + rData.width).EncodeInt(rData.y + rData.height);
    }
    
    public int GetLength() {
        return pSendBuff.readableBytes();
    }

    @Override
    public final String toString() {
        byte[] aData = new byte[pSendBuff.readableBytes()];
        int nReaderIndex = pSendBuff.readerIndex();
        pSendBuff.getBytes(nReaderIndex, aData);
        pSendBuff.readerIndex(nReaderIndex);
        return HexUtils.ToHex(aData);
    }

    public byte[] GetData() {
        byte[] aData = new byte[pSendBuff.readableBytes()];
        int nReaderIndex = pSendBuff.readerIndex();
        pSendBuff.getBytes(nReaderIndex, aData);
        return aData;
    }
    
    //Might be broken~!
    public OutPacket Clone() {
        return (new OutPacket((short) nPacketID)).Encode(this.GetData());
    }
}
