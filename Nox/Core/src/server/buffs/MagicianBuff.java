package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Magician;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class MagicianBuff extends AbstractBuffClass {

    public MagicianBuff() {
        skills = new int[]{
            Magician.MAGIC_GUARD,};

    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.MAGICIAN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Magician.MAGIC_GUARD: //Magic Guard
                eff.statups.put(CharacterTemporaryStat.MagicGuard, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
