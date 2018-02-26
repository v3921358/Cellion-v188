package handling.game;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.Randomizer;
import net.InPacket;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class UseGoldenHammerHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        //[21 D5 10 04] [16 00 00 00] [7B B0 25 00] [01 00 00 00] [03 00 00 00]
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        byte slot = (byte) iPacket.DecodeInteger();
        int itemId = iPacket.DecodeInteger();
        iPacket.Skip(4);
        byte equipslot = (byte) iPacket.DecodeInteger();
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        Equip equip = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(equipslot);

        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.write(CWvsContext.enableActions());
            return;
        }
        int success;
        if (itemId == 2470004 && Randomizer.nextInt(100) < 20) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if ((itemId == 2470001 || itemId == 2470002) && Randomizer.nextInt(100) < 50) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if (itemId == 2470000 || itemId == 2470003) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else {
            success = 1;
        }
        c.write(CSPacket.GoldenHammer((byte) 2, success));
        equip.setViciousHammer((byte) 1);
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true);
    }

}
