package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isPhantom;
import constants.skills.Phantom;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class PhantomBuff extends AbstractBuffClass {

    public PhantomBuff() {
        skills = new int[]{
            Phantom.BAD_LUCK_WARD,
            Phantom.GHOSTWALK,
            Phantom.CANE_BOOSTER,
            Phantom.MAPLE_WARRIOR,
            Phantom.PRIERE_DARIA,
            Phantom.FINAL_JUDGMENT_DRAW,
            Phantom.FINAL_FEINT,
            Phantom.PHANTOM_SWIFTNESS,
            Phantom.PENOMBRE,
            Phantom.TO_THE_SKIES,
            Phantom.INFILTRATE,
            Phantom.HEROIC_MEMORIES,
            Phantom.CLAIR_DE_LUNE,
            Phantom.VOL_DAME
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isPhantom(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Phantom.BAD_LUCK_WARD:
                eff.statups.put(CharacterTemporaryStat.MaxHP, eff.info.get(MapleStatInfo.indieMhpR));//indieMhpR/x
                eff.statups.put(CharacterTemporaryStat.IndieMMPR, eff.info.get(MapleStatInfo.indieMmpR));//indieMmpR/x
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.y));
                break;
            case Phantom.GHOSTWALK:
                eff.statups.put(CharacterTemporaryStat.DarkSight, eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.CANE_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.PRIERE_DARIA:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.damR));
                eff.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, eff.info.get(MapleStatInfo.ignoreMobpdpR));
                break;
            case Phantom.FINAL_JUDGMENT_DRAW:
                eff.info.put(MapleStatInfo.time, 30000);
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.v));
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.x));//x
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.y));//y
                break;
            case Phantom.FINAL_FEINT:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.ReviveOnce, 1);
                break;
            case Phantom.PHANTOM_SWIFTNESS:
                eff.monsterStatus.put(MonsterStatus.WATK, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.PDD, eff.info.get(MapleStatInfo.y));
                break;
            case Phantom.PENOMBRE:
                eff.info.put(MapleStatInfo.damage, eff.info.get(MapleStatInfo.v));
                eff.info.put(MapleStatInfo.attackCount, eff.info.get(MapleStatInfo.w));
                eff.info.put(MapleStatInfo.mobCount, eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.TO_THE_SKIES:
                eff.moveTo(eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.INFILTRATE:
                eff.statups.put(CharacterTemporaryStat.Invisible, eff.info.get(MapleStatInfo.x));
                break;
            case Phantom.HEROIC_MEMORIES:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Phantom.CLAIR_DE_LUNE:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, 10);
                break;
            case Phantom.VOL_DAME:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
