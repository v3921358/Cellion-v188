package handling.game;

import client.ClientSocket;
import net.InPacket;
import enums.FieldLimitType;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SpawnPetHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());

        if (!FieldLimitType.UnableToUsePet.check(c.getPlayer().getMap())) {
            c.getPlayer().spawnPet(iPacket.DecodeByte(), iPacket.DecodeByte() > 0, true);
        }
    }
}
