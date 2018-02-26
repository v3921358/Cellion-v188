package handling.game;

import client.MapleClient;
import server.maps.objects.MapleReactor;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class HitReactorHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final int oid = iPacket.DecodeInteger();
        final int charPos = iPacket.DecodeInteger();
        final short stance = iPacket.DecodeShort();
        iPacket.DecodeInteger(); // new int v169. Seems to be always zero?
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        // System.out.println("Hit Reactor:  " + reactor);
        if (reactor == null || !reactor.isAlive()) {
            return;
        }
        reactor.hitReactor(charPos, stance, c);
    }

}
