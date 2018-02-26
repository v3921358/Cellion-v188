package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Buccaneer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BuccaneerBuff extends AbstractBuffClass {

    public BuccaneerBuff() {
        skills = new int[]{
            Buccaneer.DOUBLE_DOWN,
            Buccaneer.PIRATES_REVENGE,
            Buccaneer.SPEED_INFUSION,
            Buccaneer.MAPLE_WARRIOR,
            Buccaneer.CROSSBONES,
            Buccaneer.EPIC_ADVENTURE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BUCCANEER.getId()
                || job == MapleJob.BUCCANEER_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Buccaneer.DOUBLE_DOWN:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case Buccaneer.PIRATES_REVENGE:
                eff.info.put(MapleStatInfo.cooltime, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, (int) eff.info.get(MapleStatInfo.damR));
                break;
            case Buccaneer.SPEED_INFUSION:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Buccaneer.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Buccaneer.CROSSBONES:
                eff.statups.put(CharacterTemporaryStat.IndiePADR, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Buccaneer.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
