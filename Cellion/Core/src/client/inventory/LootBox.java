/*
 * Cellion Development
 */
package client.inventory;

import constants.GameConstants;
import constants.JobConstants;
import enums.InventoryType;
import enums.NPCChatType;
import enums.NPCInterfaceType;
import handling.world.World;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import tools.Pair;
import tools.Utility;
import tools.packet.CField;

/**
 * Loot Box
 * @author Mazen Massoud
 */
public class LootBox {
    
    /**
     * Loot Box IDs
     */
    public static final int NEBULITE_BOX = 2430692, // Note: Nebulite Socket Creator : 2930000 
                            EQUIPMENT_BOX_LV30 = 2430447,
                            EQUIPMENT_BOX_LV50 = 2430450,
                            EQUIPMENT_BOX_LV70 = 2430452,
                            BASIC_HAIR_BOX = 2431230,
                            BASIC_FACE_BOX = 2431091,
                            PREMIUM_HAIR_BOX = 2431228,
                            PREMIUM_FACE_BOX = 2431092;
    
    public static final int FREE_LOOT_BOX = 2436114,    // Weird Gift Box
                            VOTE_LOOT_BOX = 2436501,    // Special Gift Box
                            DONOR_LOOT_BOX = 2431642;   // Special Value Pack Gift Box
    
    /**
     * OnLootBoxRequest
     * @param pPlayer
     * @param nBoxID
     * @return 
     */
    public static boolean OnLootBoxRequest(User pPlayer, int nBoxID) {
        if (!pPlayer.haveItem(nBoxID) || !pPlayer.hasInventorySpace(1442001, 3)) {
            return false;
        }
        
        final MapleItemInformationProvider pItemInfo = MapleItemInformationProvider.getInstance();
        Calendar pCalendar = Calendar.getInstance();
        int tMonth = pCalendar.get(Calendar.MONTH) + 1;
        boolean bSeasonOne = false, bSeasonTwo = false, bSeasonThree = false, bSeasonFour = false;
        int nTopTierChance, nMidTierChance, nFirstResult = 0, nSecondResult = 0, nThirdResult = 0;
        int[] aItemPool = {0};
        String sBoxName, sBoxType, sTierResult, sFirstTier = "", sSecondTier = "", sThirdTier = "";
        
        if (tMonth >= 1 && tMonth <= 3) bSeasonOne = true; 
        else if (tMonth >= 4 && tMonth <= 6) bSeasonTwo = true;
        else if (tMonth >= 7 && tMonth <= 9) bSeasonThree = true;
        else if (tMonth >= 10 && tMonth <= 12) bSeasonFour = true;
        
        switch (nBoxID) {
            case FREE_LOOT_BOX:
                sBoxName = "Weird Gift (Tier 3)";
                sBoxType = "Weird Gift : ";
                nTopTierChance = 1;
                nMidTierChance = 20;
                break;
            case VOTE_LOOT_BOX:
                sBoxName = "Special Gift (Tier 2)";
                sBoxType = "Special Gift : ";
                nTopTierChance = 5;
                nMidTierChance = 30;
                break;
            case DONOR_LOOT_BOX:
                sBoxName = "Special Value Gift (Tier 1)";
                sBoxType = "Special Value Gift : ";
                nTopTierChance = 20;
                nMidTierChance = 70;
                break;
            default:
                return false;
        }
        
        for (int i = 0; i < 3; i++) {
            if (Utility.resultSuccess(nTopTierChance)) {
                sTierResult = "#dLegendary#k";
                if (bSeasonOne) aItemPool = Utility.appendArrays(aTopTier_Default, aTopTier_SeasonOne);
                else if (bSeasonTwo) aItemPool = Utility.appendArrays(aTopTier_Default, aTopTier_SeasonTwo);
                else if (bSeasonThree) aItemPool = Utility.appendArrays(aTopTier_Default, aTopTier_SeasonThree);
                else if (bSeasonFour) aItemPool = Utility.appendArrays(aTopTier_Default, aTopTier_SeasonFour);
            } else if (Utility.resultSuccess(nMidTierChance)) {
                sTierResult = "#bRare#k";
                if (bSeasonOne) aItemPool = Utility.appendArrays(aMidTier_Default, aMidTier_SeasonOne);
                else if (bSeasonTwo) aItemPool = Utility.appendArrays(aMidTier_Default, aMidTier_SeasonTwo);
                else if (bSeasonThree) aItemPool = Utility.appendArrays(aMidTier_Default, aMidTier_SeasonThree);
                else if (bSeasonFour) aItemPool = Utility.appendArrays(aMidTier_Default, aMidTier_SeasonFour);
            } else {
                sTierResult = "#kNormal";
                if (bSeasonOne) aItemPool = Utility.appendArrays(aLowTier_Default, aLowTier_SeasonOne);
                else if (bSeasonTwo) aItemPool = Utility.appendArrays(aLowTier_Default, aLowTier_SeasonTwo);
                else if (bSeasonThree) aItemPool = Utility.appendArrays(aLowTier_Default, aLowTier_SeasonThree);
                else if (bSeasonFour) aItemPool = Utility.appendArrays(aLowTier_Default, aLowTier_SeasonFour);
            }
            
            switch (i) {
                case 0:
                    sFirstTier = sTierResult;
                    nFirstResult = aItemPool[Utility.getRandomSelection(aItemPool.length)];
                    break;
                case 1:
                    sSecondTier = sTierResult;
                    nSecondResult = aItemPool[Utility.getRandomSelection(aItemPool.length)];
                    break;
                case 2:
                    sThirdTier = sTierResult;
                    nThirdResult = aItemPool[Utility.getRandomSelection(aItemPool.length)];
                    break;
            }
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, nBoxID, 1, false, true);
        if (nFirstResult != 0) pPlayer.gainItem(nFirstResult, 1); 
        if (nSecondResult != 0) pPlayer.gainItem(nSecondResult, 1); 
        if (nThirdResult != 0) pPlayer.gainItem(nThirdResult, 1); 
        
        String sResult = "#d" + sBoxName + "#k\r\n"
                       + "Loot Box Results\r\n\r\n"
                       + "\t#v" + nFirstResult + "# : " + sFirstTier + "\r\n"
                       + "\t#v" + nSecondResult + "# : " + sSecondTier + "\r\n"
                       + "\t#v" + nThirdResult + "# : " + sThirdTier + "\r\n"
                       + "";
        
        if (sFirstTier.equals("#dLegendary#k")) 
            World.Broadcast.broadcastMessage(CField.getGameMessage("" + sBoxType + pPlayer.getName() + " has obtained a Legendary Cosmetic! (" + pItemInfo.getName(nFirstResult) + ")", (byte) 3));
        
        if (sSecondTier.equals("#dLegendary#k")) 
            World.Broadcast.broadcastMessage(CField.getGameMessage("" + sBoxType + pPlayer.getName() + " has obtained a Legendary Cosmetic! (" + pItemInfo.getName(nSecondResult) + ")", (byte) 3));
        
        if (sThirdTier.equals("#dLegendary#k")) 
            World.Broadcast.broadcastMessage(CField.getGameMessage("" + sBoxType + pPlayer.getName() + " has obtained a Legendary Cosmetic! (" + pItemInfo.getName(nThirdResult) + ")", (byte) 3));
        
        pPlayer.getClient().SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sResult, NPCInterfaceType.NPC_Cancellable));
        return true;
    }
    
    /**
     * OnNebuliteBoxRequest
     * @param pPlayer
     * @return 
     */
    public static boolean OnNebuliteBoxRequest(User pPlayer) {
        if (!pPlayer.haveItem(NEBULITE_BOX) || !pPlayer.hasInventorySpace(aNebulite[0], 1)) {
            return false;
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, LootBox.NEBULITE_BOX, 1, false, true);
        pPlayer.gainItem(aNebulite[Utility.getRandomSelection(aNebulite.length)], 1); // Give the player a random Nebulite.
        return true;
    }
    
    /**
     * OnEquipmentBoxRequest
     * @param pPlayer
     * @param nBoxID
     * @return 
     */
    public static boolean OnEquipmentBoxRequest(User pPlayer, int nBoxID) {
        int[][] aBoxItems = aEquipment_Lv30_Warrior; // Initialize incase player is a beginner.
        
        switch (nBoxID) { 
            case EQUIPMENT_BOX_LV30:
                if (GameConstants.isWarriorJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv30_Warrior;
                } else if (GameConstants.isBowmanJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv30_Bowman;
                } else if (GameConstants.isMagicianJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv30_Magician;
                } else if (GameConstants.isThiefJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv30_Thief;
                } else if (GameConstants.isPirateJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv30_Pirate;
                }
                
                if (!JobConstants.hasSecondJob(pPlayer.getJob())) {
                    pPlayer.dropMessage(5, "Sorry, you do not meet the job requirement to open this box.");
                    return false;
                }
                break;
            case EQUIPMENT_BOX_LV50:
                if (GameConstants.isWarriorJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv50_Warrior;
                } else if (GameConstants.isBowmanJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv50_Bowman;
                } else if (GameConstants.isMagicianJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv50_Magician;
                } else if (GameConstants.isThiefJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv50_Thief;
                } else if (GameConstants.isPirateJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv50_Pirate;
                }
                
                if (!JobConstants.hasSecondJob(pPlayer.getJob())) {
                    pPlayer.dropMessage(5, "Sorry, you do not meet the job requirement to open this box.");
                    return false;
                }
                break;
            case EQUIPMENT_BOX_LV70:
                if (GameConstants.isWarriorJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv70_Warrior;
                } else if (GameConstants.isBowmanJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv70_Bowman;
                } else if (GameConstants.isMagicianJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv70_Magician;
                } else if (GameConstants.isThiefJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv70_Thief;
                } else if (GameConstants.isPirateJob(pPlayer.getJob())) {
                    aBoxItems = aEquipment_Lv70_Pirate;
                }
                
                if (!JobConstants.hasThirdJob(pPlayer.getJob())) {
                    pPlayer.dropMessage(5, "Sorry, you do not meet the job requirement to open this box.");
                    return false;
                }
                break;
            default:
                return false;
        }
        
        if (!pPlayer.haveItem(nBoxID) || !pPlayer.hasInventorySpace(1322124, aBoxItems.length)) {
            return false;
        }
        
        List<Pair<Integer, Integer>> aBoxResult = new ArrayList<>();
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, nBoxID, 1, false, true);
        for (int i = 0; i < aBoxItems.length; i++) {
            if (aBoxItems[i][1] == pPlayer.getJob() || aBoxItems[i][1] == 0) { // Check if equip is for all jobs or the players current job.
                if (aBoxItems[i][0] == (pPlayer.getGender() + 1) || aBoxItems[i][0] == 0) { // Check if equip is for all genders or the players current gender.
                    aBoxResult.add(new Pair<>(aBoxItems[i][2], aBoxItems[i][3])); // Give the player all items from the specified box.
                }
                //pPlayer.dropMessage(5, "Gender: " + pPlayer.getGender() + " / Gender Required: " + aBoxItems[i][1]);
            }
        }
        for (int i = 0; i < aBoxResult.size(); i++) {
            pPlayer.gainItem(aBoxResult.get(i).getLeft(), aBoxResult.get(i).getRight());
        }
        return true;
    }
    
    /**
     * OnCosmeticBoxRequest
     * @param pPlayer
     * @param nBoxType
     * @return 
     */
    public static boolean OnCosmeticBoxRequest(User pPlayer, byte nBoxType) {
        int nBoxID;
        int[] aBoxSelections;
        
        switch (nBoxType) {
            case 1:
                nBoxID = BASIC_HAIR_BOX;
                aBoxSelections = aBasicHair;
                break;
            case 2:
                nBoxID = BASIC_FACE_BOX;
                aBoxSelections = aBasicFace;
                break;
            case 3:
                nBoxID = PREMIUM_HAIR_BOX;
                aBoxSelections = aPremiumHair;
                break;
            case 4:
                nBoxID = PREMIUM_FACE_BOX;
                aBoxSelections = aPremiumFace;
                break;
            default:
                return false;
        }
        
        if (!pPlayer.haveItem(nBoxID)) {
            return false;
        }
        
        MapleInventoryManipulator.removeById(pPlayer.getClient(), InventoryType.USE, nBoxID, 1, false, true); // Consume the Cosmetic Box.
        pPlayer.setFace(aBoxSelections[Utility.getRandomSelection(aBoxSelections.length)]); // Give the player a random style.
        return true;
    }
    
    /**
     * Loot Box Items 
     * Season One: Items Only Available During December - February 
     * Season Two: Items Only Available During March - May 
     * Season Three: Items Only Available During June - August 
     * Season Four: Items Only Available During September - November 
     * Default: Items Available Regardless of Season
     */
    private static int[] aTopTier_SeasonOne = {
        1702758, // Pop Star Mic Skin
        1702756, // Starlit Dreamweaver
        1702737, // Frost Staff
        1702735, // Rose Constellation
        1702731, // Snowrabbit
        1702723, // Power Porker Trio
        1702722, // Winter Bunny
        1702709, // High-five Neon V
        1702660, // Snowman Weapon
    };
    private static int[] aMidTier_SeasonOne = {
        1702759, // Charming Cherry Pop
        1702757, // Cygnus's Guard
        1702717, // Glow Stick of Love
    };
    private static int[] aLowTier_SeasonOne = {
        1702668, // Winter Deer Tambourine
        1702658, // Holiday Tree Ring
    };

    private static int[] aTopTier_SeasonTwo = {
        1702770, // Fancy Feather Quill
        1702752, // Silver Flower Child Weapon
        1702750, // Strawberry Fitness Jump Rope
        1702749, // Love Letter Book Bag
        1702748, // Guinea Pig Weapon
        1702745, // Spring Fairy Flower
        1702744, // Starlight Lantern
        1702728, // Sweet Jelly Paw
        1702727, // Rabbit Soap Shooter
        1702712, // Moon Bunny Bell Weapon
        1702703, // Natural Ink Painting
        1702687, // Strawberry Bon Bon
        1702675, // Smile Seed Weapon
        1702639, // Kitty Bangle
        1702627, // Sakura Sword
        1702617, // Lotus Fantasy
    };
    private static int[] aMidTier_SeasonTwo = {
        1702768, // Mallow Fluff on a Stick
        1702761, // Sproutbrella
        1702719, // Flutter Flower Doll Weapon
        1702716, // Dew Parasol
        1702707, // Pony's Carrot
        1702702, // Porong Fan
        1702693, // Bubble Leaf Weapon
        1702692, // Chicken Cutie Weapon
        1702691, // Fairy Flora
        1702628, // Farmer's Glorious Egg Stick
        1702612, // Fairy Pico
    };
    private static int[] aLowTier_SeasonTwo = {
        1702740, // Go Yellow Chicks!
        1702732, // Rabbit in a Hat
        1702654, // Mr. Hot Spring Kitty
    };

    private static int[] aTopTier_SeasonThree = {
        1702778, // Summer Flower Fairy Weapon
        1702777, // Refreshing Lemon Weapon
        1702771, // Banana Shake
        1702765, // Soda Pop Weapon
        1702729, // Deep-fried Drumstick
        1702706, // Ice Cream Scream
        1702704, // Blue Marine Knowledge
        1702699, // Colorful Beach Ball
        1702694, // Pastel Rose
        1702686, // Sweet Pig Weapon
        1702682, // Triple Fish Skewer
        1702673, // Monkey Banana
        1702608, // Marine Stripe Umbrella
    };
    private static int[] aMidTier_SeasonThree = {
        1702772, // Carrot Cake Shake
        1702746, // Dinofrog
        1702705, // Teddy Tube Wave
        1702697, // Cup Cat Weapon
        1702672, // Duckling Cross Bag
        1702689, // Fairy Flora
        1702614, // Baseball Bat
        1702607, // Cheese Carrot Stick
    };
    private static int[] aLowTier_SeasonThree = {
        1702616, // Ducky Candy Bar
    };

    private static int[] aTopTier_SeasonFour = {
        1702785, // Cursed Bat Weapon
        1702764, // Iron Mace Uniform Weapon
        1702753, // Pandora Weapon
        1702747, // Baby Magpie Buddy
        1702726, // Pumpkin Star
        1702725, // Necromancer
        1702724, // Undead Teddy
        1702721, // ????????? ??????
        1702714, // Witch's Staff
        1702710, // Kamaitachi's Sickle
        1702637, // Hard Carrier Suitcase
        1702630, // Striking Lantern
    };
    private static int[] aMidTier_SeasonFour = {
        1702755, // Sweet Baguette
        1702671, // Magic Tome Weapon
        1702634, // Maple Zombies
        1702631, // Bloody Fairytale
    };
    private static int[] aLowTier_SeasonFour = {
    };

    private static int[] aTopTier_Default = { 
        1702784, // Feather Messenger Weapon
        1702776, // Water Granos and Weapon
        1702769, // ????????? ????????? ????????????
        1702766, // Diamond Brilliance
        1702742, // Nova Enchanter Staff
        1702733, // Monk Drum
        1702720, // Maple M Playphone
        1702718, // Shadow Warrior's Sword
        1702715, // Lachelein Fantasia
        1702713, // Bichon Paw Weapon
        1702701, // Dragonmare Ninth Sword
        1702700, // Universal Transparent Weapon
        1702688, // Superstar M
        1702680, // Camellia's Sword
        1702640, // Bunny Snowman Attacker
        1702638, // Blue Marine Thirst For Knowledge
        1702633, // Banana Monkey Attacker
        1702736, // Frost Staff
        1702626, // British Handbag Weapon
    };
    private static int[] aMidTier_Default = { 
        1702783, // Rabble Rouser Weapon
        1702773, // Eagle Weapon Skin
        1702767, // Mustachio on a Stick
        1702734, // Maple 5000-Day Flag
        1702711, // Owl Spellbook
        1702690, // Noble Maple Rod
        1702685, // Red Phoenix Weapon
        1702684, // Blue Phoenix Weapon
        1702681, // Flask of Life
        1702677, // Lil Damien
        1702676, // Muse Crystal
        1702629, // Vintage Cellphone
        1702625, // Sparking Bluebird
        1702621, // Mystery Dice
        1702620, // Mystery Dice
        1702619, // Musical Green Onion
        1702613, // Crown Rod
        1702611, // Duckling Cross Bag Weapon
        1702599, // Hoya Roar
    };
    private static int[] aLowTier_Default = { 
        1702760, // Hamster Devotion!
        1702708, // No Name
        1702698, // Blaster Weapon
        1702696, // Silver Wolf
        1702695, // Overly Cute Puppy
        1702679, // Playful Black Nyanya
        1702678, // Lil Alicia
        1702667, // Lil Phantom
        1702666, // Lil Aran
        1702665, // Lil Evan
        1702659, // Timemaster
        1702657, // Lil Shade
        1702656, // Lil Luminous
        1702655, // Lil Mercedes
        1702653, // Transparent Arm Cannon
        1702652, // Forgotten Hero's Knuckle
        1702651, // Forgotten Hero's Knuckle
        1702650, // Shining Rod of Equilibrium
        1702649, // Shining Rod of Equilibrium
        1702648, // Maha the Polearm
        1702647, // Maha the Polearm
        1702646, // Phantom's Cane
        1702645, // Phantom's Cane
        1702644, // Elven Monarch's Dual Bowguns
        1702643, // Elven Monarch's Dual Bowguns
        1702642, // Dragon Master's Wand
        1702641, // Dragon Master's Wand
        1702635, // Mr. Orlov Coin Sword
        1702632, // Zakum Arms
        1702624, // Master Time
        1702623, // Today Jay
        1702606, // Squid
        1702605, // Donut
        1702604, // Parfait
        1702603, // Rib Steak
        1702602, // Hamburger
        1702601, // Bacon
        1702600, // Pasta
    };
    
    /**
     * Nebulite Box
     */
    private static int[] aNebulite = {
        3060000, // [D] Nebulite (STR)
        3060001, // [D] Nebulite (STR)
        3060002, // [D] Nebulite (STR)
        3060010, // [D] Nebulite (DEX)
        3060011, // [D] Nebulite (DEX)
        3060012, // [D] Nebulite (DEX)
        3060020, // [D] Nebulite (INT)
        3060021, // [D] Nebulite (INT)
        3060022, // [D] Nebulite (INT)
        3060030, // [D] Nebulite (LUK)
        3060031, // [D] Nebulite (LUK)
        3060032, // [D] Nebulite (LUK)
        3060040, // [D] Nebulite (Max HP)
        3060041, // [D] Nebulite (Max HP)
        3060042, // [D] Nebulite (Max HP)
        3060050, // [D] Nebulite (Max MP)
        3060051, // [D] Nebulite (Max MP)
        3060052, // [D] Nebulite (Max MP)
        3060060, // [D] Nebulite (DEX)
        3060061, // [D] Nebulite (DEX)
        3060070, // [D] Nebulite (DEX)
        3060071, // [D] Nebulite (DEX)
        3060080, // [D] Nebulite (Speed)
        3060081, // [D] Nebulite (Speed)
        3060090, // [D] Nebulite (Jump)
        3060091, // [D] Nebulite (Jump)
        3060100, // [D] Nebulite (Weapon ATT)
        3060110, // [D] Nebulite (Magic ATT)
        3060120, // [D] Nebulite (Weapon Defense)
        3060121, // [D] Nebulite (Weapon Defense)
        3060122, // [D] Nebulite (Weapon Defense)
        3060130, // [D] Nebulite (Magic Defense)
        3060131, // [D] Nebulite (Magic Defense)
        3060132, // [D] Nebulite (Magic Defense)
        3060140, // [D] Nebulite (Face)
        3060150, // [D] Nebulite (Face)
        3060160, // [D] Nebulite (Face)
        3060170, // [D] Nebulite (Face)
        3060180, // [D] Nebulite (Face)
        3061000, // [C] Nebulite (STR)
        3061001, // [C] Nebulite (STR)
        3061010, // [C] Nebulite (DEX)
        3061011, // [C] Nebulite (DEX)
        3061020, // [C] Nebulite (INT)
        3061021, // [C] Nebulite (INT)
        3061030, // [C] Nebulite (LUK)
        3061031, // [C] Nebulite (LUK)
        3061040, // [C] Nebulite (Max HP)
        3061041, // [C] Nebulite (Max HP)
        3061042, // [C] Nebulite (Max HP)
        3061050, // [C] Nebulite (Max MP)
        3061051, // [C] Nebulite (Max MP)
        3061052, // [C] Nebulite (Max MP)
        3061060, // [C] Nebulite (DEX)
        3061061, // [C] Nebulite (DEX)
        3061070, // [C] Nebulite (DEX)
        3061071, // [C] Nebulite (DEX)
        3061080, // [C] Nebulite (Speed)
        3061081, // [C] Nebulite (Speed)
        3061090, // [C] Nebulite (Jump)
        3061091, // [C] Nebulite (Jump)
        3061100, // [C] Nebulite (Weapon ATT)
        3061110, // [C] Nebulite (Magic ATT)
        3061120, // [C] Nebulite (Weapon Defense)
        3061121, // [C] Nebulite (Weapon Defense)
        3061122, // [C] Nebulite (Weapon Defense)
        3061123, // [C] Nebulite (Weapon Defense)
        3061124, // [C] Nebulite (Weapon Defense)
        3061125, // [C] Nebulite (Weapon Defense)
        3061130, // [C] Nebulite (Magic Defense)
        3061131, // [C] Nebulite (Magic Defense)
        3061132, // [C] Nebulite (Magic Defense)
        3061133, // [C] Nebulite (Magic Defense)
        3061134, // [C] Nebulite (Magic Defense)
        3061135, // [C] Nebulite (Magic Defense)
        3061140, // [C] Nebulite (STR %)
        3061150, // [C] Nebulite (DEX %)
        3061160, // [C] Nebulite (INT %)
        3061170, // [C] Nebulite (LUK %)
        3061180, // [C] Nebulite (Max HP %)
        3061190, // [C] Nebulite (Max MP %)
        3061200, // [C] Nebulite (DEX %)
    };
    
    /**
     * Equipment Box
     * Syntax: {Gender (0 = Both, 1 = Male, 2 = Female), Required Job ID (0 For Any), Item ID, Item Quantity}
     * Note: Job ID refers to the current job that the player would be at the specified level.
     */
    private static int[][] aEquipment_Lv30_Warrior = {
        {0, 2110, 1442001, 1}, // Aran Weapon
        {0, 3710, 1582001, 1}, // Blaster Weapon
        {0, 130, 1432002, 1}, // DK Spear
        {0, 130, 1442001, 1}, // DK Polearm
        {0, 1110, 1402002, 1}, // Dawn Warrior 2H Sword
        {0, 3120, 1232002, 1}, // Demon Avenger Weapon
        {0, 3110, 1322124, 1}, // Demon Slayer Weapon
        {0, 110, 1402002, 1}, // Hero 2H Sword
        {0, 4110, 1542002, 1}, // Hayato Weapon
        {0, 6110, 1402002, 1}, // Kaiser Weapon
        {0, 5110, 1302008, 1}, // Mihile 1H Sword
        {0, 120, 1322014, 1}, // Paladin 1H Mace
        {0, 0, 1002023, 1}, // Helm
        {1, 0, 1050196, 1}, // Male Overall
        {2, 0, 1051010, 1}, // Female Overall
        {0, 0, 1082007, 1}, // Gloves
        {0, 0, 1072041, 1}, // Boots
    };
    
    private static int[][] aEquipment_Lv30_Magician = {
        {0, 3210, 1382017, 1}, // Battle Mage Staff
        {0, 11210, 1252002, 1}, // Beast Tamer Weapon
        {0, 230, 1382017, 1}, // Bishop Staff
        {0, 1210, 1382017, 1}, // Blaze Wizard Staff
        {0, 2212, 1382017, 1}, // Evan Staff
        {0, 210, 1382017, 1}, // F/P Mage Staff
        {0, 220, 1382017, 1}, // I/L Mage Staff
        {0, 4210, 1552002, 1}, // Kanna Weapon
        {0, 14210, 1262001, 1}, // Kinesis Weapon
        {0, 2710, 1212002, 1}, // Luminous Weapon
        {0, 0, 1020234, 1}, // Helm
        {1, 0, 1050026, 1}, // Male Overall
        {2, 0, 1051005, 1}, // Female Overall
        {0, 0, 1082053, 1}, // Gloves
        {0, 0, 1072078, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv30_Thief = {
        {0, 431, 1332009, 1}, // Dual Blade Dagger
        {0, 431, 1342001, 1}, // Dual Blade Katara
        {0, 410, 1472008, 1}, // Night Lord Weapon
        {0, 1410, 1472008, 1}, // Night Walker Weapon
        {0, 2410, 1362005, 1}, // Phantom Weapon
        {0, 420, 1332009, 1}, // Shadower Dagger
        {0, 3610, 1242002, 1}, // Xenon Weapon
        {0, 0, 1002171, 1}, // Helm
        {1, 0, 1040057, 1}, // Male Top
        {2, 0, 1041048, 1}, // Female Top
        {1, 0, 1060046, 1}, // Male Bottom
        {2, 0, 1061046, 1}, // Female Bottom 
        {0, 0, 1082044, 1}, // Gloves
        {0, 0, 1072582, 1}, // Boots
    };

    private static int[][] aEquipment_Lv30_Bowman = {
        {0, 310, 1452005, 1}, // Bowman Weapon
        {0, 320, 1462004, 1}, // Xbowman Weapon
        {0, 2310, 1522004, 1}, // Mercedes Weapon
        {0, 1310, 1452005, 1}, // Wind Archer Weapon
        {0, 3310, 1462004, 1}, // Wild Hunter Weapon
        {0, 0, 1002161, 1}, // Helm
        {1, 0, 1040068, 1}, // Male Top
        {2, 0, 1041055, 1}, // Female Top
        {1, 0, 1060057, 1}, // Male Bottom
        {2, 0, 1061051, 1}, // Female Bottom
        {0, 0, 1082050, 1}, // Gloves
        {0, 0, 1072081, 1}, // Boots
    };

    private static int[][] aEquipment_Lv30_Pirate = {
        {0, 6510, 1222002, 1}, // Angelic Burster Weapon
        {0, 530, 1532004, 1}, // Cannon Master Weapon
        {0, 520, 1492004, 1}, // Corsair Weapon
        {0, 570, 1492004, 1}, // Jett Weapon
        {0, 3510, 1492004, 1}, // Mechanic Weapon
        {0, 510, 1482004, 1}, // Buccaneer Weapon
        {0, 2510, 1482004, 1}, // Shade Weapon
        {0, 1510, 1482004, 1}, // Thunder Breaker Weapon
        {0, 0, 1002622, 1}, // Helm
        {0, 0, 1052235, 1}, // Overall
        {0, 0, 1082189, 1}, // Gloves
        {0, 0, 1072583, 1}, // Boots
    };

    private static int[][] aEquipment_Lv50_Warrior = {
        {0, 2110, 1442036, 1}, // Aran Weapon
        {0, 3710, 1582002, 1}, // Blaster Weapon
        {0, 130, 1432026, 1}, // DK Spear
        {0, 130, 1442036, 1}, // DK Polearm
        {0, 1110, 1402026, 1}, // Dawn Warrior 2H Sword
        {0, 3120, 1232053, 1}, // Demon Avenger Weapon
        {0, 3110, 1322125, 1}, // Demon Slayer Weapon
        {0, 110, 1402026, 1}, // Hero 2H Sword
        {0, 4110, 1542053, 1}, // Hayato Weapon
        {0, 6110, 1402026, 1}, // Kaiser Weapon
        {0, 5110, 1302189, 1}, // Mihile 1H Sword
        {0, 120, 1322017, 1}, // Paladin 1H Mace
        {0, 0, 1002028, 1}, // Helm
        {1, 0, 1040089, 1}, // Male Top
        {2, 0, 1041183, 1}, // Female Top
        {1, 0, 1060078, 1}, // Male Bottom
        {2, 0, 1061088, 1}, // Female Bottom
        {0, 0, 1082011, 1}, // Gloves
        {0, 0, 1072135, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv50_Magician = {
        {0, 3210, 1382026, 1}, // Battle Mage Staff
        {0, 11210, 1252052, 1}, // Beast Tamer Weapon
        {0, 230, 1382026, 1}, // Bishop Staff
        {0, 1210, 1382026, 1}, // Blaze Wizard Staff
        {0, 2212, 1382026, 1}, // Evan Staff
        {0, 210, 1382026, 1}, // F/P Mage Staff
        {0, 220, 1382026, 1}, // I/L Mage Staff
        {0, 4210, 1552053, 1}, // Kanna Weapon
        {0, 14210, 1262019, 1}, // Kinesis Weapon
        {0, 2710, 1212046, 1}, // Luminous Weapon
        {0, 0, 1002218, 1}, // Helm
        {1, 0, 1050049, 1}, // Male Overall
        {2, 0, 1051030, 1}, // Female Overall
        {0, 0, 1072143, 1}, // Gloves
        {0, 0, 1082080, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv50_Thief = {
        {0, 431, 1332040, 1}, // Dual Blade Dagger
        {0, 431, 1342003, 1}, // Dual Blade Katara
        {0, 410, 1472041, 1}, // Night Lord Weapon
        {0, 1410, 1472041, 1}, // Night Walker Weapon
        {0, 2410, 1362053, 1}, // Phantom Weapon
        {0, 420, 1332040, 1}, // Shadower Dagger
        {0, 3610, 1242003, 1}, // Xenon Weapon
        {0, 0, 1002207, 1}, // Helm
        {1, 0, 1040097, 1}, // Male Top
        {2, 0, 1041080, 1}, // Female Top
        {1, 0, 1060085, 1}, // Male Bottom
        {2, 0, 1061079, 1}, // Female Bottom 
        {0, 0, 1072130, 1}, // Gloves
        {0, 0, 1082066, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv50_Bowman = {
        {0, 310, 1452034, 1}, // Bowman Weapon
        {0, 320, 1462030, 1}, // Xbowman Weapon
        {0, 2310, 1522035, 1}, // Mercedes Weapon
        {0, 1310, 1452034, 1}, // Wind Archer Weapon
        {0, 3310, 1462030, 1}, // Wild Hunter Weapon
        {0, 0, 1002214, 1}, // Helm
        {1, 0, 1050052, 1}, // Male Overall
        {2, 0, 1051039, 1}, // Female Overall
        {0, 0, 1072125, 1}, // Gloves
        {0, 0, 1082083, 1}, // Boots
    };

    private static int[][] aEquipment_Lv50_Pirate = {
        {0, 6510, 1222037, 1}, // Angelic Burster Weapon
        {0, 530, 1532052, 1}, // Cannon Master Weapon
        {0, 520, 1492060, 1}, // Corsair Weapon
        {0, 570, 1492060, 1}, // Jett Weapon
        {0, 3510, 1492060, 1}, // Mechanic Weapon
        {0, 510, 1482060, 1}, // Buccaneer Weapon
        {0, 2510, 1482060, 1}, // Shade Weapon
        {0, 1510, 1482060, 1}, // Thunder Breaker Weapon
        {0, 0, 1002631, 1}, // Helm
        {0, 0, 1052116, 1}, // Overall
        {0, 0, 1082198, 1}, // Gloves
        {0, 0, 1072303, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv70_Warrior = {
        {0, 2111, 1442038, 1}, // Aran Weapon
        {0, 3711, 1582004, 1}, // Blaster Weapon
        {0, 131, 1432028, 1}, // DK Spear
        {0, 131, 1442038, 1}, // DK Polearm
        {0, 1111, 1402028, 1}, // Dawn Warrior 2H Sword
        {0, 3121, 1232039, 1}, // Demon Avenger Weapon
        {0, 3111, 1322044, 1}, // Demon Slayer Weapon
        {0, 111, 1402028, 1}, // Hero 2H Sword
        {0, 4111, 1542055, 1}, // Hayato Weapon
        {0, 6111, 1402028, 1}, // Kaiser Weapon
        {0, 5111, 1302047, 1}, // Mihile 1H Sword
        {0, 121, 1322044, 1}, // Paladin 1H Mace
        {0, 0, 1002030, 1}, // Helm
        {1, 0, 1040103, 1}, // Male Top
        {2, 0, 1041098, 1}, // Female Top
        {1, 0, 1060091, 1}, // Male Bottom
        {2, 0, 1061096, 1}, // Female Bottom
        {0, 0, 1082105, 1}, // Gloves
        {0, 0, 1072156, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv70_Magician = {
        {0, 3211, 1382028, 1}, // Battle Mage Staff
        {0, 11211, 1252054, 1}, // Beast Tamer Weapon
        {0, 231, 1382028, 1}, // Bishop Staff
        {0, 1211, 1382028, 1}, // Blaze Wizard Staff
        {0, 2214, 1382028, 1}, // Evan Staff
        {0, 211, 1382028, 1}, // F/P Mage Staff
        {0, 221, 1382028, 1}, // I/L Mage Staff
        {0, 4211, 1552055, 1}, // Kanna Weapon
        {0, 14211, 1262020, 1}, // Kinesis Weapon
        {0, 2711, 1212039, 1}, // Luminous Weapon
        {0, 0, 1002254, 1}, // Helm
        {1, 0, 1050205, 1}, // Male Overall
        {2, 0, 1051249, 1}, // Female Overall
        {0, 0, 1082100, 1}, // Gloves
        {0, 0, 1072160, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv70_Thief = {
        {0, 433, 1332042, 1}, // Dual Blade Dagger
        {0, 433, 1342052, 1}, // Dual Blade Katara
        {0, 411, 1472043, 1}, // Night Lord Weapon
        {0, 1411, 1472042, 1}, // Night Walker Weapon
        {0, 2411, 1362055, 1}, // Phantom Weapon
        {0, 421, 1332041, 1}, // Shadower Dagger
        {0, 3611, 1242030, 1}, // Xenon Weapon
        {0, 0, 1002284, 1}, // Helm
        {1, 0, 1040107, 1}, // Male Top
        {2, 0, 1041101, 1}, // Female Top
        {1, 0, 1060095, 1}, // Male Bottom
        {2, 0, 1061101, 1}, // Female Bottom 
        {0, 0, 1082097, 1}, // Gloves
        {0, 0, 1072163, 1}, // Boots
    };

    private static int[][] aEquipment_Lv70_Bowman = {
        {0, 311, 1452036, 1}, // Bowman Weapon
        {0, 321, 1462032, 1}, // Xbowman Weapon
        {0, 2311, 1522037, 1}, // Mercedes Weapon
        {0, 1311, 1452036, 1}, // Wind Archer Weapon
        {0, 3311, 1462032, 1}, // Wild Hunter Weapon
        {0, 0, 1002289, 1}, // Helm
        {1, 0, 1050064, 1}, // Male Overall
        {2, 0, 1051065, 1}, // Female Overall
        {0, 0, 1082108, 1}, // Gloves
        {0, 0, 1072167, 1}, // Boots
    };
	
    private static int[][] aEquipment_Lv70_Pirate = {
        {0, 6511, 1222039, 1}, // Angelic Burster Weapon
        {0, 531, 1532054, 1}, // Cannon Master Weapon
        {0, 521, 1492062, 1}, // Corsair Weapon
        {0, 571, 1492062, 1}, // Jett Weapon
        {0, 3511, 1492062, 1}, // Mechanic Weapon
        {0, 511, 1482062, 1}, // Buccaneer Weapon
        {0, 2511, 1482062, 1}, // Shade Weapon
        {0, 1511, 1482062, 1}, // Thunder Breaker Weapon
        {0, 0, 1002637, 1}, // Helm
        {0, 0, 1052122, 1}, // Overall
        {0, 0, 1082204, 1}, // Gloves
        {0, 0, 1072309, 1}, // Boots
    };
    
    /**
     * Cosmetic Box
     */
    private static int[] aBasicHair = {
        0, 
        0, 
    };
    
    private static int[] aBasicFace = {
        0, 
        0, 
    };
    
    private static int[] aPremiumHair = {
        0, 
        0, 
    };
    
    private static int[] aPremiumFace = {
        0, 
        0, 
    };
}
