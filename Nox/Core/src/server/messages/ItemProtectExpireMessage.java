package server.messages;

import java.util.List;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class ItemProtectExpireMessage implements MessageInterface {

    private final List<Integer> items;

    public ItemProtectExpireMessage(List<Integer> item) {
        this.items = item;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.ItemProtectExpire.getType());
        oPacket.Encode(items.size());
        for (Integer item : items) {
            oPacket.EncodeInteger(item);
        }
    }

}
