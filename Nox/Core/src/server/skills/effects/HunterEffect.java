package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hunter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class HunterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Hunter.ARROW_BOMB:
                break;
            case Hunter.BOW_BOOSTER_2:
                break;
            case Hunter.BOW_MASTERY_2:
                break;
            case Hunter.COVERING_FIRE:
                break;
            case Hunter.DOUBLE_JUMP_4:
                break;
            case Hunter.FINAL_ATTACK_BOW:
                break;
            case Hunter.PHYSICAL_TRAINING_8:
                break;
            case Hunter.QUIVER_CARTRIDGE:
                break;
            case Hunter.QUIVER_CARTRIDGE_1:
                break;
            case Hunter.SILVER_HAWK:
                break;
            case Hunter.SOUL_ARROW_BOW:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 310;
    }

}
