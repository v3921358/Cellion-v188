package handling.login;

import client.ClientSocket;
import constants.WorldConstants;
import service.ChannelServer;
import service.LoginServer;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class PicCheck implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    private static boolean loginFailCount(final ClientSocket c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        //boolean view = c.getChannel() == 1;
        final String password = iPacket.DecodeString();
        final int charId = iPacket.DecodeInt();

        c.setWorld(iPacket.DecodeByte());
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldConstants.WorldOption.isExists(c.getWorld())) {
            c.Close();
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
            c.updateLoginState(ClientSocket.MapleClientLoginState.Login_ServerTransition, s);

            c.SendPacket(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        } else {
            c.SendPacket(CLogin.getLoginFailed(4)); // Password fail instead, works well enough.
            //c.write(CLogin.secondPwError((byte) 0x14)); // Error 38.
        }
    }

}
