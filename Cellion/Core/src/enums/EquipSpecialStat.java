/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * @author Itzik
 */
public enum EquipSpecialStat {

    TOTAL_DAMAGE(0x1),
    ALL_STAT(0x2),
    KARMA_COUNT(0x4),
    GRADE_OPTION(0x8),
    ITEM_STATE(0x10);

    private final int value;

    private EquipSpecialStat(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
