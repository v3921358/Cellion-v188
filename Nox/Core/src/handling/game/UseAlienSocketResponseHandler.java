package handling.game;

import client.Client;
import net.InPacket;
import tools.packet.CSPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseAlienSocketResponseHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        iPacket.Skip(4); // all 0
        c.SendPacket(CSPacket.useAlienSocket(false));
    }
}
