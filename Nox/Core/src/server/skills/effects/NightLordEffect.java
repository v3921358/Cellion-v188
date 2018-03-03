package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.NightLord;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class NightLordEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case NightLord.BLEED_DART:
                break;
            case NightLord.CLAW_EXPERT:
                break;
            case NightLord.DARK_HARMONY:
                break;
            case NightLord.DARK_HARMONY_1:
                break;
            case NightLord.DEATH_STAR:
                break;
            case NightLord.EPIC_ADVENTURE:
                break;
            case NightLord.EXPERT_THROWING_STAR_HANDLING_1:
                break;
            case NightLord.FRAILTY_CURSE:
                break;
            case NightLord.FRAILTY_CURSE_BOSS_RUSH:
                break;
            case NightLord.FRAILTY_CURSE_ENHANCE:
                break;
            case NightLord.FRAILTY_CURSE_SLOW:
                break;
            case NightLord.HEROS_WILL_1:
                break;
            case NightLord.HYPER_ACCURACY_3000_300_30_3:
                break;
            case NightLord.HYPER_CRITICAL_3000_300_30_3:
                break;
            case NightLord.HYPER_DEFENSE_700_70_7:
                break;
            case NightLord.HYPER_DEXTERITY_3000_300_30_3:
                break;
            case NightLord.HYPER_FURY_3000_300_30_3:
                break;
            case NightLord.HYPER_HEALTH_3000_300_30_3:
                break;
            case NightLord.HYPER_INTELLIGENCE_3000_300_30_3:
                break;
            case NightLord.HYPER_JUMP_3000_300_30_3:
                break;
            case NightLord.HYPER_LUCK_3000_300_30_3:
                break;
            case NightLord.HYPER_MAGIC_DEFENSE_3000_300_30_3:
                break;
            case NightLord.HYPER_MANA_3000_300_30_3:
                break;
            case NightLord.HYPER_SPEED_3000_300_30_3:
                break;
            case NightLord.HYPER_STRENGTH_3000_300_30_3:
                break;
            case NightLord.MAPLE_WARRIOR_1:
                break;
            case NightLord.NIGHT_LORDS_MARK:
                break;
            case NightLord.NIGHT_LORDS_MARK_1:
                break;
            case NightLord.NINJA_AMBUSH:
                break;
            case NightLord.NINJA_STORM:
                break;
            case NightLord.QUAD_STAR:
                break;
            case NightLord.QUAD_STAR_BOSS_RUSH:
                break;
            case NightLord.QUAD_STAR_EXTRA_STRIKE:
                break;
            case NightLord.QUAD_STAR_REINFORCE:
                break;
            case NightLord.SHADOW_SHIFTER_1:
                break;
            case NightLord.SHOWDOWN:
                break;
            case NightLord.SHOWDOWN_ENHANCE:
                break;
            case NightLord.SHOWDOWN_REINFORCE:
                break;
            case NightLord.SHOWDOWN_SPREAD:
                break;
            case NightLord.SUDDEN_RAID:
                break;
            case NightLord.TAUNT:
                break;
            case NightLord.TOXIC_VENOM_2:
                break;
            case NightLord.TRIPLE_THROW:
                break;
            case NightLord.VENOMOUS_STAR:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 412;
    }

}
