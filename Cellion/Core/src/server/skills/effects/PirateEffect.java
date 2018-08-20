package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Pirate;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PirateEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Pirate.BULLET_TIME:
                break;
            case Pirate.DASH:
                pEffect.statups.put(CharacterTemporaryStat.DashSpeed, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DashJump, pEffect.info.get(StatInfo.y));
                pEffect.info.put(StatInfo.time, 20000);
                break;
            case Pirate.DOUBLE_SHOT:
                break;
            case Pirate.FLASH_FIST:
                break;
            case Pirate.FORTUNES_FAVOR_1:
                break;
            case Pirate.OCTOPUSH:
                break;
            case Pirate.SHADOW_HEART_2:
                break;
            case Pirate.SOMMERSAULT_KICK:
                break;
            case Pirate.TRIPLE_FIRE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 500;
    }

}
