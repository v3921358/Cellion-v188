package handling.login;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

public final class PermissionRequestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        // dont print anything here, someone could just prank us and spam this packet
        // use mina sessionCreated if you want to print xxx connected 

        //System.out.println(c.getSessionIPAddress() + " Connected!");
    }

}
