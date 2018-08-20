package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

public final class CharSelectHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int charId = iPacket.DecodeInt();
        String name = iPacket.DecodeString();
        // MapleCharacter character = c.loadCharacterById(charId);

        //if (character != null) {
        //   if (character.getName().contains(name)) {
        //Idk if we have toi do something in here but I will leave it like this for now.
        //  }
        //  }
    }

}
