package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Evan;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class EvanEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Evan.FIRE_CIRCLE:
                break;
            case Evan.TELEPORT:
                break;
            case Evan.ADVANCED_DRAGON_SPARK:
                break;
            case Evan.DRAGON_FLASH_1:
                break;
            case Evan.DRAGON_FLASH_2:
                break;
            case Evan.DRAGON_FLASH_3:
                break;
            case Evan.HIGH_WISDOM_5:
                break;
            case Evan.LIGHTNING_BOLT:
                break;
            case Evan.MAGIC_BOOSTER_6:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Evan.MAGIC_GUARD_1:
            case Evan.MAGIC_GUARD_3:
                pEffect.statups.put(CharacterTemporaryStat.MagicGuard, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Evan.MANA_BURST_II:
                break;
            case Evan.PARTNERS:
                break;
            case Evan.RETURN:
                break;
            case Evan.RETURN_FLASH:
                break;
            case Evan.SPELL_MASTERY_5:
                break;
            case Evan.SUPPORT_JUMP:
                break;
            case Evan.WIND_CIRCLE_1:
                break;
            case Evan.WIND_FLASH_1:
                break;
            case Evan.WIND_FLASH_2:
                break;
            case Evan.WIND_FLASH_3:
                break;
            case Evan.HIGH_WISDOM_4:
                break;
            case Evan.ICE_BREATH:
                break;
            case Evan.SPELL_MASTERY_4:
                break;
            case Evan.ELEMENTAL_DECREASE_1:
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(StatInfo.x));
                break;
            case Evan.MAGIC_FLARE_:
                break;
            case Evan.MAGIC_SHIELD:
                break;
            case Evan.CRITICAL_MAGIC:
                break;
            case Evan.CRITICAL_MAGIC_:
                break;
            case Evan.DRAGON_BLINK:
                break;
            case Evan.DRAGON_DIVE:
                break;
            case Evan.DRAGON_DIVE_1:
                break;
            case Evan.DRAGON_POTENTIAL:
                break;
            case Evan.DRAGON_THRUST_:
                break;
            case Evan.ELEMENTAL_DECREASE_3:
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(StatInfo.x));
                break;
            case Evan.MAGIC_AMPLIFICATION_1:
                break;
            case Evan.MAGIC_BOOSTER_4:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Evan.MAGIC_DEBRIS:
                break;
            case Evan.MAGIC_RESISTANCE_1:
                break;
            case Evan.MANA_BURST_III:
                break;
            case Evan.RETURN_DIVE:
                break;
            case Evan.SLOW_3:
                break;
            case Evan.THUNDER_CIRCLE:
                break;
            case Evan.THUNDER_DIVE:
                break;
            case Evan.THUNDER_DIVE_1:
                break;
            case Evan.THUNDER_FLASH:
                break;
            case Evan.THUNDER_FLASH_1:
                break;
            case Evan.DRAGON_SPARK:
                break;
            case Evan.FIRE_BREATH_:
                break;
            case Evan.KILLER_WINGS_:
                break;
            case Evan.MAGIC_AMPLIFICATION:
                break;
            case Evan.MAGIC_RESISTANCE:
                pEffect.statups.put(CharacterTemporaryStat.MagicResistance, pEffect.info.get(StatInfo.x));
                break;
            case Evan.DRAGON_FURY_2:
                break;
            case Evan.EARTHQUAKE:
                break;
            case Evan.ONYX_SHROUD:
                break;
            case Evan.PHANTOM_IMPRINT_:
                break;
            case Evan.RECOVERY_AURA:
                break;
            case Evan.TELEPORT_MASTERY_5:
                break;
            case Evan.BLAZE_GUARDBREAK:
                break;
            case Evan.BLAZE_PERSIST:
                break;
            case Evan.BLAZE_REINFORCE:
                break;
            case Evan.BLESSING_OF_THE_ONYX_1:
                pEffect.statups.put(CharacterTemporaryStat.EMAD, pEffect.info.get(StatInfo.emad));
                pEffect.statups.put(CharacterTemporaryStat.EPDD, pEffect.info.get(StatInfo.epdd));
                break;
            case Evan.DARK_FOG_1:
                break;
            case Evan.DARK_FOG_COOLDOWN_CUTTER:
                break;
            case Evan.DARK_FOG_DAMAGE_SPLIT:
                break;
            case Evan.DARK_FOG_REINFORCE:
                break;
            case Evan.DRAGON_BREATH_1:
                break;
            case Evan.DRAGON_FURY_1:
                break;
            case Evan.DRAGON_MASTER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicleExpire, 1932193);
                pEffect.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                //pEffect.statups.put(CharacterTemporaryStat.NewFlying, 1);
                break;
            case Evan.DRAGON_MASTER_1:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932193);
                pEffect.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                //pEffect.statups.put(CharacterTemporaryStat.NewFlying, 1);
                break;
            case Evan.EARTHSHATTERING_DIVE:
                break;
            case Evan.EARTH_BREATH:
                break;
            case Evan.EARTH_BREATH_1:
                break;
            case Evan.EARTH_CIRCLE_1:
                break;
            case Evan.EARTH_DIVE:
                break;
            case Evan.ENHANCED_MAGIC_DEBRIS:
                break;
            case Evan.FLAME_WHEEL:
                break;
            case Evan.FRENZIED_SOUL:
                break;
            case Evan.HEROIC_MEMORIES_4:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Evan.HEROIC_MEMORIES_5:
                break;
            case Evan.HEROS_WILL_80_8:
                break;
            case Evan.HEROS_WILL_90_9:
                break;
            case Evan.HIGH_DRAGON_POTENTIAL:
                break;
            case Evan.HOWLING_WIND:
                break;
            case Evan.HYPER_ACCURACY_30_3:
                break;
            case Evan.HYPER_CRITICAL_40_4:
                break;
            case Evan.HYPER_DEXTERITY_30_3:
                break;
            case Evan.HYPER_FURY_30_3:
                break;
            case Evan.HYPER_HEALTH_30_3:
                break;
            case Evan.HYPER_INTELLIGENCE_30_3:
                break;
            case Evan.HYPER_JUMP_30_3:
                break;
            case Evan.HYPER_LUCK_40_4:
                break;
            case Evan.HYPER_MAGIC_DEFENSE_30_3:
                break;
            case Evan.HYPER_MANA_30_3:
                break;
            case Evan.HYPER_SPEED_30_3:
                break;
            case Evan.HYPER_STRENGTH_40_4:
                break;
            case Evan.HYPER_WEAPON_DEFENSE_2:
                break;
            case Evan.ILLUSION:
                break;
            case Evan.ILLUSION_GUARDBREAK:
                break;
            case Evan.ILLUSION_RANGE_UP:
                break;
            case Evan.ILLUSION_REINFORCE:
                break;
            case Evan.LUNGS_OF_STONE:
                break;
            case Evan.MAGIC_MASTERY:
                break;
            case Evan.MAGIC_MASTERY_1:
                break;
            case Evan.MANA_BURST_IV:
                break;
            case Evan.MANA_BURST_IV_1:
                break;
            case Evan.MAPLE_WARRIOR_100_10:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Evan.MAPLE_WARRIOR_90_9:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Evan.ONYX_WILL_1:
                break;
            case Evan.RETURN_FLAME:
                break;
            case Evan.RETURN_FLAME_1:
                break;
            case Evan.ROLLING_THUNDER:
                break;
            case Evan.SPEEDY_DRAGON_BREATH:
                break;
            case Evan.SPEEDY_DRAGON_DIVE:
                break;
            case Evan.SPEEDY_DRAGON_FLASH:
                break;
            case Evan.SUMMON_ONYX_DRAGON:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Evan.SUMMON_ONYX_DRAGON_1:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Evan.THUNDER_OVERLOAD:
                break;
            case Evan.WIND_BREATH:
                break;
            case Evan.WIND_BREATH_OPPORTUNITY:
                break;
            case Evan.BLAZE:
                break;
            case Evan.BLESSING_OF_THE_ONYX:
                pEffect.statups.put(CharacterTemporaryStat.EMAD, pEffect.info.get(StatInfo.emad));
                pEffect.statups.put(CharacterTemporaryStat.EPDD, pEffect.info.get(StatInfo.epdd));
                break;
            case Evan.DARK_FOG:
                break;
            case Evan.ONYX_WILL:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.damage));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case Evan.SOUL_STONE:
                break;
            case Evan.SIXTH_PARTY_TONIGHT:
                break;
            case Evan.SIXTH_PARTY_TONIGHT_1:
                break;
            case Evan.ANIS_JUDGMENT_2:
                break;
            case Evan.ARCHANGELIC_BLESSING_2:
                break;
            case Evan.ARCHANGELIC_BLESSING_3:
                break;
            case Evan.ARCHANGEL_1000_100_10:
                break;
            case Evan.ARCHANGEL_1000_100_10_1:
                break;
            case Evan.BACK_TO_NATURE:
                break;
            case Evan.BALROG:
                break;
            case Evan.BALROGS_HELLFIRE_2:
                break;
            case Evan.BALROG_100_10:
                break;
            case Evan.BAMBOO_THRUST_2:
                break;
            case Evan.BLESSING_OF_THE_FAIRY:
                break;
            case Evan.BLUE_SCOOTER_30_3:
                break;
            case Evan.BUFFALO:
                break;
            case Evan.CHICKEN_6:
                break;
            case Evan.CLOUD:
                break;
            case Evan.CROKING_4:
                break;
            case Evan.DARK_ANGELIC_BLESSING_1:
                break;
            case Evan.DARK_ANGEL_50_5:
                break;
            case Evan.DECENT_ADVANCED_BLESSING_50_5:
                break;
            case Evan.DECENT_COMBAT_ORDERS_50_5:
                break;
            case Evan.DECENT_HASTE_40_4:
                break;
            case Evan.DECENT_HYPER_BODY_50_5:
                break;
            case Evan.DECENT_MYSTIC_DOOR_50_5:
                break;
            case Evan.DECENT_SHARP_EYES_50_5:
                break;
            case Evan.DECENT_SPEED_INFUSION_50_5:
                break;
            case Evan.DRAGON:
                break;
            case Evan.DRAGON_FLIGHT:
                break;
            case Evan.DRAGON_LEVEL_3:
                break;
            case Evan.DRAGON_RIDERS_ENERGY_BREATH_2:
                break;
            case Evan.EMPRESSS_BLESSING_1:
                break;
            case Evan.F1_MACHINE_4:
                break;
            case Evan.FOLLOW_THE_LEAD_60_6:
                break;
            case Evan.FORTUNE:
                break;
            case Evan.FREEZING_AXE_1:
                break;
            case Evan.FROG_1:
                break;
            case Evan.GARGOYLE_5:
                break;
            case Evan.GIANT_POTION_50000_5000_500_50_5:
                break;
            case Evan.GIANT_POTION_60000_6000_600_60_6:
                break;
            case Evan.GIANT_POTION_70000_7000_700_70_7:
                break;
            case Evan.GIANT_RABBIT_7:
                break;
            case Evan.GODDESS_GUARD_30_3:
                break;
            case Evan.GODDESS_GUARD_90_9:
                break;
            case Evan.HELICOPTER:
                break;
            case Evan.HELICOPTER_10:
                break;
            case Evan.HEROS_ECHO_10:
                break;
            case Evan.HIDDEN_POTENTIAL_HERO_1:
                break;
            case Evan.HIGHWAY_PATROL_CAR:
                break;
            case Evan.HORNTAILS_FLAME_BREATH_2:
                break;
            case Evan.HOTAIR_BALLOON:
                break;
            case Evan.ICE_CHOP_2:
                break;
            case Evan.ICE_CURSE_2:
                break;
            case Evan.ICE_DOUBLE_JUMP_50_5:
                break;
            case Evan.ICE_KNIGHT_1:
                break;
            case Evan.ICE_SMASH_1:
                break;
            case Evan.ICE_TEMPEST_3:
                break;
            case Evan.INHERITED_WILL:
                break;
            case Evan.INVINCIBLE_BARRIER_2:
                break;
            case Evan.INVISIBLE_BALROG:
                break;
            case Evan.JR_TANK:
                break;
            case Evan.JUMP_DOWN_2:
                break;
            case Evan.KNIGHTS_CHARIOT:
                break;
            case Evan.KURENAI_RUN_AWAY_4:
                break;
            case Evan.LAW_OFFICER:
                break;
            case Evan.LEGENDARY_SPIRIT_:
                break;
            case Evan.LEONARDO_THE_LION_50_5:
                break;
            case Evan.LINK_MANAGER_7:
                break;
            case Evan.LION_5:
                break;
            case Evan.LOVELY_SCOOTER_4:
                break;
            case Evan.LOW_RIDER:
                break;
            case Evan.LOW_RIDER_30_3:
                break;
            case Evan.MAGICAL_WOODEN_HORSE_4:
                break;
            case Evan.MAGIC_BROOM:
                break;
            case Evan.MAKER_70_7:
                break;
            case Evan.METEO_SHOWER_2:
                break;
            case Evan.MIST_BALROG_40_4:
                break;
            case Evan.MONSTER_RIDER_4:
                break;
            case Evan.MOTHERSHIP_5:
                break;
            case Evan.MU_GONGS_ABSOLUTE_DESTRUCTION_2:
                break;
            case Evan.NADESHIKO_FLY_HIGH:
                break;
            case Evan.NAPOLEAN_MOUNT:
                break;
            case Evan.NIGHTMARE:
                break;
            case Evan.NIGHTMARE_100_10_1:
                break;
            case Evan.NIMBLE_FEET_3:
                pEffect.statups.put(CharacterTemporaryStat.Speed, 10 + (pEffect.getLevel() - 1) * 5);
                break;
            case Evan.NIMBUS_CLOUD_60_6:
                break;
            case Evan.NINAS_PENTACLE:
                break;
            case Evan.ORANGE_MUSHROOM_50_5:
                break;
            case Evan.OS3A_MACHINE_5:
                break;
            case Evan.OS4_SHUTTLE_7:
                break;
            case Evan.OSTRICH_30_3:
                break;
            case Evan.OWL_1:
                break;
            case Evan.OWL__5:
                break;
            case Evan.PACHINKO_ROBO:
                break;
            case Evan.PEGASUS:
                break;
            case Evan.PIGS_WEAKNESS_7:
                break;
            case Evan.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN_2:
                break;
            case Evan.PINK_BEAR_HOTAIR_BALLOON_50_5:
                break;
            case Evan.POWER_SUIT_30_3:
                break;
            case Evan.RABBIT_RICKSHAW:
                break;
            case Evan.RAGE_OF_PHARAOH_70_7:
                break;
            case Evan.RECOVER:
                break;
            case Evan.RED_DRACO_1:
                break;
            case Evan.RED_TRUCK_5:
                break;
            case Evan.RETRO_SCOOTER_4:
                break;
            case Evan.REXS_CHARGE_2:
                break;
            case Evan.REXS_HYENA_1:
                break;
            case Evan.RUNE_PERSISTENCE:
                break;
            case Evan.SANTA_SLED_30_3:
                break;
            case Evan.SHINJO_1:
                break;
            case Evan.SHINJO_100_10:
                break;
            case Evan.SLIMES_WEAKNESS_6:
                break;
            case Evan.SMALL_RABBIT:
                break;
            case Evan.SOARING_80_8:
                break;
            case Evan.SOARING_MOUNT:
                break;
            case Evan.SPACESHIP_100_10:
                break;
            case Evan.SPACE_BEAM_90_9:
                break;
            case Evan.SPACE_DASH_90_9:
                break;
            case Evan.SPIRIT_OF_ROCKS_DOOM_STRIKE_2:
                break;
            case Evan.SPIRIT_VIKING:
                break;
            case Evan.STUMPS_WEAKNESS_7:
                break;
            case Evan.THREE_SNAILS_4:
                break;
            case Evan.TIGER_4:
                break;
            case Evan.TRANSFORMER_4:
                break;
            case Evan.TURTLE_1:
                break;
            case Evan.UNICORN_6:
                break;
            case Evan.VISITOR_MELEE_ATTACK_8:
                break;
            case Evan.VISITOR_RANGE_ATTACK_8:
                break;
            case Evan.VON_LEONS_LION_SLASH_2:
                break;
            case Evan.WHITE_ANGELIC_BLESSING_2:
                break;
            case Evan.WHITE_ANGEL_4:
                break;
            case Evan.WILL_OF_THE_ALLIANCE_2:
                break;
            case Evan.WITCHS_BROOMSTICK_1000_100_10_1:
                break;
            case Evan.WITCHS_BROOMSTICK_4000_400_40_4:
                break;
            case Evan.YETI_50_5:
                break;
            case Evan.YETI_RIDER_3:
                break;
            case Evan.ZAKUMS_TOWERING_INFERNO_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2210 || nClass == 2211 || nClass == 2212 || nClass == 2213 || nClass == 2214 || nClass == 2215 || nClass == 2216 || nClass == 2217 || nClass == 2218 || nClass == 2001;
    }

}
