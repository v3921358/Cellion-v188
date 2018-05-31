package enums;

/**
 * @author Steven
 *
 */
public enum EnchantmentStats {

    WATK(0x1),
    MATK(0x2),
    STR(0x4),
    DEX(0x8),
    INT(0x10),
    LUK(0x20),
    PDD(0x40),
    MDD(0x80),
    MHP(0x100),
    MMP(0x200),
    ACC(0x400),
    EVA(0x800),
    JUMP(0x1000),
    SPEED(0x2000);

    private int value;

    /**
     * This is the enum which corrisponds to the scroll mask for the enchantment system
     *
     * @param value
     */
    private EnchantmentStats(int value) {
        this.value = value;
    }

    /**
     * @return value of the enum
     */
    public int getValue() {
        return value;
    }
}
