/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.auction;

import client.ClientSocket;
import handling.world.CharacterTransfer;
import handling.world.World;
import net.InPacket;
import server.maps.objects.User;
import service.AuctionServer;
import service.ChannelServer;
import service.LoginServer;
import tools.packet.AuctionPacket;
import tools.packet.CField;

/**
 *
 * @author William
 */
public class AuctionOperation {

    public static void EnterAuction(final CharacterTransfer transfer, final ClientSocket c) {
        if (transfer == null) {
            c.Close();
            return;
        }
        User chr = User.reconstructCharacter(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.Close();
            return;
        }

        final ClientSocket.MapleClientLoginState state = c.getLoginState();
        boolean allowLogin = false;

        switch (state) {
            case Login_ServerTransition:
            case ChangeChannel:
                if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    allowLogin = true;
                }
                break;
        }

        if (!allowLogin) {
            c.setPlayer(null);
            c.Close();
            return;
        }
        c.updateLoginState(ClientSocket.MapleClientLoginState.Login_LoggedIn, c.getSessionIPAddress());

        AuctionServer.getPlayerStorage().registerPlayer(chr);
        
        c.SendPacket(AuctionPacket.enterAuction(c));
    }

    public static void LeaveAuction(final InPacket iPacket, final ClientSocket c, final User chr) {
        AuctionServer.getPlayerStorage().deregisterPlayer(chr);

        c.updateLoginState(ClientSocket.MapleClientLoginState.Login_ServerTransition, c.getSessionIPAddress());

        try {
            World.changeChannelData(new CharacterTransfer(chr), chr.getId(), c.getChannel());
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            c.SendPacket(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
            c.disconnect(false, true);
        }
        
    }
}
