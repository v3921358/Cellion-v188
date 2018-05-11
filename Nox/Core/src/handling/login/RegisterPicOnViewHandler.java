package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

public final class RegisterPicOnViewHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        boolean view = true;
        boolean haspic = true;
        PicHandling.CharLogin(iPacket, c, view, haspic);
    }

}
