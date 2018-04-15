package net;

/**
 * Represents a packet handler for a received MapleStory packet.
 * 
 * @author Novak
 * @param <E> specific type of NettyClient -> more than likely the inheritance
 */
public interface ProcessPacket<E extends Socket> {
    
    public boolean ValidateState(E ClientSocket);
    
    public void Process(E ClientSocket, InPacket iPacket);
}