package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class HardcoreExpMessage implements MessageInterface {

    private final int unk;
    private final int unk1;

    public HardcoreExpMessage(int unk, int unk1) {
        this.unk = unk;
        this.unk1 = unk1;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.HardcoreExp.getType());
        oPacket.EncodeInt(unk);
        oPacket.EncodeInt(unk1);
    }

}
