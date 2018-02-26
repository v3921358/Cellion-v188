package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeF (lack of better name for the time being, but these are all the movements that appear sixth in
 * MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeF extends AbstractLifeMovement {

    /**
     * Initializes the values of command, wobble, footholdFallStart, moveAction, tElapse, randCnt, and actualRandCnt.
     *
     * @param command the command number
     * @param wobble the x and y position of the wobble
     * @param footholdFallStart the footholdFallStart
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the tElapse (also known as duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeF() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.Encode(getCommand());
        oPacket.EncodeShort(getPosition().x);
        oPacket.EncodeShort(getPosition().y);
        oPacket.EncodeShort(getFootholdFallStart());
        oPacket.Encode(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.Encode(getbForcedStop());
    }
}
