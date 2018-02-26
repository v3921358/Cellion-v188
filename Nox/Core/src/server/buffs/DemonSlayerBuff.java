package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isDemonSlayer;
import constants.skills.DemonSlayer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class DemonSlayerBuff extends AbstractBuffClass {

    public DemonSlayerBuff() {
        skills = new int[]{
            DemonSlayer.MAPLE_WARRIOR,
            DemonSlayer.VORTEX_OF_DOOM,
            DemonSlayer.CHAOS_LOCK,
            DemonSlayer.DEMON_CRY,
            DemonSlayer.DARK_METAMORPHOSIS,
            DemonSlayer.LEECH_AURA,
            DemonSlayer.BATTLE_PACT,
            DemonSlayer.DEMONIC_FORTITUDE,
            DemonSlayer.VENGEANCE,
            DemonSlayer.BOUNDLESS_RAGE,
            DemonSlayer.BLACKHEARTED_STRENGTH,
            DemonSlayer.BLUE_BLOOD
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isDemonSlayer(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case DemonSlayer.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.VORTEX_OF_DOOM:
            case DemonSlayer.CHAOS_LOCK:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case DemonSlayer.DEMON_CRY:
                eff.monsterStatus.put(MonsterStatus.SHOWDOWN, eff.info.get(MapleStatInfo.w));
                eff.monsterStatus.put(MonsterStatus.MDD, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.PDD, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.MATK, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.WATK, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.ACC, eff.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.DARK_METAMORPHOSIS:
                eff.statups.put(CharacterTemporaryStat.DamR, (int) eff.info.get(MapleStatInfo.damR));
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, (int) eff.info.get(MapleStatInfo.indieMhpR));
                eff.statups.put(CharacterTemporaryStat.PowerGuard, (int) eff.info.get(MapleStatInfo.damage));
                break;
            case DemonSlayer.BATTLE_PACT:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.DEMONIC_FORTITUDE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case DemonSlayer.VENGEANCE:
                eff.statups.put(CharacterTemporaryStat.PowerGuard, eff.info.get(MapleStatInfo.y));
                break;
            case DemonSlayer.BOUNDLESS_RAGE:
                eff.statups.put(CharacterTemporaryStat.InfinityForce, eff.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.BLACKHEARTED_STRENGTH:
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.IndieMADR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieBDR, eff.info.get(MapleStatInfo.x));
                break;
            case DemonSlayer.BLUE_BLOOD:
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
