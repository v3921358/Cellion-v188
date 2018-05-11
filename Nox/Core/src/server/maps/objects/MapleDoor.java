package server.maps.objects;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import client.ClientSocket;
import server.MaplePortal;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.WvsContext.PartyPacket;

public class MapleDoor extends MapleMapObject {

    private final WeakReference<User> owner;
    private final MapleMap town;
    private final MaplePortal townPortal;
    private final MapleMap target;
    private final int skillId, ownerId;
    private final Point targetPosition;
    private final Point originalCharacterPosition;

    public MapleDoor(final User owner, final Point targetPosition, final int skillId) {
        super();
        this.owner = new WeakReference<>(owner);
        this.ownerId = owner.getId();
        this.target = owner.getMap();
        this.targetPosition = targetPosition;
        this.originalCharacterPosition = owner.getPosition();
        setPosition(this.targetPosition);
        this.town = this.target.getReturnMap();
        this.townPortal = getFreePortal();
        this.skillId = skillId; // May be from bishop or beast tamer EKA_EXPRESS
    }

    public MapleDoor(final MapleDoor origDoor) {
        super();
        this.owner = new WeakReference<>(origDoor.owner.get());
        this.town = origDoor.town;
        this.townPortal = origDoor.townPortal;
        this.target = origDoor.target;
        this.targetPosition = new Point(origDoor.targetPosition);
        this.originalCharacterPosition = targetPosition;
        this.skillId = origDoor.skillId;
        this.ownerId = origDoor.ownerId;
        setPosition(townPortal.getPosition());
    }

    public final int getSkill() {
        return skillId;
    }

    public final int getOwnerId() {
        return ownerId;
    }

    private MaplePortal getFreePortal() {
        final List<MaplePortal> freePortals = new ArrayList<>();

        for (final MaplePortal port : town.getPortals()) {
            if (port.getType() == 6) {
                freePortals.add(port);
            }
        }
        Collections.sort(freePortals, new Comparator<MaplePortal>() {

            @Override
            public final int compare(final MaplePortal o1, final MaplePortal o2) {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() == o2.getId()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (MapleMapObject obj : town.getAllMapObjects(MapleMapObjectType.DOOR)) {
            final MapleDoor door = (MapleDoor) obj;
            /// hmm
            if (door.getOwner() != null && door.getOwner().getParty() != null && getOwner() != null && getOwner().getParty() != null && getOwner().getParty().getId() == door.getOwner().getParty().getId()) {
                return null; //one per
            }
            freePortals.remove(door.getTownPortal());
        }
        if (freePortals.size() <= 0) {
            return null;
        }
        return freePortals.iterator().next();
    }

    @Override
    public final void sendSpawnData(final ClientSocket client) {
        if (getOwner() == null || target == null || client.getPlayer() == null) {
            return;
        }
        if (target.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
            client.SendPacket(CField.spawnDoor(getOwnerId(), target.getId() == client.getPlayer().getMapId() ? targetPosition : townPortal.getPosition(), originalCharacterPosition, true, skillId)); //spawnDoor always has same position.
            if (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
                client.SendPacket(PartyPacket.partyPortal(town.getId(), target.getId(), skillId, target.getId() == client.getPlayer().getMapId() ? targetPosition : townPortal.getPosition(), true));
            }
            client.SendPacket(WvsContext.spawnPortal(town.getId(), target.getId(), skillId, target.getId() == client.getPlayer().getMapId() ? targetPosition : townPortal.getPosition()));
        }
    }

    @Override
    public final void sendDestroyData(final ClientSocket client) {
        if (client.getPlayer() == null || getOwner() == null || target == null) {
            return;
        }
        if (target.getId() == client.getPlayer().getMapId() || getOwnerId() == client.getPlayer().getId() || (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
            client.SendPacket(CField.removeDoor(getOwnerId(), false));
            if (getOwner() != null && getOwner().getParty() != null && client.getPlayer().getParty() != null && (getOwnerId() == client.getPlayer().getId() || getOwner().getParty().getId() == client.getPlayer().getParty().getId())) {
                client.SendPacket(PartyPacket.partyPortal(999999999, 999999999, 0, new Point(-1, -1), false));
            }
            client.SendPacket(WvsContext.spawnPortal(999999999, 999999999, 0, null));
        }
    }

    public final void warp(final User chr, final boolean toTown) {
        if (chr.getId() == getOwnerId() || (getOwner() != null && getOwner().getParty() != null && chr.getParty() != null && getOwner().getParty().getId() == chr.getParty().getId())) {
            if (!toTown) {
                chr.changeMap(target, target.findClosestPortal(targetPosition));
            } else {
                chr.changeMap(town, townPortal);
            }
        } else {
            chr.getClient().SendPacket(WvsContext.enableActions());
        }
    }

    public final User getOwner() {
        return owner.get();
    }

    public final MapleMap getTown() {
        return town;
    }

    public final MaplePortal getTownPortal() {
        return townPortal;
    }

    public final MapleMap getTarget() {
        return target;
    }

    public final Point getTargetPosition() {
        return targetPosition;
    }

    public Point getOriginalCharacterPosition() {
        return originalCharacterPosition;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.DOOR;
    }
}
