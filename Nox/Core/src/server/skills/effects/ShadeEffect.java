package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Shade;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ShadeEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Shade.COSMIC_BALANCE:
                break;
            case Shade.FLASH_FIST_1:
                break;
            case Shade.FLASH_FIST_3:
                break;
            case Shade.SWIFT_STRIKE:
                break;
            case Shade.SWIFT_STRIKE_1:
                break;
            case Shade.VULPES_LEAP:
                break;
            case Shade.BACK_STEP:
                break;
            case Shade.BLADE_IMP_DOWNWARD_SLASH_1:
                break;
            case Shade.BLADE_IMP_FORWARD_SLASH:
                break;
            case Shade.FOX_SPIRITS:
                pEffect.statups.put(CharacterTemporaryStat.HiddenPossession, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Shade.FOX_SPIRIT_MASTERY:
                break;
            case Shade.FOX_SPIRIT_MASTERY_1:
                break;
            case Shade.GROUND_POUND:
                break;
            case Shade.GROUND_POUND_1:
                break;
            case Shade.GROUND_POUND_SHOCKWAVE:
                break;
            case Shade.KNUCKLE_MASTERY_2:
                break;
            case Shade.SPIRIT_BOND_2:
                break;
            case Shade.STRENGTH_TRAINING:
                break;
            case Shade.BLADE_IMP_SPIN_SLASH:
                break;
            case Shade.HARMONIOUS_DEFENSE:
                break;
            case Shade.SHOCKWAVE_PUNCH:
                break;
            case Shade.SHOCKWAVE_PUNCH_1:
                break;
            case Shade.SHOCKWAVE_PUNCH_2:
                break;
            case Shade.SHOCKWAVE_PUNCH_3:
                break;
            case Shade.SPIRIT_BOND_3:
                break;
            case Shade.SPIRIT_FRENZY:
                break;
            case Shade.SPIRIT_FRENZY_1:
                break;
            case Shade.SPIRIT_REDEMPTION:
                break;
            case Shade.SPIRIT_TRAP:
                break;
            case Shade.SUMMON_OTHER_SPIRIT:
                break;
            case Shade.WEAKEN:
                break;
            case Shade.BOMB_PUNCH:
                break;
            case Shade.BOMB_PUNCH_100_10:
                break;
            case Shade.BOMB_PUNCH_100_10_1:
                break;
            case Shade.BOMB_PUNCH_200_20_2:
                break;
            case Shade.BOMB_PUNCH_CRITICAL_CHANCE:
                break;
            case Shade.BOMB_PUNCH_REINFORCE:
                break;
            case Shade.BOMB_PUNCH_SPREAD:
                break;
            case Shade.CRITICAL_INSIGHT:
                break;
            case Shade.DEATH_MARK:
                break;
            case Shade.FIRE_FOX_SPIRITSREPEATED_ATTACK_BONUS:
                break;
            case Shade.FIRE_FOX_SPIRITSSUMMON_CHANCE:
                break;
            case Shade.FIRE_FOX_SPIRITS_REINFORCE:
                break;
            case Shade.FIRE_FOX_SPIRIT_MASTERY:
                break;
            case Shade.FIRE_FOX_SPIRIT_MASTERY_1:
                break;
            case Shade.HEROIC_MEMORIES_2:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Shade.HEROS_WILL_4:
                break;
            case Shade.HIGH_QUALITY_KNUCKLE_MASTERY:
                break;
            case Shade.HYPER_ACCURACY_800_80_8:
                break;
            case Shade.HYPER_CRITICAL_800_80_8:
                break;
            case Shade.HYPER_DEFENSE_300_30_3:
                break;
            case Shade.HYPER_DEXTERITY_800_80_8:
                break;
            case Shade.HYPER_FURY_800_80_8:
                break;
            case Shade.HYPER_HEALTH_800_80_8:
                break;
            case Shade.HYPER_INTELLIGENCE_800_80_8:
                break;
            case Shade.HYPER_JUMP_900_90_9:
                break;
            case Shade.HYPER_LUCK_800_80_8:
                break;
            case Shade.HYPER_MAGIC_DEFENSE_900_90_9:
                break;
            case Shade.HYPER_MANA_800_80_8:
                break;
            case Shade.HYPER_SPEED_900_90_9:
                break;
            case Shade.HYPER_STRENGTH_800_80_8:
                break;
            case Shade.MAPLE_WARRIOR_7:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Shade.SOUL_SPLITTER:
                break;
            case Shade.SPIRIT_BOND_4:
                break;
            case Shade.SPIRIT_BOND_MAX:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.indiePad));
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(StatInfo.indieBooster));
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.x));
                break;
            case Shade.SPIRIT_BOND_MAX_1:
                break;
            case Shade.SPIRIT_CLAW:
                break;
            case Shade.SPIRIT_CLAW_BOSS_RUSH:
                break;
            case Shade.SPIRIT_CLAW_EXTRA_STRIKE:
                break;
            case Shade.SPIRIT_CLAW_REINFORCE:
                break;
            case Shade.SPIRIT_INCARNATION:
                break;
            case Shade.SPIRIT_INCARNATION_1:
                break;
            case Shade.SPIRIT_WARD:
                pEffect.statups.put(CharacterTemporaryStat.SpiritGuard, pEffect.info.get(StatInfo.x));
                break;
            case Shade.ARCHANGELIC_BLESSING_60_6:
                break;
            case Shade.ARCHANGELIC_BLESSING_70_7:
                break;
            case Shade.ARCHANGEL_4:
                break;
            case Shade.ARCHANGEL_5:
                break;
            case Shade.BAMBOO_RAIN_5:
                break;
            case Shade.BLESSING_OF_THE_FAIRY_20_2:
                break;
            case Shade.CLOSE_CALL:
                break;
            case Shade.DARK_ANGELIC_BLESSING_8:
                break;
            case Shade.DARK_ANGEL_2:
                break;
            case Shade.DECENT_ADVANCED_BLESSING:
                break;
            case Shade.DECENT_COMBAT_ORDERS:
                break;
            case Shade.DECENT_HASTE:
                break;
            case Shade.DECENT_HYPER_BODY:
                break;
            case Shade.DECENT_MYSTIC_DOOR:
                break;
            case Shade.DECENT_SHARP_EYES:
                break;
            case Shade.DECENT_SPEED_INFUSION:
                break;
            case Shade.EMPRESSS_BLESSING_9:
                break;
            case Shade.FOLLOW_THE_LEAD_5:
                break;
            case Shade.FOX_TROT:
                break;
            case Shade.FREEZING_AXE_9:
                break;
            case Shade.GIANT_POTION_6:
                break;
            case Shade.GIANT_POTION_7:
                break;
            case Shade.GIANT_POTION_8:
                break;
            case Shade.HEROS_ECHO_2:
                break;
            case Shade.HIDDEN_POTENTIAL_HERO_7:
                break;
            case Shade.ICE_CHOP_9:
                break;
            case Shade.ICE_CURSE_9:
                break;
            case Shade.ICE_DOUBLE_JUMP_2:
                break;
            case Shade.ICE_KNIGHT_5:
                break;
            case Shade.ICE_SMASH_9:
                break;
            case Shade.ICE_TEMPEST_9:
                break;
            case Shade.INVINCIBILITY_6:
                break;
            case Shade.LEGENDARY_SPIRIT_5:
                break;
            case Shade.LINK_MANAGER_30_3:
                break;
            case Shade.MAKER_5:
                break;
            case Shade.MASTER_OF_ORGANIZATION_4:
                break;
            case Shade.MASTER_OF_ORGANIZATION_5:
                break;
            case Shade.MASTER_OF_SWIMMING_2:
                break;
            case Shade.PIGS_WEAKNESS_8:
                break;
            case Shade.PIRATE_BLESSING_3:
                break;
            case Shade.POWER_EXPLOSION_5:
                break;
            case Shade.RAGE_OF_PHARAOH_5:
                break;
            case Shade.SLIMES_WEAKNESS_7:
                break;
            case Shade.SOARING_6:
                break;
            case Shade.SPACESHIP_6:
                break;
            case Shade.SPACE_BEAM_5:
                break;
            case Shade.SPACE_DASH_5:
                break;
            case Shade.SPIRIT_BOND_1:
                break;
            case Shade.STUMPS_WEAKNESS_8:
                break;
            case Shade.VISITOR_MELEE_ATTACK_2:
                break;
            case Shade.VISITOR_RANGE_ATTACK_2:
                break;
            case Shade.WHITE_ANGELIC_BLESSING_8:
                break;
            case Shade.WHITE_ANGEL_40_4:
                break;
            case Shade.WILL_OF_THE_ALLIANCE_9:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 2500 || nClass == 2510 || nClass == 2511 || nClass == 2512 || nClass == 2005;
    }

}
