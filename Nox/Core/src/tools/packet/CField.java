package tools.packet;

import client.CharacterTemporaryStat;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import client.MapleClient;
import client.MapleKeyLayout;
import client.MapleQuestStatus;
import client.Skill;
import client.SkillFactory;
import client.SkillMacro;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.ItemConstants;
import constants.QuickMove.QuickMoveNPC;
import constants.ServerConstants;
import constants.skills.DualBlade;
import constants.skills.Blaster;
import constants.skills.Kinesis;
import constants.skills.Mechanic;
import constants.skills.WildHunter;
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
import server.MapleTrade;
import server.Randomizer;
import server.events.MapleSnowball;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.objects.MapleAndroid;
import server.maps.objects.User;
import server.maps.objects.MapleDragon;
import server.maps.objects.MapleHaku;
import server.maps.objects.MapleKite;
import server.maps.objects.MapleMist;
import server.maps.objects.MapleNPC;
import server.maps.objects.MapleReactor;
import server.maps.objects.MapleRuneStone;
import server.maps.objects.Summon;
import server.maps.objects.MechDoor;
import server.maps.objects.MonsterFamiliar;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import server.shops.ShopOperationType;
import handling.world.AttackMonster;
import handling.game.PlayerDamageHandler;
import handling.game.WhisperHandler.WhisperFlag;
import java.awt.Rectangle;
import javax.swing.text.Position;
import net.Packet;
import server.MapleStatEffect;
import static server.MapleStatInfo.s;
import server.maps.Map_MCarnival;
import server.maps.Map_MaplePlatform;
import server.maps.objects.ForceAtom;
import server.maps.objects.Pet;
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
     * @param MapleClient the client object
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static Packet getServerIP(MapleClient c, int port, int clientId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.SelectCharacterResult.getValue());
        oPacket.Encode(0);
        oPacket.Encode(0);

        oPacket.Encode(ServerConstants.NEXON_IP);
        oPacket.EncodeShort(port);
        oPacket.EncodeInteger(0);
        oPacket.EncodeShort(0);

        oPacket.Encode(ServerConstants.NEXON_CHAT_IP); // another ip address
        oPacket.EncodeShort(0); // another port

        oPacket.EncodeInteger(clientId);

        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.Encode(0);

        byte[] interServerAuthBuffer = new byte[8];
        Randomizer.nextBytes(interServerAuthBuffer);
        oPacket.Encode(interServerAuthBuffer); // TODO: Check on this when the client sends PLAYER_LOGGEDIN on the channel servers

        return oPacket.ToPacket();
    }

    /**
     * Gets a packet telling the client the IP of the new channel.
     *
     * @param MapleClient the client object
     * @param port The port the channel is on.
     * @return The server IP packet.
     */
    public static Packet getChannelChange(MapleClient c, int port) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MigrateCommand.getValue());
        oPacket.Encode(1);
        oPacket.Encode(ServerConstants.NEXON_IP);
        oPacket.EncodeShort(port);
        oPacket.EncodeInteger(0); //dunno, couldnt find the packet in the idb because I was not looking hard enough
        return oPacket.ToPacket();
    }
    
    public static Packet OnWhisper(int nFlag, String sFind, String sReceiver, String sMsg, int nLocationResult, int dwLocation, int nTargetPosition_X, int nTargetPosition_Y, boolean bFromAdmin, boolean bSuccess) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.Whisper.getValue());
        
        oPacket.Encode(nFlag);
        switch (nFlag) {
            case WhisperFlag.ReplyReceive:
                oPacket.EncodeString(sReceiver);
                oPacket.Encode(dwLocation);
                oPacket.Encode(bFromAdmin);
                oPacket.EncodeString(sMsg);
                break;
            case WhisperFlag.BlowWeather:
                oPacket.EncodeString(sReceiver);
                oPacket.Encode(bFromAdmin);
                oPacket.EncodeString(sMsg);
                break;
            case WhisperFlag.ReplyResult:
            case WhisperFlag.AdminResult:
                oPacket.EncodeString(sFind);
                oPacket.Encode(bSuccess);
                break;
            case WhisperFlag.FindResult:
            case WhisperFlag.LocationResult:
                oPacket.EncodeString(sFind);
                oPacket.Encode(nLocationResult);
                oPacket.EncodeInteger(dwLocation);
                if (nLocationResult == WhisperFlag.GameSvr) {
                    oPacket.EncodeInteger(nTargetPosition_X);
                    oPacket.EncodeInteger(nTargetPosition_Y);
                }
                break;
            case WhisperFlag.BlockedResult:
                oPacket.EncodeString(sFind);
                oPacket.Encode(bSuccess);
                break;
        }
        return oPacket.ToPacket();
    }
    
    public static Packet OnGroupMessage(int nType, String sFrom, String sMsg) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.GroupMessage.getValue());
        oPacket.Encode(nType);
        oPacket.EncodeString(sFrom);
        oPacket.EncodeString(sMsg);
        return oPacket.ToPacket();
    }
    
    public static Packet createForceAtom(boolean bByMob, int nUserOwner, int nTargetID, int nForceAtomType, boolean bToMob,
                                     int nTargets, int nSkillID, ForceAtom pForceAtom, Rectangle rRect, int nArriveDirection, int nArriveRange,
                                     Point forcedTargetPos, int bulletID, Point pos) {
        
        List<Integer> nNumbers = new ArrayList<>();
        nNumbers.add(nTargets);
        List<ForceAtom> forceAtomInfos = new ArrayList<>();
        forceAtomInfos.add(pForceAtom);
        return createForceAtom(bByMob, nUserOwner, nTargetID, nForceAtomType, bToMob, nNumbers, nSkillID, forceAtomInfos,
                rRect, nArriveDirection, nArriveRange, forcedTargetPos, bulletID, pos);
    }

    public static Packet createForceAtom(boolean bByMob, int nUserOwner, int nCharID, int nForceAtomType, boolean bToMob,
                                     List<Integer> aTargets, int nSkillID, List<ForceAtom> aForceAtoms, Rectangle rRect, int nArriveDirection, int nArriveRange,
                                     Point forcedTargetPos, int bulletID, Point pos) {
        
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(bByMob);
        if(bByMob) {
            oPacket.EncodeInteger(nUserOwner);
        }
        oPacket.EncodeInteger(nCharID);
        oPacket.EncodeInteger(nForceAtomType);
        if(nForceAtomType != 0 && nForceAtomType != 9 && nForceAtomType != 14) {
            oPacket.Encode(bToMob);
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
                    oPacket.EncodeInteger(aTargets.size());
                    for (int i : aTargets) {
                        oPacket.EncodeInteger(i);
                    }
                    break;
                default:
                    oPacket.EncodeInteger(aTargets.get(0));
                    break;
            }
            oPacket.EncodeInteger(nSkillID);
        }
        for(ForceAtom pAtom : aForceAtoms) {
            oPacket.Encode(1);
            pAtom.encode(oPacket);
        }
        oPacket.Encode(0);
        switch (nForceAtomType) {
            case 11:
                oPacket.EncodeRectangle(rRect);
                oPacket.EncodeInteger(bulletID);
                break;
            case 9:
            case 15:
                oPacket.EncodeRectangle(rRect);
                break;
            case 16:
                oPacket.EncodePosition(pos);
                break;
            case 17:
                oPacket.EncodeInteger(nArriveDirection);
                oPacket.EncodeInteger(nArriveRange);
                break;
            case 18:
                oPacket.EncodePosition(forcedTargetPos);
                break;
        }
        
        oPacket.Fill(0, 29);

        return oPacket.ToPacket();
    }
    
    /**
     * Handles Final Attack.
     */
    public static Packet finalAttackRequest(User pPlayer, int nSkill, int nFinalSkill, int nDelay, int nMob, int nRequestTime) {
        return finalAttackRequest(pPlayer, nSkill, nFinalSkill, nDelay, nMob, nRequestTime, false, null);
    }

    public static Packet finalAttackRequest(User pPlayer, int nSkill, int nFinalSkill, int nDelay, int nMob, int nRequestTime, boolean bLeft, Point pBase) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserFinalAttackRequest.getValue());

        oPacket.EncodeInteger(nSkill); // nSkillId
        oPacket.EncodeInteger(nFinalSkill); // nFinalSkillId
        oPacket.EncodeInteger(pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).getItemId()); // nWeaponId
        oPacket.EncodeInteger(nDelay); // nDelay
        oPacket.EncodeInteger(nMob); // nMobId
        oPacket.EncodeInteger(nRequestTime); // nReqTime

        if (nSkill == 101000102) { // Air Riot
            oPacket.Encode(bLeft);
            oPacket.EncodePosition(pBase);
        }

        return oPacket.ToPacket();
    }

    public static Packet OrbitalFlame(int cid, int skillid, int effect, int direction, int range) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Fill(0, 1);
        oPacket.EncodeInteger(cid);
        /* Unk */
        oPacket.EncodeInteger(0x11);
        /* onOrbitalFlame */
        oPacket.Encode(1);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(skillid);
        oPacket.Encode(1);
        oPacket.EncodeInteger(2);
        oPacket.EncodeInteger(effect); // Orbital Flame Effect
        oPacket.EncodeInteger(17);
        oPacket.EncodeInteger(17);
        oPacket.EncodeInteger(90);
        oPacket.Fill(0, 12);
        oPacket.EncodeInteger(Randomizer.nextInt());
        oPacket.EncodeInteger(8);
        oPacket.EncodeInteger(0); // v174+
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(direction);
        oPacket.EncodeInteger(range);

        return oPacket.ToPacket();
    }

    public static Packet sendKaiserSkillShortcut(int[] skills) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.KaiserSkillShortcut.getValue());
        for (int i = 0; i < 3; i++) {
            if (skills[i] != 0) {
                oPacket.Encode(true);
                oPacket.Encode(i);
                oPacket.EncodeInteger(skills[i]);
                int x = 0;
                oPacket.Encode(x);
                if (x != 0) {
                    oPacket.Encode(0);
                    oPacket.EncodeInteger(0);
                }
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet getMacros(SkillMacro[] macros) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MacroSysDataInit.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        oPacket.Encode(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                oPacket.EncodeString(macro.getName());
                oPacket.Encode(macro.getShout());
                oPacket.EncodeInteger(macro.getSkill1());
                oPacket.EncodeInteger(macro.getSkill2());
                oPacket.EncodeInteger(macro.getSkill3());
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet gameMsg(String msg) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserNoticeMsg.getValue());
        oPacket.encodeString(msg, false);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet innerPotentialMsg(String msg) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.GetCharacterPosition.getValue());
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet updateInnerPotential(byte ability, int skill, int level, int rank) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.CharacterPotentialSet.getValue());
        oPacket.Encode(1); //unlock
        oPacket.Encode(1); //0 = no update
        oPacket.EncodeShort(ability); //1-3
        oPacket.EncodeInteger(skill); //skill id (7000000+)
        oPacket.EncodeShort(level); //level, 0 = blank inner ability
        oPacket.EncodeShort(rank); //rank
        oPacket.Encode(1); //0 = no update

        return oPacket.ToPacket();
    }

    public static Packet innerPotentialResetMessage() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.CharacterPotentialReset.getValue());
        oPacket.EncodeString("Inner Potential has been reconfigured.");
        oPacket.Encode(1);
        return oPacket.ToPacket();
    }

    public static Packet updateHonour(int honourLevel, int honourExp, boolean levelup) {
        /*
         * data:
         * 03 00 00 00
         * 69 00 00 00
         * 01
         */
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CharacterHonorExp.getValue());

        oPacket.EncodeInteger(honourLevel);
        oPacket.EncodeInteger(honourExp);
        oPacket.Encode(levelup ? 1 : 0); //shows level up effect

        return oPacket.ToPacket();
    }

    public static Packet getCharInfo(User mc) {
        return getWarpToMap(mc, null, 0, false);
    }

    public static Packet getWarpToMap(User chr, MapleMap to, int spawnPoint, boolean bCharacterData) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.SetField.getValue());

        // Save character data upon entering a new map.
        chr.saveToDB(false, false);

        // Prevent disconnects upon changing map with Shadow Partner active - Mazen.
        if (GameConstants.isNightWalkerCygnus(chr.getJob())) {
            chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowServant);
            chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowIllusion);
            chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowPartner);
        }

        //CClient::DecodeOptMan (Size)
        oPacket.EncodeShort(0);

        /*oPacket.encodeShort(2);
        oPacket.encodeInteger(1);
        oPacket.encodeInteger(0);
        oPacket.encodeInteger(2);
        oPacket.encodeInteger(0);
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
        oPacket.EncodeInteger(chr.getClient().getChannel() - 1);

        //Admin Byte
        oPacket.Encode(0);

        //Follow Feature (wOldDriverID)
        oPacket.EncodeInteger(0);

        //Logging Into Handler (1) Changing Map (2) bPopupDlg
        oPacket.Encode(bCharacterData ? 1 : 2);

        //oPacket.encode(1); // bPopupDlg
        //?
        oPacket.EncodeInteger(0);

        //nFieldWidth (Map Width)
        oPacket.EncodeInteger(800);

        //nFieldHeight (Map Height)
        oPacket.EncodeInteger(600);

        //bCharacterData
        oPacket.Encode(bCharacterData);

        //Size (string (size->string))
        oPacket.EncodeShort(0); // notifier check (used for block reason)

        if (bCharacterData) {
            //3 randomized integers (For the critical damage calculation)
            chr.CRand().connectData(oPacket);

            PacketHelper.addCharacterInfo(oPacket, chr);
            //PacketHelper.addLuckyLogoutInfo(oPacket, false, null, null, null);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        } else {

            //bUsingBuffProtector (Calls the revive function upon death)
            oPacket.Encode(0);

            /*
	        if ( (*(TSingleton<CUserLocal>::ms_pInstance._m_pStr + 336) & 0xFFFFFFFE) == 18 || v13 )
		    {
		      CWvsContext::OnRevive(v2);
		      v2->m_bUsingBuffProtector = 0;
		    }
             */
            oPacket.EncodeInteger(to.getId());
            oPacket.Encode(spawnPoint);
            oPacket.EncodeInteger(chr.getStat().getHp());

            //Boolean (int + int)
            oPacket.Encode(0);

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
        oPacket.Encode(0);

        //pChatBlockReason
        oPacket.Encode(0);

        //Some Korean Event (Fame-up)
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));

        //paramFieldInit.nMobStatAdjustRate
        oPacket.EncodeInteger(100);

        //Party Map Experience Boolean (int + string(bgm) + int(fieldid))
        oPacket.Encode(0);

        /*if ( CInPacket::Decode1(iPacket) )
        {
          ZRef<CFieldCustom>::_Alloc(&paramFieldInit.pFieldCustom);
          CFieldCustom::Decode(paramFieldInit.pFieldCustom.p, iPacket);
        }*/
        //Boolean
        oPacket.Encode(0);

        /*
           if ( CInPacket::Decode1(iPacket) )
    		CWvsContext::OnInitPvPStat(v2); 
         */
        //bCanNotifyAnnouncedQuest
        oPacket.Encode(0);

        //Boolean ((int + byte(size))->(int, int, int))->(long, int, int)
        oPacket.Encode(0);

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
        oPacket.Encode(0);
        if (starPlanet) {
            //nRoundID
            oPacket.EncodeInteger(0);

            //the size, cannot exceed the count of 10
            oPacket.Encode(0);

            //anPoint
            oPacket.EncodeInteger(0);

            //anRanking
            oPacket.EncodeInteger(0);

            //atLastCheckRank (timeGetTime - 300000)
            oPacket.EncodeInteger(0);

            //ftShiningStarExpiredTime
            oPacket.EncodeLong(0);

            //nShiningStarPickedCount
            oPacket.EncodeInteger(0);

            //nRoundStarPoint
            oPacket.EncodeInteger(0);
        }

        //Boolean (int + byte + long)
        boolean aStarPlanetRoundInfo = false;
        oPacket.Encode(aStarPlanetRoundInfo);
        if (aStarPlanetRoundInfo) {
            //nStarPlanetRoundID
            oPacket.EncodeInteger(0);

            //nStarPlanetRoundState
            oPacket.Encode(0);

            //ftStarPlanetRoundEndDate
            oPacket.EncodeLong(0);
        }

        //CWvsContext::DecodeStarPlanetRoundInfo(pCtx, iPacket);
        oPacket.EncodeInteger(0);//CUser::DecodeTextEquipInfo

        //FreezeAndHotEventInfo::Decode
        oPacket.Encode(0); //nAccountType
        oPacket.EncodeInteger(0); //dwAccountID

        //CCUser::DecodeEventBestFriendInfo
        oPacket.EncodeInteger(0); //dwEventBestFriendAID
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet removeBGLayer(boolean remove, int map, byte layer, int duration) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.REMOVE_BG_LAYER.getValue());
        oPacket.Encode(remove ? 1 : 0); //Boolean show or remove
        oPacket.EncodeInteger(map);
        oPacket.Encode(layer); //Layer to show/remove
        oPacket.EncodeInteger(duration);

        return oPacket.ToPacket();
    }

    public static Packet setMapObjectVisible(List<Pair<String, Byte>> objects) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SET_MAP_OBJECT_VISIBLE.getValue());
        oPacket.Encode(objects.size());
        for (Pair<String, Byte> object : objects) {
            oPacket.EncodeString(object.getLeft());
            oPacket.Encode(object.getRight());
        }

        return oPacket.ToPacket();
    }

    public static Packet pvpBlocked(int type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.Encode(type);

        return oPacket.ToPacket();
    }

    public static Packet enchantResult(int result) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemInGameCubeResult.getValue());
        oPacket.EncodeInteger(result);//0=fail/1=sucess/2=idk/3=shows stats

        return oPacket.ToPacket();
    }

    public static Packet showEquipEffect() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetActiveEffectItem.getValue());

        return oPacket.ToPacket();
    }

    public static Packet showEquipEffect(int team) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetActiveEffectItem.getValue());
        oPacket.EncodeShort(team);

        return oPacket.ToPacket();
    }

    public static Packet MapEff(String path) {
        return environmentChange(path, 4, 0);//was 3
    }

    public static Packet MapNameDisplay(int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 4, 0);
    }

    public static Packet Aran_Start() {
        return environmentChange("Aran/balloon", 4, 0);
    }

    public static Packet musicChange(String song) {
        return environmentChange(song, 7, 0);//was 6
    }

    public static Packet showEffect(String effect) {
        return environmentChange(effect, 4, 0);//was 3
    }

    public static Packet playSound(String sound) {
        return environmentChange(sound, 5, 0);//was 4
    }

    public static Packet playSound(String sound, int delay) {
        return environmentChange(sound, 5, delay);//was 4
    }

    public static Packet environmentChange(String env, int mode, int delay) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BossEnvironment.getValue());
        oPacket.Encode(mode);
        oPacket.EncodeString(env);
        oPacket.EncodeInteger(delay); // 0x64

        return oPacket.ToPacket();
    }

    public static Packet trembleEffect(int type, int delay) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BossEnvironment.getValue());
        oPacket.Encode(1);
        oPacket.Encode(type);
        oPacket.EncodeInteger(delay);
        oPacket.EncodeShort(30);
        // oPacket.encodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet environmentMove(String env, int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MoveEnvironment.getValue());
        oPacket.EncodeString(env);
        oPacket.EncodeInteger(mode);

        return oPacket.ToPacket();
    }

    public static Packet getUpdateEnvironment(MapleMap map) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UpdateEnvironment.getValue());
        oPacket.EncodeInteger(map.getEnvironment().size());
        for (Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            oPacket.EncodeString(mp.getKey());
            oPacket.EncodeInteger(mp.getValue());
        }

        return oPacket.ToPacket();
    }

    public static Packet startMapEffect(String msg, int itemid, boolean active) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FieldEffect.getValue());
        //oPacket.encode(active ? 0 : 1);

        oPacket.EncodeInteger(itemid);
        if (active) {
            oPacket.EncodeString(msg);
        }
        oPacket.Encode(active ? 0 : 1); // Moved down here for v176

        return oPacket.ToPacket();
    }

    public static Packet removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static Packet CriticalGrowing(int critical) { // Prime Critical
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
        PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.CriticalGrowing);
        oPacket.EncodeShort(critical);
        oPacket.EncodeInteger(4220015);
        oPacket.Fill(0, 22);

        return oPacket.ToPacket();
    }

    public static Packet OnOffFlipTheCoin(boolean on) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserFlipTheCoinEnabled.getValue());
        oPacket.Encode(on ? 1 : 0);

        return oPacket.ToPacket();
    }

    /**
     * Create the map environment for when elite boss enters. void __thiscall CField::OnEliteState(CField *this, CInPacket *iPacket)
     *
     * @return
     */
    /* public static Packet getEliteState() {
        OutPacket oPacket = new OutPacket(80);
        
        oPacket.encodeShort(SendPacketOpcode.ELITE_STATE.getValue());
        
        final int eliteState = 2;
        final int v3 = 0;
        
        oPacket.encodeInteger(eliteState); // this->m_nEliteState = CInPacket::Decode4(iPacket);
        oPacket.encodeInteger(v3); // v3 = CInPacket::Decode4(iPacket);
        
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
    public static Packet getGMEffect(int value, int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GM_EFFECT.getValue());
        oPacket.Encode(value);
        oPacket.Fill(0, 17);

        return oPacket.ToPacket();
    }

    public static Packet showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OX_QUIZ.getValue());
        oPacket.Encode(askQuestion ? 1 : 0);
        oPacket.Encode(questionSet);
        oPacket.EncodeShort(questionId);

        return oPacket.ToPacket();
    }

    /**
     * Used to show description in maps such as Ola Ola [109030001] as specified in WZ file
     *
     * @return
     */
    public static Packet showEventInstructions() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet getPVPClock(int type, int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Clock.getValue());
        oPacket.Encode(3);
        oPacket.Encode(type);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet getBanBanClock(int time, int direction) {
        OutPacket packet = new OutPacket(80);
        packet.EncodeShort(SendPacketOpcode.Clock.getValue());
        packet.Encode(5);
        packet.Encode(direction); //0:?????? 1:????
        packet.EncodeInteger(time);
        return packet.ToPacket();
    }

    public static Packet getClock(int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Clock.getValue());
        oPacket.Encode(2);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet getClockTime(int hour, int min, int sec) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Clock.getValue());
        oPacket.Encode(1);
        oPacket.Encode(hour);
        oPacket.Encode(min);
        oPacket.Encode(sec);

        return oPacket.ToPacket();
    }

    public static Packet boatPacket(int effect, int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ContiMove.getValue());
        oPacket.Encode(effect);
        oPacket.Encode(mode);

        return oPacket.ToPacket();
    }

    public static Packet setBoatState(int effect) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ContiState.getValue());
        oPacket.Encode(effect);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet stopClock() {
        return (new OutPacket(10)).Encode(getPacketFromHexString(Integer.toHexString(SendPacketOpcode.DestroyClock.getValue()) + " 00")).ToPacket();
    }

    public static Packet showAriantScoreBoard() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ShowArenaResult.getValue());

        return oPacket.ToPacket();
    }

    public static Packet quickSlot(String skil) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.QuickslotMappedInit.getValue());
        oPacket.Encode(skil == null ? 0 : 1);
        if (skil != null) {
            String[] slots = skil.split(",");
            for (int i = 0; i < 8; i++) {
                oPacket.EncodeInteger(Integer.parseInt(slots[i]));
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet getMovingPlatforms(MapleMap map) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FootHoldMove.getValue());
        oPacket.EncodeInteger(map.getSharedMapResources().footholds.getMovingPlatforms().size());
        for (Map_MaplePlatform mp : map.getSharedMapResources().footholds.getMovingPlatforms()) {
            oPacket.EncodeString(mp.name);
            oPacket.EncodeInteger(mp.start);
            oPacket.EncodeInteger(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                oPacket.EncodeInteger(mp.SN.get(x));
            }
            oPacket.EncodeInteger(mp.speed);
            oPacket.EncodeInteger(mp.x1);
            oPacket.EncodeInteger(mp.x2);
            oPacket.EncodeInteger(mp.y1);
            oPacket.EncodeInteger(mp.y2);
            oPacket.EncodeInteger(mp.x1);
            oPacket.EncodeInteger(mp.y1);
            oPacket.EncodeShort(mp.r);
        }

        return oPacket.ToPacket();
    }

    public static Packet sendPVPMaps() {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PvPStatusResult.getValue());
        oPacket.Encode(3); //max amount of players
        for (int i = 0; i < 20; i++) {
            oPacket.EncodeInteger(10); //how many peoples in each map
        }
        oPacket.Fill(0, 124);
        oPacket.EncodeShort(150); ////PVP 1.5 EVENT!
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet gainForce(int oid, int count, int color) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(1); // 0 = remote user?
        oPacket.EncodeInteger(oid);
        byte newcheck = 0;
        oPacket.EncodeInteger(newcheck); //unk
        if (newcheck > 0) {
            oPacket.EncodeInteger(0); //unk
            oPacket.EncodeInteger(0); //unk
        }
        oPacket.Encode(0);
        oPacket.EncodeInteger(4); // size, for each below
        oPacket.EncodeInteger(count); //count
        oPacket.EncodeInteger(color); //color, 1-10 for demon, 1-2 for phantom
        oPacket.EncodeInteger(0); //unk
        oPacket.EncodeInteger(0); //unk
        return oPacket.ToPacket();
    }

    public static Packet gainForce(boolean isRemote, User chr, List<Integer> oid, int type, int skillid, List<Pair<Integer, Integer>> forceInfo, Point monsterpos, int throwingStar) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        // int orbitalFlame = GameConstants.getOrbitalFlame(skillid);
        oPacket.Encode(isRemote);
        if (isRemote) {
            oPacket.EncodeInteger(chr.getId());
        }
        oPacket.EncodeInteger(isRemote ? oid.get(0) : chr.getId());// dwTargetID
        oPacket.EncodeInteger(type); // ForceAtomType

        if (!(type == 0 || type == 9 || type == 14)) {
            oPacket.Encode(1);
            if (GameConstants.isSpecialForce(type)) {
                oPacket.EncodeInteger(oid.size()); // size
                for (int i = 0; i < oid.size(); i++) {
                    oPacket.EncodeInteger(oid.get(i));
                }
            } else {
                oPacket.EncodeInteger(oid.get(0));
            }
            oPacket.EncodeInteger(skillid); //skillid
        }

        for (Pair<Integer, Integer> info : forceInfo) {
            oPacket.Encode(1); // while on/off
            oPacket.EncodeInteger(info.left); // count(dwKey)
            oPacket.EncodeInteger(info.right); // color(nInc)
            oPacket.EncodeInteger(Randomizer.rand(15, 29));//nFirstImpact
            oPacket.EncodeInteger(Randomizer.rand(5, 6));//nSecondImpact
            oPacket.EncodeInteger(Randomizer.rand(35, 50));//nAngle
            oPacket.EncodeInteger((skillid / 1000000) == 14 ? 1 : 0); //nStartDelay
            oPacket.EncodeInteger(0); //ptStart.x
            oPacket.EncodeInteger(0); //ptStart.y
            oPacket.EncodeInteger((skillid / 1000000) == 14 ? 51399013 : 0); // dwCreateTime
            //  oPacket.EncodeInteger(GameConstants.getOrbitalCount(orbitalFlame)); //nMaxHitCount
            // maybe mroe int
        }

        oPacket.Encode(0); // where read??

        if (type == 11) {
            oPacket.EncodeInteger(chr.getPosition().x);
            oPacket.EncodeInteger(chr.getPosition().y);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }
        if (type == 9 || type == 15) {
            oPacket.EncodeInteger(0x1E3);
            oPacket.EncodeInteger(-106);
            oPacket.EncodeInteger(0x1F7);
            oPacket.EncodeInteger(-86);
        }
        if (type == 16) {
            oPacket.EncodeInteger(543);
            oPacket.EncodeInteger(-325);
        }
        if (type == 17) {
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }
        if (type == 18) {
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }

        return oPacket.ToPacket();
    }

    public static Packet getAndroidTalkStyle(int npc, String talk, int... args) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
        oPacket.Encode(4);
        oPacket.EncodeInteger(npc);
        oPacket.EncodeShort(10);
        oPacket.EncodeString(talk);
        oPacket.Encode(args.length);

        for (int i = 0; i < args.length; i++) {
            oPacket.EncodeInteger(args[i]);
        }
        return oPacket.ToPacket();
    }

    public static Packet getQuickMoveInfo(boolean show, List<QuickMoveNPC> qm) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetQuickMoveInfo.getValue());
        oPacket.Encode(qm.size() <= 0 ? 0 : show ? qm.size() : 0);
        if (show && qm.size() > 0) {
            for (QuickMoveNPC qmn : qm) {
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(qmn.getId());
                oPacket.EncodeInteger(qmn.getType());
                oPacket.EncodeInteger(qmn.getLevel());
                oPacket.EncodeString(qmn.getDescription());
                oPacket.EncodeLong(PacketHelper.getTime(-2));
                oPacket.EncodeLong(PacketHelper.getTime(-1));
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet spawnPlayerMapObject(User chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEnterField.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(chr.getLevel());
        oPacket.EncodeString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if (ultExplorer != null && ultExplorer.getCustomData() != null) {
            oPacket.EncodeString(ultExplorer.getCustomData());
        } else {
            oPacket.EncodeString("");
        }
        MapleGuild gs = Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            oPacket.EncodeString(gs.getName());
            oPacket.EncodeShort(gs.getLogoBG());
            oPacket.Encode(gs.getLogoBGColor());
            oPacket.EncodeShort(gs.getLogo());
            oPacket.Encode(gs.getLogoColor());
        } else {
            oPacket.Encode(new byte[8]);
        }

        oPacket.Encode(chr.getGender());
        oPacket.EncodeInteger(chr.getFame());
        oPacket.EncodeInteger(1); //m_nFarmLevel
        oPacket.EncodeInteger(0); //m_nNameTagMark

        // Remove certain effects upon entering the map in order to avoid disconnects, for now. 
        Utility.removeBuffFromMap(chr, CharacterTemporaryStat.ShadowServant);
        Utility.removeBuffFromMap(chr, CharacterTemporaryStat.ShadowIllusion);
        
        BuffPacket.encodeForRemote(oPacket, chr);

        oPacket.EncodeShort(chr.getJob());
        oPacket.EncodeShort(chr.getSubcategory());
        oPacket.EncodeInteger(chr.getStat().starForceEnhancement); //m_nTotalCHUC
        oPacket.EncodeInteger(0);
        writeCharacterLook(oPacket, chr, true);
        oPacket.EncodeInteger(0); // Unknown
        oPacket.EncodeInteger(0); // Unknown

        // Begins a sub (sub_1158EC0) 188T
        oPacket.EncodeInteger(0); // Unknown
        oPacket.EncodeInteger(0); // Unknown
        oPacket.EncodeInteger(0); // Unknown // Encode 2 ints per size
        // End Sub

        oPacket.EncodeInteger(0); //m_dwDriverID
        oPacket.EncodeInteger(0); //m_dwPassenserID
        oPacket.EncodeInteger(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //nChocoCount
        oPacket.EncodeInteger(chr.getItemEffect()); //nActiveEffectItemID
        //oPacket.EncodeInteger(0); //nMonkeyEffectItemID
        //MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(124000));
        //oPacket.EncodeInteger(stat != null && stat.getCustomData() != null ? Integer.parseInt(stat.getCustomData()) : 0); //nActiveNickItemID (Title)
        oPacket.EncodeInteger(0); //nDamageSkin
        oPacket.EncodeInteger(0); //ptPos.x
        oPacket.EncodeInteger(0); //nDemonWingID
        oPacket.EncodeInteger(0); //nKaiserWingID
        oPacket.EncodeInteger(0); //nKaiserTailID
        oPacket.EncodeInteger(0); //m_nCompletedSetItemID
        oPacket.EncodeShort(-1); //m_nFieldSeatID
        oPacket.EncodeInteger(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0); //m_nPortableChairID
        oPacket.EncodeInteger(0);// if (int > 0), decodestr
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.Encode(0); // Unknown
        oPacket.EncodeShort(chr.getTruePosition().x);
        oPacket.EncodeShort(chr.getTruePosition().y);
        oPacket.Encode(chr.getStance());
        oPacket.EncodeShort(chr.getFh());
        oPacket.Encode(0);
        oPacket.Encode(0); // if this byte is > 0, do a loop and write an int for pet
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.Encode(0);//CAvatar::SetMechanicHUE
        oPacket.EncodeInteger(chr.getMount().getLevel());
        oPacket.EncodeInteger(chr.getMount().getExp());
        oPacket.EncodeInteger(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(oPacket, chr);
        oPacket.Encode(chr.getChalkboard() != null);

        if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
            oPacket.EncodeString(chr.getChalkboard());
        }

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(oPacket, rings.getLeft());
        addRingInfo(oPacket, rings.getMid());
        addMRingInfo(oPacket, rings.getRight(), chr);

        oPacket.Encode(0);//mask
        oPacket.EncodeInteger(0); //v4->m_nEvanDragonGlide_Riding

        if (GameConstants.isKaiser(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            oPacket.EncodeInteger(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            oPacket.EncodeInteger(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "primium");
            oPacket.Encode(x == null ? 0 : Integer.parseInt(x));
        }

        oPacket.EncodeInteger(0); //CUser::SetMakingMeisterSkillEff

        PacketHelper.addFarmInfo(oPacket, chr.getClient(), 0);
        for (int i = 0; i < 5; i++) {
            oPacket.Encode(-1);
        }

        oPacket.EncodeInteger(0); // LOOP: If int > 0, write a string
        oPacket.Encode(1);

        // TO-DO: If it is a Honey Butterfly mount, write an  int. If that int > 0, loop and write an int on each iteration.
        oPacket.Encode(0); // if byte > 0 && another byte, decode two ints, two shorts, something to do with 12101025 (Flashfire)
        oPacket.Encode(0);//CUser::StarPlanetRank::Decode
        oPacket.EncodeInteger(0); //sub_15ECDF0
        oPacket.EncodeInteger(0); //CUser::DecodeTextEquipInfo
        oPacket.Encode(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInteger(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInteger(0); //CUser::DecodeEventBestFriendInfo
        oPacket.Encode(0); //CUserRemote::OnKinesisPsychicEnergyShieldEffect
        oPacket.Encode(1);//asume this and the next int is some waterEvent shit
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(0);
        oPacket.EncodeString("");
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0); //sub_15B8CB0

        return oPacket.ToPacket();
    }

    // Back up of spawnPlayerMapObject (January 6, 2018)
    /*public static Packet spawnPlayerMapObject(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEnterField.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(chr.getLevel());
        oPacket.EncodeString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if (ultExplorer != null && ultExplorer.getCustomData() != null) {
            oPacket.EncodeString(ultExplorer.getCustomData());
        } else {
            oPacket.EncodeString("");
        }
        MapleGuild gs = Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            oPacket.EncodeString(gs.getName());
            oPacket.EncodeShort(gs.getLogoBG());
            oPacket.Encode(gs.getLogoBGColor());
            oPacket.EncodeShort(gs.getLogo());
            oPacket.Encode(gs.getLogoColor());
        } else {
            oPacket.Encode(new byte[8]);
        }

        oPacket.Encode(chr.getGender());
        oPacket.EncodeInteger(chr.getFame());
        oPacket.EncodeInteger(1); //m_nFarmLevel
        oPacket.EncodeInteger(0); //m_nNameTagMark

        BuffPacket.encodeForRemote(oPacket, chr);

        oPacket.EncodeShort(chr.getJob());
        oPacket.EncodeShort(chr.getSubcategory());
        oPacket.EncodeInteger(chr.getStat().starForceEnhancement); //m_nTotalCHUC
        writeCharacterLook(oPacket, chr, true);
        oPacket.EncodeInteger(0); //m_dwDriverID
        oPacket.EncodeInteger(0); //m_dwPassenserID
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //nChocoCount
        oPacket.EncodeInteger(chr.getItemEffect()); //nActiveEffectItemID
        oPacket.EncodeInteger(0); //nMonkeyEffectItemID
        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(124000));
        oPacket.EncodeInteger(stat != null && stat.getCustomData() != null ? Integer.parseInt(stat.getCustomData()) : 0); //nActiveNickItemID (Title)
        oPacket.EncodeInteger(0); //nDamageSkin
        oPacket.EncodeInteger(0); //ptPos.x
        oPacket.EncodeInteger(0); //nDemonWingID
        oPacket.EncodeInteger(0); //nKaiserWingID
        oPacket.EncodeInteger(0); //nKaiserTailID
        oPacket.EncodeInteger(0); //m_nCompletedSetItemID
        oPacket.EncodeShort(-1); //m_nFieldSeatID
        oPacket.EncodeInteger(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0); //m_nPortableChairID
        oPacket.EncodeInteger(0);// if (int > 0), decodestr
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeShort(chr.getTruePosition().x);
        oPacket.EncodeShort(chr.getTruePosition().y);
        oPacket.Encode(chr.getStance());
        oPacket.EncodeShort(chr.getFh());
        oPacket.Encode(0); // bAdminClient boolean
        oPacket.Encode(chr.getPets().size() > 0); // if this byte is > 0, do a loop and write an int for pet
        
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                oPacket.Encode(1);
                oPacket.EncodeInteger(chr.getPetIndex(pet));
                PetPacket.addPetData(chr, pet);
            }
        }
        
        //for (int i = 0; i < 3; i++) {
        //    MaplePet pPet = chr.getPet(i);
        //    if (pPet.getSummoned()) {
        //        oPacket.Encode(1);
        //        oPacket.EncodeInteger(chr.getPetIndex(pPet));
        //        PetPacket.addPetData(chr, pPet);
        //    }
        //}
        
        //oPacket.Encode(0);
        oPacket.Encode(0); 
        oPacket.Encode(0);//CAvatar::SetMechanicHUE
        oPacket.EncodeInteger(chr.getMount().getLevel());
        oPacket.EncodeInteger(chr.getMount().getExp());
        oPacket.EncodeInteger(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(oPacket, chr);
        oPacket.Encode(chr.getChalkboard() != null);

        if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
            oPacket.EncodeString(chr.getChalkboard());
        }

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(oPacket, rings.getLeft());
        addRingInfo(oPacket, rings.getMid());
        addMRingInfo(oPacket, rings.getRight(), chr);

        oPacket.Encode(0);//mask
        oPacket.EncodeInteger(0); //v4->m_nEvanDragonGlide_Riding

        if (GameConstants.isKaiser(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            oPacket.EncodeInteger(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            oPacket.EncodeInteger(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "primium");
            oPacket.Encode(x == null ? 0 : Integer.parseInt(x));
        }

        oPacket.EncodeInteger(0); //CUser::SetMakingMeisterSkillEff

        PacketHelper.addFarmInfo(oPacket, chr.getClient(), 0);
        for (int i = 0; i < 5; i++) {
            oPacket.Encode(-1);
        }

        oPacket.EncodeInteger(0); // LOOP: If int > 0, write a string
        oPacket.Encode(1);

        // TO-DO: If it is a Honey Butterfly mount, write an  int. If that int > 0, loop and write an int on each iteration.
        oPacket.Encode(0); // if byte > 0 && another byte, decode two ints, two shorts, something to do with 12101025 (Flashfire)
        oPacket.Encode(0);//CUser::StarPlanetRank::Decode
        oPacket.EncodeInteger(0); //sub_15ECDF0
        oPacket.EncodeInteger(0); //CUser::DecodeTextEquipInfo
        oPacket.Encode(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInteger(0); //CUser::DecodeFreezeHotEventInfo
        oPacket.EncodeInteger(0); //CUser::DecodeEventBestFriendInfo
        oPacket.Encode(0); //CUserRemote::OnKinesisPsychicEnergyShieldEffect
        oPacket.Encode(1);//asume this and the next int is some waterEvent shit
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(0);
        oPacket.EncodeString("");
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0); //sub_15B8CB0

        return oPacket.ToPacket();
    }*/
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

    public static Packet removePlayerFromMap(int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserLeaveField.getValue());
        oPacket.EncodeInteger(cid);

        return oPacket.ToPacket();
    }

    public static Packet getChatText(int cidfrom, String text, boolean whiteBG, boolean appendToChatLogList) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserChat.getValue());
        oPacket.EncodeInteger(cidfrom);
        oPacket.Encode(whiteBG ? 1 : 0);
        oPacket.EncodeString(text);//
        oPacket.Encode(appendToChatLogList ? 0 : 1); // Changed to the ! opposite on v176
        oPacket.Encode(0);
        oPacket.Encode(-1);

        return oPacket.ToPacket();
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
    public static Packet getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit, int item, int scroll) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemUpgradeEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(scrollSuccess == ScrollResult.SUCCESS ? 1 : scrollSuccess == ScrollResult.CURSE ? 2 : 0);
        oPacket.Encode(legendarySpirit);
        oPacket.EncodeInteger(scroll);
        oPacket.EncodeInteger(item);
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    /**
     * This packet displays the effect after using a magnifying glass
     *
     * @param int chr - character id
     * @param short pos - position of the item to reveal
     *
     */
    public static Packet showMagnifyingEffect(int chr, short pos) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserItemReleaseEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.EncodeShort(pos);
        oPacket.Encode(0);

        return oPacket.ToPacket();
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
    public static Packet showPotentialReset(int chr, boolean success, int itemid) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserItemUnreleaseEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(success);
        oPacket.EncodeInteger(itemid);
        return oPacket.ToPacket();
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
    public static Packet showLuckyEffect(int cId, boolean success, int itemId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserItemLuckyItemEffect.getValue());
        oPacket.EncodeInteger(cId);
        oPacket.Encode(success);
        oPacket.EncodeInteger(itemId); //guess
        return oPacket.ToPacket();
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
    public static Packet showMemorialEffect(int chr, boolean success, int cubeId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserItemMemorialCubeEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(success);
        oPacket.EncodeInteger(cubeId);
        return oPacket.ToPacket();
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
    public static Packet showBonusPotentialReset(int chr, boolean success, int cubeId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserItemAdditionalUnReleaseEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(success);
        oPacket.EncodeInteger(cubeId);
        return oPacket.ToPacket();
    }

    public static Packet showNebuliteEffect(int chr, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ShowNebuliteEffect.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(success);
        oPacket.EncodeString(success ? "Successfully mounted Nebulite." : "Failed to mount Nebulite.");

        return oPacket.ToPacket();
    }

    public static Packet useNebuliteFusion(int cid, int itemId, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SHOW_FUSION_EFFECT.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(success);
        oPacket.EncodeInteger(itemId);

        return oPacket.ToPacket();
    }

    public static Packet pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackMonster> attack) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserHitByUser.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(playerLevel);
        oPacket.EncodeInteger(skill);
        oPacket.Encode(skillLevel);
        oPacket.EncodeInteger(linkSkill != skill ? linkSkill : 0);
        oPacket.Encode(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        oPacket.Encode(direction);
        oPacket.Encode(movementSkill ? 1 : 0);
        oPacket.Encode(pushTarget ? 1 : 0);
        oPacket.Encode(pullTarget ? 1 : 0);
        oPacket.Encode(0);
        oPacket.EncodeShort(stance);
        oPacket.Encode(speed);
        oPacket.Encode(mastery);
        oPacket.EncodeInteger(projectile);
        oPacket.EncodeInteger(chargeTime);
        oPacket.EncodeInteger(range);
        oPacket.Encode(attack.size());
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.Encode(attackCount);
        oPacket.Encode(0);

        for (AttackMonster p : attack) {
            oPacket.EncodeInteger(p.getObjectId());
            oPacket.EncodeInteger(0);
            oPacket.EncodePosition(p.getPosition());
            oPacket.Encode(0);
            oPacket.EncodeInteger(0);
            for (Pair<Long, Boolean> atk : p.getAttacks()) {
                oPacket.EncodeLong(atk.left);
                oPacket.Encode(atk.right);
                oPacket.EncodeShort(0);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FieldWeather_Add.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(mistSkill);
        oPacket.Encode(mistLevel);
        oPacket.EncodeInteger(damage);
        oPacket.Encode(8);
        oPacket.EncodeInteger(1000);

        return oPacket.ToPacket();
    }

    public static Packet pvpCool(int cid, List<Integer> attack) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserResetAllDot.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(attack.size());
        for (int i : attack) {
            oPacket.EncodeInteger(i);
        }

        return oPacket.ToPacket();
    }

    public static Packet teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTeslaTriangle.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(sum1);
        oPacket.EncodeInteger(sum2);
        oPacket.EncodeInteger(sum3);

        oPacket.Fill(0, 69);//test

        return oPacket.ToPacket();
    }

    public static Packet followEffect(int initiator, int replier, Point toMap) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserFollowCharacter.getValue());
        oPacket.EncodeInteger(initiator);
        oPacket.EncodeInteger(replier);
        oPacket.EncodeLong(0);
        if (replier == 0) {
            oPacket.Encode(toMap == null ? 0 : 1);
            if (toMap != null) {
                oPacket.EncodeInteger(toMap.x);
                oPacket.EncodeInteger(toMap.y);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet showPQReward(int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserShowPQReward.getValue());
        oPacket.EncodeInteger(cid);
        for (int i = 0; i < 6; i++) {
            oPacket.Encode(0);
        }

        return oPacket.ToPacket();
    }

    public static Packet craftMake(int cid, int something, int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMakingSkillResult.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(something);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMakingMeisterSkillEff.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(craftID);
        oPacket.EncodeInteger(ranking);
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeInteger(quantity);
        oPacket.EncodeInteger(exp);

        return oPacket.ToPacket();
    }

    public static Packet harvestResult(int cid, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserGatherResult.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(success ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet playerDamaged(int cid, int dmg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserExplode.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(dmg);

        return oPacket.ToPacket();
    }

    public static Packet showPyramidEffect(int chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PyramidLethalAttack.getValue());
        oPacket.EncodeInteger(chr);
        oPacket.Encode(1);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet pamsSongEffect(int cid) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserWaitQueueReponse.getValue());
        oPacket.EncodeInteger(cid);
        return oPacket.ToPacket();
    }

    public static Packet enableHaku(MapleHaku haku) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.FoxManExclResult.getValue());
        oPacket.EncodeInteger(haku.getOwner());
        return oPacket.ToPacket();
    }

    public static Packet changeHakuEquip(MapleHaku haku, boolean change, boolean enableActions) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FoxManModified.getValue());
        oPacket.EncodeInteger(haku.getOwner());
        oPacket.Encode(change);
        if (change) {
            oPacket.EncodeInteger(haku.getEquipId());
        }
        oPacket.Encode(enableActions);
        return oPacket.ToPacket();
    }

    public static Packet transformHaku(int cid, boolean change) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FoxManShowChangeEffect.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(change ? 2 : 1);
        return oPacket.ToPacket();
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
    public static Packet spawnHaku(MapleHaku h, boolean oldForm) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FoxManEnterField.getValue());
        oPacket.EncodeInteger(h.getOwner());
        oPacket.EncodeShort(oldForm ? 1 : 0);  //value % map size
        oPacket.EncodePosition(h.getPosition());
        oPacket.Encode(h.getStance());//m_nMoveAction
        oPacket.EncodeShort(h.getFootHold());
        oPacket.EncodeInteger(0);//m_nUpgrade
        oPacket.EncodeInteger(0);//m_anFoxManEquip[0]
        return oPacket.ToPacket();
    }

    public static Packet moveHaku(MapleHaku h, Point pos, List<LifeMovementFragment> res) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FoxManMove.getValue());
        oPacket.EncodeInteger(h.getOwner());

        PacketHelper.serializeMovementList(oPacket, h, res, 0);
        return oPacket.ToPacket();
    }

    public static Packet destroyHaku(int charId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.FoxManMove.getValue());
        oPacket.EncodeInteger(charId);
        return oPacket.ToPacket();
    }

    public static Packet spawnDragon(MapleDragon d) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DragonEnterField.getValue());
        oPacket.EncodeInteger(d.getOwner());
        oPacket.EncodeInteger(d.getPosition().x);
        oPacket.EncodeInteger(d.getPosition().y);
        oPacket.Encode(d.getStance());
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(d.getJobId());
        return oPacket.ToPacket();
    }

    public static Packet removeDragon(int chrid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DragonVanish_Script.getValue());
        oPacket.EncodeInteger(chrid);
        return oPacket.ToPacket();
    }

    public static Packet moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DragonMove.getValue());
        oPacket.EncodeInteger(d.getOwner());

        PacketHelper.serializeMovementList(oPacket, d, moves, 0);

        return oPacket.ToPacket();
    }

    public static Packet spawnAndroid(User chr, MapleAndroid android) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.AndroidEnterField.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(MapleAndroid.getAndroidTemplateId(android.getItem().getItemId()));
        oPacket.EncodeShort(android.getPos().x);
        oPacket.EncodeShort(android.getPos().y - 20);
        oPacket.Encode(android.getStance());
        oPacket.EncodeShort(chr.getFh());
        oPacket.EncodeShort(android.getSkin() - 2000);
        oPacket.EncodeShort(android.getHair() - 30000);
        oPacket.EncodeShort(android.getFace() - 20000);
        oPacket.EncodeString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            oPacket.EncodeInteger(item != null ? item.getItemId() : 0);
        }

        return oPacket.ToPacket();
    }

    public static Packet moveAndroid(User chr, List<LifeMovementFragment> res) {
        OutPacket oPacket = new OutPacket(80);

        MapleAndroid android = chr.getAndroid();

        oPacket.EncodeShort(SendPacketOpcode.AndroidMove.getValue());
        oPacket.EncodeInteger(chr.getId());

        PacketHelper.serializeMovementList(oPacket, android, res, 0);

        return oPacket.ToPacket();
    }

    public static Packet showAndroidEmotion(int cid, byte emo1) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.AndroidActionSet.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(0);//nActingSet
        oPacket.Encode(emo1);
        return oPacket.ToPacket();
    }

    public static Packet updateAndroidLook(boolean itemOnly, User cid, MapleAndroid android, boolean enableActions) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AndroidModified.getValue());
        oPacket.EncodeInteger(cid.getId());
        oPacket.Encode(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i = (short) (i - 1)) {
                Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                oPacket.EncodeInteger(item != null ? item.getItemId() : 0);
            }
        } else {
            oPacket.EncodeShort(0);
            oPacket.EncodeShort(android.getHair() - 30000);
            oPacket.EncodeShort(android.getFace() - 20000);
            oPacket.EncodeString(android.getName());
        }
        oPacket.Encode(enableActions);
        return oPacket.ToPacket();
    }

    public static Packet deactivateAndroid(int cid) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.AndroidLeaveField.getValue());
        oPacket.EncodeInteger(cid);
        return oPacket.ToPacket();
    }

    public static Packet removeFamiliar(int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarEnterField.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeShort(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet spawnFamiliar(MonsterFamiliar mf, boolean spawn, boolean respawn) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(respawn ? SendPacketOpcode.FamiliarTransferField.getValue() : SendPacketOpcode.FamiliarEnterField.getValue());
        oPacket.EncodeInteger(mf.getCharacterId());
        oPacket.Encode(spawn ? 1 : 0);
        oPacket.Encode(respawn ? 1 : 0);
        oPacket.Encode(0);
        if (spawn) {
            oPacket.EncodeInteger(mf.getFamiliar());
            oPacket.EncodeInteger(mf.getFatigue());
            oPacket.EncodeInteger(mf.getVitality() * 300); //max fatigue
            oPacket.EncodeString(mf.getName());
            oPacket.EncodePosition(mf.getTruePosition());
            oPacket.Encode(mf.getStance());
            oPacket.EncodeShort(mf.getFh());
        }

        return oPacket.ToPacket();
    }

    /**
     * This packet handles the movement for Familiars
     *
     * @param MonsterFamiliar - fam
     * @param List<LifeMovementFragment> - moves
     *
     * @return oPacket
     */
    public static Packet moveFamiliar(MonsterFamiliar fam, List<LifeMovementFragment> moves) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarMove.getValue());
        oPacket.EncodeInteger(fam.getCharacterId());
        oPacket.Encode(0); //idk
        PacketHelper.serializeMovementList(oPacket, fam, moves, 0);

        return oPacket.ToPacket();
    }

    /**
     * This packet handles the movement for Familiars
     *
     * @param MonsterFamiliar - fam
     * @param List<LifeMovementFragment> - moves
     *
     * @return oPacket
     */
    public static Packet touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarAction.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(0);
        oPacket.Encode(unk);
        oPacket.EncodeInteger(objectid); //possibly mobid or oid for mob
        oPacket.EncodeInteger(type);
        oPacket.EncodeInteger(delay);
        oPacket.EncodeInteger(damage); //

        return oPacket.ToPacket();
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
    public static Packet familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarAttack.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(0);// idk
        oPacket.Encode(unk);
        oPacket.Encode(attackPair.size());
        for (Triple<Integer, Integer, List<Integer>> s : attackPair) {
            oPacket.EncodeInteger(s.left);
            oPacket.Encode(s.mid);
            oPacket.Encode(s.right.size());
            for (int damage : s.right) {
                oPacket.EncodeInteger(damage);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet renameFamiliar(MonsterFamiliar mf) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarNameResult.getValue());
        oPacket.EncodeInteger(mf.getCharacterId());
        oPacket.Encode(0);
        oPacket.EncodeInteger(mf.getFamiliar());
        oPacket.EncodeString(mf.getName());

        return oPacket.ToPacket();
    }

    public static Packet updateFamiliar(MonsterFamiliar mf) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarFatigueResult.getValue());
        oPacket.EncodeInteger(mf.getCharacterId());
        oPacket.EncodeInteger(mf.getFamiliar());
        oPacket.EncodeInteger(mf.getFatigue());
        oPacket.EncodeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2L));

        return oPacket.ToPacket();
    }

    public static Packet movePlayer(User chr, List<LifeMovementFragment> moves) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMove.getValue());
        oPacket.EncodeInteger(chr.getId());
        PacketHelper.serializeMovementList(oPacket, chr, moves, 0);

        return oPacket.ToPacket();
    }

    // <editor-fold defaultstate="visible" desc="Third party attack display"> 
    public static Packet closeRangeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, boolean energy, int lvl, byte mastery, byte unk, int charge) {
        return addAttackInfo(energy ? 4 : 0, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, 0, null, 0);
    }

    public static Packet rangedAttack(int cid, int tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackMonster> damage, Point pos, int lvl, byte mastery, byte unk) {
        return addAttackInfo(1, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, 0);
    }

    public static Packet strafeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackMonster> damage, Point pos, int lvl, byte mastery, byte unk, int ultLevel) {
        return addAttackInfo(2, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, ultLevel);
    }

    public static Packet magicAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, int charge, int lvl, byte unk) {
        return addAttackInfo(3, cid, tbyte, skill, level, display, speed, damage, lvl, (byte) 0, unk, charge, null, 0);
    }

    public static Packet addAttackInfo(int type, int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackMonster> damage, int lvl, byte mastery, byte unk, int charge, Point pos, int ultLevel) {
        OutPacket oPacket = new OutPacket(80);

        switch (type) {
            case 0:
                oPacket.EncodeShort(SendPacketOpcode.UserMeleeAttack.getValue());
                break;
            case 1:
            case 2:
                oPacket.EncodeShort(SendPacketOpcode.UserShootAttack.getValue());
                break;
            case 3:
                oPacket.EncodeShort(SendPacketOpcode.UserMagicAttack.getValue());
                break;
            default:
                oPacket.EncodeShort(SendPacketOpcode.UserBodyAttack.getValue());
                break;
        }

        oPacket.EncodeInteger(cid);
        oPacket.Encode(0);
        oPacket.Encode(tbyte);
        byte mobCount = (byte) (tbyte >> 4);
        byte damagePerMob = (byte) (tbyte & 0xF);
        oPacket.Encode(lvl);//moblvl
        oPacket.Encode(level);//skillvl
        if (level > 0) {
            oPacket.EncodeInteger(skill);
        }

        if (Skill.isZeroSkill(skill)) {
            short zero1 = 0;
            short zero2 = 0;
            oPacket.Encode(zero1 > 0 || zero2 > 0); //boolean
            if (zero1 > 0 || zero2 > 0) {
                oPacket.EncodeShort(zero1);
                oPacket.EncodeShort(zero2);
                //there is a full handler so better not write zero
            }
        }

        if (type == 2) {
            oPacket.Encode(ultLevel);
            if (ultLevel > 0) {
                oPacket.EncodeInteger(3220010);
            }
        }

        if (skill == 80001850) {
            oPacket.Encode(0); //boolean if true then int
        }
        if ((skill == 42001000 || skill > 42001004 && skill <= 42001006) || (skill == 40021185 || skill == 42001006 || skill == 80011067)) {
            oPacket.Encode(0); // Unknown
            // if above > 0 encode int
        }

        oPacket.Encode(0); //v20
        // nTime = v20 & 0x20
        // bRepeatAttack = v20 & 4
        // bShadowPartner = v20 & 8

        byte v22 = 0;
        oPacket.Encode(v22); //v22
        oPacket.EncodeInteger(0); //nOption3
        oPacket.EncodeInteger(0); //nBySummonedID

        /*
        if ((v22 & 2) != 0) { //bBuckShot
            oPacket.encodeInteger(0);//nSkillID
            oPacket.encodeInteger(0);//nSkillLvl
        }

        if ((v22 & 8) != 0) {
            oPacket.encode(0);//nPassiveAttackCount
        }
         */
        oPacket.EncodeShort(display);
        if (display <= 1616) {
            oPacket.Encode(-1);//v30 bDragon ataack and move action?
            oPacket.EncodeShort(0); // ptAttackRefPoint.x (Dragon specific)
            oPacket.EncodeShort(0); // ptAttackRefPoint.y (Dragon specific)
            oPacket.Encode(0); //bShowFixedDamage
            oPacket.Encode(6); //v206
            oPacket.Encode(speed); //nActionSpeed
            oPacket.Encode(mastery); //nMastery
            oPacket.EncodeInteger(charge); //nBulletItemID 
            for (AttackMonster monster : damage) {
                if (monster.getAttacks() != null) {
                    oPacket.EncodeInteger(monster.getObjectId());
                    oPacket.Encode(0);//v38-38)*
                    oPacket.Encode(0);//v38*
                    oPacket.Encode(0);//v38[1]
                    oPacket.EncodeShort(0);//v40
                    if (skill == 80001835 || skill == 42111002 || skill == 80011050) {
                        oPacket.Encode(0);//bKeyDown
                        //if bKeyDown annoying loop about how long and other shit
                    } else {
                        for (Pair<Long, Boolean> hits : monster.getAttacks()) {
                            oPacket.Encode(hits.right);//bCrit
                            oPacket.EncodeLong(hits.left); //Iterate over damage.
                        }
                    }
                    if (Kinesis.is_kinesis_psychiclock_skill(skill)) {
                        oPacket.EncodeInteger(0);//idk
                    }

                    if (skill == Blaster.ROCKET_RUSH) {
                        oPacket.Encode(0);
                    }
                }
            }
            if (skill == 2321001 || skill == 2221052 || skill == 11121052 || skill == 12121054) { //Keydown skills
                oPacket.EncodeInteger(0); //tKeyDown
            } else if (Skill.isSupernovaSkill(skill) || Skill.isScreenCenterAttackSkill(skill) || skill == 101000202 || skill == 101000102
                    || skill == 80001762 || skill == 80002212 || skill == 400041019 || skill == 400031016 || skill == 400041024) {
                oPacket.EncodeInteger(0);//attackRefPointx
                oPacket.EncodeInteger(0);//attackRefPointy
            }

            //if is_keydown_skill_rect_move_xyz : encode new position (2 shorts)
            if (Skill.isKeydownSkillRectMoveXY(skill)) {
                oPacket.EncodeShort(0);
                oPacket.EncodeShort(0);
            }
            if (skill == 51121009) {
                oPacket.Encode(0); //bEncodeFixedDamage
            } else if (skill == 112110003) {
                oPacket.EncodeInteger(0);
            } else if (skill == 42100007) {
                oPacket.EncodeShort(0);
                oPacket.Encode(0);
            }

            if (skill == 21120019 || skill == 37121052 || skill >= 400041002 && skill <= 400041005 || skill == 11121014 || skill == 5101004) {
                oPacket.EncodeInteger(0); // pTeleport.pt.x
                oPacket.EncodeInteger(0); // pTeleport.pt.y
            }
            if (skill == 400020009 || skill == 400020010 || skill == 400020011 || skill == 400021029 || skill == 400021053) {
                oPacket.EncodeShort(0);
                oPacket.EncodeShort(0);
            }
            if (Skill.IsUnknown5thJobFunc(skill)) {
                oPacket.EncodeInteger(0); // Unknown
                oPacket.Encode(false); // Unknown
            }
        }

        return oPacket.ToPacket();
    }
// </editor-fold>

    public static Packet skillEffect(User from, int skillId, byte level, short display, byte unk) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSkillPrepare.getValue());
        oPacket.EncodeInteger(from.getId());
        oPacket.EncodeInteger(skillId);
        oPacket.Encode(level);
        oPacket.EncodeShort(display);
        oPacket.Encode(unk);
        if (skillId == 13111020) {
            oPacket.EncodePosition(from.getPosition()); // Position
        }
        if (skillId == 27101202) {
            oPacket.EncodePosition(from.getPosition()); // Position
        }

        return oPacket.ToPacket();
    }

    public static Packet skillCancel(User from, int skillId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSkillCancel.getValue());
        oPacket.EncodeInteger(from.getId());
        oPacket.EncodeInteger(skillId);

        return oPacket.ToPacket();
    }

    /**
     * CUserRemote::OnHit
     *
     * This method deals with damage being done to the player
     *
     */
    public static Packet damagePlayer(int cid, PlayerDamageHandler.PlayerDamageType type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserHit.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(type.getType());
        oPacket.EncodeInteger(damage);
        oPacket.Encode(0); //bCritical
        //oPacket.encode(0); //goes to CUser::MakeIncDecHPEffect REMOVED v176+ ?
        if (type.getType() >= -1) {
            oPacket.EncodeInteger(monsteridfrom);
            oPacket.Encode(direction);
            oPacket.EncodeInteger(skillid);
            oPacket.EncodeInteger(pDMG);
            oPacket.Encode(0);
            if (pDMG > 0) {
                oPacket.Encode(pPhysical ? 1 : 0);
                oPacket.EncodeInteger(pID);
                oPacket.Encode(pType);
                oPacket.EncodePosition(pPos);
            }
            oPacket.Encode(offset);
            if (offset == 1) {
                oPacket.EncodeInteger(offset_d);
            }
        }
        oPacket.EncodeInteger(damage);
        if ((damage <= 0) || (fake > 0)) {
            oPacket.EncodeInteger(fake);
        }

        return oPacket.ToPacket();
    }

    public static Packet facialExpression(User from, int expression) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEmotion.getValue());
        oPacket.EncodeInteger(from.getId());
        oPacket.EncodeInteger(expression);
        oPacket.EncodeInteger(-1);
        oPacket.Encode(0);

        return oPacket.ToPacket();
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
    public static Packet directionFacialExpression(int expression, int duration) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEmotionLocal.getValue());
        oPacket.EncodeInteger(expression);
        oPacket.EncodeInteger(duration);
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static Packet itemEffect(int characterid, int itemid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetActiveEffectItem.getValue());
        oPacket.EncodeInteger(characterid);
        oPacket.EncodeInteger(itemid);

        return oPacket.ToPacket();
    }

    public static Packet showTitle(int characterid, int itemid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetActiveNickItem.getValue());
        oPacket.EncodeInteger(characterid);
        oPacket.EncodeInteger(itemid);

        return oPacket.ToPacket();
    }

    public static Packet showAngelicBusterTransformation(int characterid, int effectId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetDressUpState.getValue());
        oPacket.EncodeInteger(characterid);
        oPacket.EncodeInteger(effectId);

        return oPacket.ToPacket();
    }

    public static Packet setAngelicBusterTransformation(int bSet, int infinite) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetDressUpState.getValue());
        oPacket.Encode(bSet);
        oPacket.Encode(infinite);

        return oPacket.ToPacket();
    }

    public static Packet showChair(int characterid, int itemid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSetActivePortableChair.getValue());
        oPacket.EncodeInteger(characterid);
        oPacket.EncodeInteger(itemid);

        oPacket.EncodeInteger(0); // bPortableMessage
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0); // lTowerChair.size
        oPacket.Encode(false);
        oPacket.EncodeInteger(0);
        oPacket.Encode(false);

        return oPacket.ToPacket();
    }

    public static Packet updateCharLook(User chr, boolean second) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserAvatarModified.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(1);
        PacketHelper.addCharLook(oPacket, chr, false, second);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(oPacket, rings.getLeft());
        addRingInfo(oPacket, rings.getMid());
        addMRingInfo(oPacket, rings.getRight(), chr);
        oPacket.EncodeInteger(0); // completedSetItemID
        oPacket.EncodeInteger(0); // totalCHUC
        oPacket.EncodeInteger(0);
        return oPacket.ToPacket();
    }

    public static Packet updatePartyMemberHP(int cid, int curhp, int maxhp) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserHP.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(curhp);
        oPacket.EncodeInteger(maxhp);

        return oPacket.ToPacket();
    }

    public static Packet changeTeam(int cid, int type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserPvPTeamChanged.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(type);

        return oPacket.ToPacket();
    }

    public static Packet showHarvesting(int cid, int tool) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GatherActionSet.getValue());
        oPacket.EncodeInteger(cid);
        if (tool > 0) {
            oPacket.Encode(1);
            oPacket.Encode(0);
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(tool);
            oPacket.Fill(0, 30);
        } else {
            oPacket.Encode(0);
            oPacket.Fill(0, 33);
        }

        return oPacket.ToPacket();
    }

    public static Packet getPVPHPBar(int cid, int hp, int maxHp) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UpdatePvPHPTag.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(hp);
        oPacket.EncodeInteger(maxHp);

        return oPacket.ToPacket();
    }

    public static Packet cancelChair(int chrId, int id) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserSitResult.getValue());
        oPacket.EncodeInteger(chrId);
        if (id == -1) {
            oPacket.Encode(0);
        } else {
            oPacket.Encode(1);
            oPacket.EncodeShort(id);
        }

        return oPacket.ToPacket();
    }

    public static Packet instantMapWarp(byte portal) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTeleport.getValue());
        oPacket.Encode(0);
        oPacket.Encode(portal);

        return oPacket.ToPacket();
    }

    public static Packet updateQuestInfo(User c, int quest, int npc, byte progress) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserQuestResult.getValue());
        oPacket.Encode(progress);
        oPacket.EncodeInteger(quest);
        oPacket.EncodeInteger(npc);
        oPacket.EncodeInteger(0);

        if (c.isIntern()) {
            c.dropMessage(5, "[Quest Debug] Updating Quest ID : " + quest);
        }
        
        return oPacket.ToPacket();
    }

    public static Packet updateQuestFinish(int quest, int npc, int nextquest) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserQuestResult.getValue());
        oPacket.Encode(11);//was 10
        oPacket.EncodeInteger(quest);  // Version 174, this is an integer 
        oPacket.EncodeInteger(npc);
        oPacket.EncodeInteger(nextquest);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet sendHint(String hint, int width, int height) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserHint.getValue());
        oPacket.EncodeString(hint);
        oPacket.EncodeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
        oPacket.EncodeShort(Math.max(height, 5));
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet updateCombo(int value) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ModCombo.getValue());
        oPacket.EncodeInteger(value);

        return oPacket.ToPacket();
    }

    public static Packet rechargeCombo(int value) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.IncComboByComboRecharge.getValue());
        oPacket.EncodeInteger(value);

        return oPacket.ToPacket();
    }

    public static Packet getFollowMessage(String msg) {
        return getGameMessage(msg, (short) 11);
    }

    public static Packet getGameMessage(String msg, short colour) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserChatMsg.getValue());
        oPacket.EncodeShort(colour);
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet getBuffZoneEffect(int itemId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserBuffzoneEffect.getValue());
        oPacket.EncodeInteger(itemId);

        return oPacket.ToPacket();
    }

    public static Packet getTimeBombAttack() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserTimeBombAttack.getValue());
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(10);
        oPacket.EncodeInteger(6);

        return oPacket.ToPacket();
    }

    public static Packet moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserPassiveMove.getValue());
        oPacket.EncodeInteger(0);
        oPacket.EncodePosition(otherStart);
        oPacket.EncodePosition(myStart);
        PacketHelper.serializeMovementList(oPacket, null, moves, 0);
        oPacket.Encode(17);
        for (int i = 0; i < 8; i++) {
            oPacket.Encode(0);
        }
        oPacket.Encode(0);
        oPacket.EncodePosition(otherEnd);
        oPacket.EncodePosition(otherStart);
        oPacket.Fill(0, 100);

        return oPacket.ToPacket();
    }//CUser::OnPassiveMove

    public static Packet getFollowMsg(int opcode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserFollowCharacterFailed.getValue());
        oPacket.EncodeLong(opcode);

        return oPacket.ToPacket();
    }

    public static Packet registerFamiliar(MonsterFamiliar mf) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FamiliarRegister.getValue());
        oPacket.EncodeLong(mf.getId());
        mf.writeRegisterPacket(oPacket, false);
        oPacket.EncodeShort(mf.getVitality() >= 3 ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet createUltimate(int amount) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CreateNewCharacterResultPremiumAdventurer.getValue());
        oPacket.EncodeInteger(amount);

        return oPacket.ToPacket();
    }

    public static Packet harvestMessage(int oid, int msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GatherRequestResult.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(msg);

        return oPacket.ToPacket();
    }

    public static Packet openBag(int index, int itemId, boolean firstTime) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserBagItemUseResult.getValue());
        oPacket.EncodeInteger(index);
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeShort(firstTime ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet dragonBlink(int portalId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.RandomTeleportKey.getValue());
        oPacket.Encode(portalId);

        return oPacket.ToPacket();
    }

    /**
     * Single skill cooldown
     *
     * @param skillid
     * @param time
     * @return the packet
     */
    public static Packet skillCooldown(int skillid, int time) {
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
    public static Packet skillCooldown(List<Pair<Integer, Integer>> cooldowns) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SkillCooltimeSet.getValue());

        oPacket.EncodeInteger(cooldowns.size()); // number of skills to cooldown, new in v170
        for (Pair<Integer, Integer> cooldown : cooldowns) {
            oPacket.EncodeInteger(cooldown.getLeft());
            oPacket.EncodeInteger(cooldown.getRight());
        }
        return oPacket.ToPacket();
    }

    public static Packet dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte nEnterType) {
        return dropItemFromMapObject(drop, null, dropfrom, dropto, nEnterType);
    }

    public static Packet dropItemFromMapObject(MapleMapItem drop, Mob mob, Point dropfrom, Point dropto, byte nEnterType) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DropEnterField.getValue());
        oPacket.Encode(0); //eDropType
        oPacket.Encode(nEnterType); //nEnterType
        oPacket.EncodeInteger(drop.getObjectId()); //dwId
        oPacket.Encode(drop.getMeso() > 0); //bIsMoney
        oPacket.EncodeInteger(0); //nDropMotionType
        oPacket.EncodeInteger(0); //nDropSpeed
        oPacket.Encode(drop.isNoMoveItem()); //bNoMove
        oPacket.Fill(0, 3);
        oPacket.EncodeInteger(drop.getItemId()); //nInfo
        oPacket.EncodeInteger(drop.getOwner()); //dwOwnerID
        oPacket.Encode(drop.getDropType()); //nOwnType
        oPacket.EncodePosition(dropto);
        oPacket.EncodeInteger(mob != null ? mob.getId() : 0); //dwSourceID
        oPacket.Encode(0);
        oPacket.Encode(0);
        if (nEnterType != 2) {
            oPacket.EncodePosition(dropfrom);
            oPacket.EncodeInteger(0);//tDelay
        }
        oPacket.Encode(0); //bExplosiveDrop
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
	        oPacket.encodeInteger(0); //bCollisionPickUp
	        oPacket.encode(0);
	        oPacket.encode(0); //bPrepareCollisionPickUp
         */
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(oPacket, drop.getItem().getExpiration());
        }
        oPacket.Encode(!drop.isPlayerDrop()); //bByPet
        oPacket.Encode(0);

        /*
        if ( CInPacket::Decode1(iPacket) ) {
	          pra[0] = -1073471723;
	          v85 = _com_ptr_t<_com_IIID<IWzGr2DLayer,&__s_GUID const _GUID_6dc8c7ce_8e81_4420_b4f6_4b60b7d5fcdf>>::operator->(&v8->pLayer);
	          IWzGr2DLayer::Putz(v85, pra[0]);
	        } 
         */
        oPacket.EncodeShort(0); //nFallingVY
        oPacket.Encode(0); //v8->bFadeInEffect = CInPacket::Decode1(iPacket) != 0;
        oPacket.Encode(0); //nMakeType
        oPacket.Encode(drop.isCollisionPickUpDrop()); // bCollisionPickUp
        oPacket.Fill(0, 3);
        oPacket.Encode(0); //goes to a swtich with four possibilities
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet explodeDrop(int oid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DropLeaveField.getValue());
        oPacket.Encode(4);
        oPacket.EncodeInteger(oid);
        oPacket.EncodeShort(655);

        return oPacket.ToPacket();
    }

    public static Packet removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static Packet removeItemFromMap(int oid, int animation, int cid, int slot) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DropLeaveField.getValue());
        oPacket.Encode(animation);
        oPacket.EncodeInteger(oid);
        if (animation >= 2) {
            oPacket.EncodeInteger(cid);
            if (animation == 5) {
                oPacket.EncodeInteger(slot);
            }
        }
        return oPacket.ToPacket();
    }

    public static Packet spawnClockMist(final MapleMist clock) {
        OutPacket packet = new OutPacket(80);

        packet.EncodeShort(SendPacketOpcode.AffectedAreaCreated.getValue());
        packet.EncodeInteger(clock.getObjectId());
        packet.Encode(1);
        packet.EncodeInteger(clock.getMobOwner().getObjectId());
        packet.EncodeInteger(clock.getMobSkill().getSkillId());
        packet.Encode(clock.getClockType());
        packet.EncodeShort(0x07);//clock.getSkillDelay());
        packet.EncodeInteger(clock.getBox().x);
        packet.EncodeInteger(clock.getBox().y);
        packet.EncodeInteger(clock.getBox().x + clock.getBox().width);
        packet.EncodeInteger(clock.getBox().y + clock.getBox().height);
        packet.EncodeInteger(0);
        packet.EncodePosition(clock.getMobOwner().getPosition());
        packet.EncodeInteger(0);
        packet.EncodeInteger(clock.getClockType() == 1 ? 15 : clock.getClockType() == 2 ? -15 : 0);
        packet.EncodeInteger(0x78);
        //System.out.println(packet.toString());

        return packet.ToPacket();
    }

    public static Packet spawnObtacleAtomBomb() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ObtacleAtomCreate.getValue());

        //Number of bomb objects to spawn.  You can also just send multiple packets instead of putting them all in one packet.
        oPacket.EncodeInteger(500);

        //Unknown, this part is from IDA.
        byte unk = 0;
        oPacket.Encode(unk); //animation data or some shit
        if (unk == 1) {
            oPacket.EncodeInteger(300); //from Effect.img/BasicEff/ObtacleAtomCreate/%d
            oPacket.Encode(0); //rest idk
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
        }

        oPacket.Encode(1);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(900); //POSX
        oPacket.EncodeInteger(-1347); //POSY
        oPacket.EncodeInteger(25);
        oPacket.EncodeInteger(3);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(25);
        oPacket.EncodeInteger(-5);
        oPacket.EncodeInteger(1000);
        oPacket.EncodeInteger(800);
        oPacket.EncodeInteger(80);
        return oPacket.ToPacket();
    }

    /**
     * Spawns a mist in the affected map area ?OnAffectedAreaCreated@CAffectedAreaPool@@IAEXAAVCInPacket@@@Z
     *
     * @param mist
     * @return
     */
    public static Packet spawnMist(MapleMist mist) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AffectedAreaCreated.getValue());
        oPacket.EncodeInteger(mist.getObjectId());

        oPacket.Encode(0);
        //oPacket.Encode(0);
        oPacket.EncodeInteger(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            oPacket.EncodeInteger(mist.getSourceSkill().getId());
        } else {
            oPacket.EncodeInteger(mist.getMobSkill().getSkillId());
        }
        oPacket.Encode(mist.getSkillLevel());
        oPacket.EncodeShort(mist.getSkillDelay());
        oPacket.EncodeInteger(mist.getBox().x);
        oPacket.EncodeInteger(mist.getBox().y);
        oPacket.EncodeInteger(mist.getBox().x + mist.getBox().width);
        oPacket.EncodeInteger(mist.getBox().y + mist.getBox().height);
        oPacket.EncodeInteger(mist.isShelter() ? 1 : 0);
        oPacket.EncodeInteger(0);
        oPacket.EncodePosition(mist.getPosition());
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        if (GameConstants.isFlipAffectedAreaSkill(mist.getSourceSkill().getId())) {
            oPacket.Encode(0);
        }
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    // Previous Packet for spawnMist
    /*public static Packet spawnMist(MapleMist mist) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AffectedAreaCreated.getValue());
        oPacket.EncodeInteger(mist.getObjectId());//wsUOL

        oPacket.Encode(mist.isMobMist() ? 0 : mist.isPoisonMist());
        oPacket.EncodeInteger(mist.getOwnerId());//pProp

        int sourceSkillId = 0;
        if (mist.getMobSkill() == null) {
            sourceSkillId = mist.getSourceSkill().getId();
        } else {
            sourceSkillId = mist.getMobSkill().getSkillId();
        }
        oPacket.EncodeInteger(sourceSkillId);

        oPacket.Encode(mist.getSkillLevel());
        oPacket.EncodeShort(mist.getSkillDelay());//sLoopUOL
        oPacket.EncodeRectangle(mist.getBox());
        oPacket.EncodeInteger(0);//bFail
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);

        // New in v170
        oPacket.Encode(true); // sPreUOL.baseclass_0.m_Data = (_bstr_t::Data_t *)(CInPacket::Decode1(iPacket) != 0); cache of some sort?    
        oPacket.EncodeInteger(0); // sTemp.baseclass_0.m_Data = (_bstr_t::Data_t *)CInPacket::Decode4(iPacket);

        if (GameConstants.isFlipAffectedAreaSkill(sourceSkillId)) { // This is hard coded! We still need to look into WZ on other flip affected area skill
            oPacket.Encode(0);
        }
        oPacket.EncodeInteger(0); // v4 = (_bstr_t::Data_t *)CInPacket::Decode4(iPacket);

        // Write an additional int to be safe, because we don't have the list of flipAffectedSkill from WZ yet...
        // If any new skill uses it
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }*/
    /**
     * Remove the mist from the map ?OnAffectedAreaRemoved@CAffectedAreaPool@@IAEXAAVCInPacket@@@Z
     *
     * @param oid
     * @param eruption
     * @return
     */
    public static Packet removeMist(int oid, boolean eruption) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AffectedAreaRemoved.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.Encode(eruption ? 1 : 0);

        return oPacket.ToPacket();
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
    public static Packet spawnDoor(int oid, Point pos, Point originalCharacterPosition, boolean animation, int skillid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TownPortalCreated.getValue());
        oPacket.Encode(animation ? 0 : 1);
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(skillid); // new on v170
        oPacket.EncodePosition(pos);
        oPacket.EncodePosition(originalCharacterPosition);// new in v170, character position

        return oPacket.ToPacket();
    }

    public static Packet removeDoor(int oid, boolean animation) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TownPortalRemoved.getValue());
        oPacket.Encode(animation ? 0 : 1);
        oPacket.EncodeInteger(oid);

        return oPacket.ToPacket();
    }

    public static Packet spawnKiteError() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CreateMessageBoxFailed.getValue());

        return oPacket.ToPacket();
    }

    public static Packet spawnKite(MapleKite kite) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MessageBoxEnterField.getValue());
        oPacket.EncodeInteger(kite.getObjectId());
        oPacket.EncodeInteger(kite.getItemID());
        oPacket.EncodeString(kite.getMessage());
        oPacket.EncodeString(kite.getName());
        oPacket.EncodePosition(kite.getPosition());

        return oPacket.ToPacket();
    }

    public static Packet destroyKite(int oid, boolean animation) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MessageBoxLeaveField.getValue());
        oPacket.Encode(animation ? 0 : 1);
        oPacket.EncodeInteger(oid);

        return oPacket.ToPacket();
    }

    public static Packet spawnMechDoor(MechDoor md, boolean animated) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OpenGateCreated.getValue());
        oPacket.Encode(animated ? 0 : 1);
        oPacket.EncodeInteger(md.getOwnerId());
        oPacket.EncodePosition(md.getTruePosition());
        oPacket.Encode(md.getId());
        oPacket.EncodeInteger(md.getPartyId());
        return oPacket.ToPacket();
    }

    public static Packet removeMechDoor(MechDoor md, boolean animated) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OpenGateClose.getValue());
        oPacket.Encode(animated ? 0 : 1);
        oPacket.EncodeInteger(md.getOwnerId());
        oPacket.Encode(md.getId());

        return oPacket.ToPacket();
    }

    public static Packet triggerReactor(MapleReactor reactor, int stance) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ReactorChangeState.getValue());
        oPacket.EncodeInteger(reactor.getObjectId());
        oPacket.Encode(reactor.getState());
        oPacket.EncodePosition(reactor.getTruePosition());
        oPacket.EncodeInteger(stance);
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        return oPacket.ToPacket();
    }

    public static Packet spawnReactor(MapleReactor reactor) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ReactorEnterField.getValue());
        oPacket.EncodeInteger(reactor.getObjectId());
        oPacket.EncodeInteger(reactor.getReactorId());
        oPacket.Encode(reactor.getState());
        oPacket.EncodePosition(reactor.getTruePosition());
        oPacket.Encode(reactor.getFacingDirection());
        oPacket.EncodeString(reactor.getName());

        return oPacket.ToPacket();
    }

    public static Packet destroyReactor(MapleReactor reactor) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ReactorStateReset.getValue());
        oPacket.EncodeInteger(reactor.getObjectId());
        oPacket.Encode(reactor.getState());
        oPacket.EncodePosition(reactor.getPosition());

        return oPacket.ToPacket();
    }

    public static Packet makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DecomposerEnterField.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeString(cname);
        oPacket.EncodeInteger(pos.x);
        oPacket.EncodeInteger(pos.y);
        oPacket.EncodeShort(timeLeft);
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeInteger(fee);

        return oPacket.ToPacket();
    }

    public static Packet removeExtractor(int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DecomposerLeaveField.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(1);

        return oPacket.ToPacket();
    }

    public static Packet rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SnowBallState.getValue());
        oPacket.Encode(type);
        oPacket.EncodeInteger(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        oPacket.EncodeInteger(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        oPacket.EncodeShort(ball1 == null ? 0 : ball1.getPosition());
        oPacket.Encode(0);
        oPacket.EncodeShort(ball2 == null ? 0 : ball2.getPosition());
        oPacket.Fill(0, 11);

        return oPacket.ToPacket();
    }

    public static Packet enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static Packet hitSnowBall(int team, int damage, int distance, int delay) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SnowBallHit.getValue());
        oPacket.Encode(team);
        oPacket.EncodeShort(damage);
        oPacket.Encode(distance);
        oPacket.Encode(delay);

        return oPacket.ToPacket();
    }

    public static Packet snowballMessage(int team, int message) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SnowBallMsg.getValue());
        oPacket.Encode(team);
        oPacket.EncodeInteger(message);

        return oPacket.ToPacket();
    }

    public static Packet leftKnockBack() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SnowBallTouch.getValue());

        return oPacket.ToPacket();
    }

    public static Packet hitCoconut(boolean spawn, int id, int type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CoconutHit.getValue());
        oPacket.EncodeInteger(spawn ? 32768 : id);
        oPacket.Encode(spawn ? 0 : type);

        return oPacket.ToPacket();
    }

    public static Packet coconutScore(int[] coconutscore) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CoconutScore.getValue());
        oPacket.EncodeShort(coconutscore[0]);
        oPacket.EncodeShort(coconutscore[1]);

        return oPacket.ToPacket();
    }

    public static Packet showChaosZakumShrine(boolean spawned, int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        oPacket.Encode(spawned ? 1 : 0);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static Packet showHorntailShrine(boolean spawned, int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        oPacket.Encode(spawned ? 1 : 0);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet getRPSMode(byte mode, int mesos, int selection, int answer) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.RPSGame.getValue());
        oPacket.Encode(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                oPacket.EncodeInteger(mesos);
                break;
            case 8:
                oPacket.EncodeInteger(9000019);
                break;
            case 11:
                oPacket.Encode(selection);
                oPacket.Encode(answer);
        }

        return oPacket.ToPacket();
    }

    public static Packet messengerInvite(String from, int messengerid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(3);
        oPacket.EncodeString(from);
        oPacket.Encode(1);//channel?
        oPacket.EncodeInteger(messengerid);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet addMessengerPlayer(String from, User chr, int position, int channel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(0);
        oPacket.Encode(position);
        writeCharacterLook(oPacket, chr);
        oPacket.EncodeString(from);
        oPacket.Encode(channel);
        oPacket.Encode(1); // v140
        oPacket.EncodeInteger(chr.getJob());

        return oPacket.ToPacket();
    }

    public static Packet removeMessengerPlayer(int position) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(2);
        oPacket.Encode(position);

        return oPacket.ToPacket();
    }

    public static Packet updateMessengerPlayer(String from, User chr, int position, int channel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(0); // v140.
        oPacket.Encode(position);
        writeCharacterLook(oPacket, chr);
        oPacket.EncodeString(from);
        oPacket.Encode(channel);
        oPacket.Encode(0); // v140.
        oPacket.EncodeInteger(chr.getJob()); // doubt it's the job, lol. v140.

        return oPacket.ToPacket();
    }

    public static Packet joinMessenger(int position) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(1);
        oPacket.Encode(position);

        return oPacket.ToPacket();
    }

    public static Packet messengerChat(String charname, String text) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(6);
        oPacket.EncodeString(charname);
        oPacket.EncodeString(text);

        return oPacket.ToPacket();
    }

    public static Packet messengerNote(String text, int mode, int mode2) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(mode);
        oPacket.EncodeString(text);
        oPacket.Encode(mode2);

        return oPacket.ToPacket();
    }

    public static Packet messengerOpen(byte type, List<User> chars) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GetLotteryResult.getValue());
        oPacket.Encode(type); //7 in messenger open ui 8 new ui
        if (chars.isEmpty()) {
            oPacket.EncodeShort(0);
        }
        for (User chr : chars) {
            oPacket.Encode(1);
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(0); //likes
            oPacket.EncodeLong(0); //some time
            oPacket.EncodeString(chr.getName());
            writeCharacterLook(oPacket, chr);
        }

        return oPacket.ToPacket();
    }

    public static Packet messengerCharInfo(User chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Messenger.getValue());
        oPacket.Encode(0x0B);
        oPacket.EncodeString(chr.getName());
        oPacket.EncodeInteger(chr.getJob());
        oPacket.EncodeInteger(chr.getFame());
        oPacket.EncodeInteger(0); //likes
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        oPacket.EncodeString(gs != null ? gs.getName() : "-");
        MapleGuildAlliance alliance = World.Alliance.getAlliance(gs.getAllianceId());
        oPacket.EncodeString(alliance != null ? alliance.getName() : "");
        oPacket.Encode(2);

        return oPacket.ToPacket();
    }

    public static Packet removeFromPackageList(boolean remove, int Package) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Parcel.getValue());
        oPacket.Encode(24);
        oPacket.EncodeInteger(Package);
        oPacket.Encode(remove ? 3 : 4);

        return oPacket.ToPacket();
    }

    public static Packet sendPackageMSG(byte operation, List<MaplePackageActions> packages) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Parcel.getValue());
        oPacket.Encode(operation);

        switch (operation) {
            case 9:
                oPacket.Encode(1);
                break;
            case 10:
                oPacket.Encode(0);
                oPacket.Encode(packages.size());

                for (MaplePackageActions dp : packages) {
                    oPacket.EncodeInteger(dp.getPackageId());
                    oPacket.EncodeString(dp.getSender(), 13);
                    oPacket.EncodeInteger(dp.getMesos());
                    oPacket.EncodeLong(PacketHelper.getTime(dp.getSentTime()));
                    oPacket.Fill(0, 205);

                    if (dp.getItem() != null) {
                        oPacket.Encode(1);
                        PacketHelper.addItemInfo(oPacket, dp.getItem());
                    } else {
                        oPacket.Encode(0);
                    }
                }
                oPacket.Encode(0);
        }

        return oPacket.ToPacket();
    }

    /**
     * Sends the keymap data to the user
     *
     * @param layout
     * @param jobid
     * @return
     */
    public static Packet getKeymap(MapleKeyLayout layout, int jobid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FuncKeyMappedInit.getValue());
        layout.writeData(oPacket, jobid);

        return oPacket.ToPacket();
    }

    public static Packet petAutoHP(int itemId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetConsumeItemInit.getValue());
        oPacket.EncodeInteger(itemId);

        return oPacket.ToPacket();
    }

    public static Packet petAutoMP(int itemId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetConsumeMPItemInit.getValue());
        oPacket.EncodeInteger(itemId);

        return oPacket.ToPacket();
    }

    public static Packet petAutoCure(int itemId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetConsumeCureItemInit.getValue());
        oPacket.EncodeInteger(itemId);

        return oPacket.ToPacket();
    }

    public static Packet petAutoBuff(int skillId) {
        OutPacket oPacket = new OutPacket(80);

        //oPacket.encodeShort(SendPacketOpcode.PET_AUTO_BUFF.getValue());
        oPacket.EncodeInteger(skillId);

        return oPacket.ToPacket();
    }

    public static void addRingInfo(OutPacket oPacket, List<MapleRing> rings) {
        oPacket.Encode(rings.size());
        for (MapleRing ring : rings) {
            oPacket.EncodeInteger(1);
            oPacket.EncodeLong(ring.getRingId());
            oPacket.EncodeLong(ring.getPartnerRingId());
            oPacket.EncodeInteger(ring.getItemId());
        }
    }

    public static void addMRingInfo(OutPacket oPacket, List<MapleRing> rings, User chr) {
        oPacket.Encode(rings.size());
        for (MapleRing ring : rings) {
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(ring.getPartnerChrId());
            oPacket.EncodeInteger(ring.getItemId());
        }
    }

    public static Packet getBuffBar(long millis) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BUFF_BAR.getValue());
        oPacket.EncodeLong(millis);

        return oPacket.ToPacket();
    }

    public static Packet getBoosterFamiliar(int cid, int familiar, int id) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BOOSTER_FAMILIAR.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(familiar);
        oPacket.EncodeLong(id);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet viewSkills(User chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ResultStealSkillList.getValue());
        List<Integer> skillz = new ArrayList<Integer>();
        for (Skill sk : chr.getSkills().keySet()) {
            if ((sk.canBeLearnedBy(chr.getJob())) && (GameConstants.canSteal(sk)) && (!skillz.contains(Integer.valueOf(sk.getId())))) {
                skillz.add(Integer.valueOf(sk.getId()));
            }
        }
        oPacket.Encode(1);
        oPacket.EncodeInteger(chr.getId());
        oPacket.EncodeInteger(skillz.isEmpty() ? 2 : 4);
        oPacket.EncodeInteger(chr.getJob());
        oPacket.EncodeInteger(skillz.size());
        for (int skill : skillz) {
            oPacket.EncodeInteger(skill);
        }
        return oPacket.ToPacket();
    }

    public static class InteractionPacket {

        public static Packet getTradeInvite(User c) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
            oPacket.Encode(4);//was 3
            oPacket.EncodeString(c.getName());
//            oPacket.encodeInteger(c.getLevel());
            oPacket.EncodeInteger(c.getJob());
            return oPacket.ToPacket();
        }

        public static Packet getTradeMesoSet(byte number, long meso) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.UPDATE_MESO.action);
            oPacket.Encode(number);
            oPacket.EncodeLong(meso);
            return oPacket.ToPacket();
        }

        public static Packet gachaponMessage(Item item, String town, User player) {
            final OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
            oPacket.Encode(0x0B);
            oPacket.EncodeString(player.getName() + " : got a(n)");
            oPacket.EncodeInteger(0); //random?
            oPacket.EncodeString(town);
            PacketHelper.addItemInfo(oPacket, item);
            return oPacket.ToPacket();
        }

        public static Packet getTradeItemAdd(byte number, Item item) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.SET_ITEMS.action);
            oPacket.Encode(number);
            oPacket.Encode(item.getPosition());
            PacketHelper.addItemInfo(oPacket, item);

            return oPacket.ToPacket();
        }

        public static Packet getTradeStart(MapleClient c, MapleTrade trade, byte number) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
//            oPacket.encode(PlayerInteractionHandler.Interaction.START_TRADE.action);
//            if (number != 0){//13 a0
////                oPacket.encode(HexTool.getByteArrayFromHexString("13 01 01 03 FE 53 00 00 40 08 00 00 00 E2 7B 00 00 01 E9 50 0F 00 03 62 98 0F 00 04 56 BF 0F 00 05 2A E7 0F 00 07 B7 5B 10 00 08 3D 83 10 00 09 D3 D1 10 00 0B 13 01 16 00 11 8C 1F 11 00 12 BF 05 1D 00 13 CB 2C 1D 00 31 40 6F 11 00 32 6B 46 11 00 35 32 5C 19 00 37 20 E2 11 00 FF 03 B6 98 0F 00 05 AE 0A 10 00 09 CC D0 10 00 FF FF 00 00 00 00 13 01 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 4D 6F 6D 6F 6C 6F 76 65 73 4B 48 40 08"));
//                oPacket.encode(19);
//                oPacket.encode(1);
//                PacketHelper.addCharLook(oPacket, trade.getPartner().getChr(), false);
//                oPacket.encodeString(trade.getPartner().getChr().getName());
//                oPacket.encodeShort(trade.getPartner().getChr().getJob());
//            }else{
            oPacket.Encode(20);
            oPacket.Encode(4);
            oPacket.Encode(2);
            oPacket.Encode(number);

            if (number == 1) {
                oPacket.Encode(0);
                writeCharacterLook(oPacket, trade.getPartner().getCharacter());
                oPacket.EncodeString(trade.getPartner().getCharacter().getName());
                oPacket.EncodeShort(trade.getPartner().getCharacter().getJob());
            }
            oPacket.Encode(number);
            writeCharacterLook(oPacket, c.getPlayer());
            oPacket.EncodeString(c.getPlayer().getName());
            oPacket.EncodeShort(c.getPlayer().getJob());
            oPacket.Encode(255);
//            }
            return oPacket.ToPacket();
        }

        public static Packet getTradeConfirmation() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.CONFIRM_TRADE.action);

            return oPacket.ToPacket();
        }

        public static Packet TradeMessage(byte UserSlot, byte message) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.EXIT.action);
//            oPacket.encode(25);//new v141
            oPacket.Encode(UserSlot);
            oPacket.Encode(message);

            return oPacket.ToPacket();
        }

        public static Packet getTradeCancel(byte UserSlot) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
            oPacket.Encode(PlayerInteractionHandler.Interaction.EXIT.action);
            oPacket.Encode(UserSlot);
            oPacket.Encode(2);//was 2 originally, 7 = Trade Successful message.

            return oPacket.ToPacket();
        }
    }

    public static class NPCPacket {

        public static Packet spawnNPC(MapleNPC life, boolean minimap) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.NpcEnterField.getValue());

            encodeSpawnNPCData(oPacket, life, minimap);

            return oPacket.ToPacket();
        }

        public static Packet spawnNPCRequestController(MapleNPC life, boolean minimap) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.NpcChangeController.getValue());
            oPacket.Encode(1);

            encodeSpawnNPCData(oPacket, life, minimap);

            return oPacket.ToPacket();
        }

        private static void encodeSpawnNPCData(OutPacket oPacket, MapleNPC life, boolean minimap) {
            oPacket.EncodeInteger(life.getObjectId());
            oPacket.EncodeInteger(life.getId());
            oPacket.EncodeShort(life.getPosition().x);
            oPacket.EncodeShort(life.getCy());
            oPacket.Encode(0); // New in v176
            oPacket.Encode(life.getF() == 1 ? 0 : 1);
            oPacket.EncodeShort(life.getFh());
            oPacket.EncodeShort(life.getRx0());
            oPacket.EncodeShort(life.getRx1());
            oPacket.Encode(minimap ? 1 : 0);
            oPacket.EncodeInteger(0);//new 143

            // new v169
            oPacket.Encode(0); // m_nMoveAction
            oPacket.EncodeInteger(-1); // CNpc::SetPresentItem(v3, v69);

            int n_tPresent = 0;
            oPacket.EncodeInteger(n_tPresent);

            int m_nNoticeBoardType = 0;
            oPacket.EncodeInteger(m_nNoticeBoardType);
            if (m_nNoticeBoardType == 1) {
                oPacket.EncodeInteger(0); // m_nNoticeBoardType
            }

            oPacket.EncodeInteger(0); // if ( !v3->m_bHideToLocalUser && v74 ) CNpc::ApplyCreateAlpha(v3, v74);

            final String sLocalRepeatEffect = "";
            oPacket.EncodeString(sLocalRepeatEffect);

            boolean decodeCScreenInfo = false;
            oPacket.Encode(decodeCScreenInfo);
            if (decodeCScreenInfo) {
                // v77 = CScreenInfo::Decode((ZRef<CScreenInfo> *)&pvarg.boolVal, iPacket);

                oPacket.Encode(0); // v2 = CInPacket::Decode1(iPacket); CScreenInfo::CreateScreenInfo(&pScreenInfo, (unsigned __int8)v2);
                oPacket.EncodeInteger(0);
            }
        }

        public static Packet getMapSelection(final int npcid, final String sel) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npcid);
            oPacket.EncodeShort(0x11);
            oPacket.EncodeInteger(npcid == 2083006 ? 1 : 0); //neo city
            oPacket.EncodeInteger(npcid == 9010022 ? 1 : 0); //dimensional
            oPacket.EncodeString(sel);

            return oPacket.ToPacket();
        }

        public static Packet removeNPC(int objectid) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.NpcLeaveField.getValue());
            oPacket.EncodeInteger(objectid);

            return oPacket.ToPacket();
        }

        public static Packet removeNPCController(int objectid) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.NpcChangeController.getValue());
            oPacket.Encode(0);
            oPacket.EncodeInteger(objectid);

            return oPacket.ToPacket();
        }

        public static Packet toggleNPCShow(int oid, boolean hide) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.NpcUpdateLimitedInfo.getValue());
            oPacket.EncodeInteger(oid);
            oPacket.Encode(hide ? 0 : 1);
            return oPacket.ToPacket();
        }

        public static Packet setNPCSpecialAction(int oid, String action) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.NpcEmotion.getValue());
            oPacket.EncodeInteger(oid);
            oPacket.EncodeString(action);
            oPacket.EncodeInteger(0); //unknown yet
            oPacket.Encode(0); //unknown yet
            return oPacket.ToPacket();
        }

        public static Packet NPCSpecialAction(int oid, int x, int y) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ForceMoveByScript.getValue());
            oPacket.EncodeInteger(oid);
            oPacket.EncodeInteger(x);
            oPacket.EncodeInteger(y);
            return oPacket.ToPacket();
        }

        public static Packet setNPCScriptable() {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.NpcCharacterBaseAction.getValue());

            List<Pair<Integer, String>> npcs = new LinkedList<Pair<Integer, String>>();
            npcs.add(new Pair<>(9070006, "Why...why has this happened to me? My knightly honor... My knightly pride..."));
            npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));

            oPacket.Encode(npcs.size());
            for (Pair<Integer, String> s : npcs) {
                oPacket.EncodeInteger(s.getLeft());
                oPacket.EncodeString(s.getRight());
                oPacket.EncodeInteger(0);
//                oPacket.encodeInteger(Integer.MAX_VALUE);
                oPacket.Encode(0);
            }
            return oPacket.ToPacket();
        }

        public static Packet getNPCTalk(int npc, NPCChatType msgType, String talk, NPCChatByType type) {
            return getNPCTalk(npc, msgType, talk, type, npc);
        }

        public static Packet getNPCTalk(int npc, NPCChatType msgType, String talk, NPCChatByType type, int overrideNpcId) {
            return getNPCTalk(npc, msgType, talk, type.getValue(), overrideNpcId); // legacy compatible for now, there are just too many scripts.. my god
        }

        public static Packet getNPCTalk(int npc, NPCChatType msgType, String talk, byte type, int overrideNpcId) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc); // npc

            final boolean unk = false;
            oPacket.Encode(unk); // bSpecificSpeaker
            if (unk) {
                oPacket.EncodeInteger(0); // nSpecificSpeakerTemplateID
            }

            oPacket.Encode(msgType.getType());
            oPacket.EncodeShort(type); // mask
            oPacket.Encode(0); // eColor
            if ((type & 0x4) != 0) {
                oPacket.EncodeInteger(overrideNpcId);
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
                    oPacket.Encode(msgType.allowBack() ? 1 : 0);
                    oPacket.Encode(msgType.allowForward() ? 1 : 0);
                    break;
            }
            oPacket.EncodeInteger(0); // new v169
            return oPacket.ToPacket();
        }

        public static Packet getEnglishQuiz(int npc, byte type, int diffNPC, String talk, String endBytes) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(10); //not sure
            oPacket.EncodeShort(type);
            oPacket.Encode(0); // new v169
            if ((type & 0x4) != 0) {
                oPacket.EncodeInteger(diffNPC);
            }
            oPacket.EncodeString(talk);
            oPacket.Encode(HexTool.getByteArrayFromHexString(endBytes));
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getAdviceTalk(String[] wzinfo) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(8);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.Encode(1);
            oPacket.Encode(wzinfo.length);
            oPacket.Encode(0); // new v169
            for (String data : wzinfo) {
                oPacket.EncodeString(data);
            }
            oPacket.EncodeInteger(0); // new v169
            return oPacket.ToPacket();
        }

        public static Packet getSlideMenu(int npcid, int type, int lasticon, String sel) {
            //Types: 0 - map selection 1 - neo city map selection 2 - korean map selection 3 - tele rock map selection 4 - dojo buff selection
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4); //slide menu
            oPacket.EncodeInteger(npcid);
            oPacket.Encode(0);
            oPacket.Encode(NPCChatType.OnAskSlideMenu.getType());//0x12
            oPacket.EncodeShort(0);//bParam = false
            oPacket.Encode(0);//bsecond for a few types
            oPacket.EncodeInteger(type); //menu type
            oPacket.EncodeInteger(type == 0 ? lasticon : 0); //last icon on menu
            oPacket.EncodeString(sel);
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getNPCTalkStyle(int npc, String talk, int[] args, boolean second) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);

            boolean unk = false;
            oPacket.Encode(unk);
            if (unk) {
                /* 
                if ( (unsigned __int8)CInPacket::Decode1(a2) )
                    CInPacket::Decode4(a2);*/
                oPacket.EncodeInteger(0); // 
            }
            oPacket.Encode(NPCChatType.OnAskAvater.getType());
            oPacket.EncodeShort(0); // mask? idk
            oPacket.Encode(second ? 1 : 0);//new143

            /* new in v170, these 2 bytes seems to show preview of some type
            See: http://pastebin.com/m643nXtt sub_D0C230
            
                LOBYTE(v68) = (unsigned __int8)CInPacket::Decode1((int)a4) != 0;
                LOBYTE(v66) = (unsigned __int8)CInPacket::Decode1(v6) != 0;
            
                if ( (_BYTE)v66 != 1 )
                    goto LABEL_10;
                v24 = *(_DWORD *)(CWvsContext::GetCharacterData((int)v65, (int)&v72) + 4);
             */
            oPacket.Encode(0);
            oPacket.Encode(0);

            oPacket.EncodeString(talk);

            oPacket.Encode(args.length);
            for (int i = 0; i < args.length; i++) {
                oPacket.EncodeInteger(args[i]);
            }

            return oPacket.ToPacket();
        }

        public static Packet getNPCTalkNum(int npc, String talk, int def, int min, int max) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(0);//new 142
            oPacket.Encode(NPCChatType.OnAskNumber.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.Encode(0);//bsecond for a few types
            oPacket.EncodeString(talk);
            oPacket.EncodeInteger(def);
            oPacket.EncodeInteger(min);
            oPacket.EncodeInteger(max);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getNPCTalkText(int npc, String talk) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(0);
            oPacket.Encode(NPCChatType.OnAskText.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.Encode(0);//bsecond for a few types
            oPacket.EncodeString(talk);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getNPCTalkQuiz(int npc, String caption, String talk, int time) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(0);
            oPacket.Encode(NPCChatType.OnInitialQuiz.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.Encode(0);//bsecond for a few types
            oPacket.EncodeString(caption);
            oPacket.EncodeString(talk);
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0xF); //no idea
            oPacket.EncodeInteger(time); //seconds
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getSelfTalkText(String text) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(1);
            oPacket.EncodeShort(0);
            oPacket.Encode(17);
            oPacket.Encode(0); // new v169
            oPacket.EncodeString(text);
            oPacket.Encode(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0); // new v169

            return oPacket.ToPacket();
        }

        public static Packet getNPCTutoEffect(String effect) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);
            oPacket.Encode(1);
            oPacket.EncodeShort(257);
            oPacket.EncodeString(effect);
            oPacket.EncodeInteger(0); // new v169
            return oPacket.ToPacket();
        }

        public static Packet getCutSceneSkip() {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);
            oPacket.Encode(2);
            oPacket.Encode(5);
            oPacket.EncodeInteger(9010000); //Maple administrator
            oPacket.EncodeString("Would you like to skip the tutorial cutscenes?");
            oPacket.EncodeInteger(0); // new v169
            return oPacket.ToPacket();
        }

        public static Packet getDemonSelection() {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(2159311); //npc
            oPacket.Encode(0x16);
            oPacket.Encode(1);
            oPacket.EncodeShort(1);
            oPacket.Fill(0, 8);
            return oPacket.ToPacket();
        }

        public static Packet getAngelicBusterAvatarSelect(int npc) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(0);
            oPacket.Encode(NPCChatType.OnAskSlideMenu.getType());
            oPacket.EncodeShort(0);//bParam = false
            oPacket.Encode(0);//bsecond for a few types
            return oPacket.ToPacket();
        }

        public static Packet getEvanTutorial(String data) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ScriptMessage.getValue());

            oPacket.Encode(8);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.Encode(1);
            oPacket.Encode(1);
            oPacket.EncodeString(data);

            return oPacket.ToPacket();
        }

        public static Packet getNPCShop(int shopNPCId, MapleShop shop, MapleClient c) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.OpenShopDlg.getValue());
            oPacket.Encode(0); //if this is true then send a int with m_dwPetTemplateID (pet item id)
            PacketHelper.addShopInfo(oPacket, shop, c);

            return oPacket.ToPacket();
        }

        public static Packet confirmShopTransaction(ShopOperationType code, MapleShop shop, MapleClient c, int indexBought) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ShopResult.getValue());
            oPacket.Encode(code.getOp());
            if (code == ShopOperationType.Sell) {
                PacketHelper.addShopInfo(oPacket, shop, c);
            } else {
                oPacket.Encode(indexBought >= 0);
                if (indexBought >= 0) {
                    oPacket.EncodeInteger(indexBought);
                    oPacket.EncodeInteger(0);
                    oPacket.EncodeShort(0);
                } else {
                    oPacket.Encode(0);
                    oPacket.EncodeInteger(0);
                }
            }

            return oPacket.ToPacket();
        }

        public static Packet getStorage(int npcId, byte slots, Collection<Item> items, long meso) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(22); // 0x16
            oPacket.EncodeInteger(npcId);
            oPacket.Encode(slots);
            oPacket.EncodeLong(0x7E); // BUFFER	[7E00000000000000]
            oPacket.EncodeLong(meso);
            oPacket.Encode(0);
            oPacket.Encode((byte) items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            oPacket.Encode(0);
            oPacket.Encode(0);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }

        public static Packet getStorageFull() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(17);

            return oPacket.ToPacket();
        }

        public static Packet mesoStorage(byte slots, long meso) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(19);
            oPacket.Encode(slots);
            oPacket.EncodeShort(2);
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeLong(meso);

            return oPacket.ToPacket();
        }

        public static Packet arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(15);
            oPacket.Encode(slots);
            oPacket.Encode(124);
            oPacket.Fill(0, 10);
            oPacket.Encode(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            oPacket.Encode(0);
            return oPacket.ToPacket();
        }

        public static Packet storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(13);
            oPacket.Encode(slots);
            oPacket.EncodeShort(type.getBitfieldEncoding());
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(0);
            oPacket.Encode(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            return oPacket.ToPacket();
        }

        public static Packet takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.TrunkResult.getValue());
            oPacket.Encode(9);
            oPacket.Encode(slots);
            oPacket.EncodeShort(type.getBitfieldEncoding());
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(0);
            oPacket.Encode(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(oPacket, item);
            }
            return oPacket.ToPacket();
        }
    }

    public static class SummonPacket {

        public static Packet jaguarActive(boolean active) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.JaguarActive.getValue());

            oPacket.Encode(active);

            return oPacket.ToPacket();
        }
        
        public static Packet jaguarSkillRequest(int nSkillID) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.JaguarSkill.getValue());

            oPacket.EncodeInteger(nSkillID);

            return oPacket.ToPacket();
        }

        /**
         * This packet spawns the summons onto the world
         *
         * @param MapleSummon
         * @param animated - if the summon has a spawn animation
         *
         * @return oPacket
         */
        public static Packet spawnSummon(Summon summon, boolean animated) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedEnterField.getValue());
            oPacket.EncodeInteger(summon.getOwnerId());
            oPacket.EncodeInteger(summon.getObjectId());
            oPacket.EncodeInteger(summon.getSkill());
            oPacket.Encode(summon.getOwnerLevel() - 1);
            oPacket.Encode(summon.getSkillLevel());
            oPacket.EncodePosition(summon.getPosition());
            oPacket.Encode(summon.getSkill() == 32111006 || summon.getSkill() == 33101005 || summon.getSkill() == 14000027 ? 5 : 4);// Summon Reaper Buff - Call of the Wild

            if (summon.getSkill() == 35121003 && summon.getOwner().getMap() != null) {//Giant Robot SG-88
                oPacket.EncodeShort(summon.getOwner().getMap().getSharedMapResources().footholds.findBelow(summon.getPosition()).getId());
            } else {
                oPacket.EncodeShort(0);
            }
            oPacket.Encode(summon.getMovementType().getValue());
            oPacket.Encode(summon.getSummonType());
            oPacket.Encode(animated ? 1 : 0);
            oPacket.EncodeInteger(0);//dwMobId
            oPacket.Encode(0); //bFlyMob
            oPacket.Encode(0);//bBeforeFirstAttack
            oPacket.EncodeInteger(0);//nLookId
            oPacket.EncodeInteger(0);//nBulletId
            User chr = summon.getOwner();
            oPacket.Encode((summon.getSkill() == DualBlade.MIRRORED_TARGET || summon.getSkill() == 14111024 || summon.getSkill() == 14121054 || summon.getSkill() == 14121055 || summon.getSkill() == 14121056) && chr != null ? 1 : 0); // Mirrored Target boolean for character look
            if ((summon.getSkill() == DualBlade.MIRRORED_TARGET || summon.getSkill() == 14111024 || summon.getSkill() == 14121054 || summon.getSkill() == 14121055 || summon.getSkill() == 14121056) && chr != null) { // Mirrored Target
                writeCharacterLook(oPacket, chr);
            }
            if (summon.getSkill() == Mechanic.ROCK_N_SHOCK) {// Rock 'n Shock m_nTeslaCoilState
                oPacket.Encode(0);
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
                oPacket.EncodeInteger(400); // Unknown
                oPacket.EncodeInteger(30); // Unknown
            } else {
                if (summon.getSkill() == 42111003) {
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                    oPacket.EncodeShort(0);
                } else if (summon.getSkill() == 400051014) {
                    oPacket.EncodeLong(0);
                } else if (summon.getSkill() >= 400051028 && summon.getSkill() <= 400051032) {
                    oPacket.Encode(0);
                }
            }
            oPacket.Encode(0); //m_bJaguarActive

            // v176
            oPacket.EncodeInteger(summon.getSummonDuration()); // v2->m_tSummonTerm = CInPacket::Decode4(iPacket);
            oPacket.Encode(1); // m_bAttackActive
            oPacket.EncodeInteger(0);
            if (summon.getSkill() >= 33001007 && summon.getSkill() <= 33001015) { // Jaguars
                oPacket.Encode(0);
                oPacket.EncodeInteger(0);
            }

            return oPacket.ToPacket();
        }

        /**
         * This packet removes the summons from the world
         *
         * @param MapleSummon
         * @param animated - if the summon has a remove animation
         *
         * @return oPacket
         */
        public static Packet removeSummon(Summon summon, boolean animated) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedLeaveField.getValue());
            oPacket.EncodeInteger(summon.getOwnerId());
            oPacket.EncodeInteger(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                    case 14000027:
                    case 14111024:
                    case 14121054:
                        oPacket.Encode(10);
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
                        oPacket.Encode(5);
                        break;
                    default:
                        oPacket.Encode(4);
                        break;
                }
            } else {
                oPacket.Encode(1);
            }

            return oPacket.ToPacket();
        }

        /**
         * This packet moves the summons
         *
         * @param MapleSummon
         * @param List<LifeMovementFragment> moves - a list of movement
         *
         * @return oPacket
         */
        public static Packet moveSummon(Summon summon, List<LifeMovementFragment> moves) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedMove.getValue());
            oPacket.EncodeInteger(summon.getOwnerId());
            oPacket.EncodeInteger(summon.getObjectId());
            PacketHelper.serializeMovementList(oPacket, summon, moves, 0);

            return oPacket.ToPacket();
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
        public static Packet summonAttack(int cid, int summonSkillId, byte animation, List<Pair<Integer, Long>> allDamage, int level, boolean counter) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedAttack.getValue());
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(summonSkillId);
            oPacket.Encode(level - 1);
            oPacket.Encode(animation); //nAction
            oPacket.Encode(allDamage.size()); //nAttackCount
            for (Pair<Integer, Long> attackEntry : allDamage) {
                oPacket.EncodeInteger(attackEntry.left);
                oPacket.Encode(7);
                oPacket.EncodeLong(attackEntry.right);
            }
            oPacket.Encode(counter); //bCounterAttack
            oPacket.Encode(false); // bNoAction?
            oPacket.EncodeShort(0); // X (Spirit Bond?)
            oPacket.EncodeShort(0); // Y (Spirit Bond?)
            oPacket.EncodeInteger(0); // SkillID?
            return oPacket.ToPacket();
        }

        public static Packet pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackMonster> attack) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedAttackPvP.getValue());
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(oid);
            oPacket.Encode(playerLevel);
            oPacket.Encode(animation);
            oPacket.EncodePosition(pos);
            oPacket.EncodeInteger(0);

            oPacket.Encode(attack.size());
            for (AttackMonster p : attack) {
                oPacket.EncodeInteger(p.getObjectId());
                oPacket.EncodePosition(p.getPosition());
                oPacket.Encode(p.getAttacks().size());
                oPacket.Encode(0);

                for (Pair<Long, Boolean> atk : p.getAttacks()) {
                    oPacket.EncodeLong(atk.left);
                }
            }

            return oPacket.ToPacket();
        }

        public static Packet summonSkill(int cid, int summonSkillId, int newStance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedSetReference.getValue());
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(summonSkillId);
            oPacket.Encode(newStance);

            return oPacket.ToPacket();
        }

        public static Packet damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SummonedHPTagUpdate.getValue());
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(summonSkillId);
            oPacket.Encode(unkByte);
            oPacket.EncodeInteger(damage);
            oPacket.EncodeInteger(monsterIdFrom);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }
    }

    public static class UIPacket {

        public static Packet getDirectionStatus(boolean enable) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.InGameCurNodeEventEnd.getValue());
            oPacket.Encode(enable ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet openUI(int type) {
            OutPacket oPacket = new OutPacket(3);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUI.getValue());
            oPacket.EncodeInteger(type);

            return oPacket.ToPacket();
        }

        public static Packet sendRepairWindow(int npc) {
            OutPacket oPacket = new OutPacket(10);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(33);
            oPacket.EncodeInteger(npc);
            oPacket.EncodeInteger(0);//new143

            return oPacket.ToPacket();
        }

        public static Packet sendJewelCraftWindow(int npc) {
            OutPacket oPacket = new OutPacket(10);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(104);
            oPacket.EncodeInteger(npc);
            oPacket.EncodeInteger(0);//new143

            return oPacket.ToPacket();
        }

        public static Packet startAzwan(int npc) {
            OutPacket oPacket = new OutPacket(10);
            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(70);
            oPacket.EncodeInteger(npc);
            oPacket.EncodeInteger(0);//new143
            return oPacket.ToPacket();
        }

        public static Packet openUIOption(int type, int npc) {
            OutPacket oPacket = new OutPacket(10);
            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(type);
            oPacket.EncodeInteger(npc);
            return oPacket.ToPacket();
        }

        public static Packet sendDojoResult(int points) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(0x48);
            oPacket.EncodeInteger(points);

            return oPacket.ToPacket();
        }

        public static Packet sendAzwanResult() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(0x45);
            oPacket.EncodeInteger(0);

            return oPacket.ToPacket();
        }

        public static Packet DublStart(boolean dark) { // Lmao
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
            oPacket.Encode(0x28);
            oPacket.Encode(dark ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet DublStartAutoMove() {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.FieldCreateFallingCatcher.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(2);

            return oPacket.ToPacket();
        }

        public static Packet IntroLock(boolean enable) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.INTRO_LOCK.getValue());
            oPacket.Encode(enable ? 1 : 0);
            oPacket.EncodeInteger(0);

            return oPacket.ToPacket();
        }

        /**
         * Creates a nice fade in-out effect to 16:9 resolution from the center.
         *
         * @param enable
         * @return
         */
        public static Packet IntroEnableUI(boolean enable) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.INTRO_ENABLE_UI.getValue());
            oPacket.Encode(enable ? 1 : 0);
            oPacket.Encode(1);
            if (enable) {
                oPacket.Encode(1);
                oPacket.Encode(0);
            }
            return oPacket.ToPacket();
        }

        public static Packet UserHireTutor(boolean summon) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserHireTutor.getValue());
            oPacket.Encode(summon ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet UserTutorMessage(int type) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserTutorMsg.getValue());
            oPacket.Encode(1);
            oPacket.EncodeInteger(type);
            oPacket.EncodeInteger(7000);

            return oPacket.ToPacket();
        }

        public static Packet UserTutorMessage(String message) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserTutorMsg.getValue());
            oPacket.Encode(0);
            oPacket.EncodeString(message);
            oPacket.EncodeInteger(200);
            oPacket.EncodeShort(0);
            oPacket.EncodeInteger(10000);

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(int type, int value, int x) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            if (x > 0) {
                oPacket.Encode(x);
            }
            oPacket.Encode((byte) type);
            oPacket.EncodeInteger(value);

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(int type, int value) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());

            oPacket.Encode((byte) type);
            oPacket.EncodeInteger(value);

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(String data, int value, int x, int y, int a, int b) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(2);
            oPacket.EncodeString(data);
            oPacket.EncodeInteger(value);
            oPacket.EncodeInteger(x);
            oPacket.EncodeInteger(y);
            oPacket.Encode(a);
            if (a > 0) {
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(b);
            if (b > 1) {
                oPacket.EncodeInteger(0);
            }

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(String data, int value, int x, int y) {
            return UIPacket.UserInGameDirectionEvent(data, value, x, y, 0);
        }

        public static Packet UserInGameDirectionEvent(String data, int value, int x, int y, int npc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(2);
            oPacket.EncodeString(data);
            oPacket.EncodeInteger(value);
            oPacket.EncodeInteger(x);
            oPacket.EncodeInteger(y);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);
            oPacket.Encode(1);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(1);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(byte x, int value, int a, int b) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(5);
            oPacket.Encode(x);
            oPacket.EncodeInteger(value);
            if (x == 0) {
                oPacket.EncodeInteger(a);
                oPacket.EncodeInteger(b);
            }

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent(byte x, int value) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(5);
            oPacket.Encode(x);
            oPacket.EncodeInteger(value);

            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent_1(String data, int value, int x, int y, int npc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(2);
            oPacket.encodeString(data, false);
            oPacket.EncodeInteger(value);
            oPacket.EncodeInteger(x);
            oPacket.EncodeInteger(y);
            oPacket.Encode(1);
            oPacket.EncodeInteger(npc);
            oPacket.Encode(0);

            // Added for BeastTamer
            return oPacket.ToPacket();
        }

        public static Packet UserInGameDirectionEvent_new(byte x, int value) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserInGameDirectionEvent.getValue());
            oPacket.Encode(5);
            oPacket.Encode(x);
            oPacket.EncodeInteger(value);
            if (x == 0) {
                oPacket.EncodeInteger(value);
                oPacket.EncodeInteger(value);
            }

            return oPacket.ToPacket();
        }

        public static Packet moveScreen(int x) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MOVE_SCREEN_X.getValue());
            oPacket.EncodeInteger(x);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);

            return oPacket.ToPacket();
        }

        public static Packet screenDown() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MOVE_SCREEN_DOWN.getValue());

            return oPacket.ToPacket();
        }

        public static Packet reissueMedal(int itemId, int type) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.MedalReissueResult.getValue());
            oPacket.Encode(type);
            oPacket.EncodeInteger(itemId);

            return oPacket.ToPacket();
        }

        public static Packet playMovie(String data, boolean show) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserPlayMovieClip.getValue());
            oPacket.EncodeString(data);
            oPacket.Encode(show ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet setRedLeafStatus(int joejoe, int hermoninny, int littledragon, int ika) {
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
            OutPacket oPacket = new OutPacket(80);

            //oPacket.encodeShort();
            oPacket.EncodeInteger(7512034); //no idea
            oPacket.EncodeInteger(24316509); //no idea
            oPacket.EncodeInteger(7512034); //no idea
            oPacket.EncodeInteger(4); //no idea
            oPacket.EncodeInteger(0); //no idea
            oPacket.EncodeInteger(9410165); //joe joe
            oPacket.EncodeInteger(joejoe); //amount points added
            oPacket.EncodeInteger(9410166); //hermoninny
            oPacket.EncodeInteger(hermoninny); //amount points added
            oPacket.EncodeInteger(9410167); //little dragon
            oPacket.EncodeInteger(littledragon); //amount points added
            oPacket.EncodeInteger(9410168); //ika
            oPacket.EncodeInteger(ika); //amount points added

            return oPacket.ToPacket();
        }

        public static Packet sendRedLeaf(int points, boolean viewonly) {
            /*
             * slea:
             * 73 00 00 00
             * 0A 00 00 00
             * 01
             */
            OutPacket oPacket = new OutPacket(10);

            oPacket.EncodeShort(SendPacketOpcode.UserOpenUIWithOption.getValue());
            oPacket.EncodeInteger(0x73);
            oPacket.EncodeInteger(points);
            oPacket.Encode(viewonly ? 1 : 0); //if view only, then complete button is disabled

            return oPacket.ToPacket();
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

        public static Packet showForeignEffect(UserEffectCodes effect) {
            return showForeignEffect(-1, effect, 0);
        }

        public static Packet showForeignEffect(int cid, UserEffectCodes effect) {
            return showForeignEffect(-1, effect, 0);
        }

        public static Packet showForeignEffect(int cid, UserEffectCodes effect, int val) {
            OutPacket oPacket = new OutPacket(80);

            if (cid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(cid);
            }
            oPacket.Encode(effect.getEffectId());

            switch (effect) {
                case ItemMaker:
                    oPacket.Fill(0, 4); // hmm could this be the delay?
                    break;
                case FieldItemConsumed:
                    oPacket.EncodeInteger(val);
                    break;
            }

            oPacket.Fill(0, 69);//I really can't be fucked rn..

            return oPacket.ToPacket();
        }

        /**
         * Shows the text message with regards to burning field when entering a map
         *
         * @param bannerText
         * @return
         */
        public static Packet showBurningFieldTextEffect(String bannerText) {
            OutPacket oPacket = new OutPacket(80);

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
            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.TextEffect.getEffectId());
            //oPacket.encodeString("#fn³ª´®°íµñ ExtraBold##fs26#          Burning Stage 1: 10% Bonus EXP!  ");
            oPacket.EncodeString(String.format("#fn³ª´®°íµñ ExtraBold##fs26#          %s        ", bannerText));
            oPacket.EncodeInteger(0x32); // *(float *)&v848 = COERCE_FLOAT(CInPacket::Decode4(a2));
            oPacket.EncodeInteger(0x5DC); // *(float *)&v854 = COERCE_FLOAT(CInPacket::Decode4(a2));
            oPacket.EncodeInteger(0x4); // Align whole text    3 = Left, 5 = right, 4 = Center
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0xFFFFFF38); // Seems to be the X and Y position? 
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(4);
            oPacket.EncodeInteger(2);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);

            return oPacket.ToPacket();
        }

        public static Packet showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level);
        }

        public static Packet showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level) {
            OutPacket oPacket = new OutPacket(80);

            if (cid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(cid);
            }
            oPacket.Encode(UserEffectCodes.Skill_DiceEffect.getEffectId()); // TODO
            oPacket.EncodeInteger(effectid);
            oPacket.EncodeInteger(effectid2);
            oPacket.EncodeInteger(skillid);
            oPacket.Encode(level);
            oPacket.Encode(0);
            oPacket.Fill(0, 100);

            return oPacket.ToPacket();
        }

        public static Packet useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.ProtectOnDieItemUse.getEffectId());
            oPacket.Encode(safetyCharm ? 1 : 0);
            oPacket.Encode(charmsleft);
            oPacket.Encode(daysleft);
            if (!safetyCharm) {
                oPacket.EncodeInteger(0);
            }
            oPacket.Fill(0, 4);

            return oPacket.ToPacket();
        }

        public static Packet Mulung_DojoUp2() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(10);

            return oPacket.ToPacket();
        }

        public static Packet showOwnHpHealed(int amount) {
            return showHpHealed(-1, amount);
        }

        public static Packet showHpHealed(int cid, int amount) {
            OutPacket oPacket = new OutPacket(80);

            if (cid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(cid);
            }
            oPacket.Encode(UserEffectCodes.IncDecHPEffect_EX.getEffectId());
            oPacket.EncodeInteger(amount);

            return oPacket.ToPacket();
        }

        public static Packet showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static Packet showRewardItemAnimation(int itemId, String effect, int from_playerid) {
            OutPacket oPacket = new OutPacket(80);

            if (from_playerid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(from_playerid);
            }
            oPacket.Encode(UserEffectCodes.BuffItemEffect.getEffectId());
            oPacket.EncodeInteger(itemId);

            if (effect != null && effect.length() > 0) {
                oPacket.Encode(1);
                oPacket.EncodeString(effect);
            } else {
                oPacket.Encode(0);
            }

            return oPacket.ToPacket();
        }

        public static Packet showCashItemEffect(int itemId) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.FieldItemConsumed.getEffectId());
            oPacket.EncodeInteger(itemId);

            return oPacket.ToPacket();
        }

        public static Packet useWheel(byte charmsleft) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.UpgradeTombItemUse.getEffectId());
            oPacket.Encode(charmsleft);

            return oPacket.ToPacket();
        }

        public static Packet showOwnBuffEffect(int skillid, UserEffectCodes effect, int playerLevel, int skillLevel) {
            return showBuffeffect(-1, skillid, effect, playerLevel, skillLevel, (byte) 3);
        }

        public static Packet showOwnBuffEffect(int skillid, UserEffectCodes effect, int playerLevel, int skillLevel, byte direction) {
            return showBuffeffect(-1, skillid, effect, playerLevel, skillLevel, direction);
        }

        public static Packet showBuffeffect(int cid, int skillid, UserEffectCodes effect, int playerLevel, int skillLevel) {
            return showBuffeffect(cid, skillid, effect, playerLevel, skillLevel, (byte) 3);
        }

        public static Packet showBuffeffect(int cid, int skillid, UserEffectCodes effect, int playerLevel, int skillLevel, byte direction) {
            OutPacket oPacket = new OutPacket(80);

            if (cid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(cid);
            }
            oPacket.Encode(effect.getEffectId());  // TODO, update for V170 [check]
            oPacket.EncodeInteger(skillid);
            oPacket.Encode(playerLevel - 1);
            if (effect == UserEffectCodes.SkillUseBySummoned && skillid == 31111003) {
                oPacket.EncodeInteger(0);
            }
            oPacket.Encode(skillLevel);
            if ((direction != 3) || (skillid == 1320006) || (skillid == 30001062) || (skillid == 30001061)) {
                oPacket.Encode(direction);
            }
            if (skillid == 30001062) {
                oPacket.EncodeInteger(0);
            }
            oPacket.Fill(0, 10);

            return oPacket.ToPacket();
        }

        public static Packet showWZUOLEffect(String data, boolean flipImage) {
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
        public static Packet showWZUOLEffect(String data, boolean flipImage, int characterid, int time, int mode) {
            OutPacket oPacket = new OutPacket(80);

            if (characterid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(characterid);
            }
            oPacket.Encode(UserEffectCodes.EffectUOL.getEffectId());
            oPacket.EncodeString(data);

            oPacket.Encode(flipImage ? 1 : 0); // new in v170

            oPacket.EncodeInteger(time); // v868 = CInPacket::Decode4(a3);
            oPacket.EncodeInteger(mode); // v874 = CInPacket::Decode4(a3);

            switch (mode) {
                case 1:
                    break;
                case 2:
                    oPacket.EncodeInteger(0); // Most likely the delay.
                    break;
                case 3:
                    break;
            }

            // Client seems to do something 
            // if v868 < 0, else something
            return oPacket.ToPacket();
        }

        /**
         * Shows the reserved effect for map cut-scene
         *
         * @param data
         * @return
         */
        public static Packet showReservedEffect_CutScene(String data) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.ReservedEffect.getEffectId());

            /*LOBYTE(bFlip) = CInPacket::Decode1(iPacket) != 0;
	      nRange = CInPacket::Decode4(iPacket);
	      nNameHeight = CInPacket::Decode4(iPacket);
	      CInPacket::DecodeStr(iPacket, &sMsg);*/
            boolean show = true;
            oPacket.Encode(show);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);
            oPacket.EncodeString(data);

            return oPacket.ToPacket();
        }

        public static Packet showOwnPetLevelUp(User chr, byte index) {
            OutPacket oPacket = new OutPacket(80);
            if (chr == null) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
            }
            oPacket.Encode((int) UserEffectCodes.Pet.getEffectId());
            oPacket.Encode(new int[]{0, index, 0}[0]);
            oPacket.EncodeInteger(new int[]{0, index, 0}[1]);
            if (chr == null) {
                oPacket.EncodeInteger(new int[]{0, index, 0}[2]);
            }

            return oPacket.ToPacket();
        }

        public static Packet showOwnPVPChampionEffect() {
            return showPVPChampionEffect(-1);
        }

        public static Packet showPVPChampionEffect(int from_playerid) {
            OutPacket oPacket = new OutPacket(80);

            if (from_playerid == -1) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            } else {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeInteger(from_playerid);
            }
            oPacket.Encode(UserEffectCodes.PvPChampion.getEffectId());  // TODO, update for V170
            oPacket.EncodeInteger(30000);

            return oPacket.ToPacket();
        }

        public static Packet showWeirdEffect(String effect, int itemId) {
            final OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.Encode(UserEffectCodes.PlayExclSoundWithDownBGM.getEffectId());
            oPacket.EncodeString(effect);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);//weird high number is it will keep showing it lol
            oPacket.EncodeInteger(2);
            oPacket.EncodeInteger(itemId);
            return oPacket.ToPacket();
        }

        public static Packet showWeirdEffect(int chrId, String effect, int itemId) {
            final OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
            oPacket.EncodeInteger(chrId);
            oPacket.Encode(UserEffectCodes.PlayExclSoundWithDownBGM.getEffectId());
            oPacket.EncodeString(effect);
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);//weird high number is it will keep showing it lol
            oPacket.EncodeInteger(2);//this makes it read the itemId
            oPacket.EncodeInteger(itemId);

            return oPacket.ToPacket();
        }
    }

    public static Packet sendBoxDebug(short opcode, int itemId, List<Integer> items) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(opcode);
        oPacket.EncodeShort(1);
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeInteger(items.size());
        for (int item : items) {
            oPacket.EncodeInteger(item);
        }
        return oPacket.ToPacket();
    }

    public static Packet getCassandrasCollection() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BingoCassandraResult.getValue());
        oPacket.Encode(6);

        return oPacket.ToPacket();
    }

    public static Packet unsealBox(int reward) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEffectLocal.getValue());
        oPacket.Encode(0x31);
        oPacket.Encode(1);
        oPacket.EncodeInteger(reward);
        oPacket.EncodeInteger(1);

        return oPacket.ToPacket();
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
    public static Packet consumeItemEffect(int cId, int itemId) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserConsumeItemEffect.getValue());
        oPacket.EncodeInteger(cId);
        oPacket.EncodeInteger(itemId);
        return oPacket.ToPacket();
    }

    /**
     * This packet is sent to the client so the client can update what tag state it is in
     *
     * @param chr
     * @return MapleCharacter chr - Character object
     */
    public static Packet zeroTagState(User chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ZERO_TAG_STATE.getValue());
        oPacket.EncodeInteger(chr.getId());

        return oPacket.ToPacket();
    }

    public static class RunePacket {

        public static Packet runeMsg(int type, long time) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.SHOW_MAP_NAME.getValue());
            oPacket.EncodeInteger(type);
            switch (type) {
                case 2://Cant use another rune yet (time left)
                    oPacket.EncodeInteger((int) time);
                    break;
                case 4://That rune is to strong for you to handle
                    break;
                case 5://Shows arrows
                    break;
            }

            return oPacket.ToPacket();
        }

        public static Packet spawnRune(MapleRuneStone rune) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RuneEnterField.getValue());
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(rune.getRuneType().getType());
            oPacket.EncodeInteger((int) rune.getPosition().getX());
            oPacket.EncodeInteger((int) rune.getPosition().getY());
            oPacket.Encode(rune.isFacingLeft());

            return oPacket.ToPacket();
        }

        public static Packet removeRune(MapleRuneStone rune, User chr) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RuneActSuccess.getValue());
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(chr.getId());

            return oPacket.ToPacket();
        }

        public static Packet removeAllRune() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RUNE_STONE_CLEAR_AND_ALL_REGISTER.getValue());
            int count = 0;
            oPacket.EncodeInteger(count); // count
            for (int i = 0; i < count; i++) {
                oPacket.EncodeInteger(0); // not sure, but whatever
            }

            return oPacket.ToPacket();
        }

        public static Packet finishRune() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RuneLeaveField.getValue());
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(6041607);//idk?//was 7032481

            return oPacket.ToPacket();
        }

        public static Packet RuneAction(int type, int time) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RuneActSuccess.getValue());
            oPacket.EncodeInteger(type);
            oPacket.EncodeInteger(time);

            return oPacket.ToPacket();
        }

        public static Packet showRuneEffect(int type) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.RuneStoneSkillAck.getValue());
            oPacket.EncodeInteger(type);

            return oPacket.ToPacket();
        }

    }
}
