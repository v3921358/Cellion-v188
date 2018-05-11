package handling.login;

import client.Client;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class CheckHotFixHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        c.SendPacket(CLogin.ApplyHotFix());
    }
}
