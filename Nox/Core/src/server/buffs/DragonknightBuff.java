package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Dragonknight;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class DragonknightBuff extends AbstractBuffClass {

    public DragonknightBuff() {
        skills = new int[]{
            Dragonknight.EVIL_EYE_OF_DOMINATION,
            Dragonknight.CROSS_SURGE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.DRAGONKNIGHT.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Dragonknight.EVIL_EYE_OF_DOMINATION:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.statups.put(CharacterTemporaryStat.Beholder, eff.info.get(MapleStatInfo.x));
                break;
            case Dragonknight.CROSS_SURGE:
                // TODO
                break;
        }
    }
}
