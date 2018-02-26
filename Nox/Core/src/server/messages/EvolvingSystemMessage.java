package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class EvolvingSystemMessage implements MessageInterface {

    private int unk;
    private int type;

    public EvolvingSystemMessage(int unk, int type) {
        this.unk = unk;
        this.type = type;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.EvolvingSystem.getType());
        oPacket.Encode(unk);
        oPacket.Encode(type);
    }

}
