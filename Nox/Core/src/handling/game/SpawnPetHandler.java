package handling.game;

import client.MapleClient;
import net.InPacket;
import server.maps.FieldLimitType;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SpawnPetHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());

        if (!FieldLimitType.UnableToUsePet.check(c.getPlayer().getMap())) {
            c.getPlayer().spawnPet(iPacket.DecodeByte(), iPacket.DecodeByte() > 0, true);
        }
    }
}
