package client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import constants.skills.*;
import constants.skills.Global;
import constants.skills.Aran;
import constants.skills.BattleMage;
import constants.skills.DemonAvenger;
import constants.skills.Kaiser;
import constants.skills.Kanna;
import constants.skills.Kinesis;
import constants.skills.Luminous;
import constants.skills.Magician;
import constants.skills.Warrior;
import constants.skills.Xenon;
import handling.world.World;
import handling.world.MapleGuild;
import handling.world.MapleGuildSkill;
import net.OutPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.StatInfo;
import server.MapleStringInformationProvider;
import server.StructSetItem;
import server.StructSetItem.SetItem;
import server.life.Element;
import server.maps.objects.User;
import server.potentials.ItemPotentialOption;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialStats;
import server.potentials.ItemPotentialType;
import tools.Pair;
import tools.Triple;
import tools.Tuple;
import tools.packet.CField.EffectPacket;
import tools.packet.WvsContext;
import tools.packet.JobPacket;

/**
 * PlayerStat Handler
 * @author Mazen Massoud
 */
public class PlayerStats implements Serializable {

    private static final long serialVersionUID = -679541993413738569L;
    private final List<Triple<Integer, String, Integer>> psdSkills = new ArrayList<>();
    private final Map<Integer, Integer> setHandling = new HashMap<>(), skillsIncrement = new HashMap<>(), damageIncrease = new HashMap<>();
    private final EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
    private final List<Equip> durabilityHandling = new ArrayList<>(), equipLevelHandling = new ArrayList<>();
    private transient float shouldHealHP, shouldHealMP;
    public transient short passive_sharpeye_min_percent, passive_sharpeye_percent, crit_rate;
    private transient byte passive_mastery;
    private transient float localmaxbasedamage, localmaxbasepvpdamage, localmaxbasepvpdamageL;
    public transient boolean equippedWelcomeBackRing, hasClone, Berserk;
    public transient double expBuff, indieExpBuff, dropBuff, mesoBuff, cashBuff, mesoGuard, mesoGuardMeso, standardMagicGuard, magic_guard_rate, expMod, expMod_ElveBlessing, dropMod, pickupRange, dam_r, bossdam_r;
    public transient int recoverHP, recoverMP, mpconReduce, mpconPercent, incMesoProp, reduceCooltime, coolTimeR, suddenDeathR, expLossReduceR, DAMreflect, DAMreflect_rate, ignoreTakenDAMr, ignoreTakenDAMr_rate, ignoreTakenDAM, ignoreTakenDAM_rate,
            hpRecover, hpRecoverProp, hpRecoverPercent, mpRecover, mpRecoverProp, RecoveryUP, BuffUP, RecoveryUP_Skill, BuffUP_Skill,
            incAllskill, combatOrders, ignoreTargetDEF, defRange, BuffUP_Summon, speed, jump, harvestingTool,
            equipmentBonusExp, cashMod, levelBonus, asrR, terR, pickRate, decreaseDebuff, equippedFairy, equippedSummon,
            pvpDamage, hpRecoverTime, mpRecoverTime, dot, dotTime, questBonus, pvpRank, pvpExp, wdef, mdef, trueMastery, damX, damageCapIncrease;
    public transient int avoidability_weapon, avoidability_magic, avoidability_equipment, avoidability_skill, avoidabilityRate;
    public transient int def, element_ice, element_fire, element_light, element_psn;
    public int hp, maxhp, mp, dark_force, maxmp, str, dex, luk, int_;
    
    /*Percent Increase Variables*/
    private transient int percent_hp, percent_mp, percent_str, percent_dex, percent_int, percent_luk, percent_acc, percent_atk, percent_matk, percent_wdef, percent_mdef;

    /*Flat Increase Variables*/
    public transient int localstr, localdex, localluk, localint_, localmaxhp, localmaxmp, magic, watk, hands, accuracy, targetPlus;
    
    /*Temporary HP & MP Variables for OnPassiveRequest*/
    private int localmaxhp_ = 0, localmaxmp_ = 0; 
    
    /*Star Force*/
    public int starForceEnhancement;
    public int starForceClass;
    public float starForceDamageRate;

    // Threads
    private final ReentrantLock reLock = new ReentrantLock();

    private void OnResetLocalStats(final int nJobID) {
        accuracy = 0;
        wdef = 0;
        mdef = 0;
        damX = 0;
        dark_force = 0;
        starForceEnhancement = 0;
        damageCapIncrease = 0; 
        localdex = getDex();
        localint_ = getInt();
        localstr = getStr();
        localluk = getLuk();
        speed = 100;
        jump = 100;
        avoidability_weapon = 0;
        avoidability_magic = 0;
        avoidability_equipment = 0;
        avoidability_skill = 0;
        avoidabilityRate = 0;
        pickupRange = 0.0;
        decreaseDebuff = 0;
        asrR = 0;
        terR = 0;
        dot = 0;
        questBonus = 1;
        dotTime = 0;
        trueMastery = 0;
        percent_wdef = 0;
        percent_mdef = 0;
        percent_hp = 0;
        percent_mp = 0;
        targetPlus = 0;
        percent_str = 0;
        percent_dex = 0;
        percent_int = 0;
        percent_luk = 0;
        percent_acc = 0;
        percent_atk = 0;
        percent_matk = 0;
        crit_rate = 5;
        passive_sharpeye_min_percent = 20;
        passive_sharpeye_percent = 50;
        magic = 0;
        watk = 0;
        pvpDamage = 0;
        mesoGuard = 50.0;
        mesoGuardMeso = 0.0;
        standardMagicGuard = 0.0d;
        magic_guard_rate = 0.0d;
        dam_r = 100.0;
        bossdam_r = 100.0;
        expBuff = 100.0;
        indieExpBuff = 100.0;
        cashBuff = 100.0;
        dropBuff = 100.0;
        mesoBuff = 100.0;
        recoverHP = 0;
        recoverMP = 0;
        mpconReduce = 0;
        mpconPercent = 100;
        incMesoProp = 0;
        reduceCooltime = 0;
        coolTimeR = 0;
        suddenDeathR = 0;
        expLossReduceR = 0;
        DAMreflect = 0;
        DAMreflect_rate = 0;
        ignoreTakenDAMr = 0;
        ignoreTakenDAMr_rate = 0;
        ignoreTakenDAM = 0;
        ignoreTakenDAM_rate = 0;
        ignoreTargetDEF = 0;
        targetPlus = 0;
        hpRecover = 0;
        hpRecoverProp = 0;
        hpRecoverPercent = 0;
        mpRecover = 0;
        mpRecoverProp = 0;
        pickRate = 0;
        equippedWelcomeBackRing = false;
        equippedFairy = 0;
        equippedSummon = 0;
        hasClone = false;
        Berserk = false;
        equipmentBonusExp = 0;
        RecoveryUP = 0;
        BuffUP = 0;
        RecoveryUP_Skill = 0;
        BuffUP_Skill = 0;
        BuffUP_Summon = 0;
        dropMod = 1.0;
        expMod = 1.0;
        expMod_ElveBlessing = 1.0;
        cashMod = 1;
        levelBonus = 0;
        incAllskill = 0;
        combatOrders = 0;
        defRange = isRangedJob(nJobID) ? 200 : 0;
        durabilityHandling.clear();
        equipLevelHandling.clear();
        skillsIncrement.clear();
        damageIncrease.clear();
        setHandling.clear();
        harvestingTool = 0;
        element_fire = 100;
        element_ice = 100;
        element_light = 100;
        element_psn = 100;
        def = 100;
    }

    public void OnCalculateLocalStats(User pPlayer) {
        OnCalculateLocalStats(false, pPlayer);
    }

    /**
     * Re-calculate the player's overall stats when a new buff is casted; equipment is removed/worn; scrolling an item that's equipped
     *
     * NOTE NOTE, IMPORTANT: This function must always be locked via 'reLock', single-threaded... because it may not only be accessed by the
     * player's thread but also Timers and other party members.
     *
     * @param bFirstLogin
     * @param pPlayer
     */
    public void OnCalculateLocalStats(boolean bFirstLogin, User pPlayer) {

        reLock.lock();
        try {
            
            OnResetLocalStats(pPlayer.getJob()); // Reset before recalculating increases.

            final MapleItemInformationProvider pItemProvider = MapleItemInformationProvider.getInstance();
            ItemPotentialOption pPotentialOpt;
            final Map<Skill, SkillEntry> aData = new HashMap<>();
            
            int nOldMaxHP = localmaxhp;
            int nOldMaxMP = localmaxmp;
            localmaxhp_ = getMaxHp(); // Set Temporary HP Variable
            localmaxmp_ = getMaxMp(); // Set Temporary MP Variable

            for (MapleTraitType t : MapleTraitType.values()) {
                pPlayer.getTrait(t).clearLocalExp();
            }
            
            // Loop through all of character's equipped item.
            final Iterator<Item> pIterator = pPlayer.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
            while (pIterator.hasNext()) {
                final Equip pEquip = (Equip) pIterator.next();
                if (pEquip.getPosition() == -11) {
                    if (InventoryConstants.isMagicWeapon(pEquip.getItemId())) {
                        final Map<String, Integer> aEqStat = MapleItemInformationProvider.getInstance().getEquipStats(pEquip.getItemId());
                        if (aEqStat != null) { // Slow, Poison, Darkness, Seal, Freeze
                            for (Map.Entry<String, Integer> aStat : aEqStat.entrySet()) {
                                switch (aStat.getKey()) {
                                    case "incRMAF":
                                        element_fire = aStat.getValue();
                                        break;
                                    case "incRMAI":
                                        element_ice = aStat.getValue();
                                        break;
                                    case "incRMAL":
                                        element_light = aStat.getValue();
                                        break;
                                    case "incRMAS":
                                        element_psn = aStat.getValue();
                                        break;
                                    case "elemDefault":
                                        def = aStat.getValue();
                                        break;
                                }
                            }
                        }
                    }
                }
                
                if ((pEquip.getItemId() / 10000 == 166 && pEquip.getAndroid() != null
                        || pEquip.getItemId() / 10000 == 167) && pPlayer.getAndroid() == null) {
                    final Equip android = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27);
                    final Equip heart = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -28);
                    if (android != null && heart != null) {
                        pPlayer.setAndroid(pEquip.getAndroid());
                    }
                }
                
                pPlayer.getTrait(MapleTraitType.craft).addLocalExp(pEquip.getHands());
                
                starForceEnhancement += pEquip.getEnhance();
                accuracy += pEquip.getAcc();
                avoidability_equipment += pEquip.getAvoid();
                localmaxhp_ += pEquip.getHp();
                localmaxmp_ += pEquip.getMp();
                localdex += pEquip.getDex();
                localint_ += pEquip.getInt();
                localstr += pEquip.getStr();
                localluk += pEquip.getLuk();
                watk += pEquip.getWatk();
                magic += pEquip.getMatk();
                wdef += pEquip.getWdef();
                mdef += pEquip.getMdef();
                speed += pEquip.getSpeed();
                jump += pEquip.getJump();
                pvpDamage += pEquip.getPVPDamage();
                bossdam_r += pEquip.getBossDamage();
                ignoreTargetDEF += pEquip.getIgnorePDR();
                dam_r += pEquip.getTotalDamage();
                percent_str += pEquip.getAllStat();
                percent_dex += pEquip.getAllStat();
                percent_int += pEquip.getAllStat();
                percent_luk += pEquip.getAllStat();
                percent_hp += pEquip.getMHPr();
                percent_mp += pEquip.getMMPr();

                switch (pEquip.getItemId()) {
                    case 1112127:
                        equippedWelcomeBackRing = true;
                        break;
                    case 1122017:
                        equippedFairy = 10;
                        break;
                    case 1122158:
                        equippedFairy = 5;
                        break;
                    case 1112594:
                        equippedSummon = 1085;
                        break;
                    case 1112585:
                        equippedSummon = 1085;
                        break;
                    case 1112586:
                        equippedSummon = 1087;
                        break;
                    case 1112663:
                        equippedSummon = 1179;
                        break;
                    default:
                        for (int nEqBonus : GameConstants.Equipments_Bonus) {
                            if (pEquip.getItemId() == nEqBonus) {
                                //equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                                break;
                            }
                        }
                        break;
                }
                
                final Integer nEquipSetId = pItemProvider.getSetItemID(pEquip.getItemId());
                final Pair<Integer, Integer> nEquipAdditions = handleEquipAdditions(pItemProvider, pPlayer, bFirstLogin, aData, pEquip.getItemId());
               
                if (nEquipSetId != null && nEquipSetId > 0) {
                    if (setHandling.containsKey(nEquipSetId)) {
                        setHandling.put(nEquipSetId, setHandling.get(nEquipSetId) + 1); // ID of Item Set, number of items to go with the set.
                    } else {
                        setHandling.put(nEquipSetId, 1);                                // ID of Item Set, number of items to go with the set.
                    }
                }
                
                if (nEquipAdditions != null) {
                    localmaxhp_ += nEquipAdditions.getLeft();
                    localmaxmp_ += nEquipAdditions.getRight();
                }
                
                if (pEquip.getIncSkill() > 0 && pItemProvider.getEquipSkills(pEquip.getItemId()) != null) {
                    for (final int nEquipSkills : pItemProvider.getEquipSkills(pEquip.getItemId())) {
                        final Skill pSkill = SkillFactory.getSkill(nEquipSkills);
                        if (pSkill != null && pSkill.canBeLearnedBy(pPlayer.getJob())) {
                            int nVal = 1;
                            if (skillsIncrement.get(pSkill.getId()) != null) {
                                nVal += skillsIncrement.get(pSkill.getId());
                            }
                            skillsIncrement.put(pSkill.getId(), nVal);
                        }
                    }
                }
                
                if (!pEquip.getPotentialTier().isHiddenType()) {
                    final int reqLevel = pItemProvider.getReqLevel(pEquip.getItemId());

                    if (reqLevel >= 10) {
                        handleEquipPotentialStats(pPlayer, reqLevel, pEquip.getPotential1());
                        handleEquipPotentialStats(pPlayer, reqLevel, pEquip.getPotential2());
                        handleEquipPotentialStats(pPlayer, reqLevel, pEquip.getPotential3());
                    }
                }
                
                if (pEquip.getDurability() > 0) {
                    durabilityHandling.add((Equip) pEquip);
                }
                if (GameConstants.getMaxLevel(pEquip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(pEquip.getItemId()) == null ? (pEquip.getEquipLevel() <= GameConstants.getMaxLevel(pEquip.getItemId())) : (pEquip.getEquipLevel() < GameConstants.getMaxLevel(pEquip.getItemId())))) {
                    equipLevelHandling.add((Equip) pEquip);
                }
            }

            // Star Force Class Calculation
            if (pPlayer.getMap() != null) {
                final int requiredStarForce = pPlayer.getMap().getSharedMapResources().starForceBarrier;
                this.starForceClass = getStarForceClassByParam(requiredStarForce, starForceEnhancement);
                this.starForceDamageRate = getStarforceAttackRate(starForceClass);
            }

            // Calculate Item Set Additional Stats
            final Iterator<Entry<Integer, Integer>> pIterator2 = setHandling.entrySet().iterator();
            while (pIterator2.hasNext()) {
                final Entry<Integer, Integer> nEntry = pIterator2.next();
                final StructSetItem pSet = pItemProvider.getSetItem(nEntry.getKey());

                if (pSet != null) {
                    final Map<Integer, SetItem> aItems = pSet.getItems();
                    for (Entry<Integer, SetItem> aSetItemStats : aItems.entrySet()) {

                        if (aSetItemStats.getKey() <= nEntry.getValue()) { // key = required amount of set to wear
                            SetItem pSetItem = aSetItemStats.getValue();

                            localstr += pSetItem.incSTR + pSetItem.incAllStat;
                            localdex += pSetItem.incDEX + pSetItem.incAllStat;
                            localint_ += pSetItem.incINT + pSetItem.incAllStat;
                            localluk += pSetItem.incLUK + pSetItem.incAllStat;
                            watk += pSetItem.incPAD;
                            magic += pSetItem.incMAD;
                            speed += pSetItem.incSpeed;
                            accuracy += pSetItem.incACC;
                            localmaxhp_ += pSetItem.incMHP;
                            localmaxmp_ += pSetItem.incMMP;
                            percent_hp += pSetItem.incMHPr;
                            percent_mp += pSetItem.incMMPr;

                            wdef += pSetItem.incPDD;
                            mdef += pSetItem.incMDD;
                        }
                    }
                }
            }
            
            for (Item pItem : pPlayer.getInventory(MapleInventoryType.CASH).newList()) {
                if (pItem.getItemId() / 100000 == 52) {
                    if (expMod < 3 && (pItem.getItemId() == 5211060 || pItem.getItemId() == 5211050 || pItem.getItemId() == 5211051 || pItem.getItemId() == 5211052 || pItem.getItemId() == 5211053 || pItem.getItemId() == 5211054)) {
                        expMod = 3.0; //overwrite
                    } else if (expMod < 2 && (pItem.getItemId() == 5210000 || pItem.getItemId() == 5210001 || pItem.getItemId() == 5210002 || pItem.getItemId() == 5210003 || pItem.getItemId() == 5210004 || pItem.getItemId() == 5210005 || pItem.getItemId() == 5211061 || pItem.getItemId() == 5211000 || pItem.getItemId() == 5211001 || pItem.getItemId() == 5211002 || pItem.getItemId() == 5211003 || pItem.getItemId() == 5211046 || pItem.getItemId() == 5211047 || pItem.getItemId() == 5211048 || pItem.getItemId() == 5211049)) {
                        expMod = 2.0;
                    } else if (expMod < 1.5 && (pItem.getItemId() == 5211068)) {
                        expMod = 1.5;
                    }
                } else if (dropMod == 1.0 && pItem.getItemId() / 10000 == 536) {
                    if (pItem.getItemId() >= 5360000 && pItem.getItemId() < 5360100) {
                        dropMod = 2.0;
                    }
                } else if (pItem.getItemId() == 5710000) {
                    questBonus = 2;
                } else if (pItem.getItemId() == 5590000) {
                    levelBonus += 5;
                }
            }
            
            if (dropMod > 0 && ServerConstants.DOUBLE_TIME) {
                dropMod *= 2.0;
            }
            
            if (expMod > 0 && ServerConstants.DOUBLE_TIME) {
                expMod *= 2.0;
            }
            
            for (Item pItem : pPlayer.getInventory(MapleInventoryType.ETC).list()) {
                switch (pItem.getItemId()) {
                    case 4030003:
                        pickupRange = Double.POSITIVE_INFINITY;
                        break;
                    case 4030004:
                        hasClone = true;
                        break;
                    case 4031864:
                        cashMod = 2;
                        break;
                }
            }
            
            if (bFirstLogin && pPlayer.getLevel() >= 30) {
                if (pPlayer.isGM()) { //!job lol
                    for (int i = 0; i < allJobs.length; i++) {
                        aData.put(SkillFactory.getSkill(1085 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                        aData.put(SkillFactory.getSkill(1087 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                    }
                } else {
                    aData.put(SkillFactory.getSkill(getSkillByJob(1085, pPlayer.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                    aData.put(SkillFactory.getSkill(getSkillByJob(1087, pPlayer.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                }
            }
            
            // add to localmaxhp_ if percentage plays a role in it, else add_hp ?
            OnProfessionToolRequest(pPlayer);
            OnTemporaryStatRequest(pPlayer); 
            
            Integer nBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.EMHP);
            if (nBuff != null) {
                localmaxhp_ += nBuff;
            }
            nBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.EMMP);
            if (nBuff != null) {
                localmaxmp_ += nBuff;
            }
            nBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.IncMaxHP);
            if (nBuff != null) {
                localmaxhp_ += nBuff;
            }
            nBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.IncMaxMP);
            if (nBuff != null) {
                localmaxmp_ += nBuff;
            }
            
            OnPassiveSkillRequest(pPlayer);
            OnHyperPassiveSkillRequest(pPlayer);
            OnHyperStatPassiveRequest(pPlayer);

            if (pPlayer.getGuildId() > 0) {
                final MapleGuild g = World.Guild.getGuild(pPlayer.getGuildId());
                if (g != null && g.getSkills().size() > 0) {
                    final long now = System.currentTimeMillis();
                    for (MapleGuildSkill gs : g.getSkills()) {
                        if (gs.timestamp > now && gs.activator.length() > 0) {
                            final StatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                            crit_rate += e.getCr();
                            watk += e.getAttackX();
                            magic += e.getMagicX();
                            expBuff *= (e.getEXPRate() + 100.0) / 100.0;
                            avoidabilityRate += e.getER();
                            percent_wdef += e.getPDDRate();
                            percent_mdef += e.getMDDRate();
                        }
                    }
                }
            }
            
            for (Pair<Integer, Integer> nCardEffects : pPlayer.getCharacterCard().getCardEffects()) {
                final StatEffect e = SkillFactory.getSkill(nCardEffects.getLeft()).getEffect(nCardEffects.getRight());
                percent_wdef += e.getPDDRate();
                watk += (e.getLevelToWatk() * pPlayer.getLevel());
                percent_hp += e.getPercentHP();
                percent_mp += e.getPercentMP();
                magic += (e.getLevelToMatk() * pPlayer.getLevel());
                RecoveryUP += e.getMPConsumeEff();
                percent_acc += e.getPercentAcc();
                crit_rate += e.getCr();
                jump += e.getPassiveJump();
                speed += e.getPassiveSpeed();
                avoidabilityRate += e.getPercentAvoid();
                damX += (e.getLevelToDamage() * pPlayer.getLevel());
                BuffUP_Summon += e.getSummonTimeInc();
                expLossReduceR += e.getEXPLossRate();
                asrR += e.getASRRate();
                // ignoreMobDamR
                suddenDeathR += e.getSuddenDeathR();
                BuffUP_Skill += e.getBuffTimeRate();
                // onHitHpRecoveryR
                // onHitMpRecoveryR
                coolTimeR += e.getCooltimeReduceR();
                incMesoProp += e.getMesoAcquisition();
                damX += Math.floor((e.getHpToDamage() * nOldMaxHP) / 100.0f);
                damX += Math.floor((e.getMpToDamage() * nOldMaxHP) / 100.0f);
                // finalAttackDamR
                passive_sharpeye_percent += e.getCriticalMax();
                ignoreTargetDEF += e.getIgnoreMob();
                localstr += e.getStrX();
                localdex += e.getDexX();
                localint_ += e.getIntX();
                localluk += e.getLukX();
                localmaxhp_ += e.getMaxHpX();
                localmaxmp_ += e.getMaxMpX();
                watk += e.getAttackX();
                magic += e.getMagicX();
                bossdam_r += e.getBossDamage();
            }

            localstr += Math.floor((localstr * percent_str) / 100.0f);
            localdex += Math.floor((localdex * percent_dex) / 100.0f);
            localint_ += Math.floor((localint_ * percent_int) / 100.0f);
            localluk += Math.floor((localluk * percent_luk) / 100.0f);
            
            if (localint_ > localdex) {
                accuracy += localint_ + Math.floor(localluk * 1.2);
            } else {
                accuracy += localluk + Math.floor(localdex * 1.2);
            }
            
            watk += Math.floor((watk * percent_atk) / 100.0f);
            magic += Math.floor((magic * percent_matk) / 100.0f);
            localint_ += Math.floor((localint_ * percent_matk) / 100.0f);

            wdef += Math.floor((localstr * 1.2) + ((localdex + localluk) * 0.5) + (localint_ * 0.4));
            mdef += Math.floor((localstr * 0.4) + ((localdex + localluk) * 0.5) + (localint_ * 1.2));
            wdef += Math.min(GameConstants.maxWdefMdef,
                    Math.floor((wdef * percent_wdef) / 100.0f));
            mdef += Math.min(GameConstants.maxWdefMdef,
                    Math.floor((wdef * percent_mdef) / 100.0f));

            hands = localdex + localint_ + localluk;
            calculateFame(pPlayer);
            ignoreTargetDEF += pPlayer.getTrait(MapleTraitType.charisma).getLevel() / 10;
            pvpDamage += pPlayer.getTrait(MapleTraitType.charisma).getLevel() / 10;
            asrR += pPlayer.getTrait(MapleTraitType.will).getLevel() / 5;

            // Calculate Accuracy
            accuracy += Math.min(GameConstants.maxAccAvoid,
                    Math.floor((accuracy * percent_acc) / 100.0f));
            accuracy += Math.min(GameConstants.maxAccAvoid,
                    pPlayer.getTrait(MapleTraitType.insight).getLevel() * 15 / 10);

            // Calculate Avoidability
            avoidability_weapon += Math.min(GameConstants.maxAccAvoid,
                    (localdex + localluk + (avoidability_equipment + avoidability_skill)) * (1d + (avoidabilityRate / 100d)));
            avoidability_magic += Math.min(GameConstants.maxAccAvoid,
                    (localint_ + localluk + (avoidability_equipment + avoidability_skill)) * (1d + (avoidabilityRate / 100d)));

            // Calculate HP Percentage Boost
            localmaxhp_ += percent_hp * 0.01f * nOldMaxHP;
            localmaxhp_ += pPlayer.getTrait(MapleTraitType.will).getLevel() * 20;
            localmaxhp = Math.min(GameConstants.maxHP, localmaxhp_);

            // Calculate MP Percentage Boost
            localmaxmp_ += percent_mp * 0.01f * nOldMaxMP;
            localmaxmp_ += pPlayer.getTrait(MapleTraitType.sense).getLevel() * 20;
            localmaxmp = Math.min(GameConstants.maxMP, localmaxmp_);

            if (pPlayer.getEventInstance() != null && pPlayer.getEventInstance().getName().startsWith("PVP")) { // Hack
                StatEffect pEffect;
                localmaxhp = Math.min(40000, localmaxhp * 3); // Approximation
                localmaxmp = Math.min(20000, localmaxmp * 2);
                
                for (int i : pvpSkills) {
                    Skill pSkill = SkillFactory.getSkill(i);
                    if (pSkill != null && pSkill.canBeLearnedBy(pPlayer.getJob())) {
                        aData.put(pSkill, new SkillEntry((byte) 1, (byte) 0, -1));
                        pEffect = pSkill.getEffect(1);
                        switch ((i / 1000000) % 10) {
                            case 1:
                                if (pEffect.getX() > 0) {
                                    pvpDamage += (wdef / pEffect.getX());
                                }
                                break;
                            case 3:
                                hpRecoverProp += pEffect.getProb();
                                hpRecover += pEffect.getX();
                                mpRecoverProp += pEffect.getProb();
                                mpRecover += pEffect.getX();
                                break;
                            case 5:
                                crit_rate += pEffect.getProb();
                                passive_sharpeye_percent = 100;
                                break;
                        }
                        break;
                    }
                }
                
                pEffect = pPlayer.getStatForBuff(CharacterTemporaryStat.Morph);
                if (pEffect != null && pEffect.getSourceId() % 10000 == 1105) { //ice knight
                    localmaxhp = 500000;
                    localmaxmp = 500000;
                }
            }
            
            pPlayer.changeSkillLevelSkip(aData, false);
            
            if (GameConstants.isDemonSlayer(pPlayer.getJob())) {
                localmaxmp = GameConstants.getMPByJob(pPlayer.getJob()) + dark_force;
            } else if (GameConstants.isZero(pPlayer.getJob())) {
                localmaxmp = 100;
            }
            
            if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
                pPlayer.getClient().SendPacket(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
            }
            
            calculateSharpEyePassive(pPlayer);
            calcPassiveMasteryAmount(pPlayer);
            recalcPVPRank(pPlayer);

            if (bFirstLogin) {
                pPlayer.silentEnforceMaxHpMp();
                relocHeal(pPlayer);
            } else {
                pPlayer.enforceMaxHpMp();
            }
            calculateMaxBaseDamage(Math.max(magic, watk), pvpDamage, pPlayer);
            trueMastery = Math.min(100, trueMastery);
            passive_sharpeye_min_percent = (short) Math.min(passive_sharpeye_min_percent, passive_sharpeye_percent);
            if (nOldMaxHP != 0 && nOldMaxHP != localmaxhp) {
                pPlayer.updatePartyMemberHP();
            }
        } finally {
            reLock.unlock();
        }
    }
    
    private void OnPassiveSkillRequest(User pPlayer) {
        Skill pSkill;
        int nSLV;
        StatEffect pEffect;

        psdSkills.clear();
        for (Skill pPsd : pPlayer.getSkills().keySet()) {
            if (pPsd != null && pPsd.getPsd() == 1) {
                Triple<Integer, String, Integer> psdSkill = new Triple<>(0, "", 0);
                psdSkill.left = pPsd.getPsdSkill();
                psdSkill.mid = pPsd.getPsdDamR();         // This only handles damage increases; some skills have effects other than that, so TODO
                psdSkill.mid = pPsd.getPsdtarget();
                psdSkill.right = pPsd.getId();
                psdSkills.add(psdSkill);
            }
        }

        // Warrior
        pSkill = SkillFactory.getSkill(Spearman.PHYSICAL_TRAINING_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Warrior.POWER_STRIKE_1, pEffect.getX());
            damageIncrease.put(Warrior.SLASH_BLAST_1, pEffect.getY());
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Berserker.LORD_OF_DARKNESS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
            hpRecoverProp += pEffect.getProb();
            hpRecoverPercent += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(DarkKnight.BERSERK);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }
        pSkill = SkillFactory.getSkill(Page.PHYSICAL_TRAINING_10_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Warrior.POWER_STRIKE_1, pEffect.getX());
            damageIncrease.put(Warrior.SLASH_BLAST_1, pEffect.getY());
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Paladin.GUARDIAN_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            asrR += pSkill.getEffect(nSLV).getASRRate();
            terR += pSkill.getEffect(nSLV).getTERRate();
        }
        pSkill = SkillFactory.getSkill(Fighter.PHYSICAL_TRAINING_100_10);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Warrior.POWER_STRIKE_1, pEffect.getX());
            damageIncrease.put(Warrior.SLASH_BLAST_1, pEffect.getY());
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Crusader.CHANCE_ATTACK);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }
        pSkill = SkillFactory.getSkill(Hero.COMBAT_MASTERY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            ignoreTargetDEF += pSkill.getEffect(nSLV).getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Hero.ADVANCED_FINAL_ATTACK_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
            damageIncrease.put(Fighter.FINAL_ATTACK_6, (int) pEffect.getDamage());
        }
        pSkill = SkillFactory.getSkill(Warrior.IRON_BODY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localmaxhp_ += pSkill.getEffect(nSLV).getPercentHP();
            def += pSkill.getEffect(nSLV).getPDDX();
            System.out.println(def);
        }
        pSkill = SkillFactory.getSkill(Warrior.WARRIOR_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            // TO-DO: Add Stance
            speed += pSkill.getEffect(nSLV).getPassiveSpeed();
            jump += pSkill.getEffect(nSLV).getPassiveJump();
            localmaxhp_ += pSkill.getEffect(nSLV).getMaxHpPerLevel();
        }
        pSkill = SkillFactory.getSkill(WhiteKnight.SHIELD_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getX();
            percent_mdef += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(Paladin.ACHILLES_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_wdef += pSkill.getEffect(nSLV).getT();
        }
        pSkill = SkillFactory.getSkill(Paladin.ADVANCED_CHARGE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            trueMastery += pSkill.getEffect(nSLV).getMastery();
        }
        pSkill = SkillFactory.getSkill(Berserker.ELEMENTAL_RESISTANCE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            terR += pSkill.getEffect(nSLV).getX();
        }

        // Magician
        pSkill = SkillFactory.getSkill(Magician.MP_BOOST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            final StatEffect MPBoostEffect = pSkill.getEffect(nSLV);
            percent_mp += MPBoostEffect.getPercentMP(); // Max MP increased by 20%
            localmaxmp_ += MPBoostEffect.getMaxMpPerLevel() * pPlayer.getLevel();
            // TODO: +3% Critical Rate  w/ Wand Equipped
        }
        
        // Fire Poison
        pSkill = SkillFactory.getSkill(FirePoisonWizard.HIGH_WISDOM_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(FirePoisonMage.BURNING_MAGIC);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dotTime += pEffect.getX();
            dot += pEffect.getZ();
        }
        pSkill = SkillFactory.getSkill(FirePoisonMage.ELEMENT_AMPLIFICATION_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mpconPercent += pEffect.getX() - 100;
            dam_r += pEffect.getY();
            bossdam_r += pEffect.getY();
        }
        pSkill = SkillFactory.getSkill(FirePoisonArchMage.MIST_ERUPTION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(FirePoisonMage.POISON_MIST, (int) pEffect.getX());
        }
        pSkill = SkillFactory.getSkill(FirePoisonArchMage.IFRIT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            terR += pSkill.getEffect(nSLV).getTERRate();
        }
        pSkill = SkillFactory.getSkill(FirePoisonArchMage.BUFF_MASTERY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            magic += pSkill.getEffect(nSLV).getMagicX();
        }
        pSkill = SkillFactory.getSkill(FirePoisonArchMage.ARCANE_AIM);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getX() * pEffect.getY();
            bossdam_r += pEffect.getX() * pEffect.getY();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }

        // Ice Lightning
        pSkill = SkillFactory.getSkill(IceLightningWizard.HIGH_WISDOM_7);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(IceLightningMage.STORM_MAGIC);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            dot += pSkill.getEffect(nSLV).getZ();
        }
        pSkill = SkillFactory.getSkill(IceLightningMage.ELEMENT_AMPLIFICATION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mpconPercent += pEffect.getX() - 100;
            dam_r += pEffect.getY();
            bossdam_r += pEffect.getY();
        }
        pSkill = SkillFactory.getSkill(IceLightningArchMage.ELQUINES);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            terR += pSkill.getEffect(nSLV).getTERRate();
        }
        pSkill = SkillFactory.getSkill(IceLightningArchMage.BUFF_MASTERY_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            magic += pSkill.getEffect(nSLV).getMagicX();
        }
        pSkill = SkillFactory.getSkill(IceLightningArchMage.ARCANE_AIM_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getX() * pEffect.getY();
            bossdam_r += pEffect.getX() * pEffect.getY();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }

        // Bishop
        pSkill = SkillFactory.getSkill(Cleric.HIGH_WISDOM);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(Priest.HOLY_FOCUS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            crit_rate += pSkill.getEffect(nSLV).getCr();
        }
        pSkill = SkillFactory.getSkill(Bishop.BUFF_MASTERY_5);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            magic += pSkill.getEffect(nSLV).getMagicX();
        }
        pSkill = SkillFactory.getSkill(Bishop.ARCANE_AIM_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getX() * pEffect.getY();
            bossdam_r += pEffect.getX() * pEffect.getY();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }

        // Bowmaster
        pSkill = SkillFactory.getSkill(Archer.ARCHERY_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            defRange += pSkill.getEffect(nSLV).getRange();
        }
        pSkill = SkillFactory.getSkill(Hunter.BOW_MASTERY_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            trueMastery += pSkill.getEffect(nSLV).getMastery();
        }
        pSkill = SkillFactory.getSkill(Hunter.PHYSICAL_TRAINING_8);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localstr += pSkill.getEffect(nSLV).getStrX();
            localdex += pSkill.getEffect(nSLV).getDexX();
        }
        pSkill = SkillFactory.getSkill(Ranger.MARKSMANSHIP_3);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            ignoreTargetDEF += pSkill.getEffect(nSLV).getIgnoreMob();
            watk += pSkill.getEffect(nSLV).getAttackX();
        }
        pSkill = SkillFactory.getSkill(Ranger.FOCUSED_FURY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            asrR += pSkill.getEffect(nSLV).getASRRate();
            terR += pSkill.getEffect(nSLV).getTERRate();
        }
        pSkill = SkillFactory.getSkill(Bowmaster.ADVANCED_FINAL_ATTACK_3);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            watk += pSkill.getEffect(nSLV).getAttackX();
        }
        pSkill = SkillFactory.getSkill(Ranger.EVASION_BOOST_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(Bowmaster.BOW_EXPERT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin(); // TODO: Double check these, Min/Max Crit was removed in v179.
            watk += pSkill.getEffect(nSLV).getX();
        }
        pSkill = SkillFactory.getSkill(Bowmaster.MARKSMANSHIP);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Bowmaster.SPIRIT_LINK_PHOENIX);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0 && pPlayer.getBuffedValue(CharacterTemporaryStat.SpiritLink) != null) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getX();
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }

        // Marksman
        pSkill = SkillFactory.getSkill(Crossbowman.PHYSICAL_TRAINING_70_7);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Archer.ARROW_BLOW, pEffect.getX());
            damageIncrease.put(Archer.DOUBLE_SHOT_1, pEffect.getY());
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Marksman.ULTIMATE_STRAFE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            damageIncrease.put(Sniper.STRAFE_2, pSkill.getEffect(nSLV).getDamage() - 150);
        }
        pSkill = SkillFactory.getSkill(Sniper.EVASION_BOOST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(Marksman.CROSSBOW_EXPERT_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Marksman.MARKSMANSHIP_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Marksman.SPIRIT_LINK_FROSTPREY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0 && pPlayer.getBuffedValue(CharacterTemporaryStat.SpiritLink) != null) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getX();
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }

        // Thief
        pSkill = SkillFactory.getSkill(Thief.HASTE_4);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            speed += pEffect.getSpeed();
        }
            pSkill = SkillFactory.getSkill(Thief.NIMBLE_BODY_2);
            nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localluk += pEffect.getLukX();
        }

        // Night Lord

        pSkill = SkillFactory.getSkill(Assassin.PHYSICAL_TRAINING_50_5);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localluk += pEffect.getLukX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(NightLord.DARK_HARMONY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            ignoreTargetDEF += pEffect.getIgnoreMob();
            watk += pEffect.getAttackX();
        }
        pSkill = SkillFactory.getSkill(Assassin.PHYSICAL_TRAINING_50_5);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localluk += pEffect.getLukX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Hermit.ENVELOPING_DARKNESS_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            asrR += pEffect.getASRRate();
            terR += pEffect.getTERRate();
        }
        pSkill = SkillFactory.getSkill(Hermit.EXPERT_THROWING_STAR_HANDLING);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Thief.LUCKY_SEVEN, pEffect.getDAMRate());
            damageIncrease.put(Assassin.SHURIKEN_BURST_1, pEffect.getDAMRate());
            damageIncrease.put(Assassin.GUST_CHARM_1, pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Hermit.ALCHEMIC_ADRENALINE_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            RecoveryUP += pEffect.getX() - 100;
        }

        // Shadower

        pSkill = SkillFactory.getSkill(Bandit.CHANNEL_KARMA_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
        }
        pSkill = SkillFactory.getSkill(Shadower.DAGGER_EXPERT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
        }
        pSkill = SkillFactory.getSkill(Shadower.BOOMERANG_STAB);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // Savage Blow, Steal, and Assaulter
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Bandit.SAVAGE_BLOW, (int) pEffect.getDAMRate());
            damageIncrease.put(Bandit.STEAL, (int) pEffect.getDAMRate());
            damageIncrease.put(ChiefBandit.PHASE_DASH, (int) pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(ChiefBandit.MESO_MASTERY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mesoBuff *= (pEffect.getMesoRate() + 100.0) / 100.0;
            pickRate += pEffect.getU();
            mesoGuard -= pEffect.getV();
            mesoGuardMeso -= pEffect.getW();
            damageIncrease.put(4211006, pEffect.getX());
        }
        pSkill = SkillFactory.getSkill(ChiefBandit.ENVELOPING_DARKNESS_3);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP() + 27;
            asrR += pEffect.getASRRate();
            terR += pEffect.getTERRate();
        }
        pSkill = SkillFactory.getSkill(Bandit.SHADOW_RESISTANCE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            asrR += pEffect.getASRRate();
            terR += pEffect.getTERRate();
        }
        pSkill = SkillFactory.getSkill(Bandit.SHIELD_MASTERY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getX();
            percent_mdef += pEffect.getX();
            watk += pEffect.getY();
        }

        // Dual Blade
        pSkill = SkillFactory.getSkill(DualBlade.THORNS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(DualBlade.ENVELOPING_DARKNESS_4);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            asrR += pEffect.getASRRate();
            terR += pEffect.getTERRate();
        }
        pSkill = SkillFactory.getSkill(DualBlade.CHANNEL_KARMA_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
        }
        pSkill = SkillFactory.getSkill(DualBlade.LIFE_DRAIN);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            hpRecoverProp += pEffect.getProb();
            hpRecoverPercent += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(DualBlade.FINAL_CUT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // Fatal Blow, Slash Storm, Tornado Spin, Bloody Storm, Upper Stab, and Flying Assaulter
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(DualBlade.FATAL_BLOW, (int) pEffect.getDAMRate());
            damageIncrease.put(DualBlade.SLASH_STORM, (int) pEffect.getDAMRate());
            damageIncrease.put(DualBlade.TORNADO_SPIN_2, (int) pEffect.getDAMRate());
            damageIncrease.put(DualBlade.TORNADO_SPIN_3, (int) pEffect.getDAMRate());
            damageIncrease.put(DualBlade.BLOODY_STORM, (int) pEffect.getDAMRate());
            damageIncrease.put(DualBlade.UPPER_STAB, pEffect.getDAMRate());
            damageIncrease.put(DualBlade.FLYING_ASSAULTER, pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(DualBlade.MIRRORED_TARGET);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(DualBlade.SELF_HASTE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            speed += pEffect.getSpeed();
        }
        pSkill = SkillFactory.getSkill(DualBlade.SHADOW_RESISTANCE_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            asrR += pEffect.getASRRate();
            terR += pEffect.getTERRate();
        }
        pSkill = SkillFactory.getSkill(DualBlade.MIRRORED_TARGET);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getPDDRate();
            percent_mdef += pEffect.getMDDRate();
        }

        // Buccaneer
        pSkill = SkillFactory.getSkill(Marauder.BRAWLING_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // Backspin Blow, Double Uppercut, and Corkscrew Blow
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Brawler.BACKSPIN_BLOW_1, pEffect.getX());
            damageIncrease.put(Brawler.DOUBLE_UPPERCUT_1, pEffect.getY());
            damageIncrease.put(Brawler.CORKSCREW_BLOW_2, pEffect.getZ());
        }
        pSkill = SkillFactory.getSkill(Brawler.PHYSICAL_TRAINING_40_4);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDex();
        }
        pSkill = SkillFactory.getSkill(Brawler.HP_BOOST_5);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getPercentHP();
            //stance prop handled by client?
        }
        pSkill = SkillFactory.getSkill(Buccaneer.PIRATES_REVENGE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            //TODO indieDamR
            damageIncrease.put(Brawler.CORKSCREW_BLOW_2, pEffect.getDAMRate());
            damageIncrease.put(Marauder.SPIRAL_ASSAULT, pEffect.getDAMRate());
        }


        // Corsair
        pSkill = SkillFactory.getSkill(Corsair.ELEMENTAL_BOOST_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // Flamethrower and Ice Splitter
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Outlaw.FLAMETHROWER, pEffect.getDamage());
            damageIncrease.put(Outlaw.ICE_SPLITTER, pEffect.getDamage());
        }
        pSkill = SkillFactory.getSkill(Gunslinger.PHYSICAL_TRAINING_3);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Corsair.MAJESTIC_PRESENCE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
            trueMastery += pEffect.getMastery();
        }
        pSkill = SkillFactory.getSkill(Outlaw.FULLMETAL_JACKET);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDAMRate();
        }
        pSkill = SkillFactory.getSkill(Outlaw.OUTLAWS_CODE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getPDDRate();
            localmaxhp_ += pEffect.getMaxHpX();
            localmaxmp_ += pEffect.getMaxMpX();
        }

        // Cannoneer
        pSkill = SkillFactory.getSkill(CannonMaster.CANNON_OVERLOAD);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDAMRate();
            trueMastery += pEffect.getMastery();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Cannoneer.CANNON_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            trueMastery += pEffect.getMastery();
        }
        pSkill = SkillFactory.getSkill(Cannoneer.CANNON_BOOST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pSkill.getEffect(nSLV).getAttackX();
            def += pEffect.getPDDX();
        }
        pSkill = SkillFactory.getSkill(Cannoneer.PIRATE_TRAINING);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(CannonBlaster.MONKEY_MADNESS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            damageIncrease.put(Cannoneer.BARREL_BOMB, pSkill.getEffect(nSLV).getDAMRate());
        }
        pSkill = SkillFactory.getSkill(CannonBlaster.PIRATE_RUSH);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_hp += pEffect.getHpR();
            percent_wdef += pEffect.getPDDRate();
            asrR += pEffect.getASRRate();
        }
        pSkill = SkillFactory.getSkill(CannonBlaster.REINFORCED_CANNON);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            watk += pSkill.getEffect(nSLV).getAttackX();
        }

        // Jett
        pSkill = SkillFactory.getSkill(Jett.QUICK_MOTION_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            accuracy += pEffect.getAccX();
            jump += pEffect.getPassiveJump();
            speed += pSkill.getEffect(nSLV).getSpeed();
        }
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Jett.PHYSICAL_TRAINING_7);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Jett.HIGH_LIFE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getPDDRate();
            percent_mdef += pEffect.getMDDRate();
            localmaxhp_ += pEffect.getMaxHpX();
            localmaxmp_ += pEffect.getMaxMpX();
        }
        pSkill = SkillFactory.getSkill(Jett.CUTTING_EDGE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Jett.COLLATERAL_DAMAGE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
            passive_sharpeye_percent += pEffect.getCriticalMax();
            bossdam_r += pEffect.getBossDamage();
        }

        // Dawn Warrior
        pSkill = SkillFactory.getSkill(DawnWarrior.HP_BOOST_3);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        // Blaze Wizard
        pSkill = SkillFactory.getSkill(BlazeWizard.MP_BOOST_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_mp += pSkill.getEffect(nSLV).getPercentMP();
        }
        pSkill = SkillFactory.getSkill(BlazeWizard.MAGIC_CRITICAL);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            terR += pSkill.getEffect(nSLV).getX();
        }
        pSkill = SkillFactory.getSkill(BlazeWizard.ELEMENT_AMPLIFICATION_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mpconPercent += pEffect.getX() - 100;
            dam_r += pEffect.getY();
            bossdam_r += pEffect.getY();
        }
        pSkill = SkillFactory.getSkill(BlazeWizard.IFRIT_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            terR += pSkill.getEffect(nSLV).getTERRate();
        }

        // Wind Archer
        pSkill = SkillFactory.getSkill(WindArcher.ARCHERY_MASTERY_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            defRange += pSkill.getEffect(nSLV).getRange();
        }
        pSkill = SkillFactory.getSkill(WindArcher.EVASION_BOOST_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(WindArcher.BOW_EXPERT_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }

        // Night Walker
        pSkill = SkillFactory.getSkill(NightWalker.ALCHEMIST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            RecoveryUP += pEffect.getX() - 100;
            BuffUP += pEffect.getY() - 100;
        }
        pSkill = SkillFactory.getSkill(NightWalker.KEEN_EYES);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            defRange += pSkill.getEffect(nSLV).getRange();
        }

        // Thunder Breaker
        pSkill = SkillFactory.getSkill(ThunderBreaker.HP_BOOST_6);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        //Aran
        pSkill = SkillFactory.getSkill(Aran.HIGH_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Aran.SNOW_CHARGE_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDAMRate();
            bossdam_r += pEffect.getDAMRate();
        }
        pSkill = SkillFactory.getSkill(Aran.FULL_SWING_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            damageIncrease.put(Aran.SMASH_WAVE, pSkill.getEffect(nSLV).getW());
        }
        pSkill = SkillFactory.getSkill(Aran.OVER_SWING);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            damageIncrease.put(Aran.COMBO_FENRIR, pSkill.getEffect(nSLV).getZ());
        }
        pSkill = SkillFactory.getSkill(Aran.SUDDEN_STRIKE_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Aran.FINAL_CHARGE_1, pEffect.getDAMRate());
            damageIncrease.put(Aran.FINAL_TOSS_2, pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Aran.DRAIN);        // Aran Drain grants +% Max HP
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(Aran.HIGH_DEFENSE); // Aran High Defense Grants +% Max HP
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        // Evan
        pSkill = SkillFactory.getSkill(Evan.MAGIC_AMPLIFICATION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mpconPercent += pEffect.getX() - 100;
            dam_r += pEffect.getY();
            bossdam_r += pEffect.getY();
        }
        pSkill = SkillFactory.getSkill(Evan.DRAGON_FURY_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }
        pSkill = SkillFactory.getSkill(Evan.MAGIC_MASTERY); // This is an invisible skill.
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            magic += pEffect.getX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Global.MAGIC_LINK); // High Life
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            //localmaxmp_ += eff.getMaxMpX();
            percent_mp += pEffect.getMpR();
            localmaxmp_ += (120 * pPlayer.getLevel());
        }

        // Mercedes
        pSkill = SkillFactory.getSkill(Mercedes.ELVEN_GRACE);
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            pPlayer.getTrait(MapleTraitType.charm).addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
        }
        pSkill = SkillFactory.getSkill(Mercedes.POTENTIAL_POWER);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(Mercedes.PHYSICAL_TRAINING_6);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(23110004); // TODO: Wrong Buff ID
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getProb();
            damageIncrease.put(Mercedes.RISING_RUSH, pSkill.getEffect(nSLV).getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Mercedes.ANCIENT_WARDING);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getProb();
        }
        pSkill = SkillFactory.getSkill(Mercedes.DUAL_BOWGUNS_EXPERT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            watk += pSkill.getEffect(nSLV).getX();
        }
        pSkill = SkillFactory.getSkill(Mercedes.DEFENSE_BREAK);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            ignoreTargetDEF += pSkill.getEffect(nSLV).getX();
        }
        pSkill = SkillFactory.getSkill(Mercedes.ROLLING_MOONSAULT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            damageIncrease.put(23101001, pSkill.getEffect(nSLV).getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Mercedes.ADVANCED_FINAL_ATTACK);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            watk += pSkill.getEffect(nSLV).getAttackX();
        }
        pSkill = SkillFactory.getSkill(Mercedes.ELVEN_BLESSING_1);
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            this.expMod_ElveBlessing += pSkill.getEffect(nSLV).getX() / 100f;
        }
        if (pPlayer.getBuffedValue(CharacterTemporaryStat.BullsEye) != null) {
            crit_rate += pSkill.getEffect(nSLV).getX();
            passive_sharpeye_min_percent += pSkill.getEffect(nSLV).getX();
            passive_sharpeye_percent += pSkill.getEffect(nSLV).getY();
        }
        
        // Phantom 
        pSkill = SkillFactory.getSkill(Phantom.PHANTOM_INSTINCT_1); // +10% Crit Rate
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Phantom.DEXTEROUS_TRAINING); // +40 DEX, Base Avoidability: +20%
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localdex += pEffect.getDexX();
            avoidabilityRate += pEffect.getER();
        }
        pSkill = SkillFactory.getSkill(Phantom.PHANTOM_SWIFTNESS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            speed += pEffect.getPassiveSpeed();
            jump += pEffect.getPassiveJump();
        }
        pSkill = SkillFactory.getSkill(Phantom.FEATHER_FOOT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            avoidabilityRate += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(Phantom.DEVILS_LUCK);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localluk += pEffect.getLukX();
        }
        pSkill = SkillFactory.getSkill(Phantom.PIERCING_VISION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            crit_rate += pEffect.getCr();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Phantom.FINAL_FEINT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localluk += pEffect.getLukX();
        }

        // TODO: Shade Passives

        // Luminous
        pSkill = SkillFactory.getSkill(Luminous.STANDARD_MAGIC_GUARD);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);

            float percentDamageAffectsMP = pEffect.getX() / 100.0f;
            standardMagicGuard = percentDamageAffectsMP;

        }
        pSkill = SkillFactory.getSkill(Luminous.MANA_WELL);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_mp += pEffect.getPercentMP();
        }

        // Demon Slayer
        mpRecoverProp = 100;
        pSkill = SkillFactory.getSkill(DemonSlayer.HP_BOOST_7);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getHpR();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.BARBED_LASH);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(DemonSlayer.DEMON_LASH, pEffect.getDAMRate());
            damageIncrease.put(DemonSlayer.DEMON_LASH_1, pEffect.getDAMRate());
            damageIncrease.put(DemonSlayer.DEMON_LASH_2, pEffect.getDAMRate());
            damageIncrease.put(DemonSlayer.DEMON_LASH_3, pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.PHYSICAL_TRAINING_10);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.POSSESSED_AEGIS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            avoidabilityRate += pEffect.getX();
            // HACK: shouldn't be here
            hpRecoverPercent += pEffect.getY();
            hpRecoverProp += pEffect.getX();
            //mpRecover += eff.getY(); // handle in takeDamage
            //mpRecoverProp += eff.getX();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.MAX_FURY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            mpRecover += 1;
            mpRecoverProp += pEffect.getProb();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.BINDING_DARKNESS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            ignoreTargetDEF += pSkill.getEffect(nSLV).getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.DEMON_THRASH);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(DemonSlayer.DEMON_LASH, pEffect.getX());
            damageIncrease.put(DemonSlayer.DEMON_LASH_1, pEffect.getX());
            damageIncrease.put(DemonSlayer.DEMON_LASH_2, pEffect.getX());
            damageIncrease.put(DemonSlayer.DEMON_LASH_3, pEffect.getX());
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.BARRICADE_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.FURY_UNLEASHED_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            bossdam_r += pEffect.getBossDamage();
            mpRecover += pEffect.getX();
            mpRecoverProp += pEffect.getBossDamage();
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.DEMONIC_BLOOD);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            pPlayer.getTrait(MapleTraitType.will).addLocalExp(GameConstants.getTraitExpNeededForLevel(pEffect.getY()));
            pPlayer.getTrait(MapleTraitType.charisma).addLocalExp(GameConstants.getTraitExpNeededForLevel(pEffect.getZ()));
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.CURSE_OF_FURY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            hpRecoverPercent += pEffect.getX();
            hpRecoverProp += pEffect.getProb(); //yes
        }
        pSkill = SkillFactory.getSkill(DemonSlayer.MAX_FURY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localmaxmp_ += pEffect.getMaxDemonFury();
        }

        // Demon Avenger
        pSkill = SkillFactory.getSkill(DemonAvenger.MAPLE_WARRIOR_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += nSLV / 2;
        }
        pSkill = SkillFactory.getSkill(DemonAvenger.RAGE_WITHIN);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localmaxhp_ += 100 + (nSLV * 100);
            def += nSLV * 100;
        }

        // Battle Mage
        pSkill = SkillFactory.getSkill(BattleMage.ADVANCED_BLUE_AURA_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            asrR += pSkill.getEffect(nSLV).getASRRate();
            terR += pSkill.getEffect(nSLV).getTERRate();
        }
        pSkill = SkillFactory.getSkill(BattleMage.BATTLE_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDAMRate();
            bossdam_r += pEffect.getDAMRate();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(BattleMage.ADVANCED_DARK_AURA);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            magic += pSkill.getEffect(nSLV).getMagicX();
        }
        pSkill = SkillFactory.getSkill(BattleMage.ADVANCED_YELLOW_AURA);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            avoidabilityRate += pSkill.getEffect(nSLV).getER();
        }
        pSkill = SkillFactory.getSkill(BattleMage.ENERGIZE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(BattleMage.ORDINARY_CONVERSION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(BattleMage.BATTLE_RAGE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
            percent_mp += pSkill.getEffect(nSLV).getPercentMP();
        }
        // Wild Hunter
        pSkill = SkillFactory.getSkill(WildHunter.CROSSBOW_EXPERT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(WildHunter.JAGUAR_BOOST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            dam_r += pEffect.getDamage();
            bossdam_r += pEffect.getDamage();
        }
        pSkill = SkillFactory.getSkill(WildHunter.WILD_INSTINCT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            ignoreTargetDEF += pEffect.getIgnoreMob();
            avoidabilityRate += pEffect.getER();
        }

        // Mechanic
        pSkill = SkillFactory.getSkill(Mechanic.MECHANIC_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            watk += pSkill.getEffect(nSLV).getAttackX();
        }
        pSkill = SkillFactory.getSkill(Mechanic.EXTREME_MECH);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            trueMastery += pSkill.getEffect(nSLV).getMastery();
        }
        pSkill = SkillFactory.getSkill(Mechanic.METAL_FIST_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // ME-07 Drillhands, Atomic Hammer
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Mechanic.ME07_DRILLHANDS, pEffect.getDAMRate());
            damageIncrease.put(Mechanic.ATOMIC_HAMMER, pEffect.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Mechanic.SATELLITE_SAFETY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { // Satellite
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Mechanic.SATELLITE_2, pEffect.getDAMRate());
            //damageIncrease.put(35111009, (int) eff.getDAMRate());
            //damageIncrease.put(35111010, (int) eff.getDAMRate());
        }
        pSkill = SkillFactory.getSkill(Mechanic.ROBOT_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) { //Satellite
            pEffect = pSkill.getEffect(nSLV);
            damageIncrease.put(Mechanic.ACCELERATION_BOT_EX7, pEffect.getX());
            damageIncrease.put(Mechanic.HEALING_ROBOT_HLX, pEffect.getX());
            damageIncrease.put(Mechanic.BOTS_N_TOTS_1, pEffect.getX());
            damageIncrease.put(Mechanic.AMPLIFIER_ROBOT_AF11, pEffect.getX());
            damageIncrease.put(Mechanic.BOTS_N_TOTS, pEffect.getX());
            BuffUP_Summon += pEffect.getY();
        }

        // Xenon
        // Xenon's Multilateral have only one level, so no need to get the effect from the skill.
        // Just give the correct increase based on skill description. -Mazen
        pSkill = SkillFactory.getSkill(Xenon.FORTUNE_10_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            if (pPlayer.getStr() >= 20 && pPlayer.getDex() >= 20 && pPlayer.getLuk() >= 20) {
                percent_atk = +3; // +3%
            }
        }
        pSkill = SkillFactory.getSkill(Xenon.MULTILATERAL_II);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            if (pPlayer.getStr() >= 40 && pPlayer.getDex() >= 40 && pPlayer.getLuk() >= 40) {
                percent_hp += 5; // +5%
                percent_mp += 5; // +5%
            }
        }
        pSkill = SkillFactory.getSkill(Xenon.MULTILATERAL_III);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            if (pPlayer.getStr() >= 90 && pPlayer.getDex() >= 90 && pPlayer.getLuk() >= 90) {
                percent_hp += 5; // +5%
                percent_mp += 5; // +5%
            }
        }
        pSkill = SkillFactory.getSkill(Xenon.MULTILATERAL_IV);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            if (pPlayer.getStr() >= 150 && pPlayer.getDex() >= 150 && pPlayer.getLuk() >= 150) {
                percent_hp += 10; // +10%
                percent_mp += 10; // +10%
            }
        }
        pSkill = SkillFactory.getSkill(Xenon.MULTILATERAL_V);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            if (pPlayer.getStr() >= 150 && pPlayer.getDex() >= 150 && pPlayer.getLuk() >= 150) {
                percent_hp += 10; // +10%
                percent_mp += 10; // +10%
            }
        }
        pSkill = SkillFactory.getSkill(Xenon.MULTILATERAL_VI);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            targetPlus += pSkill.getEffect(nSLV).gettargetPlus();
        }
        pSkill = SkillFactory.getSkill(Xenon.EFFICIENCY_STREAMLINE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            localmaxhp_ += pSkill.getEffect(nSLV).getMaxHpX();
            localmaxmp_ += pSkill.getEffect(nSLV).getMaxMpX();
        }

        // TODO: Blaster

        // Hayato
        pSkill = SkillFactory.getSkill(Hayato.JINSOKU); // Willow Dodge 2
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            avoidabilityRate += pEffect.getPercentAvoid(); //dodgeChance += eff.getER();
        }
        pSkill = SkillFactory.getSkill(Hayato.UNFALTERING_BLADE); // Unfaltering Blade
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            accuracy += pEffect.getX();
            passive_sharpeye_percent += pEffect.getCriticalMax();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Hayato.CENTER_KI_1); // Center Ki
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }

        // Kanna
        pSkill = SkillFactory.getSkill(Kanna.ELEMENTAL_BLESSING);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
            magic += pSkill.getEffect(nSLV).getMagicX();
        }

        // Mihille
        // Mihile 1st Job Passive Skills
        pSkill = SkillFactory.getSkill(Mihile.HP_BOOST);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(Mihile.SOUL_SHIELD);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            percent_wdef += pEffect.getX();
            percent_mdef += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(Mihile.SOUL_DEVOTION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            accuracy += pEffect.getAccX();
            speed += pEffect.getPassiveSpeed();
            jump += pEffect.getPassiveJump();
        }
        // Mihile 2nd Job Passive Skills
        pSkill = SkillFactory.getSkill(Mihile.PHYSICAL_TRAINING_90_9);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
        }
        pSkill = SkillFactory.getSkill(Mihile.ADVANCED_FINAL_ATTACK_1);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getAttackX();
            damageIncrease.put(Mihile.FINAL_ATTACK_5, pEffect.getDamage());
        }
        // Mihile 3rd Job Passive Skills
        pSkill = SkillFactory.getSkill(Mihile.SELF_RECOVERY_2);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            hpRecoverProp += pEffect.getProb();
            hpRecover += pEffect.getX();
            mpRecoverProp += pEffect.getProb();
            mpRecover += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(Mihile.INTENSE_FOCUS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            // Add Attack Speed here
        }
        pSkill = SkillFactory.getSkill(Mihile.RIGHTEOUS_INDIGNATION);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            asrR += pEffect.getX();
            percent_atk += pEffect.getX();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        // Mihile 4th Job Passive Skills
        pSkill = SkillFactory.getSkill(Mihile.COMBAT_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            ignoreTargetDEF += pEffect.getIgnoreMob();
        }
        pSkill = SkillFactory.getSkill(Mihile.EXPERT_SWORD_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pSkill.getEffect(nSLV).getX();
            trueMastery += pEffect.getMastery();
            passive_sharpeye_min_percent += pEffect.getCriticalMin();
        }
        pSkill = SkillFactory.getSkill(Mihile.SOUL_ASYLUM);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_wdef += pSkill.getEffect(nSLV).getT();
        }

        // Kaiser
        pSkill = SkillFactory.getSkill(Kaiser.INNER_BLAZE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(Kaiser.ADVANCED_INNER_BLAZE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        pSkill = SkillFactory.getSkill(Kaiser.IRON_WILL);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        // Angelic Buster
        pSkill = SkillFactory.getSkill(AngelicBuster.MELODY_CROSS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localmaxhp_ += pSkill.getEffect(nSLV).getIndieMHp();
        }
        pSkill = SkillFactory.getSkill(AngelicBuster.BEAUTIFUL_SOUL);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localdex  += pSkill.getEffect(nSLV).getDexX();
        }
        pSkill = SkillFactory.getSkill(AngelicBuster.LOVE_ME_HEART);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localmaxhp_ += pSkill.getEffect(nSLV).getIndieMHp();
        }

        // Zero
        pSkill = SkillFactory.getSkill(Zero.RESOLUTION_TIME);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
            localstr += pSkill.getEffect(nSLV).getStrX();
        }
        pSkill = SkillFactory.getSkill(Zero.REINFORCE_BODY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        // Beast Tamer
        pSkill = SkillFactory.getSkill(BeastTamer.GROWTH_SPURT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
            localluk += pSkill.getEffect(nSLV).getLukX();
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
            percent_mp += pSkill.getEffect(nSLV).getEnhancedMP();
        }
        pSkill = SkillFactory.getSkill(BeastTamer.WELL_FED);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(BeastTamer.RIPPLING_FELINE_MUSCLES);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(BeastTamer.CAT_WIT);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(BeastTamer.KITTY_TREATS);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localmaxhp_ += pSkill.getEffect(nSLV).getIndieMHp();
            localmaxmp_ += pSkill.getEffect(nSLV).getIndieMMp();
        }

        // Kinesis
        pSkill = SkillFactory.getSkill(Kinesis.MENTAL_FORTITUDE);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }
        // Kinesis Second Job
        pSkill = SkillFactory.getSkill(Kinesis.ESP_MASTERY);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            localint_ += pSkill.getEffect(nSLV).getIntX();
        }
        pSkill = SkillFactory.getSkill(Kinesis.MENTAL_STRENGTH);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV > 0) {
            percent_hp += pSkill.getEffect(nSLV).getPercentHP();
        }

        // Global
        pSkill = SkillFactory.getSkill(Global.PIRATE_BLESSING_1);
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            localstr += pEffect.getStrX();
            localdex += pEffect.getDexX();
            localint_ += pEffect.getIntX();
            localluk += pEffect.getLukX();
            percent_hp += pEffect.getHpR();
            percent_mp += pEffect.getMpR();
        }
        pSkill = SkillFactory.getSkill(Global.FURY_UNLEASHED);
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            bossdam_r += pEffect.getBossDamage();
        }
        if (GameConstants.isExplorer(pPlayer.getJob())) {
            pSkill = SkillFactory.getSkill(74);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                levelBonus += pSkill.getEffect(nSLV).getX();
            }

            pSkill = SkillFactory.getSkill(80);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                levelBonus += pSkill.getEffect(nSLV).getX();
            }

            pSkill = SkillFactory.getSkill(10074);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                levelBonus += pSkill.getEffect(nSLV).getX();
            }

            pSkill = SkillFactory.getSkill(10080);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                levelBonus += pSkill.getEffect(nSLV).getX();
            }

            pSkill = SkillFactory.getSkill(110);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                pEffect = pSkill.getEffect(nSLV);
                localstr += pEffect.getStrX();
                localdex += pEffect.getDexX();
                localint_ += pEffect.getIntX();
                localluk += pEffect.getLukX();
                percent_hp += pEffect.getHpR();
                percent_mp += pEffect.getMpR();
            }

            pSkill = SkillFactory.getSkill(10110);
            nSLV = pPlayer.getSkillLevel(pSkill);
            if (nSLV >0) {
                pEffect = pSkill.getEffect(nSLV);
                localstr += pEffect.getStrX();
                localdex += pEffect.getDexX();
                localint_ += pEffect.getIntX();
                localluk += pEffect.getLukX();
                percent_hp += pEffect.getHpR();
                percent_mp += pEffect.getMpR();
            }
        }
        pSkill = SkillFactory.getSkill(GameConstants.getBOF_ForJob(pPlayer.getJob()));
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
            magic += pEffect.getY();
            accuracy += pEffect.getX();
        }
        pSkill = SkillFactory.getSkill(Citizen.POTION_MASTERY_5);
        nSLV = pPlayer.getTotalSkillLevel(pSkill);
        if (nSLV >0) {
            RecoveryUP += pSkill.getEffect(nSLV).getX() - 100;
        }
        pSkill = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(pPlayer.getJob()));
        nSLV = pPlayer.getSkillLevel(pSkill);
        if (nSLV >0) {
            pEffect = pSkill.getEffect(nSLV);
            watk += pEffect.getX();
            magic += pEffect.getY();
            accuracy += pEffect.getZ();
        }
        
    }

    private void OnHyperStatPassiveRequest(User pPlayer) {
        for (int nHyperSkill : MapleSpecialStats.ALL_HYPER_STATS) {
            Skill pSkill = SkillFactory.getSkill(nHyperSkill);
            int nSLV = pPlayer.getSkillLevel(pSkill);

            if (nSLV <= 0) {
                continue;
            }
            
            StatEffect pEffect = pSkill.getEffect(nSLV);

            for (Map.Entry<StatInfo, Integer> aInfo : pEffect.getAllStatInfo().entrySet()) {
                if (aInfo.getValue() <= 0) {
                    continue;
                }
                
                //pPlayer.dropMessage(5, info.getKey().toString() + " " + info.getValue());
                
                switch (aInfo.getKey()) {
                    case strFX:
                        localstr += aInfo.getValue();
                        break;
                    case dexFX:
                        localdex += aInfo.getValue();
                        break;
                    case intFX:
                        localint_ += aInfo.getValue();
                        break;
                    case lukFX:
                        localluk += aInfo.getValue();
                        break;
                    case mmpR:
                        percent_mp += aInfo.getValue();
                        break;
                    case mhpR:
                        percent_hp += aInfo.getValue();
                        break;
                    case MDF:
                        dark_force += aInfo.getValue();
                        break;
                    case psdSpeed:
                        speed += aInfo.getValue();
                        break;
                    case psdJump:
                        jump += aInfo.getValue();
                        break;
                    case cr:
                        this.crit_rate += aInfo.getValue();
                        break;
                    case criticaldamageMin:
                        passive_sharpeye_min_percent += aInfo.getValue();
                        break;
                    case criticaldamageMax:
                        passive_sharpeye_percent += aInfo.getValue();
                        break;
                    case ignoreMobpdpR:
                        ignoreTargetDEF += aInfo.getValue();
                        break;
                    case damR:
                        this.dam_r += aInfo.getValue();
                        break;
                    case bdR:
                        this.bossdam_r += aInfo.getValue();
                        break;
                    case terR: // ELEMENTAL_RESISTANCE_RATE
                        this.terR += aInfo.getValue();
                        break;
                    case asrR: // ABNORMAL_RESISTANCE_RATE
                        this.asrR += aInfo.getValue();
                        break;
                    case stanceProp:
                        break;
                }
            }
        }
    }

    private void OnHyperPassiveSkillRequest(User pPlayer) {
        int nPrefix = pPlayer.getJob() * 10000;
        Skill pSkill;
        int nSLV;
        StatEffect pEffect;
        for (int i = 30; i < 50; i++) {
            int nSkillID = nPrefix + i;
            pSkill = SkillFactory.getSkill(nSkillID);
            nSLV = pPlayer.getSkillLevel(pSkill);

            if (pSkill != null && pSkill.isHyper() && nSLV > 0) {
                pEffect = pSkill.getEffect(nSLV);
                if (pEffect != null) {
                    switch (i) {
                        case 30:
                            localstr += pEffect.getStrX();
                            break;
                        case 31:
                            localdex += pEffect.getDexX();
                            break;
                        case 32:
                            localint_ += pEffect.getIntX();
                            break;
                        case 33:
                            localluk += pEffect.getLukX();
                            break;
                        case 34:
                            crit_rate += pEffect.getCr();
                            break;
                        /*case 35: // Removed v179?
                            accuracy += eff.getAccR(); 
                            break;*/
                        case 36:
                            percent_hp += pEffect.getPercentHP();
                            break;
                        case 37:
                            percent_mp += pEffect.getPercentMP();
                            break;
                        case 38:
                            localmaxmp_ += pEffect.getMaxDemonFury();
                            break;
                        case 39:
                            wdef += pEffect.getPDDX();
                            break;
                        case 40:
                            mdef += pEffect.getMDDX();
                            break;
                        case 41:
                            speed += pEffect.getSpeed();
                            break;
                        case 42:
                            jump += pEffect.getJump();
                            break;
                    }

                    Tuple<String, String, String> skillName = MapleStringInformationProvider.getSkillStringCache().get(nSkillID);

                    if (skillName != null) {
                        int skill = GameConstants.findSkillByName(skillName.get_2().split(" - ")[0], nPrefix, 0);
                        if (skill != 0) {
                            Skill skil = SkillFactory.getSkill(skill);
                            if (skil != null && pPlayer.getSkillLevel(skil) > 0) {
                                if (pEffect.getDAMRate() > 0) {
                                    //skil.getEffect(pPlayer.getSkillLevel(skil)).setDAMRate(eff.getDAMRate());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void OnTemporaryStatRequest(User pPlayer) {
        for (Map.Entry<CharacterTemporaryStat, CharacterTemporaryStatValueHolder> nBuffedVal: pPlayer.getBuffedValuesPlayerStats()) {
            final StatEffect pEffect = nBuffedVal.getValue().effect;
            final Integer nBuff = nBuffedVal.getValue().value;

            if (pEffect == null) {
                continue;
            }

            switch (nBuffedVal.getKey()) {
                case RideVehicle: {
                    if (pEffect.getSourceId() == 33001001) { // Jaguar
                        crit_rate += pEffect.getW();
                        percent_hp += pEffect.getZ();
                    }
                    break;
                }
                case Dice: {
                    percent_wdef += GameConstants.getDiceStat(nBuff, 2);
                    percent_mdef += GameConstants.getDiceStat(nBuff, 2);
                    percent_hp += GameConstants.getDiceStat(nBuff, 3);
                    percent_mp += GameConstants.getDiceStat(nBuff, 3);
                    crit_rate += GameConstants.getDiceStat(nBuff, 4);
                    dam_r += GameConstants.getDiceStat(nBuff, 5);
                    bossdam_r += GameConstants.getDiceStat(nBuff, 5);
                    expBuff *= (GameConstants.getDiceStat(nBuff, 6) + 100.0) / 100.0;
                    break;
                }
                case MagicGuard: {
                    magic_guard_rate += nBuff / 100.0d;
                    break;
                }
                case IncMaxHP: {
                    percent_hp += nBuff;
                    break;
                }
                case IncMaxMP: {
                    percent_mp += nBuff;
                    break;
                }
                case AsrR: {
                    asrR += nBuff;
                    break;
                }
                case TerR: {
                    terR += nBuff;
                    break;
                }
                case Infinity: {
                    percent_matk += nBuff - 1;
                    break;
                }
                case OnixDivineProtection: {
                    avoidabilityRate += nBuff;
                    break;
                }
                case PVPDamage: {
                    pvpDamage += nBuff;
                    break;
                }
                case PvPScoreBonus: {
                    pvpDamage += nBuff;
                    break;
                }
                case IndieBooster: {
                    speed += nBuff;
                    break;
                }
                //case BLUE_AURA: {
                //   percent_wdef += eff.getZ() + eff.getY();
                //  percent_mdef += eff.getZ() + eff.getY();
                // break;
                //}
                case Conversion: {
                    percent_hp += nBuff;
                    break;
                }
                case IndieMHP: {
                    percent_hp += nBuff;
                    break;
                }
                case IndieMMP: {
                    percent_mp += nBuff;
                    break;
                }
                case MaxMP: {
                    percent_mp += nBuff;
                    break;
                }
                //case BUFF_MASTERY: { // idk
                //   BuffUP_Skill += buff;
                //  break;
                //}
                case STR: {
                    localstr += nBuff;
                    break;
                }
                case DEX: {
                    localdex += nBuff;
                    break;
                }
                case LUK: {
                    localint_ += nBuff;
                    break;
                }
                case INT: {
                    localluk += nBuff;
                    break;
                }
                case IndieAllStat: {
                    localstr += nBuff;
                    localdex += nBuff;
                    localint_ += nBuff;
                    localluk += nBuff;
                    break;
                }
                case EPDD: {
                    wdef += nBuff;
                    break;
                }
                case IndiePADR: {
                    wdef += nBuff;
                    break;
                }
                case BasicStatUp: {
                    final double d = nBuff.doubleValue() / 100.0;
                    localstr += d * str; // Base Only
                    localdex += d * dex;
                    localluk += d * luk;
                    localint_ += d * int_;
                    break;
                }
                case MaxLevelBuff: {
                    final double d = nBuff.doubleValue() / 100.0;
                    watk += (int) (watk * d);
                    magic += (int) (magic * d);
                    break;
                }
                case ComboAbilityBuff: {
                    watk += nBuff / 10;
                    break;
                }
                case MesoGuard: {
                    mesoGuardMeso += nBuff.doubleValue();
                    break;
                }
                case ExpBuffRate: {
                    expBuff *= nBuff.doubleValue() / 100.0;
                    break;
                }
                case IndieEXP: {
                    indieExpBuff += nBuff.doubleValue();
                    break;
                }
                case DropRate: {
                    dropBuff *= nBuff.doubleValue() / 100.0;
                    break;
                }
                case ACASH_RATE: {
                    cashBuff *= nBuff.doubleValue() / 100.0;
                    break;
                }
                case MesoUp: {
                    mesoBuff *= nBuff.doubleValue() / 100.0;
                    break;
                }
                case IndiePAD: {
                    watk += nBuff;
                    break;
                }
                case IndieMAD: {
                    magic += nBuff;
                    break;
                }
                case PAD: {
                    watk += nBuff;
                    break;
                }
                case DamR: {
                    dam_r += nBuff;
                    bossdam_r += nBuff;
                    break;
                }
                case EPAD: {
                    watk += nBuff;
                    break;
                }
                case EnergyCharged: {
                    watk += pEffect.getWatk();
                    accuracy += pEffect.getAcc();
                    break;
                }
                case MAD: {
                    magic += nBuff;
                    break;
                }
                case Speed:
                case DashSpeed: {
                    speed += nBuff;
                    break;
                }
                case Jump:
                case DashJump: {
                    jump += nBuff;
                    break;
                }
                case NoDebuff: {
                    crit_rate = 100; //INTENSE
                    asrR = 100; //INTENSE

                    wdef += pEffect.getX();
                    mdef += pEffect.getX();
                    watk += pEffect.getX();
                    magic += pEffect.getX();
                    break;
                }
                case AssistCharge: {
                    dam_r += nBuff.doubleValue();
                    bossdam_r += nBuff.doubleValue();
                    break;
                }
                case FinalCut: {
                    dam_r += nBuff.doubleValue();
                    bossdam_r += nBuff.doubleValue();
                    break;
                }
                case HowlingAttackDamage: {
                    dam_r += nBuff.doubleValue();
                    bossdam_r += nBuff.doubleValue();
                    break;
                }
                case PinkbeanAttackBuff: {
                    dam_r += nBuff.doubleValue();
                    bossdam_r += nBuff.doubleValue();
                    break;
                }
                case Bless: {
                    watk += pEffect.getX();
                    magic += pEffect.getY();
                    accuracy += pEffect.getV();
                    break;
                }
                case Concentration: {
                    mpconReduce += nBuff;
                    break;
                }
                case AdvancedBless: {
                    watk += pEffect.getX();
                    magic += pEffect.getY();
                    accuracy += pEffect.getV();
                    mpconReduce += pEffect.getMPConReduce();
                    break;
                }
                case MagicResistance: {
                    asrR += pEffect.getX();
                    break;
                }
                case ComboCounter: {
                    dam_r += (pEffect.getV() + pEffect.getDAMRate()) * (nBuff - 1);
                    bossdam_r += (pEffect.getV() + pEffect.getDAMRate()) * (nBuff - 1);
                    break;
                }
                case SUMMON: {
                    if (pEffect.getSourceId() == 35121010) { //amp
                        dam_r += pEffect.getX();
                        bossdam_r += pEffect.getX();
                    }
                    break;
                }
                case BMageAura: { // TODO: Handle Colors for Battle Mage Aura Flag
                    dam_r += pEffect.getX();
                    bossdam_r += pEffect.getX();
                    break;
                }
                case Beholder: {
                    trueMastery += pEffect.getMastery();
                    break;
                }
                case Mechanic: {
                    crit_rate += pEffect.getCr();
                    break;
                }
                case PyramidEffect: {
                    dam_r += pEffect.getBerserk();
                    bossdam_r += pEffect.getBerserk();
                    break;
                }
                case WeaponCharge: {
                    dam_r += pEffect.getDamage();
                    bossdam_r += pEffect.getDamage();
                    break;
                }
                case PickPocket: {
                    pickRate = pEffect.getProb();
                    break;
                }
                case BlessingArmor: {
                    watk += pEffect.getEnhancedWatk();
                    break;
                }
                case DarkSight: {
                    dam_r += nBuff;
                    bossdam_r += nBuff;
                    break;
                }
                case Enrage: {
                    dam_r += nBuff;
                    bossdam_r += nBuff;
                    break;
                }
                case CombatOrders: {
                    combatOrders += nBuff;
                    break;
                }
                case SharpEyes: {
                    crit_rate += pEffect.getX();
                    passive_sharpeye_percent += pEffect.getCriticalMax();
                    break;
                }
                case HowlingCritical: {
                    crit_rate += nBuff;
                    break;
                }
                case IncMaxDamage: {
                    damageCapIncrease += nBuff;
                    break;
                }
            }
        }

        if (jump > 123) {
            jump = 123; // Cap Character Speed at Max
        }

        // Map is null when entering CS or Farm. In which case we skip this.
        if (pPlayer.getMap() != null && pPlayer.getMap().getSharedMapResources() != null && pPlayer.getMap().getSharedMapResources().forcedSpeed <= 0) {
            if (speed > 140) {
                speed = 140;
            }
            
            Integer nMonsterRidingBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.RideVehicle);
            if (nMonsterRidingBuff != null) {
                jump = 120;
                switch (nMonsterRidingBuff) {
                    case 1:
                        speed = 150;
                        break;
                    case 2:
                        speed = 170;
                        break;
                    case 3:
                        speed = 180;
                        break;
                    default:
                        speed = 200;
                        break;
                }
            }
        } else if (pPlayer.getMap() != null && pPlayer.getMap().getSharedMapResources() != null) {
            speed = pPlayer.getMap().getSharedMapResources().forcedSpeed; // Used in maps such as Shinsoo School Road
        }
    }

    private void handleEquipPotentialStats(User pPlayer, int nRequiredLevel, int nPotentialID) {
        final ItemPotentialOption pOption = ItemPotentialProvider.getPotentialInfo(nPotentialID);
        if (pOption != null) {
            final List<Pair<ItemPotentialType, ItemPotentialStats>> aStatsForThisEquipLevel = pOption.getSuitableStats(nRequiredLevel);
            if (aStatsForThisEquipLevel == null) {
                return;
            }

            for (Pair<ItemPotentialType, ItemPotentialStats> pStat : aStatsForThisEquipLevel) {
                switch (pStat.getLeft()) {
                    case incSTR:
                        localstr += pStat.getRight().getValue();
                        break;
                    case incSTRr:
                        percent_str += pStat.getRight().getValue();
                        break;
                    case incSTRlv:  // STR per 10 Character Levels: +#incSTRlv"
                        localstr += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incDEX:
                        localdex += pStat.getRight().getValue();
                        break;
                    case incDEXr:
                        percent_dex += pStat.getRight().getValue();
                        break;
                    case incDEXlv:  // DEX per 10 Character Levels: +#incDEXlv
                        localdex += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incINT:
                        localint_ += pStat.getRight().getValue();
                        break;
                    case incINTr:
                        percent_int += pStat.getRight().getValue();
                        break;
                    case incINTlv:
                        localint_ += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incLUK:
                        localluk += pStat.getRight().getValue();
                        break;
                    case incLUKr:
                        percent_luk += pStat.getRight().getValue();
                        break;
                    case incLUKlv:
                        localluk += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incAsrR:   // Abnormal Status = Disease
                        asrR += pStat.getRight().getValue();
                        break;
                    case incTerR:   // Elemental Resistance = Avoid Element Damage from Monster
                        terR += pStat.getRight().getValue();
                        break;
                    case incMHP:
                        localmaxhp_ += pStat.getRight().getValue();
                        break;
                    case incMHPr:
                        percent_hp += pStat.getRight().getValue();
                        break;
                    case incMHPlv:  // HP per 10 Character Levels: +#incMHPlv"/>
                        localmaxhp_ += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incMMP:
                        this.localmaxmp_ += pStat.getRight().getValue();
                        break;
                    case incMMPr:
                        percent_mp += pStat.getRight().getValue();
                        break;
                    case incACC:
                        accuracy += pStat.getRight().getValue();
                        break;
                    case incACCr:   // incEVA -> increase dodge
                        percent_acc += pStat.getRight().getValue();
                        break;
                    case incEVA:
                        break;
                    case incEVAr:
                        avoidabilityRate += pStat.getRight().getValue();
                        break;
                    //
                    case incJump:   // Client Sided
                        jump += pStat.getRight().getValue();
                        break;
                    case incPAD:
                        watk += pStat.getRight().getValue();
                        break;
                    case incPADr:
                        percent_atk += pStat.getRight().getValue();
                        break;
                    case incPADlv:  // ATT per 10 Character Levels: +#incPADlv"/>
                        watk += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incPDD:
                        wdef += pStat.getRight().getValue();
                        break;
                    case incPDDr:
                        percent_wdef += pStat.getRight().getValue();
                        break;
                    case incMAD:
                        magic += pStat.getRight().getValue();
                        break;
                    case incMADr:
                        percent_matk += pStat.getRight().getValue();
                        break;
                    case incMADlv:  // TODO
                        magic += pStat.getRight().getValue() * Math.round(pPlayer.getLevel() / 10);
                        break;
                    case incDAMr:
                        if (pStat.getRight().isBoss()) {
                            bossdam_r += pStat.getRight().getValue();
                        } else {
                            dam_r += pStat.getRight().getValue();
                        }
                        break;
                    case incMDD:
                        mdef += pStat.getRight().getValue();
                        break;
                    case incMDDr:
                        percent_mdef += pStat.getRight().getValue();
                        break;
                    case incCr:     // Critical Rate
                        crit_rate += pStat.getRight().getValue();
                        break;
                    case incAllskill:
                        incAllskill += pStat.getRight().getValue();
                        break;
                    case incMaxDamage:
                        this.damageCapIncrease += pStat.getRight().getValue();
                        break;
                    case incCriticaldamageMin:
                        passive_sharpeye_min_percent += pStat.getRight().getValue();
                    case incCriticaldamageMax:
                        passive_sharpeye_percent += pStat.getRight().getValue();
                        break;
                    case incEXPr:
                        this.expBuff += pStat.getRight().getValue();
                        break;
                    case level:                                                     // Skills
                        break;
                    case incMesoProp:
                        incMesoProp += pStat.getRight().getValue();                 // Mesos + %
                        break;
                    case MP:                                                        // "#prop% chance to recover #HP HP when attacking."/>
                        mpRecover += pStat.getRight().getValue();
                        mpRecoverProp += pStat.getRight().getProbability();
                        break;
                    case HP:
                        hpRecover += pStat.getRight().getValue();
                        hpRecoverProp += pStat.getRight().getProbability();
                        break;
                    case incSpeed:                                                  // Client Sidded
                        speed += pStat.getRight().getValue();
                        break;
                    case incRewardProp:
                        dropBuff += pStat.getRight().getValue();                    // Drop Rate Inc.
                        break;
                    case RecoveryHP:
                        recoverHP += pStat.getRight().getValue();                   // This shouldn't be here, set 4 seconds.
                        break;
                    case RecoveryMP:
                        recoverMP += pStat.getRight().getValue();                   // This shouldn't be here, set 4 seconds.
                        break;
                    case ignoreDAM:
                        ignoreTakenDAM += pStat.getRight().getValue();
                        ignoreTakenDAM_rate += pStat.getRight().getProbability();
                        break;
                    case ignoreDAMr:
                        ignoreTakenDAMr += pStat.getRight().getValue();
                        ignoreTakenDAMr_rate += pStat.getRight().getProbability();
                        break;
                    case ignoreTargetDEF:
                        ignoreTargetDEF += pStat.getRight().getValue();
                        break;
                    case DAMreflect:
                        DAMreflect += pStat.getRight().getValue();
                        DAMreflect_rate += pStat.getRight().getProbability();
                        break;
                    case mpconReduce:
                        mpconReduce += pStat.getRight().getValue();
                        break;
                    case RecoveryUP:
                        RecoveryUP += pStat.getRight().getValue();                  // Only for hp items and skills
                        break;
                    case mpRestore:                                                 // TODO
                    case time:                                                      // Client Sided
                        break;
                    case reduceCooltime:
                        reduceCooltime += pStat.getRight().getValue();              // tSeconds
                        break;
                    default:
                        break;
                }
            }
        }

        /* bossdam_r *= (soc.get("bdR") + 100.0) / 100.0;
        ignoreTargetDEF *= (soc.get("imdR") + 100.0) / 100.0;*/
        // TODO: Auto Steal potentials (modify handleSteal), potentials with invincible stuffs, abnormal status duration decrease,
        // poison, stun, etc (uses level field -> cast disease to mob/player), face?
    }

    public List<Triple<Integer, String, Integer>> getPsdSkills() {
        return psdSkills;
    }

    /**
     * Get the current player's star force class int __thiscall CUserLocal::GetStarForceClass(CUserLocal *this)
     *
     * @param requiredStarForce
     * @param playerStarForce
     * @return
     */
    private static int getStarForceClassByParam(int requiredStarForce, int playerStarForce) {
        int result;
        if (requiredStarForce > 0) {
            int percentage = (int) (100 * ((float) playerStarForce / (float) requiredStarForce));

            if (percentage >= 100) {
                return 6;//5;
            } else if (percentage >= 70) {
                result = 5;//5;
            } else if (percentage >= 50) {
                result = 4;//3
            } else if (percentage >= 30) {
                result = 3;//2;
            } else if (percentage >= 10) {
                result = 2;//1;
            } else {
                result = 0;
            }
        } else {
            result = 5;
        }
        return result;
    }

    /**
     * Calculates the stack damage rate the player is allowed to attack, according to its star force vs map barrier req long double __cdecl
     * GetStarForceDamageRate(int nStartForceClass)
     *
     * @param starForceClass
     * @return
     */
    private static float getStarforceAttackRate(int starForceClass) {
        /*Non star force map = 480k
        
        [0%] 0/25 star force = 1
        [20%] 5/25 star force = 70k -> 0.1458333333333333
        [28%] 7/25 star force = 70k -> 0.1458333333333333

        [32%] 8/25 star force = 200k -> 0.4166666666666667
        [36%] 9/25 star force = 200k -> 0.4166666666666667
        [40%] 10/25 star force = 200k -> 0.4166666666666667
        [44%] 11/25 star force = 200k -> 0.4166666666666667
        [48%] 12/25 star force = 200k -> 0.4166666666666667
        [52%] 13/25 star force = 200k -> 0.4166666666666667

        [56%] 14/25 star force = 350k -> 0.7291
        [60%] 15/25 star force = 350k -> 0.729166
        [64%] 16/25 star force = 350k -> 0.7291
        [68%] 17/25 star force = 350k -> 0.7291

        [72%] 18/25 star force = 480k -> 1
        [76%] 19/25 star force = 480k -> 1
        [80%] 20/25 star force = 480k -> 1
        [96%] 24/25 star force = 480k -> 1

        [100%] 25/25 star force = 700k -> 1.45
        [200%] 50/25 star force = 700k -> 1.45
        [400%] 100/25 star force = 700k -> 1.45 */

        switch (starForceClass) {
            case 0:
                return 0.0f;
            case 1:
                return 0.1f;
            case 2:
                return 0.3f;
            case 3:
                return 0.5f;
            case 4:
                return 0.7f;
            case 5:
                return 1.0f;
            case 6: // this is removed n the version after the KMST leak.. idk which..
                return 1.5f;
        }
        return 1;
    }

    public boolean checkEquipLevels(final User pPlayer, long nGain) {
        boolean changed = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(Math.min(eq.getItemEXP() + nGain, Long.MAX_VALUE));

            if (eq.getEquipLevel() > lvlz) { //lvlup
                for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                    //now for the equipment increments...
                    final Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    int extra = eq.getYggdrasilWisdom();
                    switch (extra) {
                        case 1:
                            inc.get(lvlz + i).put("STRMin", 1);
                            inc.get(lvlz + i).put("STRMax", 3);
                            break;
                        case 2:
                            inc.get(lvlz + i).put("DEXMin", 1);
                            inc.get(lvlz + i).put("DEXMax", 3);
                            break;
                        case 3:
                            inc.get(lvlz + i).put("INTMin", 1);
                            inc.get(lvlz + i).put("INTMax", 3);
                            break;
                        case 4:
                            inc.get(lvlz + i).put("LUKMin", 1);
                            inc.get(lvlz + i).put("LUKMax", 3);
                            break;
                        default:
                            break;
                    }
                    if (inc != null && inc.containsKey(lvlz + i)) { //flair = 1
                        eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                    }
                    //UGH, skillz
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) == null && GameConstants.getMaxLevel(eq.getItemId()) < (lvlz + i) && Math.random() < 0.1 && eq.getIncSkill() <= 0 && ii.getEquipSkills(eq.getItemId()) != null) {
                        for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                            final Skill skil = SkillFactory.getSkill(zzz);
                            if (skil != null && skil.canBeLearnedBy(pPlayer.getJob())) { //dont go over masterlevel :D
                                eq.setIncSkill(skil.getId());

                                Tuple<String, String, String> skillName = MapleStringInformationProvider.getSkillStringCache().get(zzz);
                                if (skillName != null) {
                                    pPlayer.dropMessage(5, "Your skill has gained a levelup: " + skillName.get_2() + " +1");
                                }
                            }
                        }
                    }
                }
                changed = true;
            }
            pPlayer.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            pPlayer.equipChanged();
            pPlayer.getClient().SendPacket(EffectPacket.showForeignEffect(EffectPacket.UserEffectCodes.ItemLevelup));
            pPlayer.getMap().broadcastPacket(pPlayer, EffectPacket.showForeignEffect(pPlayer.getId(), EffectPacket.UserEffectCodes.ItemLevelup), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(final User pPlayer, int gain) {
        return checkEquipDurabilitys(pPlayer, gain, false);
    }

    public boolean checkEquipDurabilitys(final User pPlayer, int gain, boolean aboveZero) {
        if (pPlayer.inPVP()) {
            return true;
        }
        List<Equip> all = new ArrayList<>(durabilityHandling);
        for (Equip item : all) {
            if (item != null && ((item.getPosition() >= 0) == aboveZero)) {
                item.setDurability(item.getDurability() + gain);
                if (item.getDurability() < 0) { //shouldnt be less than 0
                    item.setDurability(0);
                }
            }
        }
        for (Equip eqq : all) {
            if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) { //> 0 went to negative
                if (pPlayer.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    List<ModifyInventory> mod = new ArrayList<>();
                    pPlayer.getClient().SendPacket(WvsContext.inventoryOperation(true, mod));
                    return false;
                }
                durabilityHandling.remove(eqq);
                final short pos = pPlayer.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(pPlayer.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                pPlayer.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }

    private void calculateSharpEyePassive(final User pPlayer) {
        Skill critSkill;
        int critlevel;
        if (GameConstants.isDemonSlayer(pPlayer.getJob())) {
            critSkill = SkillFactory.getSkill(DemonSlayer.DEADLY_CRITS);
            critlevel = pPlayer.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isMercedes(pPlayer.getJob())) {
            critSkill = SkillFactory.getSkill(Mercedes.DEADLY_CRITS_4);
            critlevel = pPlayer.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isResistance(pPlayer.getJob())) {
            critSkill = SkillFactory.getSkill(Citizen.DEADLY_CRITS_5);
            critlevel = pPlayer.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        }
        switch (pPlayer.getJob()) { // Apply passive Critical bonus
            case 410: // Assasin
            case 411: // Hermit
            case 412: { // Night Lord
                critSkill = SkillFactory.getSkill(Assassin.CRITICAL_THROW); // Critical Throw
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2412: { // Phantom
                critSkill = SkillFactory.getSkill(Phantom.CANE_EXPERT);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 1410:
            case 1411:
            case 1412: { // Night Walker
                critSkill = SkillFactory.getSkill(NightWalker.CRITICAL_THROW_1);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3100:
            case 3110:
            case 3111:
            case 3112: {
                critSkill = SkillFactory.getSkill(DemonSlayer.OUTRAGE); //TODO LEGEND, not final
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 2300:
            case 2310:
            case 2311:
            case 2312: {
                critSkill = SkillFactory.getSkill(Mercedes.SHARP_AIM);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3210:
            case 3211:
            case 3212: {
                critSkill = SkillFactory.getSkill(BattleMage.STAFF_MASTERY);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 434: {
                critSkill = SkillFactory.getSkill(DualBlade.SHARPNESS);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 520:
            case 521:
            case 522: {
                critSkill = SkillFactory.getSkill(Gunslinger.CRITICAL_SHOT_1);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1211:
            case 1212: {
                critSkill = SkillFactory.getSkill(BlazeWizard.MAGIC_CRITICAL);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 530:
            case 531:
            case 532: {
                critSkill = SkillFactory.getSkill(Cannoneer.CRITICAL_FIRE);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 510:
            case 511:
            case 512: { // Buccaner, Viper
                critSkill = SkillFactory.getSkill(Marauder.STUN_MASTERY);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) critSkill.getEffect(critlevel).getProb();
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                //final Skill critSkill2 = SkillFactory.getSkill(5100008);
                // final int critlevel2 = player.getTotalSkillLevel(critSkill);
                //  if (critlevel2 > 0) {
                //     this.passive_sharpeye_rate += (short) critSkill2.getEffect(critlevel2).getCr();
                //     this.passive_sharpeye_min_percent += critSkill2.getEffect(critlevel2).getCriticalMin();
                // }
                return;
            }
            case 1511:
            case 1512: {
                critSkill = SkillFactory.getSkill(ThunderBreaker.CRITICAL_PUNCH_2);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2111:
            case 2112: {
                critSkill = SkillFactory.getSkill(Aran.ADVANCED_COMBO_ABILITY);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) ((critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getY()) + critSkill.getEffect(critlevel).getCr());
                }
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322: { // Bowman
                critSkill = SkillFactory.getSkill(Archer.CRITICAL_SHOT);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: { // Wind Archer
                critSkill = SkillFactory.getSkill(WindArcher.CRITICAL_SHOT_3);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: { //Evan
                critSkill = SkillFactory.getSkill(Evan.CRITICAL_MAGIC_);
                critlevel = pPlayer.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
        }
    }

    private void calcPassiveMasteryAmount(final User pPlayer) {
        if (pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11) == null) {
            passive_mastery = 0;
            return;
        }
        final int skil;
        final MapleWeaponType weaponType = GameConstants.getWeaponType(pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11).getItemId());
        boolean acc = true;
        switch (weaponType) {
            case BOW:
                skil = GameConstants.isCygnusKnight(pPlayer.getJob()) ? WindArcher.BOW_MASTERY : Hunter.BOW_MASTERY_2;
                break;
            case CLAW:
                skil = Assassin.CLAW_MASTERY;
                break;
            case CANE:
                skil = pPlayer.getTotalSkillLevel(Phantom.CANE_EXPERT) > 0 ? Phantom.CANE_EXPERT : Phantom.CANE_MASTERY;
                break;
            case CANNON:
                skil = Cannoneer.CANNON_MASTERY;
                break;
            case KATARA:
            case DAGGER:
                skil = pPlayer.getJob() >= 430 && pPlayer.getJob() <= 434 ? DualBlade.KATARA_MASTERY : Bandit.DAGGER_MASTERY;
                break;
            case CROSSBOW:
                skil = GameConstants.isResistance(pPlayer.getJob()) ? WildHunter.CROSSBOW_MASTERY_1 : Crossbowman.CROSSBOW_MASTERY;
                break;
            case AXE1H:
            case BLUNT1H:
                skil = GameConstants.isResistance(pPlayer.getJob()) ? DemonSlayer.WEAPON_MASTERY_1 : (GameConstants.isCygnusKnight(pPlayer.getJob()) ? DawnWarrior.SWORD_MASTERY : (pPlayer.getJob() > 112 ? Page.WEAPON_MASTERY_2 : Fighter.WEAPON_MASTERY_3)); //hero/pally
                break;
            case AXE2H:
            case SWORD1H:
            case SWORD2H:
            case BLUNT2H:
                skil = GameConstants.isCygnusKnight(pPlayer.getJob()) ? DawnWarrior.SWORD_MASTERY : (pPlayer.getJob() > 112 ? Page.WEAPON_MASTERY_2 : Fighter.WEAPON_MASTERY_3);
                break;
            case POLE_ARM:
                skil = GameConstants.isAran(pPlayer.getJob()) ? Aran.POLEARM_MASTERY : Spearman.WEAPON_MASTERY;
                break;
            case SPEAR:
                skil = Spearman.WEAPON_MASTERY;
                break;
            case KNUCKLE:
                skil = GameConstants.isCygnusKnight(pPlayer.getJob()) ? ThunderBreaker.KNUCKLE_MASTERY_3 : Brawler.KNUCKLE_MASTERY_1;
                break;
            case GUN:
                skil = GameConstants.isResistance(pPlayer.getJob()) ? Mechanic.MECHANIC_MASTERY : (GameConstants.isJett(pPlayer.getJob()) ? Jett.GUN_MASTERY_2 : Gunslinger.GUN_MASTERY);
                break;
            case DUAL_BOW:
                skil = Mercedes.DUAL_BOWGUNS_MASTERY;
                break;
            case WAND:
            case STAFF:
                acc = false;
                skil = GameConstants.isResistance(pPlayer.getJob()) ? BattleMage.STAFF_MASTERY : (pPlayer.getJob() <= 212 ? FirePoisonWizard.SPELL_MASTERY_2 : (pPlayer.getJob() <= 222 ? IceLightningWizard.SPELL_MASTERY_6 : (pPlayer.getJob() <= 232 ? Cleric.SPELL_MASTERY : (pPlayer.getJob() <= 2000 ? BlazeWizard.SPELL_MASTERY_3 : Evan.SPELL_MASTERY_4))));
                break;
            default:
                passive_mastery = 0;
                return;

        }
        if (pPlayer.getSkillLevel(skil) <= 0) {
            passive_mastery = 0;
            return;
        }// TODO: add job id check above skill, etc
        final StatEffect eff = SkillFactory.getSkill(skil).getEffect(pPlayer.getTotalSkillLevel(skil));
        if (acc) {
            accuracy += eff.getX();
            if (skil == Mechanic.MECHANIC_MASTERY) {
                watk += eff.getX();
            }
        } else {
            magic += eff.getX();
        }
        crit_rate += eff.getCr();
        passive_mastery = (byte) eff.getMastery();
        trueMastery += eff.getMastery() + weaponType.getBaseMastery();
        if (pPlayer.getJob() == 412) {
            final Skill bx = SkillFactory.getSkill(NightLord.CLAW_EXPERT); // Claw Expert
            final int bof = pPlayer.getTotalSkillLevel(bx);
            if (bof > 0) {
                final StatEffect eff2 = bx.getEffect(bof);
                passive_mastery = (byte) eff2.getMastery(); // Override
                accuracy += eff2.getPercentAcc();
                avoidabilityRate += eff2.getPercentAvoid();
                watk += eff2.getX();
                trueMastery -= eff.getMastery(); // - old
                trueMastery += eff2.getMastery(); // add new
            }
        }
    }

    private void calculateFame(final User pPlayer) {
        pPlayer.getTrait(MapleTraitType.charm).addLocalExp(pPlayer.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            pPlayer.getTrait(t).recalcLevel();
        }
    }

    public final short passive_sharpeye_min_percent() {
        return passive_sharpeye_min_percent;
    }

    public final short passive_sharpeye_percent() {
        return passive_sharpeye_percent;
    }

    public final short passive_sharpeye_rate() {
        return crit_rate;
    }

    public final byte passive_mastery() {
        return passive_mastery; //* 5 + 10 for mastery %
    }

    public final void calculateMaxBaseDamage(final int watk, final int pvpDamage, User pPlayer) {
        if (watk <= 0) {
            localmaxbasedamage = 1;
            localmaxbasepvpdamage = 1;
        } else {
            final Item weapon_item = pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            final Item weapon_item2 = pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
            final int job = pPlayer.getJob();
            final MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.NOT_A_WEAPON : GameConstants.getWeaponType(weapon_item.getItemId());
            final MapleWeaponType weapon2 = weapon_item2 == null ? MapleWeaponType.NOT_A_WEAPON : GameConstants.getWeaponType(weapon_item2.getItemId());
            int mainstat, secondarystat, mainstatpvp, secondarystatpvp;
            boolean mage = GameConstants.isMage(job);
            switch (weapon) {
                case BOW:
                case CROSSBOW:
                case GUN:
                    mainstat = localdex;
                    secondarystat = localstr;
                    mainstatpvp = dex;
                    secondarystatpvp = str;
                    break;
                case DAGGER:
                case KATARA:
                case CLAW:
                case CANE:
                    mainstat = localluk;
                    secondarystat = localdex + localstr;
                    mainstatpvp = luk;
                    secondarystatpvp = dex + str;
                    break;
                default:
                    if (mage) {
                        mainstat = localint_;
                        secondarystat = localluk;
                        mainstatpvp = int_;
                        secondarystatpvp = luk;
                    } else {
                        mainstat = localstr;
                        secondarystat = localdex;
                        mainstatpvp = str;
                        secondarystatpvp = dex;
                    }
                    break;
            }
            localmaxbasepvpdamage = weapon.getMaxDamageMultiplier() * (4 * mainstatpvp + secondarystatpvp) * (100.0f + (pvpDamage / 100.0f));
            localmaxbasepvpdamageL = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (100.0f + (pvpDamage / 100.0f));
            if (weapon2 != MapleWeaponType.NOT_A_WEAPON && weapon_item != null && weapon_item2 != null) {
                Equip we1 = (Equip) weapon_item;
                Equip we2 = (Equip) weapon_item2;
                localmaxbasedamage = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we2.getMatk() : we2.getWatk())) / 100.0f);
                localmaxbasedamage += weapon2.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we1.getMatk() : we1.getWatk())) / 100.0f);
            } else {
                localmaxbasedamage = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (watk / 100.0f);
            }
        }
    }

    public final float getHealHP() {
        return shouldHealHP;
    }

    public final float getHealMP() {
        return shouldHealMP;
    }

    public final void relocHeal(User pPlayer) {
        final int playerjob = pPlayer.getJob();

        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = /*GameConstants.isDemonSlayer(pPlayer.getJob()) ? 0 : */(3 + recoverMP + (localint_ / 10)); // i think
        mpRecoverTime = 0;
        hpRecoverTime = 0;
        if (playerjob == 111 || playerjob == 112) {
            final Skill effect = SkillFactory.getSkill(Crusader.SELF_RECOVERY_4);
            final int lvl = pPlayer.getSkillLevel(effect);
            if (lvl > 0) {
                StatEffect eff = effect.getEffect(lvl);
                if (eff.getHp() > 0) {
                    shouldHealHP += eff.getHp();
                    hpRecoverTime = 4000;
                }
                shouldHealMP += eff.getMp();
                mpRecoverTime = 4000;
            }

        } else if (playerjob == 1111 || playerjob == 1112) {
            final Skill effect = SkillFactory.getSkill(DawnWarrior.SELF_RECOVERY_3);
            final int lvl = pPlayer.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
                mpRecoverTime = 4000;
            }
        } else if (GameConstants.isMercedes(playerjob)) {
            final Skill effect = SkillFactory.getSkill(Mercedes.ELVEN_HEALING);
            final int lvl = pPlayer.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += (effect.getEffect(lvl).getX() * localmaxhp) / 100;
                hpRecoverTime = 4000;
                shouldHealMP += (effect.getEffect(lvl).getX() * localmaxmp) / 100;
                mpRecoverTime = 4000;
            }
        } else if (GameConstants.isJett(playerjob) && playerjob != 508) {
            final Skill effect = SkillFactory.getSkill(Jett.PERSEVERANCE);
            final int lvl = pPlayer.getSkillLevel(effect);
            if (lvl > 0) {
                final StatEffect eff = effect.getEffect(lvl);
                shouldHealHP += eff.getX();
                shouldHealMP += eff.getX();
                hpRecoverTime = eff.getY();
                mpRecoverTime = eff.getY();
            }
        } else if (playerjob == 3111 || playerjob == 3112) {
            final Skill effect = SkillFactory.getSkill(DemonSlayer.MAX_FURY); // Improving MP Recovery
            final int lvl = pPlayer.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getY();
                mpRecoverTime = 4000;
            }
        }
        if (pPlayer.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else if (pPlayer.getMap() != null) { // Because Heal isn't multipled when there's a chair :)
            final float recvRate = pPlayer.getMap().getSharedMapResources().recoveryRate;
            if (recvRate > 0) {
                shouldHealHP *= recvRate;
                shouldHealMP *= recvRate;
            }
        }
    }

    public void connectData(OutPacket oPacket) {
        oPacket.EncodeShort(str);
        oPacket.EncodeShort(dex);
        oPacket.EncodeShort(int_);
        oPacket.EncodeShort(luk);
        oPacket.EncodeInt(hp);
        oPacket.EncodeInt(maxhp);
        oPacket.EncodeInt(mp);
        oPacket.EncodeInt(maxmp);
    }

    private final static int[] allJobs = {0, 10000, 10000000, 20000000, 20010000, 20020000, 20030000, 20040000, 30000000, 30010000, 50000000};
    public final static int[] pvpSkills = {1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};

    public static int getSkillByJob(final int skillID, final int job) { //test
        return skillID + (GameConstants.getBeginnerJob((short) job) * 10000);
    }

    public final int getSkillIncrement(final int skillID) {
        if (skillsIncrement.containsKey(skillID)) {
            return skillsIncrement.get(skillID);
        }
        return 0;
    }

    public final int getElementBoost(final Element key) {
        if (elemBoosts.containsKey(key)) {
            return elemBoosts.get(key);
        }
        return 0;
    }

    public final int getDamageIncrease(final int key) {
        if (damageIncrease.containsKey(key)) {
            return damageIncrease.get(key) + damX;
        }
        return damX;
    }

    public final int getAccuracy() {
        return accuracy;
    }

    public void heal_noUpdate(User pPlayer) {
        setHp(getCurrentMaxHp(), pPlayer);
        setMp(getCurrentMaxMp(pPlayer.getJob()), pPlayer);
    }

    public void heal(User pPlayer) {
        heal_noUpdate(pPlayer);
        pPlayer.updateSingleStat(Stat.HP, getCurrentMaxHp());
        pPlayer.updateSingleStat(Stat.MP, getCurrentMaxMp(pPlayer.getJob()));
    }

    public Pair<Integer, Integer> handleEquipAdditions(MapleItemInformationProvider ii, User pPlayer, boolean first_login, Map<Skill, SkillEntry> sData, final int itemId) {
        final List<Triple<String, String, String>> additions = ii.getEquipAdditions(itemId);
        if (additions == null) {
            return null;
        }
        int localmaxhp_x = 0, localmaxmp_x = 0;
        int skillid = 0, skilllevel = 0;
        String craft, job, level;
        for (final Triple<String, String, String> add : additions) {
            if (add.getMid().contains("con")) {
                continue;
            }
            final int right = Integer.parseInt(add.getRight());
            switch (add.getLeft()) {
                case "elemboost":
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("elemVol") && (craft == null || craft != null && pPlayer.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        int value = Integer.parseInt(add.getRight().substring(1, add.getRight().length()));
                        final Element key = Element.getFromChar(add.getRight().charAt(0));
                        if (elemBoosts.get(key) != null) {
                            value += elemBoosts.get(key);
                        }
                        elemBoosts.put(key, value);
                    }
                    break;
                case "mobcategory": //skip the category, thinkings too expensive to have yet another Map<Integer, Integer> for damage calculations
                    if (add.getMid().equals("damage")) {
                        dam_r += right;
                        bossdam_r += right;
                    }
                    break;
                case "critical": // lv critical lvl?
                    boolean canJob = false,
                     canLevel = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (pPlayer.getJob() == Integer.parseInt(x)) {
                                    canJob = true;
                                }
                            }
                        } else if (pPlayer.getJob() == Integer.parseInt(job)) {
                            canJob = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null) {
                        if (pPlayer.getLevel() >= Integer.parseInt(level)) {
                            canLevel = true;
                        }
                    }
                    if ((job != null && canJob || job == null) && (level != null && canLevel || level == null)) {
                        switch (add.getMid()) {
                            case "prob":
                                crit_rate += right;
                                break;
                            case "damage":
                                passive_sharpeye_min_percent += right;
                                passive_sharpeye_percent += right; //???CONFIRM - not sure if this is max or minCritDmg
                                break;
                        }
                    }
                    break;
                case "boss": // ignore prob, just add
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if (add.getMid().equals("damage") && (craft == null || craft != null && pPlayer.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        bossdam_r += right;
                    }
                    break;
                case "mobdie": // lv, hpIncRatioOnMobDie, hpRatioProp, mpIncRatioOnMobDie, mpRatioProp, modify =D, don't need mob to die
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if ((craft == null || craft != null && pPlayer.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        switch (add.getMid()) {
                            case "hpIncOnMobDie":
                                hpRecover += right;
                                hpRecoverProp += 5;
                                break;
                            case "mpIncOnMobDie":
                                mpRecover += right;
                                mpRecoverProp += 5;
                                break;
                        }
                    }
                    break;
                case "skill": // all these are additional skills
                    if (first_login) {
                        craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                        if ((craft == null || craft != null && pPlayer.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                            switch (add.getMid()) {
                                case "id":
                                    skillid = right;
                                    break;
                                case "level":
                                    skilllevel = right;
                                    break;
                            }
                        }
                    }
                    break;
                case "hpmpchange":
                    switch (add.getMid()) {
                        case "hpChangerPerTime":
                            recoverHP += right;
                            break;
                        case "mpChangerPerTime":
                            recoverMP += right;
                            break;
                    }
                    break;
                case "statinc":
                    boolean canJobx = false,
                     canLevelx = false;
                    job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                    if (job != null) {
                        if (job.contains(",")) {
                            final String[] jobs = job.split(",");
                            for (final String x : jobs) {
                                if (pPlayer.getJob() == Integer.parseInt(x)) {
                                    canJobx = true;
                                }
                            }
                        } else if (pPlayer.getJob() == Integer.parseInt(job)) {
                            canJobx = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null && pPlayer.getLevel() >= Integer.parseInt(level)) {
                        canLevelx = true;
                    }
                    if ((!canJobx && job != null) || (!canLevelx && level != null)) {
                        continue;
                    }
                    if (itemId == 1142367) {
                        final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (day != 1 && day != 7) {
                            continue;
                        }
                    }
                    switch (add.getMid()) {
                        case "incPAD":
                            watk += right;
                            break;
                        case "incMAD":
                            magic += right;
                            break;
                        case "incSTR":
                            localstr += right;
                            break;
                        case "incDEX":
                            localdex += right;
                            break;
                        case "incINT":
                            localint_ += right;
                            break;
                        case "incLUK":
                            localluk += right;
                            break;
                        case "incJump":
                            jump += right;
                            break;
                        case "incMHP":
                            localmaxhp_x += right;
                            break;
                        case "incMMP":
                            localmaxmp_x += right;
                            break;
                        case "incPDD":
                            wdef += right;
                            break;
                        case "incMDD":
                            mdef += right;
                            break;
                        case "incACC":
                            accuracy += right;
                            break;
                        case "incEVA":
                            break;
                        case "incSpeed":
                            speed += right;
                            break;
                        case "incMMPr":
                            percent_mp += right;
                            break;
                    }
                    break;
            }
        }
        if (skillid != 0 && skilllevel != 0) {
            sData.put(SkillFactory.getSkill(skillid), new SkillEntry((byte) skilllevel, (byte) 0, -1));
        }
        return new Pair<>(localmaxhp_x, localmaxmp_x);
    }

    public final void OnProfessionToolRequest(final User pPlayer) {
        if (pPlayer.getProfessionLevel(92000000) > 0 || pPlayer.getProfessionLevel(92010000) > 0) {
            final Iterator<Item> itera = pPlayer.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) { // Goes to first harvesting tool and stops.
                final Equip equip = (Equip) itera.next();
                if (equip.getDurability() != 0 && (equip.getItemId() / 10000 == 150 && pPlayer.getProfessionLevel(92000000) > 0) || (equip.getItemId() / 10000 == 151 && pPlayer.getProfessionLevel(92010000) > 0)) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }

    public void recalcPVPRank(User pPlayer) {
        this.pvpRank = 10;
        this.pvpExp = pPlayer.getTotalBattleExp();
        for (int i = 0; i < 10; i++) {
            if (pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
                pvpRank--;
                pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
            }
        }
    }

    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / localmaxhp);
    }

    public final int getStr() {
        return str;
    }

    public final int getDex() {
        return dex;
    }

    public final int getInt() {
        return int_;
    }

    public final int getLuk() {
        return luk;
    }

    public final int getHp() {
        return hp;
    }

    public final int getMp() {
        return mp;
    }

    public final int getMaxHp() {
        return maxhp;
    }

    public final int getMaxMp() {
        return maxmp;
    }

    public final void setStr(final short str, User pPlayer) {
        this.str = str;
        OnCalculateLocalStats(pPlayer);
    }

    public final void setDex(final short dex, User pPlayer) {
        this.dex = dex;
        OnCalculateLocalStats(pPlayer);
    }

    public final void setInt(final short int_, User pPlayer) {
        this.int_ = int_;
        OnCalculateLocalStats(pPlayer);
    }

    public final void setLuk(final short luk, User pPlayer) {
        this.luk = luk;
        OnCalculateLocalStats(pPlayer);
    }

    public final boolean setHp(final int newhp, User pPlayer) {
        return setHp(newhp, false, pPlayer);
    }

    public boolean setHp(int newhp, boolean silent, User pPlayer) {
        final int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;

        if (pPlayer != null) {
            if (!silent) {
                pPlayer.checkBerserk();
                pPlayer.updatePartyMemberHP();
            }
            if (oldHp > hp && !pPlayer.isAlive()) {
                pPlayer.playerDead();
            }
        }
        if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
            pPlayer.getClient().SendPacket(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
        }
        return hp != oldHp;
    }

    public final boolean setMp(final int newmp, final User pPlayer) {
        final int oldMp = mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = tmp;
        return mp != oldMp;
    }

    public final void setMaxHp(final int hp, User pPlayer) {
        this.maxhp = hp;
        OnCalculateLocalStats(pPlayer);
    }

    public final void setMaxMp(final int mp, User pPlayer) {
        this.maxmp = mp;
        OnCalculateLocalStats(pPlayer);
    }

    public final void setInfo(final int maxhp, final int maxmp, final int hp, final int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }

    public final int getTotalStr() {
        return localstr;
    }

    public final int getTotalDex() {
        return localdex;
    }

    public final int getTotalInt() {
        return localint_;
    }

    public final int getTotalLuk() {
        return localluk;
    }

    public final int getTotalWatk() {
        return watk;
    }

    public final int getTotalMagic() {
        return magic;
    }

    public final int getCurrentMaxHp() {
        return localmaxhp;
    }

    public final int getCurrentMaxMp(final int job) {
        return localmaxmp; // localmaxmp is enforced by relocStats, for demon slayer etc, dont need to check again here
    }

    public final int getHands() {
        return hands; // Only used for stimulator/maker skills
    }

    public final float getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }

    public final float getCurrentMaxBasePVPDamage() {
        return localmaxbasepvpdamage;
    }

    public final float getCurrentMaxBasePVPDamageL() {
        return localmaxbasepvpdamageL;
    }

    public final boolean isRangedJob(final int job) {
        return GameConstants.isJett(job) || GameConstants.isMercedes(job) || GameConstants.isCannoneer(job) || job == 400 || (job / 10 == 52) || (job / 100 == 3) || (job / 100 == 13) || (job / 100 == 14) || (job / 100 == 33) || (job / 100 == 35) || (job / 10 == 41);
    }

    public int getGriticalRate() {
        return crit_rate;
    }

    public int getPassiveSharpeyePercent() {
        return passive_sharpeye_min_percent;
    }
}