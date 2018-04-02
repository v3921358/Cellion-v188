package handling.game;

import client.MapleClient;
import client.MapleSpecialStats.MapleHyperStats;
import client.Skill;
import client.SkillFactory;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class DistributeHyperStatHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInteger());
        int skillid = iPacket.DecodeInteger(); // 80000400

        Skill skill = SkillFactory.getSkill(skillid);

        if (skill != null && skill.isHyperStat()) {
            int currentSkillLevel = chr.getSkillLevel(skill);

            int remainingHyperStat = MapleHyperStats.getRemainingHyperStat(chr);
            int requiredHyperStat = MapleHyperStats.getRequiredHyperStatSP(currentSkillLevel);

            byte nextSkillLevel = (byte) (currentSkillLevel + 1);

            if (remainingHyperStat > 0 && remainingHyperStat >= requiredHyperStat && nextSkillLevel <= skill.getMaxLevel()) {
                chr.changeSkillLevel(skill, nextSkillLevel, (byte) skill.getMaxLevel());
                chr.changed_skills = true;
                chr.getStat().recalcLocalStats(chr);
                return;
            }

        }
        c.write(CWvsContext.enableActions());
    }
}
