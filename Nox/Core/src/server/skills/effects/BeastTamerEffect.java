package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import constants.skills.BeastTamer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BeastTamerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case BeastTamer.ARCHANGEL_2000_200_20_2:
                break;
            case BeastTamer.ARCHANGEL_4000_400_40_4:
                break;
            case BeastTamer.BAMBOO_RAIN_60_6:
                break;
            case BeastTamer.BEAR_MODE:
                break;
            case BeastTamer.BEASTLY_RESOLVE:
                break;
            case BeastTamer.BEAST_SCEPTER_MASTERY:
                break;
            case BeastTamer.BIRDSEYE_VIEW:
                pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(MapleStatInfo.indieCr));
                pEffect.statups.put(CharacterTemporaryStat.EPDD, pEffect.info.get(MapleStatInfo.indiePdd));
                break;
            case BeastTamer.BLESSING_OF_THE_FAIRY_2:
                break;
            case BeastTamer.CAT_MODE:
                break;
            case BeastTamer.COMMON_DOUBLE_JUMP:
                break;
            case BeastTamer.CRITTER_SELECT:
                break;
            case BeastTamer.DARK_ANGEL_60_6:
                break;
            case BeastTamer.DECENT_ADVANCED_BLESSING_40_4:
                break;
            case BeastTamer.DECENT_COMBAT_ORDERS_40_4:
                break;
            case BeastTamer.DECENT_HASTE_30_3:
                break;
            case BeastTamer.DECENT_HYPER_BODY_40_4:
                break;
            case BeastTamer.DECENT_MYSTIC_DOOR_40_4:
                break;
            case BeastTamer.DECENT_SHARP_EYES_40_4:
                break;
            case BeastTamer.DECENT_SPEED_INFUSION_40_4:
                break;
            case BeastTamer.EMPRESSS_BLESSING_3:
                break;
            case BeastTamer.FLY:
                pEffect.statups.put(CharacterTemporaryStat.NewFlying, 1);
                break;
            case BeastTamer.FOCUSED_TIME_1:
                break;
            case BeastTamer.FOCUS_SPIRIT:
                break;
            case BeastTamer.FOLLOW_THE_LEAD_80_8:
                break;
            case BeastTamer.FREEZING_AXE_2:
                break;
            case BeastTamer.GROWTH_SPURT:
                break;
            case BeastTamer.GUARDIAN_LEAP:
                break;
            case BeastTamer.HAWK_FLOCK:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(MapleStatInfo.speed));
                pEffect.statups.put(CharacterTemporaryStat.Jump, pEffect.info.get(MapleStatInfo.jump));
                break;
            case BeastTamer.HAWK_MODE:
                break;
            case BeastTamer.HIDDEN_POTENTIAL_HERO_2:
                break;
            case BeastTamer.HOMEWARD_BOUND:
                break;
            case BeastTamer.ICE_CHOP_3:
                break;
            case BeastTamer.ICE_CURSE_3:
                break;
            case BeastTamer.ICE_DOUBLE_JUMP_70_7:
                break;
            case BeastTamer.ICE_KNIGHT_70_7:
                break;
            case BeastTamer.ICE_SMASH_2:
                break;
            case BeastTamer.ICE_TEMPEST_2:
                break;
            case BeastTamer.INVINCIBILITY_80_8:
                break;
            case BeastTamer.LEGENDARY_SPIRIT_80_8:
                break;
            case BeastTamer.LINK_MANAGER_1:
                break;
            case BeastTamer.MAKER_90_9:
                break;
            case BeastTamer.MAPLE_GUARDIAN:
                break;
            case BeastTamer.MODE_CANCEL:
                break;
            case BeastTamer.PIGS_WEAKNESS_3:
                break;
            case BeastTamer.POWER_EXPLOSION_60_6:
                break;
            case BeastTamer.RAGE_OF_PHARAOH_90_9:
                break;
            case BeastTamer.SLIMES_WEAKNESS_3:
                break;
            case BeastTamer.SNOW_LEOPARD_MODE:
                break;
            case BeastTamer.SOARING_100_10:
                break;
            case BeastTamer.SPACESHIP_100_10_1:
                break;
            case BeastTamer.SPACE_BEAM_100_10:
                break;
            case BeastTamer.SPACE_DASH_100_10:
                break;
            case BeastTamer.STUMPS_WEAKNESS_3:
                break;
            case BeastTamer.VISITOR_MELEE_ATTACK_9:
                break;
            case BeastTamer.VISITOR_RANGE_ATTACK_9:
                break;
            case BeastTamer.WHITE_ANGELIC_BLESSING_4:
                break;
            case BeastTamer.WHITE_ANGEL_100_10:
                break;
            case BeastTamer.WILL_OF_THE_ALLIANCE_4:
                break;
            case BeastTamer.BEAR_35_HIT:
                break;
            case BeastTamer.BEAR_ASSAULT:
                break;
            case BeastTamer.BEAR_REBORN:
                break;
            case BeastTamer.BEAR_STRENGTH:
                break;
            case BeastTamer.BILLOWING_TRUMPET:
                break;
            case BeastTamer.DEEP_BREATH:
                break;
            case BeastTamer.DEFENSE_IGNORANCE:
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                break;
            case BeastTamer.DUMB_LUCK:
                break;
            case BeastTamer.FISHY_SLAP:
                break;
            case BeastTamer.FORT_FOLLOWUP:
                break;
            case BeastTamer.FORT_THE_BRAVE:
                break;
            case BeastTamer.FURIOUS_STRIKES:
                break;
            case BeastTamer.LIL_FORT:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case BeastTamer.MAJESTIC_TRUMPET:
                break;
            case BeastTamer.PAW_SWIPE:
                break;
            case BeastTamer.PAW_SWIPE_2:
                break;
            case BeastTamer.PAW_SWIPE_3:
                break;
            case BeastTamer.REALLY_DEEP_BREATH:
                break;
            case BeastTamer.TABLE_FLIP:
                break;
            case BeastTamer.WELL_FED:
                break;
            case BeastTamer.ADVANCED_THUNDER_DASH:
                break;
            case BeastTamer.BRO_ATTACK:
                break;
            case BeastTamer.DEADLY_FANGS:
                break;
            case BeastTamer.LEOPARDS_PAW:
                break;
            case BeastTamer.LEOPARDS_PAW_1:
                break;
            case BeastTamer.LEOPARDS_POUNCE:
                break;
            case BeastTamer.LEOPARDS_POUNCE_1:
                break;
            case BeastTamer.LEOPARDS_ROAR:
                break;
            case BeastTamer.LEOPARDS_ROAR_1:
                break;
            case BeastTamer.LEOPARD_HIDE:
                break;
            case BeastTamer.LEOPARD_REFLEXES:
                break;
            case BeastTamer.LETHAL_LAI:
                break;
            case BeastTamer.MACHO_DANCE:
                break;
            case BeastTamer.MACHO_INCARNATE:
                break;
            case BeastTamer.MACHO_SLAM:
                break;
            case BeastTamer.PARTY_TIME:
                break;
            case BeastTamer.RIPPLING_FELINE_MUSCLES:
                break;
            case BeastTamer.SNOW_LEOPARD_2ND_POUNCE:
                break;
            case BeastTamer.THREEPOINT_POUNCE:
                break;
            case BeastTamer.THUNDER_DASH:
                break;
            case BeastTamer.THUNDER_TRAIL:
                break;
            case BeastTamer.BLUE_CARD:
                break;
            case BeastTamer.CATS_CLAWS:
                break;
            case BeastTamer.CATS_CRADLE_BLITZKRIEG:
                break;
            case BeastTamer.CAT_WIT:
                break;
            case BeastTamer.FIRE_KITTY:
                break;
            case BeastTamer.FORMATION_ATTACK_GUARDBREAK:
                break;
            case BeastTamer.FORMATION_ATTACK_REINFORCE:
                break;
            case BeastTamer.FORMATION_ATTACK_SPREAD:
                break;
            case BeastTamer.FRIENDS_OF_ARBY:
                break;
            case BeastTamer.FRIEND_LAUNCHER:
                break;
            case BeastTamer.FRIEND_LAUNCHER_2:
                break;
            case BeastTamer.FRIEND_LAUNCHER_3:
                break;
            case BeastTamer.FRIEND_LAUNCHER_4:
                break;
            case BeastTamer.FRIEND_LAUNCHER_ENHANCE:
                break;
            case BeastTamer.FRIEND_LAUNCHER_RANGE:
                break;
            case BeastTamer.FRIEND_LAUNCHER_REINFORCE:
                break;
            case BeastTamer.FRIEND_LAUNCHER_SPREAD:
                break;
            case BeastTamer.FURIOUS_STRIKES_BOSS_RUSH:
                break;
            case BeastTamer.FURIOUS_STRIKES_CRITICAL_CHANCE:
                break;
            case BeastTamer.FURIOUS_STRIKES_REINFORCE:
                break;
            case BeastTamer.GOLD_CARD:
                break;
            case BeastTamer.GREEN_CARD:
                break;
            case BeastTamer.GROUP_BEAR_BLASTER:
                break;
            case BeastTamer.HYPER_ACCURACY_600_60_6:
                break;
            case BeastTamer.HYPER_CRITICAL_600_60_6:
                break;
            case BeastTamer.HYPER_DEFENSE_100_10_1:
                break;
            case BeastTamer.HYPER_DEXTERITY_600_60_6:
                break;
            case BeastTamer.HYPER_FURY_600_60_6:
                break;
            case BeastTamer.HYPER_HEALTH_600_60_6:
                break;
            case BeastTamer.HYPER_INTELLIGENCE_600_60_6:
                break;
            case BeastTamer.HYPER_JUMP_600_60_6:
                break;
            case BeastTamer.HYPER_LUCK_600_60_6:
                break;
            case BeastTamer.HYPER_MAGIC_DEFENSE_600_60_6:
                break;
            case BeastTamer.HYPER_MANA_600_60_6:
                break;
            case BeastTamer.HYPER_SPEED_600_60_6:
                break;
            case BeastTamer.HYPER_STRENGTH_600_60_6:
                break;
            case BeastTamer.KITTY_BATTLE_SQUAD:
                break;
            case BeastTamer.KITTY_TREATS:
                break;
            case BeastTamer.MEOW_CARD:
                break;
            case BeastTamer.MEOW_CURE:
                break;
            case BeastTamer.MEOW_GOLD_CARD:
                break;
            case BeastTamer.MEOW_HEAL:
                break;
            case BeastTamer.MEOW_REVIVE:
                break;
            case BeastTamer.MOUSERS_INSIGHT:
                break;
            case BeastTamer.PURRPOWERED:
                break;
            case BeastTamer.PURR_ZONE:
                break;
            case BeastTamer.RAPTOR_TALONS:
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, pEffect.info.get(MapleStatInfo.indieMad));
                break;
            case BeastTamer.RAZOR_BEAK:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(MapleStatInfo.speed));
                pEffect.statups.put(CharacterTemporaryStat.Jump, pEffect.info.get(MapleStatInfo.jump));
                break;
            case BeastTamer.RED_CARD:
                break;
            case BeastTamer.STICKY_PAWS:
                break;
            case BeastTamer.TEAM_ROAR:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.TeamRoar, 1);
                pEffect.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                break;
            case BeastTamer.THREEPOINT_POUNCE_EXTRA_STRIKE:
                break;
            case BeastTamer.THREEPOINT_POUNCE_REINFORCE:
                break;
            case BeastTamer.THREEPOINT_POUNCE_SPREAD:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 11000 || nClass == 11200 || nClass == 11200 || nClass == 11211 || nClass == 11212;
    }

}
