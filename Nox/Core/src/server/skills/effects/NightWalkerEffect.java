package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.NightWalker;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class NightWalkerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case NightWalker.DARKNESS:
                break;
            case NightWalker.DARK_ELEMENTAL:
                break;
            case NightWalker.DARK_SIGHT:
                break;
            case NightWalker.DARK_SIGHT_1:
                break;
            case NightWalker.DISORDER:
                break;
            case NightWalker.HASTE_1:
                break;
            case NightWalker.HASTE_2:
                break;
            case NightWalker.KEEN_EYES:
                break;
            case NightWalker.LUCKY_SEVEN_1:
                break;
            case NightWalker.LUCKY_SEVEN_2:
                break;
            case NightWalker.MAGIC_THEFT:
                break;
            case NightWalker.NIMBLE_BODY:
                break;
            case NightWalker.NIMBLE_BODY_1:
                break;
            case NightWalker.SHADOW_BAT:
                break;
            case NightWalker.SHADOW_BAT_1:
                break;
            case NightWalker.SHADOW_BAT_2:
                break;
            case NightWalker.SHADOW_BAT_3:
                break;
            case NightWalker.SHADOW_DODGE:
                break;
            case NightWalker.SHADOW_JUMP:
                break;
            case NightWalker.ADAPTIVE_DARKNESS:
                break;
            case NightWalker.BAT_AFFINITY:
                break;
            case NightWalker.CLAW_BOOSTER:
                break;
            case NightWalker.CLAW_MASTERY_1:
                break;
            case NightWalker.CRITICAL_THROW_1:
                break;
            case NightWalker.CRITICAL_THROW_2:
                break;
            case NightWalker.FLASH_JUMP_1:
                break;
            case NightWalker.GUST_CHARM:
                break;
            case NightWalker.HASTE:
                break;
            case NightWalker.PHYSICAL_TRAINING_200_20_2:
                break;
            case NightWalker.PHYSICAL_TRAINING_300_30_3:
                break;
            case NightWalker.SHURIKEN_BURST:
                break;
            case NightWalker.THROWING_BOOSTER:
                break;
            case NightWalker.THROWING_MASTERY:
                break;
            case NightWalker.TRIPLE_THROW_1:
                break;
            case NightWalker.TRIPLE_THROW_2:
                break;
            case NightWalker.VAMPIRE:
                break;
            case NightWalker.VANISH:
                break;
            case NightWalker.ADAPTIVE_DARKNESS_II:
                break;
            case NightWalker.ALCHEMIC_ADRENALINE:
                break;
            case NightWalker.ALCHEMIC_ADRENALINE_1:
                break;
            case NightWalker.ALCHEMIST:
                break;
            case NightWalker.AVENGER:
                break;
            case NightWalker.BAT_AFFINITY_II:
                break;
            case NightWalker.DARKNESS_ASCENDING:
                break;
            case NightWalker.DARK_FLARE_1:
                break;
            case NightWalker.DARK_SERVANT:
                break;
            case NightWalker.ENVELOPING_DARKNESS:
                break;
            case NightWalker.ENVELOPING_DARKNESS_1:
                break;
            case NightWalker.POISON_BOMB:
                break;
            case NightWalker.QUAD_STAR_1:
                break;
            case NightWalker.QUAD_STAR_2:
                break;
            case NightWalker.QUAD_STAR_3:
                break;
            case NightWalker.SHADE_SPLITTER:
                break;
            case NightWalker.SHADOW_PARTNER_1:
                break;
            case NightWalker.SHADOW_SPARK:
                break;
            case NightWalker.SHADOW_SPARK_1:
                break;
            case NightWalker.SHADOW_STARS:
                break;
            case NightWalker.SHADOW_WEB:
                break;
            case NightWalker.SPIRIT_PROJECTION:
                break;
            case NightWalker.VENOM_1:
                break;
            case NightWalker.ADAPTIVE_DARKNESS_III:
                break;
            case NightWalker.BAT_AFFINITY_III:
                break;
            case NightWalker.CALL_OF_CYGNUS_2:
                break;
            case NightWalker.DARK_BLESSING:
                break;
            case NightWalker.DARK_OMEN:
                break;
            case NightWalker.DARK_OMEN_COOLDOWN_CUTTER:
                break;
            case NightWalker.DARK_OMEN_REINFORCE:
                break;
            case NightWalker.DARK_OMEN_SPREAD:
                break;
            case NightWalker.DOMINION:
                break;
            case NightWalker.GLORY_OF_THE_GUARDIANS_1:
                break;
            case NightWalker.HYPER_ACCURACY_10:
                break;
            case NightWalker.HYPER_CRITICAL_10:
                break;
            case NightWalker.HYPER_DEFENSE_8:
                break;
            case NightWalker.HYPER_DEXTERITY_10_1:
                break;
            case NightWalker.HYPER_FURY_10:
                break;
            case NightWalker.HYPER_HEALTH_10:
                break;
            case NightWalker.HYPER_INTELLIGENCE_10_1:
                break;
            case NightWalker.HYPER_JUMP_10:
                break;
            case NightWalker.HYPER_LUCK_10_1:
                break;
            case NightWalker.HYPER_MAGIC_DEFENSE_10:
                break;
            case NightWalker.HYPER_MANA_10:
                break;
            case NightWalker.HYPER_SPEED_10:
                break;
            case NightWalker.HYPER_STRENGTH_10_1:
                break;
            case NightWalker.QUINTUPLE_STAR:
                break;
            case NightWalker.QUINTUPLE_STAR_1:
                break;
            case NightWalker.QUINTUPLE_STAR_BOSS_RUSH:
                break;
            case NightWalker.QUINTUPLE_STAR_CRITICAL_CHANCE:
                break;
            case NightWalker.QUINTUPLE_STAR_REINFORCE:
                break;
            case NightWalker.SHADOW_ILLUSION:
                break;
            case NightWalker.SHADOW_ILLUSION_1:
                break;
            case NightWalker.SHADOW_ILLUSION_2:
                break;
            case NightWalker.SHADOW_SLIP:
                break;
            case NightWalker.SHADOW_STITCH:
                break;
            case NightWalker.THROWING_EXPERT:
                break;
            case NightWalker.VITALITY_SIPHON:
                break;
            case NightWalker.VITALITY_SIPHON_EXTRA_POINT:
                break;
            case NightWalker.VITALITY_SIPHON_PREPARATION:
                break;
            case NightWalker.VITALITY_SIPHON_STEEL_SKIN:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1400 || nClass == 1410 || nClass == 1411 || nClass == 1412;
    }

}
