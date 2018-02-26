package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Crossbowman;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CrossbowmanBuff extends AbstractBuffClass {

    public CrossbowmanBuff() {
        skills = new int[]{
            Crossbowman.SOUL_ARROW_CROSSBOW,
            Crossbowman.CROSSBOW_BOOSTER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CROSSBOWMAN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Crossbowman.CROSSBOW_BOOSTER: //Bow Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Crossbowman.SOUL_ARROW_CROSSBOW: //SoulArrow xbow
                eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, 1);
                break;
        }
    }
}
