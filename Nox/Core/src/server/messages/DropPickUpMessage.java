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
    private int nItemID;
    private int itemQuantity;

    public DropPickUpMessage(int mode, int nItemID, int itemQuantity) {
        this.mode = mode;
        this.nItemID = nItemID;
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
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(mode);
        switch (mode) {
            case -10:
                oPacket.EncodeInt(nItemID);
                break;
            case 0:
                oPacket.EncodeInt(nItemID);
                oPacket.EncodeInt(itemQuantity);
                break;
            case 1:
                oPacket.EncodeByte(0); // bFallDeduct
                oPacket.EncodeInt(itemQuantity); // nAmount
                oPacket.EncodeShort(0); // nPremiumBonus
                oPacket.EncodeShort(0);
                break;
            case 2:
                oPacket.EncodeInt(nItemID);
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
