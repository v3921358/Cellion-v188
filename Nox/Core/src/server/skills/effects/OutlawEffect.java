package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Outlaw;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class OutlawEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Outlaw.ALL_ABOARD:
                break;
            case Outlaw.ALL_ABOARD_1:
                break;
            case Outlaw.ALL_ABOARD_2:
                break;
            case Outlaw.ALL_ABOARD_3:
                break;
            case Outlaw.ALL_ABOARD_4:
                break;
            case Outlaw.ALL_ABOARD_5:
                break;
            case Outlaw.ALL_ABOARD_6:
                break;
            case Outlaw.BLACKBOOT_BILL:
                break;
            case Outlaw.BLUNDERBUSTER:
                break;
            case Outlaw.BURST_FIRE:
                break;
            case Outlaw.CROSS_CUT_BLAST:
                break;
            case Outlaw.CROSS_CUT_BLAST_1:
                break;
            case Outlaw.FLAMETHROWER:
                break;
            case Outlaw.FULLMETAL_JACKET:
                break;
            case Outlaw.GAVIOTA:
                break;
            case Outlaw.HOMING_BEACON:
                break;
            case Outlaw.ICE_SPLITTER:
                break;
            case Outlaw.OCTOCANNON:
                break;
            case Outlaw.OCTOPUS:
                break;
            case Outlaw.OUTLAWS_CODE:
                break;
            case Outlaw.ROLL_OF_THE_DICE_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 521;
    }

}
