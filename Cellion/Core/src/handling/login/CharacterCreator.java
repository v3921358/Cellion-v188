package handling.login;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import enums.InventoryType;
import constants.JobConstants;
import constants.ServerConstants;
import constants.skills.Aran;
import constants.skills.DanceMoves;
import constants.skills.Hayato;
import constants.skills.Kaiser;
import constants.skills.Noblesse;
import constants.skills.Shade;
import constants.skills.Xenon;
import constants.skills.Zero;
import handling.PacketThrottleLimits;
import net.InPacket;
import net.ProcessPacket;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
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
public final class CharacterCreator implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        String name = iPacket.DecodeString();
        iPacket.DecodeInt(); // 0
        iPacket.DecodeInt(); // -1
        LoginInformationProvider.JobType pJob = LoginInformationProvider.JobType.getByType(iPacket.DecodeInt());
        short subcategory = iPacket.DecodeShort();
        byte nGender = iPacket.DecodeByte();
        byte nSkin = iPacket.DecodeByte();
        iPacket.DecodeByte(); // 6/7/8
        int nFace = iPacket.DecodeInt();
        int nHair = iPacket.DecodeInt();
        int nHairColour = -1, nHat = -1, nTop, nBottom = -1, nCape = -1, nFaceMark = -1, nEars = -1, nTail = -1, nShield = -1, nShoes, nWeapon;
        int[] wrongEars = {1004062, 1004063, 1004064};
        int[] correctEars = {5010116, 5010117, 5010118};
        int[] wrongTails = {1102661, 1102662, 1102663};
        int[] correctTails = {5010119, 5010120, 5010121};
        boolean bNoSkin = pJob == LoginInformationProvider.JobType.Demon || pJob == LoginInformationProvider.JobType.Mercedes || pJob == LoginInformationProvider.JobType.Jett || pJob == LoginInformationProvider.JobType.PinkBean;
        int[][] nGuideBookCollection = new int[][]{{4161001, 0}, {4161047, 1}, {4161048, 2000}, {4161052, 2001}, {4161054, 3}, {4161079, 2002}};
        int nGuideBook = 0;
        int nIndex = 0;
        User pNewCharacter = User.getDefault(c, pJob);
        int nCharacterPos = c.loadCharacters(c.getWorld()).size();
        final MapleItemInformationProvider pItemProvider = MapleItemInformationProvider.getInstance();
        final MapleInventory pEquip = pNewCharacter.getInventory(InventoryType.EQUIPPED);
        Item pItem;

        if (pJob == null) {
            c.SendPacket(CLogin.addNewCharEntry(null, 10));
            return;
        }

        for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
            if (j.getJobType() == pJob.getType()) {
                if (j.getFlag() != JobConstants.LoginJob.JobFlag.Available.getFlag()) {
                    c.SendPacket(CLogin.addNewCharEntry(null, 10));
                    return;
                }
            }
        }

        /**
         * Receive Appearance Data from Client
         */
        if (pJob.hasHairColor()) nHairColour = iPacket.DecodeInt();
        if (pJob.hasSkinColor()) iPacket.DecodeInt();
        if (pJob.hasFaceMark()) nFaceMark = iPacket.DecodeInt();
        if (pJob.hasEars()) nEars = iPacket.DecodeInt();
        if (pJob.hasTail()) nTail = iPacket.DecodeInt();
        
        /**
         * Receive Equipment Data from Client
         */
        if (pJob.hasHat()) nHat = iPacket.DecodeInt();
        nTop = iPacket.DecodeInt();
        if (pJob.hasBottom()) nBottom = iPacket.DecodeInt();
        if (pJob.hasCape()) nCape = iPacket.DecodeInt();
        nShoes = iPacket.DecodeInt();
        nWeapon = iPacket.DecodeInt();
        if (iPacket.GetRemainder() >= 4) nShield = iPacket.DecodeInt();

        int[] nItems = new int[]{nFace, nHair, nHairColour, bNoSkin ? -1 : nSkin, nFaceMark, nEars, nTail, nHat, nTop, nBottom, nCape, nShoes, nWeapon, nShield};
        if (pJob != LoginInformationProvider.JobType.BeastTamer) {
            for (int i : nItems) {
                if (i > -1) {
                    if (!LoginInformationProvider.getInstance().isEligibleItem(nGender, nIndex, pJob.getType(), i)) {
                        LogHelper.PACKET_EDIT_HACK.get().info(String.format("[CharacterCreator] Account [ID = %d, name = %s] has tried to create a character with ineligible items. Job [%s %d], ItemID: %d", c.getAccID(), c.getAccountName(), pJob.toString(), pJob.getJobId(), i));
                        c.Close();
                        return;
                    }
                    nIndex++;
                }
            }
        }

        // Make character should come last in this statement, else 'canMakeCharacter' will get spam updated.
        if (!CharacterCreationUtil.canCreateChar(name, c.isGm())
                || (LoginInformationProvider.getInstance().isForbiddenName(name) && !c.isGm())
                || (!CharacterCreationUtil.canMakeCharacter(c.getWorld(), c.getAccID()) && !c.isGm())) {
            c.SendPacket(CLogin.addNewCharEntry(null, 10));
            return;
        }

        /* Hair Colours 
         * Blond 36482 (36490 color : 3)
         * Black 36480 (36487 color : 0)
         * Brown 36487 (36494 color : 7)
         * Missing Orange (36489 color : 2)
         **/
        if (nHairColour < 0) nHairColour = 0;
        if (pJob != LoginInformationProvider.JobType.Mihile) nHair += nHairColour;
        if (nFaceMark < 0) nFaceMark = 0;
        
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
        
        if (nEars < 0) nEars = 0;
        if (nTail < 0) nTail = 0;

        pNewCharacter.setWorld((byte) c.getWorld());
        pNewCharacter.setFace(nFace);
        pNewCharacter.setCharListPosition(nCharacterPos);
        pNewCharacter.setHair(nHair);
        pNewCharacter.setZeroBetaHair(37623);
        pNewCharacter.setZeroBetaFace(21290);
        pNewCharacter.setAngelicDressupFace(21173);
        pNewCharacter.setAngelicDressupHair(37141);
        pNewCharacter.setAngelicDressupSuit(5010094);
        pNewCharacter.setGender(nGender);
        pNewCharacter.setName(name);
        pNewCharacter.setSkinColor(nSkin);
        pNewCharacter.setFaceMarking(nFaceMark);
        pNewCharacter.setEars(nEars);
        pNewCharacter.setTail(nTail);

        /* Reference
         * -1 Hat | -2 Face | -3 Eye acc | -4 Ear acc | -5 Topwear 
         * -6 Bottom | -7 Shoes | -9 Cape | -10 Shield | -11 Weapon
         **/
        int[][] equips = new int[][]{{nHat, -1}, {nTop, -5}, {nBottom, -6}, {nCape, -9}, {nShoes, -7}, {nWeapon, -11}, {nShield, -10}};
        for (int[] i : equips) {
            if (i[0] > 0) {
                pItem = pItemProvider.getEquipById(i[0]);
                pItem.setPosition((byte) i[1]);
                pItem.setGMLog("Character Creation");
                pEquip.addFromDB(pItem);
            }
        }
        if (pJob == LoginInformationProvider.JobType.AngelicBuster || pJob == LoginInformationProvider.JobType.Kaiser) {
            pItem = pItemProvider.getEquipById(pJob == LoginInformationProvider.JobType.Kaiser ? 1352500 : 1352601);
            pItem.setPosition((byte) -10);
            pItem.setGMLog("Nova Shield");
            pEquip.addFromDB(pItem);
        }
        if (pJob == LoginInformationProvider.JobType.AngelicBuster) {
            pItem = pItemProvider.getEquipById(1352600);
            pItem.setPosition((byte) -10);
            pItem.setGMLog("Secondary Weapon");
            pEquip.addFromDB(pItem);
        }
        if (pJob == LoginInformationProvider.JobType.Kinesis) {
            pItem = pItemProvider.getEquipById(1353200);
            pItem.setPosition((byte) -10);
            pItem.setGMLog("Chess Piece");
            pEquip.addFromDB(pItem);
        }

        // Additional skills that are not added by default.
        int[][] aSkills = new int[][]{
            {80001152}, //Resistance
            {80001152, 1281}, //Explorer
            {10001244, 10000252, 10001253, 10001254, 80001152, Noblesse.ELEMENTAL_HARMONY, Noblesse.IMPERIAL_RECALL}, //Cygnus
            {20000194, Aran.COMBAT_STEP}, //Aran
            {20010022, 20010194}, //Evan
            {20020109, 20021110, 20020111, 20020112}, //Mercedes
            {30010112, 30010110, 30010111, 30010185, 30010242, 30010241, 30010231, 30010230}, //Demon
            {20031251, 20030204, 20030206, 20031208, 20031207, 20031203, 20031205}, //Phantom2
            {80001152, 1281}, //Dual Blader
            {50001214}, //Mihile
            {20040216, 20040217, 20040218, 20040219, 20040220, 20040221, 20041222, 27001100, 27000207, 27001201}, //Luminous
            {60001216, 60001217, 60001225, 60001218, 60000219, Kaiser.EXCLUSIVE_SPELL, Kaiser.EXCLUSIVE_SPELL_1}, //Kaiser
            {60011216, 60010217, 60011218, 60011219, 60011220, 60011221, 60011222}, //Angelic Buster
            {}, //Cannoneer
            {30020232, 30020233, 30020234, 30020240}, //Xenon (Removed 30021238 - Beam Dance, don't think it belongs here).
            {100000279, 100000282, 100001263, 100001264, 100001265, 100001266, 10000168}, //Zero (Removed 100001262 - Temple Recall, don't want it used yet)
            {20051284, 20050285, 20050286}, // Shade
            {228, 80001151}, //Jett - core aura doesn't work?
            {Hayato.QUICK_DRAW, Hayato.SUMMER_RAIN, Hayato.MASTER_OF_BLADES, Hayato.SHIMADA_HEART}, //Hayato
            {40020000, 40020001, 40020002, 40020109}, //Kanna
            {80001152, 110001251, 110001510, 110001501, 110001502, 110001503, 110001504, 110001506, 110000515, 110000513}, //Beast Tamer
            {80001152}, //Pink Bean
            {} //Kinesis
            /*Not sure which of these we need to add
             140001289 - Psychic Attack
             140001290 - Return
             140000291 - ESP
             140000292 - Judgment*/
        };

        if (aSkills[pJob.getType()].length > 0) {
            final Map<Skill, SkillEntry> mSkill = new HashMap<>();
            Skill pSkill;
            if (pJob != LoginInformationProvider.JobType.Zero) { // Not for Zero
                for (int i : aSkills[pJob.getType()]) {
                    pSkill = SkillFactory.getSkill(i);
                    int maxLevel = 1;
                    if (pSkill != null && pSkill.getMaxLevel() != 0) {
                        maxLevel = pSkill.getMaxLevel();
                    }
                    if (pSkill != null & maxLevel < 1) {
                        maxLevel = pSkill.getMasterLevel();
                    }
                    mSkill.put(pSkill, new SkillEntry((byte) 1, (byte) maxLevel, -1));
                }
            }
            if (pJob == LoginInformationProvider.JobType.Aran) {
                mSkill.put(SkillFactory.getSkill(Aran.COMBAT_STEP), new SkillEntry((byte) 1, (byte) 1, -1)); 
            }
            if (pJob == LoginInformationProvider.JobType.Cygnus) {
                mSkill.put(SkillFactory.getSkill(Noblesse.ELEMENTAL_HARMONY), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Noblesse.IMPERIAL_RECALL), new SkillEntry((byte) 1, (byte) 1, -1));
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
                mSkill.put(SkillFactory.getSkill(Zero.HEAVY_SWORD_MASTERY), new SkillEntry((byte) 8, (byte) 10, -1));
                mSkill.put(SkillFactory.getSkill(Zero.LONG_SWORD_MASTERY), new SkillEntry((byte) 8, (byte) 10, -1));
            }
            if (pJob == LoginInformationProvider.JobType.BeastTamer) {
                mSkill.put(SkillFactory.getSkill(110001511), new SkillEntry((byte) 0, (byte) 30, -1));
                mSkill.put(SkillFactory.getSkill(110001512), new SkillEntry((byte) 0, (byte) 5, -1));
                mSkill.put(SkillFactory.getSkill(110000513), new SkillEntry((byte) 0, (byte) 30, -1));
                mSkill.put(SkillFactory.getSkill(110000515), new SkillEntry((byte) 0, (byte) 10, -1));
                mSkill.put(SkillFactory.getSkill(110001510), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110001501), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110001502), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110001503), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110001504), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110001506), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(110000515), new SkillEntry((byte) 10, (byte) 10, -1));
                mSkill.put(SkillFactory.getSkill(110000513), new SkillEntry((byte) 30, (byte) 30, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Resistance) { // Mechanic (Hack Fix)
                mSkill.put(SkillFactory.getSkill(35120000), new SkillEntry((byte) 1, (byte) 10, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Demon) { // Demon Blood Pact
                mSkill.put(SkillFactory.getSkill(30010242), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(30010241), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(30010231), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(30010230), new SkillEntry((byte) 1, (byte) 1, -1));
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
                mSkill.put(SkillFactory.getSkill(Kaiser.EXCLUSIVE_SPELL), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Kaiser.EXCLUSIVE_SPELL_1), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Shade) {
                mSkill.put(SkillFactory.getSkill(Shade.FOX_TROT), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Shade.SPIRIT_BOND_1), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Shade.CLOSE_CALL), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            if (pJob == LoginInformationProvider.JobType.Hayato) {
                mSkill.put(SkillFactory.getSkill(Hayato.QUICK_DRAW), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Hayato.SUMMER_RAIN), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Hayato.MASTER_OF_BLADES), new SkillEntry((byte) 1, (byte) 1, -1));
                mSkill.put(SkillFactory.getSkill(Hayato.SHIMADA_HEART), new SkillEntry((byte) 1, (byte) 1, -1));
            }

            mSkill.put(SkillFactory.getSkill(80001436), new SkillEntry((byte) 1, (byte) 1, -1));// Actions Skill (Allows Dancing)
            for (DanceMoves moves : DanceMoves.values()) {
                mSkill.put(SkillFactory.getSkill(moves.getSkillid()), new SkillEntry((byte) 1, (byte) 1, -1));
            }
            pNewCharacter.changeSkillLevelSkip(mSkill, false);
        }

        for (int[] i : nGuideBookCollection) {
            if (pNewCharacter.getJob() == i[1]) {
                nGuideBook = i[0];
            } else if (pNewCharacter.getJob() / 1000 == i[1]) {
                nGuideBook = i[0];
            }
        }
        
        if (nGuideBook > 0) pNewCharacter.getInventory(InventoryType.ETC).addItem(new Item(nGuideBook, (byte) 0, (short) 1, (byte) 0));

        if (pJob == LoginInformationProvider.JobType.Zero) {
            pNewCharacter.setLevel((short) 100);
            pNewCharacter.getStat().maxhp = 7000;
            pNewCharacter.getStat().hp = 7000;
            pNewCharacter.getStat().maxmp = 100;
            pNewCharacter.getStat().mp = 100;
            pNewCharacter.getStat().str = 500;
            pNewCharacter.setRemainingSp(5, 0); // Alpha
            pNewCharacter.setRemainingSp(5, 1); // Beta
        } else {
            if (pJob == LoginInformationProvider.JobType.Kinesis) {
                pNewCharacter.getStat().maxmp = 5;
                pNewCharacter.getStat().mp = 5;
            } else {
                pNewCharacter.getStat().maxmp = 300;
                pNewCharacter.getStat().mp = 300;
            }
            pNewCharacter.setLevel((short) 10);
            pNewCharacter.getStat().maxhp = 300;
            pNewCharacter.getStat().hp = 300;
            pNewCharacter.getStat().str = 4;
            pNewCharacter.getStat().dex = 4;
            pNewCharacter.getStat().int_ = 4;
            pNewCharacter.getStat().luk = 4;
            pNewCharacter.setRemainingAp(50);
            pNewCharacter.setRemainingSp(5, 0);
            pNewCharacter.setRemainingSp(5, 1);
        }

        if (ServerConstants.UNIVERSAL_START) {
            switch (pJob) {
                case DualBlade:
                    pNewCharacter.setJob((short) 430);
                    break;
                case Cannoneer:
                    pNewCharacter.setJob((short) 501);
                    break;
                case Jett:
                    pNewCharacter.setJob((short) 508);
                    break;
                case Phantom:
                    pNewCharacter.setJob((short) 2400);
                    break;
                case Shade:
                    pNewCharacter.setJob((short) 2500);
                    break;
                case Luminous:
                    pNewCharacter.setJob((short) 2700);
                    break;
                case Mihile:
                    pNewCharacter.setJob((short) 5100);
                    break;
                case Kaiser:
                    pNewCharacter.setJob((short) 6100); 
                    break;
                case AngelicBuster:
                    pNewCharacter.setJob((short) 6500);
                    break;
                case Kinesis:
                    pNewCharacter.setJob((short) 14200);
                    break;
            }
        }

        // Save Character
        User.saveNewCharToDB(pNewCharacter, pJob, subcategory);
        c.SendPacket(CLogin.addNewCharEntry(pNewCharacter, 0));
        c.createdChar(pNewCharacter.getId());
        pNewCharacter.newCharRewards();
    }
}