package server.messages;

import enums.MessageOpcodesType;
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
        oPacket.EncodeByte(MessageOpcodesType.IncreaseWP.getType());
        oPacket.EncodeInt(0);
    }

}
