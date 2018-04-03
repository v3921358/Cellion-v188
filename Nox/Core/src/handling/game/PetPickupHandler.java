package handling.game;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.ItemType;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import static handling.game._CommonPlayerOperationHandler.useItem;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class PetPickupHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 || c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1 || c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1 || c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
            c.getPlayer().dropMessage(5, "Your inventory appears to be full.");
            return;
        }

        final byte petz = (byte) iPacket.DecodeInteger();//c.getPlayer().getPetIndex((int)slea.readLong());
        final Pet pet = chr.getPet(petz);
        chr.updateTick(iPacket.DecodeInteger());
        c.getPlayer().setScrolledPosition((short) 0);
        iPacket.Skip(1); // [4] Zero, [4] Seems to be tickcount, [1] Always zero
        final Point Client_ReportedPos = iPacket.DecodePosition();
        final MapleMapObject ob = chr.getMap().getMapObject(iPacket.DecodeInteger(), MapleMapObjectType.ITEM);

        if (ob == null || pet == null) {
            c.getPlayer().dropMessage(5, "The item pickup has failed due to an unknown reason.");
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.write(CWvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
                c.write(CWvsContext.getInventoryFull());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && mapitem.isPlayerDrop()) {
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getSharedMapResources().everlast))) {
                c.write(CWvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.write(CWvsContext.enableActions());
                return;
            }

            final double Distance = Client_ReportedPos.distanceSq(mapitem.getPosition());
            if (Distance > 10000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (pet.getPos().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.PET_ITEMVAC_SERVER);
            }

            // Meso Pickup Handling
            if (mapitem.getMeso() > 0) {
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<User> toGive = new LinkedList<>();
                    final int splitMeso = mapitem.getMeso() * 40 / 100;
                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        User m = chr.getMap().getCharacterById(z.getId());
                        if (m != null && m.getId() != chr.getId()) {
                            toGive.add(m);
                        }
                    }
                    for (final User m : toGive) {
                        m.gainMeso(splitMeso / toGive.size(), true);
                    }
                    chr.gainMeso(mapitem.getMeso() - splitMeso, true);
                } else {
                    chr.gainMeso(mapitem.getMeso(), true);
                }
                removeItem_Pet(chr, mapitem, petz);
            } else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId()) || mapitem.getItemId() / 10000 == 291) {
                c.write(CWvsContext.enableActions());
            } else if (useItem(c, mapitem.getItemId())) {
                removeItem_Pet(chr, mapitem, petz);
            } else if (mapitem.getItemId() / 10000 != 291 && MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                chr.dropMessage(5, "[Debug] Pet Pickup Item ID: " + mapitem.getItem().getItemId());
                MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem);
                removeItem_Pet(chr, mapitem, petz);

                // Display
                if (!mapitem.isPlayerDrop() && mapitem.getItem().getType() == ItemType.Equipment) {
                    chr.getClient().write(CWvsContext.InfoPacket.showEquipmentPickedUp((Equip) mapitem.getItem()));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static final void removeItem_Pet(final User chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), pet));
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandomDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }
}
