package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.DemonSlayer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DemonSlayerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DemonSlayer.BATTLE_PACT_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.DEMON_LASH:
                break;
            case DemonSlayer.DEMON_LASH_1:
                break;
            case DemonSlayer.DEMON_LASH_2:
                break;
            case DemonSlayer.DEMON_LASH_3:
                break;
            case DemonSlayer.GRIM_SCYTHE:
                break;
            case DemonSlayer.GUARDIAN_ARMOR_3:
                break;
            case DemonSlayer.HP_BOOST_7:
                break;
            case DemonSlayer.SHADOW_SWIFTNESS:
                break;
            case DemonSlayer.BARBED_LASH:
                break;
            case DemonSlayer.CHAOS_LOCK:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case DemonSlayer.DARK_THRUST:
                break;
            case DemonSlayer.OUTRAGE:
                break;
            case DemonSlayer.PHYSICAL_TRAINING_10:
                break;
            case DemonSlayer.SOUL_EATER:
                break;
            case DemonSlayer.VENGEANCE:
                pEffect.statups.put(CharacterTemporaryStat.PowerGuard, pEffect.info.get(MapleStatInfo.y));
                break;
            case DemonSlayer.WEAPON_MASTERY_1:
                break;
            case DemonSlayer.BLACKHEARTED_STRENGTH:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(MapleStatInfo.z));
                pEffect.statups.put(CharacterTemporaryStat.IndieMADR, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieBDR, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.BLACKHEARTED_STRENGTH_1:
                break;
            case DemonSlayer.CARRION_BREATH_1:
                break;
            case DemonSlayer.DEMON_LASH_ARCH:
                break;
            case DemonSlayer.FOCUSED_FURY:
                break;
            case DemonSlayer.INSULT_TO_INJURY:
                break;
            case DemonSlayer.JUDGMENT:
                break;
            case DemonSlayer.MAX_FURY:
                break;
            case DemonSlayer.POSSESSED_AEGIS:
                break;
            case DemonSlayer.RAVEN_STORM:
                break;
            case DemonSlayer.VORTEX_OF_DOOM:
                break;
            case DemonSlayer.BARRICADE_MASTERY:
                break;
            case DemonSlayer.BINDING_DARKNESS:
                break;
            case DemonSlayer.BLUE_BLOOD:
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.BOUNDLESS_RAGE:
                pEffect.statups.put(CharacterTemporaryStat.InfinityForce, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.CERBERUS_CHOMP:
                break;
            case DemonSlayer.DARK_METAMORPHOSIS:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.damR));
                pEffect.statups.put(CharacterTemporaryStat.IndieMHPR, pEffect.info.get(MapleStatInfo.indieMhpR));
                pEffect.statups.put(CharacterTemporaryStat.PowerGuard, pEffect.info.get(MapleStatInfo.damage));
                break;
            case DemonSlayer.DARK_METAMORPHOSIS_ENHANCE:
                break;
            case DemonSlayer.DARK_METAMORPHOSIS_REDUCED_FURY:
                break;
            case DemonSlayer.DARK_METAMORPHOSIS_REINFORCE:
                break;
            case DemonSlayer.DEMONIC_FORTITUDE_1:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case DemonSlayer.DEMON_CRY:
                pEffect.monsterStatus.put(MonsterStatus.SHOWDOWN, pEffect.info.get(MapleStatInfo.w));
                pEffect.monsterStatus.put(MonsterStatus.MDD, pEffect.info.get(MapleStatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.PDD, pEffect.info.get(MapleStatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.MATK, pEffect.info.get(MapleStatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.WATK, pEffect.info.get(MapleStatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.ACC, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.DEMON_IMPACT:
                break;
            case DemonSlayer.DEMON_IMPACT_EXTRA_STRIKE:
                break;
            case DemonSlayer.DEMON_IMPACT_REDUCE_FURY:
                break;
            case DemonSlayer.DEMON_IMPACT_REINFORCE:
                break;
            case DemonSlayer.DEMON_LASH_FURY_:
                break;
            case DemonSlayer.DEMON_LASH_REINFORCE:
                break;
            case DemonSlayer.DEMON_LASH_REINFORCE_DURATION:
                break;
            case DemonSlayer.DEMON_THRASH:
                break;
            case DemonSlayer.HYPER_ACCURACY_8:
                break;
            case DemonSlayer.HYPER_CRITICAL_8:
                break;
            case DemonSlayer.HYPER_DEFENSE_6:
                break;
            case DemonSlayer.HYPER_DEXTERITY_8:
                break;
            case DemonSlayer.HYPER_FURY_8:
                break;
            case DemonSlayer.HYPER_HEALTH_8:
                break;
            case DemonSlayer.HYPER_INTELLIGENCE_8:
                break;
            case DemonSlayer.HYPER_JUMP_8:
                break;
            case DemonSlayer.HYPER_LUCK_8:
                break;
            case DemonSlayer.HYPER_MAGIC_DEFENSE_8:
                break;
            case DemonSlayer.HYPER_MANA_8:
                break;
            case DemonSlayer.HYPER_SPEED_8:
                break;
            case DemonSlayer.HYPER_STRENGTH_8:
                break;
            case DemonSlayer.INFERNAL_CONCUSSION:
                break;
            case DemonSlayer.INFERNAL_CONCUSSION_1:
                break;
            case DemonSlayer.LEECH_AURA:
                break;
            case DemonSlayer.MAPLE_WARRIOR_900_90_9:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.OBSIDIAN_SKIN:
                break;
            case DemonSlayer.ARCHANGELIC_BLESSING_4:
                break;
            case DemonSlayer.ARCHANGELIC_BLESSING_5:
                break;
            case DemonSlayer.ARCHANGEL_8:
                break;
            case DemonSlayer.ARCHANGEL_9:
                break;
            case DemonSlayer.BALROG_8:
                break;
            case DemonSlayer.BAMBOO_RAIN_7:
                break;
            case DemonSlayer.BLACK_SCOOTER_3:
                break;
            case DemonSlayer.BLESSING_OF_THE_FAIRY_3:
                break;
            case DemonSlayer.BLOOD_PACT:
                break;
            case DemonSlayer.BLUE_SCOOTER_2:
                break;
            case DemonSlayer.CALL_OF_THE_HUNTER_2:
                break;
            case DemonSlayer.CAPTURE_2:
                break;
            case DemonSlayer.CHARGE_TOY_TROJAN_3:
                break;
            case DemonSlayer.CROCO_3:
                break;
            case DemonSlayer.CRYSTAL_THROW_2:
                break;
            case DemonSlayer.CURSE_OF_FURY:
                break;
            case DemonSlayer.DARK_ANGELIC_BLESSING_2:
                break;
            case DemonSlayer.DARK_ANGEL_4:
                break;
            case DemonSlayer.DARK_WINDS:
                break;
            case DemonSlayer.DEADLY_CRITS:
                break;
            case DemonSlayer.DECENT_ADVANCED_BLESSING_60_6:
                break;
            case DemonSlayer.DECENT_COMBAT_ORDERS_60_6:
                break;
            case DemonSlayer.DECENT_HASTE_50_5:
                break;
            case DemonSlayer.DECENT_HYPER_BODY_60_6:
                break;
            case DemonSlayer.DECENT_MYSTIC_DOOR_60_6:
                break;
            case DemonSlayer.DECENT_SHARP_EYES_60_6:
                break;
            case DemonSlayer.DECENT_SPEED_INFUSION_60_6:
                break;
            case DemonSlayer.DEMONIC_BLOOD:
                break;
            case DemonSlayer.DEMON_WINGS:
                break;
            case DemonSlayer.DEMON_WINGS_1:
                break;
            case DemonSlayer.EMPRESSS_BLESSING_2:
                break;
            case DemonSlayer.EXCEED:
                break;
            case DemonSlayer.FOLLOW_THE_LEAD_8:
                break;
            case DemonSlayer.FORTUNE_7:
                break;
            case DemonSlayer.FREEZING_AXE_3:
                break;
            case DemonSlayer.FURY_UNLEASHED_1:
                break;
            case DemonSlayer.GIANT_POTION_20_2:
                break;
            case DemonSlayer.GIANT_POTION_30_3:
                break;
            case DemonSlayer.GIANT_POTION_40_4:
                break;
            case DemonSlayer.GODDESS_GUARD_2:
                break;
            case DemonSlayer.GODDESS_GUARD_20_2:
                break;
            case DemonSlayer.HEROS_ECHO_3:
                break;
            case DemonSlayer.HIDDEN_POTENTIAL_RESISTANCE:
                break;
            case DemonSlayer.HYPER_POTION_MASTERY:
                break;
            case DemonSlayer.ICE_CHOP_4:
                break;
            case DemonSlayer.ICE_CURSE_4:
                break;
            case DemonSlayer.ICE_DOUBLE_JUMP_4:
                break;
            case DemonSlayer.ICE_KNIGHT_9:
                break;
            case DemonSlayer.ICE_SMASH_3:
                break;
            case DemonSlayer.ICE_TEMPEST_4:
                break;
            case DemonSlayer.INFILTRATE_2:
                break;
            case DemonSlayer.INVINCIBILITY_8:
                break;
            case DemonSlayer.LEGENDARY_SPIRIT_8:
                break;
            case DemonSlayer.LEONARDO_THE_LION_4:
                break;
            case DemonSlayer.LINK_MANAGER:
                break;
            case DemonSlayer.MAKER_8:
                break;
            case DemonSlayer.MECHANIC_DASH_2:
                break;
            case DemonSlayer.MIST_BALROG_5:
                break;
            case DemonSlayer.MONSTER_RIDING_3:
                break;
            case DemonSlayer.MOTORCYCLE_3:
                break;
            case DemonSlayer.NIGHTMARE_8:
                break;
            case DemonSlayer.NIMBUS_CLOUD_5:
                break;
            case DemonSlayer.ORANGE_MUSHROOM_5:
                break;
            case DemonSlayer.OSTRICH_4:
                break;
            case DemonSlayer.PINK_BEAR_HOTAIR_BALLOON_5:
                break;
            case DemonSlayer.PINK_SCOOTER_3:
                break;
            case DemonSlayer.POTION_MASTERY_1:
                break;
            case DemonSlayer.POWER_EXPLOSION_7:
                break;
            case DemonSlayer.POWER_SUIT_4:
                break;
            case DemonSlayer.RACE_KART_3:
                break;
            case DemonSlayer.RAGE_OF_PHARAOH_8:
                break;
            case DemonSlayer.SANTA_SLED_2:
                break;
            case DemonSlayer.SHINJO_8:
                break;
            case DemonSlayer.SOARING_9:
                break;
            case DemonSlayer.SPACESHIP_10:
                break;
            case DemonSlayer.SPACE_BEAM_9:
                break;
            case DemonSlayer.SPACE_DASH_9:
                break;
            case DemonSlayer.STAR_FORCE_CONVERSION:
                break;
            case DemonSlayer.TEST_2:
                break;
            case DemonSlayer.TRANSFORMED_ROBOT_3:
                break;
            case DemonSlayer.WHITE_ANGELIC_BLESSING_3:
                break;
            case DemonSlayer.WHITE_ANGEL_10:
                break;
            case DemonSlayer.WILD_RAGE_1:
                break;
            case DemonSlayer.WILL_OF_THE_ALLIANCE_3:
                break;
            case DemonSlayer.WITCHS_BROOMSTICK_10:
                break;
            case DemonSlayer.WITCHS_BROOMSTICK_9:
                break;
            case DemonSlayer.YETI_5:
                break;
            case DemonSlayer.YETI_MOUNT_8:
                break;
            case DemonSlayer.YETI_MOUNT_9:
                break;
            case DemonSlayer.ZD_TIGER_3:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3100 || nClass == 3110 || nClass == 3111 || nClass == 3112 || nClass == 3001;
    }

}
