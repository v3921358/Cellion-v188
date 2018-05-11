package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import static handling.game._CommonPlayerOperationHandler.UseTeleRock;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseTeleRockHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232 || c.getPlayer().hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        boolean used = UseTeleRock(iPacket, c, itemId);
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.SendPacket(WvsContext.enableActions());
    }
}
