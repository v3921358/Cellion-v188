package server.life;

/**
 *
 * @author Lloyd Korn
 */
public class MonsterSkill {

    private final int skillid, action, level, skillAfter, effectAfter, preSkillIndex, preSkillCount;
    private final boolean onlyFsm;

    public MonsterSkill(int skillid, int action, int level, int skillAfter, int effectAfter, int preSkillIndex, int preSkillCount,
            boolean onlyFsm) {
        this.skillid = skillid;
        this.action = action;
        this.level = level;
        this.skillAfter = skillAfter;
        this.effectAfter = effectAfter;
        this.preSkillIndex = preSkillIndex;
        this.preSkillCount = preSkillCount;
        this.onlyFsm = onlyFsm;
    }

    public MobSkill getSkill() {
        return MobSkillFactory.getMobSkill(skillid, level);
    }

    public int getSkillId() {
        return skillid;
    }

    public int getLevel() {
        return level;
    }

    public int getAction() {
        return action;
    }

    public int getSkillAfter() {
        return skillAfter;
    }

    public int getEffectAfter() {
        return effectAfter;
    }

    public int getPreSkillIndex() {
        return preSkillIndex;
    }

    public int getPreSkillCount() {
        return preSkillCount;
    }

    public boolean getOnlyFsm() {
        return onlyFsm;
    }

}
