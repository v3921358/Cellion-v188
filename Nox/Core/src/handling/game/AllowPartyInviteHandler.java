package handling.game;

import client.MapleClient;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class AllowPartyInviteHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (iPacket.DecodeByte() > 0) {
            c.getPlayer().getQuestRemove(Quest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(Quest.getInstance(122901));
        }
    }
}
