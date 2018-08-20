package server.maps.objects;

import java.awt.Rectangle;

import client.ClientSocket;
import scripting.provider.ReactorScriptManager;
import server.Timer.MapTimer;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactorStats;
import tools.Pair;
import tools.packet.CField;

public class Reactor extends AnimatedMapleMapObject {

    private final int rid;
    private final MapleReactorStats stats;
    private byte state = 0;
    private int delay = -1;
    private MapleMap map;
    private String name = "";
    private boolean timerActive = false, alive = true, custom = false;
    private boolean isProfessions; // defines if this is a herb or vien

    public Reactor(MapleReactorStats stats, int rid) {
        this.stats = stats;
        this.rid = rid;
        this.isProfessions = false;
    }

    public boolean isProfessions() {
        return isProfessions;
    }

    public void setisProfessions(boolean isProfessions) {
        this.isProfessions = isProfessions;
    }

    public void setCustom(boolean c) {
        this.custom = c;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    public boolean isTimerActive() {
        return timerActive;
    }

    public int getReactorId() {
        return rid;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.REACTOR;
    }

    public int getReactorType() {
        return stats.getType(state);
    }

    public byte getTouch() {
        return stats.canTouch(state);
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public MapleMap getMap() {
        return map;
    }

    public Pair<Integer, Integer> getReactItem() {
        return stats.getReactItem(state);
    }

    @Override
    public void sendDestroyData(ClientSocket client) {
        client.SendPacket(CField.destroyReactor(this));
    }

    @Override
    public void sendSpawnData(ClientSocket client) {
        client.SendPacket(CField.spawnReactor(this));
    }

    public void forceStartReactor(ClientSocket c) {
        ReactorScriptManager.getInstance().act(c, this);
    }

    public void forceHitReactor(final byte newState) {
        setState((byte) newState);
        setTimerActive(false);
        map.broadcastPacket(CField.triggerReactor(this, (short) 0));
    }

    //hitReactor command for item-triggered reactors
    public void hitReactor(ClientSocket c) {
        hitReactor(0, (short) 0, c);
    }

    public void forceTrigger() {
        map.broadcastPacket(CField.triggerReactor(this, (short) 0));
    }

    public void delayedDestroyReactor(long delay) {
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                map.destroyReactor(getObjectId());
            }
        }, delay);
    }

    public void hitReactor(int charPos, short stance, ClientSocket c) {
        if (stats.getType(state) < 999 && stats.getType(state) != -1) {
            //type 2 = only hit from right (kerning swamp plants), 00 is air left 02 is ground left
            final byte oldState = state;
            if (!(stats.getType(state) == 2 && (charPos == 0 || charPos == 2))) { // next state
                state = stats.getNextState(state);

                if (stats.getNextState(state) == -1 || stats.getType(state) == 999) { //end of reactor
                    if ((stats.getType(state) < 100 || stats.getType(state) == 999) && delay > 0) { //reactor broken
                        map.destroyReactor(getObjectId());
                    } else { //item-triggered on final step
                        map.broadcastPacket(CField.triggerReactor(this, stance));
                    }
                    //if (rid > 200011) {
                    ReactorScriptManager.getInstance().act(c, this);
                    //}
                } else { //reactor not broken yet
                    boolean done = false;
                    map.broadcastPacket(CField.triggerReactor(this, stance)); //magatia is weird cause full beaker can be activated by gm hat o.o
                    if (state == stats.getNextState(state) || rid == 2618000 || rid == 2309000) { //current state = next state, looping reactor
                        if (rid > 200011) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        done = true;
                    }
                    if (stats.getTimeOut(state) > 0) {
                        if (!done && rid > 200011) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        scheduleSetState(state, oldState, stats.getTimeOut(state));
                    }
                }
            }
        }
    }

    public Rectangle getArea() {
        int height = stats.getBR().y - stats.getTL().y;
        int width = stats.getBR().x - stats.getTL().x;
        int origX = getTruePosition().x + stats.getTL().x;
        int origY = getTruePosition().y + stats.getTL().y;

        return new Rectangle(origX, origY, width, height);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Reactor " + getObjectId() + " of id " + rid + " at position " + getPosition().toString() + " state" + state + " type " + stats.getType(state);
    }

    public void delayedHitReactor(final ClientSocket c, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                hitReactor(c);
            }
        }, delay);
    }

    public void scheduleSetState(final byte oldState, final byte newState, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (Reactor.this.state == oldState) {
                    forceHitReactor(newState);
                }
            }
        }, delay);
    }
}
