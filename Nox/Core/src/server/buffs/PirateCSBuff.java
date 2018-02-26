package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.PirateCS;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class PirateCSBuff extends AbstractBuffClass {

    public PirateCSBuff() {
        skills = new int[]{
            PirateCS.BLAST_BACK
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.PIRATE_CS.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case PirateCS.BLAST_BACK:
                eff.monsterStatus.put(MonsterStatus.SPEED, eff.info.get(MapleStatInfo.z));
        }
    }
}
