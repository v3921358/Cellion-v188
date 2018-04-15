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
        oPacket.EncodeByte(_MessageOpcodesType.CoreInventory.getType());
        oPacket.EncodeByte(mode);
        switch (mode) {
            case 0x16:
                oPacket.EncodeInt(0); //itemId
                oPacket.EncodeInt(0);
                break;
            case 0x19:
                oPacket.EncodeInt(0); //itemId
                oPacket.EncodeInt(0);
                break;
        }
    }

}
