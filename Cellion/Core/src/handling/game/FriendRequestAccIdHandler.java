/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.WvsContext;

/**
 *
 * @author Mazen Massoud
 */
public class FriendRequestAccIdHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.SendPacket(WvsContext.OnLoadAccountIDOfCharacterFriendResult(c.getPlayer().getBuddylist()));
    }
}
