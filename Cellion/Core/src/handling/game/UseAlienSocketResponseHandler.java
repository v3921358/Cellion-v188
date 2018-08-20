package handling.game;

import client.ClientSocket;
import net.InPacket;
import tools.packet.CSPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseAlienSocketResponseHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.Skip(4); // all 0
        c.SendPacket(CSPacket.useAlienSocket(false));
    }
}
