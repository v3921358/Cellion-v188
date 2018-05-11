package handling.game;

import client.Client;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import server.maps.objects.User;
import tools.LogHelper;
import net.InPacket;
import net.ProcessPacket;

/**
 * When the player uses a skill such as 'Escape' for Xenon, 'Temple Recall' for Zero or 'Return' for Kinesis E4 00 EE E5 F5 05 01 00
 *
 * No enableaction needed for this packet request.
 *
 * @author
 */
public class UserEffectLocalHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        int skillId = iPacket.DecodeInt();
        int skillLevel_client = iPacket.DecodeShort();

        Skill skill = SkillFactory.getSkill(skillId);

        // boolean isReturnHQ = GameConstants.isReturnHQSkill(skillId);
        if (skill != null/* || isReturnHQ*/) {

            int skillLevel = chr.getSkillLevel(skillId);
            if (skillLevel > 0 && skillLevel_client == skillLevel) { // Check if the player have the skill
                // TODO: There might be some animation here to display to third party.
                return;
            }
        }
        //System.out.println(skillId + " " + isReturnHQ);
        LogHelper.PACKET_EDIT_HACK.get().info(
                String.format("[UserEffectLocalHandler] %s [ChrID: %d; AccId %d] has tried to use an invalid skill effect. Id = %d", chr.getName(), chr.getId(), c.getAccID(), skillId)
        );

        if (GameConstants.isMechanic(chr.getJob())) { //incorrect disconnect
            return;
        } else {
            c.Close(); // hacker
        }
    }

}
