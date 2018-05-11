package handling.game;

import client.Client;
import static handling.world.MobHandler.checkShammos;
import server.Randomizer;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class FriendlyDamageHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        Mob mobfrom = map.getMonsterByOid(iPacket.DecodeInt());
        iPacket.Skip(4);
        Mob mobto = map.getMonsterByOid(iPacket.DecodeInt());

        if ((mobfrom != null) && (mobto != null) && (mobto.getStats().isFriendly())) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel()) / 2;
            mobto.damage(chr, damage, true);
            checkShammos(chr, mobto, map);
        }
    }

}
