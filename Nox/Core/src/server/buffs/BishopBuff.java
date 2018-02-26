package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Bishop;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 * @author Novak
 *
 */
@BuffEffectManager
public class BishopBuff extends AbstractBuffClass {

    public BishopBuff() {
        skills = new int[]{
            Bishop.MAPLE_WARRIOR,
            Bishop.BAHAMUT,
            Bishop.INFINITY,
            Bishop.ADVANCED_BLESSING,
            Bishop.RIGHTEOUSLY_INDIGNANT,
            Bishop.EPIC_ADVENTURE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BISHOP.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Bishop.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Bishop.BAHAMUT:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Bishop.INFINITY:
                eff.setHpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.setMpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.statups.put(CharacterTemporaryStat.Infinity, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                break;
            case Bishop.ADVANCED_BLESSING:
                eff.statups.put(CharacterTemporaryStat.AdvancedBless, eff.info.get(MapleStatInfo.x));
                //eff.statups.put(CharacterTemporaryStat.IndieMHP, eff.info.get(MapleStatInfo.indieMmp));
                //eff.statups.put(CharacterTemporaryStat.IndieMMP, eff.info.get(MapleStatInfo.indieMhp));
                break;
            case Bishop.RIGHTEOUSLY_INDIGNANT:
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.ignoreMobpdpR));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.MAD, eff.info.get(MapleStatInfo.indieMad));
                // eff.statups.put(CharacterTemporaryStat.VengeanceOfAngel, 1);
                break;
            case Bishop.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
