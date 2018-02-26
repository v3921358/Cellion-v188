package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ILWizard;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ILWizardBuff extends AbstractBuffClass {

    public ILWizardBuff() {
        skills = new int[]{
            ILWizard.MAGIC_BOOSTER,
            ILWizard.CHILLING_STEP,
            ILWizard.MEDITATION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.IL_WIZARD.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case ILWizard.MAGIC_BOOSTER: //Magic Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case ILWizard.CHILLING_STEP: //Teleport Mastery
                eff.info.put(MapleStatInfo.mpCon, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.TeleportMasteryOn, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                break;
            case ILWizard.MEDITATION: //inc matk
                eff.statups.put(CharacterTemporaryStat.MAD, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
