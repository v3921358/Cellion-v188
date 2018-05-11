package handling.game;

import client.Client;
import client.QuestStatus;
import client.QuestStatus.QuestState;
import client.MapleStat;
import client.PlayerRandomStream;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import java.time.LocalDateTime;
import java.util.List;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.quest.Quest;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;
import server.NebuliteGrade;
import server.potentials.ItemPotentialOption;
import server.potentials.ItemPotentialProvider;
import server.skills.VCore;
import server.skills.VMatrixRecord;

/**
 *
 * @author
 */
public class UseScriptedNPCItemHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        long expiration_days = 0;
        int mountid = 0;
        //int npc = MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("npc").intValue();
        //String script = MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("script").toString();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int npc = 9010000; // for now
        String script = "consume_" + itemId; // for now

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory() && !chr.inPVP()) {
            switch (toUse.getItemId()) {
                case 2431789:
                case 2431790: {
                    NPCScriptManager.getInstance().start(c, 2080008, null);
                    break;
                }
                case 2430007: { // Blank Compass
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1, "Scripted item: " + itemId + " on " + LocalDateTime.now()); // Gold Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1, "Scripted item: " + itemId + " on " + LocalDateTime.now()); // Blank Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001, null);
                    break;
                }
                case 2431935: {
                    Integer[] itemArray = {2290000,
                        2290002, 2290004, 2290006, 2290008, 2290010, 2290012, 2290014, 2290016, 2290018, 2290019, 2290020, 2290022, 2290024, 2290026, 2290028,
                        2290030, 2290032, 2290034, 2290036, 2290038, 2290040, 2290042, 2290044, 2290046, 2290048, 2290050, 2290052, 2290054, 2290056, 2290058, 2290060,
                        2290062, 2290064, 2290066, 2290068, 2290070, 2290072, 2290074, 2290076, 2290078, 2290080, 2290082, 2290084, 2290086, 2290088,
                        2290090, 2290092, 2290094, 2290096, 2290097, 2290099, 2290101, 2290102, 2290104, 2290106, 2291022, 2291013, 2291011, 2291010, 2291008, 2291007,
                        2291005, 2291003, 2291002, 2291000, 2290998, 2290995, 2290996, 2290993, 2290979, 2290977, 2290975, 2290973, 2290971, 2290969, 2290967,
                        2290966, 2290964, 2290962, 2290960, 2290959, 2290957, 2290955, 2290953, 2290951, 2290949, 2290947, 2290945, 2290943, 2290941,
                        2290939, 2290928, 2290798, 2290808, 2290809, 2290810, 2290811, 2290812, 2290813, 2290814, 2290815, 2290816, 2290817, 2290818,
                        2290819, 2290820, 2290821, 2290822, 2290823, 2290824, 2290825, 2290826, 2290827, 2290828, 2290829, 2290830, 2290831, 2290832,
                        2290833, 2290834, 2290835, 2290836, 2290837, 2290838, 2290839, 2290840, 2290841, 2290842, 2290843, 2290844, 2290845, 2290846,
                        2290847, 2290848, 2290849, 2290850, 2290851, 2290852, 2290853, 2290854, 2290855, 2290856, 2290857, 2290858, 2290859, 2290860,
                        2290861, 2290862, 2290863, 2290864, 2290865, 2290866, 2290867, 2290794, 2290795, 2290796, 2290791, 2290792, 2290789, 2290787,
                        2290777, 2290775, 2290773, 2290771, 2290769, 2290767, 2290765, 2290751, 2290752, 2290753, 2290763, 2290749, 2290747, 2290745,
                        2290743, 2290741, 2290739, 2290734, 2290732, 2290729, 2290730, 2290727, 2290725, 2290706, 2290707, 2290704, 2290689, 2290689,
                        2290651, 2290649, 2290647, 2290645, 2290639, 2290641, 2290637, 2290635, 2290633, 2290631, 2290629, 2290627, 2290624, 2290625,
                        2290622, 2290619, 2290620, 2290617, 2290615, 2290612, 2290613, 2290610, 2290608, 2290599, 2290597, 2290595, 2290593, 2290591,
                        2290580, 2290589, 2290523, 2290521, 2290519, 2290516, 2290517, 2290514, 2290512, 2290446, 2290445, 2290443, 2290441, 2290438, 2290439,
                        2290436, 2290434, 2290432, 2290430, 2290426, 2290427, 2290428, 2290424, 2290422, 2290420, 2290418, 2290416, 2290414, 2290412,
                        2290378, 2290370, 2290366, 2290363, 2290364,
                        2290361, 2290358, 2290359, 2290356, 2290349, 2290354, 2290333, 2290331, 2290328, 2290329, 2290326, 2290324, 2290322, 2290292, 2290290,
                        2290284, 2290281, 2290282, 2290279, 2290277, 2290275, 2290246, 2290244, 2290242, 2290240, 2290238, 2290236, 2290234, 2290232,
                        2290230, 2290228, 2290226, 2290206, 2290204, 2290205, 2290153, 2290154, 2290155, 2290156, 2290151, 2290150, 2290148, 2290146, 2290144, 2290142, 2290140, 2290138, 2290136, 2290134, 2290132, 2290130, 2290128, 2290126, 2290123, 2290124, 2290121, 2290119, 2290117, 2290115, 2290114, 2290112, 2290110};
                    int randomizer = Randomizer.nextInt(itemArray.length);
                    int reward = itemArray[randomizer];
                    if (MapleItemInformationProvider.getInstance().itemExists(reward)) {
                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                        MapleInventoryManipulator.addById(c, reward, (short) 1, "Reward item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                    }
                    break;
                }

                case 2430121: {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300166), c.getPlayer().getPosition());
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    break;
                }
                case 2430112: //miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 25, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049400, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049401, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430481: //super miracle cube fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 30) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049701, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 30, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049701, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430481) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049300, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049300, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Advanced Equip Enhancement Scroll, 30 for Epic Potential Scroll 80%.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430691: // nebulite diffuser fragment
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430691) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 5750001, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 5750001, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Nebulite Diffuser.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430748: // premium fusion ticket 
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430748) >= 20) {
                            if (MapleInventoryManipulator.checkSpace(c, 4420000, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 20, true, false)) {
                                MapleInventoryManipulator.addById(c, 4420000, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 20 Fragments for a Premium Fusion Ticket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430692: // nebulite box
                    if (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430692) >= 1) {
                            NebuliteGrade nebuliteGrade;
                            final int random = Randomizer.nextInt(100);

                            if (random < 10) {
                                nebuliteGrade = NebuliteGrade.GradeB;
                            } else if (random < 30) {
                                nebuliteGrade = NebuliteGrade.GradeC;
                            } else {
                                nebuliteGrade = NebuliteGrade.GradeD;
                            }
                            ItemPotentialOption potential = ItemPotentialProvider.getRandomNebulitePotential(nebuliteGrade);

                            if (potential != null) {
                                final int newId = potential.getOptionId();

                                if (MapleInventoryManipulator.checkSpace(c, newId, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                    MapleInventoryManipulator.addById(c, newId, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                                    c.SendPacket(CWvsContext.InfoPacket.getShowItemGain(newId, (short) 1, true));
                                } else {
                                    c.getPlayer().dropMessage(5, "Please make some space.");
                                }
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "You do not have a Nebulite Box.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 5680019: {//starling hair 
                    //if (c.getPlayer().getGender() == 1) {
                    int hair = 32150 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 5680020: {//starling hair 
                    //if (c.getPlayer().getGender() == 0) {
                    int hair = 32160 + (c.getPlayer().getHair() % 10);
                    c.getPlayer().setHair(hair);
                    c.getPlayer().updateSingleStat(MapleStat.HAIR, hair);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (byte) 1, false);
                    //}
                    break;
                }
                case 3994225:
                    c.getPlayer().dropMessage(5, "Please bring this item to the NPC.");
                    break;
                case 2430212: //energy drink
                    QuestStatus marr = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    long lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 5);
                    }
                    break;
                case 2430213: //energy drink
                    marr = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 10);
                    }
                    break;
                case 2430220: //energy drink
                case 2430214: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 30);
                    }
                    break;
                case 2430227: //energy drink
                    if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 50);
                    }
                    break;
                case 2430231: //energy drink
                    marr = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.ENERGY_DRINK));
                    if (marr.getCustomData() == null) {
                        marr.setCustomData("0");
                    }
                    lastTime = Long.parseLong(marr.getCustomData());
                    if (lastTime + (600000) > System.currentTimeMillis()) {
                        c.getPlayer().dropMessage(5, "You can only use one energy drink per 10 minutes.");
                    } else if (c.getPlayer().getFatigue() > 0) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().setFatigue(c.getPlayer().getFatigue() - 40);
                    }
                    break;
                case 2430144: //smb
                    final int itemid = Randomizer.nextInt(999) + 2290000;
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430370:
                    if (MapleInventoryManipulator.checkSpace(c, 2028062, (short) 1, "")) {
                        MapleInventoryManipulator.addById(c, 2028062, (short) 1, "Reward item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                case 2430158: //lion king
                    if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 100) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310010, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310010, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000630) >= 50) {
                            if (MapleInventoryManipulator.checkSpace(c, 4310009, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4310009, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "The corrupted power of the medal is too strong. To purify the medal, you need at least #r50#k #bPurification Totems#k.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430159:
                    Quest.getInstance(3182).forceComplete(c.getPlayer(), 2161004);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    break;
                case 2430200: //thunder stone
                    if (c.getPlayer().getQuestStatus(31152) != QuestState.Completed) {
                        c.getPlayer().dropMessage(5, "You have no idea how to use it.");
                    } else if (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000660) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000661) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000662) >= 1 && c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4000663) >= 1) {
                            if (MapleInventoryManipulator.checkSpace(c, 4032923, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000660, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000661, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000662, 1, true, false) && MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4000663, 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 4032923, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 1 of each Stone for a Dream Key.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430130:
                    if (GameConstants.isResistance(c.getPlayer().getJob())) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        c.getPlayer().gainExp((int) (20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate(c.getPlayer().getWorld()))), true, true, false);
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use this item.");
                    }
                    break;
                case 2430131: //energy charge
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    c.getPlayer().gainExp((int) (20000 + (c.getPlayer().getLevel() * 50 * c.getChannelServer().getExpRate(c.getPlayer().getWorld()))), true, true, false);
                    break;
                case 2430132:
                case 2430134: //resistance box
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getJob() == 3200 || c.getPlayer().getJob() == 3210 || c.getPlayer().getJob() == 3211 || c.getPlayer().getJob() == 3212) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1382101, (short) 1, "Scripted item: " + itemId + " on " + LocalDateTime.now());
                        } else if (c.getPlayer().getJob() == 3300 || c.getPlayer().getJob() == 3310 || c.getPlayer().getJob() == 3311 || c.getPlayer().getJob() == 3312) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1462093, (short) 1, "Scripted item: " + itemId + " on " + LocalDateTime.now());
                        } else if (c.getPlayer().getJob() == 3500 || c.getPlayer().getJob() == 3510 || c.getPlayer().getJob() == 3511 || c.getPlayer().getJob() == 3512) {
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                            MapleInventoryManipulator.addById(c, 1492080, (short) 1, "Scripted item: " + itemId + " on " + LocalDateTime.now());
                        } else {
                            c.getPlayer().dropMessage(5, "You may not use this item.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Make some space.");
                    }
                    break;
                case 2430182:
                    //TODO:Fix it
                    break;
                case 2430218:
                case 2430230:
                case 2430473:
                case 2430479:
                case 2430632:
                case 2430697:
                case 2430979:
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) - c.getPlayer().getExp(), true, true, false);
                    break;
                case 2430036: //croco 1 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430170: //croco 7 day
                    mountid = 1027;
                    expiration_days = 7;
                    break;
                case 2430037: //black scooter 1 day
                    mountid = 1028;
                    expiration_days = 1;
                    break;
                case 2430038: //pink scooter 1 day
                    mountid = 1029;
                    expiration_days = 1;
                    break;
                case 2430039: //clouds 1 day
                    mountid = 1030;
                    expiration_days = 1;
                    break;
                case 2430040: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 1;
                    break;
                case 2430223: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 15;
                    break;
                case 2430259: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 3;
                    break;
                case 2430242: //motorcycle
                    mountid = 80001018;
                    expiration_days = 10;
                    break;
                case 2430243: //power suit
                    mountid = 80001019;
                    expiration_days = 10;
                    break;
                case 2430261: //power suit
                    mountid = 80001019;
                    expiration_days = 3;
                    break;
                case 2430249: //motorcycle
                    mountid = 80001027;
                    expiration_days = 3;
                    break;
                case 2430225: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 10;
                    break;
                case 2430053: //croco 30 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430054: //black scooter 30 day
                    mountid = 1028;
                    expiration_days = 30;
                    break;
                case 2430055: //pink scooter 30 day
                    mountid = 1029;
                    expiration_days = 30;
                    break;
                case 2430257: //pink
                    mountid = 1029;
                    expiration_days = 7;
                    break;
                case 2430056: //mist rog 30 day
                    mountid = 1035;
                    expiration_days = 30;
                    break;
                case 2430057:
                    mountid = 1033;
                    expiration_days = 30;
                    break;
                case 2430072: //ZD tiger 7 day
                    mountid = 1034;
                    expiration_days = 7;
                    break;
                case 2430073: //lion 15 day
                    mountid = 1036;
                    expiration_days = 15;
                    break;
                case 2430074: //unicorn 15 day
                    mountid = 1037;
                    expiration_days = 15;
                    break;
                case 2430272: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 3;
                    break;
                case 2430275: //spiegelmann
                    mountid = 80001033;
                    expiration_days = 7;
                    break;
                case 2430075: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 15;
                    break;
                case 2430076: //red truck 15 day
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430077: //gargoyle 15 day
                    mountid = 1040;
                    expiration_days = 15;
                    break;
                case 2430080: //shinjo 20 day
                    mountid = 1042;
                    expiration_days = 20;
                    break;
                case 2430082: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 7;
                    break;
                case 2430260: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 3;
                    break;
                case 2430091: //nightmare 10 day
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430092: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 10;
                    break;
                case 2430263: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 3;
                    break;
                case 2430093: //ostrich 10 day
                    mountid = 1051;
                    expiration_days = 10;
                    break;
                case 2430101: //pink bear 10 day
                    mountid = 1052;
                    expiration_days = 10;
                    break;
                case 2430102: //transformation robo 10 day
                    mountid = 1053;
                    expiration_days = 10;
                    break;
                case 2430103: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 30;
                    break;
                case 2430266: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 3;
                    break;
                case 2430265: //chariot
                    mountid = 1151;
                    expiration_days = 3;
                    break;
                case 2430258: //law officer
                    mountid = 1115;
                    expiration_days = 365;
                    break;
                case 2430117: //lion 1 year
                    mountid = 1036;
                    expiration_days = 365;
                    break;
                case 2430118: //red truck 1 year
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430119: //gargoyle 1 year
                    mountid = 1040;
                    expiration_days = 365;
                    break;
                case 2430120: //unicorn 1 year
                    mountid = 1037;
                    expiration_days = 365;
                    break;
                case 2430271: //owl 30 day
                    mountid = 1069;
                    expiration_days = 3;
                    break;
                case 2430136: //owl 30 day
                    mountid = 1069;
                    expiration_days = 30;
                    break;
                case 2430137: //owl 1 year
                    mountid = 1069;
                    expiration_days = 365;
                    break;
                case 2430145: //mothership
                    mountid = 1070;
                    expiration_days = 30;
                    break;
                case 2430146: //mothership
                    mountid = 1070;
                    expiration_days = 365;
                    break;
                case 2430147: //mothership
                    mountid = 1071;
                    expiration_days = 30;
                    break;
                case 2430148: //mothership
                    mountid = 1071;
                    expiration_days = 365;
                    break;
                case 2430135: //os4
                    mountid = 1065;
                    expiration_days = 15;
                    break;
                case 2430149: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 30;
                    break;
                case 2430262: //leonardo 30 day
                    mountid = 1072;
                    expiration_days = 3;
                    break;
                case 2430179: //witch 15 day
                    mountid = 1081;
                    expiration_days = 15;
                    break;
                case 2430264: //witch 15 day
                    mountid = 1081;
                    expiration_days = 3;
                    break;
                case 2430201: //giant bunny 60 day
                    mountid = 1096;
                    expiration_days = 60;
                    break;
                case 2430228: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 60;
                    break;
                case 2430276: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 15;
                    break;
                case 2430277: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 365;
                    break;
                case 2430283: //trojan
                    mountid = 1025;
                    expiration_days = 10;
                    break;
                case 2430291: //hot air
                    mountid = 1145;
                    expiration_days = -1;
                    break;
                case 2430293: //nadeshiko
                    mountid = 1146;
                    expiration_days = -1;
                    break;
                case 2430295: //pegasus
                    mountid = 1147;
                    expiration_days = -1;
                    break;
                case 2430297: //dragon
                    mountid = 1148;
                    expiration_days = -1;
                    break;
                case 2430299: //broom
                    mountid = 1149;
                    expiration_days = -1;
                    break;
                case 2430301: //cloud
                    mountid = 1150;
                    expiration_days = -1;
                    break;
                case 2430303: //chariot
                    mountid = 1151;
                    expiration_days = -1;
                    break;
                case 2430305: //nightmare
                    mountid = 1152;
                    expiration_days = -1;
                    break;
                case 2430307: //rog
                    mountid = 1153;
                    expiration_days = -1;
                    break;
                case 2430309: //mist rog
                    mountid = 1154;
                    expiration_days = -1;
                    break;
                case 2430311: //owl
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430313: //helicopter
                    mountid = 1156;
                    expiration_days = -1;
                    break;
                case 2430315: //pentacle
                    mountid = 1118;
                    expiration_days = -1;
                    break;
                case 2430317: //frog
                    mountid = 1121;
                    expiration_days = -1;
                    break;
                case 2430319: //turtle
                    mountid = 1122;
                    expiration_days = -1;
                    break;
                case 2430321: //buffalo
                    mountid = 1123;
                    expiration_days = -1;
                    break;
                case 2430323: //tank
                    mountid = 1124;
                    expiration_days = -1;
                    break;
                case 2430325: //viking
                    mountid = 1129;
                    expiration_days = -1;
                    break;
                case 2430327: //pachinko
                    mountid = 1130;
                    expiration_days = -1;
                    break;
                case 2430329: //kurenai
                    mountid = 1063;
                    expiration_days = -1;
                    break;
                case 2430331: //horse
                    mountid = 1025;
                    expiration_days = -1;
                    break;
                case 2430333: //tiger
                    mountid = 1034;
                    expiration_days = -1;
                    break;
                case 2430335: //hyena
                    mountid = 1136;
                    expiration_days = -1;
                    break;
                case 2430337: //ostrich
                    mountid = 1051;
                    expiration_days = -1;
                    break;
                case 2430339: //low rider
                    mountid = 1138;
                    expiration_days = -1;
                    break;
                case 2430341: //napoleon
                    mountid = 1139;
                    expiration_days = -1;
                    break;
                case 2430343: //croking
                    mountid = 1027;
                    expiration_days = -1;
                    break;
                case 2430346: //lovely
                    mountid = 1029;
                    expiration_days = -1;
                    break;
                case 2430348: //retro
                    mountid = 1028;
                    expiration_days = -1;
                    break;
                case 2430350: //f1
                    mountid = 1033;
                    expiration_days = -1;
                    break;
                case 2430352: //power suit
                    mountid = 1064;
                    expiration_days = -1;
                    break;
                case 2430354: //giant rabbit
                    mountid = 1096;
                    expiration_days = -1;
                    break;
                case 2430356: //small rabit
                    mountid = 1101;
                    expiration_days = -1;
                    break;
                case 2430358: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = -1;
                    break;
                case 2430360: //chicken
                    mountid = 1054;
                    expiration_days = -1;
                    break;
                case 2430362: //transformer
                    mountid = 1053;
                    expiration_days = -1;
                    break;
                case 2430292: //hot air
                    mountid = 1145;
                    expiration_days = 90;
                    break;
                case 2430294: //nadeshiko
                    mountid = 1146;
                    expiration_days = 90;
                    break;
                case 2430296: //pegasus
                    mountid = 1147;
                    expiration_days = 90;
                    break;
                case 2430298: //dragon
                    mountid = 1148;
                    expiration_days = 90;
                    break;
                case 2430300: //broom
                    mountid = 1149;
                    expiration_days = 90;
                    break;
                case 2430302: //cloud
                    mountid = 1150;
                    expiration_days = 90;
                    break;
                case 2430304: //chariot
                    mountid = 1151;
                    expiration_days = 90;
                    break;
                case 2430306: //nightmare
                    mountid = 1152;
                    expiration_days = 90;
                    break;
                case 2430308: //rog
                    mountid = 1153;
                    expiration_days = 90;
                    break;
                case 2430310: //mist rog
                    mountid = 1154;
                    expiration_days = 90;
                    break;
                case 2430312: //owl
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430314: //helicopter
                    mountid = 1156;
                    expiration_days = 90;
                    break;
                case 2430316: //pentacle
                    mountid = 1118;
                    expiration_days = 90;
                    break;
                case 2430318: //frog
                    mountid = 1121;
                    expiration_days = 90;
                    break;
                case 2430320: //turtle
                    mountid = 1122;
                    expiration_days = 90;
                    break;
                case 2430322: //buffalo
                    mountid = 1123;
                    expiration_days = 90;
                    break;
                case 2430326: //viking
                    mountid = 1129;
                    expiration_days = 90;
                    break;
                case 2430328: //pachinko
                    mountid = 1130;
                    expiration_days = 90;
                    break;
                case 2430330: //kurenai
                    mountid = 1063;
                    expiration_days = 90;
                    break;
                case 2430332: //horse
                    mountid = 1025;
                    expiration_days = 90;
                    break;
                case 2430334: //tiger
                    mountid = 1034;
                    expiration_days = 90;
                    break;
                case 2430336: //hyena
                    mountid = 1136;
                    expiration_days = 90;
                    break;
                case 2430338: //ostrich
                    mountid = 1051;
                    expiration_days = 90;
                    break;
                case 2430340: //low rider
                    mountid = 1138;
                    expiration_days = 90;
                    break;
                case 2430342: //napoleon
                    mountid = 1139;
                    expiration_days = 90;
                    break;
                case 2430344: //croking
                    mountid = 1027;
                    expiration_days = 90;
                    break;
                case 2430347: //lovely
                    mountid = 1029;
                    expiration_days = 90;
                    break;
                case 2430349: //retro
                    mountid = 1028;
                    expiration_days = 90;
                    break;
                case 2430351: //f1
                    mountid = 1033;
                    expiration_days = 90;
                    break;
                case 2430353: //power suit
                    mountid = 1064;
                    expiration_days = 90;
                    break;
                case 2430355: //giant rabbit
                    mountid = 1096;
                    expiration_days = 90;
                    break;
                case 2430357: //small rabit
                    mountid = 1101;
                    expiration_days = 90;
                    break;
                case 2430359: //rabbit rickshaw
                    mountid = 1102;
                    expiration_days = 90;
                    break;
                case 2430361: //chicken
                    mountid = 1054;
                    expiration_days = 90;
                    break;
                case 2430363: //transformer
                    mountid = 1053;
                    expiration_days = 90;
                    break;
                case 2430324: //high way
                    mountid = 1158;
                    expiration_days = -1;
                    break;
                case 2430345: //high way
                    mountid = 1158;
                    expiration_days = 90;
                    break;
                case 2430367: //law off
                    mountid = 1115;
                    expiration_days = 3;
                    break;
                case 2430365: //pony
                    mountid = 1025;
                    expiration_days = 365;
                    break;
                case 2430366: //pony
                    mountid = 1025;
                    expiration_days = 15;
                    break;
                case 2430369: //nightmare
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430392: //speedy
                    mountid = 80001038;
                    expiration_days = 90;
                    break;
                case 2430476: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430477: //red truck? but name is pegasus?
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430232: //fortune
                    mountid = 1106;
                    expiration_days = 10;
                    break;
                case 2430511: //spiegel
                    mountid = 80001033;
                    expiration_days = 15;
                    break;
                case 2430512: //rspiegel
                    mountid = 80001033;
                    expiration_days = 365;
                    break;
                case 2430536: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 365;
                    break;
                case 2430537: //buddy buggy
                    mountid = 80001114;
                    expiration_days = 15;
                    break;
                case 2430229: //bunny rickshaw 60 day
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430199: //santa sled
                    mountid = 1102;
                    expiration_days = 60;
                    break;
                case 2430206: //race
                    mountid = 1089;
                    expiration_days = 7;
                    break;
                case 2430211: //race
                    mountid = 80001009;
                    expiration_days = 30;
                    break;
                case 2430611: {
                    NPCScriptManager.getInstance().start(c, 9010010, "consume_2430611");
                    break;
                }
                case 2430690: {
                    if (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1 && c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
                        if (Randomizer.nextInt(100) < 30) { //30% for Hilla's Pet
                            if (MapleInventoryManipulator.checkSpace(c, 5000217, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 5000217, (short) 1, "", Pet.createPet(5000217, "Blackheart", 1, 0, 100, MapleInventoryIdentifier.getInstance(), 0, (short) 0), 45, false, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(0, "Please make more space");
                            }
                        } else //70% for Hilla's Pet's earrings
                        {
                            if (MapleInventoryManipulator.checkSpace(c, 1802354, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                                MapleInventoryManipulator.addById(c, 1802354, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            } else {
                                c.getPlayer().dropMessage(0, "Please make more space");
                            }
                        }
                    } else {
                        c.getPlayer().dropMessage(0, "Please make more space");
                    }
                    break;
                }
                case 2431855: {//First Explorer Gift Box
                    if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 2 && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 2) {
                        if (MapleInventoryManipulator.checkSpace(c, 1052646, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, toUse.getItemId(), 1, true, false)) {
                            MapleInventoryManipulator.addById(c, 1052646, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            MapleInventoryManipulator.addById(c, 1072850, (short) 1, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            MapleInventoryManipulator.addById(c, 2000013, (short) 50, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            MapleInventoryManipulator.addById(c, 2000014, (short) 50, "Scripted item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                        } else {
                            c.getPlayer().dropMessage(0, "Please make more space");
                        }
                    } else {
                        c.getPlayer().dropMessage(0, "Please make more space");
                    }
                    break;
                }
                case 2432251: {//Kobold Musk
                    Quest.getInstance(59051).forceComplete(c.getPlayer(), 0);
                    NPCScriptManager.getInstance().start(c, 9390315, "BeastTamerQuestLine2");
                    break;
                }

                // Damage Skins
                case 2431965: // base damage Skin: 0
                case 2431966: // digital Sunrise Skin Damage: 1
                case 2432084: // digital Sunrise damage the skin (no)
                case 2431967: // Kritias Skin Damage: 2
                case 2432131: // Party Quest Skin Damage: 3
                case 2432153: // Hard Hitting: 4
                case 2432638: // Creative Impact Damage Skin
                case 2432659: // Creative Impact Damage Skin
                case 2432154: // sweet traditional Han Skin Damage: 5
                case 2432637: // sweet traditional one and damage the skin
                case 2432658: // sweet traditional one and damage the skin
                case 2432207: // Club Henesys' damage Skin: 6
                case 2432354: // Merry Christmas Skin Damage: 7
                case 2432355: // Snow Blossom Skin Damage: 8
                case 2432972: // Snow Blossom Skin Damage
                case 2432465: // damage the skin of Alicia: 9
                case 2432479: // Dorothy skin damage: 10
                case 2432526: // Keyboard Warrior Skin Damage: 11
                case 2432639: // Keyboard Warrior Skin Damage
                case 2432660: // Keyboard Warrior Skin Damage
                case 2432532: // spring breeze rustling skin damage: 12
                case 2432592: // solo troops skin damage: 13
                case 2432640: // Reminiscence skin damage: 14
                case 2432661: // Remy you damage the skin Suns
                case 2432710: // Orange Mushroom Skin Damage: 15
                case 2432836: // crown damage Skin: 16
                case 2432973: // monotone skin damage: 17
                case 2433063: // Star Planet skin: 18
                case 2433178: // Halloween Skin (bones): 20
                case 2433456: // Hangul Skin: 21
                case 2435960: // Fried Chicken Dmg Skin(Unknown ItemID): 22
                case 2433715: // Striped Damage Skin: 23
                case 2433804: // Couples Army Damage Skin: 24
                case 5680343: // Star Damage Skin: 25
                case 2433913: // Yeti and Pepe Damage Skin: 26
                case 2433980: // Slime and Mushroom Damage Skin: 27
                case 2433981: // Pink bean Damage skin: 28
                case 2436229: // Pig Bar Dmg Skin(Unknown ItemID): 29
                // case 2432659: // Hard-Hitting Dmg Skin (already in): 30 - (4)
                // case 2432526: // Keyboard Warrior (already in): 31 - (11)
                // case 2432710: // Orange mushroom Skin Damage(already in): 32 - (15)
                // case 2432355: // Snowflake Dmg Skin(already in): 33 - (8)
                case 2434248: // Rainbow Boom Damage Skin: 34
                case 2433362: // Night Sky Damage Skin: 35
                case 2434274: // Marshmallow Damage Skin: 36
                case 2434289: // Mu Lung Dojo Dmg Skin: 37
                case 2434390: // Teddy Damage Skin: 38
                case 2434391: // Mighty Ursus Damage Skin: 39
                case 5680395: // Scorching Heat Damage Skin: 40
                case 2434528: // USA Damage Skin: 41
                case 2434529: // Churro Damage Skin: 42
                case 2434530: // Singapore Night Damage Skin: 43
                case 2433571: // Scribble Crush Damage Skin: 44
                case 2434574: // Full Moon Damage Skin: 45
                case 2433828: // White Heaven Sun Damage Skin: 46
                case 2432804: // Princess No Damage Skin: 47
                case 2434654: // Murgoth Damage Skin: 48
                case 2435326: // Nine-Tailed Fox Damage Skin: 49
                case 2432749: // Zombie Damage Skin: 50
                case 2434710: // MVP Special Damage Skin: 51
                case 2433777: // Black Heaven Damage Skin: 52
                case 2434824: // Monster Park Damage Skin: 53
                // case 2431966: // Digital Damage Skin(already in): 54 - (1)
                // case 2431967: // Kritias Damage Skin(already in): 55 - (2)
                // case 2432154: // Sweet tea cake Damage Skin(already in): 56 - (5)
                // case 2432354: // Merry Christmas Damage Skin(already in): 57 - (7)
                // case 2432532: // Gentle spring breeze damage skin(already in): 58 - (12)
                // case 2433715: // Striped Damage Skin(already in): 59 - (23)
                // case 2433063: // Star Damage Skin(already in): 60 - (25)
                // case 2433913: // Yeti and Pepe Damage Skin(already in): 61 - (26)
                // case 2433980: // Slime and Mushroom Damage Skin(already in): 62 - (27)
                // case 2434248: // Rainbow Boom Damage Skin(already in): 63 - (34)
                // case 2433362: // Night Sky Damage Skin(already in): 64 - (35)
                // case 2434274: // Marshmallow Damage Skin(already in): 65 - (36)
                // case 2434390: // Teddy Damage Skin(already in): 66 - (38)
                // case 5680395: // Scorching Heat Damage Skin(already in): 67 - (40)
                // case 2434528: // USA Damage Skin(already in): 68 - (41)
                // case 2434529: // Churro Damage Skin(already in): 69 - (42)
                // case 2434530: // Singapore Night Damage Skin(already in): 70 - (43)
                // case 2433571: // Scribble Crush Damage Skin(already in): 71 - (44)
                // case 2434574: // Full Moon Damage Skin(already in): 72 - (45)
                // case 2433828: // White Heaven Sun Damage Skin(already in): 73 - (46)
                case 2434662: // Jelly Beans Damage Skin: 74
                case 2434664: // Soft-Serve Damage Skin: 75
                case 2434868: // Christmas lights Damage skin: 76
                case 2436041: // Phantom Damage Skin: 77
                case 2436042: // Mercedes Damage Skin: 78
                case 2435046: // Fireworks Damage Skin: 79
                case 2435047: // Heart Balloon Damage Skin: 80
                case 2435836: // Neon Sign Damage Skin: 81
                case 2435141: // Freeze Tag Damage Skin: 82
                case 2435179: // Candy Damage Skin: 83
                case 2435162: // Antique Gold Damage Skin: 84
                case 2435157: // Calligraphy Damage Skin: 85
                case 2435835: // Explosion Damage Skin: 86
                case 2435159: // Snow-wing Damage Skin: 87
                case 2436044: // Miho Damage Skin: 88
                case 2434663: // Donut Damage Skin: 89
                case 2435182: // Music Score Damage Skin: 90
                case 2435850: // Moon Bunny Damage Skin: 91
                case 2435184: // Forest of Tenacity Damage Skin: 92
                case 2435222: // Festival Tortoise Damage Skin: 93
                case 2435293: // April Fools' Damage Skin: 94
                case 2435313: // Blackheart Day Damage Skin: 95
                case 2435331: // Bubble April Fools' Damage Skin: 96
                case 2435332: // Retro April Fools' Damage Skin: 97
                case 2435333: // Monochrome April Fools' Damage Skin: 98
                case 2435334: // Sparkling April Fools' Damage Skin: 99
                case 2435316: // Haste Damage Skin: 100
                case 2435408: // 13th Anniversary Maple Leaf Damage Skin: 101
                case 2435427: // Cyber Damage Skin: 102
                case 2435428: // Cosmic Damage Skin: 103
                case 2435429: // Choco Donut Damage Skin: 104
                case 2435456: // Lovely Damage Skin: 105
                case 2435493: // Monster Balloon Damage Skin: 106
                // case 2435331: // Bubble April Fools' Damage Skin(already in): 107 - (96)
                // case 2435334: // Sparkling April Fools' Damage Skin(already in): 108 - (99)
                case 2435959: // Henesys Damage Skin (unknown ID): 109
                case 2435958: // Leafre Damage Skin (unknown ID): 110
                case 2435431: // Algebraic Damage Skin: 111
                case 2435430: // Blue Fire Damage Skin: 112
                case 2435432: // Purple Damage Skin: 113
                case 2435433: // Nanopixel Damage Skin: 114
                case 2434601: // Invisible Damage Skin(unknown ID): 115
                case 2435521: // Crystal Damage Skin: 116
                case 2435196: // Crow Damage Skin: 117
                case 2435523: // Chocolate Damage Skin: 118
                case 2435524: // Spark Damage Skin: 119
                case 2435538: // Royal Damage Skin: 120
                case 2435832: // Chrome Damage Skin (Ver.1): 121
                case 2435833: // Neon Lights Damage Skin: 122
                case 2435839: // Cosmic Damage Skin(Cards): 123
                case 2435840: // Gilded Damage Skin: 124
                case 2435841: // Batty Damage Skin: 125
                case 2435849: // Monochrome April Fools' Damage Skin: 126
                case 2435972: // Vanishing Journey Damage Skin: 127
                case 2436023: // Chu Chu Damage Skin: 128
                case 2436024: // Lachelein Damage Skin: 129
                case 2436026: // Poison flame Damage Skin: 130
                case 2436027: // Blue Strike Damage Skin: 131
                case 2436028: // Music Power Damage Skin: 132
                case 2436029: // Collage Power Damage Skin: 133
                case 2436045: // Starlight Aurora Damage Skin: 134
                {
                    if (!GameConstants.isZero(chr.getJob())) {
                        int itemidd = toUse.getItemId();
                        Quest quest = Quest.getInstance(7291);
                        QuestStatus queststatus = new QuestStatus(quest, QuestState.Started);
                        int skinnum = GameConstants.getDamageSkinNumberByItem(itemidd);
                        String skinString = String.valueOf(skinnum);
                        queststatus.setCustomData(skinString == null ? "0" : skinString);
                        c.getPlayer().updateQuest(queststatus);
                        c.SendPacket(CWvsContext.showQuestMsg("Damage skin has been changed!"));
                        chr.getMap().broadcastMessage(chr, CWvsContext.showForeignDamageSkin(chr, skinnum), false);
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    } else {
                        c.SendPacket(CWvsContext.showQuestMsg("Zero can't used skins!"));
                    }
                    break;
                }
                //                //                //                //                //                //                //                //

                case 5680021:
                    int expiration;
                    final int chance = Randomizer.nextInt(100);
                    if (chance < 25) {
                        expiration = -1;
                    } else if (chance < 45) {
                        expiration = 90;
                    } else {
                        expiration = 30;
                    }
                    //int chair = GameConstants.chairGachapon();
                    //MapleInventoryManipulator.addById(c, chair, (short) 1, null, null, expiration, "Chair Gachapon");
                    break;
                case 2435902: // Nodestone: Credit @Five
                    //Pointer<Integer> nDecRet = new Pointer<>(0);
                    //List<ChangeLog> aChangeLog = new ArrayList<>();
                    //if (c.getPlayer().pUserInventory.RawRemoveItem(InventoryType.Consume, nPOS, nCount, aChangeLog, nDecRet, null) && nDecRet.Get() == nCount) {
                    if (MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2435902, 1, false, true)) {
                        VMatrixRecord pRecord = new VMatrixRecord();
                        int nRate = (int) (PlayerRandomStream.GetRandom() % 101);
                        if (nRate < 5) { // Special Node
                            List<VCore> aSpecialNodes = VCore.GetSpecialNodes();
                            VCore pCore = aSpecialNodes.get((int) (PlayerRandomStream.GetRandom() % aSpecialNodes.size()));
                            pRecord.nCoreID = pCore.nCoreID;
                            pRecord.nSkillID = 0; // Neckson sets to 0? We have pCore.pOption.nSkillID
                            pRecord.nSLV = 1; // pCore.pOption.nSLV
                            pRecord.nMasterLev = 1; // pCore.nMaxLevel
                            long tCur = System.currentTimeMillis();
                            tCur += 86400000 * pCore.nExpireAfter;
                            //FileTime ftNow = FileTime.GetSystemTime();
                            //ftNow.Add(FileTime.Day, pCore.nExpireAfter);
                            pRecord.ftExpirationDate = tCur;
                            aSpecialNodes.clear();
                        } else if (nRate < 15) { // Skill Node
                            List<VCore> aDecentNodes = VCore.GetDecentNodes();
                            List<VCore> aJobCore = VCore.GetJobNodes();
                            List<VCore> aClassCore = VCore.GetClassNodes();

                            nRate = (int) (PlayerRandomStream.GetRandom() % 101);
                            VCore pCore;
                            if (nRate < 40) { // Decent Skills
                                pCore = aDecentNodes.get((int) (PlayerRandomStream.GetRandom() % aDecentNodes.size()));
                            } else if (nRate < 48) { // Blink
                                pCore = VCore.GetCore(10000007);
                            } else if (nRate < 54) { // Rope Lift
                                pCore = VCore.GetCore(10000000);
                            } else if (nRate < 60) { // Erda Nova
                                pCore = VCore.GetCore(10000008);
                            } else if (nRate < 65) { // Will of Erda
                                pCore = VCore.GetCore(10000009);
                            } else if (nRate < 72) { // Usable Class Skill
                                pCore = aClassCore.get((int) (PlayerRandomStream.GetRandom() % aClassCore.size()));
                                while (!pCore.IsClassSkill(c.getPlayer().getJob())) {
                                    pCore = aClassCore.get((int) (PlayerRandomStream.GetRandom() % aClassCore.size()));
                                }
                            } else if (nRate < 74) { // Unusable Class Skill
                                pCore = aClassCore.get((int) (PlayerRandomStream.GetRandom() % aClassCore.size()));
                                while (pCore.IsClassSkill(c.getPlayer().getJob())) {
                                    pCore = aClassCore.get((int) (PlayerRandomStream.GetRandom() % aClassCore.size()));
                                }
                            } else if (nRate < 79) { // Usable Job Skill
                                pCore = aJobCore.get((int) (PlayerRandomStream.GetRandom() % aJobCore.size()));
                                while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aJobCore.get((int) (PlayerRandomStream.GetRandom() % aJobCore.size()));
                                }
                            } else { // Unusable Job Skill
                                pCore = aJobCore.get((int) (PlayerRandomStream.GetRandom() % aJobCore.size()));
                                while (pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aJobCore.get((int) (PlayerRandomStream.GetRandom() % aJobCore.size()));
                                }
                            }
                            aDecentNodes.clear();
                            aClassCore.clear();
                            aJobCore.clear();
                            pRecord.nCoreID = pCore.nCoreID;
                            pRecord.nSkillID = pCore.aConnectSkill.get(0);
                            pRecord.nSLV = 1;
                            pRecord.nMasterLev = pCore.nMaxLevel;
                        } else { // Boost Node
                            nRate = (int) (PlayerRandomStream.GetRandom() % 101);
                            VCore pCore;
                            if (nRate < 70) {
                                List<VCore> aBoostNode = VCore.GetBoostNodes();
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                pRecord.nCoreID = pCore.nCoreID;
                                pRecord.nSkillID = pCore.aConnectSkill.get(0);
                                pRecord.nSLV = 1;
                                pRecord.nMasterLev = pCore.nMaxLevel;
                                aBoostNode.remove(pCore);
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                pRecord.nSkillID2 = pCore.aConnectSkill.get(0);
                                aBoostNode.remove(pCore);
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                pRecord.nSkillID3 = pCore.aConnectSkill.get(0);
                                aBoostNode.clear();
                            } else {
                                List<VCore> aBoostNode = VCore.GetBoostNodes();
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (pCore.IsJobSkill(c.getPlayer().getJob())) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                int nJob = Integer.valueOf(pCore.aJob.get(0));
                                pRecord.nCoreID = pCore.nCoreID;
                                pRecord.nSkillID = pCore.aConnectSkill.get(0);
                                pRecord.nSLV = 1;
                                pRecord.nMasterLev = pCore.nMaxLevel;
                                aBoostNode.remove(pCore);
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (!pCore.IsJobSkill(nJob)) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                pRecord.nSkillID2 = pCore.aConnectSkill.get(0);
                                aBoostNode.remove(pCore);
                                pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                while (!pCore.IsJobSkill(nJob)) {
                                    pCore = aBoostNode.get((int) (PlayerRandomStream.GetRandom() % aBoostNode.size()));
                                }
                                pRecord.nSkillID3 = pCore.aConnectSkill.get(0);
                                aBoostNode.clear();
                            }
                        }
                        //c.getPlayer().pUserInventory.SendInventoryOperation(Request.Excl, aChangeLog);
                        c.getPlayer().aVMatrixRecord.add(pRecord);
                        c.getPlayer().write(CWvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, false, 0, 0));
                        c.getPlayer().write(CWvsContext.OnNodeStoneResult(pRecord.nCoreID, pRecord.nSkillID, pRecord.nSkillID2, pRecord.nSkillID3));
                        c.getPlayer().dropMessage(5, "You used the Nodestone and got a Node.");
                        //c.getPlayer().usCharacterDataModFlag |= DBChar.ItemSlotConsume.Get() | DBChar.WildHunterInfo.Get();
                    }
                    break;
                default:
                    NPCScriptManager.getInstance().startItemScript(c, npc, script); //maple admin as default npc
                    break;
            }
        }
        if (mountid > 0) {
            mountid = PlayerStats.getSkillByJob(mountid, c.getPlayer().getJob());
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "You already have this skill.");
            } else if (SkillFactory.getSkill(mountid) == null) {
                c.getPlayer().dropMessage(5, "The skill could not be gained.");
            } else if (expiration_days > 0) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                c.getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(mountid), (byte) 1, (byte) 1, System.currentTimeMillis() + expiration_days * 24 * 60 * 60 * 1000);
                c.getPlayer().dropMessage(5, "The skill has been attained.");
            }
        }
        c.SendPacket(CWvsContext.enableActions());
    }
}
