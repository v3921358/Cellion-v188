/**
 * Cellion Development
 */
package handling;

import handling.game.FreeMarketTransferRequest;
import handling.game.GroupMessageHandler;
import handling.game.BossMatchmakingHandler;
import client.ClientSocket;
import constants.ServerConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import service.LoginServer;
import service.RecvPacketOpcode;
import enums.ServerMode.MapleServerMode;
import net.InPacket;
import tools.LogHelper;
import tools.packet.CLogin;
import handling.cashshop.*;
import handling.common.*;
import handling.farm.*;
import handling.game.*;
import handling.login.*;
import io.netty.buffer.Unpooled;
import server.movement.types.*;
import net.ProcessPacket;

/**
 * Received Operations Handler
 *
 * @author Mazen Massoud
 * @author Novak
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private MapleServerMode mode;
    private static final ProcessPacket[] handlers;
    private int world = -1, channel = -1;
    public static boolean Log_Packets = false;
    private static int numDC = 0;
    private static long lastDC = System.currentTimeMillis();

    // Constants for Packet Throttling
    private static final String PACKET_THROTTLE_TIME = "p_tTime";
    private static final String PACKET_THROTTLE_RESETTIME = "p_tResetTime";
    private static final String PACKET_THROTTLE_COUNT = "p_tCount";

    static {

        handlers = new ProcessPacket[1500];

        /**
         * Client Handlers
         */
        handlers[RecvPacketOpcode.ClientDumpLog.getValue()] = new ClientErrorDumper();
        handlers[RecvPacketOpcode.CheckHotfix.getValue()] = new CheckHotFixHandler();
        handlers[RecvPacketOpcode.ExceptionLog.getValue()] = new CrashInfoDumper();
        handlers[RecvPacketOpcode.AliveAck.getValue()] = new KeepAliveHandler();
        handlers[RecvPacketOpcode.NMCORequest.getValue()] = new NMCORequestHandler();
        handlers[RecvPacketOpcode.PrivateServerPacket.getValue()] = new PrivateServerPacketHandler();
        handlers[RecvPacketOpcode.MigrateIn.getValue()] = new MigrateInHandler();
        handlers[RecvPacketOpcode.UserTransferFieldRequest.getValue()] = new UserTransferFieldRequest();
        handlers[RecvPacketOpcode.MapLogin.getValue()] = new MapLoginHandler();
        handlers[RecvPacketOpcode.PermissionRequest.getValue()] = new PermissionRequestHandler();
        handlers[RecvPacketOpcode.UnknownSpam.getValue()] = new ClientLoadingStateHandler();
        handlers[RecvPacketOpcode.CheckLoginAuthInfo.getValue()] = new LoginPasswordHandler();
        handlers[RecvPacketOpcode.WorldInfoLogoutRequest.getValue()] = new WorldInfoRequestHandler();
        handlers[RecvPacketOpcode.WorldInfoRequest.getValue()] = new WorldInfoRequestHandler();
        handlers[RecvPacketOpcode.SelectWorld.getValue()] = new CharListRequestHandler();
        handlers[RecvPacketOpcode.UserLimitRequest.getValue()] = new UserLimitRequestHandler();
        handlers[RecvPacketOpcode.CheckDuplicatedID.getValue()] = new CheckDuplicatedIDHandler();
        handlers[RecvPacketOpcode.CreateNewCharacter.getValue()] = new CharacterCreator();
        handlers[RecvPacketOpcode.CreateNewCharacter_PremiumAdventurer.getValue()] = new UltimateCharacterCreator();
        handlers[RecvPacketOpcode.DeleteCharacter.getValue()] = new CharacterDeletor();
        handlers[RecvPacketOpcode.ChangeSPWRequest.getValue()] = new PicChangeHandler();
        handlers[RecvPacketOpcode.SetSPW.getValue()] = new CharSelectSetPICHandler();
        handlers[RecvPacketOpcode.CheckSPWRequest.getValue()] = new PicCheck();
        //handlers[RecvPacketOpcode.UpdateCharacterCard.getValue()] = new UpdateCharacterCards();
        //handlers[RecvPacketOpcode.CharacterBurning.getValue()] = new CharacterBurnRequestHandler(); // IDK
        handlers[RecvPacketOpcode.UserPortalTeleportRequest.getValue()] = new OnUserPortalTeleportRequest();
        handlers[RecvPacketOpcode.UserMapTransferRequest.getValue()] = new OnUserMapTransferRequest();
        handlers[RecvPacketOpcode.UserGivePopularityRequest.getValue()] = new OnUserGivePopularityRequest();
        handlers[RecvPacketOpcode.UserADBoardClose.getValue()] = new OnUserADBoardClose();
        handlers[RecvPacketOpcode.UserMedalReissueRequest.getValue()] = new OnUserMedalReissueRequest();
        handlers[RecvPacketOpcode.UserParcelRequest.getValue()] = new OnUserParcelRequest();
        handlers[RecvPacketOpcode.UserEntrustedShopRequest.getValue()] = new OnUserEntrustedShopRequest();
        handlers[RecvPacketOpcode.UserStoreBankRequest.getValue()] = new OnUserStoreBankRequest();
        //handlers[RecvPacketOpcode.SnowBallHit.getValue()] = new OnSnowBallHit(); // IDK
        //handlers[RecvPacketOpcode.CoconutHit.getValue()] = new OnCoconutHit(); // IDK
        handlers[RecvPacketOpcode.ZeroTag.getValue()] = new OnZeroTag();
        handlers[RecvPacketOpcode.PartyAdverRequest.getValue()] = new OnPartyAdverRequest();
        handlers[RecvPacketOpcode.ExpeditionRequest.getValue()] = new OnExpeditionRequest();
        handlers[RecvPacketOpcode.UserTransferChannelRequest.getValue()] = new ChannelChangeHandler();
        handlers[RecvPacketOpcode.UserMigrateToMonsterFarm.getValue()] = new FarmEntryHandler();
        handlers[RecvPacketOpcode.UserCharacterInfoRequest.getValue()] = new CharacterInfoRequestHandler();
        handlers[RecvPacketOpcode.UserMigrateToCashShopRequest.getValue()] = new EnterCashShopHandler();
        handlers[RecvPacketOpcode.RequestReloginCookie.getValue()] = new ReloginCookieHandler();
        handlers[RecvPacketOpcode.UpdateLoginCookie.getValue()] = new UpdateLoginCookieHandler();
        /**
         * PvP Handlers
         */
        handlers[RecvPacketOpcode.UserMigrateToPvpRequest.getValue()] = new EnterPvpHandler();
        handlers[RecvPacketOpcode.UserMigrateToPveRequest.getValue()] = new LeavePvpHandler();
        //handlers[RecvPacketOpcode.UserAttackUser.getValue()] = new AttackPvpHandler(); // IDK
        handlers[RecvPacketOpcode.SummonedAttackPvP.getValue()] = new SummonPvpHandler();

        /**
         * Azwan Handlers
         */
        //handlers[RecvPacketOpcode.UserTransferAswanRequest.getValue()] = new EnterAzwanHandler();
        //handlers[RecvPacketOpcode.UserTransferAswanReadyRequest.getValue()] = new EnterAzwanEventHandler();
        //handlers[RecvPacketOpcode.AswanRetireRequest.getValue()] = new LeaveAzwanHandler();
        /**
         * Movement Handlers
         */
        handlers[RecvPacketOpcode.UserMove.getValue()] = new PlayerMovement();
        handlers[RecvPacketOpcode.FoxManMove.getValue()] = new HakuMovement();
        handlers[RecvPacketOpcode.FoxManActionSetUseRequest.getValue()] = new HakuMovement();
        handlers[RecvPacketOpcode.AndroidMove.getValue()] = new AndroidMovement();
        handlers[RecvPacketOpcode.PassiveskillInfoUpdate.getValue()] = new FamiliarMovement();
        handlers[RecvPacketOpcode.MobMove.getValue()] = new MobMovement();
        handlers[RecvPacketOpcode.DragonMove.getValue()] = new DragonMovement();
        handlers[RecvPacketOpcode.SummonedMove.getValue()] = new SummonMovement();
        handlers[RecvPacketOpcode.PetMove.getValue()] = new PetMovement();

        /**
         * Social Chat Handler
         */
        handlers[RecvPacketOpcode.UserChat.getValue()] = new GeneralChatHandler();
        handlers[RecvPacketOpcode.Messenger.getValue()] = new MessengerHandler();
        handlers[RecvPacketOpcode.Whisper.getValue()] = new WhisperHandler();
        handlers[RecvPacketOpcode.GroupMessage.getValue()] = new GroupMessageHandler();

        /**
         * Combat Handlers
         */
        handlers[RecvPacketOpcode.UserMeleeAttack.getValue()] = new NormalCloseRangeAttack();
        handlers[RecvPacketOpcode.UserShootAttack.getValue()] = new RangedAttack();
        handlers[RecvPacketOpcode.UserMagicAttack.getValue()] = new MagicAttack();
        handlers[RecvPacketOpcode.UserNonTargetForceAtomAttack.getValue()] = new NonTargetAtomAttackHandler();
        handlers[RecvPacketOpcode.UserSkillUseRequest.getValue()] = new SpecialAttackMove();
        handlers[RecvPacketOpcode.UserBodyAttack.getValue()] = new PassiveEnergyCloseRangeAttack();
        handlers[RecvPacketOpcode.UserFinalAttackRequest.getValue()] = new FinalAttackHandler();

        /**
         * MonsterBook Handlers
         */
        handlers[RecvPacketOpcode.MonsterBookCodeRequest.getValue()] = new MonsterBookInfoRequest();
        handlers[RecvPacketOpcode.MonsterBookCardDropRequest.getValue()] = new MonsterBookDropsRequest();

        /**
         * Crafting Handlers
         */
        handlers[RecvPacketOpcode.UserOptionChangeRequest.getValue()] = new ProfessionInfo();

        /**
         * Player Handlers
         */
        handlers[RecvPacketOpcode.UserEmotion.getValue()] = new PlayerEmotionHandler();
        handlers[RecvPacketOpcode.UserHit.getValue()] = new PlayerDamageHandler();
        handlers[RecvPacketOpcode.UserChangeStatRequest.getValue()] = new PlayerHealAction();
        handlers[RecvPacketOpcode.UserSkillCancelRequest.getValue()] = new BuffCancel();
        handlers[RecvPacketOpcode.UserPortableChairSitRequest.getValue()] = new OnUserPortableChairSitRequest();
        handlers[RecvPacketOpcode.UserSitRequest.getValue()] = new OnUserSitRequest();
        handlers[RecvPacketOpcode.UserDropMoneyRequest.getValue()] = new MesoDrop();
        handlers[RecvPacketOpcode.UserPortalScriptRequest.getValue()] = new OnUserPortalScriptRequest();
        handlers[RecvPacketOpcode.DropPickUpRequest.getValue()] = new ItemPickupHandler();
        handlers[RecvPacketOpcode.UserEffectLocal.getValue()] = new UserEffectLocalHandler();
        handlers[RecvPacketOpcode.RuneStoneSkillReq.getValue()] = new RuneStoneCompleteArrowHandler();
        handlers[RecvPacketOpcode.RuneStoneUseReq.getValue()] = new RuneStoneStartHandler();
        handlers[RecvPacketOpcode.MakeEnterFieldPacketForQuickMove.getValue()] = new NPCQuickMoveHandler();
        handlers[RecvPacketOpcode.UserSetDressChangedRequest.getValue()] = new SetDressChangedRequest();
        handlers[RecvPacketOpcode.UserSkillLearnItemUseRequest.getValue()] = new UseSkillBookHandler();
        handlers[RecvPacketOpcode.QuickslotKeyMappedModified.getValue()] = new QuickSlot();
        handlers[RecvPacketOpcode.UserTransferFreeMarketRequest.getValue()] = new FreeMarketTransferRequest();

        /**
         * Android Handlers
         */
        handlers[RecvPacketOpcode.AndroidEmotion.getValue()] = new AndroidEmotion();

        /**
         * Pet Handlers
         */
        handlers[RecvPacketOpcode.UserPetFoodItemUseRequest.getValue()] = new PetFoodHandler();
        handlers[RecvPacketOpcode.PetStatChangeItemUseRequest.getValue()] = new PetAutoPotionHandler();
        handlers[RecvPacketOpcode.PetInteractionRequest.getValue()] = new PetCommandHandler();
        handlers[RecvPacketOpcode.UserActivatePetRequest.getValue()] = new SpawnPetHandler();
        handlers[RecvPacketOpcode.PetDropPickUpRequest.getValue()] = new PetPickupHandler();
        handlers[RecvPacketOpcode.PetAction.getValue()] = new PetChatHandler();

        /**
         * Item Handlers
         */
        handlers[RecvPacketOpcode.UserStatChangeItemCancelRequest.getValue()] = new CancelItemEffectHandler();
        handlers[RecvPacketOpcode.UserStatChangeItemUseRequest.getValue()] = new UseItemEffectHandler();
        handlers[RecvPacketOpcode.UserActivateNickItem.getValue()] = new TitleEquipHandler();
        handlers[RecvPacketOpcode.UserUIOpenItemUseRequest.getValue()] = new OpenItemUI();

        /**
         * NPC Handlers
         */
        handlers[RecvPacketOpcode.UserScriptMessageAnswer.getValue()] = new NPCMoreTalkHandler();
        handlers[RecvPacketOpcode.NpcMove.getValue()] = new NpcMovement();
        handlers[RecvPacketOpcode.UserSelectNpc.getValue()] = new NPCTalkHandler();
        handlers[RecvPacketOpcode.UserShopRequest.getValue()] = new NPCShopHandler();
        handlers[RecvPacketOpcode.UserQuestRequest.getValue()] = new QuestActionHandler();
        handlers[RecvPacketOpcode.UserTrunkRequest.getValue()] = new StorageHandler();

        /**
         * Player Stat Handler
         */
        handlers[RecvPacketOpcode.UserHyperStatSkillUpRequest.getValue()] = new DistributeHyperStatHandler();
        handlers[RecvPacketOpcode.UserHyperStatSkillResetRequest.getValue()] = new ResetHyperStatHandler();
        handlers[RecvPacketOpcode.UserRequestInstanceTable.getValue()] = new UpdateHyperStatHandler();
        handlers[RecvPacketOpcode.UserAbilityUpRequest.getValue()] = new DistributeAPHandler();
        handlers[RecvPacketOpcode.UserAbilityMassUpRequest.getValue()] = new AutoDistributeAPHandler();
        handlers[RecvPacketOpcode.UserSkillUpRequest.getValue()] = new DistributeSPHandler();
        handlers[RecvPacketOpcode.UserHyperSkillUpRequest.getValue()] = new DistributeHyperHandler();
        handlers[RecvPacketOpcode.UserHyperSkillResetRequest.getValue()] = new ResetHyperHandler();

        /**
         * Player Operation Handlers
         */
        handlers[RecvPacketOpcode.GuildRequest.getValue()] = new GuildOperationHandler();
        handlers[RecvPacketOpcode.GuildBBS.getValue()] = new GuildBBSOperation();
        handlers[RecvPacketOpcode.GuildJoinRequest.getValue()] = new GuildJoinRequestHandler();
        handlers[RecvPacketOpcode.GuildJoinCancelRequest.getValue()] = new CancelGuildJoinRequestHandler();
        handlers[RecvPacketOpcode.PartyRequest.getValue()] = new PartyOperationHandler();
        handlers[RecvPacketOpcode.PartyResult.getValue()] = new DenyPartyRequestHandler();
        handlers[RecvPacketOpcode.PartyInvitableSet.getValue()] = new AllowPartyInviteHandler();
        handlers[RecvPacketOpcode.MiniRoom.getValue()] = new PlayerInteractionHandler();
        handlers[RecvPacketOpcode.UserRewardRequest.getValue()] = new OnUserRewardClaimRequest();
        handlers[RecvPacketOpcode.BeginPartyMatch.getValue()] = new BossMatchmakingHandler(); // IDK

        // handlers[RecvPacketOpcode.SaveDamageSkinRequest.getValue()] = new SaveDamageSkinRequest(); // TODO
        // handlers[672] = new BossMatchmakingHandler(); // TODO
        /**
         * Buddy List Handlers
         */
        handlers[RecvPacketOpcode.FriendRequest.getValue()] = new BuddylistModifyHandler();
        handlers[RecvPacketOpcode.LoadAccountIDOfCharacterFriendRequest.getValue()] = new FriendRequestAccIdHandler();

        /**
         * Reactor Handlers
         */
        handlers[RecvPacketOpcode.ReactorClick.getValue()] = new ClickTouchReactorHandler();
        handlers[RecvPacketOpcode.ReactorRectInMob.getValue()] = new ClickTouchReactorHandler();
        handlers[RecvPacketOpcode.ReactorHit.getValue()] = new HitReactorHandler();

        /**
         * Summon Handlers
         */
        handlers[RecvPacketOpcode.SummonedHit.getValue()] = new SummonsDamageHandler();
        handlers[RecvPacketOpcode.SummonedAttack.getValue()] = new SummonAttackHandler();
        handlers[RecvPacketOpcode.Remove.getValue()] = new RemoveSummonHandler();
        handlers[RecvPacketOpcode.SummonedSkill.getValue()] = new SubSummonHandler();
        handlers[RecvPacketOpcode.MobApplyCtrl.getValue()] = new AutoAggroHandler();

        /**
         * Inventory Operations Handlers
         */
        handlers[RecvPacketOpcode.UserSortItemRequest.getValue()] = new ItemSortHandler();
        handlers[RecvPacketOpcode.UserGatherItemRequest.getValue()] = new ItemGatherHandler();
        handlers[RecvPacketOpcode.UserChangeSlotPositionRequest.getValue()] = new ItemMoveHandler();
        handlers[RecvPacketOpcode.UserPopOrPushBagItemToInven.getValue()] = new MoveBagHandler();
        handlers[RecvPacketOpcode.UserBagToBagItem.getValue()] = new SwitchBagHandler();
        handlers[RecvPacketOpcode.UserConsumeCashItemUseRequest.getValue()] = new UseCashItemHandler();
        handlers[RecvPacketOpcode.UserStatChangeItemUseRequest.getValue()] = new UseItemHandler();
        handlers[RecvPacketOpcode.UserItemReleaseRequest.getValue()] = new UseMagnifyingGlassHandler();
        handlers[RecvPacketOpcode.UserScriptItemUseRequest.getValue()] = new UseScriptedNPCItemHandler();
        handlers[RecvPacketOpcode.UserPortalScrollUseRequest.getValue()] = new UseReturnScrollHandler();
        handlers[RecvPacketOpcode.UserEquipmentEnchantWithSingleUIRequest.getValue()] = new EnchantmentHandler();
        handlers[RecvPacketOpcode.UserUpgradeAssistItemUseRequest.getValue()] = new UseScrollsHandler();
        handlers[RecvPacketOpcode.UserItemOptionUpgradeItemUseRequest.getValue()] = new UsePotentialScrollHandler();
        handlers[RecvPacketOpcode.UserAdditionalOptUpgradeItemUseRequest.getValue()] = new UseBonusPotentialScrollHandler();
        handlers[RecvPacketOpcode.UserUpgradeItemUseRequest.getValue()] = new UseScrollsHandler();
        handlers[RecvPacketOpcode.UserTamingMobFoodItemUseRequest.getValue()] = new UseMountFoodHandler();
        handlers[RecvPacketOpcode.UserMobSummonItemUseRequest.getValue()] = new UseSummonBagHandler();
        handlers[RecvPacketOpcode.UserFreeMiracleCubeItemUseRequest.getValue()] = new UseCraftedCubeHandler();
        handlers[RecvPacketOpcode.UserMemorialCubeOptionRequest.getValue()] = new UseSelectBlackCubeOptionHandler();
        handlers[RecvPacketOpcode.UserMapTransferItemUseRequest.getValue()] = new UseTeleRockHandler();
        //handlers[RecvPacketOpcode.PAM_SONG.getValue()] = new UsePamSongHandler(); // IDK

        /**
         * Skill Handlers
         */
        handlers[RecvPacketOpcode.UserSkillPrepareRequest.getValue()] = new SkillEffectHandler();
        handlers[RecvPacketOpcode.UserMacroSysDataModified.getValue()] = new SkillMacroHandler();
        handlers[RecvPacketOpcode.FuncKeyMappedModified.getValue()] = new KeyMap();
        handlers[RecvPacketOpcode.SetSonOfLinkedSkillRequest.getValue()] = new LinkedSkillRequest();

        /**
         * Aran Combo Handler
         */
        handlers[RecvPacketOpcode.RequestIncCombo.getValue()] = new AranComboHandler();

        /**
         * Blaze Wizard Handlers
         */
        handlers[RecvPacketOpcode.UserFlameOrbRequest.getValue()] = new OrbitalFlameHandler();
        handlers[RecvPacketOpcode.UserForceAtomCollision.getValue()] = new AtomCollisionHandler();

        /**
         * Kaiser Handlers
         */
        handlers[RecvPacketOpcode.KaiserSkillShortcut.getValue()] = new KaiserSkillShortcut();
        handlers[RecvPacketOpcode.UserRequestFlyingSwordStart.getValue()] = new ReleaseTempestBlades();

        /**
         * Phantom Handlers
         */
        handlers[RecvPacketOpcode.UserRequestSetStealSkillSlot.getValue()] = new SetStealSkillSlotHandler();
        handlers[RecvPacketOpcode.UserRequestStealSkillMemory.getValue()] = new StealSkillMemoryHandler();
        handlers[RecvPacketOpcode.UserRequestStealSkillList.getValue()] = new StealSkillListHandler();

        /**
         * Kinesis Handlers
         */
        handlers[RecvPacketOpcode.CreateKinesisPsychicArea.getValue()] = new CreateKinesisPsychicAreaHandler();
        handlers[RecvPacketOpcode.DoActivePsychicArea.getValue()] = new DoActivePsychicAreaHandler();
        handlers[RecvPacketOpcode.ReleasePsychicArea.getValue()] = new ReleasePsychicAreaHandler();
        handlers[RecvPacketOpcode.DebuffPsychicArea.getValue()] = new DebuffPsychicAreaHandler();
        handlers[RecvPacketOpcode.CreatePsychicLock.getValue()] = new CreatePsychicLockHandler();
        handlers[RecvPacketOpcode.ResetPathPsychicLock.getValue()] = new ResetPathPsychicLockHandler();
        handlers[RecvPacketOpcode.ReleasePsychicLock.getValue()] = new ReleasePsychicLockHandler();
        handlers[RecvPacketOpcode.DecPsychicPointRequest.getValue()] = new KinesisPsychicHandler(); // TODO

        /**
         * Client Environment Handlers
         */
        handlers[RecvPacketOpcode.UpdateClientEnvironment.getValue()] = new ClientEnvironmentHandler();

        /**
         * V Matrix Handlers
         */
        handlers[RecvPacketOpcode.UserUpdateMatrix.getValue()] = new UpdateMatrixHandler();

        /**
         * Cash Shop Handlers
         */
        //handlers[RecvPacketOpcode.BUY_CS_ITEM.getValue()] = new CashShopPurchase();
        //handlers[RecvPacketOpcode.CashShopCheckCouponRequest.getValue()] = new CashCouponHandler();
        //handlers[RecvPacketOpcode.CASH_CATEGORY.getValue()] = new CashCategorySwitch();
        //handlers[RecvPacketOpcode.CS_UPDATE.getValue()] = new UpdatePlayerCashInfo();
    }

    public ServerHandler(MapleServerMode pMode) {
        this(-1, -1, pMode);
    }

    public ServerHandler(int nWorld, int nChannel, MapleServerMode pMode) {
        this.world = nWorld;
        this.channel = nChannel;
        this.mode = pMode;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext pContext, Throwable pThrowable) {
        Channel ch = pContext.channel();
        try {
            ClientSocket c = (ClientSocket) ch.attr(ClientSocket.SESSION_KEY).get();
            c.disconnect(true, false);
            c.cancelPingTask();
        } catch (Exception pException) {
            LogHelper.GENERAL_EXCEPTION.get().error("Unable to save disconnected session.");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext pContext) {
        Channel pChannel = pContext.channel();

        int SendSeq = (int) (Math.random() * Integer.MAX_VALUE);
        int RecvSeq = (int) (Math.random() * Integer.MAX_VALUE);

        ClientSocket pClientSocket = new ClientSocket(pChannel, SendSeq, RecvSeq);

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.println("[Session] Opened Session (" + pClientSocket.getSessionIPAddress() + ")");
        }

        final String sAddress = pClientSocket.GetIP(); // Start Checking IP
        LoginServer.removeIPAuth(sAddress);

        pClientSocket.setWorld(world);
        pClientSocket.setChannel(channel);
        pClientSocket.setReceiving(true);

        if (pClientSocket.GetPort() == LoginServer.PORT) {
            pChannel.writeAndFlush(Unpooled.wrappedBuffer(CLogin.HandshakeLogin(SendSeq, RecvSeq).GetData()));
        } else {
            pChannel.writeAndFlush(Unpooled.wrappedBuffer(CLogin.HandshakeGame(SendSeq, RecvSeq).GetData()));
        }
        pChannel.attr(ClientSocket.SESSION_KEY).set(pClientSocket);

        pClientSocket.startPing(pChannel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext pContext) {
        Channel pChannel = pContext.channel();

        try {
            ClientSocket c = (ClientSocket) pChannel.attr(ClientSocket.SESSION_KEY).get();
            c.cancelPingTask();
            c.disconnect(true, false);

            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                System.out.println("[Session] Closed Session (" + c.GetIP() + ")");
            }
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().error("Unable to save inactive session.");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext pContext, Object pMsg) {
        InPacket iPacket = (InPacket) pMsg;
        Channel pChannel = pContext.channel();

        ClientSocket pClientSocket = (ClientSocket) pChannel.attr(ClientSocket.SESSION_KEY).get();

        if (!pClientSocket.isReceiving() || pMsg == null) {
            return;
        }

        int nPacketID = iPacket.DecodeShort();
        ProcessPacket pHandler = handlers[nPacketID];

        String head = "Unknown";
        for (RecvPacketOpcode packet : RecvPacketOpcode.values()) {
            if (packet.getValue() == (int) nPacketID) {
                head = packet.name();
            }
        }

        try {
            if (pHandler != null) {
                if (pHandler.ValidateState(pClientSocket)) {
                    switch (head) {
                        case "PrivateServerPacket":
                        case "AliveAck":
                        case "UserQuestRequest":
                        case "UserMove":
                        case "SummonedMove":
                        case "NpcMove":
                        //case "MobMove":
                        case "MobApplyCtrl":
                        case "UserChangeStatRequest":
                        case "UserActivateDamageSkin":
                        case "UpdateClientEnvironment":
                            if (ServerConstants.REDUCED_DEBUG_SPAM) {
                                break; // Doesn't display these packets, prevents major console spam.
                            }
                        default:
                            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                                System.out.printf("[Recv Operation] %s (%d) : %s%n", head, nPacketID, iPacket.toString());
                            }
                            break;
                    }
                    pHandler.Process(pClientSocket, iPacket);
                } else {
                    LogHelper.PACKET_HANDLER.get().error("Unvalid packet handling state for " + (pClientSocket.getPlayer() == null ? "null" : pClientSocket.getPlayer()) + " (" + pClientSocket.getAccountName() + ") on map " + (pClientSocket.getPlayer() == null ? "null" : pClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString());
                }
            } else {
                if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                    switch (head) {
                        case "UserActivateDamageSkin":
                            if (ServerConstants.REDUCED_DEBUG_SPAM) {
                                break; // Doesn't display these packets, prevents major console spam.
                            }
                        default:
                            System.out.println("[Unhandled Operation] " + head + " (" + nPacketID + ") " + iPacket.toString() + " : The respected receive operation is currently unhandled.");
                            break;
                    }
                }
            }
        } catch (Throwable pThrowable) {
            LogHelper.PACKET_HANDLER.get().error("Error for " + (pClientSocket.getPlayer() == null ? "null" : pClientSocket.getPlayer()) + " (" + pClientSocket.getAccountName() + ") on map " + (pClientSocket.getPlayer() == null ? "null" : pClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString(), pThrowable);
        }
    }
}
