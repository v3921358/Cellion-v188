package enums;

/**
 *
 * @author Itzik
 */
public enum EquipStat { //GW_ItemSlotEquipBase::Encode

    SLOTS(0x01),
    LEVEL(0x02),
    STR(0x04),
    DEX(0x08),
    INT(0x10),
    LUK(0x20),
    MHP(0x40),
    MMP(0x80),
    WATK(0x100),
    MATK(0x200),
    PDD(0x400),
    MDD(0x800),
    ACC(0x1000),
    EVA(0x2000),
    HANDS(0x4000),
    SPEED(0x8000),
    JUMP(0x10000),
    FLAG(0x20000),
    INC_SKILL(0x40000),
    ITEM_LEVEL(0x80000),
    ITEM_EXP(0x100000),
    DURABILITY(0x200000),
    VICIOUS_HAMMER(0x400000),
    PVP_DAMAGE(0x800000),
    REDUCE_REQUIREMENT(0x1000000),
    SPELL_TRACE(0x2000000),
    DURABILITY_SPECIAL(0x4000000),
    REQUIRED_LEVEL(0x8000000),
    YGGDRASIL_WISDOM(0x10000000),
    FINAL_STRIKE(0x20000000),
    BOSS_DAMAGE(0x40000000),
    IGNORE_PDR(0x80000000);

    private final int value;

    private EquipStat(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public final boolean check(int flag) {
        return (flag & value) != 0;
    }

    public enum EnhanctBuff {

        UPGRADE_TIER(0x1),
        NO_DESTROY(0x2),
        SCROLL_SUCCESS(0x4);
        private final int value;

        private EnhanctBuff(int value) {
            this.value = value;
        }

        public final byte getValue() {
            return (byte) value;
        }

        public final boolean check(int flag) {
            return (flag & value) != 0;
        }
    }
}
