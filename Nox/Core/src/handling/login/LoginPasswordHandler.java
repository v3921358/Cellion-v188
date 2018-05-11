package handling.login;

import java.util.Calendar;

import client.ClientSocket;
import constants.ServerConstants;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import tools.packet.CLogin;
import tools.packet.PacketHelper;
import net.ProcessPacket;
import server.api.ApiCallback;
import server.api.ApiFactory;
import server.api.data.UserInfo;

public final class LoginPasswordHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return !c.isLoggedIn();
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        byte unk = iPacket.DecodeByte();
        String pwd = iPacket.DecodeString();
        String login = iPacket.DecodeString().replace("NP12:auth06:5:0:", "");

        if (!ServerConstants.USE_API) {
            PasswordLogin(c, login, pwd);
        } else {
            ApiFactory.getFactory().getUserAuthToken(c, login, pwd, new ApiCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail() {
                    //session.getRemoteAddress().toString()
                    c.SendPacket(CLogin.getLoginFailed(6));
                    loginFailCount(c);
                    if (loginFailCount(c)) {
                        c.Close();
                    }
                }
            });
        }
    }

    private static boolean loginFailCount(final ClientSocket c) {
        c.loginAttempt++;
        return c.loginAttempt > ServerConstants.LOGIN_ATTEMPTS;
    }

    public static void checkLumiereAccount(ClientSocket c, UserInfo data) {
        if (data.getError() == "unauthorized" || data.getId() == 0) { // The last should never ever be sent by the API since the endpoint is called directly from the database, but just in case.
            c.SendPacket(CLogin.getLoginFailed(8));
            return;
        }

        // Check to see if they verified their Lumiere account or if they need reverification.
        if (!data.isVerified() || data.isRequireReverification()) {
            c.SendPacket(CLogin.getLoginFailed(16));
            return;
        }

        APILogin(c, data);
    }

    public static void APILogin(ClientSocket c, UserInfo data) {

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.AuthLogin(data.getName(), data.getId());

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                User.ban(c.GetIP().split(":")[0], "Enforcing account ban, account " + data.getName(), false, 4, false);
            }
        }
        if (loginok != 0) {
            if (loginok == 3) {
                c.SendPacket(WvsContext.broadcastMsg(1, c.showBanReason(data.getName(), true)));
                c.SendPacket(CLogin.getLoginFailed(1)); //Shows no message, used for unstuck the login button
            } else {
                c.SendPacket(CLogin.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.SendPacket(CLogin.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.Close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static void PasswordLogin(ClientSocket c, String name, String password) {

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.LoginPassword(name, password);

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                User.ban(c.GetIP().split(":")[0], "Enforcing account ban, account " + name, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (loginok == 3) {
                c.SendPacket(WvsContext.broadcastMsg(1, c.showBanReason(name, true)));
                c.SendPacket(CLogin.getLoginFailed(1)); //Shows no message, used for unstuck the login button
            } else {
                c.SendPacket(CLogin.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            if (!loginFailCount(c)) {
                c.clearInformation();
                c.SendPacket(CLogin.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            } else {
                c.Close();
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }
}
