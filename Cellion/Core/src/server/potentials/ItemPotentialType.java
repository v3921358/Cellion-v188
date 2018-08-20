package server.potentials;

import tools.LogHelper;

/**
 *
 * @author Lloyd Korn
 */
public enum ItemPotentialType {
    incSTR,
    incSTRr,
    incSTRlv,
    //
    incDEX,
    incDEXr,
    incDEXlv,
    //
    incINT,
    incINTr,
    incINTlv,
    //
    incLUK,
    incLUKr,
    incLUKlv,
    //
    incAsrR,
    incTerR,
    //
    incMHP,
    incMHPr,
    incMHPlv,
    //
    incMMP,
    incMMPr,
    //
    incACC,
    incACCr,
    incEVA,
    incEVAr,
    //
    incJump,
    //
    incPAD,
    incPADr,
    incPADlv,
    //
    incPDD,
    incPDDr,
    //
    incMAD,
    incMADr,
    incMADlv,
    //
    incDAMr,
    //
    incMDD,
    incMDDr,
    //
    incCr, // critical rate
    incAllskill,
    //
    incMaxDamage,
    //
    incCriticaldamageMin,
    incCriticaldamageMax,
    //
    incEXPr,
    //
    level, // Skills
    incMesoProp,
    MP,
    HP,
    incSpeed,
    incRewardProp,
    RecoveryHP,
    RecoveryMP,
    ignoreDAM,
    ignoreDAMr,
    ignoreTargetDEF,
    DAMreflect,
    mpconReduce,
    RecoveryUP,
    mpRestore,
    time,
    reduceCooltime,
    NULL,
    face; // client sidded, dont need to handle

    private ItemPotentialType() {
    }

    public static ItemPotentialType fromString(String Str) {
        try {
            return valueOf(Str);
        } catch (Exception e) {
            LogHelper.UNCODED.get().info(String.format("[ItemPotentialType] An unknown potential type has been found [%s]. Please update it under server/ItemPotentialType", Str));
            return NULL;
        }
    }
}
