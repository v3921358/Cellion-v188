package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;
import tools.packet.WvsContext;

/**
 * UserSitRequest
 * @author Mazen Massoud
 */
public final class OnUserSitRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        int nChairID = iPacket.DecodeShort();
        
        if (pPlayer.getMap().getSharedMapResources().noChair) {
            pPlayer.completeDispose();
            return;
        }
        
        if (nChairID == -1) {
            pPlayer.setChair(0);
            pPlayer.getMap().broadcastPacket(CField.cancelChair(pPlayer.getId(), -1));
            pPlayer.getMap().broadcastPacket(CField.showChair(pPlayer.getId(), 0));
        } else {
            pPlayer.setChair(nChairID);
            c.SendPacket(CField.showChair(pPlayer.getId(), nChairID));
        }
        
        pPlayer.completeDispose();
    }
}