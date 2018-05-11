/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.CWvsContext;

/**
 *
 * @author Mazen Massoud
 */
public class FriendRequestAccIdHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        c.SendPacket(CWvsContext.OnLoadAccountIDOfCharacterFriendResult(c.getPlayer().getBuddylist()));
    }
}
