package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class GiveBuffMessage implements MessageInterface {

    private int itemId;

    public GiveBuffMessage(int itemId) {
        this.itemId = itemId;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.GiveBuff.getType());
        oPacket.EncodeInteger(itemId);
    }

}
