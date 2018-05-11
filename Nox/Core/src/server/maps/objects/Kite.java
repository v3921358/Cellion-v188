package server.maps.objects;

import client.ClientSocket;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledFuture;
import server.Timer.MapTimer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

public class Kite extends MapleMapObject {

    private final int ItemID;
    private final int MapID;
    private final byte KiteType;
    private final String PlayerName;
    private final String Message;
    private WeakReference<MapleMap> Map_ref;
    private ScheduledFuture schedule; // TODO : Do something about this in future if should we have other ways of cancelling kite other than timer

    public Kite(int ItemID, int MapId, String PlayerName, MapleMap Map, String Message, byte KiteType, Point position) {
        this.ItemID = ItemID;
        this.MapID = MapId;
        this.KiteType = KiteType;
        this.PlayerName = PlayerName;
        this.Message = Message;
        this.Map_ref = new WeakReference<MapleMap>(Map);
        setPosition(position);

        schedule = MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                destroyKite();
            }
        }, 1000 * 60 * 60); // one hour perhaps? Not sure... 
    }

    public void destroyKite() {
        final MapleMap map = Map_ref.get();

        if (map != null) {
            map.broadcastMessage(CField.destroyKite(getObjectId(), true));
            map.removeMapObject(this);

            // cleanup
            schedule = null;
            Map_ref = new WeakReference<MapleMap>(null);
        }
    }

    public int getMapid() {
        return MapID;
    }

    public byte getKiteType() {
        return KiteType;
    }

    public int getItemID() {
        return ItemID;
    }

    public String getMessage() {
        return Message;
    }

    public String getName() {
        return PlayerName;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.KITE;
    }

    @Override
    public void sendSpawnData(ClientSocket client) {
        client.SendPacket(CField.spawnKite(this));
    }

    @Override
    public void sendDestroyData(ClientSocket client) {
        //Nothing here either
        client.SendPacket(CField.destroyKite(getObjectId(), true));
    }
}
