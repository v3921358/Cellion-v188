package server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.QuestStatus;
import client.Trait.MapleTraitType;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import enums.EquipSlotType;
import client.inventory.InventoryException;
import client.inventory.Item;
import enums.ItemFlag;
import enums.ItemType;
import client.inventory.MapleInventoryIdentifier;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import client.inventory.RingSet;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import server.maps.MapleMapItem;
import server.maps.objects.Android;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.potentials.ItemPotentialOption;
import server.potentials.ItemPotentialProvider;
import enums.ItemPotentialTierType;
import server.quest.Quest;
import tools.LogHelper;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CSPacket;
import tools.packet.WvsContext;
import tools.packet.WvsContext.InfoPacket;

public class MapleInventoryManipulator {

    public static void addRing(User chr, int itemId, int ringId, int sn, String partner) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        Item ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.getClient().SendPacket(CSPacket.sendBoughtRings(GameConstants.isCrushRing(itemId), ring, sn, chr.getClient().getAccID(), partner));
    }

    public static boolean addbyItem(final ClientSocket c, final Item item) {
        return addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(final ClientSocket c, final Item item, final boolean fromcs) {
        final InventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            }
            return newSlot;
        }
        if (GameConstants.isHarvesting(item.getItemId())) {
            c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
        }

        List<ModifyInventory> mod = new ArrayList<>();
        mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
        c.SendPacket(WvsContext.inventoryOperation(true, mod));

        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, Pet pet) {
        int uniqueid = -1;
        if (InventoryConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getItem().getUniqueId();
            } else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        } else if (GameConstants.getInventoryType(itemId) == InventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) { //less work to do
            uniqueid = MapleInventoryIdentifier.getInstance(); //shouldnt be generated yet, so put it here
        }
        return uniqueid;
    }

    public static boolean addById(ClientSocket c, int itemId, short quantity, String gmLog) {
        return addById(c, itemId, quantity, null, null, 0, false, gmLog);
    }

    public static boolean addById(ClientSocket c, int itemId, short quantity, String owner, String gmLog) {
        return addById(c, itemId, quantity, owner, null, 0, false, gmLog);
    }

    public static byte addId(ClientSocket c, int itemId, short quantity, String owner, String gmLog) {
        return addId(c, itemId, quantity, owner, null, 0, false, gmLog);
    }

    public static boolean addById(ClientSocket c, int itemId, short quantity, String owner, Pet pet, String gmLog) {
        return addById(c, itemId, quantity, owner, pet, 0, false, gmLog);
    }

    public static boolean addById(ClientSocket c, int itemId, short quantity, String owner, Pet pet, long period, boolean hours, String gmLog) {
        return addId(c, itemId, quantity, owner, pet, period, hours, gmLog) >= 0;
    }

    public static byte addId(ClientSocket c, int itemId, short quantity, String owner, Pet pet, long period, boolean hours, String gmLog) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false) || !ii.itemExists(itemId)) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
            return -1;
        }
        final InventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(InventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                for (Item eItem : existing) {//This will update the quantity of an item that already exist in the players inventory
                    short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                        eItem.setQuantity(newQ);

                        List<ModifyInventory> mod = new ArrayList<>();
                        mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, eItem));
                        c.SendPacket(WvsContext.inventoryOperation(true, mod));
                    }
                }
                Item nItem; // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0, uniqueid);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
                            return -1;
                        }
                        if (gmLog != null) {
                            nItem.setGMLog(gmLog);
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0) {
                            nItem.setExpiration(System.currentTimeMillis() + (period * (hours ? 1 : 24) * 60 * 60 * 1000));
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.getItem().setPosition(newSlot);
                            c.getPlayer().addPet(pet);
                        }
                        List<ModifyInventory> mod = new ArrayList<>();
                        mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nItem));
                        c.SendPacket(WvsContext.inventoryOperation(true, mod));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        c.getPlayer().havePartyQuest(itemId);
                        c.SendPacket(WvsContext.enableActions());
                        return (byte) newSlot;
                    }
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                    return -1;
                }
                if (period > 0) {
                    nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, nItem));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
            }
        } else {
            if (quantity == 1) {
                final Item nEquip = ii.getEquipById(itemId, uniqueid);
                if (owner != null) {
                    nEquip.setOwner(owner);
                }
                if (gmLog != null) {
                    nEquip.setGMLog(gmLog);
                }
                if (period > 0) {
                    nEquip.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
                if (newSlot == -1) {
                    c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
                    return -1;
                }
                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nEquip));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                if (GameConstants.isHarvesting(itemId)) {
                    c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
                }
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        c.getPlayer().havePartyQuest(itemId);
        return (byte) newSlot;
    }

    public static Item addbyIdGachapon(final ClientSocket c, final int itemId, short quantity) {
        if (c.getPlayer().getInventory(InventoryType.EQUIP).getNextFreeSlot() == -1 || c.getPlayer().getInventory(InventoryType.USE).getNextFreeSlot() == -1 || c.getPlayer().getInventory(InventoryType.ETC).getNextFreeSlot() == -1 || c.getPlayer().getInventory(InventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.isPickupRestricted(itemId) && c.getPlayer().haveItem(itemId, 1, true, false)) || (!ii.itemExists(itemId))) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<ModifyInventory>()));
            return null;
        }
        final InventoryType type = GameConstants.getInventoryType(itemId);

        if (!type.equals(InventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemId);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemId);

            if (!GameConstants.isRechargable(itemId)) {
                Item nItem = null;
                boolean recieved = false;

                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            nItem = (Item) i.next();
                            short oldQ = nItem.getQuantity();

                            if (oldQ < slotMax) {
                                recieved = true;

                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                nItem.setQuantity(newQ);
                                List<ModifyInventory> mod = new ArrayList<>();
                                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, nItem));
                                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0);
                        final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1 && recieved) {
                            return nItem;
                        } else if (newSlot == -1) {
                            return null;
                        }
                        recieved = true;

                        List<ModifyInventory> mod = new ArrayList<>();
                        mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nItem));
                        c.SendPacket(WvsContext.inventoryOperation(true, mod));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0);
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    return null;
                }
                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nItem));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        } else {
            if (quantity == 1) {
                final Item item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                final short newSlot = c.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    return null;
                }
                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                c.getPlayer().havePartyQuest(item.getItemId());
                return item;
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        return null;
    }

    /**
     * Adds an equipment to the player's inventory from an item drop [reactor, monster, etc]
     *
     * @param c
     * @param item
     * @param show
     * @return
     */
    public static boolean addFromDrop(ClientSocket c, final Item item, boolean show) {
        return addFromDrop(c, item, show, null);
    }

    /**
     * Adds an equipment to the player's inventory from an item drop [reactor, monster, etc]
     *
     * @param c
     * @param item
     * @param show
     * @param mItem
     * @return
     */
    public static boolean addFromDrop(ClientSocket c, Item item, boolean show, MapleMapItem mItem) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (c.getPlayer() == null || (ii.isPickupRestricted(item.getItemId()) && c.getPlayer().haveItem(item.getItemId(), 1, true, false)) || (!ii.itemExists(item.getItemId()))) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            return false;
        }
        short quantity = item.getQuantity();
        final InventoryType type = GameConstants.getInventoryType(item.getItemId());

        if (!type.equals(InventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(item.getItemId());
            final List<Item> existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity < 1) { //wth
                    c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                    return false;
                }
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<Item> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            final Item eItem = (Item) i.next();
                            final short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                                final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);

                                List<ModifyInventory> mod = new ArrayList<>();
                                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, eItem));
                                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    final short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    final Item nItem = new Item(item.getItemId(), (byte) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    nItem.setGMLog(item.getGMLog());
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    List<ModifyInventory> mod = new ArrayList<>();
                    mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nItem));
                    c.SendPacket(WvsContext.inventoryOperation(true, mod));
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                nItem.setGMLog(item.getGMLog());
                final short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                    return false;
                }

                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, nItem));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
            }
        } else {
            if (quantity == 1) {
                if (mItem != null && item.getType() == ItemType.Equipment) {
                    if (item.getType() == ItemType.Equipment) {
                        ItemPotentialProvider.addPotentialtoMonsterItemDrop((Equip) item, mItem.isBossDrop(), mItem.isEliteBossDrop());
                    }
                }

                final short newSlot = c.getPlayer().getInventory(type).addItem(item);
                if (newSlot == -1) {
                    c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                    return false;
                }
                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
                if (GameConstants.isHarvesting(item.getItemId())) {
                    c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
                }
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }

        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.SendPacket(InfoPacket.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    public static boolean checkSpace(final ClientSocket c, final int itemid, int quantity, final String owner) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (c == null || c.getPlayer() == null || (ii.isPickupRestricted(itemid) && c.getPlayer().haveItem(itemid, 1, true, false)) || (!ii.itemExists(itemid))) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final InventoryType type = GameConstants.getInventoryType(itemid);
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) { //wtf is causing this?
            return false;
        }
        if (!type.equals(InventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(itemid);
            final List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    for (Item eItem : existing) {
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }
            }
            // add new slots if there is still something left
            final int numSlotsNeeded;
            if (slotMax > 0 && !GameConstants.isRechargable(itemid)) {
                numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        } else {
            return !c.getPlayer().getInventory(type).isFull();
        }
    }

    public static boolean removeFromSlot(final ClientSocket c, final InventoryType type, final short slot, final short quantity, final boolean fromDrop) {
        return removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static boolean removeFromSlot(final ClientSocket c, final InventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (GameConstants.isHarvesting(item.getItemId())) {
                c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
            }

            List<ModifyInventory> mod = new ArrayList<>();
            if (item.getQuantity() == 0 && !allowZero) {
                mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
            } else {
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, item));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
            }
            return true;
        }
        return false;
    }

    public static boolean removeById(final ClientSocket c, final InventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            int theQ = item.getQuantity();
            if (remremove <= theQ && removeFromSlot(c, type, item.getPosition(), (short) remremove, fromDrop, consume)) {
                remremove = 0;
                break;
            } else if (remremove > theQ && removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume)) {
                remremove -= theQ;
            }
        }
        return remremove <= 0;
    }

    public static boolean removeFromSlot_Lock(final ClientSocket c, final InventoryType type, final short slot, short quantity, final boolean fromDrop, final boolean consume) {
        if (c.getPlayer() == null || c.getPlayer().getInventory(type) == null) {
            return false;
        }
        final Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            if (ItemFlag.LOCK.check(item.getFlag()) || ItemFlag.UNTRADABLE.check(item.getFlag())) {
                return false;
            }
            return removeFromSlot(c, type, slot, quantity, fromDrop, consume);
        }
        return false;
    }

    public static boolean removeById_Lock(final ClientSocket c, final InventoryType type, final int itemId) {
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (removeFromSlot_Lock(c, type, item.getPosition(), (short) 1, false, false)) {
                return true;
            }
        }
        return false;
    }

    public static void move(final ClientSocket c, final InventoryType type, final short src, final short dst) {
        if (src == dst) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        final Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            c.getPlayer().dropMessage(1, "" + type);
            return;
        }
        boolean bag = false, switchSrcDst = false, bothBag = false;
        short eqIndicator = -1;
        if (dst > c.getPlayer().getInventory(type).getSlotLimit()) {
            if (type == InventoryType.ETC && dst > 100 && dst % 100 != 0) {
                final int eSlot = c.getPlayer().getExtendedSlot((dst / 100) - 1);
                if (eSlot > 0) {
                    final StatEffect ee = ii.getItemEffect(eSlot);
                    if (dst % 100 > ee.getSlotCount() || ee.getType() != ii.getBagType(source.getItemId()) || ee.getType() <= 0) {
                        c.getPlayer().dropMessage(1, "You may not move that item to the bag.");
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    } else {
                        eqIndicator = 0;
                        bag = true;
                    }
                } else {
                    c.getPlayer().dropMessage(1, "You may not move it to that bag.");
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
            } else {
                c.getPlayer().dropMessage(1, "You may not move it there.");
                c.SendPacket(WvsContext.enableActions());
                return;
            }
        }
        if (src > c.getPlayer().getInventory(type).getSlotLimit() && type == InventoryType.ETC && src > 100 && src % 100 != 0) {
            //source should be not null so not much checks are needed
            if (!bag) {
                switchSrcDst = true;
                eqIndicator = 0;
                bag = true;
            } else {
                bothBag = true;
            }
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (GameConstants.isHarvesting(source.getItemId())) {
            c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
        }
        List<ModifyInventory> mod = new ArrayList<>();
        if (!type.equals(InventoryType.EQUIP) && initialTarget != null
                && initialTarget.getItemId() == source.getItemId()
                && initialTarget.getOwner().equals(source.getOwner())
                && initialTarget.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(InventoryType.CASH)) {
            if (GameConstants.isHarvesting(initialTarget.getItemId())) {
                c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
            }
            if ((olddstQ + oldsrcQ) > slotMax) {
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, source));
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, initialTarget));
            } else {
                mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, source));
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, initialTarget));
            }
        } else {
            mod.add(new ModifyInventory(ModifyInventoryOperation.Move, source, src));
        }

        c.SendPacket(WvsContext.inventoryOperation(true, mod));
    }

    public static void equip(final ClientSocket c, final short src, short dst) {
        final User chr = c.getPlayer();
        if (chr.isDeveloper()) {
            chr.dropMessage(5, "[Equip Debug] Slot Type : " + dst);
        }
        if (chr == null || dst == EquipSlotType.MonsterBook.getSlot()) {
            return;
        }
        final PlayerStats statst = c.getPlayer().getStat();

        final Equip source = (Equip) chr.getInventory(InventoryType.EQUIP).getItem(src);
        final Equip target = (Equip) chr.getInventory(InventoryType.EQUIPPED).getItem(dst); // Currently equipping

        if (source == null || source.getDurability() == 0 || GameConstants.isHarvesting(source.getItemId())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());

        if (stats == null
                || !ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), c.getPlayer().getStat().levelBonus, source.getReqLevel())) {

            c.SendPacket(WvsContext.enableActions());
            return;
        }
        // Equipment slot check
        final EquipSlotType type = ii.getSlotType(source.getItemId());
        final boolean isCashItem = ii.isCash(source.getItemId());
        //final int equipSlotCheckResult = type.checkEquipmentSlotNumber(isCashItem, dst, source.getItemId(), chr); // Check for equipment slot

        /*if (!GameConstants.requiresEquipHotfix(source.getItemId())) {
            final int equipSlotCheckResult = type.checkEquipmentSlotNumber(isCashItem, dst, source.getItemId(), chr); // Check for equipment slot
            if (equipSlotCheckResult != 1) {
                if (equipSlotCheckResult == 0) { // slot check error
                    // Logging
                    LogHelper.PACKET_EDIT_HACK.get().info(
                            String.format("[MapleInventoryManipulator] %s [ChrID: %d; AccId %d] has tried to equip ItemId %d %s to SlotNumber %d", chr.getName(), chr.getId(), c.getAccID(), source.getItemId(), type.name(), dst)
                    );
                    //        System.out.println(String.format("[MapleInventoryManipulator] %s [ChrID: %d; AccId %d] has tried to equip ItemId %d %s to SlotNumber %d", chr.getName(), chr.getId(), c.getAccID(), source.getItemId(), type.name(), dst));
                } else if (equipSlotCheckResult == -1) { // inventory full
                    // nothing to do... 
                }
                c.write(CWvsContext.enableActions());
                return;
            }
        }*/
        List<ModifyInventory> mod = new ArrayList<>();

        short flag = source.getFlag();
        if (stats.get("equipTradeBlock") != null || source.getItemId() / 10000 == 167) { // Block trade when equipped.
            if (!ItemFlag.UNTRADABLE.check(flag)) {
                flag |= ItemFlag.UNTRADABLE.getValue();
                source.setFlag(flag);
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, source));
            }
        }

        if (InventoryConstants.isWeapon(source.getItemId())) {
            // c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.BOOSTER);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.NoBulletConsume);
            // c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SOULARROW);
            // c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
            // c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.LIGHTNING_CHARGE);
        }

        if (source.getItemId() / 10000 == 166) {
            if (source.getAndroid() == null) {
                int uid = MapleInventoryIdentifier.getInstance();
                source.setUniqueId(uid);
                source.setAndroid(Android.createAndroid(source.getItemId(), uid));
                flag = (short) (flag | ItemFlag.LOCK.getValue());
                flag = (short) (flag | ItemFlag.UNTRADABLE.getValue());
                flag = (short) (flag | ItemFlag.ANDROID_ACTIVATED.getValue());
                source.setFlag(flag);
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, source));
            }
            chr.removeAndroid();
            chr.setAndroid(source.getAndroid());
        } else if ((dst <= -1300) && (chr.getAndroid() != null)) {
            chr.setAndroid(chr.getAndroid());
        }
        if (source.getCharmEXP() > 0 && !ItemFlag.CHARM_EQUIPPED.check(flag)) {
            chr.getTrait(MapleTraitType.charm).addExp(source.getCharmEXP(), chr);
            source.setCharmEXP((short) 0);
            flag |= ItemFlag.CHARM_EQUIPPED.getValue();
            source.setFlag(flag);
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, source));
        }

        chr.getInventory(InventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(InventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(InventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(InventoryType.EQUIP).addFromDB(target);
        }

        // Handle other stuff, like buffs, potentials
        if (InventoryConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.Booster);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.NoBulletConsume);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.SoulArrow);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.WeaponCharge);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.AssistCharge);
        } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.Mechanic);
        } else if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }

        // Handle potential skills
        if (source.getPotentialTier() != ItemPotentialTierType.None && !source.getPotentialTier().isHiddenType()) {
            handleEquipPotentialSkill(c.getPlayer(), true, ItemPotentialProvider.getPotentialInfo(source.getPotential1()));
            handleEquipPotentialSkill(c.getPlayer(), true, ItemPotentialProvider.getPotentialInfo(source.getPotential2()));
            handleEquipPotentialSkill(c.getPlayer(), true, ItemPotentialProvider.getPotentialInfo(source.getPotential3()));
        }

        source.setPosition(dst);
        mod.add(new ModifyInventory(ModifyInventoryOperation.Move, source, src));
        c.SendPacket(WvsContext.inventoryOperation(true, mod));
        chr.equipChanged(); // this code also update the character's stats
    }

    public static void unequip(final ClientSocket c, short src, short dst) {
        Equip source = (Equip) c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) c.getPlayer().getInventory(InventoryType.EQUIP).getItem(dst);
        if (dst < 0 || src == EquipSlotType.MonsterBook.getSlot()) {
            return;
        }
        List<ModifyInventory> mod = new ArrayList<>();
        if (source == null && (src <= -1500 && src >= -1512)) {
            short alphaCashPos = GameConstants.getAlphaCashPosition(src);
            source = (Equip) c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(alphaCashPos);
            mod.add(new ModifyInventory(ModifyInventoryOperation.Move, source, alphaCashPos));
            src = alphaCashPos;
        }
        if (source == null) {
            return;
        }
        if (source.isBetaShare()) {
            if (source.getPosition() > -1500) {
                Equip remove = (Equip) source.copy();
                short pos = GameConstants.getBetaCashPosition(source.getPosition());
                remove.setPosition(pos);
                source.setBetaShare(false);
                mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, remove, pos));
            }
        }
        c.getPlayer().getInventory(InventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(InventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(InventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(InventoryType.EQUIPPED).addFromDB(target);
        }
        if (InventoryConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.Booster);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.NoBulletConsume);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.SoulArrow);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.WeaponCharge);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.AssistCharge);
        } else if (source.getItemId() / 10000 == 190 || source.getItemId() / 10000 == 191) {
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
            c.getPlayer().cancelEffectFromTemporaryStat(CharacterTemporaryStat.Mechanic);
        } else if (source.getItemId() / 10000 == 166 || source.getItemId() / 10000 == 167) {
            c.getPlayer().removeAndroid();
        } else if (src <= -1300 && c.getPlayer().getAndroid() != null) {
            c.getPlayer().setAndroid(c.getPlayer().getAndroid());
        } else if (source.getItemId() == 1122017) {
            c.getPlayer().cancelFairySchedule(true);
        }

        if (source.getPotentialTier() != ItemPotentialTierType.None && !source.getPotentialTier().isHiddenType()) {
            handleEquipPotentialSkill(c.getPlayer(), false, ItemPotentialProvider.getPotentialInfo(source.getPotential1()));
            handleEquipPotentialSkill(c.getPlayer(), false, ItemPotentialProvider.getPotentialInfo(source.getPotential2()));
            handleEquipPotentialSkill(c.getPlayer(), false, ItemPotentialProvider.getPotentialInfo(source.getPotential3()));
        }

        mod.add(new ModifyInventory(ModifyInventoryOperation.Move, source, src));
        c.SendPacket(WvsContext.inventoryOperation(true, mod));
        c.getPlayer().equipChanged();
    }

    /**
     * Handles the addition and removal of potential skills while equipping or unequipping a weapon.
     *
     * @param chr
     * @param isEquipping
     * @param lineOption
     */
    private static void handleEquipPotentialSkill(User chr, boolean isEquipping, ItemPotentialOption lineOption) {
        if (lineOption != null) {
            int skillIdForJob = PlayerStats.getSkillByJob(lineOption.getSkill().getBasicSkillId(), chr.getJob());
            Skill skill = SkillFactory.getSkill(skillIdForJob);

            if (skill != null) {
                chr.changeSingleSkillLevel(skill, isEquipping ? 1 : 0, (byte) 0, 0);
            }
        }
    }

    public static boolean drop(final ClientSocket c, InventoryType type, final short src, final short quantity) {
        return drop(c, type, src, quantity, false);
    }

    public static boolean drop(final ClientSocket c, InventoryType type, final short src, short quantity, final boolean npcInduced) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = InventoryType.EQUIPPED;
        }
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return false;
        }
        final Item source = c.getPlayer().getInventory(type).getItem(src);
        if (quantity < 0 || source == null || src == -55 || (!npcInduced && InventoryConstants.isPet(source.getItemId())) || (quantity == 0 && !GameConstants.isRechargable(source.getItemId())) || c.getPlayer().inPVP()) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        
        if (!ServerConstants.GM_TRADING && c.getPlayer().getGMLevel() == 3) { // Regular GMs can't drop items for players, just delete from inventory.
            removeFromSlot(c, type, src, quantity, false);
            c.SendPacket(WvsContext.enableActions());
            return false;
        }

        final short flag = source.getFlag();
        if (quantity > source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == InventoryType.EQUIP)) { // hack
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        final Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, source));
            c.SendPacket(WvsContext.inventoryOperation(true, mod));

            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true, false);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true, false);
                } else if (GameConstants.isAnyDropMap(c.getPlayer().getMapId())) {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true, false);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            } else {
                if ((InventoryConstants.isPet(source.getItemId()) || ItemFlag.UNTRADABLE.check(flag)) && !GameConstants.isAnyDropMap(c.getPlayer().getMapId())) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true, false);
                }
            }
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            if (GameConstants.isHarvesting(source.getItemId())) {
                c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
            }
            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, source));
            c.SendPacket(WvsContext.inventoryOperation(true, mod));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true, false);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true, false);
                } else if (GameConstants.isAnyDropMap(c.getPlayer().getMapId())) {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true, false);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            } else {
                if ((InventoryConstants.isPet(source.getItemId()) || ItemFlag.UNTRADABLE.check(flag)) && !GameConstants.isAnyDropMap(c.getPlayer().getMapId())) {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                } else {
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true, false);
                }
            }
        }
        return true;
    }
}
