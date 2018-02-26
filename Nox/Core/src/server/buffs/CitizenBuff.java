package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Citizen;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CitizenBuff extends AbstractBuffClass {

    public CitizenBuff() {
        skills = new int[]{
            Citizen.INFILTRATE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CITIZEN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Citizen.INFILTRATE:
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                eff.statups.put(CharacterTemporaryStat.Sneak, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
