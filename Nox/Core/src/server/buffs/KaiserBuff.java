package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isKaiser;
import constants.skills.Kaiser;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 * @author Mazen
 */
@BuffEffectManager
public class KaiserBuff extends AbstractBuffClass {

    public KaiserBuff() {
        skills = new int[]{
            Kaiser.DEFENDER_MODE,
            Kaiser.ATTACKER_MODE,
            Kaiser.FINAL_FORM,
            Kaiser.FINAL_FORM_1,
            Kaiser.FINAL_TRANCE,
            Kaiser.PIERCING_BLAZE,
            Kaiser.BLAZE_ON,
            Kaiser.CURSEBITE,
            Kaiser.CATALYZE,
            Kaiser.KAISERS_MAJESTY,
            Kaiser.NOVA_WARRIOR,
            Kaiser.GRAND_ARMOR,
            Kaiser.STONE_DRAGON};
    }

    @Override
    public boolean containsJob(int job) {
        return isKaiser(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Kaiser.DEFENDER_MODE:
                eff.statups.put(CharacterTemporaryStat.PDD, eff.info.get(MapleStatInfo.pddX));
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.mhpR));
                eff.statups.put(CharacterTemporaryStat.ReshuffleSwitch, 0);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Kaiser.ATTACKER_MODE:
                eff.statups.put(CharacterTemporaryStat.BdR, eff.info.get(MapleStatInfo.bdR));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.cr));
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.padX));
                eff.statups.put(CharacterTemporaryStat.ReshuffleSwitch, 0);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Kaiser.FINAL_FORM: // 3job kaiser
            case Kaiser.FINAL_FORM_1: // 4job kaiser
            case Kaiser.FINAL_TRANCE: // hyper kaiser 
                eff.statups.put(CharacterTemporaryStat.Morph, eff.info.get(MapleStatInfo.morph));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.cr));
                eff.statups.put(CharacterTemporaryStat.IndiePMdR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case Kaiser.PIERCING_BLAZE:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Kaiser.BLAZE_ON:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Kaiser.CURSEBITE:
                eff.statups.put(CharacterTemporaryStat.AsrR, -(eff.info.get(MapleStatInfo.asrR)));
                eff.statups.put(CharacterTemporaryStat.TerR, -(eff.info.get(MapleStatInfo.terR)));
                break;
            case Kaiser.CATALYZE:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case Kaiser.KAISERS_MAJESTY:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                break;
            case Kaiser.NOVA_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Kaiser.GRAND_ARMOR:
                eff.statups.put(CharacterTemporaryStat.DamageReduce, eff.info.get(MapleStatInfo.w));
                eff.statups.put(CharacterTemporaryStat.DamageReduce, eff.info.get(MapleStatInfo.v));
                break;
            case Kaiser.STONE_DRAGON:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
