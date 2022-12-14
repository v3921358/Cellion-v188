package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.CannonBlaster;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CannonBlasterEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case CannonBlaster.BARREL_ROULETTE:
                break;
            case CannonBlaster.BARREL_ROULETTE_1:
                break;
            case CannonBlaster.CANNON_JUMP:
                break;
            case CannonBlaster.CANNON_SPIKE:
                break;
            case CannonBlaster.COUNTER_CRUSH:
                break;
            case CannonBlaster.LUCK_OF_THE_DIE:
                pEffect.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case CannonBlaster.MONKEY_FURY:
                break;
            case CannonBlaster.MONKEY_FURY_1:
                break;
            case CannonBlaster.MONKEY_MADNESS:
                break;
            case CannonBlaster.MONKEY_WAVE:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case CannonBlaster.MONKEY_WAVE_1:
                break;
            case CannonBlaster.PIRATE_RUSH:
                break;
            case CannonBlaster.REINFORCED_CANNON:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 531;
    }

}
