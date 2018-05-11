package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class ClientLoadingStateHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        // does nothing for now
        int count = iPacket.DecodeInt();
        if (iPacket.GetRemainder() == 4) {
            int code = iPacket.DecodeInt();
        }

    }

}
