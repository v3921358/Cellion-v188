package handling.game;

import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class RecipeHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 251) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
    }

}
