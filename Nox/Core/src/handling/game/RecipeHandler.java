package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class RecipeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(InventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 251) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
        }
    }

}
