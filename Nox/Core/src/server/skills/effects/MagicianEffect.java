package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Magician;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class MagicianEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Magician.ELEMENTAL_WEAKNESS:
                break;
            case Magician.ENERGY_BOLT:
                break;
            case Magician.ENERGY_BOLT_1:
                break;
            case Magician.MAGIC_ARMOR:
                break;
            case Magician.MAGIC_ARMOR_2:
                break;
            case Magician.MAGIC_CLAW_1:
                break;
            case Magician.MAGIC_GUARD_2:
                pEffect.info.put(StatInfo.time, 2100000000);
                pEffect.statups.put(CharacterTemporaryStat.MagicGuard, pEffect.info.get(StatInfo.x));
                break;
            case Magician.MP_BOOST:
                break;
            case Magician.TELEPORT_20_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 200;
    }

}
