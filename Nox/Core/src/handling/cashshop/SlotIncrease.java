package handling.cashshop;

import client.MapleCharacterCreationUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.Database;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.sql.Connection;
import java.sql.SQLException;
import server.CashItemFactory;
import server.CashItemInfo;
import server.maps.objects.User;
import server.quest.MapleQuest;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 * @author Mazen Massoud
 */
public class SlotIncrease {

    public static void inventory(InPacket iPacket, MapleClient c, User pPlayer) {
        iPacket.Skip(7);
        byte nType = (byte) iPacket.Decode();
        System.out.println(nType);
        if (pPlayer.getCSPoints(2) >= 6000 && pPlayer.getInventory(getInventoryType(nType)).getSlotLimit() < 89) {
            pPlayer.modifyCSPoints(2, -6000, false);
            pPlayer.expandInventory(nType, 8);
            pPlayer.dropMessage(1, "Your " + getInventoryType(nType) + " inventory slots have been increased successfully. ");
            pPlayer.saveToDB(false, false);
        } else {
            c.SendPacket(CSPacket.sendCSFail(0xA4));
        }
    }

    /*public static void inventory(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        int itemPrice = iPacket.DecodeInt();
        boolean cameFromCoupon = iPacket.DecodeByte() > 0;
        MapleInventoryType inventoryType;
        if (cameFromCoupon) {
            inventoryType = getInventoryType(iPacket.DecodeInt());
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
    }*/
    public static void storage(InPacket iPacket, MapleClient c, User pPlayer) {
        iPacket.Skip(1);
        int itemPrice = iPacket.DecodeInt();
        int coupon = iPacket.DecodeByte() > 0 ? 2 : 1;
        if (pPlayer.getCSPoints(itemPrice) >= 4000 * coupon && pPlayer.getStorage().getSlots() < (49 - (4 * coupon))) {
            pPlayer.modifyCSPoints(itemPrice, -4000 * coupon, false);
            pPlayer.getStorage().increaseSlots((byte) (4 * coupon));
            try (Connection con = Database.GetConnection()) {
                pPlayer.getStorage().saveToDB(con);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            pPlayer.dropMessage(1, "Your storage slots have been increased successfully.");
            //c.write(CField.getCharInfo(chr));
        } else {
            c.SendPacket(CSPacket.sendCSFail(0xA4));
        }
    }

    public static void charSlots(InPacket iPacket, MapleClient c, User pPlayer) {
        int nSlots = MapleCharacterCreationUtil.getCharacterSlots(c.getAccID(), pPlayer.getWorld());

        if (nSlots == 0 || c.getPlayer().getCSPoints(2) < 6900 || nSlots >= GameConstants.characterSlotMax) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        if (MapleCharacterCreationUtil.gainCharacterSlot(c.getAccID(), pPlayer.getWorld(), nSlots)) {
            c.getPlayer().modifyCSPoints(2, -6900, false);
            pPlayer.dropMessage(1, "Your character slots have been increased successfully.");
        } else {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
    }

    /*public static void charSlots(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        final int itemPrice = iPacket.DecodeInt();
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());
        int slots = MapleCharacterCreationUtil.getCharacterSlots(c.getAccID(), c.getWorld());

        if (slots == 0
                || // load database failed.
                iteminfo == null
                || c.getPlayer().getCSPoints(itemPrice) < iteminfo.getPrice()
                || slots >= GameConstants.characterSlotMax
                || iteminfo.getId() != 5430000) {
            c.write(CSPacket.sendCSFail(0));
            //playerCashShopInfo(c);
            return;
        }

        if (MapleCharacterCreationUtil.gainCharacterSlot(c.getAccID(), c.getWorld(), slots)) {
            c.getPlayer().modifyCSPoints(itemPrice, -iteminfo.getPrice(), false);
            chr.dropMessage(1, "Character slots increased to: " + (slots + 1));
        } else {
            c.write(CSPacket.sendCSFail(0));
        }
    }*/
    public static void pendantSlots(InPacket iPacket, MapleClient c, User pPlayer) {

        MapleQuestStatus pPendantStatus = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));

        if (pPlayer.getCSPoints(2) < 30000) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        if (pPendantStatus != null && pPendantStatus.getCustomData() != null && Long.parseLong(pPendantStatus.getCustomData()) >= System.currentTimeMillis()) {
            pPlayer.dropMessage(1, "You already have access to an additional pendant slot.");
            c.SendPacket(CSPacket.sendCSFail(0));
        } else {
            pPlayer.getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) 7 * 24 * 60 * 60000)));
            pPlayer.modifyCSPoints(1, -30000, false);
            pPlayer.dropMessage(1, "You have unlocked access to an additional pendant slot.");
            pPlayer.saveToDB(false, false);
        }
    }

    /*public static void pendantSlots(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.DecodeByte(); //Action is short?
        iPacket.DecodeInt(); //always 1 - No Idea
        int sn = iPacket.DecodeInt();
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
    }*/
    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 1:
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 2:
            case 50200094:
                return MapleInventoryType.USE;
            case 3:
            case 50200197:
                return MapleInventoryType.SETUP;
            case 4:
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }
}
