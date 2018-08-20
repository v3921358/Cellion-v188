package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.IceLightningWizard;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class IceLightningWizardEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case IceLightningWizard.CHILLING_STEP:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.mpCon, pEffect.info.get(StatInfo.y));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case IceLightningWizard.COLD_BEAM:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                break;
            case IceLightningWizard.COLD_BEAM_1:
                break;
            case IceLightningWizard.FREEZING_CRUSH:
                break;
            case IceLightningWizard.HIGH_WISDOM_7:
                break;
            case IceLightningWizard.MAGIC_BOOSTER_5:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case IceLightningWizard.MEDITATION_2:
                pEffect.statups.put(CharacterTemporaryStat.MAD, pEffect.info.get(StatInfo.x));
                break;
            case IceLightningWizard.MP_EATER_2:
                break;
            case IceLightningWizard.SLOW_4:
                break;
            case IceLightningWizard.SPELL_MASTERY_6:
                break;
            case IceLightningWizard.TELEPORT_7:
                break;
            case IceLightningWizard.THUNDER_BOLT:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 220;
    }

}
