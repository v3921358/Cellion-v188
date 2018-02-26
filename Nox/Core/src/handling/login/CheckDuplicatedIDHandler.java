package handling.login;

import client.MapleCharacterCreationUtil;
import client.MapleClient;
import net.InPacket;
import tools.packet.CLogin;
import netty.ProcessPacket;

public final class CheckDuplicatedIDHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        LoginInformationProvider li = LoginInformationProvider.getInstance();
        String name = iPacket.DecodeString();
        boolean nameUsed = true;

        if (MapleCharacterCreationUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.write(CLogin.charNameResponse(name, nameUsed));
    }

}
