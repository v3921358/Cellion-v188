package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeA (lack of better name for the time being, but these are all the movements that appear first in
 * MovementParse). This class sets the appropriate parameters, and serializes the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeA extends AbstractLifeMovement {

    /**
     * Initializes the values of command, position, wobble, foothold, footholdFallStart, offset, moveAction, tElapse, bForceStop.
     *
     * @param command the command number
     * @param position the x and y position
     * @param wobble the x and y wobble position
     * @param foothold the foothold
     * @param footholdFallStart the footholdFallStart
     * @param offset the x and y offset position
     * @param moveAction the moveAction (also known as stance)
     * @param tElapse the time elapsed (or duration)
     * @param bForcedStop the bForcedStop
     */
    public MovementTypeA() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.Encode(getCommand());
        oPacket.EncodeShort(getPosition().x);
        oPacket.EncodeShort(getPosition().y);
        oPacket.EncodeShort(getWobble().x);
        oPacket.EncodeShort(getWobble().y);
        oPacket.EncodeShort(getFoothold());
        if (getCommand() == 15 || getCommand() == 16) {
            oPacket.EncodeShort(getFootholdFallStart());
        }
        oPacket.EncodeShort(getOffset().x);
        oPacket.EncodeShort(getOffset().y);
        oPacket.Encode(getStance());
        oPacket.EncodeShort(getDuration());
        oPacket.Encode(getbForcedStop());
    }
}
