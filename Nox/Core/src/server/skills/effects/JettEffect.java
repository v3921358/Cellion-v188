package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Jett;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class JettEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Jett.BOUNTY_CHASER:
                break;
            case Jett.COSMIC_SHIELD:
                break;
            case Jett.DARK_CLARITY_1:
                break;
            case Jett.GUN_BOOSTER:
                break;
            case Jett.GUN_MASTERY_1:
                break;
            case Jett.GUN_MASTERY_2:
                break;
            case Jett.PERSEVERANCE:
                break;
            case Jett.PHYSICAL_TRAINING_7:
                break;
            case Jett.STARLINE_TWO:
                break;
            case Jett.STELLAR_IMPACT:
                break;
            case Jett.TRIPLE_FIRE_2:
                break;
            case Jett.UNK_124:
                break;
            case Jett.UNK_126:
                break;
            case Jett.VOLT_BARRAGE:
                break;
            case Jett.VORTEX_CROSS:
                break;
            case Jett.BLASTER_OVERDRIVE:
                break;
            case Jett.CLAIRVOYANT:
                break;
            case Jett.COSMIC_UPHEAVAL:
                break;
            case Jett.CUTTING_EDGE:
                break;
            case Jett.DRONE_BLAST:
                break;
            case Jett.FALLING_STARS:
                break;
            case Jett.GAMMA_MISSILE:
                break;
            case Jett.HIGH_LIFE:
                break;
            case Jett.ROLL_OF_THE_DICE_3:
                break;
            case Jett.SLIPSTREAM_SUIT:
                break;
            case Jett.SOLAR_ARRAY:
                break;
            case Jett.SPACIAL_SHIFT:
                break;
            case Jett.STARLINE_THREE:
                break;
            case Jett.TURRET_DEPLOYMENT:
                break;
            case Jett.BACKUP_BEATDOWN:
                break;
            case Jett.BACKUP_BEATDOWN_EXTRA_STRIKE:
                break;
            case Jett.BACKUP_BEATDOWN_REINFORCE:
                break;
            case Jett.BACKUP_BEATDOWN_SPREAD:
                break;
            case Jett.BIONIC_MAXIMIZER:
                break;
            case Jett.BRAIN_SCRAMBLER_1:
                break;
            case Jett.COLLATERAL_DAMAGE:
                break;
            case Jett.COUNTERATTACK_1:
                break;
            case Jett.DOUBLE_DOWN_2:
                break;
            case Jett.EPIC_ADVENTURE_20_2:
                break;
            case Jett.GIGA_BLASTER:
                break;
            case Jett.HEROS_WILL_300_30_3:
                break;
            case Jett.HEROS_WILL_400_40_4:
                break;
            case Jett.HIGH_GRAVITY:
                break;
            case Jett.HYPER_ACCURACY_400_40_4:
                break;
            case Jett.HYPER_CRITICAL_400_40_4:
                break;
            case Jett.HYPER_DEFENSE_90_9:
                break;
            case Jett.HYPER_DEXTERITY_300_30_3:
                break;
            case Jett.HYPER_FURY_400_40_4:
                break;
            case Jett.HYPER_HEALTH_400_40_4:
                break;
            case Jett.HYPER_INTELLIGENCE_300_30_3:
                break;
            case Jett.HYPER_JUMP_400_40_4:
                break;
            case Jett.HYPER_LUCK_400_40_4:
                break;
            case Jett.HYPER_MAGIC_DEFENSE_400_40_4:
                break;
            case Jett.HYPER_MANA_400_40_4:
                break;
            case Jett.HYPER_SPEED_400_40_4:
                break;
            case Jett.HYPER_STRENGTH_300_30_3:
                break;
            case Jett.INVINCIBILITY_30_3:
                break;
            case Jett.MAPLE_WARRIOR_400_40_4:
                break;
            case Jett.MAXIMUM_FIREPOWER:
                break;
            case Jett.MIGHTY_METEOR:
                break;
            case Jett.PLANET_BUSTER:
                break;
            case Jett.PLANET_BUSTER_CRITICAL_CHANCE:
                break;
            case Jett.PLANET_BUSTER_EXTRA_STRIKE:
                break;
            case Jett.PLANET_BUSTER_GUARDBREAK:
                break;
            case Jett.RELENTLESS:
                break;
            case Jett.SINGULARITY_SHOCK:
                break;
            case Jett.STARFALL:
                break;
            case Jett.STARFORCE_SALVO:
                break;
            case Jett.STARFORCE_SALVO_BOSS_RUSH:
                break;
            case Jett.STARFORCE_SALVO_RANGE:
                break;
            case Jett.STARFORCE_SALVO_REINFORCE:
                break;
            case Jett.STARLINE_FOUR:
                break;
            case Jett.STRIKEFORCE_SHOWDOWN:
                break;
            case Jett.SUBORBITAL_BOMBARDIER:
                break;
            case Jett.BLASTER_BARRAGE:
                break;
            case Jett.GALACTIC_MIGHT:
                break;
            case Jett.QUICK_MOTION_1:
                break;
            case Jett.SEASONED_HUNTER:
                break;
            case Jett.SHADOW_HEART_1:
                break;
            case Jett.SPACE_WALK:
                break;
            case Jett.TORNADO_UPPERCUT:
                break;
            case Jett.TUTORIAL_SKILL:
                break;
            case Jett.TUTORIAL_SKILL_1:
                break;
            case Jett.TUTORIAL_SKILL_2:
                break;
            case Jett.UNK_92:
                break;
            case Jett.VORTEX_JUMP:
                break;
            case Jett._STARLINE_ONE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 570 || nClass == 571 || nClass == 572 || nClass == 508;
    }

}
