package handling;

import client.ClientSocket;
import net.InPacket;

public interface MaplePacketHandler {

    void handlePacket(InPacket iPacket, ClientSocket c);

    boolean validateState(ClientSocket c);
}
