package handling.game;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.jobs.Cygnus.DawnWarriorHandler;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Blaster;
import constants.skills.DemonAvenger;
import constants.skills.DemonSlayer;
import constants.skills.Hayato;
import constants.skills.NightWalker;
import constants.skills.Page;
import constants.skills.Zero;
import client.jobs.Explorer;
import client.jobs.Resistance;
import client.jobs.Sengoku;
import client.jobs.Sengoku.HayatoHandler;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import handling.world.PlayerHandler;
import net.InPacket;
import server.StatEffect;
import server.StatInfo;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball;
import server.maps.objects.User;
import service.ChannelServer;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.JobPacket;

/**
 * CloseRangeAttack
 * @author Mazen Massoud
 */
public final class CloseRangeAttack {

    public static void closeRangeAttack(InPacket iPacket, ClientSocket c, User pPlayer, boolean bPassiveAttack) {

        if (pPlayer == null || pPlayer.hasBlockedInventory() 
                || pPlayer.getMap() == null || (bPassiveAttack
                && (pPlayer.getBuffedValue(CharacterTemporaryStat.EnergyCharged) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.BMageAura) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.SUMMON) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.Asura) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.TeleportMasteryOn) == null))) {
            return;
        }

        AttackInfo pAttack = DamageParse.OnAttack(RecvPacketOpcode.UserMeleeAttack, iPacket, pPlayer);
        
        Skill pSkill = null;
        int nSLV = pPlayer.getTotalSkillLevel(pSkill);
        StatEffect pEffect = null;
        Item pShield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
        int nAttackCount = pShield != null && (pShield.getItemId() / 10000 == 134) ? 2 : 1;
        double nMaxDamage = pPlayer.getStat().getCurrentMaxBaseDamage();
        final boolean bMirror = (pPlayer.hasBuff(CharacterTemporaryStat.ShadowPartner) || pPlayer.hasBuff(CharacterTemporaryStat.ShadowServant));
        
        if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[CloseRangeAttack Debug] Skill ID : " + pAttack.skill);

        if (pAttack.skill != 0) {
            
            pSkill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(pAttack.skill));
            
            if (pSkill == null || (GameConstants.isAngel(pAttack.skill) && pPlayer.getStat().equippedSummon % 10000 != pAttack.skill % 10000)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            
            pEffect = pAttack.getAttackEffect(pPlayer, nSLV <= 0 ? pAttack.skillLevel : nSLV, pSkill);

            if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {

                int nHPCost = (c.getPlayer().getMaxHP() / 150); // 1% of Maximum HP as Skill Cost.
                c.getPlayer().addHP(-nHPCost);

                // Demon Avenger Overload Stacks
                int nExceedMax = c.getPlayer().getSkillLevel(31220044) > 0 ? 18 : 20;
                if (c.getPlayer().getExceed() + 1 > nExceedMax) {
                    c.getPlayer().setExceed((short) nExceedMax);
                } else {
                    c.getPlayer().gainExceed((short) 1);
                }
                if (GameConstants.isExceedAttack(pSkill.getId())) {
                    pPlayer.handleExceedAttack(pSkill.getId());
                }
                
            } else if (GameConstants.isDemonSlayer(pPlayer.getJob()) && !pPlayer.hasBuff(CharacterTemporaryStat.InfinityForce)) {

                switch (pAttack.skill) { // Hack fix for some Fury Costs.
                    case DemonSlayer.DEMON_CRY:
                        pPlayer.setMp(pPlayer.getStat().getMp() + 20);
                        break;
                    case DemonSlayer.CHAOS_LOCK:
                        pPlayer.setMp(pPlayer.getStat().getMp() + 10);
                        break;
                    case DemonSlayer.CERBERUS_CHOMP:
                        pPlayer.setMp(pPlayer.getStat().getMp() + 50);
                        break;
                }
                
            } else if (GameConstants.isWarriorHero(pPlayer.getJob())) {
                
                if (pPlayer.getStatForBuff(CharacterTemporaryStat.ComboCounter) != null) { // Combo Attack
                    Explorer.HeroHandler.handleComboAttack(pPlayer);
                    Explorer.HeroHandler.handleComboOrbs(pPlayer, pAttack.skill);
                } else {
                    pPlayer.setPrimaryStack(0);
                }
                
            } else if (GameConstants.isBlaster(pPlayer.getJob())) {
                
                switch (pAttack.skill) {
                    case Blaster.BUNKER_BUSTER_EXPLOSION:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_1:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_2:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_3:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_4:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_5:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_6:
                    case Blaster.BUNKER_BUSTER_EXPLOSION_7:
                        Resistance.BlasterHandler.handleOverheat(pPlayer);
                        break;
                    case 37001004:
                    case Blaster.REVOLVING_CANNON_PLUS:
                    case Blaster.REVOLVING_CANNON_PLUS_II:
                    case Blaster.REVOLVING_CANNON_PLUS_III:
                        Resistance.BlasterHandler.handleAmmoCost(pPlayer);
                        Resistance.BlasterHandler.handleGaugeIncrease(pPlayer);
                        c.SendPacket(JobPacket.BlasterPacket.onRWMultiChargeCancelRequest((byte) 1, pAttack.skill));
                        break;
                }
                
            } else if (GameConstants.isHayato(pPlayer.getJob())) {
                
                if (pAttack.skill == Hayato.SUMMER_RAIN) {
                    HayatoHandler.updateBladeStanceRequest(pPlayer, 0);
                }
                
            } else if (GameConstants.isDawnWarriorCygnus(pPlayer.getJob())) {

                DawnWarriorHandler.handleEquinox(pPlayer);
                
            } else if (GameConstants.isZero(pPlayer.getJob())) {

                switch (pAttack.skill) {
                    case Zero.RISING_SLASH_2:
                    case Zero.FLASH_CUT:
                    case Zero.SPIN_DRIVER:
                    case Zero.GIGA_CRASH:
                        pPlayer.zeroChange(true);
                        break;
                    case Zero.MOON_STRIKE_1:
                    case Zero.FLASH_ASSAULT:
                    case Zero.ROLLING_CROSS:
                    case Zero.WIND_CUTTER:
                        pPlayer.zeroChange(false);
                        break;
                }
                
            } else if (GameConstants.isAngelicBuster(pPlayer.getJob())) {
                
                if (pEffect != null) {
                    int Recharge = pEffect.getOnActive();
                    if (Recharge > -1) {
                        if (Randomizer.isSuccess(Recharge)) {
                            c.SendPacket(JobPacket.AngelicPacket.unlockSkill());
                            c.SendPacket(JobPacket.AngelicPacket.showRechargeEffect());
                        } else {
                            c.SendPacket(JobPacket.AngelicPacket.lockSkill(pAttack.skill));
                        }
                    } else {
                        c.SendPacket(JobPacket.AngelicPacket.lockSkill(pAttack.skill));
                    }
                }
                
            }
            
            if (GameConstants.isEventMap(pPlayer.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(pPlayer.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!pPlayer.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (pPlayer.getMapId() == i) {
                                pPlayer.dropMessage(5, "You may not use that here.");
                                return;
                            }
                        }
                    }
                }
            }

            if (pEffect != null) {
                nMaxDamage *= (pEffect.getDamage() + pPlayer.getStat().getDamageIncrease(pAttack.skill)) / 100.0D;
                nAttackCount = pEffect.getAttackCount();

                // Handle Cooldown
                if (pEffect.getCooldown(pPlayer) > 0 && !bPassiveAttack) {
                    if (pPlayer.skillisCooling(pAttack.skill)) {
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    }
                    pPlayer.addCooldown(pAttack.skill, System.currentTimeMillis(), pEffect.getCooldown(pPlayer));
                }
            }
        }

        DamageParse.modifyCriticalAttack(pAttack, pPlayer, 1, pEffect);
        if (bMirror) nAttackCount *= 2;
        
        if (!bPassiveAttack) {
            if ((pPlayer.getMapId() == 109060000 || pPlayer.getMapId() == 109060002 || pPlayer.getMapId() == 109060004) && pAttack.skill == 0) {
                MapleSnowball.MapleSnowballs.hitSnowball(pPlayer);
            }

            int nFinisherOrbs = 0;
            Integer nComboBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.ComboCounter);

            if (PlayerHandler.isFinisher(pAttack.skill) > 0) {
                if (nComboBuff != null) {
                    nFinisherOrbs = nComboBuff - 1;
                }
                if (nFinisherOrbs <= 0) {
                    return;
                }
                pPlayer.handleOrbconsume(PlayerHandler.isFinisher(pAttack.skill));
            }
        }
        
        pPlayer.checkFollow();
        
        switch (pAttack.skill) {
            case Aran.MAHAS_DOMAIN: {
                pPlayer.write(CField.closeRangeAttack(pPlayer.getId(), pAttack.numberOfHits | 16 * pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, pAttack.allDamage, bPassiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag, pAttack.charge));
                break;
            }
            default: {
                if (!pPlayer.isHidden()) {
                    pPlayer.getMap().broadcastPacket(pPlayer, CField.closeRangeAttack(pPlayer.getId(), pAttack.numberOfHits | 16 * pAttack.tbyte , pAttack.skill, nSLV, pAttack.display, pAttack.speed, pAttack.allDamage, bPassiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag, pAttack.charge), pPlayer.getTruePosition());
                } else {
                    pPlayer.getMap().broadcastGMMessage(pPlayer, CField.closeRangeAttack(pPlayer.getId(), pAttack.numberOfHits | 16 * pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, pAttack.allDamage, bPassiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag, pAttack.charge), false);
                }
                break;
            }
        }
        
        DamageParse.applyAttack(pAttack, pSkill, pPlayer, nAttackCount, nMaxDamage, pEffect, bMirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        
        /*int nBullet = 1;
        switch (pAttack.skill) {
            case Page.FLAME_CHARGE_1:
                nBullet = pEffect.getAttackCount();
                DamageParse.applyAttack(pAttack, pSkill, pPlayer, nAttackCount, nMaxDamage, pEffect, AttackType.NON_RANGED);
                break;
            default:
                DamageParse.applyAttack(pAttack, pSkill, pPlayer, nAttackCount, nMaxDamage, pEffect, AttackType.NON_RANGED);
                break;
        }*/
        
        pAttack.cleanupMemory(); // Clean up memory references.
    }
}
