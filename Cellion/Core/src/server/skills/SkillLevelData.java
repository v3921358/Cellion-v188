/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.skills;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import provider.wz.cache.WzDataStorage;
import provider.wz.nox.NoxBinaryReader;
import server.maps.FieldType;
import server.maps.SharedMapResources;
import tools.Pair;

/**
 *
 * @author Npvak
 */
public final class SkillLevelData {

    public int nHP, nHcHP, nMP, nPAD, nPDD, nMAD, nMDD, nACC, nEVA, nSTR, nDEX, nINT, nLUK, nCraft, nSpeed, nJump, nMorph, nHPCon, nMPCon, nSoulMPCon,
            nForceCon, nMoneyCon, nPowerCon, nItemCon, nItemConNo, nIceGageCon, nHPRCon, nAranComboCon, nPsychicCon, nPsychicRecovery, nPsychicReq, nDamage,
            nPVPDamage, nPVPDamageX, nFixDamage, nSelfDestruction, tTime, tHcTime, tSubTime, tHcSubTime, tAttackDelay, nProp, nHcProp, nSubProp, nHcSubProp, nAttackCount,
            nDamageToBoss, nBulletCount, nBulletConsume, nBulletSpeed, nMastery, nMobCount, nX, nY, nZ, nEMHP, nEMMP, nEPAD, nIndiePAD, nIndieMAD, nIndiePDD,
            nIndieMDD, nIndieMHP, nIndieMHPR, nIndieMMP, nIndieMMPR, nIndieACC, nIndieEVA, nIndieEVAR, nIndieJump, nIndieSpeed, nIndieAllStat, nIndieDamR, nIndieMDF, nIndieBooster,
            nIndieMaxDamageOver, nIndieMaxDamageOverR, nIndieCr, nIndieAsrR, nIndieTerR, nIndiePDDR, nIndieMDDR, nIndieBDR, nIndieStance, nIndieIgnoreMobpdpR, nIndieEXP, nIndiePADR,
            nIndieMADR, nIndieDrainHP, nIndiePMdR, nIndieForceJump, nIndieForceSpeed, nEMAD, nEPDD, nEMDD, nRange, nCooltime, nHcCooltime, nCooltimeMS, nHcCooltimeMS,
            nHcReflect, nHcSummonHP, nMHPr, nMMPr, nMHPx, nMMPx, nCr, nCriticalDamageMin, nCriticalDamageMax, nACCr, nEVAr, nAbormalResistance, nElementalResistance, nPDDr, nMDDr, nPDr, nMDr, nDamageRate, nPDamr, nMDamr, nBDamr,
            nBPDamr, nPADr, nMADr, nEXPr, nDot, nDotInterval, nDotTime, nDotSuperPos, nDotTickDamR, nIgnoreMobPdpR, nASRr, nTERr, nMESOr, nPADx, nMADx, nIgnoreMobDamR, nPsdJump, nPsdSpeed, nPsdSpeedMax,
            nPsdIncMaxDam, nOverChargeR, nDiscountR, nMESO, nPQPointR, nMileage, nItemUpgradeBonusR, nItemCursedProtectR, nItemTUCProtectR, nPDDx, nMDDx, nACCx, nEVAx, nSTRx, nDEXx, nINTx, nLUKx,
            nRequiredGuildLevel, nGgpCon, nIgpCon, nPrice, nPriceUnit, nExtendPrice, nPeriod, nMPConReduce, nActionSpeed, nKP, nSTRfx, nDEXfx, nINTfx, nLUKfx, nPDD2MDD, nMDD2PDD, nACC2MP, nEVA2HP, nSTR2DEX,
            nDEX2STR, nINT2LUK, nLUK2DEX, nLV2PAD, nLV2MAD, nNBDamr, nTowerDamr, nMinionDeathProp, nAbNormalDamr, nACC2DAM, nPDD2DAM, nMDD2DAM, nPDD2MDx, nMDD2PDx, nNoCoolProp,
            nPassivePlus, nTargetPlus, nBuffTimeR, nDropR, nLV2PDamX, nLV2MDamX, nMpConEff, nLV2DamX, nSummonTimeR, nExpLossReduceR, nSuddenDeathR, nSuddenDeathMinT,
            nOnHitHpRecoveryR, nOnHitMpRecoveryR, nCoolTimeR, nMHP2DamX, nMMP2DamX, nFinalAttackDamR, nDotHealHPPerSecondR, nDamPlus, nAreaDotCount, nStrR, nDexR, nLukR, nIntR,
            nReduceForceR, nMDF, nLV2Str, nLV2Dex, nLV2Int, nLV2Luk, nStanceProp, nLV2MMP, nLV2MHP, nCostMPR, nCostHPR, nMobCountDamR, nDamAbsorbShieldR, nKillHpRecoveryR, nMDamageOver,
            nOnActive, nFixCoolTime, nIncMobRateDummy, nS, nU, nV, nW, nQ, nS2, nU2, nV2, nW2, nQ2, nT, nGauage;
    public Point pLt, pRb, pLt2, pRb2, pLt3, pRb3, pLt4, pRb4;
    public Rectangle rcAffectedArea, rcAffectedArea2, rcAffectedArea3, rcAffectedArea4, rcUnionVariableAffectedArea, vrcAffectedArea;
    public String sAction;

    SkillLevelData(NoxBinaryReader data, boolean hasLevelData) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
