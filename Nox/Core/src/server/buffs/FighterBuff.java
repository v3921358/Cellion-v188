package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Fighter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class FighterBuff extends AbstractBuffClass {

    public FighterBuff() {
        skills = new int[]{
            Fighter.WEAPON_BOOSTER,
            Fighter.COMBO_ATTACK,
            Fighter.RAGE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.FIGHTER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Fighter.WEAPON_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case Fighter.RAGE:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.PowerGuard, eff.info.get(MapleStatInfo.x));
                break;
            case Fighter.COMBO_ATTACK: // Combo
                eff.statups.put(CharacterTemporaryStat.ComboCounter, 0);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
        }
    }
}
