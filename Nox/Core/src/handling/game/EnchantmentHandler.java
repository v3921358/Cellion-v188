package handling.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import client.ClientSocket;
import client.inventory.Enchant;
import client.inventory.EnchantmentActions;
import client.inventory.EnchantmentScroll;
import client.inventory.EnchantmentStats;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class EnchantmentHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    /**
     * This handles the enchantment system for items
     *
     * @see handling.MaplePacketHandler#handlePancket(tools.data.input.InPacket, client.Client)
     */
    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        byte type = iPacket.DecodeByte();
        Enchant enchant = new Enchant(type);
        short pos = 0;
        byte bonus = 0;
        List<EnchantmentScroll> scrolls = new ArrayList<>();
        EnchantmentScroll scroll = null;
        switch (enchant.getAction()) {
            case SCROLL_UPGRADE:
                chr.updateTick(iPacket.DecodeInt());
                pos = iPacket.DecodeShort();
                loadScrolls(scrolls); //hard-coded for now
                enchant.setScrolls(scrolls);
                scroll = scrolls.get(iPacket.DecodeInt());
                if (scroll == null) {
                    chr.write(WvsContext.enableActions());
                    return;
                }
                enchant.setAction(EnchantmentActions.SCROLL_RESULT);
                break;
            case HYPER_UPGRADE:
                chr.updateTick(iPacket.DecodeInt());
                pos = iPacket.DecodeShort();
                bonus = (byte) ((iPacket.DecodeByte() != 0) ? 12 : 0); //I do not know the real bonus value
                if (bonus > 1) {
                    iPacket.DecodeInt(); //unsigned int max gets returned no matter what
                }
                iPacket.DecodeInt();//This has to do with like the above (scroll position)
                iPacket.DecodeInt();//-1
                enchant.setAction(EnchantmentActions.STARFORCE_RESULT);
                break;
            case FEVER_TIME:
                //No more data coming from client
                break;
            case MINI_GAME:
                //No more data coming from client
                break;
            case TRANSFER_HAMMER:
                chr.updateTick(iPacket.DecodeInt());
                pos = iPacket.DecodeShort();
                enchant.setAction(EnchantmentActions.TRANSFER_HAMMER_RESULT);
                break;
            default:
                pos = (short) iPacket.DecodeInt();
                break;
        }
        Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(pos);
        if (equip == null && enchant.getAction() != EnchantmentActions.TRANSFER_HAMMER_RESULT) {
            equip = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(pos);
        }
        if (equip != null) {
            Equip copy = (Equip) equip.copy();
            enchant.setOldEquip(copy);
            Random r = new Random();
            int chance = r.nextInt(99);

            switch (enchant.getAction()) {
                case SCROLLLIST:
                    if (equip.getUpgradeSlots() < 1) {
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    }
                    loadScrolls(scrolls); //hard-coded for now
                    enchant.setScrolls(scrolls);
                    break;
                case SCROLL_RESULT:
                    if (equip.getUpgradeSlots() < 1) {
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    }
                    int cost = scroll.getCost();
                    if (chr.haveItem(4001832, cost)) {
                        chr.removeItem(4001832, cost);
                    } else {
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    }
                    int probability = 0;
                    switch (scroll.getType()) {
                        case 0:
                            probability = 99;
                            break;
                        case 1:
                            probability = 70;
                            break;
                        case 2:
                            probability = 30;
                            break;
                        case 3:
                            probability = 15;
                            break;
                    }
                    if (chance <= probability) {
                        for (Entry<EnchantmentStats, Integer> stat : scroll.getStats().entrySet()) {
                            enchant.scroll(equip, stat.getKey(), stat.getValue());
                        }
                        enchant.setPassed(1);
                        equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() - 1));
                        equip.setLevel((byte) (equip.getLevel() + 1));
                    } else {
                        enchant.setPassed(0);
                        equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() - 1));
                    }
                    chr.forceUpdateItem(equip);
                    enchant.setNewEquip(equip);
                    break;
                case STARFORCE:
                    enchant.setRates(equip);
                    enchant.canUpgrade(chr, equip);
                    enchant.setLevel(equip.getLevel());
                    enchant.setSeed(r.nextInt());
                    if (equip.getEnchantFail() > 1) {
                        enchant.setChanceTime(true);
                    }
                    break;
                case STARFORCE_RESULT:
                    enchant.setRates(equip);
                    if (chr.getMeso() < enchant.getCost()) {
                        chr.write(WvsContext.enableActions());
                        return;
                    } else {
                        chr.gainMeso(-enchant.getCost(), false);
                    }
                    if (chance <= bonus + (enchant.getPerMille() / 10) || equip.getEnchantFail() > 1) {
                        equip.setEnchantFail(0);
                        enchant.setPassed(1);
                        enchant.canUpgrade(chr, equip);
                        for (Entry<EnchantmentStats, Short> stat : enchant.getStarForce().entrySet()) {
                            enchant.scroll(equip, stat.getKey(), stat.getValue());
                        }
                        equip.setEnhance((byte) (equip.getEnhance() + 1));
                    } else {
                        enchant.setPassed(3);
                        if (enchant.canDowngrade()) {
                            enchant.setPassed(0);
                            equip.setEnhance((byte) (equip.getEnhance() - 1));
                            equip.setEnchantFail(equip.getEnchantFail() + 1);
                            enchant.canUpgrade(chr, equip);
                            for (Entry<EnchantmentStats, Short> stat : enchant.getStarForce().entrySet()) {
                                enchant.scroll(equip, stat.getKey(), -stat.getValue());
                            }
                            if (enchant.getDestroyChance() > 0) {
                                if (chance <= (enchant.getDestroyChance() / 10)) {
                                    equip.setSpellTrace((short) 136);
                                    enchant.setPassed(2);
                                    int enhance = equip.getEnhance();
                                    for (int i = 0; i < enhance; i++) {
                                        enchant.canUpgrade(chr, equip);
                                        for (Entry<EnchantmentStats, Short> stat : enchant.getStarForce().entrySet()) {
                                            enchant.scroll(equip, stat.getKey(), -stat.getValue());
                                        }
                                        equip.setEnhance((byte) (equip.getEnhance() - 1));
                                    }
                                }
                            }
                        }
                    }
                    chr.forceUpdateItem(equip);
                    enchant.setNewEquip(equip);
                    break;
                case VESTIGE_COMPENSATION_RESULT:
                    break;
                case TRANSFER_HAMMER_RESULT:
                    short tracePos = iPacket.DecodeShort();
                    Equip trace = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(tracePos);
                    if (trace == null) {
                        trace = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(tracePos);
                    }
                    trace.setSpellTrace((short) 0);
                    //trace.getStats().remove(EquipStat.SPELL_TRACE);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, pos, (short) 1, false);
                    trace.setPosition(pos);
                    MapleInventoryManipulator.move(c, MapleInventoryType.EQUIP, tracePos, pos);
                    chr.forceUpdateItem(trace);
                    enchant.setNewEquip(trace);
                    break;
                case UNKOWN_FAILURE_RESULT:
                    break;
                default:
                    break;
            }
        }
        //chr.fakeRelog2();
        c.SendPacket(WvsContext.enchantmentSystem(enchant));
    }

    /*
	 * cost = level / 10 + x (for 100%) level <= 70, x = 1 if the scrollType > 0 
	 * 
	 * 
	 * 100% scroll
	 * 80 : 14
	 * 120 : 95, 80
	 **/
    public void loadScrolls(List<EnchantmentScroll> scrolls) { //for now
        EnchantmentScroll s = new EnchantmentScroll();
        s.setName("100% STR Scroll");
        s.getStats().put(EnchantmentStats.STR, 1);
        s.getStats().put(EnchantmentStats.MHP, 5);
        s.getStats().put(EnchantmentStats.PDD, 1);
        s.getStats().put(EnchantmentStats.MDD, 1);
        s.setCost(5);
        s.setType(0);
        s.willPass(true);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("70% STR Scroll");
        s.getStats().put(EnchantmentStats.STR, 2);
        s.getStats().put(EnchantmentStats.MHP, 15);
        s.getStats().put(EnchantmentStats.PDD, 2);
        s.getStats().put(EnchantmentStats.MDD, 2);
        s.setCost(6);
        s.setType(1);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("30% STR Scroll");
        s.getStats().put(EnchantmentStats.STR, 3);
        s.getStats().put(EnchantmentStats.MHP, 30);
        s.getStats().put(EnchantmentStats.PDD, 4);
        s.getStats().put(EnchantmentStats.MDD, 4);
        s.setCost(7);
        s.setType(2);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("100% INT Scroll");
        s.getStats().put(EnchantmentStats.INT, 1);
        s.getStats().put(EnchantmentStats.MHP, 5);
        s.getStats().put(EnchantmentStats.PDD, 1);
        s.getStats().put(EnchantmentStats.MDD, 1);
        s.setCost(5);
        s.setType(0);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("70% INT Scroll");
        s.getStats().put(EnchantmentStats.INT, 2);
        s.getStats().put(EnchantmentStats.MHP, 15);
        s.getStats().put(EnchantmentStats.PDD, 2);
        s.getStats().put(EnchantmentStats.MDD, 2);
        s.setCost(6);
        s.setType(1);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("30% INT Scroll");
        s.getStats().put(EnchantmentStats.INT, 3);
        s.getStats().put(EnchantmentStats.MHP, 30);
        s.getStats().put(EnchantmentStats.PDD, 4);
        s.getStats().put(EnchantmentStats.MDD, 4);
        s.setCost(7);
        s.setType(2);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("100% DEX Scroll");
        s.getStats().put(EnchantmentStats.DEX, 1);
        s.getStats().put(EnchantmentStats.MHP, 5);
        s.getStats().put(EnchantmentStats.PDD, 1);
        s.getStats().put(EnchantmentStats.MDD, 1);
        s.setCost(5);
        s.setType(0);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("70% DEX Scroll");
        s.getStats().put(EnchantmentStats.DEX, 2);
        s.getStats().put(EnchantmentStats.MHP, 15);
        s.getStats().put(EnchantmentStats.PDD, 2);
        s.getStats().put(EnchantmentStats.MDD, 2);
        s.setCost(6);
        s.setType(1);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("30% DEX Scroll");
        s.getStats().put(EnchantmentStats.DEX, 3);
        s.getStats().put(EnchantmentStats.MHP, 30);
        s.getStats().put(EnchantmentStats.PDD, 4);
        s.getStats().put(EnchantmentStats.MDD, 4);
        s.setCost(7);
        s.setType(2);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("100% LUK Scroll");
        s.getStats().put(EnchantmentStats.LUK, 1);
        s.getStats().put(EnchantmentStats.MHP, 5);
        s.getStats().put(EnchantmentStats.PDD, 1);
        s.getStats().put(EnchantmentStats.MDD, 1);
        s.setCost(5);
        s.setType(0);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("70% LUK Scroll");
        s.getStats().put(EnchantmentStats.LUK, 2);
        s.getStats().put(EnchantmentStats.MHP, 15);
        s.getStats().put(EnchantmentStats.PDD, 2);
        s.getStats().put(EnchantmentStats.MDD, 2);
        s.setCost(6);
        s.setType(1);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("30% LUK Scroll");
        s.getStats().put(EnchantmentStats.LUK, 3);
        s.getStats().put(EnchantmentStats.MHP, 30);
        s.getStats().put(EnchantmentStats.PDD, 4);
        s.getStats().put(EnchantmentStats.MDD, 4);
        s.setCost(7);
        s.setType(2);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("100% HP Scroll");
        s.getStats().put(EnchantmentStats.MHP, 55);
        s.getStats().put(EnchantmentStats.PDD, 1);
        s.getStats().put(EnchantmentStats.MDD, 1);
        s.setCost(5);
        s.setType(0);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("70% HP Scroll");
        s.getStats().put(EnchantmentStats.MHP, 115);
        s.getStats().put(EnchantmentStats.PDD, 2);
        s.getStats().put(EnchantmentStats.MDD, 2);
        s.setCost(6);
        s.setType(1);
        scrolls.add(s);
        s = new EnchantmentScroll();
        s.setName("30% HP Scroll");
        s.getStats().put(EnchantmentStats.MHP, 180);
        s.getStats().put(EnchantmentStats.PDD, 4);
        s.getStats().put(EnchantmentStats.MDD, 4);
        s.setCost(7);
        s.setType(2);
        scrolls.add(s);
    }
}
