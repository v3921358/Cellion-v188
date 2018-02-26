package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isThunderBreakerCygnus;
import constants.skills.ThunderBreaker;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ThunderBreakerBuff extends AbstractBuffClass {

    public ThunderBreakerBuff() {
        skills = new int[]{
            ThunderBreaker.SEAWALL,
            ThunderBreaker.IRONCLAD,
            ThunderBreaker.KNUCKLE_BOOSTER,
            ThunderBreaker.SPEED_INFUSION,
            ThunderBreaker.LIGHTNING_ELEMENTAL,
            ThunderBreaker.ARC_CHARGER,
            ThunderBreaker.PRIMAL_BOLT,
            ThunderBreaker.CALL_OF_CYGNUS,
            ThunderBreaker.GLORY_OF_THE_GUARDIANS,
            ThunderBreaker.LINK_MASTERY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isThunderBreakerCygnus(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case ThunderBreaker.SEAWALL:
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.asrR));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.terR));
                break;
            case ThunderBreaker.IRONCLAD:
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.y));
                break;
            case ThunderBreaker.KNUCKLE_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.SPEED_INFUSION:
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.LIGHTNING_ELEMENTAL:
                eff.statups.put(CharacterTemporaryStat.CygnusElementSkill, 1);
                eff.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, eff.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.ARC_CHARGER:
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.PRIMAL_BOLT:
                eff.statups.put(CharacterTemporaryStat.StrikerHyperElectric, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case ThunderBreaker.CALL_OF_CYGNUS:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.GLORY_OF_THE_GUARDIANS:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case ThunderBreaker.LINK_MASTERY:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
