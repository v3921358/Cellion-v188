package handling.game;

import client.MapleCharacterUtil;
import client.ClientSocket;
import constants.ServerConstants;
import handling.PacketThrottleLimits;
import handling.world.World;
import net.InPacket;
import server.commands.CommandProcessor;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.WvsContext;
import net.ProcessPacket;
import tools.Utility;
import tools.packet.CField;

/**
 *
 * @author Mazen Massoud
 */
public class WhisperHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int dwFlag = iPacket.DecodeByte();
        c.getPlayer().updateTick(iPacket.DecodeInt()); // tRequestTime
        if (dwFlag == WhisperFlag.ReplyRequest || dwFlag == WhisperFlag.FindRequest || dwFlag == WhisperFlag.BlockedResult || dwFlag == WhisperFlag.LocationRequest) {
            String sTarget = iPacket.DecodeString();
            User pUser = Utility.requestCharacter(sTarget); //c.getChannelServer().getPlayerStorage().getCharacterByName(sTarget);

            if (dwFlag == WhisperFlag.ReplyRequest || dwFlag == WhisperFlag.BlockedResult) {
                String sText = iPacket.DecodeString();
                if (pUser != null && pUser.getMap() != null) {
                    pUser.SendPacket(CField.OnWhisper(WhisperFlag.ReplyReceive, null, c.getPlayer().getName(), sText, 0, (c.getChannel() - 1), 0, 0, c.getPlayer().isAdmin(), false));
                    c.getPlayer().SendPacket(CField.OnWhisper(WhisperFlag.ReplyResult, pUser.getName(), null, null, 0, 0, 0, 0, c.getPlayer().isAdmin(), true));
                } else {
                    c.getPlayer().SendPacket(CField.OnWhisper(WhisperFlag.ReplyResult, sTarget, null, null, 0, 0, 0, 0, c.getPlayer().isAdmin(), false));
                }
            } else if (dwFlag == WhisperFlag.FindRequest || dwFlag == WhisperFlag.LocationRequest) {
                if (pUser != null && pUser.getMap() != null) {
                    c.getPlayer().SendPacket(CField.OnWhisper(dwFlag == WhisperFlag.FindRequest ? WhisperFlag.FindResult : WhisperFlag.LocationResult, pUser.getName(), null, null, pUser.isAdmin() && !c.getPlayer().isAdmin() ? WhisperFlag.Admin : WhisperFlag.GameSvr, pUser.getMapId(), pUser.getPosition().x, pUser.getPosition().y, false, false));
                } else {
                    c.getPlayer().SendPacket(CField.OnWhisper(dwFlag == WhisperFlag.FindRequest ? WhisperFlag.FindResult : WhisperFlag.LocationResult, sTarget, null, null, WhisperFlag.Admin, 0, 0, 0, false, false));
                }
            }
        }
    }

    /**
     *
     * @author Five
     */
    public class WhisperFlag {

        public static final int // Whisper Flags
                Location = 0x1,
                Whisper = 0x2,
                Request = 0x4,
                Result = 0x8,
                Receive = 0x10,
                Blocked = 0x20,
                Location_F = 0x40,
                Manager = 0x80;

        public static final int // Location Results
                None = 0,
                GameSvr = 1,
                ShopSvr = 2,
                OtherChannel = 3,
                Admin = 4;

        public static final int // Whisper Modes
                FindRequest = WhisperFlag.Request | WhisperFlag.Location,
                ReplyRequest = WhisperFlag.Request | WhisperFlag.Whisper,
                LocationRequest = WhisperFlag.Request | WhisperFlag.Location_F,
                FindResult = WhisperFlag.Result | WhisperFlag.Location,
                LocationResult = WhisperFlag.Result | WhisperFlag.Location_F,
                ReplyResult = WhisperFlag.Result | WhisperFlag.Whisper,
                ReplyReceive = WhisperFlag.Receive | WhisperFlag.Whisper,
                BlockedResult = WhisperFlag.Blocked | WhisperFlag.Whisper,
                AdminResult = WhisperFlag.Manager | WhisperFlag.Result | WhisperFlag.Whisper,
                BlowWeather = WhisperFlag.Manager | WhisperFlag.Receive | WhisperFlag.Whisper,
                ModeNone = 0xFFFFFFFF;
    }
}
