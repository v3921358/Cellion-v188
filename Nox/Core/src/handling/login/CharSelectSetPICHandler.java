package handling.login;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;

public final class CharSelectSetPICHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        boolean view = false;
        boolean haspic = false;

        PicHandling.CharLogin(iPacket, c, view, haspic);
    }

}
