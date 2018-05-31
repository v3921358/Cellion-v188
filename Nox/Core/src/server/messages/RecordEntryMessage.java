package server.messages;

import enums.MessageOpcodesType;
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
        oPacket.EncodeByte(MessageOpcodesType.RecordEntryMessage.getType());
        oPacket.EncodeByte(mode);
        switch (mode) {
            case 0:
                oPacket.EncodeShort(0); //nCategory
                oPacket.EncodeInt(0); //ItemId
                oPacket.EncodeInt(0); //nCount
                break;
            case 1:
                oPacket.EncodeInt(0);
                break;
            case 6:
                oPacket.EncodeInt(0); //itemId
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                break;
        }
    }

}
