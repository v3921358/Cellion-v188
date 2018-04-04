package handling.game;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import java.util.Map;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class UseSummonBagHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();

        if (!chr.isAlive() || chr.hasBlockedInventory() || chr.inPVP()) {
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInteger();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && (c.getPlayer().getMapId() < 910000000 || c.getPlayer().getMapId() > 910000022)) {
            final Map<String, Integer> toSpawn = MapleItemInformationProvider.getInstance().getEquipStats(itemId);

            if (toSpawn == null) {
                c.write(CWvsContext.enableActions());
                return;
            }
            Mob ht = null;
            int type = 0;
            for (Map.Entry<String, Integer> i : toSpawn.entrySet()) {
                if (i.getKey().startsWith("mob") && Randomizer.nextInt(99) <= i.getValue()) {
                    ht = MapleLifeFactory.getMonster(Integer.parseInt(i.getKey().substring(3)));
                    chr.getMap().spawnMonster_sSack(ht, chr.getPosition(), type);
                }
            }
            if (ht == null) {
                c.write(CWvsContext.enableActions());
                return;
            }

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.write(CWvsContext.enableActions());
    }

}
