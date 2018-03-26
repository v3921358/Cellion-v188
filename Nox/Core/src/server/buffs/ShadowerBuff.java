package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isThiefShadower;
import constants.skills.Shadower;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ShadowerBuff extends AbstractBuffClass {

    public ShadowerBuff() {
        skills = new int[]{
            Shadower.MAPLE_WARRIOR,
            Shadower.BOOMERANG_STAB,
            Shadower.EPIC_ADVENTURE,
            Shadower.FLIP_OF_THE_COIN,
            Shadower.SHADOWER_INSTINCT,
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isThiefShadower(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Shadower.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Shadower.BOOMERANG_STAB:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Shadower.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Shadower.FLIP_OF_THE_COIN:
                //eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Shadower.SHADOWER_INSTINCT:
                //eff.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, (int) eff.getLevel());
                //eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
