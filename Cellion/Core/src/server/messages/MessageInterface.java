package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 * This class will handle all the different messages the player will recieve when performing certain actions
 */
public interface MessageInterface {

    public void messagePacket(OutPacket oPacket);
}
