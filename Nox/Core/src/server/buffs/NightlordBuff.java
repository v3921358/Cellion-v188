package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Nightlord;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class NightlordBuff extends AbstractBuffClass {

    public NightlordBuff() {
        skills = new int[]{
            Nightlord.MAPLE_WARRIOR,
            Nightlord.EPIC_ADVENTURE,
            Nightlord.DARK_HARMONY,
            Nightlord.BLEED_DART
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.NIGHTLORD.getId()
                || job == MapleJob.NIGHTLORD_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Nightlord.DARK_HARMONY: //Dark Harmony
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));//test - works without
                break;
            case Nightlord.MAPLE_WARRIOR: //Maple Warrior 
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Nightlord.BLEED_DART:
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
            case Nightlord.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;

        }
    }
}
