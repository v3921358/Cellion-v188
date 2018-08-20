package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Xenon;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class XenonEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Xenon.BEAM_SPLINE:
                break;
            case Xenon.CIRCUIT_SURGE:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case Xenon.GODDESS_GUARD_40_4:
                break;
            case Xenon.GODDESS_GUARD_500_50_5:
                break;
            case Xenon.MULTILATERAL_II:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.PINPOINT_SALVO:
                break;
            case Xenon.PROPULSION_BURST:
                break;
            case Xenon.RADIAL_NERVE:
                break;
            case Xenon.EFFICIENCY_STREAMLINE:
                pEffect.statups.put(CharacterTemporaryStat.IndieMMPR, pEffect.info.get(StatInfo.indieMmpR));
                pEffect.statups.put(CharacterTemporaryStat.IndieMHPR, pEffect.info.get(StatInfo.indieMhpR));
                break;
            case Xenon.ION_THRUST:
                break;
            case Xenon.MULTILATERAL_III:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.PERSPECTIVE_SHIFT:
                pEffect.info.put(StatInfo.powerCon, 6);
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.x));
                break;
            case Xenon.PERSPECTIVE_SHIFT_1:
                break;
            case Xenon.PINPOINT_SALVO_REDESIGN_A:
                break;
            case Xenon.QUICKSILVER_CONCENTRATE:
                break;
            case Xenon.QUICKSILVER_FLASH:
                break;
            case Xenon.QUICKSILVER_TAKEOFF:
                break;
            case Xenon.STRUCTURAL_INTEGRITY:
                break;
            case Xenon.XENON_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Xenon.XENON_MASTERY:
                break;
            case Xenon.AEGIS_SYSTEM:
                pEffect.statups.put(CharacterTemporaryStat.XenonAegisSystem, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Xenon.AEGIS_SYSTEM_1:
                break;
            case Xenon.COMBAT_SWITCH_AIR_WHIP:
                break;
            case Xenon.COMBAT_SWITCH_EXPLOSION:
                break;
            case Xenon.COMBAT_SWITCH_FISSION:
                break;
            case Xenon.DIAGONAL_CHASE:
                break;
            case Xenon.EMERGENCY_RESUPPLY:
                pEffect.statups.put(CharacterTemporaryStat.SurplusSupply, pEffect.info.get(StatInfo.x));
                break;
            case Xenon.GRAVITY_PILLAR:
                break;
            case Xenon.HYBRID_DEFENSES:
                pEffect.statups.put(CharacterTemporaryStat.EVAR, pEffect.info.get(StatInfo.prop));
                pEffect.statups.put(CharacterTemporaryStat.DamAbsorbShield, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.MANIFEST_PROJECTOR:
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, 1);
                break;
            case Xenon.MULTILATERAL_IV:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.PINPOINT_SALVO_REDESIGN_B:
                break;
            case Xenon.TRIANGULATION:
                break;
            case Xenon.AMARANTH_GENERATOR:
                pEffect.statups.put(CharacterTemporaryStat.SurplusSupply, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.AmaranthGenerator, pEffect.info.get(StatInfo.y));
                pEffect.info.put(StatInfo.time, 10000);
                break;
            case Xenon.BEAM_DANCE:
                break;
            case Xenon.BEAM_DANCE_BLUR:
                break;
            case Xenon.BEAM_DANCE_REINFORCE:
                break;
            case Xenon.BEAM_DANCE_SPREAD:
                break;
            case Xenon.ENTANGLING_LASH:
                break;
            case Xenon.HEROS_WILL_8:
                break;
            case Xenon.HYPER_ACCURACY_5000_500_50_5:
                break;
            case Xenon.HYPER_CRITICAL_5000_500_50_5:
                break;
            case Xenon.HYPER_DEFENSE_800_80_8:
                break;
            case Xenon.HYPER_DEXTERITY_5000_500_50_5:
                break;
            case Xenon.HYPER_FURY_5000_500_50_5:
                break;
            case Xenon.HYPER_HEALTH_5000_500_50_5:
                break;
            case Xenon.HYPER_INTELLIGENCE_5000_500_50_5:
                break;
            case Xenon.HYPER_JUMP_5000_500_50_5:
                break;
            case Xenon.HYPER_LUCK_5000_500_50_5:
                break;
            case Xenon.HYPER_MAGIC_DEFENSE_5000_500_50_5:
                break;
            case Xenon.HYPER_MANA_5000_500_50_5:
                break;
            case Xenon.HYPER_SPEED_5000_500_50_5:
                break;
            case Xenon.HYPER_STRENGTH_5000_500_50_5:
                break;
            case Xenon.HYPOGRAM_FIELD_FORCE_FIELD:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.HYPOGRAM_FIELD_PENETRATE:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.HYPOGRAM_FIELD_PERSIST:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.HYPOGRAM_FIELD_REINFORCE:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.HYPOGRAM_FIELD_SPEED:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.HYPOGRAM_FIELD_SUPPORT:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.INSTANT_SHOCK:
                break;
            case Xenon.MAPLE_WARRIOR_9:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Xenon.MECHA_PURGE_BOMBARD:
                break;
            case Xenon.MECHA_PURGE_BOMBARDMENT:
                break;
            case Xenon.MECHA_PURGE_GUARDBREAK:
                break;
            case Xenon.MECHA_PURGE_REINFORCE:
                break;
            case Xenon.MECHA_PURGE_SNIPE:
                break;
            case Xenon.MECHA_PURGE_SPREAD:
                break;
            case Xenon.MULTILATERAL_V:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.MULTILATERAL_VI:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.OFFENSIVE_MATRIX:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.y));
                break;
            case Xenon.OFFENSIVE_MATRIX_1:
                break;
            case Xenon.OOPARTS_CODE:
                pEffect.statups.put(CharacterTemporaryStat.BDR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Xenon.ORBITAL_CATACLYSM:
                break;
            case Xenon.ORBITAL_CATACLYSM_1:
                break;
            case Xenon.PINPOINT_SALVO_PERFECT_DESIGN:
                break;
            case Xenon.TEMPORAL_POD:
                break;
            case Xenon.XENON_EXPERT:
                break;
            case Xenon.ARCHANGELIC_BLESSING_2000_200_20_2:
                break;
            case Xenon.ARCHANGELIC_BLESSING_3000_300_30_3:
                break;
            case Xenon.ARCHANGEL_600_60_6:
                break;
            case Xenon.ARCHANGEL_700_70_7:
                break;
            case Xenon.BALROG_90_9:
                break;
            case Xenon.BAMBOO_RAIN_40_4:
                break;
            case Xenon.BEAM_DANCE_1:
                break;
            case Xenon.BLACK_SCOOTER_8:
                break;
            case Xenon.BLESSING_OF_THE_FAIRY_90_9:
                break;
            case Xenon.BLUE_SCOOTER_10_1:
                break;
            case Xenon.CALL_OF_THE_HUNTER_7:
                break;
            case Xenon.CAPTURE_7:
                break;
            case Xenon.CHARGE_TOY_TROJAN_8:
                break;
            case Xenon.CROCO_8:
                break;
            case Xenon.CRYSTAL_THROW_6:
                break;
            case Xenon.DARK_ANGELIC_BLESSING_60_6:
                break;
            case Xenon.DARK_ANGEL_30_3:
                break;
            case Xenon.DEADLY_CRITS_7:
                break;
            case Xenon.DECENT_ADVANCED_BLESSING_1:
                break;
            case Xenon.DECENT_COMBAT_ORDERS_1:
                break;
            case Xenon.DECENT_HASTE_1:
                break;
            case Xenon.DECENT_HYPER_BODY_1:
                break;
            case Xenon.DECENT_MYSTIC_DOOR_1:
                break;
            case Xenon.DECENT_SHARP_EYES_1:
                break;
            case Xenon.DECENT_SPEED_INFUSION_1:
                break;
            case Xenon.EMPRESSS_BLESSING_90_9:
                break;
            case Xenon.FOLLOW_THE_LEAD_50_5:
                break;
            case Xenon.FORTUNE_10_1:
                break;
            case Xenon.FREEZING_AXE_70_7:
                break;
            case Xenon.GIANT_POTION_10000_1000_100_10:
                break;
            case Xenon.GIANT_POTION_10000_1000_100_10_1:
                break;
            case Xenon.GIANT_POTION_9000_900_90_9:
                break;
            case Xenon.HEROS_ECHO_9:
                break;
            case Xenon.HIDDEN_POTENTIAL_RESISTANCE_2:
                break;
            case Xenon.HYBRID_LOGIC_1:
                break;
            case Xenon.ICE_CHOP_8:
                break;
            case Xenon.ICE_CURSE_8:
                break;
            case Xenon.ICE_DOUBLE_JUMP_30_3:
                break;
            case Xenon.ICE_KNIGHT_30_3:
                break;
            case Xenon.ICE_SMASH_70_7:
                break;
            case Xenon.ICE_TEMPEST_8:
                break;
            case Xenon.INFILTRATE_6:
                break;
            case Xenon.INVINCIBILITY_60_6:
                break;
            case Xenon.LEGENDARY_SPIRIT_60_6:
                break;
            case Xenon.LEONARDO_THE_LION_20_2:
                break;
            case Xenon.LIBERTY_BOOSTERS:
                break;
            case Xenon.LINK_MANAGER_70_7:
                break;
            case Xenon.MAKER_60_6:
                break;
            case Xenon.MECHANIC_DASH_6:
                break;
            case Xenon.MIMIC_PROTOCOL:
                break;
            case Xenon.MIST_BALROG_30_3:
                break;
            case Xenon.MODAL_SHIFT:
                break;
            case Xenon.MONSTER_RIDING_7:
                break;
            case Xenon.MOTORCYCLE_8:
                break;
            case Xenon.MULTILATERAL_I:
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.w));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.z));
                break;
            case Xenon.NIGHTMARE_80_8:
                break;
            case Xenon.NIMBUS_CLOUD_50_5:
                break;
            case Xenon.ORANGE_MUSHROOM_30_3:
                break;
            case Xenon.OSTRICH_10_1:
                break;
            case Xenon.PINK_BEAR_HOTAIR_BALLOON_20_2:
                break;
            case Xenon.PINK_SCOOTER_8:
                break;
            case Xenon.POTION_MASTERY_7:
                break;
            case Xenon.POWER_EXPLOSION_40_4:
                break;
            case Xenon.POWER_SUIT_20_2:
                break;
            case Xenon.PROMESSA_ESCAPE:
                break;
            case Xenon.RACE_KART_8:
                break;
            case Xenon.RAGE_OF_PHARAOH_60_6:
                break;
            case Xenon.SANTA_SLED_10_1:
                break;
            case Xenon.SHINJO_90_9:
                break;
            case Xenon.SOARING_70_7:
                break;
            case Xenon.SPACESHIP_90_9:
                break;
            case Xenon.SPACE_BEAM_80_8:
                break;
            case Xenon.SPACE_DASH_80_8:
                break;
            case Xenon.SUPPLY_SURPLUS:
                break;
            case Xenon.TEST_9:
                break;
            case Xenon.TRANSFORMED_ROBOT_7:
                break;
            case Xenon.WHITE_ANGELIC_BLESSING_9:
                break;
            case Xenon.WHITE_ANGEL_60_6:
                break;
            case Xenon.WILL_OF_THE_ALLIANCE_8:
                break;
            case Xenon.WITCHS_BROOMSTICK_1000_100_10:
                break;
            case Xenon.WITCHS_BROOMSTICK_800_80_8:
                break;
            case Xenon.YETI_20_2:
                break;
            case Xenon.YETI_MOUNT_200_20_2:
                break;
            case Xenon.YETI_MOUNT_300_30_3:
                break;
            case Xenon.ZD_TIGER_8:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3600 || nClass == 3610 || nClass == 3611 || nClass == 3612 || nClass == 3002;
    }

}
