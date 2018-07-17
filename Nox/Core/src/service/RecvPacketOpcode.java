package service;

public enum RecvPacketOpcode {

    /**
     * Login - Received Operation Codes
     */
    DummyCode(100),// Version 188
    BeginSocket(101),// Version 188
    SecurityPacket(102),// Version 188
    PermissionRequest(103),// Version 188
    LoginBasicInfo(104),// Version 188
    CheckLoginAuthInfo(105),// Version 188
    SelectWorld(106),// Version 188
    CheckSPWRequest(107),// Version 188
    SelectCharacter(108),// Version 188
    CheckSPWExistRequest(109),// Version 188
    MigrateIn(110),// Version 188
    WorldInfoLogoutRequest(114),// Version 188
    WorldInfoForShiningRequest(115),// Version 188
    CheckDuplicatedID(116),// Version 188
    LogoutWorld(117),// Version 188
    PermissionRequest_Fake(118),// Version 188
    CheckLoginAuthInfo_Fake(119),// Version 188
    CreateMapleAccount_Fake(120),// Version 188
    SelectAccount_Fake(121),// Version 188
    SelectWorld_Fake(122),// Version 188
    SelectCharacter_Fake(123),// Version 188
    CreateNewCharacter_Fake(124),// Version 188
    CreateNewCharacter(125),// Version 188
    CreateNewCharacterInCS(126),// Version 188
    CreateNewCharacter_PremiumAdventurer(127),// Version 188
    DeleteCharacter(128),// Version 188
    ReservedDeleteCharacterConfirm(129),// Version 188
    ReservedDeleteCharacterCancel(130),// Version 188
    RenameCharacter(131),// Version 188
    AliveAck_Fake(132),// Version 188
    ExceptionLog(133),// Version 188
    PrivateServerPacket(134),// Version 188
    ResetLoginStateOnCheckOTP(135),// Version 188
    AlbaRequest(142),// Version 188
    UpdateLoginCookie(143),// Version 188
    CheckCenterAndGameAreConnected(144),// Version 188
    ResponseToCheckAliveAck_Fake(145),// Version 188
    CreateMapleAccount(146),// Version 188
    AliveAck(151),// Version 188
    ResponseToCheckAliveAck(152),// Version 188
    ClientDumpLog(153),// Version 188
    CrcErrorLog(154),// Version 188
    PerformanceInfoProvidedConsent(155),// Version 188
    CheckHotfix(156),// Version 188
    UnknownSpam(158),// Version 188
    ChangeCharacterLocation(164),// Version 188
    UserLimitRequest(167),// Version 188
    WorldInfoRequest(171),// Version 188
    SetSPW(176),// Version 188
    ChangeSPWRequest(180),// Version 188
    NMCORequest(182),// Version 188
    MapLogin(183), // Version 188
    EndSocket(184),// Version 188

    /**
     * Game - Received Operation Codes
     */
    BeginUser(183),// Version 188
    UserTransferFieldRequest(184),// Version 188
    UserTransferChannelRequest(185),// Version 188
    UserTransferToHubRequest(186),// Version 188
    WorldTransferRequest(187),// Version 188
    WorldTransferShinningStarRequest(188),// Version 188
    UserMigrateToCashShopRequest(190),// Version 188
    UserMigrateToAuctionRequest(191),// Version 188
    PartyMigrateToPvpRequest(192),// Version 188
    UserMigrateToMonsterFarm(194),// Version 188
    UserMigrateToMonsterFarmByInviteItem(195),// Version 188
    UserRequestPvPStatus(196),// Version 188
    UserMigrateToPveRequest(197),// Version 188
    UserMove(198),// Version 188
    UserSitRequest(199),// Version 188
    UserPortableChairSitRequest(200),// Version 188
    UserEmoticonItemUseRequest(201),// Version 188
    UserDanceStopRequest(202),// Version 188
    UserMeleeAttack(203),// Version 188
    UserShootAttack(204),// Version 188
    UserMagicAttack(205),// Version 188
    UserBodyAttack(206),// Version 188
    UserAreaDotAttack(207),// Version 188
    UserMovingShootAttackPrepare(208),// Version 188
    UserHit(210),// Version 188
    UserChat(211),// Version 188
    UserADBoardClose(212),// Version 188
    UserEmotion(213),// Version 188
    AndroidEmotion(214),// Version 188
    UserActivateNickItem(215),// Version 188
    UserActivateDamageSkin(216),// Version 188
    UserActivateDamageSkin_Premium(217),// Version 188
    UserDamageSkinSaveRequest(218),// Version 188
    UserDefaultWingItem(219),// Version 188
    UserKaiserTransformWing(220),// Version 188
    UserKaiserTransformTail(221),// Version 188
    UserUpgradeTombEffect(222),// Version 188
    UserHP(223),// Version 188
    Premium(224),// Version 188
    UserBanMapByMob(225),// Version 188
    UserMonsterBookSetCover(226),// Version 188
    UserSelectNpc(227),// Version 188
    UserRemoteShopOpenRequest(228),// Version 188
    UserScriptMessageAnswer(229),// Version 188
    UserShopRequest(230),// Version 188
    UserTrunkRequest(231),// Version 188
    UserEntrustedShopRequest(232),// Version 188
    UserStoreBankRequest(233),// Version 188
    UserParcelRequest(234),// Version 188
    UserEffectLocal(235),// Version 188
    UserSpecialEffectLocal(236),// Version 188
    UserFinalAttackRequest(237),// Version 188
    UserCreateAreaDotRequest(238),// Version 188
    UserCreateHolidomRequest(239),// Version 188
    ReqMakingSkillEff(240),// Version 188
    ShopScannerRequest(241),// Version 188
    ShopLinkRequest(242),// Version 188
    AuctionRequest(243),// Version 188
    AdminShopRequest(244),// Version 188
    UserGatherItemRequest(245),// Version 188
    UserSortItemRequest(246),// Version 188
    UserChangeSlotPositionRequest(247),// Version 188
    UserTextEquipInfo(248),// Version 188
    UserPopOrPushBagItemToInven(249),// Version 188
    UserBagToBagItem(250),// Version 188
    UserPourInBagToBag(251),// Version 188
    UserStatChangeItemUseRequest(252),// Version 188
    UserStatChangeItemCancelRequest(253),// Version 188
    UserStatChangeByPortableChairRequest(254),// Version 188
    UserMobSummonItemUseRequest(255),// Version 188
    UserPetFoodItemUseRequest(256),// Version 188
    UserTamingMobFoodItemUseRequest(257),// Version 188
    UserScriptItemUseRequest(258),// Version 188
    UserRecipeOpenItemUseRequest(259),// Version 188
    UserConsumeCashItemUseRequest(260),// Version 188
    UserAdditionalSlotExtendItemUseRequest(261),// Version 188
    UserCashPetPickUpOnOffRequest(262),// Version 188
    UserCashPetSkillSettingRequest(263),// Version 188
    UserOptionChangeRequest(264),// Version 188
    UserDestroyPetItemRequest(265),// Version 188
    UserBridleItemUseRequest(266),// Version 188
    UserSkillLearnItemUseRequest(267),// Version 188
    UserSkillResetItemUseRequest(268),// Version 188
    UserAbilityResetItemUseRequest(269),// Version 188
    UserAbilityChangeItemUseRequest(270),// Version 188
    UserExpConsumeItemUseRequest(271),// Version 188
    UserMonsterLifeInviteItemUseRequest(272),// Version 188
    UserExpItemGetRequest(273),// Version 188
    UserCharSlotIncItemUseRequest(274),// Version 188
    UserCharRenameItemUseRequest(275),// Version 188
    UserKaiserColorChangeItemUseRequest(276),// Version 188
    UserShopScannerItemUseRequest(278),// Version 188
    UserMapTransferItemUseRequest(279),// Version 188
    UserPortalScrollUseRequest(280),// Version 188
    UserFieldTransferRequest(281),// Version 188
    UserUpgradeItemUseRequest(282),// Version 188
    UserUpgradeAssistItemUseRequest(283),// Version 188
    UserHyperUpgradeItemUseRequest(284),// Version 188
    UserExItemUpgradeItemUseRequest(285),// Version 188
    UserKarmaConsumeItemUseRequest(286),// Version 188
    UserEventUpgradeItemUseRequest(287),// Version 188
    UserItemOptionUpgradeItemUseRequest(288),// Version 188
    UserAdditionalOptUpgradeItemUseRequest(289),// Version 188
    UserItemSlotExtendItemUseRequest(290),// Version 188
    UserWeaponTempItemOptionRequest(291),// Version 188
    UserItemSkillSocketUpgradeItemUseRequest(292),// Version 188
    UserItemSkillOptionUpgradeItemUseRequest(293),// Version 188
    UserFreeMiracleCubeItemUseRequest(294),// Version 188
    UserEquipmentEnchantWithSingleUIRequest(295),// Version 188
    UserUIOpenItemUseRequest(289),// Version 188
    UserBagItemUseRequest(290),// Version 188
    UserItemReleaseRequest(299),// Version 188
    UserMemorialCubeOptionRequest(300),// Version 188
    UserAbilityUpRequest(307),// Version 188
    UserAbilityMassUpRequest(308),// Version 188
    UserDotHeal(309),// Version 188
    UserChangeStatRequest(310),// Version 188
    UserChangeStatRequestByItemOption(311),// Version 188
    SetSonOfLinkedSkillRequest(314),// Version 188
    UserSkillUpRequest(315),// Version 188
    UserSkillUseRequest(316),// Version 188
    UserSkillCancelRequest(317),// Version 188
    UserSkillPrepareCancelRequest(318),
    UserSkillPrepareRequest(319),// Version 188
    UserDropMoneyRequest(322),// Version 188
    UserGivePopularityRequest(323),// Version 188
    UserPartyRequest(324),// Version 188
    UserCharacterInfoRequest(325),// Version 188
    UserActivatePetRequest(326),// Version 188
    UserRegisterPetAutoBuffRequest(327),// Version 188
    UserTemporaryStatUpdateRequest(328),// Version 188
    UserPortalScriptRequest(329),// Version 188
    UserPortalTeleportRequest(331),// Version 188
    UserCallingTeleportRequest(332),// Version 188
    UserMapTransferRequest(333),// Version 188
    UserAntiMacroItemUseRequest(334),// Version 188
    UserAntiMacroSkillUseRequest(335),// Version 188
    UserAntiMacroRefreshRequest(336),// Version 188
    UserClaimRequest(338),// Version 188
    UserQuestRequest(339),// Version 188
    UserMedalReissueRequest(340),// Version 188
    UserCalcDamageStatSetRequest(341),// Version 188
    UserB2BodyRequest(342),// Version 188
    UserThrowGrenade(343),// Version 188
    UserDestroyGrenade(344),// Version 188
    UserCreateAuraByGrenade(345),// Version 188
    UserSetMoveGrenade(346),// Version 188
    UserMacroSysDataModified(347),// Version 188
    UserSelectNpcItemUseRequest(348),// Version 188
    UserLotteryItemUseRequest(349),// Version 188
    UserRouletteStartRequest(350),// Version 188
    UserRouletteResultRequest(351),// Version 188
    UserItemMakeRequest(352),// Version 188
    UserRepairDurability(353),// Version 188
    UserRepairDurabilityAll(353),// Version 188
    UserQuestRecordSetState(354),// Version 188
    UserClientTimerEndRequest(355),// Version 188
    UserClientResolutionResult(356),// Version 188
    UserFollowCharacterRequest(357),// Version 188
    UserFollowCharacterWithdraw(358),// Version 188
    UserSelectPQReward(359),// Version 188
    UserRequestPQReward(360),// Version 188
    SetPassenserResult(361),// Version 188
    UserRequestInstanceTable(362),// Version 188
    UserRequestCreateItemPot(363),// Version 188
    UserRequestRemoveItemPot(364),// Version 188
    UserRequestIncItemPotLifeSatiety(365),// Version 188
    UserRequestCureItemPotLifeSick(366),// Version 188
    UserRequestComplateToItemPot(367),// Version 188
    UserRequestRespawn(368),// Version 188
    UserConsumeHairItemUseRequest(369),// Version 188
    UserForceAtomCollision(370),// Version 188
    UserDebuffObjCollision(371),// Version 188
    UserUpdateLapidification(372),// Version 188

    // Unsure when this +5 starts...
    UserRequestCharacterPotentialSkillRandSet(377),// Version 188
    UserRequestCharacterPotentialSkillRandSetUI(378),// Version 188
    UserRequestOccumpationData(379),// Version 188
    UserRequestAswanTimeTableClientInit(380),// Version 188
    UserProtectBuffOnDieItemRequest(381),// Version 188
    UserProtectBuffOnDieMaplePointRequest(382),// Version 188
    UserProtectExpOnDieMaplePointRequest(383),// Version 188
    UserKeyDownAreaMoving(384),// Version 188
    UserCheckWeddingExRequest(385),// Version 188
    UserCatchDebuffCollision(386),// Version 188
    UserAffectedAreaCreated(387),// Version 188
    UserAffectedAreaRemoved(388),// Version 188
    UserDazzleHit(389),// Version 188
    UserMesoExchangeRequest(390),// Version 188
    ZeroTag(391),// Version 188
    ZeroShareCashEquipPart(392),// Version 188
    ZeroLastAssistState(393),// Version 188
    UserShootAttackInFPS(396),// Version 188
    UserLuckyItemUseRequest(389),// Version 188
    UserMobMoveAbilityChange(399),// Version 188
    UserDragonAction(400),// Version 188
    UserDragonBreathEarthEffect(401),// Version 188
    UserRenameRequest(402),// Version 188
    BroadcastMsg(403),// Version 188
    GroupMessage(404),// Version 188
    FieldUniverseMessage(405),// Version 188
    Whisper(406),// Version 188
    Messenger(407),// Version 188
    MiniRoom(408),// Version 188
    PartyRequest(410),// Version 188
    PartyResult(411),// Version 188
    PartyInvitableSet(412),// Version 188
    ExpeditionRequest(413),// Version 188
    PartyAdverRequest(414),// Version 188
    UrusPartyRequest(415),// Version 188
    GuildRequest(416),// Version 188
    GuildResult(417),// Version 188
    GuildJoinRequest(418),// Version 188
    GuildJoinCancelRequest(419),// Version 188
    GuildJoinReject(420),// Version 188
    GuildContentRankRequest(421),// Version 188
    TowerRankRequest(422),// Version 188
    Admin(423),// Version 188
    Log(424),// Version 188
    FriendRequest(426),// Version 188
    StarFriendRequest(427),// Version 188
    StarPlanetPointRequest(428),// Version 188
    LoadAccountIDOfCharacterFriendRequest(429),// Version 188
    MemoRequest(430),// Version 188
    MemoFlagRequest(431),// Version 188
    EnterTownPortalRequest(431),// Version 188
    EnterRandomPortalRequest(432),// Version 188
    EnterOpenGateRequest(433),// Version 188
    SlideRequest(434),// Version 188
    FuncKeyMappedModified(435),// Version 188
    RPSGame(436),// Version 188
    GSRPSGame(437),// Version 188
    StarPlanet_GSRPSGame(438),// Version 188
    GSRPSForceSelect(439),// Version 188
    MarriageRequest(440),// Version 188
    WeddingWishListRequest(441),// Version 188
    GuestBless(442),// Version 188
    BoobyTrapAlert(443),// Version 188
    StalkBegin(444),// Version 188
    AllianceRequest(445),// Version 188
    AllianceResult(446),// Version 188
    TalkToTutor(447),// Version 188
    TalkToPartner(448),// Version 188
    UserSwitchRP(449),// Version 188
    RequestIncCombo(450),// Version 188
    RequestDecCombo(451),// Version 188
    RequestSetBlessOfDarkness(455),// Version 188
    RequestSetHpBaseDamage(456),// Version 188
    MobCrcKeyChangedReply(457),// Version 188
    MobCrcDataResult(458),// Version 188
    MakingSkillRequest(459),// Version 188
    BroadcastEffectToSplit(460),// Version 188
    BroadcastOneTimeActionToSplit(461),// Version 188
    BroadcastAffectedEffectToSplit(462),// Version 188
    DebugOnlyCommand(463),// Version 188
    MicroBuffEndTime(464),// Version 188
    RequestSessionValue(465),// Version 188
    UserTransferFreeMarketRequest(466),// Version 188
    UserRequestSetStealSkillSlot(467),// Version 188
    UserRequestStealSkillMemory(468),// Version 188
    UserRequestStealSkillList(469),// Version 188
    UserRequestStealSkill(470),// Version 188
    RewardMobListRequest(471),// Version 188
    UserLvUpGuideNotice(472),// Version 188
    ResetCrossHunterAlert(473),// Version 188
    CrossHunterCompleteRequest(474),// Version 188
    CrossHunterShopRequest(475),// Version 188
    UserEquipSlotLevelMinusItemUseRequest(476),// Version 188
    BoardGameRequest(477),// Version 188

    UserRequestFlyingSwordStart(480),// Version 188
    BingoRequest(481),// Version 188
    BingoCassandraRequest(482),// Version 188
    ActionBarRequest(483),// Version 188
    UserRequestSetOffTrinity(484),// Version 188
    MesoRangerRequest(485),// Version 188
    UserRequestSetSmashCount(486),// Version 188

    UserHyperSkillUpRequest(487),// Version 188
    UserHyperSkillResetRequest(488),// Version 188
    UserHyperStatSkillUpRequest(489),// Version 188
    UserHyperStatSkillResetRequest(490),// Version 188

    // somewhere might be off by 1 before waitqueue
    UserSetDressChangedRequest(491),// Version 188
    EntryRecordRequest(492),// Version 188
    SetMaxGauge(493),// Version 188
    UserReturnEffectResponse(494),// Version 188
    GetServerTime(495),// Version 188
    GetCharacterPosition(496),// Version 188
    UserRequestChangeMobZoneState(497),// Version 188
    EvolvingRequest(498),// Version 188
    UserMixerRequest(499),// Version 188
    SummonEventReward(500),// Version 188
    MysticFieldMove(501),// Version 188
    YutGameRequest(502),// Version 188
    UserJewelCraftRequest(503),// Version 188
    //ValuePackRequest(504),// Version 188

    WaitQueueRequest(504),// Version 188
    RequestReloginCookie(505),// Version 188

    // might be off by 1
    CheckTrickOrTreatRequest(507),// Version 188
    MonsterFarmMigrateOutRequest(508),// Version 188
    HalloweenCandyRankingRequest(509),// Version 188
    GetRewardRequest(510),// Version 188 
    MapleStyleBonusRequest(511),// Version 188
    MapleStyleAdviceRequest(512),// Version 188
    MapleStyleSetScoreRequest(513),// Version 188
    Mentoring(514),// Version 188
    GetLotteryResult(515),// Version 188
    RootabyssEnterRequest(516),// Version 188
    UserSetItemAction(517),// Version 188
    UserSetBitsCase(518),// Version 188
    UserSetBitsSlot(519),// Version 188
    UserAntiMacroQuestionResult(520),// Version 188
    UserPinkbeanRolling(521),// Version 188
    UserPinkbeanYoYoStack(522),// Version 188
    UserQuickMoveScript(523),// Version 188
    TimeGateRequest(524),// Version 188
    UserSelectAndroid(525),// Version 188
    UserCompleteNpcSpeech(526),// Version 188
    UserCompleteAnotherUserCheck(527),// Version 188
    UserCompleteComboKillCountCheck(528),// Version 188
    UserCompleteMultiKillCountCheck(529),// Version 188
    UserCompleteMultiKillCheck(530),// Version 188
    UserDamageOnFallingCheck(531),// Version 188
    UserCompletePersonalShopBuyCheck(532),// Version 188
    UserDailyCommitmentCheck(533),// Version 188

    // todo fix
    UserMobDropMesoPickup(54),// Version 188
    UserBreakTimeFieldEnter(521),// Version 188
    UserRunActQuest(522),// Version 188
    JournalAvatarRequest(523),// Version 188
    RequestEventList(524),// Version 188
    UserSignRequest(525),// Version 188
    AddAttackReset(526),// Version 188
    SetEventNameTag(527),// Version 188
    UserAffectedAreaRemoveByTime(528),// Version 188
    RequestFreeChangeJob(548),// Version 188
    LibraryStartScript(521),// Version 188
    ChannelUserCountRequest(522),// Version 188
    UnUrusSelectedSkillList(523),// Version 188
    SoulDungeonSys(524),// Version 188

    UserSoulEffectRequest(551),
    UserSpinOffNewModifyRequest(552),
    BlackList(553),
    
    // unsure
    UserUpdateMatrix(570),
    EndOverHeat(571),
    TradeKingShopReq(572),
    TradeKingShopInfoReq(573),
    
    BeginPet(578),
    PetMove(579),
    PetAction(580),
    PetInteractionRequest(581),
    PetDropPickUpRequest(582),
    PetStatChangeItemUseRequest(583),
    PetUpdateExceptionListRequest(584),
    PetFoodItemUseRequest(585),
    PetOpenShop(586),
    EndPet(587),
    BeginSkillPet(588),
    SkillPetMove(589),
    SkillPetAction(590),
    SkillPetState(591),
    SkillPetDropPickUpRequest(592),
    SkillPetUpdateExceptionListRequest(593),
    EndSkillPet(594),
    BeginSummoned(595),
    SummonedMove(596),
    SummonedAttack(597),
    SummonedHit(598),
    SummonedSkill(599),
    Remove(600),
    SummonedAttackPvP(601),
    SummonedAction(602),
    SummonedAssistAttackDone(603),
    EndSummoned(605),
    BeginDragon(606),
    DragonMove(607),
    DragonGlide(608),
    EndDragon(609),
    BeginAndroid(610),
    AndroidMove(611),
    AndroidActionSet(612),
    EndAndroid(613),
    BeginFoxMan(614),
    FoxManMove(615),
    FoxManActionSetUseRequest(616),
    EndFoxMan(617),
    QuickslotKeyMappedModified(618),
    UpdateClientEnvironment(619),
    PassiveskillInfoUpdate(620),
    DirectionNodeCollision(621),
    UserLaserInfoForRemote(622),
    ReturnTeleportDebuff(623),
    MemoInGameRequest(624),
    EgoEquipGaugeCompleteReturn(626),
    EgoEquipCreateUpgradeItem(627),
    EgoEquipCreateUpgradeItemCostRequest(628),
    EgoEquipTalkRequest(629),
    EgoEquipCheckUpdateItemRequest(630),
    InheritanceInfoRequest(631),
    InheritanceUpgradeRequest(632),
    MirrorReadingSelectBookRequest(633),
    LikePoint(634),
    UserNonTargetForceAtomAttack(667),
    
    UserFlameOrbRequest(715),
    User_SADResultUI_Close(716),
    FreeLookChangeRequest(717),
    FreeLookChangeUIOpenFailed(718),
    UserSmartPhoneCallRequest(719),
    UserJaguarChangeRequest(720),
    FPSLog(721),
    PacketModifyLog(722),
    BattleUserAvatarSelect(723),
    BattleUserAttack(724),
    BattleUserAttackExpire(725),
    BattleUserAttackPosition(726),
    BattleUserHit(727),
    BattleUserHitByMob(728),
    BattleStatCoreRequest(729),
    BattleUserAlive(730),
    UserContentsBookRequest(731),
    PerformanceClientLogin(732),
    PerformanceClientInField(733),
    PingCheckRequest_ClientToGame(734),
    Ping_ClientToGame(735),
    PlantPotClick(736),
    RandomMissionRequest(737),
    ItemCollection_SetFlag(738),
    ItemCollection_CheckComplete(739),
    SelfStatChangeRequest(740),
    CashBuffEventCancle(741),
    CreatePsychicLock(743),
    ResetPathPsychicLock(744),
    ReleasePsychicLock(745),
    CreateKinesisPsychicArea(747),
    DoActivePsychicArea(748),
    DebuffPsychicArea(749),
    ReleasePsychicArea(750),
    PsychicOverRequest(751),
    DecPsychicPointRequest(752),
    TouchMeEndRequest(753),
    BiteAttackResponse(754),
    SaveUrusSkill(755),
    GetSavedUrusSkill(756),
    UrusShopRequest(757),
    UrusPartyMemberList(758),
    UserKeyDownStepRequest(759),
    DailyGiftRequest(760),
    SkillCommandLock(762),
    BeastFormWingOnOff(763),
    ResetAirHitCountRequest(764),
    RWActionCancel(765),
    ReleaseRWGrab(766),
    RWClearCurrentAttackRequest(767),
    RWMultiChargeCancelRequest(768),
    FuntionFootholdMan(769),
    
    MonsterBookCodeRequest(792),// Version 188
    MonsterBookCardDropRequest(794),// Version 188
    GachapierrotRequest(796),// Version 188
    SocketRequest(797),// Version 188
    SocketCreatorRequest(798),// Version 188
    SocketCreatorResult(799),// Version 188
    SocketFusionRequest(800),// Version 188
    UserRegisterFamiliarRequest(810),// Version 188
    FamiliarEnterField(811),// Version 188
    UserRegisterFamiliarNameRequest(812),// Version 188
    GuildBBS(819),// Version 188

    KaiserSkillShortcut(832), // lmao dawg I'm assuming this goes somewhere here, but gotta confirm :P

    SalonRequest(853),// Version 188
    SetDamageSkinRequest(855),// Version 188
    RemoveDamageSkinRequest(856),// Version 188

    // Unsure
    BeginFamiliar(866),
    FamiliarMove(867),// Version 188
    FamiliarActionRequest(868),// Version 188
    FamiliarAttackRequest(869),// Version 188
    FamiliarAutoBuffRequest(870),// Version 188
    EndFamiliar(871),// Version 188

    EndUser(892),// Version 188
    BeginField(893),// Version 188
    BeginLifePool(894),// Version 188
    BeginMob(895),// Version 188
    MobMove(896),// Version 188
    MobApplyCtrl(897),// Version 188
    MobDropPickUpRequest(898),// Version 188
    MobHitByObstacle(899),// Version 188
    MobHitByObstacleAtom(900),// Version 188
    MobHitByMob(901),// Version 188
    MobSelfDestruct(902),// Version 188
    MobSelfDestructCollisionGroup(903),// Version 188
    MobAttackMob(904),// Version 188
    MobSkillDelayEnd(905),// Version 188
    MobTimeBombEnd(906),// Version 188
    MobEscortCollision(907),// Version 188
    MobRequestEscortInfo(908),// Version 188
    MobEscortStopEndRequest(909),// Version 188
    MobAreaAttackDisease(910),// Version 188
    MobExplosionStart(911),// Version 188
    MobLiftingEnd(912),// Version 188
    MobUpdateFixedMoveDir(913),// Version 188
    MobCreateFireWalk(914),// Version 188
    MobAfterDeadRequest(915),// Version 188
    MobDamageShareInfo(916),// Version 188
    MobCreateAffectedArea(917),// Version 188
    MobDownResponse(918),// Version 188
    EndMob(920),// Version 188
    BeginNpc(921),// Version 188
    NpcMove(922),// Version 188
    NpcSpecialAction(923),// Version 188
    EndNpc(924),// Version 188
    EndLifePool(925),// Version 188
    BeginDropPool(926),// Version 188
    DropPickUpRequest(927),// Version 188
    EndDropPool(928),// Version 188
    BeginReactorPool(929),// Version 188
    ReactorHit(930),// Version 188
    ReactorClick(931),// Version 188
    ReactorRectInMob(932),// Version 188
    ReactorOnKey(933),// Version 188
    EndReactorPool(934),// Version 188
    
    BeginPartyMatch(669), // Version 188 (Was 961)
    InvitePartyMatch(962),
    CancelInvitePartyMatch(963),
    PartyMemberCandidateRequest(964),
    UrusPartyMemberCandidateRequest(965),
    PartyCandidateRequest(966),
    IntrusionFriendCandidateRequest(967),
    IntrusionLobbyCandidateRequest(968),
    EndPartyMatch(969),
    GatherRequest(970),
    GatherEndNotice(971),
    ActChangeReactorUseRequst(972),
    UserAntiMacroBombRequest(973),
    MakeEnterFieldPacketForQuickMove(974),
    RuneStoneUseReq(975),
    RuneStoneSkillReq(976),
    EndField(1088),// Version 187
    
    BeginCashShop(1146),// Version 188
    CS_UPDATE(1147),// Version 188
    BUY_CS_ITEM(1148),// Version 188
    CashShopCheckCouponRequest(1149),// Version 188
    RewardLogRequest(1151),// Version 188
    CASH_CATEGORY(1160),// Version 188
    CASH_UNKNOWN(1161),// Version 188
    EndCashShop(1163),// Version 188
    
    BeginItemUpgrade(1177),// Version 188
    GoldHammerRequest(1178),// Version 188
    GoldHammerComplete(1179),// Version 188
    EndItemUpgrade(1180),// Version 188
    BeginBattleRecord(1182),// Version 188
    BattleRecordOnOffRequest(1183),// Version 188
    BattleRecordSkillDamageLog(1184),// Version 188
    EndBattleRecord(1185),// Version 188
    CommerciUINotice(1246),// Version 188
    UserRewardRequest(1247) // Version 188
    ;

    // reserved for debugging purpose
    private int code;

    public final short getValue() {
        return (short) code;
    }

    private RecvPacketOpcode(int code) {
        this.code = code;
    }

}
