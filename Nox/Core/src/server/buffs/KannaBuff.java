package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Kanna;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class KannaBuff extends AbstractBuffClass {

    public KannaBuff() {
        skills = new int[]{
            Kanna.BREATH_OF_THE_UNSEEN, // Breath of the Unseen - HiddenBuff
            Kanna.HAKUS_GIFT_1,
            Kanna.FOXFIRE, // Foxfire - HiddenBuff
            Kanna.SHIKIGAMI_CHARM, // Shikigami Charm - HiddenBuff
            Kanna.RADIANT_PEACOCK, // Radiant Peacock
            Kanna.DAWNS_WARRIOR_MAPLE_WARRIOR, // Maple Warrior
            Kanna.KISHIN_SHOUKAN, // Kishin Shoukan
            Kanna.PRINCESSS_VOW
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.KANNA.getId()
                || job == MapleJob.KANNA1.getId()
                || job == MapleJob.KANNA2.getId()
                || job == MapleJob.KANNA3.getId()
                || job == MapleJob.KANNA4.getId()
                || job == MapleJob.KANNA5.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Kanna.BREATH_OF_THE_UNSEEN: // Breath of the Unseen - HiddenBuff?
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case Kanna.DAWNS_WARRIOR_MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Kanna.HAKUS_GIFT_1:
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.hp));
                break;
            case Kanna.FOXFIRE:// Foxfire
            case Kanna.SHIKIGAMI_CHARM:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Kanna.RADIANT_PEACOCK: // Radiant Peacock
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Kanna.PRINCESSS_VOW: // Princess's Vow
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Kanna.KISHIN_SHOUKAN: // Kishin Shoukan
                eff.info.put(MapleStatInfo.time, 60000);
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
        }
    }
}
