package server.movement;

import net.OutPacket;

/**
 * This is a class to represent MovementTypeH (lack of better name for the time being, but these are all the movements that appear eighth in
 * MovementParse). This seems to be related to chair or some form of recovery. This class sets the appropriate parameters, and serializes
 * the information (ie. packet writing).
 *
 * @author Brian
 *
 */
public class MovementTypeH extends AbstractLifeMovement {

    /**
     *
     * @param command the command number
     * @param bStat the bStat (I assume this is something to do with stats. Maybe what stat it recovers?)
     */
    public MovementTypeH() {
        super();
    }

    @Override
    public void serialize(OutPacket oPacket) {
        oPacket.Encode(getCommand());
        oPacket.Encode(getbStat());
    }
}
