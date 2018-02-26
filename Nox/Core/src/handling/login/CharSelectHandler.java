package handling.login;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;

public final class CharSelectHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int charId = iPacket.DecodeInteger();
        String name = iPacket.DecodeString();
        // MapleCharacter character = c.loadCharacterById(charId);

        //if (character != null) {
        //   if (character.getName().contains(name)) {
        //Idk if we have toi do something in here but I will leave it like this for now.
        //  }
        //  }
    }

}
