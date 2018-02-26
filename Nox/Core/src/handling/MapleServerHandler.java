package handling;

import client.MapleClient;
import constants.ServerConstants;
import crypto.CAESCipher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import service.LoginServer;
import net.Packet;
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
import netty.ProcessPacket;

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
        //Combat handlers
        handlers[RecvPacketOpcode.UserMeleeAttack.getValue()] = new NormalCloseRangeAttack();
        handlers[RecvPacketOpcode.UserShootAttack.getValue()] = new RangedAttack();
        handlers[RecvPacketOpcode.UserMagicAttack.getValue()] = new MagicAttack();
        handlers[RecvPacketOpcode.UserSkillUseRequest.getValue()] = new SpecialAttackMove();
        handlers[RecvPacketOpcode.UserBodyAttack.getValue()] = new PassiveEnergyCloseRangeAttack();
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
        handlers[RecvPacketOpcode.UserPortalScriptRequest.getValue()] = new MapChangeSpecialHandler();
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
        // Buddy List handlers
        handlers[RecvPacketOpcode.FriendRequest.getValue()] = new BuddylistModifyHandler();
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
        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            t.printStackTrace();
        }
        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.println("[Session] Exception Caught");
        }
        // XXX remove later, disconnect client after this point
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        int SendSeq = (int) (Math.random() * Integer.MAX_VALUE);
        int RecvSeq = (int) (Math.random() * Integer.MAX_VALUE);

        MapleClient client = new MapleClient(ch, SendSeq, RecvSeq);

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.println("[Session] Opened Session (" + client.getSessionIPAddress() + ")");
        }

        // Start of IP checking
        final String address = client.GetIP();

        LoginServer.removeIPAuth(address);

        client.setWorld(world);
        client.setChannel(channel);
        client.setReceiving(true);

        client.write(CLogin.Handshake(SendSeq, RecvSeq));

        ch.attr(MapleClient.CLIENT_KEY).set(client);
        ch.attr(MapleClient.CRYPTO_KEY).set(new CAESCipher(new byte[]{
            (byte) 0x29, 0x00, 0x00, 0x00,
            (byte) 0xF6, 0x00, 0x00, 0x00,
            (byte) 0x18, 0x00, 0x00, 0x00,
            (byte) 0x5E, 0x00, 0x00, 0x00,
            (byte) 0xCA, 0x00, 0x00, 0x00,
            (byte) 0x5A, 0x00, 0x00, 0x00,
            (byte) 0x40, 0x00, 0x00, 0x00,
            (byte) 0x61, 0x00, 0x00, 0x00
        }));

        client.startPing(ch);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();

        MapleClient c = (MapleClient) ch.attr(MapleClient.CLIENT_KEY).get();

        c.disconnect(true, false); // handle this is we don't soft disconnect through handler

        c.cancelPingTask();

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.println("[Session] Closed Session (" + c.GetIP() + ")");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet buffer = (Packet) msg;
        Channel ch = ctx.channel();

        MapleClient ClientSocket = (MapleClient) ch.attr(MapleClient.CLIENT_KEY).get();
        InPacket iPacket = ClientSocket.GetDecoder().Next(buffer);

        if (!ClientSocket.isReceiving() || msg == null) {
            return;
        }

        int packetId = iPacket.DecodeShort();
        ProcessPacket handler = handlers[packetId];
        
        String head = "Unknown";
        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            head = "Unknown";
            for (RecvPacketOpcode packet : RecvPacketOpcode.values()) {
                if (packet.getValue() == (int) packetId) {
                    head = packet.name();
                }
            }
            
            switch (head) {
                case "AliveAck":
                case "UserQuestRequest":
                case "UserMove":
                case "NpcMove":
                case "MobMove":
                case "UserChangeStatRequest":
                    if (ServerConstants.REDUCED_DEBUG_SPAM) {
                        break; // Doesn't display these packets, prevents major console spam.
                    }
                default:
                    System.out.printf("[Recv Operation] %s (%d) : %s%n", head, packetId, buffer.toString());
                    break;
            }
        }

        try {
            if (handler != null) {
                if (handler.ValidateState(ClientSocket)) {
                    handler.Process(ClientSocket, iPacket);
                } else {
                    LogHelper.PACKET_HANDLER.get().error("Unvalid packet handling state for " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer()) + " (" + ClientSocket.getAccountName() + ") on map " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString());
                }
            } else {
                if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
                    System.out.println("[Unhandled Operation] " + head + " (" + packetId + ") : The respected receive operation is currently unhandled.");
                }
            }
        } catch (Throwable t) {
            LogHelper.PACKET_HANDLER.get().error("Error for " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer()) + " (" + ClientSocket.getAccountName() + ") on map " + (ClientSocket.getPlayer() == null ? "null" : ClientSocket.getPlayer().getMapId()) + " with packet " + iPacket.toString(), t);
        }
    }
}
