package server.messages;

import net.OutPacket;

;

/**
 * @author Steven
 *
 */
public class RecordEntryMessage implements MessageInterface {

    private final int mode;

    public RecordEntryMessage(int mode) {
        this.mode = mode;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.RecordEntryMessage.getType());
        oPacket.Encode(mode);
        switch (mode) {
            case 0:
                oPacket.EncodeShort(0); //nCategory
                oPacket.EncodeInteger(0); //ItemId
                oPacket.EncodeInteger(0); //nCount
                break;
            case 1:
                oPacket.EncodeInteger(0);
                break;
            case 6:
                oPacket.EncodeInteger(0); //itemId
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0);
                break;
        }
    }

}
