package service;

public enum SendPacketOpcode {

    CheckPasswordResult(0),// Version 180
    WorldInformation(1),// Version 180
    LatestConnectedWorld(2),// Version 180
    RecommendWorldMessage(3),// Version 180
    SetClientKey(4),// Version 180
    SetPhysicalWorldID(5),// Version 180
    SelectWorldResult(6),// Version 180
    SelectCharacterResult(7),// Version 180
    AccountInfoResult(8),// Version 180
    CreateMapleAccountResult(9),// Version 180
    CheckDuplicatedIDResult(10),// Version 180
    CreateNewCharacterResult(11),// Version 180
    DeleteCharacterResult(12),// Version 180
    ReservedDeleteCharacterResult(13),// Version 180
    ReservedDeleteCharacterCancelResult(14),// Version 180
    RenameCharacterResult(15),// Version 180
    SetCharacterID(16),// Version 180
    MigrateCommand(17),// Version 180
    AliveReq(18),// Version 180
    PingCheckResult_ClientToGame(19),// Version 180
    AuthenCodeChanged(20),// Version 180
    AuthenMessage(21),// Version 180
    SecurityPacket(22),// Version 180
    PrivateServerPacket(23),// Version 180
    ChangeSPWResult(24),// Version 180
    CheckSPWExistResult(32),// Version 180
    CheckWebLoginEmailID(33),// Version 180
    CheckCrcResult(34),// Version 180
    AlbaRequestResult(35),// Version 180
    ApplyHotfix(37),// Version 180
    UserLimitResult(40),// Version 180
    SetCharacterCreation(47),// Version 180
    NMCOResult(49),// Version 180
    MapLogin(53),
    CheckSPWResult(54),// Version 180
    REMOVE_BG_LAYER(55),// Version 180
    BackgroundEffect(56),// Version 180
    KINESIS_INTRO_ENV2(62),// Version 180
    KINESIS_INTRO_ENV(63),// Version 180
    BeginCharacterData(73),// Version 180
    InventoryOperation(73),// Version 180
    InventoryGrow(74),// Version 180
    StatChanged(75),// Version 180
    TemporaryStatSet(76),// Version 180
    TemporaryStatReset(77),// Version 180
    ForcedStatSet(78),// Version 180
    ForcedStatReset(79),// Version 180
    ChangeSkillRecordResult(80),// Version 180
    ChangeStealMemoryResult(81),// Version 180
    UserDamageFallingCheck(82),// Version 180
    PersonalShopBuyCheck(83),// Version 180
    MobDropMesoPickup(84),// Version 180
    BreakTimeFieldEnter(85),// Version 180
    RuneActSuccess(86),// Version 180
    ResultStealSkillList(87),// Version 180
    SkillUseResult(88),// Version 180
    ExclRequest(89),// Version 180
    GivePopularityResult(90),// Version 180
    Message(91),// Version 180
    MemoResult(92),// Version 180
    MapTransferResult(93),// Version 180
    AntiMacroResult(94),// Version 180
    AntiMacroBombResult(95),// Version 180
    InitialQuizStart(96),// Version 180
    ClaimResult(97),// Version 180
    SetClaimSvrAvailableTime(98),// Version 180
    ClaimSvrStatusChanged(99),// Version 180
    StarPlanetUserCount(100),// Version 180
    SetTamingMobInfo(101),// Version 180
    QuestClear(102),// Version 180
    EntrustedShopCheckResult(103),// Version 180
    SkillLearnItemResult(104),// Version 180
    SkillResetItemResult(105),// Version 180
    AbilityResetItemResult(106),// Version 180
    ExpConsumeResetItemResult(107),// Version 180
    ExpItemGetResult(108),// Version 180
    CharSlotIncItemResult(109),// Version 180
    CharRenameItemResult(110),// Version 180
    GatherItemResult(111),// Version 180
    SortItemResult(112),// Version 180
    RemoteShopOpenResult(113),// Version 180
    PetDeadMessage(114),// Version 180
    CharacterInfo(115),// Version 180
    PartyResult(116),// Version 180
    PartyMemberCandidateResult(117),// Version 180
    UrusPartyMemberCandidateResult(118),// Version 180
    PartyCandidateResult(119),// Version 180
    UrusPartyResult(120),// Version 180
    IntrusionFriendCandidateResult(121),// Version 180
    IntrusionLobbyCandidateResult(122),// Version 180
    ExpeditionRequest(123),// Version 180
    ExpedtionResult(124),// Version 180
    FriendResult(125),// Version 180
    StarFriendResult(126),// Version 180
    LoadAccountIDOfCharacterFriendResult(127),// Version 180
    GuildRequest(128),// Version 180
    GuildResult(128),// Version 180
    AllianceResult(129),// Version 180
    TownPortal(130),// Version 180
    BrodcastMsg(131),// Version 180
    ECHO_MESSAGE(132), // Need to confirm.
    IncubatorResult(133),// Version 180
    IncubatorHotItemResult(134),// Version 180
    ShopScannerResult(135),// Version 180
    ShopLinkResult(136),// Version 180
    MarriageRequest(137),// Version 180
    MarriageResult(138),// Version 180
    WeddingGiftResult(139),// Version 180
    NotifyMarriedPartnerMapTransfer(140),// Version 180
    CashPetFoodResult(141),// Version 180
    CashPetPickUpOffResult(142),// Version 180
    CashPetSkillSettingResult(143),// Version 180
    CashLookChangeResult(144),// Version 180
    CashPetDyeingResult(145),// Version 180
    SetWeekEventMessage(146),// Version 180
    SetPotionDiscountRate(147),// Version 180
    BridleMobCatchFail(148),// Version 180
    ImitatedNPCData(149),// Version 180
    ImitatedNPCDisableInfo(150),// Version 180
    JournalAvatar(151),// Version 180
    LimitedNPCDisableInfo(152),// Version 180
    MonsterBookSetCard(153),// Version 180
    MonsterBookSetCover(154),// Version 180
    HourChanged(155),// Version 180
    MiniMapOnOff(156),// Version 180
    ConsultAuthkeyUpdate(157),// Version 180
    ClassCompetitionAuthkeyUpdate(158),// Version 180
    WebBoardAuthkeyUpdate(159),// Version 180
    SessionValue(160),// Version 180
    PartyValue(161),// Version 180
    FieldSetVariable(162),// Version 180
    FieldValue(163),// Version 180
    BonusExpRateChanged(164),// Version 180
    FamilyChartResult(165),// Version 180
    FamilyInfoResult(166),// Version 180
    FamilyResult(167),// Version 180
    FamilyJoinRequest(168),// Version 180
    FamilyJoinRequestResult(169),// Version 180
    FamilyJoinAccepted(170),// Version 180
    FamilyPrivilegeList(171),// Version 180
    FamilyFamousPointIncResult(172),// Version 180
    FamilyNotifyLoginOrLogout(173),// Version 180
    FamilySetPrivilege(174),// Version 180
    FamilySummonRequest(175),// Version 180
    NotifyLevelUp(176),// Version 180
    NotifyWedding(177),// Version 180
    NotifyJobChange(178),// Version 180
    SetBuyEquipExt(179),// Version 180
    SetPassenserRequest(180),// Version 180
    ScriptProgressMessageBySoul(181),// Version 180
    ScriptProgressMessage(182),// Version 180
    ScriptProgressItemMessage(183),// Version 180
    SetStaticScreenMessage(184),// Version 180
    OffStaticScreenMessage(185),// Version 180
    WeatherEffectNotice(186),// Version 180
    WeatherEffectNoticeY(187),// Version 180
    ProgressMessageFont(188),// Version 180
    DataCRCCheckFailed(189),// Version 180
    ShowSlotMessage(190),// Version 180
    WildHunterInfo(191),// Version 180
    ZeroInfo(192),// Version 180
    ZeroWP(193),// Version 180
    ZeroInfoSubHP(194),// Version 180
    OpenUICreatePremiumAdventurer(195),// Version 180
    FieldSetEnterSuccessed(196),// Version 180
    ResultInstanceTable(197),// Version 180
    CoolTimeSet(198),// Version 180
    ItemPotChange(199),// Version 180
    ItemCoolTimeChange(200),// Version 180
    SetAdDisplayInfo(201),// Version 180
    SetAdDisplayStatus(202),// Version 180
    SetSonOfLinkedSkillResult(203),// Version 180
    SetMapleStyeInfo(207),// Version 180
    SetBuyLimitCount(208),// Version 180
    ResetBuyLimitcount(209),// Version 180
    UpdateUIEventListInfo(210),// Version 180
    DojangRanking(211),// Version 180
    DefenseGameResponse_Shop(212),// Version 180
    DefenseGameResponse_Inventory(213),// Version 180
    ShutdownMessage(214),// Version 180
    ResultSetStealSkill(215),// Version 180
    SlashCommand(216),// Version 180
    StartNavigationRequest(217),// Version 180
    FuncKeySetByScript(218),// Version 180
    CharacterPotentialSet(219),// Version 180
    CharacterPotentialReset(220),// Version 180
    CharacterHonorExp(221),// Version 180
    AswanStateInfo(222),// Version 180
    AswanResult(223),// Version 180
    ReadyForRespawn(224),// Version 180
    ReadyForRespawnByPoint(225),// Version 180
    OpenReadyForRespawnUI(226),// Version 180
    CharacterHonorGift(227),// Version 180
    CrossHunterCompleteResult(228),// Version 180
    CrossHunterShopResult(229),// Version 180
    SetCashItemNotice(230),// Version 180
    SetSpecialCashItem(231),// Version 180
    ShowEventNotice(232),// Version 180
    BoardGameResult(233),// Version 180
    YutGameResult(234),// Version 180
    ValuePackResult(235),// Version 180
    UserUseNaviFlyingResult(236),// Version 180
    MapleStyleResult(237),// Version 180
    OpenWeddingEx(238),// Version 180
    BingoResult(239),// Version 180
    BingoCassandraResult(240),// Version 180
    UpdateVIPGrade(241),// Version 180
    MesoRangerResult(242),// Version 180
    SetMaplePoint(243),// Version 180
    SetAdditionalCashInfo(244),// Version 180
    SetMiracleTime(245),// Version 180
    HyperSkillResetResult(246),// Version 180
    GetServerTime(247),// Version 180
    GetCharacterPosition(248),// Version 180
    SetFixDamageForTest(249),// Version 180
    ReturnEffectConfirm(252),// Version 180
    ReturnEffectModified(253),// Version 180
    WhiteAdditionalCubeResult(254),// Version 180
    BlackCubeResult(255),// Version 180
    MemorialCubeResult(256),// Version 180
    MemorialCubeModified(257),// Version 180
    DressUpInfoModified(258),// Version 180
    ResetOnStateForOnOffSkill(260),// Version 180
    SetOffStateForOnOffSkill(261),// Version 180
    IssueReloginCookie(262),// Version 180
    AvatarPackTest(263),// Version 180
    EvolvingResult(264),// Version 180
    ActionBarResult(265),// Version 180
    GuildContentResult(266),// Version 180
    GuildSearchResult(267),// Version 180
    BufferFlyResult(268),// Version 180
    HalloweenCandyRankingResult(269),// Version 180
    GetRewardResult(270),// Version 180
    Mentoring(271),// Version 180
    GetLotteryResult(272),// Version 180
    CheckProcess(273),// Version 180
    CompleteNpcSpeechSuccess(274),// Version 180
    CompleteSpecialCheckSuccess(275),// Version 180
    SetAccountInfo(276),// Version 180
    SetGachaponFeverTime(277),// Version 180
    AvatarMegaphoneRes(278),// Version 180
    AvatarMegaphoneUpdateMessage(279),// Version 180
    AvatarMegaphoneClearMessage(280),// Version 180
    RequestEventList(281),// Version 180
    LikePoint(282),// Version 180
    SignErrorAck(283),// Version 180
    AskAfterErrorAck(284),// Version 180
    EventNameTagInfo(285),// Version 180
    GiveEventNameTag(286),// Version 180
    JobFreeChangeResult(287),// Version 180
    EventLotteryOpen(288),// Version 180
    EventLotteryResult(289),// Version 180
    InvasionSupportSet(290),// Version 180
    InvasionSupportAttackResult(291),// Version 180
    InvasionSupportBossKill(292),// Version 180
    InvasionSupportSetttingResult(293),// Version 180
    InvasionElapsedTime(294),// Version 180
    InvasionSystemMsg(295),// Version 180
    InvasionBossKeyChange(296),// Version 180
    ScreenMsg(297),// Version 180
    TradeBlockForSnapshot(298),// Version 180
    LimitGoodsNoticeResult(299),// Version 180
    MonsterBattle(300),// Version 180
    MonsterBattleCombat(301),// Version 180
    UniverseBossPossible(302),// Version 180
    UniverseBossImpossible(303),// Version 180
    CashShopPreviewInfo(304),// Version 180
    ChangeSoulCollectionResult(305),// Version 180
    SelectSoulCollectionResult(306),// Version 180
    UserMasterPieceResult(307),// Version 180
    PendantSlotIncResult(308),// Version 180
    BossArenaMatchSucess(309),// Version 180
    BossArenaMatchFail(310),// Version 180
    BossArenaMatchRequestDone(311),// Version 180
    UserSoulMatching(312),// Version 180
    Catapult_UpgradeSkill(313),// Version 180
    Catapult_ResetSkill(314),// Version 180
    PartyQuestRankingResult(315),// Version 180
    CoordinationContestInfo(316),// Version 180
    WorldTransferResult(317),// Version 180
    TrunkSlotIncItemResult(318),// Version 180
    EliteMobWorldMapNotice(319),// Version 180
    RandomPortalWorldMapNotice(320),// Version 180
    WorldTransferHelperNotify(321),// Version 180
    EquipmentEnchantDisplay(322),// Version 180
    TopTowerRankResult(323),// Version 180
    FriendTowerRankResult(324),// Version 180
    TowerResultUIOpen(325),// Version 180
    MannequinResult(326),// Version 180
    IronBoxEvent(327),// Version 180
    CreateKoreanJumpingGame(328),// Version 180
    CreateSwingGame(329),// Version 180
    UserUpdateMapleTVShowTime(330),// Version 180
    ReturnToTitle(331),// Version 180
    ReturnToCharacterSelect(332),// Version 180
    FlameWizardFlameWalkEffect(333),// Version 180
    lameWizardFlareBlink(334),// Version 180
    SummonedAvatarSync(335),// Version 180
    CashShopEventInfo(336),// Version 180
    BlackList(337),// Version 180
    UIOpenTest(338),// Version 180
    BlackListView(339),// Version 180
    ScrollUpgradeFeverTime(340),// Version 180
    TextEquipInfo(341),// Version 180
    TextEquipUIOpen(342),// Version 180
    UIStarPlanetMiniGameResult(343),// Version 180
    UIStarPlanetTrendShop(344),// Version 180
    UIStarPlanetQueue(345),// Version 180
    UIStarPlanetQueueErr(346),// Version 180
    StarPlanetRoundInfo(347),// Version 180
    StarPlanetResult(348),// Version 180
    BackSpeedCtrl(349),// Version 180
    SetMazeArea(350),// Version 180
    CharacterBurning(351),// Version 180
    BattleStatCoreInfo(352),// Version 180
    BattleStatCoreAck(353),// Version 180
    GachaponRewardTestResult(354),// Version 180
    MasterPieceTestRewardResult(355),// Version 180
    RoyalStyleTestRewardResult(356),// Version 180
    BeautyCouponTestRewardResult(357),// Version 180
    NickSkillExpired(358),// Version 180
    RandomMissionResult(359),// Version 180
    TresureResult(360),// Version 180
    TresureJumpHigh(361),// Version 180
    ItemCollection_SetFlag(362),// Version 180
    ItemCollection_CheckComplete(363),// Version 180
    ItemCollection_SendCollectionList(364),// Version 180
    ToadsHammerRequestResult(365),// Version 180
    HyperStatSkillResetResult(366),// Version 180
    InventoryOperationResult(367),// Version 180
    GetSavedUrusSkill(368),// Version 180
    SetRolePlayingCharacterInfo(369),// Version 180
    MVP_Alarm(370),// Version 180
    MonsterCollecion_CompleteReward_Result(371),// Version 180
    UserTowerChairSettingResult(372),// Version 180
    NeedClientResponse(373),// Version 180
    CharacterModified(374),// Version 180
    TradeKingShopItem(376),// Version 180
    TradeKingShopRes(377),// Version 180
    PlatFormarEnterResult(379),// Version 180
    PlatFormar_Oxyzen(380),// Version 180
    VMatrixUpdate(383),// Version 180
    NodeStoneResult(384),// Version 180
    NodeEnhanceResult(385),
    NodeShardResult(386),
    NodeCraftResult(387),
    UserGenderResult(401),// Version 180
    GuildBBSResult(402),// Version 180
    CARD_DROPS(409),// Version 180
    GM_POLICE(416),// Version 180
    PAM_SONG(426),// Version 180
    CommerciUIResponse(431),// Version 180
    MAGIC_WHEEL(436),// Version 180
    REWARD(437),// Version 180
    RemoveDamageSkinResult(442),// Version 180
    MacroSysDataInit(447),// Version 180
    BeginStage(448),// Version 180
    SetField(448),// Version 180
    SetStarPlanetField(450),// Version 180
    SetFarmField(451),// Version 180
    SetCashShop(452),// Version 180
    EndStage(452),// Version 180
    BeginField(453),// Version 180
    TransferFieldReqIgnored(453),// Version 180
    TransferChannelReqIgnored(454),// Version 180
    TransferPvpReqIgnored(455),// Version 180
    FieldSpecificData(456),// Version 180
    GroupMessage(457),// Version 180
    FieldUniverseMessage(458),// Version 180
    Whisper(459),// Version 180
    MobSummonItemUseResult(460),// Version 180
    BossEnvironment(461),// Version 180
    MoveEnvironment(463),// Version 180
    UpdateEnvironment(464),// Version 180
    BlowWeather(465),// Version 180
    AdminResult(466),// Version 180
    Quiz(467),// Version 180
    Desc(468),// Version 180
    FieldEffect(469),// Version 180
    Clock(474),// Version 180
    ContiMove(475),// Version 180
    ContiState(476),// Version 180
    SetQuestClear(477),// Version 180
    SetQuestTime(478),// Version 180
    SetObjectState(479),// Version 180
    DestroyClock(480),// Version 180
    ShowArenaResult(481),// Version 180
    StalkResult(482),// Version 180
    MassacreIncGauge(483),// Version 180
    MassacreResult(484),// Version 180
    QuickslotMappedInit(485),// Version 180
    FootHoldMove(486),// Version 180
    CorrectFootHoldMove(487),// Version 180
    DynamicObjMove(488),// Version 180
    DynamicObjShowHide(489),// Version 180
    DynamicObjUrusSync(490),// Version 180
    FieldKillCount(491),// Version 180
    SmartMobNoticeMsg(492),// Version 180
    MobPhaseChange(493),// Version 180
    MobZoneChange(494),// Version 180
    MobOrderFromSvr(495),// Version 180
    PvPStatusResult(496),// Version 180
    InGameCurNodeEventEnd(497),// Version 180
    ForceAtomCreate(498),// Version 180
    SetAchieveRate(500),// Version 180
    SetQuickMoveInfo(501),// Version 180
    ChangeAswanSiegeWeaponGauge(502),// Version 180
    ObtacleAtomCreate(503),// Version 180
    ObtacleAtomClear(504),// Version 180
    Box2dFootHoldCreate(505),// Version 180
    DebuffObjOn(506),// Version 180
    FieldCreateFallingCatcher(507),// Version 180
    MobChaseEffectSet(508),// Version 180
    MesoExchangeResult(509),// Version 180
    SetMirrorDungeonInfo(510),// Version 180
    SetIntrusion(511),// Version 180
    CannotDropForTradeBlock(512),// Version 180
    FootHoldOnOff(513),// Version 180
    LadderRopeOnOff(514),// Version 180
    MomentAreaOnOff(515),// Version 180
    MomentAreaOnOffAll(516),// Version 180
    ChatLetClientConnect(517),// Version 180
    ChatInduceClientConnect(518),// Version 180
    CoordinationContestResult(519),// Version 180
    EliteState(520),// Version 180
    PlaySound(521),// Version 180
    StackEventGauge(522),// Version 180
    SetUnionField(523),// Version 180
    MTalkOfflineAccountFriendsNameResult(524),// Version 180
    StarPlanetBurningTimeInfo(525),// Version 180
    PublicShareStateValue(526),// Version 180
    FunctionTempBlock(527),// Version 180
    StatusBar(528),// Version 180
    FieldSkillDelay(529),// Version 180
    FieldWeather_Add(530),// Version 180
    FieldWeather_Remove(531),// Version 180
    FieldWeather_Msg(532),// Version 180
    AddWreckage(533),// Version 180
    DeleteWreckage(534),// Version 180
    CreateMirrorImage(535),// Version 180
    FuntionFootholdMan(536),// Version 180
    BeginUserPool(539),// Version 180
    UserEnterField(539),// Version 180
    UserLeaveField(540),// Version 180
    BeginUserCommon(541),// Version 180
    UserChat(541),// Version 180
    UserADBoard(542),// Version 180
    UserMiniRoomBalloon(543),// Version 180
    UserConsumeItemEffect(544),// Version 180
    UserItemUpgradeEffect(545),// Version 180
    UserItemEventUpgradeEffect(546),// Version 180
    UserItemSkillSocketUpgradeEffect(547),// Version 180
    UserItemSkillOptionUpgradeEffect(548),// Version 180
    UserItemReleaseEffect(549),// Version 180
    UserItemUnreleaseEffect(550),// Version 180
    UserItemLuckyItemEffect(551),// Version 180
    UserItemMemorialCubeEffect(552),// Version 180
    UserItemAdditionalUnReleaseEffect(553),// Version 180
    UserItemAdditionalSlotExtendEffect(554),// Version 180
    UserItemFireWorksEffect(555),// Version 180
    UserItemOptionChangeEffect(556),// Version 180
    UserItemRedCubeResult(557),// Version 180
    UserItemBonusCubeResult(558),// Version 180
    UserHitByUser(559),// Version 180
    UserDotByUser(560),// Version 180
    UserResetAllDot(561),// Version 180
    UserDamageByUser(562),// Version 180
    UserTeslaTriangle(563),// Version 180
    UserFollowCharacter(564),// Version 180
    UserShowPQReward(565),// Version 180
    UserSetOneTimeAction(566),// Version 180
    UserMakingSkillResult(567),// Version 180
    UserMakingMeisterSkillEff(568),// Version 180
    UserGatherResult(569),// Version 180
    UserExplode(570),// Version 180
    UserHitByCounter(571),// Version 180
    PyramidLethalAttack(572),// Version 180
    UserMixerResult(573),// Version 180
    UserWaitQueueReponse(574),// Version 180
    UserCategoryEventNameTag(575),// Version 180
    UserSetDamageSkin(576),// Version 180
    UserSetDamageSkinPremium(577),// Version 180
    UserSoulEffect(578),// Version 180
    UserSitResult(579),// Version 180
    UserStarPlanetPointInfo(580),// Version 180
    UserStarPlanetAvatarLookSet(581),// Version 180
    UserTossedBySkill(582),// Version 180
    UserBattleAttackHit(583),// Version 180
    UserBattleUserHitByMob(584),// Version 180
    UserFreezeHotEventInfo(585),// Version 180
    UserEventBestFriendInfo(586),// Version 180
    UserSetReapeatOneTimeAction(587),// Version 180
    UserSetReplaceMoveAction(588),// Version 180
    UserItemInGameCubeResult(589),// Version 180
    UserBattlePvPTemporaryStat(590),// Version 180
    UserSetActiveEmoticonItem(591),// Version 180
    UserCreatePsychicLock(592),// Version 180
    UserRecreatePathPsychicLock(593),// Version 180
    UserReleasePsychicLock(594),// Version 180
    UserReleasePsychicLockMob(595),// Version 180
    UserCreatePsychicArea(596),// Version 180
    UserReleasePsychicArea(597),// Version 180
    UserRWZeroBunkerMobBind(598),// Version 180
    UserBeastFormWingOnOff(599),// Version 180
    UserMesoChairAddMeso(600),// Version 180
    UserRefreshNameTagMark(601),// Version 180
    UserStigmaEffect(604),// Version 180
    BeginPet(605),// Version 180
    PetActivated(605),// Version 180
    PetMove(606),// Version 180
    PetAction(607),// Version 180
    PetNameChanged(608),// Version 180
    PetLoadExceptionList(610),// Version 180
    PetHueChanged(613),// Version 180
    PetModified(614),// Version 180
    PetActionCommand(615),// Version 180
    EndPet(615),// Version 180
    BeginDragon(616),// Version 180
    DragonEnterField(616),// Version 180
    DragonMove(617),// Version 180
    DragonVanish_Script(618),// Version 180
    DragonLeaveField(619),// Version 180
    EndDragon(619),// Version 180
    BeginAndroid(620),// Version 180
    AndroidEnterField(620),// Version 180
    AndroidMove(621),// Version 180
    AndroidActionSet(622),// Version 180
    AndroidModified(623),// Version 180
    AndroidLeaveField(624),// Version 180
    EndAndroid(624),// Version 180
    BeginFoxMan(625),// Version 180
    FoxManEnterField(625),// Version 180
    FoxManMove(626),// Version 180
    FoxManExclResult(627),// Version 180
    FoxManShowChangeEffect(628),// Version 180
    FoxManModified(629),// Version 180
    FoxManLeaveField(630),// Version 180
    EndFoxMan(630),// Version 180
    BeginSkillPet(631),// Version 180
    SkillPetMove(632),// Version 180
    SkillPetAction(633),// Version 180
    SkillPetState(634),// Version 180
    SkillPetNameChanged(635),// Version 180
    SkillPetLoadExceptionList(636),// Version 180
    SkillPetTransferField(637),// Version 180
    EndSkillPet(638),// Version 180
    ShowNebuliteEffect(640), // Need to confirm
    BeginFamiliar(648),// Version 180
    FamiliarEnterField(648),// Version 180
    FamiliarMove(649),// Version 180
    FamiliarAction(650),// Version 180
    FamiliarAttack(651),// Version 180
    FamiliarNameResult(652),// Version 180
    FamiliarTransferField(653),// Version 180
    FamiliarFatigueResult(654),// Version 180
    EndFamiliar(654),// Version 180
    EndUserCommon(658),// Version 180
    BeginUserRemote(660),// Version 180
    UserMove(660),// Version 180
    UserMeleeAttack(661),// Version 180
    UserShootAttack(662),// Version 180
    UserMagicAttack(663),// Version 180
    UserBodyAttack(664),// Version 180
    UserSkillPrepare(665),// Version 180
    UserMovingShootAttackPrepare(666),// Version 180
    UserSkillCancel(667),// Version 180
    UserHit(668),// Version 180
    UserEmotion(669),// Version 180
    AndroidEmotion(670),// Version 180
    UserSetActiveEffectItem(671),// Version 180
    UserSetActiveMonkeyEffectItem(672),// Version 180
    UserSetActiveNickItem(673),// Version 180
    UserSetDefaultWingItem(674),// Version 180
    UserSetKaiserTransformItem(675),// Version 180
    UserSetCustomRiding(676),// Version 180
    UserShowUpgradeTombEffect(677),// Version 180
    UserSetActivePortableChair(678),// Version 180
    UserAvatarModified(679),// Version 180
    UserEffectRemote(680),// Version 180
    UserTemporaryStatSet(681),// Version 180
    UserTemporaryStatReset(682),// Version 180
    UserHP(683),// Version 180
    UserGuildNameChanged(684),// Version 180
    UserGuildMarkChanged(685),// Version 180
    UserPvPTeamChanged(686),// Version 180
    GatherActionSet(687),// Version 180
    UpdatePvPHPTag(688),// Version 180
    UserEvanDragonGlide(689),// Version 180
    UserKeyDownAreaMove(690),// Version 180
    UserLaserInfo(691),// Version 180
    UserKaiserColorOrMorphChange(692),// Version 180
    UserDestroyGranade(693),// Version 180
    UserSetItemAction(694),// Version 180
    ZeroTag(703),// Version 180
    UserIntrusionEffect(704),// Version 180
    ZeroLastAssistState(705),// Version 180
    UserSetMoveGrenade(706),// Version 180
    EndUserRemote(713),// Version 180
    BeginUserLocal(714),// Version 180
    UserEmotionLocal(714),// Version 180
    AndroidEmotionLocal(715),// Version 180
    UserEffectLocal(716),// Version 180
    UserTeleport(717),// Version 180
    UserQuestResult(723),// Version 180
    UserHint(724), // Need to confirm
    UserPetSkillChanged(725),// Version 180
    UserBalloonMsg(726),// Version 180
    PlayEventSound(727),// Version 180
    PlayMinigameSound(728),// Version 180
    UserMakerResult(729),// Version 180
    UserOpenConsultBoard(730),// Version 180
    UserOpenClassCompetitionPage(731),// Version 180
    UserOpenUI(732),// Version 180
    UserOpenUIWithOption(733),// Version 180
    INTRO_LOCK(734),// Version 180
    INTRO_ENABLE_UI(735),// Version 180
    INTRO_DISABLE_UI(736),// Version 180
    UserHireTutor(737),// Version 180
    UserTutorMsg(738),// Version 180
    HireTutorById(739),// Version 180
    UserSetPartner(740),// Version 180
    UserSetPartnerAction(741),// Version 180
    UserSetPartnerForceFlip(742),// Version 180
    UserSwitchRP(743),// Version 180
    ModCombo(744),// Version 180
    IncComboByComboRecharge(745),// Version 180
    SetRadioSchedule(746),// Version 180
    UserOpenSkillGuide(747),// Version 180
    UserNoticeMsg(748),// Version 180
    UserChatMsg(749),// Version 180
    UserSetUtilDlg(750),// Version 180
    UserBuffzoneEffect(751),// Version 180
    UserTimeBombAttack(752),// Version 180
    UserExplosionAttack(753),// Version 180
    UserPassiveMove(754),// Version 180
    UserFollowCharacterFailed(755),// Version 180
    UserRequestExJablin(756),// Version 180
    CreateNewCharacterResultPremiumAdventurer(757),// Version 180
    GatherRequestResult(758),// Version 180
    RuneStoneUseAck(759),// Version 180
    UserBagItemUseResult(760),// Version 180
    RandomTeleportKey(761),// Version 180
    SetGagePoint(762),// Version 180
    UserInGameDirectionEvent(763),// Version 180
    MedalReissueResult(764),// Version 180
    DodgeSkillReady(766),// Version 180
    RemoveMicroBuffSkill(767),// Version 180
    UserPlayMovieClip(768),// Version 180
    RewardMobListResult(769),// Version 180
    IncJudgementStack(770),// Version 180
    IncCharmByCashPRMsg(771),// Version 180
    SetBuffProtector(772),// Version 180
    ChangeLarknessStack(773),// Version 180
    DetonateBomb(774),// Version 180
    AggroRankInfo(775),// Version 180
    DeathCountInfo(776),// Version 180
    IndividualDeathCountInfo(777),// Version 180
    UserSetDressUpState(778),// Version 180
    UserSeverAckMobZoneStateChange(779),// Version 180
    SummonEventRank(780),// Version 180
    SummonEventReward(781),// Version 180
    UserRandomEmotion(782),// Version 180
    UserFlipTheCoinEnabled(783),// Version 180
    UserTrickOrTreatResult(784),// Version 180
    UserGiantPetBuff(785),// Version 180
    UserB2BodyResult(786),// Version 180
    SetDead(787),// Version 180
    OpenUIOnDead(788),// Version 180
    ExpiredNotice(789),// Version 180
    UserLotteryItemUIOpen(790),// Version 180
    UserRouletteStart(791),// Version 180
    UserSitOnTimeCapsule(792),// Version 180
    UserSitOnDummyChair(793),// Version 180
    UserGoMonsterFarm(794),// Version 180
    UserMonsterLifeInviteItemResult(795),// Version 180
    PhotoGetResult(796),// Version 180
    UserFinalAttackRequest(797),// Version 180
    UserSetGun(798),// Version 180
    UserSetAmmo(799),// Version 180
    UserCreateGun(800),// Version 180
    UserClearGun(801),// Version 180
    UserShootAttackInFPSMode(802),// Version 180
    MirrorDungeonEnterFail(803),// Version 180
    MirrorDungeonUnitCleared(804),// Version 180
    MirrorDungeonDetail(805),// Version 180
    MirrorDungeonRecord(806),// Version 180
    UserOpenURL(807),// Version 180
    ZeroCombatRecovery(808),// Version 180
    MirrorStudyUIOpen(809),// Version 180
    SkillCooltimeReduce(810),// Version 180
    MirrorReadingUIOpen(811),// Version 180
    UserControlMobSkillPushAck(812),// Version 180
    ZeroLevelUpAlram(813),// Version 180
    UserControlMobSkillPopAck(814),// Version 180
    UserControlMobSkillFail(815),// Version 180
    SummonedForceRemove(816),// Version 180
    UserRespawn(817),// Version 180
    UserControlMobSkillForcedPopAck(818),// Version 180
    MonsterBattleCapture(819),// Version 180
    IsUniverse(820),// Version 180
    PortalGroupAck(821),// Version 180
    SetMovable(822),// Version 180
    UserControlMobSkillPushCoolTime(823),// Version 180
    MoveParticleEff(824),// Version 180
    UserDoActiveEventSkillByScript(825),// Version 180
    SetStatusBarJobNameBlur(826),// Version 180
    RuneStoneSkillAck(827),// Version 180
    RuneStoneOverTime(828),// Version 180
    MoveToContents_CannotMigrate(829),// Version 180
    PlayAmbientSound(831),// Version 180
    StopAmbientSound(832),// Version 180
    FlameWizardElementFlameSummon(833),// Version 180
    UserCameraMode(834),// Version 180
    SpotlightToCharacter(835),// Version 180
    CheckBossPartyByScriptDone(836),// Version 180
    FreeLookChangeUIOpen(837),// Version 180
    FreeLookChangeSuccess(838),// Version 180
    SetGrayBackGround(839),// Version 180
    GetNpcCurrentAction(840),// Version 180
    CameraRotation(841),// Version 180
    CameraSwitch(842),// Version 180
    CameraCtrlMsg(843),// Version 180
    UserSetFieldFloating(844),// Version 180
    AddPopupSay(845),// Version 180
    RemovePopupSay(846),// Version 180
    JaguarSkill(847),// Version 180
    NpcActionLayerRelmove(848),// Version 180
    UserClientResolutionRequest(849),// Version 180
    UserBonusAttackRequest(850),// Version 180
    UserRandAreaAttackRequest(851),// Version 180
    JaguarActive(852),// Version 180
    SkillCooltimeSetM(853),// Version 180
    SetCarryReactorInfo(854),// Version 180
    ReactorSkillUseRequest(855),// Version 180
    OpenBattlePvPChampSelectUI(856),// Version 180
    BattlePvPItemDropSound(857),// Version 180
    SetPointForMiniGame(858),// Version 180
    PlantPotClickResult(859),// Version 180
    PlantPotEffect(860),// Version 180
    UserFixDamage(861),// Version 180
    RoyalGuardAttack(862),// Version 180
    DoActivePsychicArea(863),// Version 180
    UserEnterFieldPsychicInfo(864),// Version 180
    UserLeaveFieldPsychicInfo(865),// Version 180
    TouchMeStateResult(866),// Version 180
    UrusFieldScoreUpdate(867),// Version 180
    UrusReusltUIOpen(868),// Version 180
    UrusNoMoreLife(869),// Version 180
    RegisterTeleport(870),// Version 180
    UserCreateAreaDotInfo(872),// Version 180
    SetSlownDown(875),// Version 180
    RegisterExtraSkill(876),// Version 180
    ResWarriorLiftMobInfo(877),// Version 180
    UserRenameResult(878),// Version 180
    UserDamageSkinSaveResult(879),// Version 180
    UserLocalStigmaStackDuration(880),// Version 180
    MesoGiveSucceeded(883),// Version 180
    MesoGiveFailed(884),// Version 180
    RandomMesoGiveSucceeded(885),// Need to confirm.
    RandomMesoGiveFailed(886),// Need to confirm.
    FamiliarRegister(903),// Version 180
    KaiserSkillShortcut(910),// Need to confirm.
    SalonResult(922),// Version 180
    ModHayatoCombo(926),// Version 180
    SkillCooltimeSet(933),// Version 180
    EndUserLocal(933),// Version 180
    EndUserPool(934),// Version 180
    BeginSummoned(934),// Version 180
    SummonedEnterField(935),// Version 180
    SummonedLeaveField(936),// Version 180
    SummonedMove(937),// Version 180
    SummonedAttack(938),// Version 180
    SummonedAttackPvP(939),// Version 180
    SummonedSetReference(940),// Version 180
    SummonedSkill(941),// Version 180
    SummonedSkillPvP(942),// Version 180
    SummonedHPTagUpdate(943),// Version 180
    SummonedAttackDone(944),// Version 180
    SummonedSetAbleResist(945),// Version 180
    SummonedAction(946),// Version 180
    SummonedAssistAttackRequest(947),// Version 180
    SummonedAttackActive(948),// Version 180
    SummonedBeholderRevengeAttack(949),// Version 180
    SummonedHit(950),// Version 180
    EndSummoned(950),// Version 180
    BeginMobPool(953),// Version 180
    MobEnterField(953),// Version 180
    MobLeaveField(954),// Version 180
    MobChangeController(955),// Version 180
    MobSetAfterAttack(956),// Version 180
    MobBlockAttack(957),// Version 180
    UrusOnlyMobSkill(958),// Version 180
    BeginMob(959),// Version 180
    MobMove(959),// Version 180
    MobCtrlAck(960),// Version 180
    MobCtrlHint(961),// Version 180
    MobStatSet(962),// Version 180
    MobStatReset(963),// Version 180
    MobSuspendReset(964),// Version 180
    MobAffected(965),// Version 180
    MobDamaged(966),// Version 180
    MobSpecialEffectBySkill(969),// Version 180
    MobHPChange(970),// Version 180
    MobCrcKeyChanged(971),// Version 180
    MobCrcDataRequest(972),// Version 180
    MobHPIndicator(973),// Version 180
    MobCatchEffect(974),// Version 180
    MobStealEffect(975),// Version 180
    MobEffectByItem(976),// Version 180
    MobSpeaking(977),// Version 180
    MobMessaging(978),// Version 180
    MobSkillDelay(979),// Version 180
    MobRequestResultEscortInfo(980),// Version 180
    MobEscortStopEndPermmision(981),// Version 180
    MobEscortStopByScript(982),// Version 180
    MobEscortStopSay(983),// Version 180
    MobEscortReturnBefore(984),// Version 180
    MobNextAttack(985),// Version 180
    MobTeleport(986),// Version 180
    MobForcedAction(987),// Version 180
    MobForcedSkillAction(988),// Version 180
    MobTimeResist(990),// Version 180
    MobAllKill(991),// Version 180
    MobAttackBlock(992),// Version 180
    MobAttackPriority(993),// Version 180
    MobAttackTimeInfo(994),// Version 180
    MobDamageShareInfoToLocal(995),// Version 180
    MobDamageShareInfoToRemote(996),// Version 180
    BreakDownTimeZoneTimeOut(997),// Version 180
    MoveAreaSet(998),// Version 180
    MobDoSkillByHit(999),// Version 180
    CastingBarSkill(1000),// Version 180
    MobFlyTarget(1001),// Version 180
    BounceAttackSkill(1002),// Version 180
    MobAffectedAreaByHit(1003),// Version 180
    MobLtRbDamageSkill(1004),// Version 180
    MobSummonSubBodySkill(1005),// Version 180
    MobLaserControl(1006),// Version 180
    MobScale(1007),// Version 180
    MobSpecialAction(1008),// Version 180
    MobPartSystem(1009),// Version 180
    MobForceChase(1010),// Version 180
    MobHangOver(1011),// Version 180
    MobHangOverRelease(1012),// Version 180
    MobDeadFPSMode(1013),// Version 180
    MobAirHit(1014),// Version 180
    MobDemianDelayedAttackCreate(1015),// Version 180
    RegisterMobZone(1016),// Version 180
    UnregisterMobZone(1017),// Version 180
    SetNextTargetFromSvr(1018),// Version 180
    MobAttackedByMob(1019),// Version 180
    EndMob(1026),// Version 180
    EndMobPool(1027),// Version 180
    BeginMinionPool(1028),// Version 180
    MinionEnterField(1028),// Version 180
    MinionLeaveField(1029),// Version 180
    MinionChangeController(1030),// Version 180
    MinionGenBeyondSplit(1031),// Version 180
    EndMinionPool(1031),// Version 180
    BeginNpcPool(1032),// Version 180
    NpcEnterField(1032),// Version 180
    NpcLeaveField(1033),// Version 180
    NpcEnterFieldForQuickMove(1034),// Version 180
    NpcChangeController(1035),// Version 180
    BeginNpc(1037),// Version 180
    NpcMove(1037),// Version 180
    NpcUpdateLimitedInfo(1038),// Version 180
    NpcShowQuizScore(1039),// Version 180
    NpcShowQuizScoreAni(1040),// Version 180
    ForceMoveByScript(1041),// Version 180
    ForceFlipByScript(1042),// Version 180
    NpcEmotion(1044),// Version 180
    NpcCharacterBaseAction(1045),// Version 180
    NpcViewOrHide(1046),// Version 180
    NpcPresentTimeSet(1048),// Version 180
    NpcSpecialActionReset(1049),// Version 180
    NpcSetScreenInfo(1050),// Version 180
    NpcLocalRepeatEffect(1051),// Version 180
    NpcSetNoticeBoardInfo(1052),// Version 180
    NpcSpecialAction(1053),// Version 180
    BeginNpcTemplate(1054),// Version 180
    NpcSetScript(1054),// Version 180
    EndNpcTemplate(1054),// Version 180
    EndNpcPool(1055),// Version 180
    BeginEmployeePool(1056),// Version 180
    EmployeeEnterField(1056),// Version 180
    EmployeeLeaveField(1057),// Version 180
    EmployeeMiniRoomBalloon(1058),// Version 180
    EndEmployeePool(1058),// Version 180
    BeginDropPool(1059),// Version 180
    DropEnterField(1059),// Version 180
    DropLeaveField(1061),// Version 180
    EndDropPool(1061),// Version 180
    BeginMessageBoxPool(1062),// Version 180
    CreateMessageBoxFailed(1062),// Version 180
    MessageBoxEnterField(1063),// Version 180
    MessageBoxLeaveField(1064),// Version 180
    EndMessageBoxPool(1064),// Version 180
    BeginAffectedAreaPool(1065),// Version 180
    AffectedAreaCreated(1065),// Version 180
    MobSkillInstalledFire(1066),// Version 180
    AffectedAreaRemoved(1067),// Version 180
    EndAffectedAreaPool(1067),// Version 180
    BeginTownPortalPool(1068),// Version 180
    TownPortalCreated(1068),// Version 180
    TownPortalRemoved(1069),// Version 180
    EndTownPortalPool(1069),// Version 180
    BeginRandomPortalPool(1070),// Version 180
    RandomPortalCreated(1070),// Version 180
    RandomPortalTryEnterRequest(1071),// Version 180
    RandomPortalRemoved(1072),// Version 180
    EndRandomPortalPool(1072),// Version 180
    BeginOpenGatePool(1073),// Version 180
    OpenGateCreated(1073),// Version 180
    OpenGateClose(1074),// Version 180
    OpenGateRemoved(1075),// Version 180
    EndOpenGatePool(1075),// Version 180
    BeginReactorPool(1076),// Version 180
    ReactorChangeState(1076),// Version 180
    ReactorMove(1077),// Version 180
    ReactorEnterField(1078),// Version 180
    ReactorStateReset(1079),// Version 180
    ReactorOwnerInfo(1080),// Version 180
    ReactorRemove(1081),// Version 180
    ReactorLeaveField(1082),// Version 180
    EndReactorPool(1082),// Version 180
    BeginFishingZonePool(1083),// Version 180
    FishingInfo(1083),// Version 180
    FishingReward(1084),// Version 180
    FishingZoneInfo(1085),// Version 180
    EndFishingZonePool(1085),// Version 180
    BeginPersonalMapObject(1086),// Version 180
    DecomposerEnterField(1086),// Version 180
    DecomposerLeaveField(1087),// Version 180
    EndPersonalMapObject(1087),// Version 180
    BeginEtcFieldObj(1051),// Version 177
    SnowBallState(1051),// Version 177
    SnowBallHit(1052),// Version 177
    SnowBallMsg(1053),// Version 177
    SnowBallTouch(1054),// Version 177
    CoconutHit(1055),// Version 177
    CoconutScore(1056),// Version 177
    HealerMove(1057),// Version 177
    PulleyStateChange(1058),// Version 177
    MCarnivalEnter(1059),// Version 177
    MCarnivalPersonalCP(1060),// Version 177
    MCarnivalTeamCScore(1061),// Version 177
    MCarnivalSpellCooltime(1062),// Version 177
    MCarnivalResultSuccess(1063),// Version 177
    MCarnivalResultFail(1064),// Version 177
    MCarnivalDeath(1065),// Version 177
    MCarnivalMemberOut(1066),// Version 177
    MCarnivalGameResult(1067),// Version 177
    MCarnivalUpdateRankInfo(1068),// Version 177
    ArenaScore(1069),// Version 177
    BattlefieldEnter(1070),// Version 177
    BattlefieldScore(1071),// Version 177
    BattlefieldTeamChanged(1072),// Version 177
    WitchtowerScore(1073),// Version 177
    BossSummonTimer(1074),// Version 177
    PVPFieldEnter(1075),// Version 177
    PVPTeamChange(1076),// Version 177
    PVPModeChange(1077),// Version 177
    PVPStateChange(1078),// Version 177
    PVPUpdateCount(1079),// Version 177
    PVPModeResult(1080),// Version 177
    PVPUpdateTeamInfo(1081),// Version 177
    PVPUpdateRankInfo(1082),// Version 177
    PVPHPChanged(1083),// Version 177
    PVPTeamScore(1084),// Version 177
    PVPReviveMessage(1085),// Version 177
    PVPScreenEffect(1086),// Version 177
    PVPIceKnightHPChange(1087),// Version 177
    DefenseWave(1088),// Version 177
    DefenseLife(1089),// Version 177
    DefensePoint(1090),// Version 177
    DefenseScoreRank(1091),// Version 177
    DefenseResult(1092),// Version 177
    RandomDropPicked(1093),// Version 177
    BroadCastRandomDropPicked(1094),// Version 177
    BroadCastRandomDropPhase(1095),// Version 177
    RandomDropResult(1096),// Version 177
    RandomDropPointEffect(1097),// Version 177
    PVPHardCoreMigrate(1098),// Version 177
    PVPHardCoreDead(1099),// Version 177
    PVPHardCoreKill(1100),// Version 177
    PVPHardcoreGauge(1101),// Version 177
    PVPHardcoreFieldInfo(1102),// Version 177
    PVPHardcoreEnter(1103),// Version 177
    PVPHardcoreFieldChange(1104),// Version 177
    PVPHardcoreShutDown(1105),// Version 177
    PVPHardcoreChampionEffect(1106),// Version 177
    MultiStage_StageSet(1107),// Version 177
    MultiStage_MobCount(1108),// Version 177
    Cook_SetRecipes(1109),// Version 177
    RuneEnterField(1206),// Version 180
    RuneLeaveField(1207),// Version 180
    EndEtcFieldObj(1294),// Version 177
    BeginScript(1343),// Version 180
    ScriptMessage(1343),// Version 180
    EndScript(1343),// Version 180
    BeginShop(1344),// Version 180
    OpenShopDlg(1344),// Version 180
    ShopResult(1345),// Version 180
    EndShop(1345),// Version 180
    GACHAPIERROT(1346),// Version 180
    GACHAPON_UPDATE(1347),// Version 180
    TrunkResult(1370),// Version 180
    BeginStoreBank(1371),// Version 180
    StoreBankGetAllResult(1371),// Version 180
    StoreBankResult(1372),// Version 180
    EndStoreBank(1372),// Version 180
    RPSGame(1373),// Version 180
    GSRPSGame(1374),// Version 180
    StarPlanet_GSRPSGame(1375),// Version 180
    Messenger(1376),// Version 180
    MiniRoom(1377),// Version 180
    SetCashShopInitialItem(1378),// Version 180
    TryMigrateCashShop(1379),// Version 180
    BeginTournament(1380),// Version 180
    Tournament(1380),// Version 180
    TournamentMatchTable(1381),// Version 180
    TournamentSetPrize(1382),// Version 180
    TournamentNoticeUEW(1383),// Version 180
    TournamentAvatarInfo(1384),// Version 180
    EndTournament(1384),// Version 180
    BeginWedding(1385),// Version 180
    WeddingProgress(1385),// Version 180
    WeddingCremonyEnd(1386),// Version 180
    EndWedding(1386),// Version 180
    BeginGhostPark(1387),// Version 180
    GhostParkFieldStart(1387),// Version 180
    GhostParkRuneUseAck(1388),// Version 180
    GhostParkRuneAppear(1389),// Version 180
    GhostParkRuneDisappear(1390),// Version 180
    GhostParkSetKilledMobExpRate(1391),// Version 180
    GhostParkSetCurseLevelExpRate(1392),// Version 180
    GhostParkHomingBulletCreate(1393),// Version 180
    GhostParkFieldEnd(1394),// Version 180 // 1332 V176 // 1394 - 1332 = 62
    EndGhostPark(1394),// Version 180
    Parcel(1395),// Version 180
    EndField(1396),// Version 180
    BeginFuncKeyMapped(1509),// Version 180
    FuncKeyMappedInit(1509),// Version 180
    PetConsumeItemInit(1510),// Version 180
    PetConsumeMPItemInit(1511),// Version 180
    PetConsumeCureItemInit(1512),// Version 180
    PetBuff(1513),// Version 180
    EndFuncKeyMapped(1513),// Version 180
    BeginGoldenHammer(1514),// Version 180
    GoldenHammerResult(1515),// Version 180
    EndGoldenHammer(1516),// Version 180
    BeginEgoEquip(1531),// Version 180
    EgoEquipGaugeCompleteReturn(1532),// Version 180
    EgoEquipCreateUpgradeItemCostInfo(1533),// Version 180
    EgoEquipCheckUpgradeItemResult(1534),// Version 180
    EgoEquipItemUpgradeEffect(1535),// Version 180
    EndEgoEquip(1536),// Version 180
    BeginInheritance(1537),// Version 180
    InheritanceInfo(1538),// Version 180
    InheritanceComplete(1539),// Version 180
    EndInheritance(1540),// Version 180
    BeginMirrorReading(1541),// Version 180
    MirrorReadingSelectBookResult(1542),// Version 180
    EndMirrorReading(1543),// Version 180
    BeginFieldAttackObjPool(1544),// Version 180
    FieldAttackObjCreate(1544),// Version 180
    FieldAttackObjRemoveBySingleKey(1545),// Version 180
    FieldAttackObjRemoveByList(1546),// Version 180
    FieldAttackObjRemoveAll(1547),// Version 180
    FieldAttackObjStart(1548),// Version 180
    FieldAttackObjSetAttack(1549),// Version 180
    FieldAttackObjSetOwner(1550),// Version 180
    FieldAttackObjResetOwner(1551),// Version 180
    FieldAttackObjPushAct(1562),// Version 180
    EndFieldAttackObjPool(1553),// Version 180
    BeginBattleRecord(1555),// Version 180
    BattleRecordDotDamageInfo(1556),// Version 180
    BattleRecordKillDamageInfo(1557),// Version 180
    BattleRecordBattleDamageInfo(1558),// Version 180
    BattleRecordRequestResult(1559),// Version 180
    BattleRecordAggroInfo(1560),// Version 180
    BattleRecordSkillDamageLog(1561),// Version 180
    EndBattleRecord(1562),// Version 180
    SocketCreateResult(1565),// Version 180
    ViciousHammerResult(1567),// Version 180

    GOLDEN_HAMMER(1431),
    ZERO_SCROLL_START(1447),
    ZERO_WEAPON_UI(1448),
    ZERO_PLACE_SCROLL(1449),
    ZERO_POTENTIAL_RESET(1450),
    PLACE_ARROW_BLASTER(1460),
    BLASTER_DISSAPEAR(1462),
    PLACE_ARROW_BLASTER2(1465),
    BATTLE_ANALYSIS(1475),
    ALIEN_SOCKET_CREATOR(1481),
    VICIOUS_HAMMER(1483),
    BOOSTER_PACK(1490),
    BLOCK_PORTAL(1491),
    BOOSTER_FAMILIAR(1492),
    BUFF_BAR(4095),
    GAME_POLL_REPLY(4095),
    GAME_POLL_QUESTION(4095),
    ENGLISH_QUIZ(4095),
    FISHING_BOARD_UPDATE(4095),
    BOAT_EFFECT(4095),
    FISHING_CAUGHT(4095),
    SIDEKICK_OPERATION(4095),
    FARM_PACKET1(860),
    FARM_ITEM_PURCHASED(861),
    FARM_ITEM_GAIN(856),
    HARVEST_WARU(858),
    FARM_MONSTER_GAIN(859),
    FARM_INFO(872),
    FARM_MONSTER_INFO(873),
    FARM_QUEST_DATA(874),
    FARM_QUEST_INFO(875),
    FARM_MESSAGE(876), //36C
    UPDATE_MONSTER(877),
    AESTHETIC_POINT(878),
    UPDATE_WARU(879),
    FARM_EXP(884),
    FARM_PACKET4(885),
    QUEST_ALERT(887),
    FARM_PACKET8(888),
    FARM_FRIENDS_BUDDY_REQUEST(891),
    FARM_FRIENDS(892),
    FARM_USER_INFO(904),
    FARM_AVATAR(906),
    FRIEND_INFO(909),
    FARM_RANKING(911), //+69
    SPAWN_FARM_MONSTER1(915),
    SPAWN_FARM_MONSTER2(916),
    RENAME_MONSTER(917),
    START_TV(1334), // VERSION 170
    REMOVE_TV(1335), // VERSION 170
    ENABLE_TV(1336), // VERSION 170

    /*CS_UPDATE(1388), //355
    CS_OPERATION(1389), //356
    CS_MESO_UPDATE(1392), //359
    //0x314 int itemid int sn
    CASH_SHOP(1412), // VERSION 176
    CASH_SHOP_UPDATE(1413), // VERSION 176*/
    CS_UPDATE(1406), //355
    CS_OPERATION(1407), //356
    CS_MESO_UPDATE(1408), //359
    //0x314 int itemid int sn
    CASH_SHOP(1431), // VERSION 176
    CASH_SHOP_UPDATE(1432), // VERSION 176

    GACHAPON_STAMPS(595),
    FREE_CASH_ITEM(596),
    CS_SURPRISE(597),
    XMAS_SURPRISE(598),
    ONE_A_DAY(600),
    NX_SPEND_GIFT(602),
    RECEIVE_GIFT(602), //new v145

    HORNTAIL_SHRINE(1207), // VERSION 170
    PINK_ZAKUM_SHRINE(1199), // VERSION 170
    ZAKUM_SHRINE(993), // VERSION 170
    CHAOS_ZAKUM_SHRINE(1211), // VERSION 170
    MONSTER_CARNIVAL_OBTAINED_CP(1047), //296
    MONSTER_CARNIVAL_STATS(1048), ////297
    MONSTER_CARNIVAL_SUMMON(1049), //299
    MONSTER_CARNIVAL_MESSAGE(1051), //29A
    MONSTER_CARNIVAL_DIED(1052), //29B
    MONSTER_CARNIVAL_LEAVE(1053), //29C
    MONSTER_CARNIVAL_RESULT(1054), //29D
    MONSTER_CARNIVAL_RANKING(1055), //29E
    MOVE_SCREEN_X(409), //199
    MOVE_SCREEN_DOWN(410), //19A
    SHOW_MAP_NAME(730), // VERSION 169 - guess
    RUNE_STONE_CLEAR_AND_ALL_REGISTER(1156),
    RUNE_STONE_DISAPPEAR(1157),
    RUNE_STONE_APPEAR(1158),
    KINESIS_PSYCHIC_ENERGY_SHIELD_EFFECT(674), // VERSION 170
    ZERO_TAG(668), //VERSION 170
    ZERO_TAG_STATE(670), //VERSION 170
    PET_CHAT(582), // VERSION 170
    PET_SIZE(586), // VERSION 170
    SHOW_FUSION_EFFECT(615), // VERSION 170
    // CMapLoadable::OnPacket
    SET_MAP_OBJECT_VISIBLE(303), //112
    // There are 6 news ones under CMapLoadable
    GM_EFFECT(450), // VERSION 170
    OX_QUIZ(451), // VERSION 170
    GMEVENT_INSTRUCTIONS(452), // VERSION 170
    TREASURE_BOX(399), // VERSION 170 
    NEW_YEAR_CARD(400), // VERSION 170 
    BLESSING_BOX(401), // VERSION 170 
    CREW_BOX(403), // VERSION 170. I don't know if the name is right. 
    RANDOM_Morph(404), // VERSION 170 
    CANCEL_NAME_CHANGE_2(405);//:v

    private int code;
    public static SendPacketOpcode eOp;

    public void setValue(int code) {
        this.code = code;
    }

    public int getValue() {
        eOp = this;
        return code;
    }

    private SendPacketOpcode(int code) {
        this.code = code;
    }
}
