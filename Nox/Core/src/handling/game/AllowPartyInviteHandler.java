package handling.game;

import client.ClientSocket;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class AllowPartyInviteHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (iPacket.DecodeByte() > 0) {
            c.getPlayer().getQuestRemove(Quest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(Quest.getInstance(122901));
        }
    }
}
