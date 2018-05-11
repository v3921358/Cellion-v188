package handling.game;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomCancelSwipeHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        iPacket.DecodeInt();
        // does nothing
    }
}
