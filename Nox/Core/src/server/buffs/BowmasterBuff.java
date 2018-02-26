package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import static constants.GameConstants.isArcherBowmaster;
import constants.skills.Bowmaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BowmasterBuff extends AbstractBuffClass {

    public BowmasterBuff() {
        skills = new int[]{
            Bowmaster.MAPLE_WARRIOR,
            Bowmaster.SHARP_EYES,
            Bowmaster.EPIC_ADVENTURE,
            Bowmaster.ILLUSION_STEP,
            Bowmaster.CONCENTRATION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isArcherBowmaster(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Bowmaster.ILLUSION_STEP: //Illusion Step
                eff.statups.put(CharacterTemporaryStat.DEX, eff.info.get(MapleStatInfo.dex));
                eff.statups.put(CharacterTemporaryStat.EVAR, eff.info.get(MapleStatInfo.x));
                break;
            case Bowmaster.CONCENTRATION: //Concentration
                eff.statups.put(CharacterTemporaryStat.Concentration, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Bowmaster.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Bowmaster.SHARP_EYES:
                eff.statups.put(CharacterTemporaryStat.SharpEyes, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Bowmaster.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
