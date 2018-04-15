package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class PvpPointMessage implements MessageInterface {

    private final int battleEXP;
    private final int battlePoints;

    public PvpPointMessage(int battleEXP, int battlePoints) {
        this.battleEXP = battleEXP;
        this.battlePoints = battlePoints;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.PvpPoint.getType());
        oPacket.EncodeInt(battleEXP);
        oPacket.EncodeInt(battlePoints);
    }

}
