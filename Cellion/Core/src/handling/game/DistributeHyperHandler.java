package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DistributeHyperHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        int skillid = iPacket.DecodeInt();
        final Skill skill = SkillFactory.getSkill(skillid);
        final int remainingSp = chr.getRemainingHSp(skill.getHyper() - 1);

        final int maxlevel = 1;
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if (maxlevel <= 0) {
                c.SendPacket(WvsContext.enableActions());
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }

        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                c.SendPacket(WvsContext.enableActions());
                chr.dropMessage(1, "This skill has been blocked and may not be added.");
                return;
            }
        }

        if ((/*remainingSp >= 1 && */curLevel == 0) && skill.canBeLearnedBy(chr.getJob())) {
            chr.setRemainingHSp(skill.getHyper() - 1, remainingSp - 1);
            chr.updateHyperSPAmount();

            chr.changeSingleSkillLevel(skill, (byte) 1, (byte) 1, SkillFactory.getDefaultSExpiry(skill), true);
        } else {
            c.SendPacket(WvsContext.enableActions());
        }
    }

}
