package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hermit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class HermitBuff extends AbstractBuffClass {

    public HermitBuff() {
        skills = new int[]{
            Hermit.DARK_FLARE,
            Hermit.SHADOW_WEB,
            Hermit.SHADOW_STARS,
            Hermit.SHADOW_PARTNER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.HERMIT.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Hermit.SHADOW_PARTNER: //Shadow Partner
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case Hermit.SHADOW_STARS:
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, 0);
                break;
            case Hermit.DARK_FLARE:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Hermit.SHADOW_WEB:
                eff.monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                break;
        }
    }
}
