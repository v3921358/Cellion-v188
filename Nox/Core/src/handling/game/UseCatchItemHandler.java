package handling.game;

import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import java.time.LocalDateTime;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseCatchItemHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemid = iPacket.DecodeInt();
        final Mob mob = chr.getMap().getMonsterByOid(iPacket.DecodeInt());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMap map = chr.getMap();

        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null && !chr.hasBlockedInventory() && itemid / 10000 == 227 && MapleItemInformationProvider.getInstance().getCardMobId(itemid) == mob.getId()) {
            if (!MapleItemInformationProvider.getInstance().isMobHP(itemid) || mob.getHp() <= mob.getMobMaxHp() / 2) {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 1));
                map.killMonster(mob, chr, true, false, (byte) 1);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
                if (MapleItemInformationProvider.getInstance().getCreateId(itemid) > 0) {
                    MapleInventoryManipulator.addById(c, MapleItemInformationProvider.getInstance().getCreateId(itemid), (short) 1, "Catch item " + itemid + " on " + LocalDateTime.now());
                }
            } else {
                map.broadcastMessage(MobPacket.catchMonster(mob.getObjectId(), itemid, (byte) 0));
                c.SendPacket(CWvsContext.catchMob(mob.getId(), itemid, (byte) 0));
            }
        }
        c.SendPacket(CWvsContext.enableActions());
    }
}
