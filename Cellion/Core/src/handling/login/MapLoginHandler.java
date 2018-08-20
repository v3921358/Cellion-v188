/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.CLogin;

/**
 *
 * @author Mazen Massoud
 */
public final class MapLoginHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nMapLogin = (int) (Math.random() % 5);
        String sMapLogin = "MapLogin";
        if (nMapLogin > 0) {
            sMapLogin = sMapLogin + nMapLogin;
        }
        c.SendPacket(CLogin.OnMapLogin(sMapLogin));
    }
}
