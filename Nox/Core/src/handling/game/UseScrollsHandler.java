package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import enums.ItemFlag;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ItemConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import enums.ItemPotentialTierType;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseScrollsHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());
        short slot = iPacket.DecodeShort();
        short dst = iPacket.DecodeShort();
        iPacket.DecodeShort();
        boolean legendary = iPacket.DecodeByte() > 0;

        UseUpgradeScroll(slot, dst, (short) 0, c, c.getPlayer(), 0, legendary);

    }

    public static boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final ClientSocket c, final User chr, final int vegas, final boolean legendarySpirit) {
        boolean whiteScroll = false;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        chr.setScrolledPosition((short) 0);
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        Equip toScroll;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(InventoryType.EQUIPPED).getItem(dst);
        } else {
            toScroll = (Equip) chr.getInventory(InventoryType.EQUIP).getItem(dst);
        }

        final byte oldLevel = toScroll.getLevel();
        final byte oldEnhance = toScroll.getEnhance();
        final ItemPotentialTierType oldPotentialState = toScroll.getPotentialTier();
        final short oldFlag = toScroll.getFlag();
        final short oldSlots = toScroll.getUpgradeSlots();

        Item scroll = chr.getInventory(InventoryType.USE).getItem(slot);
        if (scroll == null) {
            scroll = chr.getInventory(InventoryType.CASH).getItem(slot);
            if (scroll == null) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        }
        if (scroll.getQuantity() <= 0
                || ItemConstants.isPotentialScroll(scroll.getItemId())
                || (GameConstants.isAzwanScroll(scroll.getItemId()) && toScroll.getUpgradeSlots() < MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).get("tuc")) // Check azwan scroll
                ) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }

        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (ii.isCash(toScroll.getItemId()) || toScroll.getEnhance() >= 12) {
                c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) { //not a durability item
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            c.SendPacket(WvsContext.enableActions());
            return false;
        } else if ((!GameConstants.isTablet(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId())) && toScroll.getDurability() >= 0) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        Item wscroll = null;

        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            c.SendPacket(WvsContext.enableActions());
            return false;
        }

        if (whiteScroll) {
            wscroll = chr.getInventory(InventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0: //1h
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !InventoryConstants.isWeapon(toScroll.getItemId())) {
                        c.SendPacket(WvsContext.enableActions());
                        return false;
                    }
                    break;
                case 1: //2h
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !InventoryConstants.isWeapon(toScroll.getItemId())) {
                        c.SendPacket(WvsContext.enableActions());
                        return false;
                    }
                    break;
                case 2: //armor
                    if (GameConstants.isAccessory(toScroll.getItemId()) || InventoryConstants.isWeapon(toScroll.getItemId())) {
                        c.SendPacket(WvsContext.enableActions());
                        return false;
                    }
                    break;
                case 3: //accessory
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || InventoryConstants.isWeapon(toScroll.getItemId())) {
                        c.SendPacket(WvsContext.enableActions());
                        return false;
                    }
                    break;
            }
        } else if (!GameConstants.isAccessoryScroll(scroll.getItemId())
                && !GameConstants.isChaosScroll(scroll.getItemId())
                && !GameConstants.isCleanSlate(scroll.getItemId())
                && !GameConstants.isEquipScroll(scroll.getItemId())
                && !GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }

        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1003, chr.getJob()))) <= 0) {
                c.SendPacket(WvsContext.enableActions());
                return false;
            }
        }

        // Scroll Success/ Failure/ Curse
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        Equip.ScrollResult scrollSuccess = Equip.ScrollResult.FAIL;
        if (scrolled == null) {
            if (ItemFlag.SHIELD_WARD.check(oldFlag)) {
                scrolled = toScroll;
                scrollSuccess = Equip.ScrollResult.FAIL;
                scrolled.setFlag((short) (oldFlag - ItemFlag.SHIELD_WARD.getValue()));
            } else {
                scrollSuccess = Equip.ScrollResult.CURSE;
            }
            //   } else if ((scroll.getItemId() / 100 == 20497 && scrolled.getState() == 1) || scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState || scrolled.getFlag() > oldFlag) {
            //      scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if ((GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots)) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        }
        MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        } else if (scrollSuccess == Equip.ScrollResult.FAIL && scrolled.getUpgradeSlots() < oldSlots && c.getPlayer().getInventory(InventoryType.CASH).findById(5640000) != null) {
            chr.setScrolledPosition(scrolled.getPosition());
            if (vegas == 0) {
                c.SendPacket(WvsContext.pamSongUI());
            }
        }

        List<ModifyInventory> modifications = new ArrayList<>();
        if (scrollSuccess == Equip.ScrollResult.CURSE) {
            modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, toScroll));
            if (dst < 0) {
                chr.getInventory(InventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(InventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        }
        modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, scrolled));
        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, scrolled));

        c.SendPacket(WvsContext.inventoryOperation(true, modifications));
        chr.getMap().broadcastPacket(chr, CField.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
        c.SendPacket(CField.enchantResult(scrollSuccess == Equip.ScrollResult.SUCCESS ? 1 : scrollSuccess == Equip.ScrollResult.CURSE ? 2 : 0));
        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }
        return true;
    }
}


/*   if (scroll.getItemId() == 5064200) { //TODO: test this
            Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(toScroll.getPosition());
            Equip equip = (Equip) item;
            int itemid = toScroll.getItemId();
            int potential1 = equip.getPotential1();
            int potential2 = equip.getPotential2();
            int potential3 = equip.getPotential3();
            int bonuspotential1 = equip.getBonusPotential1();
            int bonuspotential2 = equip.getBonusPotential2();
            short position = toScroll.getPosition();
            
            chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            Equip neweq = (Equip) ii.getEquipById(itemid);
            neweq.setPotential1(potential1);
            neweq.setPotential2(potential2);
            neweq.setPotential3(potential3);
            neweq.setBonusPotential1(bonuspotential1);
            neweq.setBonusPotential2(bonuspotential2);
            neweq.setPosition(position);
            MapleInventoryManipulator.addbyItem(c, neweq);
        }*/
