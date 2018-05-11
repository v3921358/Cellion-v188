package handling.login;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class ClientLoadingStateHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        // does nothing for now
        int count = iPacket.DecodeInt();
        if (iPacket.GetRemainder() == 4) {
            int code = iPacket.DecodeInt();
        }

    }

}
