package server.messages;

import client.inventory.Item;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class DropPickUpMessage implements MessageInterface {

    private int mode = 4;
    private Item item;
    private int itemQuantity;

    public DropPickUpMessage(int mode, Item item, int itemQuantity) {
        this.mode = mode;
        this.item = item;
        this.itemQuantity = itemQuantity;
    }

    /**
     * (non-Javadoc)
     *
     * @see server.messages.MessageHandler#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.DropPickup.getType());
        oPacket.Encode(mode);
        switch (mode) {
            case -10:
                oPacket.EncodeInteger(item.getItemId());
                break;
            case 0:
                oPacket.EncodeInteger(item.getItemId());
                oPacket.EncodeInteger(itemQuantity);
                break;
            case 1:
                oPacket.Encode(0);
                oPacket.EncodeInteger(0);
                oPacket.EncodeShort(0);
                oPacket.EncodeShort(0);
                break;
            case 2:
                oPacket.EncodeInteger(item.getItemId());
                break;
            case 4:
                return;
            case 8:
                oPacket.EncodeInteger(0);
                oPacket.EncodeShort(0);
                break;
        }

    }
}
