/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import netty.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;

/**
 *
 * @author Five
 */
public class GroupMessageHandler implements ProcessPacket<MapleClient> {

    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nType = iPacket.DecodeByte();
        int nCount = iPacket.DecodeByte();
        List<Integer> aCharacterID = new ArrayList<>(nCount);
        for(int i = 0; i < nCount; i++) {
            aCharacterID.add(iPacket.DecodeInteger());
        }
        String sMessage = iPacket.DecodeString();
        
        // nTypes: 0 = Buddy; 1 = Party; 2 = Guild; 3 = Alliance;
        switch (nType) { // TODO: I dont know how u check for parties/buddies/guilds/alliances but make sure they exist and have at least 1 user online else return
            case 0: // Buddy Check
                boolean bAvailable = false;
                List<Integer> aBuddyListIDs = c.getPlayer().getBuddylist().getBuddyIds();
                for (int i = 0; i < aBuddyListIDs.size(); i++) {
                    User pBuddy = c.getChannelServer().getPlayerStorage().getCharacterById(aBuddyListIDs.get(i));
                    if (pBuddy != null) {
                        bAvailable = true;
                    }
                }
                if (!bAvailable) {
                    return;
                }
                break;
            case 1: // Party Check
                if (c.getPlayer().getParty() == null) {
                    return;
                }
                break;
            case 2: // Guild Check
                if (c.getPlayer().getGuild() == null) {
                    return;
                }
                break;
            case 3: // Alliance Check
                break;
        }
        
        for(int dwCharacterID : aCharacterID) {
            User pUser = c.getChannelServer().getPlayerStorage().getCharacterById(dwCharacterID);
            if(pUser != null) {
                pUser.SendPacket(CField.OnGroupMessage(nType, c.getPlayer().getName(), sMessage));
            }
        }
        aCharacterID.clear();
    }
}
