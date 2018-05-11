package handling.game;

import client.Client;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DenyGuildRequestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        iPacket.Skip(1);
        String from = iPacket.DecodeString();

        final User cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null
                && GuildOperationHandler.getGuildInvitationList().remove(c.getPlayer().getName().toLowerCase()) != null) {
            cfrom.getClient().SendPacket(CWvsContext.GuildPacket.rejectInvite(c.getPlayer().getName()));
        }
    }
}
