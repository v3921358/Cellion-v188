/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.CWvsContext;

/**
 *
 * @author Mazen
 */
public class FriendRequestAccIdHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.SendPacket(CWvsContext.OnLoadAccountIDOfCharacterFriendResult(c.getPlayer().getBuddylist()));
    }
}
