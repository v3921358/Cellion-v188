package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class IncreaseSpMessage implements MessageInterface {

    private final int jobId;
    private final int amount;

    public IncreaseSpMessage(int jobId, int amount) {
        this.jobId = jobId;
        this.amount = amount;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.IncreaseSP.getType());
        oPacket.EncodeShort(jobId);
        oPacket.EncodeByte(amount);
    }

}
