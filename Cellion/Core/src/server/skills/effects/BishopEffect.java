package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Bishop;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BishopEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Bishop.ADVANCED_BLESSING:
                pEffect.statups.put(CharacterTemporaryStat.AdvancedBless, pEffect.info.get(StatInfo.x));
                break;
            case Bishop.ADVANCED_BLESSING_BOSS_RUSH:
                break;
            case Bishop.ADVANCED_BLESSING_EXTRA_POINT:
                break;
            case Bishop.ADVANCED_BLESSING_FEROCITY:
                break;
            case Bishop.ANGEL_RAY:
                break;
            case Bishop.ARCANE_AIM_2:
                break;
            case Bishop.BAHAMUT:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Bishop.BIG_BANG_2:
                break;
            case Bishop.BLESSED_HARMONY:
                break;
            case Bishop.BUFF_MASTERY_4:
                break;
            case Bishop.BUFF_MASTERY_5:
                break;
            case Bishop.EPIC_ADVENTURE_30_3:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Bishop.GENESIS:
                break;
            case Bishop.HEAVENS_DOOR:
                break;
            case Bishop.HEAVENS_DOOR_1:
                break;
            case Bishop.HEROS_WILL_900_90_9:
                break;
            case Bishop.HOLY_MAGIC_SHELL_COOLDOWN_CUTTER:
                break;
            case Bishop.HOLY_MAGIC_SHELL_EXTRA_GUARD:
                break;
            case Bishop.HOLY_MAGIC_SHELL_PERSIST:
                break;
            case Bishop.HOLY_SYMBOL_EXPERIENCE:
                break;
            case Bishop.HOLY_SYMBOL_ITEM_DROP:
                break;
            case Bishop.HOLY_SYMBOL_PREPARATION:
                break;
            case Bishop.HYPER_ACCURACY_1000_100_10:
                break;
            case Bishop.HYPER_CRITICAL_1000_100_10:
                break;
            case Bishop.HYPER_DEFENSE_500_50_5:
                break;
            case Bishop.HYPER_DEXTERITY_1000_100_10:
                break;
            case Bishop.HYPER_FURY_1000_100_10:
                break;
            case Bishop.HYPER_HEALTH_1000_100_10:
                break;
            case Bishop.HYPER_INTELLIGENCE_1000_100_10:
                break;
            case Bishop.HYPER_JUMP_800_80_8:
                break;
            case Bishop.HYPER_LUCK_1000_100_10:
                break;
            case Bishop.HYPER_MAGIC_DEFENSE_800_80_8:
                break;
            case Bishop.HYPER_MANA_1000_100_10:
                break;
            case Bishop.HYPER_SPEED_800_80_8:
                break;
            case Bishop.HYPER_STRENGTH_1000_100_10:
                break;
            case Bishop.INFINITY_2:
                pEffect.setHpR(pEffect.info.get(StatInfo.y) / 100.0);
                pEffect.setMpR(pEffect.info.get(StatInfo.y) / 100.0);
                pEffect.statups.put(CharacterTemporaryStat.Infinity, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(StatInfo.prop));
                break;
            case Bishop.MANA_REFLECTION:
                break;
            case Bishop.MAPLE_WARRIOR_1000_100_10:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Bishop.RESURRECTION_2:
                break;
            case Bishop.RIGHTEOUSLY_INDIGNANT:
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.ignoreMobpdpR));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(StatInfo.indieBooster));
                pEffect.statups.put(CharacterTemporaryStat.MAD, pEffect.info.get(StatInfo.indieMad));
                // pEffect.statups.put(CharacterTemporaryStat.VengeanceOfAngel, 1);
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 232;
    }

}
