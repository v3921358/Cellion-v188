package tools.packet;

import java.util.List;
import java.util.Set;

import client.MapleClient;
import client.PartTimeJob;
import constants.GameConstants;
import constants.JobConstants;
import constants.JobConstants.LoginJob;
import constants.ServerConstants;
import constants.WorldConstants.WorldOption;
import handling.login.Balloon;
import handling.login.WorldServerBackgroundHandler;
import service.LoginServer;
import service.SendPacketOpcode;
import provider.data.HexTool;
import net.OutPacket;
import net.Packet;
import server.maps.objects.User;
import service.ChannelServer;

public class CLogin {

    public static Packet Handshake(int sendIv, int recvIv) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(15);
        oPacket.EncodeShort(ServerConstants.MAPLE_VERSION);
        oPacket.EncodeString(ServerConstants.MAPLE_PATCH);
        oPacket.EncodeInteger(recvIv);
        oPacket.EncodeInteger(sendIv);
        oPacket.Encode(ServerConstants.MAPLE_LOCALE);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static final Packet AliveReq() {
        OutPacket oPacket = new OutPacket(2);

        oPacket.EncodeShort(SendPacketOpcode.AliveReq.getValue());

        return oPacket.ToPacket();
    }

    public static final Packet ApplyHotFix() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ApplyHotfix.getValue());
        byte[] hotfix = ServerConstants.hotfix();
        if (ServerConstants.APPLY_HOTFIX && hotfix != null && hotfix.length > 0) {
            oPacket.EncodeInteger(hotfix.length);
            oPacket.Encode(hotfix);
        } else {
            oPacket.Encode(true);
        }

        return oPacket.ToPacket();
    }

    /**
     * The server's confirmation back to the client that the server is ready to start the auth cycle.
     *
     * @return the packet that confirms to the client that the server is ready for the auth cycle
     */
    public static final Packet NCMOResult(boolean useAuthServer) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NMCOResult.getValue());
        oPacket.Encode(useAuthServer ? 0 : 1);

        if (ServerConstants.FORCE_HOTFIX) {
            CLogin.ApplyHotFix(); // Fixes Nexon client issue where this packet sometimes isn't requested.
        }
        
        return oPacket.ToPacket();
    }

    public static Packet CheckPasswordResult(MapleClient client) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.Encode(0);//related to blocked account
        /**
         * If above is equal to two If we want to add block reasons: oPacket.encode() //nBlockReason oPacket.encodeLong(data)
         *
         */
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeString(client.getAccountName());
        oPacket.EncodeInteger(client.getAccID());
        oPacket.Encode(client.getGender());

        //Admin Byte
        oPacket.Encode(0); //oPacket.encode(client.isGm());
        // Set all the bits for GM, so we can test out stuff
        int subcode = 0; // just a placeholder for now. It is gmLevel * 64 

        /*v60 = (v59 >> 4) & 1;
    	v61 = (v59 >> 5) & 1;
    	pBlockReasonIter.m_pInterface = ((v59 >> 13) & 1);
    	For the int below*/
        oPacket.EncodeInteger(subcode);
        oPacket.EncodeInteger(0); // some time value. It is set to 0 for me from my sniffing session

        //Admin Byte?
        oPacket.Encode(0); //oPacket.encode(client.isGm());

        oPacket.EncodeString(client.getAccountName());
        oPacket.Encode(3); // 3 on GMS
        oPacket.Encode(0);
        oPacket.EncodeLong(0); // some time value. 0 on GMS
        oPacket.EncodeLong(PacketHelper.getTime(client.getCreated().getTime())); // creation time
        oPacket.EncodeInteger(0x20); // unknown int
        getJobList(oPacket);
        oPacket.Encode(0);
        oPacket.EncodeInteger(-1);
        oPacket.Encode(1);
        oPacket.Encode(1);
        oPacket.EncodeLong(0); // Session ID

        // Pop Up Message
        if (ServerConstants.SHOW_LOADING_MESSAGE && !client.isGm()) {
            client.write(CWvsContext.broadcastMsg(""));
            client.write(CWvsContext.broadcastMsg(1, "Welcome " + client.getAccountName() + ", "
                    + "\r\n\r\nPlease be patient while the game client is loading data during your character selection."
                    + "\r\n\r\nThis process may take a long time."));
        }

        return oPacket.ToPacket();
    }

    /**
     * This packet is a extension of getJobListPacket() It contains the information regarding which job is enabled, and if certain jobs can
     * be enabled.
     *
     * @param oPacket
     */
    public static final void getJobList(OutPacket oPacket) {
        oPacket.Encode(ServerConstants.HIDE_STAR_PLANET_WORLD_UI ? 1 : 0); //toggle star planet world UI
        oPacket.Encode(4); // Doesn't appear to write job order anymore... (totes does tho ya cunt)
        for (LoginJob j : LoginJob.values()) {
            oPacket.Encode(j.getFlag());
            oPacket.EncodeShort(1);
        }
    }

    /**
     * This packet is a what dictates which jobs are enabled It contains the information regarding which job is enabled, and if certain jobs
     * can be enabled.
     *
     * @param
     * @return oPacket
     */
    public static final Packet getJobListPacket() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetCharacterCreation.getValue());
        getJobList(oPacket);

        return oPacket.ToPacket();
    }

    /*
     *  00 
     *  22 22 29 01 
     *  00 00 00 00 
     *  00 00 00 00 
     *  00 00 00 
     *  [xxxxxxxx //Maple-ascii string with account username
     *  03 00 00 00 
     *  00 00 00 00 
     *  00 00 
     *  [xxxx] //Maple-ascii string with account username
     *  [B0 0F 89 DE 82 FD C9 01] //created date and time
     *  24 00 00 00 
     *  6C 72 4E 25 CB 05 C6 65 00 00 
		//add in job order bullshit here
       FF FF FF FF
       
    
      //something else
       [6E 9C 5E 00] //characterId 
       [0A 00] [4C 6F 6E 67 4B 61 74 61 72 61] 
       [E3 4D 00 00] 
       [EC CB 00 00] 
       [3E 2A 00 00] 
       [98 41 00 00] 
       [74 E0 00 00] 
       [DF 07] 
       [0A 00] 
       [05 00] 
       [10 00] 
       [17 00] 
       [30 00] 
       [1F 00] 
       [5E 02] 
       
       [DF 07] 
       [0A 00] 
       [05 00] 
       [10 00] 
       [17 00] 
       [32 00] 
       [31 00] 
       [B3 03]
     */
    public static final Packet getAccountSpecifications(MapleClient c) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AccountInfoResult.getValue());
        oPacket.Encode(0); //unk
        oPacket.EncodeInteger(c.getAccID());
        oPacket.Encode(new byte[11]);
        oPacket.EncodeString(c.getAccountName());
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.Encode(new byte[8]); // BUFFER	[0000000000000000] 
        oPacket.EncodeString(c.getAccountName());
        oPacket.EncodeLong(PacketHelper.getTime(c.getCreated().getTime()));
        oPacket.EncodeInteger(0x24);
        oPacket.Encode(new byte[10]); //unk
        getJobList(oPacket);
        oPacket.Encode(1);
        oPacket.Encode(1);
        oPacket.EncodeShort(0);
        oPacket.EncodeInteger(-1);

        return oPacket.ToPacket();
    }

    public static final Packet getLoginFailed(int reason) {
        OutPacket oPacket = new OutPacket(16);

        oPacket.EncodeShort(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.Encode(reason);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    /*
     * location: UI.wz/Login.img/Notice/text
     * reasons:
     * useful:
     * 32 - server under maintenance check site for updates
     * 35 - your computer is running thirdy part programs close them and play again
     * 36 - due to high population char creation has been disabled
     * 43 - revision needed your ip is temporary blocked
     * 75-78 are cool for auto register
     
     */
    public static Packet getPermBan(byte reason) {
        OutPacket oPacket = new OutPacket(16);

        oPacket.EncodeShort(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeShort(reason);
        oPacket.Encode(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return oPacket.ToPacket();
    }

    public static Packet getTempBan(long timestampTill, byte reason) {
        OutPacket oPacket = new OutPacket(17);

        oPacket.EncodeShort(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);
        oPacket.Encode(reason);
        oPacket.EncodeLong(timestampTill);

        return oPacket.ToPacket();
    }

    public static final Packet deleteCharResponse(int cid, int state) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DeleteCharacterResult.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(state);

        return oPacket.ToPacket();
    }

    public static Packet secondPwError(byte mode) {
        OutPacket oPacket = new OutPacket(3);

        oPacket.EncodeShort(SendPacketOpcode.CheckSPWExistResult.getValue());
        oPacket.EncodeShort(mode);

        return oPacket.ToPacket();
    }

    /**
     * Sends the client an authentication response it requested. This is used by the client to check against unauthorized launching of
     * clients. >>> ?OnPrivateServerAuth@CClientSocket@@IAEXAAVCInPacket@@@Z
     *
     * @param clientCurrentThreadID
     * @return
     */
    public static Packet sendAuthResponse(int clientCurrentThreadID) {
        OutPacket oPacket = new OutPacket(80);
        /*void __cdecl CClientSocket::OnPrivateServerAuth(CInPacket *iPacket) {
          unsigned int dwPrivateServerKey; // STA0_4@1
          struct _TEB *v2; // eax@7
          _DWORD *v3; // ecx@7
          _DWORD *i; // eax@7

          dwPrivateServerKey = CInPacket::Decode4(iPacket);
          if ( dwPrivateServerKey == (GetCurrentThreadId() ^ 0x91) )
          {
            if ( TSecType<int>::operator int(&g_bisAuthPacket) )
            {
              TSecType<int>::operator=(&g_bisAuthPacket, 0);
            }
            else if ( TSecType<int>::operator int(&g_bisAuthPacket2) )
            {
              TSecType<int>::operator=(&g_bisAuthPacket2, 0);
            }
          }
          else
          {
            v2 = NtCurrentTeb();
            v3 = v2->NtTib.StackLimit;
            for ( i = v2->NtTib.StackBase; i > v3; *i = 0 )
              --i;
          }
        }
         */

        oPacket.EncodeShort(SendPacketOpcode.PrivateServerPacket.getValue());
        int response = clientCurrentThreadID ^ SendPacketOpcode.PrivateServerPacket.getValue();
        oPacket.EncodeInteger(response);

        return oPacket.ToPacket();
    }

    public static Packet enableRecommended(int world) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.LatestConnectedWorld.getValue());
        oPacket.EncodeInteger(world);

        return oPacket.ToPacket();
    }

    public static Packet sendRecommended(int world, String message) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.RecommendWorldMessage.getValue());
        oPacket.Encode(message != null ? 1 : 0);
        if (message != null) {
            oPacket.EncodeInteger(world);
            oPacket.EncodeString(message);
        }

        return oPacket.ToPacket();
    }

    public static Packet getServerList(int serverId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WorldInformation.getValue());
        oPacket.Encode(serverId);

        final String worldName = LoginServer.getInstance().getTrueServerName();
        oPacket.EncodeString(worldName);
        oPacket.Encode(WorldOption.getById(serverId).getFlag());
        oPacket.EncodeString(ServerConstants.EVENT_MESSAGE);
        oPacket.EncodeShort(100); // event EXP
        oPacket.EncodeShort(100); // event drop
        oPacket.Encode(0); // block char creation

        Set<Integer> channels = LoginServer.getInstance().getLoad().keySet();
        oPacket.Encode(channels.size());

        int channelId = 0;
        for (Integer channel : channels) {
            channelId++;

            oPacket.EncodeString(worldName + "-" + channelId);

            //Channel Load Bar Formula
            oPacket.EncodeInteger(Math.max(2, ChannelServer.getChannelLoad().get(channel) * 64 / (ServerConstants.USER_LIMIT / ServerConstants.CHANNEL_COUNT)) + 3);

            oPacket.Encode(serverId);
            oPacket.EncodeShort(channelId - 1);
        }

        oPacket.EncodeShort(GameConstants.getBalloons().size());
        for (Balloon balloon : GameConstants.getBalloons()) {
            oPacket.EncodeShort(balloon.nX);
            oPacket.EncodeShort(balloon.nY);
            oPacket.EncodeString(balloon.sMessage);
        }
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet getEndOfServerList() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WorldInformation.getValue());
        oPacket.Encode(0xFF);
        oPacket.Encode(0); // boolean disable cash shop and trade msg
        oPacket.Encode(0); // 174.1
        oPacket.Encode(0); // 174.1

        return oPacket.ToPacket();
    }

    public static Packet getServerStatus(int status) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserLimitResult.getValue());
        oPacket.EncodeShort(status);

        return oPacket.ToPacket();
    }

    /**
     * The packet to send the current background images for world selection Map.wz/Obj/login.img/WorldSelect/background/background is the
     * location in the wz.
     *
     * @return oPacket
     */
    public static Packet changeBackground() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BackgroundEffect.getValue());
        oPacket.Encode(WorldServerBackgroundHandler.values().length);

        for (WorldServerBackgroundHandler backgrounds : WorldServerBackgroundHandler.values()) {
            oPacket.EncodeString(backgrounds.getImage());
            oPacket.Encode(backgrounds.getFlag());
        }

        return oPacket.ToPacket();
    }

    /**
     * @param secondpw - The string for pic
     * @param chars - A list of MapleCharacter objects
     * @param charslots - The amount of slots an account has
     * @param isRebootServer - Whether this server belongs to reboot serv
     *
     * This packet handles the character list, a few things to note: -Pic or "secondpw" known values: 0 = No pic 1 = Account has a pic 2 =
     * Dummy pic 4 = Outdated pic
     * @return oPacket
     */
    public static Packet getCharList(MapleClient c, String secondpw, List<User> chars, int charslots, boolean isRebootServer) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SelectWorldResult.getValue());
        oPacket.Encode(0); // mode
        oPacket.EncodeString(isRebootServer ? "reboot" : " normal");
        oPacket.EncodeInteger(4);
        oPacket.Encode((c.isGm() || ServerConstants.BURNING_CHARACTER_EVENT) ? 0 : 1);//m_BurningEventBlock
        oPacket.EncodeInteger(0); // if > 0, write int and long
        long currentTime = System.currentTimeMillis();
        oPacket.EncodeLong(PacketHelper.getTime(currentTime)); // I guess. All I know is it is some time value.
        oPacket.Encode(0); // not sure what this is?

        // Characters by order
        User[] characters = chars.toArray(new User[chars.size()]);
        oPacket.EncodeInteger(characters.length); // the character reorganization packet
        for (int i = 0; i < characters.length - 1; i++) {
            if (characters[i].getCharListPosition() > characters[i + 1].getCharListPosition()) {
                User temp = characters[i];
                User temp2 = characters[i + 1];
                characters[i] = temp2;
                characters[i + 1] = temp;
                i = -1;
            }
        }
        for (int i = 0; i < characters.length; i++) {
            oPacket.EncodeInteger(characters[i].getId());
        }

        // Characters information
        oPacket.Encode(chars.size());
        for (User chr : chars) {
            addCharEntry(oPacket, chr);
            oPacket.Encode(0);

            boolean ranking = (!chr.isGM()) && (chr.getLevel() >= 30);
            oPacket.Encode(ranking ? 1 : 0);
            if (ranking) {
                oPacket.EncodeInteger(chr.getRank());
                oPacket.EncodeInteger(chr.getRankMove());
                oPacket.EncodeInteger(chr.getJobRank());
                oPacket.EncodeInteger(chr.getJobRankMove());
            }
        }

        if (ServerConstants.ENABLE_PIC) {
            oPacket.Encode(secondpw == null || secondpw.length() == 0 ? 0 : 1);
        } else {
            oPacket.Encode(2);
        }
        oPacket.Encode(0); //you can enable and disable character slots with this 
        oPacket.EncodeInteger(charslots);
        oPacket.EncodeInteger(0); //char slots bought with cs coupons
        oPacket.EncodeInteger(-1);
        oPacket.EncodeLong(PacketHelper.getTime(currentTime));
        oPacket.Encode(0);//Enables Name Change UI. (click it to change charname :)
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    /**
     * Response on creating a new character. If the result = 10, 30, or 62 it means there is an issue creating it and thus you need not sent
     * the addCharEntry data See: http://pastebin.com/R6fQMwcV 0: successful, requires sending of addCharEntry data too 10: The server is
     * too busy to fulfill your request right now
     *
     * @param chr
     * @param result
     * @return
     */
    public static Packet addNewCharEntry(User chr, int result) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CreateNewCharacterResult.getValue());
        oPacket.Encode(result); // this is an int, according to KMST
        if (result == 0) {
            addCharEntry(oPacket, chr);
        }
        return oPacket.ToPacket();
    }

    public static Packet charNameResponse(String charname, boolean nameUsed) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CheckDuplicatedIDResult.getValue());
        oPacket.EncodeString(charname);
        oPacket.Encode(nameUsed ? 1 : 0);

        return oPacket.ToPacket();
    }

    private static void addCharEntry(OutPacket oPacket, User chr) {
        PacketHelper.addCharStats(oPacket, chr);
        PacketHelper.addCharLook(oPacket, chr, true, false);
        if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(oPacket, chr, true, true);
        }
    }

    public static Packet partTimeJob(int cid, short type, long time) {
        //1) 0A D2 CD 01 70 59 9F EA
        //2) 0B D2 CD 01 B0 6B 9C 18
        PartTimeJob job = new PartTimeJob(cid);
        job.setJob((byte) type);
        job.setTime(time);

        return updatePartTimeJob(job);
    }

    public static Packet updatePartTimeJob(PartTimeJob partTime) {
        OutPacket oPacket = new OutPacket(21);

        oPacket.EncodeShort(SendPacketOpcode.AlbaRequestResult.getValue());
        oPacket.EncodeInteger(partTime.getCharacterId());
        oPacket.Encode(0);
        PacketHelper.addPartTimeJob(oPacket, partTime);

        return oPacket.ToPacket();
    }

    /**
     * Gets a packet detailing the changing of pic result 0 = success 46 = pic is too recent
     *
     * @return oPacket
     */
    public static Packet changePic(byte result) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ChangeSPWResult.getValue());
        oPacket.Encode(result);

        return oPacket.ToPacket();
    }

    /**
     * Enables the character burning event effect.
     *
     * @param operation: 0 = Fire Effect 6 = A brokem message popup
     */
    public static Packet burningEventEffect(byte operation, int characterId) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CharacterBurning.getValue());
        oPacket.Encode(operation);
        oPacket.EncodeInteger(characterId);
        return oPacket.ToPacket();
    }

    public static Packet OnMapLogin(String sMapLogin) {
        final OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MapLogin.getValue());
        oPacket.EncodeString(sMapLogin);
        return oPacket.ToPacket();
    }
}
