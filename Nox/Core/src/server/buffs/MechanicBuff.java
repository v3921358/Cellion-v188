package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isMechanic;
import constants.skills.Mechanic;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class MechanicBuff extends AbstractBuffClass {

    public MechanicBuff() {
        skills = new int[]{
            Mechanic.EXTREME_MECH,
            Mechanic.HUMANOID_MECH,
            Mechanic.TANK_MECH,
            Mechanic.MECHANIC_RAGE,
            Mechanic.ROLL_OF_THE_DICE,
            Mechanic.DOUBLE_DOWN,
            Mechanic.MAPLE_WARRIOR,
            Mechanic.PUNCH_LAUNCHER,
            Mechanic.GIANT_ROBOT_SG88,
            Mechanic.ROCK_N_SHOCK,
            Mechanic.BOTS_N_TOTS,
            Mechanic.BOTS_N_TOTS_1,
            Mechanic.PERFECT_ARMOR,
            Mechanic.OVERCLOCK,
            Mechanic.FOR_LIBERTY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isMechanic(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Mechanic.EXTREME_MECH:
            case Mechanic.HUMANOID_MECH:
            case Mechanic.TANK_MECH:
                eff.statups.put(CharacterTemporaryStat.IndieSpeed, eff.info.get(MapleStatInfo.indieSpeed));
                eff.statups.put(CharacterTemporaryStat.EMHP, eff.info.get(MapleStatInfo.emhp));
                eff.statups.put(CharacterTemporaryStat.EMMP, eff.info.get(MapleStatInfo.emmp));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.EPDD, eff.info.get(MapleStatInfo.epdd));
                eff.statups.put(CharacterTemporaryStat.Mechanic, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Mechanic.MECHANIC_RAGE:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Mechanic.ROLL_OF_THE_DICE:
            case Mechanic.DOUBLE_DOWN:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case Mechanic.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Mechanic.PUNCH_LAUNCHER:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Mechanic.GIANT_ROBOT_SG88:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Mechanic.ROCK_N_SHOCK:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Mechanic.BOTS_N_TOTS:
            case Mechanic.BOTS_N_TOTS_1:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Mechanic.PERFECT_ARMOR:
                eff.statups.put(CharacterTemporaryStat.PowerGuard, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Mechanic.OVERCLOCK:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.x));
                break;
            case Mechanic.FOR_LIBERTY:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
