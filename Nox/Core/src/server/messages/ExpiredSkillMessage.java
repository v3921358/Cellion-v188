package server.messages;

import java.util.List;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class ExpiredSkillMessage implements MessageInterface {

    private final List<Integer> skills;

    public ExpiredSkillMessage(List<Integer> skills) {
        this.skills = skills;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.ExpiredSkill.getType());
        oPacket.Encode(skills.size());
        for (Integer skill : skills) {
            oPacket.EncodeInteger(skill);
        }
    }

}
