package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class CommitmentMessage implements MessageInterface {

    public CommitmentMessage() {
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.Commitment.getType());
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
    }

}
