package handling.game;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Blaster;
import constants.skills.DemonAvenger;
import constants.skills.DemonSlayer;
import constants.skills.NightWalker;
import constants.skills.Page;
import constants.skills.Zero;
import handling.jobs.Explorer;
import handling.jobs.Resistance;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import handling.world.PlayerHandler;
import net.InPacket;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball;
import server.maps.objects.MapleCharacter;
import service.ChannelServer;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;

public final class CloseRangeAttack {

    public static void closeRangeAttack(InPacket iPacket, MapleClient c, MapleCharacter pPlayer, boolean passiveAttack) {

        if (pPlayer == null || pPlayer.hasBlockedInventory() || pPlayer.getMap() == null
                || (passiveAttack
                && (pPlayer.getBuffedValue(CharacterTemporaryStat.EnergyCharged) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.BMageAura) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.SUMMON) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.Asura) == null
                && pPlayer.getBuffedValue(CharacterTemporaryStat.TeleportMasteryOn) == null))) {
            return;
        }

        //AttackInfo attack = DamageParse.parseCloseRangeAttack(iPacket, chr, passiveAttack);
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserMeleeAttack, iPacket, pPlayer);
        
        /*if (passiveAttack) {
            attack = DamageParse.OnAttack(RecvPacketOpcode.UserBodyAttack, iPacket, chr);
        } else {
            attack = DamageParse.OnAttack(RecvPacketOpcode.UserMeleeAttack, iPacket, chr);
        }*/
        
        if (pPlayer.isDeveloper()) {
            c.getPlayer().dropMessage(5, "[Debug] CloseRangeAttack: Skill ID (" + attack.skill + ")");
        }
        
        final boolean mirror = (pPlayer.hasBuff(CharacterTemporaryStat.ShadowPartner) || pPlayer.hasBuff(CharacterTemporaryStat.ShadowServant));
        double maxdamage = pPlayer.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
        int attackCount = shield != null && (shield.getItemId() / 10000 == 134) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;

        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
            if (skill == null || (GameConstants.isAngel(attack.skill) && pPlayer.getStat().equippedSummon % 10000 != attack.skill % 10000)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            
            if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {

                // 1% of Maximum HP as Skill Cost.
                int hpCost = (c.getPlayer().getMaxHP() / 150);
                c.getPlayer().addHP(-hpCost);

                // Demon Avenger Overload Stacks
                int exceedMax = c.getPlayer().getSkillLevel(31220044) > 0 ? 18 : 20;
                if (c.getPlayer().getExceed() + 1 > exceedMax) {
                    c.getPlayer().setExceed((short) exceedMax);
                } else {
                    c.getPlayer().gainExceed((short) 1);
                }
                if (GameConstants.isExceedAttack(skill.getId())) {
                    pPlayer.handleExceedAttack(skill.getId());
                }
            } else if (GameConstants.isDemonSlayer(pPlayer.getJob()) && !pPlayer.hasBuff(CharacterTemporaryStat.InfinityForce)) {
                
                switch (attack.skill) { // Hack fix for some Fury Costs.
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
                    pPlayer.dropMessage(5, "Combo :" + pPlayer.getComboStack());
                    Explorer.HeroHandler.handleComboAttack(pPlayer);
                    Explorer.HeroHandler.handleComboOrbs(pPlayer, attack.skill);
                    //pPlayer.handleComboAttack();
                } else {
                    pPlayer.setComboStack(0);
                }
            } else if (GameConstants.isBlaster(pPlayer.getJob())) {
                switch (attack.skill) {
                    case 37000011:
                    case 37000012:
                    case 37000013:
                    case Blaster.BUNKER_BUSTER_EXPLOSION:
                        Resistance.BlasterHandler.handleOverheat(pPlayer);
                        break;
                    case 37001004:
                    case Blaster.REVOLVING_CANNON_PLUS:
                    case Blaster.REVOLVING_CANNON_PLUS_2:
                    case Blaster.REVOLVING_CANNON_PLUS_3: 
                        Resistance.BlasterHandler.handleAmmoCost(pPlayer);
                        Resistance.BlasterHandler.handleGaugeIncrease(pPlayer);
                        c.write(JobPacket.BlasterPacket.onRWMultiChargeCancelRequest((byte) 1, attack.skill));
                        break;
                    
                }
            }

            switch (attack.skill) {
                case Zero.RISING_SLASH:
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
            skillLevel = pPlayer.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(pPlayer, skillLevel <= 0 ? attack.skillLevel : skillLevel, skill);
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

            if (effect != null) {
                if (GameConstants.isAngelicBuster(pPlayer.getJob())) {
                    int Recharge = effect.getOnActive();
                    if (Recharge > -1) {
                        if (Randomizer.isSuccess(Recharge)) {
                            c.write(JobPacket.AngelicPacket.unlockSkill());
                            c.write(JobPacket.AngelicPacket.showRechargeEffect());
                        } else {
                            c.write(JobPacket.AngelicPacket.lockSkill(attack.skill));
                        }
                    } else {
                        c.write(JobPacket.AngelicPacket.lockSkill(attack.skill));
                    }
                }
            }
            if (effect != null) {
                maxdamage *= (effect.getDamage() + pPlayer.getStat().getDamageIncrease(attack.skill)) / 100.0D;
                attackCount = effect.getAttackCount();

                // Handle cooldown
                if (effect.getCooldown(pPlayer) > 0 && !passiveAttack) {
                    if (pPlayer.skillisCooling(attack.skill)) {
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                    pPlayer.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(pPlayer));
                }
            }
        }

        DamageParse.modifyCriticalAttack(attack, pPlayer, 1, effect);

        attackCount *= (mirror ? 2 : 1);
        if (!passiveAttack) {
            if ((pPlayer.getMapId() == 109060000 || pPlayer.getMapId() == 109060002 || pPlayer.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowball.MapleSnowballs.hitSnowball(pPlayer);
            }

            int numFinisherOrbs = 0;
            Integer comboBuff = pPlayer.getBuffedValue(CharacterTemporaryStat.ComboCounter);

            if (PlayerHandler.isFinisher(attack.skill) > 0) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                if (numFinisherOrbs <= 0) {
                    return;
                }
                pPlayer.handleOrbconsume(PlayerHandler.isFinisher(attack.skill));
            }
        }
        pPlayer.checkFollow();
        
        if (attack.skill != Aran.MAHAS_DOMAIN) {
        
            if (!pPlayer.isHidden()) {
                pPlayer.getMap().broadcastMessage(pPlayer, CField.closeRangeAttack(pPlayer.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, passiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), attack.attackFlag, attack.charge), pPlayer.getTruePosition());
            } else {
                pPlayer.getMap().broadcastGMMessage(pPlayer, CField.closeRangeAttack(pPlayer.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, passiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), attack.attackFlag, attack.charge), false);
            }
        } else {
            pPlayer.write(CField.closeRangeAttack(pPlayer.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, passiveAttack, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), attack.attackFlag, attack.charge));
        }
        
        
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        int bulletCount = 1;
        switch (attack.skill) {
            case Page.FLAME_CHARGE:
                bulletCount = effect.getAttackCount();
                DamageParse.applyAttack(attack, skill, pPlayer, skillLevel, maxdamage, effect, AttackType.NON_RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.applyAttack(attack, skill, pPlayer, skillLevel, maxdamage, effect, AttackType.NON_RANGED);
                //DamageParse.applyAttackMagic(attack, skill, chr, effect, maxdamage);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }

        // Cleanup memory refs
        attack.cleanupMemory();
    }

}
