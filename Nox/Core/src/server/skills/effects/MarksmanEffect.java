package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Marksman;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MarksmanEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Marksman.ARROW_ILLUSION:
                break;
            case Marksman.BOLT_SURPLUS:
                break;
            case Marksman.BULLSEYE_SHOT:
                pEffect.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, pEffect.info.get(MapleStatInfo.ignoreMobpdpR));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.BullsEye, 1);
                break;
            case Marksman.CROSSBOW_EXPERT_1:
                break;
            case Marksman.ELITE_PUPPET_1:
                break;
            case Marksman.EPIC_ADVENTURE_6:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case Marksman.HEROS_WILL_10_1:
                break;
            case Marksman.HIGH_SPEED_SHOT:
                break;
            case Marksman.HYPER_ACCURACY_100_10_1:
                break;
            case Marksman.HYPER_CRITICAL_100_10_1:
                break;
            case Marksman.HYPER_DEFENSE_80_8:
                break;
            case Marksman.HYPER_DEXTERITY_100_10_1:
                break;
            case Marksman.HYPER_FURY_100_10_1:
                break;
            case Marksman.HYPER_HEALTH_100_10_1:
                break;
            case Marksman.HYPER_INTELLIGENCE_100_10_1:
                break;
            case Marksman.HYPER_JUMP_100_10_1:
                break;
            case Marksman.HYPER_LUCK_100_10_1:
                break;
            case Marksman.HYPER_MAGIC_DEFENSE_100_10_1:
                break;
            case Marksman.HYPER_MANA_100_10_1:
                break;
            case Marksman.HYPER_SPEED_100_10_1:
                break;
            case Marksman.HYPER_STRENGTH_100_10_1:
                break;
            case Marksman.ILLUSION_STEP:
                pEffect.statups.put(CharacterTemporaryStat.Blind, pEffect.info.get(MapleStatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.ACC, pEffect.info.get(MapleStatInfo.x));
                break;
            case Marksman.LAST_MAN_STANDING:
                break;
            case Marksman.MAPLE_WARRIOR_20_2:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(MapleStatInfo.x));
                break;
            case Marksman.MARKSMANSHIP_2:
                break;
            case Marksman.PIERCING_ARROW:
                break;
            case Marksman.PIERCING_ARROW_1:
                break;
            case Marksman.PIERCING_ARROW_EXTRA_STRIKE:
                break;
            case Marksman.PIERCING_ARROW_REINFORCE:
                break;
            case Marksman.PIERCING_ARROW_SPREAD:
                break;
            case Marksman.SHARP_EYES:
                pEffect.statups.put(CharacterTemporaryStat.SharpEyes, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.CriticalGrowing, pEffect.info.get(MapleStatInfo.x));
                break;
            case Marksman.SHARP_EYES_CRITICAL_CHANCE_1:
                break;
            case Marksman.SHARP_EYES_GUARDBREAK_1:
                break;
            case Marksman.SHARP_EYES_PERSIST_1:
                break;
            case Marksman.SNIPE:
                break;
            case Marksman.SNIPE_BOSS_RUSH:
                break;
            case Marksman.SNIPE_COOLDOWN_CUTTER:
                break;
            case Marksman.SNIPE_REINFORCE:
                break;
            case Marksman.SPIRIT_LINK_FROSTPREY:
                break;
            case Marksman.ULTIMATE_STRAFE:
                break;
            case Marksman.VITAL_HUNTER:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 322;
    }

}
