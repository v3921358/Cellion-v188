package server.potentials;

import client.inventory.Equip;
import client.inventory.EquipSlotType;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import provider.wz.cache.WzDataStorage;
import provider.wz.nox.NoxBinaryReader;
import server.MapleItemInformationProvider;
import server.NebuliteGrade;
import server.Randomizer;
import static server.Randomizer.rand;
import tools.Pair;

/**
 * Deals with everything in the game related to equipment potentials
 *
 * @author Lloyd Korn
 */
public class ItemPotentialProvider {

    // Cubes
    private static final List<ItemPotentialOption> potentialStats = new LinkedList();
    private static final Map<Integer, ItemPotentialOption> potentialStatsMap = new HashMap<>();

    private static final Map<Integer, ItemPotentialOption> map_RareItem = new HashMap<>(); // For quick searching
    private static final Map<Integer, ItemPotentialOption> map_AnyItem = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> map_EpicItem = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> map_UniqueItem = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> map_LegendaryItem = new HashMap<>();

    // Constants
    public static final int RATE_REDCUBE_TIERUP = 10,
            RATE_BLACK_CUBE_TIERUP = 20,
            RATE_MEMORIAL_CUBE_TIERUP = 20,
            RATE_SUPER_MIRACLECUBE_TIERUP = 15,
            RATE_GAMECUBE_TIERUP = 5;
    private static final int RATE_MONSTERDROP_HAVEPOTENTIAL = 50; // rate for equipment dropped from boss to have at least a rare.
    private static final int RATE_MONSTERDROP_EPIC = 30;
    private static final int RATE_MONSTERDROP_BOSS_EPIC = 50;
    private static final int RATE_MONSTERDROP_ELITEBOSS_UNIQUE = 30;
    private static final int RATE_3LINE_POTENTIAL = 60; // Rate to achieve 3 line potential

    // Nebulite
    private static final Map<Integer, ItemPotentialOption> socketGradeSCache = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> socketGradeACache = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> socketGradeBCache = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> socketGradeCCache = new HashMap<>();
    private static final Map<Integer, ItemPotentialOption> socketGradeDCache = new HashMap<>();

    public static void initialize() {
        loadPotentials();
    }

    private static void loadPotentials() {
        if (!map_RareItem.isEmpty() || !map_AnyItem.isEmpty() || !map_EpicItem.isEmpty() || !map_UniqueItem.isEmpty() || !map_LegendaryItem.isEmpty()) {
            throw new RuntimeException("MapleItemPotentialProvider have already been loaded!");
        }
        try {
            final NoxBinaryReader data = WzDataStorage.getBinaryItemData();

            int numPotentials = data.readInt();
            for (int i = 0; i < numPotentials; i++) {
                ItemPotentialOption option = new ItemPotentialOption();

                int potentialId = data.readInt();
                option.setOptionId(potentialId);

                // Info
                option.setRequiredLevel((short) data.readShort());
                option.setOptionType((short) data.readShort());
                option.setWeight((byte) data.readByte());

                Map<Integer, List<Pair<ItemPotentialType, ItemPotentialStats>>> stats_add = new HashMap();
                option.setPotentialStats(stats_add);

                int numLevels = data.readByte();
                for (int z = 0; z < numLevels; z++) {
                    int level = data.readByte();

                    boolean isBossStat = data.readBoolean();
                    boolean isFacePotential = data.readBoolean();
                    int statusPotential_AttackType = data.readInt();
                    byte statusPotential_level = (byte) data.readByte();
                    int statusPotential_prop = data.readInt();

                    stats_add.put(level, new ArrayList()); // set default array

                    int numPropCount = data.readByte();
                    for (int a = 0; a < numPropCount; a++) {
                        String propName = data.readAsciiString();

                        if (data.readBoolean()) {
                            stats_add.get(level).add(
                                    new Pair<>(
                                            ItemPotentialType.fromString(propName),
                                            new ItemPotentialStats(isBossStat, data.readInt(), statusPotential_AttackType, statusPotential_level, statusPotential_prop)));

                        }
                    }
                    option.setPotentialStats(stats_add);

                    // Gonna hard code the potential skills :( 
                    switch (potentialId) {
                        case 31001:
                            option.setSkill(ItemPotentialSkill.Decent_Haste);
                            break;
                        case 31002:
                            option.setSkill(ItemPotentialSkill.Decent_MysticDoor);
                            break;
                        case 31003:
                            option.setSkill(ItemPotentialSkill.Decent_SharpEye);
                            break;
                        case 31004:
                            option.setSkill(ItemPotentialSkill.Decent_HyperBody);
                            break;
                        case 41005:
                            option.setSkill(ItemPotentialSkill.Decent_CombatOrders);
                            break;
                        case 41006:
                            option.setSkill(ItemPotentialSkill.Decent_AdvancedBlessing);
                            break;
                        case 41007:
                            option.setSkill(ItemPotentialSkill.Decent_SpeedInfusion);
                            break;
                    }
                }
                switch (potentialId / 10000) {
                    case 0: // Any
                        map_AnyItem.put(potentialId, option);
                        break;
                    case 1: // Rare
                        map_RareItem.put(potentialId, option);
                        break;
                    case 2: // Epic
                        map_EpicItem.put(potentialId, option);
                        break;
                    case 3: // Unique
                        map_UniqueItem.put(potentialId, option);
                        break;
                    case 4: // Legendary
                        map_LegendaryItem.put(potentialId, option);
                        break;
                }
                potentialStatsMap.put(potentialId, option);
                potentialStats.add(option);
            }

            System.out.println(String.format("[Info] Loaded %d Potential Stats.", potentialStats.size()));

            data.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        /*     final MapleData nebuliteData = itemData.getData("Install/0306.img");
        for (MapleData dat : nebuliteData) {
            ItemPotentialOption item = new ItemPotentialOption();
            item.optionId = Integer.parseInt(dat.getName()); // Item Id
            item.optionType = MapleDataTool.getInt("optionType", dat.getChildByPath("socket"), 0);
            for (MapleData info : dat.getChildByPath("socket/option")) {
                final String optionString = MapleDataTool.getString("optionString", info, "");
                final int level = MapleDataTool.getInt("level", info, 0);
                if (level > 0) { // Save memory
                    item.data.put(optionString, level);
                }
            }
            switch (item.optionId) {
                case 3063370: // Haste
                    item.data.put("skillID", 8000);
                    break;
                case 3063380: // Mystic Door
                    item.data.put("skillID", 8001);
                    break;
                case 3063390: // Sharp Eyes
                    item.data.put("skillID", 8002);
                    break;
                case 3063400: // Hyper Body
                    item.data.put("skillID", 8003);
                    break;
                case 3064470: // Combat Orders
                    item.data.put("skillID", 8004);
                    break;
                case 3064480: // Advanced Blessing
                    item.data.put("skillID", 8005);
                    break;
                case 3064490: // Speed Infusion
                    item.data.put("skillID", 8006);
                    break;
            }
            switch (GameConstants.getNebuliteGrade(item.optionId)) {
                case GradeS: //S
                    socketGradeSCache.put(Integer.parseInt(dat.getName()), item);
                    break;
                case GradeA: //A
                    socketGradeACache.put(Integer.parseInt(dat.getName()), item);
                    break;
                case GradeB: //B
                    socketGradeBCache.put(Integer.parseInt(dat.getName()), item);
                    break;
                case GradeC: //C
                    socketGradeCCache.put(Integer.parseInt(dat.getName()), item);
                    break;
                case GradeD: //D
                    socketGradeDCache.put(Integer.parseInt(dat.getName()), item);
                    break; // impossible to be -1 since we're looping in 306.img.xml					
            }
        }*/
 /*  socketCache.put(4, gradeS);
        socketCache.put(3, gradeA);
        socketCache.put(2, gradeB);
        socketCache.put(1, gradeC);
        socketCache.put(0, gradeD);*/
    }

    /**
     * Adds potential to an item dropped by a monster. The stats will be decided when the item is used with magnifying.
     *
     * @param equip
     * @param isBossMonster
     * @param isEliteBoss
     */
    public static void addPotentialtoMonsterItemDrop(Equip equip, boolean isBossMonster, boolean isEliteBoss) {
        addPotentialtoMonsterItemDrop(equip, isBossMonster, isEliteBoss, ItemPotentialTierType.None);
    }

    /**
     * Adds potential to an item dropped by a monster. The stats will be decided when the item is used with magnifying.
     *
     * @param equip
     * @param isBossMonster
     * @param isEliteBoss
     * @param forceOverrideTier The tier to override regardless of isBossMonster or isEliteBoss property
     */
    public static void addPotentialtoMonsterItemDrop(Equip equip, boolean isBossMonster, boolean isEliteBoss, ItemPotentialTierType forceOverrideTier) {
        if (equip.getUpgradeSlots() == 0 || !GameConstants.canScroll(equip.getItemId()) || equip.getPotentialTier() != ItemPotentialTierType.None) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId());
        if (reqLevel < 10) {
            return;
        }

        if (forceOverrideTier != ItemPotentialTierType.None) {
            equip.setPotentialTier(forceOverrideTier);
        } else // 30% rate to be epic
        if (Randomizer.nextInt(100) < RATE_MONSTERDROP_HAVEPOTENTIAL) {
            ItemPotentialTierType tier = ItemPotentialTierType.Rare_Hidden; // at least rare at this point in time

            if (isEliteBoss) {
                if (Randomizer.nextInt(100) < RATE_MONSTERDROP_ELITEBOSS_UNIQUE) {
                    tier = ItemPotentialTierType.Unique_Hidden;
                } else if (Randomizer.nextInt(100) < RATE_MONSTERDROP_BOSS_EPIC) {
                    tier = ItemPotentialTierType.Epic_Hidden;
                }// else rare
            } else if (isBossMonster) {
                if (Randomizer.nextInt(100) < RATE_MONSTERDROP_BOSS_EPIC) {
                    tier = ItemPotentialTierType.Epic_Hidden;
                }// else rare
            } else if (Randomizer.nextInt(100) < RATE_MONSTERDROP_EPIC) {
                tier = ItemPotentialTierType.Epic_Hidden;
            }// else rare

            equip.setPotentialTier(tier);
        }
    }

    // Dan's Potential Method
    /*public void SetRandomPotential(int nCubeItemID, int nOptionCount, int nGrade) {
        pItemOption.nGrade = (byte) nGrade;
        // TODO: Figure out about options 60001 -> 70119 (60001 -> 60073) (70000 -> 70119)
        List<Integer> aOptions = new ArrayList<>(); // There has to be a better way to do this...
        for (Map.Entry<Integer, ItemOptionInfo> pOptionInfo : ItemOptionInfoBase.mItemOption.entrySet()) {
            if (pOptionInfo.getKey() > 10000 && pOptionInfo.getKey() < 60000) { // I dont think items can get anything besides these that arent bFixedPotential
                aOptions.add(pOptionInfo.getKey());
            }
        }
        int nRate = 10;
        switch (nCubeItemID) { // TODO: Better rate for best potential line based on cube
            case 5062009: // Red Cube
                nRate = 10;
                break;
        }
        for (int i = 0; i < nOptionCount; i++) {
            boolean bValid = false;
            while (!bValid) {
                int nOption = aOptions.get((int) (Rand32.GetRandom() % aOptions.size()));
                ItemOptionInfo pOption = ItemOptionInfoBase.GetItemOption(nOption);
                if (pOption != null && pOption.nReqLevel <= ItemInfo.GetEquipItem(nItemID).nrLevel && ItemOptionInfoBase.IsValidOptionType(pOption.nItemOptionID, nItemID)) {
                    if (nGrade == ItemGrade.Legendary) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 40000 && pOption.nItemOptionID <= 41007) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 40000 && pOption.nItemOptionID <= 41007 : pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Unique) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004 : pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Epic) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406 : pOption.nItemOptionID >= 10000 && pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Rare) {
                        if (i == 0) {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 10000 && pOption.nItemOptionID <= 10291 : pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        } else {
                            if (pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        }
                    }
                    if (bValid) {
                        SetPotentialOption(i, pOption.nItemOptionID);
                        bValid = true;
                    }
                }
            }
        }
        aOptions.clear();
    }*/
    public static boolean IsValidOptionType(int nItemOptionID, int nItemID) {
        final EquipSlotType pBodyPart = MapleItemInformationProvider.getInstance().getSlotType(nItemID);

        if (MapleItemInformationProvider.getInstance().getEquipById(nItemID) == null || pBodyPart == null) {
            return false;
        }
        switch (nItemOptionID) {
            case 10:
                if (pBodyPart == EquipSlotType.Weapon) {
                    return true;
                }
                if (pBodyPart == EquipSlotType.Weapon_TakingBothSlot_Shield) {
                    return true;
                }
                if (pBodyPart == EquipSlotType.Shield_OrDualBlade) {
                    return true;
                }
                if (pBodyPart == EquipSlotType.Si_Emblem) {
                    return true;
                }
                return true; //TODO: CHECK IF NOT ZERO WEAPON (For all cases)
            case 11:
                if (pBodyPart == EquipSlotType.Weapon) {
                    return false;
                }
                if (pBodyPart == EquipSlotType.Weapon_TakingBothSlot_Shield) {
                    return false;
                }
                if (pBodyPart == EquipSlotType.Shield_OrDualBlade) {
                    return false;
                }
                if (pBodyPart == EquipSlotType.Si_Emblem) {
                    return false;
                }
                return true;
            case 20:
                if (pBodyPart != EquipSlotType.Cap && pBodyPart != EquipSlotType.Glove && pBodyPart != EquipSlotType.Shoes && pBodyPart != EquipSlotType.Shield_OrDualBlade
                        && pBodyPart != EquipSlotType.Coat && pBodyPart != EquipSlotType.Pants && pBodyPart != EquipSlotType.Cape && pBodyPart.getSlot() != 22) {
                    if (pBodyPart.getSlot() != 23) {
                        return false;
                    }
                }
                return true;
            case 21:
                if (pBodyPart != EquipSlotType.Cap && pBodyPart != EquipSlotType.Glove && pBodyPart != EquipSlotType.Shoes && pBodyPart != EquipSlotType.Shield_OrDualBlade && pBodyPart != EquipSlotType.Coat
                        && pBodyPart != EquipSlotType.Pants && pBodyPart != EquipSlotType.Cape && pBodyPart.getSlot() != 22) {
                    if (pBodyPart.getSlot() != 23) {
                        return true;
                    }
                }
                return true;
            case 40:
                if (pBodyPart.getSlot() != 17 && pBodyPart.getSlot() != 12 && pBodyPart.getSlot() != 13 && pBodyPart.getSlot() != 15 && pBodyPart.getSlot() != 16
                        && pBodyPart != EquipSlotType.Accessary_Face && pBodyPart != EquipSlotType.Accessary_Eye && pBodyPart != EquipSlotType.Earring) {
                    return false;
                }
                return true;
            case 41:
                if (pBodyPart.getSlot() == 17 || pBodyPart.getSlot() == 12 || pBodyPart.getSlot() == 13 || pBodyPart.getSlot() == 15 || pBodyPart.getSlot() == 16 || pBodyPart == EquipSlotType.Accessary_Face || pBodyPart == EquipSlotType.Accessary_Eye) {
                    return false;
                }
                if (pBodyPart == EquipSlotType.Earring) {
                    return false;
                }
                return true;
            case 51:
                if (pBodyPart != EquipSlotType.Cap) {
                    return false;
                }
                return true;
            case 52:
                if (pBodyPart != EquipSlotType.Coat) {
                    return false;
                }
                return true;
            case 53:
                if (pBodyPart == EquipSlotType.Coat) {
                    if (pBodyPart == EquipSlotType.Longcoat) {
                        return false;
                    }
                } else if (pBodyPart != EquipSlotType.Pants) {
                    return false;
                }
                return true;
            case 54:
                if (pBodyPart != EquipSlotType.Glove) {
                    return false;
                }
                return true;
            case 55:
                if (pBodyPart != EquipSlotType.Shoes) {
                    return false;
                }
                return true;
            case 56:
                if (pBodyPart.getSlot() != 30) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    /**
     * Cube Bonus Potential Line Generator
     *
     * @author Mazen
     *
     * @purpose Generate a bonus Potential Line based on fully customizable Potential tables based upon the Item Potential Tier and
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
     *
     * @author Mazen
     *
     * @purpose Generate a Potential Line based on fully customizable Potential tables based upon the Item Potential Tier and
     */
    public static int generatePotential(Equip pEquip, ItemPotentialTierType pTier) {
        ArrayList<Integer> aPossiblePotential = new ArrayList<Integer>();
        final EquipSlotType pSlot = MapleItemInformationProvider.getInstance().getSlotType(pEquip.getItemId());
        final int nReqLevel = MapleItemInformationProvider.getInstance().getReqLevel(pEquip.getItemId());

        int aMainLegendaryPotential[] = {
            42066, // 12% Luck
            42065, // 12% Intelligence
            40041, // 12% Strength
            42064, // 12% Dexterity
            30044, // 9% Luck
            30043, // 9% Intelligence
            30041, // 9% Strength
            30042, // 9% Dexterity
            40086, // 9% All Stat
            30086, // 6% All Stat
        };

        int aWeaponEmblemLegendaryPotential[] = {
            40292, // 40% Ignore Defense
            40291, // 35% Ignore Defense
            30291, // 30% Ignore Defense
            42051, // 12% Attack
            32051, // 9% Atacck
            40052, // 12% Magic Attack
            30052, // 9% Magic Attack
            42070, // 12% Damage
            30070, // 9% Damage
            40055, // 12% Crit Chance
            32057, // 9% Crit Chance
            42095, // +1 Attack Per 10 Levels
            42096, // +1 Magic Attack Per 10 Levels
            40357, // 10% Chance to Ignore 40% Damage
        };

        int aWeaponLegendaryPotential[] = {
            40603, // 40% Boss Damage
            40602, // 35% Boss Damage
            40601, // 30% Boss Damage
        };

        int aNonWeaponLegendaryPotential[] = {
            40045, // 12% HP
            40046, // 12% MP
            40053, // 12% Defense                           
            30045, // 9% HP
            30046, // 9% MP
            30054, // 9% Defense 
            30356, // 5% Chance to Ignore 20% Damage
            40356, // 10% Chance to Ignore 20% Damage   
            30357, // 5% Chance to Ignore 40% Damage    
            40357, // 10% Chance to Ignore 40% Damage   
        };

        int aAccessoryLegendaryPotential[] = {
            22056, // +2% Critical Rate
            42052, // +2% Attack
            42071, // +2% Damage
            40656, // +20% Drop Rate
        };

        int aHatLegendaryPotential[] = {
            40556, // -1 Second Cooldown Reduction
            40557, // -2 Second Cooldown Reduction
            31002, // Decent Mystic Door
            41006, // Decent Advanced Blessing
        };

        int aGloveLegendaryPotential[] = {
            20055, // +8% Critical Rate
            31003, // Decent Sharp Eyes
            41007, // Decent Speed Infusion
        };

        int aShoeLegendaryPotential[] = {
            31001, // Decent Haste
            41005, // Decent Combat Orders
        };

        int aPantsLegendaryPotential[] = {
            31004, // Decent Hyper Body
        };

        int aCustomUniquePotential[] = {
            30086, // 6% ALL STAT
            20086, // 4% ALL STAT
            30044, // 9% LUK
            30043, // 9% INT
            30041, // 9% STR
            30042, // 9% DEX
            30045, // 9% HP
            30046, // 9% MP
            30054, // 9% DEF
            20044, // 6% LUK
            20043, // 6% INT
            20041, // 6% STR
            20042, // 6% DEX
            22045, // 6% HP
            20046, // 6% MP
            20054, // 6% DEF
            30356, // 5% Chance to Ignore 20% Damage
            40356, // 10% Chance to Ignore 20% Damage
            30357, // 5% Chance to Ignore 40% Damage
            40357, // 10% Chance to Ignore 40% Damage
        };

        int aCustomEpicPotential[] = {
            20086, // 4% ALL STAT
            30054, // 9% DEF
            20044, // 6% LUK
            20043, // 6% INT
            20041, // 6% STR
            20042, // 6% DEX
            22045, // 6% HP
            20046, // 6% MP
            20054, // 6% DEF
            10044, // 3% LUK
            10043, // 3% INT
            10041, // 3% STR
            10042, // 3% DEX
            12045, // 3% HP
            10046, // 3% MP
            10054, // 3% DEF
            30356, // 5% Chance to Ignore 20% Damage
            40356, // 10% Chance to Ignore 20% Damage
            30357, // 5% Chance to Ignore 40% Damage
            40357, // 10% Chance to Ignore 40% Damage
        };

        int aCustomRarePotential[] = {
            10044, // 3% LUK
            10043, // 3% INT
            10041, // 3% STR
            10042, // 3% DEX
            12045, // 3% HP
            10046, // 3% MP
            10054, // 3% DEF
            30356, // 5% Chance to Ignore 20% Damage
            40356, // 10% Chance to Ignore 20% Damage
            30357, // 5% Chance to Ignore 40% Damage
            40357, // 10% Chance to Ignore 40% Damage
        };

        if (pTier == ItemPotentialTierType.Legendary && nReqLevel >= 70) { // Only defined Legendary Lv. 100+ Potential Lines
            for (int nValue : aMainLegendaryPotential) {
                aPossiblePotential.add(nValue); // Adds General Potential Values (Legendary)
            }
            if (isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || pSlot == EquipSlotType.Si_Emblem 
                    || InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
                for (int nValue : aWeaponEmblemLegendaryPotential) { // Adds Emblem Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) || InventoryConstants.isSecondaryWeapon(pEquip.getItemId()) 
                    || InventoryConstants.isSpecialShield(pEquip.getItemId())) {
                for (int nValue : aWeaponLegendaryPotential) { // Adds Weapon Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (!isWeaponRelatedPotentialClass(pSlot, pEquip.getItemId()) && !InventoryConstants.isSecondaryWeapon(pEquip.getItemId())) {
                for (int nValue : aNonWeaponLegendaryPotential) { // Adds Non-Weapon Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (pSlot == EquipSlotType.Accessary_Eye || pSlot == EquipSlotType.Accessary_Face || pSlot == EquipSlotType.Accessary_Pocket) {
                for (int nValue : aAccessoryLegendaryPotential) { // Adds Accessory Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (pSlot == EquipSlotType.Cap) {
                for (int nValue : aHatLegendaryPotential) { // Adds Hat Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (pSlot == EquipSlotType.Glove) {
                for (int nValue : aGloveLegendaryPotential) { // Adds Glove Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (pSlot == EquipSlotType.Shoes) {
                for (int nValue : aShoeLegendaryPotential) { // Adds Shoe Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
            if (pSlot == EquipSlotType.Pants) {
                for (int nValue : aPantsLegendaryPotential) { // Adds Pants Potential Values (Legendary)
                    aPossiblePotential.add(nValue);
                }
            }
        } else if (pTier == ItemPotentialTierType.Legendary) { // Use Unique Table for Legendary under level 70.
            for (int nValue : aCustomUniquePotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Unique) {
            for (int nValue : aCustomUniquePotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Unique)
            }
        } else if (pTier == ItemPotentialTierType.Epic) {
            for (int nValue : aCustomEpicPotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Epic)
            }
        } else if (pTier == ItemPotentialTierType.Rare) {
            for (int nValue : aCustomRarePotential) {
                aPossiblePotential.add(nValue); // Adds Custom Potential Values (Rare)
            }
        } else {
            return decideStats(pEquip, pTier, nReqLevel).getOptionId(); // Very Random Potential
        }

        Random pRandomPotential = new Random();
        int nIndex = pRandomPotential.nextInt(aPossiblePotential.size());
        return aPossiblePotential.get(nIndex);
    }

    /**
     * Resets the potential of an equipment with miracle cube.
     *
     * @param equip
     * @param rateToRaiseTier The probability over 100%
     * @param rateToDecreaseTier The probability over 100% to decrease the potential tier
     * @param maxTier The max tier the item can be upgraded to.
     * @param hidePotential Hides the potential after resetting, requiring the user to magnify
     * @param miracleRate The miracle rate -- Double Miracle Time
     * @return
     */
    public static boolean resetPotential(Equip equip, int rateToRaiseTier, int rateToDecreaseTier, ItemPotentialTierType maxTier, boolean hidePotential, float miracleRate) {
        if (equip.getPotentialTier().isHiddenType()) { // needs to have something
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId());
        if (reqLevel < 10) {
            return false;
        }
        ItemPotentialTierType currenttierType = equip.getPotentialTier();

        if (Randomizer.nextInt(100) < rateToDecreaseTier) { // check if this item is able to tier down via probability
            ItemPotentialTierType newTier = ItemPotentialTierType.getItemPotentialTierTypeFromInt(currenttierType.getValue() - 1);
            if (newTier != ItemPotentialTierType.None && !newTier.isHiddenType()) {
                currenttierType = newTier;
            }
        } else if (Randomizer.nextInt(100) < (rateToRaiseTier * miracleRate)) { // check if this item is able to tier up via probability
            ItemPotentialTierType newTier = ItemPotentialTierType.getItemPotentialTierTypeFromInt(currenttierType.getValue() + 1);
            if (newTier != ItemPotentialTierType.None && !newTier.isHiddenType()) {
                if (newTier.getValue() <= maxTier.getValue()) {
                    currenttierType = newTier;
                }
            }
        }

        if (ServerConstants.CONTROLLED_POTENTIAL_RESULTS) {
            if (equip.getPotential1() != 0) {
                equip.setPotential1(generatePotential(equip, currenttierType));
            }
            if (equip.getPotential2() != 0) {
                equip.setPotential2(generatePotential(equip, currenttierType));
            }
            if (equip.getPotential3() != 0) {
                equip.setPotential3(generatePotential(equip, currenttierType));
            }
        } else {
            // Only update stats if the original potential lines already have one.
            // as 2 -> 3 lines potential may only be achieved with stamps.
            if (equip.getPotential1() != 0) {
                equip.setPotential1(decideStats(equip, currenttierType, reqLevel).getOptionId());
            }
            if (equip.getPotential2() != 0) {
                equip.setPotential2(decideStats(equip, currenttierType, reqLevel).getOptionId());
            }
            if (equip.getPotential3() != 0) {
                equip.setPotential3(decideStats(equip, currenttierType, reqLevel).getOptionId());
            }
        }

        //equip.setBonusPotential1(generatePotential(equip, currenttierType));
        //equip.setBonusPotential2(generatePotential(equip, currenttierType));
        //equip.setBonusPotential3(generatePotential(equip, currenttierType));

        /*System.out.println("Potential Tier: " + currenttierType);
        System.out.println("Potential 1: " + equip.getPotential1());
        System.out.println("Potential 2: " + equip.getPotential2());
        System.out.println("Potential 3: " + equip.getPotential3());*/
        if (hidePotential) {
            ItemPotentialTierType hiddenType = ItemPotentialTierType.getHiddenPotentialTier(currenttierType);
            if (hiddenType != ItemPotentialTierType.None) {
                equip.setPotentialTier(hiddenType);
            } else {
                equip.setPotentialTier(currenttierType);
            }
        } else {
            equip.setPotentialTier(currenttierType);
        }
        return true;
    }

    /**
     * Resets the bonus potential of an equipment with miracle cube.
     */
    public static boolean resetBonusPotential(Equip pEquip, int rateToRaiseTier, int rateToDecreaseTier, ItemPotentialTierType maxTier, boolean hidePotential, float miracleRate) {
        if (pEquip.getPotentialBonusTier().isHiddenType()) {
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(pEquip.getItemId());
        if (reqLevel < 10) {
            return false;
        }
        //ItemPotentialTierType pCurrentTier = pEquip.getPotentialBonusTier();
        
        if (pEquip.getBonusPotential1() != 0) {
            pEquip.setBonusPotential1(generateBonusPotential(pEquip));
            //pEquip.setBonusPotential1(generateBonusPotential(pEquip, pCurrentTier));
        }
        if (pEquip.getBonusPotential2() != 0) {
            pEquip.setBonusPotential2(generateBonusPotential(pEquip));
            //pEquip.setBonusPotential2(generateBonusPotential(pEquip, pCurrentTier));
        }
        if (pEquip.getBonusPotential3() != 0) {
            pEquip.setBonusPotential3(generateBonusPotential(pEquip));
            //pEquip.setBonusPotential3(generateBonusPotential(pEquip, pCurrentTier));
        }
        return true;
    }

    public static boolean useBonusPotentialScroll(Equip pEquip, ItemPotentialTierType pTier) {
        final int nReqLevel = MapleItemInformationProvider.getInstance().getReqLevel(pEquip.getItemId());
        if (pEquip.getPotentialTier() != ItemPotentialTierType.None) {
            return false;
        }
        if (nReqLevel < 10) {
            return false;
        }
        boolean is3LinePotential = true;
        pEquip.setBonusPotential1(generateBonusPotential(pEquip));
        pEquip.setBonusPotential2(generateBonusPotential(pEquip));
        //pEquip.setBonusPotential1(generateBonusPotential(pEquip, pTier));
        //pEquip.setBonusPotential2(generateBonusPotential(pEquip, pTier));
        if (is3LinePotential) {
            pEquip.setBonusPotential3(generateBonusPotential(pEquip));
            //pEquip.setBonusPotential3(generateBonusPotential(pEquip, pTier));
        } else {
            pEquip.setBonusPotential3(0);
        }
        pEquip.setPotentialBonusTier(pTier);
        return true;
    }

    public static boolean useAwakeningStamp(Equip equip) {
        // Check if equipment is revealed, Rare, Unique, Epic, or Legendary
        // and have the third potential as none.
        if (equip.getPotentialTier() == ItemPotentialTierType.None || equip.getPotential3() != 0 || equip.getPotentialTier().isHiddenType()) {
            return false;
        }
        // Only Items Lv 10 and above can have potentials.
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId());
        if (reqLevel < 10) {
            return false;
        }
        // Decide stats based on tier
        final ItemPotentialTierType currentTier = equip.getPotentialTier();
        final ItemPotentialOption potential_line3 = decideStats(equip, currentTier, reqLevel);

        equip.setPotential3(potential_line3.getOptionId());

        return true;
    }

    public static boolean usePotentialScroll(Equip equip, ItemPotentialTierType tierType) {
        if (equip.getPotentialTier() != ItemPotentialTierType.None) {
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId());
        if (reqLevel < 10) {
            return false;
        }

        boolean isNewEquipment = equip.getPotential1() == 0 && equip.getPotential2() == 0 && equip.getPotential3() == 0; // whether this equipment is newly acquired from mobs, and have not been magnified yet
        boolean is3LinePotential = false;

        if (isNewEquipment) { // decide if this item should be a 2 line or 3 liner. there's a small chance that the equipment may be a 2 liner, requiring the usage of stamps
            if (Randomizer.nextInt(100) < RATE_3LINE_POTENTIAL) {
                is3LinePotential = true;
            }
        } else {
            is3LinePotential = equip.getPotential3() != 0;
        }

        if (ServerConstants.CONTROLLED_POTENTIAL_RESULTS) {
            equip.setPotential1(generatePotential(equip,tierType));
            equip.setPotential2(generatePotential(equip,tierType));
            if (is3LinePotential) {
                equip.setPotential3(generatePotential(equip,tierType));
            } else {
                equip.setPotential3(0);
            }
            equip.setPotentialTier(tierType);
        } else {
            ItemPotentialOption potential_line1 = decideStats(equip, tierType, reqLevel);
            ItemPotentialOption potential_line2 = decideStats(equip, tierType, reqLevel);
            ItemPotentialOption potential_line3 = is3LinePotential ? decideStats(equip, tierType, reqLevel) : null;

            equip.setPotential1(potential_line1.getOptionId());
            equip.setPotential2(potential_line2.getOptionId());
            if (potential_line3 != null) {
                equip.setPotential3(potential_line3.getOptionId());
            } else {
                equip.setPotential3(0);
            }
            equip.setPotentialTier(tierType);
        }
        return true;
    }

    /**
     * Magnify equipment from hidden potential state
     *
     * @param equip
     * @return
     */
    public static boolean magnifyEquipment(Equip equip) {
        if (!equip.getPotentialTier().isHiddenType()) {
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(equip.getItemId());
        if (reqLevel < 10) {
            return false;
        }

        ItemPotentialTierType currenttierType = equip.getPotentialTier();
        ItemPotentialTierType magnifiedTierType;
        switch (currenttierType) {
            case Rare_Hidden:
                magnifiedTierType = ItemPotentialTierType.Rare;
                break;
            case Epic_Hidden:
                magnifiedTierType = ItemPotentialTierType.Epic;
                break;
            case Unique_Hidden:
                magnifiedTierType = ItemPotentialTierType.Unique;
                break;
            case Legendary_Hidden:
                magnifiedTierType = ItemPotentialTierType.Legendary;
                break;
            case Hidden:
                magnifiedTierType = ItemPotentialTierType.Rare;
                break;
            default:
                return false;
        }

        boolean isNewEquipment = equip.getPotential1() == 0 && equip.getPotential2() == 0 && equip.getPotential3() == 0; // whether this equipment is newly acquired from mobs, and have not been magnified yet
        boolean is3LinePotential = false;

        if (isNewEquipment) { // decide if this item should be a 2 line or 3 liner. there's a small chance that the equipment may be a 2 liner, requiring the usage of stamps
            if (Randomizer.nextInt(100) < RATE_3LINE_POTENTIAL) {
                is3LinePotential = true;
            }
        } else {
            is3LinePotential = equip.getPotential3() != 0;
        }

        ItemPotentialOption potential_line1 = decideStats(equip, currenttierType, reqLevel);
        ItemPotentialOption potential_line2 = decideStats(equip, currenttierType, reqLevel);
        ItemPotentialOption potential_line3 = is3LinePotential ? decideStats(equip, currenttierType, reqLevel) : null;

        equip.setPotential1(potential_line1.getOptionId());
        equip.setPotential2(potential_line2.getOptionId());
        if (potential_line3 != null) {
            equip.setPotential3(potential_line3.getOptionId());
        } else {
            equip.setPotential3(0);
        }
        equip.setPotentialTier(magnifiedTierType);

        return true;
    }

    /**
     * Decides on a random potential stat based on the potential tier, required level, and types of equipment (eg: weapon, overall)
     *
     * @param type
     * @param reqLevel
     * @return
     */
    private static ItemPotentialOption decideStats(Equip equip, ItemPotentialTierType type, int reqLevel) {
        ItemPotentialOption selectedOption = null;
        final List<ItemPotentialOption> chosen = new ArrayList();
        int loopCount = 0;

        Map<Integer, ItemPotentialOption> selectedPotentialHashMap;
        switch (type) {
            case Rare:
            case Rare_Hidden: {
                selectedPotentialHashMap = map_RareItem;
                break;
            }
            case Epic:
            case Epic_Hidden: {
                selectedPotentialHashMap = map_EpicItem;
                break;
            }
            case Unique:
            case Unique_Hidden: {
                selectedPotentialHashMap = map_UniqueItem;
                break;
            }
            case Legendary:
            case Legendary_Hidden: {
                selectedPotentialHashMap = map_LegendaryItem;
                break;
            }
            case Hidden:
                selectedPotentialHashMap = map_AnyItem;
                break;
            default: {
                return null;
            }
        }
        while (selectedOption == null) {
            Object[] entries = selectedPotentialHashMap.entrySet().toArray();
            ItemPotentialOption innerSelectedOption = ((Map.Entry<Integer, ItemPotentialOption>) entries[Randomizer.nextInt(entries.length)]).getValue();

            if (!chosen.contains(innerSelectedOption)) {
                chosen.add(innerSelectedOption); // add it to a list, to make sure that it doesnt get selected again

                if (reqLevel >= innerSelectedOption.getRequiredLevel()
                        && // check level
                        IsValidOptionType(innerSelectedOption.getOptionType(), equip.getItemId())) { // check 
                    selectedOption = innerSelectedOption;
                }
            }
            loopCount++;
            if (loopCount > 100) {
                break; // cant be, but just in case we do get an unlimited loop due to changes in WZ data
            }
        }
        return selectedOption;
    }

    /**
     * Checks if this random is the right potential for the equipment type
     *
     * @param itemid
     * @param potentialOptionType
     * @return
     */
    private static boolean isValidItemOption_ForType(int itemid, int potentialOptionType) {
        final EquipSlotType slotType = MapleItemInformationProvider.getInstance().getSlotType(itemid);

        System.out.println("[Debug] Cube Potential Option (" + potentialOptionType + ")");

        switch (potentialOptionType) {
            case 55:
                return slotType == EquipSlotType.Shoes;
            case 54:
                return slotType == EquipSlotType.Glove;
            case 53:
                return slotType == EquipSlotType.Longcoat || slotType == EquipSlotType.Pants || slotType == EquipSlotType.Coat;
            case 52:
                return slotType == EquipSlotType.Cape;
            case 51:
                return slotType == EquipSlotType.Cap;
            case 40:
                return slotType == EquipSlotType.Accessary_Eye || slotType == EquipSlotType.Accessary_Face;
            case 20:
                return !isWeaponRelatedPotentialClass(slotType, itemid) && !(slotType == EquipSlotType.Accessary_Eye || slotType == EquipSlotType.Accessary_Face);
            case 11:
                return isWeaponRelatedPotentialClass(slotType, itemid);
            case 10:
                return !isWeaponRelatedPotentialClass(slotType, itemid);
            case 0:
                return true;
            default:
                System.out.println("[Warning] Found new Potential Option Type (" + potentialOptionType + ")");
                break;
        }

        /*switch (potentialOptionType) {
            case 0: // Any or not stated
                return !isWeaponRelatedPotentialClass(slotType, itemid);
            case 10:
                return isWeaponRelatedPotentialClass(slotType, itemid);
            case 11: // armor
                return slotType == EquipSlotType.Shoes || slotType == EquipSlotType.Glove || slotType == EquipSlotType.Longcoat || slotType == EquipSlotType.Pants || slotType == EquipSlotType.Coat || slotType == EquipSlotType.Cape || slotType == EquipSlotType.Cap;
            case 20:  // No idea
                return !isWeaponRelatedPotentialClass(slotType, itemid);
            case 40: // face accessory
                return slotType == EquipSlotType.Accessary_Eye || slotType == EquipSlotType.Accessary_Face;
            case 51:
                return slotType == EquipSlotType.Cap;
            case 52:
                return slotType == EquipSlotType.Cape;
            case 53:
                return slotType == EquipSlotType.Longcoat || slotType == EquipSlotType.Pants || slotType == EquipSlotType.Coat;
            case 54:
                return slotType == EquipSlotType.Glove;
            case 55:
                return slotType == EquipSlotType.Shoes;
            case 90: // Not sure
                return true;
            default:
                LogHelper.UNCODED.get().info("[ItemPotentialProvider] New potential option found, type: " + potentialOptionType);
                break;
        }*/
        return true;
    }

    /*public void SetRandomPotential(int nCubeItemID, int nOptionCount, int nGrade) {
        pItemOption.nGrade = (byte) nGrade;
        // TODO: Figure out about options 60001 -> 70119 (60001 -> 60073) (70000 -> 70119)
        List<Integer> aOptions = new ArrayList<>(); // There has to be a better way to do this...
        for (Map.Entry<Integer, ItemOptionInfo> pOptionInfo : ItemOptionInfoBase.mItemOption.entrySet()) {
            if (pOptionInfo.getKey() > 10000 && pOptionInfo.getKey() < 60000) { // I dont think items can get anything besides these that arent bFixedPotential
                aOptions.add(pOptionInfo.getKey());
            }
        }
        int nRate = 10;
        switch (nCubeItemID) { // TODO: Better rate for best potential line based on cube
            case 5062009: // Red Cube
                nRate = 10;
                break;
        }
        for (int i = 0; i < nOptionCount; i++) {
            boolean bValid = false;
            while (!bValid) {
                int nOption = aOptions.get((int) (Rand32.GetRandom() % aOptions.size()));
                ItemOptionInfo pOption = ItemOptionInfoBase.GetItemOption(nOption);
                if (pOption != null && pOption.nReqLevel <= ItemInfo.GetEquipItem(nItemID).nrLevel && ItemOptionInfoBase.IsValidOptionType(pOption.nItemOptionID, nItemID)) {
                    if (nGrade == ItemGrade.Legendary) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 40000 && pOption.nItemOptionID <= 41007) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 40000 && pOption.nItemOptionID <= 41007 : pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Unique) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 30000 && pOption.nItemOptionID <= 31004 : pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Epic) {
                        if (i == 0) {
                            if (pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406) {
                                bValid = true;
                            }
                        } else {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 20000 && pOption.nItemOptionID <= 20406 : pOption.nItemOptionID >= 10000 && pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        }
                    } else if (nGrade == ItemGrade.Rare) {
                        if (i == 0) {
                            if (Rand32.GetRandom() % 101 < nRate ? pOption.nItemOptionID >= 10000 && pOption.nItemOptionID <= 10291 : pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        } else {
                            if (pOption.nItemOptionID <= 10291) {
                                bValid = true;
                            }
                        }
                    }
                    if (bValid) {
                        SetPotentialOption(i, pOption.nItemOptionID);
                        bValid = true;
                    }
                }
            }
        }
        aOptions.clear();
    }*/
    /**
     * Weapon related potential items which are able to gain boss % or boss ignore stats
     *
     * @param slotType
     * @return
     */
    private static boolean isWeaponRelatedPotentialClass(EquipSlotType slotType, int itemid) {
        return slotType == EquipSlotType.Weapon
                || slotType == EquipSlotType.Weapon_TakingBothSlot_Shield
                //|| slotType == EquipSlotType.Si_Emblem // This one is wrong :p.
                || (GameConstants.isKatara(itemid) && slotType == EquipSlotType.Shield_OrDualBlade); // Is katara
    }

    /*  public static boolean addPotentialWithPotentialScroll(int scrollItemId) {
        boolean isEpic = scrollItemId / 100 == 20497 && scrollItemId < 2049750;
        boolean isUnique = scrollItemId / 100 == 20497 && scrollItemId >= 2049750;
        
        
    }*/
    /**
     * Returns the item potential information by the potential ID as specified in WZ file
     *
     * @param potId
     * @return
     */
    public static List<ItemPotentialOption> getPotentialInfos(int potId) {
        // return potentialCache.get(potId);
        return null;
    }

    public static ItemPotentialOption getPotentialInfo(int potId) {
        return potentialStatsMap.get(potId);
    }

    /**
     * Returns the Nebulite information by potential ID as specified in the WZ file.
     *
     * @param potId
     * @return
     */
    public static ItemPotentialOption getSocketInfo(final int potId) {
        final NebuliteGrade grade = GameConstants.getNebuliteGrade(potId);

        switch (grade) {
            case GradeS:
                return socketGradeSCache.get(potId);
            case GradeA:
                return socketGradeACache.get(potId);
            case GradeB:
                return socketGradeBCache.get(potId);
            case GradeC:
                return socketGradeCCache.get(potId);
            case GradeD:
                return socketGradeDCache.get(potId);
        }
        return null;
    }

    /**
     * Gets a random nebulite potential stat based on the grade
     *
     * @param grade
     * @return
     */
    public static ItemPotentialOption getRandomNebulitePotential(NebuliteGrade grade) {
        switch (grade) {
            case GradeS:
                return socketGradeSCache.get(Randomizer.nextInt(socketGradeSCache.size()));
            case GradeA:
                return socketGradeACache.get(Randomizer.nextInt(socketGradeACache.size()));
            case GradeB:
                return socketGradeBCache.get(Randomizer.nextInt(socketGradeBCache.size()));
            case GradeC:
                return socketGradeCCache.get(Randomizer.nextInt(socketGradeCCache.size()));
            case GradeD:
                return socketGradeDCache.get(Randomizer.nextInt(socketGradeDCache.size()));
        }
        return null;
    }
}
