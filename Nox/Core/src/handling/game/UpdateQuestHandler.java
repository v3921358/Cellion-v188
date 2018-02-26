package handling.game;

import client.MapleClient;
import server.quest.MapleQuest;
import net.InPacket;
import netty.ProcessPacket;

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
        final MapleQuest quest = MapleQuest.getInstance(iPacket.DecodeShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

}
