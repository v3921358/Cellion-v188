package handling.login;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

public final class RegisterPicOnViewHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        boolean view = true;
        boolean haspic = true;
        PicHandling.CharLogin(iPacket, c, view, haspic);
    }

}
