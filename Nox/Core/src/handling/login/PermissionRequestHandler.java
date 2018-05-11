package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

public final class PermissionRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        // dont print anything here, someone could just prank us and spam this packet
        // use mina sessionCreated if you want to print xxx connected 

        //System.out.println(c.getSessionIPAddress() + " Connected!");
    }

}
