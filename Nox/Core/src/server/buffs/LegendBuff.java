package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Legend;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class LegendBuff extends AbstractBuffClass {

    public LegendBuff() {
        skills = new int[]{
            Legend.RECOVERY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.LEGEND.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Legend.RECOVERY:
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
