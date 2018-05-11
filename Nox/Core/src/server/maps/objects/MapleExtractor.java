package server.maps.objects;

import client.Client;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

public class MapleExtractor extends MapleMapObject {

    public int owner, timeLeft, itemId, fee;
    public long startTime;
    public String ownerName;

    public MapleExtractor(User owner, int itemId, int fee, int timeLeft) {
        super();
        this.owner = owner.getId();
        this.itemId = itemId;
        this.fee = fee;
        this.ownerName = owner.getName();
        this.startTime = System.currentTimeMillis();
        this.timeLeft = timeLeft;
        setPosition(owner.getPosition());
    }

    public int getTimeLeft() { //tbh idk if this is even right, lol
        return timeLeft;
    }

    @Override
    public void sendSpawnData(Client client) {
        client.SendPacket(CField.makeExtractor(owner, ownerName, getTruePosition(), getTimeLeft(), itemId, fee));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.SendPacket(CField.removeExtractor(this.owner));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.EXTRACTOR;
    }
}
