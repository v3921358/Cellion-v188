package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.AdditionalSkills;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class AdditionalBuff extends AbstractBuffClass {

    public AdditionalBuff() {
        skills = new int[]{
            AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT2,
            AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT3,
            AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT,
            AdditionalSkills.DARKNESS,
            AdditionalSkills.FREEZE,
            AdditionalSkills.SLOW,
            AdditionalSkills.POISON,
            AdditionalSkills.SEAL,
            AdditionalSkills.ELVEN_BLESSING
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.ADDITIONAL_SKILLS.getId()
                || job == MapleJob.ADDITIONAL_SKILLS1.getId()
                || job == MapleJob.ADDITIONAL_SKILLS2.getId()
                || job == MapleJob.ADDITIONAL_SKILLS3.getId()
                || job == MapleJob.ADDITIONAL_SKILLS4.getId()
                || job == MapleJob.ADDITIONAL_SKILLS5.getId()
                || job == MapleJob.ADDITIONAL_SKILLS6.getId()
                || job == MapleJob.ADDITIONAL_SKILLS7.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT2:
            case AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT3:
            case AdditionalSkills.LIBERATE_THE_RUNE_OF_MIGHT:
                eff.statups.put(CharacterTemporaryStat.Inflation, 2);
                break;
            case AdditionalSkills.DARKNESS:
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.info.get(MapleStatInfo.x));
                break;
            case AdditionalSkills.FREEZE:
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.info.put(MapleStatInfo.time, eff.info.get(MapleStatInfo.time) * 2);
                break;
            case AdditionalSkills.SLOW:
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.info.get(MapleStatInfo.x));
                break;
            case AdditionalSkills.POISON:
                eff.monsterStatus.put(MonsterStatus.POISON, 1);
                break;
            case AdditionalSkills.SEAL:
                eff.monsterStatus.put(MonsterStatus.SEAL, 1);
                break;
            case AdditionalSkills.ELVEN_BLESSING:
                eff.moveTo(eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
