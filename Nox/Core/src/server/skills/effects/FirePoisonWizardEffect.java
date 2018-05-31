package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.FirePoisonWizard;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class FirePoisonWizardEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case FirePoisonWizard.ELEMENTAL_DRAIN:
                break;
            case FirePoisonWizard.FLAME_ORB:
                break;
            case FirePoisonWizard.HIGH_WISDOM_2:
                break;
            case FirePoisonWizard.IGNITE:
                break;
            case FirePoisonWizard.IGNITE_1:
                break;
            case FirePoisonWizard.MAGIC_BOOSTER_2:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case FirePoisonWizard.MEDITATION_1:
                break;
            case FirePoisonWizard.MP_EATER_1:
                break;
            case FirePoisonWizard.POISON_BREATH:
                break;
            case FirePoisonWizard.SLOW_2:
                break;
            case FirePoisonWizard.SPELL_MASTERY_2:
                break;
            case FirePoisonWizard.TELEPORT_5:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 210;
    }

}
