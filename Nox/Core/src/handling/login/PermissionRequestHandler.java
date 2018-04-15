package handling.login;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;

public final class PermissionRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        // dont print anything here, someone could just prank us and spam this packet
        // use mina sessionCreated if you want to print xxx connected 

        //System.out.println(c.getSessionIPAddress() + " Connected!");
    }

}
