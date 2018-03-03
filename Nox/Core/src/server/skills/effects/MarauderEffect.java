package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Marauder;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MarauderEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Marauder.ADMIRALS_WINGS:
                break;
            case Marauder.ADMIRALS_WINGS_1:
                break;
            case Marauder.BRAWLING_MASTERY:
                break;
            case Marauder.ENERGY_BURST:
                break;
            case Marauder.ENERGY_CHARGE:
                break;
            case Marauder.ENERGY_DRAIN:
                break;
            case Marauder.HEDGEHOG_BUSTER:
                break;
            case Marauder.PRECISION_STRIKES:
                break;
            case Marauder.ROLL_OF_THE_DICE_5:
                break;
            case Marauder.SHOCKWAVE:
                break;
            case Marauder.SPIRAL_ASSAULT:
                break;
            case Marauder.STATIC_THUMPER:
                break;
            case Marauder.STATIC_THUMPER_1:
                break;
            case Marauder.STUN_MASTERY:
                break;
            case Marauder.SUPERCHARGE:
                break;
            case Marauder.TRANSFORMATION:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 511;
    }

}
