package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseResetCoreAuraHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        /*    Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((byte) slot);
        if ((starDust == null) || (c.getPlayer().hasBlockedInventory())) {
            c.write(CWvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
        }*/
    }
}
