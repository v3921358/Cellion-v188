package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Page;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class PageBuff extends AbstractBuffClass {

    public PageBuff() {
        skills = new int[]{
            Page.WEAPON_BOOSTER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.PAGE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Page.WEAPON_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
        }
    }
}
