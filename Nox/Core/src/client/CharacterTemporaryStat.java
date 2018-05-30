package client;

import constants.GameConstants;
import java.io.Serializable;

import handling.TemporaryStat;

public enum CharacterTemporaryStat implements Serializable, TemporaryStat {
    None(0xFFFFFFFF),
    IndiePAD(0),// Version 188
    IndieMAD(1),// Version 188
    IndiePDD(2),// Version 188
    IndieMHP(3),// Version 188
    IndieMHPR(4),// Version 188
    IndieMMP(5),// Version 188
    IndieMMPR(6),// Version 188
    IndieACC(7),
    IndieEVA(8),
    IndieJump(9),// Version 188
    IndieSpeed(10),// Version 188
    IndieAllStat(11),// Version 188
    IndieDodgeCriticalTime(12),// Version 188
    IndieEXP(13),// Version 188
    IndieBooster(15),// Version 188
    IndieFixedDamageR(16),// Version 188
    PyramidStunBuff(17),// Version 188
    PyramidFrozenBuff(18),// Version 188
    PyramidFireBuff(19),// Version 188
    PyramidBonusDamageBuff(20),// Version 188
    IndieRelaxEXP(21),// Version 188
    IndieSTR(22),// Version 188
    IndieDEX(23),// Version 188
    IndieINT(24),// Version 188
    IndieLUK(25),// Version 188
    IndieDamR(26),// Version 188
    IndieScriptBuff(27),// Version 188
    IndieMDF(28),// Version 188
    IndieAsrR(29),// Version 188
    IndieTerR(30),// Version 188
    IndieCr(31),// Version 188
    IndiePDDR(32),// Version 188
    IndieCrDam(33),// Version 188
    IndieBDR(34),// Version 188
    IndieStatR(35),// Version 188
    IndieStance(36),// Version 188
    IndieIgnoreMobpdpR(37),// Version 188
    IndieEmpty(38),// Version 188
    IndiePADR(39),// Version 188
    IndieMADR(40),// Version 188
    IndieCrDamR(41),// Version 188
    IndieDrainHP(42),// Version 188
    IndiePMdR(43),// Version 188
    IndieForceJump(44),// Version 188
    IndieForceSpeed(45),// Version 188
    IndieQrPointTerm(46),// Version 188
    IndieSummon(47),
    IndieNotDamaged(49),// Version 188
    IndieUnknown50(50),// Version 188
    IndiePartyExp(53),// Version 188
    IndiePartyDrop(54),// Version 188
    IndieUnknown55(55),// Version 188
    IndieStatCount(57),// Version 188
    PAD(58),// Version 188
    PDD(59),// Version 188
    MAD(60),// Version 188
    Craft(63),// Version 188
    Speed(64),// Version 188
    Jump(65),// Version 188
    MagicGuard(66),// Version 188
    DarkSight(67),// Version 188
    Booster(68),// Version 188
    PowerGuard(69),// Version 188
    MaxHP(70),// Version 188
    MaxMP(71),// Version 188
    Invincible(72),// Version 188
    SoulArrow(73),// Version 188
    Stun(74),// Version 188
    Poison(75),// Version 188
    Seal(76),// Version 188
    Darkness(77),// Version 188
    ComboCounter(78),// Version 188
    Unknown79(79),
    Unknown80(80),
    Unknown81(81),
    WeaponCharge(82),// Version 188
    HolySymbol(83),// Version 188
    MesoUp(84),// Version 188
    ShadowPartner(85),// Version 188
    PickPocket(86),// Version 188
    MesoGuard(87),// Version 188
    Thaw(88),// Version 188
    Weakness(89),// Version 188
    Curse(90),// Version 188
    Slow(91),// Version 188
    Morph(92),// Version 188
    Regen(93),// Version 188
    BasicStatUp(94),// Version 188
    Stance(95),// Version 188
    SharpEyes(96),// Version 188
    ManaReflection(97),// Version 188
    Attract(98),// Version 188
    NoBulletConsume(99),// Version 188
    Infinity(100),// Version 188
    AdvancedBless(101),// Version 188
    IllusionStep(102),// Version 188
    Blind(103),// Version 188
    Concentration(104),// Version 188
    BanMap(105),// Version 188
    MaxLevelBuff(106),// Version 188
    MesoUpByItem(107),// Version 188
    Ghost(110),// Version 188
    Barrier(111),// Version 188
    ReverseInput(112),// Version 188
    ItemUpByItem(113),// Version 188
    RespectPImmune(114),// Version 188
    RespectMImmune(115),// Version 188
    DefenseAtt(116),// Version 188
    DefenseState(117),// Version 188
    DojangBerserk(118),// Version 188
    DojangInvincible(119),// Version 188
    DojangShield(120),// Version 188
    DropBuffRate(121),// Version 188
    BladeClone(122),// Version 188
    ElementalReset(123),// Version 188
    HideAttack(124),// Version 188
    EventRate(125),// Version 188
    ComboAbilityBuff(126),// Version 188
    ComboDrain(127),// Version 188
    ComboBarrier(128),// Version 188
    BodyPressure(129),// Version 188
    RepeatEffect(130),// Version 188
    ExpBuffRate(131),// Version 188
    StopPortion(132),// Version 188
    StopMotion(133),// Version 188
    Fear(134),// Version 188
    HiddenPieceOn(135),// Version 188
    MagicShield(136),// Version 188
    MagicResistance(137),// Version 188
    SoulStone(138),// Version 188
    Flying(139),// Version 188
    Frozen(140),// Version 188
    AssistCharge(141),// Version 188
    Enrage(142),// Version 188
    DrawBack(143),// Version 188
    NotDamaged(144),// Version 188
    FinalCut(145),// Version 188
    HowlingAttackDamage(146),// Version 188
    BeastFormDamageUp(147),// Version 188
    Dance(148),// Version 188
    EMHP(149),// Version 188
    EMMP(150),// Version 188
    EPAD(151),// Version 188
    EMAD(152),// Version 188
    EPDD(153),// Version 188
    Guard(154),// Version 188
    Cyclone(155),// Version 188
    HowlingCritical(156),// Version 188
    HowlingMaxMP(157),// Version 188
    HowlingDefence(158),// Version 188
    HowlingEvasion(159),// Version 188
    Conversion(160),// Version 188
    Revive(161),// Version 188
    PinkbeanMinibeenMove(162),// Version 188
    Sneak(164),// Version 188
    Mechanic(165),// Version 188
    BeastFormMaxHP(166),// Version 188
    Dice(167),// Version 188
    BlessingArmor(168),// Version 188
    DamR(169),// Version 188
    TeleportMasteryOn(170),// Version 188
    CombatOrders(171),// Version 188
    Beholder(172),// Version 188
    DispelItemOption(173),// Version 188
    Inflation(174),// Version 188
    OnixDivineProtection(175),// Version 188
    Web(176),// Version 188
    Bless(177),// Version 188
    TimeBomb(178),// Version 188
    Disorder(179),// Version 188
    Thread(180),// Version 188
    Team(181),// Version 188
    Explosion(182),// Version 188
    BuffLimit(183),// Version 188
    STR(184),// Version 188
    INT(185),// Version 188
    DEX(186),// Version 188
    LUK(187),// Version 188
    DispelItemOptionByField(188),// Version 188
    DarkTornado(189),// Version 188
    PVPDamage(190),// Version 188
    PvPScoreBonus(191),// Version 188
    PvPInvincible(192),// Version 188
    PvPRaceEffect(193),// Version 188
    WeaknessMdamage(194), // Version 188
    Frozen2(195),// Version 188
    AmplifyDamage(196),// Version 188
    IceKnight(197),// Version 188
    Shock(198),// Version 188
    InfinityForce(199),// Version 188
    IncMaxHP(200),// Version 188
    IncMaxMP(201),// Version 188
    HolyMagicShell(202),// Version 188
    KeyDownTimeIgnore(203),// Version 188
    ArcaneAim(204),// Version 188
    MasterMagicOn(205),// Version 188
    AsrR(206),// Version 188
    TerR(207),// Version 188
    DamAbsorbShield(208),// Version 188
    DevilishPower(209),// Version 188
    Roulette(210),// Version 188
    SpiritLink(211),// Version 188
    AsrRByItem(212),// Version 188
    Event(213),// Version 188
    CriticalBuff(214),// Version 188
    DropRate(215),// Version 188
    PlusExpRate(216),// Version 188
    ItemInvincible(217),// Version 188
    Awake(218),// Version 188
    ItemCritical(219),// Version 188
    ItemEvade(220),// Version 188
    Event2(221),// Version 188
    VampiricTouch(222),// Version 188
    DDR(223),// Version 188
    IncTerR(224),// Version 188
    IncAsrR(225),// Version 188
    DeathMark(226),// Version 188
    UsefulAdvancedBless(227),// Version 188
    Lapidification(228),// Version 188
    VenomSnake(229),// Version 188
    CarnivalAttack(230),// Version 188
    CarnivalDefence(231),// Version 188
    CarnivalExp(232),// Version 188
    SlowAttack(233),// Version 188
    PyramidEffect(234),// Version 188
    KillingPoint(235),// Version 188
    HollowPointBullet(236),// Version 188
    KeyDownMoving(237),// Version 188
    IgnoreTargetDEF(238),// Version 188
    ReviveOnce(239),// Version 188
    Invisible(240),// Version 188
    EnrageCr(241),// Version 188
    EnrageCrDamMin(242),// Version 188
    Judgement(243),// Version 188
    DojangLuckyBonus(244),// Version 188
    PainMark(245),// Version 188
    Magnet(246),// Version 188
    MagnetArea(247),// Version 188
    Unknown248(248),
    DemonAwakening(249),
    VampDeath(250),// Version 188
    BlessingArmorIncPAD(251),// Version 188
    KeyDownAreaMoving(252),// Version 188
    Larkness(253),// Version 188
    StackBuff(254),// Version 188
    BlessOfDarkness(255),// Version 188
    AntiMagicShell(256),// Version 188
    LifeTidal(257),// Version 188
    HitCriDamR(258),// Version 188
    SmashStack(259),// Version 188
    PartyBarrier(260),// Version 188
    ReshuffleSwitch(261),// Version 188
    SpecialAction(262),// Version 188
    VampDeathSummon(263),// Version 188
    StopForceAtomInfo(264),// Version 188
    SoulGazeCriDamR(265),// Version 188
    SoulRageCount(266),// Version 188
    PowerTransferGauge(267),// Version 188
    AffinitySlug(268),// Version 188
    Trinity(269),// Version 188
    IncMaxDamage(270),// Version 188
    BossShield(271),// Version 188
    MobZoneState(272),// Version 188
    GiveMeHeal(273),// Version 188
    TouchMe(274),// Version 188
    Contagion(275),// Version 188
    ComboUnlimited(276),// Version 188
    SoulExalt(277),// Version 188
    IgnorePCounter(278),// Version 188
    IgnoreAllCounter(279),// Version 188
    IgnorePImmune(280),// Version 188
    IgnoreAllImmune(281),// Version 188
    FinalJudgement(282),// Version 188
    Unknown283(283),
    IceAura(284),// Version 188
    FireAura(285),// Version 188
    VengeanceOfAngel(286),// Version 188
    HeavensDoor(287),// Version 188
    Preparation(288),// Version 188
    BullsEye(289),// Version 188
    IncEffectHPPotion(290),// Version 188
    IncEffectMPPotion(291),// Version 188
    BleedingToxin(292),// Version 188
    IgnoreMobDamR(293),// Version 188
    Asura(294),// Version 188
    Unknown295(295),
    FlipTheCoin(296),// Version 188
    UnityOfPower(297),// Version 188
    Stimulate(298),// Version 188
    ReturnTeleport(299),// Version 188
    DropRIncrease(300),// Version 188
    IgnoreMobpdpR(301),// Version 188
    BDR(302),// Version 188
    CapDebuff(303),// Version 188
    Exceed(304),// Version 188
    DiabolikRecovery(305),// Version 188
    FinalAttackProp(306),// Version 188
    ExceedOverload(307),// Version 188
    OverloadCount(308),// Version 188
    BuckShot(309),// Version 188
    FireBomb(310),// Version 188
    HalfstatByDebuff(311),// Version 188
    SurplusSupply(312),// Version 188
    SetBaseDamage(313),// Version 188
    EVAR(314),// Version 188
    NewFlying(315),// Version 188
    AmaranthGenerator(316),// Version 188
    OnCapsule(317),// Version 188
    CygnusElementSkill(318),// Version 188
    StrikerHyperElectric(319),// Version 188
    EventPointAbsorb(320),// Version 188
    EventAssemble(321),// Version 188
    StormBringer(322),// Version 188
    ACCR(323),// Version 188
    DEXR(324),// Version 188
    Albatross(325),// Version 188
    Translucence(326),// Version 188
    PoseType(327),// Version 188
    LightOfSpirit(328),// Version 188
    ElementSoul(329),// Version 188
    GlimmeringTime(330),// Version 188
    TrueSight(331),// Version 188
    SoulExplosion(332),// Version 188
    SoulMP(333),// Version 188
    FullSoulMP(334),// Version 188
    SoulSkillDamageUp(335),// Version 188
    ElementalCharge(336),// Version 188
    Restoration(337),// Version 188
    CrossOverChain(338),// Version 188
    ChargeBuff(339),// Version 188
    Reincarnation(340),// Version 188
    KnightsAura(341),// Version 188
    ChillingStep(342),// Version 188
    DotBasedBuff(343),// Version 188
    BlessEnsenble(344),// Version 188
    ComboCostInc(345),// Version 188
    ExtremeArchery(346),// Version 188
    NaviFlying(347),// Version 188
    QuiverCatridge(348),// Version 188
    AdvancedQuiver(349),// Version 188
    UserControlMob(350),// Version 188
    ImmuneBarrier(351),// Version 188
    ArmorPiercing(352),// Version 188
    ZeroAuraStr(353),// Version 188
    ZeroAuraSpd(354),// Version 188
    CriticalGrowing(355),// Version 188
    QuickDraw(356),// Version 188
    BowMasterConcentration(357),// Version 188
    TimeFastABuff(359),// Version 188
    TimeFastBBuff(360),// Version 188
    GatherDropR(361),// Version 188
    AimBox2D(362),// Version 188
    IncMonsterBattleCaptureRate(363),// Version 188
    CursorSniping(365),// Version 188
    DebuffTolerance(366),// Version 188
    DotHealHPPerSecond(367),// Version 188
    SpiritGuard(368),// Version 188
    PreReviveOnce(369),// Version 188
    SetBaseDamageByBuff(370),// Version 188
    LimitMP(371),// Version 188
    ReflectDamR(372),// Version 188
    ComboTempest(373),// Version 188
    MHPCutR(374),// Version 188
    MMPCutR(375),// Version 188
    SelfWeakness(376),// Version 188
    ElementDarkness(377),// Version 188
    FlareTrick(378),// Version 188
    Ember(379),// Version 188
    Dominion(380),// Version 188
    SiphonVitality(381),// Version 188
    DarknessAscension(382),// Version 188
    BossWaitingLinesBuff(383),// Version 188
    DamageReduce(384),// Version 188
    ShadowServant(385),// Version 188
    ShadowIllusion(386),// Version 188
    KnockBack(387),// Version 188
    AddAttackCount(388),// Version 188
    ComplusionSlant(389),// Version 188
    JaguarSummoned(390),// Version 188
    JaguarCount(391),// Version 188
    SSFShootingAttack(392),// Version 188
    DevilCry(393),// Version 188
    ShieldAttack(394),// Version 188
    BMageAura(395),// Version 188
    DarkLighting(396),// Version 188
    AttackCountX(397),// Version 188
    BMageDeath(398),// Version 188
    BombTime(399),// Version 188
    NoDebuff(400),// Version 188
    BattlePvP_Mike_Shield(401),// Version 188
    BattlePvP_Mike_Bugle(402),// Version 188
    XenonAegisSystem(403),// Version 188
    AngelicBursterSoulSeeker(404),// Version 188
    HiddenPossession(405),// Version 188
    NightWalkerBat(406),// Version 188
    NightLordMark(407),// Version 188
    WizardIgnite(408),// Version 188
    FireBarrier(409),// Version 188
    ChangeFoxMan(410),// Version 188
    Unknown411(411),
    Unknown412(412),
    Unknown413(413),
    Unknown414(414),
    BattlePvP_Helena_Mark(416),// Version 188
    BattlePvP_Helena_WindSpirit(417),// Version 188
    BattlePvP_LangE_Protection(418),// Version 188
    BattlePvP_LeeMalNyun_ScaleUp(419),// Version 188
    BattlePvP_Revive(420),// Version 188
    PinkbeanAttackBuff(421),// Version 188
    PinkbeanRelax(422),// Version 188
    PinkbeanRollingGrade(423),// Version 188
    PinkbeanYoYoStack(424),// Version 188
    RandAreaAttack(425),// Version 188
    NextAttackEnhance(426),// Version 188
    AranBeyonderDamAbsorb(427),// Version 188
    AranCombotempastOption(428),// Version 188
    NautilusFinalAttack(429),// Version 188
    ViperTimeLeap(430),// Version 188
    RoyalGuardState(431),// Version 188
    RoyalGuardPrepare(432),// Version 188
    MichaelSoulLink(433),// Version 188
    MichaelStanceLink(434),// Version 188
    TriflingWhimOnOff(435),// Version 188
    AddRangeOnOff(436),// Version 188
    KinesisPsychicPoint(437),// Version 188
    KinesisPsychicOver(438),// Version 188
    KinesisPsychicShield(439),// Version 188
    KinesisIncMastery(440),// Version 188
    KinesisPsychicEnergeShield(441),// Version 188
    BladeStance(442),// Version 188
    DebuffActiveSkillHPCon(443),// Version 188
    DebuffIncHP(444),// Version 188
    BowMasterMortalBlow(445),// Version 188
    AngelicBursterSoulResonance(446),// Version 188
    Fever(447),// Version 188
    IgnisRore(448),// Version 188
    RpSiksin(449),// Version 188
    TeleportMasteryRange(450),// Version 188
    FixCoolTime(451),// Version 188
    IncMobRateDummy(452),// Version 188
    AdrenalinBoost(453),// Version 188
    AranSmashSwing(454),// Version 188
    AranDrain(455),// Version 188
    AranBoostEndHunt(456),// Version 188
    HiddenHyperLinkMaximization(457),// Version 188
    RWCylinder(458),// Version 188
    RWCombination(460),// Version 188
    RWMagnumBlow(461),// Version 188
    RWBarrier(462),// Version 188
    RWBarrierHeal(463),// Version 188
    RWMaximizeCannon(464),// Version 188
    RWOverHeat(465),// Version 188
    UsingScouter(466),// Version 188
    RWMovingEvar(467),// Version 188
    Stigma(468),// Version 188
    Unknown469(469),
    Unknown475(475),
    Unknown476(476),
    LightningCascade(477),
    Unknown478(478),
    Unknown479(479),
    Unknown480(480),
    Unknown481(481),
    Unknown485(485),
    Unknown486(486),
    Unknown487(487), // Version 188
    ThrowingStarBarrage(488),
    Unknown495(495),
    Unknown496(496),
    Unknown497(497),
    Unknown498(498),
    Unknown499(499),
    Unknown500(500),
    Unknown501(501),
    Unknown503(503),
    Unknown507(507),
    HayatoStance(508),// Version 188
    HayatoStanceBonus(509),// Version 188
    EyeForEye(510),// Version 188
    WillowDodge(511),// Version 188
    HayatoPAD(513),// Version 188
    HayatoHPR(514),// Version 188
    HayatoMPR(515),// Version 188
    Unknown516(516),
    Jinsoku(519),// Version 188
    HayatoCr(520),// Version 188
    HakuDEF(521), // Version 188
    KannaBDR(522),// Version 188
    Battoujutsu(523),// Version 188
    Unknown524(524),
    Unknown525(525),
    Unknown526(526),
    AnimalChange(527),// Version 188
    TeamRoar(528),// Version 188
    Unknown530(530),
    Unknown532(532),
    Unknown533(533),
    Unknown534(534),
    Unknown535(535),
    Unknown536(536),
    Unknown538(538),
    Unknown539(539),
    YukiMusume(540),
    Unknown543(543),
    Unknown544(544),
    EnergyCharged(545),// Version 188
    DashSpeed(546),// Version 188
    DashJump(547),// Version 188
    RideVehicle(548),// Version 188
    PartyBooster(549),// Version 188
    GuidedBullet(550),// Version 188
    Undead(551),// Version 188
    RideVehicleExpire(552),// Version 188
    
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
        return uFlag < CharacterTemporaryStat.IndieStatCount.uFlag;
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
