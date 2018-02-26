package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Cleric;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ClericBuff extends AbstractBuffClass {

    public ClericBuff() {
        skills = new int[]{
            Cleric.INVINCIBLE,
            Cleric.BLESS,
            Cleric.BLESSED_ENSEMBLE,
            Cleric.MAGIC_BOOSTER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CLERIC.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Cleric.MAGIC_BOOSTER: //Magic Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Cleric.INVINCIBLE: //Invicible
                eff.statups.put(CharacterTemporaryStat.Invincible, eff.info.get(MapleStatInfo.x));
                break;
            case Cleric.BLESSED_ENSEMBLE: //Blessed Ensemble
                eff.statups.put(CharacterTemporaryStat.BlessEnsenble, eff.info.get(MapleStatInfo.x));
                break;
            case Cleric.BLESS: //Bless   
                eff.statups.put(CharacterTemporaryStat.Bless, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
