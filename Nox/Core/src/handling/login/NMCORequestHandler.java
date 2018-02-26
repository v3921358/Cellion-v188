package handling.login;

import client.MapleClient;
import net.InPacket;
import tools.packet.CLogin;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class NMCORequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.write(CLogin.NCMOResult(true));
    }
}
