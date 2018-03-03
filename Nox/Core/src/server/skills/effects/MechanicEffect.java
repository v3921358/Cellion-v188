package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Mechanic;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MechanicEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Mechanic.FLAME_LAUNCHER:
                break;
            case Mechanic.FORTUNES_FAVOR_4:
                break;
            case Mechanic.GATLING_GUN:
                break;
            case Mechanic.GODDESS_GUARD_1000_100_10:
                break;
            case Mechanic.GODDESS_GUARD_4000_400_40_4:
                break;
            case Mechanic.HUMANOID_MECH:
                break;
            case Mechanic.ME07_DRILLHANDS:
                break;
            case Mechanic.ROCKET_BOOSTER:
                break;
            case Mechanic.ATOMIC_HAMMER:
                break;
            case Mechanic.ENHANCED_FLAME_LAUNCHER:
                break;
            case Mechanic.ENHANCED_GATLING_GUN:
                break;
            case Mechanic.HEAVY_GATLING_GUN:
                break;
            case Mechanic.HEAVY_WEAPON_MASTERY:
                break;
            case Mechanic.HOMING_BEACON_1:
                break;
            case Mechanic.MECHANIC_MASTERY:
                break;
            case Mechanic.MECHANIC_RAGE:
                break;
            case Mechanic.OPEN_PORTAL_GX9:
                break;
            case Mechanic.PERFECT_ARMOR:
                break;
            case Mechanic.PHYSICAL_TRAINING_30_3:
                break;
            case Mechanic.ROBO_LAUNCHER_RM7:
                break;
            case Mechanic.ROCKET_BOOSTER_1:
                break;
            case Mechanic.ACCELERATION_BOT_EX7:
                break;
            case Mechanic.ADVANCED_HOMING_BEACON:
                break;
            case Mechanic.AP_SALVO:
                break;
            case Mechanic.BATTLE_PROGRAM:
                break;
            case Mechanic.HEALING_ROBOT_HLX:
                break;
            case Mechanic.HEAVY_SALVO:
                break;
            case Mechanic.MECHANIZED_DEFENSE_SYSTEM:
                break;
            case Mechanic.MECH_SIEGE_MODE:
                break;
            case Mechanic.METAL_FIST_MASTERY:
                break;
            case Mechanic.OVERCLOCK:
                break;
            case Mechanic.OVERCLOCK_1:
                break;
            case Mechanic.PUNCH_LAUNCHER:
                break;
            case Mechanic.ROCK_N_SHOCK:
                break;
            case Mechanic.ROLL_OF_THE_DICE:
                break;
            case Mechanic.SATELLITE:
                break;
            case Mechanic.SATELLITE_1:
                break;
            case Mechanic.SATELLITE_2:
                break;
            case Mechanic.SUPPORT_UNIT_HEX:
                break;
            case Mechanic.TANK_MECH:
                break;
            case Mechanic.AMPLIFIER_ROBOT_AF11:
                break;
            case Mechanic.AP_SALVO_PLUS:
                break;
            case Mechanic.AP_SALVO_PLUS_1:
                break;
            case Mechanic.AP_SALVO_PLUS_EXTRA_STRIKE:
                break;
            case Mechanic.BOTS_N_TOTS:
                break;
            case Mechanic.BOTS_N_TOTS_1:
                break;
            case Mechanic.DISTORTION_BOMB:
                break;
            case Mechanic.DOUBLE_DOWN_3:
                break;
            case Mechanic.ENHANCED_SUPPORT_UNIT:
                break;
            case Mechanic.EXTREME_MECH:
                break;
            case Mechanic.FOR_LIBERTY_3:
                break;
            case Mechanic.FULL_SPREAD:
                break;
            case Mechanic.GIANT_ROBOT_SG88_:
                break;
            case Mechanic.HEAVY_SALVO_PLUS:
                break;
            case Mechanic.HEAVY_SALVO_PLUS_SPREAD:
                break;
            case Mechanic.HEROS_WILL_600_60_6:
                break;
            case Mechanic.HOMING_BEACON_RESEARCH:
                break;
            case Mechanic.HYPER_ACCURACY_1000_100_10_1:
                break;
            case Mechanic.HYPER_CRITICAL_1000_100_10_1:
                break;
            case Mechanic.HYPER_DEFENSE_600_60_6:
                break;
            case Mechanic.HYPER_DEXTERITY_2000_200_20_2:
                break;
            case Mechanic.HYPER_FURY_1000_100_10_1:
                break;
            case Mechanic.HYPER_HEALTH_1000_100_10_1:
                break;
            case Mechanic.HYPER_INTELLIGENCE_2000_200_20_2:
                break;
            case Mechanic.HYPER_JUMP_1000_100_10_1:
                break;
            case Mechanic.HYPER_LUCK_1000_100_10_1:
                break;
            case Mechanic.HYPER_MAGIC_DEFENSE_1000_100_10_1:
                break;
            case Mechanic.HYPER_MANA_1000_100_10_1:
                break;
            case Mechanic.HYPER_SPEED_1000_100_10_1:
                break;
            case Mechanic.HYPER_STRENGTH_2000_200_20_2:
                break;
            case Mechanic.LASER_BLAST:
                break;
            case Mechanic.MAPLE_WARRIOR_600_60_6:
                break;
            case Mechanic.MECH_ALLOY_RESEARCH:
                break;
            case Mechanic.MECH_HOVERING:
                break;
            case Mechanic.MECH_MISSILE_TANK:
                break;
            case Mechanic.MECH_SIEGE_MODE_1:
                break;
            case Mechanic.ROBOT_MASTERY:
                break;
            case Mechanic.ROCK_N_SHOCK_COOLDOWN_CUTTER:
                break;
            case Mechanic.ROCK_N_SHOCK_PERSIST:
                break;
            case Mechanic.ROCK_N_SHOCK_REINFORCE:
                break;
            case Mechanic.SALVO_REINFORCE:
                break;
            case Mechanic.SATELLITE_SAFETY:
                break;
            case Mechanic.SUPPORT_UNIT_HEX_PARTY_REINFORCE:
                break;
            case Mechanic.SUPPORT_UNIT_HEX_PERSIST:
                break;
            case Mechanic.SUPPORT_UNIT_HEX_REINFORCE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3500 || nClass == 3510 || nClass == 3511 || nClass == 3512;
    }

}
