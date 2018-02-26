/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;
import tools.packet.CLogin;

/**
 *
 * @author Mazen
 */
public final class MapLoginHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nMapLogin = (int) (Math.random() % 5);
        String sMapLogin = "MapLogin";
        if (nMapLogin > 0) {
            sMapLogin = sMapLogin + nMapLogin;
        }
        c.write(CLogin.OnMapLogin(sMapLogin));
    }
}
