package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.CannonMaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CannonMasterBuff extends AbstractBuffClass {

    public CannonMasterBuff() {
        skills = new int[]{
            CannonMaster.DOUBLE_DOWN,
            CannonMaster.MAPLE_WARRIOR,
            CannonMaster.ANCHORS_AWEIGH,
            CannonMaster.MONKEY_MILITIA,
            CannonMaster.PIRATES_SPIRIT,
            CannonMaster.EPIC_ADVENTURE,
            CannonMaster.BUCKSHOT
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CANNON_MASTER.getId()
                || job == MapleJob.CANNON_MASTER_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case CannonMaster.DOUBLE_DOWN:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case CannonMaster.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case CannonMaster.ANCHORS_AWEIGH:
                eff.statups.put(CharacterTemporaryStat.PUPPET, 1);
                break;
            case CannonMaster.MONKEY_MILITIA:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case CannonMaster.PIRATES_SPIRIT:
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                break;
            case CannonMaster.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case CannonMaster.BUCKSHOT:
                eff.statups.put(CharacterTemporaryStat.AttackCountX, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
