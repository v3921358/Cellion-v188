/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;
import server.api.data.UserInfo;
import tools.packet.CLogin;

/**
 *
 * @author Song Lin
 */
public class OnSelectWorld implements ProcessPacket<MapleClient> {

    public static void checkLumiereAccount(MapleClient c, UserInfo data) {
        if (data.getError() == "unauthorized" || data.getId() == 0) { // The last should never ever be sent by the API since the endpoint is called directly from the database, but just in case.
            c.SendPacket(CLogin.getLoginFailed(8));
            return;
        }

        // Check to see if they verified their Lumiere account or if they need reverification.
        if (!data.isVerified() || data.isRequireReverification()) {
            c.SendPacket(CLogin.getLoginFailed(16));
            return;
        }

        //login(c, data);
    }

    @Override
    public boolean ValidateState(MapleClient ClientSocket) {
        return true;
    }

    @Override
    public void Process(MapleClient pClient, InPacket iPacket) {
        if (!iPacket.DecodeBool()) {
            return;
        }

        pClient.sAccountToken = iPacket.DecodeString();
        iPacket.Skip(21);

        pClient.setWorld(iPacket.Decode());
        pClient.setChannel(iPacket.Decode());

    }
}
