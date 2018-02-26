package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import static constants.GameConstants.isZero;
import constants.skills.Zero;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class ZeroBuff extends AbstractBuffClass {

    public ZeroBuff() {
        skills = new int[]{
            Zero.FOCUSED_TIME,
            Zero.RHINNES_PROTECTION,
            Zero.DIVINE_FORCE,
            Zero.DIVINE_SPEED
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isZero(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Zero.FOCUSED_TIME: //Focused Time
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.x));
                break;
            case Zero.RHINNES_PROTECTION: // Rhinne's Protection
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Zero.DIVINE_FORCE: // Divine Force
                eff.statups.put(CharacterTemporaryStat.ZeroAuraStr, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieTerR, eff.info.get(MapleStatInfo.indieTerR));
                eff.statups.put(CharacterTemporaryStat.IndieAsrR, eff.info.get(MapleStatInfo.indieAsrR));
                eff.statups.put(CharacterTemporaryStat.IndiePDD, eff.info.get(MapleStatInfo.indiePdd));
                eff.statups.put(CharacterTemporaryStat.IndieMAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indieMad));
                break;
            case Zero.DIVINE_SPEED: // Divine Speed
                eff.statups.put(CharacterTemporaryStat.ZeroAuraSpd, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.IndieJump, eff.info.get(MapleStatInfo.indieJump));
                eff.statups.put(CharacterTemporaryStat.IndieSpeed, eff.info.get(MapleStatInfo.indieSpeed));
                break;
        }
    }
}
