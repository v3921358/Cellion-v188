package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Five
 */
public class SetStealSkillSlotHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nSlotSkillID = iPacket.DecodeInt();
        int nStealSkillID = iPacket.DecodeInt();
        boolean bSet = false;
        if (nStealSkillID == 0) {
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, 0);
        } else {
            bSet = true;
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, nStealSkillID);
        }
        c.SendPacket(WvsContext.OnResultSetStealSkill(1, bSet, nSlotSkillID, nStealSkillID));
    }
}
