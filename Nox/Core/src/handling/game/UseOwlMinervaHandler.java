package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import java.util.List;
import server.MapleInventoryManipulator;
import server.stores.HiredMerchant;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseOwlMinervaHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemid = iPacket.DecodeInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000 && !c.getPlayer().hasBlockedInventory()) {
            final int itemSearch = iPacket.DecodeInt();
            final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
            if (hms.size() > 0) {
                c.SendPacket(WvsContext.getOwlSearched(itemSearch, hms));
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            } else {
                c.getPlayer().dropMessage(1, "Unable to find the item.");
            }
        }
        c.SendPacket(WvsContext.enableActions());
    }

}
