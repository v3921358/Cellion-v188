package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isJett;
import constants.skills.Jett;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 * @author Mazen
 * @author Sjonnie
 * @author Asura
 */
@BuffEffectManager
public class JettBuff extends AbstractBuffClass {

    public JettBuff() {
        skills = new int[]{
            Jett.BOUNTY_CHASER,
            Jett.HIGH_GRAVITY,
            Jett.SLIPSTREAM_SUIT,
            Jett.GALACTIC_MIGHT,
            Jett.MAPLE_WARRIOR,
            Jett.TURRET_DEPLOYMENT,
            Jett.EPIC_ADVENTURE,
            Jett.BIONIC_MAXIMIZER,
            Jett.SINGULARITY_SHOCK
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isJett(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Jett.BOUNTY_CHASER:
                eff.statups.put(CharacterTemporaryStat.DEX, eff.info.get(MapleStatInfo.dexX));
                eff.statups.put(CharacterTemporaryStat.STR, eff.info.get(MapleStatInfo.strX));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case Jett.HIGH_GRAVITY:
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                eff.statups.put(CharacterTemporaryStat.IndieAllStat, eff.info.get(MapleStatInfo.indieAllStat));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.indieCr));
                break;
            case Jett.SLIPSTREAM_SUIT:
                eff.statups.put(CharacterTemporaryStat.DEX, eff.info.get(MapleStatInfo.x) * 10);
                eff.statups.put(CharacterTemporaryStat.EVAR, eff.info.get(MapleStatInfo.y));
                //eff.statups.put(CharacterTemporaryStat.ACCR, eff.info.get(MapleStatInfo.y));
                break;
            case Jett.GALACTIC_MIGHT:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Jett.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Jett.TURRET_DEPLOYMENT:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Jett.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Jett.BIONIC_MAXIMIZER: //Bionic Maximizer
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.v));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.w));
                eff.statups.put(CharacterTemporaryStat.DamageReduce, eff.info.get(MapleStatInfo.y));
                break;
            case Jett.SINGULARITY_SHOCK:
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
