package handling.game;

import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import static handling.game._CommonPlayerOperationHandler.UseTeleRock;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseTeleRockHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232 || c.getPlayer().hasBlockedInventory()) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        boolean used = UseTeleRock(iPacket, c, itemId);
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.SendPacket(CWvsContext.enableActions());
    }
}
