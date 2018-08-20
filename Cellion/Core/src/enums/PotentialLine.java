/*
 * Cellion Development
 */
package enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Potential Lines
 * @author Mazen Massoud
 */
public enum PotentialLine {
    
    // TODO: Add Potential Names to Enum and Confirm the values are accurate.
    Plus_12_Percent_LUK(42066, "LUK: +12%"),
    Plus_12_Percent_INT(42065, "INT: +12%"),
    Plus_12_Percent_STR(42065, "STR: +12%"),
    Plus_12_Percent_DEX(42064, "DEX: +12%"),
    Plus_9_Percent_LUK(30044, "LUK: +9%"),
    Plus_9_Percent_INT(30043, "INT: +9%"),
    Plus_9_Percent_STR(30041, "STR: +9%"),
    Plus_9_Percent_DEX(30042, "DEX: +9%"),
    Plus_9_Percent_ALL_STAT(40086, "All Stats: +9%"),
    Plus_6_Percent_ALL_STAT(30086, "All Stats: +6%"),
    Plus_40_Percent_Ignore_Defence(40292, "Ignore Monster DEF: +40%"),
    Plus_35_Percent_Ignore_Defence(40291, "Ignore Monster DEF: +35%"),
    Plus_30_Percent_Ignore_Defence(30291, "Ignore Monster DEF: +30%"),
    Plus_13_Percent_ATT(42051, "ATT: +13%"),
    Plus_10_Percent_ATT(32051, "ATT: +10%"),
    Plus_13_Percent_MAG_ATT(40052, "Magic ATT: +13%"),
    Plus_10_Percent_MAG_ATT(30052, "Magic ATT: +10%"),
    Plus_13_Percent_DMG(42070, "Damage: +13%"),
    Plus_10_Percent_DMG(30070, "Damage: +10%"),
    Plus_12_Percent_Critical_Chance(40055, "Critical Rate: +12%"),
    Plus_9_Percent_Critical_Chance(32057, "Critical Rate: +9%"),
    ATT_PER_10_Levels_Plus_1(42095, "ATT per 10 Levels: +1"),
    MAG_ATT_PER_10_Levels_Plus_1(42096, "Magic ATT per 10 Levels: +1"),
    Plus_40_Percent_Boss_Damage(40603, "Boss Monster Damage: +40%"),
    Plus_35_Percent_Boss_Damage(40602, "Boss Monster Damage: +35%"),
    Plus_30_Percent_Boss_Damage(40601, "Boss Monster Damage: +30%"),
    Plus_12_Percent_HP(40045, "Max HP: +12%"),
    Plus_12_Percent_MP(40046, "Max MP: +12%"),
    Plus_12_Percent_DEF(40053, "DEF: +12%"),
    Plus_9_Percent_HP(30045, "Max HP: +9%"),
    Plus_9_Percent_MP(30046, "Max MP: +9%"),
    Plus_9_Percent_DEF(30054, "DEF: +9%"),
    Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage(30356, "5% Chance to Ignore 20% Damage"),
    Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage(40356, "10% Chance to Ignore 20% Damage"),
    Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage(30357, "5% Chance to Ignore 40% Damage"),
    Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage(40357, "10% Chance to Ignore 40% Damage"),
    Plus_2_Percent_Critical_Rate(22056, "Critical Rate: +2%"),
    Plus_2_Percent_ATT(42052, "ATT: +2%"),
    Plus_2_Percent_DMG(42071, "Damage: +2%"),
    Plus_20_Percent_Drop_Rate(40656, "Item Drop Rate: +20%"),
    Minus_1_Second_Cooldown_Reduction(40556, "Skill Cooldown: -1 Sec"),
    Minus_2_Second_Cooldown_Reduction(40557, "Skill Cooldown: -1 Sec"),
    Decent_Mystic_Door(31002, "Enables <Decent Mystic Door>"),
    Decent_Advanced_Blessing(41006, "Enables <Decent Advanced Blessing>"),
    Plus_8_Percent_Critical_Rate(20055, "Critical Rate: +8%"),
    Decent_Sharp_Eyes(31003, "Enables <Decent Sharp Eyes>"),
    Decent_Speed_Infusion(41007, "Enables <Decent Speed Infusion>"),
    Decent_Haste(31001, "Enables <Decent Haste>"),
    Decent_Combat_Orders(41005, "Enables <Decent Combat Orders>"),
    Decent_Hyper_Body(31004, "Enables <Decent Hyper Body>"),
    Plus_4_Percent_ALL_STAT(20086, "All Stats: +4%"),
    Plus_6_Percent_LUK(20044, "LUK: +6%"),
    Plus_6_Percent_INT(20043, "INT: +6%"),
    Plus_6_Percent_STR(20041, "STR: +6%"),
    Plus_6_Percent_DEX(20042, "DEX: +6%"),
    Plus_6_Percent_HP(22045, "Max HP: +6%"),
    Plus_6_Percent_MP(20046, "Max MP: +6%"),
    Plus_6_Percent_DEF(20054, "DEF: +6%"),
    Plus_3_Percent_LUK(10044, "LUK: +3%"),
    Plus_3_Percent_INT(10043, "INT: +3%"),
    Plus_3_Percent_STR(10041, "STR: +3%"),
    Plus_3_Percent_DEX(10042, "DEX: +3%"),
    Plus_3_Percent_HP(12045, "Max HP: +3%"),
    Plus_3_Percent_MP(10046, "Max MP: +3%"),
    Plus_3_Percent_DEF(10054, "DEF: +3%"),
    LUK_PER_10_Levels_Plus_1(30094, "LUK per 10 Levels: +1"),
    INT_PER_10_Levels_Plus_1(32093, "INT per 10 Levels: +1"),
    STR_PER_10_Levels_Plus_1(32091, "STR per 10 Levels: +1"),
    DEX_PER_10_Levels_Plus_1(32092, "DEX per 10 Levels: +1"),
    LUK_PER_10_Levels_Plus_2(42094, "LUK per 10 Levels: +2"),
    INT_PER_10_Levels_Plus_2(42093, "INT per 10 Levels: +2"),
    STR_PER_10_Levels_Plus_2(42091, "STR per 10 Levels: +2"),
    DEX_PER_10_Levels_Plus_2(42092, "DEX per 10 Levels: +2"),
    ATT_Plus_19(40011, "ATT: +19"),
    ATT_Plus_15(42011, "ATT: +15"),
    MAG_ATT_Plus_19(40012, "Magic ATT: +19"),
    MAG_ATT_Plus_15(42012, "Magic ATT: +15"),
    Plus_18_Percent_Boss_Damage(42602, "Boss Monster Damage: +18%"),
    Plus_12_Percent_Boss_Damage(32601, "Boss Monster Damage: +12%"),
    MAX_HP_Plus_10_Percent(42047, "Max HP: +10%"),
    MAX_HP_Plus_7_Percent(32047, "Max HP: +7%"),
    Plus_10_Percent_MAX_MP(42046, "Max MP: +10%"), // Says +11%
    Plus_7_Percent_MAX_MP(32048, "Max MP: +7%"),
    Plus_15_Percent_TO_REGEN_MANA_ON_ATT(32206, "15% chance to recover 40 MP when attacking."), // the amount of mp changes on the weapon level (40mp on lower than 70, 85mp on lower than 140 and 100mp on 140+)
    Plus_15_Percent_TO_REGEN_HP_ON_ATT(32201, "15% chance to recover 40 HP when attacking."), // the amount of hp changes on the weapon level (40hp on lower than 70, 85hp on lower than 140 and 100hp on 140+)
    Plus_4_Percent_Ignore_DEF(42292, "Ignore Monster DEF: +4%"),
    Plus_7_Percent_LUK(42043, "LUK: +7%"),
    Plus_7_Percent_INT(42044, "INT: +7%"),
    Plus_7_Percent_STR(42041, "STR: +7%"),
    Plus_7_Percent_DEX(42042, "DEX: +7%"),
    Plus_10_Percent_MaxHP(42045, "Max HP: +10%"),
    Plus_10_Percent_MaxMP(42048, "Max MP: +10%"),
    Plus_10_Percent_DEF(42055, "DEF: +10%"),
    Plus_5_Percent_LUK(32044, "LUK: +5%"),
    Plus_5_Percent_INT(32043, "INT: +5%"),
    Plus_5_Percent_STR(32041, "STR: +5%"),
    Plus_5_Percent_DEX(32042, "DEX: +5%"),
    Plus_5_Percent_ALL_STAT(42086, "All Stats: +6%"),
    Plus_7_Percent_MaxHP(32045, "Max HP: +7%"), 
    Plus_7_Percent_MaxMP(32046, "Max MP: +7%"),
    Plus_7_Percent_DEF(32056, "DEF: +7%"),
    Plus_250_HP(40005, "Max HP: +250"),
    Plus_250_MP(42008, "Max MP: +250"),
    HP_Recovery_Items_and_Skills_Plus_20_Percent(32551, "HP Recover Items and Skills: +20%"),
    STR_Plus_18(42001, "STR: +18"),
    INT_Plus_18(42003, "INT: +18"),
    DEX_Plus_18(42002, "DEX: +18%"),
    LUK_Plus_18(40004, "LUK: +18%"),
    ALL_STAT_Plus_12(40081, "All Stats: +12"),
    All_Elemental_Res_Plus_4_Percent(32802, "All Elemental Resistances: +4%"),
    Critical_Damage_Plus_1_Percent(42060, "Critical Damage: +1%"),
    Plus_2_Percent_MAG_ATT(42054, "Magic ATT: +2%"),
    Crit_Chance_Plus_2_Percent(42058, "Critical  Rate: +2%"),
    Plus_5_Percent_Meso_Drop_Rate(42650, "Mesos Obtained: +5%"),
    Plus_5_Percent_Item_Drop_Rate(42656, "Item Drop Rate: +5%"),
    Plus_3_Percent_ALL_STAT(32086, "All Stats: +3%"), // 4% on onyx and 5% on SW
    Plus_200_HP(30005, "Max HP: +200"),
    Plus_200_MP(32008, "Max MP: +200"),
    STR_Plus_14(32001, "STR: +14"), // Onyx says +16 and SW says +17
    INT_Plus_14(32003, "INT: +14"), // Onyx says +16 and SW says +17
    DEX_Plus_14(32002, "DEX: +14"), // Onyx says +16 and SW says +17
    LUK_Plus_14(30004, "LUK: +14"), // Onyx says +16 and SW says +17
    ALL_STAT_Plus_8(30081, "ALL STAT: +8"), // Does not give an line
    ATT_Plus_13(32011, "ATT: +13"),
    Plus_5_Percent_MP(22048, "Max MP: +5%"),
    Plus_5_Percent_DEF(22055, ""), // says Critical Rate: +6%
    Plus_2_Percent_LUK(12044, "LUK: +2%"),
    Plus_2_Percent_INT(12043, "INT: +2%"),
    Plus_2_Percent_STR(12041, "STR: +2%"),
    Plus_2_Percent_DEX(12042, "DEX: +2%"),
    Plus_1_Percent_ALL_STAT(22086, "All Stats: +1%"), // Onyx gives 3% and SW 4%
    Plus_2_Percent_MP(12046, "Max MP: +2%"),
    Plus_2_Percent_DEF(12056, ""), // Does not give an line 
    Plus_150_HP(20005, "Max HP: +150"), // FK gives +15 and SW gives +195
    Plus_150_MP(22008, "Max MP: +150"), // FK gives +15 and SW gives +180 <????
    STR_Plus_10(22001, "STR: +10"), // Fruit Knife gives +4 and Onyx gives +14 and SW gives +15
    INT_Plus_10(22003, "INT: +10"), // Fruit Knife gives +4 and Onyx gives +14 and SW gives +15
    DEX_Plus_10(22002, "DEX: +10"), // Fruit Knife gives +4 and Onyx gives +14 and SW gives +15
    LUK_Plus_10(20004, "LUK: +10"), // Fruit Knife gives +4 and Onyx gives +14 and SW gives +15
    ATT_Plus_11(22011, "ATT: +11"), // Fruit Knife gives +4 and SW gives +12
    Plus_100_HP(10005, "Max HP: +100"), // FK gives +10 and SW gives +125
    Plus_100_MP(12008, "Max MP: +100"), // FK gives +10 and SW gives +100 <????
    STR_Plus_6(12001, "STR: +6"), // FK gives +2 and Onyx gives +10 and SW gives +11
    INT_Plus_6(12003, "INT: +6"), // FK gives +2 and Onyx gives +10 and SW gives +11
    DEX_Plus_6(12002, "DEX: +6"), // FK gives +2 and Onyx gives +10 and SW gives +11
    LUK_Plus_6(10004, "LUK: +6"), // FK gives +2 and Onyx gives +10 and SW gives +11
    ALL_STAT_Plus_4(10081, "ALL STAT: +4"), // FK gives +1 and Onyx gives +5 and SW gives +6
    ATT_Plus_10(12011, "ATT: +10"), // FK gives +1 and SW gives +11
    Plus_4_Percent_LUK(22043, "LUK: +4%"), // FK gives +1% and SW gives +5%
    Plus_4_Percent_INT(22044, "INT: +4%"), // FK gives +1% and SW gives +5%
    Plus_4_Percent_STR(22041, "STR: +4%"), // FK gives +1% and SW gives +5%
    Plus_4_Percent_DEX(22042, "DEX: +4%"); // FK gives +1% and SW gives +5%
    
    private final int nValue;
    private final String sPotentialName;
    private static final Map<Integer, PotentialLine> aPotentialCollection = new HashMap<Integer, PotentialLine>();
    
    private static final List<PotentialLine> aLineValues = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int nSize = aLineValues.size();
    private static final Random pRandom = new Random();
    
    static {
        for (PotentialLine pLine : PotentialLine.values()) {
            aPotentialCollection.put(pLine.getValue(), pLine);
        }
    }
    
    PotentialLine(int nValue, String sPotentialName) {
        this.nValue = nValue;
        this.sPotentialName = sPotentialName;
    }
    
    public static PotentialLine get(int nValue) {
        return aPotentialCollection.get(nValue);
    }
    
    public int getValue() {
        return nValue;
    }

    public String getName() {
        return sPotentialName;
    }
    
    public static PotentialLine getRandom()  {
        return aLineValues.get(pRandom.nextInt(nSize));
    }
    
    public static boolean isBonusPotential(PotentialLine pLine) {
        return (pLine.getValue() >= 42001 && pLine.getValue() <= 42656);
    }
    
    public static boolean isNebulitePotential(PotentialLine pLine) {
        return (pLine.getValue() >= 70000 && pLine.getValue() <= 70119);
    }
}
