/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleDataTool;
import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.wz.cache.WzDataStorage;

/**
 *
 * @author Five
 */
public class VCore {

    public byte nType;
    public short nMaxLevel;
    public int nCoreID, nExpireAfter;
    public String sName, sDesc;
    public CoreOption pOption = new CoreOption();
    public List<String> aJob = new ArrayList<>();
    public List<Integer> aConnectSkill = new ArrayList<>();
    public static Map<Integer, EnforceOption> mSkillEnforce, mBoostEnforce, mSpecialEnforce;
    public static Map<Integer, VCore> mVCore = new HashMap<>();
    public static final int Skill = 0, Boost = 1, Special = 2; // nType

    public VCore() {
        mSkillEnforce = new HashMap<>();
        mBoostEnforce = new HashMap<>();
        mSpecialEnforce = new HashMap<>();
    }

    public static void Load() {
        MapleDataProvider pEtcWz = WzDataStorage.getEtcWZ();
        final MapleData pCoreDataChild = pEtcWz.getData("VCore.img").getChildByPath("CoreData");
        final MapleData pEnforcementDataChild = pEtcWz.getData("VCore.img").getChildByPath("Enforcement");
        VCore pVCore;
        for (MapleData pCore : pCoreDataChild.getChildren()) {
            pVCore = new VCore();
            pVCore.nCoreID = Integer.valueOf(pCore.getName());
            pVCore.nType = (byte) MapleDataTool.getIntConvert("type", pCore, 0);
            pVCore.nMaxLevel = (short) MapleDataTool.getIntConvert("maxLevel", pCore, 0);
            pVCore.sName = MapleDataTool.getString("name", pCore, "");
            pVCore.sDesc = MapleDataTool.getString("desc", pCore, "");
            pVCore.nExpireAfter = (byte) MapleDataTool.getIntConvert("expireAfter", pCore, 0);

            MapleData pConnectSkillChild = pCore.getChildByPath("connectSkill");
            if (pConnectSkillChild != null) {
                for (MapleData pSkill : pConnectSkillChild.getChildren()) {
                    pVCore.aConnectSkill.add(MapleDataTool.getIntConvert(pSkill));
                }
            }

            MapleData pJobChild = pCore.getChildByPath("job");
            if (pJobChild != null) {
                for (MapleData pJob : pJobChild.getChildren()) {
                    pVCore.aJob.add(MapleDataTool.getString(pJob));
                }
            }

            if (pVCore.nType == Special) {
                MapleData pSpecial = pCore.getChildByPath("spCoreOption");

                MapleData pCond = pSpecial.getChildByPath("cond");
                pVCore.pOption.sCondType = MapleDataTool.getString("string", pCond, "");
                pVCore.pOption.nCooltime = MapleDataTool.getIntConvert("cooltime", pCond, 0);
                pVCore.pOption.nValidTime = MapleDataTool.getIntConvert("validtime", pCond, 0);
                pVCore.pOption.nCount = MapleDataTool.getIntConvert("count", pCond, 0);
                pVCore.pOption.dProb = MapleDataTool.getDouble("prob", pCond, 0);

                MapleData pEffect = pSpecial.getChildByPath("effect");
                pVCore.pOption.sEffectType = MapleDataTool.getString("type", pEffect, "");
                pVCore.pOption.nSkillID = MapleDataTool.getIntConvert("skill_id", pEffect, 0);
                pVCore.pOption.nSLV = (short) MapleDataTool.getIntConvert("skill_level", pEffect, 0);
                pVCore.pOption.nHealPercent = MapleDataTool.getIntConvert("heal_percent", pEffect, 0);
                pVCore.pOption.nReducePercent = MapleDataTool.getIntConvert("reducePercent", pEffect, 0);
            }
            mVCore.put(pVCore.nCoreID, pVCore);
        }
        RegisterEnforcementData(pEnforcementDataChild);
    }

    public static void RegisterEnforcementData(MapleData pEnforcementDataChild) {
        EnforceOption pOption;

        MapleData pSkillChild = pEnforcementDataChild.getChildByPath("Skill");
        for (MapleData pSkill : pSkillChild.getChildren()) {
            pOption = new EnforceOption();
            int nLevel = Integer.valueOf(pSkill.getName());
            pOption.nEnforceExp = MapleDataTool.getIntConvert("expEnforce", pSkill, 0);
            pOption.nNextExp = MapleDataTool.getIntConvert("nextExp", pSkill, 0);
            pOption.nExtract = MapleDataTool.getIntConvert("extract", pSkill, 0);
            mSkillEnforce.put(nLevel, pOption);
        }

        MapleData pEnforceChild = pEnforcementDataChild.getChildByPath("Enforce");
        for (MapleData pEnforce : pEnforceChild.getChildren()) {
            pOption = new EnforceOption();
            int nLevel = Integer.valueOf(pEnforce.getName());
            pOption.nEnforceExp = MapleDataTool.getIntConvert("expEnforce", pEnforce, 0);
            pOption.nNextExp = MapleDataTool.getIntConvert("nextExp", pEnforce, 0);
            pOption.nExtract = MapleDataTool.getIntConvert("extract", pEnforce, 0);
            mBoostEnforce.put(nLevel, pOption);
        }

        MapleData pSpecialChild = pEnforcementDataChild.getChildByPath("Special");
        for (MapleData pSpecial : pSpecialChild.getChildren()) {
            pOption = new EnforceOption();
            int nLevel = Integer.valueOf(pSpecial.getName());
            pOption.nEnforceExp = MapleDataTool.getIntConvert("expEnforce", pSpecial, 0);
            pOption.nNextExp = MapleDataTool.getIntConvert("nextExp", pSpecial, 0);
            pOption.nExtract = MapleDataTool.getIntConvert("extract", pSpecial, 0);
            mSpecialEnforce.put(nLevel, pOption);
        }
    }

    public static VCore GetCore(int nCoreID) {
        return mVCore.get(nCoreID);
    }

    public static boolean IsSkillNode(int nCoreID) {
        return GetCore(nCoreID).nType == Skill;
    }

    public static boolean IsBoostNode(int nCoreID) {
        return GetCore(nCoreID).nType == Boost;
    }

    public static boolean IsSpecialNode(int nCoreID) {
        return GetCore(nCoreID).nType == Special;
    }

    public static int GetMaxLevel(int nType) {
        if (nType == Skill || nType == Boost) {
            return 25;
        } else {
            return 1;
        }
    }

    public boolean IsClassSkill(int nJob) {
        if (aJob.contains("warrior")) {
            return GameConstants.isWarriorHero(nJob) || GameConstants.isWarriorPaladin(nJob) || GameConstants.isWarriorDarkKnight(nJob) || GameConstants.isDawnWarrior(nJob) || GameConstants.isDemon(nJob)
                    || GameConstants.isMihile(nJob) || GameConstants.isKaiser(nJob) || GameConstants.isZero(nJob) || GameConstants.isAran(nJob) || GameConstants.isBlaster(nJob)
                    || GameConstants.isHayato(nJob);
        } else if (aJob.contains("magician")) {
            return GameConstants.isMagicianIceLightning(nJob) || GameConstants.isMagicianFirePoison(nJob) || GameConstants.isMagicianBishop(nJob) || GameConstants.isLuminous(nJob) || GameConstants.isBattleMage(nJob)
                    || GameConstants.isKinesis(nJob) || GameConstants.isEvan(nJob) || GameConstants.isBlazeWizardCygnus(nJob) || GameConstants.isKanna(nJob) || GameConstants.isBeastTamer(nJob);
        } else if (aJob.contains("archer")) {
            return GameConstants.isArcherBowmaster(nJob) || GameConstants.isWindArcher(nJob) || GameConstants.isWildHunter(nJob) || GameConstants.isArcherMarksman(nJob) || GameConstants.isMercedes(nJob);
        } else if (aJob.contains("rogue")) {
            return GameConstants.isThiefNightLord(nJob) || GameConstants.isThiefShadower(nJob) || GameConstants.isDualBlade(nJob) || GameConstants.isNightWalkerCygnus(nJob) || GameConstants.isPhantom(nJob);
        } else if (aJob.contains("pirate")) {
            return GameConstants.isXenon(nJob) || GameConstants.isPirateBuccaneer(nJob) || GameConstants.isPirateCorsair(nJob) || GameConstants.isThunderBreakerCygnus(nJob) || GameConstants.isCannoneer(nJob)
                    || GameConstants.isMechanic(nJob) || GameConstants.isShade(nJob) || GameConstants.isAngelicBuster(nJob) || GameConstants.isJett(nJob);
        }
        return false;
    }

    public boolean IsJobSkill(int nJob) {
        if (aJob.contains("warrior") || aJob.contains("magician") || aJob.contains("archer")
                || aJob.contains("rogue") || aJob.contains("pirate")) {
            return false;
        }
        for (String sJob : aJob) {
            if (Integer.valueOf(sJob) == nJob) {
                return true;
            }
        }
        return false;
    }

    public static Map<Integer, EnforceOption> GetEnforceOption(int nType) {
        if (nType == Skill) {
            return mSkillEnforce;
        } else if (nType == Boost) {
            return mBoostEnforce;
        } else if (nType == Special) {
            return mSpecialEnforce;
        }
        return null;
    }

    public static List<VCore> GetSkillNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : mVCore.values()) {
            if (pVCore.nType == Skill) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public static List<VCore> GetBoostNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : mVCore.values()) {
            if (pVCore.nType == Boost && !pVCore.aJob.contains("none")) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public static List<VCore> GetSpecialNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : mVCore.values()) {
            if (pVCore.nType == Special) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public static List<VCore> GetClassNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : GetSkillNodes()) {
            if (pVCore.aJob.contains("warrior") || pVCore.aJob.contains("magician") || pVCore.aJob.contains("archer")
                    || pVCore.aJob.contains("rogue") || pVCore.aJob.contains("pirate")) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public static List<VCore> GetJobNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : GetSkillNodes()) {
            if (!pVCore.aJob.contains("all") && !pVCore.aJob.contains("none") && !pVCore.aJob.contains("warrior") && !pVCore.aJob.contains("magician")
                    && !pVCore.aJob.contains("archer") && !pVCore.aJob.contains("rogue") && !pVCore.aJob.contains("pirate")) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public static List<VCore> GetDecentNodes() {
        List<VCore> aVCore = new ArrayList<>();
        for (VCore pVCore : GetSkillNodes()) {
            if (pVCore.sName.contains("Decent")) {
                aVCore.add(pVCore);
            }
        }
        return aVCore;
    }

    public class CoreOption {

        public short nSLV;
        public int nSkillID, nCooltime, nValidTime, nCount, nHealPercent, nReducePercent;
        public String sEffectType, sCondType;
        public double dProb;
    }

    public static class EnforceOption {

        public int nEnforceExp, nNextExp, nExtract;
    }
}
