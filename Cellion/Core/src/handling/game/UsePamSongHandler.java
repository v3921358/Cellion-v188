package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.Item;
import enums.InventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UsePamSongHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final Item pam = c.getPlayer().getInventory(InventoryType.CASH).findById(5640000);
        if (iPacket.DecodeByte() > 0 && c.getPlayer().getScrolledPosition() != 0 && pam != null && pam.getQuantity() > 0) {
            final InventoryType inv = c.getPlayer().getScrolledPosition() < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP;
            final Item item = c.getPlayer().getInventory(inv).getItem(c.getPlayer().getScrolledPosition());
            c.getPlayer().setScrolledPosition((short) 0);
            if (item != null) {
                final Equip eq = (Equip) item;
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItemFlag(eq, inv);
                MapleInventoryManipulator.removeFromSlot(c, InventoryType.CASH, pam.getPosition(), (short) 1, true, false);
                c.getPlayer().getMap().broadcastPacket(CField.pamsSongEffect(c.getPlayer().getId()));
            }
        } else {
            c.getPlayer().setScrolledPosition((short) 0);
        }
    }
}
