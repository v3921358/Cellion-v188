package server.messages;

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
        oPacket.Encode(_MessageOpcodesType.ExpiredCashItem.getType());
        oPacket.EncodeInteger(itemId);
    }

}
