package handling.game;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class UseExpItemHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.write(CWvsContext.enableActions());
            return;
        }

        c.getPlayer().updateTick(iPacket.DecodeInteger());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInteger();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.write(CWvsContext.enableActions());
            return;
        }
        if (!MapleItemInformationProvider.getInstance().getEquipStats(itemId).containsKey("exp")) {
            c.write(CWvsContext.enableActions());
            return;
        }
        MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("exp");
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
    }

}
