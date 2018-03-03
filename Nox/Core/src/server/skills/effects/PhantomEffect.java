package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Phantom;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PhantomEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Phantom.DOUBLE_ENTENDRE:
                break;
            case Phantom.FEATHER_FOOT:
                break;
            case Phantom.IMPECCABLE_MEMORY_I:
                break;
            case Phantom.PHANTOM_SWIFTNESS:
                break;
            case Phantom.CALLING_CARD:
                break;
            case Phantom.CANE_BOOSTER:
                break;
            case Phantom.CANE_MASTERY:
                break;
            case Phantom.CARTE_BLANCHE:
                break;
            case Phantom.DEVILS_LUCK:
                break;
            case Phantom.IMPECCABLE_MEMORY_II:
                break;
            case Phantom.MILLE_CARTES:
                break;
            case Phantom.BAD_LUCK_WARD:
                break;
            case Phantom.BLASON:
                break;
            case Phantom.CLAIR_DE_LUNE:
                break;
            case Phantom.FINAL_FEINT:
                break;
            case Phantom.IMPECCABLE_MEMORY_III:
                break;
            case Phantom.MIST_MASK:
                break;
            case Phantom.PIERCING_VISION:
                break;
            case Phantom.RAPIER_WIT:
                break;
            case Phantom.RAPIER_WIT_1:
                break;
            case Phantom.ARCHANGELIC_BLESSING_400_40_4:
                break;
            case Phantom.ARCHANGELIC_BLESSING_500_50_5:
                break;
            case Phantom.ARCHANGEL_100_10:
                break;
            case Phantom.ARCHANGEL_90_9:
                break;
            case Phantom.BALROG_40_4:
                break;
            case Phantom.BAMBOO_RAIN_10_1:
                break;
            case Phantom.BLACK_SCOOTER_5:
                break;
            case Phantom.BLESSING_OF_THE_FAIRY_50_5:
                break;
            case Phantom.BLUE_SCOOTER_8:
                break;
            case Phantom.CALL_OF_THE_HUNTER_5:
                break;
            case Phantom.CAPTURE_5:
                break;
            case Phantom.CHARGE_TOY_TROJAN_5:
                break;
            case Phantom.CHUMP_CHANGE:
                break;
            case Phantom.CROCO_5:
                break;
            case Phantom.CRYSTAL_THROW_5:
                break;
            case Phantom.DARK_ANGELIC_BLESSING_20_2:
                break;
            case Phantom.DARK_ANGEL_10:
                break;
            case Phantom.DEADLY_CRITS_6:
                break;
            case Phantom.DECENT_ADVANCED_BLESSING_7:
                break;
            case Phantom.DECENT_COMBAT_ORDERS_7:
                break;
            case Phantom.DECENT_HASTE_6:
                break;
            case Phantom.DECENT_HYPER_BODY_7:
                break;
            case Phantom.DECENT_MYSTIC_DOOR_7:
                break;
            case Phantom.DECENT_SHARP_EYES_7:
                break;
            case Phantom.DECENT_SPEED_INFUSION_7:
                break;
            case Phantom.DEXTEROUS_TRAINING:
                break;
            case Phantom.EMPRESSS_BLESSING_40_4:
                break;
            case Phantom.FOLLOW_THE_LEAD_20_2:
                break;
            case Phantom.FORTUNE_30_3:
                break;
            case Phantom.FREEZING_AXE_30_3:
                break;
            case Phantom.GHOSTWALK:
                break;
            case Phantom.GIANT_POTION_700_70_7:
                break;
            case Phantom.GIANT_POTION_800_80_8:
                break;
            case Phantom.GIANT_POTION_900_90_9:
                break;
            case Phantom.GODDESS_GUARD_5000_500_50_5:
                break;
            case Phantom.GODDESS_GUARD_9:
                break;
            case Phantom.HEROS_ECHO_7:
                break;
            case Phantom.HIDDEN_POTENTIAL_HERO_9:
                break;
            case Phantom.ICE_CHOP_50_5:
                break;
            case Phantom.ICE_CURSE_50_5:
                break;
            case Phantom.ICE_DOUBLE_JUMP_9:
                break;
            case Phantom.ICE_KNIGHT_60_6:
                break;
            case Phantom.ICE_SMASH_30_3:
                break;
            case Phantom.ICE_TEMPEST_50_5:
                break;
            case Phantom.INFILTRATE_5:
                break;
            case Phantom.INVINCIBILITY_20_2:
                break;
            case Phantom.JUDGMENT_DRAW_4:
                break;
            case Phantom.JUDGMENT_DRAW_5:
                break;
            case Phantom.JUDGMENT_DRAW_AUTOMANUAL:
                break;
            case Phantom.LEGENDARY_SPIRIT_20_2:
                break;
            case Phantom.LEONARDO_THE_LION_9:
                break;
            case Phantom.LINK_MANAGER_80_8:
                break;
            case Phantom.LOADOUT:
                break;
            case Phantom.MAKER_20_2:
                break;
            case Phantom.MECHANIC_DASH_5:
                break;
            case Phantom.MIST_BALROG_9:
                break;
            case Phantom.MONSTER_MOUNT:
                break;
            case Phantom.MOTORCYCLE_5:
                break;
            case Phantom.NIGHTMARE_40_4:
                break;
            case Phantom.NIMBUS_CLOUD_9:
                break;
            case Phantom.ORANGE_MUSHROOM_9:
                break;
            case Phantom.OSTRICH_8:
                break;
            case Phantom.PHANTOM_INSTINCT_1:
                break;
            case Phantom.PINK_BEAR_HOTAIR_BALLOON_9:
                break;
            case Phantom.PINK_SCOOTER_5:
                break;
            case Phantom.POTION_MASTERY_6:
                break;
            case Phantom.POWER_EXPLOSION_10_1:
                break;
            case Phantom.POWER_SUIT_8:
                break;
            case Phantom.RACE_KART_5:
                break;
            case Phantom.RAGE_OF_PHARAOH_20_2:
                break;
            case Phantom.ROLLS_3:
                break;
            case Phantom.ROYCE_3:
                break;
            case Phantom.SANTA_SLED_8:
                break;
            case Phantom.SHINJO_40_4:
                break;
            case Phantom.SHROUD_WALK:
                break;
            case Phantom.SKILL_SWIPE:
                break;
            case Phantom.SOARING_30_3:
                break;
            case Phantom.SPACESHIP_50_5:
                break;
            case Phantom.SPACE_BEAM_40_4:
                break;
            case Phantom.SPACE_DASH_40_4:
                break;
            case Phantom.TEST_6:
                break;
            case Phantom.TO_THE_SKIES:
                break;
            case Phantom.TRANSFORMED_ROBOT_5:
                break;
            case Phantom.WHITE_ANGELIC_BLESSING_50_5:
                break;
            case Phantom.WHITE_ANGEL_80_8:
                break;
            case Phantom.WILL_OF_THE_ALLIANCE_30_3:
                break;
            case Phantom.WITCHS_BROOMSTICK_100_10:
                break;
            case Phantom.WITCHS_BROOMSTICK_100_10_1:
                break;
            case Phantom.YETI_9:
                break;
            case Phantom.YETI_MOUNT_50_5:
                break;
            case Phantom.YETI_MOUNT_60_6:
                break;
            case Phantom.ZD_TIGER_5:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2400 || nClass == 2410 || nClass == 2411 || nClass == 2003;
    }

}
