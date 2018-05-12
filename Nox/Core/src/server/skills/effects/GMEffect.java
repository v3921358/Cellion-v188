package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.GM;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class GMEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case GM.ADMINS_BLESSING:
                break;
            case GM.ADMIN_ANTIMACRO:
                break;
            case GM.HASTE_NORMAL:
                break;
            case GM.HIDE_1:
                break;
            case GM.HYPER_BODY_2:
                break;
            case GM.RESURRECTION_1:
                break;
            case GM.SUPER_DRAGON_ROAR_1:
                break;
            case GM.SUPER_DRAGON_ROAR_2:
                break;
            case GM.SUPER_MAGIC_ASSAULT:
                break;
            case GM.TELEPORT_10:
                break;
            case GM.TELEPORT_9:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 900;
    }

}
