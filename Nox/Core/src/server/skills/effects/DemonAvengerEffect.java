package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.DemonAvenger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DemonAvengerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DemonAvenger.DEMONIC_VERACITY:
                break;
            case DemonAvenger.EXCEED_DOUBLE_SLASH:
                break;
            case DemonAvenger.EXCEED_DOUBLE_SLASH_1:
                break;
            case DemonAvenger.EXCEED_DOUBLE_SLASH_2:
                break;
            case DemonAvenger.EXCEED_DOUBLE_SLASH_3:
                break;
            case DemonAvenger.EXCEED_DOUBLE_SLASH_4:
                break;
            case DemonAvenger.LIFE_SAP:
                break;
            case DemonAvenger.OVERLOAD_RELEASE:
                break;
            case DemonAvenger.ABYSSAL_CONNECTION:
                break;
            case DemonAvenger.ABYSSAL_CONNECTION_1:
                break;
            case DemonAvenger.BATTLE_PACT:
                break;
            case DemonAvenger.BAT_SWARM:
                break;
            case DemonAvenger.DESPERADO_MASTERY:
                break;
            case DemonAvenger.EXCEED_DEMON_STRIKE:
                break;
            case DemonAvenger.EXCEED_DEMON_STRIKE_1:
                break;
            case DemonAvenger.EXCEED_DEMON_STRIKE_2:
                break;
            case DemonAvenger.EXCEED_DEMON_STRIKE_3:
                break;
            case DemonAvenger.EXCEED_DEMON_STRIKE_4:
                break;
            case DemonAvenger.RAGE_WITHIN:
                break;
            case DemonAvenger.UNBREAKABLE_STEEL:
                break;
            case DemonAvenger.ADVANCED_LIFE_SAP:
                break;
            case DemonAvenger.DIABOLIC_RECOVERY:
                break;
            case DemonAvenger.EXCEED_LUNAR_SLASH:
                break;
            case DemonAvenger.EXCEED_LUNAR_SLASH_1:
                break;
            case DemonAvenger.EXCEED_LUNAR_SLASH_2:
                break;
            case DemonAvenger.EXCEED_LUNAR_SLASH_3:
                break;
            case DemonAvenger.EXCEED_LUNAR_SLASH_4:
                break;
            case DemonAvenger.PAIN_DAMPENER:
                break;
            case DemonAvenger.SHIELD_CHARGE:
                break;
            case DemonAvenger.SHIELD_CHARGE_1:
                break;
            case DemonAvenger.VITALITY_VEIL:
                break;
            case DemonAvenger.WARD_EVIL:
                break;
            case DemonAvenger.ADVANCED_DESPERADO_MASTERY:
                break;
            case DemonAvenger.BLOOD_PRISON:
                break;
            case DemonAvenger.DEFENSE_EXPERTISE:
                break;
            case DemonAvenger.DEMONIC_FORTITUDE:
                break;
            case DemonAvenger.EXCEED_EXECUTION:
                break;
            case DemonAvenger.EXCEED_EXECUTION_1:
                break;
            case DemonAvenger.EXCEED_EXECUTION_2:
                break;
            case DemonAvenger.EXCEED_EXECUTION_3:
                break;
            case DemonAvenger.EXCEED_EXECUTION_4:
                break;
            case DemonAvenger.EXCEED_OPPORTUNITY:
                break;
            case DemonAvenger.EXCEED_REDUCE_OVERLOAD:
                break;
            case DemonAvenger.EXCEED_REINFORCE:
                break;
            case DemonAvenger.FORBIDDEN_CONTRACT:
                break;
            case DemonAvenger.HYPER_ACCURACY_40_4:
                break;
            case DemonAvenger.HYPER_CRITICAL_30_3:
                break;
            case DemonAvenger.HYPER_DEFENSE_10_1:
                break;
            case DemonAvenger.HYPER_DEXTERITY_40_4:
                break;
            case DemonAvenger.HYPER_FURY_40_4:
                break;
            case DemonAvenger.HYPER_HEALTH_40_4:
                break;
            case DemonAvenger.HYPER_INTELLIGENCE_40_4:
                break;
            case DemonAvenger.HYPER_JUMP_40_4:
                break;
            case DemonAvenger.HYPER_LUCK_30_3:
                break;
            case DemonAvenger.HYPER_MAGIC_DEFENSE_40_4:
                break;
            case DemonAvenger.HYPER_MANA_40_4:
                break;
            case DemonAvenger.HYPER_SPEED_40_4:
                break;
            case DemonAvenger.HYPER_STRENGTH_30_3:
                break;
            case DemonAvenger.INFERNAL_EXCEED:
                break;
            case DemonAvenger.MAPLE_WARRIOR_2:
                break;
            case DemonAvenger.NETHER_SHIELD:
                break;
            case DemonAvenger.NETHER_SHIELD_1:
                break;
            case DemonAvenger.NETHER_SHIELD_2:
                break;
            case DemonAvenger.NETHER_SHIELD_RANGE:
                break;
            case DemonAvenger.NETHER_SHIELD_REINFORCE:
                break;
            case DemonAvenger.NETHER_SHIELD_SPREAD:
                break;
            case DemonAvenger.NETHER_SLICE:
                break;
            case DemonAvenger.OVERWHELMING_POWER:
                break;
            case DemonAvenger.OVERWHELMING_POWER_1:
                break;
            case DemonAvenger.THOUSAND_SWORDS:
                break;
            case DemonAvenger.WARD_EVIL_HARDEN:
                break;
            case DemonAvenger.WARD_EVIL_IMMUNITY_ENHANCE_1:
                break;
            case DemonAvenger.WARD_EVIL_IMMUNITY_ENHANCE_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3101 || nClass == 3120 || nClass == 3121 || nClass == 3122;
    }

}
