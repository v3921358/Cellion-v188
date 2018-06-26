/*
 * Cellion Development
 */
package client.inventory;

import enums.InventoryType;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import tools.Utility;

/**
 * Loot Box
 * @author Mazen Massoud
 */
public class LootBox {
    
    /**
     * Loot Box IDs
     */
    public static final int NEBULITE_BOX = 2430692, // Note: Nebulite Socket Creator : 2930000 
                        EQUIPMENT_BOX_LV30 = 0,
                        EQUIPMENT_BOX_LV50 = 0,
                        EQUIPMENT_BOX_LV70 = 0,
                        BASIC_HAIR_BOX = 0,
                        BASIC_FACE_BOX = 0,
                        PREMIUM_HAIR_BOX = 0,
                        PREMIUM_FACE_BOX = 0;
    
    /**
     * Nebulite Box
     */
    private static int[] aNebulite = {
        3060000, // [D] Nebulite (STR)
        3060001, // [D] Nebulite (STR)
        3060002, // [D] Nebulite (STR)
        3060010, // [D] Nebulite (DEX)
        3060011, // [D] Nebulite (DEX)
        3060012, // [D] Nebulite (DEX)
        3060020, // [D] Nebulite (INT)
        3060021, // [D] Nebulite (INT)
        3060022, // [D] Nebulite (INT)
        3060030, // [D] Nebulite (LUK)
        3060031, // [D] Nebulite (LUK)
        3060032, // [D] Nebulite (LUK)
        3060040, // [D] Nebulite (Max HP)
        3060041, // [D] Nebulite (Max HP)
        3060042, // [D] Nebulite (Max HP)
        3060050, // [D] Nebulite (Max MP)
        3060051, // [D] Nebulite (Max MP)
        3060052, // [D] Nebulite (Max MP)
        3060060, // [D] Nebulite (DEX)
        3060061, // [D] Nebulite (DEX)
        3060070, // [D] Nebulite (DEX)
        3060071, // [D] Nebulite (DEX)
        3060080, // [D] Nebulite (Speed)
        3060081, // [D] Nebulite (Speed)
        3060090, // [D] Nebulite (Jump)
        3060091, // [D] Nebulite (Jump)
        3060100, // [D] Nebulite (Weapon ATT)
        3060110, // [D] Nebulite (Magic ATT)
        3060120, // [D] Nebulite (Weapon Defense)
        3060121, // [D] Nebulite (Weapon Defense)
        3060122, // [D] Nebulite (Weapon Defense)
        3060130, // [D] Nebulite (Magic Defense)
        3060131, // [D] Nebulite (Magic Defense)
        3060132, // [D] Nebulite (Magic Defense)
        3060140, // [D] Nebulite (Face)
        3060150, // [D] Nebulite (Face)
        3060160, // [D] Nebulite (Face)
        3060170, // [D] Nebulite (Face)
        3060180, // [D] Nebulite (Face)
        3061000, // [C] Nebulite (STR)
        3061001, // [C] Nebulite (STR)
        3061010, // [C] Nebulite (DEX)
        3061011, // [C] Nebulite (DEX)
        3061020, // [C] Nebulite (INT)
        3061021, // [C] Nebulite (INT)
        3061030, // [C] Nebulite (LUK)
        3061031, // [C] Nebulite (LUK)
        3061040, // [C] Nebulite (Max HP)
        3061041, // [C] Nebulite (Max HP)
        3061042, // [C] Nebulite (Max HP)
        3061050, // [C] Nebulite (Max MP)
        3061051, // [C] Nebulite (Max MP)
        3061052, // [C] Nebulite (Max MP)
        3061060, // [C] Nebulite (DEX)
        3061061, // [C] Nebulite (DEX)
        3061070, // [C] Nebulite (DEX)
        3061071, // [C] Nebulite (DEX)
        3061080, // [C] Nebulite (Speed)
        3061081, // [C] Nebulite (Speed)
        3061090, // [C] Nebulite (Jump)
        3061091, // [C] Nebulite (Jump)
        3061100, // [C] Nebulite (Weapon ATT)
        3061110, // [C] Nebulite (Magic ATT)
        3061120, // [C] Nebulite (Weapon Defense)
        3061121, // [C] Nebulite (Weapon Defense)
        3061122, // [C] Nebulite (Weapon Defense)
        3061123, // [C] Nebulite (Weapon Defense)
        3061124, // [C] Nebulite (Weapon Defense)
        3061125, // [C] Nebulite (Weapon Defense)
        3061130, // [C] Nebulite (Magic Defense)
        3061131, // [C] Nebulite (Magic Defense)
        3061132, // [C] Nebulite (Magic Defense)
        3061133, // [C] Nebulite (Magic Defense)
        3061134, // [C] Nebulite (Magic Defense)
        3061135, // [C] Nebulite (Magic Defense)
        3061140, // [C] Nebulite (STR %)
        3061150, // [C] Nebulite (DEX %)
        3061160, // [C] Nebulite (INT %)
        3061170, // [C] Nebulite (LUK %)
        3061180, // [C] Nebulite (Max HP %)
        3061190, // [C] Nebulite (Max MP %)
        3061200, // [C] Nebulite (DEX %)
    };
    
    /**
     * Equipment Box
     * Syntax: {Required Job ID (0 For Any), Item ID, Item Quantity}
     */
    private static int[][] aEquipment_Lv30 = {
        {0, 0, 1}, // Item Name
        {0, 0, 1}, // Item Name
    };
    
    private static int[][] aEquipment_Lv50 = {
        {0, 0, 1}, // Item Name
        {0, 0, 1}, // Item Name
    };
    
    private static int[][] aEquipment_Lv70 = {
        {0, 0, 1}, // Item Name
        {0, 0, 1}, // Item Name
    };
    
    /**
     * Cosmetic Box
     */
    private static int[] aBasicHair = {
        0, 
        0, 
    };
    
    private static int[] aBasicFace = {
        0, 
        0, 
    };
    
    private static int[] aPremiumHair = {
        0, 
        0, 
    };
    
    private static int[] aPremiumFace = {
        0, 
        0, 
    };
    
    public static boolean OnNebuliteBoxRequest(User pPlayer) {
        if (!pPlayer.haveItem(NEBULITE_BOX) || !pPlayer.hasInventorySpace(4, 1)) {
            return false;
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, LootBox.NEBULITE_BOX, 1, false, true);
        pPlayer.gainItem(aNebulite[Utility.getRandomSelection(aNebulite.length)], 1); // Give the player a random Nebulite.
        return true;
    }
    
    public static boolean OnEquipmentBoxRequest(User pPlayer, byte nBoxLevel) {
        int nBoxID;
        int[][] aBoxItems;
        
        switch (nBoxLevel) {
            case 30:
                nBoxID = EQUIPMENT_BOX_LV30;
                aBoxItems = aEquipment_Lv30;
                break;
            case 50:
                nBoxID = EQUIPMENT_BOX_LV50;
                aBoxItems = aEquipment_Lv50;
                break;
            case 70:
                nBoxID = EQUIPMENT_BOX_LV70;
                aBoxItems = aEquipment_Lv70;
                break;
            default:
                return false;
        }
        
        if (!pPlayer.haveItem(nBoxID) || !pPlayer.hasInventorySpace(1, aBoxItems.length)) {
            return false;
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, nBoxID, 1, false, true);
        for (int i = 0; i < aBoxItems.length; i++) {
            pPlayer.gainItem(aBoxItems[0][i], aBoxItems[1][i]); // Give the player all items from the specified box.
        }
        return true;
    }
    
    public static boolean OnCosmeticBoxRequest(User pPlayer, byte nBoxType) {
        int nBoxID;
        int[] aBoxSelections;
        
        switch (nBoxType) {
            case 1:
                nBoxID = BASIC_HAIR_BOX;
                aBoxSelections = aBasicHair;
                break;
            case 2:
                nBoxID = BASIC_FACE_BOX;
                aBoxSelections = aBasicFace;
                break;
            case 3:
                nBoxID = PREMIUM_HAIR_BOX;
                aBoxSelections = aPremiumHair;
                break;
            case 4:
                nBoxID = PREMIUM_FACE_BOX;
                aBoxSelections = aPremiumFace;
                break;
            default:
                return false;
        }
        
        if (!pPlayer.haveItem(nBoxID)) {
            return false;
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, nBoxID, 1, false, true); // Consume the Cosmetic Box.
        pPlayer.setFace(aBoxSelections[Utility.getRandomSelection(aBoxSelections.length)]); // Give the player a random style.
        return true;
    }
}
