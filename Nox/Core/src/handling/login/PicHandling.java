package handling.login;

import client.MapleClient;
import constants.WorldConstants;
import service.ChannelServer;
import service.LoginServer;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CLogin;

/**
 *
 * @author Novak
 */
public class PicHandling {

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }

    public static void CharLogin(InPacket iPacket, MapleClient c, boolean view, boolean haspic) {
        iPacket.DecodeByte(); // 1?
        iPacket.DecodeByte(); // 1?
        final int charId = iPacket.DecodeInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(iPacket.DecodeInt());
        }
        final String currentpw = c.getSecondPassword();
        if (!c.isLoggedIn() || loginFailCount(c) || (currentpw != null && (!currentpw.equals("")
                || haspic)) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null
                || !WorldConstants.WorldOption.isExists(c.getWorld())) {
            c.Close();
            return;
        }
        c.updateMacs(iPacket.DecodeString());
        iPacket.DecodeString();
        if (iPacket.GetRemainder() != 0) {
            final String setpassword = iPacket.DecodeString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.SendPacket(CLogin.secondPwError((byte) 0x14));
                return;
            }
        } else if (haspic) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel(), 0);

        c.updateLoginState(MapleClient.MapleClientLoginState.LOGIN_SERVER_TRANSITION, s);
        c.SendPacket(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }
}
