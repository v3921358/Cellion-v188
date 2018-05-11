package handling.game;

import client.Client;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.MobPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DisplayNodeHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        Mob mob_from = chr.getMap().getMonsterByOid(iPacket.DecodeInt());
        if (mob_from != null) {
            chr.getClient().SendPacket(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
    }

}
