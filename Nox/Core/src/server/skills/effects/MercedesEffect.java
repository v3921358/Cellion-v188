package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Mercedes;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MercedesEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Mercedes.GLIDE_BLAST:
                break;
            case Mercedes.NATURES_BALANCE_2:
                break;
            case Mercedes.POTENTIAL_POWER:
                break;
            case Mercedes.SHARP_AIM:
                break;
            case Mercedes.SWIFT_DUAL_SHOT:
                break;
            case Mercedes.DUAL_BOWGUNS_BOOST:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.DUAL_BOWGUNS_MASTERY:
                break;
            case Mercedes.FINAL_ATTACK_DUAL_BOWGUNS:
                break;
            case Mercedes.PARTING_SHOT:
                break;
            case Mercedes.PHYSICAL_TRAINING_6:
                break;
            case Mercedes.PIERCING_STORM:
                break;
            case Mercedes.RISING_RUSH:
                break;
            case Mercedes.RISING_RUSH_1:
                break;
            case Mercedes.SPIRIT_SURGE:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.damage));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.SPIRIT_SURGE_1:
                break;
            case Mercedes.AERIAL_BARRAGE:
                break;
            case Mercedes.ELEMENTAL_KNIGHTS:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.info.put(StatInfo.time, 210000);
                break;
            case Mercedes.ELEMENTAL_KNIGHTS_1:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.info.put(StatInfo.time, 210000);
                break;
            case Mercedes.ELEMENTAL_KNIGHTS_2:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.info.put(StatInfo.time, 210000);
                break;
            case Mercedes.GUST_DIVE:
                break;
            case Mercedes.IGNIS_ROAR:
                pEffect.statups.put(CharacterTemporaryStat.IgnisRore, 1);
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case Mercedes.LEAP_TORNADO:
                break;
            case Mercedes.STUNNING_STRIKES_1:
                break;
            case Mercedes.UNICORN_SPIKE:
                pEffect.monsterStatus.put(MonsterStatus.IMPRINT, pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.WATER_SHIELD:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(StatInfo.terR));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(StatInfo.terR));
                pEffect.statups.put(CharacterTemporaryStat.DamAbsorbShield, pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.ADVANCED_FINAL_ATTACK:
                break;
            case Mercedes.ANCIENT_WARDING:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.EMHP, (int) pEffect.info.get(StatInfo.emhp));
                break;
            case Mercedes.DEFENSE_BREAK:
                break;
            case Mercedes.DUAL_BOWGUNS_EXPERT:
                break;
            case Mercedes.ELVISH_BLESSING:
                //pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                //pEffect.statups.put(CharacterTemporaryStat.KnockBack, pEffect.info.get(StatInfo.x)); // juicy error 38
                break;
            case Mercedes.HEROIC_MEMORIES_1:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Mercedes.HEROS_WILL_500_50_5:
                break;
            case Mercedes.HYPER_ACCURACY_1:
                break;
            case Mercedes.HYPER_CRITICAL_1:
                break;
            case Mercedes.HYPER_DEXTERITY_1:
                break;
            case Mercedes.HYPER_FURY_1:
                break;
            case Mercedes.HYPER_HEALTH_1:
                break;
            case Mercedes.HYPER_INTELLIGENCE_1:
                break;
            case Mercedes.HYPER_JUMP_1:
                break;
            case Mercedes.HYPER_LUCK_1:
                break;
            case Mercedes.HYPER_MAGIC_DEFENSE_1:
                break;
            case Mercedes.HYPER_MANA_1:
                break;
            case Mercedes.HYPER_SPEED_1:
                break;
            case Mercedes.HYPER_STRENGTH_1:
                break;
            case Mercedes.HYPER_WEAPON_DEFENSE:
                break;
            case Mercedes.ISHTARS_RING:
                break;
            case Mercedes.ISHTARS_RING_BOSS_RUSH:
                break;
            case Mercedes.ISHTARS_RING_GUARDBREAK:
                break;
            case Mercedes.ISHTARS_RING_REINFORCE:
                break;
            case Mercedes.LIGHTNING_EDGE:
                break;
            case Mercedes.MAPLE_WARRIOR_500_50_5:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.ROLLING_MOONSAULT:
                break;
            case Mercedes.SPIKES_ROYALE_1:
                pEffect.monsterStatus.put(MonsterStatus.PDD, -pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.SPIKES_ROYALE_ARMORBREAK:
                break;
            case Mercedes.SPIKES_ROYALE_REINFORCE:
                break;
            case Mercedes.SPIKES_ROYALE_TEMPER_LINK:
                break;
            case Mercedes.SPIRIT_NIMBLE_FLIGHT:
                break;
            case Mercedes.SPIRIT_NIMBLE_FLIGHT_1:
                break;
            case Mercedes.STAGGERING_STRIKES:
                break;
            case Mercedes.WATER_SHIELD_REINFORCE:
                break;
            case Mercedes.WATER_SHIELD_TRUE_IMMUNITY_1:
                break;
            case Mercedes.WATER_SHIELD_TRUE_IMMUNITY_2:
                break;
            case Mercedes.WRATH_OF_ENLIL:
                break;
            case Mercedes.ARCHANGELIC_BLESSING_100_10:
                break;
            case Mercedes.ARCHANGELIC_BLESSING_100_10_1:
                break;
            case Mercedes.ARCHANGEL_60_6:
                break;
            case Mercedes.ARCHANGEL_70_7:
                break;
            case Mercedes.BALROG_10:
                break;
            case Mercedes.BAMBOO_RAIN_8:
                break;
            case Mercedes.BLACK_SCOOTER_4:
                break;
            case Mercedes.BLESSING_OF_THE_FAIRY_10_1:
                break;
            case Mercedes.BLUE_SCOOTER_6:
                break;
            case Mercedes.CALL_OF_THE_HUNTER_3:
                break;
            case Mercedes.CAPTURE_3:
                break;
            case Mercedes.CHARGE_TOY_TROJAN_4:
                break;
            case Mercedes.CROCO_4:
                break;
            case Mercedes.CRYSTAL_THROW_3:
                break;
            case Mercedes.DARK_ANGELIC_BLESSING_10:
                break;
            case Mercedes.DARK_ANGEL_8:
                break;
            case Mercedes.DEADLY_CRITS_4:
                break;
            case Mercedes.DECENT_ADVANCED_BLESSING_4:
                break;
            case Mercedes.DECENT_COMBAT_ORDERS_4:
                break;
            case Mercedes.DECENT_HASTE_4:
                break;
            case Mercedes.DECENT_HYPER_BODY_4:
                break;
            case Mercedes.DECENT_MYSTIC_DOOR_4:
                break;
            case Mercedes.DECENT_SHARP_EYES_4:
                break;
            case Mercedes.DECENT_SPEED_INFUSION_4:
                break;
            case Mercedes.ELVEN_BLESSING_1:
                pEffect.moveTo(pEffect.info.get(StatInfo.x));
                break;
            case Mercedes.ELVEN_GRACE:
                break;
            case Mercedes.ELVEN_HEALING:
                break;
            case Mercedes.EMPRESSS_BLESSING_20_2:
                break;
            case Mercedes.FOLLOW_THE_LEAD_9:
                break;
            case Mercedes.FORTUNE_3:
                break;
            case Mercedes.FREEZING_AXE_10_1:
                break;
            case Mercedes.GIANT_POTION_400_40_4:
                break;
            case Mercedes.GIANT_POTION_500_50_5:
                break;
            case Mercedes.GIANT_POTION_600_60_6:
                break;
            case Mercedes.GODDESS_GUARD_300_30_3:
                break;
            case Mercedes.GODDESS_GUARD_60_6:
                break;
            case Mercedes.HEROS_ECHO_4:
                break;
            case Mercedes.HIDDEN_POTENTIAL_HERO_8:
                break;
            case Mercedes.ICE_CHOP_20_2:
                break;
            case Mercedes.ICE_CURSE_20_2:
                break;
            case Mercedes.ICE_DOUBLE_JUMP_8:
                break;
            case Mercedes.ICE_KNIGHT_3:
                break;
            case Mercedes.ICE_SMASH_10_1:
                break;
            case Mercedes.ICE_TEMPEST_20_2:
                break;
            case Mercedes.INFILTRATE_3:
                break;
            case Mercedes.INVINCIBILITY_9:
                break;
            case Mercedes.LEGENDARY_SPIRIT_9:
                break;
            case Mercedes.LEONARDO_THE_LION_6:
                break;
            case Mercedes.LINK_MANAGER_9:
                break;
            case Mercedes.MAKER_9:
                break;
            case Mercedes.MECHANIC_DASH_3:
                break;
            case Mercedes.MIST_BALROG_6:
                break;
            case Mercedes.MONSTER_RIDING_4:
                break;
            case Mercedes.MOTORCYCLE_4:
                break;
            case Mercedes.NIGHTMARE_9:
                break;
            case Mercedes.NIMBUS_CLOUD_6:
                break;
            case Mercedes.ORANGE_MUSHROOM_6:
                break;
            case Mercedes.OSTRICH_5:
                break;
            case Mercedes.PINK_BEAR_HOTAIR_BALLOON_6:
                break;
            case Mercedes.PINK_SCOOTER_4:
                break;
            case Mercedes.POTION_MASTERY_4:
                break;
            case Mercedes.POWER_EXPLOSION_8:
                break;
            case Mercedes.POWER_SUIT_5:
                break;
            case Mercedes.RACE_KART_4:
                break;
            case Mercedes.RAGE_OF_PHARAOH_9:
                break;
            case Mercedes.SANTA_SLED_6:
                break;
            case Mercedes.SHINJO_9:
                break;
            case Mercedes.SOARING_10:
                break;
            case Mercedes.SPACESHIP_10_1:
                break;
            case Mercedes.SPACE_BEAM_10:
                break;
            case Mercedes.SPACE_DASH_10:
                break;
            case Mercedes.STUNNING_STRIKES:
                break;
            case Mercedes.SYLVIDIA:
                break;
            case Mercedes.SYLVIDIA_1:
                break;
            case Mercedes.TEST_3:
                break;
            case Mercedes.TRANSFORMED_ROBOT_4:
                break;
            case Mercedes.UPDRAFT:
                break;
            case Mercedes.WHITE_ANGELIC_BLESSING_30_3:
                break;
            case Mercedes.WHITE_ANGEL_6:
                break;
            case Mercedes.WILL_OF_THE_ALLIANCE_20_2:
                break;
            case Mercedes.WITCHS_BROOMSTICK_40_4:
                break;
            case Mercedes.WITCHS_BROOMSTICK_60_6:
                break;
            case Mercedes.YETI_6:
                break;
            case Mercedes.YETI_MOUNT_10:
                break;
            case Mercedes.YETI_MOUNT_10_1:
                break;
            case Mercedes.ZD_TIGER_4:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2300 || nClass == 2310 || nClass == 2311 || nClass == 2312 || nClass == 2002;
    }

}
