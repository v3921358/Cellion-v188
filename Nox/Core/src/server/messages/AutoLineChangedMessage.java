package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class AutoLineChangedMessage implements MessageInterface {

    private final String message;

    public AutoLineChangedMessage(String message) {
        this.message = message;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.AutoLineChanged.getType());
        oPacket.EncodeString(message);
    }

}
