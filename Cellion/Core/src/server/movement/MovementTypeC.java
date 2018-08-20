package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeC (lack of better name for the time being, but these are all the movements that appear third in
 * MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeC extends AbstractLifeMovement {

    /**
     * Initializes the value of command, wobble, footholdFallStart, moveAction, tElapse, randCnt, and actualRandCnt.
     *
     * @param command the command number
     * @param wobble the x and y wobble positions
     * @param footholdFallStart the footholdFallStart
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the tElapse (also known as duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeC() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.EncodeByte(getCommand());
        oPacket.EncodeShort(getWobble().x);
        oPacket.EncodeShort(getWobble().y);

        if (getCommand() == 20 || getCommand() == 21) {
            oPacket.EncodeShort(getFootholdFallStart());
        }
        oPacket.EncodeByte(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.EncodeByte(getbForcedStop());
    }
}
