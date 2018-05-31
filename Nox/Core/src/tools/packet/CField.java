package tools.packet;

import client.CharacterTemporaryStat;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import client.ClientSocket;
import client.MapleKeyLayout;
import client.QuestStatus;
import client.Skill;
import client.SkillMacro;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.QuickMove.QuickMoveNPC;
import constants.ServerConstants;
import constants.SkillConstants;
import constants.skills.DualBlade;
import constants.skills.Blaster;
import constants.skills.Kinesis;
import constants.skills.Mechanic;
import handling.game.PlayerInteractionHandler;
import handling.world.World;
import handling.world.World.Guild;
import handling.world.MapleGuild;
import handling.world.MapleGuildAlliance;
import service.SendPacketOpcode;
import provider.data.HexTool;
import net.OutPacket;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import server.MaplePackageActions;
import server.Trade;
import server.Randomizer;
import server.events.MapleSnowball;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.objects.Android;
import server.maps.objects.User;
import server.maps.objects.EvanDragon;
import server.maps.objects.KannaHaku;
import server.maps.objects.Kite;
import server.maps.objects.Mist;
import server.life.NPCLife;
import server.maps.objects.Reactor;
import server.maps.objects.RuneStone;
import server.maps.objects.Summon;
import server.maps.objects.MechDoor;
import server.maps.objects.MonsterFamiliar;
import server.movement.LifeMovementFragment;
import server.quest.Quest;
import server.shops.Shop;
import server.shops.ShopOperationType;
import handling.world.AttackMonster;
import handling.game.PlayerDamageHandler;
import handling.game.WhisperHandler.WhisperFlag;
import handling.world.AttackInfo;
import java.awt.Rectangle;
import java.util.Random;
import server.maps.Map_MaplePlatform;
import server.maps.objects.ForceAtom;
import tools.Pair;
import tools.Triple;
import tools.Utility;

public class CField {

    public static int DEFAULT_BUFFMASK = 0;

    public static byte[] getPacketFromHexString(String hex) {
        return HexTool.getByteArrayFromHexString(hex);
    }

    /**
     * Gets a packet telling the client the IP of the channel server.
     *
     * @param Client the client object
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static OutPacket getServerIP(ClientSocket c, int port, int clientId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SelectCharacterResult.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        oPacket.Encode(ServerConstants.NEXON_IP);
        oPacket.EncodeShort(port);
        oPacket.EncodeInt(0);
        oPacket.EncodeShort(0);

        oPacket.EncodeInt(Randomizer.nextInt());
        oPacket.EncodeInt(clientId);

        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);

        byte[] interServerAuthBuffer = new byte[8];
        Randomizer.nextBytes(interServerAuthBuffer);
        oPacket.Encode(interServerAuthBuffer); // TODO: Check on this when the client sends PLAYER_LOGGEDIN on the channel servers
        oPacket.EncodeByte(0);
        return oPacket;
    }

    /**
     * Gets a packet telling the client the IP of the new channel.
     *
     * @param Client the client object
     * @param port The port the channel is on.
     * @return The server IP packet.
     */
    public static OutPacket getChannelChange(ClientSocket c, int port) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MigrateCommand.getValue());
        oPacket.EncodeByte(1);
        oPacket.Encode(ServerConstants.NEXON_IP);
        oPacket.EncodeShort(port);
        oPacket.EncodeInt(0); //dunno, couldnt find the packet in the idb because I was not looking hard enough
        return oPacket;
    }

    public static OutPacket OnWhisper(int nFlag, String sFind, String sReceiver, String sMsg, int nLocationResult, int dwLocation, int nTargetPosition_X, int nTargetPosition_Y, boolean bFromAdmin, boolean bSuccess) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Whisper.getValue());

        oPacket.EncodeByte(nFlag);
        switch (nFlag) {
            case WhisperFlag.ReplyReceive:
                oPacket.EncodeString(sReceiver);
                oPacket.EncodeByte(dwLocation);
                oPacket.EncodeBool(bFromAdmin);
                oPacket.EncodeString(sMsg);
                break;
            case WhisperFlag.BlowWeather:
                oPacket.EncodeString(sReceiver);
                oPacket.EncodeBool(bFromAdmin);
                oPacket.EncodeString(sMsg);
                break;
            case WhisperFlag.ReplyResult:
            case WhisperFlag.AdminResult:
                oPacket.EncodeString(sFind);
                oPacket.EncodeBool(bSuccess);
                break;
            case WhisperFlag.FindResult:
            case WhisperFlag.LocationResult:
                oPacket.EncodeString(sFind);
                oPacket.EncodeByte(nLocationResult);
                oPacket.EncodeInt(dwLocation);
                if (nLocationResult == WhisperFlag.GameSvr) {
                    oPacket.EncodeInt(nTargetPosition_X);
                    oPacket.EncodeInt(nTargetPosition_Y);
                }
                break;
            case WhisperFlag.BlockedResult:
                oPacket.EncodeString(sFind);
                oPacket.EncodeBool(bSuccess);
                break;
        }
        return oPacket;
    }

    public static OutPacket OnGroupMessage(int nType, String sFrom, String sMsg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GroupMessage.getValue());
        oPacket.EncodeByte(nType);
        oPacket.EncodeString(sFrom);
        oPacket.EncodeString(sMsg);
        return oPacket;
    }

    public static OutPacket createForceAtom(boolean bByMob, int nUserOwner, int nTargetID, int nForceAtomType, boolean bToMob,
            int nTargets, int nSkillID, ForceAtom pForceAtom, Rectangle rRect, int nArriveDirection, int nArriveRange,
            Point forcedTargetPos, int bulletID, Point pos) {

        List<Integer> nNumbers = new ArrayList<>();
        nNumbers.add(nTargets);
        List<ForceAtom> forceAtomInfos = new ArrayList<>();
        forceAtomInfos.add(pForceAtom);
        return createForceAtom(bByMob, nUserOwner, nTargetID, nForceAtomType, bToMob, nNumbers, nSkillID, forceAtomInfos,
                rRect, nArriveDirection, nArriveRange, forcedTargetPos, bulletID, pos);
    }

    public static OutPacket createForceAtom(boolean bByMob, int nUserOwner, int nCharID, int nForceAtomType, boolean bToMob,
            List<Integer> aTargets, int nSkillID, List<ForceAtom> aForceAtoms, Rectangle rRect, int nArriveDirection, int nArriveRange,
            Point forcedTargetPos, int bulletID, Point pos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.EncodeBool(bByMob);
        if (bByMob) {
            oPacket.EncodeInt(nUserOwner);
        }
        oPacket.EncodeInt(nCharID);
        oPacket.EncodeInt(nForceAtomType);
        if (nForceAtomType != 0 && nForceAtomType != 9 && nForceAtomType != 14) {
            oPacket.EncodeBool(bToMob);
            switch (nForceAtomType) {
                case 2:
                case 3:
                case 6:
                case 7:
                case 11:
                case 12:
                case 13:
                case 17:
                case 19:
                case 20:
                case 23:
                case 24:
                case 25:
                    oPacket.EncodeInt(aTargets.size());
                    for (int i : aTargets) {
                        oPacket.EncodeInt(i);
                    }
                    break;
                default:
                    oPacket.EncodeInt(aTargets.get(0));
                    break;
            }
            oPacket.EncodeInt(nSkillID);
        }
        for (ForceAtom pAtom : aForceAtoms) {
            oPacket.EncodeByte(1);
            pAtom.encode(oPacket);
        }
        oPacket.EncodeByte(0);
        switch (nForceAtomType) {
            case 11:
                oPacket.EncodeRectangle(rRect);
                oPacket.EncodeInt(bulletID);
                break;
            case 9:
            case 15:
                oPacket.EncodeRectangle(rRect);
                break;
            case 16:
                oPacket.EncodePosition(pos);
                break;
            case 17:
                oPacket.EncodeInt(nArriveDirection);
                oPacket.EncodeInt(nArriveRange);
                break;
            case 18:
                oPacket.EncodePosition(forcedTargetPos);
                break;
        }

        oPacket.Fill(0, 29);

        return oPacket;
    }

    /**
     * Handles Final Attack.
     */
    public static OutPacket finalAttackRequest(User pPlayer, int nSkill, int nFinalSkill, int nDelay, int nMob, int nRequestTime) {
        return finalAttackRequest(pPlayer, nSkill, nFinalSkill, nDelay, nMob, nRequestTime, false, null);
    }

    public static OutPacket finalAttackRequest(User pPlayer, int nSkill, int nFinalSkill, int nDelay, int nMob, int nRequestTime, boolean bLeft, Point pBase) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserFinalAttackRequest.getValue());

        oPacket.EncodeInt(nSkill); // nSkillId
        oPacket.EncodeInt(nFinalSkill); // nFinalSkillId
        oPacket.EncodeInt(pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).getItemId()); // nWeaponId
        oPacket.EncodeInt(nDelay); // nDelay
        oPacket.EncodeInt(nMob); // nMobId
        oPacket.EncodeInt(nRequestTime); // nReqTime

        if (nSkill == 101000102) { // Air Riot
            oPacket.EncodeBool(bLeft);
            oPacket.EncodePosition(pBase);
        }

        return oPacket;
    }

    public static OutPacket OrbitalFlame(int cid, int skillid, int effect, int direction, int range) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Fill(0, 1);
        oPacket.EncodeInt(cid);
        /* Unk */
        oPacket.EncodeInt(0x11);
        /* onOrbitalFlame */
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(skillid);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(2);
        oPacket.EncodeInt(effect); // Orbital Flame Effect
        oPacket.EncodeInt(17);
        oPacket.EncodeInt(17);
        oPacket.EncodeInt(90);
        oPacket.Fill(0, 12);
        oPacket.EncodeInt(Randomizer.nextInt());
        oPacket.EncodeInt(8);
        oPacket.EncodeInt(0); // v174+
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(direction);
        oPacket.EncodeInt(range);

        return oPacket;
    }

    public static OutPacket sendKaiserSkillShortcut(int[] skills) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.KaiserSkillShortcut.getValue());
        for (int i = 0; i < 3; i++) {
            if (skills[i] != 0) {
                oPacket.EncodeBool(true);
                oPacket.EncodeByte(i);
                oPacket.EncodeInt(skills[i]);
                int x = 0;
                oPacket.EncodeByte(x);
                if (x != 0) {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }
        }

        return oPacket;
    }

    public static OutPacket getMacros(SkillMacro[] macros) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MacroSysDataInit.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        oPacket.EncodeByte(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                oPacket.EncodeString(macro.getName());
                oPacket.EncodeByte(macro.getShout());
                oPacket.EncodeInt(macro.getSkill1());
                oPacket.EncodeInt(macro.getSkill2());
                oPacket.EncodeInt(macro.getSkill3());
            }
        }

        return oPacket;
    }

    public static OutPacket gameMsg(String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserNoticeMsg.getValue());
        oPacket.EncodeString(msg);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket innerPotentialMsg(String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GetCharacterPosition.getValue());
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket updateInnerPotential(byte ability, int skill, int level, int rank) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterPotentialSet.getValue());
        oPacket.EncodeByte(1); //unlock
        oPacket.EncodeByte(1); //0 = no update
        oPacket.EncodeShort(ability); //1-3
        oPacket.EncodeInt(skill); //skill id (7000000+)
        oPacket.EncodeShort(level); //level, 0 = blank inner ability
        oPacket.EncodeShort(rank); //rank
        oPacket.EncodeByte(1); //0 = no update

        return oPacket;
    }

    public static OutPacket innerPotentialResetMessage() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterPotentialReset.getValue());
        oPacket.EncodeString("Inner Potential has been reconfigured.");
        oPacket.EncodeByte(1);
        return oPacket;
    }

    public static OutPacket updateHonour(int honourLevel, int honourExp, boolean levelup) {
        /*
         * data:
         * 03 00 00 00
         * 69 00 00 00
         * 01
         */

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterHonorExp.getValue());

        oPacket.EncodeInt(honourLevel);
        oPacket.EncodeInt(honourExp);
        oPacket.EncodeByte(levelup ? 1 : 0); //shows level up effect

        return oPacket;
    }

    public static OutPacket getCharInfo(User pPlayer) {
        return getWarpToMap(pPlayer, null, 0, false);
    }

    public static OutPacket getWarpToMap(User pPlayer, MapleMap pToMap, int nSpawnPoint, boolean bCharacterData) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetField.getValue());

        // Prevent disconnects upon changing map with Shadow Partner active - Mazen.
        if (GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
            pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowServant);
            pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowIllusion);
            pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowPartner);
        }

        //CClient::DecodeOptMan (Size)
        oPacket.EncodeShort(0);

        /*oPacket.encodeShort(2);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(2);
        oPacket.EncodeInt(0);
        /*
        public static final int GlobalOpt = 0x0;
    	public static final int LastConnectInfo = 0x1;
    	public static final int CharacterOpt = 0x2;
        
        int v4 = m_mOpt.size();
        oPacket.Encode2(v4);
        if (!m_mOpt.isEmpty()) {
            for (Map.Entry<Integer, Integer> mOpt : m_mOpt.entrySet()) {
                oPacket.Encode4(mOpt.getKey());//dwType
                oPacket.Encode4(mOpt.getValue());//nOpt
            }
        }*/
        //Channel
        oPacket.EncodeInt(pPlayer.getClient().getChannel() - 1);

        //Admin Byte
        oPacket.EncodeByte(0);

        //Follow Feature (wOldDriverID)
        oPacket.EncodeInt(0);

        //Logging Into Handler (1) Changing Map (2) bPopupDlg
        oPacket.EncodeByte(bCharacterData ? 1 : 2);

        //oPacket.encode(1); // bPopupDlg
        //?
        oPacket.EncodeInt(0);

        //nFieldWidth (Map Width)
        oPacket.EncodeInt(800);

        //nFieldHeight (Map Height)
        oPacket.EncodeInt(600);

        //bCharacterData
        oPacket.EncodeBool(bCharacterData);

        //Size (string (size->string))
        oPacket.EncodeShort(0); // notifier check (used for block reason)

        if (bCharacterData) {
            //3 randomized integers (For the critical damage calculation)
            pPlayer.CRand().connectData(oPacket);

            PacketHelper.addCharacterInfo(oPacket, pPlayer);
            //PacketHelper.addLuckyLogoutInfo(oPacket, false, null, null, null);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        } else {

            //bUsingBuffProtector (Calls the revive function upon death)
            oPacket.EncodeByte(0);

            /*
	        if ( (*(TSingleton<CUserLocal>::ms_pInstance._m_pStr + 336) & 0xFFFFFFFE) == 18 || v13 )
		    {
		      CWvsContext::OnRevive(v2);
		      v2->m_bUsingBuffProtector = 0;
		    }
             */
            oPacket.EncodeInt(pToMap.getId());
            oPacket.EncodeByte(nSpawnPoint);
            oPacket.EncodeInt(pPlayer.getStat().getHp());

            //Boolean (int + int)
            oPacket.EncodeByte(0);

            /*
	         *(&off_20DFDB8 + 73) = (void **)v25;
		       if ( v25 )
		       {
		        *(&off_20DFDB8 + 71) = (void **)CInPacket::Decode4(a3);
		        *(&off_20DFDB8 + 72) = (void **)CInPacket::Decode4(a3);
		       }
             */
        }

        //CWvsContext::SetWhiteFadeInOut
        oPacket.EncodeByte(0);

        //pChatBlockReason
        oPacket.EncodeByte(0);

        //Some Korean Event (Fame-up)
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));

        //paramFieldInit.nMobStatAdjustRate
        oPacket.EncodeInt(100);

        //Party Map Experience Boolean (int + string(bgm) + int(fieldid))
        oPacket.EncodeByte(0);

        /*if ( CInPacket::Decode1(iPacket) )
        {
          ZRef<CFieldCustom>::_Alloc(&paramFieldInit.pFieldCustom);
          CFieldCustom::Decode(paramFieldInit.pFieldCustom.p, iPacket);
        }*/
        //Boolean
        oPacket.EncodeByte(0);

        /*
           if ( CInPacket::Decode1(iPacket) )
    		CWvsContext::OnInitPvPStat(v2); 
         */
        //bCanNotifyAnnouncedQuest
        oPacket.EncodeByte(0);

        //Boolean ((int + byte(size))->(int, int, int))->(long, int, int)
        oPacket.EncodeByte(0);

        /*if ( CInPacket::Decode1(iPacket) )
        {
          v32 = CInPacket::Decode4(iPacket);
          v33 = g_pStage.p;
          v34 = v32;
          if ( g_pStage.p && (v35 = g_pStage.p->vfptr->IsKindOf, v277 = &CField::ms_RTTI_CField, (v35)(&g_pStage.p->vfptr)) )
            v36 = v33;
          else
            v36 = 0;
          CField::DrawStackEventGauge(v36, v34);
        }*/
        boolean starPlanet = false;
        oPacket.EncodeByte(0);
        if (starPlanet) {
            //nRoundID
            oPacket.EncodeInt(0);

            //the size, cannot exceed the count of 10
            oPacket.EncodeByte(0);

            //anPoint
            oPacket.EncodeInt(0);

            //anRanking
            oPacket.EncodeInt(0);

            //atLastCheckRank (timeGetTime - 300000)
            oPacket.EncodeInt(0);

            //ftShiningStarExpiredTime
            oPacket.EncodeLong(0);

            //nShiningStarPickedCount
            oPacket.EncodeInt(0);

            //nRoundStarPoint
            oPacket.EncodeInt(0);
        }

        //Boolean (int + byte + long)
        boolean aStarPlanetRoundInfo = false;
        oPacket.EncodeBool(aStarPlanetRoundInfo);
        if (aStarPlanetRoundInfo) {
            //nStarPlanetRoundID
            oPacket.EncodeInt(0);

            //nStarPlanetRoundState
            oPacket.EncodeByte(0);

            //ftStarPlanetRoundEndDate
            oPacket.EncodeLong(0);
        }

        //CWvsContext::DecodeStarPlanetRoundInfo(pCtx, iPacket);
        oPacket.EncodeInt(0);//CUser::DecodeTextEquipInfo

        //FreezeAndHotEventInfo::Decode
        oPacket.EncodeByte(0); //nAccountType
        oPacket.EncodeInt(0); //dwAccountID

        //CCUser::DecodeEventBestFriendInfo
        oPacket.EncodeInt(0); //dwEventBestFriendAID
        oPacket.EncodeInt(0);

        // Okay so there is more data to write here for certain maps such as Von Bon's boss arena. 
        // Need to check IDA later for this, fill for now. -Mazen
        oPacket.Fill(0, 39);
        
        return oPacket;
    }

    public static OutPacket removeBGLayer(boolean remove, int map, byte layer, int duration) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetMapTaggedObjectVisible.getValue());
        oPacket.EncodeByte(remove ? 1 : 0); //Boolean show or remove
        oPacket.EncodeInt(map);
        oPacket.EncodeByte(layer); //Layer to show/remove
        oPacket.EncodeInt(duration);

        return oPacket;
    }

    public static OutPacket setMapObjectVisible(List<Pair<String, Byte>> objects) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetMapTaggedObjectVisible.getValue());
        oPacket.EncodeByte(objects.size());
        for (Pair<String, Byte> object : objects) {
            oPacket.EncodeString(object.getLeft());
            oPacket.EncodeByte(object.getRight());
        }

        return oPacket;
    }

    public static OutPacket enchantResult(int result) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemInGameCubeResult.getValue());
        oPacket.EncodeInt(result);//0=fail/1=sucess/2=idk/3=shows stats

        return oPacket;
    }

    public static OutPacket showEquipEffect() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActiveEffectItem.getValue());

        return oPacket;
    }

    public static OutPacket showEquipEffect(int team) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActiveEffectItem.getValue());
        oPacket.EncodeShort(team);

        return oPacket;
    }

    public static OutPacket MapEff(String path) {
        return environmentChange(path, 4, 0);//was 3
    }

    public static OutPacket MapNameDisplay(int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 4, 0);
    }

    public static OutPacket Aran_Start() {
        return environmentChange("Aran/balloon", 4, 0);
    }

    public static OutPacket musicChange(String song) {
        return environmentChange(song, 7, 0);//was 6
    }

    public static OutPacket showEffect(String effect) {
        return environmentChange(effect, 4, 0);//was 3
    }

    public static OutPacket playSound(String sound) {
        return environmentChange(sound, 5, 0);//was 4
    }

    public static OutPacket playSound(String sound, int delay) {
        return environmentChange(sound, 5, delay);//was 4
    }

    public static OutPacket environmentChange(String env, int mode, int delay) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldEffect.getValue());
        oPacket.EncodeByte(mode);
        oPacket.EncodeString(env);
        oPacket.EncodeInt(delay); // 0x64

        return oPacket;
    }

    public static OutPacket trembleEffect(int type, int delay) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldEffect.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(delay);
        oPacket.EncodeShort(30);
        // oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket environmentMove(String env, int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MoveEnvironment.getValue());
        oPacket.EncodeString(env);
        oPacket.EncodeInt(mode);

        return oPacket;
    }

    public static OutPacket getUpdateEnvironment(MapleMap map) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UpdateEnvironment.getValue());
        oPacket.EncodeInt(map.getEnvironment().size());
        for (Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            oPacket.EncodeString(mp.getKey());
            oPacket.EncodeInt(mp.getValue());
        }

        return oPacket;
    }

    public static OutPacket startMapEffect(String msg, int itemid, boolean active) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldEffect.getValue());
        //oPacket.encode(active ? 0 : 1);

        oPacket.EncodeInt(itemid);
        if (active) {
            oPacket.EncodeString(msg);
        }
        oPacket.EncodeByte(active ? 0 : 1); // Moved down here for v176

        return oPacket;
    }

    public static OutPacket removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static OutPacket CriticalGrowing(int critical) { // Prime Critical

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
        PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.CriticalGrowing);
        oPacket.EncodeShort(critical);
        oPacket.EncodeInt(4220015);
        oPacket.Fill(0, 22);

        return oPacket;
    }

    public static OutPacket OnOffFlipTheCoin(boolean on) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserFlipTheCoinEnabled.getValue());
        oPacket.EncodeByte(on ? 1 : 0);

        return oPacket;
    }

    /**
     * Create the map environment for when elite boss enters. void __thiscall CField::OnEliteState(CField *this, CInPacket *iPacket)
     *
     * @return
     */
    /* public static OutPacket getEliteState() {
        
        
        OutPacket oPacket = new OutPacket(SendPacketOpcode.ELITE_STATE.getValue());
        
        final int eliteState = 2;
        final int v3 = 0;
        
        oPacket.EncodeInt(eliteState); // this->m_nEliteState = CInPacket::Decode4(iPacket);
        oPacket.EncodeInt(v3); // v3 = CInPacket::Decode4(iPacket);
        
        if (eliteState == 2) {
            oPacket.encodeString("Bgm06/FinalFight"); // CInPacket::DecodeStr(iPacket, &sBgm);
        } else {
            if (eliteState != 3) { 
                // is not 3 = reset
            } else {
                oPacket.encodeString(""); // CInPacket::DecodeStr(iPacket, (ZXString<char> *)&pPropSpecialEliteEffect);
                oPacket.encodeString("0"); // CInPacket::DecodeStr(iPacket, &sBackUOL);
                
                if (v3 != 0) {
                    // play bonus start
                }
            }
        }
        
        return oPacket.createPacket();
    }*/
    public static OutPacket getGMEffect(int value, int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GM_EFFECT.getValue());
        oPacket.EncodeByte(value);
        oPacket.Fill(0, 17);

        return oPacket;
    }

    public static OutPacket showOXQuiz(int questionSet, int questionId, boolean askQuestion) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OX_QUIZ.getValue());
        oPacket.EncodeByte(askQuestion ? 1 : 0);
        oPacket.EncodeByte(questionSet);
        oPacket.EncodeShort(questionId);

        return oPacket;
    }

    /**
     * Used to show description in maps such as Ola Ola [109030001] as specified in WZ file
     *
     * @return
     */
    public static OutPacket showEventInstructions() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket getPVPClock(int type, int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Clock.getValue());
        oPacket.EncodeByte(3);
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket getBanBanClock(int time, int direction) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.Clock.getValue());
        oPacket.EncodeByte(5);
        oPacket.EncodeByte(direction); //0:?????? 1:????
        oPacket.EncodeInt(time);
        return oPacket;
    }

    public static OutPacket getClock(int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Clock.getValue());
        oPacket.EncodeByte(2);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket getClockTime(int hour, int min, int sec) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Clock.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(hour);
        oPacket.EncodeByte(min);
        oPacket.EncodeByte(sec);

        return oPacket;
    }

    public static OutPacket boatPacket(int effect, int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ContiMove.getValue());
        oPacket.EncodeByte(effect);
        oPacket.EncodeByte(mode);

        return oPacket;
    }

    public static OutPacket setBoatState(int effect) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ContiState.getValue());
        oPacket.EncodeByte(effect);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket stopClock() {
        return (new OutPacket(SendPacketOpcode.DestroyClock.getValue()));
    }

    public static OutPacket showAriantScoreBoard() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ShowArenaResult.getValue());

        return oPacket;
    }

    public static OutPacket quickSlot(String skil) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.QuickslotMappedInit.getValue());
        oPacket.EncodeByte(skil == null ? 0 : 1);
        if (skil != null) {
            String[] slots = skil.split(",");
            for (int i = 0; i < 8; i++) {
                oPacket.EncodeInt(Integer.parseInt(slots[i]));
            }
        }

        return oPacket;
    }

    public static OutPacket getMovingPlatforms(MapleMap map) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FootHoldMove.getValue());
        oPacket.EncodeInt(map.getSharedMapResources().footholds.getMovingPlatforms().size());
        for (Map_MaplePlatform mp : map.getSharedMapResources().footholds.getMovingPlatforms()) {
            oPacket.EncodeString(mp.name);
            oPacket.EncodeInt(mp.start);
            oPacket.EncodeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                oPacket.EncodeInt(mp.SN.get(x));
            }
            oPacket.EncodeInt(mp.speed);
            oPacket.EncodeInt(mp.x1);
            oPacket.EncodeInt(mp.x2);
            oPacket.EncodeInt(mp.y1);
            oPacket.EncodeInt(mp.y2);
            oPacket.EncodeInt(mp.x1);
            oPacket.EncodeInt(mp.y1);
            oPacket.EncodeShort(mp.r);
        }

        return oPacket;
    }

    public static OutPacket sendPVPMaps() {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.PvPStatusResult.getValue());
        oPacket.EncodeByte(3); //max amount of players
        for (int i = 0; i < 20; i++) {
            oPacket.EncodeInt(10); //how many peoples in each map
        }
        oPacket.Fill(0, 124);
        oPacket.EncodeShort(150); ////PVP 1.5 EVENT!
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket gainForce(int oid, int count, int color) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.EncodeByte(1); // 0 = remote user?
        oPacket.EncodeInt(oid);
        byte newcheck = 0;
        oPacket.EncodeInt(newcheck); //unk
        if (newcheck > 0) {
            oPacket.EncodeInt(0); //unk
            oPacket.EncodeInt(0); //unk
        }
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(4); // size, for each below
        oPacket.EncodeInt(count); //count
        oPacket.EncodeInt(color); //color, 1-10 for demon, 1-2 for phantom
        oPacket.EncodeInt(0); //unk
        oPacket.EncodeInt(0); //unk
        return oPacket;
    }

    public static OutPacket gainForce(boolean isRemote, User chr, List<Integer> oid, int type, int skillid, List<Pair<Integer, Integer>> forceInfo, Point monsterpos, int throwingStar) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        // int orbitalFlame = GameConstants.getOrbitalFlame(skillid);
        oPacket.EncodeBool(isRemote);
        if (isRemote) {
            oPacket.EncodeInt(chr.getId());
        }
        oPacket.EncodeInt(isRemote ? oid.get(0) : chr.getId());// dwTargetID
        oPacket.EncodeInt(type); // ForceAtomType

        if (!(type == 0 || type == 9 || type == 14)) {
            oPacket.EncodeByte(1);
            if (GameConstants.isSpecialForce(type)) {
                oPacket.EncodeInt(oid.size()); // size
                for (int i = 0; i < oid.size(); i++) {
                    oPacket.EncodeInt(oid.get(i));
                }
            } else {
                oPacket.EncodeInt(oid.get(0));
            }
            oPacket.EncodeInt(skillid); //skillid
        }

        for (Pair<Integer, Integer> info : forceInfo) {
            oPacket.EncodeByte(1); // while on/off
            oPacket.EncodeInt(info.left); // count(dwKey)
            oPacket.EncodeInt(info.right); // color(nInc)
            oPacket.EncodeInt(Randomizer.rand(15, 29));//nFirstImpact
            oPacket.EncodeInt(Randomizer.rand(5, 6));//nSecondImpact
            oPacket.EncodeInt(Randomizer.rand(35, 50));//nAngle
            oPacket.EncodeInt((skillid / 1000000) == 14 ? 1 : 0); //nStartDelay
            oPacket.EncodeInt(0); //ptStart.x
            oPacket.EncodeInt(0); //ptStart.y
            oPacket.EncodeInt((skillid / 1000000) == 14 ? 51399013 : 0); // dwCreateTime
            //  oPacket.EncodeInt(GameConstants.getOrbitalCount(orbitalFlame)); //nMaxHitCount
            // maybe mroe int
        }

        oPacket.EncodeByte(0); // where read??

        if (type == 11) {
            oPacket.EncodeInt(chr.getPosition().x);
            oPacket.EncodeInt(chr.getPosition().y);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }
        if (type == 9 || type == 15) {
            oPacket.EncodeInt(0x1E3);
            oPacket.EncodeInt(-106);
            oPacket.EncodeInt(0x1F7);
            oPacket.EncodeInt(-86);
        }
        if (type == 16) {
            oPacket.EncodeInt(543);
            oPacket.EncodeInt(-325);
        }
        if (type == 17) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }
        if (type == 18) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }

        return oPacket;
    }

    public static OutPacket getAndroidTalkStyle(int npc, String talk, int... args) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
        oPacket.EncodeByte(4);
        oPacket.EncodeInt(npc);
        oPacket.EncodeShort(10);
        oPacket.EncodeString(talk);
        oPacket.EncodeByte(args.length);

        for (int i = 0; i < args.length; i++) {
            oPacket.EncodeInt(args[i]);
        }
        return oPacket;
    }

    public static OutPacket getQuickMoveInfo(boolean show, List<QuickMoveNPC> qm) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetQuickMoveInfo.getValue());
        oPacket.EncodeByte(qm.size() <= 0 ? 0 : show ? qm.size() : 0);
        if (show && qm.size() > 0) {
            for (QuickMoveNPC qmn : qm) {
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(qmn.getId());
                oPacket.EncodeInt(qmn.getType());
                oPacket.EncodeInt(qmn.getLevel());
                oPacket.EncodeString(qmn.getDescription());
                oPacket.EncodeLong(PacketHelper.getTime(-2));
                oPacket.EncodeLong(PacketHelper.getTime(-1));
            }
        }

        return oPacket;
    }

    public static OutPacket spawnPlayerMapObject(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEnterField.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(chr.getLevel());
        oPacket.EncodeString(chr.getName());
        QuestStatus ultExplorer = chr.getQuestNoAdd(Quest.getInstance(111111));
        if (ultExplorer != null && ultExplorer.getCustomData() != null) {
            oPacket.EncodeString(ultExplorer.getCustomData());
        } else {
            oPacket.EncodeString("");
        }
        MapleGuild gs = Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            oPacket.EncodeString(gs.getName());
            oPacket.EncodeShort(gs.getLogoBG());
            oPacket.EncodeByte(gs.getLogoBGColor());
            oPacket.EncodeShort(gs.getLogo());
            oPacket.EncodeByte(gs.getLogoColor());
        } else {
            oPacket.Encode(new byte[8]);
        }

        oPacket.EncodeByte(chr.getGender());
        oPacket.EncodeInt(chr.getFame());
        oPacket.EncodeInt(1); //m_nFarmLevel
        oPacket.EncodeInt(0); //m_nNameTagMark

        // Remove certain effects upon entering the map in order to avoid disconnects, for now. 
        Utility.removeBuffFromMap(chr, CharacterTemporaryStat.ShadowServant);
        Utility.removeBuffFromMap(chr, CharacterTemporaryStat.ShadowIllusion);

        BuffPacket.encodeForRemote(oPacket, chr);

        oPacket.EncodeShort(chr.getJob());
        oPacket.EncodeShort(chr.getSubcategory());
        oPacket.EncodeInt(chr.getStat().starForceEnhancement); //m_nTotalCHUC
        oPacket.EncodeInt(0);
        writeCharacterLook(oPacket, chr, true);
        oPacket.EncodeInt(0); // Unknown
        oPacket.EncodeInt(0); // Unknown

        // Begins a sub (sub_1158EC0) 188T
        oPacket.EncodeInt(0); // Unknown
        oPacket.EncodeInt(0); // Unknown
        oPacket.EncodeInt(0); // Unknown // Encode 2 ints per size
        // End Sub

        oPacket.EncodeInt(0); //m_dwDriverID
        oPacket.EncodeInt(0); //m_dwPassenserID
        oPacket.EncodeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //nChocoCount
        oPacket.EncodeInt(chr.getItemEffect()); //nActiveEffectItemID
        //oPacket.EncodeInt(0); //nMonkeyEffectItemID
        //MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(124000));
        //oPacket.EncodeInt(stat != null && stat.getCustomData() != null ? Integer.parseInt(stat.getCustomData()) : 0); //nActiveNickItemID (Title)
        oPacket.EncodeInt(0); //nDamageSkin
        oPacket.EncodeInt(0); //ptPos.x
        oPacket.EncodeInt(0); //nDemonWingID
        oPacket.EncodeInt(0); //nKaiserWingID
        oPacket.EncodeInt(0); //nKaiserTailID
        oPacket.EncodeInt(0); //m_nCompletedSetItemID
        oPacket.EncodeShort(-1); //m_nFieldSeatID
        oPacket.EncodeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0); //m_nPortableChairID
        oPacket.EncodeInt(0);// if (int > 0), decodestr
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0); // Unknown
        oPacket.EncodeShort(chr.getTruePosition().x);
        oPacket.EncodeShort(chr.getTruePosition().y);
        oPacket.EncodeByte(chr.getStance());
        oPacket.EncodeShort(chr.getFh());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0); // if this byte is > 0, do a loop and write an int for pet
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);//CAvatar::SetMechanicHUE
        oPacket.EncodeInt(chr.getMount().getLevel());
        oPacket.EncodeInt(chr.getMount().getExp());
        oPacket.EncodeInt(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(oPacket, chr);
        oPacket.EncodeBool(chr.getChalkboard() != null);

        if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
            oPacket.EncodeString(chr.getChalkboard());
        }

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(oPacket, rings.getLeft());
        addRingInfo(oPacket, rings.getMid());
        addMRingInfo(oPacket, rings.getRight(), chr);

        oPacket.EncodeByte(0);//mask
        oPacket.EncodeInt(0); //v4->m_nEvanDragonGlide_Riding

        if (GameConstants.isKaiser(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            oPacket.EncodeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            oPacket.EncodeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "primium");
            oPacket.EncodeByte(x == null ? 0 : Integer.parseInt(x));
        }

        oPacket.EncodeInt(0); //CUser::SetMakingMeisterSkillEff

        PacketHelper.addFarmInfo(oPacket, chr.getClient(), 0);
        for (int i = 0; i < 5; i++) {
            oPacket.EncodeByte(-1);
        }

        oPacket.EncodeInt(0); // LOOP: If int > 0, write a string
        oPacket.EncodeByte(1);

        // TO-DO: If it is a Honey Butterfly mount, write an  int. If that int > 0, loop and write an int on each iteration.
        oPacket.EncodeByte(0); // if byte > 0 && another byte, decode two ints, two shorts, something to do with 12101025 (Flashfire)
        oPacket.EncodeByte(0);//CUser::StarPlanetRank::Decode
        oPacket.EncodeInt(0); //sub_15ECDF0
        oPacket.EncodeInt(0); //CUser::DecodeTextEquipInfo
        oPacket.EncodeByte(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInt(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInt(0); //CUser::DecodeEventBestFriendInfo
        oPacket.EncodeByte(0); //CUserRemote::OnKinesisPsychicEnergyShieldEffect
        oPacket.EncodeByte(1);//asume this and the next int is some waterEvent shit
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(0);
        oPacket.EncodeString("");
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0); //sub_15B8CB0

        oPacket.Fill(0, 499);
        
        return oPacket;
    }

    /**
     * Jumps to writeCharacterLook() with mega display toggled off.
     *
     * @param oPacket OutPacket to construct the packet
     * @param chr The MapleCharacter to write the appearance of.
     */
    public static void writeCharacterLook(OutPacket oPacket, User chr) {
        writeCharacterLook(oPacket, chr, true);
    }

    /**
     * Writes the characters look(s)
     * <p>
     * Filters the Characters jobs and current states in order to write the currently activated character look for Zero/Ab. If no special
     * job is found, it writes the default CharacterLook.
     *
     * @param oPacket OutPacket to construct the packet
     * @param chr The MapleCharacter to write the appearance of.
     * @param mega Toggle mega display.
     */
    public static void writeCharacterLook(OutPacket oPacket, User chr, boolean mega) {
        if (GameConstants.isAngelicBuster(chr.getJob())) {
            if (chr.isAngelicDressupState()) {
                PacketHelper.addCharLook(oPacket, chr, mega, true);
            } else {
                PacketHelper.addCharLook(oPacket, chr, mega, false);
            }
        } else if (GameConstants.isZero(chr.getJob())) {
            if (chr.isZeroBetaState()) {
                PacketHelper.addCharLook(oPacket, chr, mega, true);
                PacketHelper.addCharLook(oPacket, chr, mega, false);
            } else {
                PacketHelper.addCharLook(oPacket, chr, mega, false);
                PacketHelper.addCharLook(oPacket, chr, mega, true);
            }
        } else {
            PacketHelper.addCharLook(oPacket, chr, mega, false);
        }
    }

    public static OutPacket removePlayerFromMap(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserLeaveField.getValue());
        oPacket.EncodeInt(cid);

        return oPacket;
    }

    public static OutPacket getChatText(int cidfrom, String text, boolean whiteBG, boolean appendToChatLogList) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserChat.getValue());
        oPacket.EncodeInt(cidfrom);
        oPacket.EncodeByte(whiteBG ? 1 : 0);
        oPacket.EncodeString(text);//
        oPacket.EncodeByte(appendToChatLogList ? 0 : 1); // Changed to the ! opposite on v176
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(-1);

        return oPacket;
    }

    /**
     * This packet shows the effect of a scroll to an item
     *
     * @param int chr - Character Id
     * @param ScrollResult scrollSuccess - the result of the the scroll
     * @param boolean legendarySpirit - if the player is using the legendary spirit skill
     * @param int item - the item id that is being scrolled
     * @param int scroll - the id of the scroll thats being used
     *
     * @return oPacket
     */
    public static OutPacket getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit, int item, int scroll) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemUpgradeEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeByte(scrollSuccess == ScrollResult.SUCCESS ? 1 : scrollSuccess == ScrollResult.CURSE ? 2 : 0);
        oPacket.EncodeBool(legendarySpirit);
        oPacket.EncodeInt(scroll);
        oPacket.EncodeInt(item);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    /**
     * This packet displays the effect after using a magnifying glass
     *
     * @param int chr - character id
     * @param short pos - position of the item to reveal
     *
     */
    public static OutPacket showMagnifyingEffect(int chr, short pos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemReleaseEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeShort(pos);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    /**
     * This packet shows the effect after the potential of an item has been reset
     *
     * @param int chr - character id
     * @param boolean success - if it succeeded
     * @param int itemid - the id of the item
     *
     * @return oPacket
     *
     */
    public static OutPacket showPotentialReset(int chr, boolean success, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemUnreleaseEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeBool(success);
        oPacket.EncodeInt(itemid);
        return oPacket;
    }

    /**
     * This packet displays the effect after using a lucky scroll
     *
     * @param int cId - Character Id
     * @param boolean success - if it succeeded
     * @param guess int itemId - I do not know if this is correct
     *
     * @return oPacket
     */
    public static OutPacket showLuckyEffect(int cId, boolean success, int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemLuckyItemEffect.getValue());
        oPacket.EncodeInt(cId);
        oPacket.EncodeBool(success);
        oPacket.EncodeInt(itemId); //guess
        return oPacket;
    }

    /**
     * This packet shows the effect after using a memorial cube
     *
     * @param int chr - character id
     * @param boolean success - if it succeeded
     * @param int cubeId - the id of the cube
     *
     * @return oPacket
     *
     */
    public static OutPacket showMemorialEffect(int chr, boolean success, int cubeId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemMemorialCubeEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeBool(success);
        oPacket.EncodeInt(cubeId);
        return oPacket;
    }

    /**
     * This packet shows the effect after resetting the bonus potential on a item
     *
     * @param int chr - character id
     * @param boolean success - if it succeeded
     * @param int cubeId - the id of the cube
     *
     * @return oPacket
     *
     */
    public static OutPacket showBonusPotentialReset(int chr, boolean success, int cubeId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserItemAdditionalUnReleaseEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeBool(success);
        oPacket.EncodeInt(cubeId);
        return oPacket;
    }

    public static OutPacket showNebuliteEffect(int chr, boolean success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ShowNebuliteEffect.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeBool(success);
        oPacket.EncodeString(success ? "Successfully mounted Nebulite." : "Failed to mount Nebulite.");

        return oPacket;
    }

    public static OutPacket useNebuliteFusion(int cid, int itemId, boolean success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SHOW_FUSION_EFFECT.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeBool(success);
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    public static OutPacket pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackMonster> attack) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserHitByUser.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(playerLevel);
        oPacket.EncodeInt(skill);
        oPacket.EncodeByte(skillLevel);
        oPacket.EncodeInt(linkSkill != skill ? linkSkill : 0);
        oPacket.EncodeByte(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        oPacket.EncodeByte(direction);
        oPacket.EncodeByte(movementSkill ? 1 : 0);
        oPacket.EncodeByte(pushTarget ? 1 : 0);
        oPacket.EncodeByte(pullTarget ? 1 : 0);
        oPacket.EncodeByte(0);
        oPacket.EncodeShort(stance);
        oPacket.EncodeByte(speed);
        oPacket.EncodeByte(mastery);
        oPacket.EncodeInt(projectile);
        oPacket.EncodeInt(chargeTime);
        oPacket.EncodeInt(range);
        oPacket.EncodeByte(attack.size());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(attackCount);
        oPacket.EncodeByte(0);

        for (AttackMonster p : attack) {
            oPacket.EncodeInt(p.getObjectId());
            oPacket.EncodeInt(0);
            oPacket.EncodePosition(p.getPosition());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(0);
            for (Pair<Long, Boolean> atk : p.getAttacks()) {
                oPacket.EncodeLong(atk.left);
                oPacket.EncodeBool(atk.right);
                oPacket.EncodeShort(0);
            }
        }

        return oPacket;
    }

    public static OutPacket getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldWeather_Add.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(mistSkill);
        oPacket.EncodeByte(mistLevel);
        oPacket.EncodeInt(damage);
        oPacket.EncodeByte(8);
        oPacket.EncodeInt(1000);

        return oPacket;
    }

    public static OutPacket pvpCool(int cid, List<Integer> attack) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserResetAllDot.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(attack.size());
        for (int i : attack) {
            oPacket.EncodeInt(i);
        }

        return oPacket;
    }

    public static OutPacket teslaTriangle(int cid, int sum1, int sum2, int sum3) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserTeslaTriangle.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(sum1);
        oPacket.EncodeInt(sum2);
        oPacket.EncodeInt(sum3);

        oPacket.Fill(0, 69);//test

        return oPacket;
    }

    public static OutPacket followEffect(int initiator, int replier, Point toMap) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserFollowCharacter.getValue());
        oPacket.EncodeInt(initiator);
        oPacket.EncodeInt(replier);
        oPacket.EncodeLong(0);
        if (replier == 0) {
            oPacket.EncodeByte(toMap == null ? 0 : 1);
            if (toMap != null) {
                oPacket.EncodeInt(toMap.x);
                oPacket.EncodeInt(toMap.y);
            }
        }

        return oPacket;
    }

    public static OutPacket showPQReward(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserShowPQReward.getValue());
        oPacket.EncodeInt(cid);
        for (int i = 0; i < 6; i++) {
            oPacket.EncodeByte(0);
        }

        return oPacket;
    }

    public static OutPacket craftMake(int cid, int something, int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMakingSkillResult.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(something);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMakingMeisterSkillEff.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(craftID);
        oPacket.EncodeInt(ranking);
        oPacket.EncodeInt(itemId);
        oPacket.EncodeInt(quantity);
        oPacket.EncodeInt(exp);

        return oPacket;
    }

    public static OutPacket harvestResult(int cid, boolean success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserGatherResult.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(success ? 1 : 0);

        return oPacket;
    }

    public static OutPacket playerDamaged(int cid, int dmg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserExplode.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(dmg);

        return oPacket;
    }

    public static OutPacket showPyramidEffect(int chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PyramidLethalAttack.getValue());
        oPacket.EncodeInt(chr);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket pamsSongEffect(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserWaitQueueReponse.getValue());
        oPacket.EncodeInt(cid);
        return oPacket;
    }

    public static OutPacket enableHaku(KannaHaku haku) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManExclResult.getValue());
        oPacket.EncodeInt(haku.getOwner());
        return oPacket;
    }

    public static OutPacket changeHakuEquip(KannaHaku haku, boolean change, boolean enableActions) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManModified.getValue());
        oPacket.EncodeInt(haku.getOwner());
        oPacket.EncodeBool(change);
        if (change) {
            oPacket.EncodeInt(haku.getEquipId());
        }
        oPacket.EncodeBool(enableActions);
        return oPacket;
    }

    public static OutPacket transformHaku(int cid, boolean change) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManShowChangeEffect.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(change ? 2 : 1);
        return oPacket;
    }

    /**
     * This packet will spawn Haku
     *
     * @param MapleHaku h
     * @param boolean oldForm - Haku form 0 is the new one 1 is the old one
     *
     * @return oPacket
     *
     */
    public static OutPacket spawnHaku(KannaHaku h, boolean oldForm) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManEnterField.getValue());
        oPacket.EncodeInt(h.getOwner());
        oPacket.EncodeShort(oldForm ? 1 : 0);  //value % map size
        oPacket.EncodePosition(h.getPosition());
        oPacket.EncodeByte(h.getStance());//m_nMoveAction
        oPacket.EncodeShort(h.getFootHold());
        oPacket.EncodeInt(0);//m_nUpgrade
        oPacket.EncodeInt(0);//m_anFoxManEquip[0]
        return oPacket;
    }

    public static OutPacket moveHaku(KannaHaku h, Point pos, List<LifeMovementFragment> res) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManMove.getValue());
        oPacket.EncodeInt(h.getOwner());

        PacketHelper.serializeMovementList(oPacket, h, res, 0);
        return oPacket;
    }

    public static OutPacket destroyHaku(int charId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FoxManMove.getValue());
        oPacket.EncodeInt(charId);
        return oPacket;
    }

    public static OutPacket spawnDragon(EvanDragon d) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DragonEnterField.getValue());
        oPacket.EncodeInt(d.getOwner());
        oPacket.EncodeInt(d.getPosition().x);
        oPacket.EncodeInt(d.getPosition().y);
        oPacket.EncodeByte(d.getStance());
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(d.getJobId());
        return oPacket;
    }

    public static OutPacket removeDragon(int chrid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DragonVanish_Script.getValue());
        oPacket.EncodeInt(chrid);
        return oPacket;
    }

    public static OutPacket moveDragon(EvanDragon d, Point startPos, List<LifeMovementFragment> moves) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DragonMove.getValue());
        oPacket.EncodeInt(d.getOwner());

        PacketHelper.serializeMovementList(oPacket, d, moves, 0);

        return oPacket;
    }

    public static OutPacket spawnAndroid(User chr, Android android) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AndroidEnterField.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(Android.getAndroidTemplateId(android.getItem().getItemId()));
        oPacket.EncodeShort(android.getPos().x);
        oPacket.EncodeShort(android.getPos().y - 20);
        oPacket.EncodeByte(android.getStance());
        oPacket.EncodeShort(chr.getFh());
        oPacket.EncodeShort(android.getSkin() - 2000);
        oPacket.EncodeShort(android.getHair() - 30000);
        oPacket.EncodeShort(android.getFace() - 20000);
        oPacket.EncodeString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            oPacket.EncodeInt(item != null ? item.getItemId() : 0);
        }

        return oPacket;
    }

    public static OutPacket moveAndroid(User chr, List<LifeMovementFragment> res) {

        Android android = chr.getAndroid();

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AndroidMove.getValue());
        oPacket.EncodeInt(chr.getId());

        PacketHelper.serializeMovementList(oPacket, android, res, 0);

        return oPacket;
    }

    public static OutPacket showAndroidEmotion(int cid, byte emo1) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AndroidActionSet.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(0);//nActingSet
        oPacket.EncodeByte(emo1);
        return oPacket;
    }

    public static OutPacket updateAndroidLook(boolean itemOnly, User cid, Android android, boolean enableActions) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AndroidModified.getValue());
        oPacket.EncodeInt(cid.getId());
        oPacket.EncodeByte(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i = (short) (i - 1)) {
                Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                oPacket.EncodeInt(item != null ? item.getItemId() : 0);
            }
        } else {
            oPacket.EncodeShort(0);
            oPacket.EncodeShort(android.getHair() - 30000);
            oPacket.EncodeShort(android.getFace() - 20000);
            oPacket.EncodeString(android.getName());
        }
        oPacket.EncodeBool(enableActions);
        return oPacket;
    }

    public static OutPacket deactivateAndroid(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AndroidLeaveField.getValue());
        oPacket.EncodeInt(cid);
        return oPacket;
    }

    public static OutPacket removeFamiliar(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarEnterField.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeShort(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket spawnFamiliar(MonsterFamiliar mf, boolean spawn, boolean respawn) {

        OutPacket oPacket = new OutPacket(respawn ? SendPacketOpcode.FamiliarTransferField.getValue() : SendPacketOpcode.FamiliarEnterField.getValue());
        oPacket.EncodeInt(mf.getCharacterId());
        oPacket.EncodeByte(spawn ? 1 : 0);
        oPacket.EncodeByte(respawn ? 1 : 0);
        oPacket.EncodeByte(0);
        if (spawn) {
            oPacket.EncodeInt(mf.getFamiliar());
            oPacket.EncodeInt(mf.getFatigue());
            oPacket.EncodeInt(mf.getVitality() * 300); //max fatigue
            oPacket.EncodeString(mf.getName());
            oPacket.EncodePosition(mf.getTruePosition());
            oPacket.EncodeByte(mf.getStance());
            oPacket.EncodeShort(mf.getFh());
        }

        return oPacket;
    }

    /**
     * This packet handles the movement for Familiars
     *
     * @param MonsterFamiliar - fam
     * @param List<LifeMovementFragment> - moves
     *
     * @return oPacket
     */
    public static OutPacket moveFamiliar(MonsterFamiliar fam, List<LifeMovementFragment> moves) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarMove.getValue());
        oPacket.EncodeInt(fam.getCharacterId());
        oPacket.EncodeByte(0); //idk
        PacketHelper.serializeMovementList(oPacket, fam, moves, 0);

        return oPacket;
    }

    /**
     * This packet handles the movement for Familiars
     *
     * @param MonsterFamiliar - fam
     * @param List<LifeMovementFragment> - moves
     *
     * @return oPacket
     */
    public static OutPacket touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarAction.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(unk);
        oPacket.EncodeInt(objectid); //possibly mobid or oid for mob
        oPacket.EncodeInt(type);
        oPacket.EncodeInt(delay);
        oPacket.EncodeInt(damage); //

        return oPacket;
    }

    /**
     * This packet sends the familiar attack information to the client
     *
     * @param int cid- character Id
     * @param byte unk - we do not know yet
     * @param List<Triple<Integer, Integer, List<Integer>>> attackPair - the attack / damage information
     *
     * @return oPacket
     */
    public static OutPacket familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarAttack.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(0);// idk
        oPacket.EncodeByte(unk);
        oPacket.EncodeByte(attackPair.size());
        for (Triple<Integer, Integer, List<Integer>> s : attackPair) {
            oPacket.EncodeInt(s.left);
            oPacket.EncodeByte(s.mid);
            oPacket.EncodeByte(s.right.size());
            for (int damage : s.right) {
                oPacket.EncodeInt(damage);
            }
        }

        return oPacket;
    }

    public static OutPacket renameFamiliar(MonsterFamiliar mf) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarNameResult.getValue());
        oPacket.EncodeInt(mf.getCharacterId());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(mf.getFamiliar());
        oPacket.EncodeString(mf.getName());

        return oPacket;
    }

    public static OutPacket updateFamiliar(MonsterFamiliar mf) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarFatigueResult.getValue());
        oPacket.EncodeInt(mf.getCharacterId());
        oPacket.EncodeInt(mf.getFamiliar());
        oPacket.EncodeInt(mf.getFatigue());
        oPacket.EncodeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2L));

        return oPacket;
    }

    public static OutPacket movePlayer(User chr, List<LifeMovementFragment> moves) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMove.getValue());
        oPacket.EncodeInt(chr.getId());
        PacketHelper.serializeMovementList(oPacket, chr, moves, 0);

        oPacket.Fill(0, 19);
        
        return oPacket;
    }

    // <editor-fold defaultstate="visible" desc="Third party attack display"> 
    public static OutPacket closeRangeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, boolean energy, int lvl, byte mastery, byte unk, int charge) {
        return addAttackInfo(energy ? 4 : 0, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, 0, null, 0);
    }

    public static OutPacket rangedAttack(int cid, int tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackMonster> damage, Point pos, int lvl, byte mastery, byte unk) {
        return addAttackInfo(1, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, 0);
    }

    public static OutPacket strafeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackMonster> damage, Point pos, int lvl, byte mastery, byte unk, int ultLevel) {
        return addAttackInfo(2, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, ultLevel);
    }

    public static OutPacket magicAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, int charge, int lvl, byte unk) {
        return addAttackInfo(3, cid, tbyte, skill, level, display, speed, damage, lvl, (byte) 0, unk, charge, null, 0);
    }

    public static OutPacket addAttackInfo(int type, int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, int lvl, byte mastery, byte unk, int charge, Point pos, int ultLevel) {

        OutPacket oPacket;
        switch (type) {
            case 0:
                oPacket = new OutPacket(SendPacketOpcode.UserMeleeAttack.getValue());
                break;
            case 1:
            case 2:
                oPacket = new OutPacket(SendPacketOpcode.UserShootAttack.getValue());
                break;
            case 3:
                oPacket = new OutPacket(SendPacketOpcode.UserMagicAttack.getValue());
                break;
            default:
                oPacket = new OutPacket(SendPacketOpcode.UserBodyAttack.getValue());
                break;
        }

        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(tbyte);
        oPacket.EncodeByte(lvl);
        oPacket.EncodeByte(level);
        if (level > 0) {
            oPacket.EncodeInt(skill);
        }

        if (Skill.isZeroSkill(skill)) {
            short zero1 = 0;
            short zero2 = 0;
            oPacket.EncodeBool(zero1 > 0 || zero2 > 0); //boolean
            if (zero1 > 0 || zero2 > 0) {
                oPacket.EncodeShort(zero1);
                oPacket.EncodeShort(zero2);
                //there is a full handler so better not write zero
            }
        }

        if (type == 2) {
            oPacket.EncodeByte(ultLevel);
            if (ultLevel > 0) {
                oPacket.EncodeInt(3220010); // this should be 5XXXXXX fuckwit
            }
        }

        if (skill == 80001850) {
            oPacket.EncodeByte(0); //boolean if true then int
        }
        if ((skill == 42001000 || skill > 42001004 && skill <= 42001006) || (skill == 40021185 || skill == 42001006 || skill == 80011067)) {
            oPacket.EncodeByte(0); // Unknown
            // if above > 0 encode int
        }

        oPacket.EncodeByte(0); //v20
        // nTime = v20 & 0x20
        // bRepeatAttack = v20 & 4
        // bShadowPartner = v20 & 8

        oPacket.EncodeByte(0); //v22
        oPacket.EncodeInt(0); //nOption3
        oPacket.EncodeInt(0); //nBySummonedID

        /*
        if ((v22 & 2) != 0) { //bBuckShot
            oPacket.EncodeInt(0);//nSkillID
            oPacket.EncodeInt(0);//nSkillLvl
        }

        if ((v22 & 8) != 0) {
            oPacket.encode(0);//nPassiveAttackCount
        }
         */
        
        oPacket.EncodeShort(display);
        if (display <= 1616) {
            oPacket.EncodeByte(-1); //v30 bDragon ataack and move action?
            oPacket.EncodeShort(0); // ptAttackRefPoint.x (Dragon specific)
            oPacket.EncodeShort(0); // ptAttackRefPoint.y (Dragon specific)
            oPacket.EncodeByte(0); //bShowFixedDamage
            oPacket.EncodeByte(6); //v206
            oPacket.EncodeByte(speed); //nActionSpeed
            oPacket.EncodeByte(mastery); //nMastery
            oPacket.EncodeInt(charge); //nBulletItemID 
            for (AttackMonster monster : damage) {
                if (monster.getAttacks() != null) {
                    oPacket.EncodeInt(monster.getObjectId());
                    oPacket.EncodeByte(0);// hitacton
                    oPacket.EncodeByte(0);// bleft
                    oPacket.EncodeByte(0);//v38[1]
                    oPacket.EncodeShort(0);//tdelay
                    if (skill == 80001835 || skill == 42111002 || skill == 80011050) {
                        for (Pair<Long, Boolean> hits : monster.getAttacks()) {
                            oPacket.EncodeLong(hits.left); //Iterate over damage.
                        }
                    } else {
                        for (Pair<Long, Boolean> hits : monster.getAttacks()) {
                            oPacket.EncodeBool(hits.right);//bCrit
                            oPacket.EncodeLong(hits.left); //Iterate over damage.
                        }
                    }
                    if (Kinesis.is_kinesis_psychiclock_skill(skill)) {
                        oPacket.EncodeInt(0);//idk
                    }

                    if (skill == Blaster.ROCKET_RUSH) {
                        oPacket.EncodeByte(0);
                    }
                }
            }
            if (skill == 2321001 || skill == 2221052 || skill == 11121052 || skill == 12121054) { //Keydown skills
                oPacket.EncodeInt(0); //tKeyDown
            } else if (Skill.isSupernovaSkill(skill) || Skill.isScreenCenterAttackSkill(skill) || skill == 101000202 || skill == 101000102
                    || skill == 80001762 || skill == 80002212 || skill == 400041019 || skill == 400031016 || skill == 400041024) {
                oPacket.EncodeInt(0);//attackRefPointx
                oPacket.EncodeInt(0);//attackRefPointy
            }

            //if is_keydown_skill_rect_move_xyz : encode new position (2 shorts)
            if (Skill.isKeydownSkillRectMoveXY(skill)) {
                oPacket.EncodeShort(0); 
                oPacket.EncodeShort(0); 
            }
            
            if (skill == 51121009) {
                oPacket.EncodeByte(0); //bEncodeFixedDamage
            } else if (skill == 112110003) {
                oPacket.EncodeInt(0);
            } else if (skill == 42100007) {
                oPacket.EncodeShort(0);
                oPacket.EncodeByte(0);
            }

            if (skill == 21120019 || skill == 37121052 || skill >= 400041002 && skill <= 400041005 || skill == 11121014 || skill == 5101004) {
                oPacket.EncodeInt(0); // pTeleport.pt.x
                oPacket.EncodeInt(0); // pTeleport.pt.y
            }
            if (skill == 400020009 || skill == 400020010 || skill == 400020011 || skill == 400021029 || skill == 400021053) {
                oPacket.EncodeShort(0);
                oPacket.EncodeShort(0);
            }
            if (Skill.IsUnknown5thJobFunc(skill)) {
                oPacket.EncodeInt(0); // Unknown
                oPacket.EncodeBool(false); // Unknown
            }
        }
        
        //oPacket.Fill(0, 29); // Please for the love of god.
        
        return oPacket;
    }
// </editor-fold>

    public static OutPacket skillEffect(User from, int skillId, byte level, short display, byte unk) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSkillPrepare.getValue());
        oPacket.EncodeInt(from.getId());
        oPacket.EncodeInt(skillId);
        oPacket.EncodeByte(level);
        oPacket.EncodeShort(display);
        oPacket.EncodeByte(unk);
        oPacket.EncodePosition(from.getPosition()); // Position
        return oPacket;
    }

    public static OutPacket skillCancel(User from, int skillId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSkillCancel.getValue());
        oPacket.EncodeInt(from.getId());
        oPacket.EncodeInt(skillId);

        return oPacket;
    }

    /**
     * CUserRemote::OnHit
     *
     * This method deals with damage being done to the player
     *
     */
    public static OutPacket damagePlayer(int cid, PlayerDamageHandler.PlayerDamageType type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserHit.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(type.getType());
        oPacket.EncodeInt(damage);
        oPacket.EncodeByte(0); //bCritical
        //oPacket.encode(0); //goes to CUser::MakeIncDecHPEffect REMOVED v176+ ?
        if (type.getType() >= -1) {
            oPacket.EncodeInt(monsteridfrom);
            oPacket.EncodeByte(direction);
            oPacket.EncodeInt(skillid);
            oPacket.EncodeInt(pDMG);
            oPacket.EncodeByte(0);
            if (pDMG > 0) {
                oPacket.EncodeByte(pPhysical ? 1 : 0);
                oPacket.EncodeInt(pID);
                oPacket.EncodeByte(pType);
                oPacket.EncodePosition(pPos);
            }
            oPacket.EncodeByte(offset);
            if (offset == 1) {
                oPacket.EncodeInt(offset_d);
            }
        }
        oPacket.EncodeInt(damage);
        if ((damage <= 0) || (fake > 0)) {
            oPacket.EncodeInt(fake);
        }

        return oPacket;
    }

    public static OutPacket facialExpression(User from, int expression) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEmotion.getValue());
        oPacket.EncodeInt(from.getId());
        oPacket.EncodeInt(expression);
        oPacket.EncodeInt(-1);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    /**
     * This packet deals with the facial expression of the player
     *
     * @param int expression
     * @param int duration
     * @return OutPacket Facial Expressions: 0 - Normal 1 - F1 2 - F2 3 - F3 4 - F4 5 - F5 6 - F6 7 - F7 8 - Vomit 9 - Panic 10 - Sweetness
     * 11 - Kiss 12 - Wink 13 - Ouch! 14 - Goo goo eyes 15 - Blaze 16 - Star 17 - Love 18 - Ghost 19 - Constant Sigh 20 - Sleepy 21 -
     * Flaming hot 22 - Bleh 23 - No Face
     */
    public static OutPacket directionFacialExpression(int expression, int duration) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEmotionLocal.getValue());
        oPacket.EncodeInt(expression);
        oPacket.EncodeInt(duration);
        oPacket.EncodeByte(0);
        return oPacket;
    }

    public static OutPacket itemEffect(int characterid, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActiveEffectItem.getValue());
        oPacket.EncodeInt(characterid);
        oPacket.EncodeInt(itemid);

        return oPacket;
    }

    public static OutPacket showTitle(int characterid, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActiveNickItem.getValue());
        oPacket.EncodeInt(characterid);
        oPacket.EncodeInt(itemid);

        return oPacket;
    }

    public static OutPacket showAngelicBusterTransformation(int characterid, int effectId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetDressUpState.getValue());
        oPacket.EncodeInt(characterid);
        oPacket.EncodeInt(effectId);

        return oPacket;
    }

    public static OutPacket setAngelicBusterTransformation(int bSet, int infinite) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetDressUpState.getValue());
        oPacket.EncodeByte(bSet);
        oPacket.EncodeByte(infinite);

        return oPacket;
    }

    public static OutPacket OnShowChair(int nCharacterID, int nChairID) {
        return OnShowChair(nCharacterID, nChairID, 0, 0);
    }

    public static OutPacket OnShowChair(int nCharacterID, int nChairID, int nTowerChair, int bMessage) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActivePortableChair.getValue());

        oPacket.EncodeInt(nCharacterID);
        oPacket.EncodeInt(nChairID);

        oPacket.EncodeInt(bMessage);
        if (bMessage > 0) {
            oPacket.EncodeString(""); // sMessage
        }

        oPacket.EncodeInt(nTowerChair);
        if (nTowerChair > 0) {
            oPacket.EncodeInt(0); // nTowerChairSize
        }

        oPacket.EncodeInt(0); // nMesoChairCount

        oPacket.EncodeInt(0); // nUnk GMS
        oPacket.EncodeInt(0); // nUnk GMS

        return oPacket;
    }

    public static OutPacket showChair(int characterid, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetActivePortableChair.getValue());
        oPacket.EncodeInt(characterid);
        oPacket.EncodeInt(itemid);

        oPacket.EncodeInt(0); // bPortableMessage
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0); // lTowerChair.size
        oPacket.EncodeBool(false);
        oPacket.EncodeInt(0);
        oPacket.EncodeBool(false);
        
        oPacket.Fill(0, 29);

        return oPacket;
    }

    public static OutPacket updateCharLook(User chr, boolean second) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserAvatarModified.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(1);
        PacketHelper.addCharLook(oPacket, chr, false, second);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(oPacket, rings.getLeft());
        addRingInfo(oPacket, rings.getMid());
        addMRingInfo(oPacket, rings.getRight(), chr);
        oPacket.EncodeInt(0); // completedSetItemID
        oPacket.EncodeInt(0); // totalCHUC
        oPacket.EncodeInt(0);
        return oPacket;
    }

    public static OutPacket updatePartyMemberHP(int cid, int curhp, int maxhp) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserHP.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(curhp);
        oPacket.EncodeInt(maxhp);

        return oPacket;
    }

    public static OutPacket changeTeam(int cid, int type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserPvPTeamChanged.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(type);

        return oPacket;
    }

    public static OutPacket showHarvesting(int cid, int tool) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GatherActionSet.getValue());
        oPacket.EncodeInt(cid);
        if (tool > 0) {
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(0);
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(tool);
            oPacket.Fill(0, 30);
        } else {
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 33);
        }

        return oPacket;
    }

    public static OutPacket getPVPHPBar(int cid, int hp, int maxHp) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UpdatePvPHPTag.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(hp);
        oPacket.EncodeInt(maxHp);

        return oPacket;
    }

    public static OutPacket cancelChair(int chrId, int id) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSitResult.getValue());
        oPacket.EncodeInt(chrId);
        if (id == -1) {
            oPacket.EncodeByte(0);
        } else {
            oPacket.EncodeByte(1);
            oPacket.EncodeShort(id);
        }

        return oPacket;
    }

    public static OutPacket instantMapWarp(byte portal) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserTeleport.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(portal);

        return oPacket;
    }

    public static OutPacket updateQuestInfo(User c, int quest, int npc, byte progress) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserQuestResult.getValue());
        oPacket.EncodeByte(progress);
        oPacket.EncodeInt(quest);
        oPacket.EncodeInt(npc);
        oPacket.EncodeInt(0);

        if (c.isIntern()) {
            c.dropMessage(5, "[Quest Debug] Updating Quest ID : " + quest);
        }

        return oPacket;
    }

    public static OutPacket updateQuestFinish(int quest, int npc, int nextquest) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserQuestResult.getValue());
        oPacket.EncodeByte(11);//was 10
        oPacket.EncodeInt(quest);  // Version 174, this is an integer 
        oPacket.EncodeInt(npc);
        oPacket.EncodeInt(nextquest);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket sendHint(String hint, int width, int height) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserBalloonMsg.getValue()); // todo guesss?
        oPacket.EncodeString(hint);
        oPacket.EncodeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
        oPacket.EncodeShort(Math.max(height, 5));
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket updateCombo(int value) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ModCombo.getValue());
        oPacket.EncodeInt(value);

        return oPacket;
    }

    public static OutPacket rechargeCombo(int value) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.IncComboByComboRecharge.getValue());
        oPacket.EncodeInt(value);

        return oPacket;
    }

    public static OutPacket getFollowMessage(String msg) {
        return getGameMessage(msg, (short) 11);
    }

    public static OutPacket getGameMessage(String msg, short colour) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserChatMsg.getValue());
        oPacket.EncodeShort(colour);
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket getBuffZoneEffect(int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserBuffzoneEffect.getValue());
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    public static OutPacket getTimeBombAttack() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserTimeBombAttack.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(10);
        oPacket.EncodeInt(6);

        return oPacket;
    }

    public static OutPacket moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserPassiveMove.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodePosition(otherStart);
        oPacket.EncodePosition(myStart);
        PacketHelper.serializeMovementList(oPacket, null, moves, 0);
        oPacket.EncodeByte(17);
        for (int i = 0; i < 8; i++) {
            oPacket.EncodeByte(0);
        }
        oPacket.EncodeByte(0);
        oPacket.EncodePosition(otherEnd);
        oPacket.EncodePosition(otherStart);
        oPacket.Fill(0, 100);

        return oPacket;
    }//CUser::OnPassiveMove

    public static OutPacket getFollowMsg(int opcode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserFollowCharacterFailed.getValue());
        oPacket.EncodeLong(opcode);

        return oPacket;
    }

    public static OutPacket registerFamiliar(MonsterFamiliar mf) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FamiliarRegister.getValue());
        oPacket.EncodeLong(mf.getId());
        mf.writeRegisterPacket(oPacket, false);
        oPacket.EncodeShort(mf.getVitality() >= 3 ? 1 : 0);

        return oPacket;
    }

    public static OutPacket createUltimate(int amount) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CreateNewCharacterResultPremiumAdventurer.getValue());
        oPacket.EncodeInt(amount);

        return oPacket;
    }

    public static OutPacket harvestMessage(int oid, int msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GatherRequestResult.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(msg);

        return oPacket;
    }

    public static OutPacket openBag(int index, int itemId, boolean firstTime) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserBagItemUseResult.getValue());
        oPacket.EncodeInt(index);
        oPacket.EncodeInt(itemId);
        oPacket.EncodeShort(firstTime ? 1 : 0);

        return oPacket;
    }

    public static OutPacket dragonBlink(int portalId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RandomTeleportKey.getValue());
        oPacket.EncodeByte(portalId);

        return oPacket;
    }

    /**
     * Single skill cooldown
     *
     * @param skillid
     * @param time
     * @return the packet
     */
    public static OutPacket skillCooldown(int skillid, int time) {
        List<Pair<Integer, Integer>> cooldowns = new ArrayList<Pair<Integer, Integer>>();
        cooldowns.add(new Pair<Integer, Integer>(skillid, time));

        return skillCooldown(cooldowns);
    }

    /**
     * An array of cooldowns to notify
     *
     * @param cooldowns An array of skills to sent, pair left = skillid & pair right = time in seconds
     * @return
     */
    public static OutPacket skillCooldown(List<Pair<Integer, Integer>> cooldowns) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SkillCooltimeSet.getValue());

        oPacket.EncodeInt(cooldowns.size()); // number of skills to cooldown, new in v170
        for (Pair<Integer, Integer> cooldown : cooldowns) {
            oPacket.EncodeInt(cooldown.getLeft());
            oPacket.EncodeInt(cooldown.getRight());
        }
        return oPacket;
    }

    public static OutPacket dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte nEnterType) {
        return dropItemFromMapObject(drop, null, dropfrom, dropto, nEnterType);
    }

    public static OutPacket dropItemFromMapObject(MapleMapItem drop, Mob mob, Point dropfrom, Point dropto, byte nEnterType) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DropEnterField.getValue());
        oPacket.EncodeByte(0); //eDropType
        oPacket.EncodeByte(nEnterType); //nEnterType
        oPacket.EncodeInt(drop.getObjectId()); //dwId
        oPacket.EncodeBool(drop.getMeso() > 0); //bIsMoney
        oPacket.EncodeInt(0); //nDropMotionType
        oPacket.EncodeInt(0); //nDropSpeed
        oPacket.EncodeBool(drop.isNoMoveItem()); //bNoMove
        oPacket.Fill(0, 3);
        oPacket.EncodeInt(drop.getItemId()); //nInfo
        oPacket.EncodeInt(drop.getOwner()); //dwOwnerID
        oPacket.EncodeByte(drop.getDropType()); //nOwnType
        oPacket.EncodePosition(dropto);
        oPacket.EncodeInt(mob != null ? mob.getId() : 0); //dwSourceID
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        if (nEnterType != 2) {
            oPacket.EncodePosition(dropfrom);
            oPacket.EncodeInt(0);//tDelay
        }
        oPacket.EncodeByte(0); //bExplosiveDrop
        /* 
        if (pFakeMoney)
	        oPacket.encode(0); //bByPet
	        oPacket.encode(0);
	        
	         if ( CInPacket::Decode1(iPacket) ) {
		          pra[0] = -1073471723;
		          v85 = _com_ptr_t<_com_IIID<IWzGr2DLayer,&__s_GUID const _GUID_6dc8c7ce_8e81_4420_b4f6_4b60b7d5fcdf>>::operator->(&v8->pLayer);
		          IWzGr2DLayer::Putz(v85, pra[0]);
		        } 
	         
	        oPacket.encodeShort(0); //nFallingVY
	        oPacket.encode(0); //v8->bFadeInEffect = CInPacket::Decode1(iPacket) != 0;
	        oPacket.encode(0); //nMakeType
	        oPacket.EncodeInt(0); //bCollisionPickUp
	        oPacket.encode(0);
	        oPacket.encode(0); //bPrepareCollisionPickUp
         */
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(oPacket, drop.getItem().getExpiration());
        }
        oPacket.EncodeBool(!drop.isPlayerDrop()); //bByPet
        oPacket.EncodeByte(0);

        /*
        if ( CInPacket::Decode1(iPacket) ) {
	          pra[0] = -1073471723;
	          v85 = _com_ptr_t<_com_IIID<IWzGr2DLayer,&__s_GUID const _GUID_6dc8c7ce_8e81_4420_b4f6_4b60b7d5fcdf>>::operator->(&v8->pLayer);
	          IWzGr2DLayer::Putz(v85, pra[0]);
	        } 
         */
        oPacket.EncodeShort(0); //nFallingVY
        oPacket.EncodeByte(0); //v8->bFadeInEffect = CInPacket::Decode1(iPacket) != 0;
        oPacket.EncodeByte(0); //nMakeType
        oPacket.EncodeBool(drop.isCollisionPickUpDrop()); // bCollisionPickUp
        oPacket.Fill(0, 3);
        oPacket.EncodeByte(0); //goes to a swtich with four possibilities
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket explodeDrop(int oid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DropLeaveField.getValue());
        oPacket.EncodeByte(4);
        oPacket.EncodeInt(oid);
        oPacket.EncodeShort(655);

        return oPacket;
    }

    public static OutPacket removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static OutPacket removeItemFromMap(int oid, int animation, int cid, int slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DropLeaveField.getValue());
        oPacket.EncodeByte(animation);
        oPacket.EncodeInt(oid);
        if (animation >= 2) {
            oPacket.EncodeInt(cid);
            if (animation == 5) {
                oPacket.EncodeInt(slot);
            }
        }
        return oPacket;
    }

    public static OutPacket spawnClockMist(final Mist clock) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.AffectedAreaCreated.getValue());

        oPacket.EncodeInt(clock.getObjectId());
        oPacket.EncodeByte(1);//bMobOrigin
        oPacket.EncodeInt(clock.getMobOwner().getObjectId());//nProp
        oPacket.EncodeInt(clock.getMobSkill().getSkillId());
        oPacket.EncodeByte(clock.getClockType());
        oPacket.EncodeShort(clock.getMobSkill().getEffectDelay());
        oPacket.EncodeInt(0);//nUnk, new or for clocks specifically.
        oPacket.EncodeInt(clock.getBox().x);
        oPacket.EncodeInt(clock.getBox().y);
        oPacket.EncodeInt(clock.getBox().x + clock.getBox().width);
        oPacket.EncodeInt(clock.getBox().y + clock.getBox().height);
        oPacket.EncodeInt(0);//bFail
        oPacket.EncodePosition(clock.getMobOwner().getPosition());//mobForce.nMobPosX
        oPacket.EncodeInt(0);//nForce
        oPacket.EncodeInt(clock.getClockType() == 1 ? 15 : clock.getClockType() == 2 ? -15 : 0);//dwOption clock.getClockType() == 1 ? 15 : clock.getClockType() == 2 ? -15 : 0
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.Fill(0, 69);//could be removed probably
        //System.out.println(packet.toString());

        return oPacket;
    }

    public static OutPacket spawnObtacleAtomBomb() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ObtacleAtomCreate.getValue());

        //Number of bomb objects to spawn.  You can also just send multiple packets instead of putting them all in one packet.
        oPacket.EncodeInt(500);

        //Unknown, this part is from IDA.
        byte unk = 0;
        oPacket.EncodeByte(unk); //animation data or some shit
        if (unk == 1) {
            oPacket.EncodeInt(300); //from Effect.img/BasicEff/ObtacleAtomCreate/%d
            oPacket.EncodeByte(0); //rest idk
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }

        oPacket.EncodeByte(1);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(900); //POSX
        oPacket.EncodeInt(-1347); //POSY
        oPacket.EncodeInt(25);
        oPacket.EncodeInt(3);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(25);
        oPacket.EncodeInt(-5);
        oPacket.EncodeInt(1000);
        oPacket.EncodeInt(800);
        oPacket.EncodeInt(80);
        return oPacket;
    }

    /**
     * Spawns a mist in the affected map area ?OnAffectedAreaCreated@CAffectedAreaPool@@IAEXAAVCInPacket@@@Z
     *
     * @param mist
     * @return
     */
    public static OutPacket spawnMist(Mist mist) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AffectedAreaCreated.getValue());
        oPacket.EncodeInt(mist.getObjectId());

        oPacket.EncodeByte(0);
        //oPacket.Encode(0);
        oPacket.EncodeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            oPacket.EncodeInt(mist.getSourceSkill().getId());
        } else {
            oPacket.EncodeInt(mist.getMobSkill().getSkillId());
        }
        oPacket.EncodeByte(mist.getSkillLevel());
        oPacket.EncodeShort(mist.getSkillDelay());
        oPacket.EncodeInt(mist.getBox().x);
        oPacket.EncodeInt(mist.getBox().y);
        oPacket.EncodeInt(mist.getBox().x + mist.getBox().width);
        oPacket.EncodeInt(mist.getBox().y + mist.getBox().height);
        oPacket.EncodeInt(mist.isShelter() ? 1 : 0);
        oPacket.EncodeInt(0);
        oPacket.EncodePosition(mist.getPosition());
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        if (GameConstants.isFlipAffectedAreaSkill(mist.getSourceSkill().getId())) {
            oPacket.EncodeByte(0);
        }
        oPacket.EncodeInt(0);

        return oPacket;
    }

    /**
     * Remove the mist from the map ?OnAffectedAreaRemoved@CAffectedAreaPool@@IAEXAAVCInPacket@@@Z
     *
     * @param oid
     * @param eruption
     * @return
     */
    public static OutPacket removeMist(int oid, boolean eruption) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AffectedAreaRemoved.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(eruption ? 1 : 0);

        return oPacket;
    }

    /**
     * Creates a mystical town portal
     *
     * @param oid
     * @param pos
     * @param originalCharacterPosition
     * @param animation
     * @param skillid
     * @return
     */
    public static OutPacket spawnDoor(int oid, Point pos, Point originalCharacterPosition, boolean animation, int skillid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TownPortalCreated.getValue());
        oPacket.EncodeByte(animation ? 0 : 1);
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(skillid); // new on v170
        oPacket.EncodePosition(pos);
        oPacket.EncodePosition(originalCharacterPosition);// new in v170, character position

        return oPacket;
    }

    public static OutPacket removeDoor(int oid, boolean animation) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TownPortalRemoved.getValue());
        oPacket.EncodeByte(animation ? 0 : 1);
        oPacket.EncodeInt(oid);

        return oPacket;
    }

    public static OutPacket spawnKiteError() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CreateMessageBoxFailed.getValue());

        return oPacket;
    }

    public static OutPacket spawnKite(Kite kite) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MessageBoxEnterField.getValue());
        oPacket.EncodeInt(kite.getObjectId());
        oPacket.EncodeInt(kite.getItemID());
        oPacket.EncodeString(kite.getMessage());
        oPacket.EncodeString(kite.getName());
        oPacket.EncodePosition(kite.getPosition());

        return oPacket;
    }

    public static OutPacket destroyKite(int oid, boolean animation) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MessageBoxLeaveField.getValue());
        oPacket.EncodeByte(animation ? 0 : 1);
        oPacket.EncodeInt(oid);

        return oPacket;
    }

    public static OutPacket spawnMechDoor(MechDoor md, boolean animated) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenGateCreated.getValue());
        oPacket.EncodeByte(animated ? 0 : 1);
        oPacket.EncodeInt(md.getOwnerId());
        oPacket.EncodePosition(md.getTruePosition());
        oPacket.EncodeByte(md.getId());
        oPacket.EncodeInt(md.getPartyId());
        return oPacket;
    }

    public static OutPacket removeMechDoor(MechDoor md, boolean animated) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenGateClose.getValue());
        oPacket.EncodeByte(animated ? 0 : 1);
        oPacket.EncodeInt(md.getOwnerId());
        oPacket.EncodeByte(md.getId());

        return oPacket;
    }

    public static OutPacket triggerReactor(Reactor reactor, int stance) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ReactorChangeState.getValue());
        oPacket.EncodeInt(reactor.getObjectId());
        oPacket.EncodeByte(reactor.getState());
        oPacket.EncodePosition(reactor.getTruePosition());
        oPacket.EncodeInt(stance);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        return oPacket;
    }

    public static OutPacket spawnReactor(Reactor reactor) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ReactorEnterField.getValue());
        oPacket.EncodeInt(reactor.getObjectId());
        oPacket.EncodeInt(reactor.getReactorId());
        oPacket.EncodeByte(reactor.getState());
        oPacket.EncodePosition(reactor.getTruePosition());
        oPacket.EncodeByte(reactor.getFacingDirection());
        oPacket.EncodeString(reactor.getName());

        return oPacket;
    }

    public static OutPacket destroyReactor(Reactor reactor) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ReactorStateReset.getValue());
        oPacket.EncodeInt(reactor.getObjectId());
        oPacket.EncodeByte(reactor.getState());
        oPacket.EncodePosition(reactor.getPosition());

        return oPacket;
    }

    public static OutPacket makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DecomposerEnterField.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeString(cname);
        oPacket.EncodeInt(pos.x);
        oPacket.EncodeInt(pos.y);
        oPacket.EncodeShort(timeLeft);
        oPacket.EncodeInt(itemId);
        oPacket.EncodeInt(fee);

        return oPacket;
    }

    public static OutPacket removeExtractor(int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DecomposerLeaveField.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(1);

        return oPacket;
    }

    public static OutPacket rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SnowBallState.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        oPacket.EncodeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        oPacket.EncodeShort(ball1 == null ? 0 : ball1.getPosition());
        oPacket.EncodeByte(0);
        oPacket.EncodeShort(ball2 == null ? 0 : ball2.getPosition());
        oPacket.Fill(0, 11);

        return oPacket;
    }

    public static OutPacket enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static OutPacket hitSnowBall(int team, int damage, int distance, int delay) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SnowBallHit.getValue());
        oPacket.EncodeByte(team);
        oPacket.EncodeShort(damage);
        oPacket.EncodeByte(distance);
        oPacket.EncodeByte(delay);

        return oPacket;
    }

    public static OutPacket snowballMessage(int team, int message) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SnowBallMsg.getValue());
        oPacket.EncodeByte(team);
        oPacket.EncodeInt(message);

        return oPacket;
    }

    public static OutPacket leftKnockBack() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SnowBallTouch.getValue());

        return oPacket;
    }

    public static OutPacket hitCoconut(boolean spawn, int id, int type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CoconutHit.getValue());
        oPacket.EncodeInt(spawn ? 32768 : id);
        oPacket.EncodeByte(spawn ? 0 : type);

        return oPacket;
    }

    public static OutPacket coconutScore(int[] coconutscore) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CoconutScore.getValue());
        oPacket.EncodeShort(coconutscore[0]);
        oPacket.EncodeShort(coconutscore[1]);

        return oPacket;
    }

    public static OutPacket showChaosZakumShrine(boolean spawned, int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        oPacket.EncodeByte(spawned ? 1 : 0);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static OutPacket showHorntailShrine(boolean spawned, int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        oPacket.EncodeByte(spawned ? 1 : 0);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket getRPSMode(byte mode, int mesos, int selection, int answer) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RPSGame.getValue());
        oPacket.EncodeByte(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                oPacket.EncodeInt(mesos);
                break;
            case 8:
                oPacket.EncodeInt(9000019);
                break;
            case 11:
                oPacket.EncodeByte(selection);
                oPacket.EncodeByte(answer);
        }

        return oPacket;
    }

    public static OutPacket messengerInvite(String from, int messengerid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(3);
        oPacket.EncodeString(from);
        oPacket.EncodeByte(1);//channel?
        oPacket.EncodeInt(messengerid);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket addMessengerPlayer(String from, User chr, int position, int channel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(position);
        writeCharacterLook(oPacket, chr);
        oPacket.EncodeString(from);
        oPacket.EncodeByte(channel);
        oPacket.EncodeByte(1); // v140
        oPacket.EncodeInt(chr.getJob());

        return oPacket;
    }

    public static OutPacket removeMessengerPlayer(int position) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(2);
        oPacket.EncodeByte(position);

        return oPacket;
    }

    public static OutPacket updateMessengerPlayer(String from, User chr, int position, int channel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(0); // v140.
        oPacket.EncodeByte(position);
        writeCharacterLook(oPacket, chr);
        oPacket.EncodeString(from);
        oPacket.EncodeByte(channel);
        oPacket.EncodeByte(0); // v140.
        oPacket.EncodeInt(chr.getJob()); // doubt it's the job, lol. v140.

        return oPacket;
    }

    public static OutPacket joinMessenger(int position) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(position);

        return oPacket;
    }

    public static OutPacket messengerChat(String charname, String text) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(6);
        oPacket.EncodeString(charname);
        oPacket.EncodeString(text);

        return oPacket;
    }

    public static OutPacket messengerNote(String text, int mode, int mode2) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(mode);
        oPacket.EncodeString(text);
        oPacket.EncodeByte(mode2);

        return oPacket;
    }

    public static OutPacket messengerOpen(byte type, List<User> chars) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GetLotteryResult.getValue());
        oPacket.EncodeByte(type); //7 in messenger open ui 8 new ui
        if (chars.isEmpty()) {
            oPacket.EncodeShort(0);
        }
        for (User chr : chars) {
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(0); //likes
            oPacket.EncodeLong(0); //some time
            oPacket.EncodeString(chr.getName());
            writeCharacterLook(oPacket, chr);
        }

        return oPacket;
    }

    public static OutPacket messengerCharInfo(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Messenger.getValue());
        oPacket.EncodeByte(0x0B);
        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInt(chr.getJob());
        oPacket.EncodeInt(chr.getFame());
        oPacket.EncodeInt(0); //likes
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        oPacket.EncodeString(gs != null ? gs.getName() : "-");
        MapleGuildAlliance alliance = World.Alliance.getAlliance(gs.getAllianceId());
        oPacket.EncodeString(alliance != null ? alliance.getName() : "");
        oPacket.EncodeByte(2);

        return oPacket;
    }

    public static OutPacket removeFromPackageList(boolean remove, int Package) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Parcel.getValue());
        oPacket.EncodeByte(24);
        oPacket.EncodeInt(Package);
        oPacket.EncodeByte(remove ? 3 : 4);

        return oPacket;
    }

    public static OutPacket sendPackageMSG(byte operation, List<MaplePackageActions> packages) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Parcel.getValue());
        oPacket.EncodeByte(operation);

        switch (operation) {
            case 9:
                oPacket.EncodeByte(1);
                break;
            case 10:
                oPacket.EncodeByte(0);
                oPacket.EncodeByte(packages.size());

                for (MaplePackageActions dp : packages) {
                    oPacket.EncodeInt(dp.getPackageId());
                    oPacket.EncodeString(dp.getSender(), 13);
                    oPacket.EncodeInt(dp.getMesos());
                    oPacket.EncodeLong(PacketHelper.getTime(dp.getSentTime()));
                    oPacket.Fill(0, 205);

                    if (dp.getItem() != null) {
                        oPacket.EncodeByte(1);
                        PacketHelper.addItemInfo(oPacket, dp.getItem());
                    } else {
                        oPacket.EncodeByte(0);
                    }
                }
                oPacket.EncodeByte(0);
        }

        return oPacket;
    }

    /**
     * Sends the keymap data to the user
     *
     * @param layout
     * @param jobid
     * @return
     */
    public static OutPacket getKeymap(MapleKeyLayout layout, int jobid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FuncKeyMappedInit.getValue());
        layout.writeData(oPacket, jobid);

        return oPacket;
    }

    public static OutPacket petAutoHP(int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetConsumeItemInit.getValue());
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    public static OutPacket petAutoMP(int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetConsumeMPItemInit.getValue());
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    public static OutPacket petAutoCure(int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetConsumeCureItemInit.getValue());
        oPacket.EncodeInt(itemId);

        return oPacket;
    }

    /*
    public static OutPacket petAutoBuff(int skillId) {
        

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PET_AUTO_BUFF.getValue());
        oPacket.EncodeInt(skillId);

        return oPacket;
    }
     */
    public static void addRingInfo(OutPacket oPacket, List<MapleRing> rings) {
        oPacket.EncodeByte(rings.size());
        for (MapleRing ring : rings) {
            oPacket.EncodeInt(1);
            oPacket.EncodeLong(ring.getRingId());
            oPacket.EncodeLong(ring.getPartnerRingId());
            oPacket.EncodeInt(ring.getItemId());
        }
    }

    public static void addMRingInfo(OutPacket oPacket, List<MapleRing> rings, User chr) {
        oPacket.EncodeByte(rings.size());
        for (MapleRing ring : rings) {
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(ring.getPartnerChrId());
            oPacket.EncodeInt(ring.getItemId());
        }
    }

    public static OutPacket viewSkills(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ResultStealSkillList.getValue());
        List<Integer> skillz = new ArrayList<Integer>();
        for (Skill sk : chr.getSkills().keySet()) {
            if ((sk.canBeLearnedBy(chr.getJob())) && (GameConstants.canSteal(sk)) && (!skillz.contains(Integer.valueOf(sk.getId())))) {
                skillz.add(Integer.valueOf(sk.getId()));
            }
        }
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(skillz.isEmpty() ? 2 : 4);
        oPacket.EncodeInt(chr.getJob());
        oPacket.EncodeInt(skillz.size());
        for (int skill : skillz) {
            oPacket.EncodeInt(skill);
        }
        return oPacket;
    }

    public static class InteractionPacket {

        public static OutPacket getTradeInvite(User c) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
            oPacket.EncodeByte(4);//was 3
            oPacket.EncodeString(c.getName());
//            oPacket.EncodeInt(c.getLevel());
            oPacket.EncodeInt(c.getJob());
            return oPacket;
        }

        public static OutPacket getTradeMesoSet(byte number, long meso) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.UPDATE_MESO.action);
            oPacket.EncodeByte(number);
            oPacket.EncodeLong(meso);
            return oPacket;
        }

        public static OutPacket gachaponMessage(Item item, String town, User player) {
            final OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
            oPacket.EncodeByte(0x0B);
            oPacket.EncodeString(player.getName() + " : got a(n)");
            oPacket.EncodeInt(0); //random?
            oPacket.EncodeString(town);
            PacketHelper.addItemInfo(oPacket, item);
            return oPacket;
        }

        public static OutPacket getTradeItemAdd(byte number, Item item) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.SET_ITEMS.action);
            oPacket.EncodeByte(number);
            oPacket.EncodeByte(item.getPosition());
            PacketHelper.addItemInfo(oPacket, item);

            return oPacket;
        }

        public static OutPacket getTradeStart(ClientSocket c, Trade trade, byte number) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
//            oPacket.encode(PlayerInteractionHandler.Interaction.START_TRADE.action);
//            if (number != 0){//13 a0
////                oPacket.encode(HexTool.getByteArrayFromHexString("13 01 01 03 FE 53 00 00 40 08 00 00 00 E2 7B 00 00 01 E9 50 0F 00 03 62 98 0F 00 04 56 BF 0F 00 05 2A E7 0F 00 07 B7 5B 10 00 08 3D 83 10 00 09 D3 D1 10 00 0B 13 01 16 00 11 8C 1F 11 00 12 BF 05 1D 00 13 CB 2C 1D 00 31 40 6F 11 00 32 6B 46 11 00 35 32 5C 19 00 37 20 E2 11 00 FF 03 B6 98 0F 00 05 AE 0A 10 00 09 CC D0 10 00 FF FF 00 00 00 00 13 01 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 4D 6F 6D 6F 6C 6F 76 65 73 4B 48 40 08"));
//                oPacket.encode(19);
//                oPacket.encode(1);
//                PacketHelper.addCharLook(oPacket, trade.getPartner().getChr(), false);
//                oPacket.encodeString(trade.getPartner().getChr().getName());
//                oPacket.encodeShort(trade.getPartner().getChr().getJob());
//            }else{
            oPacket.EncodeByte(20);
            oPacket.EncodeByte(4);
            oPacket.EncodeByte(2);
            oPacket.EncodeByte(number);

            if (number == 1) {
                oPacket.EncodeByte(0);
                writeCharacterLook(oPacket, trade.getPartner().getCharacter());
                oPacket.EncodeString(trade.getPartner().getCharacter().getName());
                oPacket.EncodeShort(trade.getPartner().getCharacter().getJob());
            }
            oPacket.EncodeByte(number);
            writeCharacterLook(oPacket, c.getPlayer());
            oPacket.EncodeString(c.getPlayer().getName());
            oPacket.EncodeShort(c.getPlayer().getJob());
            oPacket.EncodeByte(255);
//            }
            return oPacket;
        }

        public static OutPacket getTradeConfirmation() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.CONFIRM_TRADE.action);

            return oPacket;
        }

        public static OutPacket TradeMessage(byte UserSlot, byte message) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.EXIT.action);
//            oPacket.encode(25);//new v141
            oPacket.EncodeByte(UserSlot);
            oPacket.EncodeByte(message);

            return oPacket;
        }

        public static OutPacket getTradeCancel(byte UserSlot) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
            oPacket.EncodeByte(PlayerInteractionHandler.Interaction.EXIT.action);
            oPacket.EncodeByte(UserSlot);
            oPacket.EncodeByte(2);//was 2 originally, 7 = Trade Successful message.

            return oPacket;
        }
    }

    public static class NPCPacket {

        public static OutPacket spawnNPC(NPCLife life, boolean minimap) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcEnterField.getValue());

            encodeSpawnNPCData(oPacket, life, minimap);

            return oPacket;
        }

        public static OutPacket spawnNPCRequestController(NPCLife life, boolean minimap) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcChangeController.getValue());
            oPacket.EncodeByte(1);

            encodeSpawnNPCData(oPacket, life, minimap);

            return oPacket;
        }

        private static void encodeSpawnNPCData(OutPacket oPacket, NPCLife life, boolean minimap) {
            oPacket.EncodeInt(life.getObjectId());
            oPacket.EncodeInt(life.getId());
            oPacket.EncodeShort(life.getPosition().x);
            oPacket.EncodeShort(life.getCy());
            oPacket.EncodeByte(0); // New in v176
            oPacket.EncodeByte(life.getF() == 1 ? 0 : 1);
            oPacket.EncodeShort(life.getFh());
            oPacket.EncodeShort(life.getRx0());
            oPacket.EncodeShort(life.getRx1());
            oPacket.EncodeByte(minimap ? 1 : 0);
            oPacket.EncodeInt(0);//new 143

            // new v169
            oPacket.EncodeByte(0); // m_nMoveAction
            oPacket.EncodeInt(-1); // CNpc::SetPresentItem(v3, v69);

            int n_tPresent = 0;
            oPacket.EncodeInt(n_tPresent);

            int m_nNoticeBoardType = 0;
            oPacket.EncodeInt(m_nNoticeBoardType);
            if (m_nNoticeBoardType == 1) {
                oPacket.EncodeInt(0); // m_nNoticeBoardType
            }

            oPacket.EncodeInt(0); // if ( !v3->m_bHideToLocalUser && v74 ) CNpc::ApplyCreateAlpha(v3, v74);

            final String sLocalRepeatEffect = "";
            oPacket.EncodeString(sLocalRepeatEffect);

            boolean decodeCScreenInfo = false;
            oPacket.EncodeBool(decodeCScreenInfo);
            if (decodeCScreenInfo) {
                // v77 = CScreenInfo::Decode((ZRef<CScreenInfo> *)&pvarg.boolVal, iPacket);

                oPacket.EncodeByte(0); // v2 = CInPacket::Decode1(iPacket); CScreenInfo::CreateScreenInfo(&pScreenInfo, (unsigned __int8)v2);
                oPacket.EncodeInt(0);
            }
        }

        public static OutPacket getMapSelection(final int npcid, final String sel) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npcid);
            oPacket.EncodeShort(0x11);
            oPacket.EncodeInt(npcid == 2083006 ? 1 : 0); //neo city
            oPacket.EncodeInt(npcid == 9010022 ? 1 : 0); //dimensional
            oPacket.EncodeString(sel);

            return oPacket;
        }

        public static OutPacket removeNPC(int objectid) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcLeaveField.getValue());
            oPacket.EncodeInt(objectid);

            return oPacket;
        }

        public static OutPacket removeNPCController(int objectid) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcChangeController.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(objectid);

            return oPacket;
        }

        public static OutPacket toggleNPCShow(int oid, boolean hide) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcUpdateLimitedInfo.getValue());
            oPacket.EncodeInt(oid);
            oPacket.EncodeByte(hide ? 0 : 1);
            return oPacket;
        }

        public static OutPacket setNPCSpecialAction(int oid, String action) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcEmotion.getValue());
            oPacket.EncodeInt(oid);
            oPacket.EncodeString(action);
            oPacket.EncodeInt(0); //unknown yet
            oPacket.EncodeByte(0); //unknown yet
            return oPacket;
        }

        public static OutPacket NPCSpecialAction(int oid, int x, int y) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceMoveByScript.getValue());
            oPacket.EncodeInt(oid);
            oPacket.EncodeInt(x);
            oPacket.EncodeInt(y);
            return oPacket;
        }

        public static OutPacket setNPCScriptable() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcCharacterBaseAction.getValue());

            List<Pair<Integer, String>> npcs = new LinkedList<Pair<Integer, String>>();
            npcs.add(new Pair<>(9070006, "Why...why has this happened to me? My knightly honor... My knightly pride..."));
            npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));

            oPacket.EncodeByte(npcs.size());
            for (Pair<Integer, String> s : npcs) {
                oPacket.EncodeInt(s.getLeft());
                oPacket.EncodeString(s.getRight());
                oPacket.EncodeInt(0);
//                oPacket.EncodeInt(Integer.MAX_VALUE);
                oPacket.EncodeByte(0);
            }
            return oPacket;
        }

        public static OutPacket getNPCTalk(int npc, NPCChatType msgType, String talk, NPCChatByType type) {
            return getNPCTalk(npc, msgType, talk, type, npc);
        }

        public static OutPacket getNPCTalk(int npc, NPCChatType msgType, String talk, NPCChatByType type, int overrideNpcId) {
            return getNPCTalk(npc, msgType, talk, type.getValue(), overrideNpcId); // legacy compatible for now, there are just too many scripts.. my god
        }

        public static OutPacket getNPCTalk(int npc, NPCChatType msgType, String talk, byte type, int overrideNpcId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc); // npc

            final boolean unk = false;
            oPacket.EncodeBool(unk); // bSpecificSpeaker
            if (unk) {
                oPacket.EncodeInt(0); // nSpecificSpeakerTemplateID
            }

            oPacket.EncodeByte(msgType.getType());
            oPacket.EncodeShort(type); // mask
            oPacket.EncodeByte(0); // eColor
            if ((type & 0x4) != 0) {
                oPacket.EncodeInt(overrideNpcId);
            }
            oPacket.EncodeString(talk);

            switch (msgType) {
                case OnAskYesNo:
                case OnAskAccept:
                case OnAskSlideMenu:
                case OnAskText:
                case OnAskNumber:
                case OnAskMenu:
                case OnInitialQuiz:
                case OnInitialSpeedQuiz:
                    break;
                default:
                    oPacket.EncodeByte(msgType.allowBack() ? 1 : 0);
                    oPacket.EncodeByte(msgType.allowForward() ? 1 : 0);
                    break;
            }
            oPacket.EncodeInt(0); // new v169
            return oPacket;
        }

        public static OutPacket getEnglishQuiz(int npc, byte type, int diffNPC, String talk, String endBytes) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(10); //not sure
            oPacket.EncodeShort(type);
            oPacket.EncodeByte(0); // new v169
            if ((type & 0x4) != 0) {
                oPacket.EncodeInt(diffNPC);
            }
            oPacket.EncodeString(talk);
            oPacket.Encode(HexTool.getByteArrayFromHexString(endBytes));
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getAdviceTalk(String[] wzinfo) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(8);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(wzinfo.length);
            oPacket.EncodeByte(0); // new v169
            for (String data : wzinfo) {
                oPacket.EncodeString(data);
            }
            oPacket.EncodeInt(0); // new v169
            return oPacket;
        }

        public static OutPacket getSlideMenu(int npcid, int type, int lasticon, String sel) {
            //Types: 0 - map selection 1 - neo city map selection 2 - korean map selection 3 - tele rock map selection 4 - dojo buff selection

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4); //slide menu
            oPacket.EncodeInt(npcid);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(NPCChatType.OnAskSlideMenu.getType());//0x12
            oPacket.EncodeShort(0);//bParam = false
            oPacket.EncodeByte(0);//bsecond for a few types
            oPacket.EncodeInt(type); //menu type
            oPacket.EncodeInt(type == 0 ? lasticon : 0); //last icon on menu
            oPacket.EncodeString(sel);
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getNPCTalkStyle(int npc, String talk, int[] args, boolean second) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);

            boolean unk = false;
            oPacket.EncodeBool(unk);
            if (unk) {
                /* 
                if ( (unsigned __int8)CInPacket::Decode1(a2) )
                    CInPacket::Decode4(a2);*/
                oPacket.EncodeInt(0); // 
            }
            oPacket.EncodeByte(NPCChatType.OnAskAvater.getType());
            oPacket.EncodeShort(0); // mask? idk
            oPacket.EncodeByte(second ? 1 : 0);//new143

            /* new in v170, these 2 bytes seems to show preview of some type
            See: http://pastebin.com/m643nXtt sub_D0C230
            
                LOBYTE(v68) = (unsigned __int8)CInPacket::Decode1((int)a4) != 0;
                LOBYTE(v66) = (unsigned __int8)CInPacket::Decode1(v6) != 0;
            
                if ( (_BYTE)v66 != 1 )
                    goto LABEL_10;
                v24 = *(_DWORD *)(CWvsContext::GetCharacterData((int)v65, (int)&v72) + 4);
             */
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(0);

            oPacket.EncodeString(talk);

            oPacket.EncodeByte(args.length);
            for (int i = 0; i < args.length; i++) {
                oPacket.EncodeInt(args[i]);
            }

            return oPacket;
        }

        public static OutPacket getNPCTalkNum(int npc, String talk, int def, int min, int max) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(0);//new 142
            oPacket.EncodeByte(NPCChatType.OnAskNumber.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.EncodeByte(0);//bsecond for a few types
            oPacket.EncodeString(talk);
            oPacket.EncodeInt(def);
            oPacket.EncodeInt(min);
            oPacket.EncodeInt(max);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getNPCTalkText(int npc, String talk) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(NPCChatType.OnAskText.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.EncodeByte(0);//bsecond for a few types
            oPacket.EncodeString(talk);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getNPCTalkQuiz(int npc, String caption, String talk, int time) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(NPCChatType.OnInitialQuiz.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.EncodeByte(0);//bsecond for a few types
            oPacket.EncodeString(caption);
            oPacket.EncodeString(talk);
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0xF); //no idea
            oPacket.EncodeInt(time); //seconds
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getSelfTalkText(String text) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(1);
            oPacket.EncodeShort(0);
            oPacket.EncodeByte(17);
            oPacket.EncodeByte(0); // new v169
            oPacket.EncodeString(text);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0); // new v169

            return oPacket;
        }

        public static OutPacket getNPCTutoEffect(String effect) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeShort(257);
            oPacket.EncodeString(effect);
            oPacket.EncodeInt(0); // new v169
            return oPacket;
        }

        public static OutPacket getCutSceneSkip() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(2);
            oPacket.EncodeByte(5);
            oPacket.EncodeInt(9010000); //Maple administrator
            oPacket.EncodeString("Would you like to skip the tutorial cutscenes?");
            oPacket.EncodeInt(0); // new v169
            return oPacket;
        }

        public static OutPacket getDemonSelection() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(2159311); //npc
            oPacket.EncodeByte(0x16);
            oPacket.EncodeByte(1);
            oPacket.EncodeShort(1);
            oPacket.Fill(0, 8);
            return oPacket;
        }

        public static OutPacket getAngelicBusterAvatarSelect(int npc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(NPCChatType.OnAskSlideMenu.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.EncodeByte(0);//bsecond for a few types
            return oPacket;
        }

        public static OutPacket getEvanTutorial(String data) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptMessage.getValue());

            oPacket.EncodeByte(8);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(1);
            oPacket.EncodeString(data);

            return oPacket;
        }

        /**
         * OnShopRequest
         *
         * @param shopNPCId
         * @param shop
         * @param c
         * @return
         */
        public static OutPacket OnShopRequest(int shopNPCId, Shop shop, ClientSocket c) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenShopDlg.getValue());
            oPacket.EncodeByte(0); // If True, EncodeInt nPetItemID (m_dwPetTemplateID)
            PacketHelper.addShopInfo(oPacket, shop, c);

            return oPacket;
        }

        /**
         * OnShopTransaction
         *
         * @param pType
         * @param pShop
         * @param c
         * @param nPurchasedIndex
         * @return
         */
        public static OutPacket OnShopTransaction(ShopOperationType pType, Shop pShop, ClientSocket c, int nPurchasedIndex) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ShopResult.getValue());
            oPacket.EncodeByte(pType.getOp());

            if (pType == ShopOperationType.Sell) {
                PacketHelper.addShopInfo(oPacket, pShop, c);
            } else {
                oPacket.EncodeBool(nPurchasedIndex >= 0);
                if (nPurchasedIndex >= 0) {
                    oPacket.EncodeInt(nPurchasedIndex);
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeByte(0);
                    oPacket.EncodeByte(0);
                    oPacket.EncodeShort(0); // nUnk
                }
            }

            return oPacket;
        }

        /**
         * Old Packet for OnShopTransaction, just here for reference. -Mazen
         */
        public static OutPacket sconfirmShopTransaction(ShopOperationType code, Shop shop, ClientSocket c, int indexBought) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ShopResult.getValue());
            oPacket.EncodeByte(code.getOp());

            if (code == ShopOperationType.Sell) {
                PacketHelper.addShopInfo(oPacket, shop, c);
            } else {
                oPacket.EncodeBool(indexBought >= 0);
                if (indexBought >= 0) {
                    oPacket.EncodeInt(indexBought);
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeByte(0);
                    oPacket.EncodeByte(0);
                    oPacket.EncodeShort(0); // nUnk
                }
            }

            /*if (code == ShopOperationType.Sell) {
                PacketHelper.addShopInfo(oPacket, shop, c);
            } else {
                oPacket.EncodeBool(indexBought >= 0);
                if (indexBought >= 0) {
                    oPacket.EncodeInt(indexBought);
                    oPacket.EncodeInt(0);
                    oPacket.EncodeShort(0);
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }*/
            return oPacket;
        }

        public static OutPacket getStorage(int npcId, byte slots, Collection<Item> items, long meso) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(22); // 0x16
            oPacket.EncodeInt(npcId);
            oPacket.EncodeByte(slots);
            oPacket.EncodeLong(0x7E); // BUFFER	[7E00000000000000]
            oPacket.EncodeLong(meso);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte((byte) items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(0);

            return oPacket;
        }

        public static OutPacket getStorageFull() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(17);

            return oPacket;
        }

        public static OutPacket mesoStorage(byte slots, long meso) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(19);
            oPacket.EncodeByte(slots);
            oPacket.EncodeShort(2);
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(meso);

            return oPacket;
        }

        public static OutPacket arrangeStorage(byte slots, Collection<Item> items, boolean changed) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(15);
            oPacket.EncodeByte(slots);
            oPacket.EncodeByte(124);
            oPacket.Fill(0, 10);
            oPacket.EncodeByte(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            oPacket.EncodeByte(0);
            return oPacket;
        }

        public static OutPacket storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(13);
            oPacket.EncodeByte(slots);
            oPacket.EncodeShort(type.getBitfieldEncoding());
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            return oPacket;
        }

        public static OutPacket takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.TrunkResult.getValue());
            oPacket.EncodeByte(9);
            oPacket.EncodeByte(slots);
            oPacket.EncodeShort(type.getBitfieldEncoding());
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            return oPacket;
        }
    }

    public static class SummonPacket {

        public static OutPacket jaguarActive(boolean active) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.JaguarActive.getValue());

            oPacket.EncodeBool(active);

            return oPacket;
        }

        public static OutPacket jaguarSkillRequest(int nSkillID) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.JaguarSkill.getValue());

            oPacket.EncodeInt(nSkillID);

            return oPacket;
        }

        /**
         * This packet spawns the summons onto the world
         *
         * @param MapleSummon
         * @param animated - if the summon has a spawn animation
         *
         * @return oPacket
         */
        public static OutPacket spawnSummon(Summon summon, boolean animated) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedEnterField.getValue());
            oPacket.EncodeInt(summon.getOwnerId());
            oPacket.EncodeInt(summon.getObjectId());
            oPacket.EncodeInt(summon.getSkill());
            oPacket.EncodeByte(summon.getOwnerLevel() - 1);
            oPacket.EncodeByte(summon.getSkillLevel());
            oPacket.EncodePosition(summon.getPosition());
            oPacket.EncodeByte(summon.getSkill() == 32111006 || summon.getSkill() == 33101005 || summon.getSkill() == 14000027 ? 5 : 4);// Summon Reaper Buff - Call of the Wild

            if (summon.getSkill() == 35121003 && summon.getOwner().getMap() != null) {//Giant Robot SG-88
                oPacket.EncodeShort(summon.getOwner().getMap().getSharedMapResources().footholds.findBelow(summon.getPosition()).getId());
            } else {
                oPacket.EncodeShort(0);
            }
            oPacket.EncodeByte(summon.getMovementType().getValue());
            oPacket.EncodeByte(summon.getSummonType());
            oPacket.EncodeByte(animated ? 1 : 0);
            oPacket.EncodeInt(0);//dwMobId
            oPacket.EncodeByte(0); //bFlyMob
            oPacket.EncodeByte(0);//bBeforeFirstAttack
            oPacket.EncodeInt(0);//nLookId
            oPacket.EncodeInt(0);//nBulletId
            User chr = summon.getOwner();
            oPacket.EncodeByte((summon.getSkill() == DualBlade.MIRRORED_TARGET || summon.getSkill() == 14111024 || summon.getSkill() == 14121054 || summon.getSkill() == 14121055 || summon.getSkill() == 14121056) && chr != null ? 1 : 0); // Mirrored Target boolean for character look
            if ((summon.getSkill() == DualBlade.MIRRORED_TARGET || summon.getSkill() == 14111024 || summon.getSkill() == 14121054 || summon.getSkill() == 14121055 || summon.getSkill() == 14121056) && chr != null) { // Mirrored Target
                writeCharacterLook(oPacket, chr);
            }
            if (summon.getSkill() == Mechanic.ROCK_N_SHOCK) {// Rock 'n Shock m_nTeslaCoilState
                oPacket.EncodeByte(0);
                /*
	         do
		        {
		          v8 = CInPacket::Decode2(iPacket);
		          pTriangle.p->p[v3].x = v8;
		          v9 = CInPacket::Decode2(iPacket);
		          pTriangle.p->p[v3++].y = v9;
		        }
		        while ( v3 < 3 );
                 */
            }
            if (GameConstants.SUB_ADCBA0(summon.getSkill())) {
                oPacket.EncodeInt(400); // Unknown
                oPacket.EncodeInt(30); // Unknown
            } else {
                if (summon.getSkill() == 42111003) {
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                } else if (summon.getSkill() == 400051014) {
                    oPacket.EncodeLong(0);
                } else if (summon.getSkill() >= 400051028 && summon.getSkill() <= 400051032) {
                    oPacket.EncodeByte(0);
                }
            }
            oPacket.EncodeByte(0); //m_bJaguarActive

            // v176
            oPacket.EncodeInt(summon.getSummonDuration()); // v2->m_tSummonTerm = CInPacket::Decode4(iPacket);
            oPacket.EncodeByte(1); // m_bAttackActive
            oPacket.EncodeInt(0);
            if (summon.getSkill() >= 33001007 && summon.getSkill() <= 33001015) { // Jaguars
                oPacket.EncodeByte(0);
                oPacket.EncodeInt(0);
            }

            return oPacket;
        }

        /**
         * This packet removes the summons from the world
         *
         * @param MapleSummon
         * @param animated - if the summon has a remove animation
         *
         * @return oPacket
         */
        public static OutPacket removeSummon(Summon summon, boolean animated) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedLeaveField.getValue());
            oPacket.EncodeInt(summon.getOwnerId());
            oPacket.EncodeInt(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                    case 14000027:
                    case 14111024:
                    case 14121054:
                        oPacket.EncodeByte(10);
                        break;
                    case 33101008:
                    case 35111001:
                    case 35111002:
                    case 35111005:
                    case 35111009:
                    case 35111010:
                    case 35111011:
                    case 35121009:
                    case 35121010:
                    case 35121011:
                        oPacket.EncodeByte(5);
                        break;
                    default:
                        oPacket.EncodeByte(4);
                        break;
                }
            } else {
                oPacket.EncodeByte(1);
            }

            return oPacket;
        }

        /**
         * This packet moves the summons
         *
         * @param MapleSummon
         * @param List<LifeMovementFragment> moves - a list of movement
         *
         * @return oPacket
         */
        public static OutPacket moveSummon(Summon summon, List<LifeMovementFragment> moves) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedMove.getValue());
            oPacket.EncodeInt(summon.getOwnerId());
            oPacket.EncodeInt(summon.getObjectId());
            PacketHelper.serializeMovementList(oPacket, summon, moves, 0);

            return oPacket;
        }

        /**
         * This packet handles the attacks for the summons
         *
         * @param int cid - Character Id
         * @param int summonSkillId - skill Id of the summon
         * @param byte animation - the animation
         * @param List<Pair<Integer, Integer>> allDamage - the damage to be dealt
         * @param int level - player level
         * @param boolean counter - If the summon does a counter attack
         *
         */
        public static OutPacket summonAttack(int cid, int summonSkillId, byte animation, List<Pair<Integer, Long>> allDamage, int level, boolean counter) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedAttack.getValue());
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(summonSkillId);
            oPacket.EncodeByte(level - 1);
            oPacket.EncodeByte(animation); //nAction
            oPacket.EncodeByte(allDamage.size()); //nAttackCount
            for (Pair<Integer, Long> attackEntry : allDamage) {
                oPacket.EncodeInt(attackEntry.left);
                oPacket.EncodeByte(7);
                oPacket.EncodeLong(attackEntry.right);
            }
            oPacket.EncodeBool(counter); //bCounterAttack
            oPacket.EncodeBool(false); // bNoAction?
            oPacket.EncodeShort(0); // X (Spirit Bond?)
            oPacket.EncodeShort(0); // Y (Spirit Bond?)
            oPacket.EncodeInt(0); // SkillID?
            
            oPacket.Fill(0, 29);
            
            return oPacket;
        }

        public static OutPacket pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackMonster> attack) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedAttackPvP.getValue());
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(oid);
            oPacket.EncodeByte(playerLevel);
            oPacket.EncodeByte(animation);
            oPacket.EncodePosition(pos);
            oPacket.EncodeInt(0);

            oPacket.EncodeByte(attack.size());
            for (AttackMonster p : attack) {
                oPacket.EncodeInt(p.getObjectId());
                oPacket.EncodePosition(p.getPosition());
                oPacket.EncodeByte(p.getAttacks().size());
                oPacket.EncodeByte(0);

                for (Pair<Long, Boolean> atk : p.getAttacks()) {
                    oPacket.EncodeLong(atk.left);
                }
            }

            return oPacket;
        }

        public static OutPacket summonSkill(int cid, int summonSkillId, int newStance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedSetReference.getValue());
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(summonSkillId);
            oPacket.EncodeByte(newStance);

            return oPacket;
        }

        public static OutPacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SummonedHPTagUpdate.getValue());
            oPacket.EncodeInt(cid);
            oPacket.EncodeInt(summonSkillId);
            oPacket.EncodeByte(unkByte);
            oPacket.EncodeInt(damage);
            oPacket.EncodeInt(monsterIdFrom);
            oPacket.EncodeByte(0);

            return oPacket;
        }
    }

    public static class UIPacket {

        public static OutPacket getDirectionStatus(boolean enable) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.InGameCurNodeEventEnd.getValue());
            oPacket.EncodeByte(enable ? 1 : 0);

            return oPacket;
        }

        public static OutPacket openUI(int type) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUI.getValue());
            oPacket.EncodeInt(type);

            return oPacket;
        }

        public static OutPacket sendRepairWindow(int npc) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(33);
            oPacket.EncodeInt(npc);
            oPacket.EncodeInt(0);//new143

            return oPacket;
        }

        public static OutPacket sendJewelCraftWindow(int npc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(104);
            oPacket.EncodeInt(npc);
            oPacket.EncodeInt(0);//new143

            return oPacket;
        }

        public static OutPacket startAzwan(int npc) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(70);
            oPacket.EncodeInt(npc);
            oPacket.EncodeInt(0);//new143
            return oPacket;
        }

        public static OutPacket openUIOption(int type, int npc) {
            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(type);
            oPacket.EncodeInt(npc);
            return oPacket;
        }

        public static OutPacket sendDojoResult(int points) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(0x48);
            oPacket.EncodeInt(points);

            return oPacket;
        }

        public static OutPacket sendAzwanResult() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(0x45);
            oPacket.EncodeInt(0);

            return oPacket;
        }

        public static OutPacket DublStart(boolean dark) { // Lmao

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
            oPacket.EncodeByte(0x28);
            oPacket.EncodeByte(dark ? 1 : 0);

            return oPacket;
        }

        public static OutPacket DublStartAutoMove() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldCreateFallingCatcher.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(2);

            return oPacket;
        }

        public static OutPacket IntroLock(boolean enable) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetDirectionMode.getValue());
            oPacket.EncodeByte(enable ? 1 : 0);
            oPacket.EncodeInt(0);

            return oPacket;
        }

        /**
         * Creates a nice fade in-out effect to 16:9 resolution from the center.
         *
         * @param enable
         * @return
         */
        public static OutPacket IntroEnableUI(boolean enable) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetInGameDirectionMode.getValue());
            oPacket.EncodeByte(enable ? 1 : 0);
            oPacket.EncodeByte(1);
            if (enable) {
                oPacket.EncodeByte(1);
                oPacket.EncodeByte(0);
            }
            return oPacket;
        }

        public static OutPacket UserHireTutor(boolean summon) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserHireTutor.getValue());
            oPacket.EncodeByte(summon ? 1 : 0);

            return oPacket;
        }

        public static OutPacket UserTutorMessage(int type) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserTutorMsg.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(type);
            oPacket.EncodeInt(7000);

            return oPacket;
        }

        public static OutPacket UserTutorMessage(String message) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserTutorMsg.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeString(message);
            oPacket.EncodeInt(200);
            oPacket.EncodeShort(0);
            oPacket.EncodeInt(10000);

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(int type, int value, int x) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            if (x > 0) {
                oPacket.EncodeByte(x);
            }
            oPacket.EncodeByte((byte) type);
            oPacket.EncodeInt(value);

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(int type, int value) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());

            oPacket.EncodeByte((byte) type);
            oPacket.EncodeInt(value);

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(String data, int value, int x, int y, int a, int b) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(2);
            oPacket.EncodeString(data);
            oPacket.EncodeInt(value);
            oPacket.EncodeInt(x);
            oPacket.EncodeInt(y);
            oPacket.EncodeByte(a);
            if (a > 0) {
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(b);
            if (b > 1) {
                oPacket.EncodeInt(0);
            }

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(String data, int value, int x, int y) {
            return UIPacket.UserInGameDirectionEvent(data, value, x, y, 0);
        }

        public static OutPacket UserInGameDirectionEvent(String data, int value, int x, int y, int npc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(2);
            oPacket.EncodeString(data);
            oPacket.EncodeInt(value);
            oPacket.EncodeInt(x);
            oPacket.EncodeInt(y);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(0);

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(byte x, int value, int a, int b) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(5);
            oPacket.EncodeByte(x);
            oPacket.EncodeInt(value);
            if (x == 0) {
                oPacket.EncodeInt(a);
                oPacket.EncodeInt(b);
            }

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent(byte x, int value) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(5);
            oPacket.EncodeByte(x);
            oPacket.EncodeInt(value);

            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent_1(String data, int value, int x, int y, int npc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(2);
            oPacket.EncodeString(data);
            oPacket.EncodeInt(value);
            oPacket.EncodeInt(x);
            oPacket.EncodeInt(y);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(npc);
            oPacket.EncodeByte(0);

            // Added for BeastTamer
            return oPacket;
        }

        public static OutPacket UserInGameDirectionEvent_new(byte x, int value) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.EncodeByte(5);
            oPacket.EncodeByte(x);
            oPacket.EncodeInt(value);
            if (x == 0) {
                oPacket.EncodeInt(value);
                oPacket.EncodeInt(value);
            }

            return oPacket;
        }

        public static OutPacket moveScreen(int x) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MOVE_SCREEN_X.getValue());
            oPacket.EncodeInt(x);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);

            return oPacket;
        }

        public static OutPacket screenDown() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MOVE_SCREEN_DOWN.getValue());

            return oPacket;
        }

        public static OutPacket reissueMedal(int itemId, int type) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.MedalReissueResult.getValue());
            oPacket.EncodeByte(type);
            oPacket.EncodeInt(itemId);

            return oPacket;
        }

        public static OutPacket playMovie(String data, boolean show) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserPlayMovieClip.getValue());
            oPacket.EncodeString(data);
            oPacket.EncodeByte(show ? 1 : 0);

            return oPacket;
        }

        /*
        public static OutPacket setRedLeafStatus(int joejoe, int hermoninny, int littledragon, int ika) {
            //packet made to set status
            //should remove it and make a handler for it, it's a recv opcode
            /*
             * slea:
             * E2 9F 72 00
             * 5D 0A 73 01
             * E2 9F 72 00
             * 04 00 00 00
             * 00 00 00 00
             * 75 96 8F 00
             * 55 01 00 00
             * 76 96 8F 00
             * 00 00 00 00
             * 77 96 8F 00
             * 00 00 00 00
             * 78 96 8F 00
             * 00 00 00 00
         */
        //oPacket.encodeShort()
        /*
            oPacket.EncodeInt(7512034); //no idea
            oPacket.EncodeInt(24316509); //no idea
            oPacket.EncodeInt(7512034); //no idea
            oPacket.EncodeInt(4); //no idea
            oPacket.EncodeInt(0); //no idea
            oPacket.EncodeInt(9410165); //joe joe
            oPacket.EncodeInt(joejoe); //amount points added
            oPacket.EncodeInt(9410166); //hermoninny
            oPacket.EncodeInt(hermoninny); //amount points added
            oPacket.EncodeInt(9410167); //little dragon
            oPacket.EncodeInt(littledragon); //amount points added
            oPacket.EncodeInt(9410168); //ika
            oPacket.EncodeInt(ika); //amount points added

            return oPacket;
        }
         */
        public static OutPacket sendRedLeaf(int points, boolean viewonly) {
            /*
             * slea:
             * 73 00 00 00
             * 0A 00 00 00
             * 01
             */

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInt(0x73);
            oPacket.EncodeInt(points);
            oPacket.EncodeByte(viewonly ? 1 : 0); //if view only, then complete button is disabled

            return oPacket;
        }
    }

    public static class EffectPacket {

        public static enum UserEffectCodes {

            LevelUp((byte) 0x00),
            SkillUse((byte) 0x01),
            SkillUseBySummoned((byte) 0x02),
            SkillAffected((byte) 0x03),
            SkillAffected_Ex((byte) 0x04),
            SkillAffected_Select((byte) 0x05),
            SkillSpecialAffected((byte) 0x06),
            Quest((byte) 0x07), // mastery book effect if sending 0
            Pet((byte) 0x08),
            SkillSpecial((byte) 0x09),
            Resist((byte) 0x0A),
            ProtectOnDieItemUse((byte) 0x0B), // 'The EXP did not drop after using item'
            PlayPortalSE((byte) 0x0C),
            JobChanged((byte) 0x0D), // Seems to be some item box opening effect
            QuestComplete((byte) 0x0E),
            IncDecHPEffect((byte) 0x0F),
            BuffItemEffect((byte) 0x10), // Seems to be some item box opening effect
            SquibEffect((byte) 0x11),
            MonsterBookCardGet((byte) 0x12), // mainly sound only, GMS 170 doesnt seems to have the effect animation anymore :( 
            LotteryUse((byte) 0x13),
            ItemLevelup((byte) 0x14),
            ItemMaker((byte) 0x15),
            ExpItemConsumed((byte) 0x16), // Seems to be some item box opening effect, same as unknown3
            FieldItemConsumed((byte) 0x17), // EXP gained when you touch the exp objects spawned by killing mobs
            ReservedEffect((byte) 0x18), // not sure
            // Something new is in here, missing in KMST 0x19
            UpgradeTombItemUse((byte) 0x1A), // Wheel of Destiny
            BattlefieldItemUse((byte) 0x1B), // Premium wheel of destiny
            AvatarOriented((byte) 0x1B),
            AvatarOrientedRepeat((byte) 0x1C),
            AvatarOrientedMultipleRepeat((byte) 0x1D),
            IncubatorUse((byte) 0x1E),
            PlaySoundWithMuteBGM((byte) 0x1F), // This may be wrong, I can't confirm it
            PlayExclSoundWithDownBGM((byte) 0x20), // This may be wrong, I can't confirm it
            // Something new is in here, missing in KMST 0x21
            SpiritStoneUse((byte) 0x22),
            IncDecHPEffect_EX((byte) 0x23),
            IncDecHPRegenEffect((byte) 0x24),
            // Something new is in here, missing in KMST 0x25
            EffectUOL((byte) 0x26),
            PVP((byte) 0x27),
            PvPChampion((byte) 0x28),
            PvPGradeUp((byte) 0x29),
            PvPRevive((byte) 0x2A), // character flickers lol
            JobEffect((byte) 0x2B),
            FadeInOut((byte) 0x2C),
            MobSkillHit((byte) 0x2D),
            AswanSiegeAttack((byte) 0x2E), // some map arrows it seems
            BlindEffect((byte) 0x2F),
            BossShieldCount((byte) 0x30),
            ResetOnStateForOnOffSkill((byte) 0x33), // Angelic Burster Recharge (0x33) Version 180
            JewelCraft((byte) 0x32),
            ConsumeEffect((byte) 0x33), // don't know what the id for this is, since 0x33 is Angelic Buster recharge
            PetBuff((byte) 0x34), // some esclamation mark if 0 is sent
            LotteryUIResult((byte) 0x35),
            LeftMonsterNumber((byte) 0x36),
            ReservedEffectRepeat((byte) 0x37),
            RobbinsBomb((byte) 0x38),
            SkillMode((byte) 0x39),
            ActQuestComplete((byte) 0x3A),
            Point((byte) 0x3B), // beware! error 38 lol
            SpeechBalloon((byte) 0x3C),
            TextEffect((byte) 0x3D),
            SkillPreLoopEnd((byte) 0x3E),
            Aiming((byte) 0x3F),
            PickUpItem((byte) 0x40),
            BattlePvP_IncDecHp((byte) 0x41), // what is this? Just shows a monster like damaged on player
            BiteAttack_ReceiveSuccess((byte) 0x42), // monster book caught
            BiteAttack_ReceiveFail((byte) 0x43), // Fail animation
            IncDecHPEffect_Delayed((byte) 0x44),
            Lightness((byte) 0x45),
            ActionSetUsed((byte) 0x46),
            // Non KMST like naming convention... TODO: FIX
            Upgrade_Option((byte) 0x48), // Shows a banner 'Congrats, you have gained Upgrade potion for playing an hour'
            Familiar_Escape((byte) 0x4A), // familiar message in grey

            /// From here onwards, these are unknowns to be updated
            Skill_DiceEffect((byte) 0x03);

            private final int effectid;

            private UserEffectCodes(int effectid) {
                this.effectid = effectid;
            }

            public int getEffectId() {
                return effectid;
            }
        }

        public static OutPacket showForeignEffect(UserEffectCodes effect) {
            return showForeignEffect(-1, effect, 0);
        }

        public static OutPacket showForeignEffect(int cid, UserEffectCodes effect) {
            return showForeignEffect(-1, effect, 0);
        }

        public static OutPacket showForeignEffect(int cid, UserEffectCodes effect, int val) {

            OutPacket oPacket;
            if (cid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(cid);
            }
            oPacket.EncodeByte(effect.getEffectId());

            switch (effect) {
                case ItemMaker:
                    oPacket.Fill(0, 4); // hmm could this be the delay?
                    break;
                case FieldItemConsumed:
                    oPacket.EncodeInt(val);
                    break;
            }

            oPacket.Fill(0, 99);//I really can't be fucked rn..

            return oPacket;
        }

        /**
         * Shows the text message with regards to burning field when entering a map
         *
         * @param bannerText
         * @return
         */
        public static OutPacket showBurningFieldTextEffect(String bannerText) {

            /*3D 
            4F 00 
            23 66 6E B3 AA B4 AE B0 ED B5 F1 20 45 78 74 72 
            61 42 6F 6C 64 23 23 66 73 32 36 23 20 20 20 20 
            20 20 20 20 20 20 42 75 72 6E 69 6E 67 20 53 74 
            61 67 65 20 31 3A 20 31 30 25 20 42 6F 6E 75 73 
            20 45 58 50 21 20 20 20 20 20 20 20 20 20 20 

            32 00 00 00 // floating value
            DC 05 00 00  // floating value

            04 00 00 00 
            00 00 00 00 
            38 FF FF FF 
            01 00 00 00 
            04 00 00 00 
            02 00 00 00 // v640
            00 00 00 00 
            00 00 00 00*/
            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.TextEffect.getEffectId());
            //oPacket.encodeString("#fn³ª´®°íµñ ExtraBold##fs26#          Burning Stage 1: 10% Bonus EXP!  ");
            oPacket.EncodeString(String.format("#fn³ª´®°íµñ ExtraBold##fs26#          %s        ", bannerText));
            oPacket.EncodeInt(0x32); // *(float *)&v848 = COERCE_FLOAT(CInPacket::Decode4(a2));
            oPacket.EncodeInt(0x5DC); // *(float *)&v854 = COERCE_FLOAT(CInPacket::Decode4(a2));
            oPacket.EncodeInt(0x4); // Align whole text    3 = Left, 5 = right, 4 = Center
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0xFFFFFF38); // Seems to be the X and Y position? 
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(4);
            oPacket.EncodeInt(2);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);

            return oPacket;
        }

        public static OutPacket showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level);
        }

        public static OutPacket showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level) {

            OutPacket oPacket;
            if (cid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(cid);
            }
            oPacket.EncodeByte(UserEffectCodes.Skill_DiceEffect.getEffectId()); // TODO
            oPacket.EncodeInt(effectid);
            oPacket.EncodeInt(effectid2);
            oPacket.EncodeInt(skillid);
            oPacket.EncodeByte(level);
            oPacket.EncodeByte(0);
            oPacket.Fill(0, 100);

            return oPacket;
        }

        public static OutPacket useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.ProtectOnDieItemUse.getEffectId());
            oPacket.EncodeByte(safetyCharm ? 1 : 0);
            oPacket.EncodeByte(charmsleft);
            oPacket.EncodeByte(daysleft);
            if (!safetyCharm) {
                oPacket.EncodeInt(0);
            }
            oPacket.Fill(0, 4);

            return oPacket;
        }

        public static OutPacket Mulung_DojoUp2() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(10);

            return oPacket;
        }

        public static OutPacket showOwnHpHealed(int amount) {
            return showHpHealed(-1, amount);
        }

        public static OutPacket showHpHealed(int cid, int amount) {

            OutPacket oPacket;
            if (cid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(cid);
            }
            oPacket.EncodeByte(UserEffectCodes.IncDecHPEffect_EX.getEffectId());
            oPacket.EncodeInt(amount);

            oPacket.Fill(0, 99); // Avoid DC

            return oPacket;
        }

        public static OutPacket showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static OutPacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {

            OutPacket oPacket;
            if (from_playerid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(from_playerid);
            }
            oPacket.EncodeByte(UserEffectCodes.BuffItemEffect.getEffectId());
            oPacket.EncodeInt(itemId);

            if (effect != null && effect.length() > 0) {
                oPacket.EncodeByte(1);
                oPacket.EncodeString(effect);
            } else {
                oPacket.EncodeByte(0);
            }

            return oPacket;
        }

        public static OutPacket showCashItemEffect(int itemId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.FieldItemConsumed.getEffectId());
            oPacket.EncodeInt(itemId);

            return oPacket;
        }

        public static OutPacket useWheel(byte charmsleft) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.UpgradeTombItemUse.getEffectId());
            oPacket.EncodeByte(charmsleft);

            return oPacket;
        }

        public static OutPacket showOwnBuffEffect(int skillid, UserEffectCodes effect, int playerLevel, int skillLevel) {
            return showBuffeffect(-1, skillid, effect, playerLevel, skillLevel, (byte) 3);
        }

        public static OutPacket showOwnBuffEffect(int skillid, UserEffectCodes effect, int playerLevel, int skillLevel, byte direction) {
            return showBuffeffect(-1, skillid, effect, playerLevel, skillLevel, direction);
        }

        public static OutPacket showBuffeffect(int cid, int skillid, UserEffectCodes effect, int playerLevel, int skillLevel) {
            return showBuffeffect(cid, skillid, effect, playerLevel, skillLevel, (byte) 3);
        }

        public static OutPacket showBuffeffect(int cid, int skillid, UserEffectCodes effect, int playerLevel, int skillLevel, byte direction) {

            OutPacket oPacket;
            if (cid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(cid);
            }
            oPacket.EncodeByte(effect.getEffectId());  // TODO, update for V170 [check]
            oPacket.EncodeInt(skillid);
            oPacket.EncodeByte(playerLevel - 1);
            if (effect == UserEffectCodes.SkillUseBySummoned && skillid == 31111003) {
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(skillLevel);
            if ((direction != 3) || (skillid == 1320006) || (skillid == 30001062) || (skillid == 30001061)) {
                oPacket.EncodeByte(direction);
            }
            if (skillid == 30001062) {
                oPacket.EncodeInt(0);
            }
            oPacket.Fill(0, 29);

            return oPacket;
        }

        public static OutPacket showWZUOLEffect(String data, boolean flipImage) {
            return showWZUOLEffect(data, flipImage, -1, 0, 0);
        }

        /**
         * Shows an image in the WZ UOL path.
         *
         * @param data
         * @param flipImage Should the image be flipped horizontally?
         * @param characterid the character object OID
         * @param direction
         * @param time
         * @param mode
         * @return
         */
        public static OutPacket showWZUOLEffect(String data, boolean flipImage, int characterid, int time, int mode) {

            OutPacket oPacket;
            if (characterid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(characterid);
            }
            oPacket.EncodeByte(UserEffectCodes.EffectUOL.getEffectId());
            oPacket.EncodeString(data);

            oPacket.EncodeByte(flipImage ? 1 : 0); // new in v170

            oPacket.EncodeInt(time); // v868 = CInPacket::Decode4(a3);
            oPacket.EncodeInt(mode); // v874 = CInPacket::Decode4(a3);

            switch (mode) {
                case 1:
                    break;
                case 2:
                    oPacket.EncodeInt(0); // Most likely the delay.
                    break;
                case 3:
                    break;
            }

            // Client seems to do something 
            // if v868 < 0, else something
            return oPacket;
        }

        /**
         * Shows the reserved effect for map cut-scene
         *
         * @param data
         * @return
         */
        public static OutPacket showReservedEffect_CutScene(String data) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.ReservedEffect.getEffectId());

            /*LOBYTE(bFlip) = CInPacket::Decode1(iPacket) != 0;
	      nRange = CInPacket::Decode4(iPacket);
	      nNameHeight = CInPacket::Decode4(iPacket);
	      CInPacket::DecodeStr(iPacket, &sMsg);*/
            boolean show = true;
            oPacket.EncodeBool(show);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeString(data);

            return oPacket;
        }

        public static OutPacket showOwnPetLevelUp(User chr, byte index) {
            OutPacket oPacket;
            if (chr == null) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
            }
            oPacket.EncodeByte((int) UserEffectCodes.Pet.getEffectId());
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(index);
            if (chr == null) {
                oPacket.EncodeInt(0);
            }

            return oPacket;
        }

        public static OutPacket showOwnPVPChampionEffect() {
            return showPVPChampionEffect(-1);
        }

        public static OutPacket showPVPChampionEffect(int from_playerid) {

            OutPacket oPacket;
            if (from_playerid == -1) {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInt(from_playerid);
            }
            oPacket.EncodeByte(UserEffectCodes.PvPChampion.getEffectId());  // TODO, update for V170
            oPacket.EncodeInt(30000);

            return oPacket;
        }

        public static OutPacket showWeirdEffect(String effect, int itemId) {
            final OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeByte(UserEffectCodes.PlayExclSoundWithDownBGM.getEffectId());
            oPacket.EncodeString(effect);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);//weird high number is it will keep showing it lol
            oPacket.EncodeInt(2);
            oPacket.EncodeInt(itemId);
            return oPacket;
        }

        public static OutPacket showWeirdEffect(int chrId, String effect, int itemId) {
            final OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeInt(chrId);
            oPacket.EncodeByte(UserEffectCodes.PlayExclSoundWithDownBGM.getEffectId());
            oPacket.EncodeString(effect);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);//weird high number is it will keep showing it lol
            oPacket.EncodeInt(2);//this makes it read the itemId
            oPacket.EncodeInt(itemId);

            return oPacket;
        }
    }

    public static OutPacket getCassandrasCollection() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BingoCassandraResult.getValue());
        oPacket.EncodeByte(6);

        return oPacket;
    }

    public static OutPacket unsealBox(int reward) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectLocal.getValue());
        oPacket.EncodeByte(0x31);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(reward);
        oPacket.EncodeInt(1);

        return oPacket;
    }

    /**
     * This packet will display the effect of a consumed item
     *
     * @param int cId - Character Id
     * @param int itemId - the id of the item
     *
     * @return oPacket
     *
     */
    public static OutPacket consumeItemEffect(int cId, int itemId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserConsumeItemEffect.getValue());
        oPacket.EncodeInt(cId);
        oPacket.EncodeInt(itemId);
        return oPacket;
    }

    /**
     * This packet is sent to the client so the client can update what tag state it is in
     *
     * @param chr
     * @return MapleCharacter chr - Character object
     */
    public static OutPacket zeroTagState(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ZeroTag.getValue());
        oPacket.EncodeInt(chr.getId());

        return oPacket;
    }

    public static class RunePacket {

        public static OutPacket runeMsg(int type, long time) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.SHOW_MAP_NAME.getValue());
            oPacket.EncodeInt(type);
            switch (type) {
                case 2://Cant use another rune yet (time left)
                    oPacket.EncodeInt((int) time);
                    break;
                case 4://That rune is to strong for you to handle
                    break;
                case 5://Shows arrows
                    break;
            }

            return oPacket;
        }

        public static OutPacket spawnRune(RuneStone rune) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneEnterField.getValue());
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(rune.getRuneType().getType());
            oPacket.EncodeInt((int) rune.getPosition().getX());
            oPacket.EncodeInt((int) rune.getPosition().getY());
            oPacket.EncodeBool(rune.isFacingLeft());

            return oPacket;
        }

        public static OutPacket removeRune(RuneStone rune, User chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneActSuccess.getValue());
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(chr.getId());

            return oPacket;
        }

        public static OutPacket removeAllRune() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RUNE_STONE_CLEAR_AND_ALL_REGISTER.getValue());
            int count = 0;
            oPacket.EncodeInt(count); // count
            for (int i = 0; i < count; i++) {
                oPacket.EncodeInt(0); // not sure, but whatever
            }

            return oPacket;
        }

        public static OutPacket finishRune() {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneLeaveField.getValue());
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(6041607);//idk?//was 7032481

            return oPacket;
        }

        public static OutPacket RuneAction(int type, int time) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneActSuccess.getValue());
            oPacket.EncodeInt(type);
            oPacket.EncodeInt(time);

            return oPacket;
        }

        public static OutPacket showRuneEffect(int type) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneStoneSkillAck.getValue());
            oPacket.EncodeInt(type);

            return oPacket;
        }

    }
}
