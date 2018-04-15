package server.maps.objects;

import client.MapleClient;
import client.MapleJob;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

public class MapleDragon extends AnimatedMapleMapObject {

    private final int owner, jobid;

    public MapleDragon(User owner) {
        super();
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        //if (jobid < MapleJob.EVAN1.getId() || jobid > MapleJob.EVAN10.getId()) {
        if (jobid < MapleJob.EVAN1.getId() || jobid > MapleJob.EVAN5.getId()) {
            return;
        }
        setPosition(owner.getTruePosition());
        setStance(4);
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.SendPacket(CField.spawnDragon(this));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.SendPacket(CField.removeDragon(this.owner));
    }

    public int getOwner() {
        return this.owner;
    }

    public int getJobId() {
        return this.jobid;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }
}
