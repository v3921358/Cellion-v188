package handling.login;

import client.MapleCharacterCreationUtil;
import client.Client;
import net.InPacket;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class CheckDuplicatedIDHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        LoginInformationProvider li = LoginInformationProvider.getInstance();
        String name = iPacket.DecodeString();
        boolean nameUsed = true;

        if (MapleCharacterCreationUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.SendPacket(CLogin.charNameResponse(name, nameUsed));
    }

}
