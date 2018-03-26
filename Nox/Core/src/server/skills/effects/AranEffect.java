package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Aran;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class AranEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Aran.BODY_PRESSURE:
                pEffect.statups.put(CharacterTemporaryStat.BodyPressure, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Aran.COMBAT_STEP:
                break;
            case Aran.COMBO_ABILITY:
                pEffect.statups.put(CharacterTemporaryStat.ComboAbilityBuff, 100);
                break;
            case Aran.DOUBLE_SWING:
                break;
            case Aran.GUARDIAN_ARMOR:
                break;
            case Aran.POLEARM_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case Aran.SMASH_SWING:
                break;
            case Aran.SMASH_SWING_1:
                break;
            case Aran.SMASH_SWING_2:
                break;
            case Aran.SMASH_WAVE:
                break;
            case Aran.SMASH_WAVE_1:
                break;
            case Aran.BODY_PRESSURE_1:
                break;
            case Aran.COMBO_DRAIN:
                break;
            case Aran.COMBO_FENRIR:
                break;
            case Aran.COMBO_SMASH:
                break;
            case Aran.COMMAND_MASTERY_I:
                break;
            case Aran.DRAIN:
                pEffect.statups.put(CharacterTemporaryStat.AranDrain, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Aran.FINAL_ATTACK_1:
                break;
            case Aran.FINAL_CHARGE:
                break;
            case Aran.FINAL_CHARGE_1:
                break;
            case Aran.FINAL_CHARGE_2:
                break;
            case Aran.FINAL_TOSS:
                break;
            case Aran.FINAL_TOSS_4:
                break;
            case Aran.PHYSICAL_TRAINING_1:
                break;
            case Aran.POLEARM_MASTERY:
                break;
            case Aran.ROLLING_SPIN:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Aran.ROLLING_SPIN_3:
                break;
            case Aran.SELF_RECOVERY:
                break;
            case Aran.SNOW_CHARGE_1:
                break;
            case Aran.SWING_STUDIES_I:
                break;
            case Aran.TRIPLE_SWING:
                break;
            case Aran.ADRENALINE_RUSH:
                break;
            case Aran.ADVANCED_COMBO_ABILITY:
                break;
            case Aran.AERO_SWING:
                break;
            case Aran.AERO_SWING_1:
                break;
            case Aran.AERO_SWING_2:
                break;
            case Aran.CLEAVING_BLOWS:
                break;
            case Aran.COMBO_FENRIR_1:
                break;
            case Aran.COMBO_RECHARGE:
                break;
            case Aran.FINAL_BLOW:
                break;
            case Aran.FINAL_BLOW_1:
                break;
            case Aran.FINAL_BLOW_2:
                break;
            case Aran.FINAL_BLOW_3:
                break;
            case Aran.FINAL_TOSS_1:
                break;
            case Aran.FINAL_TOSS_2:
                break;
            case Aran.FULL_SWING:
                break;
            case Aran.FULL_SWING_1:
                break;
            case Aran.FULL_SWING_2:
                break;
            case Aran.FULL_SWING_3:
                break;
            case Aran.GATHERING_HOOK:
                break;
            case Aran.GATHERING_HOOK_1:
                break;
            case Aran.JUDGMENT_DRAW:
                break;
            case Aran.JUDGMENT_DRAW_1:
                break;
            case Aran.JUDGMENT_DRAW_2:
                break;
            case Aran.JUDGMENT_DRAW_3:
                break;
            case Aran.MAHA_BLESSING:
                pEffect.statups.put(CharacterTemporaryStat.MAD, pEffect.info.get(MapleStatInfo.indieMad));
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(MapleStatInfo.indiePad));
                break;
            case Aran.MIGHT:
                pEffect.statups.put(CharacterTemporaryStat.KnockBack, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, pEffect.info.get(MapleStatInfo.epad));
                pEffect.statups.put(CharacterTemporaryStat.EPDD, pEffect.info.get(MapleStatInfo.epdd));
                break;
            case Aran.MIGHT_1:
                break;
            case Aran.ROLLING_SPIN_1:
                break;
            case Aran.ROLLING_SPIN_2:
                break;
            case Aran.SNOW_CHARGE:
                pEffect.statups.put(CharacterTemporaryStat.WeaponCharge, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.z));
                break;
            case Aran.ADRENALINE_BURST:
                break;
            case Aran.ADVANCED_FINAL_ATTACK_5:
                break;
            case Aran.BEYONDER_BLADE:
                break;
            case Aran.BEYOND_BLADE:
                break;
            case Aran.BEYOND_BLADE_1:
                break;
            case Aran.BEYOND_BLADE_2:
                break;
            case Aran.BEYOND_BLADE_3:
                break;
            case Aran.BEYOND_BLADE_4:
                break;
            case Aran.BEYOND_BLADE_5:
                break;
            case Aran.BEYOND_BLADE_6:
                break;
            case Aran.BEYOND_BLADE_BARRAGE:
                break;
            case Aran.BOSS_REVERSE_COMBO:
                break;
            case Aran.COMBO_BARRIER:
                break;
            case Aran.COMBO_BARRIER_1:
                break;
            case Aran.COMBO_RECHARGE_COMBO_UP:
                break;
            case Aran.COMBO_RECHARGE_COOLDOWN_CUTTER:
                break;
            case Aran.COMBO_RECHARGE_RELEASE:
                break;
            case Aran.COMBO_TEMPEST:
                break;
            case Aran.COMBO_TEMPEST_BONUS:
                break;
            case Aran.COMBO_TEMPEST_RELEASE:
                break;
            case Aran.COMBO_TEMPEST_TEMPER_LINK:
                break;
            case Aran.COMMAND_MASTERY_II:
                break;
            case Aran.FINAL_BLOW_4:
                break;
            case Aran.FINAL_BLOW_5:
                break;
            case Aran.FINAL_BLOW_6:
                break;
            case Aran.FINAL_BLOW_BONUS:
                break;
            case Aran.FINAL_BLOW_GUARDBREAK:
                break;
            case Aran.FINAL_BLOW_REINFORCE:
                break;
            case Aran.FINISHER_HUNTERS_PREY:
                break;
            case Aran.FINISHER_HUNTERS_PREY_1:
                break;
            case Aran.FINISHER_STORM_OF_FEAR:
                break;
            case Aran.FINISHER_STORM_OF_FEAR_1:
                break;
            case Aran.FINISHER_STORM_OF_FEAR_2:
                break;
            case Aran.FREEZE_STANDING:
                break;
            case Aran.FRENZIED_SWING:
                break;
            case Aran.HEAVY_SWING:
                break;
            case Aran.HEROIC_MEMORIES_3:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case Aran.HEROS_WILL_70_7:
                break;
            case Aran.HIGH_DEFENSE:
                break;
            case Aran.HIGH_MASTERY:
                break;
            case Aran.HYPER_ACCURACY_2000_200_20_2:
                break;
            case Aran.HYPER_CRITICAL_2000_200_20_2:
                break;
            case Aran.HYPER_DEXTERITY_1000_100_10_1:
                break;
            case Aran.HYPER_FURY_2000_200_20_2:
                break;
            case Aran.HYPER_HEALTH_2000_200_20_2:
                break;
            case Aran.HYPER_INTELLIGENCE_1000_100_10_1:
                break;
            case Aran.HYPER_JUMP_2000_200_20_2:
                break;
            case Aran.HYPER_LUCK_2000_200_20_2:
                break;
            case Aran.HYPER_MAGIC_DEFENSE_2000_200_20_2:
                break;
            case Aran.HYPER_MANA_2000_200_20_2:
                break;
            case Aran.HYPER_SPEED_2000_200_20_2:
                break;
            case Aran.HYPER_STRENGTH_1000_100_10_1:
                break;
            case Aran.HYPER_WEAPON_DEFENSE_5:
                break;
            case Aran.MAHAS_DOMAIN:
                break;
            case Aran.MAHAS_DOMAIN_1:
                break;
            case Aran.MAPLE_WARRIOR_70_7:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(MapleStatInfo.x));
                break;
            case Aran.MERCILESS_HUNT:
                break;
            case Aran.OVERSWING:
                break;
            case Aran.OVERSWING_1:
                break;
            case Aran.OVERSWING_2:
                break;
            case Aran.OVER_SWING:
                break;
            case Aran.PIERCING_BEYOND_BLADE:
                break;
            case Aran.REBOUNDING_SWING:
                break;
            case Aran.STORMING_TERROR:
                break;
            case Aran.SUDDEN_STRIKE_2:
                break;
            case Aran.SURGING_ADRENALINE:
                break;
            case Aran.SWING_STUDIES_II:
                break;
            case Aran.UNLIMITED_COMBO:
                break;
            case Aran.SIXTH_PARTY_TONIGHT_6:
                break;
            case Aran.SIXTH_PARTY_TONIGHT_7:
                break;
            case Aran.AGILE_BODY:
                break;
            case Aran.ANIS_JUDGMENT_4:
                break;
            case Aran.ARCHANGELIC_BLESSING_1000_100_10:
                break;
            case Aran.ARCHANGELIC_BLESSING_1000_100_10_1:
                break;
            case Aran.ARCHANGEL_400_40_4:
                break;
            case Aran.ARCHANGEL_500_50_5:
                break;
            case Aran.BALROGS_HELLFIRE_4:
                break;
            case Aran.BALROG_30_3:
                break;
            case Aran.BALROG_70_7:
                break;
            case Aran.BAMBOO_THRUST_1:
                break;
            case Aran.BLESSING_OF_THE_FAIRY_60_6:
                break;
            case Aran.BLUE_SCOOTER_10:
                break;
            case Aran.BUFFALO_4:
                break;
            case Aran.CHICKEN_5:
                break;
            case Aran.CLOUD_4:
                break;
            case Aran.COMBAT_STEP_1:
                break;
            case Aran.COMBO_KILL_BLESSING_1:
                break;
            case Aran.CROKING_3:
                break;
            case Aran.DARK_ANGELIC_BLESSING_50_5:
                break;
            case Aran.DARK_ANGEL_20_2:
                break;
            case Aran.DECENT_ADVANCED_BLESSING_10_1:
                break;
            case Aran.DECENT_COMBAT_ORDERS_10_1:
                break;
            case Aran.DECENT_HASTE_10:
                break;
            case Aran.DECENT_HYPER_BODY_10_1:
                break;
            case Aran.DECENT_MYSTIC_DOOR_10_1:
                break;
            case Aran.DECENT_SHARP_EYES_10_1:
                break;
            case Aran.DECENT_SPEED_INFUSION_10_1:
                break;
            case Aran.DRAGON_4:
                break;
            case Aran.DRAGON_LEVEL_7:
                break;
            case Aran.DRAGON_RIDERS_ENERGY_BREATH_4:
                break;
            case Aran.ECHO_OF_HERO_3:
                break;
            case Aran.EMPRESSS_BLESSING_80_8:
                break;
            case Aran.F1_MACHINE_3:
                break;
            case Aran.FOLLOW_THE_LEAD_90_9:
                break;
            case Aran.FORTUNE_8:
                break;
            case Aran.FREEZING_AXE_60_6:
                break;
            case Aran.FROG_5:
                break;
            case Aran.GARGOYLE_4:
                break;
            case Aran.GIANT_POTION_6000_600_60_6:
                break;
            case Aran.GIANT_POTION_7000_700_70_7:
                break;
            case Aran.GIANT_POTION_8000_800_80_8:
                break;
            case Aran.GIANT_RABBIT_6:
                break;
            case Aran.GODDESS_GUARD_400_40_4:
                break;
            case Aran.GODDESS_GUARD_900_90_9:
                break;
            case Aran.HELICOPTER_6:
                break;
            case Aran.HELICOPTER_8:
                break;
            case Aran.HELPER_1:
                break;
            case Aran.HIDDEN_POTENTIAL_HERO_10_1:
                break;
            case Aran.HIGHWAY_PATROL_CAR_4:
                break;
            case Aran.HORNTAILS_FLAME_BREATH_4:
                break;
            case Aran.HOTAIR_BALLOON_4:
                break;
            case Aran.ICE_CHOP_60_6:
                break;
            case Aran.ICE_CURSE_60_6:
                break;
            case Aran.ICE_DOUBLE_JUMP_20_2:
                break;
            case Aran.ICE_KNIGHT_10:
                break;
            case Aran.ICE_SMASH_60_6:
                break;
            case Aran.ICE_TEMPEST_60_6:
                break;
            case Aran.INVINCIBLE_BARRIER_1:
                break;
            case Aran.INVISIBLE_BALROG_4:
                break;
            case Aran.JR_TANK_4:
                break;
            case Aran.JUMP_DOWN_1:
                break;
            case Aran.KNIGHTS_CHARIOT_4:
                break;
            case Aran.KURENAI_RUN_AWAY_3:
                break;
            case Aran.LAW_OFFICER_4:
                break;
            case Aran.LEGENDARY_SPIRIT_40_4:
                break;
            case Aran.LEONARDO_THE_LION_10_1:
                break;
            case Aran.LINK_MANAGER_20_2:
                break;
            case Aran.LION_4:
                break;
            case Aran.LOVELY_SCOOTER_3:
                break;
            case Aran.LOW_RIDER_10_1:
                break;
            case Aran.LOW_RIDER_9:
                break;
            case Aran.MAGICAL_WOODEN_HORSE_3:
                break;
            case Aran.MAGIC_BROOM_4:
                break;
            case Aran.MAKER_40_4:
                break;
            case Aran.METEO_SHOWER_1:
                break;
            case Aran.MIST_BALROG_10_1:
                break;
            case Aran.MONSTER_RIDER_3:
                break;
            case Aran.MOTHERSHIP_4:
                break;
            case Aran.MU_GONGS_ABSOLUTE_DESTRUCTION_4:
                break;
            case Aran.NADESHIKO_FLY_HIGH_4:
                break;
            case Aran.NAPOLEAN_MOUNT_4:
                break;
            case Aran.NIGHTMARE_30_3:
                break;
            case Aran.NIGHTMARE_70_7:
                break;
            case Aran.NIMBUS_CLOUD_30_3:
                break;
            case Aran.NINAS_PENTACLE_4:
                break;
            case Aran.ORANGE_MUSHROOM_10_1:
                break;
            case Aran.OS3A_MACHINE_4:
                break;
            case Aran.OS4_SHUTTLE_5:
                break;
            case Aran.OSTRICH_10:
                break;
            case Aran.OWL_5:
                break;
            case Aran.OWL__4:
                break;
            case Aran.PACHINKO_ROBO_3:
                break;
            case Aran.PEGASUS_4:
                break;
            case Aran.PIGS_WEAKNESS:
                break;
            case Aran.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN_4:
                break;
            case Aran.PINK_BEAR_HOTAIR_BALLOON_10_1:
                break;
            case Aran.POWER_SUIT_10:
                break;
            case Aran.RABBIT_RICKSHAW_5:
                break;
            case Aran.RAGE_OF_PHARAOH_40_4:
                break;
            case Aran.RECOVERY_3:
                pEffect.statups.put(CharacterTemporaryStat.Regen, pEffect.info.get(MapleStatInfo.x));
                break;
            case Aran.RED_DRACO_5:
                break;
            case Aran.RED_TRUCK_4:
                break;
            case Aran.REGAINED_MEMORY:
                break;
            case Aran.RETRO_SCOOTER_3:
                break;
            case Aran.RETURN_TO_RIEN:
                break;
            case Aran.REXS_CHARGE_4:
                break;
            case Aran.REXS_HYENA_5:
                break;
            case Aran.SANTA_SLED_10:
                break;
            case Aran.SHINJO_30_3:
                break;
            case Aran.SHINJO_70_7:
                break;
            case Aran.SLIMES_WEAKNESS:
                break;
            case Aran.SMALL_RABBIT_4:
                break;
            case Aran.SOARING_50_5:
                break;
            case Aran.SOARING_MOUNT_3:
                break;
            case Aran.SPACESHIP_70_7:
                break;
            case Aran.SPACE_BEAM_60_6:
                break;
            case Aran.SPACE_DASH_60_6:
                break;
            case Aran.SPIRIT_OF_ROCKS_DOOM_STRIKE_4:
                break;
            case Aran.SPIRIT_VIKING_4:
                break;
            case Aran.STUMPS_WEAKNESS:
                break;
            case Aran.THREE_SNAILS_3:
                break;
            case Aran.TIGER_3:
                break;
            case Aran.TRANSFORMER_3:
                break;
            case Aran.TURTLE_5:
                break;
            case Aran.TUTORIAL_SKILL_3:
                break;
            case Aran.TUTORIAL_SKILL_4:
                break;
            case Aran.TUTORIAL_SKILL_5:
                break;
            case Aran.TUTORIAL_SKILL_6:
                break;
            case Aran.TUTORIAL_SKILL_7:
                break;
            case Aran.UNICORN_4:
                break;
            case Aran.VISITOR_MELEE_ATTACK_6:
                break;
            case Aran.VISITOR_RANGE_ATTACK_6:
                break;
            case Aran.VON_LEONS_LION_SLASH_4:
                break;
            case Aran.WHITE_ANGELIC_BLESSING_100_10:
                break;
            case Aran.WHITE_ANGEL_10_1:
                break;
            case Aran.WILL_OF_THE_ALLIANCE_50_5:
                break;
            case Aran.WITCHS_BROOMSTICK_400_40_4:
                break;
            case Aran.WITCHS_BROOMSTICK_600_60_6:
                break;
            case Aran.YETI_10_1:
                break;
            case Aran.YETI_MOUNT_90_9:
                break;
            case Aran.YETI_RIDER_2:
                break;
            case Aran.ZAKUMS_TOWERING_INFERNO_4:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2100 || nClass == 2110 || nClass == 2111 || nClass == 2112 || nClass == 2000;
    }

}
