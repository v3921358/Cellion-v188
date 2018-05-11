/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author song_lin
 */
public class ViewServerListHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (iPacket.DecodeByte() == 0) {
            WorldInfoRequestHandler.sendServerList(iPacket, c);
        }
    }
}
