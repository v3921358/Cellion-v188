package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Spearman;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class SpearmanBuff extends AbstractBuffClass {

    public SpearmanBuff() {
        skills = new int[]{
            Spearman.WEAPON_BOOSTER,
            Spearman.HYPER_BODY,
            Spearman.EVIL_EYE,
            Spearman.IRON_WILL
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.SPEARMAN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Spearman.WEAPON_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case Spearman.EVIL_EYE:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.statups.put(CharacterTemporaryStat.Beholder, eff.info.get(MapleStatInfo.x));
                break;
            case Spearman.IRON_WILL: // Iron Will
                eff.statups.put(CharacterTemporaryStat.PDD, eff.info.get(MapleStatInfo.pdd));
                break;
            case Spearman.HYPER_BODY: // Hyper Body
                eff.statups.put(CharacterTemporaryStat.MaxHP, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.MaxMP, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
