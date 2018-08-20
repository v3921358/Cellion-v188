package handling.game;

import client.ClientSocket;
import client.MonsterStatus;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class MobBombHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        Mob mobfrom = map.getMonsterByOid(iPacket.DecodeInt());
        iPacket.Skip(4);
        iPacket.DecodeInt();

        if ((mobfrom != null) && (mobfrom.getBuff(MonsterStatus.MONSTER_BOMB) != null));
    }

}
