package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.WindArcher;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class WindArcherBuff extends AbstractBuffClass {

    public WindArcherBuff() {
        skills = new int[]{
            WindArcher.STORM_ELEMENTAL,
            WindArcher.BOW_BOOSTER,
            WindArcher.EMERALD_FLOWER,
            WindArcher.SYLVAN_AID,
            WindArcher.ALBATROSS,
            WindArcher.ALBATROSS_MAX,
            WindArcher.CALL_OF_CYGNUS,
            WindArcher.TOUCH_OF_THE_WIND,
            WindArcher.SHARP_EYES,
            WindArcher.GLORY_OF_THE_GUARDIANS,
            WindArcher.STORM_BRINGER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.WINDARCHER1.getId()
                || job == MapleJob.WINDARCHER2.getId()
                || job == MapleJob.WINDARCHER3.getId()
                || job == MapleJob.WINDARCHER4.getId()
                || job == MapleJob.WINDARCHER5.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case WindArcher.STORM_ELEMENTAL:// Storm Elemental
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case WindArcher.BOW_BOOSTER:// Bow Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case WindArcher.EMERALD_FLOWER:
                eff.statups.put(CharacterTemporaryStat.PUPPET, 1);
                break;
            case WindArcher.SYLVAN_AID:// Sylvan Aid
                eff.statups.put(CharacterTemporaryStat.IndiePAD, (int) (eff.info.get(MapleStatInfo.indiePad) * 1.5));
                eff.statups.put(CharacterTemporaryStat.HowlingCritical, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.SoulArrow, 1);
                break;
            case WindArcher.ALBATROSS:
            case WindArcher.ALBATROSS_MAX:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(CharacterTemporaryStat.Albatross, eff.info.get(MapleStatInfo.x));
                break;
            case WindArcher.CALL_OF_CYGNUS:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case WindArcher.TOUCH_OF_THE_WIND:// Touch of the Wind
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.indieMhpR));
                break;
            case WindArcher.SHARP_EYES:// Sharp Eyes
                eff.statups.put(CharacterTemporaryStat.SharpEyes, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.CriticalGrowing, eff.info.get(MapleStatInfo.x));
                break;
            case WindArcher.GLORY_OF_THE_GUARDIANS:// Glory of the Guardians
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case WindArcher.STORM_BRINGER:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, 40);
                eff.statups.put(CharacterTemporaryStat.StormBringer, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
