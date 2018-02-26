package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.FPMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class FPMageBuff extends AbstractBuffClass {

    public FPMageBuff() {
        skills = new int[]{
            FPMage.ELEMENTAL_DECREASE,
            FPMage.TELEPORT_MASTERY,
            FPMage.ELEMENTAL_ADAPTATION_FIRE_POISON
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.FP_MAGE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case FPMage.ELEMENTAL_ADAPTATION_FIRE_POISON: //Elemental Adaptation (Fire, Poison)
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, 1);
                break;
            case FPMage.ELEMENTAL_DECREASE: //Elemental Decrease
                eff.statups.put(CharacterTemporaryStat.ElementalReset, eff.info.get(MapleStatInfo.x));
                break;
            case FPMage.TELEPORT_MASTERY: //Teleport Mastery
                eff.info.put(MapleStatInfo.mpCon, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.TeleportMasteryOn, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
