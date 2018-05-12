package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Spearman;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class SpearmanEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Spearman.EVIL_EYE:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.statups.put(CharacterTemporaryStat.Beholder, pEffect.info.get(StatInfo.x));
                break;
            case Spearman.FINAL_ATTACK_2:
                break;
            case Spearman.GROUND_SMASH_1:
                break;
            case Spearman.HYPER_BODY_1:
                pEffect.statups.put(CharacterTemporaryStat.MaxHP, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.MaxMP, pEffect.info.get(StatInfo.x));
                break;
            case Spearman.IRON_WILL_2:
                pEffect.statups.put(CharacterTemporaryStat.PDD, pEffect.info.get(StatInfo.pdd));
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
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x) * 2);
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
