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
                        //if (bDisplayUI) pPlayer.SendPacket(CubePacket.OnPlatinumCubeResult(pPlayer.getId(), pPreviousTier != pEquip.getPotentialTier(), pEquip.getPosition(), nCubeID, pEquip));
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
     * @return 
     *
     * @purpose Generate a bonus Potential Line based on fully customizable Potential tables based upon the Item Potential Tier and
     * @param pEquip
     */
    public static int generateBonusPotential(Equip pEquip/*, ItemPotentialTierType pTier*/) {
        
        ArrayList<Integer> aPossiblePotential = new ArrayList<Integer>();
        final EquipSlotType pSlot = MapleItemInformationProvider.getInstance().getSlotType(pEquip.getItemId());
        final int nReqLevel = MapleItemInformationProvider.getInstance().getReqLevel(pEquip.getItemId());

        PotentialLine aEmblemLegendaryBonus[] = {
            Plus_40_Percent_Ignore_Defence,
            Plus_35_Percent_Ignore_Defence,
            Plus_30_Percent_Ignore_Defence,
            Plus_13_Percent_ATT,
            Plus_10_Percent_ATT,
            Plus_13_Percent_MAG_ATT,
            Plus_10_Percent_MAG_ATT,
            Plus_13_Percent_DMG,
            Plus_10_Percent_DMG,
            Plus_12_Percent_Critical_Chance,
            Plus_9_Percent_Critical_Chance,
            Plus_12_Percent_HP,
            Plus_12_Percent_MP,
            Plus_12_Percent_LUK,
            Plus_12_Percent_INT,
            Plus_12_Percent_STR,
            Plus_12_Percent_DEX,
            Plus_9_Percent_HP,
            Plus_9_Percent_MP,
            Plus_9_Percent_LUK,
            Plus_9_Percent_INT,
            Plus_9_Percent_STR,
            Plus_9_Percent_DEX,
            Plus_9_Percent_ALL_STAT,
            Plus_6_Percent_ALL_STAT,
            LUK_PER_10_Levels_Plus_1,
            INT_PER_10_Levels_Plus_1,
            STR_PER_10_Levels_Plus_1,
            DEX_PER_10_Levels_Plus_1,
            LUK_PER_10_Levels_Plus_2,
            INT_PER_10_Levels_Plus_2,
            STR_PER_10_Levels_Plus_2,
            DEX_PER_10_Levels_Plus_2,
            ATT_PER_10_Levels_Plus_1,
            MAG_ATT_PER_10_Levels_Plus_1,
            ATT_Plus_19,
            ATT_Plus_15,
            MAG_ATT_Plus_19,
            MAG_ATT_Plus_15
        };

        PotentialLine aWeaponLegendaryBonus[] = {
            Plus_18_Percent_Boss_Damage,
            Plus_12_Percent_Boss_Damage,
            Plus_13_Percent_ATT,
            Plus_10_Percent_ATT,
            Plus_13_Percent_MAG_ATT,
            Plus_10_Percent_MAG_ATT,
            Plus_13_Percent_DMG,
            Plus_10_Percent_DMG,
            Plus_12_Percent_Critical_Chance,
            Plus_9_Percent_Critical_Chance,
            MAX_HP_Plus_10_Percent,
            MAX_HP_Plus_7_Percent,
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
            Plus_10_Percent_MAX_MP,
            Plus_7_Percent_MAX_MP,
            Plus_15_Percent_TO_REGEN_MANA_ON_ATT,
            Plus_15_Percent_TO_REGEN_HP_ON_ATT,
            LUK_PER_10_Levels_Plus_1,
            INT_PER_10_Levels_Plus_1,
            STR_PER_10_Levels_Plus_1,
            DEX_PER_10_Levels_Plus_1,
            LUK_PER_10_Levels_Plus_2,
            INT_PER_10_Levels_Plus_2,
            STR_PER_10_Levels_Plus_2,
            DEX_PER_10_Levels_Plus_2,
            ATT_PER_10_Levels_Plus_1,
            MAG_ATT_PER_10_Levels_Plus_1,
            Plus_4_Percent_Ignore_DEF,
            ATT_Plus_19,
            ATT_Plus_15,
            MAG_ATT_Plus_19,
            MAG_ATT_Plus_15
        };

        PotentialLine aNonWeaponLegendaryBonus[] = {
            Plus_7_Percent_LUK,
            Plus_7_Percent_INT,
            Plus_7_Percent_STR,
            Plus_7_Percent_DEX,
            Plus_10_Percent_MaxHP,
            Plus_10_Percent_MaxMP,
            Plus_10_Percent_DEF,
            Plus_5_Percent_LUK,
            Plus_5_Percent_INT,
            Plus_5_Percent_STR,
            Plus_5_Percent_DEX,
            Plus_7_Percent_MaxHP,
            Plus_7_Percent_MaxMP,
            Plus_7_Percent_DEF,
            Plus_250_HP,
            Plus_250_MP,
            LUK_PER_10_Levels_Plus_1,
            INT_PER_10_Levels_Plus_1,
            STR_PER_10_Levels_Plus_1,
            DEX_PER_10_Levels_Plus_1,
            LUK_PER_10_Levels_Plus_2,
            INT_PER_10_Levels_Plus_2,
            STR_PER_10_Levels_Plus_2,
            DEX_PER_10_Levels_Plus_2,
            HP_Recovery_Items_and_Skills_Plus_20_Percent,
            STR_Plus_18,
            INT_Plus_18,
            DEX_Plus_18,
            LUK_Plus_18,
            ALL_STAT_Plus_12,
            ATT_Plus_15,
            All_Elemental_Res_Plus_4_Percent,
            Critical_Damage_Plus_1_Percent,
            Plus_2_Percent_MAG_ATT,
            Plus_2_Percent_ATT,
            Crit_Chance_Plus_2_Percent
        };

        PotentialLine aAccessoryLegendaryBonus[] = {
            Plus_5_Percent_Meso_Drop_Rate,
            Plus_5_Percent_Item_Drop_Rate,
        };

        PotentialLine aCustomUniqueBonus[] = {
            Plus_5_Percent_LUK,
            Plus_5_Percent_INT,
            Plus_5_Percent_STR,
            Plus_5_Percent_DEX,
            Plus_7_Percent_MaxHP,
            Plus_7_Percent_MaxMP,
            Plus_7_Percent_DEF,
            Plus_3_Percent_LUK,
            Plus_3_Percent_INT,
            Plus_3_Percent_STR,
            Plus_3_Percent_DEX,
            Plus_3_Percent_HP,
            Plus_3_Percent_MP,
            Plus_3_Percent_DEF,
            Plus_3_Percent_ALL_STAT,
            Plus_200_HP,
            Plus_200_MP,
            STR_Plus_14,
            INT_Plus_14,
            DEX_Plus_14,
            LUK_Plus_14,
            ALL_STAT_Plus_8,
            ATT_Plus_13,
            LUK_PER_10_Levels_Plus_1,
            INT_PER_10_Levels_Plus_1,
            STR_PER_10_Levels_Plus_1,
            DEX_PER_10_Levels_Plus_1
        };

        PotentialLine aCustomEpicBonus[] = {
            Plus_4_Percent_LUK,
            Plus_4_Percent_INT,
            Plus_4_Percent_STR,
            Plus_4_Percent_DEX,
            Plus_6_Percent_HP,
            Plus_6_Percent_MP,
            Plus_6_Percent_DEF,
            Plus_2_Percent_LUK,
            Plus_2_Percent_INT,
            Plus_2_Percent_STR,
            Plus_2_Percent_DEX,
            Plus_1_Percent_ALL_STAT,
            Plus_3_Percent_HP,
            Plus_3_Percent_MP,
            Plus_3_Percent_DEF,
            Plus_150_HP,
            Plus_150_MP,
            STR_Plus_10,
            INT_Plus_10,
            DEX_Plus_10,
            LUK_Plus_10,
            ALL_STAT_Plus_8,
            ATT_Plus_11
        };

        PotentialLine aCustomRareBonusPotential[] = {
            Plus_2_Percent_LUK,
            Plus_2_Percent_INT,
            Plus_2_Percent_STR,
            Plus_2_Percent_DEX,
            Plus_3_Percent_HP,
            Plus_3_Percent_MP,
            Plus_3_Percent_DEF,
            Plus_100_HP,
            Plus_100_MP,
            LUK_Plus_6,
            STR_Plus_6,
            INT_Plus_6,
            DEX_Plus_6,
            ALL_STAT_Plus_4,
            ATT_Plus_10
        };

        PotentialLine aCustomBonusPotential[] = {
            Plus_5_Percent_LUK,
            Plus_5_Percent_INT,
            Plus_5_Percent_STR,
            Plus_5_Percent_DEX,
            Plus_5_Percent_ALL_STAT,
            Plus_7_Percent_MaxHP,
            Plus_7_Percent_MaxMP,
            Plus_7_Percent_DEF
        };
        
        //if (pTier == ItemPotentialTierType.Legendary && nReqLevel >= 70) { // Only defined Legendary Lv. 70+ Potential Lines
        if (pSlot == EquipSlotType.Si_Emblem) {
            for (PotentialLine pLine : aEmblemLegendaryBonus) { // Adds Emblem Potential Values (Legendary)
                aPossiblePotential.add(pLine.getValue());
            }
        }
        if (isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
            for (PotentialLine pLine : aWeaponLegendaryBonus) { // Adds Weapon Potential Values (Legendary)
                aPossiblePotential.add(pLine.getValue());
            }
        }
        if (!isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) && !InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
            for (PotentialLine pLine : aNonWeaponLegendaryBonus) { // Adds Non-Weapon Potential Values (Legendary)
                aPossiblePotential.add(pLine.getValue());
            }
        }
        if (pSlot == EquipSlotType.Accessary_Eye || pSlot == EquipSlotType.Accessary_Face || pSlot == EquipSlotType.Accessary_Pocket) {
            for (PotentialLine pLine : aAccessoryLegendaryBonus) { // Adds Accessory Potential Values (Legendary)
                aPossiblePotential.add(pLine.getValue());
            }
        }
        /*} else if (pTier == ItemPotentialTierType.Unique) {
            for (PotentialLine pLine : aCustomUniqueBonus) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Epic) {
            for (PotentialLine pLine : aCustomEpicBonus) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Epic)
            }
        } else if (pTier == ItemPotentialTierType.Rare) {
            for (PotentialLine pLine : aCustomRareBonus) {
                aPossiblePotential.add(pLine.getValue()); // Adds Custom Potential Values (Rare)
            }
        } else {
            for (PotentialLine pLine : aCustomBonus) { // Adds extra random stats here just incase.
                aPossiblePotential.add(pLine.getValue());
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
            Plus_40_Percent_Ignore_Defence,
            Plus_35_Percent_Ignore_Defence,
            Plus_30_Percent_Ignore_Defence,
            Plus_13_Percent_ATT,
            Plus_10_Percent_ATT,
            Plus_13_Percent_MAG_ATT,
            Plus_10_Percent_MAG_ATT,
            Plus_13_Percent_DMG,
            Plus_10_Percent_DMG,
            Plus_12_Percent_Critical_Chance,
            Plus_9_Percent_Critical_Chance,
            ATT_PER_10_Levels_Plus_1,
            MAG_ATT_PER_10_Levels_Plus_1,
            Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage
        };

        PotentialLine aWeaponLegendary[] = {
            Plus_40_Percent_Boss_Damage,
            Plus_35_Percent_Boss_Damage,
            Plus_30_Percent_Boss_Damage,
        };

        PotentialLine aNonWeaponLegendary[] = {
            Plus_12_Percent_HP,
            Plus_12_Percent_MP,
            Plus_12_Percent_DEF,
            Plus_9_Percent_HP,
            Plus_9_Percent_MP,
            Plus_9_Percent_DEF,
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
            Plus_9_Percent_HP,
            Plus_9_Percent_MP,
            Plus_9_Percent_DEF,
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
            Plus_9_Percent_DEF,
            Plus_9_Percent_LUK,
            Plus_9_Percent_INT,
            Plus_9_Percent_STR,
            Plus_9_Percent_DEX,
            Plus_9_Percent_HP,
            Plus_9_Percent_MP,
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
