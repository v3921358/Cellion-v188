package handling;

import client.ClientSocket;

public abstract class AbstractMaplePacketHandler implements MaplePacketHandler {

    @Override
    public boolean validateState(ClientSocket c) {
        return c.isLoggedIn();
    }
}
