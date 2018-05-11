package handling.game;

import client.ClientSocket;
import client.MapleDisease;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseItemHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION) || chr.hasBlockedInventory() || chr.inPVP()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
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

        if (!FieldLimitType.UnableToConsumeStatChangeItem.check(chr.getMap())) { //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getSharedMapResources().consumeItemCoolTime > 0) {
                    chr.setNextConsume(time + (chr.getMap().getSharedMapResources().consumeItemCoolTime * 1000));
                }
            }

        } else {
            c.SendPacket(WvsContext.enableActions());
        }
    }
}
