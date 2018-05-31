package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Page;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PageEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Page.BLIZZARD_CHARGE_1:
                break;
            case Page.CLOSE_COMBAT:
                break;
            case Page.ELEMENTAL_CHARGE:
                break;
            case Page.FINAL_ATTACK_4:
                break;
            case Page.FLAME_CHARGE_1:
                break;
            case Page.GROUND_SMASH_2:
                break;
            case Page.PHYSICAL_TRAINING_10_1:
                break;
            case Page.POWER_GUARD:
                break;
            case Page.SLIPSTREAM_2:
                break;
            case Page.THREATEN_1:
                break;
            case Page.WEAPON_BOOSTER_2:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x) * 2);
                break;
            case Page.WEAPON_MASTERY_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 120;
    }

}
