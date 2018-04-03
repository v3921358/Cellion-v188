package handling.game;

import client.MapleClient;
import client.MapleDisease;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.objects.User;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class PetAutoPotionHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();

        iPacket.Skip(9);
        chr.updateTick(iPacket.DecodeInteger());
        final short slot = iPacket.DecodeShort();
        if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleDisease.POTION)) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != iPacket.DecodeInteger()) {
            c.write(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.write(CWvsContext.enableActions());
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
            c.write(CWvsContext.enableActions());
        }
    }
}
