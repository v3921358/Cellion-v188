package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Corsair;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CorsairBuff extends AbstractBuffClass {

    public CorsairBuff() {
        //Skills that have a handled effect
        skills = new int[]{
            Corsair.DOUBLE_DOWN,
            Corsair.PIRATES_REVENGE,
            Corsair.MAPLE_WARRIOR,
            Corsair.AHOY_MATEYS,
            Corsair.EPIC_ADVENTURE,
            Corsair.WHALERS_POTION
        };

        //Effects that create a buff icon.
        //just a test/concept. Not used rn.
        userEffects = new int[]{
            Corsair.DOUBLE_DOWN,
            Corsair.PIRATES_REVENGE,
            Corsair.MAPLE_WARRIOR,
            Corsair.AHOY_MATEYS,
            Corsair.EPIC_ADVENTURE,
            Corsair.WHALERS_POTION
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CORSAIR.getId()
                || job == MapleJob.CORSAIR_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Corsair.DOUBLE_DOWN:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case Corsair.PIRATES_REVENGE:
                eff.info.put(MapleStatInfo.cooltime, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DamR, (int) eff.info.get(MapleStatInfo.damR));
                break;
            case Corsair.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Corsair.AHOY_MATEYS:
                eff.info.put(MapleStatInfo.time, 120000);
                break;
            case Corsair.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Corsair.JOLLY_ROGER: //Jolly Roger
                eff.statups.put(CharacterTemporaryStat.IndiePADR, eff.info.get(MapleStatInfo.indiePad));//or x?
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.z));
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.x));
                break;
            case Corsair.WHALERS_POTION: //Whaler's Potion
                eff.statups.put(CharacterTemporaryStat.MaxHP, eff.info.get(MapleStatInfo.x)); //Max HP: +40%                  
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.y));//Status Ailment and Elemental Resistance: +15%
                eff.statups.put(CharacterTemporaryStat.Invincible, eff.info.get(MapleStatInfo.w)); //Damage Intake: -15%  
                break;
        }
    }
}
