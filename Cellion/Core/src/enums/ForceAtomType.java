/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * @author
 */
public enum ForceAtomType {
    PHANTOM_CARD_1(1, 1), //Phantom - Carte Blanch (2nd Job)
    PHANTOM_CARD_2(1, 2), //Phantom - Carte Noir (4th Job)
    KAISER_WEAPON_THROW_1(2, 1), //Kaiser 3 Swords (2nd Job)
    KAISER_WEAPON_THROW_2(2, 2), //Kaiser 5 Swords (4th Job)
    KAISER_WEAPON_THROW_MORPH_1(2, 3), //Kaiser 3 Swords Final Form
    KAISER_WEAPON_THROW_MORPH_2(2, 4), //Kaiser 5 Swords Final Form
    AB_ORB(3, 1), //AB - Soul Seeker
    DA_ORB(3, 2), //Megiddo Flame (mage FP Lv170 Hyper)
    NETHER_SHIELD(3, 3), //Nether Shield
    RABBIT_ORB(3, 4), // Shade
    FLAMING_RABBIT_ORB(3, 5), // same for 4, but insta disappear (byMob?)
    XENON_ROCKET_1(5, 1), //Xenon Aegis System Rockets
    XENON_ROCKET_2(5, 2), //Xenon Aegis System Rockets
    XENON_ROCKET_3(6, 1), //Xenon Pinpoint Salvo
    WA_ARROW_1(7, 1), //WA Green Arrow
    WA_ARROW_2(7, 2), //WA Purple Arrow
    WA_ARROW_HYPER(8, 1), //WA Hyper Arrow
    KANNA_ORB_1(9, 1), // to char
    KANNA_ORB_2(9, 2), // to char
    BM_ARROW(10, 1), //Magic Arrow from Quiver Cartridge
    ASSASSIN_MARK(11, 1), //Assassin's Mark
    NIGHTLORD_MARK(11, 2), //Night Lord's Mark
    FLYING_MESO(12, 1), //Flying Meso - Shadower's Meso Explosion
    BLUE_RABBIT_ORB(13, 1), //Shade 2nd Job
    RED_RABBIT_ORB(13, 2), //Shade 4th Job upgrade
    YELLOW_ORB_TO_SELF(14, 1), //Looks similar to Demon Slayer's Fury Orbs but it's yellow instead of blue
    NIGHT_WALKER_BAT(15, 1), //Night Walker Bat  from Mob?
    NIGHT_WALKER_BAT_4(15, 2), //Night Walker Bats(4th)  from Mob?
    NIGHT_WALKER_FROM_MOB(16, 1), //Night Walker Bat
    NIGHT_WALKER_FROM_MOB_4(16, 2), //Night Walker Bats (4th Job)
    ORBITAL_FLAME_1(17, 1), //Blaze Wizard(1)
    ORBITAL_FLAME_3(17, 2), //Blaze Wizard(3)
    ORBITAL_FLAME_2(17, 3), //Blaze Wizard(2)
    ORBITAL_FLAME_4(17, 4), //Blaze Wizard(4)
    STAR_1(18, 1), //Star (white/blue interior) - Star Planet?
    STAR_2(18, 2), //Star (purple) - Star Planet?
    KINESIS_ORB(18, 3), // ?
    KINESIS_SMALL_ORB(18, 4), // ?
    YELLOW_BLACK_ORB(18, 6), //Looks like the Soul orbs from having a Soul Enchanted weapon but these are different colour and bigger
    PURPLE_BLACK_ORB(18, 10), //Looks like the Soul orbs from having a Soul Enchanted weapon but these are different colour and bigger
    MECH_ROCKET(19, 1),
    MECH_MEGA_ROCKET_1(20, 1),
    MECH_MEGA_ROCKET_2(20, 2),
    KINESIS_ORB_REAL(22, 1),
    FAST_STAR_ORB(24, 1), // ?
    TRANSPARENT_AB_ORB(25, 1), // same for 26, but that disappears
    ;

    private int forceAtomType;
    private int inc;

    ForceAtomType(int forceAtomType, int inc) {
        this.forceAtomType = forceAtomType;
        this.inc = inc;
    }

    public int getForceAtomType() {
        return forceAtomType;
    }

    public void setForceAtomType(int forceAtomType) {
        this.forceAtomType = forceAtomType;
    }

    public int getInc() {
        return inc;
    }

    public void setInc(int inc) {
        this.inc = inc;
    }
}
