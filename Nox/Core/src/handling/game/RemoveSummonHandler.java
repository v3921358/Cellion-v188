package handling.game;

import client.CharacterTemporaryStat;
import client.Client;
import net.InPacket;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.Summon;
import tools.packet.CField;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class RemoveSummonHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final MapleMapObject obj = c.getPlayer().getMap().getMapObject(iPacket.DecodeInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof Summon)) {
            return;
        }
        final Summon summon = (Summon) obj;
        if (summon.getOwnerId() != c.getPlayer().getId() || summon.getSkillLevel() <= 0) {
            c.getPlayer().dropMessage(5, "Error.");
            return;
        }
        if (summon.getSkill() == 35111002 || summon.getSkill() == 35121010) { //rock n shock, amp
            return;
        }
        c.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
        c.getPlayer().getMap().removeMapObject(summon);
        c.getPlayer().removeVisibleMapObject(summon);
        c.getPlayer().removeSummon(summon);
        if (summon.getSkill() != 35121011) {
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.SUMMON);
            //TODO: Multi Summoning, must do something about hack buffstat
        }
    }

}
