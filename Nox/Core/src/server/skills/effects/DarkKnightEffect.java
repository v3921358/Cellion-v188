package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.DarkKnight;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class DarkKnightEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case DarkKnight.AURA_OF_THE_EVIL_EYE:
                break;
            case DarkKnight.BARRICADE_MASTERY_1:
                break;
            case DarkKnight.BERSERK:
                break;
            case DarkKnight.DARK_IMPALE:
                break;
            case DarkKnight.DARK_THIRST:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                pEffect.statups.put(CharacterTemporaryStat.Regen, pEffect.info.get(StatInfo.x));
                break;
            case DarkKnight.EPIC_ADVENTURE_10_1:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case DarkKnight.EVIL_EYE_1:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.statups.put(CharacterTemporaryStat.Beholder, pEffect.info.get(StatInfo.x));
                break;
            case DarkKnight.EVIL_EYE_AURA_REINFORCE:
                break;
            case DarkKnight.EVIL_EYE_HEX_REINFORCE:
                break;
            case DarkKnight.EVIL_EYE_REINFORCE:
                break;
            case DarkKnight.FINAL_PACT:
                break;
            case DarkKnight.FINAL_PACT_1:
                break;
            case DarkKnight.FINAL_PACT_CRITICAL_CHANCE:
                break;
            case DarkKnight.FINAL_PACT_DAMAGE:
                break;
            case DarkKnight.FINAL_PACT_REDUCE_TARGET:
                break;
            case DarkKnight.GUNGNIRS_DESCENT:
                break;
            case DarkKnight.GUNGNIRS_DESCENT_BOSS_RUSH:
                break;
            case DarkKnight.GUNGNIRS_DESCENT_GUARDBREAK:
                break;
            case DarkKnight.GUNGNIRS_DESCENT_REINFORCE:
                break;
            case DarkKnight.HEROS_WILL_100_10:
                break;
            case DarkKnight.HEX_OF_THE_EVIL_EYE:
                break;
            case DarkKnight.HYPER_ACCURACY_80_8:
                break;
            case DarkKnight.HYPER_CRITICAL_80_8:
                break;
            case DarkKnight.HYPER_DEFENSE_50_5:
                break;
            case DarkKnight.HYPER_DEXTERITY_80_8:
                break;
            case DarkKnight.HYPER_FURY_80_8:
                break;
            case DarkKnight.HYPER_HEALTH_80_8:
                break;
            case DarkKnight.HYPER_INTELLIGENCE_80_8:
                break;
            case DarkKnight.HYPER_JUMP_80_8:
                break;
            case DarkKnight.HYPER_LUCK_80_8:
                break;
            case DarkKnight.HYPER_MAGIC_DEFENSE_80_8:
                break;
            case DarkKnight.HYPER_MANA_80_8:
                break;
            case DarkKnight.HYPER_SPEED_80_8:
                break;
            case DarkKnight.HYPER_STRENGTH_80_8:
                break;
            case DarkKnight.MAGIC_CRASH_5:
                break;
            case DarkKnight.MAPLE_WARRIOR_100_10_1:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(StatInfo.x));
                break;
            case DarkKnight.MONSTER_MAGNET_2:
                break;
            case DarkKnight.NIGHTSHADE_EXPLOSION:
                break;
            case DarkKnight.POWER_STANCE_5:
                break;
            case DarkKnight.POWER_STANCE_6:
                break;
            case DarkKnight.REVENGE_OF_THE_EVIL_EYE:
                break;
            case DarkKnight.RUSH_4:
                break;
            case DarkKnight.SACRIFICE_1:
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.ignoreMobpdpR));
                pEffect.statups.put(CharacterTemporaryStat.BdR, pEffect.info.get(StatInfo.indieBDR));
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 132;
    }

}
