package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class BlockedBehaviorMessage implements MessageInterface {

    private final int type;

    public BlockedBehaviorMessage(int type) {
        this.type = type;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.BlockedBehavior.getType());

        // 0 = "Zero cannot get mesos in Maple World until Chapter 1 of the Main Quest is complete."
        // 1 = "Zero cannot get items in Maple World until Chapter 1 of the Main Quest is complete."
        oPacket.EncodeInteger(type);
    }

}
