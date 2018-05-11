package server.life;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.wz.nox.NoxBinaryReader;

import server.MapleStringInformationProvider;

public class MonsterStats {

    /*
    public enum MobStat {

    PAD(0),
    PDR(1),
    MAD(2),
    MDR(3),
    ACC(4),
    EVA(5),
    Speed(6),
    Stun(7),
    Freeze(8),
    Poison(9),
    Seal(10),
    Darkness(11),
    PowerUp(12),
    MagicUp(13),
    PGuardUp(14),
    MGuardUp(15),
    PImmune(16),
    MImmune(17),
    Web(18),
    HardSkin(19),
    Ambush(20),
    Venom(21),
    Blind(22),
    SealSkill(23),
    Dazzle(24),
    PCounter(25),
    MCounter(26),
    RiseByToss(27),
    BodyPressure(28),
    Weakness(29),
    Showdown(30),
    MagicCrash(31),
    DamagedElemAttr(32),
    Dark(33),
    Mystery(34),
    AddDamParty(35),
    HitCriDamR(36),
    Fatality(37),
    Lifting(38),
    DeadlyCharge(39),
    Smite(40),
    AddDamSkill(41),
    Incizing(42),
    DodgeBodyAttack(43),
    DebuffHealing(44),
    AddDamSkill2(45),
    BodyAttack(46),
    TempMoveAbility(47),
    FixDamRBuff(48),
    ElementDarkness(49),
    AreaInstallByHit(50),
    BMageDebuff(51),
    JaguarProvoke(52),
    JaguarBleeding(53),
    DarkLightning(54),
    PinkbeanFlowerPot(55),
    BattlePvP_Helena_Mark(56),
    PsychicLock(57),
    PsychicLockCoolTime(58),
    PsychicGroundMark(59),
    PowerImmune(60),
    PsychicForce(61),
    MultiPMDR(62),
    ElementResetBySummon(63),
    BahamutLightElemAddDam(64),
    BossPropPlus(65),
    MultiDamSkill(66),
    RWLiftPress(67),
    RWChoppingHammer(68),
    Unknown(69), // 2 Unknowns, not sure where they start between here and HangOver
    Unknown2(70),
    TimeBomb(71),
    Treasure(72),
    AddEffect(73),
    Invincible(74),
    Explosion(75),
    HangOver(76),
    Burned(77),
    BalogDisable(78),
    ExchangeAttack(79),
    AddBuffStat(80),
    LinkTeam(81),
    SoulExplosion(82),
    SeperateSoulP(83),
    SeperateSoulC(84),
    Ember(85),
    TrueSight(86),
    Laser(87),
    StatResetSkill(88),
    COUNT(89);
    
    private final int nValue;
    private final int nIndex;
    
    private MobStat(int uFlag) {
            this.nValue = 1 << (0x1F - (uFlag & 0x1F));
            this.nIndex = 3 - (uFlag >> 5);
    }
}
     */
    private final MonsterHpDisplayType HPDisplayType;
    private final byte cp, rareItemDropLevel, summonType;
    private final MonsterCategory category;
    private short level,
            tagColor, tagBgColor,
            selfDestruction_action;
    private long hp, mp, finalmaxHP;
    private final int id, wzDirectoryCRC, link, exp, removeAfter, buffToGive, fixedDamage, dropItemPeriod, point, eva, acc,
            PADamage, MADamage,
            PhysicalDefense, MagicDefense,
            hpRecovery, mpRecovery,
            PDRate, MDRate,
            trait_Ambition, trait_willEXP, trait_charmEXP,
            speed, pushed,
            wp;
    private boolean bodyAttack, dualGauge, removeOnMiss, ignoreFieldOut, boss, undead, ffaLoot, firstAttack, isExplosiveReward,
            mobile, fly, onlyNormalAttack, friendly, noDoom, partyBonusMob, changeableMob, escort,
            unknownDefaultMaxHP, unknownDefaultMaxMP,
            showNotRemoteDam, ignoreMoveImpact, individualReward, isRemoteRange, useReaction;
    private final EnumMap<Element, ElementalEffectiveness> resistance = new EnumMap<>(Element.class);
    private final List<Integer> revives = new ArrayList<>();

    private final int fs;

    // Skills
    private final List<MonsterSkill> skills = new ArrayList();
    private final Map<Byte, MobAttackInfo> mobAttacks = new HashMap();

    // Self destruction
    private final int selfDestruction_removeAfter, selfDestruction_hp;

    // Banish
    private final byte banType;
    private BanishInfo banish;

    // ETC
    private boolean enableSpawnTimeTrack = false; // Reduces memory, since int64 is required to track the spawn time per mob

    public MonsterStats(int Mobid, NoxBinaryReader data) throws IOException {
        this.id = Mobid;

        this.wzDirectoryCRC = data.readInt(); // crc , unk. TODO

        this.level = (short) data.readByte();
        this.category = MonsterCategory.getFromInt(data.readByte());
        this.rareItemDropLevel = (byte) data.readByte();
        this.ignoreFieldOut = data.readBoolean();
        this.onlyNormalAttack = data.readBoolean();
        this.boss = data.readBoolean();
        this.firstAttack = data.readBoolean();
        this.isExplosiveReward = data.readBoolean();
        this.ffaLoot = data.readBoolean();
        this.undead = data.readBoolean();
        this.friendly = data.readBoolean();
        this.escort = data.readBoolean();
        this.cp = (byte) data.readByte();
        this.partyBonusMob = data.readBoolean();
        this.dualGauge = data.readBoolean();
        this.noDoom = data.readBoolean();
        this.removeOnMiss = data.readBoolean();
        this.summonType = (byte) data.readByte();
        this.bodyAttack = data.readBoolean();
        this.changeableMob = data.readBoolean();
        this.showNotRemoteDam = data.readBoolean();
        this.ignoreMoveImpact = data.readBoolean();
        this.individualReward = data.readBoolean();
        this.isRemoteRange = data.readBoolean();
        this.useReaction = data.readBoolean();
        this.wp = data.readInt();

        this.eva = data.readShort();
        this.acc = data.readShort();

        this.hp = data.readLong();
        this.mp = data.readLong();
        this.finalmaxHP = data.readLong();

        this.unknownDefaultMaxHP = data.readBoolean();
        this.unknownDefaultMaxMP = data.readBoolean();

        this.exp = data.readInt();
        this.pushed = data.readInt();
        this.speed = data.readInt();
        this.PADamage = data.readInt();
        this.MADamage = data.readInt();
        this.removeAfter = data.readInt();
        this.fixedDamage = data.readInt();
        this.buffToGive = data.readInt();
        this.PhysicalDefense = data.readInt();
        this.MagicDefense = data.readInt();
        this.trait_Ambition = data.readInt();
        this.trait_willEXP = data.readInt();
        this.trait_charmEXP = data.readInt();
        this.PDRate = data.readInt();
        this.MDRate = data.readInt();
        this.hpRecovery = data.readInt();
        this.mpRecovery = data.readInt();

        this.dropItemPeriod = data.readInt();
        this.point = data.readInt();
        this.fs = data.readInt();

        final String ElementStr = data.readAsciiString();
        for (int i = 0; i < ElementStr.length(); i += 2) {
            resistance.put(
                    Element.getFromChar(ElementStr.charAt(i)),
                    ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(ElementStr.charAt(i + 1)))));
        }

        // Revive
        final int RevivesSize = data.readByte();
        for (int z = 0; z < RevivesSize; z++) {
            revives.add(data.readInt());
        }
        // Skill
        final int SkillSize = data.readByte();
        for (int s = 0; s < SkillSize; s++) {
            int Skill_SkillAfter = data.readInt();
            int Skill_EffectAfter = data.readInt();
            int Skill_Skill = data.readInt();
            int Skill_Action = data.readInt();
            int Skill_Level = data.readInt();
            byte Skill_preskillindex = (byte) data.readByte();
            byte Skill_preskillcount = (byte) data.readByte();
            boolean onlyFsm = data.readBoolean();

            final MonsterSkill stab = new MonsterSkill(Skill_Skill, Skill_Action, Skill_Level, Skill_SkillAfter, Skill_EffectAfter, Skill_preskillindex, Skill_preskillcount,
                    onlyFsm);
            skills.add(stab);

            if (Skill_SkillAfter > 0 || Skill_EffectAfter > 0) {
                this.enableSpawnTimeTrack = true;
            }
        }

        // Link
        this.link = data.readInt();

        // Self Destruction
        if (data.readBoolean()) {
            this.selfDestruction_hp = data.readInt();
            this.selfDestruction_action = data.readShort();
            this.selfDestruction_removeAfter = data.readInt();
        } else {
            this.selfDestruction_hp = 0;
            this.selfDestruction_action = -1;
            this.selfDestruction_removeAfter = -1;
        }
        // Hp Display
        this.tagColor = data.readShort();
        this.tagBgColor = data.readShort();

        // Banish
        if (data.readBoolean()) {
            this.banType = (byte) data.readByte();

            final String BanMsg = data.readAsciiString();
            final int BanMap = data.readInt();
            final String BanStr = data.readAsciiString();

            this.banish = new BanishInfo(BanMsg, BanMap, BanStr);
        } else {
            this.banType = -1;
        }
        // Attack
        final int AttackSize = data.readByte();
        for (int s = 0; s < AttackSize; s++) {
            byte attackNum = (byte) data.readByte();
            byte attackAction = (byte) data.readByte();
            boolean attackMagic = data.readBoolean();
            boolean attackDeadly = data.readBoolean();
            boolean attackKb = data.readBoolean();
            int attackBulletSpeed = data.readInt();
            int attackMpBurn = data.readInt();
            int attackDisease = data.readInt();
            int attackLevel = data.readInt();
            int attackConMp = data.readInt();
            boolean isElement = data.readBoolean();

            byte type = (byte) data.readByte();
            short fixDamR = data.readShort();
            boolean fixDamRType = data.readBoolean();
            short attackRatio = data.readShort();

            int cooltime = data.readInt();
            int damageSharingTime = data.readInt();
            boolean notMissAttack = data.readBoolean();

            mobAttacks.put(attackNum, new MobAttackInfo(attackNum, attackAction, attackMagic, attackDeadly, attackKb, attackBulletSpeed, attackMpBurn, attackDisease, attackLevel, attackConMp, isElement,
                    type, fixDamR, fixDamRType, attackRatio, cooltime, damageSharingTime, notMissAttack));
        }
        // Movements
        this.fly = data.readBoolean();
        this.mobile = data.readBoolean();

        data.readShort(); // Movement speed, not handled for now

        this.HPDisplayType = MonsterHpDisplayType.getFromInt((byte) data.readByte());
    }

    public void setChange(boolean invin) {
        this.changeableMob = invin;
    }

    public int getId() {
        return id;
    }

    public int getWzDirectoryCRC() {
        return wzDirectoryCRC;
    }

    public int getLinkId() {
        return link;
    }

    public int getExp() {
        return exp;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;//(hp * 3L / 2L);
    }

    public long getFinalmaxHP() {
        return finalmaxHP;
    }

    public long getMp() {
        return mp;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getSelfD() {
        return selfDestruction_action;
    }

    public int getSelfDHp() {
        return selfDestruction_hp;
    }

    public int getPADamage() {
        return PADamage;
    }

    public int getMADamage() {
        return MADamage;
    }

    public int getFixedDamage() {
        return fixedDamage;
    }

    public boolean isChangeableMob() {
        return changeableMob;
    }

    public int getPhysicalDefense() {
        return PhysicalDefense;
    }

    public int getMagicDefense() {
        return MagicDefense;
    }

    public int getPushed() {
        return pushed;
    }

    public final int getEva() {
        return eva;
    }

    public final int getAcc() {
        return acc;
    }

    public final int getSpeed() {
        return speed;
    }

    public boolean getOnlyNoramlAttack() {
        return onlyNormalAttack;
    }

    public BanishInfo getBanishInfo() {
        return banish;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public byte getrareItemDropLevel() {
        return rareItemDropLevel;
    }

    public boolean isBoss() {
        return boss;
    }

    public boolean isFfaLoot() {
        return ffaLoot;
    }

    public boolean isEscort() {
        return escort;
    }

    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    public boolean getMobile() {
        return mobile;
    }

    public boolean getFly() {
        return fly;
    }

    public List<Integer> getRevives() {
        return Collections.unmodifiableList(revives);
    }

    public boolean getUndead() {
        return undead;
    }

    public byte getSummonType() {
        return summonType;
    }

    public MonsterCategory getCategory() {
        return category;
    }

    public int getPDRate() {
        return PDRate;
    }

    public int getMDRate() {
        return MDRate;
    }

    public EnumMap<Element, ElementalEffectiveness> getElements() {
        return resistance;
    }

    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    public void removeEffectiveness(Element e) {
        resistance.remove(e);
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
    }

    public String getName() {
        if (MapleStringInformationProvider.getMobStringCache().containsKey(id)) {
            return MapleStringInformationProvider.getMobStringCache().get(id);
        }
        return "";
    }

    public short getTagColor() {
        return tagColor;
    }

    public short getTagBgColor() {
        return tagBgColor;
    }

    public List<MonsterSkill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public boolean hasSkill(int skillId, int level) {
        if (skills.stream().anyMatch((skill) -> (skill.getSkillId() == skillId && skill.getLevel() == level))) {
            return true;
        }
        return false;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public byte getCP() {
        return cp;
    }

    public int getPoint() {
        return point;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public boolean isChangeable() {
        return changeableMob;
    }

    public boolean isPartyBonus() {
        return partyBonusMob;
    }

    public boolean isNoDoom() {
        return noDoom;
    }

    public int getBuffToGive() {
        return buffToGive;
    }

    public MonsterHpDisplayType getHPDisplayType() {
        return HPDisplayType;
    }

    public int getDropItemPeriod() {
        return dropItemPeriod;
    }

    public MobAttackInfo getMobAttack(int attack) {
        if (attack >= this.mobAttacks.size() || attack < 0) {
            return null;
        }
        return this.mobAttacks.get((byte) attack);
    }

    public Collection<MobAttackInfo> getMobAttacks() {
        return Collections.unmodifiableCollection(this.mobAttacks.values());
    }

    public int dropsMeso() {
        if (getRemoveAfter() != 0 || getOnlyNoramlAttack() || getDropItemPeriod() > 0 || getCP() > 0 || getPoint() > 0 || getFixedDamage() > 0 || getSelfD() != -1 || getPDRate() <= 0 || getMDRate() <= 0) {
            return 0;
        }
        //final String mt = stats.getMobType();
        //if (mt != null && mt.length() > 0 && mt.charAt(0) == '7') {
        //    return 0; //bosses; magatia pq
        //}
        final int mobId = getId() / 100000;
        if (mobId == 97 || mobId == 95 || mobId == 93 || mobId == 91 || mobId == 90) {
            return 0;
        }
        if (isExplosiveReward()) {
            return 7;
        }
        if (isBoss()) {
            return 2;
        }
        return 1;
    }

    public int getFs() {
        return fs;
    }

    public boolean isignoreFieldOut() {
        return ignoreFieldOut;
    }

    public int getCharismaEXP() {
        return this.trait_charmEXP;
    }

    public int getAmbitionEXP() {
        return this.trait_Ambition;
    }

    public int getWillEXP() {
        return this.trait_willEXP;
    }

    public int getWP() {
        return wp;
    }

    public boolean isUnknownDefaultMaxHP() {
        return unknownDefaultMaxHP;
    }

    public boolean isUnknownDefaultMaxMP() {
        return unknownDefaultMaxMP;
    }

    public boolean getShowNotRemoteDam() {
        return showNotRemoteDam;
    }

    public boolean getIgnoreMoveImpact() {
        return ignoreMoveImpact;
    }

    public boolean getIndividualReward() {
        return individualReward;
    }

    public boolean getIsRemoteRange() {
        return isRemoteRange;
    }

    public boolean getIsUseReaction() {
        return useReaction;
    }
}
