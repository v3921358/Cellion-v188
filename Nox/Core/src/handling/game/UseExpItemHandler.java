package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseExpItemHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (!MapleItemInformationProvider.getInstance().getEquipStats(itemId).containsKey("exp")) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("exp");
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
    }

}
