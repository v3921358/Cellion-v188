package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Hero;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class HeroBuff extends AbstractBuffClass {

    public HeroBuff() {
        skills = new int[]{
            Hero.MAPLE_WARRIOR,
            Hero.ENRAGE,
            Hero.EPIC_ADVENTURE,
            Hero.CRY_VALHALLA
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.HERO.getId()
                || job == MapleJob.HERO_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Hero.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Hero.ENRAGE:
                eff.statups.put(CharacterTemporaryStat.Enrage, eff.info.get(MapleStatInfo.x) * 100 + eff.info.get(MapleStatInfo.mobCount));
                break;
            case Hero.CRY_VALHALLA:
                eff.statups.clear();
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Hero.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
