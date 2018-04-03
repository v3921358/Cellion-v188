package handling.game;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class UseMountFoodHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInteger());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemid = iPacket.DecodeInteger(); //2260000 usually
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();

        if (itemid / 10000 == 226 && toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null && !c.getPlayer().hasBlockedInventory()) {
            final int fatigue = mount.getFatigue();

            boolean levelup = false;
            mount.setFatigue((byte) -30);

            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (level < 30 && mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1)) {
                    mount.setLevel((byte) (level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(CWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.write(CWvsContext.enableActions());
    }
}
