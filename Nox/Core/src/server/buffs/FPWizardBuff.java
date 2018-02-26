package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.FPWizard;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class FPWizardBuff extends AbstractBuffClass {

    public FPWizardBuff() {
        skills = new int[]{
            FPWizard.MAGIC_BOOSTER,
            FPWizard.MEDITATION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.FP_WIZARD.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case FPWizard.MAGIC_BOOSTER: //Magic Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case FPWizard.MEDITATION: //inc matk
                break;
        }
    }
}
