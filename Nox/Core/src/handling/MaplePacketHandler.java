package handling;

import client.Client;
import net.InPacket;

public interface MaplePacketHandler {

    void handlePacket(InPacket iPacket, Client c);

    boolean validateState(Client c);
}
