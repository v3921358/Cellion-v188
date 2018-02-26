package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeD (lack of better name for the time being, but these are all the movements that appear fourth in
 * MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeD extends AbstractLifeMovement {

    /**
     * Initializes the values of command, moveAction,tElapse, randCnt, and actualRandCnt.
     *
     * @param command the command number
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the tElapse (also known as duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeD() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.Encode(getCommand());
        oPacket.Encode(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.Encode(getbForcedStop());
    }
}
