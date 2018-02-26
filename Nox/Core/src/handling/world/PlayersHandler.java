package handling.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleQuestStatus.MapleQuestState;
import client.MapleReward;
import client.MapleStat;
import client.SkillFactory;
import client.anticheat.ReportType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import scripting.EventInstanceManager;
import scripting.EventManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.Randomizer;
import server.events.MapleCoconut;
import server.events.MapleCoconut.MapleCoconuts;
import server.events.MapleEventType;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SummonMovementType;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleDoor;
import server.maps.objects.MapleMist;
import server.maps.objects.MapleSummon;
import server.maps.objects.MechDoor;
import server.quest.MapleQuest;
import tools.LogHelper;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.Reward;
import tools.packet.JobPacket;

public class PlayersHandler {

    public static void Note(final InPacket iPacket, final MapleCharacter chr) {
        final byte type = iPacket.DecodeByte();

        switch (type) {
            case 0:
                String name = iPacket.DecodeString();
                String msg = iPacket.DecodeString();
                boolean fame = iPacket.DecodeByte() > 0;
                iPacket.DecodeInteger(); //0?
                Item itemz = chr.getCashInventory().findByCashId((int) iPacket.DecodeLong());
                if (itemz == null || !itemz.getGiftFrom().equalsIgnoreCase(name) || !chr.getCashInventory().canSendNote(itemz.getUniqueId())) {
                    return;
                }
                try {
                    chr.sendNote(name, msg, fame ? 1 : 0);
                    chr.getCashInventory().sendedNote(itemz.getUniqueId());
                } catch (Exception e) {
                }
                break;
            case 1:
                short num = iPacket.DecodeShort();
                if (num < 0) { // note overflow, shouldn't happen much unless > 32767 
                    num = 32767;
                }
                iPacket.Skip(1); // first byte = wedding boolean? 
                for (int i = 0; i < num; i++) {
                    final int id = iPacket.DecodeInteger();
                    chr.deleteNote(id, iPacket.DecodeByte() > 0 ? 1 : 0);
                }
                break;
            default:
                LogHelper.GENERAL_EXCEPTION.get().info("Unhandled note action, " + type);
        }
    }

    public static void UseDoor(final InPacket iPacket, final MapleCharacter chr) {
        final int oid = iPacket.DecodeInteger();
        final boolean mode = iPacket.DecodeByte() == 0; // specifies if backwarp or not, 1 town to target, 0 target to town

        for (MapleMapObject obj : chr.getMap().getAllDoors()) {
            final MapleDoor door = (MapleDoor) obj;
            if (door.getOwnerId() == oid) {
                door.warp(chr, mode);
                break;
            }
        }
    }

    public static void UseMechDoor(final InPacket iPacket, final MapleCharacter chr) {
        final int oid = iPacket.DecodeInteger();
        final Point pos = iPacket.DecodePosition();
        final int mode = iPacket.DecodeByte(); // specifies if backwarp or not, 1 town to target, 0 target to town
        chr.getClient().write(CWvsContext.enableActions());
        for (MapleMapObject obj : chr.getMap().getAllMechDoors()) {
            final MechDoor door = (MechDoor) obj;
            if (door.getOwnerId() == oid && door.getId() == mode) {
                chr.checkFollow();
                chr.getMap().movePlayer(chr, pos);
                break;
            }
        }
    }

    public static void DressUpRequest(final MapleCharacter chr, InPacket iPacket) {
        int code = iPacket.DecodeInteger();
        switch (code) {
            case 5010093:
                chr.getClient().write(JobPacket.AngelicPacket.updateDress(code, chr));
                chr.getClient().write(CField.updateCharLook(chr, true));
                break;
            case 5010094:
                chr.getClient().write(JobPacket.AngelicPacket.updateDress(code, chr));
                chr.getClient().write(CField.updateCharLook(chr, true));
                break;
        }
    }

    public static void TransformPlayer(final InPacket iPacket, final MapleClient c, final MapleCharacter chr) {
        // D9 A4 FD 00
        // 11 00
        // A0 C0 21 00
        // 07 00 64 66 62 64 66 62 64
        chr.updateTick(iPacket.DecodeInteger());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInteger();
        final String target = iPacket.DecodeString();

        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.write(CWvsContext.enableActions());
            return;
        }
        switch (itemId) {
            case 2212000:
                final MapleCharacter search_chr = chr.getMap().getCharacterByName(target);
                if (search_chr != null) {
                    MapleItemInformationProvider.getInstance().getItemEffect(2210023).applyTo(search_chr);
                    search_chr.dropMessage(6, chr.getName() + " has played a prank on you!");
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                }
                break;
        }
    }

    public static void startEvo(InPacket iPacket, MapleCharacter player, MapleClient c) {

        /*     final List<Integer> maps = new ArrayList<>();
        switch (mapid) {
            case 0:
                maps.add(960010100);
                maps.add(960010101);
                maps.add(960010102);
                break;
            case 1:
                maps.add(960020100);
                maps.add(960020101);
                maps.add(960020102);
                maps.add(960020103);
                break;
            case 2:
                maps.add(960030100);
                break;
            case 3:
                maps.add(689000000);
                maps.add(689000010);
                break;
            default:
                
        }     */
        //  player.getClient().getChannelServer().getEventSM().getEventManager("EvolutionLab"); //Coming Soon
        final EventManager em = c.getChannelServer().getEventSM().getEventManager("EvolutionLab");
        final EventInstanceManager eim = em.getInstance(("EvolutionLab"));
        MapleMap map = player.getClient().getChannelServer().getMapFactory().getMap(957010000);
        MaplePortal portal = map.getPortal("sp");
        //   eim.unregisterPlayer(c.getPlayer());

        player.changeEvolvingMap(map, portal, "Bgm25/CygnusGarden", 957010000);
        eim.registerPlayer(c.getPlayer());
        eim.startEventTimer(45000);
        c.getPlayer().getMap().startMapEffect("Work together and defeat Pink Zakum!.", 5120039);
    }

    public static void HOLLY(MapleClient c, InPacket iPacket) {
        final MapleSummon obj = (MapleSummon) c.getPlayer().getMap().getMapObject(iPacket.DecodeInteger(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        int skillid = iPacket.DecodeInteger();

        if (skillid == 3121013) {
            Point poss = c.getPlayer().getPosition();

            final MapleSummon tosummon = new MapleSummon(
                    c.getPlayer(),
                    SkillFactory.getSkill(3121013).getEffect(obj.getSkillLevel()),
                    new Point(obj.getTruePosition().x, obj.getTruePosition().y),
                    SummonMovementType.STATIONARY,
                    0);
            c.getPlayer().getMap().spawnSummon(tosummon);
            c.getPlayer().addSummon(tosummon);
            return;
        }
        int HP = SkillFactory.getSkill(3121013).getEffect(c.getPlayer().getSkillLevel(3121013)).getX();
        int hp = c.getPlayer().getStat().getMaxHp() * HP / 100;
        c.getPlayer().addHP(hp);
    }

    public static void FollowRequest(final InPacket iPacket, final MapleClient c) {
        MapleCharacter tt = c.getPlayer().getMap().getCharacterById(iPacket.DecodeInteger());
        if (iPacket.DecodeByte() > 0) {
            //1 when changing map
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId()) {
                tt.setFollowOn(true);
                c.getPlayer().setFollowOn(true);
            } else {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (iPacket.DecodeByte() > 0) { //cancelling follow
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getFollowId() == c.getPlayer().getId() && c.getPlayer().isFollowOn()) {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000 && tt.getFollowId() == 0 && c.getPlayer().getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) { //estimate, should less
            tt.setFollowId(c.getPlayer().getId());
            tt.setFollowOn(false);
            tt.setFollowInitiator(false);
            c.getPlayer().setFollowOn(false);
            c.getPlayer().setFollowInitiator(false);
            tt.getClient().write(CWvsContext.followRequest(c.getPlayer().getId()));
        } else {
            c.write(CWvsContext.broadcastMsg(1, "You are too far away."));
        }
    }

    public static void FollowReply(final InPacket iPacket, final MapleClient c) {
        if (c.getPlayer().getFollowId() > 0 && c.getPlayer().getFollowId() == iPacket.DecodeInteger()) {
            MapleCharacter tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if (tt != null && tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000 && tt.getFollowId() == 0 && tt.getId() != c.getPlayer().getId()) { //estimate, should less
                boolean accepted = iPacket.DecodeByte() > 0;
                if (accepted) {
                    tt.setFollowId(c.getPlayer().getId());
                    tt.setFollowOn(true);
                    tt.setFollowInitiator(false);
                    c.getPlayer().setFollowOn(true);
                    c.getPlayer().setFollowInitiator(true);
                    c.getPlayer().getMap().broadcastMessage(CField.followEffect(tt.getId(), c.getPlayer().getId(), null));
                } else {
                    c.getPlayer().setFollowId(0);
                    tt.setFollowId(0);
                    tt.getClient().write(CField.getFollowMsg(5));
                }
            } else {
                if (tt != null) {
                    tt.setFollowId(0);
                    c.getPlayer().setFollowId(0);
                }
                c.write(CWvsContext.broadcastMsg(1, "You are too far away."));
            }
        } else {
            c.getPlayer().setFollowId(0);
        }
    }

    //   public static void HOLLY(MapleClient c,  InPacket iPacket) {
    //     int skillid = iPacket.decodeInteger();
    //     if (skillid == 3121013) {
    //        Point poss = c.getPlayer().getPosition();
    ///        owner == MapleCharacter;
    //         MapleSummon summons = new MapleSummon(summon.OwnerId(), skillid, poss, SummonMovementType.STATIONARY);
    //        c.getPlayer().getMap().spawnSummon(summons);
    //        return;
    //    }
    //     }
    public static void DoRing(final MapleClient c, final String name, final int itemid) {
        final int newItemId = itemid == 2240000 ? 1112803 : (itemid == 2240001 ? 1112806 : (itemid == 2240002 ? 1112807 : (itemid == 2240003 ? 1112809 : (1112300 + (itemid - 2240004)))));
        final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
        int errcode = 0;
        if (c.getPlayer().getMarriageId() > 0) {
            errcode = 0x17;
        } else if (chr == null) {
            errcode = 0x12;
        } else if (chr.getMapId() != c.getPlayer().getMapId()) {
            errcode = 0x13;
        } else if (!c.getPlayer().haveItem(itemid, 1) || itemid < 2240000 || itemid > 2240015) {
            errcode = 0x0D;
        } else if (chr.getMarriageId() > 0 || chr.getMarriageItemId() > 0) {
            errcode = 0x18;
        } else if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) {
            errcode = 0x14;
        } else if (!MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
            errcode = 0x15;
        }
        if (errcode > 0) {
            c.write(CWvsContext.sendEngagement((byte) errcode, 0, null, null));
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().setMarriageItemId(itemid);
        chr.getClient().write(CWvsContext.sendEngagementRequest(c.getPlayer().getName(), c.getPlayer().getId()));
    }

    public static void RingAction(final InPacket iPacket, final MapleClient c) {
        final byte mode = iPacket.DecodeByte();
        switch (mode) {
            case 0:
                DoRing(c, iPacket.DecodeString(), iPacket.DecodeInteger());
                //1112300 + (itemid - 2240004)
                break;
            case 1:
                c.getPlayer().setMarriageItemId(0);
                break;
            case 2:
                //accept/deny proposal
                final boolean accepted = iPacket.DecodeByte() > 0;
                final String name = iPacket.DecodeString();
                final int id = iPacket.DecodeInteger();
                final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (c.getPlayer().getMarriageId() > 0 || chr == null || chr.getId() != id || chr.getMarriageItemId() <= 0 || !chr.haveItem(chr.getMarriageItemId(), 1) || chr.getMarriageId() > 0 || !chr.isAlive() || chr.getEventInstance() != null || !c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null) {
                    c.write(CWvsContext.sendEngagement((byte) 0x1D, 0, null, null));
                    c.write(CWvsContext.enableActions());
                    return;
                }
                if (accepted) {
                    final int itemid = chr.getMarriageItemId();
                    final int newItemId = itemid == 2240000 ? 1112803 : (itemid == 2240001 ? 1112806 : (itemid == 2240002 ? 1112807 : (itemid == 2240003 ? 1112809 : (1112300 + (itemid - 2240004)))));
                    if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "") || !MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
                        c.write(CWvsContext.sendEngagement((byte) 0x15, 0, null, null));
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                    try {
                        final int[] ringID = MapleRing.makeRing(newItemId, c.getPlayer(), chr);
                        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[1]);
                        MapleRing ring = MapleRing.loadFromDb(ringID[1]);
                        if (ring != null) {
                            eq.setRing(ring);
                        }
                        MapleInventoryManipulator.addbyItem(c, eq);

                        eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[0]);
                        ring = MapleRing.loadFromDb(ringID[0]);
                        if (ring != null) {
                            eq.setRing(ring);
                        }
                        MapleInventoryManipulator.addbyItem(chr.getClient(), eq);

                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, chr.getMarriageItemId(), 1, false, false);

                        chr.getClient().write(CWvsContext.sendEngagement((byte) 0x10, newItemId, chr, c.getPlayer()));
                        chr.setMarriageId(c.getPlayer().getId());
                        c.getPlayer().setMarriageId(chr.getId());

                        chr.fakeRelog();
                        c.getPlayer().fakeRelog();
                    } catch (Exception e) {
                        LogHelper.GENERAL_EXCEPTION.get().info("Ring Action:\n{}", e);

                    }

                } else {
                    chr.getClient().write(CWvsContext.sendEngagement((byte) 0x1E, 0, null, null));
                }
                c.write(CWvsContext.enableActions());
                chr.setMarriageItemId(0);
                break;
            case 3:
                //drop, only works for ETC
                final int itemId = iPacket.DecodeInteger();
                final MapleInventoryType type = GameConstants.getInventoryType(itemId);
                final Item item = c.getPlayer().getInventory(type).findById(itemId);
                if (item != null && type == MapleInventoryType.ETC && itemId / 10000 == 421) {
                    MapleInventoryManipulator.drop(c, type, item.getPosition(), item.getQuantity());
                }
                break;
            default:
                break;
        }
    }

    public static void Solomon(final InPacket iPacket, final MapleClient c) {
        c.write(CWvsContext.enableActions());
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(iPacket.DecodeShort());
        if (item == null || item.getItemId() != iPacket.DecodeInteger() || item.getQuantity() <= 0 || c.getPlayer().getGachExp() > 0 || c.getPlayer().getLevel() > 50 || MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getEXP() <= 0) {
            return;
        }
        c.getPlayer().setGachExp(c.getPlayer().getGachExp() + MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getEXP());
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, item.getPosition(), (short) 1, false);
        c.getPlayer().updateSingleStat(MapleStat.GACHAPONEXP, c.getPlayer().getGachExp());
    }

    public static void GachExp(final InPacket iPacket, final MapleClient c) {
        c.write(CWvsContext.enableActions());
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        if (c.getPlayer().getGachExp() <= 0) {
            return;
        }
        c.getPlayer().gainExp(c.getPlayer().getGachExp() * GameConstants.getExpRate_Quest(c.getPlayer().getLevel()), true, true, false);
        c.getPlayer().setGachExp(0);
        c.getPlayer().updateSingleStat(MapleStat.GACHAPONEXP, 0);
    }

    public static void Report(final InPacket iPacket, final MapleClient c) {
        //0 = success 1 = unable to locate 2 = once a day 3 = you've been reported 4+ = unknown reason
        MapleCharacter other;
        ReportType type;
        type = ReportType.getById(iPacket.DecodeByte());
        other = c.getPlayer().getMap().getCharacterByName(iPacket.DecodeString());
        //then,byte(?) and string(reason)
        if (other == null || type == null || other.isIntern()) {
            c.write(CWvsContext.report(4));
            return;
        }
        final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.REPORT_QUEST));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        final long currentTime = System.currentTimeMillis();
        final long theTime = Long.parseLong(stat.getCustomData());
        if (theTime + 7200000 > currentTime && !c.getPlayer().isIntern()) {
            c.write(CWvsContext.enableActions());
            c.getPlayer().dropMessage(5, "You may only report every 2 hours.");
        } else {
            stat.setCustomData(String.valueOf(currentTime));
            other.addReport(type);
            c.write(CWvsContext.report(2));
        }
    }

    public static void exitSilentCrusadeUI(final InPacket iPacket, final MapleClient c) {
        c.getPlayer().updateInfoQuest(1652, "alert=-1"); //Hide Silent Crusade icon
    }

    public static void claimSilentCrusadeReward(final InPacket iPacket, final MapleClient c) {
        short chapter = iPacket.DecodeShort();
        if (c.getPlayer() == null || !c.getPlayer().getInfoQuest(1648 + chapter).equals("m0=2;m1=2;m2=2;m3=2;m4=2")) {
            c.write(CWvsContext.enableActions());
            return;
        }
        final int use = c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot();
        final int setup = c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot();
        final int etc = c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot();
        if (use < 1 || setup < 1 || etc < 1) {
            c.write(CWvsContext.getSilentCrusadeMsg((byte) 2));
            c.write(CWvsContext.enableActions());
            return;
        }
        switch (chapter) {
            case 0:
                c.getPlayer().gainItem(3700031, 1);
                c.getPlayer().gainItem(4310029, 10);
                c.getPlayer().updateInfoQuest(1648, "m0=2;m1=2;m2=2;m3=2;m4=2;r=1"); //Show Reward Claimed
                break;
            case 1:
                c.getPlayer().gainItem(3700032, 1);
                c.getPlayer().gainItem(2430669, 1);
                c.getPlayer().gainItem(4310029, 15);
                c.getPlayer().updateInfoQuest(1649, "m0=2;m1=2;m2=2;m3=2;m4=2;r=1"); //Show Reward Claimed
                break;
            case 2:
                c.getPlayer().gainItem(3700033, 1);
                c.getPlayer().gainItem(2430668, 1);
                c.getPlayer().gainItem(4310029, 20);
                c.getPlayer().updateInfoQuest(1650, "m0=2;m1=2;m2=2;m3=2;m4=2;r=1"); //Show Reward Claimed
                break;
            case 3:
                c.getPlayer().gainItem(3700034, 1);
                c.getPlayer().gainItem(2049309, 1);
                c.getPlayer().gainItem(4310029, 30);
                c.getPlayer().updateInfoQuest(1651, "m0=2;m1=2;m2=2;m3=2;m4=2;r=1"); //Show Reward Claimed
                break;
            default:
                System.out.println("New Silent Crusade Chapter found: " + (chapter + 1));
        }
        c.write(CWvsContext.enableActions());
    }

    public static void buySilentCrusade(final InPacket iPacket, final MapleClient c) {
        //ui window is 0x49
        //iPacket: [00 00] [4F 46 11 00] [01 00]
        short slot = iPacket.DecodeShort(); //slot of item in the silent crusade window
        int itemId = iPacket.DecodeInteger();
        short quantity = iPacket.DecodeShort();
        int tokenPrice = 0, potentialGrade = 0;
        final MapleDataProvider prov = WzDataStorage.getEtcWZ();
        MapleData data = prov.getData("CrossHunterChapter.img");
        int currItemId = 0;
        for (final MapleData wzdata : data.getChildren()) {
            if (wzdata.getName().equals("Shop")) {
                for (final MapleData wzdata2 : wzdata.getChildren()) {
                    for (MapleData wzdata3 : wzdata2.getChildren()) {
                        switch (wzdata3.getName()) {
                            case "itemId":
                                currItemId = MapleDataTool.getInt(wzdata3);
                                break;
                            case "tokenPrice":
                                if (currItemId == itemId) {
                                    tokenPrice = MapleDataTool.getInt(wzdata3);
                                }
                                break;
                            case "potentialGrade":
                                if (currItemId == itemId) {
                                    potentialGrade = MapleDataTool.getInt(wzdata3);
                                }
                                break;
                        }
                    }
                }
            }
        }
        if (tokenPrice == 0) {
            System.out.println("[Silent Crusade] " + c.getPlayer().getName() + " has tried to exploit silent crusade shop.");
            c.write(CWvsContext.getSilentCrusadeMsg((byte) 3));
            c.write(CWvsContext.enableActions());
            return;
        }
        if (c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getNumFreeSlot() >= quantity) {
            if (c.getPlayer().itemQuantity(4310029) < tokenPrice) {
                c.write(CWvsContext.getSilentCrusadeMsg((byte) 1));
                c.write(CWvsContext.enableActions());
                return;
            }
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4310029, tokenPrice, false, false);
                if (itemId < 2000000 && potentialGrade > 0) {
                    Equip equip = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                    equip.setQuantity((short) 1);
                    equip.setGMLog("BUY_SILENT_CRUSADE");
                    equip.setPotential1(-potentialGrade);
                    if (!MapleInventoryManipulator.addbyItem(c, equip)) {
                        c.write(CWvsContext.getSilentCrusadeMsg((byte) 2));
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                } else {
                    if (!MapleInventoryManipulator.addById(c, itemId, (short) quantity, "BUY_SILENT_CRUSADE")) {
                        c.write(CWvsContext.getSilentCrusadeMsg((byte) 2));
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                }
                c.write(CWvsContext.getSilentCrusadeMsg((byte) 0));
                c.write(CWvsContext.enableActions());
            } else {
                c.write(CWvsContext.getSilentCrusadeMsg((byte) 2));
                c.write(CWvsContext.enableActions());
            }
        } else {
            c.write(CWvsContext.getSilentCrusadeMsg((byte) 2));
            c.write(CWvsContext.enableActions());
        }
    }

    public static void UpdatePlayerInformation(final InPacket iPacket, final MapleClient c) {
        byte mode = iPacket.DecodeByte(); //01 open ui 03 save info
        if (mode == 1) {
            if (c.getPlayer().getQuestStatus(GameConstants.PLAYER_INFORMATION) != MapleQuestState.NotStarted) {
                try {
                    String[] info = c.getPlayer().getQuest(MapleQuest.getInstance(GameConstants.PLAYER_INFORMATION)).getCustomData().split(";");
                    c.write(CWvsContext.loadInformation((byte) 2, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]), true));
                } catch (NumberFormatException ex) {
                    c.write(CWvsContext.loadInformation((byte) 4, 0, 0, 0, 0, false));
                    System.out.println("Failed to update account information: " + ex);
                }
            }
            c.write(CWvsContext.enableActions());
            return;
        }
        if (mode != 3) {
            System.out.println("New account information mode found: " + mode);
            c.write(CWvsContext.enableActions());
            return;
        }
        int country = iPacket.DecodeInteger();
        int birthday = iPacket.DecodeInteger();
        int favoriteAction = iPacket.DecodeInteger(); //kind of mask
        int favoriteLocation = iPacket.DecodeInteger(); //kind of mask
        c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PLAYER_INFORMATION)).setCustomData("location=" + country + ";birthday=" + birthday + ";favoriteaction=" + favoriteAction + ";favoritelocation=" + favoriteLocation);
    }

    public static void FindFriends(final InPacket iPacket, final MapleClient c) {
        byte mode = iPacket.DecodeByte();
        switch (mode) {
            case 5:
                if (c.getPlayer().getQuestStatus(GameConstants.PLAYER_INFORMATION) == MapleQuestState.NotStarted) {
                    c.write(CWvsContext.findFriendResult((byte) 6, null, 0, null));
                    c.write(CWvsContext.enableActions());
                    return;
                }
            case 7:
                List<MapleCharacter> characters = new LinkedList();
                for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    if (chr != c.getPlayer()) {
                        if (c.getPlayer().getQuestStatus(GameConstants.PLAYER_INFORMATION) == MapleQuestState.NotStarted || characters.isEmpty()) {
                            characters.add(chr);
                        } else {
                            if (chr.getQuestStatus(GameConstants.PLAYER_INFORMATION) == MapleQuestState.NotStarted && characters.isEmpty()) {
                                continue;
                            }
                            String[] info = c.getPlayer().getQuest(MapleQuest.getInstance(GameConstants.PLAYER_INFORMATION)).getCustomData().split(";");
                            String[] info2 = chr.getQuest(MapleQuest.getInstance(GameConstants.PLAYER_INFORMATION)).getCustomData().split(";");
                            if (info[0].equals(info2[0]) || info[1].equals(info2[1]) || info[2].equals(info2[2]) || info[3].equals(info2[3])) {
                                characters.add(chr);
                            }
                        }
                    }
                }
                if (characters.isEmpty()) {
                    c.write(CWvsContext.findFriendResult((byte) 9, null, 12, null));
                } else {
                    c.write(CWvsContext.findFriendResult((byte) 8, characters, 0, null));
                }
                break;
        }
    }

    public static void LinkSkill(final InPacket iPacket, final MapleClient c, final MapleCharacter chr) {
        //iPacket: [76 7F 31 01] [35 00 00 00]
        c.getPlayer().dropMessage(1, "Beginning link skill.");
        int skill = iPacket.DecodeInteger();
        int cid = iPacket.DecodeInteger();
        boolean found = false;
        for (MapleCharacter chr2 : c.loadCharacters(c.getPlayer().getWorld())) {
            if (chr2.getId() == cid) {
                found = true;
            }
        }
        if (GameConstants.getLinkSkillByJob(chr.getJob()) != skill || !found || chr.getLevel() > 70) {
            c.getPlayer().dropMessage(1, "An error has occured.");
            c.write(CWvsContext.enableActions());
            return;
        }
        MapleCharacter.addLinkSkill(cid, skill);
    }

    public static void reviveAzwan(InPacket iPacket, MapleClient c) {
        if (c.getPlayer() == null) {
            c.write(CWvsContext.enableActions());
            return;
        }
        if (!GameConstants.isAzwanMap(c.getPlayer().getMapId())) {
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().changeMap(c.getPlayer().getMapId(), 0);
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        c.getPlayer().getStat().heal(c.getPlayer());
    }

    public static void magicWheel(InPacket iPacket, MapleClient c) {
        final byte mode = iPacket.DecodeByte(); // 0 = open 2 = start 4 = receive reward
        if (mode == 2) {
            iPacket.DecodeInteger(); //4
            final short toUseSlot = iPacket.DecodeShort();
            iPacket.DecodeShort();
            final int tokenId = iPacket.DecodeInteger();
            if (c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(toUseSlot).getItemId() != tokenId) {
                c.write(CWvsContext.enableActions());
                return;
            }
            for (byte inv = 1; inv <= 5; inv++) {
                if (c.getPlayer().getInventory(MapleInventoryType.getByType(inv)).getNumFreeSlot() < 2) {
                    c.write(CWvsContext.magicWheel((byte) 7, null, null, 0));
                    c.write(CWvsContext.enableActions());
                    return;
                }
            }
            List<Integer> items = new LinkedList<Integer>();
            GameConstants.loadWheelRewards(items, tokenId);
            int end = Randomizer.nextInt(10);
            String data = "Magic Wheel";
            c.getPlayer().setWheelItem(items.get(end));
            if (!MapleInventoryManipulator.removeFromSlot_Lock(c, GameConstants.getInventoryType(tokenId), toUseSlot, (short) 1, false, false)) {
                c.write(CWvsContext.magicWheel((byte) 9, null, null, 0));
                c.write(CWvsContext.enableActions());
                return;
            }
            c.write(CWvsContext.magicWheel((byte) 3, items, data, end));
        } else if (mode == 4) {
            final String data = iPacket.DecodeString();
            int item;
            //try {
            //item = Integer.parseInt(data) / 2;
            item = c.getPlayer().getWheelItem();
            if (item == 0 || !MapleInventoryManipulator.addById(c, item, (short) 1, null)) {
                c.write(CWvsContext.magicWheel((byte) 0xA, null, null, 0));
                c.write(CWvsContext.enableActions());
                return;
            }
            //} catch (Exception ex) {
            //    c.write(CWvsContext.magicWheel((byte) 0xA, null, null, 0));
            //    c.write(CWvsContext.enableActions());
            //    return;
            //}
            c.getPlayer().setWheelItem(0);
            c.write(CWvsContext.magicWheel((byte) 5, null, null, 0));
        }
    }

    public static void blackFriday(InPacket iPacket, MapleClient c) {
        SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd");
        sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.getPlayer().updateInfoQuest(5604, sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", ""));
        System.out.println(sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", ""));
    }

    public static void updateRedLeafHigh(InPacket iPacket, MapleClient c) { //not finished yet
        //TODO: load and set red leaf high in sql
        iPacket.DecodeInteger(); //questid or something
        iPacket.DecodeInteger(); //joe joe quest
        int joejoe = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); //hermoninny quest
        int hermoninny = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); //little dragon quest
        int littledragon = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); //ika quest
        int ika = iPacket.DecodeInteger();
        if (joejoe + hermoninny + littledragon + ika != c.getPlayer().getFriendShipToAdd()) {
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().setFriendShipPoints(joejoe, hermoninny, littledragon, ika);
    }

    public static boolean inArea(MapleCharacter chr) {
        for (Rectangle rect : chr.getMap().getSharedMapResources().areas) {
            if (rect.contains(chr.getTruePosition())) {
                return true;
            }
        }
        for (MapleMist mist : chr.getMap().getAllMists()) {
            if (mist.getOwnerId() == chr.getId() && mist.isPoisonMist() == 2 && mist.getBox().contains(chr.getTruePosition())) {
                return true;
            }
        }
        return false;
    }

    public static void CassandrasCollection(InPacket iPacket, MapleClient c) {
        c.write(CField.getCassandrasCollection());
    }
}
