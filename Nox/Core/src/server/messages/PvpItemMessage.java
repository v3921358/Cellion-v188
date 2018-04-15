package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class PvpItemMessage implements MessageInterface {

    private final String info, data;

    public PvpItemMessage(String info, String data) {
        this.info = info;
        this.data = data;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.PvpItem.getType());
        oPacket.EncodeString(info);
        oPacket.EncodeString(data);
    }

}
