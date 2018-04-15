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
import crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteOrder;
import java.util.List;

/**
 *
 * @author Kaz Voeten
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) throws Exception {
        Socket pSocket = chc.channel().attr(Socket.SESSION_KEY).get();
        if (pSocket == null) {
            return;
        }

        if (pSocket.nLastState == 0 && in.readableBytes() >= 4) {
            ByteBuf pBuff = in.readBytes(4).order(ByteOrder.LITTLE_ENDIAN);
            InPacket iPacket = new InPacket();
            try {
                int nState = iPacket.AppendBuffer(pBuff, pSocket.bEncryptData);
                if (nState > 0) {
                    if (iPacket.uDataLen > 0x50000) {
                        System.out.println("Recv packet length overflow.");
                        return;
                    }
                    pSocket.nLastState = iPacket.uDataLen;
                    if (pSocket.bEncryptData && iPacket.DecodeSeqBase(pSocket.uSeqRcv) != AESCipher.nVersion) {
                        System.out.println("Recv packet sequence mismatch.");
                    }
                }
            } finally {
                pBuff.release();
            }
        }

        if (pSocket.nLastState > 1) {
            if (in.readableBytes() >= pSocket.nLastState) {
                InPacket iPacket = new InPacket(pSocket.nLastState);
                ByteBuf pBuff = in.readBytes(iPacket.uDataLen).order(ByteOrder.LITTLE_ENDIAN);
                try {
                    int nState = iPacket.AppendBuffer(pBuff, pSocket.bEncryptData);
                    if (nState == 2) {
                        if (pSocket.bEncryptData && !iPacket.DecryptData(pSocket.uSeqRcv)) {
                            System.out.println("Unable to decrypt data.");
                            pSocket.uSeqRcv = CIGCipher.InnoHash(pSocket.uSeqRcv, 4, 0);
                            return;
                        }
                        pSocket.uSeqRcv = CIGCipher.InnoHash(pSocket.uSeqRcv, 4, 0);
                        pSocket.nLastState = 0;
                        out.add(iPacket);
                    }
                } finally {
                    pBuff.release();
                }
            }
        }
    }
}
