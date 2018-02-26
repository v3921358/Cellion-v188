package handling.game;

import client.MapleClient;
import constants.GameConstants;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomChooseSkillHandler implements ProcessPacket<MapleClient> {

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
        final int base = iPacket.DecodeInteger();
        final int skill = iPacket.DecodeInteger();
        if (skill <= 0) {
            c.getPlayer().unchooseStolenSkill(base);
        } else {
            c.getPlayer().chooseStolenSkill(skill);
        }
    }

}
