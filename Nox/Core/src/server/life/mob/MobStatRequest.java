/*
 * Rexion Development
 */
package server.life.mob;

import constants.skills.IceLightningArchMage;
import constants.skills.IceLightningMage;
import constants.skills.IceLightningWizard;
import constants.skills.Priest;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.life.Mob;
import server.life.MobAttackInfo;
import server.maps.objects.User;
import server.skills.Option;
import tools.Utility;

/**
 * Apply Monster Status
 * @author Mazen Massoud
 */
public class MobStatRequest {
    
    public static void apply(User pPlayer, AttackInfo pAttack, MapleStatEffect pEffect) {
        Option nOption1 = new Option();
        Option nOption2 = new Option();
        Option nOption3 = new Option();
        
        for (AttackMonster pAttackMob : pAttack.allDamage) {
            Mob pMob = pAttackMob.getMonster();
            switch (pAttack.skill) {
                case IceLightningWizard.COLD_BEAM:
                    MobTemporaryStat pPoison = pMob.getTemporaryStat();
                    nOption1.nOption = 5;
                    nOption1.rOption = pAttack.skill;
                    nOption1.tOption = pEffect.info.get(MapleStatInfo.time) / 1000;
                    pPoison.addStatOptionsAndBroadcast(MobStat.Poison, nOption1);
                    break;
                case IceLightningMage.ICE_STRIKE:
                case IceLightningMage.GLACIER_CHAIN:
                    MobTemporaryStat pFreeze = pMob.getTemporaryStat();
                    nOption1.nOption = 5;
                    nOption1.rOption = pAttack.skill;
                    nOption1.tOption = pEffect.info.get(MapleStatInfo.time) / 1000;
                    pFreeze.addStatOptionsAndBroadcast(MobStat.Freeze, nOption1);
                    break;
                case IceLightningArchMage.CHAIN_LIGHTNING:
                case Priest.SHINING_RAY:
                    if (Utility.resultSuccess(pEffect.info.get(MapleStatInfo.prop))) {
                        MobTemporaryStat pStun = pMob.getTemporaryStat();
                        nOption1.nOption = 1;
                        nOption1.rOption = pAttack.skill;
                        nOption1.tOption = pEffect.info.get(MapleStatInfo.time) / 1000;
                        pStun.addStatOptionsAndBroadcast(MobStat.Stun, nOption1);
                    }
                    break;
            }
        }
    }
}
