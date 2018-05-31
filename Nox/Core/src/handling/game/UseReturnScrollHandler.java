package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import constants.ItemConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import enums.FieldLimitType;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseReturnScrollHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inPVP()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(InventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if ((itemId == ItemConstants.RETURN_SCROLL_NEAREST_TOWN && !FieldLimitType.UnableToUsePortalScroll.check(chr.getMap()))
                || !FieldLimitType.UnableToUseSpecificPortalScroll.check(chr.getMap())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);
            } else {
                c.SendPacket(WvsContext.enableActions());
            }
        } else {
            c.SendPacket(WvsContext.enableActions());
        }
    }

}
