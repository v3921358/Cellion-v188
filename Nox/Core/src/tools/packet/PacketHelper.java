package tools.packet;

import client.*;
import client.inventory.*;
import static client.inventory.EquipSlotType.Longcoat;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import handling.TemporaryStat;
import handling.world.MapleCharacterLook;
import net.OutPacket;
import provider.data.HexTool;
import server.CashItem;
import server.MapleItemInformationProvider;
import server.maps.MapleMapObject;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import server.shops.MapleShopItem;
import server.stores.AbstractPlayerStore;
import server.stores.IMaplePlayerShop;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;

import java.util.*;
import java.util.Map.Entry;

import server.skills.VMatrixRecord;

public class PacketHelper {

    public static final long FT_UT_OFFSET = 116444592000000000L;
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static long getTime(long realTimestamp) {
        if (realTimestamp == -1L) { // 00 80 05 BB 46 E6 17 02, 1/1/2079
            return MAX_TIME;
        }
        if (realTimestamp == -2L) { // 00 40 E0 FD 3B 37 4F 01, 1/1/1900
            return ZERO_TIME;
        }
        if (realTimestamp == -3L) { // 00 C0 9B 90 7D E5 17 02
            return PERMANENT;
        }
        return realTimestamp * 10000L + 116444592000000000L;
    }

    public static long decodeTime(long fakeTimestamp) {
        if (fakeTimestamp == 150842304000000000L) {
            return -1L;
        }
        if (fakeTimestamp == 94354848000000000L) {
            return -2L;
        }
        if (fakeTimestamp == 150841440000000000L) {
            return -3L;
        }
        return (fakeTimestamp - 116444592000000000L) / 10000L;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        long time;

        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        } else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444592000000000L;
    }

    public static void addImageInfo(OutPacket oPacket, byte[] image) {
        oPacket.EncodeInt(image.length);
        oPacket.Encode(image);
    }

    public static void addStartedQuestInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeByte(1);
        final List<MapleQuestStatus> started = chr.getStartedQuests();
        oPacket.EncodeShort(started.size());
        for (MapleQuestStatus q : started) {
            oPacket.EncodeInt(q.getQuest().getId()); // Version 174, this is an integer 

            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (int kills : q.getMobKills().values()) {
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                oPacket.EncodeString(sb.toString());
            } else {
                oPacket.EncodeString(q.getCustomData() == null ? "" : q.getCustomData());
            }
        }
        addNXQuestInfo(oPacket, chr);
    }

    public static void addNXQuestInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeShort(0);
        /*
         oPacket.encodeShort(7);
         oPacket.encodeMapleAsciiString("1NX5211068");
         oPacket.encodeMapleAsciiString("1");
         oPacket.encodeMapleAsciiString("SE20130619");
         oPacket.encodeMapleAsciiString("20130626060823");
         oPacket.encodeMapleAsciiString("99NX5533018");
         oPacket.encodeMapleAsciiString("1");
         oPacket.encodeMapleAsciiString("1NX1003792");
         oPacket.encodeMapleAsciiString("1");
         oPacket.encodeMapleAsciiString("1NX1702337");
         oPacket.encodeMapleAsciiString("1");
         oPacket.encodeMapleAsciiString("1NX9102857");
         oPacket.encodeMapleAsciiString("1");
         oPacket.encodeMapleAsciiString("SE20130116");
         oPacket.encodeMapleAsciiString("1");
         */
    }

    public static void addCompletedQuestInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeByte(1);
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        oPacket.EncodeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            oPacket.EncodeInt(q.getQuest().getId()); // Version 174, this is an integer 

            oPacket.EncodeInt(KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
            //v139 changed from long to int
        }
    }

    public static void addSkillInfo(OutPacket oPacket, User chr) {
        Map<Skill, SkillEntry> skills = chr.getSkills();

        oPacket.EncodeBool(skills != null); // when true, size: short (size), short (size)
        if (skills != null) {
            oPacket.EncodeShort(skills.size()); // size: int, int, long, (if mastery / 4th job: int)
            for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
                oPacket.EncodeInt(skill.getKey().getId());
                oPacket.EncodeInt(skill.getValue().skillevel);
                addExpirationTime(oPacket, skill.getValue().expiration);

                if (skill.getKey().isFourthJob()) {
                    oPacket.EncodeInt(skill.getValue().masterlevel);
                }
            }
            oPacket.EncodeShort(0);
            Map<Integer, Integer> linkskills = new HashMap<>();
            oPacket.EncodeInt(linkskills.size());
            for (Map.Entry<Integer, Integer> linkskill : linkskills.entrySet()) {
                oPacket.EncodeInt(linkskill.getKey());
                oPacket.EncodeShort(linkskill.getValue() - 1);
            }
        } else {
            System.err.println("Error: Skills = null");
        }
    }

    public static void addCoolDownInfo(OutPacket oPacket, User chr) {
        List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
        long cTime = System.currentTimeMillis();

        oPacket.EncodeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            oPacket.EncodeInt(cooling.skillId);
            oPacket.EncodeInt((int) ((cooling.length + cooling.startTime) - cTime) / 1000);
        }
    }

    public static void addRocksInfo(OutPacket oPacket, User chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            oPacket.EncodeInt(mapz[i]);
        }

        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            oPacket.EncodeInt(map[i]);
        }

        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            oPacket.EncodeInt(maps[i]);
        }
        for (int i = 0; i < 13; i++) {
            oPacket.EncodeInt(maps[i]);
        }
    }

    public static void addMiniGameRecordInfo(OutPacket oPacket, User chr) {
        short size = 0;
        oPacket.EncodeShort(size);
        for (int i = 0; i < size; i++) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
        }
    }

    public static void addRingInfo(OutPacket oPacket, User chr) {
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);

        List<MapleRing> cRing = aRing.getLeft();
        oPacket.EncodeShort(cRing.size());
        for (MapleRing ring : cRing) {
            oPacket.EncodeInt(ring.getPartnerChrId());
            oPacket.EncodeString(ring.getPartnerName(), 13);
            oPacket.EncodeLong(ring.getRingId());
            oPacket.EncodeLong(ring.getPartnerRingId());
        }

        List<MapleRing> fRing = aRing.getMid();
        oPacket.EncodeShort(fRing.size());
        for (MapleRing ring : fRing) {
            oPacket.EncodeInt(ring.getPartnerChrId());
            oPacket.EncodeString(ring.getPartnerName(), 13);
            oPacket.EncodeLong(ring.getRingId());
            oPacket.EncodeLong(ring.getPartnerRingId());
            oPacket.EncodeInt(ring.getItemId());
        }

        List<MapleRing> mRing = aRing.getRight();
        oPacket.EncodeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            oPacket.EncodeInt(marriageId);
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(ring.getPartnerChrId());
            oPacket.EncodeShort(3);
            oPacket.EncodeInt(ring.getItemId());
            oPacket.EncodeInt(ring.getItemId());
            oPacket.EncodeString(chr.getName(), 13);
            oPacket.EncodeString(ring.getPartnerName(), 13);
        }
    }

    public static void addInventoryInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeInt(0); // if this int > 0, write 3 ints and a long GW_ExpConsumeItem
        /*
		dummyBLD.nItemID = CInPacket::Decode4(iPacket);
        *&dummyBLD.nMinLev = CInPacket::Decode4(iPacket);
        *&dummyBLD.nMaxLev = CInPacket::Decode4(iPacket);
        *&dummyBLD.nRemainExp64 = CInPacket::Decode8(iPacket);
         */
        addPotionPotInfo(oPacket, chr);
        //RED stuff:
        oPacket.EncodeInt(0);

        oPacket.EncodeInt(chr.getId());

        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        oPacket.EncodeInt(0); // if > 0, write that many ints

        oPacket.EncodeByte(0); // 9 ints, 3 buffers (8)
        oPacket.EncodeByte(0); // byte, int, byte, 6 ints, string
        oPacket.EncodeByte(0); // byte, int, byte, 6 ints, string

        oPacket.EncodeByte(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        oPacket.EncodeByte(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        oPacket.EncodeByte(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        oPacket.EncodeByte(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        oPacket.EncodeByte(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            oPacket.EncodeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            oPacket.EncodeLong(getTime(-2L));
        }

        oPacket.EncodeByte(0); // new in v148

        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        final List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        for (Item item : equipped) { // Equip Inv
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Cash Equip Inv
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        iv = chr.getInventory(MapleInventoryType.EQUIP); // Equip Item Inv
        for (Item item : iv.list()) {
            addItemPosition(oPacket, item, false, false);
            addItemInfo(oPacket, item, chr);
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Evan Inventory
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Mechanic Inventory
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Android Inventory
            if (item.getPosition() <= -1200) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Angelic Buster Inventory
            if ((item.getPosition() <= -1300) && (item.getPosition() >= -1305)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Bit Inventory
            if ((item.getPosition() <= -1400) && (item.getPosition() >= -1425)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Zero Female CS Inventory
            Equip equip = (Equip) item;
            Equip beta = null;
            if (equip.isBetaShare()) {
                beta = (Equip) equip.copy();
                beta.setPosition(GameConstants.getBetaCashPosition(equip.getPosition()));
            }
            if (beta == null) {
                beta = equip;
            }
            if (GameConstants.isBetaSlot(beta.getPosition())) {
                addItemPosition(oPacket, beta, false, false);
                addItemInfo(oPacket, beta, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Monster Battle
            if ((item.getPosition() <= -5100) && (item.getPosition() >= -5106)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Arcane Symbol
            if ((item.getPosition() <= -1600) && (item.getPosition() >= -1602)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }

        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Totem Inventory
            if ((item.getPosition() <= -5000) && (item.getPosition() >= -5003)) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);

        for (Item item : equipped) { // Haku Inventory
            if (item.getPosition() == -5200) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);

        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemPosition(oPacket, item, false, false);
            addItemInfo(oPacket, item, chr);
        }
        oPacket.EncodeByte(0); // End of USE Inventory

        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemPosition(oPacket, item, false, false);
            addItemInfo(oPacket, item, chr);
        }
        oPacket.EncodeByte(0); // End of Setup Inventory

        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemPosition(oPacket, item, false, false);
                addItemInfo(oPacket, item, chr);
            }
        }
        oPacket.EncodeByte(0); // End of ETC Inventory

        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemPosition(oPacket, item, false, false);
            addItemInfo(oPacket, item, chr);
        }
        oPacket.EncodeByte(0); // End of CASH Inventory

//        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
//            oPacket.EncodeInt(i);
//            oPacket.EncodeInt(chr.getExtendedSlot(i));
//            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
//                if ((item.getPosition() > i * 100 + 100) && (item.getPosition() < i * 100 + 200)) {
//                    addItemPosition(oPacket, item, false, true);
//                    addItemInfo(oPacket, item, chr);
//                }
//            }
//            oPacket.EncodeInt(-1);
//        }
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
    }

    public static void addPotionPotInfo(OutPacket oPacket, User chr) {
        /*if (chr.getPotionPots() == null) {
            oPacket.EncodeInt(0);
            return;
        }
        oPacket.EncodeInt(chr.getPotionPots().size());
        for (MaplePotionPot p : chr.getPotionPots()) {
            oPacket.EncodeInt(p.getId());
            oPacket.EncodeInt(p.getMaxValue());
            oPacket.EncodeInt(p.getHp());
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(p.getMp());

            oPacket.EncodeLong(PacketHelper.getTime(p.getStartDate()));
            oPacket.EncodeLong(PacketHelper.getTime(p.getEndDate()));
        }*/
        oPacket.EncodeInt(0);
    }

    public static void addCharStats(OutPacket oPacket, User chr) {
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(chr.getId()); // dwCharacterIDForLog
        oPacket.EncodeInt(0); //dwWorldIDForLog
        oPacket.EncodeString(chr.getName(), 13);
        oPacket.EncodeByte(chr.getGender());
        oPacket.EncodeByte(chr.getSkinColor());
        oPacket.EncodeInt(chr.getFace());
        oPacket.EncodeInt(chr.getHair());
        oPacket.EncodeByte(-1); //nMixBaseHairColor
        oPacket.EncodeByte(0); //nMixAddHairColor
        oPacket.EncodeByte(0); //nMixHairBaseProb
        oPacket.EncodeByte(chr.getLevel());
        oPacket.EncodeShort(chr.getJob());
        chr.getStat().connectData(oPacket);
        oPacket.EncodeShort(chr.getRemainingAp());

        if (GameConstants.isExtendedSpJob(chr.getJob())) {
            int size = chr.getRemainingSpSize();
            oPacket.EncodeByte(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    oPacket.EncodeByte(i + 1);
                    oPacket.EncodeInt(chr.getRemainingSp(i));
                }
            }
        } else {
            oPacket.EncodeShort(chr.getRemainingSp());
        }
        oPacket.EncodeLong(chr.getExp());
        oPacket.EncodeInt(chr.getFame());
        oPacket.EncodeInt(0); // F3 4F 11 00
        oPacket.EncodeInt(chr.getGachExp());
        oPacket.EncodeInt(chr.getMapId());
        oPacket.EncodeByte(chr.getInitialSpawnpoint());
        oPacket.EncodeInt(0); // playtime in seconds?

        oPacket.EncodeShort(chr.getSubcategory());
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
            oPacket.EncodeInt(chr.getFaceMarking());
        }

        // Professions
        oPacket.EncodeByte(chr.getFatigue());
        oPacket.EncodeInt(GameConstants.getTimeAsInt());
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            oPacket.EncodeInt(chr.getTrait(t).getTotalExp());
        }
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            oPacket.EncodeShort(0);
        }
        oPacket.EncodeByte(0);
        oPacket.EncodeLong(getTime(-2)); // trait to here is DecodeBuffer(21) + CInPacket::Decode4

        oPacket.EncodeInt(chr.getStat().pvpExp);
        oPacket.EncodeByte(chr.getStat().pvpRank);
        oPacket.EncodeInt(chr.getBattlePoints());
        oPacket.EncodeByte(5); // nPvPModeLevel
        oPacket.EncodeByte(6); //nPvPModeType
        oPacket.EncodeInt(0); //nEventPoint

        addPartTimeJob(oPacket, User.getPartTime(chr.getId()));

        // Character card
        for (int i = 0; i < 9; i++) {
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(0);
        }
        oPacket.EncodeLong(getTime(-2));
        oPacket.EncodeLong(0);
        oPacket.EncodeLong(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeBool(chr.isBurning()); //bBurning
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
    }

    /**
     * Returns the secondary Hair for AB/ Zero jobs. This method is only called upon when beta is true, so other jobs will not accidentally
     * load secondary hair.
     *
     * @param chr MapleCharacter instance
     * @return secondary hair
     */
    private static int secondaryHair(MapleCharacterLook chr) {
        if (GameConstants.isAngelicBuster(chr.getJob())) {
            return chr.getAngelicDressupHair();
        } else { //zero
            return chr.getZeroBetaHair();
        }
    }

    /**
     * Returns the secondary Face for AB/ Zero jobs. This method is only called upon when beta is true, so other jobs will not accidentally
     * load secondary Face.
     *
     * @param chr MapleCharacter instance
     * @return secondary Face
     */
    private static int secondaryFace(MapleCharacterLook chr) {
        if (GameConstants.isAngelicBuster(chr.getJob())) {
            return chr.getAngelicDressupFace();
        } else { //zero
            return chr.getZeroBetaFace();
        }
    }

    public static void addCharLook(OutPacket oPacket, MapleCharacterLook chr, boolean mega, boolean second) {
        oPacket.EncodeByte(second ? chr.getSecondaryGender() : chr.getGender());//Secondary gender is always 1 (zeroBeta/AB) but meh
        oPacket.EncodeByte(chr.getSkinColor());//AB is always white and Zero shares skin color, so no need for secondSkin.
        oPacket.EncodeInt(second ? secondaryFace(chr) : chr.getFace());
        oPacket.EncodeInt(chr.getJob());
        oPacket.EncodeByte(mega ? 0 : 1);
        oPacket.EncodeInt(second ? secondaryHair(chr) : chr.getHair());

        final Map<Short, Integer> myEquip = new LinkedHashMap<>();
        final Map<Short, Integer> maskedEquip = new LinkedHashMap<>();
        final Map<Short, Integer> totemEquip = new LinkedHashMap<>();
        final Map<Short, Integer> equip = second ? chr.getSecondaryEquips(true) : chr.getEquips(true);
        for (Entry<Short, Integer> item : equip.entrySet()) {
            if (item.getKey() < -127) {
                continue;
            }
            short pos = (short) (item.getKey() * -1);

            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getValue());
            } else if (pos > 100 && pos != 111) {
                pos = (short) (pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                    totemEquip.put(pos, item.getValue());
                }
                myEquip.put(pos, item.getValue());
                totemEquip.put(pos, item.getValue());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getValue());
                totemEquip.put(pos, item.getValue());
            }
        }
        for (Entry<Short, Integer> totem : chr.getTotems().entrySet()) {
            short pos = (short) (totem.getKey() * -1);
            if (pos < 0 || pos > 2) { //3 totem slots
                continue;
            }
            if (totem.getValue() < 1200000 || totem.getValue() >= 1210000) {
                continue;
            }
            totemEquip.put(pos, totem.getValue());
        }

        for (Entry<Short, Integer> entry : myEquip.entrySet()) {
            int weapon = entry.getValue();
            if (GameConstants.getWeaponType(weapon) == (second ? MapleWeaponType.LAZULI : MapleWeaponType.LAPIS)) {
                continue;
            }
            oPacket.EncodeByte(entry.getKey());
            oPacket.EncodeInt(entry.getValue());
        }
        oPacket.EncodeByte(255);

        for (Entry<Short, Integer> entry : maskedEquip.entrySet()) {
            oPacket.EncodeByte(entry.getKey());
            oPacket.EncodeInt(entry.getValue());
        }
        oPacket.EncodeByte(255);

        for (Entry<Short, Integer> entry : totemEquip.entrySet()) {
            oPacket.EncodeByte(entry.getKey());
            oPacket.EncodeInt(entry.getValue());
        }
        oPacket.EncodeByte(255);

        Integer cWeapon = 0; //Integer cWeapon = equip.get(-111);

        // Checks if player has a listed cash weapon equip and fetchs the ID for the integer being written below.
        // Hack fix but neccessary for now as for some reason attempting to fetch the ID normally results in null. -MazenMapleCharacter oPlayer = (MapleCharacter) chr;
        User pPlayer = (User) chr;
        for (int i = 0; i < GameConstants.aCashWeapons.length; i++) {
            if (pPlayer.hasEquipped(GameConstants.aCashWeapons[i])) {
                cWeapon = GameConstants.aCashWeapons[i];
                break;
            }
        }

        oPacket.EncodeInt(cWeapon != null ? cWeapon : 0);
        Integer Weapon = equip.get(-11);
        oPacket.EncodeInt(Weapon != null ? Weapon : 0);
        boolean zero = GameConstants.isZero(chr.getJob());
        Integer Shield = equip.get(-10);
        oPacket.EncodeInt(!zero && Shield != null ? Shield : 0);
        oPacket.EncodeByte(GameConstants.isMercedes(chr.getJob()) ? 1 : 0); // Mercedes/Elf Ears
        oPacket.Encode(new byte[12]);
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
            oPacket.EncodeInt(chr.getFaceMarking());
        }
        if (GameConstants.isZero(chr.getJob())) {
            oPacket.EncodeBool(second);
        }
        if (GameConstants.isBeastTamer(chr.getJob())) {
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(chr.getEars());
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(chr.getTail());
        }
        oPacket.EncodeByte(0); // nMixedHairColor
        oPacket.EncodeByte(0); // nMixHairPercent
    }

    public static void addExpirationTime(OutPacket oPacket, long time) {
        oPacket.EncodeLong(getTime(time));
    }

    public static void addItemPosition(OutPacket oPacket, Item item, boolean trade, boolean bagSlot) {
        if (item == null) {
            oPacket.EncodeByte(0);
            return;
        }
        short pos = item.getPosition();
        if (pos <= -1) {
            pos = (short) (pos * -1);
            if ((pos > 100) && (pos < 1000)) {
                pos = (short) (pos - 100);
            }
        }
        if (bagSlot) {
            oPacket.EncodeInt(pos % 100 - 1);
        } else if ((!trade) && (item.getType() == ItemType.Equipment)) {
            oPacket.EncodeShort(pos);
        } else {
            oPacket.EncodeByte(pos);
        }
    }

    public static void addItemInfo(OutPacket oPacket, Item item) {
        addItemInfo(oPacket, item, null);
    }

    public static void addItemInfo(final OutPacket oPacket, final Item item, final User chr) {
        oPacket.EncodeByte(item.getPet() != null ? 3 : item.getType().getTypeValue());// Actually, getTypeValue for pet returns '3' already, but in case of server error.. 
        oPacket.EncodeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0 && !GameConstants.isMarriageRing(item.getItemId()) && item.getItemId() / 10000 != 166;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        oPacket.EncodeBool(hasUniqueId);
        if (hasUniqueId) {
            oPacket.EncodeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            addPetItemInfo(oPacket, item, item.getPet(), true);
        } else {
            addExpirationTime(oPacket, item.getExpiration());
            oPacket.EncodeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(item.getItemId()));

            if (item.getType() == ItemType.Equipment) {
                final Equip equip = (Equip) item;
                final List<EquipStat> eqStats = EquipHelper.calculateEquipStatsForEncoding((Equip) item);
                final List<EquipSpecialStat> eqSpecialStats = EquipHelper.calculateEquipSpecialStatsForEncoding((Equip) item);

                addEquipStats(oPacket, equip, eqStats, eqSpecialStats);
                addEquipBonusStats(oPacket, equip, hasUniqueId);
            } else {
                oPacket.EncodeShort(item.getQuantity());
                oPacket.EncodeString(item.getOwner());
                oPacket.EncodeShort(item.getFlag());

                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()) || item.getItemId() / 10000 == 287) {//ur missing some id checks here y'kno
                    oPacket.EncodeLong(item.getInventoryId() <= 0 ? -1 : item.getInventoryId());
                }
            }
        }
    }

    public static void addEquipStats(OutPacket oPacket, Equip equip, List<EquipStat> eqStats, List<EquipSpecialStat> eqSpecialStats) {
        int head = 0;

        for (EquipStat stat : eqStats) {
            head |= stat.getValue();
        }
        oPacket.EncodeInt(head);

        if (head != 0) {
            if (eqStats.contains(EquipStat.SLOTS)) {
                oPacket.EncodeByte(equip.getUpgradeSlots());
            }
            if (eqStats.contains(EquipStat.LEVEL)) {
                oPacket.EncodeByte(equip.getLevel());
            }
            if (eqStats.contains(EquipStat.STR)) {
                oPacket.EncodeShort(equip.getStr());
            }
            if (eqStats.contains(EquipStat.DEX)) {
                oPacket.EncodeShort(equip.getDex());
            }
            if (eqStats.contains(EquipStat.INT)) {
                oPacket.EncodeShort(equip.getInt());
            }
            if (eqStats.contains(EquipStat.LUK)) {
                oPacket.EncodeShort(equip.getLuk());
            }
            if (eqStats.contains(EquipStat.MHP)) {
                oPacket.EncodeShort(equip.getHp());
            }
            if (eqStats.contains(EquipStat.MMP)) {
                oPacket.EncodeShort(equip.getMp());
            }
            if (eqStats.contains(EquipStat.WATK)) {
                oPacket.EncodeShort(equip.getWatk());
            }
            if (eqStats.contains(EquipStat.MATK)) {
                oPacket.EncodeShort(equip.getMatk());
            }
            if (eqStats.contains(EquipStat.PDD)) {
                oPacket.EncodeShort(equip.getWdef());
            }
            if (eqStats.contains(EquipStat.HANDS)) {
                oPacket.EncodeShort(equip.getHands());
            }
            if (eqStats.contains(EquipStat.SPEED)) {
                oPacket.EncodeShort(equip.getSpeed());
            }
            if (eqStats.contains(EquipStat.JUMP)) {
                oPacket.EncodeShort(equip.getJump());
            }
            if (eqStats.contains(EquipStat.FLAG)) {
                oPacket.EncodeShort(equip.getFlag());
            }
            if (eqStats.contains(EquipStat.INC_SKILL)) {//nLevelUpType
                oPacket.EncodeByte(equip.getIncSkill() > 0 ? 1 : 0);
            }
            if (eqStats.contains(EquipStat.ITEM_LEVEL)) {//nLevel
                oPacket.EncodeByte(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
            }
            if (eqStats.contains(EquipStat.ITEM_EXP)) {//nEXP
                oPacket.EncodeLong(equip.getExpPercentage() * 100000); // Item Exp... 10000000 = 100%
            }
            if (eqStats.contains(EquipStat.DURABILITY)) {
                oPacket.EncodeInt(equip.getDurability());
            }
            if (eqStats.contains(EquipStat.VICIOUS_HAMMER)) {//nIUC
                oPacket.EncodeInt(equip.getViciousHammer());
            }
            if (eqStats.contains(EquipStat.PVP_DAMAGE)) {
                oPacket.EncodeShort(equip.getPVPDamage());
            }
            if (eqStats.contains(EquipStat.REDUCE_REQUIREMENT)) {
                oPacket.EncodeByte(0); //ReduceReq
            }
            if (eqStats.contains(EquipStat.SPELL_TRACE)) {
                oPacket.EncodeShort(equip.getSpellTrace());
            }
            if (eqStats.contains(EquipStat.DURABILITY_SPECIAL)) {
                oPacket.EncodeInt(equip.getDurability());
            }
            if (eqStats.contains(EquipStat.REQUIRED_LEVEL)) {
                oPacket.EncodeByte(equip.getReqLevel());
            }
            if (eqStats.contains(EquipStat.YGGDRASIL_WISDOM)) {
                oPacket.EncodeByte(equip.getYggdrasilWisdom());
            }
            if (eqStats.contains(EquipStat.FINAL_STRIKE)) {
                oPacket.EncodeBool(equip.getFinalStrike());
            }
            if (eqStats.contains(EquipStat.BOSS_DAMAGE)) {
                oPacket.EncodeByte(equip.getBossDamage());
            }
            if (eqStats.contains(EquipStat.IGNORE_PDR)) { //nIMDR, I wonder if it really is MDR
                oPacket.EncodeByte(equip.getIgnorePDR());
            }
        } else {
            /*
             *   if ( v3 >= 0 )
             *     v36 = 0;
             *   else
             *     v36 = (unsigned __int8)CInPacket::Decode1(a2);
             */
//            oPacket.encode(0); //unknown
        }
        addEquipSpecialStats(oPacket, equip, eqSpecialStats);
    }

    public static void addEquipSpecialStats(OutPacket oPacket, Equip equip, List<EquipSpecialStat> eqSpecialStats) {
        int head = 0;

        for (EquipSpecialStat stat : eqSpecialStats) {
            head |= stat.getValue();
        }
        oPacket.EncodeInt(head);

        if (head != 0) {
            if (eqSpecialStats.contains(EquipSpecialStat.TOTAL_DAMAGE)) {
                oPacket.EncodeByte(equip.getTotalDamage());
            }
            if (eqSpecialStats.contains(EquipSpecialStat.ALL_STAT)) {
                oPacket.EncodeByte(equip.getAllStat());
            }
            if (eqSpecialStats.contains(EquipSpecialStat.KARMA_COUNT)) {
                oPacket.EncodeByte(equip.getKarmaCount());
            }
            if (eqSpecialStats.contains(EquipSpecialStat.GRADE_OPTION)) {
                oPacket.EncodeLong(System.currentTimeMillis()); //according to the kmst idb this is wrong
            }
            if (eqSpecialStats.contains(EquipSpecialStat.ITEM_STATE)) {  //There are multiple item state
                oPacket.EncodeInt(0x100); // TODO: 0x100 = Can be upgraded still (Yellow Star); 0 = Fully Upgraded (Blue Star)
            }
        }
    }

    public static void addEquipBonusStats(OutPacket oPacket, Equip equip, boolean hasUniqueId) {
        oPacket.EncodeString(equip.getOwner());
        oPacket.EncodeByte(equip.getPotentialTier().getValue());
        oPacket.EncodeByte(equip.getEnhance());
        oPacket.EncodeShort(equip.getPotential1());
        oPacket.EncodeShort(equip.getPotential2());
        oPacket.EncodeShort(equip.getPotential3());
        oPacket.EncodeShort(equip.getBonusPotential1());
        oPacket.EncodeShort(equip.getBonusPotential2());
        oPacket.EncodeShort(equip.getBonusPotential3());
        oPacket.EncodeShort(equip.getFusionAnvil() % 100000);
        oPacket.EncodeShort(equip.getSocketState());
        oPacket.EncodeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
        oPacket.EncodeShort(equip.getSocket2() % 10000);
        oPacket.EncodeShort(equip.getSocket3() % 10000);
        if (!hasUniqueId) {
            oPacket.EncodeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
        }
        oPacket.EncodeLong(getTime(-2)); // ftEquipped
        oPacket.EncodeInt(equip.getExpBonus()); // this is nPrevBonusExpRate

        oPacket.EncodeLong(equip.getUniqueId());
        oPacket.EncodeLong(getTime(-2)); //ftExpireDate

        oPacket.EncodeInt(equip.getCashGrade() / 1000); //nGrade
        for (int i = 0; i < 3; i++) {//hardcoded 3 iterations of int
            oPacket.EncodeInt(equip.getCashStats()[i] % 1000);
        }
        oPacket.EncodeShort(equip.getSoulOptionId());
        oPacket.EncodeShort(equip.getSoulSocketId());
        oPacket.EncodeShort(equip.getSoulOption());
        if (ItemConstants.IsArcaneSymbol(equip.getItemId())) {
            oPacket.EncodeShort(equip.getArcane());
            oPacket.EncodeInt(equip.getArcaneExp());
            oPacket.EncodeShort(equip.getArcaneMaxLevel());
        }
    }

    public static void serializeMovementList(OutPacket oPacket, MapleMapObject obj, List<LifeMovementFragment> moves, int bPassive) {
        if (obj != null) {
            oPacket.EncodeInt(obj.gettEncodedGatherDuration());
            oPacket.EncodeShort(obj.getxCS());
            oPacket.EncodeShort(obj.getyCS());
            oPacket.EncodeShort(obj.getVxCS());
            oPacket.EncodeShort(obj.getVyCS());
        }

        oPacket.EncodeByte(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(oPacket);
        }
        if (bPassive > 0) {
            oPacket.EncodeByte(0);
        }
    }

    public static void addAnnounceBox(OutPacket oPacket, User chr) {
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            addInteraction(oPacket, chr.getPlayerShop());
        } else {
            oPacket.EncodeByte(0);
        }
    }

    public static void addInteraction(OutPacket oPacket, IMaplePlayerShop shop) {
        oPacket.EncodeByte(shop.getGameType());
        oPacket.EncodeInt(((AbstractPlayerStore) shop).getObjectId());
        oPacket.EncodeString(shop.getDescription());
        if (shop.getShopType() != 1) {
            oPacket.EncodeByte(shop.getPassword().length() > 0 ? 1 : 0);
        }
        oPacket.EncodeByte(shop.getItemId() % 10);
        oPacket.EncodeByte(shop.getSize());
        oPacket.EncodeByte(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            oPacket.EncodeByte(shop.isOpen() ? 0 : 1);
        }
    }

    public static void addCharacterInfo(OutPacket oPacket, User chr) {
        long mask = 0xFFFFFFFFFFFFFFFFL;
        oPacket.EncodeLong(mask);//dbcharFlag
        oPacket.EncodeByte(0); //nCombatOrders, guessing combat orders level
        for (int i = 0; i < 3; i++) {
            oPacket.EncodeInt(-20); //skill cooldown time aPetActiveSkillCoolTime
        }
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0); // loop if byte > 0, decode an int
        oPacket.EncodeInt(0); // (if int > 0, write an int and decodebuffer)
        oPacket.EncodeByte(0); // (if byte > 0, decode a byte and an int. if int > 0, decodebuffer. after the loop, decode an int and if int > 0, decode buffer)

        if ((mask & 1) != 0) {
            addCharStats(oPacket, chr);//correct
            oPacket.EncodeByte(chr.getBuddylist().getCapacity());

            oPacket.EncodeBool(chr.getBlessOfFairyOrigin() != null);//correct
            if (chr.getBlessOfFairyOrigin() != null) {
                oPacket.EncodeString(chr.getBlessOfFairyOrigin());
            }

            oPacket.EncodeBool(chr.getBlessOfEmpressOrigin() != null);//correct
            if (chr.getBlessOfEmpressOrigin() != null) {
                oPacket.EncodeString(chr.getBlessOfEmpressOrigin());
            }

            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
            oPacket.EncodeBool((ultExplorer != null) && (ultExplorer.getCustomData() != null));//correct
            if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
                oPacket.EncodeString(ultExplorer.getCustomData());
            }
        }
        if ((mask & 2) != 0) {//correct
            oPacket.EncodeLong(chr.getMeso());
        }
        if ((mask & 8) != 0) {//addItemInfo is correct, looks like this is correct
            addInventoryInfo(oPacket, chr);
        }
        if ((mask & 0x100) != 0) {//fixed, added second short for flag 0x100's second loop
            addSkillInfo(oPacket, chr);
        }
        if ((mask & 0x8000) != 0) {//removed the extra short.. wasnt supposed to be here.
            addCoolDownInfo(oPacket, chr);
        }
        if ((mask & 0x200) != 0) {//correct
            addStartedQuestInfo(oPacket, chr);
        }
        if ((mask & 0x4000) != 0) {//correct
            addCompletedQuestInfo(oPacket, chr);
        }
        if ((mask & 0x400) != 0) {//correct
            addMiniGameRecordInfo(oPacket, chr);
        }
        if ((mask & 0x800) != 0) {//correct
            addRingInfo(oPacket, chr);
        }
        if ((mask & 0x1000) != 0) {//correct
            addRocksInfo(oPacket, chr);
        }
        if ((mask & 0x20000) != 0) {//correct
            oPacket.EncodeInt(0);
        }
        if ((mask & 0x10000) != 0) {//will assume correct, can't see structure since only got cfield/packethelper
            addMonsterBookInfo(oPacket, chr); // todo fix
        }

        if ((mask & 0x100000) != 0) {//correct
            oPacket.EncodeInt(chr.getMonsterBook().getSet());
        }

        if ((mask & 0x200000) != 0) {//correct
            oPacket.EncodeShort(0); // if this short > 0, write a short
        }
        if ((mask & 0x400000) != 0) {// Familiars
            oPacket.EncodeInt(0);
        }
        if ((mask & 0x40000) != 0) {//correct
            oPacket.EncodeShort(0); // addNewYearInfo
        }
        if ((mask & 0x80000) != 0) {
            chr.addQuestInfoExPacket(oPacket); // QuestEx
        }

        if ((mask & 0x2000) != 0) {//correct
            oPacket.EncodeShort(0); // if this short > 0, write an int and addCharLook
            //Should we take isZeroBetaState into account here too? or do you actually always spawn as alpha?
            //   addCharLook(oPacket, chr, false, false);

        }

        oPacket.EncodeByte(0); // v178 new
        // if > 0, for loop int, encode int, str per size

        if ((mask & 0x1000) != 0) {//correct
            oPacket.EncodeInt(0);//if this int > 0, write two ints
            /*
            for ( mm = CInPacket::Decode4(v304); mm > 0; --mm )
		    {
		      nCount = CInPacket::Decode4(v304);
		      sValue._m_pStr = CInPacket::Decode4(v304);
		      ZMap<unsigned long,int,unsigned long>::Insert((&v306[4]._ZtlSecureTear_nWillEXP_CS + 2), &nCount, &sValue);
		    }   
             * */
        }

        if ((mask & 0x200000) != 0) {//correct
            if ((chr.getJob() >= 3300) && (chr.getJob() <= 3312)) {
                addJaguarInfo(oPacket, chr);
            }
        }

        if ((mask & 0x800) != 0) {//correct
            if (GameConstants.isZero(chr.getJob())) {
                addZeroInfo(oPacket, chr);
            }
        }

        /*    if ((mask & 0x400000) != 0) {//correct
            oPacket.encodeShort(0); // if this short > 0, write a short and int 
        }*/
        if ((mask & 0x4000000) != 0) {//correct
            oPacket.EncodeShort(0); // if this short > 0, write a short and int ZtlSecureTear_nMMP_CS
            /*
             *	GW_NpcShopBuyLimit
             *	sValue._m_pStr = CInPacket::Decode2(v304);
        	 *	v323 = CInPacket::Decode4(v304);
             *  */
        }

        if ((mask & 0x20000000) != 0) {
            for (int i = 1; i <= 5; i++) {
                for (int j = 0; j < GameConstants.getNumSteal(i); j++) {
                    oPacket.EncodeInt(chr.aStealMemory[i][j]);
                    //System.err.println(String.format("[%d][%d] = %d", i, j, chr.aStealMemory[i][j]));
                }
            }
        }

        if ((mask & 0x10000000) != 0) {
            oPacket.EncodeInt(chr.mStealSkillInfo.get(24001001));
            oPacket.EncodeInt(chr.mStealSkillInfo.get(24101001));
            oPacket.EncodeInt(chr.mStealSkillInfo.get(24111001));
            oPacket.EncodeInt(chr.mStealSkillInfo.get(24121001));
            oPacket.EncodeInt(chr.mStealSkillInfo.get(24121054));
            //System.err.println(String.format("[24001001] = %d", chr.mStealSkillInfo.get(24001001)));
            //System.err.println(String.format("[24101001] = %d", chr.mStealSkillInfo.get(24101001)));
            //System.err.println(String.format("[24111001] = %d", chr.mStealSkillInfo.get(24111001)));
            //System.err.println(String.format("[24121001] = %d", chr.mStealSkillInfo.get(24121001)));
            //System.err.println(String.format("[24121054] = %d", chr.mStealSkillInfo.get(24121054)));
        }
        if ((mask & 0x80000000) != 0) {//correct
            addAbilityInfo(oPacket, chr);
        }

        if ((mask & 0x10000) != 0) {//correct
            oPacket.EncodeShort(0); // if this short > 0, write two ints GW_SoulCollection
            /*
             *  LOWORD(v337->lRewardGradeQ._Myhead) = CInPacket::Decode4(v304);
             *  *(&v337->lRewardGradeQ._Myhead + 2) = CInPacket::Decode4(v304);
             * */
        }

        oPacket.EncodeInt(0); //new v134 //correct
        oPacket.EncodeByte(0);//correct

        if ((mask & 1) != 0) {//correct
            addHonorInfo(oPacket, chr);
        }

        if ((mask & 2) != 0) {//correct
            oPacket.EncodeByte(1);
            oPacket.EncodeShort(0);
        }

        if ((mask & 4) != 0) {//correct
            oPacket.EncodeByte(0); // if this byte > 0, decode item slot and write an int
        }

        if ((mask & 8) != 0) {//correct
            if (GameConstants.isAngelicBuster(chr.getJob())) {
                //This might actually ffing be where if(zero) write betalook
                oPacket.EncodeInt(21173); //face
                oPacket.EncodeInt(37141); //hair
                oPacket.EncodeInt(1051291); // dressup suit cant unequip
            } else {
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(-1); //nMixBaseHairColor
            oPacket.EncodeInt(0); //nMixAddHairColor
            oPacket.EncodeInt(0); //nMixHairBaseProb
        }

        if ((mask & 0x20000) != 0) {//correct
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(getTime(-2));
            oPacket.EncodeString("");
            oPacket.EncodeInt(0);
        }

        if ((mask & 0x10) != 0) {//correct
            addEvolutionInfo(oPacket, chr);
        }

        if ((mask & 0x20) != 0) {//correct
            oPacket.EncodeInt(0); // if > 0, write int and buffer
        }

        if ((mask & 0x40) != 0) {//correct
            addFarmInfo(oPacket, chr.getClient(), 0);
            oPacket.EncodeInt(1);//v146 can be 5 tho... its 1 in v169
            oPacket.EncodeInt(0);
        }

        if ((mask & 0x80) != 0) {//correct
            oPacket.EncodeByte(0); // if this byte > 0, decode an item slot, and two int
            /*
             *     v2->nCubeItemID = CInPacket::Decode4(iPacket);
             *     v2->nEItemPOS = CInPacket::Decode4(iPacket);
             * */
        }

        if ((mask & 0x400) != 0) {//correct
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(getTime(-2));
            oPacket.EncodeInt(0);
            /*
             *     v369 = CInPacket::Decode4(v304);
    			   v384[1] = 8;
    			   v384[0] = &v368[5]._ZtlSecureTear_nEXP64[1] + 1;
             	   *(&v368[5]._ZtlSecureTear_nEXP64[0] + 5) = v369;
    			   CInPacket::DecodeBuffer(v304, v384[0], v384[1]);
             	   *(&v368[5]._ZtlSecureTear_nEXP64_CS + 1) = CInPacket::Decode4(v304);
             **/
        }

        // RunnerGameRecord::Decode 0x20000 [Added on v176]
        if ((mask & 0x20000) != 0) {
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(0); // nLastScore
            oPacket.EncodeInt(0); // nHighScore
            oPacket.EncodeInt(0); // nRunnerPoint
            oPacket.EncodeLong(getTime(-2));
            oPacket.EncodeInt(10); // nTotalLeft
        }

        if ((mask & 0x80000) != 0) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeLong(0);
        }

        oPacket.EncodeShort(0); // mCollectionRecord.size: if > 0 encode

        if ((mask & 0x40000) != 0) {
            oPacket.EncodeShort(0); // mNxRecord.size: if > 0 encode
        }

        oPacket.EncodeByte(0); // new v178
        oPacket.EncodeInt(0);// EncodeTextEquipInfo

        if ((mask & 0x100000) != 0) {
            oPacket.EncodeShort(0); // size
        }

        if ((mask & 0x200000) != 0) {
            oPacket.EncodeInt(chr.aVMatrixRecord.size());
            for (VMatrixRecord pRecord : chr.aVMatrixRecord) {
                pRecord.Encode(oPacket);
            }
        }

        if ((mask & 0x4000000) != 0) {//correct
            oPacket.EncodeByte(0); // if this byte > 0, decode a byte, three ints and a long
            oPacket.EncodeShort(0); // if this short > 0, decode a byte and two ints
            oPacket.EncodeShort(0); // if this short >0, decode two ints and a long
        }

        if ((mask & 0x8000000) != 0) {//correct
            oPacket.EncodeByte(0);
        }

        if ((mask & 0x10000000) != 0) {//correct
            oPacket.EncodeInt(0); // if this int > 0, decode 2 shorts
            oPacket.EncodeInt(0); // if this int > 0, decode a short and an int
        }

        if ((mask & 0x2000) != 0) {//correct
            //addCoreAura(oPacket, chr);
            //oPacket.Encode(1); // was0
            // TODO: REVERT ^
            oPacket.Fill(0, 60);
            oPacket.EncodeLong(System.currentTimeMillis());
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(1);
        }

        if ((mask & 0x80000) != 0) {//correct
            oPacket.EncodeShort(0); // if short > 0, then decode two shorts
        }
        addRedLeafInfo(oPacket, chr);//32 byte buffer, correct
    }

    public static int getSkillBook(final int i) {
        switch (i) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static void addAbilityInfo(final OutPacket oPacket, User chr) {
        final List<InnerSkillValueHolder> skills = chr.getInnerSkills();
        oPacket.EncodeShort(skills.size());
        for (int i = 0; i < skills.size(); ++i) {
            oPacket.EncodeByte(i + 1); // key
            oPacket.EncodeInt(skills.get(i).getSkillId()); //d 7000000 id ++, 71 = char cards
            oPacket.EncodeByte(skills.get(i).getSkillLevel()); // level
            oPacket.EncodeByte(skills.get(i).getRank()); //rank, C, B, A, and S
        }

    }

    public static void addHonorInfo(final OutPacket oPacket, User chr) {
        oPacket.EncodeInt(chr.getHonorLevel()); //honor lvl
        oPacket.EncodeInt(chr.getHonourExp()); //honor exp
    }

    public static void addEvolutionInfo(final OutPacket oPacket, User chr) {
        oPacket.EncodeShort(0); // if > 0, write short and two ints  for core id and left count (amount maybe)
        oPacket.EncodeShort(0); // if > 0, write short and two ints for core id and left count (amount maybe)
    }

    public static void addCoreAura(OutPacket oPacket, User chr) {
        MapleCoreAura aura = chr.getCoreAura();
        oPacket.EncodeInt(aura.getId()); // never changes
        oPacket.EncodeInt(chr.getId());
        int level = chr.getSkillLevel(80001151) > 0 ? chr.getSkillLevel(80001151) : chr.getSkillLevel(1214);
        oPacket.EncodeInt(level);
        oPacket.EncodeInt(aura.getExpire()); // timer
        oPacket.EncodeInt(0);

        oPacket.EncodeInt(aura.getAtt()); // weapon attack
        oPacket.EncodeInt(aura.getDex()); // dex
        oPacket.EncodeInt(aura.getLuk()); // luk
        oPacket.EncodeInt(aura.getMagic()); // magic att
        oPacket.EncodeInt(aura.getInt()); // int
        oPacket.EncodeInt(aura.getStr()); // str

        oPacket.EncodeInt(0);
        oPacket.EncodeInt(aura.getTotal());//max
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);

        oPacket.EncodeLong(getTime(System.currentTimeMillis() + 86400000L));
        oPacket.EncodeByte(0);

    }

    public static void addStolenSkills(OutPacket oPacket, User chr, int jobNum, boolean writeJob) {
        if (writeJob) {
            oPacket.EncodeInt(jobNum);
        }
        int count = 0;
        if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobNum) {
                    oPacket.EncodeInt(sk.left);
                    count++;
                    if (count >= GameConstants.getNumSteal(jobNum)) {
                        break;
                    }
                }
            }
        }
        while (count < GameConstants.getNumSteal(jobNum)) { //for now?
            oPacket.EncodeInt(0);
            count++;
        }
    }

    /**
     * Adds the chosen skills from Phantom
     *
     * @param oPacket
     * @param chr
     */
    public static void addChosenSkills(OutPacket oPacket, User chr) {
        for (int i = 1; i <= 5; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
                for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == i && sk.right) {
                        oPacket.EncodeInt(sk.left);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                oPacket.EncodeInt(0);
            }
        }
    }

    public static void addMonsterBookInfo(OutPacket oPacket, User chr) {
        /*if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(oPacket);
        } else {
            chr.getMonsterBook().writeUnfinished(oPacket);
        }*/
        oPacket.EncodeBool(false);
        oPacket.EncodeShort(0);
    }

    public static void addPetItemInfo(OutPacket oPacket, Item item, Pet pet, boolean active) {
        if (item == null) {
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(oPacket, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        oPacket.EncodeInt(-1);
        oPacket.EncodeString(pet.getName(), 13);
        oPacket.EncodeByte(pet.getLevel());
        oPacket.EncodeShort(pet.getCloseness());
        oPacket.EncodeByte(pet.getFullness());
        if (item == null) {
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(oPacket, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        oPacket.EncodeShort(pet.getItem().getFlag());
        oPacket.EncodeShort(0); //_ZtlSecureTear_usPetSkill (I think this is the pet skill)
        oPacket.EncodeInt((pet.getItem().getItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        oPacket.EncodeShort(0); //_ZtlSecureTear_nAttribute
        oPacket.EncodeByte(active ? 0 : pet.getSummoned() ? pet.getSummonedValue() : 0);
        oPacket.EncodeInt(0);// nAutoBuffSkill (I think this is the skill that the player can set so the pet autobuffs
        oPacket.EncodeInt(pet.getColor()); //nPetHue
        oPacket.EncodeShort(pet.getGiant()); //nGiantRate_CS
    }

    public static void addShopInfo(OutPacket oPacket, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(shop.getNpcId()); //m_nSelectNpcItemID
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0); //m_nStarCoin
        /*oPacket.Encode(shop.getRanks().size() > 0);

        if (shop.getRanks().size() > 0) {
            oPacket.Encode(shop.getRanks().size());
            for (Pair<Integer, String> s : shop.getRanks()) {
                oPacket.EncodeInt(s.left);
                oPacket.EncodeString((String) s.right);
            }
         */
        oPacket.EncodeByte(0);

        final List<ShopRepurchase> shopRepurchases = c.getPlayer().getShopRepurchases();

        oPacket.EncodeShort(shop.getItems().size() + shopRepurchases.size());
        System.err.println("Size: " + shop.getItems().size() + shopRepurchases.size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(oPacket, item, shop, ii, null, c.getPlayer());
        }
        for (ShopRepurchase repurchaseItem : shopRepurchases) {
            Item repurchaseItemInfo = repurchaseItem.getItem();
            int ItemRepurchaseId = repurchaseItem.getItem().getItemId();
            int ItemRepurchasePrice = repurchaseItem.getPreviousSalePrice();
            short ItemRepurchaseQuantity = repurchaseItem.getItem().getQuantity();

            MapleShopItem si = new MapleShopItem(ItemRepurchaseId, ItemRepurchasePrice, (short) 0, (short) 0, ItemRepurchaseQuantity);

            addShopItemInfo(oPacket, si, shop, ii, repurchaseItemInfo, c.getPlayer());

        }
    }

    /**
     * Part of the shop packet, it contains information for the items in the shop
     *
     * Categories: 0 - No Tab 1 - Equip 2 - Use 3 - Setup 4 - Etc 5 - Recipe 6 - Scroll 7 - Special 8 - 8th Anniversary 9 - Button 10 -
     * Invitation Ticket 11 - Materials 12 - Maple 13 - Homecoming 14 - Cores 80 - JoeJoe 81 - Hermoninny 82 - Little Dragon 83 - Ika
     *
     * @param oPacket
     * @param item
     * @param shop
     * @param ii
     * @param repurchaseItemInfo
     * @param chr
     */
    /*public static void addShopItemInfo(OutPacket oPacket, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item repurchaseItemInfo, MapleCharacter chr) {
        oPacket.EncodeInt(item.getItemId());
        oPacket.EncodeInt(item.getPrice());
        oPacket.Encode(ServerConstants.SHOP_DISCOUNT);
        oPacket.EncodeInt(item.getReqItem());
        oPacket.EncodeInt(item.getReqItemQ());
        oPacket.EncodeInt(item.getPointQuestId());//nPointQuestID
        oPacket.EncodeInt(item.getPointQuestPrice());//nPointPrice
        oPacket.EncodeInt(item.getStarCoin());//nStarCoin
        oPacket.EncodeInt(item.getQuestExId());//nQuestExID
        oPacket.EncodeString("");//item.getQuestExKey());//&item.sQuestExKey
        oPacket.EncodeInt(item.getQuestExValue());//nQuestExValue
        oPacket.EncodeInt(1440 * item.getExpiration());
        oPacket.EncodeInt(0);//nLevelLimited
        oPacket.EncodeShort(item.getMinLevel());
        oPacket.EncodeShort(item.getMaxLevel());//nShowLevMax
        oPacket.EncodeInt(item.getQuestId());//nQuestID
        oPacket.EncodeLong(getTime(-2));
        oPacket.EncodeLong(getTime(-1));
        oPacket.EncodeInt(item.getCategory());
        oPacket.Encode(0);//bWorldBlock
        oPacket.EncodeInt(item.getPotentialGrade());
        oPacket.EncodeInt(item.getSaleLimit());//nBuyLimit
        oPacket.Encode(0);//p->nType which is v4
        if (!GameConstants.isRechargable(item.getItemId())) {
            oPacket.EncodeShort(item.getQuantity()); //quantity of item to buy
            oPacket.EncodeShort(item.getBuyable()); //buyable
        } else {
            long l = Double.doubleToLongBits(ii.getPrice(item.getItemId()));

            oPacket.EncodeLong(l >> 48); //CInPacket::DecodeBuffer(v9, &item.dUnitPrice, 8u);
            oPacket.EncodeShort(ii.getSlotMax(item.getItemId()));
        }

        oPacket.Encode(repurchaseItemInfo != null);
        if (repurchaseItemInfo != null) {
            addItemInfo(oPacket, repurchaseItemInfo);
        }
        if (shop.getRanks().size() > 0) {
            oPacket.Encode(item.getRank() > -1);
            if (item.getRank() > -1) {
                oPacket.Encode(item.getRank());
            }
        }
        addRedLeafInfo(oPacket, chr);
    }*/
    public static void addShopItemInfo(OutPacket oPacket, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item repurchaseItemInfo, User chr) {
        oPacket.EncodeInt(item.getItemId());
        oPacket.EncodeInt(item.getCategory());
        oPacket.EncodeInt(1440 * item.getExpiration());
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(item.getPrice());
        oPacket.EncodeByte(ServerConstants.SHOP_DISCOUNT);
        oPacket.EncodeInt(item.getReqItem());
        oPacket.EncodeInt(item.getReqItemQ());
        oPacket.EncodeInt(item.getPointQuestId());//nPointQuestID
        oPacket.EncodeInt(item.getPointQuestPrice());//nPointPrice
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0); // boolean for a sub
        oPacket.EncodeInt(item.getSaleLimit());//nBuyLimit
        oPacket.EncodeInt(0);
        oPacket.EncodeShort(item.getMinLevel());
        oPacket.EncodeShort(item.getMaxLevel());
        oPacket.EncodeByte(0); // ResetInfo
        oPacket.EncodeByte(0); // bWorldBlock?
        oPacket.EncodeLong(getTime(-2)); // ftSellStart
        oPacket.EncodeLong(getTime(-1)); // ftSellEnd
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(item.getQuestExId());
        oPacket.EncodeString(""); // sQuestExKey
        oPacket.EncodeInt(item.getQuestExValue());

        if (!GameConstants.isRechargable(item.getItemId())) {
            oPacket.EncodeShort(item.getQuantity()); //quantity of item to buy
            oPacket.EncodeShort(item.getBuyable()); //buyable
        } else {
            long l = Double.doubleToLongBits(ii.getPrice(item.getItemId()));

            oPacket.EncodeLong(l >> 48); //CInPacket::DecodeBuffer(v9, &item.dUnitPrice, 8u);
            oPacket.EncodeShort(ii.getSlotMax(item.getItemId()));
        }

        //oPacket.EncodeBool(false);
        addRedLeafInfo(oPacket, chr);
        oPacket.EncodeBool(repurchaseItemInfo != null);
        if (repurchaseItemInfo != null) {
            addItemInfo(oPacket, repurchaseItemInfo);
        }
    }

    public static void addJaguarInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeByte(chr.getIntNoRecord(GameConstants.JAGUAR));
        for (int i = 0; i < 5; i++) {
            oPacket.EncodeInt(0);
        }
    }

    public static void addZeroInfo(OutPacket oPacket, User chr) {
        short mask = 0;
        oPacket.EncodeShort(mask);
        if ((mask & 1) != 0) {
            oPacket.EncodeBool(chr.isZeroBetaState());
        }
        if ((mask & 2) != 0) {
            oPacket.EncodeInt(chr.getStat().getHp()); //hp
        }
        if ((mask & 4) != 0) {
            oPacket.EncodeInt(chr.getStat().getMp()); //mp
        }
        if ((mask & 8) != 0) {
            oPacket.EncodeByte(chr.getSkinColor());
        }
        if ((mask & 0x10) != 0) {
            oPacket.EncodeInt(chr.getZeroBetaHair());
        }
        if ((mask & 0x20) != 0) {
            oPacket.EncodeInt(chr.getZeroBetaHair());
        }
        if ((mask & 0x40) != 0) {
            oPacket.EncodeInt(chr.getMaxHp()); //maxhp
        }
        if ((mask & 0x80) != 0) {
            oPacket.EncodeInt(chr.getMaxMp()); //maxmp
        }
        if ((mask & 0x100) != 0) {
            oPacket.EncodeInt(0); //dbcharZeroLinkCashPart
        }
        if ((mask & 0x200) != 0) {
            oPacket.EncodeInt(0); //nMixBaseHairColor
            oPacket.EncodeInt(0); //nMixAddHairColor
            oPacket.EncodeInt(0); //nMixHairBaseProb
        }
    }

    public static void addBeastTamerInfo(OutPacket oPacket, User chr) {
        int beast = GameConstants.isBeastTamer(chr.getJob()) ? 1 : 0;
        String ears = Integer.toString(chr.getEars());
        String tail = Integer.toString(chr.getTail());

        oPacket.Encode(HexTool.getByteArrayFromHexString("00 00 00 00 16 00 0D 47 14 00 65 54 69 6D 65 3D 31 32 2F 31 32 2F 33 31 2F 30 30 2F 30 30 1D 47 06 00 73 74 65 70 3D 30 B6 46 07 00 63 6F 75 6E 74 3D 30 87 46 1E 00 52 47 3D 30 3B 53 4D 3D 30 3B 41 4C 50 3D 30 3B 44 42 3D 30 3B 43 44 3D 30 3B 4D 48 3D 30 16 47 31 00 52 48 3D 30 3B 47 54 3D 30 3B 57 4D 3D 30 3B 46 41 3D 30 3B 45 43 3D 30 3B 43 48 3D 30 3B 4B 44 3D 30 3B 49 4B 3D 30 3B 50 44 3D 30 3B 50 46 3D 30 9F 46 1C 00 69 6E 64 65 78 3D 31 3B 6C 61 73 74 52 3D 31 33 2F 31 32 2F 31 31 3B 73 6E 31 3D 30 A0 46 05 00 6E 75 6D 3D 30 A4 E7 2B 00"));
        oPacket.EncodeString("bTail=" + beast + ";");
        oPacket.EncodeString("bEar=" + beast + ";");
        oPacket.EncodeString("TailID=" + tail + ";");
        oPacket.EncodeString("EarID=" + ears);
        oPacket.Encode(HexTool.getByteArrayFromHexString("40 47 2F 00 63 6F 75 6E 74 3D 30 3B 64 6F 31 3D 30 3B 64 6F 32 3D 30 3B 64 61 69 6C 79 46 50 3D 30 3B 6C 61 73 74 44 61 74 65 3D 32 30 31 34 30 33 32 39 10 47 06 00 76 61 6C 32 3D 30 B9 46 1B 00 64 3D 32 30 31 34 30 32 31 39 3B 69 3D 31 32 31 31 32 31 30 30 30 30 30 30 30 30 9A 46 1A 00 63 6F 75 6E 74 30 3D 31 3B 63 6F 75 6E 74 31 3D 31 3B 63 6F 75 6E 74 32 3D 31 22 47 17 00 63 6F 6D 70 3D 31 3B 69 3D 32 33 30 30 30 30 30 30 30 30 30 30 30 30 0A 47 03 00 45 3D 31 12 47 40 00 4D 4C 3D 30 3B 4D 4D 3D 30 3B 4D 41 3D 30 3B 4D 42 3D 30 3B 4D 43 3D 30 3B 4D 44 3D 30 3B 4D 45 3D 30 3B 4D 46 3D 30 3B 4D 47 3D 30 3B 4D 48 3D 30 3B 4D 49 3D 30 3B 4D 4A 3D 30 3B 4D 4B 3D 30 FA 46 20 00 63 6F 75 6E 74 3D 35 3B 74 69 6D 65 3D 32 30 31 33 2F 31 32 2F 31 31 20 31 31 3A 35 34 3A 30 30 23 47 09 00 62 41 74 74 65 6E 64 3D 30 B4 46 07 00 63 6F 75 6E 74 3D 30 85 46 17 00 31 3D 30 3B 32 3D 30 3B 33 3D 30 3B 34 3D 30 3B 35 3D 30 3B 36 3D 30 2C 47 07 00 4C 6F 67 69 6E 3D 31 64 47 04 00 41 51 3D 30 B5 46 07 00 63 6F 75 6E 74 3D 30"));
    }

    public static void addFarmInfo(OutPacket oPacket, MapleClient c, int gender) {
        oPacket.EncodeString(c.getFarm().getName());
        oPacket.EncodeInt(c.getFarm().getWaru());
        oPacket.EncodeInt(c.getFarm().getLevel());
        oPacket.EncodeInt(c.getFarm().getExp());
        oPacket.EncodeInt(c.getFarm().getAestheticPoints());
        oPacket.EncodeInt(0); //gems

        oPacket.EncodeByte(gender);
        oPacket.EncodeInt(0); //farm theme
        oPacket.EncodeInt(0); // slot extension
        oPacket.EncodeInt(1); //nLockerSlotCount
    }

    public static void addRedLeafInfo(OutPacket oPacket, User chr) {
        oPacket.EncodeInt(chr.getClient().getAccID());//4 ints, correct
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(4);
        oPacket.EncodeInt(0);
        for (int i = 0; i < 4; i++) {
            oPacket.EncodeInt(9410165 + i);//v146 -2
            oPacket.EncodeInt(chr.getFriendShipPoints()[i]);
        }

    }

    public static void addLuckyLogoutInfo(OutPacket oPacket, boolean enable, CashItem item0, CashItem item1, CashItem item2) {
        oPacket.EncodeInt(enable ? 1 : 0);
        if (enable) {
            CSPacket.addCSItemInfo(oPacket, item0);
            CSPacket.addCSItemInfo(oPacket, item1);
            CSPacket.addCSItemInfo(oPacket, item2);
        }
    }

    public static void addPartTimeJob(OutPacket oPacket, PartTimeJob parttime) {
        oPacket.EncodeByte(parttime.getJob());
        if (parttime.getJob() > 0 && parttime.getJob() <= 5) {
            oPacket.EncodeLong(parttime.getTime());
        } else {
            oPacket.EncodeLong(getTime(-2));
        }
        oPacket.EncodeInt(parttime.getReward());
        oPacket.EncodeBool(parttime.getReward() > 0);
    }

    public static <E extends TemporaryStat> void writeSingleMask(OutPacket oPacket, E statup) {
        for (int i = GameConstants.CFlagSize; i >= 1; i--) {
            oPacket.EncodeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }

    public static <E extends TemporaryStat> void writeMask(OutPacket oPacket, Collection<E> statups) {
        int[] mask = new int[GameConstants.CFlagSize];

        for (TemporaryStat statup : statups) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            oPacket.EncodeInt(mask[(i - 1)]);
        }
    }

    public static <E extends TemporaryStat> void writeBuffMask(OutPacket oPacket, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[GameConstants.CFlagSize];
        for (Pair<E, Integer> statup : statups) {
            mask[(((TemporaryStat) statup.left).getPosition() - 1)] |= ((TemporaryStat) statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            oPacket.EncodeInt(mask[(i - 1)]);
        }
    }

    public static <E extends TemporaryStat> void writeBuffMask(OutPacket oPacket, Map<E, Integer> statups) {
        int[] mask = new int[GameConstants.CFlagSize];
        for (TemporaryStat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            oPacket.EncodeInt(mask[(i - 1)]);
        }
    }
}
