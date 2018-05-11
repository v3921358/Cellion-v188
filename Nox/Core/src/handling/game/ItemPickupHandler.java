package handling.game;

import static handling.game._CommonPlayerOperationHandler.useItem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import client.ClientSocket;
import client.QuestStatus.QuestState;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.ItemType;
import client.inventory.MapleInventoryIdentifier;
import constants.InventoryConstants;
import handling.world.MaplePartyCharacter;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ItemPickupHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        if (chr == null || c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        chr.updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);
        iPacket.Skip(1); // or is this before tick?
        final Point Client_Reportedpos = iPacket.DecodePosition();

        if (chr.getMap() == null) {
            return;
        }
        final MapleMapObject ob = chr.getMap().getMapObject(iPacket.DecodeInt(), MapleMapObjectType.ITEM);

        if (ob == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final MapleMapItem mapitem = (MapleMapItem) ob;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            if (mapitem.getQuest() > 0 && chr.getQuestStatus(mapitem.getQuest()) != QuestState.Started) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0)
                    || (mapitem.isPlayerDrop() && chr.getMap().getSharedMapResources().everlast))) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }

            final double Distance = Client_Reportedpos.distanceSq(mapitem.getPosition());
            if (Distance > 5000 && (mapitem.getMeso() > 0 || mapitem.getItemId() != 4001025)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_CLIENT, String.valueOf(Distance));
            } else if (chr.getPosition().distanceSq(mapitem.getPosition()) > 640000.0) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC_SERVER);
            }

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
                        int mesos = splitMeso / toGive.size();
                        if (!mapitem.isPlayerDrop() && m.getStat().incMesoProp > 0) {
                            mesos += Math.floor((m.getStat().incMesoProp * mesos) / 100.0f);
                        }
                        m.gainMeso(mesos, true);
                    }
                    int mesos = mapitem.getMeso() - splitMeso;
                    if (!mapitem.isPlayerDrop() && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                } else {
                    int mesos = mapitem.getMeso();
                    if (!mapitem.isPlayerDrop() && chr.getStat().incMesoProp > 0) {
                        mesos += Math.floor((chr.getStat().incMesoProp * mesos) / 100.0f);
                    }
                    chr.gainMeso(mesos, true);
                }
                removeItem(chr, mapitem, ob);

            } else if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItemId())) {
                c.SendPacket(WvsContext.enableActions());
                c.getPlayer().dropMessage(5, "This item cannot be picked up.");

            } else if (c.getPlayer().inPVP() && Integer.parseInt(c.getPlayer().getEventInstance().getProperty("ice")) == c.getPlayer().getId()) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));

            } else if (useItem(c, mapitem.getItemId())) {
                removeItem(c.getPlayer(), mapitem, ob);
                //another hack
                if (mapitem.getItemId() / 10000 == 291) {
                    //c.getPlayer().getMap().broadcastMessage(CField.getCapturePosition(c.getPlayer().getMap()));
                    //c.getPlayer().getMap().broadcastMessage(CField.resetCapture());
                }
            } else if (mapitem.getItemId() / 10000 != 291 && MapleInventoryManipulator.checkSpace(c, mapitem.getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                if (!InventoryConstants.isPet(mapitem.getItemId())) {
                    MapleInventoryManipulator.addFromDrop(c, mapitem.getItem(), true, mapitem);

                    if (c.getPlayer().isIntern()) { //More debug!
                        c.getPlayer().dropMessage(5, "[Pickup Debug] Item ID : " + mapitem.getItemId());
                    }

                    removeItem(chr, mapitem, ob);
                } else {
                    MapleInventoryManipulator.addById(c, mapitem.getItemId(), (short) 1, "", Pet.createPet(mapitem.getItemId(), MapleItemInformationProvider.getInstance().getName(mapitem.getItemId()), 1, 0, 100, MapleInventoryIdentifier.getInstance(), 0, (short) 0), 90, false, null);
                    removeItem_Pet(chr, mapitem, mapitem.getItemId());
                }

                // Display
                if (!mapitem.isPlayerDrop() && mapitem.getItem().getType() == ItemType.Equipment) {
                    chr.getClient().SendPacket(WvsContext.InfoPacket.showEquipmentPickedUp((Equip) mapitem.getItem()));
                }
            } else {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            }
        } finally {
            lock.unlock();
        }
    }

    private static void removeItem(User chr, MapleMapItem mapitem, MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getId()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);

        if (mapitem.isRandomDrop()) {
            chr.getMap().spawnRandDrop();
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
