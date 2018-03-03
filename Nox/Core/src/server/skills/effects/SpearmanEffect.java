package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Spearman;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class SpearmanEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Spearman.EVIL_EYE:
                break;
            case Spearman.FINAL_ATTACK_2:
                break;
            case Spearman.GROUND_SMASH_1:
                break;
            case Spearman.HYPER_BODY_1:
                break;
            case Spearman.IRON_WILL_2:
                break;
            case Spearman.PHYSICAL_TRAINING_2:
                break;
            case Spearman.PIERCING_DRIVE:
                break;
            case Spearman.SLIPSTREAM_1:
                break;
            case Spearman.SPEAR_SWEEP:
                break;
            case Spearman.WEAPON_BOOSTER_1:
                break;
            case Spearman.WEAPON_MASTERY:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 130;
    }

}
