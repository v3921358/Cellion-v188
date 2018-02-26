package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isBlazeWizardCygnus;
import constants.skills.BlazeWizard;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BlazeWizardBuff extends AbstractBuffClass {

    public BlazeWizardBuff() {
        skills = new int[]{
            BlazeWizard.IGNITION,
            BlazeWizard.WORD_OF_FIRE,
            BlazeWizard.PHOENIX_RUN,
            BlazeWizard.BURNING_CONDUIT,
            BlazeWizard.FIRES_OF_CREATION,
            BlazeWizard.FLAME_BARRIER,
            BlazeWizard.CALL_OF_CYGNUS,
            BlazeWizard.GLORY_OF_THE_GUARDIANS
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isBlazeWizardCygnus(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BlazeWizard.WORD_OF_FIRE:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case BlazeWizard.FLAME_BARRIER:
                eff.statups.put(CharacterTemporaryStat.FireBarrier, eff.info.get(MapleStatInfo.x));
                break;
            case BlazeWizard.BURNING_CONDUIT:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                break;
            case BlazeWizard.IGNITION:
                eff.statups.put(CharacterTemporaryStat.WizardIgnite, 1);
                break;
            case BlazeWizard.FIRES_OF_CREATION:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                //TODO
                break;
            case BlazeWizard.CALL_OF_CYGNUS:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case BlazeWizard.GLORY_OF_THE_GUARDIANS:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
        }
    }
}
