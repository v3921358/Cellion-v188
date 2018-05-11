package handling.login;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

public final class CharSelectHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
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
