package handling.game;

import client.MapleClient;
import net.InPacket;
import tools.packet.CSPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseAlienSocketResponseHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.Skip(4); // all 0
        c.SendPacket(CSPacket.useAlienSocket(false));
    }
}
