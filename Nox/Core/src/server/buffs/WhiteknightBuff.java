package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Whiteknight;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class WhiteknightBuff extends AbstractBuffClass {

    public WhiteknightBuff() {
        skills = new int[]{
            Whiteknight.LIGHTNING_CHARGE,
            Whiteknight.COMBAT_ORDERS,
            Whiteknight.HP_RECOVERY,
            Whiteknight.THREATEN,
            Whiteknight.PARASHOCK_GUARD
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.WHITEKNIGHT.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Whiteknight.LIGHTNING_CHARGE:
                eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            case Whiteknight.COMBAT_ORDERS:
                eff.statups.put(CharacterTemporaryStat.CombatOrders, eff.info.get(MapleStatInfo.x));
                break;
            case Whiteknight.HP_RECOVERY: // HP Recovery
                eff.statups.put(CharacterTemporaryStat.Regen, 1);
                break;
            case Whiteknight.THREATEN: // Threaten
                eff.monsterStatus.put(MonsterStatus.WATK, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.PDD, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.MATK, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.MDD, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.EVA, eff.info.get(MapleStatInfo.z));
                break;
            case Whiteknight.PARASHOCK_GUARD: // Parashock Guard
                eff.statups.put(CharacterTemporaryStat.KnightsAura, eff.info.get(MapleStatInfo.x) / 2);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
        }
    }
}
