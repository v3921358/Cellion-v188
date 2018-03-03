package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Bandit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BanditEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Bandit.CHANNEL_KARMA:
                break;
            case Bandit.CHANNEL_KARMA_1:
                break;
            case Bandit.CRITICAL_GROWTH:
                break;
            case Bandit.DAGGER_BOOSTER:
                break;
            case Bandit.DAGGER_MASTERY:
                break;
            case Bandit.FLASH_JUMP_2:
                break;
            case Bandit.HASTE_3:
                break;
            case Bandit.MESOGUARD:
                break;
            case Bandit.PHYSICAL_TRAINING_9:
                break;
            case Bandit.SAVAGE_BLOW:
                break;
            case Bandit.SAVAGE_BLOW_1:
                break;
            case Bandit.SHADOW_RESISTANCE:
                break;
            case Bandit.SHIELD_MASTERY_1:
                break;
            case Bandit.STEAL:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 420;
    }

}
