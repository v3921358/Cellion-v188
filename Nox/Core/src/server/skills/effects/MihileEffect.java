package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Mihile;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MihileEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Mihile.ARCHANGELIC_BLESSING_800_80_8:
                break;
            case Mihile.ARCHANGELIC_BLESSING_900_90_9:
                break;
            case Mihile.ARCHANGEL_200_20_2:
                break;
            case Mihile.ARCHANGEL_300_30_3:
                break;
            case Mihile.BALROG_60_6:
                break;
            case Mihile.BAMBOO_RAIN_20_2:
                break;
            case Mihile.BENEDICTION_OF_THE_FAIRY_1:
                break;
            case Mihile.BLACK_SCOOTER_6:
                break;
            case Mihile.BLESSING_OF_THE_FAIRY_70_7:
                break;
            case Mihile.BLUE_SCOOTER_9:
                break;
            case Mihile.CHARGE_TOY_TROJAN_6:
                break;
            case Mihile.CROCO_6:
                break;
            case Mihile.CYGNUS_DEVOTION:
                break;
            case Mihile.DARK_ANGELIC_BLESSING_40_4:
                break;
            case Mihile.DARK_ANGEL_10_1:
                break;
            case Mihile.DECENT_ADVANCED_BLESSING_9:
                break;
            case Mihile.DECENT_COMBAT_ORDERS_9:
                break;
            case Mihile.DECENT_HASTE_8:
                break;
            case Mihile.DECENT_HYPER_BODY_9:
                break;
            case Mihile.DECENT_MYSTIC_DOOR_9:
                break;
            case Mihile.DECENT_SHARP_EYES_9:
                break;
            case Mihile.DECENT_SPEED_INFUSION_9:
                break;
            case Mihile.ELEMENTAL_EXPERT:
                break;
            case Mihile.EMPRESSS_BLESSING_60_6:
                break;
            case Mihile.EMPRESSS_PRAYER_1:
                pEffect.statups.put(CharacterTemporaryStat.MaxLevelBuff, (int) pEffect.info.get(StatInfo.x));
                break;
            case Mihile.EMPRESSS_SHOUT_1:
                break;
            case Mihile.FOLLOW_THE_LEAD_30_3:
                break;
            case Mihile.FORTUNE_10:
                break;
            case Mihile.FREEZING_AXE_50_5:
                break;
            case Mihile.GIANT_POTION_3000_300_30_3:
                break;
            case Mihile.GIANT_POTION_4000_400_40_4:
                break;
            case Mihile.GIANT_POTION_5000_500_50_5:
                break;
            case Mihile.GODDESS_GUARD:
                break;
            case Mihile.GODDESS_GUARD_1000_100_10_1:
                break;
            case Mihile.HEROS_ECHO_10_1:
                break;
            case Mihile.HEROS_ECHO_8:
                break;
            case Mihile.HIDDEN_POTENTIAL_CYGNUS_KNIGHT_1:
                break;
            case Mihile.ICE_CHOP_70_7:
                break;
            case Mihile.ICE_CURSE_70_7:
                break;
            case Mihile.ICE_DOUBLE_JUMP_10_1:
                break;
            case Mihile.ICE_KNIGHT_20_2:
                break;
            case Mihile.ICE_SMASH_50_5:
                break;
            case Mihile.ICE_TEMPEST_70_7:
                break;
            case Mihile.IMPERIAL_RECALL_1:
                break;
            case Mihile.INVINCIBILITY_40_4:
                break;
            case Mihile.KNIGHTS_WATCH_1:
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(StatInfo.prop));
                break;
            case Mihile.LEGENDARY_SPIRIT_30_3:
                break;
            case Mihile.LEONARDO_THE_LION_10:
                break;
            case Mihile.LINK_MANAGER_60_6:
                break;
            case Mihile.MAKER_30_3:
                break;
            case Mihile.MIST_BALROG_10:
                break;
            case Mihile.MONSTER_MOUNT_1:
                break;
            case Mihile.MOTORCYCLE_6:
                break;
            case Mihile.NIGHTMARE_60_6:
                break;
            case Mihile.NIMBLE_FEET_2:
                pEffect.statups.put(CharacterTemporaryStat.Speed, 10 + (pEffect.getLevel() - 1) * 5);
                break;
            case Mihile.NIMBUS_CLOUD_10:
                break;
            case Mihile.NOBLE_MIND_1:
                break;
            case Mihile.ORANGE_MUSHROOM_10:
                break;
            case Mihile.OSTRICH_9:
                break;
            case Mihile.PIGS_WEAKNESS_2:
                break;
            case Mihile.PINK_BEAR_HOTAIR_BALLOON_10:
                break;
            case Mihile.PINK_SCOOTER_6:
                break;
            case Mihile.POWER_EXPLOSION_20_2:
                break;
            case Mihile.POWER_SUIT_9:
                break;
            case Mihile.RACE_KART_6:
                break;
            case Mihile.RAGE_OF_PHARAOH_30_3:
                break;
            case Mihile.RECOVERY_2:
                break;
            case Mihile.SANTA_SLED_9:
                break;
            case Mihile.SHINJO_60_6:
                break;
            case Mihile.SLIMES_WEAKNESS_2:
                break;
            case Mihile.SOARING_40_4:
                break;
            case Mihile.SPACESHIP_60_6:
                break;
            case Mihile.SPACE_BEAM_50_5:
                break;
            case Mihile.SPACE_DASH_50_5:
                break;
            case Mihile.STUMPS_WEAKNESS_2:
                break;
            case Mihile.TEST_7:
                break;
            case Mihile.THREE_SNAILS_2:
                break;
            case Mihile.TRANSFORMED_ROBOT_6:
                break;
            case Mihile.WHITE_ANGELIC_BLESSING_80_8:
                break;
            case Mihile.WHITE_ANGEL_50_5:
                break;
            case Mihile.WILL_OF_THE_ALLIANCE_40_4:
                break;
            case Mihile.WITCHS_BROOMSTICK_200_20_2:
                break;
            case Mihile.WITCHS_BROOMSTICK_300_30_3:
                break;
            case Mihile.YETI_10:
                break;
            case Mihile.YETI_MOUNT_70_7:
                break;
            case Mihile.YETI_MOUNT_80_8:
                break;
            case Mihile.ZD_TIGER_6:
                break;
            case Mihile.HP_BOOST:
                break;
            case Mihile.ROYAL_GUARD:
                pEffect.statups.put(CharacterTemporaryStat.RoyalGuardPrepare, pEffect.info.get(StatInfo.x));
                break;
            case Mihile.ROYAL_GUARD_1:
                break;
            case Mihile.ROYAL_GUARD_2:
                break;
            case Mihile.ROYAL_GUARD_3:
                break;
            case Mihile.ROYAL_GUARD_4:
                break;
            case Mihile.ROYAL_GUARD_5:
                break;
            case Mihile.ROYAL_GUARD_6:
                break;
            case Mihile.ROYAL_GUARD_7:
                break;
            case Mihile.ROYAL_GUARD_8:
                break;
            case Mihile.SOUL_BLADE:
                break;
            case Mihile.SOUL_DEVOTION:
                break;
            case Mihile.SOUL_SHIELD:
                break;
            case Mihile.WEIGHTLESS_HEART:
                break;
            case Mihile.FINAL_ATTACK_5:
                pEffect.statups.put(CharacterTemporaryStat.FinalAttackProp, pEffect.info.get(StatInfo.x));
                break;
            case Mihile.PHYSICAL_TRAINING_90_9:
                break;
            case Mihile.RADIANT_DRIVER:
                break;
            case Mihile.RALLY:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case Mihile.SOUL_DRIVER:
                break;
            case Mihile.SWORD_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Mihile.SWORD_MASTERY_3:
                break;
            case Mihile.ADVANCED_ROYAL_GUARD:
                break;
            case Mihile.ENDURING_SPIRIT:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(StatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(StatInfo.z));
                pEffect.statups.put(CharacterTemporaryStat.DamageReduce, pEffect.info.get(StatInfo.x));
                break;
            case Mihile.INTENSE_FOCUS:
                break;
            case Mihile.MAGIC_CRASH_4:
                pEffect.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
                break;
            case Mihile.RADIANT_BUSTER:
                break;
            case Mihile.RADIANT_CHARGE:
                pEffect.statups.put(CharacterTemporaryStat.WeaponCharge, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.z));
                break;
            case Mihile.RADIANT_CHARGE_1:
                break;
            case Mihile.RIGHTEOUS_INDIGNATION:
                break;
            case Mihile.ROYAL_GUARD_10:
                break;
            case Mihile.ROYAL_GUARD_9:
                break;
            case Mihile.SELF_RECOVERY_2:
                break;
            case Mihile.SOUL_LINK:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.MichaelSoulLink, pEffect.info.get(StatInfo.s));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Mihile.TRINITY_ATTACK:
                break;
            case Mihile.ADVANCED_FINAL_ATTACK_1:
                break;
            case Mihile.CALL_OF_CYGNUS_1:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Mihile.CHARGING_LIGHT:
                break;
            case Mihile.COMBAT_MASTERY:
                break;
            case Mihile.ENDURING_SPIRIT_PERSIST:
                break;
            case Mihile.ENDURING_SPIRIT_PREPARATION:
                break;
            case Mihile.ENDURING_SPIRIT_STEEL_SKIN:
                break;
            case Mihile.EXPERT_SWORD_MASTERY:
                break;
            case Mihile.FOURPOINT_ASSAULT:
                break;
            case Mihile.FOURPOINT_ASSAULT_EXTRA_STRIKE:
                break;
            case Mihile.FOURPOINT_ASSAULT_OPPORTUNITY:
                break;
            case Mihile.FOURPOINT_ASSAULT_REINFORCE:
                break;
            case Mihile.HYPER_ACCURACY_3:
                break;
            case Mihile.HYPER_CRITICAL_3:
                break;
            case Mihile.HYPER_DEFENSE_2:
                break;
            case Mihile.HYPER_DEXTERITY_3:
                break;
            case Mihile.HYPER_FURY_3:
                break;
            case Mihile.HYPER_HEALTH_3:
                break;
            case Mihile.HYPER_INTELLIGENCE_3:
                break;
            case Mihile.HYPER_JUMP_3:
                break;
            case Mihile.HYPER_LUCK_3:
                break;
            case Mihile.HYPER_MAGIC_DEFENSE_3:
                break;
            case Mihile.HYPER_MANA_3:
                break;
            case Mihile.HYPER_SPEED_3:
                break;
            case Mihile.HYPER_STRENGTH_3:
                break;
            case Mihile.POWER_STANCE:
                break;
            case Mihile.QUEEN_OF_TOMORROW:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Mihile.RADIANT_BLAST:
                break;
            case Mihile.RADIANT_BLAST_EXTRA_STRIKE:
                break;
            case Mihile.RADIANT_BLAST_REINFORCE:
                break;
            case Mihile.RADIANT_BLAST_SPREAD:
                break;
            case Mihile.RADIANT_CROSS:
                break;
            case Mihile.RADIANT_CROSS_EXTRA_STRIKE:
                break;
            case Mihile.RADIANT_CROSS_REINFORCE:
                break;
            case Mihile.RADIANT_CROSS_SPREAD:
                break;
            case Mihile.ROILING_SOUL:
                pEffect.statups.put(CharacterTemporaryStat.Enrage, 1);
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Mihile.SACRED_CUBE:
                break;
            case Mihile.SOUL_ASYLUM:
                break;
            case Mihile.STANCE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 5000 || nClass == 5100 || nClass == 5110 || nClass == 5111 || nClass == 5112;
    }

}
