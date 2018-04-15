package server.maps.objects;

import java.awt.Point;

import client.MapleClient;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

public class MechDoor extends MapleMapObject {

    private final int owner, partyid, id;

    public MechDoor(User owner, Point pos, int id) {
        super();
        this.owner = owner.getId();
        this.partyid = owner.getParty() == null ? 0 : owner.getParty().getId();
        setPosition(pos);
        this.id = id;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        client.SendPacket(CField.spawnMechDoor(this, false));
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.SendPacket(CField.removeMechDoor(this, false));
    }

    public int getOwnerId() {
        return this.owner;
    }

    public int getPartyId() {
        return this.partyid;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.DOOR;
    }
}
