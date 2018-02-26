package com;

import netty.*;
import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 *
 * @author Novak
 */
public class PacketDecoder extends ByteToMessageDecoder {

    public PacketDecoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf buffer, List<Object> oPacket) throws Exception {
        CClientSocket socket = chc.channel().attr(CClientSocket.CLIENT_KEY).get();

        if (socket != null) {
            if (socket.GetStoredLength() == -1) {
                if (buffer.readableBytes() >= 4) {
                    socket.SetStoredLength(buffer.readInt());
                }
            }

            if (socket.GetStoredLength() != -1) {
                if (buffer.readableBytes() >= socket.GetStoredLength()) {
                    byte[] data = new byte[socket.GetStoredLength()];
                    buffer.readBytes(data);
                    socket.SetStoredLength(-1);
                    oPacket.add(new Packet(data));
                }
            }
        }
    }
}
