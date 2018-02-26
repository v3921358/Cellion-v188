package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Brawler;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BrawlerBuff extends AbstractBuffClass {

    public BrawlerBuff() {
        skills = new int[]{
            Brawler.KNUCKLE_BOOSTER,
            Brawler.DARK_CLARITY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BRAWLER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Brawler.KNUCKLE_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Brawler.DARK_CLARITY: //Dark Clarity
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
        }
    }
}
