package handling;

import handling.game.GroupMessageHandler;
import handling.game.BossMatchmakingHandler;
import client.Client;
import constants.ServerConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import service.LoginServer;

import service.RecvPacketOpcode;
import service.ServerMode.MapleServerMode;
import net.InPacket;
import tools.LogHelper;
import tools.packet.CLogin;
import handling.cashshop.*;
import handling.common.*;
import handling.farm.*;
import handling.game.*;
import handling.login.*;
import server.movement.types.*;
import net.ProcessPacket;

public class MapleServerHandler extends ChannelInboundHandlerAdapter {

    private int world = -1, channel = -1;
    public static boolean Log_Packets = false;
    private static int numDC = 0;
    private static long lastDC = System.currentTimeMillis();

    private MapleServerMode mode;

    // Constants for packet throttling
    private static final String PACKET_THROTTLE_TIME = "p_tTime";
    private static final String PACKET_THROTTLE_RESETTIME = "p_tResetTime";
    private static final String PACKET_THROTTLE_COUNT = "p_tCount";

    private static final ProcessPacket[] handlers;

    static {
        handlers = new ProcessPacket[1500];
        handlers[RecvPacketOpcode.ClientDumpLog.getValue()] = new ClientErrorDumper();
        handlers[RecvPacketOpcode.CheckHotfix.getValue()] = new CheckHotFixHandler();
        handlers[RecvPacketOpcode.ExceptionLog.getValue()] = new CrashInfoDumper();
        handlers[RecvPacketOpcode.AliveAck.getValue()] = new KeepAliveHandler();
        handlers[RecvPacketOpcode.NMCORequest.getValue()] = new NMCORequestHandler();
        handlers[RecvPacketOpcode.PrivateServerPacket.getValue()] = new PrivateServerPacketHandler();
        handlers[RecvPacketOpcode.MigrateIn.getValue()] = new MigrateInHandler();
        handlers[RecvPacketOpcode.UserTransferFieldRequest.getValue()] = new UserTransferFieldRequest();
        //handlers[RecvPacketOpcode.VIEW_SELECT_PIC.getValue()] = new CharSelectHandler();
        //handlers[RecvPacketOpcode.CHARLIST_REORGANIZE.getValue()] = new CharListReorganizer();
        //handlers[RecvPacketOpcode.VIEW_REGISTER_PIC.getValue()] = new RegisterPicOnViewHandler();

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
        handlers[RecvPacketOpcode.AlbaRequest.getValue()] = new PartTimeJobHandler();
        handlers[RecvPacketOpcode.CheckSPWRequest.getValue()] = new PicCheck();
        handlers[RecvPacketOpcode.UpdateCharacterCard.getValue()] = new UpdateCharacterCards();
        handlers[RecvPacketOpcode.CharacterBurning.getValue()] = new CharacterBurnRequestHandler();

        handlers[RecvPacketOpcode.UserPortalTeleportRequest.getValue()] = new OnUserPortalTeleportRequest();
        handlers[RecvPacketOpcode.UserMapTransferRequest.getValue()] = new OnUserMapTransferRequest();
        handlers[RecvPacketOpcode.UserGivePopularityRequest.getValue()] = new OnUserGivePopularityRequest();
        handlers[RecvPacketOpcode.UserADBoardClose.getValue()] = new OnUserADBoardClose();
        handlers[RecvPacketOpcode.UserMedalReissueRequest.getValue()] = new OnUserMedalReissueRequest();
        handlers[RecvPacketOpcode.UserParcelRequest.getValue()] = new OnUserParcelRequest();
        handlers[RecvPacketOpcode.UserEntrustedShopRequest.getValue()] = new OnUserEntrustedShopRequest();
        handlers[RecvPacketOpcode.UserStoreBankRequest.getValue()] = new OnUserStoreBankRequest();
        handlers[RecvPacketOpcode.SnowBallHit.getValue()] = new OnSnowBallHit();
        handlers[RecvPacketOpcode.CoconutHit.getValue()] = new OnCoconutHit();
        handlers[RecvPacketOpcode.ZeroTag.getValue()] = new OnZeroTag();
        handlers[RecvPacketOpcode.PartyAdverRequest.getValue()] = new OnPartyAdverRequest();
        handlers[RecvPacketOpcode.ExpeditionRequest.getValue()] = new OnExpeditionRequest();

        handlers[RecvPacketOpcode.UserTransferChannelRequest.getValue()] = new ChannelChangeHandler();
        handlers[RecvPacketOpcode.UserMigrateToMonsterFarm.getValue()] = new FarmEntryHandler();
        handlers[RecvPacketOpcode.UserCharacterInfoRequest.getValue()] = new CharacterInfoRequestHandler();
        handlers[RecvPacketOpcode.UserMigrateToCashShopRequest.getValue()] = new EnterCashShopHandler();
        //Pvp Handlers
        handlers[RecvPacketOpcode.UserMigrateToPvpRequest.getValue()] = new EnterPvpHandler();
        handlers[RecvPacketOpcode.UserMigrateToPveRequest.getValue()] = new LeavePvpHandler();
        handlers[RecvPacketOpcode.UserAttackUser.getValue()] = new AttackPvpHandler();
        handlers[RecvPacketOpcode.SummonedAttackPvP.getValue()] = new SummonPvpHandler();
        //Azwan Handlers
        handlers[RecvPacketOpcode.UserTransferAswanRequest.getValue()] = new EnterAzwanHandler();
        handlers[RecvPacketOpcode.UserTransferAswanReadyRequest.getValue()] = new EnterAzwanEventHandler();
        handlers[RecvPacketOpcode.AswanRetireRequest.getValue()] = new LeaveAzwanHandler();
        //Movement handlers
        handlers[RecvPacketOpcode.UserMove.getValue()] = new PlayerMovement();
        handlers[RecvPacketOpcode.FoxManMove.getValue()] = new HakuMovement();
        handlers[RecvPacketOpcode.FoxManActionSetUseRequest.getValue()] = new HakuMovement();
        handlers[RecvPacketOpcode.AndroidMove.getValue()] = new AndroidMovement();
        handlers[RecvPacketOpcode.PassiveskillInfoUpdate.getValue()] = new FamiliarMovement();
        handlers[RecvPacketOpcode.MobMove.getValue()] = new MobMovement();
        handlers[RecvPacketOpcode.DragonMove.getValue()] = new DragonMovement();
        handlers[RecvPacketOpcode.SummonedMove.getValue()] = new SummonMovement();
        handlers[RecvPacketOpcode.PetMove.getValue()] = new PetMovement();
        // Chats
        handlers[RecvPacketOpcode.UserChat.getValue()] = new GeneralChatHandler();
        handlers[RecvPacketOpcode.Messenger.getValue()] = new MessengerHandler();
        handlers[RecvPacketOpcode.Whisper.getValue()] = new WhisperHandler();
        handlers[RecvPacketOpcode.GroupMessage.getValue()] = new GroupMessageHandler();

        //Combat handlers
        handlers[RecvPacketOpcode.UserMeleeAttack.getValue()] = new NormalCloseRangeAttack();
        handlers[RecvPacketOpcode.UserShootAttack.getValue()] = new RangedAttack();
        handlers[RecvPacketOpcode.UserMagicAttack.getValue()] = new MagicAttack();
        handlers[RecvPacketOpcode.UserSkillUseRequest.getValue()] = new SpecialAttackMove();
        handlers[RecvPacketOpcode.UserBodyAttack.getValue()] = new PassiveEnergyCloseRangeAttack();
        handlers[RecvPacketOpcode.UserFinalAttackRequest.getValue()] = new FinalAttackHandler();
        //Monsterbook handlers
        handlers[RecvPacketOpcode.GET_BOOK_INFO.getValue()] = new MonsterBookInfoRequest();
        handlers[RecvPacketOpcode.MONSTER_BOOK_DROPS.getValue()] = new MonsterBookDropsRequest();

        //Crafting handlers
        handlers[RecvPacketOpcode.UserOptionChangeRequest.getValue()] = new ProfessionInfo();

        //Player handlers
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
        //handlers[RecvPacketOpcode.SaveDamageSkinRequest.getValue()] = new SaveDamageSkinRequest();

        //Android handlers
        handlers[RecvPacketOpcode.AndroidEmotion.getValue()] = new AndroidEmotion();
        //Pet handlers
        handlers[RecvPacketOpcode.UserPetFoodItemUseRequest.getValue()] = new PetFoodHandler();
        handlers[RecvPacketOpcode.PetStatChangeItemUseRequest.getValue()] = new PetAutoPotionHandler();
        handlers[RecvPacketOpcode.PetInteractionRequest.getValue()] = new PetCommandHandler();
        handlers[RecvPacketOpcode.UserActivatePetRequest.getValue()] = new SpawnPetHandler();
        handlers[RecvPacketOpcode.PetDropPickUpRequest.getValue()] = new PetPickupHandler();
        handlers[RecvPacketOpcode.PetAction.getValue()] = new PetChatHandler();
        //Item handlers
        handlers[RecvPacketOpcode.UserStatChangeItemCancelRequest.getValue()] = new CancelItemEffectHandler();
        handlers[RecvPacketOpcode.UserStatChangeItemUseRequest.getValue()] = new UseItemEffectHandler();
        handlers[RecvPacketOpcode.UserActivateNickItem.getValue()] = new TitleEquipHandler();
        handlers[RecvPacketOpcode.UserUIOpenItemUseRequest.getValue()] = new OpenItemUI();
        // NPC Handlers
        handlers[RecvPacketOpcode.UserScriptMessageAnswer.getValue()] = new NPCMoreTalkHandler();
        handlers[RecvPacketOpcode.NpcMove.getValue()] = new NpcMovement();
        handlers[RecvPacketOpcode.UserSelectNpc.getValue()] = new NPCTalkHandler();
        handlers[RecvPacketOpcode.UserShopRequest.getValue()] = new NPCShopHandler();
        handlers[RecvPacketOpcode.UserQuestRequest.getValue()] = new QuestActionHandler();
        handlers[RecvPacketOpcode.UserTrunkRequest.getValue()] = new StorageHandler();

        // Stats handler
        handlers[RecvPacketOpcode.UserHyperStatSkillUpRequest.getValue()] = new DistributeHyperStatHandler();
        handlers[RecvPacketOpcode.UserHyperStatSkillResetRequest.getValue()] = new ResetHyperStatHandler();
        handlers[RecvPacketOpcode.UserRequestInstanceTable.getValue()] = new UpdateHyperStatHandler();
        handlers[RecvPacketOpcode.UserAbilityUpRequest.getValue()] = new DistributeAPHandler();
        handlers[RecvPacketOpcode.UserAbilityMassUpRequest.getValue()] = new AutoDistributeAPHandler();
        handlers[RecvPacketOpcode.UserSkillUpRequest.getValue()] = new DistributeSPHandler();
        handlers[RecvPacketOpcode.UserHyperSkillUpRequest.getValue()] = new DistributeHyperHandler();
        handlers[RecvPacketOpcode.UserHyperSkillResetRequset.getValue()] = new ResetHyperHandler();
        // Player Operation handler
        handlers[RecvPacketOpcode.GuildRequest.getValue()] = new GuildOperationHandler();
        handlers[RecvPacketOpcode.GuildBBS.getValue()] = new GuildBBSOperation();
        handlers[RecvPacketOpcode.GuildJoinRequest.getValue()] = new GuildJoinRequestHandler();
        handlers[RecvPacketOpcode.GuildJoinCancelRequest.getValue()] = new CancelGuildJoinRequestHandler();
        handlers[RecvPacketOpcode.PartyRequest.getValue()] = new PartyOperationHandler();
        handlers[RecvPacketOpcode.PartyResult.getValue()] = new DenyPartyRequestHandler();
        handlers[RecvPacketOpcode.PartyInvitableSet.getValue()] = new AllowPartyInviteHandler();
        handlers[RecvPacketOpcode.MiniRoom.getValue()] = new PlayerInteractionHandler();
        handlers[RecvPacketOpcode.GetRewardRequest.getValue()] = new OnUserRewardClaimRequest();
        handlers[RecvPacketOpcode.BeginEventRanking.getValue()] = new BossMatchmakingHandler();
        //handlers[672] = new BossMatchmakingHandler(); //TODO: Handle this, for Ursus + Cygnus?
        // Buddy List handlers
        handlers[RecvPacketOpcode.FriendRequest.getValue()] = new BuddylistModifyHandler();
        handlers[RecvPacketOpcode.LoadAccountIDOfCharacterFriendRequest.getValue()] = new FriendRequestAccIdHandler();
        // Reactor handlers
        handlers[RecvPacketOpcode.ReactorClick.getValue()] = new ClickTouchReactorHandler();
        handlers[RecvPacketOpcode.ReactorRectInMob.getValue()] = new ClickTouchReactorHandler();
        handlers[RecvPacketOpcode.ReactorHit.getValue()] = new HitReactorHandler();
        // Monster handlers
        // Summons handlers
        handlers[RecvPacketOpcode.SummonedHit.getValue()] = new SummonsDamageHandler();
        handlers[RecvPacketOpcode.SummonedAttack.getValue()] = new SummonAttackHandler();
        handlers[RecvPacketOpcode.Remove.getValue()] = new RemoveSummonHandler();
        handlers[RecvPacketOpcode.SummonedSkill.getValue()] = new SubSummonHandler();
        // Inventory handler
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
        handlers[RecvPacketOpcode.PAM_SONG.getValue()] = new UsePamSongHandler();
        //Skill handlers
        handlers[RecvPacketOpcode.UserSkillPrepareRequest.getValue()] = new SkillEffectHandler();
        handlers[RecvPacketOpcode.UserMacroSysDataModified.getValue()] = new SkillMacroHandler();
        handlers[RecvPacketOpcode.FuncKeyMappedModified.getValue()] = new KeyMap();
        //Aran combo handler
        handlers[RecvPacketOpcode.RequestIncCombo.getValue()] = new AranComboHandler();
        //Blaze Wizard 
        handlers[RecvPacketOpcode.UserFlameOrbRequest.getValue()] = new OrbitalFlameHandler();
        handlers[RecvPacketOpcode.UserNonTargetForceAtomAttack.getValue()] = new NonTargetAtomAttackHandler(); //just added this
        handlers[RecvPacketOpcode.UserForceAtomCollision.getValue()] = new AtomCollisionHandler();
        //Kaiser
        handlers[RecvPacketOpcode.KAISER_SKILL_SHORTCUT.getValue()] = new KaiserSkillShortcut();
        handlers[RecvPacketOpcode.UserRequestFlyingSwordStart.getValue()] = new ReleaseTempestBlades();
        //Kinesis
        //handlers[RecvPacketOpcode.CreateKinesisPsychicArea.getValue()] = new KinesisPsychicHandler();
        handlers[RecvPacketOpcode.DecPsychicPointRequest.getValue()] = new KinesisPsychicHandler();
        handlers[RecvPacketOpcode.DoActivePsychicArea.getValue()] = new KinesisAttackHandler();
        handlers[RecvPacketOpcode.DebuffPsychicArea.getValue()] = new KinesisCancelPsychicRequest();
        handlers[RecvPacketOpcode.ReleasePsychicArea.getValue()] = new KinesisDamageHandler();
        // Phantom
        handlers[RecvPacketOpcode.UserRequestSetStealSkillSlot.getValue()] = new SetStealSkillSlotHandler();
        handlers[RecvPacketOpcode.UserRequestStealSkillMemory.getValue()] = new StealSkillMemoryHandler();
        handlers[RecvPacketOpcode.UserRequestStealSkillList.getValue()] = new StealSkillListHandler();
        //Kinesis
        handlers[RecvPacketOpcode.CreateKinesisPsychicArea.getValue()] = new CreateKinesisPsychicAreaHandler();
        handlers[RecvPacketOpcode.DoActivePsychicArea.getValue()] = new DoActivePsychicAreaHandler();
        handlers[RecvPacketOpcode.ReleasePsychicArea.getValue()] = new ReleasePsychicAreaHandler();
        handlers[RecvPacketOpcode.DebuffPsychicArea.getValue()] = new DebuffPsychicAreaHandler();
        handlers[RecvPacketOpcode.CreatePsychicLock.getValue()] = new CreatePsychicLockHandler();
        handlers[RecvPacketOpcode.ResetPathPsychicLock.getValue()] = new ResetPathPsychicLockHandler();
        handlers[RecvPacketOpcode.ReleasePsychicLock.getValue()] = new ReleasePsychicLockHandler();
        //Environment handlers
        handlers[RecvPacketOpcode.UpdateClientEnvironment.getValue()] = new ClientEnvironmentHandler();

        //V Matrix
        handlers[RecvPacketOpcode.UserUpdateMatrix.getValue()] = new UpdateMatrixHandler();

        //Field change handlers
        //Anticheat handlers
        handlers[RecvPacketOpcode.BUY_CS_ITEM.getValue()] = new CashShopPurchase();
        handlers[RecvPacketOpcode.CashShopCheckCouponRequest.getValue()] = new CashCouponHandler();
        handlers[RecvPacketOpcode.CASH_CATEGORY.getValue()] = new CashCategorySwitch();
        handlers[RecvPacketOpcode.CS_UPDATE.getValue()] = new UpdatePlayerCashInfo();
    }

    public MapleServerHandler(MapleServerMode mode) {
        this(-1, -1, mode);
    }

    public MapleServerHandler(int world, int channel, MapleServerMode mode) {
        this.world = world;
        this.channel = channel;
        this.mode = mode;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        Channel ch = ctx.channel();
        try {
            Client c = (Client) ch.attr(Client.SESSION_KEY).get();
            c.disconnect(true, false);
            c.cancelPingTask();
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().error("Unable to save disconnected session.");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int SendSeq = (int) (Math.random() * Integer.MAX_VALUE);
        int RecvSeq = (int) (Math.random() * Integer.MAX_VALUE);

        Client client = new Client(ch, SendSeq, RecvSeq);

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.println("[Session] Opened Session (" + client.getSessionIPAddress() + ")");
        }

        // Start of IP checking
        final String address = client.GetIP();

        LoginServer.removeIPAuth(address);

        client.setWorld(world);
        client.setChannel(channel);
        client.setReceiving(true);

        client.SendPacket(CLogin.Handshake(SendSeq, RecvSeq));
        ch.attr(Client.SESSION_KEY).set(client);

        client.startPing(ch);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        try {
            Client c = (Client) ch.attr(Client.SESSION_KEY).get();
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InPacket iPacket = (InPacket) msg;
        Channel ch = ctx.channel();

        Client ClientSocket = (Client) ch.attr(Client.SESSION_KEY).get();

        if (!ClientSocket.isReceiving() || msg == null) {
            return;
        }

        int packetId = iPacket.DecodeShort();
        ProcessPacket handler = handlers[packetId];

        String head = "Unknown";
        for (RecvPacketOpcode packet : RecvPacketOpcode.values()) {
            if (packet.getValue() == (int) packetId) {
                head = packet.name();
            }
        }

        try {
            if (handler != null) {
                if (handler.ValidateState(ClientSocket)) {
                    switch (head) {
                        case "PrivateServerPacket":
                        case "AliveAck":
                        case "UserQuestRequest":
                        case "UserMove":
                        case "NpcMove":
                        case "MobMove":
                        case "UserChangeStatRequest":
                        case "UserActivateDamageSkin":
                        case "UpdateClientEnvironment":
                            if (ServerConstants.REDUCED_DEBUG_SPAM) {
                                break; // Doesn't display these packets, prevents major console spam.
                            }
                        default:
                            if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
                                System.out.printf("[Recv Operation] %s (%d) : %s%n", head, packetId, iPacket.toString());
                            }
                            break;
                    }
                    handler.Process(ClientSocket, iPacket);
                } else {
                    LogHelper.PACKET_HANDLER.get().error("Unvalid packet handling state for " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer()) + " (" + ClientSocket.getAccountName() + ") on map " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString());
                }
            } else {
                if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
                    switch (head) {
                        case "UserActivateDamageSkin":
                            if (ServerConstants.REDUCED_DEBUG_SPAM) {
                                break; // Doesn't display these packets, prevents major console spam.
                            }
                        default:
                            System.out.println("[Unhandled Operation] " + head + " (" + packetId + ") : The respected receive operation is currently unhandled.");
                            break;
                    }
                }
            }
        } catch (Throwable t) {
            LogHelper.PACKET_HANDLER.get().error("Error for " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer()) + " (" + ClientSocket.getAccountName() + ") on map " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString(), t);
        }
    }
}
