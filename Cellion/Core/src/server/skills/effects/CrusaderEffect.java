package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Crusader;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CrusaderEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Crusader.CHANCE_ATTACK:
                break;
            case Crusader.COMA:
                break;
            case Crusader.COMBO_ATTACK_1:
                break;
            case Crusader.COMBO_SYNERGY:
                break;
            case Crusader.ENDURE:
                break;
            case Crusader.INTREPID_SLASH:
                break;
            case Crusader.MAGIC_CRASH_6:
                break;
            case Crusader.PANIC:
                pEffect.monsterStatus.put(MonsterStatus.DARKNESS, pEffect.info.get(StatInfo.x));
                break;
            case Crusader.RUSH_5:
                break;
            case Crusader.SELF_RECOVERY_4:
                break;
            case Crusader.SHOUT:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Crusader.SHOUT_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 111;
    }

}
