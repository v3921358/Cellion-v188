package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DenyAllianceRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        AllianceOperationHandler.handleAllianceRequest(iPacket, c, true);
    }

}
