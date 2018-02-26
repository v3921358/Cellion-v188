package server.messages;

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
        oPacket.Encode(_MessageOpcodesType.Commitment.getType());
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
    }

}
