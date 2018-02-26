package handling.game;

import client.MapleClient;
import server.life.MapleMonster;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class MonsterBombHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        final int oid = iPacket.DecodeInteger();
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);

        if ((monster == null) || (!chr.isAlive()) || (chr.isHidden()) || (monster.getLinkCID() > 0)) {
            return;
        }
        short selfd = monster.getStats().getSelfD();
        if (selfd != -1) {
            chr.getMap().killMonster(monster, chr, false, false, selfd);
        }
    }

}
