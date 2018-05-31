package server.messages;

import enums.MessageOpcodesType;
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
        oPacket.EncodeByte(MessageOpcodesType.DropPickup.getType());
        oPacket.EncodeByte(mode);
        switch (mode) {
            case -10:
                oPacket.EncodeInt(item.getItemId());
                break;
            case 0:
                oPacket.EncodeInt(item.getItemId());
                oPacket.EncodeInt(itemQuantity);
                break;
            case 1:
                oPacket.EncodeByte(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeShort(0);
                oPacket.EncodeShort(0);
                break;
            case 2:
                oPacket.EncodeInt(item.getItemId());
                break;
            case 4:
                return;
            case 8:
                oPacket.EncodeInt(0);
                oPacket.EncodeShort(0);
                break;
        }

    }
}
