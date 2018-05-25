package handling.game;

import client.ClientSocket;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 * AutoAggroHandler
 * @author
 */
public class AutoAggroHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        if ((pPlayer == null) || (pPlayer.getMap() == null) || (pPlayer.isHidden())) return;
        
        Mob pMob = pPlayer.getMap().getMonsterByOid(iPacket.DecodeInt());
        if (pMob != null && pPlayer.getTruePosition().distanceSq(pMob.getTruePosition()) < 200000.0D && pMob.getLinkCID() <= 0) {
            if (pMob.getController() != null) {
                if (pPlayer.getMap().getCharacterById(pMob.getController().getId()) == null) {
                    pMob.switchController(pPlayer, true);
                } else {
                    pMob.switchController(pMob.getController(), true);
                }
            } else {
                pMob.switchController(pPlayer, true);
            }
        }
    }
}
