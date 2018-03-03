package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hero;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class HeroEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Hero.ADVANCED_COMBO:
                break;
            case Hero.ADVANCED_COMBO_ATTACK_BOSS_RUSH:
                break;
            case Hero.ADVANCED_COMBO_ATTACK_OPPORTUNITY:
                break;
            case Hero.ADVANCED_COMBO_ATTACK_REINFORCE:
                break;
            case Hero.ADVANCED_FINAL_ATTACK_2:
                break;
            case Hero.ADVANCED_FINAL_ATTACK_FEROCITY:
                break;
            case Hero.ADVANCED_FINAL_ATTACK_OPPORTUNITY:
                break;
            case Hero.ADVANCED_FINAL_ATTACK_REINFORCE:
                break;
            case Hero.COMBAT_MASTERY_1:
                break;
            case Hero.CRY_VALHALLA:
                break;
            case Hero.ENRAGE:
                break;
            case Hero.EPIC_ADVENTURE_4:
                break;
            case Hero.HEROS_WILL_9:
                break;
            case Hero.HYPER_ACCURACY_6:
                break;
            case Hero.HYPER_CRITICAL_6:
                break;
            case Hero.HYPER_DEFENSE_5:
                break;
            case Hero.HYPER_DEXTERITY_6:
                break;
            case Hero.HYPER_FURY_6:
                break;
            case Hero.HYPER_HEALTH_6:
                break;
            case Hero.HYPER_INTELLIGENCE_6:
                break;
            case Hero.HYPER_JUMP_4:
                break;
            case Hero.HYPER_LUCK_6:
                break;
            case Hero.HYPER_MAGIC_DEFENSE_4:
                break;
            case Hero.HYPER_MANA_6:
                break;
            case Hero.HYPER_SPEED_4:
                break;
            case Hero.HYPER_STRENGTH_6:
                break;
            case Hero.MAGIC_CRASH_2:
                break;
            case Hero.MAPLE_WARRIOR_10:
                break;
            case Hero.MONSTER_MAGNET_1:
                break;
            case Hero.POWER_STANCE_2:
                break;
            case Hero.POWER_STANCE_3:
                break;
            case Hero.PUNCTURE:
                break;
            case Hero.RAGING_BLOW:
                break;
            case Hero.RAGING_BLOW_1:
                break;
            case Hero.RAGING_BLOW_EXTRA_STRIKE:
                break;
            case Hero.RAGING_BLOW_REINFORCE:
                break;
            case Hero.RAGING_BLOW_SPREAD:
                break;
            case Hero.RISING_RAGE:
                break;
            case Hero.RUSH_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 112;
    }

}
