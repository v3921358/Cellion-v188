package server.messages;

import net.OutPacket;

/**
 *
 * @author Lloyd Korn
 */
public class AndroidNotPoweredMessage implements MessageInterface {

    public AndroidNotPoweredMessage() {
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.AndroidNotPowered.getType());
    }

}
