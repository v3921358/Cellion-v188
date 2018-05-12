/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.*;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.StatEffect;
import server.StatInfo;
import server.Randomizer;
import server.Timer;
import server.maps.objects.User;
import server.life.Mob;
import server.maps.objects.ForceAtom;
import server.maps.objects.ForceAtomType;
import tools.Utility;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.JobPacket;
import tools.packet.JobPacket.ShadowerPacket;

/**
 * Explorer Class Handlers
 *
 * @author Mazen Massoud
 */
public class Explorer {

    public static class HeroHandler {

        public static void handleComboOrbs(User pPlayer, int nSkillID) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }

            int nCombo = pPlayer.getPrimaryStack();
            int nMaxOrbs = getMaxOrbs(pPlayer);

            switch (nSkillID) {
                case Crusader.PANIC:
                    nCombo -= 1;
                    break;
                case Crusader.SHOUT:
                    nCombo -= 2;
                    break;
                case constants.skills.Hero.PUNCTURE:
                    nCombo -= 2;
                    break;
                case constants.skills.Hero.ENRAGE:
                    nCombo -= 1;
                    break;
            }

            if (nCombo > nMaxOrbs) {
                nCombo = nMaxOrbs;
            } else if (nCombo < 0) {
                nCombo = 0;
            }

            pPlayer.setPrimaryStack(nCombo);
            setComboAttack(pPlayer, nCombo);
        }

        public static void handleComboAttack(User pPlayer) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }

            int nCombo = pPlayer.getPrimaryStack();
            int nGainChance = 0;
            int nMaxOrbs = getMaxOrbs(pPlayer);
            boolean bDoubleChance = false;

            if (pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                nGainChance = 40; // 40%
            }
            if (pPlayer.hasSkill(Crusader.COMBO_SYNERGY)) {
                nGainChance = 80; // 80%
            }
            if (pPlayer.hasSkill(constants.skills.Hero.ADVANCED_COMBO)) {
                bDoubleChance = true;
            }

            if (Randomizer.nextInt(100) < nGainChance) {
                if (pPlayer.getPrimaryStack() + 1 > nMaxOrbs) {
                    nCombo = nMaxOrbs;
                } else {
                    nCombo++;
                }
            } else {
                if (bDoubleChance) { // Re-roll chance if player has Advanced Combo.
                    if (pPlayer.getPrimaryStack() + 1 > nMaxOrbs) {
                        nCombo = nMaxOrbs;
                    } else {
                        nCombo++;
                    }
                }
            }

            pPlayer.setPrimaryStack(nCombo);
            setComboAttack(pPlayer, nCombo);
        }

        public static void setComboAttack(User pPlayer, int nAmount) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }

            int nDuration = 210000000;
            final StatEffect buffEffects = SkillFactory.getSkill(Fighter.COMBO_ATTACK).getEffect(pPlayer.getTotalSkillLevel(Fighter.COMBO_ATTACK));

            buffEffects.statups.put(CharacterTemporaryStat.ComboCounter, nAmount);

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Fighter.COMBO_ATTACK, nDuration, buffEffects.statups, buffEffects));
        }

        public static int getMaxOrbs(User pPlayer) {
            int nMaxOrbs = 0;

            if (pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                nMaxOrbs = 4;
            }
            if (pPlayer.hasSkill(constants.skills.Hero.ADVANCED_COMBO)) {
                nMaxOrbs = 9;
            }

            return nMaxOrbs;
        }
    }

    public static class ShadowerHandler {

        public static void handleBodyCount(User pPlayer) {
            if (pPlayer == null && !GameConstants.isThiefShadower(pPlayer.getJob()) && !pPlayer.hasSkill(Shadower.SHADOWER_INSTINCT)) {
                return;
            }

            int nBodyCount = pPlayer.getPrimaryStack();

            if (nBodyCount < 5) {
                nBodyCount++;
                pPlayer.setPrimaryStack(nBodyCount);

                if (nBodyCount == 5) {
                    pPlayer.dropMessage(-1, "Maximum Body Count Reached");
                }
            }

            ShadowerPacket.setKillingPoint(nBodyCount);
        }

        public static void handleShadowerInstinct(User pPlayer) {
            int nBodyCount = pPlayer.getPrimaryStack();

            // Apply the respected buff bonuses to the player.
            final StatEffect pEffect = SkillFactory.getSkill(Shadower.SHADOWER_INSTINCT).getEffect(pPlayer.getTotalSkillLevel(Shadower.SHADOWER_INSTINCT));

            pEffect.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, pPlayer.getSkillLevel(Shadower.SHADOWER_INSTINCT));
            pEffect.statups.put(CharacterTemporaryStat.PAD, (1 + nBodyCount) * pEffect.info.get(StatInfo.x));
            if (nBodyCount > 0) {
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, (nBodyCount) * pEffect.info.get(StatInfo.x));
            }

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Shadower.SHADOWER_INSTINCT, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));

            pPlayer.setPrimaryStack(0); // Set body count back to zero.
            pPlayer.dropMessage(-1, "Body Count Reset");
        }

        public static void handleFlipTheCoin(User pPlayer) {
            int nAmount = pPlayer.getAdditionalStack();

            if (pPlayer.hasBuff(CharacterTemporaryStat.FlipTheCoin)) {
                if (nAmount < 5) {
                    nAmount++;
                    pPlayer.setAdditionalStack(nAmount);
                }
            } else {
                nAmount = 1;
                pPlayer.setAdditionalStack(1);
            }

            // Apply the respected buff bonuses to the player.
            final StatEffect pEffect = SkillFactory.getSkill(Shadower.FLIP_OF_THE_COIN).getEffect(pPlayer.getTotalSkillLevel(Shadower.FLIP_OF_THE_COIN));

            pEffect.statups.put(CharacterTemporaryStat.FlipTheCoin, nAmount);
            pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, nAmount * 10);
            pEffect.statups.put(CharacterTemporaryStat.IndieDamR, nAmount * pEffect.info.get(StatInfo.indieDamR));

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Shadower.FLIP_OF_THE_COIN, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));

            // Turn off Flip The Coin in order for the player to require another critical strike for next use.
            pPlayer.getMap().broadcastPacket(ShadowerPacket.toggleFlipTheCoin(false));
            pPlayer.dropMessage(-1, "Flip of the Coin (" + nAmount + "/5)");
        }
    }

    public static class NightLordHandler {

        public static void handleAssassinsMark(User pPlayer, Mob pMob, AttackInfo pAttack) {
            if (pPlayer.hasBuff(CharacterTemporaryStat.NightLordMark)) {
                //for (AttackMonster pAttackMob : pAttack.allDamage) {
                if (Utility.resultSuccess(60)) {
                    int nMobID = pMob.getObjectId();
                    int nInc = ForceAtomType.ASSASSIN_MARK.getInc();
                    int nType = ForceAtomType.ASSASSIN_MARK.getForceAtomType();
                    ForceAtom forceAtomInfo = new ForceAtom(1, nInc, 20, 40,
                            0, 100, (int) System.currentTimeMillis(), 1, 0,
                            new Point());
                    pPlayer.getMap().broadcastPacket(CField.createForceAtom(false, nMobID, pPlayer.getId(), nType,
                            true, nMobID, Assassin.ASSASSINS_MARK, forceAtomInfo, new Rectangle(), 0, 300,
                            pMob.getPosition(), 2070000, pMob.getPosition()));
                }
                //}
            }
        }
    }
}
