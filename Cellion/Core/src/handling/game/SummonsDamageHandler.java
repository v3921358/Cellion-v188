package handling.game;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import java.util.Iterator;
import net.InPacket;
import server.maps.objects.User;
import server.maps.objects.Summon;
import tools.packet.CField;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SummonsDamageHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.Skip(4);
        final int unkByte = iPacket.DecodeByte();
        final int damage = iPacket.DecodeInt();
        final int monsterIdFrom = iPacket.DecodeInt();
        //       iPacket.decodeByte(); // stance

        User chr = c.getPlayer();

        final Iterator<Summon> iter = chr.getSummonsReadLock().iterator();
        Summon summon;
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
                        chr.getMap().broadcastPacket(CField.SummonPacket.removeSummon(summon, true));
                        chr.getMap().removeMapObject(summon);
                        summon.getOwner().removeVisibleMapObject(summon);
                        if (summon.getOwner().getSummons().get(summon.getSkill()) != null) {
                            summon.getOwner().getSummons().remove(summon.getSkill());
                        }
                    }
                    chr.getMap().broadcastPacket(chr, CField.SummonPacket.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition());
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
