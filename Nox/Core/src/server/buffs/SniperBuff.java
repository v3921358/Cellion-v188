package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Ranger;
import constants.skills.Sniper;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class SniperBuff extends AbstractBuffClass {

    public SniperBuff() {
        skills = new int[]{
            Sniper.FREEZER,
            Sniper.RECKLESS_HUNT_CROSSBOW,
            Sniper.PAIN_KILLER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.SNIPER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Sniper.PAIN_KILLER: //PainKiller
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, eff.info.get(MapleStatInfo.asrR));
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, eff.info.get(MapleStatInfo.terR));
                break;
            case Sniper.RECKLESS_HUNT_CROSSBOW:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.padX));
                break;
            case Sniper.FREEZER:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
