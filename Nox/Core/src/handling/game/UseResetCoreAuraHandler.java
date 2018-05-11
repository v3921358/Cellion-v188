package handling.game;

import client.Client;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseResetCoreAuraHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        /*    Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((byte) slot);
        if ((starDust == null) || (c.getPlayer().hasBlockedInventory())) {
            c.write(CWvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
        }*/
    }
}
