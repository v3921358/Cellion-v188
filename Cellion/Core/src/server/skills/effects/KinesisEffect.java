package server.skills.effects;

import client.CharacterTemporaryStat;
import server.skills.effects.manager.AbstractEffect;
import client.Jobs;
import client.MonsterStatus;
import client.SkillFactory;
import constants.skills.Kinesis;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class KinesisEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Kinesis.ESP_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(StatInfo.indieBooster));
                break;
            case Kinesis.MENTAL_SHIELD:
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicShield, 1);
                pEffect.info.put(StatInfo.time, 210000000);
                break;
            case Kinesis.ALLIANCE_INSPIRATION_1:
                break;
            case Kinesis.ARCHANGELIC_BLESSING_600_60_6:
                break;
            case Kinesis.ARCHANGELIC_BLESSING_700_70_7:
                break;
            case Kinesis.ARCHANGEL_3000_300_30_3:
                break;
            case Kinesis.ARCHANGEL_5000_500_50_5:
                break;
            case Kinesis.BAMBOO_RAIN_1:
                break;
            case Kinesis.BLESSING_OF_THE_FAIRY_40_4:
                break;
            case Kinesis.DARK_ANGELIC_BLESSING_30_3:
                break;
            case Kinesis.DARK_ANGEL_70_7:
                break;
            case Kinesis.DECENT_ADVANCED_BLESSING_8:
                break;
            case Kinesis.DECENT_COMBAT_ORDERS_8:
                break;
            case Kinesis.DECENT_HASTE_7:
                break;
            case Kinesis.DECENT_HYPER_BODY_8:
                break;
            case Kinesis.DECENT_MYSTIC_DOOR_8:
                break;
            case Kinesis.DECENT_SHARP_EYES_8:
                break;
            case Kinesis.DECENT_SPEED_INFUSION_8:
                break;
            case Kinesis.EMPRESSS_BLESSING_50_5:
                break;
            case Kinesis.ESP:
                break;
            case Kinesis.FOLLOW_THE_LEAD:
                break;
            case Kinesis.FREEZING_AXE_40_4:
                break;
            case Kinesis.GIANT_POTION_100000_10000_1000_100_10:
                break;
            case Kinesis.GIANT_POTION_80000_8000_800_80_8:
                break;
            case Kinesis.GIANT_POTION_90000_9000_900_90_9:
                break;
            case Kinesis.HEROS_ECHO:
                break;
            case Kinesis.HIDDEN_POTENTIAL_HERO_10:
                break;
            case Kinesis.ICE_CHOP_40_4:
                break;
            case Kinesis.ICE_CURSE_40_4:
                break;
            case Kinesis.ICE_DOUBLE_JUMP_60_6:
                break;
            case Kinesis.ICE_KNIGHT_50_5:
                break;
            case Kinesis.ICE_SMASH_40_4:
                break;
            case Kinesis.ICE_TEMPEST_40_4:
                break;
            case Kinesis.INVINCIBILITY_2:
                break;
            case Kinesis.JUDGMENT_1:
                break;
            case Kinesis.LEGENDARY_SPIRIT_1:
                break;
            case Kinesis.LINK_MANAGER_90_9:
                break;
            case Kinesis.MAKER_1:
                break;
            case Kinesis.MASTER_OF_ORGANIZATION_7:
                break;
            case Kinesis.MASTER_OF_ORGANIZATION_8:
                break;
            case Kinesis.MASTER_OF_SWIMMING_4:
                break;
            case Kinesis.PIGS_WEAKNESS_4:
                break;
            case Kinesis.PIRATE_BLESSING_5:
                break;
            case Kinesis.POWER_EXPLOSION_1:
                break;
            case Kinesis.PSYCHIC_ATTACK:
                break;
            case Kinesis.RAGE_OF_PHARAOH_1:
                break;
            case Kinesis.RETURN_1:
                break;
            case Kinesis.SOARING_1:
                break;
            case Kinesis.SPACESHIP_1:
                break;
            case Kinesis.SPACE_BEAM_1:
                break;
            case Kinesis.SPACE_DASH_1:
                break;
            case Kinesis.STUMPS_WEAKNESS_4:
                break;
            case Kinesis.VISITOR_MELEE_ATTACK_1:
                break;
            case Kinesis.VISITOR_RANGE_ATTACK_1:
                break;
            case Kinesis.WHITE_ANGELIC_BLESSING_60_6:
                break;
            case Kinesis.WHITE_ANGEL_90_9:
                break;
            case Kinesis.ESP_MASTERY:
                break;
            case Kinesis.KINETIC_PILEDRIVER:
                break;
            case Kinesis.KINETIC_PILEDRIVER_1:
                break;
            case Kinesis.MENTAL_STRENGTH:
                break;
            case Kinesis.PSYCHIC_ARMOR:
                pEffect.statups.put(CharacterTemporaryStat.IndiePDD, pEffect.info.get(StatInfo.indiePdd));
                pEffect.statups.put(CharacterTemporaryStat.IndieStance, pEffect.info.get(StatInfo.stanceProp));
                break;
            case Kinesis.PSYCHIC_BLAST:
                break;
            case Kinesis.PSYCHIC_BLAST_VORTEX:
                break;
            case Kinesis.PSYCHIC_DRAIN:
                break;
            case Kinesis.PSYCHIC_DRAIN_1:
                break;
            case Kinesis.PURE_POWER:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Kinesis.ULTIMATE_DEEP_IMPACT:
                break;
            case Kinesis.KINETIC_COMBO:
                break;
            case Kinesis.KINETIC_JAUNT:
                break;
            case Kinesis.MIND_TREMOR:
                break;
            case Kinesis.MITIGATION:
                break;
            case Kinesis.PSYCHIC_ANNIHILATION:
                break;
            case Kinesis.PSYCHIC_ASSAULT:
                break;
            case Kinesis.PSYCHIC_ASSAULT_VORTEX:
                break;
            case Kinesis.PSYCHIC_BULWARK:
                pEffect.statups.put(CharacterTemporaryStat.IndiePDD, pEffect.info.get(StatInfo.indiePdd));
                pEffect.statups.put(CharacterTemporaryStat.IndieStance, pEffect.info.get(StatInfo.stanceProp));
                pEffect.info.put(StatInfo.time, SkillFactory.getSkill(Kinesis.PSYCHIC_ARMOR).getEffect(1).info.get(StatInfo.time));
                break;
            case Kinesis.PSYCHIC_GRAB:
                break;
            case Kinesis.PSYCHIC_REINFORCEMENT:
                pEffect.statups.put(CharacterTemporaryStat.IndieMADR, pEffect.info.get(StatInfo.indieMadR));
                break;
            case Kinesis.PSYCHIC_SMASH:
                break;
            case Kinesis.THIRD_EYE:
                break;
            case Kinesis.TRANSCENDENCE:
                break;
            case Kinesis.ULTIMATE_TRAINWRECK:
                break;
            case Kinesis.AWAKENING:
                break;
            case Kinesis.CLEAR_MIND:
                break;
            case Kinesis.CRITICAL_RUSH:
                break;
            case Kinesis.MASTERY:
                break;
            case Kinesis.MENTAL_OVERDRIVE:
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicOver, 1);
                break;
            case Kinesis.MENTAL_SHOCK:
                break;
            case Kinesis.MENTAL_TEMPEST:
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicPoint, 30);
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicEnergeShield, 1);
                break;
            case Kinesis.MENTAL_TEMPEST_1:
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicPoint, 30);
                pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicEnergeShield, 1);
                break;
            case Kinesis.MIND_BREAK:
                break;
            case Kinesis.MIND_BREAK_COOLDOWN_CUTTER:
                break;
            case Kinesis.MIND_BREAK_ENHANCE:
                break;
            case Kinesis.MIND_BREAK_REINFORCE:
                break;
            case Kinesis.MIND_QUAKE:
                break;
            case Kinesis.MIND_SCRAMBLER:
                break;
            case Kinesis.MIND_TREMOR_OVERWHELM:
                break;
            case Kinesis.MIND_TREMOR_PERSIST:
                break;
            case Kinesis.MIND_TREMOR_REINFORCE:
                break;
            case Kinesis.PRESIDENTS_ORDERS:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Kinesis.PSYCHIC_ANNIHILATION_1:
                break;
            case Kinesis.PSYCHIC_ANNIHILATION_2:
                break;
            case Kinesis.PSYCHIC_CHARGER:
                break;
            case Kinesis.PSYCHIC_CHARGER_1:
                break;
            case Kinesis.PSYCHIC_CLUTCH:
                break;
            case Kinesis.PSYCHIC_GRAB_BOSS_POINT:
                break;
            case Kinesis.PSYCHIC_GRAB_REINFORCE:
                break;
            case Kinesis.PSYCHIC_GRAB_STEEL_SKIN:
                break;
            case Kinesis.SUPREME_CONCENTRATION:
                break;
            case Kinesis.TELEPATH_TACTICS:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, pEffect.info.get(StatInfo.indieMad));
                break;
            case Kinesis.ULTIMATE_BPM:
                break;
            case Kinesis.ULTIMATE_PSYCHIC_SHOT:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 14000 || nClass == 14200 || nClass == 14210 || nClass == 14211 || nClass == 14212;
    }

}
