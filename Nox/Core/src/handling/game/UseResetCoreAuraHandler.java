package handling.game;

import client.MapleClient;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseResetCoreAuraHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        /*    Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((byte) slot);
        if ((starDust == null) || (c.getPlayer().hasBlockedInventory())) {
            c.write(CWvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
        }*/
    }
}
