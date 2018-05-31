package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Thief;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ThiefEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Thief.BANDIT_SLASH:
                break;
            case Thief.DARK_SIGHT_2:
                pEffect.statups.put(CharacterTemporaryStat.DarkSight, pEffect.info.get(StatInfo.x));
                break;
            case Thief.DISORDER_1:
                break;
            case Thief.DOUBLE_STAB:
                break;
            case Thief.FLASH_JUMP_3:
                break;
            case Thief.FLASH_JUMP_4:
                break;
            case Thief.HASTE_4:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(StatInfo.speed));
                pEffect.statups.put(CharacterTemporaryStat.Jump, pEffect.info.get(StatInfo.jump));
                break;
            case Thief.KEEN_EYES_1:
                break;
            case Thief.LUCKY_SEVEN:
                break;
            case Thief.MAGIC_THEFT_1:
                break;
            case Thief.NIMBLE_BODY_2:
                break;
            case Thief.SELF_HASTE_1:
                break;
            case Thief.SIDE_STEP:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 400;
    }

}
