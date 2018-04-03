package handling.game;

import client.MapleClient;
import client.MonsterStatus;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class MobBombHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(iPacket.DecodeInteger());
        iPacket.Skip(4);
        iPacket.DecodeInteger();

        if ((mobfrom != null) && (mobfrom.getBuff(MonsterStatus.MONSTER_BOMB) != null));
    }

}
