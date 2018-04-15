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
import static crypto.AESCipher.nVersion;
import crypto.CIGCipher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author Kaz Voeten
 */
public class PacketEncoder extends MessageToByteEncoder<OutPacket> {

    private static final int uSeqBase = (short) ((((0xFFFF - nVersion) >> 8) & 0xFF) | (((0xFFFF - nVersion) << 8) & 0xFF00));

    @Override
    protected void encode(ChannelHandlerContext chc, OutPacket oPacket, ByteBuf out) throws Exception {
        Socket pSocket = chc.channel().attr(Socket.SESSION_KEY).get();
        byte[] pBuffer = oPacket.GetData();

        if (pSocket != null) {
            pSocket.Lock();
            try {
                int uSeqSend = pSocket.uSeqSend;
                short uDataLen = (short) (((pBuffer.length << 8) & 0xFF00) | (pBuffer.length >>> 8));
                short uRawSeq = (short) ((((uSeqSend >> 24) & 0xFF) | (((uSeqSend >> 16) << 8) & 0xFF00)) ^ uSeqBase);

                if (pSocket.bEncryptData) {
                    uDataLen ^= uRawSeq;
                    if (pSocket.nCryptoMode == 1) {
                        AESCipher.Crypt(pBuffer, uSeqSend, false);
                    } else if (pSocket.nCryptoMode == 2) {
                        CIGCipher.Crypt(pBuffer, uSeqSend);
                    }
                }

                out.writeShort(uRawSeq);
                out.writeShort(uDataLen);
                out.writeBytes(pBuffer);

                pSocket.uSeqSend = CIGCipher.InnoHash(uSeqSend, 4, 0);
            } finally {
                pSocket.Unlock();
            }
        } else {
            out.writeBytes(pBuffer);
        }
    }
}
