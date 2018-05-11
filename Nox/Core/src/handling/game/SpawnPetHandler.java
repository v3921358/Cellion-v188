package handling.game;

import client.Client;
import net.InPacket;
import server.maps.FieldLimitType;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SpawnPetHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());

        if (!FieldLimitType.UnableToUsePet.check(c.getPlayer().getMap())) {
            c.getPlayer().spawnPet(iPacket.DecodeByte(), iPacket.DecodeByte() > 0, true);
        }
    }
}
