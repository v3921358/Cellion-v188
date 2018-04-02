package handling.game;

import client.MapleClient;
import constants.GameConstants;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Five
 */
public class SetStealSkillSlotHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nSlotSkillID = iPacket.DecodeInteger();
        int nStealSkillID = iPacket.DecodeInteger();
        boolean bSet = false;
        if (nStealSkillID == 0) {
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, 0);
        } else {
            bSet = true;
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, nStealSkillID);
        }
        c.write(CWvsContext.OnResultSetStealSkill(1, bSet, nSlotSkillID, nStealSkillID));
    }
}
