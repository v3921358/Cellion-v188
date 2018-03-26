package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.GameConstants;
import constants.skills.WildHunter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class WildHunterBuff extends AbstractBuffClass {

    public WildHunterBuff() {
        skills = new int[]{
            WildHunter.JAGUAR_RIDER,
            WildHunter.SOUL_ARROW_CROSSBOW,
            WildHunter.CALL_OF_THE_WILD,
            WildHunter.CROSSBOW_BOOSTER,
            WildHunter.FELINE_BERSERK,
            WildHunter.SUMMON_JAGUAR,
            WildHunter.SHARP_EYES,
            WildHunter.MAPLE_WARRIOR,
            WildHunter.FOR_LIBERTY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.WILD_HUNTER_1.getId()
                || job == MapleJob.WILD_HUNTER_2.getId()
                || job == MapleJob.WILD_HUNTER_3.getId()
                || job == MapleJob.WILD_HUNTER_4.getId()
                || job == MapleJob.WILD_HUNTER_5.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case WildHunter.JAGUAR_RIDER:
                eff.statups.put(CharacterTemporaryStat.RideVehicle, 1932015);
                eff.statups.put(CharacterTemporaryStat.JaguarSummoned, 0);
                eff.statups.put(CharacterTemporaryStat.JaguarCount, 1);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case WildHunter.SOUL_ARROW_CROSSBOW:
                eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.pad));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, 1);
                break;
            case 33001011:
            case WildHunter.SUMMON_JAGUAR:
                //eff.statups.put(CharacterTemporaryStat.JaguarSummoned, 0);
                //eff.statups.put(CharacterTemporaryStat.JaguarCount, 1);
                //eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case WildHunter.CALL_OF_THE_WILD:// Call of the Wild
                eff.statups.put(CharacterTemporaryStat.HowlingDefence, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.HowlingEvasion, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.HowlingMaxMP, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.HowlingCritical, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.HowlingAttackDamage, eff.info.get(MapleStatInfo.z));
                break;
            case WildHunter.CROSSBOW_BOOSTER:// Crossbow Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x) * 2);
                break;
            case WildHunter.FELINE_BERSERK: // Feline Berserk
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.BeastFormDamageUp, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                break;
            case WildHunter.SHARP_EYES: // Sharp Eyes
                eff.statups.put(CharacterTemporaryStat.SharpEyes, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case WildHunter.MAPLE_WARRIOR: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case WildHunter.FOR_LIBERTY:// For Liberty
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
