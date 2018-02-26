package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class FameMessage implements MessageInterface {

    private int fame;

    public FameMessage(int fame) {
        this.fame = fame;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.Fame.getType());
        oPacket.EncodeInteger(fame);
    }

}
