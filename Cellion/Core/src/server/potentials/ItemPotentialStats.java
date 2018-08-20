package server.potentials;

/**
 *
 * @author Lloyd Korn
 */
public class ItemPotentialStats {

    private final boolean isBoss;
    private final int value;

    // only applicable for poision/freeze/burn attack type
    private final int attackType;
    private final byte level;
    private final int probability;

    public ItemPotentialStats(boolean isBoss, int value,
            int attackType, byte level, int probability) {
        this.isBoss = isBoss;
        this.value = value;
        this.attackType = attackType;
        this.level = level;
        this.probability = probability;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public int getValue() {
        return value;
    }

    /**
     * The attack type for freeze/burn/poison potentials
     *
     * @return
     */
    public int getAttackType() {
        return attackType;
    }

    /**
     * The level for freeze/burn/poison potentials
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * The probability for freeze/burn/poison potentials
     *
     * @return
     */
    public int getProbability() {
        return probability;
    }
}
