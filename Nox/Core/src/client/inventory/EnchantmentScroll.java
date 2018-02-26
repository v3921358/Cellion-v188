package client.inventory;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Steven
 *
 */
public class EnchantmentScroll {

    private String name;
    private Map<EnchantmentStats, Integer> stats = new EnumMap<EnchantmentStats, Integer>(EnchantmentStats.class);
    private int cost;
    private int type;
    private boolean willPass = false;

    /**
     * This class handles the enchantment scrolls for the enhance system
     */
    public EnchantmentScroll() {
    }

    /**
     * This class handles the enchantment scrolls for the enhance system
     *
     * @param int type
     */
    public EnchantmentScroll(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * This sets the name of the scroll
     *
     * @param String name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the scrolls stats
     */
    public Map<EnchantmentStats, Integer> getStats() {
        return stats;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * This sets the cost of the scroll
     *
     * @param int cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * This sets the look of the scroll
     *
     * @param int type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * This returns the mask of all the stats
     *
     * @return the mask
     */
    public int getMask() {
        int mask = 0;
        for (EnchantmentStats stats : stats.keySet()) {
            mask |= stats.getValue();
        }
        return mask;
    }

    /**
     * @return the willPass
     */
    public boolean willPass() {
        return willPass;
    }

    /**
     * This sets if the scroll can fail
     *
     * @param willPass
     */
    public void willPass(boolean willPass) {
        this.willPass = willPass;
    }
}
