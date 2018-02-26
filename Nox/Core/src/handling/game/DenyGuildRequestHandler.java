package handling.game;

import client.MapleClient;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class DenyGuildRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.Skip(1);
        String from = iPacket.DecodeString();

        final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null
                && GuildOperationHandler.getGuildInvitationList().remove(c.getPlayer().getName().toLowerCase()) != null) {
            cfrom.getClient().write(CWvsContext.GuildPacket.rejectInvite(c.getPlayer().getName()));
        }
    }
}
