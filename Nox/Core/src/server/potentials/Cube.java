/*
 * Cellion Development
 */
package server.potentials;

import enums.ItemPotentialTierType;
import client.inventory.Equip;
import enums.EquipSlotType;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import constants.InventoryConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import enums.PotentialLine;
import static enums.PotentialLine.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CubePacket;
import tools.packet.WvsContext;

/**
 * Cube System Handler
 * @author Mazen Massoud
 */
public class Cube extends ItemPotentialProvider {
    
    /**
     * Cube Request
     * Decides how the cube is handled.
     * 
     * @param pPlayer
     * @param pEquip
     * @param nCubeID 
     * @param bDisplayUI
     * @return
     */
    public static boolean OnCubeRequest(User pPlayer, Equip pEquip, int nCubeID, boolean bDisplayUI) {
        if (pEquip == null || pPlayer.getInventory(InventoryType.USE).getNumFreeSlot() < 1) {
            pPlayer.completeDispose();
            return false;
        }
        
        ItemPotentialTierType pMaxTier, pPreviousTier = pEquip.getPotentialTier();
        int nTierDownRate, nTierUpRate, nFragmentID = 0, nMesoCost = 0;
        boolean bHidePotentialAfterReset = false;
       
        switch (nCubeID) {
            
            // Crafted Cubes
            case ItemConstants.UNK_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE3:
                pMaxTier = ItemPotentialTierType.Epic;
                nTierUpRate = ItemPotentialProvider.RATE_GAMECUBE_TIERUP;
                nTierDownRate = 0;
                break;
            case ItemConstants.TIMIC_CUBE:
            case ItemConstants.HERMES_CUBE:
            case ItemConstants.OCCULT_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE2:
            case ItemConstants.MAPLE_SAINT_WEAPON_CUBE:
            case ItemConstants.MASTER_CRAFTMANS_CUBE: // This cube is available to players.
            case ItemConstants.MASTER_CRAFTMANS_CUBE2:
            case ItemConstants.MASTER_CRAFTMANS_CUBE_UNTRADEABLE:
                pMaxTier = ItemPotentialTierType.Unique;
                nTierUpRate = ItemPotentialProvider.RATE_GAMECUBE_TIERUP;
                nTierDownRate = 0;
                nMesoCost = 1000;
                break;
            case ItemConstants.MEISTER_CUBE:
            case ItemConstants.MEISTER_CUBE2:
            case ItemConstants.MEISTER_CUBE_UNTRADEABLE:
                pMaxTier = ItemPotentialTierType.Legendary;
                nTierUpRate = ItemPotentialProvider.RATE_GAMECUBE_TIERUP;
                nTierDownRate = 0;
                break;
                
            // Special Cubes
            case ItemConstants.RED_CUBE: // This cube is available to players.
                pMaxTier = ItemPotentialTierType.Legendary;
                nFragmentID = ItemConstants.RED_CUBE_FRAGMENT;
                nTierUpRate = 5;
                nTierDownRate = 0;
                nMesoCost = 10000;
                break;
            case ItemConstants.PLATINUM_MIRACLE_CUBE: // This cube is available to players.
                pMaxTier = ItemPotentialTierType.Legendary; 
                nTierUpRate = 10;
                nTierDownRate = 0;
                nMesoCost = 10000;
                break;
            case ItemConstants.BONUS_POTENTIAL_CUBE: // This cube is available to players.
                pMaxTier = ItemPotentialTierType.Legendary;
                nFragmentID = ItemConstants.BONUS_POTENTIAL_CUBE_FRAGMENT;
                nTierUpRate = 5;
                nTierDownRate = 0;
                nMesoCost = 30000;
                break;
            default:
                pPlayer.SendPacket(CField.enchantResult(0));
                System.err.println("[Error] Attempting to handle an uncoded Cube potential request.");
                return false;
        }

        if (pPreviousTier.getValue() > pMaxTier.getValue()) { // Exploit Check
            LogHelper.PACKET_EDIT_HACK.get().info(
                String.format("[PotentialProvider] %s [ChrID: %d; AccId %d] has tried to use a cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                pPlayer.getName(), pPlayer.getId(), pPlayer.getClient().getAccID(),
                pMaxTier.toString(),
                pPreviousTier.toString(),
                pEquip.getItemId(), nCubeID));
            //pPlayer.getClient().Close();
            return false;
        }
        
        if (pPlayer.getMeso() < nMesoCost) {
            pPlayer.dropMessage(5, "Sorry, you do not have enough mesos to perform this action.");
            return false;
        }
        
        final boolean bCubeResult = OnCubeResult(pEquip, nCubeID, nTierUpRate, nTierDownRate, pMaxTier, bHidePotentialAfterReset);
        
        if (bCubeResult) {
            
            if (nFragmentID > 0) MapleInventoryManipulator.addById(pPlayer.getClient(), nFragmentID, (short) 1, "Cube on " + LocalDateTime.now());
            if (nMesoCost > 0) pPlayer.gainMeso(-nMesoCost, true, true); // Cube Meso Cost
            
            // Update Inventory Equipment 
            List<ModifyInventory> modifications = new ArrayList<>();
            modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pEquip));
            pPlayer.SendPacket(WvsContext.inventoryOperation(true, modifications));

            if (!bHidePotentialAfterReset) {
                switch (nCubeID) {
                    case ItemConstants.UNK_CUBE:
                    case ItemConstants.OCCULT_CUBE_UNTRADEABLE3:
                    case ItemConstants.TIMIC_CUBE:
                    case ItemConstants.HERMES_CUBE:
                    case ItemConstants.OCCULT_CUBE:
                    case ItemConstants.OCCULT_CUBE_UNTRADEABLE:
                    case ItemConstants.OCCULT_CUBE_UNTRADEABLE2:
                    case ItemConstants.MAPLE_SAINT_WEAPON_CUBE:
                    case ItemConstants.MASTER_CRAFTMANS_CUBE:
                    case ItemConstants.MASTER_CRAFTMANS_CUBE2:
                    case ItemConstants.MASTER_CRAFTMANS_CUBE_UNTRADEABLE:
                    case ItemConstants.MEISTER_CUBE:
                    case ItemConstants.MEISTER_CUBE2:
                    case ItemConstants.MEISTER_CUBE_UNTRADEABLE:
                        if (bDisplayUI) pPlayer.SendPacket(CubePacket.OnInGameCubeResult(pPlayer.getId(), pPreviousTier != pEquip.getPotentialTier(), pEquip.getPosition(), nCubeID, pEquip));
                        break;
                    case ItemConstants.PLATINUM_MIRACLE_CUBE:
                        //pPlayer.SendPacket(CubePacket.OnPlatinumCubeResult(pPlayer.getId(), pPreviousTier != pEquip.getPotentialTier(), pEquip.getPosition(), nCubeID, pEquip));
                        break;
                    case ItemConstants.RED_CUBE:
                        if (bDisplayUI) pPlayer.SendPacket(CubePacket.OnRedCubeResult(pPlayer.getId(), pPreviousTier != pEquip.getPotentialTier(), pEquip.getPosition(), nCubeID, pEquip));
                        break;
                    case ItemConstants.BONUS_POTENTIAL_CUBE:
                        if (bDisplayUI) pPlayer.SendPacket(CubePacket.OnBonusCubeResult(pPlayer.getId(), pPreviousTier != pEquip.getPotentialTier(), pEquip.getPosition(), nCubeID, pEquip));
                        break;
                }
            }
        } else {
            pPlayer.dropMessage(5, "The potential of this item cannot be reset.");
            return false;
        }

        pPlayer.getMap().broadcastPacket(CField.showPotentialReset(pPlayer.getId(), bCubeResult, pEquip.getItemId()));
        pPlayer.SendPacket(CField.enchantResult(bCubeResult ? 3 : 0));

        if (pPlayer.isDeveloper()) {
            pPlayer.yellowMessage("[Potential Debug] First Line ID : " + pEquip.getPotential1());
            pPlayer.yellowMessage("[Potential Debug] Second Line ID :" + pEquip.getPotential2());
            pPlayer.yellowMessage("[Potential Debug] Third Line ID : " + pEquip.getPotential3());
        }
        return true;
    }
    
    /**
     * Cube Result
     * Applies the potential results from the cube to the item.
     * 
     * @param pEquip
     * @param nCubeID
     * @param nTierUpRate
     * @param nTierDownRate
     * @param pMaxTier
     * @param bHidePotential
     * @return 
     */
    public static boolean OnCubeResult(Equip pEquip, int nCubeID, int nTierUpRate, int nTierDownRate, ItemPotentialTierType pMaxTier, boolean bHidePotential) {
        final MapleItemInformationProvider pItemInfo = MapleItemInformationProvider.getInstance();
        final int nReqLevel = pItemInfo.getReqLevel(pEquip.getItemId());
        ItemPotentialTierType pPotentialTier = pEquip.getPotentialTier();
        
        if (pEquip.getPotentialTier().isHiddenType() && nCubeID == ItemConstants.RED_CUBE) return false;
        if (pEquip.getPotentialBonusTier().isHiddenType() && nCubeID == ItemConstants.BONUS_POTENTIAL_CUBE) return false;
        if (nReqLevel < 10) return false;
        

        if ((Randomizer.nextInt(100) < nTierDownRate)) { // Potential Tier Decrease Chance
            ItemPotentialTierType pNewTier = ItemPotentialTierType.getItemPotentialTierTypeFromInt(pPotentialTier.getValue() - 1);
            if (pNewTier != ItemPotentialTierType.None && !pNewTier.isHiddenType()) {
                pPotentialTier = pNewTier;
            }
        } else if (Randomizer.nextInt(100) < (nTierUpRate * ServerConstants.MIRACLE_CUBE_RATE)) { // Potential Tier Increase Chance
            ItemPotentialTierType pNewTier = ItemPotentialTierType.getItemPotentialTierTypeFromInt(pPotentialTier.getValue() + 1);
            if (pNewTier != ItemPotentialTierType.None && !pNewTier.isHiddenType()) {
                if (pNewTier.getValue() <= pMaxTier.getValue()) {
                    pPotentialTier = pNewTier;
                }
            }
        }
        
        switch (nCubeID) {
            
            // Potential
            case ItemConstants.UNK_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE3:
            case ItemConstants.TIMIC_CUBE:
            case ItemConstants.HERMES_CUBE:
            case ItemConstants.OCCULT_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE2:
            case ItemConstants.MAPLE_SAINT_WEAPON_CUBE:
            case ItemConstants.MASTER_CRAFTMANS_CUBE:
            case ItemConstants.MASTER_CRAFTMANS_CUBE2:
            case ItemConstants.MASTER_CRAFTMANS_CUBE_UNTRADEABLE:
            case ItemConstants.MEISTER_CUBE:
            case ItemConstants.MEISTER_CUBE2:
            case ItemConstants.MEISTER_CUBE_UNTRADEABLE:
            case ItemConstants.RED_CUBE: 
                if (pEquip.getPotential1() != 0) {
                    pEquip.setPotential1(generatePotential(pEquip, pPotentialTier));
                }
                if (pEquip.getPotential2() != 0) {
                    pEquip.setPotential2(generatePotential(pEquip, pPotentialTier));
                }
                if (pEquip.getPotential3() != 0) {
                    pEquip.setPotential3(generatePotential(pEquip, pPotentialTier));
                }
                break;
            
            // All Legendary Potential Lines
            case ItemConstants.PLATINUM_MIRACLE_CUBE: 
                if (pEquip.getPotential1() != 0) {
                    pEquip.setPotential1(generatePotential(pEquip, ItemPotentialTierType.Legendary));
                }
                if (pEquip.getPotential2() != 0) {
                    pEquip.setPotential2(generatePotential(pEquip, ItemPotentialTierType.Legendary));
                }
                if (pEquip.getPotential3() != 0) {
                    pEquip.setPotential3(generatePotential(pEquip, ItemPotentialTierType.Legendary));
                }
                break;
                
            // Bonus Potential
            case ItemConstants.BONUS_POTENTIAL_CUBE: 
                if (pEquip.getBonusPotential1() != 0) {
                    pEquip.setBonusPotential1(generateBonusPotential(pEquip));
                }
                if (pEquip.getBonusPotential2() != 0) {
                    pEquip.setBonusPotential2(generateBonusPotential(pEquip));
                }
                if (pEquip.getBonusPotential3() != 0) {
                    pEquip.setBonusPotential3(generateBonusPotential(pEquip));
                }
                break;
            default:
                return false;
        }
        
        if (nTierDownRate != 0 || nTierUpRate != 0) {
            if (bHidePotential) {
                ItemPotentialTierType pHiddenTier = ItemPotentialTierType.getHiddenPotentialTier(pPotentialTier);
                if (pHiddenTier != ItemPotentialTierType.None) {
                    pEquip.setPotentialTier(pHiddenTier);
                } else {
                    pEquip.setPotentialTier(pPotentialTier);
                }
            } else {
                pEquip.setPotentialTier(pPotentialTier);
            }
        }
        return true;
    }
    
    /**
     * Cube Bonus Potential Line Generator
     * @author Mazen Massoud
     *
     * @purpose Generate a bonus Potential Line based on fully customizable Potential tables based upon the Item Potential Tier and
     * @param pEquip
     */
    public static int generateBonusPotential(Equip pEquip/*, ItemPotentialTierType pTier*/) {
        
        ArrayList<Integer> aPossiblePotential = new ArrayList<Integer>();
        final EquipSlotType pSlot = MapleItemInformationProvider.getInstance().getSlotType(pEquip.getItemId());
        final int nReqLevel = MapleItemInformationProvider.getInstance().getReqLevel(pEquip.getItemId());

        int aEmblemLegendaryBonusPotential[] = {
            40292, // 40% Ignore Defence
            40291, // 35% Ignore Defence 
            30291, // 30% Ignore Defence
            42051, // 12% ATT
            32051, // 9% ATT 
            40052, // 12% M.ATT 
            30052, // 9% M.ATT 
            42070, // 12% DMG
            30070, // 9% DMG    
            40055, // 12% CRIT CHANCE 
            32057, // 9% CRIT CHANCE 
            40045, // 12% HP
            40046, // 12% MP                    
            42066, // 12% LUK 
            42065, // 12% INT
            40041, // 12% STR 
            42064, // 12% DEX
            30044, // 9% LUK
            30043, // 9% INT 
            30041, // 9% STR
            30042, // 9% DEX
            30045, // 9% HP
            30045, // 9% HP 
            30291, // 9% MP
            40086, // 9% ALL STAT  
            30086, // 6% ALL STAT 
            30094, // LUK PER 10 Levels +1 
            32093, // INT PER 10 Levels +1 
            32091, // STR PER 10 Levels +1 
            32092, // DEX PER 10 Levels +1 
            42094, // LUK PER 10 Levels +2 
            42093, // INT PER 10 Levels +2 
            42091, // STR PER 10 Levels +2     
            42092, // DEX PER 10 Levels +2
            42096, // MAG ATT PER 10 LEVELS +1
            42095, // ATT PER 10 LEVELS +1
            40011, // ATT +18
            42011, // ATT +14
            40012, // MAG ATT +18
            42012, // MAG ATT +14
        };

        int aWeaponLegendaryBonusPotential[] = {
            42602, // 18% Boss Damage
            32601, // 12% Boss Damage
            42051, // 12% ATT
            32051, // 9% ATT
            40052, // 12% M.ATT
            30052, // 9% M.ATT 
            42070, // 12% DMG 
            30070, // 9% DMG
            40055, // 12% CRIT CHANCE
            32057, // 9% CRIT CHANCE  
            42047, // MAX HP 10%
            32047, // MAX HP 7% 
            42066, // 12% LUK
            42065, // 12% INT
            40041, // 12% STR
            42064, // 12% DEX
            30044, // 9% LUK
            30043, // 9% INT
            30041, // 9% STR
            30042, // 9% DEX
            40086, // 9% ALL STAT
            30086, // 6% ALL STAT
            42046, // 10% MAX MP
            32048, // 7% MAX MP
            32206, // 15% TO REGEN MANA ON ATT
            32201, // 15% TO REGEN HP ON ATT
            30094, // LUK PER 10 Levels +1
            32093, // INT PER 10 Levels +1 
            32091, // STR PER 10 Levels +1 
            32092, // DEX PER 10 Levels +1 
            42094, // LUK PER 10 Levels +2
            42093, // INT PER 10 Levels +2
            42091, // STR PER 10 Levels +2
            42092, // DEX PER 10 Levels +2
            42096, // MAG ATT PER 10 LEVELS +1
            42095, // ATT PER 10 LEVELS +1 
            42292, // Ignore 4% DEF 
            40011, // ATT +18
            42011, // ATT +14
            40012, // MAG ATT +18
            42012, // MAG ATT +14
        };

        int aNonWeaponLegendaryBonusPotential[] = {
            42043, // 7% LUK
            42044, // 7% INT
            42041, // 7% STR  
            42042, // 7% DEX
            42045, // 10% MaxHP
            42048, // 10% MaxMP
            42055, // 10% DEF
            32044, // 5% LUK
            32043, // 5% INT
            32041, // 5% STR
            32042, // 5% DEX
            42086, // 5% All Stats
            32045, // 7% MaxHP 
            32046, // 7% MaxMP
            32056, // 7% DEF
            40005, // +250 HP
            42008, // +250 MP
            30094, // LUK PER 10 Levels +1
            32093, // INT PER 10 Levels +1
            32091, // STR PER 10 Levels +1 
            32092, // DEX PER 10 Levels +1 
            42094, // LUK PER 10 Levels +2
            42093, // INT PER 10 Levels +2
            42091, // STR PER 10 Levels +2
            42092, // DEX PER 10 Levels +2
            32551, // HP Recovery Items and Skills +20%
            42001, // Str +18
            42003, // Int +18
            42002, // Dex +18
            40004, // Luk +18
            40081, // ALL STAT +12
            42011, // Att +14  
            32802, // All Elemental Res +4% 
            42060, // Critical Damage 1%  
            42054, // 2% Mag ATT 
            42052, // 2% Wep ATT
            42058, // Crit Chance 2%
        };

        int aAccessoryLegendaryBonusPotential[] = {
            42650, // 5% Meso Drop Rate
            42656, // 5% Item Drop Rate
        };

        int aCustomUniqueBonusPotential[] = {
            32043, // 5% LUK
            32044, // 5% INT
            32041, // 5% STR  
            32042, // 5% DEX
            32045, // 7% MaxHP
            32048, // 7% MaxMP
            32055, // 7% DEF
            22044, // 3% LUK
            22043, // 3% INT
            22041, // 3% STR
            22042, // 3% DEX
            32086, // 3% All Stats
            22045, // 3% MaxHP 
            22046, // 3% MaxMP
            22056, // 3% DEF
            30005, // +200 HP
            32008, // +200 MP
            32001, // Str +14
            32003, // Int +14
            32002, // Dex +14
            30004, // Luk +14
            30081, // ALL STAT +8
            32011, // Att +13 
            30094, // LUK PER 10 Levels +1 
            32093, // INT PER 10 Levels +1 
            32091, // STR PER 10 Levels +1 
            32092, // DEX PER 10 Levels +1 
        };

        int aCustomEpicBonusPotential[] = {
            22043, // 4% LUK
            22044, // 4% INT
            22041, // 4% STR  
            22042, // 4% DEX
            22045, // 5% MaxHP
            22048, // 5% MaxMP
            22055, // 5% DEF
            12043, // 2% INT
            12041, // 2% STR
            12042, // 2% DEX
            22086, // 1% All Stats
            12045, // 2% MaxHP 
            12046, // 2% MaxMP
            12056, // 2% DEF
            20005, // +150 HP
            22008, // +150 MP
            22001, // Str +10
            22003, // Int +10
            22002, // Dex +10
            20004, // Luk +10
            20081, // ALL STAT +8
            22011, // Att +11 
        };

        int aCustomRareBonusPotential[] = {
            12043, // 2% LUK
            12044, // 2% INT
            12041, // 2% STR  
            12042, // 2% DEX
            12045, // 2% MaxHP
            12048, // 2% MaxMP
            12055, // 2% DEF
            10005, // +100 HP
            12008, // +100 MP
            12001, // Str +6
            12003, // Int +6
            12002, // Dex +6
            10004, // Luk +6
            10081, // ALL STAT +4
            12011, // Att +10
        };

        int aCustomBonusPotential[] = {
            32044, // 5% LUK
            32043, // 5% INT
            32041, // 5% STR
            32042, // 5% DEX
            42086, // 5% All Stats
            32045, // 7% MaxHP 
            32046, // 7% MaxMP
            32056, // 7% DEF
        };
        //if (pTier == ItemPotentialTierType.Legendary && nReqLevel >= 70) { // Only defined Legendary Lv. 70+ Potential Lines
        if (pSlot == EquipSlotType.Si_Emblem) {
            for (int nValue : aEmblemLegendaryBonusPotential) { // Adds Emblem Potential Values (Legendary)
                aPossiblePotential.add(nValue);
            }
        }
        if (isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
            for (int nValue : aWeaponLegendaryBonusPotential) { // Adds Weapon Potential Values (Legendary)
                aPossiblePotential.add(nValue);
            }
        }
        if (!isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) && !InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
            for (int nValue : aNonWeaponLegendaryBonusPotential) { // Adds Non-Weapon Potential Values (Legendary)
                aPossiblePotential.add(nValue);
            }
        }
        if (pSlot == EquipSlotType.Accessary_Eye || pSlot == EquipSlotType.Accessary_Face || pSlot == EquipSlotType.Accessary_Pocket) {
            for (int nValue : aAccessoryLegendaryBonusPotential) { // Adds Accessory Potential Values (Legendary)
                aPossiblePotential.add(nValue);
            }
        }
        /*} else if (pTier == ItemPotentialTierType.Unique) {
            for (int nValue : aCustomUniqueBonusPotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Epic) {
            for (int nValue : aCustomEpicBonusPotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Epic)
            }
        } else if (pTier == ItemPotentialTierType.Rare) {
            for (int nValue : aCustomRareBonusPotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Rare)
            }
        } else {
            for (int nValue : aCustomBonusPotential) { // Adds extra random stats here just incase.
                aPossiblePotential.add(nValue);
            }
        }*/

        Random pRandomPotential = new Random();
        int nIndex = pRandomPotential.nextInt(aPossiblePotential.size());
        return aPossiblePotential.get(nIndex);
    }

    /**
     * Cube Potential Line Generator
     * @author Mazen Massoud
     * @return 
     *
     * @purpose Generate a Potential Line based on fully customizable Potential tables based upon the Item Potential Tier.
     * @param pEquip
     * @param pTier
     */
    public static int generatePotential(Equip pEquip, ItemPotentialTierType pTier) {
        ArrayList<Integer> aPossiblePotential = new ArrayList<Integer>();
        final EquipSlotType pSlot = MapleItemInformationProvider.getInstance().getSlotType(pEquip.getItemId());
        final int nReqLevel = MapleItemInformationProvider.getInstance().getReqLevel(pEquip.getItemId());

        PotentialLine aMainLegendary[] = {
            Plus_12_Percent_LUK,
            Plus_12_Percent_INT,
            Plus_12_Percent_STR,
            Plus_12_Percent_DEX,
            Plus_9_Percent_LUK,
            Plus_9_Percent_INT,
            Plus_9_Percent_STR,
            Plus_9_Percent_DEX,
            Plus_9_Percent_ALL_STAT,
            Plus_6_Percent_ALL_STAT,
        };

        PotentialLine aWeaponEmblemLegendary[] = {
            Ignore_Defence_40_Percent,
            Ignore_Defence_35_Percent,
            Ignore_Defence_30_Percent,
            ATT_12_Percent,
            ATT_9_Percent,
            MAG_ATT_12_Percent,
            MAG_ATT_9_Percent,
            DMG_12_Percent,
            DMG_9_Percent,
            Critical_Chance_12_Percent,
            Critical_Chance_9_Percent,
            ATT_Per_Level_1,
            MAG_ATT_Per_Level_1,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        PotentialLine aWeaponLegendary[] = {
            Boss_Damage_40_Percent,
            Boss_Damage_35_Percent,
            Boss_Damage_30_Percent,
        };

        PotentialLine aNonWeaponLegendary[] = {
            HP_12_Percent,
            MP_12_Percent,
            DEF_12_Percent,
            HP_9_Percent,
            MP_9_Percent,
            DEF_9_Percent,
            Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        PotentialLine aAccessoryLegendary[] = {
            Plus_2_Percent_Critical_Rate,
            Plus_2_Percent_ATT,
            Plus_2_Percent_DMG,
            Plus_20_Percent_Drop_Rate
        };

        PotentialLine aHatLegendary[] = {
            Minus_1_Second_Cooldown_Reduction,
            Minus_2_Second_Cooldown_Reduction,
            Decent_Mystic_Door,
            Decent_Advanced_Blessing
        };

        PotentialLine aGloveLegendary[] = {
            Plus_8_Percent_Critical_Rate,
            Decent_Sharp_Eyes,
            Decent_Speed_Infusion
        };

        PotentialLine aShoeLegendary[] = {
            Decent_Haste,
            Decent_Combat_Orders
        };

        PotentialLine aPantsLegendary[] = {
            Decent_Hyper_Body
        };

        PotentialLine aCustomUnique[] = {
            Plus_6_Percent_ALL_STAT,
            Plus_4_Percent_ALL_STAT,
            Plus_9_Percent_LUK,
            Plus_9_Percent_INT,
            Plus_9_Percent_STR,
            Plus_9_Percent_DEX,
            HP_9_Percent,
            MP_9_Percent,
            DEF_9_Percent,
            Plus_6_Percent_LUK,
            Plus_6_Percent_INT,
            Plus_6_Percent_STR,
            Plus_6_Percent_DEX,
            Plus_6_Percent_HP,
            Plus_6_Percent_MP,
            Plus_6_Percent_DEF,
            Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        PotentialLine aCustomEpicPotential[] = {
            Plus_4_Percent_ALL_STAT,
            DEF_9_Percent,
            Plus_9_Percent_LUK,
            Plus_9_Percent_INT,
            Plus_9_Percent_STR,
            Plus_9_Percent_DEX,
            HP_9_Percent,
            MP_9_Percent,
            Plus_6_Percent_HP,
            Plus_6_Percent_MP,
            Plus_6_Percent_DEF,
            Plus_3_Percent_LUK,
            Plus_3_Percent_INT,
            Plus_3_Percent_STR,
            Plus_3_Percent_DEX,
            Plus_3_Percent_HP,
            Plus_3_Percent_MP,
            Plus_3_Percent_DEF,
            Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        PotentialLine aCustomRarePotential[] = {
            Plus_3_Percent_LUK,
            Plus_3_Percent_INT,
            Plus_3_Percent_DEX,
            Plus_3_Percent_STR,
            Plus_3_Percent_HP,
            Plus_3_Percent_MP,
            Plus_3_Percent_DEF,
            Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage,
            Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        if (pTier == ItemPotentialTierType.Legendary && nReqLevel >= 70) { // Only defined Legendary Lv. 100+ Potential Lines
            for (PotentialLine pLine : aMainLegendary) {
                aPossiblePotential.add(pLine.getValue()); // Adds General Potential Values (Legendary)
            }
            if (ItemPotentialProvider.isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || pSlot == EquipSlotType.Si_Emblem
                    || InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
                for (PotentialLine pLine : aWeaponEmblemLegendary) { // Adds Emblem Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || InventoryConstants.isSecondaryWeapon(pEquip.getItemId())
                    || InventoryConstants.isSpecialShield(pEquip.getItemId())) {
                for (PotentialLine pLine : aWeaponLegendary) { // Adds Weapon Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (!isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) && !InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
                for (PotentialLine pLine : aNonWeaponLegendary) { // Adds Non-Weapon Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (pSlot == EquipSlotType.Accessary_Eye || pSlot == EquipSlotType.Accessary_Face || pSlot == EquipSlotType.Accessary_Pocket) {
                for (PotentialLine pLine : aAccessoryLegendary) { // Adds Accessory Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (pSlot == EquipSlotType.Cap) {
                for (PotentialLine pLine : aHatLegendary) { // Adds Hat Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (pSlot == EquipSlotType.Glove) {
                for (PotentialLine pLine : aGloveLegendary) { // Adds Glove Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (pSlot == EquipSlotType.Shoes) {
                for (PotentialLine pLine : aShoeLegendary) { // Adds Shoe Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
            if (pSlot == EquipSlotType.Pants) {
                for (PotentialLine pLine : aPantsLegendary) { // Adds Pants Potential Values (Legendary)
                    aPossiblePotential.add(pLine.getValue());
                }
            }
        } else if (pTier == ItemPotentialTierType.Legendary) { // Use Unique Table for Legendary under level 70.
            for (PotentialLine pLine : aCustomUnique) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Unique) {
            for (PotentialLine pLine : aCustomUnique) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Epic) {
            for (PotentialLine pLine : aCustomEpicPotential) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Epic)
            }
        } else if (pTier == ItemPotentialTierType.Rare) {
            for (PotentialLine pLine : aCustomRarePotential) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Rare)
            }
        } else {
            for (PotentialLine pLine : aCustomRarePotential) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Rare)
            }
            //return decideStats(pEquip, pTier, nReqLevel).getOptionId(); // Very Random Potential
        }

        Random pRandomPotential = new Random();
        int nIndex = pRandomPotential.nextInt(aPossiblePotential.size());
        return aPossiblePotential.get(nIndex);
    }
}
