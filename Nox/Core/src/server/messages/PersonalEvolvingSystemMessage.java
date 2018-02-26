package server.messages;

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
        oPacket.Encode(_MessageOpcodesType.PersonalEvolvingSystemMessage.getType());
        oPacket.Encode(unk);
        oPacket.Encode(type);
        oPacket.EncodeString(name);
    }

}
