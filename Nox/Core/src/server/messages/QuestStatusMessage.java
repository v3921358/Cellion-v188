package server.messages;

import client.QuestStatus;
import net.OutPacket;
import tools.packet.PacketHelper;

/**
 * @author Steven
 *
 */
public class QuestStatusMessage implements MessageInterface {

    private final QuestStatus quest;

    public QuestStatusMessage(QuestStatus quest) {
        this.quest = quest;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.QuestStatus.getType()); //MessageType
        oPacket.EncodeInt(quest.getQuest().getId());
        oPacket.EncodeByte(quest.getStatus().getValue());

        switch (quest.getStatus()) {
            case NotStarted:
                oPacket.EncodeByte(0);
                break;
            case Started:
                oPacket.EncodeString(quest.getCustomData() != null ? quest.getCustomData() : "");
                break;
            case Completed:
                oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
                break;
        }
    }

}
