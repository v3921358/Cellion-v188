package handling.game;

import client.Client;
import server.maps.objects.MapleReactor;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class HitReactorHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final int oid = iPacket.DecodeInt();
        final int charPos = iPacket.DecodeInt();
        final short stance = iPacket.DecodeShort();
        iPacket.DecodeInt(); // new int v169. Seems to be always zero?
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        // System.out.println("Hit Reactor:  " + reactor);
        if (reactor == null || !reactor.isAlive()) {
            return;
        }
        reactor.hitReactor(charPos, stance, c);
    }

}
