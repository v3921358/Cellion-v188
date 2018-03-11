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
import constants.EventConstants;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.skills.AdditionalSkills;
import constants.skills.Aran;
import constants.skills.Evan;
import constants.skills.BattleMage;
import constants.skills.DemonAvenger;
import constants.skills.Kaiser;
import constants.skills.Luminous;
import constants.skills.Magician;
import constants.skills.Mihile;
import constants.skills.NightWalker;
import constants.skills.Warrior;
import constants.skills.Xenon;
import constants.skills._HyperStatSkills;
import handling.world.World;
import handling.world.MapleGuild;
import handling.world.MapleGuildSkill;
import net.OutPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.MapleStringInformationProvider;
import server.StructSetItem;
import server.StructSetItem.SetItem;
import server.life.Element;
import server.maps.objects.MapleCharacter;
import server.potentials.ItemPotentialOption;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialStats;
import server.potentials.ItemPotentialType;
import tools.Pair;
import tools.Triple;
import tools.Tuple;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;

public class PlayerStats implements Serializable {

    private static final long serialVersionUID = -679541993413738569L;
    private final List<Triple<Integer, String, Integer>> psdSkills = new ArrayList<>();
    private final Map<Integer, Integer> setHandling = new HashMap<>(), skillsIncrement = new HashMap<>(), damageIncrease = new HashMap<>();
    private final EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
    private final List<Equip> durabilityHandling = new ArrayList<>(), equipLevelHandling = new ArrayList<>();
    private transient float shouldHealHP, shouldHealMP;
    public transient short passive_sharpeye_min_percent, passive_sharpeye_percent, crit_rate;
    private transient byte passive_mastery;
    public transient int localstr, localdex, localluk, localint_, localmaxhp, localmaxmp, magic, watk, hands, accuracy, targetPlus;
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
    private transient int percent_hp, percent_mp, percent_str, percent_dex, percent_int, percent_luk, percent_acc, percent_atk, percent_matk, percent_wdef, percent_mdef;

    // Star Force
    public int starForceEnhancement;
    public int starForceClass;
    public float starForceDamageRate;

    // Threads
    private final ReentrantLock reLock = new ReentrantLock();

    private void resetLocalStats(final int job) {
        accuracy = 0;
        wdef = 0;
        mdef = 0;
        damX = 0;
        dark_force = 0;
        starForceEnhancement = 0;
        damageCapIncrease = 0; // damage cap increase, by amount
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
        defRange = isRangedJob(job) ? 200 : 0;
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

    public void recalcLocalStats(MapleCharacter chra) {
        recalcLocalStats(false, chra);
    }

    private int localmaxhp_ = 0; // temporary variables, to add these at handlePassive() function, since java doesnt have 'out' arghhh
    private int localmaxmp_ = 0;

    /**
     * Re-calculate the player's overall stats when a new buff is casted; equipment is removed/worn; scrolling an item that's equipped
     *
     * NOTE NOTE, IMPORTANT: This function must always be locked via 'reLock', single-threaded... because it may not only be accessed by the
     * player's thread but also Timers and other party members
     *
     * @param first_login
     * @param chra
     */
    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {

        reLock.lock();
        try {
            // Revert everything to normal before starting.
            resetLocalStats(chra.getJob());

            // Start
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            int oldmaxhp = localmaxhp;
            int oldmaxmp = localmaxmp;

            int originalmaxhp = getMaxHp();
            int originalmaxmp = getMaxMp();

            // set temporary variables
            localmaxhp_ = getMaxHp();
            localmaxmp_ = getMaxMp();

            for (MapleTraitType t : MapleTraitType.values()) {
                chra.getTrait(t).clearLocalExp();
            }
            ItemPotentialOption soc;
            final Map<Skill, SkillEntry> sData = new HashMap<>();

            // Loop through all of character's equipped item.
            final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
            while (itera.hasNext()) {
                final Equip equip = (Equip) itera.next();
                if (equip.getPosition() == -11) {
                    if (InventoryConstants.isMagicWeapon(equip.getItemId())) {
                        final Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                        if (eqstat != null) { //slow, poison, darkness, seal, freeze
                            for (Map.Entry<String, Integer> equipstats : eqstat.entrySet()) {
                                //System.out.println(equipstats.getKey());

                                switch (equipstats.getKey()) {
                                    case "incRMAF":
                                        element_fire = equipstats.getValue();
                                        break;
                                    case "incRMAI":
                                        element_ice = equipstats.getValue();
                                        break;
                                    case "incRMAL":
                                        element_light = equipstats.getValue();
                                        break;
                                    case "incRMAS":
                                        element_psn = equipstats.getValue();
                                        break;
                                    case "elemDefault":
                                        def = equipstats.getValue();
                                        break;
                                }
                            }
                        }
                    }
                }
                if ((equip.getItemId() / 10000 == 166 && equip.getAndroid() != null
                        || equip.getItemId() / 10000 == 167) && chra.getAndroid() == null) {
                    final Equip android = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27);
                    final Equip heart = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -28);
                    if (android != null && heart != null) {
                        chra.setAndroid(equip.getAndroid());
                    }
                }
                //if (equip.getItemId() / 1000 == 1099) {
                //    equippedForce += equip.getMp();
                //}
                starForceEnhancement += equip.getEnhance();

                chra.getTrait(MapleTraitType.craft).addLocalExp(equip.getHands());
                accuracy += equip.getAcc();
                avoidability_equipment += equip.getAvoid();
                localmaxhp_ += equip.getHp();
                localmaxmp_ += equip.getMp();
                localdex += equip.getDex();
                localint_ += equip.getInt();
                localstr += equip.getStr();
                localluk += equip.getLuk();
                watk += equip.getWatk();
                magic += equip.getMatk();
                wdef += equip.getWdef();
                mdef += equip.getMdef();
                speed += equip.getSpeed();
                jump += equip.getJump();
                pvpDamage += equip.getPVPDamage();
                bossdam_r += equip.getBossDamage();
                ignoreTargetDEF += equip.getIgnorePDR();
                dam_r += equip.getTotalDamage();
                percent_str += equip.getAllStat();
                percent_dex += equip.getAllStat();
                percent_int += equip.getAllStat();
                percent_luk += equip.getAllStat();
                percent_hp += equip.getMHPr();
                percent_mp += equip.getMMPr();

                switch (equip.getItemId()) {
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
                        for (int eb_bonus : GameConstants.Equipments_Bonus) {
                            if (equip.getItemId() == eb_bonus) {
                                //equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                                break;
                            }
                        }
                        break;
                }
                final Integer equipmentSetId = ii.getSetItemID(equip.getItemId());

                if (equipmentSetId != null && equipmentSetId > 0) {
                    if (setHandling.containsKey(equipmentSetId)) {
                        setHandling.put(equipmentSetId, setHandling.get(equipmentSetId) + 1); //id of Set, number of items to go with the set
                    } else {
                        setHandling.put(equipmentSetId, 1); //id of Set, number of items to go with the set
                    }
                }
                if (equip.getIncSkill() > 0 && ii.getEquipSkills(equip.getItemId()) != null) {
                    for (final int zzz : ii.getEquipSkills(equip.getItemId())) {
                        final Skill skil = SkillFactory.getSkill(zzz);
                        if (skil != null && skil.canBeLearnedBy(chra.getJob())) { //dont go over masterlevel :D
                            int value = 1;
                            if (skillsIncrement.get(skil.getId()) != null) {
                                value += skillsIncrement.get(skil.getId());
                            }
                            skillsIncrement.put(skil.getId(), value);
                        }
                    }
                }
                final Pair<Integer, Integer> ix = handleEquipAdditions(ii, chra, first_login, sData, equip.getItemId());
                if (ix != null) {
                    localmaxhp_ += ix.getLeft();
                    localmaxmp_ += ix.getRight();
                }

                if (!equip.getPotentialTier().isHiddenType()) {
                    final int reqLevel = ii.getReqLevel(equip.getItemId());

                    if (reqLevel >= 10) {
                        handleEquipPotentialStats(chra, reqLevel, equip.getPotential1());
                        handleEquipPotentialStats(chra, reqLevel, equip.getPotential2());
                        handleEquipPotentialStats(chra, reqLevel, equip.getPotential3());
                    }
                }
                /*if (equip.getSocketState() > 15) {
                    final int[] sockets = {equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                    for (final int i : sockets) {
                        if (i > 0) {
                            soc = ItemPotentialProvider.getSocketInfo(i);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login, sData);
                            }
                        }
                    }
                }*/
                if (equip.getDurability() > 0) {
                    durabilityHandling.add((Equip) equip);
                }
                if (GameConstants.getMaxLevel(equip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? (equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())) : (equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId())))) {
                    equipLevelHandling.add((Equip) equip);
                }
            }

            // Calculate Star force class
            if (chra.getMap() != null) {
                final int requiredStarForce = chra.getMap().getSharedMapResources().starForceBarrier;

                this.starForceClass = getStarForceClassByParam(requiredStarForce, starForceEnhancement);
                this.starForceDamageRate = getStarforceAttackRate(starForceClass);
            }

            // Calculate Set item additional stats
            final Iterator<Entry<Integer, Integer>> iter = setHandling.entrySet().iterator();
            while (iter.hasNext()) {
                final Entry<Integer, Integer> entry = iter.next();
                final StructSetItem set = ii.getSetItem(entry.getKey());

                if (set != null) {
                    final Map<Integer, SetItem> itemz = set.getItems();
                    for (Entry<Integer, SetItem> setItemStats : itemz.entrySet()) {

                        if (setItemStats.getKey() <= entry.getValue()) { // key = required amount of set to wear
                            SetItem se = setItemStats.getValue();

                            localstr += se.incSTR + se.incAllStat;
                            localdex += se.incDEX + se.incAllStat;
                            localint_ += se.incINT + se.incAllStat;
                            localluk += se.incLUK + se.incAllStat;
                            watk += se.incPAD;
                            magic += se.incMAD;
                            speed += se.incSpeed;
                            accuracy += se.incACC;
                            localmaxhp_ += se.incMHP;
                            localmaxmp_ += se.incMMP;
                            percent_hp += se.incMHPr;
                            percent_mp += se.incMMPr;

                            wdef += se.incPDD;
                            mdef += se.incMDD;

                            /*       if (se.option1 > 0 && se.option1Level > 0) {
                                soc = ItemPotentialProvider.getPotentialInfo(se.option1).get(se.option1Level);
                                if (soc != null) {
                                    localmaxhp_ += soc.get("incMHP");
                                    localmaxmp_ += soc.get("incMMP");
                                    handleItemOption(soc, chra, first_login, sData);
                                }
                            }
                            if (se.option2 > 0 && se.option2Level > 0) {
                                soc = ItemPotentialProvider.getPotentialInfo(se.option2).get(se.option2Level);
                                if (soc != null) {
                                    localmaxhp_ += soc.get("incMHP");
                                    localmaxmp_ += soc.get("incMMP");
                                    handleItemOption(soc, chra, first_login, sData);
                                }
                            }*/
                        }
                    }
                }
            }
            handleProfessionTool(chra);
            for (Item item : chra.getInventory(MapleInventoryType.CASH).newList()) {
                if (item.getItemId() / 100000 == 52) {
                    if (expMod < 3 && (item.getItemId() == 5211060 || item.getItemId() == 5211050 || item.getItemId() == 5211051 || item.getItemId() == 5211052 || item.getItemId() == 5211053 || item.getItemId() == 5211054)) {
                        expMod = 3.0; //overwrite
                    } else if (expMod < 2 && (item.getItemId() == 5210000 || item.getItemId() == 5210001 || item.getItemId() == 5210002 || item.getItemId() == 5210003 || item.getItemId() == 5210004 || item.getItemId() == 5210005 || item.getItemId() == 5211061 || item.getItemId() == 5211000 || item.getItemId() == 5211001 || item.getItemId() == 5211002 || item.getItemId() == 5211003 || item.getItemId() == 5211046 || item.getItemId() == 5211047 || item.getItemId() == 5211048 || item.getItemId() == 5211049)) {
                        expMod = 2.0;
                    } else if (expMod < 1.5 && (item.getItemId() == 5211068)) {
                        expMod = 1.5;
                    }
                } else if (dropMod == 1.0 && item.getItemId() / 10000 == 536) {
                    if (item.getItemId() >= 5360000 && item.getItemId() < 5360100) {
                        dropMod = 2.0;
                    }
                } else if (item.getItemId() == 5710000) {
                    questBonus = 2;
                } else if (item.getItemId() == 5590000) {
                    levelBonus += 5;
                }
            }
            if (dropMod > 0 && EventConstants.DoubleTime) {
                dropMod *= 2.0;
            }
            if (expMod > 0 && EventConstants.DoubleTime) {
                expMod *= 2.0;
            }
            for (Item item : chra.getInventory(MapleInventoryType.ETC).list()) {
                switch (item.getItemId()) {
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
            if (first_login && chra.getLevel() >= 30) {
                if (chra.isGM()) { //!job lol
                    for (int i = 0; i < allJobs.length; i++) {
                        sData.put(SkillFactory.getSkill(1085 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                        sData.put(SkillFactory.getSkill(1087 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                    }
                } else {
                    sData.put(SkillFactory.getSkill(getSkillByJob(1085, chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                    sData.put(SkillFactory.getSkill(getSkillByJob(1087, chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                }
            }
            // add to localmaxhp_ if percentage plays a role in it, else add_hp
            handleTemporaryStats(chra);
            Integer buff = chra.getBuffedValue(CharacterTemporaryStat.EMHP);
            if (buff != null) {
                localmaxhp_ += buff;
            }
            buff = chra.getBuffedValue(CharacterTemporaryStat.EMMP);
            if (buff != null) {
                localmaxmp_ += buff;
            }
            buff = chra.getBuffedValue(CharacterTemporaryStat.IncMaxHP);
            if (buff != null) {
                localmaxhp_ += buff;
            }
            buff = chra.getBuffedValue(CharacterTemporaryStat.IncMaxMP);
            if (buff != null) {
                localmaxmp_ += buff;
            }
            handlePassiveSkills(chra);
            handleHyperPassiveSkills(chra);
            handleHyperStatPassive(chra);

            if (chra.getGuildId() > 0) {
                final MapleGuild g = World.Guild.getGuild(chra.getGuildId());
                if (g != null && g.getSkills().size() > 0) {
                    final long now = System.currentTimeMillis();
                    for (MapleGuildSkill gs : g.getSkills()) {
                        if (gs.timestamp > now && gs.activator.length() > 0) {
                            final MapleStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
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
            for (Pair<Integer, Integer> ix : chra.getCharacterCard().getCardEffects()) {
                final MapleStatEffect e = SkillFactory.getSkill(ix.getLeft()).getEffect(ix.getRight());
                percent_wdef += e.getPDDRate();
                watk += (e.getLevelToWatk() * chra.getLevel());
                percent_hp += e.getPercentHP();
                percent_mp += e.getPercentMP();
                magic += (e.getLevelToMatk() * chra.getLevel());
                RecoveryUP += e.getMPConsumeEff();
                percent_acc += e.getPercentAcc();
                crit_rate += e.getCr();
                jump += e.getPassiveJump();
                speed += e.getPassiveSpeed();
                avoidabilityRate += e.getPercentAvoid();
                damX += (e.getLevelToDamage() * chra.getLevel());
                BuffUP_Summon += e.getSummonTimeInc();
                expLossReduceR += e.getEXPLossRate();
                asrR += e.getASRRate();
                //ignoreMobDamR
                suddenDeathR += e.getSuddenDeathR();
                BuffUP_Skill += e.getBuffTimeRate();
                //onHitHpRecoveryR
                //onHitMpRecoveryR
                coolTimeR += e.getCooltimeReduceR();
                incMesoProp += e.getMesoAcquisition();
                damX += Math.floor((e.getHpToDamage() * oldmaxhp) / 100.0f);
                damX += Math.floor((e.getMpToDamage() * oldmaxhp) / 100.0f);
                //finalAttackDamR
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
            calculateFame(chra);
            ignoreTargetDEF += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
            pvpDamage += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
            asrR += chra.getTrait(MapleTraitType.will).getLevel() / 5;

            // Calculate accuracy
            accuracy += Math.min(GameConstants.maxAccAvoid,
                    Math.floor((accuracy * percent_acc) / 100.0f));
            accuracy += Math.min(GameConstants.maxAccAvoid,
                    chra.getTrait(MapleTraitType.insight).getLevel() * 15 / 10);

            // Calculate avoidability
            avoidability_weapon += Math.min(GameConstants.maxAccAvoid,
                    (localdex + localluk + (avoidability_equipment + avoidability_skill)) * (1d + (avoidabilityRate / 100d)));
            avoidability_magic += Math.min(GameConstants.maxAccAvoid,
                    (localint_ + localluk + (avoidability_equipment + avoidability_skill)) * (1d + (avoidabilityRate / 100d)));

            // Calculate HP percentage bost
            localmaxhp_ += percent_hp * 0.01f * originalmaxhp;
            localmaxhp_ += chra.getTrait(MapleTraitType.will).getLevel() * 20;
            localmaxhp = Math.min(GameConstants.maxHP, localmaxhp_);

            // Calculate MP percentage boost
            localmaxmp_ += percent_mp * 0.01f * originalmaxmp;
            localmaxmp_ += chra.getTrait(MapleTraitType.sense).getLevel() * 20;
            localmaxmp = Math.min(GameConstants.maxMP, localmaxmp_);

            if (chra.getEventInstance() != null && chra.getEventInstance().getName().startsWith("PVP")) { //hack
                MapleStatEffect eff;
                localmaxhp = Math.min(40000, localmaxhp * 3); //approximate.
                localmaxmp = Math.min(20000, localmaxmp * 2);
                //not sure on 20000 cap
                for (int i : pvpSkills) {
                    Skill skil = SkillFactory.getSkill(i);
                    if (skil != null && skil.canBeLearnedBy(chra.getJob())) {
                        sData.put(skil, new SkillEntry((byte) 1, (byte) 0, -1));
                        eff = skil.getEffect(1);
                        switch ((i / 1000000) % 10) {
                            case 1:
                                if (eff.getX() > 0) {
                                    pvpDamage += (wdef / eff.getX());
                                }
                                break;
                            case 3:
                                hpRecoverProp += eff.getProb();
                                hpRecover += eff.getX();
                                mpRecoverProp += eff.getProb();
                                mpRecover += eff.getX();
                                break;
                            case 5:
                                crit_rate += eff.getProb();
                                passive_sharpeye_percent = 100;
                                break;
                        }
                        break;
                    }
                }
                eff = chra.getStatForBuff(CharacterTemporaryStat.Morph);
                if (eff != null && eff.getSourceId() % 10000 == 1105) { //ice knight
                    localmaxhp = 500000;
                    localmaxmp = 500000;
                }
            }
            chra.changeSkillLevelSkip(sData, false);
            if (GameConstants.isDemonSlayer(chra.getJob())) {
                localmaxmp = GameConstants.getMPByJob(chra.getJob()) + dark_force;
            } else if (GameConstants.isZero(chra.getJob())) {
                localmaxmp = 100;
            }
            if (GameConstants.isDemonAvenger(chra.getJob())) {
                chra.getClient().write(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
            }
            calcPassive_SharpEye(chra);
            calcPassiveMasteryAmount(chra);

            recalcPVPRank(chra);

            if (first_login) {
                chra.silentEnforceMaxHpMp();
                relocHeal(chra);
            } else {
                chra.enforceMaxHpMp();
            }
            calculateMaxBaseDamage(Math.max(magic, watk), pvpDamage, chra);
            trueMastery = Math.min(100, trueMastery);
            passive_sharpeye_min_percent = (short) Math.min(passive_sharpeye_min_percent, passive_sharpeye_percent);
            if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
                chra.updatePartyMemberHP();
            }
        } finally {
            reLock.unlock();
        }
    }

    private void handleEquipPotentialStats(MapleCharacter chr, int equipRequiredLevel, int potentialId) {
        final ItemPotentialOption option = ItemPotentialProvider.getPotentialInfo(potentialId);
        if (option != null) {
            final List<Pair<ItemPotentialType, ItemPotentialStats>> statsForThisEquipLevel = option.getSuitableStats(equipRequiredLevel);
            if (statsForThisEquipLevel == null) {
                return;
            }

            for (Pair<ItemPotentialType, ItemPotentialStats> stat : statsForThisEquipLevel) {
                switch (stat.getLeft()) {
                    case incSTR:
                        localstr += stat.getRight().getValue();
                        break;
                    case incSTRr:
                        percent_str += stat.getRight().getValue();
                        break;
                    case incSTRlv: // STR per 10 Character Levels: +#incSTRlv"
                        localstr += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incDEX:
                        localdex += stat.getRight().getValue();
                        break;
                    case incDEXr:
                        percent_dex += stat.getRight().getValue();
                        break;
                    case incDEXlv: // DEX per 10 Character Levels: +#incDEXlv
                        localdex += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incINT:
                        localint_ += stat.getRight().getValue();
                        break;
                    case incINTr:
                        percent_int += stat.getRight().getValue();
                        break;
                    case incINTlv:
                        localint_ += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incLUK:
                        localluk += stat.getRight().getValue();
                        break;
                    case incLUKr:
                        percent_luk += stat.getRight().getValue();
                        break;
                    case incLUKlv:
                        localluk += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incAsrR:  // abnormal status = disease
                        asrR += stat.getRight().getValue();
                        break;
                    case incTerR: // elemental resistance = avoid element damage from monster
                        terR += stat.getRight().getValue();
                        break;
                    case incMHP:
                        localmaxhp_ += stat.getRight().getValue();
                        break;
                    case incMHPr:
                        percent_hp += stat.getRight().getValue();
                        break;
                    case incMHPlv: // HP per 10 Character Levels: +#incMHPlv"/>
                        localmaxhp_ += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incMMP:
                        this.localmaxmp_ += stat.getRight().getValue();
                        break;
                    case incMMPr:
                        percent_mp += stat.getRight().getValue();
                        break;
                    case incACC:
                        accuracy += stat.getRight().getValue();
                        break;
                    case incACCr: // incEVA -> increase dodge
                        percent_acc += stat.getRight().getValue();
                        break;
                    case incEVA:
                        break;
                    case incEVAr:
                        avoidabilityRate += stat.getRight().getValue();
                        break;
                    //
                    case incJump: // client sidded for now, but we'll still need to calculate ut
                        jump += stat.getRight().getValue();
                        break;
                    case incPAD:
                        watk += stat.getRight().getValue();
                        break;
                    case incPADr:
                        percent_atk += stat.getRight().getValue();
                        break;
                    case incPADlv: // ATT per 10 Character Levels: +#incPADlv"/>
                        watk += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incPDD:
                        wdef += stat.getRight().getValue();
                        break;
                    case incPDDr:
                        percent_wdef += stat.getRight().getValue();
                        break;
                    case incMAD:
                        magic += stat.getRight().getValue();
                        break;
                    case incMADr:
                        percent_matk += stat.getRight().getValue();
                        break;
                    case incMADlv: // TODO
                        magic += stat.getRight().getValue() * Math.round(chr.getLevel() / 10);
                        break;
                    case incDAMr:
                        if (stat.getRight().isBoss()) {
                            bossdam_r += stat.getRight().getValue();
                        } else {
                            dam_r += stat.getRight().getValue();
                        }
                        break;
                    case incMDD:
                        mdef += stat.getRight().getValue();
                        break;
                    case incMDDr:
                        percent_mdef += stat.getRight().getValue();
                        break;
                    case incCr: // critical rate
                        crit_rate += stat.getRight().getValue();
                        break;
                    case incAllskill:
                        incAllskill += stat.getRight().getValue();
                        break;
                    case incMaxDamage:
                        this.damageCapIncrease += stat.getRight().getValue();
                        break;
                    case incCriticaldamageMin:
                        passive_sharpeye_min_percent += stat.getRight().getValue();
                    case incCriticaldamageMax:
                        passive_sharpeye_percent += stat.getRight().getValue();
                        break;

                    case incEXPr:
                        this.expBuff += stat.getRight().getValue();
                        break;
                    case level: // Skills
                        break;
                    case incMesoProp:
                        incMesoProp += stat.getRight().getValue(); // mesos + %
                        break;
                    case MP: // "#prop% chance to recover #HP HP when attacking."/>
                        mpRecover += stat.getRight().getValue();
                        mpRecoverProp += stat.getRight().getProbability();
                        break;
                    case HP:
                        hpRecover += stat.getRight().getValue();
                        hpRecoverProp += stat.getRight().getProbability();
                        break;
                    case incSpeed: // client sidded
                        speed += stat.getRight().getValue();
                        break;
                    case incRewardProp:
                        dropBuff += stat.getRight().getValue(); // extra drop rate for item
                        break;
                    case RecoveryHP:
                        recoverHP += stat.getRight().getValue(); // This shouldn't be here, set 4 seconds.
                        break;
                    case RecoveryMP:
                        recoverMP += stat.getRight().getValue(); // This shouldn't be here, set 4 seconds.
                        break;
                    case ignoreDAM:
                        ignoreTakenDAM += stat.getRight().getValue();
                        ignoreTakenDAM_rate += stat.getRight().getProbability();
                        break;
                    case ignoreDAMr:
                        ignoreTakenDAMr += stat.getRight().getValue();
                        ignoreTakenDAMr_rate += stat.getRight().getProbability();
                        break;
                    case ignoreTargetDEF:
                        ignoreTargetDEF += stat.getRight().getValue();
                        break;
                    case DAMreflect:
                        DAMreflect += stat.getRight().getValue();
                        DAMreflect_rate += stat.getRight().getProbability();
                        break;
                    case mpconReduce:
                        mpconReduce += stat.getRight().getValue();
                        break;
                    case RecoveryUP:
                        RecoveryUP += stat.getRight().getValue(); // only for hp items and skills
                        break;
                    case mpRestore: // TODO
                    case time: // client sidded
                        break;
                    case reduceCooltime:
                        reduceCooltime += stat.getRight().getValue(); // in seconds
                        break;
                    default:
                        break;
                }
            }
        }


        /*   
        bossdam_r *= (soc.get("bdR") + 100.0) / 100.0;
        ignoreTargetDEF *= (soc.get("imdR") + 100.0) / 100.0;*/
        // TODO: Auto Steal potentials (modify handleSteal), potentials with invincible stuffs, abnormal status duration decrease,
        // poison, stun, etc (uses level field -> cast disease to mob/player), face?
    }

    public List<Triple<Integer, String, Integer>> getPsdSkills() {
        return psdSkills;
    }

    private void handlePassiveSkills(MapleCharacter chra) {
        Skill bx;
        int bof;
        MapleStatEffect eff;

        psdSkills.clear();
        for (Skill sk : chra.getSkills().keySet()) {
            if (sk.getPsd() == 1) {
                Triple<Integer, String, Integer> psdSkill = new Triple<>(0, "", 0);
                psdSkill.left = sk.getPsdSkill();
                psdSkill.mid = sk.getPsdDamR(); //This only handles damage increases; some skills have effects other than that, so TODO
                psdSkill.mid = sk.getPsdtarget();
                psdSkill.right = sk.getId();
                psdSkills.add(psdSkill);
            }
        }

        // Handle passive MP Boost skill - this is mage only, however the client also calculates it if the character's skill
        // level of this is above > 0 despite being in other job.
        bx = SkillFactory.getSkill(Magician.MP_BOOST);
        bof = chra.getTotalSkillLevel(bx);
        if (bof > 0) {
            final MapleStatEffect MPBoostEffect = bx.getEffect(bof);

            // Max MP increased by 20%
            percent_mp += MPBoostEffect.getPercentMP();

            // MP increased by 120 per level
            //      System.out.println("Before: " + localmaxmp);
            localmaxmp_ += MPBoostEffect.getMaxMpPerLevel() * chra.getLevel(); // add to temporary variable 'localmaxmp_' used by relocStats
            //      System.out.println("Boost: " + MPBoostEffect.getMaxMpPerLevel() * chra.getLevel());
            //     System.out.println("After: " + localmaxmp);

            // TODO: critical rate +3% when equipping a wand
        }

        // Elven blessing
        bx = SkillFactory.getSkill(AdditionalSkills.ELVEN_BLESSING);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            this.expMod_ElveBlessing += bx.getEffect(bof).getX() / 100f;
        }

        // Bullseye shot Critical
        if (chra.getBuffedValue(CharacterTemporaryStat.BullsEye) != null) {
            crit_rate += bx.getEffect(bof).getX();
            passive_sharpeye_min_percent += bx.getEffect(bof).getX();
            passive_sharpeye_percent += bx.getEffect(bof).getY();
        }

        switch (chra.getJob()) {
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232: {
                break;
            }
            case 1200:
            case 1210:
            case 1211:
            case 1212: {
                bx = SkillFactory.getSkill(12000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_mp += bx.getEffect(bof).getPercentMP();
                }
                bx = SkillFactory.getSkill(12110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    terR += bx.getEffect(bof).getX();
                }
                break;
            }
            case 1100:
            case 1110:
            case 1111:
            case 1112: {
                bx = SkillFactory.getSkill(11000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 2003: // Phantom noob
                // Phantom Instinct - 20030204
                // Dexterous Training - 20030206
                bx = SkillFactory.getSkill(20030204); // +10% crit rate
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    crit_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(20030206); // +40 DEX, Base Avoidability: +20%
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localdex += eff.getDexX();
                    avoidabilityRate += eff.getER();
                }
                break;
            case 2400:
            case 2410:
            case 2411:
            case 2412: { // Phantom
                // Blanc Carte - 24100003
                // Cane Mastery - 24100004
                // Luck of Phantom Thief - 24111002
                // 24111003- uses monlight effect, but is Misfortune Protection
                // 24110004 - Flash and Flee -> active
                // 24111005 - Moonlight
                // 24111006 - Phantom Charge
                // 24111008- Breeze Carte, (hidden), linked to phantom charge
                // 24121000 - Ultimate Drive
                // 24120002 - Noir Carte
                // 24121003 - Twilight
                // 24121004 - Pray of Aria
                // 24121005 - Tempest of Card
                // 24120006 - Cane Expert
                // 24121007 - Soul Steal
                // 24121008 - Maple Warrior
                // 24121009 - Hero's will
                // 24121010 - Some linked skill (Twilight)
                bx = SkillFactory.getSkill(24001002); // Swift Phantom
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speed += eff.getPassiveSpeed();
                    jump += eff.getPassiveJump();
                }
                bx = SkillFactory.getSkill(24000003); // Quick Evasion
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    avoidabilityRate += eff.getX();
                }
                bx = SkillFactory.getSkill(24100006); //Luck Monopoly
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localluk += eff.getLukX();
                }
                bx = SkillFactory.getSkill(24110007); // Acute Sense
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    crit_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(24111002); //Luck of Phantom Thief
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localluk += eff.getLukX();
                }
                break;
            }
            case 501:
            case 530:
            case 531:
            case 532:
                bx = SkillFactory.getSkill(5010003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5300008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(5311001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(5301001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(5310007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getHpR();
                    asrR += eff.getASRRate();
                    percent_wdef += eff.getPDDRate();
                }
                bx = SkillFactory.getSkill(5310006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5321009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDAMRate();
                    bossdam_r += eff.getDAMRate();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            case 3001:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
                mpRecoverProp = 100;
                bx = SkillFactory.getSkill(31000003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getHpR();
                }
                bx = SkillFactory.getSkill(31100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getDAMRate());
                    damageIncrease.put(31001006, (int) eff.getDAMRate());
                    damageIncrease.put(31001007, (int) eff.getDAMRate());
                    damageIncrease.put(31001008, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(31100005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(31100010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getX());
                    damageIncrease.put(31001006, (int) eff.getX());
                    damageIncrease.put(31001007, (int) eff.getX());
                    damageIncrease.put(31001008, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(31111007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDAMRate();
                    bossdam_r += eff.getDAMRate();
                }
                bx = SkillFactory.getSkill(31110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    avoidabilityRate += eff.getX();
                    // HACK: shouldn't be here
                    hpRecoverPercent += eff.getY();
                    hpRecoverProp += eff.getX();
                    //mpRecover += eff.getY(); // handle in takeDamage
                    //mpRecoverProp += eff.getX();
                }
                bx = SkillFactory.getSkill(31110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpRecover += 1;
                    mpRecoverProp += eff.getProb();
                }
                bx = SkillFactory.getSkill(31111006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getX();
                    bossdam_r += eff.getX();
                    crit_rate += eff.getY();
                }
                bx = SkillFactory.getSkill(31121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(31120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getX());
                    damageIncrease.put(31001006, (int) eff.getX());
                    damageIncrease.put(31001007, (int) eff.getX());
                    damageIncrease.put(31001008, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(31120008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(31120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(30010112);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    bossdam_r += eff.getBossDamage();
                    mpRecover += eff.getX();
                    mpRecoverProp += eff.getBossDamage(); //yes
                }
                bx = SkillFactory.getSkill(30010185);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    chra.getTrait(MapleTraitType.will).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getY()));
                    chra.getTrait(MapleTraitType.charisma).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getZ()));
                }
                bx = SkillFactory.getSkill(30010111);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverPercent += eff.getX();
                    hpRecoverProp += eff.getProb(); //yes
                }
                bx = SkillFactory.getSkill(31110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localmaxmp_ += eff.getMaxDemonFury(); //yes
                }
                break;
            case 3120:
            case 3121:
            case 3122:
                bx = SkillFactory.getSkill(DemonAvenger.MAPLE_WARRIOR);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += (int) bof / 2;
                }
                bx = SkillFactory.getSkill(DemonAvenger.RAGE_WITHIN);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localmaxhp_ += 100 + (bof * 100);
                    def += bof * 100;
                }
                break;
            case 3002:
            case 3600:
            case 3610:
            case 3611:
            case 3612: { // Xenon
                // Xenon's Multilateral have only one level, so no need to get the effect from the skill.
                // Just give the correct increase based on skill description. -Mazen
                bx = SkillFactory.getSkill(30020234); //Lateral 1
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    if (chra.getStr() >= 20 && chra.getDex() >= 20 && chra.getLuk() >= 20) {
                        percent_atk = +3; // +3%
                    }
                }
                bx = SkillFactory.getSkill(36000004); //Lateral 2
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    if (chra.getStr() >= 40 && chra.getDex() >= 40 && chra.getLuk() >= 40) {
                        percent_hp += 5; // +5%
                        percent_mp += 5; // +5%
                    }
                }
                bx = SkillFactory.getSkill(36100007); //Lateral 3
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    if (chra.getStr() >= 90 && chra.getDex() >= 90 && chra.getLuk() >= 90) {
                        percent_hp += 5; // +5%
                        percent_mp += 5; // +5%
                    }
                }
                bx = SkillFactory.getSkill(36110007); //Lateral 4
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    if (chra.getStr() >= 150 && chra.getDex() >= 150 && chra.getLuk() >= 150) {
                        percent_hp += 10; // +10%
                        percent_mp += 10; // +10%
                    }
                }
                bx = SkillFactory.getSkill(36120010); //Lateral 5
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    if (chra.getStr() >= 150 && chra.getDex() >= 150 && chra.getLuk() >= 150) {
                        percent_hp += 10; // +10%
                        percent_mp += 10; // +10%
                    }
                }
                bx = SkillFactory.getSkill(36120045); //Lateral 6
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    targetPlus += bx.getEffect(bof).gettargetPlus();
                }
                bx = SkillFactory.getSkill(Xenon.EFFICIENCY_STREAMLINE); //Efficiency Streamline
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localmaxhp_ += bx.getEffect(bof).getMaxHpX();
                    localmaxmp_ += bx.getEffect(bof).getMaxMpX();
                }
                break;
            }
            case 6000:
            case 6100:
            case 6110:
            case 6111:
            case 6112: { // Kaiser
                bx = SkillFactory.getSkill(Kaiser.INNER_BLAZE); //Inner Blaze
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(Kaiser.ADVANCED_INNER_BLAZE); //Adv Inner Blade
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(60000222); //Iron Will
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }

                break;
            }
            case 510:
            case 511:
            case 512: {
                bx = SkillFactory.getSkill(5100009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 1510:
            case 1511:
            case 1512: {
                bx = SkillFactory.getSkill(15100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 508:
            case 570:
            case 571:
            case 572: { // Jett
                bx = SkillFactory.getSkill(5080000); // Comet Booster
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    accuracy += eff.getAccX();
                    jump += eff.getPassiveJump();
                    speed += eff.getSpeedMax(); // TODO: split speed max and speed. (speed have a limit, while speedMax will add to the max)
                } // TODO: research more on percentage hp/mp and stats, which doesn't take effect to note.
                bx = SkillFactory.getSkill(5080004); // Shadow Heart
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    crit_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                // 5081010, 5081011: Hidden
                if (chra.getJob() >= 570) {
                    bx = SkillFactory.getSkill(5700003); // Physical Training
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        localstr += eff.getStrX();
                        localdex += eff.getDexX();
                    }
                }
                if (chra.getJob() >= 571) {
                    bx = SkillFactory.getSkill(5710004); // High Life
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        percent_wdef += eff.getPDDRate();
                        percent_mdef += eff.getMDDRate();
                        localmaxhp_ += eff.getMaxHpX();
                        localmaxmp_ += eff.getMaxMpX();
                    }
                    bx = SkillFactory.getSkill(5710005); // Cutting Edge
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        crit_rate += eff.getCr();
                        ignoreTargetDEF += eff.getIgnoreMob();
                    }
                }
                if (chra.getJob() == 572) {
                    bx = SkillFactory.getSkill(5720008); // Collateral Damage
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        crit_rate += eff.getCr();
                        passive_sharpeye_min_percent += eff.getCriticalMin();
                        passive_sharpeye_percent += eff.getCriticalMax();
                        bossdam_r += eff.getBossDamage();
                    }
                    // TODO: 5721009, 5720012(Counterattack)
                }
                break;
            }
            case 400: // Thief
            case 410: // Assassin
            case 411: // Hermit
            case 412: // Night Lord
            case 420: // Bandit
            case 421: // Chief Bandit
            case 422: { // Shadower
                bx = SkillFactory.getSkill(4001005); // Haste
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speed += eff.getSpeedMax();
                }
                // 4000010: Magic Theft, invisible.
                if (chra.getJob() >= 410 && chra.getJob() <= 412) {
                    bx = SkillFactory.getSkill(4100007); // Physical Training
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        localluk += eff.getLukX();
                        localdex += eff.getDexX();
                    }
                }
                if (chra.getJob() == 411 || chra.getJob() == 412) {
                    bx = SkillFactory.getSkill(4110008); // Enveloping Darkness
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        percent_hp += eff.getPercentHP();
                        asrR += eff.getASRRate();
                        terR += eff.getTERRate();
                    }
                    bx = SkillFactory.getSkill(4110012); // Expert Throwing Star Handling
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        damageIncrease.put(4001344, eff.getDAMRate());
                        damageIncrease.put(4101008, eff.getDAMRate());
                        damageIncrease.put(4101009, eff.getDAMRate());
                        damageIncrease.put(4101010, eff.getDAMRate());
                    }
                    bx = SkillFactory.getSkill(4110014);
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        RecoveryUP += eff.getX() - 100;
                    }
                }
                if (chra.getJob() == 412) {
                    bx = SkillFactory.getSkill(4121014); // Dark Harmony
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        ignoreTargetDEF += eff.getIgnoreMob();
                    }
                }

                bx = SkillFactory.getSkill(4200006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    asrR += eff.getASRRate();
                    terR += eff.getTERRate();
                }
                bx = SkillFactory.getSkill(4210000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getX();
                    percent_mdef += eff.getX();
                }
                break;
            }
            case 431: // Blade Acolyte
            case 432: // Blade Specialist
            case 433: // Blade Lord
            case 434: { // Blade Master
                bx = SkillFactory.getSkill(4001006); // Haste
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speed += eff.getSpeedMax();
                }

                bx = SkillFactory.getSkill(4310004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    asrR += eff.getASRRate();
                    terR += eff.getTERRate();
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getPDDRate();
                    percent_mdef += eff.getMDDRate();
                }
                break;
            }
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132: {
                bx = SkillFactory.getSkill(Warrior.WARRIOR_MASTERY);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    // TO-DO: Add stance
                    speed += bx.getEffect(bof).getPassiveSpeed();
                    jump += bx.getEffect(bof).getPassiveJump();
                    localmaxhp_ += getMaxHp() + bx.getEffect(bof).getMaxHpPerLevel();
                }
                bx = SkillFactory.getSkill(1210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getX();
                    percent_mdef += eff.getX();
                }
                bx = SkillFactory.getSkill(1220005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(1220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    trueMastery += bx.getEffect(bof).getMastery();
                }
                bx = SkillFactory.getSkill(1310000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    terR += bx.getEffect(bof).getX();
                }
                break;
            }
            case 322: { // Crossbowman
                bx = SkillFactory.getSkill(3220004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(3220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3220005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && chra.getBuffedValue(CharacterTemporaryStat.SpiritLink) != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getX();
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                break;
            }
            case 312: { // Bowmaster
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(3120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3120006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && chra.getBuffedValue(CharacterTemporaryStat.SpiritLink) != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getX();
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                break;
            }
            case 3510:
            case 3511:
            case 3512: {
                bx = SkillFactory.getSkill(35100000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(35120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    trueMastery += bx.getEffect(bof).getMastery();
                }
                break;
            }
            case 3200:
            case 3210:
            case 3211:
            case 3212: { // Battle Mage
                bx = SkillFactory.getSkill(32110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    asrR += bx.getEffect(bof).getASRRate();
                    terR += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDAMRate();
                    bossdam_r += eff.getDAMRate();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(32120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(32120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(32120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(BattleMage.ORDINARY_CONVERSION);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(BattleMage.BATTLE_RAGE);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                    percent_mp += bx.getEffect(bof).getPercentMP();
                }
                break;
            }
            case 3300:
            case 3310:
            case 3311:
            case 3312: {
                bx = SkillFactory.getSkill(33120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(33110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                bx = SkillFactory.getSkill(33120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    ignoreTargetDEF += eff.getIgnoreMob();
                    avoidabilityRate += eff.getER();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDAMRate();
                    bossdam_r += eff.getDAMRate();
                }
                break;
            }
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                magic += chra.getTotalSkillLevel(SkillFactory.getSkill(22000000));
                bx = SkillFactory.getSkill(22150000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r += eff.getY();
                    bossdam_r += eff.getY();
                }
                bx = SkillFactory.getSkill(22160000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                bx = SkillFactory.getSkill(22170001); // magic mastery, this is an invisible skill
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }

                bx = SkillFactory.getSkill(Evan.MAGIC_LINK); // High Life
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    //localmaxmp_ += eff.getMaxMpX();
                    percent_mp += eff.getMpR();
                    localmaxmp_ += (120 * chra.getLevel());
                }
                break;
            }
            case 2112: {
                bx = SkillFactory.getSkill(21120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            }
            case 4100: // Hayato
            case 4110:
            case 4111:
            case 4112:
                /*   if (chra.getJob() >= 4111) {
                        bx = SkillFactory.getSkill(41110006);// Willow Dodge
                        bof = chra.getTotalSkillLevel(bx);
                            if (bof > 0) {
                                eff = bx.getEffect(bof);
                                dodgeChance += bx.getEffect(bof).getER();
                                dam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                                bossdam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                            }
                    }
                * */
                if (chra.getJob() >= 4111) {
                    bx = SkillFactory.getSkill(41120006);// Willow Dodge 2
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        avoidabilityRate += eff.getPercentAvoid();//dodgeChance += eff.getER();
                    }
                }
                if (chra.getJob() >= 4110) {
                    bx = SkillFactory.getSkill(41100006); // Unfaltering Blade
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        accuracy += eff.getX();
                        passive_sharpeye_percent += eff.getCriticalMax();
                        passive_sharpeye_min_percent += eff.getCriticalMin();
                    }
                }
                if (chra.getJob() >= 4100) {
                    bx = SkillFactory.getSkill(41000003); // Center Ki
                    bof = chra.getTotalSkillLevel(bx);
                    if (bof > 0) {
                        eff = bx.getEffect(bof);
                        localstr += eff.getStrX();
                        localdex += eff.getDexX();
                    }
                }
                break;
        }
        bx = SkillFactory.getSkill(80000000);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint_ += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getHpR();
            percent_mp += eff.getMpR();
        }
        bx = SkillFactory.getSkill(80000001);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            bossdam_r += eff.getBossDamage();
        }
        if (GameConstants.isExplorer(chra.getJob())) {
            bx = SkillFactory.getSkill(74);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(80);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10074);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10080);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }

            bx = SkillFactory.getSkill(10110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }
        }
        bx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getX();
        }

        bx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getZ();
        }
        switch (chra.getJob()) {
            case 210:
            case 211:
            case 212: { // IL
                bx = SkillFactory.getSkill(2100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dotTime += eff.getX();
                    dot += eff.getZ();
                }
                bx = SkillFactory.getSkill(2110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r += eff.getY();
                    bossdam_r += eff.getY();
                }
                bx = SkillFactory.getSkill(2121003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(2111003, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(2120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2121005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    terR += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(2121009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getX() * eff.getY();
                    bossdam_r += eff.getX() * eff.getY();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 220:
            case 221:
            case 222: { // IL
                bx = SkillFactory.getSkill(2200007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2210000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dot += bx.getEffect(bof).getZ();
                }
                bx = SkillFactory.getSkill(2210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r += eff.getY();
                    bossdam_r += eff.getY();
                }
                bx = SkillFactory.getSkill(2220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2221005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    terR += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(2221009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getX() * eff.getY();
                    bossdam_r += eff.getX() * eff.getY();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 1211:
            case 1212: { // flame
                bx = SkillFactory.getSkill(12110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r += eff.getY();
                    bossdam_r += eff.getY();
                }

                bx = SkillFactory.getSkill(12111004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    terR += bx.getEffect(bof).getTERRate();
                }
                break;
            }
            case 230:
            case 231:
            case 232: { // Bishop
                bx = SkillFactory.getSkill(2300007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2310008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    crit_rate += bx.getEffect(bof).getCr();
                }
                bx = SkillFactory.getSkill(2320010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2321010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2320005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    asrR += bx.getEffect(bof).getASRRate();
                }
                bx = SkillFactory.getSkill(2320011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getX() * eff.getY();
                    bossdam_r += eff.getX() * eff.getY();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 2002:
            case 2300:
            case 2310:
            case 2311:
            case 2312: { // Mercedes                                
                bx = SkillFactory.getSkill(20020112);
                bof = chra.getSkillLevel(bx);
                if (bof > 0) {
                    chra.getTrait(MapleTraitType.charm).addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
                }
                bx = SkillFactory.getSkill(23000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(23100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(23110004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23110004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(23101001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(23121004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(23120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getX(); //or should we do 100?
                }
                bx = SkillFactory.getSkill(23120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(23101001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(23120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: {
                bx = SkillFactory.getSkill(13000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(13110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(13110003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312: {
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3100006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(3001004, eff.getX());
                    damageIncrease.put(3001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3110007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            }
            case 320:
            case 321:
            case 322: {
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3200006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(3001004, eff.getX());
                    damageIncrease.put(3001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(3211006, bx.getEffect(bof).getDamage() - 150);
                }
                bx = SkillFactory.getSkill(3210007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                break;
            }
            case 422: {
                bx = SkillFactory.getSkill(4221007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Savage Blow, Steal, and Assaulter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(4201005, (int) eff.getDAMRate());
                    damageIncrease.put(4201004, (int) eff.getDAMRate());
                    damageIncrease.put(4211002, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(4210012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mesoBuff *= (eff.getMesoRate() + 100.0) / 100.0;
                    pickRate += eff.getU();
                    mesoGuard -= eff.getV();
                    mesoGuardMeso -= eff.getW();
                    damageIncrease.put(4211006, eff.getX());
                }
                break;
            }
            case 433:
            case 434: {
                bx = SkillFactory.getSkill(4330007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverProp += eff.getProb();
                    hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(4341002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Fatal Blow, Slash Storm, Tornado Spin, Bloody Storm, Upper Stab, and Flying Assaulter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(4311002, (int) eff.getDAMRate());
                    damageIncrease.put(4311003, (int) eff.getDAMRate());
                    damageIncrease.put(4321000, (int) eff.getDAMRate());
                    damageIncrease.put(4321001, (int) eff.getDAMRate());
                    damageIncrease.put(4331000, (int) eff.getDAMRate());
                    damageIncrease.put(4331004, (int) eff.getDAMRate());
                    damageIncrease.put(4331005, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    avoidabilityRate += bx.getEffect(bof).getER();
                }
                break;
            }
            case 2110:
            case 2111:
            case 2112: { // Aran
                bx = SkillFactory.getSkill(21101006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDAMRate();
                    bossdam_r += eff.getDAMRate();
                }
                bx = SkillFactory.getSkill(21110002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(21000004, bx.getEffect(bof).getW());
                }
                bx = SkillFactory.getSkill(21111010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(21120002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(21100007, bx.getEffect(bof).getZ());
                }
                bx = SkillFactory.getSkill(21120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(21100002, (int) eff.getDAMRate());
                    damageIncrease.put(21110003, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(Aran.DRAIN); // Aran Drain grants +% max hp
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(Aran.HIGH_DEFENSE); // Aran High Defense grants +% max hp
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 2700:  // Luminious
            case 2710:
            case 2711:
            case 2712: {
                bx = SkillFactory.getSkill(Luminous.STANDARD_MAGIC_GUARD);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);

                    float percentDamageAffectsMP = eff.getX() / 100.0f;
                    standardMagicGuard = percentDamageAffectsMP;

                }
                bx = SkillFactory.getSkill(Luminous.MANA_WELL);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_mp += eff.getPercentMP();
                }
                break;
            }
            case 3511:
            case 3512: {
                bx = SkillFactory.getSkill(35110014);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //ME-07 Drillhands, Atomic Hammer
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35001003, (int) eff.getDAMRate());
                    damageIncrease.put(35101003, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(35121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Satellite
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35111001, (int) eff.getDAMRate());
                    //   damageIncrease.put(35111009, (int) eff.getDAMRate());
                    //   damageIncrease.put(35111010, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(35120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Satellite
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35111005, eff.getX());
                    damageIncrease.put(35111011, eff.getX());
                    damageIncrease.put(35121009, eff.getX());
                    damageIncrease.put(35121010, eff.getX());
                    damageIncrease.put(35121011, eff.getX());
                    BuffUP_Summon += eff.getY();
                }
                break;
            }
            case 110:
            case 111:
            case 112: {
                bx = SkillFactory.getSkill(1100009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                bx = SkillFactory.getSkill(1120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(1120013);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    damageIncrease.put(1100002, (int) eff.getDamage());
                }
                break;
            }
            case 120:
            case 121:
            case 122: {
                bx = SkillFactory.getSkill(1200009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1220006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    asrR += bx.getEffect(bof).getASRRate();
                    terR += bx.getEffect(bof).getTERRate();
                }
                break;
            }
            case 511:
            case 512: {
                bx = SkillFactory.getSkill(5110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Backspin Blow, Double Uppercut, and Corkscrew Blow
                    eff = bx.getEffect(bof);
                    damageIncrease.put(5101002, eff.getX());
                    damageIncrease.put(5101003, eff.getY());
                    damageIncrease.put(5101004, eff.getZ());
                }
                break;
            }
            case 520:
            case 521:
            case 522: {
                bx = SkillFactory.getSkill(5220001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Flamethrower and Ice Splitter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(5211004, (int) eff.getDamage());
                    damageIncrease.put(5211005, (int) eff.getDamage());
                }
                break;
            }
            case 130:
            case 131:
            case 132: {
                bx = SkillFactory.getSkill(1300009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1310009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    crit_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                    hpRecoverProp += eff.getProb();
                    hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(1320006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                }
                break;
            }
            case 1400:
            case 1410:
            case 1411:
            case 1412: { // Night Walker
                bx = SkillFactory.getSkill(14110003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    RecoveryUP += eff.getX() - 100;
                    BuffUP += eff.getY() - 100;
                }
                bx = SkillFactory.getSkill(14000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                /*bx = SkillFactory.getSkill(NightWalker.VITALITY_SIPHON);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localmaxhp_ += 40 * bof; //40 HP per level, 400 at level 10.
                }*/
                break;
            }
            case 5000: // Mihile 0
            case 5100: // Mihile 1
            case 5110: // Mihile 2
            case 5111: // Mihile 3
            case 5112: { // Mihile 4
                // Mihile 1st Job Passive Skills
                bx = SkillFactory.getSkill(51000000); // Mihile || HP Boost
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(51000001); // Mihile || Soul Shield
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getX();
                    percent_mdef += eff.getX();
                }
                bx = SkillFactory.getSkill(51000002); // Mihile || Soul Devotion
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    accuracy += eff.getAccX();
                    speed += eff.getPassiveSpeed();
                    jump += eff.getPassiveJump();
                }

                // Mihile 2nd Job Passive Skills
                bx = SkillFactory.getSkill(51100000); // Mihile || Physical Training
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(5001002, eff.getX());
                    damageIncrease.put(5001003, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(51120002); // Mihile || Final Attack && Advanced Final Attack
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    damageIncrease.put(51100002, (int) eff.getDamage());
                }

                // Mihile 3rd Job Passive Skills
                bx = SkillFactory.getSkill(51110000); // Mihile || Self Recovery
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverProp += eff.getProb();
                    hpRecover += eff.getX();
                    mpRecoverProp += eff.getProb();
                    mpRecover += eff.getX();
                }
                bx = SkillFactory.getSkill(51110001); // Mihile || Intense Focus
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    // Add Attack Speed here
                }
                bx = SkillFactory.getSkill(51110002); // Mihile || Righteous Indignation
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    asrR += eff.getX();
                    percent_atk += eff.getX();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }

                // Mihile 4th Job Passive Skills
                bx = SkillFactory.getSkill(51120000); // Mihile || Combat Mastery
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(51120001); // Mihile || Expert Sword Mastery
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += bx.getEffect(bof).getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(51120003); // Mihile || Soul Asylum
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
                break;
            }
        }
        if (GameConstants.isResistance(chra.getJob())) {
            bx = SkillFactory.getSkill(30000002);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                RecoveryUP += bx.getEffect(bof).getX() - 100;
            }
        }
    }

    private void handleHyperStatPassive(MapleCharacter chra) {
        for (int hyperSkill : _HyperStatSkills.ALL_HYPER_STATS) {
            Skill skill = SkillFactory.getSkill(hyperSkill);
            int currentSkillLevel = chra.getSkillLevel(skill);

            if (currentSkillLevel <= 0) {
                continue;
            }
            MapleStatEffect effect = skill.getEffect(currentSkillLevel);

            for (Map.Entry<MapleStatInfo, Integer> info : effect.getAllStatInfo().entrySet()) {
                if (info.getValue() <= 0) {
                    continue;
                }
                //        chra.dropMessage(5, info.getKey().toString() + " " + info.getValue());
                switch (info.getKey()) {
                    case strFX:
                        localstr += info.getValue();
                        break;
                    case dexFX:
                        localdex += info.getValue();
                        break;
                    case intFX:
                        localint_ += info.getValue();
                        break;
                    case lukFX:
                        localluk += info.getValue();
                        break;
                    case mmpR:
                        percent_mp += info.getValue();
                        break;
                    case mhpR:
                        percent_hp += info.getValue();
                        break;
                    case MDF:
                        dark_force += info.getValue();
                        break;
                    case psdSpeed:
                        speed += info.getValue();
                        break;
                    case psdJump:
                        jump += info.getValue();
                        break;
                    case cr:
                        this.crit_rate += info.getValue();
                        break;
                    case criticaldamageMin:
                        passive_sharpeye_min_percent += info.getValue();
                        break;
                    case criticaldamageMax:
                        passive_sharpeye_percent += info.getValue();
                        break;
                    case ignoreMobpdpR:
                        ignoreTargetDEF += info.getValue();
                        break;
                    case damR:
                        this.dam_r += info.getValue();
                        break;
                    case bdR:
                        this.bossdam_r += info.getValue();
                        break;
                    case terR: // ELEMENTAL_RESISTANCE_RATE
                        this.terR += info.getValue();
                        break;
                    case asrR: // ABNORMAL_RESISTANCE_RATE
                        this.asrR += info.getValue();
                        break;
                    case stanceProp:
                        break;
                }
            }
        }
    }

    private void handleHyperPassiveSkills(MapleCharacter chra) {
        int prefix = chra.getJob() * 10000;
        Skill bx;
        int bof;
        MapleStatEffect eff;
        for (int i = 30; i < 50; i++) {
            int skillid = prefix + i;
            bx = SkillFactory.getSkill(skillid);
            bof = chra.getSkillLevel(bx);

            if (bx != null && bx.isHyper() && bof > 0) {
                eff = bx.getEffect(bof);
                if (eff != null) {
                    switch (i) {
                        case 30:
                            localstr += eff.getStrX();
                            break;
                        case 31:
                            localdex += eff.getDexX();
                            break;
                        case 32:
                            localint_ += eff.getIntX();
                            break;
                        case 33:
                            localluk += eff.getLukX();
                            break;
                        case 34:
                            crit_rate += eff.getCr();
                            break;
                        case 35:
                            accuracy += eff.getAccR();
                            break;
                        case 36:
                            percent_hp += eff.getPercentHP();
                            break;
                        case 37:
                            percent_mp += eff.getPercentMP();
                            break;
                        case 38:
                            localmaxmp_ += eff.getMaxDemonFury();
                            break;
                        case 39:
                            wdef += eff.getPDDX();
                            break;
                        case 40:
                            mdef += eff.getMDDX();
                            break;
                        case 41:
                            speed += eff.getSpeed();
                            break;
                        case 42:
                            jump += eff.getJump();
                            break;
                    }

                    Tuple<String, String, String> skillName = MapleStringInformationProvider.getSkillStringCache().get(skillid);

                    if (skillName != null) {
                        int skill = GameConstants.findSkillByName(skillName.get_2().split(" - ")[0], prefix, 0);
                        if (skill != 0) {
                            Skill skil = SkillFactory.getSkill(skill);
                            if (skil != null && chra.getSkillLevel(skil) > 0) {
                                if (eff.getDAMRate() > 0) {
                                    //skil.getEffect(chra.getSkillLevel(skil)).setDAMRate(eff.getDAMRate());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleTemporaryStats(MapleCharacter chra) {
        for (Map.Entry<CharacterTemporaryStat, CharacterTemporaryStatValueHolder> buffedValue
                : chra.getBuffedValuesPlayerStats()) {
            final MapleStatEffect eff = buffedValue.getValue().effect;
            final Integer buff = buffedValue.getValue().value;

            if (eff == null) {
                continue;
            }

            switch (buffedValue.getKey()) {
                case RideVehicle: {
                    if (eff.getSourceId() == 33001001) { // jaguar
                        crit_rate += eff.getW();
                        percent_hp += eff.getZ();
                    }
                    break;
                }
                case Dice: {
                    percent_wdef += GameConstants.getDiceStat(buff, 2);
                    percent_mdef += GameConstants.getDiceStat(buff, 2);
                    percent_hp += GameConstants.getDiceStat(buff, 3);
                    percent_mp += GameConstants.getDiceStat(buff, 3);
                    crit_rate += GameConstants.getDiceStat(buff, 4);
                    dam_r += GameConstants.getDiceStat(buff, 5);
                    bossdam_r += GameConstants.getDiceStat(buff, 5);
                    expBuff *= (GameConstants.getDiceStat(buff, 6) + 100.0) / 100.0;
                    break;
                }
                case MagicGuard: {
                    magic_guard_rate += buff / 100.0d;
                    break;
                }
                case IncMaxHP: {
                    percent_hp += buff;
                    break;
                }
                case IncMaxMP: {
                    percent_mp += buff;
                    break;
                }
                case AsrR: {
                    asrR += buff;
                    break;
                }
                case TerR: {
                    terR += buff;
                    break;
                }
                case Infinity: {
                    percent_matk += buff - 1;
                    break;
                }
                case OnixDivineProtection: {
                    avoidabilityRate += buff;
                    break;
                }
                case PVPDamage: {
                    pvpDamage += buff;
                    break;
                }
                case PvPScoreBonus: {
                    pvpDamage += buff;
                    break;
                }
                case IndieBooster: {
                    speed += buff;
                    //percent_hp += buff; // percent_hp? This has to be a mistake.
                    break;
                }
                //case BLUE_AURA: {
                //   percent_wdef += eff.getZ() + eff.getY();
                //  percent_mdef += eff.getZ() + eff.getY();
                // break;
                //}
                case Conversion: {
                    percent_hp += buff;
                    break;
                }
                case IndieMHP: {
                    percent_hp += buff;
                    break;
                }
                case IndieMMP: {
                    percent_mp += buff;
                    break;
                }
                case MaxMP: {
                    percent_mp += buff;
                    break;
                }
                //case BUFF_MASTERY: { //idk
                //   BuffUP_Skill += buff;
                //  break;
                //}
                case STR: {
                    localstr += buff;
                    break;
                }
                case DEX: {
                    localdex += buff;
                    break;
                }
                case LUK: {
                    localint_ += buff;
                    break;
                }
                case INT: {
                    localluk += buff;
                    break;
                }
                case IndieAllStat: {
                    localstr += buff;
                    localdex += buff;
                    localint_ += buff;
                    localluk += buff;
                    break;
                }
                case EPDD: {
                    wdef += buff;
                    break;
                }
                case IndiePADR: {
                    wdef += buff;
                    break;
                }
                case BasicStatUp: {
                    final double d = buff.doubleValue() / 100.0;
                    localstr += d * str; //base only
                    localdex += d * dex;
                    localluk += d * luk;
                    localint_ += d * int_;
                    break;
                }
                case MaxLevelBuff: {
                    final double d = buff.doubleValue() / 100.0;
                    watk += (int) (watk * d);
                    magic += (int) (magic * d);
                    break;
                }
                case ComboAbilityBuff: {
                    watk += buff / 10;
                    break;
                }
                case MesoGuard: {
                    mesoGuardMeso += buff.doubleValue();
                    break;
                }
                case ExpBuffRate: {
                    expBuff *= buff.doubleValue() / 100.0;
                    break;
                }
                case IndieEXP: {
                    indieExpBuff += buff.doubleValue();
                    break;
                }
                case DropRate: {
                    dropBuff *= buff.doubleValue() / 100.0;
                    break;
                }
                case ACASH_RATE: {
                    cashBuff *= buff.doubleValue() / 100.0;
                    break;
                }
                case MesoUp: {
                    mesoBuff *= buff.doubleValue() / 100.0;
                    break;
                }
                case IndiePAD: {
                    watk += buff;
                    break;
                }
                case IndieMAD: {
                    magic += buff;
                    break;
                }
                case PAD: {
                    watk += buff;
                    break;
                }
                case DamR: {
                    dam_r += buff;
                    bossdam_r += buff;
                    break;
                }
                case EPAD: {
                    watk += buff;
                    break;
                }
                case EnergyCharged: {
                    watk += eff.getWatk();
                    accuracy += eff.getAcc();
                    break;
                }
                case MAD: {
                    magic += buff;
                    break;
                }
                case Speed:
                case DashSpeed: {
                    speed += buff;
                    break;
                }
                case Jump:
                case DashJump: {
                    jump += buff;
                    break;
                }
                case NoDebuff: {
                    crit_rate = 100; //INTENSE
                    asrR = 100; //INTENSE

                    wdef += eff.getX();
                    mdef += eff.getX();
                    watk += eff.getX();
                    magic += eff.getX();
                    break;
                }
                case AssistCharge: {
                    dam_r += buff.doubleValue();
                    bossdam_r += buff.doubleValue();
                    break;
                }
                case FinalCut: {
                    dam_r += buff.doubleValue();
                    bossdam_r += buff.doubleValue();
                    break;
                }
                case HowlingAttackDamage: {
                    dam_r += buff.doubleValue();
                    bossdam_r += buff.doubleValue();
                    break;
                }
                case PinkbeanAttackBuff: {
                    dam_r += buff.doubleValue();
                    bossdam_r += buff.doubleValue();
                    break;
                }
                case Bless: {
                    watk += eff.getX();
                    magic += eff.getY();
                    accuracy += eff.getV();
                    break;
                }
                case Concentration: {
                    mpconReduce += buff;
                    break;
                }
                case AdvancedBless: {
                    watk += eff.getX();
                    magic += eff.getY();
                    accuracy += eff.getV();
                    mpconReduce += eff.getMPConReduce();
                    break;
                }
                case MagicResistance: {
                    asrR += eff.getX();
                    break;
                }
                case ComboCounter: {
                    dam_r += (eff.getV() + eff.getDAMRate()) * (buff - 1);
                    bossdam_r += (eff.getV() + eff.getDAMRate()) * (buff - 1);
                    break;
                }
                case SUMMON: {
                    if (eff.getSourceId() == 35121010) { //amp
                        dam_r += eff.getX();
                        bossdam_r += eff.getX();
                    }
                    break;
                }
                //TODO: handle colors
                //It's all 1 flag now
                case BMageAura: {
                    dam_r += eff.getX();
                    bossdam_r += eff.getX();
                    break;
                }
                case Beholder: {
                    trueMastery += eff.getMastery();
                    break;
                }
                case Mechanic: {
                    crit_rate += eff.getCr();
                    break;
                }
                case PyramidEffect: {
                    dam_r += eff.getBerserk();
                    bossdam_r += eff.getBerserk();
                    break;
                }
                case WeaponCharge: {
                    dam_r += eff.getDamage();
                    bossdam_r += eff.getDamage();
                    break;
                }
                case PickPocket: {
                    pickRate = eff.getProb();
                    break;
                }
                case BlessingArmor: {
                    watk += eff.getEnhancedWatk();
                    break;
                }
                case DarkSight: {
                    dam_r += buff;
                    bossdam_r += buff;
                    break;
                }
                case Enrage: {
                    dam_r += buff;
                    bossdam_r += buff;
                    break;
                }
                case CombatOrders: {
                    combatOrders += buff;
                    break;
                }
                case SharpEyes: {
                    crit_rate += eff.getX();
                    passive_sharpeye_percent += eff.getCriticalMax();
                    break;
                }
                case HowlingCritical: {
                    crit_rate += buff;
                    break;
                }
                case IncMaxDamage: {
                    damageCapIncrease += buff;
                    break;
                }
            }
        }

        // Cap character speed at max
        if (jump > 123) {
            jump = 123;
        }

        //Map is null when entering CS or Farm. In which case we skip this.
        if (chra.getMap() != null
                && chra.getMap().getSharedMapResources() != null
                && chra.getMap().getSharedMapResources().forcedSpeed <= 0) {
            if (speed > 140) {
                speed = 140;
            }
            // Monster riding speed have to be handled last, because it overrides character, and the cap of 140
            Integer monsterRidingbuff = chra.getBuffedValue(CharacterTemporaryStat.RideVehicle);
            if (monsterRidingbuff != null) {
                jump = 120;
                switch (monsterRidingbuff) {
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
                        speed = 200; //lol
                        break;
                }
            }
        } else if (chra.getMap() != null //Cs check.. zzz
                && chra.getMap().getSharedMapResources() != null) {
            speed = chra.getMap().getSharedMapResources().forcedSpeed; // Used in maps such as Shinsoo School Road
        }
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
     * Calculates the sttack damage rate the player is allowed to attack, according to its star force vs map barrier req long double __cdecl
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

    public boolean checkEquipLevels(final MapleCharacter chr, long gain) {
        boolean changed = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(Math.min(eq.getItemEXP() + gain, Long.MAX_VALUE));

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
                            if (skil != null && skil.canBeLearnedBy(chr.getJob())) { //dont go over masterlevel :D
                                eq.setIncSkill(skil.getId());

                                Tuple<String, String, String> skillName = MapleStringInformationProvider.getSkillStringCache().get(zzz);
                                if (skillName != null) {
                                    chr.dropMessage(5, "Your skill has gained a levelup: " + skillName.get_2() + " +1");
                                }
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            chr.equipChanged();
            chr.getClient().write(EffectPacket.showForeignEffect(EffectPacket.UserEffectCodes.ItemLevelup));
            chr.getMap().broadcastMessage(chr, EffectPacket.showForeignEffect(chr.getId(), EffectPacket.UserEffectCodes.ItemLevelup), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain) {
        return checkEquipDurabilitys(chr, gain, false);
    }

    public boolean checkEquipDurabilitys(final MapleCharacter chr, int gain, boolean aboveZero) {
        if (chr.inPVP()) {
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
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    List<ModifyInventory> mod = new ArrayList<>();
                    chr.getClient().write(CWvsContext.inventoryOperation(true, mod));
                    return false;
                }
                durabilityHandling.remove(eqq);
                final short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }

    private void calcPassive_SharpEye(final MapleCharacter player) {
        Skill critSkill;
        int critlevel;
        if (GameConstants.isDemonSlayer(player.getJob())) {
            critSkill = SkillFactory.getSkill(30010022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isMercedes(player.getJob())) {
            critSkill = SkillFactory.getSkill(20020022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isResistance(player.getJob())) {
            critSkill = SkillFactory.getSkill(30000022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                crit_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        }
        switch (player.getJob()) { // Apply passive Critical bonus
            case 410: // Assasin
            case 411: // Hermit
            case 412: { // Night Lord
                critSkill = SkillFactory.getSkill(4100001); // Critical Throw
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2412: { // Phantom
                critSkill = SkillFactory.getSkill(24120006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 1410:
            case 1411:
            case 1412: { // Night Walker
                critSkill = SkillFactory.getSkill(14100001);
                critlevel = player.getTotalSkillLevel(critSkill);
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
                critSkill = SkillFactory.getSkill(31100006); //TODO LEGEND, not final
                critlevel = player.getTotalSkillLevel(critSkill);
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
                critSkill = SkillFactory.getSkill(23000003);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3210:
            case 3211:
            case 3212: {
                critSkill = SkillFactory.getSkill(32100006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 434: {
                critSkill = SkillFactory.getSkill(4340010);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 520:
            case 521:
            case 522: {
                critSkill = SkillFactory.getSkill(5200007);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1211:
            case 1212: {
                critSkill = SkillFactory.getSkill(12110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 530:
            case 531:
            case 532: {
                critSkill = SkillFactory.getSkill(5300004);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 510:
            case 511:
            case 512: { // Buccaner, Viper
                critSkill = SkillFactory.getSkill(5110000);
                critlevel = player.getTotalSkillLevel(critSkill);
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
                critSkill = SkillFactory.getSkill(15110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2111:
            case 2112: {
                critSkill = SkillFactory.getSkill(21110000);
                critlevel = player.getTotalSkillLevel(critSkill);
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
                critSkill = SkillFactory.getSkill(3000001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: { // Bowman
                critSkill = SkillFactory.getSkill(13000000);
                critlevel = player.getTotalSkillLevel(critSkill);
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
                critSkill = SkillFactory.getSkill(22140000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.crit_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
        }
    }

    private void calcPassiveMasteryAmount(final MapleCharacter player) {
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11) == null) {
            passive_mastery = 0;
            return;
        }
        final int skil;
        final MapleWeaponType weaponType = GameConstants.getWeaponType(player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11).getItemId());
        boolean acc = true;
        switch (weaponType) {
            case BOW:
                skil = GameConstants.isCygnusKnight(player.getJob()) ? 13100000 : 3100000;
                break;
            case CLAW:
                skil = 4100000;
                break;
            case CANE:
                skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
                break;
            case CANNON:
                skil = 5300005;
                break;
            case KATARA:
            case DAGGER:
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            case CROSSBOW:
                skil = GameConstants.isResistance(player.getJob()) ? 33100000 : 3200000;
                break;
            case AXE1H:
            case BLUNT1H:
                skil = GameConstants.isResistance(player.getJob()) ? 31100004 : (GameConstants.isCygnusKnight(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000)); //hero/pally
                break;
            case AXE2H:
            case SWORD1H:
            case SWORD2H:
            case BLUNT2H:
                skil = GameConstants.isCygnusKnight(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000); //hero/pally
                break;
            case POLE_ARM:
                skil = GameConstants.isAran(player.getJob()) ? 21100000 : 1300000;
                break;
            case SPEAR:
                skil = 1300000;
                break;
            case KNUCKLE:
                skil = GameConstants.isCygnusKnight(player.getJob()) ? 15100001 : 5100001;
                break;
            case GUN:
                skil = GameConstants.isResistance(player.getJob()) ? 35100000 : (GameConstants.isJett(player.getJob()) ? 5700000 : 5200000);
                break;
            case DUAL_BOW:
                skil = 23100005;
                break;
            case WAND:
            case STAFF:
                acc = false;
                skil = GameConstants.isResistance(player.getJob()) ? 32100006 : (player.getJob() <= 212 ? 2100006 : (player.getJob() <= 222 ? 2200006 : (player.getJob() <= 232 ? 2300006 : (player.getJob() <= 2000 ? 12100007 : 22120002))));
                break;
            default:
                passive_mastery = 0;
                return;

        }
        if (player.getSkillLevel(skil) <= 0) {
            passive_mastery = 0;
            return;
        }// TODO: add job id check above skill, etc
        final MapleStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
        if (acc) {
            accuracy += eff.getX();
            if (skil == 35100000) {
                watk += eff.getX();
            }
        } else {
            magic += eff.getX();
        }
        crit_rate += eff.getCr();
        passive_mastery = (byte) eff.getMastery();
        trueMastery += eff.getMastery() + weaponType.getBaseMastery();
        if (player.getJob() == 412) {
            final Skill bx = SkillFactory.getSkill(4120012); // Claw Expert
            final int bof = player.getTotalSkillLevel(bx);
            if (bof > 0) {
                final MapleStatEffect eff2 = bx.getEffect(bof);
                passive_mastery = (byte) eff2.getMastery(); // Override
                accuracy += eff2.getPercentAcc();
                avoidabilityRate += eff2.getPercentAvoid();
                watk += eff2.getX();
                trueMastery -= eff.getMastery(); // - old
                trueMastery += eff2.getMastery(); // add new
            }
        }
    }

    private void calculateFame(final MapleCharacter player) {
        player.getTrait(MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            player.getTrait(t).recalcLevel();
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

    public final void calculateMaxBaseDamage(final int watk, final int pvpDamage, MapleCharacter chra) {
        if (watk <= 0) {
            localmaxbasedamage = 1;
            localmaxbasepvpdamage = 1;
        } else {
            final Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            final Item weapon_item2 = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
            final int job = chra.getJob();
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

    public final void relocHeal(MapleCharacter chra) {
        final int playerjob = chra.getJob();

        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = /*GameConstants.isDemonSlayer(chra.getJob()) ? 0 : */(3 + recoverMP + (localint_ / 10)); // i think
        mpRecoverTime = 0;
        hpRecoverTime = 0;
        if (playerjob == 111 || playerjob == 112) {
            final Skill effect = SkillFactory.getSkill(1110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                MapleStatEffect eff = effect.getEffect(lvl);
                if (eff.getHp() > 0) {
                    shouldHealHP += eff.getHp();
                    hpRecoverTime = 4000;
                }
                shouldHealMP += eff.getMp();
                mpRecoverTime = 4000;
            }

        } else if (playerjob == 1111 || playerjob == 1112) {
            final Skill effect = SkillFactory.getSkill(11110000); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
                mpRecoverTime = 4000;
            }
        } else if (GameConstants.isMercedes(playerjob)) {
            final Skill effect = SkillFactory.getSkill(20020109); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += (effect.getEffect(lvl).getX() * localmaxhp) / 100;
                hpRecoverTime = 4000;
                shouldHealMP += (effect.getEffect(lvl).getX() * localmaxmp) / 100;
                mpRecoverTime = 4000;
            }
        } else if (GameConstants.isJett(playerjob) && playerjob != 508) {
            final Skill effect = SkillFactory.getSkill(5700005); // Perseverance
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                final MapleStatEffect eff = effect.getEffect(lvl);
                shouldHealHP += eff.getX();
                shouldHealMP += eff.getX();
                hpRecoverTime = eff.getY();
                mpRecoverTime = eff.getY();
            }
        } else if (playerjob == 3111 || playerjob == 3112) {
            final Skill effect = SkillFactory.getSkill(31110009); // Improving MP Recovery
            final int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getY();
                mpRecoverTime = 4000;
            }
        }
        if (chra.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else if (chra.getMap() != null) { // Because Heal isn't multipled when there's a chair :)
            final float recvRate = chra.getMap().getSharedMapResources().recoveryRate;
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
        oPacket.EncodeInteger(hp);
        oPacket.EncodeInteger(maxhp);
        oPacket.EncodeInteger(mp);
        oPacket.EncodeInteger(maxmp);
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

    public void heal_noUpdate(MapleCharacter chra) {
        setHp(getCurrentMaxHp(), chra);
        setMp(getCurrentMaxMp(chra.getJob()), chra);
    }

    public void heal(MapleCharacter chra) {
        heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, getCurrentMaxHp());
        chra.updateSingleStat(MapleStat.MP, getCurrentMaxMp(chra.getJob()));
    }

    public Pair<Integer, Integer> handleEquipAdditions(MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> sData, final int itemId) {
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
                    if (add.getMid().equals("elemVol") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
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
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJob = true;
                                }
                            }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                            canJob = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null) {
                        if (chra.getLevel() >= Integer.parseInt(level)) {
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
                    if (add.getMid().equals("damage") && (craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
                        bossdam_r += right;
                    }
                    break;
                case "mobdie": // lv, hpIncRatioOnMobDie, hpRatioProp, mpIncRatioOnMobDie, mpRatioProp, modify =D, don't need mob to die
                    craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                    if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
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
                        if ((craft == null || craft != null && chra.getTrait(MapleTraitType.craft).getLocalTotalExp() >= Integer.parseInt(craft))) {
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
                                if (chra.getJob() == Integer.parseInt(x)) {
                                    canJobx = true;
                                }
                            }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                            canJobx = true;
                        }
                    }
                    level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                    if (level != null && chra.getLevel() >= Integer.parseInt(level)) {
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

    public final void handleProfessionTool(final MapleCharacter chra) {
        if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
            final Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) { //goes to first harvesting tool and stops
                final Equip equip = (Equip) itera.next();
                if (equip.getDurability() != 0 && (equip.getItemId() / 10000 == 150 && chra.getProfessionLevel(92000000) > 0) || (equip.getItemId() / 10000 == 151 && chra.getProfessionLevel(92010000) > 0)) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }

    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
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

    public final void setStr(final short str, MapleCharacter chra) {
        this.str = str;
        recalcLocalStats(chra);
    }

    public final void setDex(final short dex, MapleCharacter chra) {
        this.dex = dex;
        recalcLocalStats(chra);
    }

    public final void setInt(final short int_, MapleCharacter chra) {
        this.int_ = int_;
        recalcLocalStats(chra);
    }

    public final void setLuk(final short luk, MapleCharacter chra) {
        this.luk = luk;
        recalcLocalStats(chra);
    }

    public final boolean setHp(final int newhp, MapleCharacter chra) {
        return setHp(newhp, false, chra);
    }

    public boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        final int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;

        if (chra != null) {
            if (!silent) {
                chra.checkBerserk();
                chra.updatePartyMemberHP();
            }
            if (oldHp > hp && !chra.isAlive()) {
                chra.playerDead();
            }
        }
        if (GameConstants.isDemonAvenger(chra.getJob())) {
            chra.getClient().write(JobPacket.AvengerPacket.giveAvengerHpBuff(hp));
        }
        return hp != oldHp;
    }

    public final boolean setMp(final int newmp, final MapleCharacter chra) {
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

    public final void setMaxHp(final int hp, MapleCharacter chra) {
        this.maxhp = hp;
        recalcLocalStats(chra);
    }

    public final void setMaxMp(final int mp, MapleCharacter chra) {
        this.maxmp = mp;
        recalcLocalStats(chra);
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
