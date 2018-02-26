package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Bandit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BanditBuff extends AbstractBuffClass {

    public BanditBuff() {
        skills = new int[]{
            Bandit.MESOGUARD,
            Bandit.DAGGER_BOOSTER,
            Bandit.STEAL
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BANDIT.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Bandit.MESOGUARD:
                eff.statups.put(CharacterTemporaryStat.MesoGuard, eff.info.get(MapleStatInfo.x));
                break;
            case Bandit.DAGGER_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Bandit.STEAL:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Bandit.CHANNEL_KARMA: //Channel Karma
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.pad));
                break;
        }
    }
}
