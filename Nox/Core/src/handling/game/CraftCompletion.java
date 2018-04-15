package handling.game;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import client.MapleClient;
import client.MapleTrait;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.InventoryConstants;
import handling.world.ItemMakerHandler.CraftRanking;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.objects.User;
import server.maps.objects.MapleExtractor;
import server.quest.MapleQuest;
import tools.Triple;
import net.InPacket;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import net.ProcessPacket;

public final class CraftCompletion implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        final int craftID = iPacket.DecodeInt();
        final SkillFactory.CraftingEntry ce = SkillFactory.getCraft(craftID);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((chr.getMapId() != 910001000 && (craftID != 92049000 || chr.getMap().getAllMapObjectSize(MapleMapObjectType.EXTRACTOR) <= 0)) || ce == null || chr.getFatigue() >= 200) {
            return;
        }
        final int theLevl = c.getPlayer().getProfessionLevel((craftID / 10000) * 10000);
        if (theLevl <= 0 && craftID != 92049000) {
            return;
        }
        int toGet = 0, expGain = 0, fatigue = 0;
        short quantity = 1;
        CraftRanking cr = CraftRanking.GOOD;

        switch (craftID) {
            case 92049000: {
                //disassembling
                final int extractorId = iPacket.DecodeInt();
                final int itemId = iPacket.DecodeInt();
                final long invId = iPacket.DecodeLong();
                final int reqLevel = ii.getReqLevel(itemId);
                final Item item = chr.getInventory(MapleInventoryType.EQUIP).findByInventoryId(invId, itemId);
                if (item == null || chr.getInventory(MapleInventoryType.ETC).isFull()) {
                    return;
                }
                if (extractorId <= 0 && (theLevl == 0 || theLevl < (reqLevel > 130 ? 6 : ((reqLevel - 30) / 20)))) {
                    return;
                } else if (extractorId > 0) {
                    final User extract = chr.getMap().getCharacterById(extractorId);
                    if (extract == null || extract.getExtractor() == null) {
                        return;
                    }
                    final MapleExtractor extractor = extract.getExtractor();
                    if (extractor.owner != chr.getId()) { //fee
                        if (chr.getMeso() < extractor.fee) {
                            return;
                        }
                        final MapleStatEffect eff = ii.getItemEffect(extractor.itemId);
                        if (eff != null && eff.getUseLevel() < reqLevel) {
                            return;
                        }
                        chr.gainMeso(-extractor.fee, true);
                        final User owner = chr.getMap().getCharacterById(extractor.owner);
                        if (owner != null && owner.getMeso() < (Integer.MAX_VALUE - extractor.fee)) {
                            owner.gainMeso(extractor.fee, false);
                        }
                    }
                }
                toGet = 4031016;
                quantity = (short) Randomizer.rand(3, InventoryConstants.isWeapon(itemId) || InventoryConstants.isOverall(itemId) ? 11 : 7);
                if (reqLevel <= 60) {
                    toGet = 4021013;
                } else if (reqLevel <= 90) {
                    toGet = 4021014;
                } else if (reqLevel <= 120) {
                    toGet = 4021015;
                }
                if (quantity <= 5) {
                    cr = CraftRanking.SOSO;
                }
                if (Randomizer.nextInt(5) == 0 && toGet != 4031016) {
                    toGet++;
                    quantity = 1;
                    cr = CraftRanking.COOL;
                }
                fatigue = 3;
                MapleInventoryManipulator.addById(c, toGet, quantity, "Made by disassemble " + itemId + " on " + LocalDateTime.now());
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item.getPosition(), (byte) 1, false);
                break;
            }
            case 92049001: {
                //fusing.
                final int itemId = iPacket.DecodeInt();
                final long invId1 = iPacket.DecodeLong();
                final long invId2 = iPacket.DecodeLong();
                final int reqLevel = ii.getReqLevel(itemId);
                Equip item1 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId1, itemId);
                Equip item2 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId2, itemId);
                for (short i = 0; i < chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit(); i++) {
                    Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem(i);
                    if (item != null && item.getItemId() == itemId && item != item1 && item != item2) {
                        if (item1 == null) {
                            item1 = (Equip) item;
                        } else if (item2 == null) {
                            item2 = (Equip) item;
                            break;
                        }

                    }
                }
                if (item1 == null || item2 == null) {
                    return;
                }
                if (theLevl < (reqLevel > 130 ? 6 : ((reqLevel - 30) / 20))) {
                    return;
                }
                int potentialState = 17, potentialChance = (theLevl * 2);
                /*  if (item1.getState() > 0 && item2.getState() > 0) {
                        potentialChance = 100;
                    } else if (item1.getState() > 0 || item2.getState() > 0) {
                        potentialChance *= 2;
                    }       if (item1.getState() == item2.getState() && item1.getState() > 17) {
                        potentialState = item1.getState();
                    }       */

                //use average stats if scrolled.
                Equip newEquip = ii.fuse(item1.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item1, item2.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item2);
                final int newStat = ii.getTotalStat(newEquip);
                if (newStat > ii.getTotalStat(item1) || newStat > ii.getTotalStat(item2)) {
                    cr = CraftRanking.COOL;
                } else if (newStat < ii.getTotalStat(item1) || newStat < ii.getTotalStat(item2)) {
                    cr = CraftRanking.SOSO;
                }
                if (Randomizer.nextInt(100) < (newEquip.getUpgradeSlots() > 0 || potentialChance >= 100 ? potentialChance : (potentialChance / 2))) {
                    //     newEquip.resetPotential_Fuse(theLevl > 5, potentialState);
                }
                newEquip.setFlag((short) ItemFlag.CRAFTED.getValue());
                newEquip.setOwner(chr.getName());
                toGet = newEquip.getItemId();
                expGain = (60 - ((theLevl - 1) * 2)) * 2;
                fatigue = 3;
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item1.getPosition(), (byte) 1, false);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item2.getPosition(), (byte) 1, false);
                MapleInventoryManipulator.addbyItem(c, newEquip);
                break;
            }
            default:
                if (ce.needOpenItem && chr.getSkillLevel(craftID) <= 0) {
                    return;
                }
                for (Map.Entry<Integer, Integer> e : ce.reqItems.entrySet()) {
                    if (!chr.haveItem(e.getKey(), e.getValue())) {
                        return;
                    }
                }
                for (Triple<Integer, Integer, Integer> i : ce.targetItems) {
                    if (!MapleInventoryManipulator.checkSpace(c, i.left, i.mid, "")) {
                        return;
                    }
                }
                for (Map.Entry<Integer, Integer> e : ce.reqItems.entrySet()) {
                    MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(e.getKey()), e.getKey(), e.getValue(), false, false);
                }
                if (Randomizer.nextInt(100) < (100 - (ce.reqSkillLevel - theLevl) * 20) || (craftID / 10000 <= 9201)) {
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    while (true) {
                        boolean passed = false;
                        for (Triple<Integer, Integer, Integer> i : ce.targetItems) {
                            if (Randomizer.nextInt(100) < i.right) {
                                toGet = i.left;
                                quantity = i.mid.shortValue();
                                Item receive;
                                if (GameConstants.getInventoryType(toGet) == MapleInventoryType.EQUIP) {
                                    Equip first = (Equip) ii.getEquipById(toGet);
                                    if (Randomizer.nextInt(100) < (theLevl * 2)) {
                                        first = (Equip) ii.randomizeStats(first);
                                        cr = CraftRanking.COOL;
                                    }
                                    if (Randomizer.nextInt(100) < (theLevl * (first.getUpgradeSlots() > 0 ? 2 : 1))) {
                                        //                   first.resetPotential();
                                        cr = CraftRanking.COOL;
                                    }
                                    receive = first;
                                    receive.setFlag((short) ItemFlag.CRAFTED.getValue());
                                } else {
                                    receive = new Item(toGet, (short) 0, quantity, (short) (ItemFlag.CRAFTED_USE.getValue()));
                                }
                                if (ce.period > 0) {
                                    receive.setExpiration(System.currentTimeMillis() + (ce.period * 60000)); //period is in minutes
                                }
                                receive.setOwner(chr.getName());
                                receive.setGMLog("Crafted from " + craftID + " on " + LocalDateTime.now());
                                MapleInventoryManipulator.addFromDrop(c, receive, true);

                                if (ce.needOpenItem) {
                                    byte mLevel = chr.getMasterLevel(craftID);
                                    if (mLevel == 1) {
                                        sa.put(ce, new SkillEntry(0, (byte) 0, SkillFactory.getDefaultSExpiry(ce)));
                                    } else if (mLevel > 1) {
                                        sa.put(ce, new SkillEntry(Integer.MAX_VALUE, (byte) (chr.getMasterLevel(craftID) - 1), SkillFactory.getDefaultSExpiry(ce)));
                                    }
                                }
                                fatigue = ce.incFatigability;
                                expGain = ce.incSkillProficiency == 0 ? (((fatigue * 20) - (ce.reqSkillLevel - theLevl) * 2) * 2) : ce.incSkillProficiency;
                                chr.getTrait(MapleTrait.MapleTraitType.craft).addExp(cr.craft, chr);
                                passed = true;
                                break;
                            }
                        }
                        if (passed) {
                            break;
                        }
                    }
                    chr.changeSkillsLevel(sa);
                } else {
                    quantity = 0;
                    cr = CraftRanking.SOSO;
                }
                break;
        }
        if (expGain > 0 && theLevl < 10) {
            expGain *= chr.getClient().getChannelServer().getTraitRate();
            if (Randomizer.nextInt(100) < chr.getTrait(MapleTrait.MapleTraitType.craft).getLevel() / 5) {
                expGain *= 2;
            }
            String s = "Alchemy";
            switch (craftID / 10000) {
                case 9200:
                    s = "Herbalism";
                    break;
                case 9201:
                    s = "Mining";
                    break;
                case 9202:
                    s = "Smithing";
                    break;
                case 9203:
                    s = "Accessory Crafting";
                    break;
            }
            chr.dropMessage(-5, s + "'s mastery increased. (+" + expGain + ")");
            if (chr.addProfessionExp((craftID / 10000) * 10000, expGain)) {
                chr.dropMessage(1, "You've accumulated " + s + " mastery. See an NPC in town to level up.");
            }
        } else {
            expGain = 0;
        }
        MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1"); //removes tutorial stuff
        chr.setFatigue((byte) (chr.getFatigue() + fatigue));
        chr.getMap().broadcastMessage(CField.craftFinished(chr.getId(), craftID, cr.i, toGet, quantity, expGain));
    }

}
