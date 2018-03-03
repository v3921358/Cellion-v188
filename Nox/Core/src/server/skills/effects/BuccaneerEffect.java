package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Buccaneer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BuccaneerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Buccaneer.BUCCANEER_BLAST:
                break;
            case Buccaneer.BUCCANEER_BLAST_EXTRA_STRIKE:
                break;
            case Buccaneer.BUCCANEER_BLAST_REINFORCE:
                break;
            case Buccaneer.BUCCANEER_BLAST_SPREAD:
                break;
            case Buccaneer.CROSSBONES:
                break;
            case Buccaneer.DEMOLITION:
                break;
            case Buccaneer.DOUBLE_BLAST:
                break;
            case Buccaneer.DOUBLE_DOWN_1:
                break;
            case Buccaneer.DOUBLE_DOWN_ADDITION:
                break;
            case Buccaneer.DOUBLE_DOWN_ENHANCE:
                break;
            case Buccaneer.DOUBLE_DOWN_SAVING_GRACE:
                break;
            case Buccaneer.DRAGON_STRIKE:
                break;
            case Buccaneer.DRAGON_STRIKE_1:
                break;
            case Buccaneer.ENERGY_ORB:
                break;
            case Buccaneer.EPIC_ADVENTURE_7:
                break;
            case Buccaneer.HEROS_WILL_20_2:
                break;
            case Buccaneer.HYPER_ACCURACY_60_6:
                break;
            case Buccaneer.HYPER_CRITICAL_60_6:
                break;
            case Buccaneer.HYPER_DEFENSE_30_3:
                break;
            case Buccaneer.HYPER_DEXTERITY_60_6:
                break;
            case Buccaneer.HYPER_FURY_60_6:
                break;
            case Buccaneer.HYPER_HEALTH_60_6:
                break;
            case Buccaneer.HYPER_INTELLIGENCE_60_6:
                break;
            case Buccaneer.HYPER_JUMP_60_6:
                break;
            case Buccaneer.HYPER_LUCK_60_6:
                break;
            case Buccaneer.HYPER_MAGIC_DEFENSE_60_6:
                break;
            case Buccaneer.HYPER_MANA_60_6:
                break;
            case Buccaneer.HYPER_SPEED_60_6:
                break;
            case Buccaneer.HYPER_STRENGTH_60_6:
                break;
            case Buccaneer.MAPLE_WARRIOR_30_3:
                break;
            case Buccaneer.NAUTILUS_STRIKE_1:
                break;
            case Buccaneer.NAUTILUS_STRIKE_2:
                break;
            case Buccaneer.OCTOPUNCH:
                break;
            case Buccaneer.OCTOPUNCH_1:
                break;
            case Buccaneer.OCTOPUNCH_BOSS_RUSH:
                break;
            case Buccaneer.OCTOPUNCH_EXTRA_STRIKE:
                break;
            case Buccaneer.OCTOPUNCH_REINFORCE:
                break;
            case Buccaneer.PIRATES_REVENGE:
                break;
            case Buccaneer.POWER_UNITY:
                break;
            case Buccaneer.POWER_UNITY_1:
                break;
            case Buccaneer.SNATCH:
                break;
            case Buccaneer.SPEED_INFUSION_1:
                break;
            case Buccaneer.STIMULATING_CONVERSATION:
                break;
            case Buccaneer.SUPER_TRANSFORMATION_1:
                break;
            case Buccaneer.TIME_LEAP:
                break;
            case Buccaneer.TYPHOON_CRUSH:
                break;
            case Buccaneer.ULTRA_CHARGE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 512;
    }

}
