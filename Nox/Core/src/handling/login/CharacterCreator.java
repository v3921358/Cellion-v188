package handling.login;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.JobConstants;
import constants.ServerConstants;
import constants.skills.Aran;
import constants.skills.DanceMoves;
import constants.skills.Global;
import constants.skills.Kaiser;
import constants.skills.Shade;
import constants.skills.Xenon;
import constants.skills.Zero;
import handling.PacketThrottleLimits;
import net.InPacket;
import netty.ProcessPacket;
import server.MapleItemInformationProvider;
import server.maps.objects.MapleCharacter;
import tools.LogHelper;
import tools.packet.CLogin;

import java.util.HashMap;
import java.util.Map;

/*  
 *  Character Creator
 *  @author Mazen Massoud
 **/
@PacketThrottleLimits(
        FlagCount = 3,
        ResetTimeMillis = 1000 * 60 * 60,
        MinTimeMillisBetweenPackets = 5000,
        FunctionName = "CharacterCreator",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public final class CharacterCreator implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String name = iPacket.DecodeString();
        iPacket.DecodeInteger(); // 0
        iPacket.DecodeInteger(); // -1
        LoginInformationProvider.JobType pJob = LoginInformationProvider.JobType.getByType(iPacket.DecodeInteger());
        short subcategory = iPacket.DecodeShort();
        byte nGender = iPacket.DecodeByte();
        byte nSkin = iPacket.DecodeByte();
        iPacket.DecodeByte(); // 6/7/8
        int nFace = iPacket.DecodeInteger();
        int nHair = iPacket.DecodeInteger();
        int nHairColour = -1, nHat = -1, nTop, nBottom = -1, nCape = -1, nFaceMark = -1, nEars = -1, nTail = -1, nShield = -1, nShoes, nWeapon;
        int[] wrongEars = {1004062, 1004063, 1004064};
        int[] correctEars = {5010116, 5010117, 5010118};
        int[] wrongTails = {1102661, 1102662, 1102663};
        int[] correctTails = {5010119, 5010120, 5010121};
        boolean bNoSkin = pJob == LoginInformationProvider.JobType.Demon || pJob == LoginInformationProvider.JobType.Mercedes || pJob == LoginInformationProvider.JobType.Jett || pJob == LoginInformationProvider.JobType.PinkBean;
        int[][] nGuideBookCollection = new int[][]{{4161001, 0}, {4161047, 1}, {4161048, 2000}, {4161052, 2001}, {4161054, 3}, {4161079, 2002}};
        int nGuideBook = 0;
        int nIndex = 0;
        MapleCharacter oNewCharacter = MapleCharacter.getDefault(c, pJob);
        int nCharacterPos = c.loadCharacters(c.getWorld()).size();
        final MapleItemInformationProvider oItemProvider = MapleItemInformationProvider.getInstance();
        final MapleInventory pEquip = oNewCharacter.getInventory(MapleInventoryType.EQUIPPED);
        Item pItem;

        if (pJob == null) {
            c.write(CLogin.addNewCharEntry(null, 10));
            return;
        }

        for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
            if (j.getJobType() == pJob.getType()) {
                if (j.getFlag() != JobConstants.LoginJob.JobFlag.ENABLED.getFlag()) {
                    c.write(CLogin.addNewCharEntry(null, 10));
                    return;
                }
            }
        }

        if (pJob.hasHairColor()) {
            nHairColour = iPacket.DecodeInteger();
        }
        if (pJob.hasSkinColor()) {
            iPacket.DecodeInteger();
        }
        if (pJob.hasFaceMark()) {
            nFaceMark = iPacket.DecodeInteger();
        }
        if (pJob.hasEars()) {
            nEars = iPacket.DecodeInteger();
        }
        if (pJob.hasTail()) {
            nTail = iPacket.DecodeInteger();
        }
        if (pJob.hasHat()) {
            nHat = iPacket.DecodeInteger();
        }

        nTop = iPacket.DecodeInteger();

        if (pJob.hasBottom()) {
            nBottom = iPacket.DecodeInteger();
        }
        if (pJob.hasCape()) {
            nCape = iPacket.DecodeInteger();
        }

        nShoes = iPacket.DecodeInteger();
        nWeapon = iPacket.DecodeInteger();

        if (iPacket.Available() >= 4) {
            nShield = iPacket.DecodeInteger();
        }

        int[] nItems = new int[]{nFace, nHair, nHairColour, bNoSkin ? -1 : nSkin, nFaceMark, nEars, nTail, nHat, nTop, nBottom, nCape, nShoes, nWeapon, nShield};
        if (pJob != LoginInformationProvider.JobType.BeastTamer) {
            for (int i : nItems) {
                if (i > -1) {
                    if (!LoginInformationProvider.getInstance().isEligibleItem(nGender, nIndex, pJob.getType(), i)) {
                        LogHelper.PACKET_EDIT_HACK.get().info(String.format("[CharacterCreator] Account [ID = %d, name = %s] has tried to create a character with ineligible items. Job [%s %d], ItemID: %d", c.getAccID(), c.getAccountName(), pJob.toString(), pJob.getJobId(), i));
                        c.close();
                        return;
                    }
                    nIndex++;
                }
            }
        }

        // Make character should come last in this statement, else 'canMakeCharacter' will get spam updated.
        if (!MapleCharacterCreationUtil.canCreateChar(name, c.isGm())
                || (LoginInformationProvider.getInstance().isForbiddenName(name) && !c.isGm())
                || (!MapleCharacterCreationUtil.canMakeCharacter(c.getWorld(), c.getAccID()) && !c.isGm())) {
            c.write(CLogin.addNewCharEntry(null, 10));
            return;
        }

        /* Hair Colours 
         * Blond 36482 (36490 color : 3)
         * Black 36480 (36487 color : 0)
         * Brown 36487 (36494 color : 7)
         * Missing Orange (36489 color : 2)
         **/
        if (nHairColour < 0) {
            nHairColour = 0;
        }
        if (pJob != LoginInformationProvider.JobType.Mihile) {
            nHair += nHairColour;
        }
        if (nFaceMark < 0) {
            nFaceMark = 0;
        }
        for (int i = 0; i < wrongEars.length; i++) {
            if (nEars == wrongEars[i]) {
                nEars = correctEars[i];
            }
        }
        for (int i = 0; i < wrongTails.length; i++) {
            if (nTail == wrongTails[i]) {
                nTail = correctTails[i];
            }
        }
        if (nEars < 0) {
            nEars = 0;
        }
        if (nTail < 0) {
            nTail = 0;
        }

        oNewCharacter.setWorld((byte) c.getWorld());
        oNewCharacter.setFace(nFace);
        oNewCharacter.setCharListPosition(nCharacterPos);
        oNewCharacter.setHair(nHair);
        oNewCharacter.setZeroBetaHair(37623);
        oNewCharacter.setZeroBetaFace(21290);
        oNewCharacter.setAngelicDressupFace(21173);
        oNewCharacter.setAngelicDressupHair(37141);
        oNewCharacter.setAngelicDressupSuit(5010094);
        oNewCharacter.setGender(nGender);
        oNewCharacter.setName(name);
        oNewCharacter.setSkinColor(nSkin);
        oNewCharacter.setFaceMarking(nFaceMark);
        oNewCharacter.setEars(nEars);
        oNewCharacter.setTail(nTail);

        switch (pJob) {
            case AngelicBuster:
                oNewCharacter.setJob((short) 6500);
                oNewCharacter.setLevel((short) 10);
                oNewCharacter.getStat().dex = 68;
                oNewCharacter.getStat().maxhp = 1000;
                oNewCharacter.getStat().hp = 1000;
                oNewCharacter.setRemainingSp(3);
                break;
            case Zero:
                oNewCharacter.setLevel((short) 100);
                oNewCharacter.getStat().str = 518;
                oNewCharacter.getStat().maxhp = 6910;
                oNewCharacter.getStat().hp = 6910;
                oNewCharacter.getStat().maxmp = 100;
                oNewCharacter.getStat().mp = 100;
                oNewCharacter.setRemainingSp(3, 0); // Alpha
                oNewCharacter.setRemainingSp(3, 1); // Beta
                break;
            case Kinesis:
                oNewCharacter.setLevel((short) 10);
                oNewCharacter.getStat().str = 4;
                oNewCharacter.getStat().int_ = 52;
                oNewCharacter.getStat().maxhp = 374;
                oNewCharacter.getStat().hp = 374;
                oNewCharacter.getStat().maxmp = 5; // PP
                oNewCharacter.getStat().mp = 5;
                break;
            case Luminous:
                oNewCharacter.setJob((short) 2700);
                oNewCharacter.setLevel((short) 10);
                oNewCharacter.getStat().str = 4;
                oNewCharacter.getStat().int_ = 57;
                oNewCharacter.getStat().maxhp = 500;
                oNewCharacter.getStat().hp = 500;
                oNewCharacter.getStat().maxmp = 1000;
                oNewCharacter.getStat().mp = 1000;
                oNewCharacter.setRemainingSp(3);
                break;
            case BeastTamer:
                oNewCharacter.setLevel((short) 10);
                oNewCharacter.getStat().maxhp = 567;
                oNewCharacter.getStat().hp = 551;
                oNewCharacter.getStat().maxmp = 270;
                oNewCharacter.getStat().mp = 263;
                oNewCharacter.setRemainingAp(45);
                oNewCharacter.setRemainingSp(3, 0);
                break;
            default:
                break;
        }

        /* Reference
         * -1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Topwear 
         * -6 Bottom | -7 Shoes | -9 Cape | -10 Shield | -11 Weapon
         **/
        int[][] equips = new int[][]{{nHat, -1}, {nTop, -5}, {nBottom, -6}, {nCape, -9}, {nShoes, -7}, {nWeapon, -11}, {nShield, -10}};
        for (int[] i : equips) {
            if (i[0] > 0) {
                pItem = oItemProvider.getEquipById(i[0]);
                pItem.setPosition((byte) i[1]);
                pItem.setGMLog("Character Creation");
                pEquip.addFromDB(pItem);
            }
        }
        if (pJob == LoginInformationProvider.JobType.AngelicBuster || pJob == LoginInformationProvider.JobType.Kaiser) {
            pItem = oItemProvider.getEquipById(pJob == LoginInformationProvider.JobType.Kaiser ? 1352500 : 1352601);
            pItem.setPosition((byte) -10);
            pItem.setGMLog("Nova Shield");
            pEquip.addFromDB(pItem);
        }

        // Additional skills that are not added by default.
        int[][] skills = new int[][]{
            {80001152}, //Resistance
            {80001152, 1281}, //Explorer
            {10001244, 10000252, 10001253, 10001254, 80001152}, //Cygnus
            {20000194}, //Aran
            {20010022, 20010194}, //Evan
            {20020109, 20021110, 20020111, 20020112}, //Mercedes
            {30010112, 30010110, 30010111, 30010185}, //Demon
            {20031251, 20030204, 20030206, 20031208, 20031207, 20031203}, //Phantom2
            {80001152, 1281}, //Dual Blader
            {50001214}, //Mihile
            {20040216, 20040217, 20040218, 20040219, 20040220, 20040221, 20041222, 27001100, 27000207, 27001201}, //Luminous
            {60001216, 60001217, 60001225, 60001218, 60000219}, //Kaiser
            {60011216, 60010217, 60011218, 60011219, 60011220, 60011221, 60011222}, //Angelic Buster
            {}, //Cannoneer
            {30020232, 30020233, 30020234, 30020240}, //Xenon (Removed 30021238 - Beam Dance, don't think it belongs here).
            {100000279, 100000282, 100001263, 100001264, 100001265, 100001266, 10000168}, //Zero (Removed 100001262 - Temple Recall, don't want it used yet)
            {20051284, 20050285, 20050286}, // Shade
            {228, 80001151}, //Jett - core aura doesn't work?
            {}, //Hayato
            {40020000, 40020001, 40020002, 40020109}, //Kanna
            {80001152, 110001251}, //Beast Tamer
            {80001152}, //Pink Bean
            {} //Kinesis
        /* Not sure which of these we need to add
             140001289 - Psychic Attack
             140001290 - Return
             140000291 - ESP
             140000292 - Judgment
         */};

        if (skills[pJob.getType()].length > 0) {
            final Map<Skill, SkillEntry> mSkill = new HashMap<>();
            Skill pSkill;
            if (pJob != LoginInformationProvider.JobType.Zero) { // Not for Zero
                for (int i : skills[pJob.getType()]) {
                    pSkill = SkillFactory.getSkill(i);
                    int maxLevel = pSkill.getMaxLevel();
                    if (maxLevel < 1) {
                        maxLevel = pSkill.getMasterLevel();
                    }
                    mSkill.put(pSkill, new SkillEntry((byte) 1, (byte) maxLevel, -1));
                }
            }
            if (pJob == LoginInformationProvider.JobType.Aran) {
                mSkill.put(SkillFactory.getSkill(Aran.COMBAT_STEP), new SkillEntry((byte) 1, (byte) 1, -1)); // Aran Flash Jump
            }
            if (pJob == LoginInformationProvider.JobType.Zero) {
                // Shared Zero Skills - All except Resolton Time, which we'll handle in MapleCharacter levelUp().
                //ss.put(SkillFactory.getSkill(Zero.TEMPLE_RECALL), new SkillEntry((byte) 1, (byte) 1, -1)); //not until zero temple is fixed up.
                mSkill.put(SkillFactory.getSkill(Zero.DUAL_COMBAT), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Zero.DIVINE_FORCE), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Zero.DIVINE_SPEED), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Zero.BURST_JUMP), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Zero.BURST_STEP), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Zero.RHINNES_PROTECTION), new SkillEntry((byte) 1, (byte) 1, -1));
                // Alpha/Beta Skills
                mSkill.put(SkillFactory.getSkill(Zero.HEAVY_SWORD_MASTERY), new SkillEntry((byte) 8, (byte) 10, -1));
                mSkill.put(SkillFactory.getSkill(Zero.LONG_SWORD_MASTERY), new SkillEntry((byte) 8, (byte) 10, -1));
            }
            if (pJob == LoginInformationProvider.JobType.BeastTamer) {
                mSkill.put(SkillFactory.getSkill(110001511), new SkillEntry((byte) 0, (byte) 30, -1));
                mSkill.put(SkillFactory.getSkill(110001512), new SkillEntry((byte) 0, (byte) 5, -1));
                mSkill.put(SkillFactory.getSkill(110000513), new SkillEntry((byte) 0, (byte) 30, -1));
                mSkill.put(SkillFactory.getSkill(110000515), new SkillEntry((byte) 0, (byte) 10, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Resistance) { // Mechanic (Hack Fix)
                mSkill.put(SkillFactory.getSkill(35120000), new SkillEntry((byte) 1, (byte) 10, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Demon) { // Demon Blood Pact
                mSkill.put(SkillFactory.getSkill(30010242), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Xenon) { // Modal Shift
                mSkill.put(SkillFactory.getSkill(Xenon.MODAL_SHIFT), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Kaiser) { // Kaiser Attack & Defense Modes
                mSkill.put(SkillFactory.getSkill(Kaiser.ATTACKER_MODE_I), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Kaiser.DEFENDER_MODE_I), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Kaiser.DRAGON_LINK), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Kaiser.VERTICAL_GRAPPLE), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Kaiser.TRANSFIGURATION), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Shade) {
                mSkill.put(SkillFactory.getSkill(Shade.FOX_TROT), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Shade.SPIRIT_BOND_1), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Shade.CLOSE_CALL), new SkillEntry((byte) 1, (byte) 1, -1));
            }

            mSkill.put(SkillFactory.getSkill(80001436), new SkillEntry((byte) 1, (byte) 1, -1));// Actions Skill (Allows Dancing)
            for (DanceMoves moves : DanceMoves.values()) {
                mSkill.put(SkillFactory.getSkill(moves.getSkillid()), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            oNewCharacter.changeSkillLevelSkip(mSkill, false);
        }

        for (int[] i : nGuideBookCollection) {
            if (oNewCharacter.getJob() == i[1]) {
                nGuideBook = i[0];
            } else if (oNewCharacter.getJob() / 1000 == i[1]) {
                nGuideBook = i[0];
            }
        }
        if (nGuideBook > 0) {
            oNewCharacter.getInventory(MapleInventoryType.ETC).addItem(new Item(nGuideBook, (byte) 0, (short) 1, (byte) 0));
        }

        // Balance changes and custom tutorial preparation.
        if (ServerConstants.UNIVERSAL_START) {
            switch (pJob) {
                case DualBlade:
                    oNewCharacter.setJob((short) 430);
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().maxmp = 300;
                    oNewCharacter.getStat().mp = 300;
                    oNewCharacter.setRemainingAp(40);
                    oNewCharacter.setRemainingSp(35, 0);
                    break;
                case Cannoneer:
                    oNewCharacter.setJob((short) 501);
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().maxmp = 230;
                    oNewCharacter.getStat().mp = 230;
                    oNewCharacter.setRemainingAp(40);
                    break;
                case Jett:
                    oNewCharacter.setJob((short) 508);
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().maxmp = 200;
                    oNewCharacter.getStat().mp = 200;
                    oNewCharacter.setRemainingAp(40);
                    break;
                case AngelicBuster:
                    oNewCharacter.setJob((short) 6500);
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().dex = 68;
                    oNewCharacter.getStat().maxhp = 1000;
                    oNewCharacter.getStat().hp = 1000;
                    oNewCharacter.setRemainingSp(5);
                    break;
                case Zero:
                    oNewCharacter.setLevel((short) 100);
                    oNewCharacter.getStat().str = 518;
                    oNewCharacter.getStat().maxhp = 6910;
                    oNewCharacter.getStat().hp = 6910;
                    oNewCharacter.getStat().maxmp = 100;
                    oNewCharacter.getStat().mp = 100;
                    oNewCharacter.setRemainingSp(5, 0); //alpha
                    oNewCharacter.setRemainingSp(5, 1); //beta
                    break;
                case Kinesis:
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().str = 4;
                    oNewCharacter.getStat().int_ = 52;
                    oNewCharacter.getStat().maxhp = 374;
                    oNewCharacter.getStat().hp = 374;
                    oNewCharacter.getStat().maxmp = 5;
                    oNewCharacter.getStat().mp = 5;
                    oNewCharacter.setRemainingSp(5);
                    break;
                case Luminous:
                    oNewCharacter.setJob((short) 2700);
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().str = 4;
                    oNewCharacter.getStat().int_ = 57;
                    oNewCharacter.getStat().maxhp = 500;
                    oNewCharacter.getStat().hp = 500;
                    oNewCharacter.getStat().maxmp = 1000;
                    oNewCharacter.getStat().mp = 1000;
                    oNewCharacter.setRemainingSp(5);
                    break;
                case BeastTamer:
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.getStat().maxhp = 567;
                    oNewCharacter.getStat().hp = 551;
                    oNewCharacter.getStat().maxmp = 270;
                    oNewCharacter.getStat().mp = 263;
                    oNewCharacter.setRemainingAp(45);
                    oNewCharacter.setRemainingSp(5, 0);
                    break;
                default:
                    oNewCharacter.setLevel((short) 9);
                    oNewCharacter.setRemainingAp(40);
                    oNewCharacter.setRemainingSp(5, 0);
                    oNewCharacter.getStat().maxmp = 350;
                    oNewCharacter.getStat().mp = 350;
                    break;
            }
        }

        // Save Character
        MapleCharacter.saveNewCharToDB(oNewCharacter, pJob, subcategory);
        c.write(CLogin.addNewCharEntry(oNewCharacter, 0));
        c.createdChar(oNewCharacter.getId());
        oNewCharacter.newCharRewards();
    }
}
