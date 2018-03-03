package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Bowmaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BowmasterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Bowmaster.ADVANCED_FINAL_ATTACK_3:
                break;
            case Bowmaster.ARMOR_BREAK:
                break;
            case Bowmaster.ARROW_BLASTER_2:
                break;
            case Bowmaster.ARROW_STREAM:
                break;
            case Bowmaster.ARROW_STREAM_EXTRA_STRIKE:
                break;
            case Bowmaster.ARROW_STREAM_REINFORCE:
                break;
            case Bowmaster.ARROW_STREAM_SPREAD:
                break;
            case Bowmaster.BINDING_SHOT:
                break;
            case Bowmaster.BOW_EXPERT:
                break;
            case Bowmaster.BROILER_SHOT:
                break;
            case Bowmaster.CONCENTRATION_2:
                break;
            case Bowmaster.ELITE_PUPPET:
                break;
            case Bowmaster.ENCHANTED_QUIVER:
                break;
            case Bowmaster.ENCHANTED_QUIVER_1:
                break;
            case Bowmaster.EPIC_ADVENTURE_40_4:
                break;
            case Bowmaster.GRITTY_GUST:
                break;
            case Bowmaster.HEROS_WILL_800_80_8:
                break;
            case Bowmaster.HURRICANE:
                break;
            case Bowmaster.HURRICANE_3:
                break;
            case Bowmaster.HURRICANE_4:
                break;
            case Bowmaster.HURRICANE_BOSS_RUSH:
                break;
            case Bowmaster.HURRICANE_REINFORCE:
                break;
            case Bowmaster.HURRICANE_SPLIT_ATTACK:
                break;
            case Bowmaster.HYPER_ACCURACY_4:
                break;
            case Bowmaster.HYPER_CRITICAL_4:
                break;
            case Bowmaster.HYPER_DEFENSE_3:
                break;
            case Bowmaster.HYPER_DEXTERITY_4:
                break;
            case Bowmaster.HYPER_FURY_4:
                break;
            case Bowmaster.HYPER_HEALTH_4:
                break;
            case Bowmaster.HYPER_INTELLIGENCE_4:
                break;
            case Bowmaster.HYPER_JUMP_6:
                break;
            case Bowmaster.HYPER_LUCK_4:
                break;
            case Bowmaster.HYPER_MAGIC_DEFENSE_6:
                break;
            case Bowmaster.HYPER_MANA_4:
                break;
            case Bowmaster.HYPER_SPEED_6:
                break;
            case Bowmaster.HYPER_STRENGTH_4:
                break;
            case Bowmaster.ILLUSION_STEP_1:
                break;
            case Bowmaster.MAPLE_WARRIOR_800_80_8:
                break;
            case Bowmaster.MARKSMANSHIP:
                break;
            case Bowmaster.SHARP_EYES_3:
                break;
            case Bowmaster.SHARP_EYES_CRITICAL_CHANCE:
                break;
            case Bowmaster.SHARP_EYES_GUARDBREAK:
                break;
            case Bowmaster.SHARP_EYES_PERSIST:
                break;
            case Bowmaster.SPIRIT_LINK_PHOENIX:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 312;
    }

}
