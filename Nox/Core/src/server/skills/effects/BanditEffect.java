package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Bandit;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BanditEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Bandit.CHANNEL_KARMA:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.pad));
                break;
            case Bandit.CHANNEL_KARMA_1:
                break;
            case Bandit.CRITICAL_GROWTH:
                break;
            case Bandit.DAGGER_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Bandit.DAGGER_MASTERY:
                break;
            case Bandit.FLASH_JUMP_2:
                break;
            case Bandit.HASTE_3:
                break;
            case Bandit.MESOGUARD:
                pEffect.statups.put(CharacterTemporaryStat.MesoGuard, pEffect.info.get(StatInfo.x));
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
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 420;
    }

}
