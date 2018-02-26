package handling.game;

import client.MapleClient;
import constants.GameConstants;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomSkillSwipeHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || !GameConstants.isPhantom(c.getPlayer().getJob())) {
            c.write(CWvsContext.enableActions());
            return;
        }
        final int skill = iPacket.DecodeInteger();
        final int cid = iPacket.DecodeInteger();

        //then a byte, 0 = learning, 1 = removing, but it doesnt matter since we can just use cid
        if (cid <= 0) {
            c.getPlayer().removeStolenSkill(skill);
        } else {
            final MapleCharacter other = c.getPlayer().getMap().getCharacterById(cid);
            if (other != null && other.getId() != c.getPlayer().getId() && other.getTotalSkillLevel(skill) > 0) {
                c.getPlayer().addStolenSkill(skill, other.getTotalSkillLevel(skill));
            }
        }
    }

}
