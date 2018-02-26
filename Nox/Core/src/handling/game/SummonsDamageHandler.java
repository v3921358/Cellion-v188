package handling.game;

import client.CharacterTemporaryStat;
import client.MapleClient;
import java.util.Iterator;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleSummon;
import tools.packet.CField;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SummonsDamageHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        iPacket.Skip(4);
        final int unkByte = iPacket.DecodeByte();
        final int damage = iPacket.DecodeInteger();
        final int monsterIdFrom = iPacket.DecodeInteger();
        //       iPacket.decodeByte(); // stance

        MapleCharacter chr = c.getPlayer();

        final Iterator<MapleSummon> iter = chr.getSummonsReadLock().iterator();
        MapleSummon summon;
        boolean remove = false;
        try {
            while (iter.hasNext()) {
                summon = iter.next();
                if (summon.isPuppet() && summon.getOwnerId() == chr.getId() && damage > 0) { //We can only have one puppet(AFAIK O.O) so this check is safe.
                    summon.addHP((short) -damage);
                    if (summon.getHP() <= 0) {
                        remove = true;
                    }
                    if (summon.getSkill() == 14000027) {
                        chr.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                        chr.getMap().removeMapObject(summon);
                        summon.getOwner().removeVisibleMapObject(summon);
                        if (summon.getOwner().getSummons().get(summon.getSkill()) != null) {
                            summon.getOwner().getSummons().remove(summon.getSkill());
                        }
                    }
                    chr.getMap().broadcastMessage(chr, CField.SummonPacket.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition());
                    break;
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (remove) {
            chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.PUPPET);
        }
    }

}
