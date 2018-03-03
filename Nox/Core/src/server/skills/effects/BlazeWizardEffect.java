package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.BlazeWizard;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BlazeWizardEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case BlazeWizard.ELEMENTAL_WEAKNESS_3:
                break;
            case BlazeWizard.FIREWALK:
                break;
            case BlazeWizard.FIREWALK_1:
                break;
            case BlazeWizard.FIREWALK_2:
                break;
            case BlazeWizard.FIRE_REPULSION:
                break;
            case BlazeWizard.FLAME:
                break;
            case BlazeWizard.FLAME_BITE:
                break;
            case BlazeWizard.FLAME_ELEMENTAL:
                break;
            case BlazeWizard.MAGIC_ARMOR_1:
                break;
            case BlazeWizard.MAGIC_CLAW:
                break;
            case BlazeWizard.MAGIC_GUARD:
                break;
            case BlazeWizard.MP_BOOST_1:
                break;
            case BlazeWizard.NATURAL_TALENT:
                break;
            case BlazeWizard.ORBITAL_FLAME:
                break;
            case BlazeWizard.ORBITAL_FLAME_1:
                break;
            case BlazeWizard.CONTROLLED_BURN:
                break;
            case BlazeWizard.ELEMENTAL_RESET:
                break;
            case BlazeWizard.FIRE_ARROW:
                break;
            case BlazeWizard.FIRE_PILLAR:
                break;
            case BlazeWizard.FLAME_VORTEX:
                break;
            case BlazeWizard.FLASHFIRE:
                break;
            case BlazeWizard.GREATER_FLAME_ELEMENTAL:
                break;
            case BlazeWizard.GREATER_ORBITAL_FLAME:
                break;
            case BlazeWizard.GREATER_ORBITAL_FLAME_1:
                break;
            case BlazeWizard.HIGH_WISDOM_3:
                break;
            case BlazeWizard.IGNITION:
                break;
            case BlazeWizard.IGNITION_1:
                break;
            case BlazeWizard.MEDITATION:
                break;
            case BlazeWizard.SLOW_1:
                break;
            case BlazeWizard.SPELL_BOOSTER:
                break;
            case BlazeWizard.SPELL_CONTROL:
                break;
            case BlazeWizard.SPELL_MASTERY_3:
                break;
            case BlazeWizard.TELEPORT_2:
                break;
            case BlazeWizard.WORD_OF_FIRE:
                break;
            case BlazeWizard.BRILLIANT_ENLIGHTENMENT:
                break;
            case BlazeWizard.BURNING_FOCUS:
                break;
            case BlazeWizard.CINDER_MAELSTROM_:
                break;
            case BlazeWizard.ELEMENT_AMPLIFICATION_1:
                break;
            case BlazeWizard.FIRE_STRIKE:
                break;
            case BlazeWizard.FLAME_GEAR:
                break;
            case BlazeWizard.FLAME_TEMPEST:
                break;
            case BlazeWizard.GRAND_FLAME_ELEMENTAL:
                break;
            case BlazeWizard.GRAND_ORBITAL_FLAME:
                break;
            case BlazeWizard.GRAND_ORBITAL_FLAME_1:
                break;
            case BlazeWizard.IFRIT_1:
                break;
            case BlazeWizard.LIBERATED_MAGIC:
                break;
            case BlazeWizard.MAGIC_CRITICAL:
                break;
            case BlazeWizard.METEOR_SHOWER_2:
                break;
            case BlazeWizard.PHOENIX_RUN:
                break;
            case BlazeWizard.PHOENIX_RUN_1:
                break;
            case BlazeWizard.SEAL_1:
                break;
            case BlazeWizard.TELEPORT_MASTERY_1:
                break;
            case BlazeWizard.BLAZING_EXTINCTION:
                break;
            case BlazeWizard.BLAZING_EXTINCTION_1:
                break;
            case BlazeWizard.BLAZING_EXTINCTION_ADD_ATTACK:
                break;
            case BlazeWizard.BLAZING_EXTINCTION_REINFORCE:
                break;
            case BlazeWizard.BLAZING_EXTINCTION_SPREAD:
                break;
            case BlazeWizard.BURNING_CONDUIT:
                break;
            case BlazeWizard.CALL_OF_CYGNUS_5:
                break;
            case BlazeWizard.CATACLYSM:
                break;
            case BlazeWizard.DRAGON_BLAZE:
                break;
            case BlazeWizard.DRAGON_BLAZE_1:
                break;
            case BlazeWizard.FINAL_FLAME_ELEMENTAL:
                break;
            case BlazeWizard.FINAL_ORBITAL_FLAME:
                break;
            case BlazeWizard.FINAL_ORBITAL_FLAME_1:
                break;
            case BlazeWizard.FIRES_OF_CREATION:
                break;
            case BlazeWizard.FIRES_OF_CREATION_1:
                break;
            case BlazeWizard.FIRES_OF_CREATION_2:
                break;
            case BlazeWizard.FLAME_BARRIER:
                break;
            case BlazeWizard.FLAME_BARRIER_1:
                break;
            case BlazeWizard.GLORY_OF_THE_GUARDIANS_4:
                break;
            case BlazeWizard.HYPER_ACCURACY_10_1:
                break;
            case BlazeWizard.HYPER_CRITICAL_10_1:
                break;
            case BlazeWizard.HYPER_DEFENSE_9:
                break;
            case BlazeWizard.HYPER_DEXTERITY_20_2:
                break;
            case BlazeWizard.HYPER_FURY_10_1:
                break;
            case BlazeWizard.HYPER_HEALTH_10_1:
                break;
            case BlazeWizard.HYPER_INTELLIGENCE_20_2:
                break;
            case BlazeWizard.HYPER_JUMP_10_1:
                break;
            case BlazeWizard.HYPER_LUCK_20_2:
                break;
            case BlazeWizard.HYPER_MAGIC_DEFENSE_10_1:
                break;
            case BlazeWizard.HYPER_MANA_10_1:
                break;
            case BlazeWizard.HYPER_SPEED_10_1:
                break;
            case BlazeWizard.HYPER_STRENGTH_20_2:
                break;
            case BlazeWizard.IGNITION_MASS_EXPLOSION:
                break;
            case BlazeWizard.IGNITION_MAX_IGNITION:
                break;
            case BlazeWizard.IGNITION_REINFORCE:
                break;
            case BlazeWizard.ORBITAL_FLAME_GUARDBREAK:
                break;
            case BlazeWizard.ORBITAL_FLAME_RANGE:
                break;
            case BlazeWizard.ORBITAL_FLAME_RANGE_1:
                break;
            case BlazeWizard.ORBITAL_FLAME_SPLIT_ATTACK:
                break;
            case BlazeWizard.PURE_MAGIC:
                break;
            case BlazeWizard.TOWERING_INFERNO:
                break;
            case BlazeWizard.WILD_BLAZE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1200 || nClass == 1210 || nClass == 1211 || nClass == 1212;
    }

}
