package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Priest;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class PriestBuff extends AbstractBuffClass {

    public PriestBuff() {
        skills = new int[]{
            Priest.TELEPORT_MASTERY,
            Priest.HOLY_MAGIC_SHELL,
            Priest.SHINING_RAY,
            Priest.HOLY_SYMBOL,
            Priest.DIVINE_PROTECTION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.PRIEST.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Priest.DIVINE_PROTECTION: //Divine Protection
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, 1);

                break;
            case Priest.HOLY_SYMBOL: //Holy Symbol
                eff.statups.put(CharacterTemporaryStat.HolySymbol, eff.info.get(MapleStatInfo.x));
                break;
            case Priest.TELEPORT_MASTERY: //Teleport Mastery
                eff.info.put(MapleStatInfo.mpCon, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.TeleportMasteryOn, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Priest.HOLY_MAGIC_SHELL:
                eff.statups.put(CharacterTemporaryStat.HolyMagicShell, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.cooltime, eff.info.get(MapleStatInfo.y));
                eff.setHpR(eff.info.get(MapleStatInfo.z) / 100.0);
                break;
            case Priest.SHINING_RAY:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
