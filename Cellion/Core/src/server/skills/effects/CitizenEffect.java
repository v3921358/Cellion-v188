package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Citizen;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CitizenEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Citizen.SIXTH_PARTY_TONIGHT_8:
                break;
            case Citizen.SIXTH_PARTY_TONIGHT_9:
                break;
            case Citizen.ANIS_JUDGMENT_5:
                break;
            case Citizen.ARCHANGELIC_BLESSING_200_20_2:
                break;
            case Citizen.ARCHANGELIC_BLESSING_300_30_3:
                break;
            case Citizen.ARCHANGEL_100_10_1:
                break;
            case Citizen.ARCHANGEL_80_8:
                break;
            case Citizen.BALROGS_HELLFIRE_5:
                break;
            case Citizen.BALROG_20_2:
                break;
            case Citizen.BALROG_50_5:
                break;
            case Citizen.BAMBOO_RAIN_10:
                break;
            case Citizen.BLESSING_OF_THE_FAIRY_30_3:
                break;
            case Citizen.BLUE_SCOOTER_7:
                break;
            case Citizen.BUFFALO_5:
                break;
            case Citizen.CALL_OF_THE_HUNTER_4:
                break;
            case Citizen.CAPTURE_4:
                break;
            case Citizen.CHICKEN_4:
                break;
            case Citizen.CLOUD_5:
                break;
            case Citizen.CROKING_2:
                break;
            case Citizen.CRYSTAL_THROW_4:
                break;
            case Citizen.DARK_ANGELIC_BLESSING_10_1:
                break;
            case Citizen.DARK_ANGEL_9:
                break;
            case Citizen.DEADLY_CRITS_5:
                break;
            case Citizen.DECENT_ADVANCED_BLESSING_80_8:
                break;
            case Citizen.DECENT_COMBAT_ORDERS_80_8:
                break;
            case Citizen.DECENT_HASTE_70_7:
                break;
            case Citizen.DECENT_HYPER_BODY_80_8:
                break;
            case Citizen.DECENT_MYSTIC_DOOR_80_8:
                break;
            case Citizen.DECENT_SHARP_EYES_80_8:
                break;
            case Citizen.DECENT_SPEED_INFUSION_80_8:
                break;
            case Citizen.DRAGON_5:
                break;
            case Citizen.DRAGON_LEVEL_8:
                break;
            case Citizen.DRAGON_RIDERS_ENERGY_BREATH_5:
                break;
            case Citizen.EMPRESSS_BLESSING_30_3:
                break;
            case Citizen.F1_MACHINE_2:
                break;
            case Citizen.FOLLOW_THE_LEAD_10:
                break;
            case Citizen.FORTUNE_9:
                break;
            case Citizen.FREEZING_AXE_20_2:
                break;
            case Citizen.FROG_6:
                break;
            case Citizen.GARGOYLE_3:
                break;
            case Citizen.GIANT_POTION_1000_100_10:
                break;
            case Citizen.GIANT_POTION_1000_100_10_1:
                break;
            case Citizen.GIANT_POTION_2000_200_20_2:
                break;
            case Citizen.GIANT_RABBIT_5:
                break;
            case Citizen.GODDESS_GUARD_600_60_6:
                break;
            case Citizen.GODDESS_GUARD_80_8:
                break;
            case Citizen.HELICOPTER_7:
                break;
            case Citizen.HEROS_ECHO_6:
                break;
            case Citizen.HIDDEN_PEACE:
                break;
            case Citizen.HIDDEN_POTENTIAL_RESISTANCE_1:
                break;
            case Citizen.HIGHWAY_PATROL_CAR_5:
                break;
            case Citizen.HORNTAILS_FLAME_BREATH_5:
                break;
            case Citizen.HOTAIR_BALLOON_5:
                break;
            case Citizen.ICE_CHOP_6:
                break;
            case Citizen.ICE_CURSE_6:
                break;
            case Citizen.ICE_DOUBLE_JUMP_10:
                break;
            case Citizen.ICE_KNIGHT_10_1:
                break;
            case Citizen.ICE_SMASH_20_2:
                break;
            case Citizen.ICE_TEMPEST_6:
                break;
            case Citizen.INFILTRATE_4:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(StatInfo.speed));
                pEffect.statups.put(CharacterTemporaryStat.Sneak, pEffect.info.get(StatInfo.x));
                break;
            case Citizen.INVINCIBILITY_10_1:
                break;
            case Citizen.INVISIBLE_BALROG_5:
                break;
            case Citizen.JR_TANK_5:
                break;
            case Citizen.KNIGHTS_CHARIOT_5:
                break;
            case Citizen.KURENAI_RUN_AWAY_2:
                break;
            case Citizen.LAW_OFFICER_5:
                break;
            case Citizen.LEGENDARY_SPIRIT_10_1:
                break;
            case Citizen.LEONARDO_THE_LION_8:
                break;
            case Citizen.LINK_MANAGER_40_4:
                break;
            case Citizen.LION_3:
                break;
            case Citizen.LOVELY_SCOOTER_2:
                break;
            case Citizen.LOW_RIDER_10:
                break;
            case Citizen.LOW_RIDER_8:
                break;
            case Citizen.MAGICAL_WOODEN_HORSE_2:
                break;
            case Citizen.MAGIC_BROOM_5:
                break;
            case Citizen.MAKER_10_1:
                break;
            case Citizen.MECHANIC_DASH_4:
                break;
            case Citizen.MIST_BALROG_8:
                break;
            case Citizen.MONSTER_RIDING_5:
                break;
            case Citizen.MOTHERSHIP_3:
                break;
            case Citizen.MU_GONGS_ABSOLUTE_DESTRUCTION_5:
                break;
            case Citizen.NADESHIKO_FLY_HIGH_5:
                break;
            case Citizen.NAPOLEAN_MOUNT_5:
                break;
            case Citizen.NIGHTMARE_20_2:
                break;
            case Citizen.NIGHTMARE_50_5:
                break;
            case Citizen.NIMBUS_CLOUD_8:
                break;
            case Citizen.NINAS_PENTACLE_5:
                break;
            case Citizen.ORANGE_MUSHROOM_8:
                break;
            case Citizen.OS3A_MACHINE_3:
                break;
            case Citizen.OS4_SHUTTLE_4:
                break;
            case Citizen.OSTRICH_7:
                break;
            case Citizen.OWL_6:
                break;
            case Citizen.OWL__3:
                break;
            case Citizen.PACHINKO_ROBO_4:
                break;
            case Citizen.PEGASUS_5:
                break;
            case Citizen.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN_5:
                break;
            case Citizen.PINK_BEAR_HOTAIR_BALLOON_8:
                break;
            case Citizen.POTION_MASTERY_5:
                break;
            case Citizen.POWER_EXPLOSION_10:
                break;
            case Citizen.POWER_SUIT_7:
                break;
            case Citizen.RABBIT_RICKSHAW_6:
                break;
            case Citizen.RAGE_OF_PHARAOH_10_1:
                break;
            case Citizen.RED_DRACO_6:
                break;
            case Citizen.RED_TRUCK_3:
                break;
            case Citizen.RETRO_SCOOTER_2:
                break;
            case Citizen.REXS_CHARGE_5:
                break;
            case Citizen.REXS_HYENA_6:
                break;
            case Citizen.SANTA_SLED_7:
                break;
            case Citizen.SECRET_ASSEMBLY:
                break;
            case Citizen.SHINJO_20_2:
                break;
            case Citizen.SHINJO_50_5:
                break;
            case Citizen.SMALL_RABBIT_5:
                break;
            case Citizen.SOARING_20_2:
                break;
            case Citizen.SOARING_MOUNT_4:
                break;
            case Citizen.SPACESHIP_40_4:
                break;
            case Citizen.SPACE_BEAM_30_3:
                break;
            case Citizen.SPACE_DASH_30_3:
                break;
            case Citizen.SPIRIT_OF_FREEDOM_BOWMAN:
                break;
            case Citizen.SPIRIT_OF_FREEDOM_MAGICIAN:
                break;
            case Citizen.SPIRIT_OF_FREEDOM_PIRATE:
                break;
            case Citizen.SPIRIT_OF_FREEDOM_WARRIOR:
                break;
            case Citizen.SPIRIT_OF_ROCKS_DOOM_STRIKE_5:
                break;
            case Citizen.SPIRIT_VIKING_5:
                break;
            case Citizen.TEST_5:
                break;
            case Citizen.TIGER_2:
                break;
            case Citizen.TRANSFORMER_2:
                break;
            case Citizen.TURTLE_6:
                break;
            case Citizen.UNICORN_3:
                break;
            case Citizen.VISITOR_MELEE_ATTACK_5:
                break;
            case Citizen.VISITOR_RANGE_ATTACK_5:
                break;
            case Citizen.VON_LEONS_LION_SLASH_5:
                break;
            case Citizen.WHITE_ANGELIC_BLESSING_6:
                break;
            case Citizen.WHITE_ANGEL_20_2:
                break;
            case Citizen.WILL_OF_THE_ALLIANCE_6:
                break;
            case Citizen.WITCHS_BROOMSTICK_70_7:
                break;
            case Citizen.WITCHS_BROOMSTICK_80_8:
                break;
            case Citizen.YETI_8:
                break;
            case Citizen.YETI_MOUNT_30_3:
                break;
            case Citizen.YETI_MOUNT_40_4:
                break;
            case Citizen.ZAKUMS_TOWERING_INFERNO_5:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3000;
    }

}
