package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class PersonalEvolvingSystemMessage implements MessageInterface {

    private final int unk;
    private final int type;
    private final String name;

    public PersonalEvolvingSystemMessage(int unk, int type, String name) {
        this.unk = unk;
        this.type = type;
        this.name = name;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.PersonalEvolvingSystemMessage.getType());
        oPacket.EncodeByte(unk);
        oPacket.EncodeByte(type);
        oPacket.EncodeString(name);
    }

}
