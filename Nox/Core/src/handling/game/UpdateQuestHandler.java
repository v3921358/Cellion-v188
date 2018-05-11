package handling.game;

import client.Client;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UpdateQuestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final Quest quest = Quest.getInstance(iPacket.DecodeShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

}
