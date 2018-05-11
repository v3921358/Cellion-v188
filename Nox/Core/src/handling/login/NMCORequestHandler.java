package handling.login;

import client.ClientSocket;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class NMCORequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.SendPacket(CLogin.NCMOResult(true));
    }
}
