package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomCancelSwipeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.DecodeInt();
        // does nothing
    }
}
