package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.DawnWarrior;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DawnWarriorEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DawnWarrior.GUARDIAN_ARMOR_1:
                break;
            case DawnWarrior.HAND_OF_LIGHT:
                pEffect.statups.put(CharacterTemporaryStat.ACCR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case DawnWarrior.HAND_OF_LIGHT_1:
                break;
            case DawnWarrior.HP_BOOST_3:
                break;
            case DawnWarrior.INNER_VOICE:
                break;
            case DawnWarrior.IRON_BODY_1:
                break;
            case DawnWarrior.POWER_STRIKE:
                break;
            case DawnWarrior.SLASH_BLAST:
                break;
            case DawnWarrior.SOUL:
                break;
            case DawnWarrior.SOUL_ELEMENT:
                pEffect.statups.put(CharacterTemporaryStat.CygnusElementSkill, 1);
                pEffect.statups.put(CharacterTemporaryStat.ElementSoul, 1);
                pEffect.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case DawnWarrior.TRIPLE_SLASH:
                break;
            case DawnWarrior.BLUSTER:
                break;
            case DawnWarrior.BRANDISH_1:
                break;
            case DawnWarrior.DIVINE_HAND:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case DawnWarrior.DIVINE_HAND_1:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case DawnWarrior.FALLING_MOON:
                pEffect.statups.put(CharacterTemporaryStat.PoseType, 1);
                pEffect.statups.put(CharacterTemporaryStat.BuckShot, 1);
                pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(StatInfo.indieCr));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case DawnWarrior.FINAL_ATTACK_3:
                break;
            case DawnWarrior.FLICKER:
                break;
            case DawnWarrior.INNER_HARMONY:
                break;
            case DawnWarrior.PHYSICAL_TRAINING:
                break;
            case DawnWarrior.POWER_REFLECTION_1:
                break;
            case DawnWarrior.RAGE_1:
                break;
            case DawnWarrior.SHADOW_TACKLE:
                break;
            case DawnWarrior.SOUL_BLADE_1:
                break;
            case DawnWarrior.SOUL_RUSH:
                break;
            case DawnWarrior.SOUL_SPEED:
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, -pEffect.info.get(StatInfo.x));
                break;
            case DawnWarrior.SWORD_BOOSTER:
                break;
            case DawnWarrior.SWORD_MASTERY:
                break;
            case DawnWarrior.SWORD_MASTERY_1:
                break;
            case DawnWarrior.TRACE_CUT:
                break;
            case DawnWarrior.ADVANCED_COMBO_1:
                break;
            case DawnWarrior.COMA_1:
                break;
            case DawnWarrior.COMBO_ATTACK_2:
                break;
            case DawnWarrior.INNER_VOICE_1:
                break;
            case DawnWarrior.INTREPID_SLASH_1:
                break;
            case DawnWarrior.LIGHT_MERGER:
                break;
            case DawnWarrior.MAGIC_CRASH_7:
                break;
            case DawnWarrior.MOON_CROSS:
                break;
            case DawnWarrior.MOON_SHADOW:
                break;
            case DawnWarrior.PANIC_1:
                break;
            case DawnWarrior.RADIANT_CHARGE_2:
                break;
            case DawnWarrior.RISING_SUN:
                pEffect.statups.put(CharacterTemporaryStat.PoseType, 2);
                //pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                //pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.indieBooster));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case DawnWarrior.SELF_RECOVERY_3:
                break;
            case DawnWarrior.SOUL_DRIVER_1:
                break;
            case DawnWarrior.SOUL_OF_THE_GUARDIAN:
                pEffect.statups.put(CharacterTemporaryStat.IncMaxHP, pEffect.info.get(StatInfo.indieMhp));
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, pEffect.info.get(StatInfo.indiePdd));
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePdd));
                break;
            case DawnWarrior.SOUL_OF_THE_GUARDIAN_1:
                break;
            case DawnWarrior.SUN_CROSS:
                break;
            case DawnWarrior.TRUE_SIGHT:
                break;
            case DawnWarrior.WILL_OF_STEEL:
                break;
            case DawnWarrior.CALL_OF_CYGNUS_3:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case DawnWarrior.CAREENING_DANCE_BOSS_RUSH:
                break;
            case DawnWarrior.CAREENING_DANCE_GUARDBREAK:
                break;
            case DawnWarrior.CAREENING_DANCE_REINFORCE:
                break;
            case DawnWarrior.CRESCENT_DIVIDE:
                break;
            case DawnWarrior.DIVIDE_AND_PIERCE_EXTRA_STRIKE:
                break;
            case DawnWarrior.DIVIDE_AND_PIERCE_REINFORCE:
                break;
            case DawnWarrior.DIVIDE_AND_PIERCE_SPREAD:
                break;
            case DawnWarrior.EQUINOX_CYCLE:
                pEffect.statups.put(CharacterTemporaryStat.GlimmeringTime, 1);
                break;
            case DawnWarrior.EQUINOX_CYCLE_1:
                pEffect.statups.put(CharacterTemporaryStat.GlimmeringTime, 1);
                break;
            case DawnWarrior.EQUINOX_CYCLE_2:
                pEffect.statups.put(CharacterTemporaryStat.GlimmeringTime, 1);
                break;
            case DawnWarrior.EQUINOX_SLASH:
                break;
            case DawnWarrior.GLORY_OF_THE_GUARDIANS_2:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case DawnWarrior.HYPER_ACCURACY_50_5:
                break;
            case DawnWarrior.HYPER_CRITICAL_50_5:
                break;
            case DawnWarrior.HYPER_DEFENSE_20_2:
                break;
            case DawnWarrior.HYPER_DEXTERITY_50_5:
                break;
            case DawnWarrior.HYPER_FURY_50_5:
                break;
            case DawnWarrior.HYPER_HEALTH_50_5:
                break;
            case DawnWarrior.HYPER_INTELLIGENCE_50_5:
                break;
            case DawnWarrior.HYPER_JUMP_50_5:
                break;
            case DawnWarrior.HYPER_LUCK_50_5:
                break;
            case DawnWarrior.HYPER_MAGIC_DEFENSE_50_5:
                break;
            case DawnWarrior.HYPER_MANA_50_5:
                break;
            case DawnWarrior.HYPER_SPEED_50_5:
                break;
            case DawnWarrior.HYPER_STRENGTH_50_5:
                break;
            case DawnWarrior.IMPALING_RAYS:
                break;
            case DawnWarrior.IMPALING_RAYS_1:
                break;
            case DawnWarrior.MASTER_OF_THE_SWORD:
                break;
            case DawnWarrior.MASTER_OF_THE_SWORD_1:
                break;
            case DawnWarrior.MOON_DANCER:
                break;
            case DawnWarrior.MOON_DANCER_1:
                break;
            case DawnWarrior.SOLAR_PIERCE:
                break;
            case DawnWarrior.SOUL_FORGE:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case DawnWarrior.SOUL_PLEDGE:
                pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(StatInfo.indieCr));
                pEffect.statups.put(CharacterTemporaryStat.IndieAllStat, pEffect.info.get(StatInfo.indieAllStat));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case DawnWarrior.SOUL_PLEDGE_1:
                break;
            case DawnWarrior.SPEEDING_SUNSET:
                break;
            case DawnWarrior.SPEEDING_SUNSET_1:
                break;
            case DawnWarrior.STUDENT_OF_THE_BLADE:
                break;
            case DawnWarrior.STYX_CROSSING:
                break;
            case DawnWarrior.STYX_CROSSING_1:
                break;
            case DawnWarrior.TRUE_SIGHT_ENHANCE:
                break;
            case DawnWarrior.TRUE_SIGHT_GUARDBREAK:
                break;
            case DawnWarrior.TRUE_SIGHT_PERSIST:
                break;
            case DawnWarrior.UNPREDICTABLE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1100 || nClass == 1110 || nClass == 1111 || nClass == 1112;
    }

}
