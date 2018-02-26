package server.potentials;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import tools.Pair;

/**
 * Defines the stats for each potential entry in the WZ. Item.wz/ItemOption.img
 *
 * @author Lloyd Korn
 */
public class ItemPotentialOption {

    private Map<Integer, List<Pair<ItemPotentialType, ItemPotentialStats>>> potentialStats; // Map key = potential level 1,2,3. Map value = potentials
    private ItemPotentialSkill skill = ItemPotentialSkill.None;
    private int optionId;
    private byte weight = 0;
    private int optionType = 0;
    private short reqLvl = 0;

    /**
     * Sets the potential stats for this equipment
     *
     * @param potentialStats
     */
    public void setPotentialStats(Map<Integer, List<Pair<ItemPotentialType, ItemPotentialStats>>> potentialStats) {
        this.potentialStats = potentialStats;
    }

    /**
     * Gets the potential stats of this equipment
     *
     * @return
     */
    public Map<Integer, List<Pair<ItemPotentialType, ItemPotentialStats>>> getPotentialStats() {
        return Collections.unmodifiableMap(potentialStats);
    }

    /**
     * Sets the skill given for this potential.
     *
     * @param skill
     */
    public void setSkill(ItemPotentialSkill skill) {
        this.skill = skill;
    }

    /**
     * Gets the skill buff that's given if should an equipment contains this potential.
     *
     * @return
     */
    public ItemPotentialSkill getSkill() {
        return skill;
    }

    /**
     * Sets the option ID of this potential.
     *
     * @param optionId
     */
    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    /**
     * Returns the option ID of this potential.
     */
    public int getOptionId() {
        return optionId;
    }

    /**
     * Sets the option type of this potential
     *
     * @param optionType
     */
    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    /**
     * Gets the option type of this potential
     *
     * @return
     */
    public int getOptionType() {
        return optionType;
    }

    /**
     * Sets the required equipment level to acquire this potential stat
     *
     * @param reqLvl
     */
    public void setRequiredLevel(short reqLvl) {
        this.reqLvl = reqLvl;
    }

    /**
     * Gets the required equipment level to acquire this potential stat
     *
     * @return
     */
    public short getRequiredLevel() {
        return reqLvl;
    }

    /**
     * Sets the weight of this potential item. Doesnt seems to be used anymore, only during Chaos patch while PVP is still here
     *
     * @param weight
     */
    public void setWeight(byte weight) {
        this.weight = weight;
    }

    /**
     * Gets the weight of this potential stat. Doesnt seems to be used anymore, only during Chaos patch while PVP is still here
     *
     * @return
     */
    public byte getWeight() {
        return weight;
    }

    /**
     * Gets the suitable potential level stat according to the equipment required level
     *
     * @param itemLvl
     * @return
     */
    public List<Pair<ItemPotentialType, ItemPotentialStats>> getSuitableStats(int itemLvl) {
        final int level = itemLvl / 10;
        if (potentialStats.containsKey(level)) {
            return potentialStats.get(level);
        }
        return null;
    }

}
