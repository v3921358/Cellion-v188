package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.GameConstants;
import constants.skills.Aran;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class AranBuff extends AbstractBuffClass {

    public AranBuff() {
        skills = new int[]{
            Aran.POLEARM_BOOSTER,
            Aran.SNOW_CHARGE,
            Aran.DRAIN,
            Aran.BODY_PRESSURE,
            Aran.COMBO_ABILITY,
            Aran.ROLLING_SPIN,
            Aran.MIGHT,
            Aran.MAHA_BLESSING,
            Aran.MAPLE_WARRIOR,
            Aran.HEROIC_MEMORIES
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isAran(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Aran.POLEARM_BOOSTER: // Polearm Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Aran.SNOW_CHARGE: // Snow Charge
                eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            case Aran.DRAIN: // Drain
                eff.statups.put(CharacterTemporaryStat.AranDrain, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Aran.BODY_PRESSURE:
                eff.statups.put(CharacterTemporaryStat.BodyPressure, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Aran.COMBO_ABILITY:
                eff.statups.put(CharacterTemporaryStat.ComboAbilityBuff, 100);
                break;
            case Aran.ROLLING_SPIN:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Aran.MIGHT: // Might
                eff.statups.put(CharacterTemporaryStat.KnockBack, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.EPDD, eff.info.get(MapleStatInfo.epdd));
                break;
            case Aran.MAHA_BLESSING: // Maha Blessing
                eff.statups.put(CharacterTemporaryStat.MAD, eff.info.get(MapleStatInfo.indieMad));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Aran.MAPLE_WARRIOR: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Aran.HEROIC_MEMORIES: // Heroic Memories
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOverR, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
