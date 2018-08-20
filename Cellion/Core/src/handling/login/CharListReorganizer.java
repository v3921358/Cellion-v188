package handling.login;

import client.ClientSocket;
import net.InPacket;
import server.maps.objects.User;
import net.ProcessPacket;

public final class CharListReorganizer implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
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
