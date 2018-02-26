package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Paladin;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class PaladinBuff extends AbstractBuffClass {

    public PaladinBuff() {
        skills = new int[]{
            Paladin.DIVINE_CHARGE,
            Paladin.MAPLE_WARRIOR,
            Paladin.EPIC_ADVENTURE,
            Paladin.ELEMENTAL_FORCE,
            Paladin.SACROSANCTITY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.PALADIN.getId()
                || job == MapleJob.PALADIN_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Paladin.DIVINE_CHARGE:
                eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            case Paladin.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Paladin.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Paladin.ELEMENTAL_FORCE: // Void Elemental
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.ElementalReset, eff.info.get(MapleStatInfo.x));
                break;
            case Paladin.SACROSANCTITY: // Sacrosanctity
                eff.statups.put(CharacterTemporaryStat.NotDamaged, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
