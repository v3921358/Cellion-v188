package enums;

public enum WeaponType {

    ROD(1.2f, 30),
    NOT_A_WEAPON(1.43f, 20),
    BOW(1.3f, 15),
    CLAW(1.75f, 15),
    CANE(1.3f, 20),
    DAGGER(1.3f, 20),
    CROSSBOW(1.35f, 15),
    AXE1H(1.2f, 20),
    SWORD1H(1.2f, 20),
    BLUNT1H(1.2f, 20),
    AXE2H(1.34f, 20),
    SWORD2H(1.34f, 20),
    BLUNT2H(1.34f, 20),
    POLE_ARM(1.49f, 20),
    SPEAR(1.49f, 20),
    STAFF(1.0f, 25), //NON EXPLORERS
    WAND(1.0f, 25), //NON EXPLORERS
    EXPLORER_STAFF(1.2f, 25),
    EXPLORER_WAND(1.2f, 25),
    KNUCKLE(1.7f, 20),
    GUN(1.5f, 15),
    CANNON(1.8f, 15),
    DUAL_BOW(1.32f, 15),
    MAGIC_ARROW(2.0f, 15), //need to confirm
    CARTE(2.0f, 15), //need to confirm
    KATARA(1.3f, 20),
    LAPIS(1.49f, 20),
    LAZULI(1.32f, 20),
    DESPERADO(1.3f, 20),
    WHIP_BLADE_ENERGY_SWORD(1.125f, 20),
    FAN(1.35f, 25),
    KATANA(1.25f, 20);

    private final float damageMultiplier;
    private final int baseMastery;

    private WeaponType(final float maxDamageMultiplier, int baseMastery) {
        this.damageMultiplier = maxDamageMultiplier;
        this.baseMastery = baseMastery;
    }

    public final float getMaxDamageMultiplier() {
        return damageMultiplier;
    }

    public final int getBaseMastery() {
        return baseMastery;
    }
};
