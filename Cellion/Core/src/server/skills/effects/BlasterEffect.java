package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Blaster;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BlasterEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Blaster.BLAST_SHIELD:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_1:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_2:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_3:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_4:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_5:
                break;
            case Blaster.DETONATE:
                break;
            case Blaster.DETONATE_1:
                break;
            case Blaster.DOUBLE_JUMP_5:
                break;
            case Blaster.MAGNUM_PUNCH:
                break;
            case Blaster.RELOAD:
                break;
            case Blaster.REVOLVING_CANNON:
                break;
            case Blaster.REVOLVING_CANNON_1:
                break;
            case Blaster.REVOLVING_CANNON_MASTERY:
                break;
            case Blaster.ARM_CANNON_BOOST:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Blaster.ARM_CANNON_MASTERY:
                break;
            case Blaster.BOBBING:
                break;
            case Blaster.BOBBING_1:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_7:
                break;
            case Blaster.CHARGE_MASTERY:
                break;
            case Blaster.DOUBLE_BLAST_1:
                break;
            case Blaster.PHYSICAL_TRAINING_80_8:
                break;
            case Blaster.REVOLVING_CANNON_2:
                break;
            case Blaster.REVOLVING_CANNON_PLUS:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION:
                break;
            case Blaster.COMBO_TRAINING:
                break;
            case Blaster.HAMMER_SMASH:
                break;
            case Blaster.HAMMER_SMASH_1:
                break;
            case Blaster.HAMMER_SMASH_2:
                break;
            case Blaster.MAGNUM_LAUNCH:
                break;
            case Blaster.REVOLVING_CANNON_PLUS_II:
                break;
            case Blaster.ROCKET_RUSH:
                break;
            case Blaster.ROCKET_RUSH_1:
                break;
            case Blaster.SHIELD_TRAINING:
                break;
            case Blaster.WEAVING:
                break;
            case Blaster.WEAVING_1:
                break;
            case Blaster.ADVANCED_CHARGE_MASTERY:
                break;
            case Blaster.AFTERSHOCK_PUNCH:
                break;
            case Blaster.BALLISTIC_HURRICANE:
                break;
            case Blaster.BALLISTIC_HURRICANE_1:
                break;
            case Blaster.BALLISTIC_HURRICANE_2:
                break;
            case Blaster.BLAST_SHIELD_RECOVERY:
                break;
            case Blaster.BUNKER_BUSTER_EXPLOSION_6:
                break;
            case Blaster.BUNKER_EXPLOSION_GUARD_BONUS:
                break;
            case Blaster.CANNON_OVERDRIVE:
                pEffect.statups.put(CharacterTemporaryStat.RWMaximizeCannon, 0);
                break;
            case Blaster.COMBO_TRAINING_II:
                break;
            case Blaster.FOR_LIBERTY_2:
                pEffect.statups.put(CharacterTemporaryStat.DamR, 10);
                break;
            case Blaster.GAUNTLET_EXPERT:
                break;
            case Blaster.HEROS_WILL_30_3:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH_1:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH_2:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH_3:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH_4:
                break;
            case Blaster.HYPER_MAGNUM_PUNCH_5:
                break;
            case Blaster.IMPROVED_BUNKER_EXPLOSION:
                break;
            case Blaster.IMPROVED_BUNKER_SHOCKWAVE:
                break;
            case Blaster.MAPLE_WARRIOR_40_4:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(StatInfo.x));
                break;
            case Blaster.MUZZLE_FLASH:
                break;
            case Blaster.PIERCING_PUNCH:
                break;
            case Blaster.POWER_PUNCH:
                break;
            case Blaster.REVOLVING_BLAST:
                break;
            case Blaster.REVOLVING_BLAST_1:
                break;
            case Blaster.REVOLVING_BLAST_2:
                break;
            case Blaster.REVOLVING_BLAST_3:
                break;
            case Blaster.REVOLVING_BLAST_4:
                break;
            case Blaster.REVOLVING_BLAST_5:
                break;
            case Blaster.REVOLVING_BLAST_6:
                break;
            case Blaster.REVOLVING_BLAST_7:
                break;
            case Blaster.REVOLVING_CANNON_PLUS_III:
                break;
            case Blaster.SHIELD_TRAINING_II:
                break;
            case Blaster.SHOTGUN_PUNCH:
                break;
            case Blaster.SHOTGUN_PUNCH_1:
                break;
            case Blaster.SPEEDY_BALLISTIC_HURRICANE:
                break;
            case Blaster.SPEEDY_VITALITY_SHIELD:
                break;
            case Blaster.VITALITY_SHIELD:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3700 || nClass == 3710 || nClass == 3711 || nClass == 3712;
    }

}
