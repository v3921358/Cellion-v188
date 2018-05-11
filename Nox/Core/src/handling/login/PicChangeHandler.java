package handling.login;

import client.ClientSocket;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class PicChangeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getSecondPassword().length() > 0 || c.getSecondPassword() != null) {
            iPacket.DecodeString(); //old pic
            String newPic = iPacket.DecodeString();
            if (c.checkSecondPassword(newPic, false)) {
                c.SendPacket(CLogin.changePic((byte) 0x14));
            } else {
                c.setSecondPassword(newPic);
                c.updateSecondPassword();
                c.SendPacket(CLogin.changePic((byte) 0));
            }
        }
    }

}
