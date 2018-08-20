package handling.game;

import client.ClientSocket;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UpdateQuestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final Quest quest = Quest.getInstance(iPacket.DecodeShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

}
