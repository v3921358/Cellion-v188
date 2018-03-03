package server.skills.effects.manager;

import server.MapleStatEffect;

public abstract class AbstractEffect {

    public abstract void SetEffect(MapleStatEffect pEffect, int nSkill);

    public abstract boolean IsCorrectClass(int nClass);

    protected int[] aSkills;
    protected int[] aUserEffects;

    public int[] GetEffects() {
        return aSkills;
    }

    public int[] GetUserEffects() {
        return aUserEffects;
    }
}
