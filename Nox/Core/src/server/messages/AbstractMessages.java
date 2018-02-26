package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class AbstractMessages implements MessageInterface {

    private int mode;

    public AbstractMessages(int mode) {
        this.mode = mode;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(mode);
    }

}
