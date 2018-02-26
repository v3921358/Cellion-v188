package server.messages;

import net.OutPacket;

/**
 *
 * @author Lloyd Korn
 */
public class RecoverFatigueMessage implements MessageInterface {

    public RecoverFatigueMessage() {
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.RecoveredFatigue.getType());
    }

}
