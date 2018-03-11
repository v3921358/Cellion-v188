package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.GameConstants;
import constants.skills.BeastTamer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class BeastTamerBuff extends AbstractBuffClass {

    public BeastTamerBuff() {
        skills = new int[]{
            BeastTamer.FLY,
            BeastTamer.DEFENSE_IGNORANCE, //110001501, // Bear Mode
        //110001502, // Snow Leopard Mode
        //110001503, // Hawk Mode
        //110001504, // Cat Mode
        //112001009 // Bear Assault
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BEASTTAMER.getId()
                || job == MapleJob.BEASTTAMER2.getId()
                || job == MapleJob.BEASTTAMER3.getId()
                || job == MapleJob.BEASTTAMER4.getId()
                || job == MapleJob.BEASTTAMER5.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BeastTamer.FLY:
                eff.statups.put(CharacterTemporaryStat.NewFlying, 1);
                break;
            case BeastTamer.DEFENSE_IGNORANCE:
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                break;
            default:
                System.out.println("BeastTamer buff not coded: " + skill);
                break;
        }
    }
}
