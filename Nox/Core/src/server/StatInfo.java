package server;

import client.CharacterTemporaryStat;

/**
 * StatInfo
 * @author AlphaEta
 * @author Mazen Massoud
 */
public enum StatInfo {

    PVPdamage(0, CharacterTemporaryStat.PVPDamage), // Battle Mode ATT Increase
    abnormalDamR(0, null), // Additional Damage on Targets with Abnormal Status
    acc(0, null), // Increase Accuracy +
    acc2dam(0, null), // Weapon Accuracy or Magic Accuracy (higher) to Damage
    acc2mp(0, null), // Accuracy to Max MP
    accR(0, null), // Accuracy %
    accX(0, null), // Accuracy + 
    ar(0, null), // Accuracy %
    asrR(0, null), // Abnormal Status Resistance % 
    attackCount(1, null), // Number of attacks, similiar to bulletCount
    bdR(0, null), // Damage on Bosses %
    bufftimeR(0, null), // Buff Skill duration increase %
    bulletConsume(0, null), // Consume bullets
    bulletCount(1, null), // Number of attacks hit
    coolTimeR(0, null), // Reduce Skill cooldown %
    cooltime(0, null), // Cooldown time
    cr(0, null), // Critical % 
    ppCon(0, null), // Kinesic Psychic Point Cost
    ppRecovery(0, null), // Kinesic Psychic Point Gain
    actionSpeed(0, null),
    criticaldamageMax(0, null), // Critical Maximum Damage Increase +
    criticaldamageMin(0, null), // Minimum Critical Damage Increase +
    damR(0, null), // Damage %
    damage(100, null), // Damage %
    damagepc(0, null), // Rage of Pharaoh and Bamboo Rain has this, drop from sky?
    dateExpire(0, null), // Useless date stuffs
    dex(0, null), // Increase DEX + 
    dex2str(0, null), // DEX to STR
    dexFX(0, null), // Increase DEX
    dexX(0, null), // Increase DEX
    dot(0, null), // Damage over time %
    dotInterval(0, null), // Damage dealt at intervals
    dotSuperpos(1, null), // Damage over time stack
    dotTime(0, null), // DOT time length (Lasts how long)
    dotHealHPPerSecondR(0, null), // 
    dropR(0, null), // Increases Drop %
    emad(0, null), // Enhanced Magic ATT
    emdd(0, null), // Enhanced Magic DEF
    emhp(0, null), // Enhanced HP
    emmp(0, null), // Enhanced MP
    epad(0, null), // Enhanced ATT
    epdd(0, null), // Enhanced DEF
    er(0, null), // Avoidability %
    eva(0, null), // Avoidability Increase, avoid
    eva2hp(0, null), // Convert Avoidability to HP
    evaR(0, null), // Avoidability %
    evaX(0, null), // Avoidability Increase
    expLossReduceR(0, null), // Reduce EXP loss at death %
    expR(0, null), // Additional % EXP
    extendPrice(0, null), // [Guild] Extend price
    finalAttackDamR(0, null), // Additional damage from Final Attack skills %
    fixdamage(0, null), // Fixed damage dealt upon using skill
    forceCon(0, null), // Fury Cost
    MDF(0, null),
    powerCon(0, null), // Surplus Energy Cost
    hp(0, null), // Restore HP/Heal
    hpCon(0, null), // HP Consumed
    iceGageCon(0, null), // Ice Gauge Cost
    ignoreMobDamR(0, CharacterTemporaryStat.IgnoreMobDamR), // Ignore Mob Damage to Player %
    ignoreMobpdpR(0, CharacterTemporaryStat.IgnoreMobpdpR), // Ignore Mob DEF % -> Attack higher
    indieBDR(0, null),
    indieAcc(0, null), // Accuracy +
    indieAllStat(0, CharacterTemporaryStat.IndieAllStat), // All Stats +
    indieDamR(0, CharacterTemporaryStat.IndieDamR), // Damage Increase %
    indieEva(0, null), // Avoidability +
    indieJump(0, CharacterTemporaryStat.IndieJump), // Jump Increase +
    indieMad(0, CharacterTemporaryStat.IndieMAD), // Magic Damage Increase
    indieMhp(0, CharacterTemporaryStat.IndieMHP), // Max HP Increase +
    indiePdd(0, CharacterTemporaryStat.IndiePDD),// ??
    indieMdd(0, null),//MDD?
    indieTerR(0, CharacterTemporaryStat.IndieTerR),
    indieAsrR(0, null),
    indieMhpR(0, CharacterTemporaryStat.IndieMHPR), // Max HP Increase %
    indieMmp(0, CharacterTemporaryStat.IndieMMP), // Max MP Increase +
    indieMmpR(0, CharacterTemporaryStat.IndieMMPR), // Max MP Increase %
    indiePad(0, CharacterTemporaryStat.IndiePAD), // Damage Increase
    indieSpeed(0, null), //Speed +
    indieExp(0, null), // exp rate
    indieBooster(0, null), //Attack Speed
    indieCr(0, null), // Critical?
    indieStance(0, null), // ?
    indieMaxDamageOver(0, null), // MaxDmg Inc over x
    int2luk(0, null), // Convert INT to LUK
    intFX(0, null), // Increase INT
    intX(0, null), // Increase INT
    itemCon(0, null), // Consumes item upon using <itemid>
    itemConNo(0, null), // amount for above
    itemConsume(0, null), // Uses certain item to cast that attack, the itemid doesn't need to be in inventory, just the effect.
    jump(0, null), // Jump Increase
    kp(0, null), // Body count attack stuffs
    luk2dex(0, null), // Convert LUK to DEX
    lukFX(0, null), // Increase LUK
    lukX(0, null), // Increase LUK
    lv2damX(0, null), // Additional damage per character level
    lv2mad(0, null), // Additional magic damage per character level
    lv2mhp(0, null), // Max HP per character level
    lv2mmp(0, null), // Max MP per character level
    lv2mdX(0, null), // Additional magic defense per character level
    lv2pad(0, null), // Additional damage per character level
    lv2pdX(0, null), // Additional defense per character level
    mad(0, null), // Magic ATT +
    madX(0, null), // Magic ATT +
    mastery(0, null), // Increase mastery
    mdd(0, null), // Magic DEF
    mdd2dam(0, null), // Magic DEF to Damage
    mdd2pdd(0, null), // Magic DEF to Weapon DEF
    mdd2pdx(0, null), // When hit with a physical attack, damage equal to #mdd2pdx% of Magic DEF is ignored
    mddR(0, null), // Magic DEF %
    mddX(0, null), // Magic DEF
    mesoR(0, null), // Mesos obtained + %
    mhp2damX(0, null), // Max HP added as additional damage
    mhpR(0, null), // Max HP %
    mhpX(0, null), // Max HP +
    minionDeathProp(0, null), // Instant kill on normal monsters in Azwan Mode
    mmp2damX(0, null), // Max MP added as additional damage
    mmpR(0, null), // Max MP %
    mmpX(0, null), // Max MP +
    onActive(0, null), // Chance to recharge skill
    mobCount(1, null), // Max Enemies hit
    morph(0, null), // Morph ID
    mp(0, null), // Restore MP/Heal
    mpCon(0, null), // MP Cost
    mpConEff(0, null), // MP Potion effect increase %
    mpConReduce(0, null), // MP Consumed reduce
    nbdR(0, null), // Increases damage by a set percentage when attacking a normal monster.
    nocoolProp(0, null), // When using a skill, Cooldown is not applied at #nocoolProp% chance. Has no effect on skills without Cooldown.
    onHitHpRecoveryR(0, null), // Chance to recover HP when attacking.
    onHitMpRecoveryR(0, null), // Chance to recover MP when attacking.
    pad(0, null), // Attack +
    padX(0, null), // Attack + 
    passivePlus(0, null), // Increases level of passive skill by #
    pdd(0, null), // Weapon DEF
    pdd2dam(0, null), // Weapon DEF added as additional damage
    pdd2mdd(0, null), // Weapon DEF added to Magic DEF
    pdd2mdx(0, null), // When hit with a magical attack, damage equal to #pdd2mdx% of Weapon DEF is ignored
    pddR(0, null), // Weapon DEF %
    pddX(0, null), // Weapon DEF
    period(0, null), // [Guild/Professions] time taken
    price(0, null), // [Guild] price to purchase
    priceUnit(0, null), // [Guild] Price stuffs
    prop(100, null), // Percentage chance over 100%
    psdJump(0, null), // Passive Jump Increase
    psdSpeed(0, null), // Passive Speed Increase
    range(0, null), // Range
    reqGuildLevel(0, null), // [Guild] guild req level
    selfDestruction(0, null), // Self Destruct Damage
    speed(0, null), // Increase moving speed
    speedMax(0, null), // Max Movement Speed +
    str(0, null), // Increase STR
    str2dex(0, null), // STR to DEX
    strFX(0, null), // Increase STR
    strX(0, null), // Increase STR
    subProp(0, null), // Summon Damage Prop
    subTime(-1, null), // Summon Damage Effect time
    suddenDeathR(0, null), // Instant kill on enemy %
    summonTimeR(0, null), // Summon Duration + %
    targetPlus(1, null), // Increases the number of enemies hit by multi-target skill
    tdR(0, null), // Increases damage by a set percentage when attacking a tower
    terR(0, null), // Elemental Resistance %
    stanceProp(0, null),
    time(-1, null), // bufflength
    s(0, null),
    t(0, null), // Damage taken reduce
    u(0, null),
    v(0, null),
    w(0, null),
    x(0, null),
    y(0, null),
    z(0, null),
    int_(0, null), //(0, null, true),
    luk(0, null);
    
    //    lt(0), //box stuffs
    //    lt2(0), //Tempest has this
    //    rb(0), //rectangle box
    //    rb2(0), //Tempest has this..
    
    private final int def;
    private final boolean special;
    private final CharacterTemporaryStat stat;

    private StatInfo(final int nDef, final CharacterTemporaryStat pStat) {
        this.def = nDef;
        this.special = false;
        this.stat = pStat;
    }

    private StatInfo(final int nDef, final CharacterTemporaryStat pStat, final boolean bSpecial) {
        this.def = nDef;
        this.special = bSpecial;
        this.stat = pStat;
    }

    public final int getDefault() {
        return def;
    }

    public final boolean isSpecial() {
        return special;
    }

    public final CharacterTemporaryStat getStat() {
        return stat;
    }
}
