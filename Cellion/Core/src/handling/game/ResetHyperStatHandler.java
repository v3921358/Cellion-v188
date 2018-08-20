package handling.game;

import client.ClientSocket;
import client.SpecialStats;
import client.SpecialStats.MapleHyperStats;
import client.Skill;
import client.SkillFactory;
import constants.skills.Global;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class ResetHyperStatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    private static final int RESET_HYPER_STAT_COST = 10_000_000;

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInt());

        if (chr.getMeso() >= RESET_HYPER_STAT_COST) {
            chr.gainMeso(-RESET_HYPER_STAT_COST, true, true);

            for (int hyperSkill : SpecialStats.ALL_HYPER_STATS) {
                Skill skill = SkillFactory.getSkill(hyperSkill);

                chr.changeSkillLevel(skill, (byte) 0, (byte) 0);
            }
        }
        c.SendPacket(WvsContext.enableActions());
    }
}
