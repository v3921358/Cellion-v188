package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import static constants.GameConstants.isDemonAvenger;
import constants.skills.DemonAvenger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class DemonAvengerBuff extends AbstractBuffClass {

    public DemonAvengerBuff() {
        skills = new int[]{
            DemonAvenger.EXCEED_EXECUTION,
            DemonAvenger.MAPLE_WARRIOR,
            DemonAvenger.BATTLE_PACT,
            DemonAvenger.WARD_EVIL,
            DemonAvenger.DEMONIC_FORTITUDE,
            DemonAvenger.DIABOLIC_RECOVERY,
            DemonAvenger.OVERWHELMING_POWER,
            DemonAvenger.ABYSSAL_CONNECTION,
            DemonAvenger.FORBIDDEN_CONTRACT,
            DemonAvenger.OVERLOAD_RELEASE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isDemonAvenger(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case DemonAvenger.EXCEED_EXECUTION:
                eff.statups.put(CharacterTemporaryStat.Exceed, eff.info.get(MapleStatInfo.x));
                break;
            case DemonAvenger.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case DemonAvenger.BATTLE_PACT:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case DemonAvenger.WARD_EVIL:
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.asrR));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.terR));
                break;
            case DemonAvenger.DEMONIC_FORTITUDE:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case DemonAvenger.DIABOLIC_RECOVERY:
                eff.statups.put(CharacterTemporaryStat.DiabolikRecovery, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.MaxHP, eff.info.get(MapleStatInfo.indieMhpR));
                break;
            case DemonAvenger.OVERWHELMING_POWER:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, 2);
                eff.statups.put(CharacterTemporaryStat.Booster, 2);
                break;
            case DemonAvenger.ABYSSAL_CONNECTION: // Abyssal Connection 
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case DemonAvenger.FORBIDDEN_CONTRACT:
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.indieMhpR));
                break;
            case DemonAvenger.OVERLOAD_RELEASE: // Overload Release
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.indieMhpR));
                eff.statups.put(CharacterTemporaryStat.ExceedOverload, 1);
                break;
        }
    }
}
