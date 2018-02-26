package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Outlaw;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class OutlawBuff extends AbstractBuffClass {

    public OutlawBuff() {
        skills = new int[]{
            Outlaw.ROLL_OF_THE_DICE,
            Outlaw.OCTOCANNON,
            Outlaw.CROSS_CUT_BLAST
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.OUTLAW.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Outlaw.ROLL_OF_THE_DICE:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case Outlaw.OCTOCANNON:
                eff.statups.put(CharacterTemporaryStat.PUPPET, 1);
                break;
            case Outlaw.CROSS_CUT_BLAST:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
        }
    }
}
