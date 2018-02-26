package handling.login;

import client.MapleClient;
import constants.WorldConstants;
import service.ChannelServer;
import service.LoginServer;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CLogin;
import netty.ProcessPacket;

public final class PicCheck implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        //boolean view = c.getChannel() == 1;
        final String password = iPacket.DecodeString();
        final int charId = iPacket.DecodeInteger();

        c.setWorld(iPacket.DecodeByte());
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldConstants.WorldOption.isExists(c.getWorld())) {
            c.close();
            return;
        }
        c.updateMacs(iPacket.DecodeString());
        String machineID = iPacket.DecodeString(); //Session ID

        if (c.checkSecondPassword(password, false) && password.length() >= 6 && password.length() <= 16 || c.isGm()) {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel(), 0);
            c.updateLoginState(MapleClient.MapleClientLoginState.LOGIN_SERVER_TRANSITION, s);

            c.write(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.write(CLogin.getLoginFailed(4)); // Password fail instead, works well enough.
            //c.write(CLogin.secondPwError((byte) 0x14)); // Error 38.
        }
    }

}
