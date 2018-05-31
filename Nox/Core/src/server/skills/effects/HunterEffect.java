package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Hunter;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class HunterEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Hunter.ARROW_BOMB:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Hunter.BOW_BOOSTER_2:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
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
                pEffect.statups.put(CharacterTemporaryStat.SoulArrow, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, pEffect.info.get(StatInfo.epad));
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 310;
    }

}
