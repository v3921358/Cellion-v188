package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class IncreaseWPMessage implements MessageInterface {

    public IncreaseWPMessage() {
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.IncreaseWP.getType());
        oPacket.EncodeInteger(0);
    }

}
