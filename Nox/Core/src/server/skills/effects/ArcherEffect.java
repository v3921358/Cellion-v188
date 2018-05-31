package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Archer;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ArcherEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Archer.ARCHERY_MASTERY:
                break;
            case Archer.ARROW_BLOW:
                break;
            case Archer.CRITICAL_SHOT:
                break;
            case Archer.DOUBLE_JUMP:
                break;
            case Archer.DOUBLE_JUMP_1:
                break;
            case Archer.DOUBLE_JUMP_3:
                break;
            case Archer.DOUBLE_JUMP_8:
                break;
            case Archer.DOUBLE_SHOT_1:
                break;
            case Archer.NATURES_BALANCE_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 300;
    }

}
