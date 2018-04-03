package client.inventory;

import client.MapleQuestStatus;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import server.quest.MapleQuest;

/**
 *
 * @author
 */
public enum EquipSlotType {
    Weapon(-11),
    Weapon_TakingBothSlot_Shield(-11),
    TamingMob(-18),
    TamingMob_MechanicTransistor(-1104),
    TamingMob_MechanicFrame(-1103),
    TamingMob_MechanicLeg(-1102),
    TamingMob_MechanicArm(-1101),
    TamingMob_MechanicEngine(-1100),
    TamingMob_DragonHat(-1000),
    TamingMob_DragonPendant(-1001),
    TamingMob_DragonWingAccessory(-1002),
    TamingMob_DragonTailAccessory(-1003),
    TamingMob_Android(-53),
    TamingMob_AndroidHeart(-54),
    TamingMob_Saddle(-24),
    Shoes(-7),
    Shoulder(-51),
    Si_Emblem(-61),
    Shield_OrDualBlade(-10),
    Ring(-12, -13, -15, -16),
    PetEquip,
    Pants(-6),
    Longcoat(-5),
    Coat(-5),
    Glove(-8),
    Cape(-9),
    Cap(-1),
    CashCap,
    Accessary_Pocket(-51),
    Accessary_Face(-2),
    Accessary_Eye(-3),
    Pendant(-59, -17), // -55 = pendant expansion.
    Medal(-49),
    Belt(-50),
    Earring(-4),
    Pet_ItemPounch(-120),
    Pet_MesoMagnet(-119),
    Bits,
    MonsterBook(-55),
    Badge(-56),
    Totem(-5000, -5001, -5002),
    UNKNOWN;

    private final List<Integer> possibleSlotNumber;

    private EquipSlotType(int slot) {
        possibleSlotNumber = new ArrayList<>();
        possibleSlotNumber.add(slot);
    }

    private EquipSlotType(int... slots) {
        possibleSlotNumber = new ArrayList<>();

        for (int i = 0; i < slots.length; i++) {
            possibleSlotNumber.add(slots[i]);
        }
    }

    public List<Integer> getPossibleSlots() {
        return possibleSlotNumber;
    }

    public int getSlot() {
        if (!possibleSlotNumber.isEmpty()) {
            return possibleSlotNumber.get(0);
        }
        throw new RuntimeException("[EquipSlotType] Item slot is unavailable for the EquipSlotType " + this.name());
    }

    /**
     *
     * @param isCashItem
     * @param dst
     * @param itemid
     * @param chr
     * @return 1 = Successful, -1 = Inventory full for unequipping, 0 = Slot check Error
     */
    public final int checkEquipmentSlotNumber(boolean isCashItem, short dst, int itemid, User chr) {
        boolean isEquippingToAndroidCashSlot = false;
        if (isCashItem && dst > -1400 && dst <= -1300) {
            isEquippingToAndroidCashSlot = true;
        }

        if (!isCashItem) {
            // Require special handling
            switch (this) {
                case Shield_OrDualBlade: {
                    Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) Weapon.getSlot());

                    if (GameConstants.isKatara(itemid)) {
                        boolean DualBlade = chr.getSubcategory() == 1;
                        if ((chr.getJob() != 900 && (!GameConstants.isDualBlade(chr.getJob()) && !DualBlade)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                            return 0;
                        }
                    } else if (weapon != null && GameConstants.isTwoHanded(weapon.getItemId()) && !constants.InventoryConstants.isSpecialShield(itemid)) {
                        if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                            return -1;
                        }
                        MapleInventoryManipulator.unequip(chr.getClient(), (short) Weapon.getSlot(), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                    }
                    break;
                }
                case Weapon_TakingBothSlot_Shield: {
                    Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) Shield_OrDualBlade.getSlot());
                    if (shield != null && GameConstants.isTwoHanded(itemid) && !constants.InventoryConstants.isSpecialShield(shield.getItemId())) {
                        short nextFreeSlot = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                        if (nextFreeSlot == -1) {
                            return -1;
                        }
                        MapleInventoryManipulator.unequip(chr.getClient(), (short) Shield_OrDualBlade.getSlot(), nextFreeSlot);
                    }

                    break;
                }
                case Pants: {
                    // Unequip overall if any
                    final Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) Longcoat.getSlot());
                    if (top != null && constants.InventoryConstants.isOverall(top.getItemId())) { // unequipping any overall
                        short nextFreeSlot = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                        if (nextFreeSlot == -1) {
                            return -1;
                        }
                        MapleInventoryManipulator.unequip(chr.getClient(), (short) Longcoat.getSlot(), nextFreeSlot);
                    }

                    break;
                }

                case Longcoat: {
                    final Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) Pants.getSlot());
                    if (bottom != null && constants.InventoryConstants.isOverall(itemid)) {
                        if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                            return -1;
                        }
                        MapleInventoryManipulator.unequip(chr.getClient(), (short) Pants.getSlot(), chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                    }
                    break;
                }
                case TamingMob_MechanicTransistor:
                case TamingMob_MechanicFrame:
                case TamingMob_MechanicLeg:
                case TamingMob_MechanicArm:
                case TamingMob_MechanicEngine:
                    if (!GameConstants.isMechanic(chr.getJob())) {
                        return 0;
                    }
                    break;
                case TamingMob_DragonHat:
                case TamingMob_DragonPendant:
                case TamingMob_DragonWingAccessory:
                case TamingMob_DragonTailAccessory:
                    if (!GameConstants.isEvan(chr.getJob())) {
                        return 0;
                    }
                    break;
                case PetEquip:
                    break;
                case Pendant:
                    if (possibleSlotNumber.size() < 2) {
                        throw new RuntimeException("[EquipSlotType] Expected at least 2 pendant slot! Did you change something? :( ");
                    }
                    if (dst == possibleSlotNumber.get(0)) { // expension slot number
                        final MapleQuestStatus quest_pendant = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
                        if (Long.parseLong(quest_pendant.getCustomData()) < System.currentTimeMillis()) {
                            return 0;
                        }
                    }
                    break;
                case Ring:
                    for (RingSet s : RingSet.values()) {
                        if (s.id.contains(itemid)) {
                            List<Integer> theList = chr.getInventory(MapleInventoryType.EQUIPPED).listIds();
                            if (!s.id.stream().noneMatch((i) -> (theList.contains(i)))) {
                                return 0;
                            }
                        }
                    }
                    break;
                case UNKNOWN:
                    return 0;
            }
        }
        if (this == UNKNOWN) {
            return 0;
        }

        final int baseSlotNumber;
        if (isEquippingToAndroidCashSlot) {
            baseSlotNumber = -1300;
        } else if (isCashItem) {
            baseSlotNumber = -100;
        } else {
            baseSlotNumber = 0;
        }

        return possibleSlotNumber.stream().anyMatch((slotNumber) -> (slotNumber + (baseSlotNumber) == dst)) ? 1 : 0;
    }
}
