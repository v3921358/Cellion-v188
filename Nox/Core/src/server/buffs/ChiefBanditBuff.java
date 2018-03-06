package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ChiefBandit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ChiefBanditBuff extends AbstractBuffClass {

    public ChiefBanditBuff() {
        skills = new int[]{
            ChiefBandit.PICK_POCKET,
            ChiefBandit.SHADOW_PARTNER,
            ChiefBandit.PHASE_DASH,
            ChiefBandit.DARK_FLARE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CHIEFBANDIT.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case ChiefBandit.PICK_POCKET: //Pick Pocket
                //eff.statups.put(CharacterTemporaryStat.PickPocket, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.MesoUp, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case ChiefBandit.SHADOW_PARTNER: //Shadow Partner
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case ChiefBandit.PHASE_DASH:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case ChiefBandit.DARK_FLARE:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
        }
    }
}
