package server.messages;

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
        oPacket.Encode(_MessageOpcodesType.QuestInfoEx.getType()); //messageType
        oPacket.EncodeInteger(questId);
        oPacket.EncodeString(data);
    }

}
