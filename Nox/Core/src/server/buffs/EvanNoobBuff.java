package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.EvanNoob;
import server.MapleStatEffect;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class EvanNoobBuff extends AbstractBuffClass {

    public EvanNoobBuff() {
        skills = new int[]{
            EvanNoob.NIMBLE_FEET
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.EVAN_NOOB.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case EvanNoob.NIMBLE_FEET:
                eff.statups.put(CharacterTemporaryStat.Speed, 10 + (eff.getLevel() - 1) * 5);
                break;
        }
    }
}
