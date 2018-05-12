package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Gunslinger;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class GunslingerEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Gunslinger.BLANK_SHOT:
                break;
            case Gunslinger.CRITICAL_SHOT_1:
                break;
            case Gunslinger.GRENADE:
                break;
            case Gunslinger.GUN_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x) * 2);
                break;
            case Gunslinger.GUN_MASTERY:
                break;
            case Gunslinger.INFINITY_BLAST:
                pEffect.statups.put(CharacterTemporaryStat.NoBulletConsume, pEffect.info.get(StatInfo.bulletConsume));
                break;
            case Gunslinger.PHYSICAL_TRAINING_3:
                break;
            case Gunslinger.RAPID_BLAST:
                break;
            case Gunslinger.RECOIL_SHOT:
                break;
            case Gunslinger.SCURVY_SUMMONS:
                break;
            case Gunslinger.SCURVY_SUMMONS_1:
                break;
            case Gunslinger.SCURVY_SUMMONS_2:
                break;
            case Gunslinger.TRIPLE_FIRE_1:
                break;
            case Gunslinger.WINGS:
                break;
            case Gunslinger.WINGS_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 520;
    }

}
