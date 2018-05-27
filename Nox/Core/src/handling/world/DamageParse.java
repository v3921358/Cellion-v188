/*
 * Cellion Development 
 */
package handling.world;

import client.*;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.skills.*;
import client.jobs.Cygnus.ThunderBreakerHandler;
import client.jobs.Explorer.NightLordHandler;
import client.jobs.Hero.AranHandler;
import client.jobs.Hero.PhantomHandler;
import client.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import server.StatEffect;
import server.Randomizer;
import server.life.*;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.messages.StylishKillMessage;
import server.messages.StylishKillMessage.StylishKillMessageType;
import tools.Pair;
import tools.packet.WvsContext;
import java.awt.*;
import java.util.*;
import java.util.List;
import server.life.mob.MobStatRequest;
import server.maps.TrainingMap;
import server.maps.objects.ForceAtom;
import server.maps.objects.ForceAtomType;
import service.RecvPacketOpcode;
import tools.LogHelper;
import tools.Utility;
import tools.packet.CField;
import tools.packet.JobPacket;
import tools.packet.JobPacket.ShadowerPacket;
import tools.packet.MobPacket;

/**
 * Damage Parse
 * @author Mazen Massoud
 * @purpose Handles all attack types and damage calculations throughout the server.
 * 
 * Index:
 * @method OnWeaponAttackRequest : Handles weapon related types of attacks and handle extra features.
 * @method OnMagicAttackRequest : Handles weapon related types of attacks and handle extra features.
 * @method UserMaxWeaponDamage : Calculates the players maximum damage for weapon based type attacks.
 * @method UserMaxMagicDamage : Calculates the players maximum damage for magic based type attacks.
 * @method OnCriticalAttack : Modifies the damage of critical attacks and handle extra features.
 * @method OnAttack : Handles the received packet and applies the damage to monsters.
 * @method OnMultiKill : Handles multiple monster kills to increase combo count and drop experience orbs.
 */
public class DamageParse {

    /**
     * OnWeaponAttack
     * @param pAttack
     * @param pSkill
     * @param pPlayer
     * @param nAttackCount
     * @param nMaxDamagePerMonster
     * @param pEffect
     * @param pAttackType 
     */
    public static void OnWeaponAttackRequest(AttackInfo pAttack, Skill pSkill, User pPlayer, int nAttackCount, double nMaxDamagePerMonster, StatEffect pEffect, AttackType pAttackType) {
       
        if (ServerConstants.ADMIN_MODE) pPlayer.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((pAttack.display & 0x8000) != 0 ? pAttack.display - 32768 : pAttack.display)).toString());
        
        if (!pPlayer.isAlive()) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        } else if (!pPlayer.getMap().getSharedMapResources().noSkillInfo.isSkillUsable(pPlayer.getJob(), pAttack.skill)) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_IN_UNAVAILABLE_MAP);
            return;
        } else if (pAttack.skill == 80001593) {
            pPlayer.yellowMessage("[AntiCheat] Please remember hacking and the use of 3rd party modifications go against our ToS.");
            return;
        }
        
        if (pAttack.real && GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 400) {
            pPlayer.getCheatTracker().checkAttack(pAttack.skill, pAttack.lastAttackTickCount);
        }

        if (pAttack.skill != 0 && pEffect != null) {
            if (GameConstants.isMulungSkill(pAttack.skill)) {
                if (pPlayer.getMapId() / 10000 != 92502) {
                    return;
                }
                if (pPlayer.getMulungEnergy() < 10000) {
                    return;
                }
                pPlayer.mulungEnergyModifier(false);
            } else if (GameConstants.isPyramidSkill(pAttack.skill)) {
                if (pPlayer.getMapId() / 1000000 != 926) {
                    return;
                }
                if (pPlayer.getPyramidSubway() != null && pPlayer.getPyramidSubway().onSkillUse(pPlayer)) {
                    //TODO: Do something here
                }
            } else if (GameConstants.isInflationSkill(pAttack.skill)) {
                if (pPlayer.getBuffedValue(CharacterTemporaryStat.Inflation) != null) {
                    //TODO: Do something here
                }
            } else if (pAttack.mobCount > pEffect.getMobCount() && !GameConstants.isMismatchingBulletSkill(pAttack.skill)) {
                if (pPlayer.isDeveloper()) {
                    pPlayer.yellowMessage("[Debug] Please add Skill (" + pAttack.skill + ") to GameConstants.isMismatchingBulletSkill method.");
                }
                //pPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                //return;
            }
        }

        boolean bUseAttackCount = pAttack.skill != Marksman.SNIPE && pAttack.skill != Mercedes.LIGHTNING_EDGE;

        if (pAttack.numberOfHits > 0 && pAttack.mobCount > 0) {
            if (!pPlayer.getStat().checkEquipDurabilitys(pPlayer, -1)) {
                pPlayer.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
                return;
            }
        }
        int nTotalDamage = 0;
        MapleMap pMap = pPlayer.getMap();
        int nTotalDamageToOneMonster = 0;
        long nMobHP = 0L;
        PlayerStats pPlayerStat = pPlayer.getStat();

        final int nPlayerDamageCap = GameConstants.damageCap + pPlayer.getStat().damageCapIncrease; // The damage cap the player is allowed to hit.
        int nCriticalDamage = pPlayerStat.passive_sharpeye_percent();
        int nShdowPartnerAttackPercentage = 0;
        if (pAttackType == AttackType.RANGED_WITH_ShadowPartner || pAttackType == AttackType.NON_RANGED_WITH_MIRROR) {
            StatEffect shadowPartnerEffect = pPlayer.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
            if (shadowPartnerEffect != null) {
                nShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
            }
            nAttackCount /= 2;
        }
        nShdowPartnerAttackPercentage *= (nCriticalDamage + 100) / 100;
        if (pAttack.skill == Shadower.ASSASSINATE) {
            nShdowPartnerAttackPercentage *= 10;
        }

        double nMaxDamagePerHit = 0.0D;

        for (AttackMonster pAttackMob : pAttack.allDamage) {
            Mob pTarget = pAttackMob.getMonster();
            if (pTarget == null || pTarget.getId() != pAttackMob.getMonsterId()) {
                continue;
            }

            if (pTarget.getLinkCID() < 1) {
                nTotalDamageToOneMonster = 0;
                nMobHP = pTarget.getMobMaxHp();
                MonsterStats pMobStat = pTarget.getStats();
                int nFixedDamage = pMobStat.getFixedDamage();
                boolean bTempest = pTarget.getStatusSourceID(MonsterStatus.FREEZE) == Paladin.HEAVENS_HAMMER;

                if (!bTempest && pPlayer.isGM()) {
                    
                    if ((pPlayer.getJob() >= MapleJob.BATTLE_MAGE_1.getId() && pPlayer.getJob() <= MapleJob.BATTLE_MAGE_4.getId() && !pTarget.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !pTarget.isBuffed(MonsterStatus.MAGIC_IMMUNITY)
                            && !pTarget.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) || pAttack.skill == Marksman.SNIPE
                            || pAttack.skill == Mercedes.LIGHTNING_EDGE || ((pPlayer.getJob() < MapleJob.BATTLE_MAGE_1.getId() || pPlayer.getJob() > MapleJob.BATTLE_MAGE_4.getId()) && !pTarget.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !pTarget.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !pTarget.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT))) {
                        nMaxDamagePerHit = UserMaxWeaponDamage(pPlayer, pTarget, pAttack, pSkill, pEffect, nMaxDamagePerMonster, nCriticalDamage);
                    } else {
                        nMaxDamagePerHit = 1.0D;
                    }
                }
                if (pMobStat.isBoss()) {
                    nMaxDamagePerHit *= pPlayerStat.bossdam_r / 100d;
                } else {
                    nMaxDamagePerHit *= pPlayerStat.dam_r / 100d;
                }

                byte nTotalAttackCount = 0;

                int nCriticalCount = 0;
                for (Pair pAttacks : pAttackMob.getAttacks()) {
                    long nDamageLine = (Long) pAttacks.left;
                    nTotalAttackCount = (byte) (nTotalAttackCount + 1);
                    if (((Boolean) pAttacks.right)) {
                        nCriticalCount++;
                    }
                    if ((bUseAttackCount) && (nTotalAttackCount - 1 == nAttackCount)) {
                        nMaxDamagePerHit = nMaxDamagePerHit / 100.0D * (nShdowPartnerAttackPercentage / 100.0D);
                    }

                    if (nFixedDamage != -1) {
                        if (pMobStat.getOnlyNoramlAttack()) {
                            nDamageLine = pAttack.skill != 0 ? 0 : nFixedDamage;
                        } else {
                            nDamageLine = nFixedDamage;
                        }
                    } else if (pMobStat.getOnlyNoramlAttack()) {
                        nDamageLine = pAttack.skill != 0 ? 0 : Math.min(nDamageLine, (int) nMaxDamagePerHit);
                    } else if (!pPlayer.isGM() 
                            && pAttack.skill != Global.LEVEL_UP) { // Add exceptions here when a skill doesn't do damage, but is being parsed correctly. -Mazen
                        
                        if (bTempest) {
                            if (nDamageLine > pTarget.getMobMaxHp()) {
                                nDamageLine = (int) Math.min(pTarget.getMobMaxHp(), Integer.MAX_VALUE);
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
                            }
                        } else if (((pPlayer.getJob() >= 3200) && (pPlayer.getJob() <= 3212) && (!pTarget.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!pTarget.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!pTarget.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) || (pAttack.skill == 23121003) || (((pPlayer.getJob() < 3200) || (pPlayer.getJob() > 3212)) && (!pTarget.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!pTarget.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) && (!pTarget.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)))) {
                            if (nDamageLine > nMaxDamagePerHit) {
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE, new StringBuilder().append("[Damage: ").append(nDamageLine).append(", Expected: ").append(nMaxDamagePerHit).append(", Mob: ").append(pTarget.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(pAttack.skill).append("]").toString());
                                if (pAttack.real) {
                                    pPlayer.getCheatTracker().checkSameDamage(nDamageLine, nMaxDamagePerHit);
                                }
                                if (nDamageLine > nMaxDamagePerHit * 2.0D) {
                                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_2, new StringBuilder().append("[Damage: ").append(nDamageLine).append(", Expected: ").append(nMaxDamagePerHit).append(", Mob: ").append(pTarget.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(pAttack.skill).append("]").toString());
                                    if (nMaxDamagePerHit != 0) {
                                        nDamageLine = (int) (nMaxDamagePerHit * 2.0D);
                                    }
                                    if (nDamageLine >= nPlayerDamageCap) {
                                        return;
                                        //pPlayer.getClient().Close();
                                    }
                                }
                            }
                        } else if (nDamageLine > nMaxDamagePerHit && nMaxDamagePerHit != 0) {
                            nDamageLine = (int) nMaxDamagePerHit;
                        }
                    }
                    
                    nTotalDamageToOneMonster += nDamageLine;

                    if ((nDamageLine == 0 || pTarget.getId() == 9700021) && pPlayer.getPyramidSubway() != null) {
                        pPlayer.getPyramidSubway().onMiss(pPlayer);
                    }
                }
                
                nTotalDamage += nTotalDamageToOneMonster;

                if (pPlayer.isDeveloper()) pPlayer.yellowMessage("[Debug] Skill ID (" + pAttack.skill + ") - Damage (" + nTotalDamageToOneMonster + ")");
                pTarget.damage(pPlayer, nTotalDamageToOneMonster, true, pAttack.skill); // Apply attack to the monster hit.

                if (pTarget.isAlive()) { // Monster is still alive after being hit.
                    //pPlayer.checkMonsterAggro(pTarget);
                } else {
                    pAttack.after_NumMobsKilled++;
                }

                if (pPlayer.getSkillLevel(36110005) > 0) {
                    Skill skill = SkillFactory.getSkill(36110005);
                    StatEffect eff = skill.getEffect(pPlayer.getSkillLevel(skill));
                    if (pPlayer.getLastComboTime() + 5000 < System.currentTimeMillis()) {
                        pTarget.setTriangulation(0);
                        //player.clearDamageMeters();
                    }
                    if (eff.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (pTarget.getTriangulation() < 3) {
                            pTarget.setTriangulation(pTarget.getTriangulation() + 1);
                        }
                        pTarget.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.DARKNESS, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                        pTarget.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.TRIANGULATION, pTarget.getTriangulation(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                    }
                }

                if (pPlayer.getBuffedValue(CharacterTemporaryStat.PickPocket) != null) {
                    if (Randomizer.nextInt(99) <= pPlayer.getStat().pickRate) {
                        pPlayer.getMap().spawnMesoDrop(1, new Point((int) (pTarget.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0D), (int) pTarget.getTruePosition().getY()), pTarget, pPlayer, false, (byte) 0);
                    }
                }

                /*
                 *  Final Attack Handling
                 *  @author Mazen Massoud
                 * 
                 *  @purpose Handle Final Attack system without the need for the packet. 
                 */
                if (pPlayer.hasSkill(pPlayer.getFinalAttackSkill())) {
                    // TODO: Handle nPropChance and nDamage based on skill level.
                    int nPropChance = 30;
                    long nFinalAttackDamage = (long) (nTotalDamageToOneMonster * 0.2);
                    for (AttackMonster pFinalAttack : pAttack.allDamage) {
                        if (Randomizer.nextInt(100) < nPropChance) {
                            //pPlayer.getClient().write(CField.finalAttackRequest(pPlayer, attack.skill, pPlayer.getFinalAttackSkill(), 0, monster.getId(), (int) System.currentTimeMillis()));
                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    pTarget.damage(pPlayer, nFinalAttackDamage, true, pPlayer.getFinalAttackSkill());
                                    pPlayer.getClient().SendPacket(MobPacket.showMonsterHP(pTarget.getObjectId(), pTarget.getHPPercent()));
                                    cancel();
                                }
                            }, 500); // 0.5 Second Delay
                        }
                    }
                }

                // Megido Flame Custom Handling
                if (pPlayer.getSkillLevel(SkillFactory.getSkill(2121055)) > 0 || pPlayer.getSkillLevel(SkillFactory.getSkill(2121052)) > 0/* || player.getJob() == 1211 || player.getJob() == 1212*/) {
                    int percent = 45;
                    int percent2 = 25;
                    for (AttackMonster at : pAttack.allDamage) {
                        Mob mob = pMap.getMonsterByOid(at.getObjectId());
                        if (pMap.getMonsterByOid(at.getObjectId()).getStats().isBoss()) {
                            if (Randomizer.nextInt(100) < percent) {
                                if (mob != null) {
                                    pPlayer.getClient().SendPacket(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        } else {
                            if (Randomizer.nextInt(100) < percent2) {
                                if (mob != null) {
                                    pPlayer.getClient().SendPacket(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        }
                    }
                }

                if ((nTotalDamageToOneMonster > 0) || (pAttack.skill == 1221011) || (pAttack.skill == 21120006)) {
                    
                    if (GameConstants.isKinesis(pPlayer.getJob())) {
                        KinesisHandler.handlePsychicPoint(pPlayer, pAttack.skill);
                    }
                    if (GameConstants.isShade(pPlayer.getJob())) {
                        if (pPlayer.hasBuff(CharacterTemporaryStat.ChangeFoxMan)) {
                            for (AttackMonster at : pAttack.allDamage) {
                                int nPercent = 35;
                                if (Randomizer.nextInt(100) < nPercent && pAttack.skill != 25100010 && pAttack.skill != 25100010) {
                                    pPlayer.getMap().broadcastPacket(JobPacket.ShadePacket.FoxSpirit(pPlayer, at));
                                }
                            }
                        }
                    }
                    if (GameConstants.isAran(pPlayer.getJob())) {
                        switch (pAttack.skill) {
                            case Aran.SMASH_SWING:
                            case Aran.SMASH_SWING_1:
                            case Aran.SMASH_SWING_2:
                                AranHandler.handleSwingStudies(pPlayer);
                                break;
                        }
                    }
                    if (GameConstants.isThunderBreakerCygnus(pPlayer.getJob())) {
                        ThunderBreakerHandler.handleLightningBuff(pPlayer);
                    }
                    if (GameConstants.isDemonSlayer(pPlayer.getJob())) {
                        pPlayer.handleForceGain(pTarget.getObjectId(), pAttack.skill);
                    }
                    if ((GameConstants.isPhantom(pPlayer.getJob())) && (pAttack.skill != 24120002) && (pAttack.skill != 24100003)) {
                        if (pPlayer.hasSkill(Phantom.CARTE_BLANCHE)) {
                            if (Randomizer.nextInt(100) < 20) {
                                pPlayer.getMap().broadcastPacket(JobPacket.PhantomPacket.ThrowCarte(pPlayer, 0/*at.getObjectId()*/));
                                PhantomHandler.handleDeck(pPlayer);
                            }
                        }
                    }
                    if (GameConstants.isXenon(pPlayer.getJob())) {
                        if (pPlayer.hasBuff(CharacterTemporaryStat.HollowPointBullet)) {
                            if (Randomizer.nextInt(100) < 30) {
                                pPlayer.getMap().broadcastPacket(JobPacket.XenonPacket.EazisSystem(pPlayer.getId(), 0));
                            }
                        }
                    }
                    if (GameConstants.isKaiser(pPlayer.getJob())) {
                        for (int i = 0; i < pAttack.mobCount; i++) {
                            pPlayer.handleKaiserCombo();
                        }
                    }
                    if (GameConstants.isThiefNightLord(pPlayer.getJob())) {
                        NightLordHandler.handleAssassinsMark(pPlayer, pTarget, pAttack);
                    }
                    if (GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                        if (pPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                            if (Utility.resultSuccess(40)) {
                                int mobID = pTarget.getObjectId();
                                int position = new Random().nextInt(80);
                                int inc = ForceAtomType.NIGHT_WALKER_FROM_MOB_4.getInc();
                                int type = ForceAtomType.NIGHT_WALKER_FROM_MOB_4.getForceAtomType();
                                ForceAtom forceAtomInfo = new ForceAtom(1, inc, 3, 3, 90, 0, (int) System.currentTimeMillis(), 1, 0, new Point(-20, position));
                                pPlayer.write(CField.createForceAtom(false, 0, pPlayer.getId(), type, true, mobID, NightWalker.SHADOW_BAT, forceAtomInfo, new Rectangle(), 0, 300, pTarget.getPosition(), NightWalker.SHADOW_BAT, pTarget.getPosition()));
                            }
                        }
                        
                        pPlayer.handleDarkElemental(); // Dark Elemental Stack Count Handler
                        for (int i = pPlayer.getDarkElementalCombo(); i > 0; i--) { // Damage Increase Handler for Dark Elemental Mark Stacks
                            nTotalDamageToOneMonster += (nTotalDamageToOneMonster * 0.8); // 80% Increase Damage per Stack
                        }
                    }
                    if (pTarget.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                        pPlayer.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    
                    pPlayer.onAttack(pTarget.getMobMaxHp(), pTarget.getMobMaxMp(), pAttack.skill, pTarget.getObjectId(), nTotalDamage, 0);
                    
                    if (GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 300 // Originally 100
                            && !GameConstants.isNoDelaySkill(pAttack.skill) && (pAttack.skill != 3101005) && (!pTarget.getStats().isBoss()) && (pPlayer.getTruePosition().distanceSq(pTarget.getTruePosition()) > GameConstants.getAttackRange(pEffect, pPlayer.getStat().defRange))) {
                        pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[Distance: ").append(pPlayer.getTruePosition().distanceSq(pTarget.getTruePosition())).append(", Expected Distance: ").append(GameConstants.getAttackRange(pEffect, pPlayer.getStat().defRange)).append(" Job: ").append(pPlayer.getJob()).append("]").toString());
                    }
                }
            }

            OnMultiKill(pAttack, pPlayer); // Handle multi kills


            // TODO: Clean up the stuff below later. -Mazen
            if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
                if (pPlayer.getSkillLevel(31010002) > 0) {
                    StatEffect eff = SkillFactory.getSkill(31010002).getEffect(pPlayer.getSkillLevel(31010002));
                    if (eff.makeChanceResult()) {
                        if (pPlayer.getExceed() / 2 > ((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX())) {
                            pPlayer.addHP((int) Math.min((nTotalDamageToOneMonster * ((((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (pPlayer.getExceed() / 2))) / 100.0D)) * -1, pPlayer.getStat().getCurrentMaxHp() / 2));
                        } else {
                            pPlayer.addHP((int) Math.min((nTotalDamageToOneMonster * ((((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (pPlayer.getExceed() / 2))) / 100.0D)), pPlayer.getStat().getCurrentMaxHp() / 2));
                        }
                    }
                }
            }

            if (pPlayer.getBuffSource(CharacterTemporaryStat.AranDrain) == Aran.DRAIN) {
                Skill skill = SkillFactory.getSkill(Aran.DRAIN);
                pPlayer.addHP(Math.min(nTotalDamage / 5, (nTotalDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100));
            }
            if (pPlayer.hasSkill(DarkKnight.DARK_THIRST)) { // Hack fix for now.
                Skill skill = SkillFactory.getSkill(DarkKnight.DARK_THIRST);
                pPlayer.addHP(Math.min(nTotalDamage / 5, (nTotalDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100));
            }
            if (pPlayer.hasSkill(DemonAvenger.LIFE_SAP)) {
                Skill skill = SkillFactory.getSkill((DemonAvenger.LIFE_SAP));
                int nLifeGain = Math.min(nTotalDamage / 2, (nTotalDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100);
                if (pPlayer.hasSkill(DemonAvenger.ADVANCED_LIFE_SAP)) {
                    nLifeGain *= 2;
                }
                pPlayer.addHP(nLifeGain);
            }

            if (pPlayer.getJob() == 422) { // Prime Critical
                int nPrimeCritical = pPlayer.acaneAim;
                if (pPlayer.acaneAim <= 23) {
                    pPlayer.acaneAim++;
                }
            }

            if (GameConstants.isLuminous(pPlayer.getJob())) {
                final Integer darkcrescendo_value = pPlayer.getBuffedValue(CharacterTemporaryStat.StackBuff);
                if (darkcrescendo_value != null && darkcrescendo_value != 1) {
                    StatEffect crescendo = SkillFactory.getSkill(27121005).getEffect(pPlayer.getSkillLevel(27121005));
                    if (crescendo != null) {

                        if (crescendo.makeChanceResult()) {
                            pPlayer.setLastCombo(System.currentTimeMillis());
                            if (pPlayer.acaneAim <= 29) {
                                pPlayer.acaneAim++;
                                crescendo.applyTo(pPlayer);
                            }
                        }
                    }
                }
            }

            if (pPlayer.getJob() >= 1500 && pPlayer.getJob() <= 1512) {
                StatEffect crescendo = SkillFactory.getSkill(15001022).getEffect(pPlayer.getSkillLevel(15001022));
                if (crescendo != null) {

                    if (crescendo.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (pPlayer.acaneAim <= 3) {
                            pPlayer.acaneAim++;
                            crescendo.applyTo(pPlayer);
                        }
                    }
                }
            }

            // Combo
            if (pPlayer.getJob() >= 420 && pPlayer.getJob() <= 422) {
                StatEffect crescendo = SkillFactory.getSkill(4200013).getEffect(pPlayer.getSkillLevel(4200013));
                if (crescendo != null) {

                    if (crescendo.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (pPlayer.acaneAim <= 30) {
                            pPlayer.acaneAim++;
                            crescendo.applyTo(pPlayer);
                        }
                    }
                }
            }
            if ((pAttack.skill == 4331003) && ((nMobHP <= 0L) || (nTotalDamageToOneMonster < nMobHP))) {
                return;
            }
            if ((nMobHP >= 0L) && (nTotalDamageToOneMonster > 0)) {
                pPlayer.afterAttack(pAttack.mobCount, pAttack.numberOfHits, pAttack.skill);
            }
            if ((pAttack.skill != 0) && ((pAttack.mobCount > 0) || ((pAttack.skill != 4331003) && (pAttack.skill != 4341002))) && (!GameConstants.isNoDelaySkill(pAttack.skill))) {
                if (pEffect != null) {
                    boolean applyTo = pEffect.applyTo(pPlayer, pAttack.position);
                }
            }
            if ((nTotalDamage > 1) && (GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 100)) {
                CheatTracker tracker = pPlayer.getCheatTracker();

                tracker.setAttacksWithoutHit(true);
                if (tracker.getAttacksWithoutHit() > 1000) {
                    tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
                }
            }
            if (pPlayer.getSkillLevel(4100012) > 0) {
                StatEffect eff = SkillFactory.getSkill(4100012).getEffect(pPlayer.getSkillLevel(4100012));
                if (eff.makeChanceResult()) {
                    for (Map.Entry z : pEffect.getMonsterStati().entrySet()) {
                        for (AttackMonster ap : pAttack.allDamage) {
                            final Mob monster = pPlayer.getMap().getMonsterByOid(ap.getObjectId());
                            monster.applyStatus(pPlayer, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), pSkill.getId(), null, false), pEffect.isPoison(), pEffect.getDuration(), true, pEffect);
                            monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.POISON, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                            monster.applyStatus(pPlayer, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), pSkill.getId(), null, false), pEffect.isPoison(), pEffect.getDuration(), true, pEffect);
                        }
                    }
                }

                int bulletCount = eff.getBulletCount();
                for (AttackMonster ap : pAttack.allDamage) {
                    final Mob source = pPlayer.getMap().getMonsterByOid(ap.getObjectId());
                    final MonsterStatusEffect check = source.getBuff(MonsterStatus.POISON);
                    if (check != null && check.getSkill() == 4100011 && check.getOwnerId() == pPlayer.getId()) { // :3
                        final List<MapleMapObject> objs = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 500000,
                                Arrays.asList(MapleMapObjectType.MONSTER));
                        final List<Mob> monsters = new ArrayList<>();
                        for (int i = 0; i < bulletCount; i++) {
                            int rand = Randomizer.rand(0, objs.size() - 1);
                            if (objs.size() < bulletCount) {
                                if (i < objs.size()) {
                                    monsters.add((Mob) objs.get(i));
                                }
                            } else {
                                monsters.add((Mob) objs.get(rand));
                                objs.remove(rand);
                            }
                        }
                        if (monsters.size() <= 0) {
                            WvsContext.enableActions();
                            return;
                        }
                        final List<Point> points = new ArrayList<>();
                        for (Mob mob : monsters) {
                            points.add(mob.getPosition());
                        }
                        pPlayer.getMap().broadcastPacket(WvsContext.giveMarkOfTheif(pPlayer.getId(), source.getObjectId(), 4100012, monsters, pPlayer.getPosition(), monsters.get(0).getPosition(), 2070005));
                    }
                }
            }
            if (pPlayer.getJob() == 412) {
                for (AttackMonster ap : pAttack.allDamage) {
                    final List<MapleMapObject> objs = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 500000, Arrays.asList(MapleMapObjectType.MONSTER));
                    final List<Mob> monsters = new ArrayList<>();
                    pPlayer.getMap().broadcastPacket(WvsContext.giveMarkOfTheif(pPlayer.getId(), ap.getObjectId(), 4100012, monsters, pPlayer.getPosition(), ap.getPosition(), 2070005));
                }
            }
        }
    }
    
    /**
     * OnMagicAttack
     * @param pAttack
     * @param pSkill
     * @param pPlayer
     * @param pEffect 
     */
    public static void OnMagicAttackRequest(AttackInfo pAttack, Skill pSkill, User pPlayer, StatEffect pEffect) {
        if (ServerConstants.ADMIN_MODE) pPlayer.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((pAttack.display & 0x8000) != 0 ? pAttack.display - 32768 : pAttack.display)).toString());
        
        if (!pPlayer.isAlive()) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        } else if (!pPlayer.getMap().getSharedMapResources().noSkillInfo.isSkillUsable(pPlayer.getJob(), pAttack.skill)) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_IN_UNAVAILABLE_MAP);
            return;
        }
        
        if (pAttack.real && GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 200) {
            pPlayer.getCheatTracker().checkAttack(pAttack.skill, pAttack.lastAttackTickCount);
        }

        if (pEffect != null) {
            if (pEffect.getBulletCount() > 1) {
                if ((pAttack.numberOfHits > pEffect.getBulletCount()) || (pAttack.mobCount > pEffect.getMobCount())) {
                    if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[Warning] Check DamageParse.java at line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ".");
                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                    //return;
                }
            } else if (((pAttack.numberOfHits > pEffect.getAttackCount()) && (pEffect.getAttackCount() != 0)) || (pAttack.mobCount > pEffect.getMobCount())) {
                if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[Warning] Check DamageParse.java at line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ".");
                pPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                //return;
            }

        }
        if ((pAttack.numberOfHits > 0) && (pAttack.mobCount > 0) && (!pPlayer.getStat().checkEquipDurabilitys(pPlayer, -1))) {
            pPlayer.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }

        double nMaxDamage = pPlayer.getStat().getCurrentMaxBaseDamage() * (pEffect.getDamage() + pPlayer.getStat().getDamageIncrease(pAttack.skill)) / 100.0D;

        if (pSkill != null && GameConstants.isBeginnerJob(pSkill.getId() / 10000) && pSkill.getId() % 10000 == 1000) {
            nMaxDamage = 40.0D;
        } else if (GameConstants.isMulungSkill(pAttack.skill)) {
            if (pPlayer.getMapId() / 10000 != 92502) {
                return;
            }
            if (pPlayer.getMulungEnergy() < 10000) {
                return;
            }
            pPlayer.mulungEnergyModifier(false);
        } else if (GameConstants.isPyramidSkill(pAttack.skill)) {
            nMaxDamage = 1.0D;

            if (pPlayer.getMapId() / 1000000 != 926) {
                return;
            }
            if ((pPlayer.getPyramidSubway() != null) && (pPlayer.getPyramidSubway().onSkillUse(pPlayer)));
        } else if ((GameConstants.isInflationSkill(pAttack.skill)) && (pPlayer.getBuffedValue(CharacterTemporaryStat.Inflation) == null)) {
            return;
        }

        PlayerStats pPlayerStat = pPlayer.getStat();

        Element pElement = null;
        if (pPlayer.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
            pElement = Element.NEUTRAL;
        } else if (pSkill != null) {
            pElement = pSkill.getElement();
        }

        double nMaxDamagePerHit = 0.0D;
        int nTotalDamage = 0;

        final int nPlayerDamageCap = GameConstants.damageCap + pPlayer.getStat().damageCapIncrease; // The damage cap the player is allowed to hit.
        int nCriticalDamage = pPlayerStat.passive_sharpeye_percent();
        Skill pEaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(pPlayer.getJob()));
        int nEaterSLV = pPlayer.getTotalSkillLevel(pEaterSkill);

        for (AttackMonster pAttackMob : pAttack.allDamage) {
            final Mob pMob = pAttackMob.getMonster();
            if (pMob == null || pMob.getId() != pAttackMob.getMonsterId()) {
                continue;
            }

            if (pMob.getLinkCID() <= 0) {
                boolean bTempest = (pMob.getStatusSourceID(MonsterStatus.FREEZE) == 21120006) && (!pMob.getStats().isBoss());
                int nTotalDamageToOneMonster = 0;
                MonsterStats pMobStat = pMob.getStats();
                int nFixedDamage = pMobStat.getFixedDamage();

                if (!bTempest && !pPlayer.isGM()) {
                    if ((!pMob.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!pMob.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
                        nMaxDamagePerHit = UserMaxMagicDamage(pPlayer, pSkill, pMob, pMobStat, pPlayerStat, pElement, nCriticalDamage, nMaxDamage, pEffect);
                    } else {
                        nMaxDamagePerHit = 1.0D;
                    }
                }

                byte nTotalAttackCount = 0;

                for (Pair pAttacks : pAttackMob.getAttacks()) {
                    long nDamageLine = (Long) pAttacks.left;
                    nTotalAttackCount = (byte) (nTotalAttackCount + 1);
                    if (nFixedDamage != -1) {
                        nDamageLine = pMobStat.getOnlyNoramlAttack() ? 0 : nFixedDamage;
                    } else if (pMobStat.getOnlyNoramlAttack()) {
                        nDamageLine = 0;
                    } else if (!pPlayer.isGM()) {
                        if (bTempest) {
                            if (nDamageLine > pMob.getMobMaxHp()) {
                                nDamageLine = (int) Math.min(pMob.getMobMaxHp(), 2147483647L);
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC);
                            }
                        } else if ((!pMob.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!pMob.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
                            if (nDamageLine > nMaxDamagePerHit) {
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC, new StringBuilder().append("[Damage: ").append(nDamageLine).append(", Expected: ").append(nMaxDamagePerHit).append(", Mob: ").append(pMob.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(pAttack.skill).append("]").toString());
                                if (pAttack.real) {
                                    pPlayer.getCheatTracker().checkSameDamage(nDamageLine, nMaxDamagePerHit);
                                }
                                if (nDamageLine > nMaxDamagePerHit * 2.0D) {
                                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC_2, new StringBuilder().append("[Damage: ").append(nDamageLine).append(", Expected: ").append(nMaxDamagePerHit).append(", Mob: ").append(pMob.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(pAttack.skill).append("]").toString());
                                    nDamageLine = (int) (nMaxDamagePerHit * 2.0D);

                                    if (nDamageLine >= nPlayerDamageCap) {
                                        return;
                                        //pPlayer.getClient().Close();
                                    }
                                }
                            }
                        } else if (nDamageLine > nMaxDamagePerHit) {
                            nDamageLine = (int) nMaxDamagePerHit;
                        }
                    }

                    nTotalDamageToOneMonster += nDamageLine;
                }

                nTotalDamage += nTotalDamageToOneMonster;

                if ((GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 150) && (!GameConstants.isNoDelaySkill(pAttack.skill)) && !GameConstants.isMismatchingBulletSkill(pAttack.skill) && (!pMob.getStats().isBoss()) && (pPlayer.getTruePosition().distanceSq(pMob.getTruePosition()) > GameConstants.getAttackRange(pEffect, pPlayer.getStat().defRange))) {
                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[Distance: ").append(pPlayer.getTruePosition().distanceSq(pMob.getTruePosition())).append(", Expected Distance: ").append(GameConstants.getAttackRange(pEffect, pPlayer.getStat().defRange)).append(" Job: ").append(pPlayer.getJob()).append("]").toString());
                    if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[Warning] Check DamageParse.java at attack delay check.");
                    return;
                }
                if ((pAttack.skill == 2301002) && (!pMobStat.getUndead())) {
                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.HEAL_ATTACKING_UNDEAD);
                    return;
                }

                pMob.damage(pPlayer, nTotalDamageToOneMonster, true, pAttack.skill); // Apply damage to monster

                if (pMob.isAlive()) { // Monster is still alive after being hit.
                    //pPlayer.checkMonsterAggro(pMob);
                } else {
                    pAttack.after_NumMobsKilled++;
                }
            }

            if (GameConstants.isLuminous(pPlayer.getJob())) {
                final Integer nDarkCrescendo = pPlayer.getBuffedValue(CharacterTemporaryStat.Larkness);

                if (nDarkCrescendo != null && nDarkCrescendo != 1) {
                    StatEffect pCrescendo = SkillFactory.getSkill(27121005).getEffect(pPlayer.getSkillLevel(27121005));
                    if (pCrescendo != null) {
                        if (pCrescendo.makeChanceResult()) {
                            pPlayer.setLastCombo(System.currentTimeMillis());
                            if (pPlayer.acaneAim <= 29) {
                                pPlayer.acaneAim++;
                                pCrescendo.applyTo(pPlayer);
                            }
                        }
                    }
                }
            } else if (GameConstants.isThunderBreakerCygnus(pPlayer.getJob())) {
                StatEffect pCrescendo = SkillFactory.getSkill(15001022).getEffect(pPlayer.getSkillLevel(15001022));
                if (pCrescendo != null) {
                    if (pCrescendo.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (pPlayer.acaneAim <= 3) {
                            pPlayer.acaneAim++;
                            pCrescendo.applyTo(pPlayer);
                        }
                    }
                }
            }

            if (pAttack.skill != 2301002) pEffect.applyTo(pPlayer);
            
            if (nTotalDamage > 1 && GameConstants.getAttackDelay(pAttack.skill, pSkill) >= 100) {
                CheatTracker tracker = pPlayer.getCheatTracker();
                tracker.setAttacksWithoutHit(true);

                if (tracker.getAttacksWithoutHit() > 1000) {
                    tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
                }
            }
        }

        OnMultiKill(pAttack, pPlayer); // Handle Multi Kills
    }

    /**
     * UserMaxWeaponDamage
     * @param pPlayer
     * @param pMob
     * @param pAttack
     * @param pSkill
     * @param pEffect
     * @param nMaxDamageToMonster
     * @param nCriticalDamagePercent
     * @return 
     */
    private static double UserMaxWeaponDamage(User pPlayer, Mob pMob, AttackInfo pAttack, Skill pSkill, StatEffect pEffect, double nMaxDamageToMonster, Integer nCriticalDamagePercent) {
        int nLevelDifference = Math.max(pMob.getStats().getLevel() - pPlayer.getLevel(), 0) * 2;
        int nHitRate = Math.min((int) Math.floor(Math.sqrt(pPlayer.getStat().getAccuracy())) - (int) Math.floor(Math.sqrt(pMob.getStats().getEva())) + 100, 100);
        if (nLevelDifference > nHitRate) {
            nHitRate = nLevelDifference;
        }
        nHitRate -= nLevelDifference;
        if ((nHitRate <= 0) && ((!GameConstants.isBeginnerJob(pAttack.skill / 10000)) || (pAttack.skill % 10000 != 1000)) && (!GameConstants.isPyramidSkill(pAttack.skill)) && (!GameConstants.isMulungSkill(pAttack.skill)) && (!GameConstants.isInflationSkill(pAttack.skill))) {
            return 0.0D;
        }
        if ((pPlayer.getMapId() / 1000000 == 914) || (pPlayer.getMapId() / 1000000 == 927)) {
            return 999999.0D;
        }

        List<Element> aElements = new ArrayList<>();
        boolean bDefined = false;
        int nCritPercent = nCriticalDamagePercent;
        int nPDRate = pMob.getStats().getPDRate();
        
        MonsterStatusEffect pMobStatEffect = pMob.getBuff(MonsterStatus.PDD);
        if (pMobStatEffect != null) nPDRate += pMobStatEffect.getX();
        
        if (pSkill != null) {
            aElements.add(pSkill.getElement());
            if (GameConstants.isBeginnerJob(pSkill.getId() / 10000)) {
                switch (pSkill.getId() % 10000) {
                    case 1000:
                        nMaxDamageToMonster = 40.0D;
                        bDefined = true;
                        break;
                    case 1020:
                        nMaxDamageToMonster = 1.0D;
                        bDefined = true;
                        break;
                    case 1009:
                        nMaxDamageToMonster = pMob.getStats().isBoss() ? pMob.getMobMaxHp() / 30L * 100L : pMob.getMobMaxHp();
                        bDefined = true;
                }
            }
            if (pEffect != null) {
                switch (pSkill.getId()) {
                    case Berserker.SACRIFICE:
                        nPDRate = pMob.getStats().isBoss() ? nPDRate : 0;
                        break;
                    case Marksman.PIERCING_ARROW:
                    case WildHunter.RICOCHET:
                        nMaxDamageToMonster *= pEffect.getMobCount();
                        bDefined = true;
                        break;
                    case Hunter.ARROW_BOMB:
                        bDefined = true;
                        break;
                    case BattleMage.TRIPLE_BLOW:
                    case BattleMage.QUAD_BLOW:
                    case BattleMage.QUINTUPLE_BLOW:
                    case BattleMage.FINISHING_BLOW:
                        nMaxDamageToMonster *= 1.5D;
                        break;
                    case Paladin.BLAST:
                    case Marksman.SNIPE:
                    case DualBlade.OWL_SPIRIT:
                    case Mercedes.LIGHTNING_EDGE:
                        if (!pMob.getStats().isBoss()) {
                            nMaxDamageToMonster = pMob.getMobMaxHp();
                            bDefined = true;
                        }
                        break;
                    case Paladin.HEAVENS_HAMMER:
                    case Aran.COMBO_TEMPEST:
                        nMaxDamageToMonster = pMob.getStats().isBoss() ? 500000.0D : pMob.getHp() - 1L;
                        bDefined = true;
                        break;
                    case Sniper.STRAFE_2:
                        if (pMob.getStatusSourceID(MonsterStatus.FREEZE) == 3211003) {
                            bDefined = true;
                            nMaxDamageToMonster = 999999.0D;
                        }
                        break;
                }
            }
        }
        
        double nElementMaxDamagePerMonster = nMaxDamageToMonster;
        if ((pPlayer.getJob() == 311) || (pPlayer.getJob() == 312) || (pPlayer.getJob() == 321) || (pPlayer.getJob() == 322)) {
            Skill pSkillRef = SkillFactory.getSkill((pPlayer.getJob() == 311) || (pPlayer.getJob() == 312) ? Ranger.MORTAL_BLOW_2 : Sniper.MORTAL_BLOW);
            if (pPlayer.getTotalSkillLevel(pSkillRef) > 0) {
                StatEffect pEff = pSkillRef.getEffect(pPlayer.getTotalSkillLevel(pSkillRef));
                if ((pEff != null) && (pMob.getHPPercent() < pEff.getX())) {
                    nElementMaxDamagePerMonster = 999999.0D;
                    bDefined = true;
                    if (pEff.getZ() > 0) {
                        pPlayer.addHP(pPlayer.getStat().getMaxHp() * pEff.getZ() / 100);
                    }
                }
            }
        } else if ((pPlayer.getJob() == 221) || (pPlayer.getJob() == 222)) {
            Skill pSkillRef = SkillFactory.getSkill(IceLightningMage.STORM_MAGIC);
            if (pPlayer.getTotalSkillLevel(pSkillRef) > 0) {
                StatEffect pEff = pSkillRef.getEffect(pPlayer.getTotalSkillLevel(pSkillRef));
                if ((pEff != null) && (pMob.getHPPercent() < pEff.getX())) {
                    nElementMaxDamagePerMonster = 999999.0D;
                    bDefined = true;
                }
            }
        }
        if ((!bDefined) || ((pSkill != null) && ((pSkill.getId() == WildHunter.RICOCHET) || (pSkill.getId() == Marksman.PIERCING_ARROW)))) {
            if (pPlayer.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
                int nChargeSkillID = pPlayer.getBuffSource(CharacterTemporaryStat.WeaponCharge);

                switch (nChargeSkillID) {
                    case 1211003:
                    case WhiteKnight.FLAME_CHARGE:
                        aElements.add(Element.FIRE);
                        break;
                    case 1211005:
                    case WhiteKnight.BLIZZARD_CHARGE:
                    case Aran.SNOW_CHARGE:
                        aElements.add(Element.ICE);
                        break;
                    case 1211007:
                    case WhiteKnight.LIGHTNING_CHARGE_1:
                    case ThunderBreaker.LIGHTNING_CHARGE:
                        aElements.add(Element.LIGHTING);
                        break;
                    case 1221003:
                    case Paladin.DIVINE_CHARGE:
                    case DawnWarrior.RADIANT_CHARGE_2:
                        aElements.add(Element.HOLY);
                        break;
                    case BlazeWizard.ELEMENTAL_RESET:
                        break;
                }
            }

            if (pPlayer.getBuffedValue(CharacterTemporaryStat.AssistCharge) != null) {
                aElements.add(Element.LIGHTING);
            }
            if (pPlayer.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
                aElements.clear();
            }
            
            double pElementalEffect;
            if (aElements.size() > 0) {
                switch (pAttack.skill) {
                    case 3111003:
                    case 3211003:
                        pElementalEffect = pEffect.getX() / 100.0D;
                        break;
                    default:
                        pElementalEffect = 0.5D / aElements.size();
                }

                for (Element element : aElements) {
                    switch (pMob.getEffectiveness(element)) {
                        case IMMUNE:
                            nElementMaxDamagePerMonster = 1.0D;
                            break;
                        case WEAK:
                            nElementMaxDamagePerMonster *= (1.0D + pElementalEffect + pPlayer.getStat().getElementBoost(element));
                            break;
                        case STRONG:
                            nElementMaxDamagePerMonster *= (1.0D - pElementalEffect - pPlayer.getStat().getElementBoost(element));
                        case NORMAL:
                            break;
                        default:
                            break;
                    }
                }
            }

            nElementMaxDamagePerMonster -= nElementMaxDamagePerMonster * (Math.max(nPDRate - Math.max(pPlayer.getStat().ignoreTargetDEF, 0) - Math.max(pEffect == null ? 0 : pEffect.getIgnoreMob(), 0), 0) / 100.0D);
            nElementMaxDamagePerMonster += nElementMaxDamagePerMonster / 100.0D * nCritPercent;

            MonsterStatusEffect pImprint = pMob.getBuff(MonsterStatus.IMPRINT);
            if (pImprint != null) nElementMaxDamagePerMonster += nElementMaxDamagePerMonster * pImprint.getX() / 100.0D;

            nElementMaxDamagePerMonster += nElementMaxDamagePerMonster * pPlayer.getDamageIncrease(pMob.getObjectId()) / 100.0D;
            nElementMaxDamagePerMonster *= ((pMob.getStats().isBoss()) && (pEffect != null) ? pPlayer.getStat().bossdam_r + pEffect.getBossDamage() : pPlayer.getStat().dam_r) / 100.0D;
        }
        if (nElementMaxDamagePerMonster > 999999.0D) {
            if (!bDefined) {
                nElementMaxDamagePerMonster = 999999.0D;
            }
        }
        nElementMaxDamagePerMonster *= pPlayer.getStat().starForceDamageRate;
        
        if (nElementMaxDamagePerMonster <= 0.0D) nElementMaxDamagePerMonster = 1.0D;
        
        return nElementMaxDamagePerMonster;
    }

    /**
     * UserMaxMagicDamage
     * @param pPlayer
     * @param pSkill
     * @param pMob
     * @param pMobStat
     * @param pPlayerStat
     * @param pElement
     * @param nSharpEye
     * @param nMaxDamagePerMonster
     * @param pEffect
     * @return 
     */
    private static double UserMaxMagicDamage(User pPlayer, Skill pSkill, Mob pMob, MonsterStats pMobStat, PlayerStats pPlayerStat, Element pElement, Integer nSharpEye, double nMaxDamagePerMonster, StatEffect pEffect) {
        int nLevelDifference = Math.max(pMobStat.getLevel() - pPlayer.getLevel(), 0) * 2;
        int nHitRate = Math.min((int) Math.floor(Math.sqrt(pPlayerStat.getAccuracy())) - (int) Math.floor(Math.sqrt(pMobStat.getEva())) + 100, 100);
        if (nLevelDifference > nHitRate) {
            nHitRate = nLevelDifference;
        }
        nHitRate -= nLevelDifference;
        if ((nHitRate <= 0) && ((!GameConstants.isBeginnerJob(pSkill.getId() / 10000)) || (pSkill.getId() % 10000 != 1000))) {
            return 0.0D;
        }

        int nCritPercent = nSharpEye;
        ElementalEffectiveness pElementalEffect = pMob.getEffectiveness(pElement);
        double nMaxElementDamagePerMob = nMaxDamagePerMonster * pElementalEffect.getValue();
        
        switch (pElementalEffect) {
            case IMMUNE:
                nMaxElementDamagePerMob = 1.0D;
                break;
            default:
                switch (pElement) {
                    case FIRE:
                        nMaxElementDamagePerMob = nMaxElementDamagePerMob / 100.0D * (pPlayerStat.element_fire + pPlayerStat.getElementBoost(pElement));
                        break;
                    case ICE:
                        nMaxElementDamagePerMob = nMaxElementDamagePerMob / 100.0D * (pPlayerStat.element_ice + pPlayerStat.getElementBoost(pElement));
                        break;
                    case LIGHTING:
                        nMaxElementDamagePerMob = nMaxElementDamagePerMob / 100.0D * (pPlayerStat.element_light + pPlayerStat.getElementBoost(pElement));
                        break;
                    case POISON:
                        nMaxElementDamagePerMob = nMaxElementDamagePerMob / 100.0D * (pPlayerStat.element_psn + pPlayerStat.getElementBoost(pElement));
                        break;
                    case DARKNESS:
                        break;
                    case HOLY:
                        break;
                    case NEUTRAL:
                        break;
                    case PHYSICAL:
                        break;
                    default:
                        nMaxElementDamagePerMob = nMaxElementDamagePerMob / 100.0D * (pPlayerStat.def + pPlayerStat.getElementBoost(pElement));
                        break;
                }
                break;
        }
        
        MonsterStatusEffect pMobStatEffect = pMob.getBuff(MonsterStatus.MDD);
        int nMDRate = pMob.getStats().getMDRate();
        if (pMobStatEffect != null) nMDRate += pMobStatEffect.getX();
        
        nMaxElementDamagePerMob -= nMaxElementDamagePerMob * (Math.max(nMDRate - pPlayerStat.ignoreTargetDEF - pEffect.getIgnoreMob(), 0) / 100.0D);
        nMaxElementDamagePerMob += nMaxElementDamagePerMob / 100.0D * nCritPercent;
        nMaxElementDamagePerMob *= (pMob.getStats().isBoss() ? pPlayer.getStat().bossdam_r : pPlayer.getStat().dam_r) / 100.0D;
        
        MonsterStatusEffect pImprint = pMob.getBuff(MonsterStatus.IMPRINT);
        if (pImprint != null) nMaxElementDamagePerMob += nMaxElementDamagePerMob * pImprint.getX() / 100.0D;
        
        nMaxElementDamagePerMob += nMaxElementDamagePerMob * pPlayer.getDamageIncrease(pMob.getObjectId()) / 100.0D;
        
        if (GameConstants.isBeginnerJob(pSkill.getId() / 10000)) {
            switch (pSkill.getId() % 10000) {
                case 1000:
                    nMaxElementDamagePerMob = 40.0D;
                    break;
                case 1020:
                    nMaxElementDamagePerMob = 1.0D;
                    break;
                case 1009:
                    nMaxElementDamagePerMob = pMob.getStats().isBoss() ? pMob.getMobMaxHp() / 30L * 100L : pMob.getMobMaxHp();
            }
        }

        switch (pSkill.getId()) {
            case BattleMage.TRIPLE_BLOW:
            case BattleMage.QUAD_BLOW:
            case BattleMage.QUINTUPLE_BLOW:
            case BattleMage.FINISHING_BLOW:
                nMaxElementDamagePerMob *= 1.5D;
        }

        if (nMaxElementDamagePerMob > GameConstants.damageCap) {
            nMaxElementDamagePerMob = GameConstants.damageCap;

            nMaxElementDamagePerMob *= pPlayerStat.starForceDamageRate;

        }
        if (nMaxElementDamagePerMob <= 0.0D) nMaxElementDamagePerMob = 1.0D;

        return nMaxElementDamagePerMob;
    }

    /**
     * OnCriticalAttack
     * @param pAttack
     * @param pPlayer
     * @param nType
     * @param pEffect 
     */
    public static final void OnCriticalAttack(AttackInfo pAttack, User pPlayer, int nType, StatEffect pEffect) {
        int nCriticalRate;
        boolean bShadow;
        List aDamageLines;
        boolean bCritical = false;
        List nDamage;
        if ((pAttack.skill != 4211006) && (pAttack.skill != 3211003) && (pAttack.skill != 4111004)) {
            nCriticalRate = pPlayer.getStat().passive_sharpeye_rate() + (pEffect == null ? 0 : pEffect.getCr());
            boolean bMirror = pPlayer.hasBuff(CharacterTemporaryStat.ShadowPartner) || pPlayer.hasBuff(CharacterTemporaryStat.ShadowServant);
            bShadow = bMirror && ((nType == 1) || (nType == 2));
            aDamageLines = new ArrayList<>();
            nDamage = new ArrayList<>();

            for (AttackMonster p : pAttack.allDamage) {
                if (p.getAttacks() != null) {
                    int hit = 0;
                    int mid_att = bShadow ? p.getAttacks().size() / 2 : p.getAttacks().size();

                    int toCrit = (pAttack.skill == 4221001) || (pAttack.skill == 3221007) || (pAttack.skill == 23121003) || (pAttack.skill == 4341005) || (pAttack.skill == 4331006) || (pAttack.skill == 21120005) ? mid_att : 0;
                    if (toCrit == 0) {
                        for (Pair eachd : p.getAttacks()) {
                            if ((!(Boolean) eachd.right) && hit < mid_att) {
                                if (((Long) eachd.left > 999999) || (Randomizer.nextInt(100) < nCriticalRate)) {
                                    toCrit++;
                                }
                                nDamage.add(eachd.left);
                            }
                            hit++;
                        }
                        if (toCrit == 0) {
                            nDamage.clear();
                        } else {
                            Collections.sort(nDamage);
                            for (int i = nDamage.size(); i > nDamage.size() - toCrit; i--) {
                                aDamageLines.add(nDamage.get(i - 1));
                            }
                            nDamage.clear();
                        }
                    } else {
                        hit = 0;
                        for (Pair<Long, Boolean> eachd : p.getAttacks()) {
                            if (!eachd.right) {
                                if (pAttack.skill == 4221001) {
                                    eachd.right = Boolean.valueOf(hit == 3);
                                } else if ((pAttack.skill == 3221007) || (pAttack.skill == 23121003) || (pAttack.skill == 21120005) || (pAttack.skill == 4341005) || (pAttack.skill == 4331006) || (((Long) eachd.left).longValue() > 999999)) {
                                    eachd.right = Boolean.valueOf(true);
                                } else if (hit >= mid_att) {
                                    eachd.right = p.getAttacks().get(hit - mid_att).right;
                                } else {
                                    eachd.right = Boolean.valueOf(aDamageLines.contains(eachd.left));
                                }
                                if (eachd.right) {
                                    bCritical = true;
                                }
                            }
                            hit++;
                        }
                        aDamageLines.clear();
                    }
                }
                if (GameConstants.isThiefShadower(pPlayer.getJob())) {
                    if (Randomizer.nextInt(100) < 50) { // TODO: Handle on critical strike correctly instead.
                        pPlayer.getMap().broadcastPacket(ShadowerPacket.toggleFlipTheCoin(true));
                    }
                }

                if (bCritical) {
                    if (GameConstants.isThiefShadower(pPlayer.getJob())) {
                        pPlayer.getMap().broadcastPacket(ShadowerPacket.toggleFlipTheCoin(true));
                    }
                    /*if (chr.getJob() == 422 && chr.dualBrid == 0 && chr.acaneAim < 5) {
                        chr.getMap().broadcastMessage(CField.OnOffFlipTheCoin(true));
                        chr.acaneAim++;
                        chr.dualBrid = 1;
                    }*/
                }
            }
        }
    }

    /**
     * OnAttack
     * @param eType
     * @param iPacket
     * @param pPlayer
     * @return 
     */
    public static AttackInfo OnAttack(RecvPacketOpcode eType, InPacket iPacket, User pPlayer) {
        
        //TrainingMap.OnMonsterAggressionRequest(pPlayer); // Aggro surrounding monsters.
        
        AttackInfo pAttack = new AttackInfo();
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeByte();
        }
        if (eType == RecvPacketOpcode.UserNonTargetForceAtomAttack) {
            iPacket.DecodeInt(); // nSkillID
            iPacket.DecodeInt(); // Unknown
            iPacket.DecodeInt(); // Unknown
        }
        int bFieldKey = iPacket.DecodeByte();
        pAttack.allDamage = new ArrayList<>();
        pAttack.tbyte = iPacket.DecodeByte();
        pAttack.mobCount = (pAttack.tbyte >>> 4 & 0xF);
        pAttack.numberOfHits = ((byte) (pAttack.tbyte & 0xF));
        pAttack.skill = iPacket.DecodeInt();

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.err.println("[Damage Operation] Skill (" + pAttack.skill + ")");
        }

        pAttack.skillLevel = iPacket.DecodeByte();
        boolean bAddAttackProc = false;
        if (eType != RecvPacketOpcode.UserMagicAttack && eType != RecvPacketOpcode.UserBodyAttack) {
            bAddAttackProc = iPacket.DecodeBool();
        }
        int nBulletItemID = 0;
        int nBulletCashItemID = 0;
        int nBulletItemPos = 0;
        int nBulletCashItemPos = 0;
        int nShootRange = 0;

        int dwMobCRC = iPacket.DecodeInt();
        if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) { // This is actually a sub, not sure what it does yet
            iPacket.DecodeByte(); // Unknown
            nBulletItemPos = iPacket.DecodeShort();
            iPacket.DecodeInt(); // Unknown
        }
        pAttack.slot = (byte) nBulletItemPos;
        int tDelay = 0;
        int nBySummonedID = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack && (Skill.isKeydownSkill(pAttack.skill) || Skill.isSupernovaSkill(pAttack.skill))) {
            pAttack.charge = iPacket.DecodeInt();
        } else if ((eType == RecvPacketOpcode.UserShootAttack || eType == RecvPacketOpcode.UserMagicAttack) && Skill.isKeydownSkill(pAttack.skill)) {
            pAttack.charge = iPacket.DecodeInt();
        }
        int nGrenade = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack) {
            if (Skill.isRushBombSkill(pAttack.skill) || pAttack.skill == 5300007 || pAttack.skill == 27120211 || pAttack.skill == 14111023
                    || pAttack.skill == 400031003 || pAttack.skill == 400031004 || pAttack.skill == 80011389 || pAttack.skill == 80011390) {
                nGrenade = iPacket.DecodeInt();
            }
        }
        boolean bZeroTag = false;
        if (Skill.isZeroSkill(pAttack.skill)) {
            bZeroTag = iPacket.DecodeBool();
        }
        if (Skill.IsUsercloneSummonableSkill(pAttack.skill)) {
            nBySummonedID = iPacket.DecodeInt();
        }
        if (pAttack.skill == 400031010) {
            iPacket.DecodeInt(); // Unknown
            iPacket.DecodeInt(); // Unknown
        }
        if (pAttack.skill == 400041019) {
            iPacket.DecodeInt(); // pRepeatSkill.ptAttackRefPoint.x
            iPacket.DecodeInt(); // pRepeatSkill.ptAttackRefPoint.y
        }
        iPacket.DecodeByte(); // Unknown (Always 0)
        pAttack.attackFlag = iPacket.DecodeByte();
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeInt(); // Unknown
            iPacket.DecodeBool(); // bNextShootExJablin && CheckApplyExJablin(pSkill, nAction)
        }
        pAttack.display = iPacket.DecodeShort();
        int nPsdTargetPlus = iPacket.DecodeInt();
        int nAttackActionType = iPacket.DecodeByte();
        if (Skill.IsEvanForceSkill(pAttack.skill)) {
            iPacket.DecodeByte(); // Unknown
        }
        if (pAttack.skill == 23111001 || pAttack.skill == 80001915 || pAttack.skill == 36111010) {
            iPacket.DecodeInt(); // Unknown
            iPacket.DecodeInt(); // Unknown
            iPacket.DecodeInt(); // Unknown
        }
        boolean bBySteal = false;
        int nOption = 0;
        if (eType == RecvPacketOpcode.UserShootAttack) {
            bBySteal = iPacket.DecodeBool();
        } else {
            nOption = iPacket.DecodeByte();
        }
        int tAttackTime = iPacket.DecodeInt();
        iPacket.DecodeInt(); // Unknown

        int nLastSkillID = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack) {
            nLastSkillID = iPacket.DecodeInt();
            if (pAttack.skill > 0 && nLastSkillID > 0) {
                iPacket.DecodeByte(); // Unknown
            }
            if (pAttack.skill == 5111009) {
                iPacket.DecodeByte(); // Unknown
            }
            if (pAttack.skill == 25111005) {
                iPacket.DecodeInt(); // nSpiritCoreEnhance
            }
        }
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeInt(); // Unknown
            nBulletCashItemPos = iPacket.DecodeShort();
            nShootRange = iPacket.DecodeByte();
            /*nBulletItemPos = iPacket.DecodeShort();
             nBulletCashItemPos = iPacket.DecodeShort();
             nShootRange = iPacket.DecodeByte();
             if (!SkillAccessor.IsShootSkillNotConsumingBullet(nSkillID, bBySteal)) {
                nBulletItemID = iPacket.DecodeInt();
             }*/

            // Rect
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeShort();

            if (pAttack.skill == 2211007) { // hackfix atm
                iPacket.DecodeInt();
            }
            if (pAttack.skill == 3111013) { // hackfix atm
                iPacket.DecodeLong();
            }
        }
        if (eType == RecvPacketOpcode.UserNonTargetForceAtomAttack) {
            iPacket.DecodeInt(); // Always 0
        }

        pAttack.allDamage = new ArrayList<>();
        if (eType == RecvPacketOpcode.UserBodyAttack) {
            for (int i = 0; i < pAttack.mobCount; i++) {
                int dwMobID = iPacket.DecodeInt();
                iPacket.DecodeInt(); // Unknown
                Point ptHit = new Point();
                Point ptPosPrev = new Point();
                ptHit.x = iPacket.DecodeShort();
                ptHit.y = iPacket.DecodeShort();
                ptPosPrev.x = iPacket.DecodeShort();
                ptPosPrev.y = iPacket.DecodeShort();
                iPacket.DecodeShort(); // tDelay
                int nSkeletonResult = iPacket.DecodeByte();
                if (nSkeletonResult == 1) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInt();
                    for (int s = 0; s < iPacket.DecodeInt(); s++) {
                        iPacket.DecodeString();
                    }
                } else if (nSkeletonResult == 2) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInt();
                }
                
                Mob monster = pPlayer.getMap().getMonsterByOid(dwMobID);
                if (monster == null) {
                    LogHelper.BUGREPORT.get().info(String.format("[DamageParse] IMPROPER PARSING OF: skill: %s, tbyte: %s, mob: %s, hits: %s\r\n", pAttack.skill, pAttack.tbyte, pAttack.mobCount, pAttack.numberOfHits));
                    monster = (Mob) pPlayer.getMap().getClosestMapObjectInRange(pPlayer.getPosition(), 10000, Arrays.asList(MapleMapObjectType.MONSTER)); // Hack fix for now, avoid NPE.
                }
                pAttack.allDamage.add(new AttackMonster(monster, dwMobID, monster.getId(), dwMobCRC, ptHit, ptPosPrev, null));
            }
        } else {
            for (int i = 0; i < pAttack.mobCount; i++) {
                int dwMobID = iPacket.DecodeInt();
                if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                    System.err.println("[Damage Operation] Mob Object (" + dwMobID + ")");
                }
                iPacket.DecodeByte(); // nHitAction
                iPacket.DecodeByte(); // Unknown
                iPacket.DecodeByte(); // Unknown
                int v37 = iPacket.DecodeByte();
                int nForeAction = (v37 & 0x7F);
                int bLeft = (byte) ((v37 >> 7) & 1);
                int nFrameIdx = iPacket.DecodeByte();
                iPacket.DecodeInt(); // Unknown
                int v38 = iPacket.DecodeByte();
                int nCalcDamageStatIndex = (v38 & 0x7F);
                boolean bDoomed = ((v38 >> 7 & 1) > 0);
                Point ptHit = new Point();
                Point ptPosPrev = new Point();
                ptHit.x = iPacket.DecodeShort();
                ptHit.y = iPacket.DecodeShort();
                pAttack.position.x = ptHit.x;
                pAttack.position.y = ptHit.y;
                ptPosPrev.x = iPacket.DecodeShort();
                ptPosPrev.y = iPacket.DecodeShort();
                if (eType == RecvPacketOpcode.UserMagicAttack) {
                    iPacket.DecodeByte(); // HP Percentage Lost
                }
                List<Pair<Long, Boolean>> damageNumbers = new ArrayList<>();
                if (pAttack.skill == 80001835 || pAttack.skill == 42111002 || pAttack.skill == 80011050) {
                    int nAttackCount = iPacket.DecodeByte();
                    if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                        for (int j = 0; j < nAttackCount; j++) {
                            long nDamage = iPacket.DecodeLong();
                            damageNumbers.add(new Pair(nDamage, false));
                        }
                    }
                } else {
                    tDelay = iPacket.DecodeShort();
                    if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                        for (int j = 0; j < pAttack.numberOfHits; j++) {
                            long nDamage = iPacket.DecodeLong();
                            damageNumbers.add(new Pair(nDamage, false));
                            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                                System.err.println("[Damage Operation] Damage Line (" + nDamage + ")");
                            }
                        }
                    }
                }
                if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                    iPacket.DecodeInt(); // pMob.GetMobUpDownYRange
                    iPacket.DecodeInt(); // pMob.GetCrc
                }
                if (pAttack.skill == 37111005) {
                    iPacket.DecodeBool(); // bRWLiftPress
                }
                int nSkeletonResult = iPacket.DecodeByte();
                if (nSkeletonResult == 1) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInt();
                    for (int s = 0; s < iPacket.DecodeInt(); s++) {
                        iPacket.DecodeString();
                    }
                } else if (nSkeletonResult == 2) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInt();
                }
                iPacket.DecodeByte(); // Unknown
                if (pAttack.skill == 142120001) {
                    //iPacket.DecodeLong(); // haxfix? Refer back here once testing some Kinesis skills. -Mazen
                }
                
                Mob monster = pPlayer.getMap().getMonsterByOid(dwMobID);//CHECK WHY THIS NULLS (no oid)
                if (monster == null) {
                    LogHelper.BUGREPORT.get().info(String.format("[DamageParse] IMPROPER PARSING OF: skill: %s, tbyte: %s, mob: %s, hits: %s\r\n", pAttack.skill, pAttack.tbyte, pAttack.mobCount, pAttack.numberOfHits));
                    monster = (Mob) pPlayer.getMap().getClosestMapObjectInRange(pPlayer.getPosition(), 10000, Arrays.asList(MapleMapObjectType.MONSTER)); // Hack fix for now, avoid NPE.
                }
                pAttack.allDamage.add(new AttackMonster(monster, dwMobID, monster.getId(), dwMobCRC, ptHit, ptPosPrev, damageNumbers));
            }
            if (eType == RecvPacketOpcode.UserMeleeAttack) {
                if (pAttack.skill == 61121052 || pAttack.skill == 36121052 || Skill.isScreenCenterAttackSkill(pAttack.skill)) {
                    iPacket.DecodeShort(); // tTime
                    iPacket.DecodeShort(); // nMobCount 
                } else {
                    if (Skill.isSupernovaSkill(pAttack.skill) || pAttack.skill == 101000102) {
                        iPacket.DecodeShort(); // m_repeatSkill.ptAttackRefPoint.x
                        iPacket.DecodeShort(); // m_repeatSkill.ptAttackRefPoint.y
                        
                        if(Skill.isAranFallingStopSkill(pAttack.skill)) {
                            iPacket.DecodeByte(); // Unkown
                        }
                        if (pAttack.skill == 21120019 || pAttack.skill == 37121052) {
                            iPacket.DecodeInt(); // m_teleport.pt.x
                            iPacket.DecodeInt(); // m_teleport.pt.y
                        }
                        if (pAttack.skill == 61121105 || pAttack.skill == 61121222 || pAttack.skill == 24121052) {
                            iPacket.DecodeShort(); // nMaxCount
                        }
                        
                        //if ( ZArray<tagPOINT>::GetCount(&nVx) ) {
                        //  do {
                        //    COutPacket::Encode2(&nMaxCount[1], *(nVx + 8 * v753));
                        //    COutPacket::Encode2(&nMaxCount[1], *(nVx + 8 * v753++ + 4));
                        //  } while ( v753 < ZArray<tagPOINT>::GetCount(&nVx) );
                        //}
                        
                        if (pAttack.skill == 101120104) {
                            // TODO: CUser::EncodeAdvancedEarthBreak
                        }
                        if (pAttack.skill == 14111006) {
                            iPacket.DecodeShort(); // nMaxCount
                            iPacket.DecodeShort(); // nDamagePerMob
                        }
                    }
                    iPacket.DecodeShort(); // Unk
                    iPacket.DecodeShort(); // nSkillID
                }
            }
        }
        // TODO: See if can parse with just this.. the rest is so much and i dont think u use any of the vars

        // Apply Monster Status, think this is enough for now. -Mazen
        if (pAttack.skill != 0) {
            MobStatRequest.apply(pPlayer, pAttack, ((pPlayer.getSkillLevel(pAttack.skill) > 0) ? SkillFactory.getSkill(pAttack.skill).getEffect(pPlayer.getSkillLevel(pAttack.skill)) : SkillFactory.getSkill(pAttack.skill).getEffect(1)));
        }

        return pAttack;
    }
    
    /**
     * OnMultiKill
     * @param pAttack
     * @param pPlayer
     */
    private static void OnMultiKill(AttackInfo pAttack, User pPlayer) {
        if (pAttack.after_NumMobsKilled > 0) {
            if (pAttack.allDamage.isEmpty()) return;
            final Optional<AttackMonster> pMob = pAttack.allDamage.stream().findFirst().filter(s -> !s.getMonster().isAlive());
            if (pMob == null || !pMob.isPresent()) return;

            /*Handle EXP Gain*/
            if (pAttack.after_NumMobsKilled > 2) {
                int nMultiKills = Math.min(10, pAttack.after_NumMobsKilled);

                MonsterStats pMobStat = LifeFactory.getMonsterStats(pMob.get().getMonsterId());
                if (pMobStat != null) {
                    final long nMobExp = pMobStat.getExp();
                    final int nAdditionalExpPercentage = 5 * (nMultiKills - 2);
                    double nExpRate = pPlayer.getClient().getChannelServer().getExpRate(pPlayer.getWorld());
                    final long nTotalExp = (long) (((long) (nExpRate * nMobExp)) * (nAdditionalExpPercentage * 0.01f));
                    pPlayer.gainExp(nTotalExp, false, false, false);
                    pPlayer.getClient().SendPacket(WvsContext.messagePacket(new StylishKillMessage(StylishKillMessageType.MultiKill, nTotalExp, nMultiKills)));
                }
            }

            /*Handle Combo EXP Orbs*/
            short nCombo = pPlayer.getCombo();
            long tTime = System.currentTimeMillis();

            if (tTime - pPlayer.getLastComboTime() > 15L * 1000L) { // 10 seconds
                nCombo = 0;
            } else {
                nCombo = (short) Math.min(1000, nCombo + 1);
                if (nCombo % 50 == 0) {
                    final int nItemID;
                    if (nCombo <= 300) {
                        nItemID = ItemConstants.COMBO_PARADE1;
                    } else if (nCombo <= 700) {
                        nItemID = ItemConstants.COMBO_PARADE2;
                    } else {
                        nItemID = ItemConstants.COMBO_PARADE3;
                    }
                    Item pDrop = new Item(nItemID, (byte) 0, (short) 1, (byte) 0);

                    Point nMobPOS = pMob.get().getPosition();
                    Point nPlayerPOS = pPlayer.getPosition();
                    Point nPOS = pPlayer.getMap().calcDropPos(new Point((int) nMobPOS.getX() + (nPlayerPOS.getX() > nMobPOS.getX() ? -50 : 50), (int) nMobPOS.getY()), nMobPOS); // Avoid placing directly on the character.

                    pPlayer.getMap().spawnItemDrop(pPlayer, pPlayer, pDrop, nPOS, false, true, true);

                    if (nCombo == 1000) nCombo = 0; // Reset
                }
            }
            
            pPlayer.setLastCombo(tTime);
            pPlayer.setCombo(nCombo);
            pPlayer.getClient().SendPacket(WvsContext.messagePacket(new StylishKillMessage(StylishKillMessage.StylishKillMessageType.Combo, nCombo, pMob.get().getObjectId())));
        }
    }
}
