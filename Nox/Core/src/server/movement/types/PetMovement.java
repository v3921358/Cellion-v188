package server.movement.types;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import client.MapleClient;
import client.MapleQuestStatus.MapleQuestState;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import static handling.game.ItemPickupHandler.removeItem_Pet;
import static handling.game._CommonPlayerOperationHandler.useItem;
import handling.world.MaplePartyCharacter;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.movement.LifeMovementFragment;
import tools.packet.PetPacket;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class PetMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int index = iPacket.DecodeInt();
        iPacket.DecodeByte();
        Pet pet = chr.getPet(index);
        if (pet == null) {
            return;
        }
        pet.settEncodedGatherDuration(iPacket.DecodeInt());
        pet.setxCS(iPacket.DecodeShort());
        pet.setyCS(iPacket.DecodeShort());
        pet.setvYCS(iPacket.DecodeShort());
        pet.setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        if (chr.getMap() != null) { // map crash hack
            pet.updatePosition(res);
            chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), index, pet, res), false);
            if (chr.hasBlockedInventory() || chr.getStat().pickupRange <= 0.0 || chr.inPVP()) {
                return;
            }
            chr.setScrolledPosition((short) 0);
            List<MapleMapObject> objects = chr.getMap().getMapObjectsInRange(chr.getTruePosition(), chr.getRange(), Arrays.asList(MapleMapObjectType.ITEM));
            for (LifeMovementFragment move : res) {
                Point pp = move.getPosition();
                boolean foundItem = false;
                for (MapleMapObject mapitemz : objects) {
                    if (mapitemz instanceof MapleMapItem && (Math.abs(pp.x - mapitemz.getTruePosition().x) <= chr.getStat().pickupRange || Math.abs(mapitemz.getTruePosition().x - pp.x) <= chr.getStat().pickupRange) && (Math.abs(pp.y - mapitemz.getTruePosition().y) <= chr.getStat().pickupRange || Math.abs(mapitemz.getTruePosition().y - pp.y) <= chr.getStat().pickupRange)) {
                        MapleMapItem mapitem = (MapleMapItem) mapitemz;
                        Lock lock = mapitem.getLock();
                        lock.lock();
                        try {
                            if (mapitem.isPickedUp()) {
                                continue;
                            }
                            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != MapleQuestState.Started) {
                                continue;
                            }
                            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                                continue;
                            }
                            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getSharedMapResources().everlast))) {
                                continue;
                            }
                            if (!mapitem.isPlayerDrop() && (mapitem.getDropType() == 1 || mapitem.getDropType() == 3) && mapitem.getOwner() != chr.getId()) {
                                continue;
                            }
                            if (mapitem.getDropType() == 2 && mapitem.getOwner() != chr.getId()) {
                                continue;
                            }
                            if (mapitem.getMeso() > 0) {
                                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                                    List<User> toGive = new LinkedList<>();
                                    int splitMeso = mapitem.getMeso() * 40 / 100;
                                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                                        User m = chr.getMap().getCharacterById(z.getId());
                                        if (m != null && m.getId() != chr.getId()) {
                                            toGive.add(m);
                                        }
                                    }
                                    for (User m : toGive) {
                                        m.gainMeso(splitMeso / toGive.size(), true, true);
                                    }
                                    chr.gainMeso(mapitem.getMeso() - splitMeso, true, true);
                                } else {
                                    chr.gainMeso(mapitem.getMeso(), true, true);
                                }
                                removeItem_Pet(chr, mapitem, index);
                                foundItem = true;
                            } else if (!MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId()) && mapitem.getItem().getItemId() / 10000 != 291) {
                                if (useItem(chr.getClient(), mapitem.getItemId())) {
                                    removeItem_Pet(chr, mapitem, index);
                                } else if (MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                                    if (MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem)) {
                                        removeItem_Pet(chr, mapitem, index);
                                        foundItem = true;
                                    }
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }
                if (foundItem) {
                    return;
                }
            }
        }
    }

}
