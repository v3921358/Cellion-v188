package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

public final class CharSelectSetPICHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        boolean view = false;
        boolean haspic = false;

        PicHandling.CharLogin(iPacket, c, view, haspic);
    }

}
