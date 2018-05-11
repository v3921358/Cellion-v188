package handling.game;

import client.ClientSocket;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ItemMoveHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(iPacket.DecodeInt());
        MapleInventoryType type = MapleInventoryType.getByType(iPacket.DecodeByte());
        final short src = iPacket.DecodeShort();
        final short dst = iPacket.DecodeShort();
        final short quantity = iPacket.DecodeShort();
        if (src < 0 && dst < 0) {
            type = MapleInventoryType.getByType((byte) -1);
        }
        if (src < 0 && dst > 0) {
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0 && src > 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (dst == 0) {
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }

        c.getPlayer().saveItemData(); // Duplication and rollback prevention.
    }
}
