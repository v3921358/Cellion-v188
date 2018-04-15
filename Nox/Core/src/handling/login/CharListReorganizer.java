package handling.login;

import client.MapleClient;
import net.InPacket;
import server.maps.objects.User;
import net.ProcessPacket;

public final class CharListReorganizer implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int accountId = iPacket.DecodeInt();
        if (c == null || accountId != c.getAccID()) {
            return;
        }
        iPacket.DecodeByte(); // 01
        int size = iPacket.DecodeInt();
        for (int i = 0; i < size; i++) {
            User character = c.loadCharacterById(iPacket.DecodeInt());
            character.setCharListPosition(i);
            character.updateCharlistPosition(i);
        }
    }

}
