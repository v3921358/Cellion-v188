package handling.game;

import client.MapleClient;
import server.quest.MapleQuest;
import net.InPacket;
import netty.ProcessPacket;

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
            c.getPlayer().getQuestRemove(MapleQuest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122901));
        }
    }
}
