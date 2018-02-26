package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Gunslinger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class GunslingerBuff extends AbstractBuffClass {

    public GunslingerBuff() {
        skills = new int[]{
            Gunslinger.GUN_BOOSTER,
            Gunslinger.INFINITY_BLAST
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.GUNSLINGER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Gunslinger.GUN_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case Gunslinger.INFINITY_BLAST:
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, eff.info.get(MapleStatInfo.bulletConsume));
                break;
        }
    }
}
