package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Ranger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class RangerBuff extends AbstractBuffClass {

    public RangerBuff() {
        skills = new int[]{
            Ranger.PHOENIX,
            Ranger.RECKLESS_HUNT_BOW
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.RANGER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Ranger.PHOENIX:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Ranger.RECKLESS_HUNT_BOW:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.padX));
                break;
        }
    }
}
