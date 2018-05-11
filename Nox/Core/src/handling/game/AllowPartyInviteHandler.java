package handling.game;

import client.Client;
import server.quest.Quest;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class AllowPartyInviteHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        if (iPacket.DecodeByte() > 0) {
            c.getPlayer().getQuestRemove(Quest.getInstance(122901));
        } else {
            c.getPlayer().getQuestNAdd(Quest.getInstance(122901));
        }
    }
}
