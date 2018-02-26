package server.messages;

import client.MapleQuestStatus;
import net.OutPacket;
import tools.packet.PacketHelper;

/**
 * @author Steven
 *
 */
public class QuestStatusMessage implements MessageInterface {

    private final MapleQuestStatus quest;

    public QuestStatusMessage(MapleQuestStatus quest) {
        this.quest = quest;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.QuestStatus.getType()); //MessageType
        oPacket.EncodeInteger(quest.getQuest().getId());
        oPacket.Encode(quest.getStatus().getValue());

        switch (quest.getStatus()) {
            case NotStarted:
                oPacket.Encode(0);
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
