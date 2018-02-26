package handling.game;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomCancelSwipeHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.DecodeInteger();
        // does nothing
    }
}
