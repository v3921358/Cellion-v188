package handling.game;

import client.Client;
import constants.GameConstants;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author Five
 */
public class SetStealSkillSlotHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int nSlotSkillID = iPacket.DecodeInt();
        int nStealSkillID = iPacket.DecodeInt();
        boolean bSet = false;
        if (nStealSkillID == 0) {
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, 0);
        } else {
            bSet = true;
            c.getPlayer().mStealSkillInfo.put(nSlotSkillID, nStealSkillID);
        }
        c.SendPacket(CWvsContext.OnResultSetStealSkill(1, bSet, nSlotSkillID, nStealSkillID));
    }
}
