package server;

import client.CharacterTemporaryStat;

/**
 * StatInfo
 * @author Mazen Massoud
 * @author Five
 */
public enum StatInfo {
    
    hcHp(0, null),
    mp(0, CharacterTemporaryStat.IncMaxMP),
    pad(0, null),
    pdd(0, null),
    mad(0, null),
    mdd(0, null),
    acc(0, null),
    eva(0, null),
    str(0, CharacterTemporaryStat.STR),
    dex(0, CharacterTemporaryStat.DEX),
    int_(0, CharacterTemporaryStat.INT, true),
    luk(0, CharacterTemporaryStat.LUK),
    craft(0, null),
    speed(0, CharacterTemporaryStat.Speed),
    jump(0, CharacterTemporaryStat.Jump),
    morph(0, CharacterTemporaryStat.Morph),
    hpCon(0, null),
    hp(0, CharacterTemporaryStat.IncMaxHP),
    mpCon(0, null),
    soulmpCon(0, null),
    forceCon(0, null),
    moneyCon(0, null),
    powerCon(0, null),
    itemCon(0, null),
    itemConNo(0, null),
    iceGageCon(0, null),
    hpRCon(0, null),
    aranComboCon(0, null),
    ppCon(0, null),
    ppRecovery(0, null),
    ppReq(0, null),
    damage(0, CharacterTemporaryStat.DamR),
    PVPdamage(0, null),
    PVPdamageX(0, null),
    fixdamage(0, null),
    selfDestruction(0, null),
    time(-1, null),
    hcTime(0, null),
    subTime(-1, null),
    hcSubTime(0, null),
    attackDelay(0, null),
    prop(100, null),
    hcProp(0, null),
    subProp(0, null),
    hcSubProp(0, null),
    attackCount(1, null),
    damageToBoss(0, null),
    bulletCount(0, null),
    bulletConsume(0, null),
    bulletSpeed(0, null),
    mastery(0, null),
    mobCount(0, null),
    x(0, null),
    y(0, null),
    z(0, null),
    action(0, null),
    emhp(0, CharacterTemporaryStat.EMHP),
    emmp(0, CharacterTemporaryStat.EMMP),
    epad(0, CharacterTemporaryStat.EPAD),
    indiePad(0, CharacterTemporaryStat.IndiePAD),
    indieMad(0, CharacterTemporaryStat.IndieMAD),
    indiePdd(0, CharacterTemporaryStat.IndiePDD),
    indieMdd(0, null),
    indieMhp(0, CharacterTemporaryStat.IndieMHP),
    indieMhpR(0, CharacterTemporaryStat.IndieMHPR),
    indieMmp(0, CharacterTemporaryStat.IndieMMP),
    indieMmpR(0, CharacterTemporaryStat.IndieMMPR),
    indieAcc(0, null),
    indieEva(0, null),
    indieEvaR(0, CharacterTemporaryStat.EVAR),
    indieJump(0, CharacterTemporaryStat.IndieJump),
    indieSpeed(0, CharacterTemporaryStat.IndieSpeed),
    indieAllStat(0, CharacterTemporaryStat.IndieAllStat),
    indieDamR(0, CharacterTemporaryStat.IndieDamR),
    indieMDF(0, null),
    indieBooster(0, CharacterTemporaryStat.IndieBooster),
    indieMaxDamageOver(0, null),
    indieMaxDamageOverR(0, null),
    indieCr(0, CharacterTemporaryStat.IndieCr),
    indieAsrR(0, CharacterTemporaryStat.IndieAsrR),
    indieTerR(0, CharacterTemporaryStat.IndieTerR),
    indiePddR(0, CharacterTemporaryStat.IndiePDDR),
    indieMddR(0, null),
    indieBDR(0, null),
    indieStance(0, CharacterTemporaryStat.IndieStance),
    indieIgnoreMobpdpR(0, CharacterTemporaryStat.IgnoreMobpdpR),
    indieExp(0, null),
    indiePadR(0, CharacterTemporaryStat.IndiePADR),
    indieMadR(0, CharacterTemporaryStat.IndieMADR),
    indieDrainHp(0, null),
    indiePMdR(0, null),
    indieForceJump(0, null),
    indieForceSpeed(0, null),
    emad(0, null),
    epdd(0, null),
    emdd(0, null),
    range(0, null),
    cooltime(0, null),
    hcCooltime(0, null),
    cooltimeMS(0, null),
    hcCooltimeMS(0, null),
    hcReflect(0, null),
    hcSummonHp(0, null),
    lt(0, null),
    rb(0, null),
    lt2(0, null),
    rb2(0, null),
    lt3(0, null),
    rb3(0, null),
    lt4(0, null),
    rb4(0, null),
    mhpR(0, null),
    mmpR(0, null),
    mhpX(0, null),
    mmpX(0, null),
    cr(0, null),
    criticaldamageMin(0, null),
    criticaldamageMax(0, null),
    accR(0, null),
    evaR(0, null),
    er(0, null),
    aR(0, null),
    eR(0, null),
    pddR(0, null),
    mddR(0, null),
    pdr(0, null),
    mdr(0, null),
    damR(0, null),
    pdR(0, null),
    mdR(0, null),
    bdR(0, null),
    bpDr(0, null),
    padR(0, null),
    madR(0, null),
    expR(0, null),
    dot(0, null),
    dotInterval(0, null),
    dotTime(0, null),
    dotSuperpos(1, null),
    dotTickDamR(0, null),
    ignoreMobpdpR(0, null),
    asrR(0, null),
    terR(0, null),
    mesoR(0, null),
    padX(0, null),
    madX(0, null),
    ignoreMobDamR(0, CharacterTemporaryStat.IgnoreMobDamR),
    psdJump(0, null),
    psdSpeed(0, null),
    psdSpeedMax(0, null),
    psdIncMaxDam(0, null),
    overChargeR(0, null),
    disCountR(0, null),
    mesoG(0, null),
    pqPointR(0, null),
    mileage(0, null),
    itemUpgradeBonusR(0, null),
    itemCursedProtectR(0, null),
    itemTUCProtectR(0, null),
    pddX(0, null),
    mddX(0, null),
    accX(0, null),
    evaX(0, null),
    strX(0, null),
    dexX(0, null),
    intX(0, null),
    lukX(0, null),
    reqGuildLevel(0, null),
    gpCon(0, null),
    igpCon(0, null),
    price(0, null),
    priceUnit(0, null),
    extendPrice(0, null),
    period(0, null),
    mpConReduce(0, null),
    actionSpeed(0, null),
    kp(0, null),
    strFX(0, null),
    dexFX(0, null),
    intFX(0, null),
    lukFX(0, null),
    pdd2mdd(0, null),
    mdd2pdd(0, null),
    acc2mp(0, null),
    eva2hp(0, null),
    str2dex(0, null),
    dex2str(0, null),
    int2luk(0, null),
    luk2dex(0, null),
    lv2pad(0, null),
    lv2mad(0, null),
    nbdR(0, null),
    tdR(0, null),
    minionDeathProp(0, null),
    abnormalDamR(0, null),
    acc2dam(0, null),
    pdd2dam(0, null),
    mdd2dam(0, null),
    pdd2mdx(0, null),
    mdd2pdx(0, null),
    nocoolProp(0, null),
    passivePlus(0, null),
    targetPlus(0, null),
    bufftimeR(0, null),
    dropR(0, null),
    lv2pdX(0, null),
    lv2mdX(0, null),
    mpConEff(0, null),
    lv2damX(0, null),
    summonTimeR(0, null),
    expLossReduceR(0, null),
    suddenDeathR(0, null),
    onHitHpRecoveryR(0, null),
    onHitMpRecoveryR(0, null),
    coolTimeR(0, null),
    mhp2damX(0, null),
    mmp2damX(0, null),
    finalAttackDamR(0, null),
    dotHealHPPerSecondR(0, null),
    damPlus(0, null),
    areaDotCount(0, null),
    strR(0, CharacterTemporaryStat.IndieSTR),
    dexR(0, CharacterTemporaryStat.IndieDEX),
    intR(0, CharacterTemporaryStat.IndieINT),
    lukR(0, CharacterTemporaryStat.IndieLUK),
    reduceForceR(0, null),
    MDF(0, null),
    lv2str(0, null),
    lv2dex(0, null),
    lv2int(0, null),
    lv2luk(0, null),
    stanceProp(0, CharacterTemporaryStat.Stance),
    lv2mmp(0, null),
    lv2mhp(0, null),
    costmpR(0, null),
    costhpR(0, null),
    mobCountDamR(0, null),
    damAbsorbShieldR(0, null),
    killRecoveryR(0, null),
    MDamageOver(0, null),
    onActive(0, null),
    fixToolTime(0, null),
    incMobRateDummy(0, null),
    s(0, null),
    u(0, null),
    v(0, null),
    w(0, null),
    q(0, null),
    s2(0, null),
    u2(0, null),
    v2(0, null),
    w2(0, null),
    q2(0, null),
    t(0, null),
    gauge(0, null),
    damR_5th(0, null),
    targetPlus_5th(0, null);
    
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