package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Shadower;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ShadowerEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Shadower.ASSASSINATE:
                break;
            case Shadower.ASSASSINATE_1:
                break;
            case Shadower.ASSASSINATE_2:
                break;
            case Shadower.ASSASSINATE_BOSS_RUSH:
                break;
            case Shadower.ASSASSINATE_GUARDBREAK:
                break;
            case Shadower.ASSASSINATE_REINFORCE:
                break;
            case Shadower.BOOMERANG_STAB:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Shadower.BOOMERANG_STAB_EXTRA_STRIKE:
                break;
            case Shadower.BOOMERANG_STAB_REINFORCE:
                break;
            case Shadower.BOOMERANG_STAB_SPREAD:
                break;
            case Shadower.DAGGER_EXPERT:
                break;
            case Shadower.EPIC_ADVENTURE_10:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Shadower.FLIP_OF_THE_COIN:
                // Handled in Special Attack Move.
                /*pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(MapleStatInfo.x));*/
                break;
            case Shadower.HEROS_WILL_700_70_7:
                break;
            case Shadower.HYPER_ACCURACY_700_70_7:
                break;
            case Shadower.HYPER_CRITICAL_700_70_7:
                break;
            case Shadower.HYPER_DEFENSE_200_20_2:
                break;
            case Shadower.HYPER_DEXTERITY_700_70_7:
                break;
            case Shadower.HYPER_FURY_700_70_7:
                break;
            case Shadower.HYPER_HEALTH_700_70_7:
                break;
            case Shadower.HYPER_INTELLIGENCE_700_70_7:
                break;
            case Shadower.HYPER_JUMP_700_70_7:
                break;
            case Shadower.HYPER_LUCK_700_70_7:
                break;
            case Shadower.HYPER_MAGIC_DEFENSE_700_70_7:
                break;
            case Shadower.HYPER_MANA_700_70_7:
                break;
            case Shadower.HYPER_SPEED_700_70_7:
                break;
            case Shadower.HYPER_STRENGTH_700_70_7:
                break;
            case Shadower.MAPLE_WARRIOR_700_70_7:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Shadower.MESO_EXPLOSION_ENHANCE:
                break;
            case Shadower.MESO_EXPLOSION_GUARDBREAK:
                break;
            case Shadower.MESO_EXPLOSION_REINFORCE:
                break;
            case Shadower.MESO_MASTERY:
                break;
            case Shadower.NINJA_AMBUSH_1:
                break;
            case Shadower.PRIME_CRITICAL:
                break;
            case Shadower.SHADOWER_INSTINCT:
                // Handled in Special Attack Move.
                break;
            case Shadower.SHADOW_SHIFTER:
                break;
            case Shadower.SHADOW_VEIL:
                break;
            case Shadower.SMOKESCREEN:
                break;
            case Shadower.SUDDEN_RAID_2:
                break;
            case Shadower.TAUNT_1:
                break;
            case Shadower.TOXIC_VENOM_1:
                break;
            case Shadower.VENOMOUS_STAB:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 422;
    }

}
