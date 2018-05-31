package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Luminous;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class LuminousEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Luminous.ABYSSAL_DROP:
                break;
            case Luminous.DARK_AFFINITY:
                break;
            case Luminous.FLASH_SHOWER:
                break;
            case Luminous.LIGHT_AFFINITY:
                break;
            case Luminous.LIGHT_SPEED_1:
                break;
            case Luminous.MAGIC_THEFT_2:
                break;
            case Luminous.MANA_WELL:
                break;
            case Luminous.MANA_WELL_1:
                break;
            case Luminous.STANDARD_MAGIC_GUARD:
                pEffect.statups.put(CharacterTemporaryStat.MagicGuard, pEffect.info.get(StatInfo.x));
                break;
            case Luminous.BLACK_BLESSING:
                break;
            case Luminous.BLINDING_PILLAR:
                break;
            case Luminous.HIGH_WISDOM_1:
                break;
            case Luminous.MAGIC_BOOSTER_3:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Luminous.PRESSURE_VOID:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Luminous.SPELL_MASTERY_1:
                break;
            case Luminous.SYLVAN_LANCE:
                break;
            case Luminous.DEATH_SCYTHE:
                break;
            case Luminous.DUSK_GUARD:
                pEffect.statups.put(CharacterTemporaryStat.IndiePDD, pEffect.info.get(StatInfo.indiePdd));
                break;
            case Luminous.LUNAR_TIDE:
                break;
            case Luminous.MOONLIGHT_SPEAR:
                break;
            case Luminous.PHOTIC_MEDITATION:
                pEffect.statups.put(CharacterTemporaryStat.EMAD, pEffect.info.get(StatInfo.emad));
                break;
            case Luminous.RAY_OF_REDEMPTION:
                break;
            case Luminous.SHADOW_SHELL:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Luminous.SPECTRAL_LIGHT_1:
                break;
            case Luminous.APOCALYPSE:
                break;
            case Luminous.APOCALYPSE_EXTRA_TARGET:
                break;
            case Luminous.APOCALYPSE_RECHARGE:
                break;
            case Luminous.APOCALYPSE_REINFORCE:
                break;
            case Luminous.ARCANE_PITCH:
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(StatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(StatInfo.y));
                break;
            case Luminous.ARMAGEDDON:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Luminous.DARKNESS_MASTERY:
                break;
            case Luminous.DARK_CRESCENDO:
                pEffect.statups.put(CharacterTemporaryStat.StackBuff, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 180000);
                break;
            case Luminous.ENDER:
                break;
            case Luminous.ENDER_EXTRA_TARGET:
                break;
            case Luminous.ENDER_RANGE_UP:
                break;
            case Luminous.ENDER_REINFORCE:
                break;
            case Luminous.EQUALIZE:
                break;
            case Luminous.HEROIC_MEMORIES_6:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Luminous.HEROS_WILL_200_20_2:
                break;
            case Luminous.HYPER_ACCURACY_200_20_2:
                break;
            case Luminous.HYPER_CRITICAL_200_20_2:
                break;
            case Luminous.HYPER_DEXTERITY_200_20_2:
                break;
            case Luminous.HYPER_FURY_200_20_2:
                break;
            case Luminous.HYPER_HEALTH_200_20_2:
                break;
            case Luminous.HYPER_INTELLIGENCE_200_20_2:
                break;
            case Luminous.HYPER_JUMP_200_20_2:
                break;
            case Luminous.HYPER_LUCK_200_20_2:
                break;
            case Luminous.HYPER_MAGIC_DEFENSE_200_20_2:
                break;
            case Luminous.HYPER_MANA_200_20_2:
                break;
            case Luminous.HYPER_SPEED_200_20_2:
                break;
            case Luminous.HYPER_STRENGTH_200_20_2:
                break;
            case Luminous.HYPER_WEAPON_DEFENSE_3:
                break;
            case Luminous.MAGIC_MASTERY_2:
                break;
            case Luminous.MAPLE_WARRIOR_300_30_3:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Luminous.MORNING_STAR:
                break;
            case Luminous.MORNING_STAR_1:
                break;
            case Luminous.REFLECTION_1:
                break;
            case Luminous.REFLECTION_RANGE_UP:
                break;
            case Luminous.REFLECTION_REINFORCE:
                break;
            case Luminous.REFLECTION_SPREAD:
                break;
            case Luminous.ARCHANGELIC_BLESSING_40_4:
                break;
            case Luminous.ARCHANGELIC_BLESSING_50_5:
                break;
            case Luminous.ARCHANGEL_2:
                break;
            case Luminous.ARCHANGEL_3:
                break;
            case Luminous.BALROG_4:
                break;
            case Luminous.BAMBOO_RAIN_2:
                break;
            case Luminous.BLACK_SCOOTER_1:
                break;
            case Luminous.BLESSING_OF_THE_FAIRY_1:
                break;
            case Luminous.BLUE_SCOOTER_1:
                break;
            case Luminous.CALL_OF_THE_HUNTER:
                break;
            case Luminous.CAPTURE:
                break;
            case Luminous.CHANGE_LIGHTDARK_MODE:
                break;
            case Luminous.CHARGE_TOY_TROJAN_1:
                break;
            case Luminous.CROCO_1:
                break;
            case Luminous.CRYSTAL_THROW:
                break;
            case Luminous.DARK_ANGELIC_BLESSING_7:
                break;
            case Luminous.DARK_ANGEL_1:
                break;
            case Luminous.DEADLY_CRITS_3:
                break;
            case Luminous.DECENT_ADVANCED_BLESSING_100_10:
                break;
            case Luminous.DECENT_COMBAT_ORDERS_100_10:
                break;
            case Luminous.DECENT_HASTE_90_9:
                break;
            case Luminous.DECENT_HYPER_BODY_100_10:
                break;
            case Luminous.DECENT_MYSTIC_DOOR_100_10:
                break;
            case Luminous.DECENT_SHARP_EYES_100_10:
                break;
            case Luminous.DECENT_SPEED_INFUSION_100_10:
                break;
            case Luminous.ECLIPSE:
                break;
            case Luminous.EMPRESSS_BLESSING_8:
                break;
            case Luminous.EQUILIBRIUM:
                break;
            case Luminous.EQUILIBRIUM_1:
                break;
            case Luminous.FLASH_BLINK:
                break;
            case Luminous.FOLLOW_THE_LEAD_3:
                break;
            case Luminous.FORTUNE_2:
                break;
            case Luminous.FREEZING_AXE_8:
                break;
            case Luminous.GIANT_POTION_3:
                break;
            case Luminous.GIANT_POTION_4:
                break;
            case Luminous.GIANT_POTION_5:
                break;
            case Luminous.GODDESS_GUARD_1:
                break;
            case Luminous.GODDESS_GUARD_100_10_1:
                break;
            case Luminous.HEROS_ECHO_1:
                break;
            case Luminous.HIDDEN_POTENTIAL_HERO_6:
                break;
            case Luminous.ICE_CHOP_7:
                break;
            case Luminous.ICE_CURSE_7:
                break;
            case Luminous.ICE_DOUBLE_JUMP_1:
                break;
            case Luminous.ICE_KNIGHT_2:
                break;
            case Luminous.ICE_SMASH_8:
                break;
            case Luminous.ICE_TEMPEST_7:
                break;
            case Luminous.INFILTRATE:
                break;
            case Luminous.INNER_LIGHT:
                break;
            case Luminous.INVINCIBILITY_3:
                break;
            case Luminous.LEGENDARY_SPIRIT_2:
                break;
            case Luminous.LEONARDO_THE_LION_2:
                break;
            case Luminous.LIGHT_WASH_1:
                break;
            case Luminous.LINK_MANAGER_2:
                break;
            case Luminous.MAKER_2:
                break;
            case Luminous.MECHANIC_DASH:
                break;
            case Luminous.MIST_BALROG_2:
                break;
            case Luminous.MONSTER_RIDING_1:
                break;
            case Luminous.MOTORCYCLE_1:
                break;
            case Luminous.NIGHTMARE_4:
                break;
            case Luminous.NIMBUS_CLOUD_2:
                break;
            case Luminous.ORANGE_MUSHROOM_2:
                break;
            case Luminous.OSTRICH_1:
                break;
            case Luminous.PINK_BEAR_HOTAIR_BALLOON_2:
                break;
            case Luminous.PINK_SCOOTER_1:
                break;
            case Luminous.POTION_MASTERY:
                break;
            case Luminous.POWER_EXPLOSION_2:
                break;
            case Luminous.POWER_SUIT_1:
                break;
            case Luminous.RACE_KART_1:
                break;
            case Luminous.RAGE_OF_PHARAOH_3:
                break;
            case Luminous.ROLLS:
                break;
            case Luminous.ROYCE:
                break;
            case Luminous.SANTA_SLED_1:
                break;
            case Luminous.SHINJO_4:
                break;
            case Luminous.SOARING_4:
                break;
            case Luminous.SPACESHIP_3:
                break;
            case Luminous.SPACE_BEAM_3:
                break;
            case Luminous.SPACE_DASH_3:
                break;
            case Luminous.SPECTRAL_LIGHT:
                break;
            case Luminous.SUNFIRE:
                break;
            case Luminous.TEST:
                break;
            case Luminous.TRANSFORMED_ROBOT_1:
                break;
            case Luminous.WHITE_ANGELIC_BLESSING_7:
                break;
            case Luminous.WHITE_ANGEL_1:
                break;
            case Luminous.WILL_OF_THE_ALLIANCE_7:
                break;
            case Luminous.WITCHS_BROOMSTICK_5:
                break;
            case Luminous.WITCHS_BROOMSTICK_6:
                break;
            case Luminous.YETI_2:
                break;
            case Luminous.YETI_MOUNT_3:
                break;
            case Luminous.YETI_MOUNT_4:
                break;
            case Luminous.ZD_TIGER_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2700 || nClass == 2710 || nClass == 2711 || nClass == 2712 || nClass == 2004;
    }

}
