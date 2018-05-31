package handling.cashshop;

import client.CharacterCreationUtil;
import client.ClientSocket;
import client.QuestStatus;
import enums.InventoryType;
import constants.GameConstants;
import database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import tools.packet.CSPacket;

/**
 * Slot Increase Handlers
 * @author Mazen Massoud
 * @author Kaz
 */
public class SlotIncrease {

    public static void OnInventoryRequest(InPacket iPacket, ClientSocket c, User pPlayer) {
        iPacket.Skip(7);
        byte nType = (byte) iPacket.DecodeByte();
        if (pPlayer.getCSPoints(2) >= 6000 && pPlayer.getInventory(getInventoryType(nType)).getSlotLimit() < 89) {
            pPlayer.modifyCSPoints(2, -6000, false);
            pPlayer.expandInventory(nType, 8);
            pPlayer.dropMessage(1, "Your " + getInventoryType(nType) + " inventory slots have been increased successfully. ");
            pPlayer.saveToDB(false, false);
        } else {
            c.SendPacket(CSPacket.sendCSFail(0xA4));
        }
    }
    
    public static void OnStorageRequest(InPacket iPacket, ClientSocket c, User pPlayer) {
        iPacket.Skip(1);
        int nPrice = iPacket.DecodeInt();
        int nCoupon = iPacket.DecodeByte() > 0 ? 2 : 1;
        if (pPlayer.getCSPoints(nPrice) >= 4000 * nCoupon && pPlayer.getStorage().getSlots() < (49 - (4 * nCoupon))) {
            pPlayer.modifyCSPoints(nPrice, -4000 * nCoupon, false);
            pPlayer.getStorage().increaseSlots((byte) (4 * nCoupon));
            
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

    public static void OnCharacterSlotRequest(InPacket iPacket, ClientSocket c, User pPlayer) {
        int nSlots = CharacterCreationUtil.getCharacterSlots(c.getAccID(), pPlayer.getWorld());

        if (nSlots == 0 || c.getPlayer().getCSPoints(2) < 6900 || nSlots >= GameConstants.characterSlotMax) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        if (CharacterCreationUtil.gainCharacterSlot(c.getAccID(), pPlayer.getWorld(), nSlots)) {
            c.getPlayer().modifyCSPoints(2, -6900, false);
            pPlayer.dropMessage(1, "Your character slots have been increased successfully.");
        } else {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
    }
    public static void OnPendantSlotRequest(InPacket iPacket, ClientSocket c, User pPlayer) {

        QuestStatus pPendantStatus = c.getPlayer().getQuestNoAdd(Quest.getInstance(GameConstants.PENDANT_SLOT));

        if (pPlayer.getCSPoints(2) < 30000) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        if (pPendantStatus != null && pPendantStatus.getCustomData() != null && Long.parseLong(pPendantStatus.getCustomData()) >= System.currentTimeMillis()) {
            pPlayer.dropMessage(1, "You already have access to an additional pendant slot.");
            c.SendPacket(CSPacket.sendCSFail(0));
        } else {
            pPlayer.getQuestNAdd(Quest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) 7 * 24 * 60 * 60000)));
            pPlayer.modifyCSPoints(1, -30000, false);
            pPlayer.dropMessage(1, "You have unlocked access to an additional pendant slot.");
            pPlayer.saveToDB(false, false);
        }
    }

    private static InventoryType getInventoryType(final int nID) {
        switch (nID) {
            case 1:
            case 50200093:
                return InventoryType.EQUIP;
            case 2:
            case 50200094:
                return InventoryType.USE;
            case 3:
            case 50200197:
                return InventoryType.SETUP;
            case 4:
            case 50200095:
                return InventoryType.ETC;
            default:
                return InventoryType.UNDEFINED;
        }
    }
}
