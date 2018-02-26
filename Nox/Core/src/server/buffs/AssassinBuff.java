package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Assassin;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 * @author Novak
 *
 */
@BuffEffectManager
public class AssassinBuff extends AbstractBuffClass {

    public AssassinBuff() {
        skills = new int[]{
            Assassin.CLAW_BOOSTER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.ASSASSIN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Assassin.CLAW_BOOSTER: //Claw Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
