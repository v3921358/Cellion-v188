/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.WvsContext;

/**
 *
 * @author Five
 */
public final class ReloginCookieHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        String sUsername = iPacket.DecodeString();
        if(!c.getAccountName().equals(sUsername)) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.SendPacket(WvsContext.OnIssueReloginCookie(sUsername));
    }
}
