package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeG (lack of better name for the time being, but these are all the movements that appear seventh
 * in MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeG extends AbstractLifeMovement {

    /**
     * Initializes the values of command, position, wobble, moveAction, tElapse, randCnt, and actualRandCnt.
     *
     * @param command the command number
     * @param position the x and y position
     * @param wobble the x and y wobble position
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the tElapse (also known as duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeG() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.Encode(getCommand());
        oPacket.EncodeShort(getPosition().x);
        oPacket.EncodeShort(getPosition().y);
        oPacket.EncodeShort(getWobble().x);
        oPacket.EncodeShort(getWobble().y);
        oPacket.Encode(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.Encode(getbForcedStop());
    }
}
