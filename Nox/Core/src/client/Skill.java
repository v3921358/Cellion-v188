package client;

import com.mysql.jdbc.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import constants.GameConstants;
import constants.skills.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.life.Element;
import tools.Pair;
import tools.Utility;

public class Skill implements Comparator<Skill> {

    private String psdDamR = "", targetPlus = "";
    private final List<MapleStatEffect> effects = new ArrayList<>();
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private final List<Pair<String, Integer>> requiredSkill = new ArrayList<>();
    private Element element = Element.NEUTRAL;
    private int id, animationTime = 0, masterLevel = 0, maxLevel = 0, delay = 0, trueMax = 0, eventTamingMob = 0, skillTamingMob = 0, skillType = 0, psd = 0, psdSkill = 0; //4 is alert
    private boolean invisible = false, chargeskill = false, timeLimited = false, combatOrders = false, pvpDisabled = false, magic = false, casterMove = false, pushTarget = false, pullTarget = false;
    private int hyper = 0;

    public Skill(final int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Skill loadFromData(final int id, final MapleData data, final MapleData delayData) {
        Skill ret = new Skill(id);
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.combatOrders = MapleDataTool.getInt("combatOrders", data, 0) > 0;
        ret.hyper = MapleDataTool.getInt("hyper", data, 0);
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        ret.psd = MapleDataTool.getInt("psd", data, 0);
        if (ret.psd == 1) {
            final MapleData psdskill = data.getChildByPath("psdSkill");
            if (psdskill != null) {
                ret.psdSkill = Integer.parseInt(data.getChildByPath("psdSkill").getChildren().get(0).getName());
            }
        }
        if ((id == 22111001 || id == 22140000 || id == 22141002)) {
            ret.masterLevel = 5; //hack
        }
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        ret.setSkillTamingMob(MapleDataTool.getInt("skillTamingMob", data, 0));
        final MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = MapleDataTool.getInt("pvp", inf, 1) <= 0;
            ret.magic = MapleDataTool.getInt("magicDamage", inf, 0) > 0;
            ret.casterMove = MapleDataTool.getInt("casterMove", inf, 0) > 0;
            ret.pushTarget = MapleDataTool.getInt("pushTarget", inf, 0) > 0;
            ret.pullTarget = MapleDataTool.getInt("pullTarget", inf, 0) > 0;
        }
        final MapleData effect = data.getChildByPath("effect");
        switch (skillType) {
            case 3:
                //final attack
                ret.animation = new ArrayList<>();
                ret.animation.add(0);
                break;
            default:
                MapleData action_ = data.getChildByPath("action");
                final MapleData hit = data.getChildByPath("hit");
                final MapleData ball = data.getChildByPath("ball");
                boolean action = false;
                if (action_ == null) {
                    if (data.getChildByPath("prepare/action") != null) {
                        action_ = data.getChildByPath("prepare/action");
                        action = true;
                    }
                }
                if (action_ != null) {
                    String d;
                    if (action) { //prepare
                        d = MapleDataTool.getString(action_, null);
                    } else {
                        d = MapleDataTool.getString("0", action_, null);
                    }
                    if (d != null) {
                        final MapleData dd = delayData.getChildByPath(d);
                        if (dd != null) {
                            for (MapleData del : dd) {
                                ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                            }
                            if (ret.delay > 30) {
                                ret.delay = (int) Math.round(ret.delay * 11.0 / 16.0);
                                ret.delay -= (ret.delay % 30);
                            }
                        }
                        if (SkillFactory.getDelay(d) != null) { //this should return true always
                            ret.animation = new ArrayList<>();
                            ret.animation.add(SkillFactory.getDelay(d));
                            if (!action) {
                                for (MapleData ddc : action_) {
                                    if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                        String c = MapleDataTool.getString(ddc);
                                        if (SkillFactory.getDelay(c) != null) {
                                            ret.animation.add(SkillFactory.getDelay(c));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;

        MapleData commonDir = data.getChildByPath("common");
        MapleData levelDir = data.getChildByPath("level");
        if (levelDir == null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", commonDir, 1); //10 just a failsafe, shouldn't actually happens
            ret.psdDamR = MapleDataTool.getString("damR", commonDir, ""); //for the psdSkill tag
            ret.targetPlus = MapleDataTool.getString("targetPlus", commonDir, "");
            ret.trueMax = ret.maxLevel + (ret.combatOrders ? 2 : 0);

            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(commonDir, id, i, "x")); // omg
            }
        } else {
            for (final MapleData leve : levelDir) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }

        final MapleData level2 = data.getChildByPath("PVPcommon");
        if (level2 != null) {
            ret.pvpEffects = new ArrayList<>();
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, i, "x"));
            }
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair<>(reqData.getName(), MapleDataTool.getInt(reqData, 1)));
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(final int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) { //incAllskill
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return animation.get(Randomizer.nextInt(animation.size()));
    }

    public boolean isPVPDisabled() {
        return pvpDisabled;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return requiredSkill.size() > 0;
    }

    public int getPsdSkill() {
        return psdSkill;
    }

    public int getPsd() {
        return psd;
    }

    public String getPsdDamR() {
        return psdDamR;
    }

    public String getPsdtarget() {
        return targetPlus;
    }

    public List<Pair<String, Integer>> getRequiredSkills() {
        return requiredSkill;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTrueMax() {
        return trueMax;
    }

    public boolean combatOrders() {
        return combatOrders;
    }

    public boolean canBeLearnedBy(int job) { //test
//        if (GameConstants.getBeginnerJob((short) (id / 10000)) == GameConstants.getBeginnerJob((short) job))
//            return true;
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.isEvan(job); //special exception for beginner -.-
        } else if (skillForJob == 0) {
            return GameConstants.isExplorer(job); //special exception for beginner
        } else if (skillForJob == 1000) {
            return GameConstants.isCygnusKnight(job); //special exception for beginner
        } else if (skillForJob == 2000) {
            return GameConstants.isAran(job); //special exception for beginner
        } else if (skillForJob == 3000) {
            return GameConstants.isResistance(job); //special exception for beginner
        } else if (skillForJob == 1) {
            return GameConstants.isCannoneer(job); //special exception for beginner
        } else if (skillForJob == 3001) {
            return GameConstants.isDemonSlayer(job) || GameConstants.isDemonAvenger(job); //special exception for beginner
        } else if (skillForJob == 2002) {
            return GameConstants.isMercedes(job); //special exception for beginner
        } else if (skillForJob == 508) {
            return GameConstants.isJett(job); //special exception for beginner
        } else if (skillForJob == 2003) {
            return GameConstants.isPhantom(job); //special exception for beginner
        } else if (skillForJob == 5000) {
            return GameConstants.isMihile(job); //special exception for beginner
        } else if (skillForJob == 2004) {
            return GameConstants.isLuminous(job); //special exception for beginner
        } else if (skillForJob == 6000) {
            return GameConstants.isKaiser(job); //special exception for beginner
        } else if (skillForJob == 6001) {
            return GameConstants.isAngelicBuster(job); //special exception for beginner
        } else if (skillForJob == 3002) {
            return GameConstants.isXenon(job); //special exception for beginner
        } else if (skillForJob == 10000) {
            return GameConstants.isZero(job); //special exception for beginner
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isDemonAvenger(skillForJob) && !GameConstants.isDemonAvenger(job)) {
            return false;
        } else if (GameConstants.isXenon(skillForJob) && !GameConstants.isXenon(job)) {
            return false;
        } else if (GameConstants.isZero(skillForJob) && !GameConstants.isZero(job)) {
            return false;
        } else if (GameConstants.isBeastTamer(skillForJob) && !GameConstants.isBeastTamer(job)) {
            return false;
        } else if (GameConstants.isAngelicBuster(skillForJob) && !GameConstants.isAngelicBuster(job)) {
            return false;
        } else if (GameConstants.isKaiser(skillForJob) && !GameConstants.isKaiser(job)) {
            return false;
        } else if (GameConstants.isMihile(skillForJob) && !GameConstants.isMihile(job)) {
            return false;
        } else if (GameConstants.isLuminous(skillForJob) && !GameConstants.isLuminous(job)) {
            return false;
        } else if (GameConstants.isPhantom(skillForJob) && !GameConstants.isPhantom(job)) {
            return false;
        } else if (GameConstants.isJett(skillForJob) && !GameConstants.isJett(job)) {
            return false;
        } else if (GameConstants.isCannoneer(skillForJob) && !GameConstants.isCannoneer(job)) {
            return false;
        } else if (GameConstants.isDemonSlayer(skillForJob) && !GameConstants.isDemonSlayer(job)) {
            return false;
        } else if (GameConstants.isExplorer(skillForJob) && !GameConstants.isExplorer(job)) {
            return false;
        } else if (GameConstants.isCygnusKnight(skillForJob) && !GameConstants.isCygnusKnight(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isMercedes(skillForJob) && !GameConstants.isMercedes(job)) {
            return false;
        } else if (GameConstants.isResistance(skillForJob) && !GameConstants.isResistance(job)) {
            return false;
        } else if ((job / 10) % 10 == 0 && (skillForJob / 10) % 10 > (job / 10) % 10) { // wrong 2nd job
            return false;
        } else if ((skillForJob / 10) % 10 != 0 && (skillForJob / 10) % 10 != (job / 10) % 10) { //wrong 2nd job
            return false;
        } else if (skillForJob % 10 > job % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public int getJobCode(int jobid) {
        boolean isBeginnerJob = isBeginnerSkill();
        int result;
        if (isBeginnerJob || jobid % 100 == 0 || jobid == MapleJob.PIRATE_CS.getId() || jobid == MapleJob.DEMON_AVENGER1.getId() || jobid == MapleJob.JETT1.getId()) {
            result = 1;
        } else {
            int v4 = jobid / 10 == 43 ? jobid % 10 / 2 : jobid % 10;
            int v5 = v4 + 2;
            result = v5 >= 2 && (v5 <= 4 || v5 <= 10 && GameConstants.isEvan(jobid)) ? v5 : 0;
        }
        return result;
    }

    public boolean isFourthJob() {
        if (hasNoMasterLevel() || id / 1000000 == 92 && (id % 10000 == 0) || hasNoMasterLevel2() || (id / 10000 == 8000 || id / 10000 == 8001)) {
            return false;
        }
        int jobid = id / 10000;
        boolean beginner = isBeginnerSkill();

        if (beginner || (jobid / 100 == MapleJob.HERO.getId() || jobid == 11000)) {
            return false;
        }
        int jobCode = getJobCode(jobid);
        if (GameConstants.isEvan(jobid)) {
            if (id == Evan.HEROS_WILL_80_8 || id == Evan.HEROS_WILL_90_9 || jobCode != 9 && jobCode != 10
                    && id != Evan.MAGIC_RESISTANCE && id != Evan.MAGIC_BOOSTER_4 && id != Evan.CRITICAL_MAGIC) {
                return false;
            }
        } else if (!hasNoMasterLevel3()) {
            if (jobCode != 4 || GameConstants.isZero(jobid)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasNoMasterLevel() {
        boolean isMasterySkill;
        if (id > Corsair.DOUBLE_DOWN_4) {
            if (id > Mercedes.ROLLING_MOONSAULT) {
                if (id > WildHunter.WILD_INSTINCT) {
                    if (id == Mechanic.DOUBLE_DOWN_3) {
                        return true;
                    }
                    isMasterySkill = id == Mihile.COMBAT_MASTERY;
                } else {
                    if (id == WildHunter.WILD_INSTINCT || id == Mercedes.STAGGERING_STRIKES) {
                        return true;
                    }
                    isMasterySkill = id == Mercedes.HEROS_WILL_500_50_5;
                }
            } else {
                if (id == Mercedes.ROLLING_MOONSAULT) {
                    return true;
                }
                if (id > Mercedes.ROLLING_MOONSAULT) {

                    isMasterySkill = id == Evan.ONYX_WILL;
                } else {
                    if (id == Aran.SUDDEN_STRIKE_2 || id == CannonMaster.DOUBLE_DOWN || id - CannonMaster.DOUBLE_DOWN == 997) {
                        return true;
                    }
                    isMasterySkill = id - CannonMaster.DOUBLE_DOWN == 999;
                }
            }
            return isMasterySkill;
        }
        if (id == Corsair.DOUBLE_DOWN_4) {
            return true;
        }

        if (id <= Hermit.EXPERT_THROWING_STAR_HANDLING) {
            if (id == Hermit.EXPERT_THROWING_STAR_HANDLING) {
                return true;
            }
            if (id > IceLightningArchMage.BUFF_MASTERY_2) {
                if (id == Bishop.BUFF_MASTERY_4) {
                    return true;
                }
                isMasterySkill = id == Sniper.MARKSMANSHIP_1;
            } else {
                if (id == IceLightningArchMage.BUFF_MASTERY_2 || id == Hero.COMBAT_MASTERY_1 || id == DarkKnight.REVENGE_OF_THE_EVIL_EYE) {
                    return true;
                }
                isMasterySkill = id == FirePoisonArchMage.BUFF_MASTERY;
            }
            return isMasterySkill;
        }

        if (id <= DualBlade.TOXIC_VENOM) {
            if (id == DualBlade.TOXIC_VENOM || id == ChiefBandit.MESO_MASTERY_1) {
                return true;
            }
            isMasterySkill = id == DualBlade.SHARPNESS;
            return isMasterySkill;
        }
        if (id < Buccaneer.DOUBLE_DOWN_1) {
            return false;
        }
        if (id > Buccaneer.DOUBLE_DOWN_1) {
            isMasterySkill = id == Corsair.PIRATES_REVENGE_3;
            if (!isMasterySkill) {
                return false;
            }
        }
        return true;
    }

    public boolean hasNoMasterLevel2() {
        return (id / 1000000 != 92 || id % 10000 == 1) && (id / 1000000 == 92) && (id % 10000 == 0);
    }

    public boolean hasNoMasterLevel3() {
        boolean isMasterySkill;
        if (id > Zero.ADVANCED_THROWING_WEAPON) {
            if (id > Zero.ADVANCED_ROLLING_ASSAULT) {
                if (id == Zero.ADVANCED_EARTH_BREAK) {
                    return true;
                }
                isMasterySkill = id == Zero.ADVANCED_STORM_BREAK;
            } else {
                if (id == Zero.ADVANCED_ROLLING_ASSAULT || id == Zero.ADVANCED_SPIN_CUTTER || id == Zero.ADVANCED_WHEEL_WIND) {
                    return true;
                }
                isMasterySkill = id == Zero.GRAND_ROLLING_CROSS;
            }
        } else {
            if (id == Zero.ADVANCED_THROWING_WEAPON) {
                return true;
            }
            if (id > DualBlade.MIRROR_IMAGE) {
                if (id == DualBlade.BLADE_FURY || id - DualBlade.BLADE_FURY == 3) {
                    return true;
                }
                isMasterySkill = id - DualBlade.BLADE_FURY == 96659097;
            } else {
                if (id == DualBlade.MIRROR_IMAGE || id == DualBlade.SLASH_STORM || id == DualBlade.FLYING_ASSAULTER) {
                    return true;
                }
                isMasterySkill = id == DualBlade.SHADOW_MELD;
            }
        }
        return isMasterySkill;
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return this.masterLevel;
    }

    public void setMasterLevel(int nMasterLevel) {
        this.masterLevel = nMasterLevel;
    }

    public int getDelay() {
        return delay;
    }

    public int getTamingMob() {
        return eventTamingMob;
    }

    public int getEventTamingMob() {
        return eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return GameConstants.isBeginnerJob(jobId);
    }

    public boolean isMagic() {
        return magic;
    }

    public boolean isHyper() {
        return hyper > 0;
    }

    public int getHyper() {
        return hyper;
    }

    public boolean isHyperStat() {
        return id / 100 == 800004; // 80000400
    }

    public boolean isMovement() {
        return casterMove;
    }

    public boolean isPush() {
        return pushTarget;
    }

    public boolean isPull() {
        return pullTarget;
    }

    public boolean isSpecialSkill() {
        int jobId = id / 10000;
        return jobId == 900 || jobId == 800 || jobId == 9000 || jobId == 9200 || jobId == 9201 || jobId == 9202 || jobId == 9203 || jobId == 9204;
    }

    @Override
    public int compare(Skill o1, Skill o2) {
        return (Integer.valueOf(o1.getId()).compareTo(o2.getId()));
    }

    /**
     * Returns whether or not the skill is Quad Star or Cannon Barrage.
     *
     * @param skillId the skill ID to be checked
     * @return whether or not the skill matches Quad Star or Cannon Barrage
     */
    public static boolean isAdvancedBulletCount(int skillId) {
        // If the skill is Quad Star or Cannon Barrage
        if (skillId == NightLord.QUAD_STAR || skillId == CannonMaster.CANNON_BARRAGE) {
            return true;
        }
        return false;
    }

    public static boolean isAdvancedAttackCount(int skillId) {
        boolean isAdvancedAttack;
        switch (skillId) { // Need to look at GMS equivalent to make sure.
            // There are definitely more cases here, but I cannot read it in
            // IDA
            case Hayato.HITOKIRI_STRIKE:
            case Mechanic.AP_SALVO_PLUS:
            case Hayato.SHINSOKU:
            case 32111003: // Dark Shock
            case Shade.SPIRIT_CLAW: // Spirit Claw
            case DemonSlayer.CARRION_BREATH_1:
            case DemonSlayer.DEMON_IMPACT:
            case Evan.DARK_FOG:
            case 22140023:
            case 15111022: // Gale
            case 51121008: // Radiant Blast
            case 51121007: // Radiant Blast
            case 65121007: // Trinity
            case 65121008: // Trinity
            case 65121101: // Trinity
            case 51121009: // Radiant Gross
            case 3121015: // Arrow Stream:
            case 2121006: // Paralyze
            case 2221006: // Chain Lightning
            case 1221011: // Heaven's Hammer
            case 1120017: // Raging Blow
            case 1121008: // Raging Blow
            case 1221009: // Blast
            case 3121020: // Hurricane
            case 3221017: // Piercing Arrow
                isAdvancedAttack = true;
                break;
            default:
                isAdvancedAttack = false;
                break;
        }
        return isAdvancedAttack;
    }

    /**
     * Returns whether or not a skill is a keydown skill.
     *
     * @param skillId the skill ID to be checked
     * @return whether or not the skill is a keydown skill
     */
    public static final boolean isKeydownSkill(int nSkillID) { // is_keydown_skill
        if (nSkillID > 31211000) { // if (nSkillID > 31211001) {
            if (nSkillID > 80011362) {
                if (nSkillID > 112111016) {
                    if (nSkillID > 400011028) {
                        if (nSkillID != 400041006 && nSkillID != 400041009 && nSkillID != 400051024) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 400011028) {
                        return true;
                    }
                    if (nSkillID <= 131001020) {
                        if (nSkillID != 131001020 && nSkillID != 131001004 && nSkillID != 131001008) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 142111010) {
                        return true;
                    }
                } else {
                    if (nSkillID == 112111016) {
                        return true;
                    }
                    if (nSkillID <= 95001001) {
                        if (nSkillID != 95001001) {
                            switch (nSkillID) {
                                case 80011366:
                                case 80011371:
                                case 80011381:
                                case 80011382:
                                case 80011387:
                                    return true;
                                default:
                                    return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                        }
                        return true;
                    }
                    if (nSkillID <= 112001008) {
                        if (nSkillID != 112001008 && (nSkillID < 101110101 || nSkillID > 101110102)) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 112110003) {
                        return true;
                    }
                }
            } else {
                if (nSkillID == 80011362) {
                    return true;
                }
                if (nSkillID > 41121001) {
                    if (nSkillID > 80001629) {
                        if (nSkillID != 80001836 && nSkillID != 80001887 && nSkillID != 80011051) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 80001629) {
                        return true;
                    }
                    if (nSkillID <= 65121003) {
                        if (nSkillID != 65121003 && nSkillID != 42121000 && nSkillID != 60011216) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 80001587) {
                        return true;
                    }
                } else {
                    if (nSkillID == 41121001) {
                        return true;
                    }
                    if (nSkillID > 35121015) {
                        if (nSkillID <= 37121003) {
                            if (nSkillID != 37121003 && nSkillID != 36101001 && nSkillID != 36121000) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 37121052) {
                            return true;
                        }
                    } else {
                        if (nSkillID == 35121015) {
                            return true;
                        }
                        if (nSkillID <= 33121114) {
                            if (nSkillID != 33121114 && nSkillID != 32121003 && nSkillID != 33121009) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 33121214) {
                            return true;
                        }
                    }
                }
            }
        } else {
            if (nSkillID == 25121030) {
                return true;
            }
            if (nSkillID > 12121054) {
                if (nSkillID > 24121005) {
                    if (nSkillID > 30021238) {
                        if (nSkillID != 31001000 && nSkillID != 31101000 && nSkillID != 31111005) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 30021238) {
                        return true;
                    }
                    if (nSkillID <= 27101202) {
                        if (nSkillID != 27101202 && nSkillID != 25111005 && nSkillID != 25121030) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 27111100) {
                        return true;
                    }
                } else {
                    if (nSkillID == 24121005) {
                        return true;
                    }
                    if (nSkillID > 20041226) {
                        if (nSkillID <= 23121000) {
                            if (nSkillID != 23121000 && (nSkillID < 21120018 || nSkillID > 21120019 && nSkillID != 22171083)) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 24121000) {
                            return true;
                        }
                    } else {
                        if (nSkillID == 20041226) {
                            return true;
                        }
                        if (nSkillID <= 14111006) {
                            if (nSkillID != 14111006 && nSkillID != 13111020 && nSkillID != 13121001) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 14121004) {
                            return true;
                        }
                    }
                }
            } else {
                if (nSkillID == 12121054) {
                    return true;
                }
                if (nSkillID > 5081010) {
                    if (nSkillID > 5721001) {
                        if (nSkillID != 5721061 && nSkillID != 11121052 && nSkillID != 11121055) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 5721001) {
                        return true;
                    }
                    if (nSkillID <= 5700010) {
                        if (nSkillID != 5700010 && nSkillID != 5221004 && nSkillID != 5311002) {
                            return nSkillID >= 80001389 && nSkillID <= 80001392;
                        }
                        return true;
                    }
                    if (nSkillID == 5711021) {
                        return true;
                    }
                } else {
                    if (nSkillID == 5081010) {
                        return true;
                    }
                    if (nSkillID > 3101008) {
                        if (nSkillID <= 4341002) {
                            if (nSkillID != 4341002 && nSkillID != 3111013 && nSkillID != 3121020) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 5081002) {
                            return true;
                        }
                    } else {
                        if (nSkillID == 3101008) {
                            return true;
                        }
                        if (nSkillID <= 2221052) {
                            if (nSkillID != 2221052 && nSkillID != 1311011 && nSkillID != 2221011) {
                                return nSkillID >= 80001389 && nSkillID <= 80001392;
                            }
                            return true;
                        }
                        if (nSkillID == 2321001) {
                            return true;
                        }
                    }
                }
            }
        }
        return nSkillID >= 80001389 && nSkillID <= 80001392;
    }

    /**
     * Returns whether or not a skill is a supernova skill (Supreme Supernova or Shadow Veil).
     *
     * @param skill the skill ID to be checked
     * @return whether or not the skill is a supernova skill
     */
    public static boolean isSupernovaSkill(int skill) {
        if (skill == AngelicBuster.SUPREME_SUPERNOVA || skill == Shadower.SHADOW_VEIL) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether a skill is a rush bomb skill.
     *
     * @param skill the skill to be checked
     * @return whether or not the skill is rush bomb skill
     */
    public static boolean isRushBombSkill(int skill) {
        boolean isRushBombSkill;
        switch (skill) {
            case 40021186: // Hidden Skill: Shoujou Kashu
            case 14111023: // Night Walker Shadow Spark (Explosion)
            case Zero.WIND_CUTTER_1:
            case Zero.STORM_BREAK_1:
            case Zero.SEVERE_STORM_BREAK:
            case Kanna.MONKEY_SPIRITS:
            case Kaiser.WING_BEAT:
            case Luminous.MORNING_STAR:
            case DemonAvenger.BAT_SWARM:
            case BlazeWizard.BLAZING_EXTINCTION:
            case Cannoneer.BARREL_BOMB:
            case Brawler.ENERGY_VORTEX:
            case IceLightningArchMage.FROZEN_ORB:
            case Brawler.TORNADO_UPPERCUT_2:
                isRushBombSkill = true;
                break;
            default:
                isRushBombSkill = false;
                break;
        }
        return isRushBombSkill;
    }

    /**
     * Returns whether or not a skill ID is a Zero skill.
     *
     * @param skill the skill to be checked
     * @return whether or not the skill is a Zero skill
     */
    public static boolean isZeroSkill(int skill) {
        int jobID = skill / 10000;
        if (jobID / 10000 == 8000) {
            jobID = skill / 100;
        }
        return jobID == 10000 || jobID == 10100
                || jobID == 10110 || jobID == 10111 || jobID == 10112;
    }

    /**
     * Returns whether a skill is hit increasing (not sure if this is correct. Nexon calls it is_userclone_summoned_able_skill)
     *
     * @param skill the skill to be checked
     * @return whether or not the skill is a hit increasing skill
     */
    public static final boolean IsUsercloneSummonableSkill(int nSkillID) { // is_userclone_summoned_able_skill
        if (nSkillID > 23110006) {
            if (nSkillID > 131001104) {
                if (nSkillID > 131001208) {
                    if (nSkillID != 131001213 && nSkillID != 131001313 && nSkillID != 131002010) {
                        return false;
                    }
                } else if (nSkillID != 131001208) {
                    switch (nSkillID) {
                        case 131001108:
                        case 131001113:
                        case 131001201:
                        case 131001202:
                        case 131001203:
                            return true;
                        default:
                            return false;
                    }
                }
                return true;
            }
            if (nSkillID >= 131001101) {
                return true;
            }
            if (nSkillID > 23121003) {
                switch (nSkillID) {
                    case 131001000:
                    case 131001001:
                    case 131001002:
                    case 131001003:
                    case 131001004:
                    case 131001005:
                    case 131001008:
                    case 131001010:
                    case 131001011:
                    case 131001012:
                    case 131001013:
                        return true;
                    default:
                        return false;
                }
            }
            if (nSkillID >= 23121002) {
                return true;
            }
            if (nSkillID <= 23120013) {
                return !(nSkillID != 23120013 && (nSkillID < 23111000 || nSkillID > 23111003));
            }
            if (nSkillID == 23121000) {
                return true;
            }
        } else {
            if (nSkillID == 23110006) {
                return true;
            }
            if (nSkillID > 14101021) {
                if (nSkillID <= 23001000) {
                    if (nSkillID != 23001000) {
                        if (nSkillID > 14120045) {
                            if (nSkillID < 14121001 || nSkillID > 14121002) {
                                return false;
                            }
                        } else if (nSkillID != 14120045 && (nSkillID < 14111020 || nSkillID > 14111023)) {
                            return false;
                        }
                    }
                    return true;
                }
                if (nSkillID <= 23101001) {
                    return !(nSkillID < 23101000 && nSkillID != 23100004);
                }
                if (nSkillID == 23101007) {
                    return true;
                }
            } else {
                if (nSkillID >= 14101020) {
                    return true;
                }
                if (nSkillID <= 11111221) {
                    if (nSkillID < 11111220) {
                        if (nSkillID > 11101221) {
                            if (nSkillID < 11111120 || nSkillID > 11111121) {
                                return false;
                            }
                        } else if (nSkillID < 11101220 && (nSkillID < 11101120 || nSkillID > 11101121)) {
                            return false;
                        }
                    }
                    return true;
                }
                if (nSkillID <= 11121203) {
                    return !(nSkillID < 11121201 && (nSkillID < 11121101 || nSkillID > 11121103));
                }
                if (nSkillID == 14001020) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether a melee skill uses a bullet (projectiles such as arrows, throwing stars, bullets).
     *
     * @param skill the melee skill to check
     * @return whether or not a melee skill is bullet consuming
     */
    public static boolean isUseBulletMeleeAttack(int skill) {
        boolean isUseBulletMelee = false;
        if (skill == NightWalker.DOMINION) {
            isUseBulletMelee = true;
        } else if ((skill >= NightWalker.SHADOW_SPARK && skill <= NightWalker.DARK_OMEN)
                || (skill >= NightWalker.SHADOW_BAT && skill <= NightWalker.SHADOW_BAT_3)) {
            isUseBulletMelee = true;
        }
        return isUseBulletMelee;
    }

    /**
     * Returns whether or not a bullet melee attack skill is non-consuming. In other words, whether or not is NightWalker's Dominion, Dark
     * Omen, and Shadow Bat.
     *
     * @param skill the bullet melee attack skill
     * @return whether or not a bullet melee attack skill is non-consuming. In other words, whether or not is NightWalker's Dominion, Dark
     * Omen, and Shadow Bat.
     */
    public static boolean isNonconsumingBulletMeleeAttack(int skill) {
        boolean isNonconsumingBullet;
        switch (skill) {
            case NightWalker.DOMINION:
            case NightWalker.SHADOW_SPARK:
            case NightWalker.DARK_OMEN:
            case NightWalker.SHADOW_BAT_2:
            case NightWalker.SHADOW_BAT_3:
                isNonconsumingBullet = true;
                break;
            default:
                isNonconsumingBullet = false;
                break;
        }
        return isNonconsumingBullet;
    }

    /**
     * Returns whether or not a skill is a screen center attack skill such as Zero's Shadow Rain, Wind Archer's Monsoon, Nightwalker's
     * Dominion, etc.
     *
     * @param skill the skill to be checked
     * @return whether or not the skill is a screen center attack skill
     */
    public static boolean isScreenCenterAttackSkill(int skill) {
        boolean isScreenCenterAttack;
        switch (skill) {
            case 80001431: // Liberate the Destructive Rune
            case Zero.SHADOW_RAIN:
            case 80001429: // Liberate the Collapsing Rune
            case WindArcher.MONSOON:
            case NightWalker.DOMINION:
            case ThunderBreaker.DEEP_RISING:
                isScreenCenterAttack = true;
                break;
            default:
                isScreenCenterAttack = false;
                break;
        }
        return isScreenCenterAttack;
    }

    /**
     * Returns if a skill is an Aran falling stop skill.
     *
     * @param skill to be checked
     * @return whether or not a skill is is an Aran falling stop skill
     */
    public static boolean isAranFallingStopSkill(int skill) {
        boolean isAranFallingStop;
        switch (skill) {
            case 21110028:
            case 21120025:
            case 21110026:
            case 21001010:
            case 21000007:
            case 21110023:
            case 80001925:
            case 80001926:
            case 80001927:
            case 80001936:
            case 80001937:
            case 80001938:
                isAranFallingStop = true;
                break;
            default:
                isAranFallingStop = false;
                break;
        }
        return isAranFallingStop;
    }

    /**
     * @param skillTamingMob the skillTamingMob to set
     */
    public void setSkillTamingMob(int skillTamingMob) {
        this.skillTamingMob = skillTamingMob;
    }

    public int getSkillTamingMob() {
        return skillTamingMob;
    }

    public static final boolean IsEvanForceSkill(int nSkillID) { // is_evan_force_skill
        if (nSkillID > 22141012) {
            if (nSkillID <= 400021012) {
                return !(nSkillID != 400021012 && (nSkillID < 22171062 || nSkillID > 22171063 && nSkillID != 80001894));
            }
            if (nSkillID == 400021046) {
                return true;
            }
        } else {
            if (nSkillID >= 22141011) {
                return true;
            }
            if (nSkillID <= 22111017) {
                return !(nSkillID != 22111017 && (nSkillID > 22111012 || nSkillID < 22111011 && (nSkillID < 22110022 || nSkillID > 22110023)));
            }
            if (nSkillID == 22140022) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isKeydownSkillRectMoveXY(int nSkillID) { // is_keydown_skill_rect_move_xy
        return nSkillID == 13111020 || nSkillID == 112111016;
    }

    public static final boolean IsUnknown5thJobFunc(int nSkillID) {
        if (nSkillID <= 400041018) {
            if (nSkillID < 400041016) {
                if (nSkillID > 400021011) {
                    if (nSkillID != 400021028 && (nSkillID <= 400021046 || nSkillID > 400021048)) {
                        return false;
                    }
                } else if (nSkillID < 400021009 && nSkillID != 400011004 && nSkillID != 400021004) {
                    return false;
                }
            }
            return true;
        }
        if (nSkillID <= 400051008) {
            return !(nSkillID != 400051008 && nSkillID != 400041020 && nSkillID != 400051003);
        }
        return nSkillID == 400051016;
    }
}
