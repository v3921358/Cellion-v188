package handling.game;

import client.MapleClient;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UpdateQuestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final Quest quest = Quest.getInstance(iPacket.DecodeShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

}
