package server.messages;

import enums.MessageOpcodesType;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class UpdateQuestMessage implements MessageInterface {

    private final int questId;
    private final String data;

    public UpdateQuestMessage(int questId, String data) {
        this.questId = questId;
        this.data = data;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.QuestInfoEx.getType()); //messageType
        oPacket.EncodeInt(questId);
        oPacket.EncodeString(data);
    }

}
