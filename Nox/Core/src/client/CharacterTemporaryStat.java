package client;

import constants.GameConstants;
import java.io.Serializable;

import handling.TemporaryStat;

public enum CharacterTemporaryStat implements Serializable, TemporaryStat {
    IndiePAD(0),// Version 178
    IndieMAD(1),// Version 178
    IndiePDD(2),// Version 178
    IndieMHP(3),// Version 178
    IndieMHPR(4),// Version 178
    IndieMMP(5),// Version 178
    IndieMMPR(6),// Version 178
    IndieJump(9),// Version 178
    IndieSpeed(10),// Version 178
    IndieAllStat(11),// Version 178
    IndieDodgeCriticalTime(12),// Version 178
    IndieEXP(13),// Version 178
    IndieBooster(14),// Version 178
    IndieFixedDamageR(15),// Version 178
    PyramidStunBuff(16),// Version 178
    PyramidFrozenBuff(17),// Version 178
    PyramidFireBuff(18),// Version 178
    PyramidBonusDamageBuff(19),// Version 178
    IndieRelaxEXP(20),// Version 178
    IndieSTR(21),// Version 178
    IndieDEX(22),// Version 178
    IndieINT(23),// Version 178
    IndieLUK(24),// Version 178
    IndieDamR(25),// Version 178
    IndieScriptBuff(26),// Version 178
    IndieMDF(27),// Version 178
    IndieAsrR(28),// Version 178
    IndieTerR(29),// Version 178
    IndieCr(30),// Version 178
    IndiePDDR(31),// Version 178
    IndieCrDam(32),// Version 178
    IndieBDR(33),// Version 178
    IndieStatR(34),// Version 178
    IndieStance(35),// Version 178
    IndieIgnoreMobpdpR(36),// Version 178
    IndieEmpty(37),// Version 178
    IndiePADR(38),// Version 178
    IndieMADR(39),// Version 178
    IndieCrDamR(40),// Version 178
    IndieDrainHP(41),// Version 178
    IndiePMdR(42),// Version 178
    IndieMaxDamageOverR(43),
    IndieForceJump(43),// Version 178
    IndieForceSpeed(44),// Version 178
    IndieQrPointTerm(46),// Version 178
    IndiePartyExp(50),// Version 178
    IndiePartyDrop(51),// Version 178
    IndieUnknown(52),// Version 178
    INDIE_STAT_COUNT(53),// Version 178
    PAD(55),// Version 178
    PDD(56),// Version 178
    MAD(57),// Version 178
    Craft(60),// Version 178
    Speed(61),// Version 178
    Jump(62),// Version 178
    MagicGuard(63),// Version 178
    DarkSight(64),// Version 178
    Booster(65),// Version 178
    PowerGuard(66),// Version 178
    MaxHP(67),// Version 178
    MaxMP(68),// Version 178
    Invincible(69),// Version 178
    SoulArrow(70),// Version 178
    Stun(71),// Version 178
    Poison(72),// Version 178
    Seal(73),// Version 178
    Darkness(74),// Version 178
    ComboCounter(75),// Version 178
    WeaponCharge(76),// Version 178
    HolySymbol(77),// Version 178
    MesoUp(78),// Version 178
    ShadowPartner(79),// Version 178
    PickPocket(80),// Version 178
    MesoGuard(81),// Version 178
    Thaw(82),// Version 178
    Weakness(83),// Version 178
    Curse(84),// Version 178
    Slow(85),// Version 178
    Morph(86),// Version 178
    Regen(87),// Version 178
    BasicStatUp(88),// Version 178
    Stance(89),// Version 178
    SharpEyes(90),// Version 178
    ManaReflection(91),// Version 178
    Attract(92),// Version 178
    NoBulletConsume(93),// Version 178
    Infinity(94),// Version 178
    AdvancedBless(95),// Version 178
    IllusionStep(96),// Version 178
    Blind(97),// Version 178
    Concentration(98),// Version 178
    BanMap(99),// Version 178
    MaxLevelBuff(100),// Version 178
    MesoUpByItem(101),// Version 178
    Ghost(102),// Version 178
    Barrier(103),// Version 178
    ReverseInput(104),// Version 178
    ItemUpByItem(105),// Version 178
    RespectPImmune(106),// Version 178
    RespectMImmune(107),// Version 178
    DefenseAtt(108),// Version 178
    DefenseState(109),// Version 178
    DojangBerserk(110),// Version 178
    DojangInvincible(111),// Version 178
    DojangShield(112),// Version 178
    DropBuffRate(113),// Version 178
    BladeClone(114),// Version 178
    ElementalReset(115),// Version 178
    HideAttack(116),// Version 178
    EventRate(117),// Version 178
    ComboAbilityBuff(118),// Version 178
    ComboDrain(119),// Version 178
    ComboBarrier(120),// Version 178
    BodyPressure(121),// Version 178
    RepeatEffect(122),// Version 178
    ExpBuffRate(123),// Version 178
    StopPortion(124),// Version 178
    StopMotion(125),// Version 178
    Fear(126),// Version 178
    HiddenPieceOn(127),// Version 178
    MagicShield(128),// Version 178
    MagicResistance(129),// Version 178
    SoulStone(130),// Version 178
    Flying(131),// Version 178
    Frozen(132),// Version 178
    AssistCharge(133),// Version 178
    Enrage(134),// Version 178
    DrawBack(135),// Version 178
    NotDamaged(136),// Version 178
    FinalCut(137),// Version 178
    HowlingAttackDamage(138),// Version 178
    BeastFormDamageUp(139),// Version 178
    Dance(140),// Version 178
    EMHP(141),// Version 178
    EMMP(142),// Version 178
    EPAD(143),// Version 178
    EMAD(144),// Version 178
    EPDD(145),// Version 178
    Guard(146),// Version 178
    Cyclone(147),// Version 178
    HowlingCritical(148),// Version 178
    HowlingMaxMP(149),// Version 178
    HowlingDefence(150),// Version 178
    HowlingEvasion(151),// Version 178
    Conversion(152),// Version 178
    Revive(153),// Version 178
    PinkbeanMinibeenMove(154),// Version 178
    Sneak(155),// Version 178
    Mechanic(156),// Version 178
    BeastFormMaxHP(157),// Version 178
    Dice(158),// Version 178
    BlessingArmor(159),// Version 178
    DamR(160),// Version 178
    TeleportMasteryOn(161),// Version 178
    CombatOrders(162),// Version 178
    Beholder(163),// Version 178
    DispelItemOption(164),// Version 178
    Inflation(165),// Version 178
    OnixDivineProtection(166),// Version 178
    Web(167),// Version 178
    Bless(168),// Version 178
    TimeBomb(169),// Version 178
    Disorder(170),// Version 178
    Thread(171),// Version 178
    Team(172),// Version 178
    Explosion(173),// Version 178
    BuffLimit(174),// Version 178
    STR(175),// Version 178
    INT(176),// Version 178
    DEX(177),// Version 178
    LUK(178),// Version 178
    DispelItemOptionByField(179),// Version 178
    DarkTornado(180),// Version 178
    PVPDamage(181),// Version 178
    PvPScoreBonus(182),// Version 178
    PvPInvincible(183),// Version 178
    PvPRaceEffect(184),// Version 178
    WeaknessMdamage(185),// Version 178
    Frozen2(186),// Version 178
    AmplifyDamage(188),// Version 178
    IceKnight(189),// Version 178
    Shock(190),// Version 178
    InfinityForce(191),// Version 178
    IncMaxHP(192),// Version 178
    IncMaxMP(193),// Version 178
    HolyMagicShell(194),// Version 178
    KeyDownTimeIgnore(195),// Version 178
    ArcaneAim(196),// Version 178
    MasterMagicOn(197),// Version 178
    AsrR(198),// Version 178
    TerR(199),// Version 178
    DamAbsorbShield(200),// Version 178
    DevilishPower(201),// Version 178
    Roulette(202),// Version 178
    SpiritLink(203),// Version 178
    AsrRByItem(204),// Version 178
    Event(205),// Version 178
    CriticalBuff(206),// Version 178
    DropRate(207),// Version 178
    PlusExpRate(208),// Version 178
    ItemInvincible(209),// Version 178
    Awake(210),// Version 178
    ItemCritical(211),// Version 178
    ItemEvade(212), // Version 178
    Event2(213),// Version 178
    VampiricTouch(214),// Version 178
    DDR(215),// Version 178
    IncTerR(216),// Version 178
    IncAsrR(217),// Version 178
    DeathMark(218),// Version 178
    UsefulAdvancedBless(219),// Version 178
    Lapidification(220),// Version 178
    VenomSnake(221),// Version 178
    CarnivalAttack(222),// Version 178
    CarnivalDefence(223),// Version 178
    CarnivalExp(224),// Version 178
    SlowAttack(225),// Version 178
    PyramidEffect(226),// Version 178
    KillingPoint(227),// Version 178
    HollowPointBullet(228),// Version 178
    KeyDownMoving(229),// Version 178
    IgnoreTargetDEF(230),// Version 178
    ReviveOnce(231),// Version 178
    Invisible(232),// Version 178
    EnrageCr(233),// Version 178
    EnrageCrDamMin(234),// Version 178
    Judgement(235),// Version 178
    DojangLuckyBonus(236),// Version 178
    PainMark(237),// Version 178
    Magnet(238),// Version 178
    MagnetArea(239),// Version 178
    Unknown240(240),
    Unknown241(241),
    VampDeath(242),// Version 178
    BlessingArmorIncPAD(243),// Version 178
    KeyDownAreaMoving(244),// Version 178
    Larkness(245),// Version 178
    StackBuff(246),// Version 178
    BlessOfDarkness(247),// Version 178
    AntiMagicShell(248),// Version 178
    LifeTidal(249),// Version 178
    HitCriDamR(250),// Version 178
    SmashStack(251),// Version 178
    PartyBarrier(252),// Version 178
    ReshuffleSwitch(253),// Version 178
    SpecialAction(254),// Version 178
    VampDeathSummon(255),// Version 178
    StopForceAtomInfo(256),// Version 178
    SoulGazeCriDamR(257),// Version 178
    SoulRageCount(258),// Version 178
    PowerTransferGauge(259),// Version 178
    AffinitySlug(260),// Version 178
    Trinity(261),// Version 178
    IncMaxDamage(262),// Version 178
    BossShield(263),// Version 178
    MobZoneState(264),// Version 178
    GiveMeHeal(265),// Version 178
    TouchMe(266),// Version 178
    Contagion(267),// Version 178
    ComboUnlimited(268),// Version 178
    SoulExalt(269),// Version 178
    IgnorePCounter(270),// Version 178
    IgnoreAllCounter(271),// Version 178
    IgnorePImmune(272),// Version 178
    IgnoreAllImmune(273),// Version 178
    FinalJudgement(274),// Version 178
    Unknown275(275),
    IceAura(276),// Version 178
    FireAura(277),// Version 178
    VengeanceOfAngel(278),// Version 178
    HeavensDoor(279),// Version 178
    Preparation(280),// Version 178
    BullsEye(281),// Version 178
    IncEffectHPPotion(282),// Version 178
    IncEffectMPPotion(283),// Version 178
    BleedingToxin(284),// Version 178
    IgnoreMobDamR(285),// Version 178
    Asura(286),// Version 178
    Unknown287(287),
    FlipTheCoin(288),// Version 178
    UnityOfPower(289),// Version 178
    Stimulate(290),// Version 178
    ReturnTeleport(291),// Version 178
    DropRIncrease(292),// Version 178
    IgnoreMobpdpR(293),// Version 178
    BdR(294),// Version 178
    CapDebuff(295),// Version 178
    Exceed(296),// Version 178
    DiabolikRecovery(297),// Version 178
    FinalAttackProp(298),// Version 178
    ExceedOverload(299),// Version 178
    OverloadCount(300),// Version 178
    BuckShot(301),// Version 178
    FireBomb(302),// Version 178
    HalfstatByDebuff(303),// Version 178
    SurplusSupply(304),// Version 178
    SetBaseDamage(305),// Version 178
    EVAR(306),// Version 178
    NewFlying(307),// Version 178
    AmaranthGenerator(308),// Version 178
    OnCapsule(309),// Version 178
    CygnusElementSkill(310),// Version 178
    StrikerHyperElectric(311),// Version 178
    EventPointAbsorb(312),// Version 178
    EventAssemble(313),// Version 178
    StormBringer(314),// Version 178
    ACCR(315),// Version 178
    DEXR(316),// Version 178
    Albatross(317),// Version 178
    Translucence(318),// Version 178
    PoseType(319),// Version 178
    LightOfSpirit(320),// Version 178
    ElementSoul(321),// Version 178
    GlimmeringTime(322),// Version 178
    TrueSight(323),// Version 178
    SoulExplosion(324),// Version 178
    SoulMP(325),// Version 178
    FullSoulMP(326),// Version 178
    SoulSkillDamageUp(327),// Version 178
    ElementalCharge(328),// Version 178
    Restoration(329),// Version 178
    CrossOverChain(330),// Version 178
    ChargeBuff(331),// Version 178
    Reincarnation(332),// Version 178
    KnightsAura(333),// Version 178
    ChillingStep(334),// Version 178
    DotBasedBuff(335),// Version 178
    BlessEnsenble(336),// Version 178
    ComboCostInc(337),// Version 178
    ExtremeArchery(338),// Version 178
    NaviFlying(339),// Version 178
    QuiverCatridge(340),// Version 178
    AdvancedQuiver(341),// Version 178
    UserControlMob(342),// Version 178
    ImmuneBarrier(343),// Version 178
    ArmorPiercing(344),// Version 178
    ZeroAuraStr(345),// Version 178
    ZeroAuraSpd(346),// Version 178
    CriticalGrowing(347),// Version 178
    QuickDraw(348),// Version 178
    BowMasterConcentration(349),// Version 178
    TimeFastABuff(350),// Version 178
    TimeFastBBuff(351),// Version 178
    GatherDropR(352),// Version 178
    AimBox2D(353),// Version 178
    IncMonsterBattleCaptureRate(354),// Version 178
    Unknown355(355),
    CursorSniping(356),// Version 178
    DebuffTolerance(357),// Version 178
    DotHealHPPerSecond(358),// Version 178
    SpiritGuard(359),// Version 178
    PreReviveOnce(360),// Version 178
    SetBaseDamageByBuff(361),// Version 178
    LimitMP(362),// Version 178
    ReflectDamR(363),// Version 178
    ComboTempest(364),// Version 178
    MHPCutR(365),// Version 178
    MMPCutR(366),// Version 178
    SelfWeakness(367),// Version 178
    ElementDarkness(368),// Version 178
    FlareTrick(369),// Version 178
    Ember(370),// Version 178
    Dominion(371),// Version 178
    SiphonVitality(372),// Version 178
    DarknessAscension(373),// Version 178
    BossWaitingLinesBuff(374),// Version 178
    DamageReduce(375),// Version 178
    ShadowServant(376),// Version 178
    ShadowIllusion(377),// Version 178
    KnockBack(378),// Version 178
    AddAttackCount(379),// Version 178
    ComplusionSlant(380),// Version 178
    JaguarSummoned(381),// Version 178
    JaguarCount(382),// Version 178
    SSFShootingAttack(383),// Version 178
    DevilCry(384),// Version 178
    ShieldAttack(385),// Version 178
    BMageAura(386),// Version 178
    DarkLighting(387),// Version 178
    AttackCountX(388),// Version 178
    BMageDeath(389),// Version 178
    BombTime(390),// Version 178
    NoDebuff(391),// Version 178
    BattlePvP_Mike_Shield(392),// Version 178
    BattlePvP_Mike_Bugle(393),// Version 178
    XenonAegisSystem(394),// Version 178
    AngelicBursterSoulSeeker(395),// Version 178
    HiddenPossession(396),// Version 178
    NightWalkerBat(397),// Version 178
    NightLordMark(398),// Version 178
    WizardIgnite(399),// Version 178
    FireBarrier(400),// Version 178
    ChangeFoxMan(401),// Version 178
    Unknown402(402),
    Unknown403(403),
    Unknown404(404),
    BattlePvP_Helena_Mark(405),// Version 178
    BattlePvP_Helena_WindSpirit(406),// Version 178
    BattlePvP_LangE_Protection(407),// Version 178
    BattlePvP_LeeMalNyun_ScaleUp(408),// Version 178
    BattlePvP_Revive(409),// Version 178
    PinkbeanAttackBuff(410),// Version 178
    PinkbeanRelax(411),// Version 178
    PinkbeanRollingGrade(412),// Version 178
    PinkbeanYoYoStack(413),// Version 178
    RandAreaAttack(414),// Version 178
    NextAttackEnhance(415),// Version 178
    AranBeyonderDamAbsorb(416),// Version 178
    AranCombotempastOption(417),// Version 178
    NautilusFinalAttack(418),// Version 178
    ViperTimeLeap(419),// Version 178
    RoyalGuardState(420),// Version 178
    RoyalGuardPrepare(421),// Version 178
    MichaelSoulLink(422),// Version 178
    MichaelStanceLink(423),// Version 178
    TriflingWhimOnOff(424),// Version 178
    AddRangeOnOff(425),// Version 178
    KinesisPsychicPoint(426),// Version 178
    KinesisPsychicOver(427),// Version 178
    KinesisPsychicShield(428),// Version 178
    KinesisIncMastery(429),// Version 178
    KinesisPsychicEnergeShield(430),// Version 178
    BladeStance(431),// Version 178
    DebuffActiveSkillHPCon(432),// Version 178
    DebuffIncHP(433),// Version 178
    BowMasterMortalBlow(434),// Version 178
    AngelicBursterSoulResonance(435),// Version 178
    Fever(436),// Version 178
    IgnisRore(437),// Version 178
    RpSiksin(438),// Version 178
    TeleportMasteryRange(439),// Version 178
    FixCoolTime(440),// Version 178
    IncMobRateDummy(441),// Version 178
    AdrenalinBoost(442),// Version 178
    AranSmashSwing(443),// Version 178
    AranDrain(444),// Version 178
    AranBoostEndHunt(445),// Version 178
    HiddenHyperLinkMaximization(446),// Version 178
    RWCylinder(447),// Version 178
    RWCombination(448),// Version 178
    RWMagnumBlow(449),// Version 178
    RWBarrier(450),// Version 178
    RWBarrierHeal(451),// Version 178
    RWMaximizeCannon(452),// Version 178
    RWOverHeat(453),// Version 178
    UsingScouter(454),// Version 178
    RWMovingEvar(455),// Version 178
    Stigma(456),// Version 178
    Unknown457(457),
    // Unknown 456 -> 487
    Unknown462(462),
    Unknown463(463),
    Unknown464(464),
    Unknown465(465),
    Unknown466(466),
    Unknown467(467),
    Unknown468(468),
    Unknown472(472),
    Unknown473(473),
    Unknown474(474),
    Unknown483(483),
    Unknown485(485),
    FamiliarShadow(487),// Version 178
    HayatoStance(488),// Version 178
    HayatoStanceBonus(489),// Version 178
    EyeForEye(490),// Version 178
    WillowDodge(491),// Version 178
    // Unknown 492
    HayatoPAD(493),// Version 178
    HayatoHPR(494),// Version 178
    HayatoMPR(495),// Version 178
    Unknown496(496),
    // Unknown 496 -> 497
    POTION_CURSE(498),// Version 178
    Jinsoku(499),// Version 178
    HayatoCr(500),// Version 178
    // Unknown 501
    KannaBDR(502),// Version 178
    Battoujutsu(503),// Version 178
    Unknown504(504),
    Unknown505(505),
    Unknown506(506),
    AnimalChange(507),// Version 178
    TeamRoar(508),// Version 178
    // Unknown 509 -> 520
    Unknown510(510),
    Unknown514(514),
    Unknown515(515),
    Unknown516(516),
    Unknown518(518),
    Unknown519(519),
    Unknown520(520),
    EnergyCharged(521),// Version 178
    DashSpeed(522),// Version 178
    DashJump(523),// Version 178
    RideVehicle(524),// Version 178
    PartyBooster(525),// Version 178
    GuidedBullet(526),// Version 178
    Undead(527),// Version 178
    RideVehicleExpire(528),// Version 178
    //Fake stats to use for tracking charstat changes
    //as a result of summons/puppets
    SUMMON(666),
    PUPPET(696),
    REAPER(966),
    //to track cash from mob rate
    ACASH_RATE(669),
    //TODO
    SHADOW(701),
    DISABLE_POTENTIAL(702),
    TORNADO_CURSE(703),
    TELEPORT(704);
    // TODO: (500) -> (543) Unknown, not in kmst so give flags and test each one then name
    private final int uFlag;
    private final int nValue;
    private final int nIndex;

    private CharacterTemporaryStat(int uFlag) {
        this.uFlag = uFlag;
        this.nValue = 1 << (0x1F - (uFlag & 0x1F));
        this.nIndex = 17 - (uFlag >> 5);
    }

    public int getFlag() {
        return uFlag;
    }

    @Override
    public int getValue() {
        return nValue;
    }

    @Override
    public int getPosition() {
        return nIndex;
    }

    public int getPosition(boolean reverse) {
        return (GameConstants.CFlagSize) - nIndex;
    }

    public boolean isIndie() {
        return uFlag < CharacterTemporaryStat.INDIE_STAT_COUNT.uFlag;
    }

    public static boolean isEnDecode4Byte(CharacterTemporaryStat uFlag) {
        switch (uFlag) {
            case CarnivalDefence:
            case SpiritLink:
            case DojangLuckyBonus:
            case SoulGazeCriDamR:
            case PowerTransferGauge:
            case ReturnTeleport:
            case ShadowPartner:
            case IncMaxDamage:
            case SetBaseDamage:
            case QuiverCatridge:
            case ImmuneBarrier:
            case NaviFlying:
            case Dance:
            case SetBaseDamageByBuff:
            case DotHealHPPerSecond:
            case MagnetArea:
            case RideVehicle:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMovementAffectingStat(CharacterTemporaryStat uFlag) {
        switch (uFlag) {
            case Speed:
            case Jump:
            case Stun:
            case Weakness:
            case Slow:
            case Morph:
            case Ghost:
            case BasicStatUp:
            case Attract:
            case DashSpeed:
            case DashJump:
            case Flying:
            case Frozen:
            case Frozen2:
            case Lapidification:
            case IndieSpeed:
            case IndieJump:
            case KeyDownMoving:
            case EnergyCharged:
            case Mechanic:
            case Magnet:
            case MagnetArea:
            case VampDeath:
            case VampDeathSummon:
            case GiveMeHeal:
            case DarkTornado:
            case NewFlying:
            case NaviFlying:
            case UserControlMob:
            case Dance:
            case SelfWeakness:
            case BattlePvP_Helena_WindSpirit:
            case BattlePvP_LeeMalNyun_ScaleUp:
            case TouchMe:
            case IndieForceSpeed:
            case IndieForceJump:
                return true;
            default:
                return false;

        }
    }

    public enum TSIndex {

        EnergyCharged(492),
        DashSpeed(493),
        DashJump(494),
        RideVehicle(495),
        PartyBooster(496),
        GuidedBullet(497),
        Undead(498),
        RideVehicleExpire(499);
        private final int nValue;
        private final int nIndex;

        private TSIndex(int uFlag) {
            this.nValue = 1 << (0x1F - (uFlag & 0x1F));
            this.nIndex = 17 - (uFlag >> 5);
        }
    }
}
