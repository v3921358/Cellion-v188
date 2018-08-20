package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DenyGuildRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.Skip(1);
        String from = iPacket.DecodeString();

        final User cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null
                && GuildOperationHandler.getGuildInvitationList().remove(c.getPlayer().getName().toLowerCase()) != null) {
            cfrom.getClient().SendPacket(WvsContext.GuildPacket.rejectInvite(c.getPlayer().getName()));
        }
    }
}
