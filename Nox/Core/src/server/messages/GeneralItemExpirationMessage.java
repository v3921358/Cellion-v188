package server.messages;

import enums.MessageOpcodesType;
import java.util.List;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class GeneralItemExpirationMessage implements MessageInterface {

    private final List<Integer> items;

    public GeneralItemExpirationMessage(List<Integer> item) {
        this.items = item;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.GeneralItemExpiration.getType());
        oPacket.EncodeByte(items.size());
        for (Integer item : items) {
            oPacket.EncodeInt(item);
        }
    }

}
