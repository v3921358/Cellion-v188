package handling.game;

import client.ClientSocket;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ItemSortHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(iPacket.DecodeByte());

        if (pInvType == MapleInventoryType.UNDEFINED || c.getPlayer().hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final MapleInventory pInv = c.getPlayer().getInventory(pInvType); //Mode should correspond with MapleInventoryType
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.SendPacket(WvsContext.finishedSort(pInvType.getType()));
        c.SendPacket(WvsContext.enableActions());
    }
}
