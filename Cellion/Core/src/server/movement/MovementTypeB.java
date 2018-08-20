package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeB (lack of better name for the time being, but these are all the movements that appear second in
 * MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeB extends AbstractLifeMovement {

    /**
     * Initializes the values of command, position, wobble, foothold, moveAction, tElapse, randCnt, and actualRandCnt.
     *
     * @param command the command number
     * @param position the x and y position
     * @param wobble the x and y wobble position
     * @param foothold the foothold
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the time elapsed (or duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeB() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.EncodeByte(getCommand());
        oPacket.EncodeShort(getPosition().x);
        oPacket.EncodeShort(getPosition().y);
        oPacket.EncodeShort(getWobble().x);
        oPacket.EncodeShort(getWobble().y);
        oPacket.EncodeShort(getFoothold());
        oPacket.EncodeByte(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.EncodeByte(getbForcedStop());

    }
}
