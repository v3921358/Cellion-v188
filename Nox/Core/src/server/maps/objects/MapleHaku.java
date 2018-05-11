package server.maps.objects;

import java.awt.Point;
import java.util.List;

import client.Client;
import client.MapleJob;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMapObjectType;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.MovementTypeA;
import tools.packet.CField;

/**
 *
 * @author Itzik
 */
public class MapleHaku extends AnimatedMapleMapObject {

    private final int owner;
    private final int jobid;
    private int equipId = 0;
    private final int fh;
    private boolean stats;
    private Point pos = new Point(0, 0);

    public MapleHaku(User owner) {
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        this.fh = owner.getFh();
        this.stats = false;
        if (this.jobid < MapleJob.KANNA1.getId() || this.jobid > MapleJob.KANNA4.getId()) {
            return;
        }
        Point p = owner.getTruePosition();
        setPosition(p);
        setStance(this.fh);
    }

    @Override
    public void sendSpawnData(Client client) {
        client.SendPacket(CField.spawnHaku(this, false));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.SendPacket(CField.destroyHaku(owner));
    }

    public int getOwner() {
        return this.owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    public void sendStats() {
        this.stats = !this.stats;
    }

    public boolean getStats() {
        return this.stats;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public final Point getPos() {
        return this.pos;
    }

    public final void setPos(Point pos) {
        this.pos = pos;
    }

    public final void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if ((move instanceof LifeMovement)) {
                if ((move instanceof MovementTypeA)) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getStance());
            }
        }
    }

    public int getFootHold() {
        return fh;
    }

    /**
     * @return the equipId
     */
    public int getEquipId() {
        return equipId;
    }

    /**
     * @param equipId the equipId to set
     */
    public void setEquipId(int equipId) {
        this.equipId = equipId;
    }
}
