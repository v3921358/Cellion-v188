package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.BladeMaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BladeMasterBuff extends AbstractBuffClass {

    public BladeMasterBuff() {
        skills = new int[]{
            BladeMaster.FINAL_CUT,
            BladeMaster.MAPLE_WARRIOR,
            BladeMaster.MIRRORED_TARGET,
            BladeMaster.EPIC_ADVENTURE,
            BladeMaster.ASURAS_ANGER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BLADE_MASTER.getId()
                || job == MapleJob.BLADE_MASTER_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BladeMaster.THORNS: // Thorns
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                eff.statups.put(CharacterTemporaryStat.EPAD, (int) eff.info.get(MapleStatInfo.epad));
                break;
            case BladeMaster.BLADE_CLONE: // Blade Clone
                eff.statups.put(CharacterTemporaryStat.StackBuff, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case BladeMaster.FINAL_CUT:
                eff.info.put(MapleStatInfo.time, 60 * 1000);
                eff.addHpR(-eff.info.get(MapleStatInfo.x) / 100.0);
                eff.statups.put(CharacterTemporaryStat.FinalCut, eff.info.get(MapleStatInfo.w));
                break;
            case BladeMaster.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case BladeMaster.MIRRORED_TARGET:
                eff.statups.put(CharacterTemporaryStat.PUPPET, 1);
                break;
            case BladeMaster.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case BladeMaster.ASURAS_ANGER:
                eff.statups.put(CharacterTemporaryStat.Asura, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
