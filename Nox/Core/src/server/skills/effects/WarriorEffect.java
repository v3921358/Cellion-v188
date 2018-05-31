package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Warrior;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class WarriorEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Warrior.GUARDIAN_ARMOR_2:
                break;
            case Warrior.HP_BOOST_4:
                break;
            case Warrior.IRON_BODY:
                pEffect.statups.put(CharacterTemporaryStat.IndiePDD, pEffect.info.get(StatInfo.indiePdd));
                break;
            case Warrior.IRON_BODY_2:
                break;
            case Warrior.POWER_STRIKE_1:
                break;
            case Warrior.SLASH_BLAST_1:
                break;
            case Warrior.WARRIOR_MASTERY:
                break;
            case Warrior.WAR_LEAP:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 100;
    }

}
