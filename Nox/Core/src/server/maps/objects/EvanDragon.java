package server.maps.objects;

import client.ClientSocket;
import client.Jobs;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

public class EvanDragon extends AnimatedMapleMapObject {

    private final int owner, jobid;

    public EvanDragon(User owner) {
        super();
        this.owner = owner.getId();
        this.jobid = owner.getJob();
        //if (jobid < MapleJob.EVAN1.getId() || jobid > MapleJob.EVAN10.getId()) {
        if (jobid < Jobs.EVAN1.getId() || jobid > Jobs.EVAN5.getId()) {
            return;
        }
        setPosition(owner.getTruePosition());
        setStance(4);
    }

    @Override
    public void sendSpawnData(ClientSocket client) {
        client.SendPacket(CField.spawnDragon(this));
    }

    @Override
    public void sendDestroyData(ClientSocket client) {
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
