package handling.game;

import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseExpPotionHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        //iPacket: [F5 4F D6 2E] [60 00] [F4 06 22 00]
        System.err.println("eror");
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        int itemid = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1
                || toUse.getItemId() != itemid || chr.getLevel() >= 250
                || chr.hasBlockedInventory() || itemid / 10000 != 223) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        if (itemid != 2230004) { //for now
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        int level = chr.getLevel();
        chr.gainExp(chr.getNeededExp() - chr.getExp(), true, true, false);
        boolean first = false;
        boolean last = false;
        int potionDstLevel = 18;
        if (!chr.getInfoQuest(7985).contains("2230004=")) {
            first = true;
        } else if (chr.getInfoQuest(7985).equals("2230004=" + potionDstLevel + "#384")) {
            last = true;
        }
        c.SendPacket(CWvsContext.updateExpPotion(last ? 0 : 2, chr.getId(), itemid, first, level, potionDstLevel));
        if (first) {
            chr.updateInfoQuest(7985, "2230004=" + level + "#384");
        }
        if (last) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.SendPacket(CWvsContext.enableActions());
    }
}
