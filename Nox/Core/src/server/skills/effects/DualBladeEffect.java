package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.DualBlade;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DualBladeEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DualBlade.KATARA_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case DualBlade.KATARA_MASTERY:
                break;
            case DualBlade.SELF_HASTE:
                pEffect.statups.put(CharacterTemporaryStat.Jump, pEffect.info.get(StatInfo.jump));
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(StatInfo.speed));
                break;
            case DualBlade.TORNADO_SPIN:
                break;
            case DualBlade.TORNADO_SPIN_1:
                break;
            case DualBlade.TRIPLE_STAB:
                break;
            case DualBlade.CHANNEL_KARMA_2:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.pad));
                break;
            case DualBlade.CHANNEL_KARMA_3:
                break;
            case DualBlade.FATAL_BLOW:
                break;
            case DualBlade.FLASH_JUMP_5:
                break;
            case DualBlade.KATARA_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
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
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
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
                pEffect.statups.put(CharacterTemporaryStat.DarkSight, (int) pEffect.getLevel());
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
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, pEffect.info.get(StatInfo.x));
                break;
            case DualBlade.OWL_SPIRIT:
                break;
            case DualBlade.SHADOW_MELD:
                break;
            case DualBlade.UPPER_STAB:
                break;
            case DualBlade.ASURAS_ANGER:
                pEffect.statups.put(CharacterTemporaryStat.Asura, pEffect.info.get(StatInfo.x));
                break;
            case DualBlade.BLADE_CLONE:
                pEffect.statups.put(CharacterTemporaryStat.StackBuff, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
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
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case DualBlade.FINAL_CUT:
                pEffect.statups.put(CharacterTemporaryStat.FinalCut, pEffect.info.get(StatInfo.w));
                pEffect.addHpR(-pEffect.info.get(StatInfo.x) / 100.0);
                pEffect.info.put(StatInfo.time, 60 * 1000);
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
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case DualBlade.MIRRORED_TARGET:
                pEffect.statups.put(CharacterTemporaryStat.PUPPET, 1);
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
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(StatInfo.prop));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, (int) pEffect.info.get(StatInfo.epad));
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
