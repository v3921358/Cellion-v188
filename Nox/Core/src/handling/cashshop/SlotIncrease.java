package handling.cashshop;

import client.MapleCharacterCreationUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.MapleInventoryType;
import constants.GameConstants;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import server.CashItemFactory;
import server.CashItemInfo;
import server.maps.objects.MapleCharacter;
import server.quest.MapleQuest;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class SlotIncrease {

    public static void inventory(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        int itemPrice = iPacket.DecodeInteger();
        boolean cameFromCoupon = iPacket.DecodeByte() > 0;
        MapleInventoryType inventoryType;
        if (cameFromCoupon) {
            inventoryType = getInventoryType(iPacket.DecodeInteger());
            if (chr.getCSPoints(itemPrice) >= 6000 && chr.getInventory(inventoryType).getSlotLimit() < 89) {
                chr.modifyCSPoints(itemPrice, -6000, false);
                chr.getInventory(inventoryType).addSlot((byte) 8);
                chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(inventoryType).getSlotLimit());
                c.write(CField.getCharInfo(chr));
            } else {
                c.write(CSPacket.sendCSFail(0xA4));
            }
        } else {
            inventoryType = MapleInventoryType.getByType(iPacket.DecodeByte());
            if (chr.getCSPoints(itemPrice) >= 4000 && chr.getInventory(inventoryType).getSlotLimit() < 93) {
                chr.modifyCSPoints(itemPrice, -4000, false);
                chr.getInventory(inventoryType).addSlot((byte) 4);
                chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(inventoryType).getSlotLimit());
                c.write(CField.getCharInfo(chr));
            } else {
                c.write(CSPacket.sendCSFail(0xA4));
            }
        }
    }

    public static void storage(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        int itemPrice = iPacket.DecodeInteger();
        int coupon = iPacket.DecodeByte() > 0 ? 2 : 1;
        if (chr.getCSPoints(itemPrice) >= 4000 * coupon && chr.getStorage().getSlots() < (49 - (4 * coupon))) {
            chr.modifyCSPoints(itemPrice, -4000 * coupon, false);
            chr.getStorage().increaseSlots((byte) (4 * coupon));
            chr.getStorage().saveToDB();
            chr.dropMessage(1, "Storage slots increased to: " + chr.getStorage().getSlots());
            c.write(CField.getCharInfo(chr));
        } else {
            c.write(CSPacket.sendCSFail(0xA4));
        }
    }

    public static void charSlots(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        final int itemPrice = iPacket.DecodeInteger();
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInteger());
        int slots = MapleCharacterCreationUtil.getCharacterSlots(c.getAccID(), c.getWorld());

        if (slots == 0
                || // load database failed.
                iteminfo == null
                || c.getPlayer().getCSPoints(itemPrice) < iteminfo.getPrice()
                || slots >= GameConstants.characterSlotMax
                || iteminfo.getId() != 5430000) {
            c.write(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        }

        if (MapleCharacterCreationUtil.gainCharacterSlot(c.getAccID(), c.getWorld(), slots)) {
            c.getPlayer().modifyCSPoints(itemPrice, -iteminfo.getPrice(), false);
            chr.dropMessage(1, "Character slots increased to: " + (slots + 1));
        } else {
            c.write(CSPacket.sendCSFail(0));
        }
    }

    public static void pendantSlots(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.DecodeByte(); //Action is short?
        iPacket.DecodeInteger(); //always 1 - No Idea
        int sn = iPacket.DecodeInteger();
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(sn);
        if (iteminfo == null || c.getPlayer().getCSPoints(1) < iteminfo.getPrice() || iteminfo.getId() / 10000 != 555) {
            c.write(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        }
        MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
            c.write(CSPacket.sendCSFail(0));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) iteminfo.getPeriod() * 24 * 60 * 60000)));
            c.getPlayer().modifyCSPoints(1, -iteminfo.getPrice(), false);
            chr.dropMessage(1, "Additional pendant slot gained.");
        }
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }
}
