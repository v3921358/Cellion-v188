package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.SuperGM;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class SuperGMEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case SuperGM.BLESS_1:
                break;
            case SuperGM.HASTE_SUPER:
                break;
            case SuperGM.HEAL_DISPEL:
                break;
            case SuperGM.HIDE:
                pEffect.statups.put(CharacterTemporaryStat.DarkSight, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case SuperGM.HOLY_SYMBOL:
                break;
            case SuperGM.HYPER_BODY:
                break;
            case SuperGM.RESURRECTION:
                pEffect.statups.put(CharacterTemporaryStat.Revive, pEffect.info.get(StatInfo.x)); // ?
                break;
            case SuperGM.SUPER_DRAGON_ROAR:
                break;
            case SuperGM.TELEPORT_4:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 910;
    }

}
