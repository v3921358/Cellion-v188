/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs;

/**
 *
 * @author Mazen
 */
import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isBlaster;
import constants.skills.Blaster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

@BuffEffectManager
public class BlasterBuff extends AbstractBuffClass {

    public BlasterBuff() {
        skills = new int[]{
            Blaster.ARM_CANNON_BOOST,
            Blaster.MAPLE_WARRIOR};
    }

    @Override
    public boolean containsJob(int job) {
        return isBlaster(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Blaster.ARM_CANNON_BOOST:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Blaster.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
        }
    }

}
