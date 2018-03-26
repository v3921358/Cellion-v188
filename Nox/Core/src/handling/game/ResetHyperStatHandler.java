package handling.game;

import client.MapleClient;
import client.MapleSpecialStats;
import client.MapleSpecialStats.MapleHyperStats;
import client.Skill;
import client.SkillFactory;
import constants.skills.Global;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class ResetHyperStatHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static final int RESET_HYPER_STAT_COST = 10_000_000;

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInteger());

        if (chr.getMeso() >= RESET_HYPER_STAT_COST) {
            chr.gainMeso(-RESET_HYPER_STAT_COST, true, true);

            for (int hyperSkill : MapleSpecialStats.ALL_HYPER_STATS) {
                Skill skill = SkillFactory.getSkill(hyperSkill);

                chr.changeSkillLevel(skill, (byte) 0, (byte) 0);
            }
        }
        c.write(CWvsContext.enableActions());
    }
}
