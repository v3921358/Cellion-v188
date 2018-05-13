package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

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
            return;
        }
        
        if (nChairID == -1) {
            pPlayer.cancelFishingTask();
            pPlayer.setChair(0);
            c.SendPacket(CField.cancelChair(pPlayer.getId(), -1));
            pPlayer.getMap().broadcastPacket(pPlayer, CField.OnShowChair(pPlayer.getId(), nChairID), false);
            pPlayer.completeDispose();
        } else {
            pPlayer.setChair(nChairID);
            c.SendPacket(CField.cancelChair(pPlayer.getId(), nChairID));
        }
    }
}
