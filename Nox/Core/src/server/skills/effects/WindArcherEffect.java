package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.WindArcher;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class WindArcherEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case WindArcher.ARCHERY_MASTERY_1:
                break;
            case WindArcher.BREEZE_ARROW:
                break;
            case WindArcher.CRITICAL_SHOT_3:
                break;
            case WindArcher.DOUBLE_SHOT_5:
                break;
            case WindArcher.NATURES_BALANCE_3:
                break;
            case WindArcher.STORM:
                break;
            case WindArcher.STORM_ELEMENTAL:
                break;
            case WindArcher.WHISPERS_OF_THE_WIND:
                break;
            case WindArcher.WIND_WALK_1:
                break;
            case WindArcher.BOW_BOOSTER:
                break;
            case WindArcher.BOW_BOOSTER_1:
                break;
            case WindArcher.BOW_MASTERY:
                break;
            case WindArcher.BOW_MASTERY_1:
                break;
            case WindArcher.DOUBLE_JUMP_2:
                break;
            case WindArcher.FAIRY_SPIRAL:
                break;
            case WindArcher.FINAL_ATTACK:
                break;
            case WindArcher.GUST_SHOT:
                break;
            case WindArcher.PHYSICAL_TRAINING_4:
                break;
            case WindArcher.PHYSICAL_TRAINING_5:
                break;
            case WindArcher.SOUL_ARROW:
                break;
            case WindArcher.STORM_SPIKE:
                break;
            case WindArcher.STRAFE:
                break;
            case WindArcher.SYLVAN_AID:
                break;
            case WindArcher.TRIFLING_WIND_I:
                break;
            case WindArcher.TRIFLING_WIND_I_1:
                break;
            case WindArcher.TRIFLING_WIND_I_2:
                break;
            case WindArcher.WIND_WALK:
                break;
            case WindArcher.ALBATROSS:
                break;
            case WindArcher.ARROW_RAIN:
                break;
            case WindArcher.BOW_EXPERT_1:
                break;
            case WindArcher.CONCENTRATE:
                break;
            case WindArcher.EAGLE_EYE:
                break;
            case WindArcher.EMERALD_FLOWER:
                break;
            case WindArcher.EVASION_BOOST_1:
                break;
            case WindArcher.FEATHERWEIGHT:
                break;
            case WindArcher.HURRICANE_1:
                break;
            case WindArcher.MORTAL_BLOW_1:
                break;
            case WindArcher.PINPOINT_PIERCE:
                break;
            case WindArcher.PUPPET:
                break;
            case WindArcher.SECOND_WIND:
                break;
            case WindArcher.SENTIENT_ARROW:
                break;
            case WindArcher.TRIFLING_WIND_II:
                break;
            case WindArcher.TRIFLING_WIND_II_1:
                break;
            case WindArcher.WIND_PIERCING:
                break;
            case WindArcher.WIND_SHOT:
                break;
            case WindArcher.ALBATROSS_MAX:
                break;
            case WindArcher.BOW_EXPERT_2:
                break;
            case WindArcher.CALL_OF_CYGNUS_4:
                break;
            case WindArcher.EMERALD_DUST:
                break;
            case WindArcher.GLORY_OF_THE_GUARDIANS_3:
                break;
            case WindArcher.HYPER_ACCURACY_7000_700_70_7:
                break;
            case WindArcher.HYPER_CRITICAL_7000_700_70_7:
                break;
            case WindArcher.HYPER_DEFENSE_900_90_9:
                break;
            case WindArcher.HYPER_DEXTERITY_7000_700_70_7:
                break;
            case WindArcher.HYPER_FURY_6000_600_60_6:
                break;
            case WindArcher.HYPER_HEALTH_7000_700_70_7:
                break;
            case WindArcher.HYPER_INTELLIGENCE_7000_700_70_7:
                break;
            case WindArcher.HYPER_JUMP_6000_600_60_6:
                break;
            case WindArcher.HYPER_LUCK_7000_700_70_7:
                break;
            case WindArcher.HYPER_MAGIC_DEFENSE_6000_600_60_6:
                break;
            case WindArcher.HYPER_MANA_7000_700_70_7:
                break;
            case WindArcher.HYPER_SPEED_6000_600_60_6:
                break;
            case WindArcher.HYPER_STRENGTH_7000_700_70_7:
                break;
            case WindArcher.MONSOON:
                break;
            case WindArcher.SHARP_EYES_2:
                break;
            case WindArcher.SONG_OF_HEAVEN:
                break;
            case WindArcher.SONG_OF_HEAVEN_BOSS_RUSH:
                break;
            case WindArcher.SONG_OF_HEAVEN_GUARDBREAK:
                break;
            case WindArcher.SONG_OF_HEAVEN_REINFORCE:
                break;
            case WindArcher.SPIRALING_VORTEX:
                break;
            case WindArcher.SPIRALING_VORTEX_1:
                break;
            case WindArcher.SPIRALING_VORTEX_EXTRA_STRIKE:
                break;
            case WindArcher.SPIRALING_VORTEX_REINFORCE:
                break;
            case WindArcher.SPIRALING_VORTEX_SPREAD:
                break;
            case WindArcher.STORM_BRINGER:
                break;
            case WindArcher.TOUCH_OF_THE_WIND:
                break;
            case WindArcher.TRIFLING_WIND_DOUBLE_CHANCE:
                break;
            case WindArcher.TRIFLING_WIND_ENHANCE:
                break;
            case WindArcher.TRIFLING_WIND_III:
                break;
            case WindArcher.TRIFLING_WIND_III_1:
                break;
            case WindArcher.TRIFLING_WIND_REINFORCE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1300 || nClass == 1310 || nClass == 1311 || nClass == 1312;
    }

}
