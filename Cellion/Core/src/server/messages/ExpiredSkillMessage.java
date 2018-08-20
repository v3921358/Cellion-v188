package server.messages;

import enums.MessageOpcodesType;
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
        oPacket.EncodeByte(MessageOpcodesType.ExpiredSkill.getType());
        oPacket.EncodeByte(skills.size());
        for (Integer skill : skills) {
            oPacket.EncodeInt(skill);
        }
    }

}
