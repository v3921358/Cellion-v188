package handling.login;

import client.MapleClient;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class CheckHotFixHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.SendPacket(CLogin.ApplyHotFix());
    }
}
