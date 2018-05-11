package handling.game;

import client.Client;
import client.Skill;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CWvsContext;

/**
 *
 * @author Five
 */
public class StealSkillListHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final int NoTarget = 0, NotAdventurer = 1, NoSkill = 2, Unknown = 3, Success = 4;
        int dwCharacterID = iPacket.DecodeInt();
        User pUser = c.getChannelServer().getPlayerStorage().getCharacterById(dwCharacterID);
        int nPhantomStealResult = Success;
        int nJob = 0;
        List<Integer> aSkill = new ArrayList<>();
        if (pUser != null) {
            nJob = pUser.getJob();
            if (nJob < 0 || nJob >= 1000) {
                nPhantomStealResult = NotAdventurer;
            }

            // TODO: You should write the true is_stealable_skill function
            for (Skill sk : pUser.getSkills().keySet()) {
                if ((sk.canBeLearnedBy(pUser.getJob())) && (GameConstants.canSteal(sk)) && (!aSkill.contains(sk.getId()))) {
                    aSkill.add(sk.getId());
                }
            }
            if (aSkill.isEmpty()) {
                nPhantomStealResult = NoSkill;
            }
        } else {
            nPhantomStealResult = NoTarget;
        }
        c.SendPacket(CWvsContext.OnResultStealSkillList(1, dwCharacterID, nPhantomStealResult, nJob, aSkill));
    }
}
