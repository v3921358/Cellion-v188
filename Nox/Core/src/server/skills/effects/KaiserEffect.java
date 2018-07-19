package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Kaiser;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class KaiserEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Kaiser.ARCHANGELIC_BLESSING_8:
                break;
            case Kaiser.ARCHANGELIC_BLESSING_9:
                break;
            case Kaiser.ARCHANGEL_800_80_8:
                break;
            case Kaiser.ARCHANGEL_900_90_9:
                break;
            case Kaiser.BALROG_80_8:
                break;
            case Kaiser.BAMBOO_RAIN_30_3:
                break;
            case Kaiser.BLACK_SCOOTER_7:
                break;
            case Kaiser.BLESSING_OF_THE_FAIRY_4:
                break;
            case Kaiser.BLUE_SCOOTER_20_2:
                break;
            case Kaiser.CALL_OF_THE_HUNTER_6:
                break;
            case Kaiser.CAPTURE_6:
                break;
            case Kaiser.CHARGE_TOY_TROJAN_7:
                break;
            case Kaiser.CROCO_7:
                break;
            case Kaiser.CRYSTAL_THROW_7:
                break;
            case Kaiser.DARK_ANGELIC_BLESSING_4:
                break;
            case Kaiser.DARK_ANGEL_40_4:
                break;
            case Kaiser.DEADLY_CRITS_1:
                break;
            case Kaiser.DECENT_ADVANCED_BLESSING_30_3:
                break;
            case Kaiser.DECENT_COMBAT_ORDERS_30_3:
                break;
            case Kaiser.DECENT_HASTE_20_2:
                break;
            case Kaiser.DECENT_HYPER_BODY_30_3:
                break;
            case Kaiser.DECENT_MYSTIC_DOOR_30_3:
                break;
            case Kaiser.DECENT_SHARP_EYES_30_3:
                break;
            case Kaiser.DECENT_SPEED_INFUSION_30_3:
                break;
            case Kaiser.DRAGON_LINK:
                break;
            case Kaiser.EMPRESSS_BLESSING_5:
                break;
            case Kaiser.EXCLUSIVE_SPELL_1:
                break;
            case Kaiser.FOLLOW_THE_LEAD_40_4:
                break;
            case Kaiser.FORTUNE_20_2:
                break;
            case Kaiser.FREEZING_AXE_5:
                break;
            case Kaiser.GIANT_POTION_20000_2000_200_20_2:
                break;
            case Kaiser.GIANT_POTION_30000_3000_300_30_3:
                break;
            case Kaiser.GIANT_POTION_40000_4000_400_40_4:
                break;
            case Kaiser.GIGAS_WAVE_2:
                break;
            case Kaiser.GODDESS_GUARD_3:
                break;
            case Kaiser.GODDESS_GUARD_6:
                break;
            case Kaiser.HIDDEN_POTENTIAL_HERO_4:
                break;
            case Kaiser.ICE_CHOP:
                break;
            case Kaiser.ICE_CURSE:
                break;
            case Kaiser.ICE_DOUBLE_JUMP_40_4:
                break;
            case Kaiser.ICE_KNIGHT_40_4:
                break;
            case Kaiser.ICE_SMASH_5:
                break;
            case Kaiser.ICE_TEMPEST:
                break;
            case Kaiser.INFILTRATE_7:
                break;
            case Kaiser.INVINCIBILITY_50_5:
                break;
            case Kaiser.IRON_WILL:
                break;
            case Kaiser.LEGENDARY_SPIRIT_50_5:
                break;
            case Kaiser.LEONARDO_THE_LION_30_3:
                break;
            case Kaiser.LINK_MANAGER_6:
                break;
            case Kaiser.MAKER_50_5:
                break;
            case Kaiser.MECHANIC_DASH_7:
                break;
            case Kaiser.MIST_BALROG_20_2:
                break;
            case Kaiser.MONSTER_RIDING_6:
                break;
            case Kaiser.MOTORCYCLE_7:
                break;
            case Kaiser.NIGHTMARE_90_9:
                break;
            case Kaiser.NIMBUS_CLOUD_40_4:
                break;
            case Kaiser.ORANGE_MUSHROOM_20_2:
                break;
            case Kaiser.OSTRICH_20_2:
                break;
            case Kaiser.PINK_BEAR_HOTAIR_BALLOON_30_3:
                break;
            case Kaiser.PINK_SCOOTER_7:
                break;
            case Kaiser.POTION_MASTERY_2:
                break;
            case Kaiser.POWER_EXPLOSION_30_3:
                break;
            case Kaiser.POWER_SUIT_10_1:
                break;
            case Kaiser.RACE_KART_7:
                break;
            case Kaiser.RAGE_OF_PHARAOH_50_5:
                break;
            case Kaiser.REALIGN_ATTACKER_MODE:
                pEffect.statups.put(CharacterTemporaryStat.BDR, pEffect.info.get(StatInfo.bdR));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.cr));
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.padX));
                pEffect.statups.put(CharacterTemporaryStat.ReshuffleSwitch, 0);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Kaiser.REALIGN_DEFENDER_MODE:
                pEffect.statups.put(CharacterTemporaryStat.PDD, pEffect.info.get(StatInfo.pddX));
                pEffect.statups.put(CharacterTemporaryStat.IndieMHPR, pEffect.info.get(StatInfo.mhpR));
                pEffect.statups.put(CharacterTemporaryStat.ReshuffleSwitch, 0);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Kaiser.ROLLS_2:
                break;
            case Kaiser.ROYCE_2:
                break;
            case Kaiser.SANTA_SLED_20_2:
                break;
            case Kaiser.SHINJO_80_8:
                break;
            case Kaiser.SOARING_60_6:
                break;
            case Kaiser.SPACESHIP_80_8:
                break;
            case Kaiser.SPACE_BEAM_70_7:
                break;
            case Kaiser.SPACE_DASH_70_7:
                break;
            case Kaiser.TEST_8:
                break;
            case Kaiser.TRANSFIGURATION:
                break;
            case Kaiser.TRANSFIGURATION_1:
                break;
            case Kaiser.TRANSFIGURATION_2:
                break;
            case Kaiser.TRANSFORMED_ROBOT_8:
                break;
            case Kaiser.VERTICAL_GRAPPLE:
                break;
            case Kaiser.WHITE_ANGELIC_BLESSING:
                break;
            case Kaiser.WHITE_ANGEL_70_7:
                break;
            case Kaiser.WILL_OF_THE_ALLIANCE:
                break;
            case Kaiser.WITCHS_BROOMSTICK_700_70_7:
                break;
            case Kaiser.WITCHS_BROOMSTICK_900_90_9:
                break;
            case Kaiser.YETI_30_3:
                break;
            case Kaiser.YETI_MOUNT_100_10:
                break;
            case Kaiser.YETI_MOUNT_100_10_1:
                break;
            case Kaiser.ZD_TIGER_7:
                break;
            case Kaiser.ARCHANGELIC_BLESSING_20_2:
                break;
            case Kaiser.ARCHANGELIC_BLESSING_30_3:
                break;
            case Kaiser.ARCHANGEL_10:
                break;
            case Kaiser.ARCHANGEL_10_1:
                break;
            case Kaiser.BALROG_5:
                break;
            case Kaiser.BAMBOO_RAIN_6:
                break;
            case Kaiser.BLACK_SCOOTER_2:
                break;
            case Kaiser.BLESSING_OF_THE_FAIRY_8:
                break;
            case Kaiser.BLUE_SCOOTER_3:
                break;
            case Kaiser.CALL_OF_THE_HUNTER_1:
                break;
            case Kaiser.CAPTURE_1:
                break;
            case Kaiser.CHARGE_TOY_TROJAN_2:
                break;
            case Kaiser.CROCO_2:
                break;
            case Kaiser.CRYSTAL_THROW_1:
                break;
            case Kaiser.DARK_ANGELIC_BLESSING_6:
                break;
            case Kaiser.DARK_ANGEL_5:
                break;
            case Kaiser.DAY_DREAMER:
                break;
            case Kaiser.DEADLY_CRITS_2:
                break;
            case Kaiser.DECENT_ADVANCED_BLESSING_90_9:
                break;
            case Kaiser.DECENT_COMBAT_ORDERS_90_9:
                break;
            case Kaiser.DECENT_HASTE_80_8:
                break;
            case Kaiser.DECENT_HYPER_BODY_90_9:
                break;
            case Kaiser.DECENT_MYSTIC_DOOR_90_9:
                break;
            case Kaiser.DECENT_SHARP_EYES_90_9:
                break;
            case Kaiser.DECENT_SPEED_INFUSION_90_9:
                break;
            case Kaiser.DRESSUP:
                break;
            case Kaiser.EMPRESSS_BLESSING_7:
                break;
            case Kaiser.EXCLUSIVE_SPELL:
                break;
            case Kaiser.FOLLOW_THE_LEAD_7:
                break;
            case Kaiser.FORTUNE_6:
                break;
            case Kaiser.FREEZING_AXE_7:
                break;
            case Kaiser.GIANT_POTION_50_5:
                break;
            case Kaiser.GIANT_POTION_60_6:
                break;
            case Kaiser.GIANT_POTION_70_7:
                break;
            case Kaiser.GODDESS_GUARD_50_5:
                break;
            case Kaiser.GODDESS_GUARD_700_70_7:
                break;
            case Kaiser.GRAPPLING_HEART:
                break;
            case Kaiser.HIDDEN_POTENTIAL_HERO_5:
                break;
            case Kaiser.HYPER_COORDINATE:
                break;
            case Kaiser.ICE_CHOP_10:
                break;
            case Kaiser.ICE_CURSE_10_1:
                break;
            case Kaiser.ICE_DOUBLE_JUMP_5:
                break;
            case Kaiser.ICE_KNIGHT_8:
                break;
            case Kaiser.ICE_SMASH_7:
                break;
            case Kaiser.ICE_TEMPEST_10:
                break;
            case Kaiser.INFILTRATE_1:
                break;
            case Kaiser.INVINCIBILITY_7:
                break;
            case Kaiser.LEGENDARY_SPIRIT_6:
                break;
            case Kaiser.LEONARDO_THE_LION_3:
                break;
            case Kaiser.LINK_MANAGER_8:
                break;
            case Kaiser.MAKER_6:
                break;
            case Kaiser.MECHANIC_DASH_1:
                break;
            case Kaiser.MIST_BALROG_3:
                break;
            case Kaiser.MONSTER_RIDING_2:
                break;
            case Kaiser.MOTORCYCLE_2:
                break;
            case Kaiser.NIGHTMARE_6:
                break;
            case Kaiser.NIMBUS_CLOUD_3:
                break;
            case Kaiser.ORANGE_MUSHROOM_4:
                break;
            case Kaiser.OSTRICH_3:
                break;
            case Kaiser.PINK_BEAR_HOTAIR_BALLOON_4:
                break;
            case Kaiser.PINK_SCOOTER_2:
                break;
            case Kaiser.POTION_MASTERY_3:
                break;
            case Kaiser.POWER_EXPLOSION_6:
                break;
            case Kaiser.POWER_SUIT_3:
                break;
            case Kaiser.RACE_KART_2:
                break;
            case Kaiser.RAGE_OF_PHARAOH_6:
                break;
            case Kaiser.ROLLS_1:
                break;
            case Kaiser.ROYCE_1:
                break;
            case Kaiser.SANTA_SLED_3:
                break;
            case Kaiser.SHINJO_6:
                break;
            case Kaiser.SOARING_7:
                break;
            case Kaiser.SOUL_BUSTER:
                break;
            case Kaiser.SPACESHIP_8:
                break;
            case Kaiser.SPACE_BEAM_7:
                break;
            case Kaiser.SPACE_DASH_7:
                break;
            case Kaiser.TERMS_AND_CONDITIONS_1:
                break;
            case Kaiser.TEST_1:
                break;
            case Kaiser.TRANSFORMED_ROBOT_2:
                break;
            case Kaiser.TRUE_HEART_INHERITANCE:
                break;
            case Kaiser.WHITE_ANGELIC_BLESSING_10_1:
                break;
            case Kaiser.WHITE_ANGEL_9:
                break;
            case Kaiser.WILL_OF_THE_ALLIANCE_10_1:
                break;
            case Kaiser.WITCHS_BROOMSTICK_10_1:
                break;
            case Kaiser.WITCHS_BROOMSTICK_7:
                break;
            case Kaiser.YETI_4:
                break;
            case Kaiser.YETI_MOUNT_5:
                break;
            case Kaiser.YETI_MOUNT_6:
                break;
            case Kaiser.ZD_TIGER_2:
                break;
            case Kaiser.AIR_LIFT:
                break;
            case Kaiser.DRAGON_SLASH_1:
                break;
            case Kaiser.DRAGON_SLASH_2:
                break;
            case Kaiser.DRAGON_SLASH_3:
                break;
            case Kaiser.FLAME_SURGE:
                break;
            case Kaiser.SCALE_SKIN:
                break;
            case Kaiser.ATTACKER_MODE_I:
                break;
            case Kaiser.BLAZE_ON:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Kaiser.DEFENDER_MODE_I:
                break;
            case Kaiser.DRAGON_SLASH_I:
                break;
            case Kaiser.IMPACT_WAVE_1:
                break;
            case Kaiser.INNER_BLAZE:
                break;
            case Kaiser.PIERCING_BLAZE_1:
                break;
            case Kaiser.SWORD_MASTERY_2:
                break;
            case Kaiser.TEMPEST_BLADES_1:
                pEffect.statups.put(CharacterTemporaryStat.StopForceAtomInfo, 1);
                break;
            case Kaiser.ADVANCED_INNER_BLAZE:
                break;
            case Kaiser.ATTACKER_MODE_II:
                break;
            case Kaiser.CATALYZE:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Kaiser.CATALYZE_1:
                break;
            case Kaiser.CURSEBITE:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, -(pEffect.info.get(StatInfo.asrR)));
                pEffect.statups.put(CharacterTemporaryStat.TerR, -(pEffect.info.get(StatInfo.terR)));
                break;
            case Kaiser.CURSEBITE_1:
                break;
            case Kaiser.DEFENDER_MODE_II:
                break;
            case Kaiser.DRAGON_SLASH:
                break;
            case Kaiser.DRAGON_SLASH_II:
                break;
            case Kaiser.FINAL_FORM_1:
                //pEffect.statups.put(CharacterTemporaryStat.Morph, pEffect.info.get(MapleStatInfo.morph)); // 1200 (3rd) // 1201 (4th)
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.cr));
                pEffect.statups.put(CharacterTemporaryStat.IndiePMdR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case Kaiser.FLAME_SURGE_1:
                break;
            case Kaiser.IMPACT_WAVE:
                break;
            case Kaiser.PIERCING_BLAZE:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Kaiser.PRESSURE_CHAIN:
                break;
            case Kaiser.PRESSURE_CHAIN_1:
                break;
            case Kaiser.SELF_RECOVERY_1:
                break;
            case Kaiser.STONE_DRAGON:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Kaiser.STONE_DRAGON_1:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Kaiser.TEMPEST_BLADES:
                pEffect.statups.put(CharacterTemporaryStat.StopForceAtomInfo, 3);
                break;
            case Kaiser.TRANSFIGURATION_TELEPORT:
                break;
            case Kaiser.WING_BEAT:
                break;
            case Kaiser.WING_BEAT_1:
                break;
            case Kaiser.WING_BEAT_2:
                break;
            case Kaiser.WING_BEAT_TRANSFIGURATION:
                break;
            case Kaiser.ADVANCED_TEMPEST_BLADES:
                pEffect.statups.put(CharacterTemporaryStat.StopForceAtomInfo, 4);
                break;
            case Kaiser.ADVANCED_TEMPEST_BLADES_1:
                pEffect.statups.put(CharacterTemporaryStat.StopForceAtomInfo, 2);
                break;
            case Kaiser.ANCESTRAL_PROMINENCE:
                break;
            case Kaiser.ATTACKER_MODE_III:
                break;
            case Kaiser.BLADE_BURST:
                break;
            case Kaiser.BLADE_BURST_1:
                break;
            case Kaiser.BLADE_BURST_2:
                break;
            case Kaiser.BLADE_BURST_3:
                break;
            case Kaiser.DEFENDER_MODE_III:
                break;
            case Kaiser.DRAGON_BARRAGE:
                break;
            case Kaiser.DRAGON_BARRAGE_1:
                break;
            case Kaiser.DRAGON_SLASH_4:
                break;
            case Kaiser.DRAGON_SLASH_III:
                break;
            case Kaiser.EXPERT_SWORD_MASTERY_1:
                break;
            case Kaiser.FINAL_FORM:
                //pEffect.statups.put(CharacterTemporaryStat.Morph, pEffect.info.get(MapleStatInfo.morph));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.cr));
                pEffect.statups.put(CharacterTemporaryStat.IndiePMdR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case Kaiser.FINAL_TRANCE:
                //pEffect.statups.put(CharacterTemporaryStat.Morph, pEffect.info.get(MapleStatInfo.morph));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.cr));
                pEffect.statups.put(CharacterTemporaryStat.IndiePMdR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case Kaiser.GIGAS_WAVE:
                break;
            case Kaiser.GIGAS_WAVE_1:
                break;
            case Kaiser.GIGAS_WAVE_BONUS_ATTACK:
                break;
            case Kaiser.GIGAS_WAVE_BURDEN:
                break;
            case Kaiser.GIGAS_WAVE_REINFORCE:
                break;
            case Kaiser.GRAND_ARMOR:
                pEffect.statups.put(CharacterTemporaryStat.DamageReduce, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.DamageReduce, pEffect.info.get(StatInfo.v));
                break;
            case Kaiser.HYPER_ACCURACY_7:
                break;
            case Kaiser.HYPER_CRITICAL_7:
                break;
            case Kaiser.HYPER_DEXTERITY_7:
                break;
            case Kaiser.HYPER_FURY_7:
                break;
            case Kaiser.HYPER_HEALTH_7:
                break;
            case Kaiser.HYPER_INTELLIGENCE_7:
                break;
            case Kaiser.HYPER_JUMP_7:
                break;
            case Kaiser.HYPER_LUCK_7:
                break;
            case Kaiser.HYPER_MAGIC_DEFENSE_7:
                break;
            case Kaiser.HYPER_MANA_7:
                break;
            case Kaiser.HYPER_SPEED_7:
                break;
            case Kaiser.HYPER_STRENGTH_7:
                break;
            case Kaiser.HYPER_WEAPON_DEFENSE_1:
                break;
            case Kaiser.INFERNO_BREATH:
                break;
            case Kaiser.INFERNO_BREATH_1:
                break;
            case Kaiser.INFERNO_BREATH_2:
                break;
            case Kaiser.INFERNO_BREATH_BLAZE:
                break;
            case Kaiser.INFERNO_BREATH_BURN:
                break;
            case Kaiser.INFERNO_BREATH_REINFORCE:
                break;
            case Kaiser.KAISERS_MAJESTY:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(StatInfo.indieBooster));
                break;
            case Kaiser.NOVA_TEMPERANCE:
                break;
            case Kaiser.NOVA_TEMPERANCE_2:
                break;
            case Kaiser.NOVA_WARRIOR_1:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Kaiser.UNBREAKABLE_WILL:
                break;
            case Kaiser.WING_BEAT_EXTRA_ATTACK:
                break;
            case Kaiser.WING_BEAT_PUMMEL:
                break;
            case Kaiser.WING_BEAT_REINFORCE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 6000 || nClass == 6001 || nClass == 6100 || nClass == 6110 || nClass == 6111 || nClass == 6112;
    }

}
