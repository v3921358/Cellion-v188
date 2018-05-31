package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class ExpiredCashItemMessage implements MessageInterface {

    private int itemId;

    public ExpiredCashItemMessage(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.ExpiredCashItem.getType());
        oPacket.EncodeInt(itemId);
    }

}
