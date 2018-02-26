package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.SuperGM;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class SuperGMBuff extends AbstractBuffClass {

    public SuperGMBuff() {
        skills = new int[]{
            SuperGM.HIDE,
            SuperGM.RESURRECTION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.SUPERGM.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case SuperGM.HIDE:
                eff.statups.put(CharacterTemporaryStat.DarkSight, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case SuperGM.RESURRECTION:
                eff.statups.put(CharacterTemporaryStat.Revive, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
