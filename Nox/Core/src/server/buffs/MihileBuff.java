package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isMihile;
import constants.skills.Mihile;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

@BuffEffectManager
public class MihileBuff extends AbstractBuffClass {

    public MihileBuff() {
        skills = new int[]{
            Mihile.FINAL_ATTACK,
            Mihile.MAGIC_CRASH,
            Mihile.ROYAL_GUARD,
            Mihile.RALLY,
            Mihile.SWORD_BOOSTER,
            Mihile.RADIANT_CHARGE,
            Mihile.ENDURING_SPIRIT,
            Mihile.CALL_OF_CYGNUS,
            Mihile.SOUL_LINK,
            Mihile.ROILING_SOUL,
            Mihile.QUEEN_OF_TOMORROW
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isMihile(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Mihile.FINAL_ATTACK:
                eff.statups.put(CharacterTemporaryStat.FinalAttackProp, eff.info.get(MapleStatInfo.x));
                break;
            case Mihile.MAGIC_CRASH:
                eff.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
                break;
            case Mihile.ROYAL_GUARD:
                eff.statups.put(CharacterTemporaryStat.RoyalGuardPrepare, eff.info.get(MapleStatInfo.x));
                break;
            case Mihile.RALLY:
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Mihile.SWORD_BOOSTER: // booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Mihile.RADIANT_CHARGE: // rad charge
                eff.statups.put(CharacterTemporaryStat.WeaponCharge, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.z));
                break;
            case Mihile.ENDURING_SPIRIT: // end spirit
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.DamageReduce, eff.info.get(MapleStatInfo.x));
                break;
            case Mihile.CALL_OF_CYGNUS: // mw
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Mihile.SOUL_LINK:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.MichaelSoulLink, eff.info.get(MapleStatInfo.s));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Mihile.ROILING_SOUL: // roiling soul
                eff.statups.put(CharacterTemporaryStat.Enrage, 1);
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Mihile.QUEEN_OF_TOMORROW:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
