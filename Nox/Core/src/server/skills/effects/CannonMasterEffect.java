package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.CannonMaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CannonMasterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case CannonMaster.ANCHORS_AWEIGH:
                break;
            case CannonMaster.BUCKSHOT:
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
                break;
            case CannonMaster.EPIC_ADVENTURE_2:
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
                break;
            case CannonMaster.MEGA_MONKEY_MAGIC:
                break;
            case CannonMaster.MONKEY_MILITIA:
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
