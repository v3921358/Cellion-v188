package handling.game;

import client.MapleClient;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.MobPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class DisplayNodeHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        Mob mob_from = chr.getMap().getMonsterByOid(iPacket.DecodeInteger());
        if (mob_from != null) {
            chr.getClient().write(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
    }

}
