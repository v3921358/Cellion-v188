package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.CannonMaster;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CannonMasterEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case CannonMaster.ANCHORS_AWEIGH:
                pEffect.statups.put(CharacterTemporaryStat.PUPPET, 1);
                break;
            case CannonMaster.BUCKSHOT:
                pEffect.statups.put(CharacterTemporaryStat.AttackCountX, pEffect.info.get(StatInfo.x));
                break;
            case CannonMaster.CANNON_BARRAGE:
                break;
            case CannonMaster.CANNON_BARRAGE_CRITICAL_CHANCE:
                break;
            case CannonMaster.CANNON_BARRAGE_EXTRA_STRIKE:
                break;
            case CannonMaster.CANNON_BARRAGE_REINFORCE:
                break;
            case CannonMaster.CANNON_BAZOOKA:
                break;
            case CannonMaster.CANNON_BAZOOKA_EXTRA_STRIKE:
                break;
            case CannonMaster.CANNON_BAZOOKA_REINFORCE:
                break;
            case CannonMaster.CANNON_BAZOOKA_SPREAD:
                break;
            case CannonMaster.CANNON_OVERLOAD:
                break;
            case CannonMaster.DOUBLE_DOWN:
                pEffect.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case CannonMaster.EPIC_ADVENTURE_2:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case CannonMaster.HEROS_WILL_7:
                break;
            case CannonMaster.HYPER_ACCURACY_2:
                break;
            case CannonMaster.HYPER_CRITICAL_2:
                break;
            case CannonMaster.HYPER_DEFENSE_1:
                break;
            case CannonMaster.HYPER_DEXTERITY_2:
                break;
            case CannonMaster.HYPER_FURY_2:
                break;
            case CannonMaster.HYPER_HEALTH_2:
                break;
            case CannonMaster.HYPER_INTELLIGENCE_2:
                break;
            case CannonMaster.HYPER_JUMP_2:
                break;
            case CannonMaster.HYPER_LUCK_2:
                break;
            case CannonMaster.HYPER_MAGIC_DEFENSE_2:
                break;
            case CannonMaster.HYPER_MANA_2:
                break;
            case CannonMaster.HYPER_SPEED_2:
                break;
            case CannonMaster.HYPER_STRENGTH_2:
                break;
            case CannonMaster.MAPLE_WARRIOR_8:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(StatInfo.x));
                break;
            case CannonMaster.MEGA_MONKEY_MAGIC:
                break;
            case CannonMaster.MONKEY_MILITIA:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case CannonMaster.MONKEY_MILITIA_1:
                break;
            case CannonMaster.MONKEY_MILITIA_ENHANCE:
                break;
            case CannonMaster.MONKEY_MILITIA_PERSIST:
                break;
            case CannonMaster.MONKEY_MILITIA_SPLITTER:
                break;
            case CannonMaster.NAUTILUS_STRIKE:
                break;
            case CannonMaster.PIRATES_SPIRIT:
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(StatInfo.prop));
                break;
            case CannonMaster.ROLLING_RAINBOW:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 532;
    }

}
