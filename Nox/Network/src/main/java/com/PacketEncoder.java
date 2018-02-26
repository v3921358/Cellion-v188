package com;

import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * @author Novak
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    public PacketEncoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void encode(ChannelHandlerContext chc, Packet iPacket, ByteBuf buffer) throws Exception {
        byte[] data = iPacket.GetData();
        
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }
}
