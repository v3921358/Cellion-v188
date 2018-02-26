package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isMercedes;
import constants.skills.Mercedes;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class MercedesBuff extends AbstractBuffClass {

    public MercedesBuff() {
        skills = new int[]{
            Mercedes.DUAL_BOWGUNS_BOOST,
            Mercedes.MAPLE_WARRIOR,
            Mercedes.SPIRIT_SURGE,
            Mercedes.IGNIS_ROAR,
            Mercedes.ELVISH_BLESSING,
            Mercedes.ANCIENT_WARDING,
            Mercedes.WATER_SHIELD,
            Mercedes.SPIKES_ROYALE,
            Mercedes.UNICORN_SPIKE,
            Mercedes.ELEMENTAL_KNIGHTS,
            Mercedes.ELVEN_BLESSING,
            Mercedes.HEROIC_MEMORIES
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isMercedes(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Mercedes.DUAL_BOWGUNS_BOOST:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.SPIRIT_SURGE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.damage));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.IGNIS_ROAR:
            case Mercedes.ELVISH_BLESSING:
                eff.statups.put(CharacterTemporaryStat.IgnisRore, 1);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Mercedes.ANCIENT_WARDING:
                eff.statups.put(CharacterTemporaryStat.DamR, (int) eff.info.get(MapleStatInfo.damR));
                eff.statups.put(CharacterTemporaryStat.EMHP, (int) eff.info.get(MapleStatInfo.emhp));
                break;
            case Mercedes.WATER_SHIELD:
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.terR));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.terR));
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.SPIKES_ROYALE:
                eff.monsterStatus.put(MonsterStatus.PDD, -eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.UNICORN_SPIKE:
                eff.monsterStatus.put(MonsterStatus.IMPRINT, eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.ELEMENTAL_KNIGHTS:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                eff.info.put(MapleStatInfo.time, 210000);
                break;
            case Mercedes.ELVEN_BLESSING:
                eff.moveTo(eff.info.get(MapleStatInfo.x));
                break;
            case Mercedes.HEROIC_MEMORIES:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
