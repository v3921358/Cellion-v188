package handling.game;

import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import java.util.List;
import net.InPacket;
import tools.packet.CField;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class PhantomViewSkillHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int victim = iPacket.DecodeInteger();
        int jobid = c.getChannelServer().getPlayerStorage().getCharacterById(victim).getJob();
        List<Integer> list = SkillFactory.getSkillsByJob(jobid);

        if (!c.getChannelServer().getPlayerStorage().getCharacterById(victim).getSkills().isEmpty() && GameConstants.isAdventurer(jobid)) {
            c.write(CField.viewSkills(c.getChannelServer().getPlayerStorage().getCharacterById(victim)));
        } else {
            c.getPlayer().dropMessage(6, "You cannot take skills off non-adventurer's");
        }
    }
}
