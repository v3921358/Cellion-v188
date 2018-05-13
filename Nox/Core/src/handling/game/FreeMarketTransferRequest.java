/*
 * Cellion Development
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;

/**
 * UserTransferFreeMarketRequest
 * @author Mazen Massoud
 */
public class FreeMarketTransferRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        int nUnk = iPacket.DecodeByte();
        int nFlag = iPacket.DecodeByte();
        
        pPlayer.changeMap(910000128 + nFlag, 10);
        pPlayer.completeDispose();
    }
}
