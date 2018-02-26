package handling.game;

import client.MapleClient;
import static handling.world.MobHandler.checkShammos;
import server.life.MapleMonster;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class HypnotizeDmgHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        MapleMonster mob_from = chr.getMap().getMonsterByOid(iPacket.DecodeInteger());
        iPacket.Skip(4);
        int to = iPacket.DecodeInteger();
        iPacket.Skip(1);
        int damage = iPacket.DecodeInteger();

        MapleMonster mob_to = chr.getMap().getMonsterByOid(to);

        if ((mob_from != null) && (mob_to != null) && (mob_to.getStats().isFriendly())) {
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            checkShammos(chr, mob_to, chr.getMap());
        }
    }

}
