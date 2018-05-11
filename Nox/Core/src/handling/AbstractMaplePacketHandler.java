package handling;

import client.Client;

public abstract class AbstractMaplePacketHandler implements MaplePacketHandler {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn();
    }
}
