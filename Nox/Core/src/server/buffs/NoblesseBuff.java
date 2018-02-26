package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Noblesse;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class NoblesseBuff extends AbstractBuffClass {

    public NoblesseBuff() {
        skills = new int[]{
            Noblesse.EMPRESSS_PRAYER,
            Noblesse.NIMBLE_FEET,
            Noblesse.RECOVERY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.NOBLESSE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Noblesse.EMPRESSS_PRAYER:
                eff.statups.put(CharacterTemporaryStat.MaxLevelBuff, (int) eff.info.get(MapleStatInfo.x));
                break;
            case Noblesse.NIMBLE_FEET:
                eff.statups.put(CharacterTemporaryStat.Speed, 10 + (eff.getLevel() - 1) * 5);
                break;
            case Noblesse.RECOVERY:
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
