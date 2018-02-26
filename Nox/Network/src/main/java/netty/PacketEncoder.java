package netty;

import crypto.CAESCipher;
import crypto.CIGCipher;
import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Implementation of a Netty encoder pattern so that encryption of MapleStory packets is possible. Follows steps using the special MapleAES
 * as well as ShandaCrypto (which became non-used after v149.2) and CIGCipher's special packet encryption as implemented post v185.0.
 *
 * @author Novak
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    public PacketEncoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void encode(ChannelHandlerContext chc, Packet iPacket, ByteBuf oBuffer) throws Exception {
        byte[] aSendBuff = iPacket.GetData();
        CClientSocket Socket = chc.channel().attr(CClientSocket.CLIENT_KEY).get();
        CAESCipher Cipher = chc.channel().attr(CClientSocket.CRYPTO_KEY).get();

        if (Socket != null) {
            int dwKey = Socket.GetSeqSend();
            byte[] aHeader = CAESCipher.GetHeader(aSendBuff.length, dwKey);

            Socket.Lock();
            try {
                if (Socket.GetCryptoMode() == 1) {
                    Cipher.Crypt(aSendBuff, dwKey);
                } else if (Socket.GetCryptoMode() == 2) {
                    CIGCipher.Encrypt(aSendBuff, dwKey);
                }
                Socket.SetSeqSend(CIGCipher.InnoHash(dwKey, 4, 0));
            } finally {
                Socket.Unlock();
            }

            oBuffer.writeBytes(aHeader);
            oBuffer.writeBytes(aSendBuff);

        } else {
            oBuffer.writeBytes(aSendBuff);
        }
    }
}
