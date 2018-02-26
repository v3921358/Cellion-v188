package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class CoreInventoryMessage implements MessageInterface {

    private int mode;

    public CoreInventoryMessage(int mode) {
        this.mode = mode;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.CoreInventory.getType());
        oPacket.Encode(mode);
        switch (mode) {
            case 0x16:
                oPacket.EncodeInteger(0); //itemId
                oPacket.EncodeInteger(0);
                break;
            case 0x19:
                oPacket.EncodeInteger(0); //itemId
                oPacket.EncodeInteger(0);
                break;
        }
    }

}
