package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ILMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ILMageBuff extends AbstractBuffClass {

    public ILMageBuff() {
        skills = new int[]{
            ILMage.ELEMENTAL_DECREASE,
            ILMage.TELEPORT_MASTERY,
            ILMage.ICE_STRIKE,
            ILMage.ELEMENTAL_ADAPTATION_ICE_LIGHTNING
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.IL_MAGE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case ILMage.ELEMENTAL_ADAPTATION_ICE_LIGHTNING: //Elemental Adaptation (Ice, Lightning)
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, eff.info.get(MapleStatInfo.x));
                break;
            case ILMage.ELEMENTAL_DECREASE: //Elemental Decrease
                eff.statups.put(CharacterTemporaryStat.ElementalReset, eff.info.get(MapleStatInfo.x));
                break;
            case ILMage.TELEPORT_MASTERY: //Teleport Mastery
                eff.info.put(MapleStatInfo.mpCon, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.TeleportMasteryOn, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case ILMage.ICE_STRIKE:
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.info.put(MapleStatInfo.time, eff.info.get(MapleStatInfo.time) * 2);
                break;
        }
    }
}
