package handling;

import client.MapleClient;
import net.InPacket;

public interface MaplePacketHandler {

    void handlePacket(InPacket iPacket, MapleClient c);

    boolean validateState(MapleClient c);
}
