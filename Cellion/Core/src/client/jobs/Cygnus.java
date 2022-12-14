/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.DawnWarrior;
import constants.skills.NightWalker;
import constants.skills.ThunderBreaker;
import constants.skills.WindArcher;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.StatEffect;
import enums.StatInfo;
import server.Timer;
import server.life.Mob;
import server.maps.objects.ForceAtom;
import enums.ForceAtomType;
import server.maps.objects.User;
import tools.Utility;
import tools.packet.BuffPacket;
import tools.packet.CField;

/**
 * Cygnus Knights Class Handlers
 *
 * @author Mazen Massoud
 */
public class Cygnus {

    public static class DawnWarriorHandler {

        public static void handleEquinox(User pPlayer) {
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.GlimmeringTime)) {
                
                if (pPlayer.getBuffedValue(CharacterTemporaryStat.PoseType) == 1) { // Falling Moon
                    pPlayer.dispelBuff(DawnWarrior.FALLING_MOON);
                    final StatEffect pEffect = SkillFactory.getSkill(DawnWarrior.RISING_SUN).getEffect(pPlayer.getTotalSkillLevel(DawnWarrior.RISING_SUN));
                    
                    pEffect.statups.put(CharacterTemporaryStat.PoseType, 2); // Rising Sun Stance
                    /*pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                    pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                    pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(MapleStatInfo.indieCr));
                    pEffect.statups.put(CharacterTemporaryStat.BuckShot, 1);*/
                    
                    final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
                    final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
                    pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
                    pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, DawnWarrior.RISING_SUN, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));
                } else { // Rising Sun
                    pPlayer.dispelBuff(DawnWarrior.RISING_SUN);
                    final StatEffect pEffect = SkillFactory.getSkill(DawnWarrior.FALLING_MOON).getEffect(pPlayer.getTotalSkillLevel(DawnWarrior.FALLING_MOON));
                    
                    pEffect.statups.put(CharacterTemporaryStat.PoseType, 1); // Falling Moon Stance
                    /*pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                    pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                    pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(MapleStatInfo.indieCr));*/
                    pEffect.statups.put(CharacterTemporaryStat.BuckShot, 1);
                    
                    final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
                    final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
                    pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
                    pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, DawnWarrior.FALLING_MOON, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));
                }
            }
        }
    }

    public static class BlazeWizardHandler {

    }

    public static class WindArcherHandler {
        
        public static void handleSecondWind(User pPlayer) {
            final StatEffect pEffect = SkillFactory.getSkill(WindArcher.SECOND_WIND).getEffect(pPlayer.getTotalSkillLevel(WindArcher.SECOND_WIND));

            pEffect.statups.put(CharacterTemporaryStat.IndiePADR, 5);
            pEffect.statups.put(CharacterTemporaryStat.PDD, pEffect.info.get(StatInfo.pddX));

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, 5000);
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, 5000, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, WindArcher.SECOND_WIND, 5000, pEffect.statups, pEffect));
        }
        
        public static void handleTriflingWind(User pPlayer, AttackInfo pAttack) {
            
            Skill pTrifling;
            if (pPlayer.hasSkill(WindArcher.TRIFLING_WIND_III_1)) pTrifling = SkillFactory.getSkill(WindArcher.TRIFLING_WIND_III_1);
            else if (pPlayer.hasSkill(WindArcher.TRIFLING_WIND_II_1)) pTrifling = SkillFactory.getSkill(WindArcher.TRIFLING_WIND_II_1);
            else if (pPlayer.hasSkill(WindArcher.TRIFLING_WIND_I)) pTrifling = SkillFactory.getSkill(WindArcher.TRIFLING_WIND_I);
            else return;
            
            int nMaxTriflingCount = pTrifling.getEffect(pPlayer.getTotalSkillLevel(pTrifling.getId())).info.get(StatInfo.x);
            int nProp = pTrifling.getEffect(pPlayer.getTotalSkillLevel(pTrifling.getId())).info.get(StatInfo.prop);
            int nSubProp = pTrifling.getEffect(pPlayer.getTotalSkillLevel(pTrifling.getId())).info.get(StatInfo.subProp);
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.TriflingWhimOnOff)) {
                for (int i = 0; i < nMaxTriflingCount; i++) {
                    int nFirstImpact = 36;
                    int nSecondImpact = 6;
                    int nAngle;

                    if (new Random().nextBoolean()) nAngle = 0;
                    else nAngle = 180;

                    for (AttackMonster pAttackMob : pAttack.allDamage) {
                        Mob pMob = (Mob) pPlayer.getMap().getMonsterByOid(pAttackMob.getObjectId());
                        if (Utility.resultSuccess(nProp)) {
                            if (Utility.resultSuccess(nSubProp)) {
                                int mobID = pAttackMob.getObjectId();
                                int inc = ForceAtomType.WA_ARROW_2.getInc();
                                int type = ForceAtomType.WA_ARROW_2.getForceAtomType();
                                ForceAtom pAtom = new ForceAtom(1, inc, nFirstImpact, nSecondImpact, nAngle, 0, (int) System.currentTimeMillis(), 1, 0, new Point(0, 0)); 
                                pPlayer.getMap().broadcastPacket(CField.createForceAtom(false, 0, pPlayer.getId(), type, true, mobID, WindArcher.TRIFLING_WIND_I_1, pAtom, new Rectangle(), 0, 300, pMob.getPosition(), WindArcher.TRIFLING_WIND_I_1, pMob.getPosition()));
                            } else {
                                int mobID = pAttackMob.getMonsterId();
                                int inc = ForceAtomType.WA_ARROW_1.getInc();
                                int type = ForceAtomType.WA_ARROW_1.getForceAtomType();
                                ForceAtom pAtom = new ForceAtom(1, inc, nFirstImpact, nSecondImpact, nAngle, 0, (int) System.currentTimeMillis(), 1, 0, new Point(0, 0)); 
                                pPlayer.getMap().broadcastPacket(CField.createForceAtom(false, 0, pPlayer.getId(), type, true, mobID, WindArcher.TRIFLING_WIND_I_1, pAtom, new Rectangle(), 0, 300, pMob.getPosition(), WindArcher.TRIFLING_WIND_I_1, pMob.getPosition()));
                            }
                        }
                    }
                }
            }
        }
        
    }

    public static class NightWalkerHandler {

        public static void handleShadowBat(User pPlayer, Mob pTarget) {
            if (pPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                if (Utility.resultSuccess(40)) {
                    int dwMobID = pTarget.getObjectId();
                    int nPosition = new Random().nextInt(80);
                    int nInc = ForceAtomType.NIGHT_WALKER_FROM_MOB_4.getInc();
                    int nType = ForceAtomType.NIGHT_WALKER_FROM_MOB_4.getForceAtomType();
                    ForceAtom forceAtomInfo = new ForceAtom(1, nInc, 3, 3, 90, 0, (int) System.currentTimeMillis(), 1, 0, new Point(-20, nPosition));
                    pPlayer.write(CField.createForceAtom(false, 0, pPlayer.getId(), nType, true, dwMobID, NightWalker.SHADOW_BAT, forceAtomInfo, new Rectangle(), 0, 300, pTarget.getPosition(), NightWalker.SHADOW_BAT, pTarget.getPosition()));
                }
            }
        }
        
        public static void handleDominionBuff(User pPlayer) {
            if (!GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                return;
            }
            final StatEffect buffEffects = SkillFactory.getSkill(NightWalker.DOMINION).getEffect(pPlayer.getTotalSkillLevel(NightWalker.DOMINION));

            buffEffects.statups.put(CharacterTemporaryStat.DamR, 20);
            buffEffects.statups.put(CharacterTemporaryStat.NotDamaged, 1);
            //buffEffects.statups.put(CharacterTemporaryStat.CriticalBuff, 100);
            //buffEffects.statups.put(CharacterTemporaryStat.Stance, 100);

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, 30000);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, 30000, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, 0, 30000, buffEffects.statups, buffEffects));
        }

    }

    public static class ThunderBreakerHandler {

        public static void handleLightningBuff(User pPlayer) {
            if (pPlayer == null && !GameConstants.isThunderBreakerCygnus(pPlayer.getJob()) && !pPlayer.hasSkill(ThunderBreaker.LIGHTNING_ELEMENTAL)) {
                return;
            }

            int nMaxCount = 1;
            double nChargeProp = 0.10;

            // Determining Maximum Amount of Lightning Buffs
            if (pPlayer.hasSkill(ThunderBreaker.ELECTRIFIED)) {
                nMaxCount++; // 2 Total Charges
                nChargeProp += 0.20; // 30% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.LIGHTNING_BOOST)) {
                nMaxCount++; // 3 Total Charges
                nChargeProp += 0.20; // 50% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.LIGHTNING_LORD)) {
                nMaxCount++; // 4 Total Charges
                nChargeProp += 0.30; // 80% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.THUNDER_GOD)) {
                nMaxCount++; // 5 Total Charges
                nChargeProp += 0.20; // 100% Total
            }

            // Handle Chance to Gain Lightning Buff
            if (new Random().nextDouble() <= nChargeProp) {

                // Update Lightning Buff Count
                if (pPlayer.getPrimaryStack() < nMaxCount) {
                    pPlayer.setPrimaryStack(pPlayer.getPrimaryStack() + 1);
                } else {
                    pPlayer.setPrimaryStack(nMaxCount);
                }
            }

            final StatEffect buffEffects = SkillFactory.getSkill(ThunderBreaker.LIGHTNING_ELEMENTAL).getEffect(pPlayer.getTotalSkillLevel(ThunderBreaker.LIGHTNING_ELEMENTAL));

            buffEffects.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pPlayer.getPrimaryStack()); // TODO: Get charges stacking properly.
            buffEffects.statups.put(CharacterTemporaryStat.StrikerHyperElectric, 1); // Hack fix to allow the use of Gale/Typhoon.

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, buffEffects.info.get(StatInfo.time));
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, buffEffects.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, ThunderBreaker.LIGHTNING_ELEMENTAL, buffEffects.info.get(StatInfo.time), buffEffects.statups, buffEffects));
        }
    }

    public static class MihileHandler {

    }
}
