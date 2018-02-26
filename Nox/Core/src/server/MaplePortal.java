package server;

import java.awt.Point;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import service.ChannelServer;
import scripting.provider.PortalScriptManager;
import server.maps.MapleMap;
import tools.packet.CWvsContext;

public class MaplePortal {

    public static final int MAP_PORTAL = 2;
    public static final int DOOR_PORTAL = 6;
    private String name, target, scriptName;
    private Point position;
    private int targetmap, type, id;
    private boolean portalState = true;

    public MaplePortal(final int type) {
        this.type = type;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final Point getPosition() {
        return position;
    }

    public final String getTarget() {
        return target;
    }

    public final int getTargetMapId() {
        return targetmap;
    }

    public final int getType() {
        return type;
    }

    public final String getScriptName() {
        return scriptName;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final void setPosition(final Point position) {
        this.position = position;
    }

    public final void setTarget(final String target) {
        this.target = target;
    }

    public final void setTargetMapId(final int targetmapid) {
        this.targetmap = targetmapid;
    }

    public final void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    public final void enterPortal(final MapleClient c) {
        if (getPosition().distanceSq(c.getPlayer().getPosition()) > 40000 && !c.getPlayer().isGM()) {
            c.write(CWvsContext.enableActions());
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        final MapleMap currentmap = c.getPlayer().getMap();
        if (!c.getPlayer().hasBlockedInventory() && (portalState || c.getPlayer().isGM())) {
            if (getScriptName() != null) {
                c.getPlayer().checkFollow();

                PortalScriptManager.getInstance().executePortalScript(this, c);
            } else if (getTargetMapId() != 999999999) {
                final MapleMap oldto = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());
                final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(GameConstants.getSpecialMapTarget(getTargetMapId()));
                if (to == null) {
                    c.write(CWvsContext.enableActions());
                    return;
                }
                if (c.getPlayer().getMapId() == 109010100 || c.getPlayer().getMapId() == 109010104 || c.getPlayer().getMapId() == 109020001) {
                    c.getPlayer().dropMessage(5, "You may not exit the event map.");
                    c.write(CWvsContext.enableActions());
                    return;
                }
                c.getPlayer().changeMapPortal(to, to.getPortal(GameConstants.getSpecialPortalTarget(oldto.getId(), getTarget())) == null ? to.getPortal(0) : to.getPortal(GameConstants.getSpecialPortalTarget(oldto.getId(), getTarget()))); //late resolving makes this harder but prevents us from loading the whole world at once
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getMap() == currentmap) { // Character is still on the same map.
            c.write(CWvsContext.enableActions());
        }
    }

    public boolean getPortalState() {
        return portalState;
    }

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}
