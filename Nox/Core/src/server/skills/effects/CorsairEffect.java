package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Corsair;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CorsairEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Corsair.AHOY_MATEYS:
                break;
            case Corsair.AIR_STRIKE_1:
                break;
            case Corsair.BATTLESHIP_2:
                break;
            case Corsair.BATTLESHIP_CANNON_1:
                break;
            case Corsair.BATTLESHIP_TORPEDO_1:
                break;
            case Corsair.BRAIN_SCRAMBLER:
                break;
            case Corsair.BRAIN_SCRAMBLER_BOSS_RUSH:
                break;
            case Corsair.BRAIN_SCRAMBLER_EXTRA_STRIKE:
                break;
            case Corsair.BRAIN_SCRAMBLER_REINFORCE:
                break;
            case Corsair.BROADSIDE:
                break;
            case Corsair.BROADSIDE_1:
                break;
            case Corsair.BROADSIDE_2:
                break;
            case Corsair.BROADSIDE_3:
                break;
            case Corsair.BULLSEYE_1:
                break;
            case Corsair.DOUBLE_DOWN_4:
                break;
            case Corsair.DOUBLE_DOWN_ADDITION_1:
                break;
            case Corsair.DOUBLE_DOWN_ENHANCE_1:
                break;
            case Corsair.DOUBLE_DOWN_SAVING_GRACE_1:
                break;
            case Corsair.EIGHTLEGS_EASTON:
                break;
            case Corsair.ELEMENTAL_BOOST_1:
                break;
            case Corsair.EPIC_ADVENTURE_8:
                break;
            case Corsair.HEROS_WILL_50_5:
                break;
            case Corsair.HYPER_ACCURACY_6000_600_60_6:
                break;
            case Corsair.HYPER_CRITICAL_6000_600_60_6:
                break;
            case Corsair.HYPER_DEFENSE_1000_100_10:
                break;
            case Corsair.HYPER_DEXTERITY_6000_600_60_6:
                break;
            case Corsair.HYPER_FURY_7000_700_70_7:
                break;
            case Corsair.HYPER_HEALTH_6000_600_60_6:
                break;
            case Corsair.HYPER_INTELLIGENCE_6000_600_60_6:
                break;
            case Corsair.HYPER_JUMP_7000_700_70_7:
                break;
            case Corsair.HYPER_LUCK_6000_600_60_6:
                break;
            case Corsair.HYPER_MAGIC_DEFENSE_7000_700_70_7:
                break;
            case Corsair.HYPER_MANA_6000_600_60_6:
                break;
            case Corsair.HYPER_SPEED_7000_700_70_7:
                break;
            case Corsair.HYPER_STRENGTH_6000_600_60_6:
                break;
            case Corsair.HYPNOTIZE_1:
                break;
            case Corsair.JOLLY_ROGER:
                break;
            case Corsair.MAJESTIC_PRESENCE:
                break;
            case Corsair.MAPLE_WARRIOR_60_6:
                break;
            case Corsair.NAUTILUS_STRIKE_3:
                break;
            case Corsair.PARROTARGETTING:
                break;
            case Corsair.PIRATES_REVENGE_3:
                break;
            case Corsair.QUICKDRAW:
                break;
            case Corsair.RAPID_FIRE_1:
                break;
            case Corsair.RAPID_FIRE_ADD_RANGE:
                break;
            case Corsair.RAPID_FIRE_BOSS_RUSH:
                break;
            case Corsair.RAPID_FIRE_REINFORCE:
                break;
            case Corsair.UGLY_BOMB:
                break;
            case Corsair.WHALERS_POTION:
                break;
            case Corsair.WRATH_OF_THE_OCTOPI_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 522;
    }

}
