package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Zero;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ZeroEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Zero.ARCHANGELIC_BLESSING:
                break;
            case Zero.ARCHANGELIC_BLESSING_1:
                break;
            case Zero.ARCHANGEL_6:
                break;
            case Zero.ARCHANGEL_7:
                break;
            case Zero.BAMBOO_RAIN_4:
                break;
            case Zero.BLESSING_OF_THE_FAIRY_5:
                break;
            case Zero.BURST_JUMP:
                break;
            case Zero.BURST_LEAP:
                break;
            case Zero.BURST_STEP:
                break;
            case Zero.DARK_ANGELIC_BLESSING:
                break;
            case Zero.DARK_ANGEL_3:
                break;
            case Zero.DECENT_ADVANCED_BLESSING_20_2:
                break;
            case Zero.DECENT_COMBAT_ORDERS_20_2:
                break;
            case Zero.DECENT_HASTE_10_1:
                break;
            case Zero.DECENT_HYPER_BODY_20_2:
                break;
            case Zero.DECENT_MYSTIC_DOOR_20_2:
                break;
            case Zero.DECENT_SHARP_EYES_20_2:
                break;
            case Zero.DECENT_SPEED_INFUSION_20_2:
                break;
            case Zero.DIVINE_FORCE:
                pEffect.statups.put(CharacterTemporaryStat.ZeroAuraStr, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieTerR, pEffect.info.get(StatInfo.indieTerR));
                pEffect.statups.put(CharacterTemporaryStat.IndieAsrR, pEffect.info.get(StatInfo.indieAsrR));
                pEffect.statups.put(CharacterTemporaryStat.IndiePDD, pEffect.info.get(StatInfo.indiePdd));
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, pEffect.info.get(StatInfo.indiePad));
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indieMad));
                break;
            case Zero.DIVINE_SPEED:
                pEffect.statups.put(CharacterTemporaryStat.ZeroAuraSpd, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(StatInfo.indieBooster));
                pEffect.statups.put(CharacterTemporaryStat.IndieJump, pEffect.info.get(StatInfo.indieJump));
                pEffect.statups.put(CharacterTemporaryStat.IndieSpeed, pEffect.info.get(StatInfo.indieSpeed));
                break;
            case Zero.DOUBLETIME:
                break;
            case Zero.DOUBLETIME_COMBAT:
                break;
            case Zero.DOUBLETIME_RECON:
                break;
            case Zero.DROP_TIME:
                break;
            case Zero.DUAL_COMBAT:
                break;
            case Zero.DUAL_COMBAT_1:
                break;
            case Zero.EMPRESSS_BLESSING:
                break;
            case Zero.FOCUSED_TIME:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.x));
                break;
            case Zero.FOLLOW_THE_LEAD_4:
                break;
            case Zero.FREEZING_AXE:
                break;
            case Zero.GIANT_POTION_10:
                break;
            case Zero.GIANT_POTION_10_1:
                break;
            case Zero.GIANT_POTION_9:
                break;
            case Zero.GODDESS_GUARD_4:
                break;
            case Zero.GODDESS_GUARD_7:
                break;
            case Zero.HIDDEN_POTENTIAL_HERO:
                break;
            case Zero.ICE_CHOP_1:
                break;
            case Zero.ICE_CURSE_1:
                break;
            case Zero.ICE_DOUBLE_JUMP_3:
                break;
            case Zero.ICE_KNIGHT_4:
                break;
            case Zero.ICE_SMASH:
                break;
            case Zero.ICE_TEMPEST_1:
                break;
            case Zero.INVINCIBILITY_5:
                break;
            case Zero.LEGENDARY_SPIRIT_4:
                break;
            case Zero.LINK_MANAGER_4:
                break;
            case Zero.MAKER_4:
                break;
            case Zero.MASTER_OF_ORGANIZATION:
                break;
            case Zero.MASTER_OF_ORGANIZATION_1:
                break;
            case Zero.MASTER_OF_SWIMMING:
                break;
            case Zero.MOON_STRIKE:
                break;
            case Zero.PIERCING_THRUST:
                break;
            case Zero.PIGS_WEAKNESS_9:
                break;
            case Zero.PIRATE_BLESSING:
                break;
            case Zero.POWER_EXPLOSION_4:
                break;
            case Zero.RAGE_OF_PHARAOH_4:
                break;
            case Zero.RESOLUTION_TIME:
                break;
            case Zero.REWIND:
                break;
            case Zero.RHINNES_BLESSING:
                break;
            case Zero.RHINNES_PROTECTION:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Zero.SHADOW_RAIN_1:
                break;
            case Zero.SHADOW_STRIKE:
                break;
            case Zero.SLIMES_WEAKNESS_8:
                break;
            case Zero.SOARING_5:
                break;
            case Zero.SPACESHIP_5:
                break;
            case Zero.SPACE_BEAM_4:
                break;
            case Zero.SPACE_DASH_4:
                break;
            case Zero.STUMPS_WEAKNESS_9:
                break;
            case Zero.TEMPLE_RECALL:
                break;
            case Zero.TIME_DISPEL:
                break;
            case Zero.TIME_DISTORTION:
                break;
            case Zero.TIME_HOLDING:
                break;
            case Zero.TIME_HOLDING_1:
                break;
            case Zero.UNK_27:
                break;
            case Zero.WHITE_ANGELIC_BLESSING_1:
                break;
            case Zero.WHITE_ANGEL_3:
                break;
            case Zero.WILL_OF_THE_ALLIANCE_1:
                break;
            case Zero.ADVANCED_EARTH_STOMP:
                break;
            case Zero.AIR_RAID:
                break;
            case Zero.AIR_RIOT:
                break;
            case Zero.AIR_RIOT_1:
                break;
            case Zero.HEAVY_SWORD_MASTERY:
                break;
            case Zero.LONG_SWORD_MASTERY:
                break;
            case Zero.MOON_STRIKE_1:
                break;
            case Zero.PIERCING_THRUST_1:
                break;
            case Zero.RISING_SLASH_2:
                break;
            case Zero.SHADOW_STRIKE_1:
                break;
            case Zero.SHADOW_STRIKE_2:
                break;
            case Zero.ADVANCED_BLADE_RING:
                break;
            case Zero.ADVANCED_SPIN_CUTTER:
                break;
            case Zero.ADVANCED_THROWING_WEAPON:
                break;
            case Zero.FLASH_ASSAULT:
                break;
            case Zero.FLASH_CUT:
                break;
            case Zero.REINFORCE_BODY:
                break;
            case Zero.SOLID_BODY:
                break;
            case Zero.SPIN_CUTTER:
                break;
            case Zero.THROWING_WEAPON:
                break;
            case Zero.ADVANCED_BLADE_TEMPEST:
                break;
            case Zero.ADVANCED_ROLLING_ASSAULT:
                break;
            case Zero.ADVANCED_ROLLING_ASSAULT_1:
                break;
            case Zero.ADVANCED_WHEEL_WIND:
                break;
            case Zero.ARMOR_SPLIT:
                break;
            case Zero.GRAND_ROLLING_CROSS:
                break;
            case Zero.GRAND_ROLLING_CROSS_1:
                break;
            case Zero.ROLLING_ASSAULT:
                break;
            case Zero.ROLLING_CROSS:
                break;
            case Zero.SPIN_DRIVER:
                break;
            case Zero.SPIN_DRIVER_1:
                break;
            case Zero.TIME_GENERATOR:
                break;
            case Zero.WHEEL_WIND:
                break;
            case Zero.ADVANCED_EARTH_BREAK:
                break;
            case Zero.ADVANCED_STORM_BREAK:
                break;
            case Zero.CRITICAL_BIND:
                break;
            case Zero.DIVINE_LEER:
                break;
            case Zero.EARTH_BREAK:
                break;
            case Zero.FALLING_STAR:
                break;
            case Zero.FALLING_STAR_1:
                break;
            case Zero.GIGA_CRASH:
                break;
            case Zero.GROUNDBREAKER:
                break;
            case Zero.HURRICANE_WIND:
                break;
            case Zero.HURRICANE_WIND_LIGHTNING_STRIKE:
                break;
            case Zero.HURRICANE_WIND_WIND_DRAWIN:
                break;
            case Zero.IMMUNE_BARRIER:
                break;
            case Zero.MEGA_GROUNDBREAKER:
                break;
            case Zero.MEGA_GROUNDBREAKER_1:
                break;
            case Zero.SEVERE_STORM_BREAK:
                break;
            case Zero.SEVERE_STORM_BREAK_1:
                break;
            case Zero.SHADOW_RAIN:
                break;
            case Zero.STORM_BREAK:
                break;
            case Zero.STORM_BREAK_1:
                break;
            case Zero.WIND_CUTTER:
                break;
            case Zero.WIND_CUTTER_1:
                break;
            case Zero.WIND_STRIKER:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 10000 || nClass == 10100 || nClass == 10110 || nClass == 10111 || nClass == 10112;
    }

}
