package handling.game;

import client.ClientSocket;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.MobPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DisplayNodeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        Mob mob_from = chr.getMap().getMonsterByOid(iPacket.DecodeInt());
        if (mob_from != null) {
            chr.getClient().SendPacket(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
    }

}
