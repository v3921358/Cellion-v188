package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class SystemMessage implements MessageInterface {

    private final String systemMessage;

    public SystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.System.getType());
        oPacket.EncodeString(systemMessage);
    }

}
