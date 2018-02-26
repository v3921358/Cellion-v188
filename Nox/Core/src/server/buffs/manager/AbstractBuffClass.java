package server.buffs.manager;

import server.MapleStatEffect;

/**
 * These classes have two major purposes. 1) These classes will serve as a reference to buffs. 2) These classes will hopefully provide a
 * structured way to add new buffs.
 *
 */
public abstract class AbstractBuffClass {

    /**
     * This is where the handling of the buffs are done.
     *
     * @param eff the effect to be had from the buff
     * @param skill the skill that provides such a buff
     */
    public abstract void handleEffect(MapleStatEffect eff, int skill);

    protected int[] skills;
    protected int[] userEffects;

    public int[] getEffects() {
        return skills;
    }

    public int[] getUserEffects() {
        return userEffects;
    }

    public boolean containsSkill(int search) {
        for (int i : skills) {
            if (i == search) {
                return true;
            }
        }
        return false;
    }

    public boolean containsJob(int job) {
        return false;
    }
}
