package netty;

import crypto.CIGCipher;
import crypto.CAESCipher;
import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * Implementation of a Netty decoder pattern so that decryption of MapleStory
 * packets is possible. Follows steps using the special MapleAES as well as
 * ShandaCrypto (which became non-used after v149.2 in GMS) .
 *
 * @author Novak
 */
public class PacketDecoder extends ByteToMessageDecoder {

    public PacketDecoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf oBuffer, List<Object> oPacket) throws Exception {
        CClientSocket Socket = chc.channel().attr(CClientSocket.CLIENT_KEY).get();
        CAESCipher Cipher = chc.channel().attr(CClientSocket.CRYPTO_KEY).get();

        if (Socket != null) {
            int dwKey = Socket.GetSeqRcv();
            if (Socket.GetStoredLength() == -1) {
                if (oBuffer.readableBytes() >= 4) {
                    int nHeader = oBuffer.readInt();
                    if (!CAESCipher.ValidateHeader(nHeader, dwKey)) {
                        Socket.close();
                        return;
                    }
                    Socket.SetStoredLength(CAESCipher.GetLength(nHeader));
                } else {
                    return;
                }
            }
            if (oBuffer.readableBytes() >= Socket.GetStoredLength()) {
                byte[] aData = new byte[Socket.GetStoredLength()];
                oBuffer.readBytes(aData);
                Socket.SetStoredLength(-1);
                
                aData = Cipher.Crypt(aData, dwKey);
                if (Socket.GetCryptoMode() == 2) {
                    //Decode opcode here
                }
                Socket.SetSeqRcv(CIGCipher.InnoHash(dwKey, 4, 0));
                
                oPacket.add(new Packet(aData));
            }
        }
    }
}
