package tools.packet;

import java.util.List;
import java.util.Set;

import client.ClientSocket;
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

import server.maps.objects.User;
import service.ChannelServer;

public class CLogin {

    public static OutPacket Handshake(int sendIv, int recvIv) {
        OutPacket oPacket = new OutPacket((short) 15);

        oPacket.EncodeShort(ServerConstants.MAPLE_VERSION);
        oPacket.EncodeString(ServerConstants.MAPLE_PATCH);
        oPacket.EncodeInt(recvIv);
        oPacket.EncodeInt(sendIv);
        oPacket.EncodeByte(ServerConstants.MAPLE_LOCALE);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static final OutPacket AliveReq() {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.AliveReq.getValue());

        return oPacket;
    }

    public static final OutPacket ApplyHotFix() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ApplyHotfix.getValue());
        byte[] hotfix = ServerConstants.hotfix();
        if (ServerConstants.APPLY_HOTFIX && hotfix != null && hotfix.length > 0) {
            oPacket.EncodeInt(hotfix.length);
            oPacket.Encode(hotfix);
        } else {
            oPacket.EncodeBool(true);
        }

        return oPacket;
    }

    /**
     * The server's confirmation back to the client that the server is ready to start the auth cycle.
     *
     * @return the packet that confirms to the client that the server is ready for the auth cycle
     */
    public static final OutPacket NCMOResult(boolean useAuthServer) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NMCOResult.getValue());
        oPacket.EncodeByte(useAuthServer ? 0 : 1);

        if (ServerConstants.FORCE_HOTFIX) {
            CLogin.ApplyHotFix(); // Fixes Nexon client issue where this packet sometimes isn't requested.
        }

        return oPacket;
    }

    public static OutPacket CheckPasswordResult(ClientSocket client) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.EncodeByte(0);//related to blocked account
        /**
         * If above is equal to two If we want to add block reasons: oPacket.encode() //nBlockReason oPacket.encodeLong(data)
         *
         */
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeString(client.getAccountName());
        oPacket.EncodeInt(client.getAccID());
        oPacket.EncodeByte(client.getGender());

        //Admin Byte
        oPacket.EncodeByte(0); //oPacket.encode(client.isGm());
        // Set all the bits for GM, so we can test out stuff
        int subcode = 0; // just a placeholder for now. It is gmLevel * 64 

        /*v60 = (v59 >> 4) & 1;
    	v61 = (v59 >> 5) & 1;
    	pBlockReasonIter.m_pInterface = ((v59 >> 13) & 1);
    	For the int below*/
        oPacket.EncodeInt(subcode);
        oPacket.EncodeInt(0); // some time value. It is set to 0 for me from my sniffing session

        //Admin Byte?
        oPacket.EncodeByte(0); //oPacket.encode(client.isGm());

        oPacket.EncodeString(client.getAccountName());
        oPacket.EncodeByte(3); // 3 on GMS
        oPacket.EncodeByte(0);
        oPacket.EncodeLong(0); // some time value. 0 on GMS
        oPacket.EncodeLong(PacketHelper.getTime(client.getCreated().getTime())); // creation time
        oPacket.EncodeInt(0x20); // unknown int
        getJobList(oPacket);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(-1);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(1);
        oPacket.EncodeLong(0); // Session ID

        // Pop Up Message
        if (ServerConstants.SHOW_LOADING_MESSAGE && !client.isGm()) {
            client.SendPacket(WvsContext.broadcastMsg(""));
            client.SendPacket(WvsContext.broadcastMsg(1, "Welcome " + client.getAccountName() + ", "
                    + "\r\n\r\nPlease be patient while the game client is loading data during your character selection."
                    + "\r\n\r\nThis process may take a long time."));
        }

        return oPacket;
    }

    /**
     * This packet is a extension of getJobListPacket() It contains the information regarding which job is enabled, and if certain jobs can
     * be enabled.
     *
     * @param oPacket
     */
    public static final void getJobList(OutPacket oPacket) {
        oPacket.EncodeByte(ServerConstants.HIDE_STAR_PLANET_WORLD_UI ? 1 : 0); //toggle star planet world UI
        oPacket.EncodeByte(4); // Doesn't appear to write job order anymore... (totes does tho ya cunt)
        for (LoginJob j : LoginJob.values()) {
            oPacket.EncodeByte(j.getFlag());
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
    public static final OutPacket getJobListPacket() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetCharacterCreation.getValue());
        getJobList(oPacket);

        return oPacket;
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
    public static final OutPacket getAccountSpecifications(ClientSocket c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AccountInfoResult.getValue());
        oPacket.EncodeByte(0); //unk
        oPacket.EncodeInt(c.getAccID());
        oPacket.Encode(new byte[11]);
        oPacket.EncodeString(c.getAccountName());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.Encode(new byte[8]); // BUFFER	[0000000000000000] 
        oPacket.EncodeString(c.getAccountName());
        oPacket.EncodeLong(PacketHelper.getTime(c.getCreated().getTime()));
        oPacket.EncodeInt(0x24);
        oPacket.Encode(new byte[10]); //unk
        getJobList(oPacket);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(1);
        oPacket.EncodeShort(0);
        oPacket.EncodeInt(-1);

        return oPacket;
    }

    public static final OutPacket getLoginFailed(int reason) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.EncodeByte(reason);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);

        return oPacket;
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
    public static OutPacket getPermBan(byte reason) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.EncodeByte(2);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeShort(reason);
        oPacket.Encode(HexTool.getByteArrayFromHexString("01 01 01 01 00"));
        return oPacket;
    }

    public static OutPacket getTempBan(long timestampTill, byte reason) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckPasswordResult.getValue());
        oPacket.EncodeByte(2);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(reason);
        oPacket.EncodeLong(timestampTill);

        return oPacket;
    }

    public static final OutPacket deleteCharResponse(int cid, int state) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DeleteCharacterResult.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(state);

        return oPacket;
    }

    public static OutPacket secondPwError(byte mode) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckSPWExistResult.getValue());
        oPacket.EncodeShort(mode);

        return oPacket;
    }

    /**
     * Sends the client an authentication response it requested. This is used by the client to check against unauthorized launching of
     * clients. >>> ?OnPrivateServerAuth@CClientSocket@@IAEXAAVCInPacket@@@Z
     *
     * @param clientCurrentThreadID
     * @return
     */
    public static OutPacket sendAuthResponse(int clientCurrentThreadID) {

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
        OutPacket oPacket = new OutPacket(SendPacketOpcode.PrivateServerPacket.getValue());
        int response = clientCurrentThreadID ^ SendPacketOpcode.PrivateServerPacket.getValue();
        oPacket.EncodeInt(response);

        return oPacket;
    }

    public static OutPacket enableRecommended(int world) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.LatestConnectedWorld.getValue());
        oPacket.EncodeInt(world);

        return oPacket;
    }

    public static OutPacket sendRecommended(int world, String message) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RecommendWorldMessage.getValue());
        oPacket.EncodeByte(message != null ? 1 : 0);
        if (message != null) {
            oPacket.EncodeInt(world);
            oPacket.EncodeString(message);
        }

        return oPacket;
    }

    public static OutPacket getServerList(int serverId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WorldInformation.getValue());
        oPacket.EncodeByte(serverId);

        final String worldName = LoginServer.getInstance().getTrueServerName();
        oPacket.EncodeString(worldName);
        oPacket.EncodeByte(WorldOption.getById(serverId).getFlag());
        oPacket.EncodeString(ServerConstants.EVENT_MESSAGE);
        oPacket.EncodeShort(100); // event EXP
        oPacket.EncodeShort(100); // event drop
        oPacket.EncodeByte(0); // block char creation

        Set<Integer> channels = LoginServer.getInstance().getLoad().keySet();
        oPacket.EncodeByte(channels.size());

        int channelId = 0;
        for (Integer channel : channels) {
            channelId++;

            oPacket.EncodeString(worldName + "-" + channelId);

            //Channel Load Bar Formula
            oPacket.EncodeInt(Math.max(2, ChannelServer.getChannelLoad().get(channel) * 64 / (ServerConstants.USER_LIMIT / ServerConstants.CHANNEL_COUNT)) + 3);

            oPacket.EncodeByte(serverId);
            oPacket.EncodeShort(channelId - 1);
        }

        oPacket.EncodeShort(GameConstants.getBalloons().size());
        for (Balloon balloon : GameConstants.getBalloons()) {
            oPacket.EncodeShort(balloon.nX);
            oPacket.EncodeShort(balloon.nY);
            oPacket.EncodeString(balloon.sMessage);
        }
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket getEndOfServerList() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WorldInformation.getValue());
        oPacket.EncodeByte(0xFF);
        oPacket.EncodeByte(0); // boolean disable cash shop and trade msg
        oPacket.EncodeByte(0); // 174.1
        oPacket.EncodeByte(0); // 174.1

        return oPacket;
    }

    public static OutPacket getServerStatus(int status) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserLimitResult.getValue());
        oPacket.EncodeShort(status);

        return oPacket;
    }

    /**
     * The packet to send the current background images for world selection Map.wz/Obj/login.img/WorldSelect/background/background is the
     * location in the wz.
     *
     * @return oPacket
     */
    public static OutPacket changeBackground() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BackgroundEffect.getValue());
        oPacket.EncodeByte(WorldServerBackgroundHandler.values().length);

        for (WorldServerBackgroundHandler backgrounds : WorldServerBackgroundHandler.values()) {
            oPacket.EncodeString(backgrounds.getImage());
            oPacket.EncodeBool(backgrounds.getFlag());
        }

        return oPacket;
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
    public static OutPacket getCharList(ClientSocket c, String secondpw, List<User> chars, int charslots, boolean isRebootServer) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SelectWorldResult.getValue());
        oPacket.EncodeByte(0); // mode
        oPacket.EncodeString(isRebootServer ? "reboot" : " normal");
        oPacket.EncodeInt(4);
        oPacket.EncodeByte((c.isGm() || ServerConstants.BURNING_CHARACTER_EVENT) ? 0 : 1);//m_BurningEventBlock
        oPacket.EncodeInt(0); // if > 0, write int and long
        long currentTime = System.currentTimeMillis();
        oPacket.EncodeLong(PacketHelper.getTime(currentTime)); // I guess. All I know is it is some time value.
        oPacket.EncodeByte(0); // not sure what this is?

        // Characters by order
        User[] characters = chars.toArray(new User[chars.size()]);
        oPacket.EncodeInt(characters.length); // the character reorganization packet
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
            oPacket.EncodeInt(characters[i].getId());
        }

        // Characters information
        oPacket.EncodeByte(chars.size());
        for (User chr : chars) {
            addCharEntry(oPacket, chr);
            oPacket.EncodeByte(0);

            boolean ranking = (!chr.isGM()) && (chr.getLevel() >= 30);
            oPacket.EncodeByte(ranking ? 1 : 0);
            if (ranking) {
                oPacket.EncodeInt(chr.getRank());
                oPacket.EncodeInt(chr.getRankMove());
                oPacket.EncodeInt(chr.getJobRank());
                oPacket.EncodeInt(chr.getJobRankMove());
            }
        }

        if (ServerConstants.ENABLE_PIC) {
            oPacket.EncodeByte(secondpw == null || secondpw.length() == 0 ? 0 : 1);
        } else {
            oPacket.EncodeByte(2);
        }
        oPacket.EncodeByte(0); //you can enable and disable character slots with this 
        oPacket.EncodeInt(charslots);
        oPacket.EncodeInt(0); //char slots bought with cs coupons
        oPacket.EncodeInt(-1);
        oPacket.EncodeLong(PacketHelper.getTime(currentTime));
        oPacket.EncodeByte(0);//Enables Name Change UI. (click it to change charname :)
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);

        return oPacket;
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
    public static OutPacket addNewCharEntry(User chr, int result) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CreateNewCharacterResult.getValue());
        oPacket.EncodeByte(result); // this is an int, according to KMST
        if (result == 0) {
            addCharEntry(oPacket, chr);
        }
        return oPacket;
    }

    public static OutPacket charNameResponse(String charname, boolean nameUsed) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckDuplicatedIDResult.getValue());
        oPacket.EncodeString(charname);
        oPacket.EncodeByte(nameUsed ? 1 : 0);

        return oPacket;
    }

    private static void addCharEntry(OutPacket oPacket, User chr) {
        PacketHelper.addCharStats(oPacket, chr);
        PacketHelper.addCharLook(oPacket, chr, true, false);
        if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(oPacket, chr, true, true);
        }
    }

    public static OutPacket partTimeJob(int cid, short type, long time) {
        //1) 0A D2 CD 01 70 59 9F EA
        //2) 0B D2 CD 01 B0 6B 9C 18
        PartTimeJob job = new PartTimeJob(cid);
        job.setJob((byte) type);
        job.setTime(time);

        return updatePartTimeJob(job);
    }

    public static OutPacket updatePartTimeJob(PartTimeJob partTime) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AlbaRequestResult.getValue());
        oPacket.EncodeInt(partTime.getCharacterId());
        oPacket.EncodeByte(0);
        PacketHelper.addPartTimeJob(oPacket, partTime);

        return oPacket;
    }

    /**
     * Gets a packet detailing the changing of pic result 0 = success 46 = pic is too recent
     *
     * @return oPacket
     */
    public static OutPacket changePic(byte result) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeSPWResult.getValue());
        oPacket.EncodeByte(result);

        return oPacket;
    }

    /**
     * Enables the character burning event effect.
     *
     * @param operation: 0 = Fire Effect 6 = A brokem message popup
     */
    public static OutPacket burningEventEffect(byte operation, int characterId) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterBurning.getValue());
        oPacket.EncodeByte(operation);
        oPacket.EncodeInt(characterId);
        return oPacket;
    }

    public static OutPacket OnMapLogin(String sMapLogin) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.MapLogin.getValue());
        oPacket.EncodeString(sMapLogin);
        return oPacket;
    }
}
