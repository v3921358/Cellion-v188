package handling.login;

import java.util.Calendar;

import client.MapleClient;
import constants.ServerConstants;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CWvsContext;
import tools.packet.CLogin;
import tools.packet.PacketHelper;
import netty.ProcessPacket;

public final class LoginPasswordHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        byte unk = iPacket.DecodeByte();
        String pwd = iPacket.DecodeString();
        String login = iPacket.DecodeString();

        login = login.replace("NP12:auth06:5:0:", "");

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.login(login, pwd, ipBan || macBan);

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.GetIP().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                if (loginok == 3) {
                    c.write(CWvsContext.broadcastMsg(1, c.showBanReason(login, true)));
                    c.write(CLogin.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                } else {
                    c.write(CLogin.getLoginFailed(loginok));
                }
            } else {
                c.close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.write(CLogin.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > ServerConstants.LOGIN_ATTEMPTS;
    }
}
