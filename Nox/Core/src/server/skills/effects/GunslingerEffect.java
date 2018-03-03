package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Gunslinger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class GunslingerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Gunslinger.BLANK_SHOT:
                break;
            case Gunslinger.CRITICAL_SHOT_1:
                break;
            case Gunslinger.GRENADE:
                break;
            case Gunslinger.GUN_BOOSTER_1:
                break;
            case Gunslinger.GUN_MASTERY:
                break;
            case Gunslinger.INFINITY_BLAST:
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
