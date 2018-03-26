package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Hayato;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class HayatoBuff extends AbstractBuffClass {

    public HayatoBuff() {
        skills = new int[]{
            Hayato.QUICK_DRAW,
            Hayato.SUMMER_RAIN,
            Hayato.MILITARY_MIGHT,
            Hayato.BATTOUJUTSU_SOUL,
            Hayato.BATTOUJUTSU_STANCE, // Battoujutsu Stance
            Hayato.WARRIORS_HEART,
            Hayato.KATANA_BOOSTER, // Katana Booster
            Hayato.IRON_SKIN, // Iron Skin
            Hayato.AKATSUKI_HERO, // Akatsuki Hero
            Hayato.PRINCESSS_VOW, // Princess's Vow
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.HAYATO.getId()
                || job == MapleJob.HAYATO1.getId()
                || job == MapleJob.HAYATO2.getId()
                || job == MapleJob.HAYATO3.getId()
                || job == MapleJob.HAYATO4.getId()
                || job == MapleJob.HAYATO5.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Hayato.QUICK_DRAW:
                eff.statups.put(CharacterTemporaryStat.BladeStance, 1);
                //eff.statups.put(CharacterTemporaryStat.HayatoStance, 2);
                //eff.statups.put(CharacterTemporaryStat.HayatoStanceBonus, 2);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Hayato.SUMMER_RAIN:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, 10);
                eff.info.put(MapleStatInfo.time, 20000);
                break;
            case Hayato.BATTOUJUTSU_SOUL:
                break;
            case Hayato.BATTOUJUTSU_STANCE: // Battoujutsu Stance
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.Battoujutsu, 1);
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.actionSpeed));
                eff.statups.put(CharacterTemporaryStat.HayatoStance, eff.info.get(MapleStatInfo.prop));
                break;
            case Hayato.MILITARY_MIGHT: // Military Might
                eff.statups.put(CharacterTemporaryStat.EMHP, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.EMMP, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.padX));
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                eff.statups.put(CharacterTemporaryStat.Jump, eff.info.get(MapleStatInfo.jump));
                break;
            case Hayato.WARRIORS_HEART:
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.damage));
                break;
            case Hayato.KATANA_BOOSTER: // Katana Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Hayato.IRON_SKIN: // Iron Skin,
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.asrR));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.terR));
                break;
            case Hayato.AKATSUKI_HERO: // Akatsuki Hero
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Hayato.PRINCESSS_VOW: // Princess's Vow
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
