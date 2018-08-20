package server.movement;

import java.awt.Point;
import net.OutPacket;

public interface LifeMovementFragment {

    /**
     * Serializes the LifeMovementFragment object (ie. packet writing).
     *
     * @param oPacket the OutPacket object
     */
    public void serialize(OutPacket oPacket);

    /**
     * Gets the position of the life object.
     *
     * @return the position of the life object
     */
    public Point getPosition();

    /**
     * @return the foothold of the life object.
     */
    public short getFoothold();
}
