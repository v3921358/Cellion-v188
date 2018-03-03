package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Global;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class GlobalEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1 || nClass == 7200 || nClass == 40000 || nClass == 40001 || nClass == 40002 || nClass == 40003 || nClass == 580 || nClass == 40004 || nClass == 40005 || nClass == 581 || nClass == 582 || nClass == 590 || nClass == 591 || nClass == 592 || nClass == 2200 || nClass == 9500 || nClass == 9000 || nClass == 8000 || nClass == 8001 || nClass == 7000 || nClass == 2412 || nClass == 14200 || nClass == 9100 || nClass == 7100 || nClass == 11211 || nClass == 9200 || nClass == 9201 || nClass == 9202 || nClass == 9203 || nClass == 9204 || nClass == 509;
    }

}
