package handling.game;

import client.Client;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ItemSortHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(iPacket.DecodeByte());

        if (pInvType == MapleInventoryType.UNDEFINED || c.getPlayer().hasBlockedInventory()) {
            c.SendPacket(CWvsContext.enableActions());
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
        c.SendPacket(CWvsContext.finishedSort(pInvType.getType()));
        c.SendPacket(CWvsContext.enableActions());
    }
}
