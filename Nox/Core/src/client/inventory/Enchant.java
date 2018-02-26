package client.inventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import constants.GameConstants;
import constants.InventoryConstants;
import server.MapleItemInformationProvider;
import server.maps.objects.MapleCharacter;

/**
 * @author Steven
 *
 */
public class Enchant {

    private EnchantmentActions action;
    private List<EnchantmentScroll> scrolls = new ArrayList<>();
    private Equip oldEquip;
    private Equip newEquip;
    private boolean feverTime;
    private boolean chanceTime;
    private int destroyChance = 0;
    private int perMille = 0;
    private int level = 0;
    private int seed = 0;
    private int passed = 0;
    private int decreaseChance = 0;
    private static int price[] = {3600, 42800, 181400, 505000, 1117700, 2139000, 3703000, 5956600,
        9059500, 9059500, 13182600, 18507900, 25228100, 33545800, 43673300, 55832200};
    private Map<EnchantmentStats, Short> starForce = new EnumMap<EnchantmentStats, Short>(EnchantmentStats.class);

    /**
     * This is class is going to handle all the functions for enchantment
     *
     */
    public Enchant() {
    }

    /**
     * This is class is going to handle all the functions for enchantment
     *
     * @param action
     */
    public Enchant(EnchantmentActions action) {
        this.action = action;
    }

    /**
     * This is class is going to handle all the functions for enchantment
     *
     * @param action
     */
    public Enchant(byte action) {
        this.action = EnchantmentActions.ADDITIONAL_POTENTIAL_UPGRADE.getAction(action);
    }

    /**
     * @returns the action
     */
    public EnchantmentActions getAction() {
        return action;
    }

    /**
     * This method sets the action of the enchantment
     *
     * @param action
     */
    public void setAction(EnchantmentActions action) {
        this.action = action;
    }

    /**
     * This returns the list of scrolls
     *
     * @return scrolls
     */
    public List<EnchantmentScroll> getScrolls() {
        return scrolls;
    }

    /**
     * This sets the scroll list that the player can use
     *
     * @param scrolls
     */
    public void setScrolls(List<EnchantmentScroll> scrolls) {
        this.scrolls = scrolls;
    }

    /**
     * @return the oldEquip
     */
    public Equip getOldEquip() {
        return oldEquip;
    }

    /**
     * Sets the old equip
     *
     * @param oldEquip
     */
    public void setOldEquip(Equip oldEquip) {
        this.oldEquip = oldEquip;
    }

    /**
     * @return the newEquip
     */
    public Equip getNewEquip() {
        return newEquip;
    }

    /**
     * Sets the new equip
     *
     * @param newEquip
     */
    public void setNewEquip(Equip newEquip) {
        this.newEquip = newEquip;
    }

    /**
     * @return the cost
     */
    public long getCost() {
        return findCost();
    }

    /**
     * @return the feverTime
     */
    public boolean isFeverTime() {
        return feverTime;
    }

    /**
     * Set fever time
     *
     * @param feverTime
     */
    public void setFeverTime(boolean feverTime) {
        this.feverTime = feverTime;
    }

    /**
     * @return the chanceTime
     */
    public boolean isChanceTime() {
        return chanceTime;
    }

    /**
     * Sets the chance time
     *
     * @param chanceTime
     */
    public void setChanceTime(boolean chanceTime) {
        this.chanceTime = chanceTime;
    }

    /**
     * @return the downgradable
     */
    public boolean canDowngrade() {
        if (!isSuperior()) {
            if (oldEquip.getEnhance() > 4 && oldEquip.getEnhance() < 10 || oldEquip.getEnhance() > 10 && oldEquip.getEnhance() < 15) {
                return true;
            }
        } else {
            if (oldEquip.getEnhance() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the destroyChance
     */
    public int getDestroyChance() {
        return destroyChance;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level of enchantment
     *
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the seed
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Sets the seed for the mini-game
     *
     * @param seed
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * @return the passed
     */
    public int hasPassed() {
        return passed;
    }

    /**
     * If the item has passed or not 0 - FAILED / LEVEL DROPPED 1 - PASSED 2 - DESTROYED 3 - FAILED 4 - DESTROYED
     *
     * @param passed the passed to set
     */
    public void setPassed(int passed) {
        this.passed = passed;
    }

    /**
     * Returns the success chance
     *
     * @returns perMillie
     */
    public int getPerMille() {
        return perMille;
    }

    /**
     * Sets the success chance
     *
     * @param perMillie
     */
    public void setPerMille(int perMille) {
        this.perMille = perMille;
    }

    /**
     * @return starForce
     */
    public Map<EnchantmentStats, Short> getStarForce() {
        return starForce;
    }

    /**
     * The chance which an item can downgrade in rank
     *
     * @return decreaseChance
     */
    public int getDecreaseChance() {
        return decreaseChance;
    }

    /**
     * @return the starForce mask
     */
    public int getMask() {
        int mask = 0;
        for (Entry<EnchantmentStats, Short> star : starForce.entrySet()) {
            mask |= star.getKey().getValue();
        }
        return mask;
    }

    /**
     * This will set the rate if success for the item that will used for star force
     *
     * @param equip
     */
    public void setRates(Equip equip) {
        int base = 950;
        byte enhance = equip.getEnhance();
        if (!isSuperior()) {
            if (enhance < 2) {
                perMille = base - ((enhance * 5) * 10);
            } else if (enhance > 1 && enhance < 4) {
                perMille = 850;
            } else if (enhance > 3 && enhance < 10) {
                perMille = (base - (((enhance + 1) * 5) * 10)) + 100;
            } else if (enhance > 10 && enhance < 12) {
                perMille = (base - (((enhance) * 5) * 10)) - 50;
            } else {
                perMille = 300;
            }
            if (canDowngrade()) {
                if (enhance < 12) {
                    decreaseChance = 1000 - perMille;
                } else {
                    decreaseChance = 690;
                    destroyChance = (int) Math.ceil(decreaseChance / 50d);
                }
            }
        } else {
            perMille = 500;
            if (enhance == 2) {
                perMille = 450;
            } else if (enhance > 2 && enhance < 9) {
                perMille = 400;
            } else if (enhance == 9) {
                perMille = 370;
            } else if (enhance > 9 && enhance < 12) {
                perMille = 350;
            } else if (enhance > 11) {
                perMille = 150 - (enhance * 10);
            }
            if (canDowngrade()) {
                decreaseChance = 1000 - perMille;
                if (enhance > 4) {
                    double divisor = 0;
                    switch (enhance) {
                        case 5:
                            divisor = 20;
                            break;
                        case 6:
                            divisor = 12;
                            break;
                        case 7:
                            divisor = 8.5;
                            break;
                        case 8:
                            divisor = 6;
                            break;
                        case 9:
                            divisor = 4.2;
                            break;
                        case 10:
                            divisor = 3.25;
                            break;
                        case 11:
                            divisor = 2.6;
                            break;
                        case 12:
                            divisor = 1.94;
                            break;
                        case 13:
                            divisor = 1.96;
                            break;
                        case 14:
                            divisor = 1.98;
                            break;
                    }
                    destroyChance = (int) (decreaseChance / divisor);
                }
            }
        }
    }

    /**
     * If the item in question is a superior ranked item
     *
     * @return isSuperor
     */
    private boolean isSuperior() {
        return MapleItemInformationProvider.getInstance().isSuperior(oldEquip.getItemId());
    }

    /**
     * This method will set the range that can be gained per attack Normal: attack / 50 Superior (Tyrant): starForce + 4; if starForce > 5
     *
     * @param stat - The stat that is going to increase
     * @param equip - The equip which we will get the range from
     * @return the amount the attack will gain
     */
    private short attackBoost(EquipStat stat, Equip equip) {
        short baseStat = stat == EquipStat.MATK ? equip.getMatk() : equip.getWatk();
        double range = (double) baseStat / 50;
        if (isSuperior() && equip.getEnhance() > 4) {
            return (short) (equip.getEnhance() + 4);
        }
        return (short) Math.ceil(range);
    }

    /**
     * This method will set the range that can be gained per def Math.ceil(def / 18)
     *
     * @param stat - The stat that is going to increase
     * @param equip - The equip which we will get the range from
     * @return the amount the def will gain
     */
    private short defBoost(EquipStat stat, Equip equip) {
        final List<EquipStat> eqStats = EquipHelper.calculateEquipStatsForEncoding(equip);

        if (eqStats.contains(stat)) {
            short def = stat == EquipStat.PDD ? equip.getWdef() : equip.getMdef();
            if (InventoryConstants.isOverall(equip.getItemId()) || InventoryConstants.isHat(equip.getItemId())) {
                return (short) Math.ceil(def / 8d);
            }
            return (short) Math.ceil(def / 18d);
        }
        return 0;

    }

    /**
     * This method will set the range that can be gained per player stat if enhance % 2 == 0 thatStat++; stat: avoid, acc, speed, jump stat
     * > 10 thatStat++;
     *
     * @param stat - The stat that is going to increase
     * @param equip - The equip which we will get the range from
     * @return the amount the stat will gain
     */
    private short playerBoost(EquipStat stat, Equip equip) {
        short stats = 0;
        if (equip.getEnhance() % 2 == 0 && equip.getEnhance() > 0) {
            stats++;
            if (equip.getEnhance() > 9) {
                stats++;
            }
        }
        return stats;
    }

    /**
     * This method will decide how much hp/mp an item will gain - Cape/Pendant/Ring/Belt/Shoulder/Weapon/Shields - starts with 5 hp -
     * Hat/overall - starts with 10 hp
     *
     * @param equip - The equip which we will get the range from
     * @return the amount the stat will gain
     */
    private short pointBoost(Equip equip) {
        short stat = 5;
        int itemId = equip.getItemId();
        if (InventoryConstants.isOverall(itemId) || InventoryConstants.isHat(itemId)) {
            stat += 5;
        }
        if (equip.getEnhance() > 3 && equip.getEnhance() < 5) {
            stat += 5;
        }
        if (equip.getEnhance() > 4 && equip.getEnhance() < 7) {
            stat += 10;
        }
        if (equip.getEnhance() > 6 && equip.getEnhance() < 9) {
            stat += 15;
        }
        if (equip.getEnhance() > 8) {
            stat += 20;
        }
        if (stat > 25) {
            stat = 25;
        }
        return stat;
    }

    /**
     * This method will set the range that can be gained per player stat Superior: if starforce < 6
     *  	19 -> 20 -> 22 -> 25 -> 29
     *
     * @param stat - The stat that is going to increase
     * @param equip - The equip which we will get the range from
     * @return the amount the stat will gain
     */
    private short statBoost(Equip equip) {
        short stat = (short) ((equip.getEnhance() > 4) ? 3 : 2);
        if (isSuperior() && equip.getEnhance() < 6) {
            stat = 19;
            for (int i = 0; i < equip.getEnhance(); i++) {
                stat += i;
            }
        }
        return stat;
    }

    /**
     * This method will fill in which stats can be upgraded
     *
     * @param chr - character object
     * @param equip - The equip that will be upgraded
     */
    public void canUpgrade(MapleCharacter chr, Equip equip) {
        final List<EquipStat> eqStats = EquipHelper.calculateEquipStatsForEncoding(equip);
        if (InventoryConstants.isWeapon(equip.getItemId())) {
            if (!eqStats.contains(EquipStat.MMP)) {
                starForce.put(EnchantmentStats.MMP, (short) 5);
            }
        }
        if (!InventoryConstants.isShoes(equip.getItemId())) {
            if (equip.getEnhance() % 2 == 0 && equip.getEnhance() > 0) {
                if (!InventoryConstants.isWeapon(equip.getItemId())) {
                    if (!eqStats.contains(EquipStat.EVA)) {
                        starForce.put(EnchantmentStats.EVA, (short) 1);
                    }
                }
                if (!eqStats.contains(EquipStat.ACC)) {
                    starForce.put(EnchantmentStats.ACC, (short) 1);
                }
            }
            if (!InventoryConstants.isJewelery(equip.getItemId())) {
                if (!eqStats.contains(EquipStat.MHP)) {
                    starForce.put(EnchantmentStats.MHP, (short) 5);
                }
            }
        } else {
            if (equip.getEnhance() % 2 == 0 && equip.getEnhance() > 0) {
                if (!InventoryConstants.isWeapon(equip.getItemId())) {
                    if (!eqStats.contains(EquipStat.SPEED)) {
                        starForce.put(EnchantmentStats.SPEED, (short) 1);
                    }
                }
                if (!eqStats.contains(EquipStat.JUMP)) {
                    starForce.put(EnchantmentStats.JUMP, (short) 1);
                }
            }
        }
        if (!eqStats.contains(EquipStat.PDD)) {
            starForce.put(EnchantmentStats.PDD, (short) 1);
        }
        if (!eqStats.contains(EquipStat.MDD)) {
            starForce.put(EnchantmentStats.MDD, (short) 1);
        }
        if (InventoryConstants.isGlove(equip.getItemId())) {
            if (equip.getEnhance() > 3 && equip.getEnhance() % 2 == 0) {
                if (GameConstants.isMage(chr.getJob())) {
                    starForce.put(EnchantmentStats.MATK, (short) 1);
                } else {
                    starForce.put(EnchantmentStats.WATK, (short) 1);
                }
            }
        }
        for (EquipStat stat : eqStats) {
            switch (stat) {
                case WATK:
                    if (canGainAttack(equip.getItemId())) {
                        starForce.put(EnchantmentStats.WATK, attackBoost(EquipStat.WATK, equip));
                    }
                    break;
                case MATK:
                    if (canGainAttack(equip.getItemId())) {
                        starForce.put(EnchantmentStats.MATK, attackBoost(EquipStat.MATK, equip));
                    }
                    break;
                case STR:
                    starForce.put(EnchantmentStats.STR, statBoost(equip));
                    break;
                case DEX:
                    starForce.put(EnchantmentStats.DEX, statBoost(equip));
                    break;
                case INT:
                    starForce.put(EnchantmentStats.INT, statBoost(equip));
                    break;
                case LUK:
                    starForce.put(EnchantmentStats.LUK, statBoost(equip));
                    break;
                case PDD:
                    starForce.put(EnchantmentStats.PDD, defBoost(EquipStat.PDD, equip));
                    break;
                case MDD:
                    starForce.put(EnchantmentStats.MDD, defBoost(EquipStat.MDD, equip));
                    break;
                case MHP:
                    if (!InventoryConstants.isJewelery(equip.getItemId())) {
                        starForce.put(EnchantmentStats.MHP, pointBoost(equip));
                    }
                    break;
                case MMP:
                    if (InventoryConstants.isWeapon(equip.getItemId())) {
                        starForce.put(EnchantmentStats.MMP, pointBoost(equip));
                    }
                    break;
                case ACC:
                    if (!InventoryConstants.isShoes(equip.getItemId())) {
                        starForce.put(EnchantmentStats.ACC, playerBoost(EquipStat.ACC, equip));
                    }
                    break;
                case EVA:
                    if (!InventoryConstants.isWeapon(equip.getItemId()) && !InventoryConstants.isShoes(equip.getItemId())) {
                        starForce.put(EnchantmentStats.EVA, playerBoost(EquipStat.EVA, equip));
                    }
                    break;
                case SPEED:
                    if (InventoryConstants.isShoes(equip.getItemId())) {
                        starForce.put(EnchantmentStats.SPEED, playerBoost(EquipStat.SPEED, equip));
                    }
                    break;
                case JUMP:
                    if (InventoryConstants.isShoes(equip.getItemId())) {
                        starForce.put(EnchantmentStats.JUMP, playerBoost(EquipStat.JUMP, equip));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This will handle the upgrade for the equipment
     *
     * @param equip
     * @param stat
     * @param bonus
     */
    public void scroll(Equip equip, EnchantmentStats stat, int bonus) {
        switch (stat) {
            case WATK:
                equip.setWatk((short) (equip.getWatk() + bonus));
                break;
            case MATK:
                equip.setMatk((short) (equip.getMatk() + bonus));
                break;
            case STR:
                equip.setStr((short) (equip.getStr() + bonus));
                break;
            case DEX:
                equip.setDex((short) (equip.getDex() + bonus));
                break;
            case INT:
                equip.setInt((short) (equip.getInt() + bonus));
                break;
            case LUK:
                equip.setLuk((short) (equip.getLuk() + bonus));
                break;
            case PDD:
                equip.setWdef((short) (equip.getWdef() + bonus));
                break;
            case MDD:
                equip.setMdef((short) (equip.getMdef() + bonus));
                break;
            case MHP:
                equip.setHp((short) (equip.getHp() + bonus));
                break;
            case MMP:
                equip.setMp((short) (equip.getMp() + bonus));
                break;
            case ACC:
                equip.setAcc((short) (equip.getAcc() + bonus));
                break;
            case EVA:
                equip.setAvoid((short) (equip.getAvoid() + bonus));
                break;
            case JUMP:
                equip.setJump((short) (equip.getJump() + bonus));
                break;
            case SPEED:
                equip.setSpeed((short) (equip.getSpeed() + bonus));
                break;
        }
    }

    private boolean canGainAttack(int itemId) {
        return InventoryConstants.isWeapon(itemId) || isSuperior();
    }

    /**
     * This method finds the cost for the star force enhancement
     */
    private long findCost() {
        int star = oldEquip.getEnhance();
        int level = oldEquip.getReqLevel();
        if (isSuperior()) {
            return price[star];
        }
        level = level / 10 > 14 ? 14 : (level / 10);
        int[] bases = {1000, 1300, 2100, 3600, 6000, 9600, 14700, 21500, 30200, 41000, 54200, 70100, 88900, 110800, 136000};
        int[] addedPerStar = {100, 300, 1100, 2500, 5000, 8700, 13700, 20500, 29100, 40000, 53300, 69100, 87900, 109700, 135000};
        return bases[level] + addedPerStar[level] * star;
    }

}
