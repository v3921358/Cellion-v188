package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.DualBlade;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DualBladeEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DualBlade.KATARA_BOOSTER:
                break;
            case DualBlade.KATARA_MASTERY:
                break;
            case DualBlade.SELF_HASTE:
                break;
            case DualBlade.TORNADO_SPIN:
                break;
            case DualBlade.TORNADO_SPIN_1:
                break;
            case DualBlade.TRIPLE_STAB:
                break;
            case DualBlade.CHANNEL_KARMA_2:
                break;
            case DualBlade.CHANNEL_KARMA_3:
                break;
            case DualBlade.FATAL_BLOW:
                break;
            case DualBlade.FLASH_JUMP_5:
                break;
            case DualBlade.KATARA_BOOSTER_1:
                break;
            case DualBlade.PHYSICAL_TRAINING_20_2:
                break;
            case DualBlade.SELF_HASTE_2:
                break;
            case DualBlade.SHADOW_RESISTANCE_1:
                break;
            case DualBlade.SLASH_STORM:
                break;
            case DualBlade.FLASHBANG:
                break;
            case DualBlade.FLYING_ASSAULTER_1:
                break;
            case DualBlade.TORNADO_SPIN_2:
                break;
            case DualBlade.TORNADO_SPIN_3:
                break;
            case DualBlade.UPPER_STAB_1:
                break;
            case DualBlade.VENOM:
                break;
            case DualBlade.ADVANCED_DARK_SIGHT_1:
                break;
            case DualBlade.BLADE_ASCENSION:
                break;
            case DualBlade.BLOODY_STORM:
                break;
            case DualBlade.CHAINS_OF_HELL:
                break;
            case DualBlade.ENVELOPING_DARKNESS_4:
                break;
            case DualBlade.FLYING_ASSAULTER:
                break;
            case DualBlade.LIFE_DRAIN:
                break;
            case DualBlade.MIRROR_IMAGE:
                break;
            case DualBlade.OWL_SPIRIT:
                break;
            case DualBlade.SHADOW_MELD:
                break;
            case DualBlade.UPPER_STAB:
                break;
            case DualBlade.ASURAS_ANGER:
                break;
            case DualBlade.BLADE_CLONE:
                break;
            case DualBlade.BLADE_FURY:
                break;
            case DualBlade.BLADE_FURY_GUARDBREAK:
                break;
            case DualBlade.BLADE_FURY_REINFORCE:
                break;
            case DualBlade.BLADE_FURY_SPREAD:
                break;
            case DualBlade.BLOODY_STORM_EXTRA_STRIKE:
                break;
            case DualBlade.BLOODY_STORM_REINFORCE:
                break;
            case DualBlade.BLOODY_STORM_SPREAD:
                break;
            case DualBlade.CHAINS_OF_HELL_1:
                break;
            case DualBlade.EPIC_ADVENTURE_9:
                break;
            case DualBlade.FINAL_CUT:
                break;
            case DualBlade.HEROS_WILL_100_10_1:
                break;
            case DualBlade.HYPER_ACCURACY_90_9:
                break;
            case DualBlade.HYPER_CRITICAL_90_9:
                break;
            case DualBlade.HYPER_DEFENSE_60_6:
                break;
            case DualBlade.HYPER_DEXTERITY_90_9:
                break;
            case DualBlade.HYPER_FURY_90_9:
                break;
            case DualBlade.HYPER_HEALTH_90_9:
                break;
            case DualBlade.HYPER_INTELLIGENCE_90_9:
                break;
            case DualBlade.HYPER_JUMP_90_9:
                break;
            case DualBlade.HYPER_LUCK_90_9:
                break;
            case DualBlade.HYPER_MAGIC_DEFENSE_90_9:
                break;
            case DualBlade.HYPER_MANA_90_9:
                break;
            case DualBlade.HYPER_SPEED_90_9:
                break;
            case DualBlade.HYPER_STRENGTH_90_9:
                break;
            case DualBlade.KATARA_EXPERT:
                break;
            case DualBlade.MAPLE_WARRIOR_200_20_2:
                break;
            case DualBlade.MIRRORED_TARGET:
                break;
            case DualBlade.MONSTER_BOMB:
                break;
            case DualBlade.PHANTOM_BLOW:
                break;
            case DualBlade.PHANTOM_BLOW_EXTRA_STRIKE:
                break;
            case DualBlade.PHANTOM_BLOW_GUARDBREAK:
                break;
            case DualBlade.PHANTOM_BLOW_REINFORCE:
                break;
            case DualBlade.SHARPNESS:
                break;
            case DualBlade.SUDDEN_RAID_1:
                break;
            case DualBlade.SUDDEN_RAID_COOLDOWN_CUTTER:
                break;
            case DualBlade.SUDDEN_RAID_CRIPPLE:
                break;
            case DualBlade.SUDDEN_RAID_REINFORCE:
                break;
            case DualBlade.THORNS:
                break;
            case DualBlade.THORNS_1:
                break;
            case DualBlade.TOXIC_VENOM:
                break;
            case DualBlade.VENOM_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 430 || nClass == 431 || nClass == 432 || nClass == 433 || nClass == 434;
    }

}
