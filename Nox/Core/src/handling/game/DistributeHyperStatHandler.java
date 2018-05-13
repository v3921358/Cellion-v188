package handling.game;

import client.ClientSocket;
import client.MapleSpecialStats.MapleHyperStats;
import client.Skill;
import client.SkillFactory;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class DistributeHyperStatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInt());
        int skillid = iPacket.DecodeInt(); // 80000400

        Skill skill = SkillFactory.getSkill(skillid);

        if (skill != null && skill.isHyperStat()) {
            int currentSkillLevel = chr.getSkillLevel(skill);

            int remainingHyperStat = MapleHyperStats.getRemainingHyperStat(chr);
            int requiredHyperStat = MapleHyperStats.getRequiredHyperStatSP(currentSkillLevel);

            byte nextSkillLevel = (byte) (currentSkillLevel + 1);

            if (remainingHyperStat > 0 && remainingHyperStat >= requiredHyperStat && nextSkillLevel <= skill.getMaxLevel()) {
                chr.changeSkillLevel(skill, nextSkillLevel, (byte) skill.getMaxLevel());
                chr.changed_skills = true;
                chr.getStat().OnCalculateLocalStats(chr);
                return;
            }

        }
        c.SendPacket(WvsContext.enableActions());
    }
}
