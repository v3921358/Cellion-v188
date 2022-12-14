package server.maps;

import enums.MapItemType;
import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.ClientSocket;
import client.QuestStatus.QuestState;
import client.inventory.Item;
import server.maps.objects.User;
import tools.packet.CField;

public class MapleMapItem extends MapleMapObject {

    private Item item;
    private final int character_ownerid;
    private int meso = 0, questid = -1;
    private byte type;
    private boolean pickedUp = false;
    private long nextExpiry = 0, nextFFA = 0;
    private int itemPropertiesBits = 0; // bit shift, see MapleMapItemProperties.java

    private final ReentrantLock lock = new ReentrantLock();

    public MapleMapItem(Item item, Point position, User owner, byte type, int questid) {
        setPosition(position);
        this.item = item;
        this.character_ownerid = owner.getId();
        this.type = type;
        this.questid = questid;
    }

    public MapleMapItem(int meso, Point position, User owner, byte type) {
        setPosition(position);
        this.item = null;
        this.character_ownerid = owner.getId();
        this.meso = meso;
        this.type = type;
    }

    public MapleMapItem(Point position, Item item) {
        setPosition(position);
        this.item = item;
        this.character_ownerid = 0;
        this.type = 2;

        setProperties(MapItemType.IsRandomDrop);
    }

    // <editor-fold defaultstate="visible" desc="Map item properties"> 
    public void setProperties(MapItemType properties) {
        itemPropertiesBits |= 1 << properties.getBitvalue();
    }

    public boolean isPlayerDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsPlayerDrop.getBitvalue())) != 0;
    }

    public boolean isRandomDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsRandomDrop.getBitvalue())) != 0;
    }

    public boolean isBossDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsBossDrop.getBitvalue())) != 0;
    }

    public boolean isEliteBossDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsEliteBossDrop.getBitvalue())) != 0;
    }

    public boolean isPickpocketDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsPickpocketDrop.getBitvalue())) != 0;
    }

    /**
     * Determines if an item that floats on the map or not
     *
     * @return
     */
    public boolean isNoMoveItem() {
        return (itemPropertiesBits & (1 << MapItemType.IsCollisionPickUp.getBitvalue())) != 0;
    }

    public boolean isCollisionPickUpDrop() {
        return (itemPropertiesBits & (1 << MapItemType.IsCollisionPickUp.getBitvalue())) != 0;
    }
    // </editor-fold> 

    public final Item getItem() {
        return item;
    }

    public void setItem(Item z) {
        this.item = z;
    }

    public final int getQuest() {
        return questid;
    }

    public final int getItemId() {
        if (getMeso() > 0) {
            return meso;
        }
        return item.getItemId();
    }

    public final int getOwner() {
        return character_ownerid;
    }

    public final int getMeso() {
        return meso;
    }

    public final boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(final boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public byte getDropType() {
        return type;
    }

    public void setDropType(byte z) {
        this.type = z;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.ITEM;
    }

    @Override
    public void sendSpawnData(final ClientSocket client) {
        if (questid <= 0 || client.getPlayer().getQuestStatus(questid) == QuestState.Started) {
            client.SendPacket(CField.dropItemFromMapObject(this, null, getTruePosition(), (byte) 2));
        }
    }

    @Override
    public void sendDestroyData(final ClientSocket client) {
        client.SendPacket(CField.removeItemFromMap(getObjectId(), 1, 0));
    }

    public Lock getLock() {
        return lock;
    }

    public void registerExpire(final long time) {
        nextExpiry = System.currentTimeMillis() + time;
    }

    public void registerFFA(final long time) {
        nextFFA = System.currentTimeMillis() + time;
    }

    public boolean shouldExpire(long now) {
        return !pickedUp && nextExpiry > 0 && nextExpiry < now;
    }

    public boolean shouldFFA(long now) {
        return !pickedUp && type < 2 && nextFFA > 0 && nextFFA < now;
    }

    public boolean hasFFA() {
        return nextFFA > 0;
    }

    public void expire(final MapleMap map) {
        pickedUp = true;
        map.broadcastPacket(CField.removeItemFromMap(getObjectId(), 0, 0));
        map.removeMapObject(this);

        if (isRandomDrop()) {
            map.spawnRandDrop();
        }
    }
}
