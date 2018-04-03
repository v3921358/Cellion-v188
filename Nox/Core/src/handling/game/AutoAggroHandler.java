package handling.game;

import client.MapleClient;
import server.life.MapleMonster;
import server.maps.objects.User;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class AutoAggroHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if ((chr == null) || (chr.getMap() == null) || (chr.isHidden())) {
            return;
        }
        MapleMonster monster = chr.getMap().getMonsterByOid(iPacket.DecodeInteger());

        if (monster != null && chr.getTruePosition().distanceSq(monster.getTruePosition()) < 200000.0D && monster.getLinkCID() <= 0) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

}
