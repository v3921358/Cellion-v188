package server.messages;

import java.util.List;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class ReplacedExpiredItemMessage implements MessageInterface {

    private final List<String> messages;

    public ReplacedExpiredItemMessage(List<String> messages) {
        this.messages = messages;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.ReplaceExpiredItem.getType());
        oPacket.Encode(messages.size());
        for (String message : messages) {
            oPacket.EncodeString(message);
        }
    }

}
