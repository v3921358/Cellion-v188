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
    Plus_12_Percent_LUK(42066, ""),
    Plus_12_Percent_INT(42065, ""),
    Plus_12_Percent_STR(42065, ""),
    Plus_12_Percent_DEX(42064, ""),
    Plus_9_Percent_LUK(30044, ""),
    Plus_9_Percent_INT(30043, ""),
    Plus_9_Percent_STR(30041, ""),
    Plus_9_Percent_DEX(30042, ""),
    Plus_9_Percent_ALL_STAT(40086, ""),
    Plus_6_Percent_ALL_STAT(30086, ""),
    Plus_40_Percent_Ignore_Defence(40292, ""),
    Plus_35_Percent_Ignore_Defence(40291, ""),
    Plus_30_Percent_Ignore_Defence(30291, ""),
    Plus_12_Percent_ATT(42051, ""),
    Plus_9_Percent_ATT(32051, ""),
    Plus_12_Percent_MAG_ATT(40052, ""),
    Plus_9_Percent_MAG_ATT(30052, ""),
    Plus_12_Percent_DMG(42070, ""),
    Plus_9_Percent_DMG(30070, ""),
    Plus_12_Percent_Critical_Chance(40055, ""),
    Plus_9_Percent_Critical_Chance(32057, ""),
    ATT_PER_10_Levels_Plus_1(42095, ""),
    MAG_ATT_PER_10_Levels_Plus_1(42096, ""),
    Plus_10_Percent_Chance_to_Ignore_40_Percent_Damage(40357, ""),
    Plus_40_Percent_Boss_Damage(40603, ""),
    Plus_35_Percent_Boss_Damage(40602, ""),
    Plus_30_Percent_Boss_Damage(40601, ""),
    Plus_12_Percent_HP(40045, ""),
    Plus_12_Percent_MP(40046, ""),
    Plus_12_Percent_DEF(40053, ""),
    Plus_9_Percent_HP(30045, ""),
    Plus_9_Percent_MP(30046, ""),
    Plus_9_Percent_DEF(30054, ""),
    Plus_5_Percent_Chance_to_Ignore_20_Percent_Damage(30356, ""),
    Plus_10_Percent_Chance_to_Ignore_20_Percent_Damage(40356, ""),
    Plus_5_Percent_Chance_to_Ignore_40_Percent_Damage(30357, ""),
    Plus_2_Percent_Critical_Rate(22056, ""),
    Plus_2_Percent_ATT(42052, ""),
    Plus_2_Percent_DMG(42071, ""),
    Plus_20_Percent_Drop_Rate(40656, ""),
    Minus_1_Second_Cooldown_Reduction(40556, ""),
    Minus_2_Second_Cooldown_Reduction(40557, ""),
    Decent_Mystic_Door(31002, ""),
    Decent_Advanced_Blessing(41006, ""),
    Plus_8_Percent_Critical_Rate(20055, ""),
    Decent_Sharp_Eyes(31003, ""),
    Decent_Speed_Infusion(41007, ""),
    Decent_Haste(31001, ""),
    Decent_Combat_Orders(41005, ""),
    Decent_Hyper_Body(31004, ""),
    Plus_4_Percent_ALL_STAT(20086, ""),
    Plus_6_Percent_LUK(20044, ""),
    Plus_6_Percent_INT(20043, ""),
    Plus_6_Percent_STR(20041, ""),
    Plus_6_Percent_DEX(20042, ""),
    Plus_6_Percent_HP(22045, ""),
    Plus_6_Percent_MP(20046, ""),
    Plus_6_Percent_DEF(20054, ""),
    Plus_3_Percent_LUK(10044, ""),
    Plus_3_Percent_INT(10043, ""),
    Plus_3_Percent_STR(10041, ""),
    Plus_3_Percent_DEX(10042, ""),
    Plus_3_Percent_HP(12045, ""),
    Plus_3_Percent_MP(10046, ""),
    Plus_3_Percent_DEF(10054, ""),
    LUK_PER_10_Levels_Plus_1(30094, ""),
    INT_PER_10_Levels_Plus_1(32093, ""),
    STR_PER_10_Levels_Plus_1(32091, ""),
    DEX_PER_10_Levels_Plus_1(32092, ""),
    LUK_PER_10_Levels_Plus_2(42094, ""),
    INT_PER_10_Levels_Plus_2(42093, ""),
    STR_PER_10_Levels_Plus_2(42091, ""),
    DEX_PER_10_Levels_Plus_2(42092, ""),
    ATT_Plus_18(40011, ""),
    ATT_Plus_14(42011, ""),
    MAG_ATT_Plus_18(40012, ""),
    MAG_ATT_Plus_14(42012, ""),
    Plus_18_Percent_Boss_Damage(42602, ""),
    Plus_12_Percent_Boss_Damage(32601, ""),
    MAX_HP_Plus_10_Percent(42047, ""),
    MAX_HP_Plus_7_Percent(32047, ""),
    Plus_10_Percent_MAX_MP(42046, ""),
    Plus_7_Percent_MAX_MP(32048, ""),
    Plus_15_Percent_TO_REGEN_MANA_ON_ATT(32206, ""),
    Plus_15_Percent_TO_REGEN_HP_ON_ATT(32201, ""),
    Plus_4_Percent_Ignore_DEF(42292, ""),
    Plus_7_Percent_LUK(42043, ""),
    Plus_7_Percent_INT(42044, ""),
    Plus_7_Percent_STR(42041, ""),
    Plus_7_Percent_DEX(42042, ""),
    Plus_10_Percent_MaxHP(42045, ""),
    Plus_10_Percent_MaxMP(42048, ""),
    Plus_10_Percent_DEF(42055, ""),
    Plus_5_Percent_LUK(32044, ""),
    Plus_5_Percent_INT(32043, ""),
    Plus_5_Percent_STR(32041, ""),
    Plus_5_Percent_DEX(32042, ""),
    Plus_5_Percent_ALL_STAT(42086, ""),
    Plus_7_Percent_MaxHP(32045, ""),
    Plus_7_Percent_MaxMP(32046, ""),
    Plus_7_Percent_DEF(32056, ""),
    Plus_250_HP(40005, ""),
    Plus_250_MP(42008, ""),
    HP_Recovery_Items_and_Skills_Plus_20_Percent(32551, ""),
    STR_Plus_18(42001, ""),
    INT_Plus_18(42003, ""),
    DEX_Plus_18(42002, ""),
    LUK_Plus_18(40004, ""),
    ALL_STAT_Plus_12(40081, ""),
    All_Elemental_Res_Plus_4_Percent(32802, ""),
    Critical_Damage_Plus_1_Percent(42060, ""),
    Plus_2_Percent_MAG_ATT(42054, ""),
    Crit_Chance_Plus_2_Percent(42058, ""),
    Plus_5_Percent_Meso_Drop_Rate(42650, ""),
    Plus_5_Percent_Item_Drop_Rate(42656, ""),
    Plus_3_Percent_ALL_STAT(32086, ""),
    Plus_200_HP(30005, ""),
    Plus_200_MP(32008, ""),
    STR_Plus_14(32001, ""),
    INT_Plus_14(32003, ""),
    DEX_Plus_14(32002, ""),
    LUK_Plus_14(30004, ""),
    ALL_STAT_Plus_8(30081, ""),
    ATT_Plus_13(32011, ""),
    Plus_5_Percent_MP(22048, ""),
    Plus_5_Percent_DEF(22055, ""),
    Plus_2_Percent_LUK(12044, ""),
    Plus_2_Percent_INT(12043, ""),
    Plus_2_Percent_STR(12041, ""),
    Plus_2_Percent_DEX(12042, ""),
    Plus_1_Percent_ALL_STAT(22086, ""),
    Plus_2_Percent_MP(12046, ""),
    Plus_2_Percent_DEF(12056, ""),
    Plus_150_HP(20005, ""),
    Plus_150_MP(22008, ""),
    STR_Plus_10(22001, ""),
    INT_Plus_10(22003, ""),
    DEX_Plus_10(22002, ""),
    LUK_Plus_10(20004, ""),
    ATT_Plus_11(22011, ""),
    Plus_100_HP(10005, ""),
    Plus_100_MP(12008, ""),
    STR_Plus_6(12001, ""),
    INT_Plus_6(12003, ""),
    DEX_Plus_6(12002, ""),
    LUK_Plus_6(10004, ""),
    ALL_STAT_Plus_4(10081, ""),
    ATT_Plus_10(12011, ""),
    Plus_4_Percent_LUK(22043, ""),
    Plus_4_Percent_INT(22044, ""),
    Plus_4_Percent_STR(22041, ""),
    Plus_4_Percent_DEX(22042, "");
    
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
}
