package handling.game;

import java.lang.ref.WeakReference;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import client.jobs.Hero;
import client.jobs.Hero.LuminousHandler;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import client.jobs.Kinesis.*;
import constants.skills.Magician;
import service.ChannelServer;
import server.StatEffect;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.JobPacket;
import net.ProcessPacket;
import server.life.mob.MobStatRequest;
import service.RecvPacketOpcode;

public final class MagicAttack implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer == null || pPlayer.hasBlockedInventory() || pPlayer.getMap() == null) {
            return;
        }

        //AttackInfo attack = DamageParse.parseDmgMa(iPacket, chr);
        AttackInfo pAttack = DamageParse.OnAttack(RecvPacketOpcode.UserMagicAttack, iPacket, pPlayer);

        if (GameConstants.isLuminous(pPlayer.getJob())) {
            LuminousHandler.handleLuminousGauge(pPlayer, pAttack.skill);
        }

        if (pAttack == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        Skill pSkill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(pAttack.skill));
        if (pSkill == null || (GameConstants.isAngel(pAttack.skill) && (pPlayer.getStat().equippedSummon % 10000 != pAttack.skill % 10000))) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int nSkillLevel = pPlayer.getTotalSkillLevel(pSkill);
        StatEffect pEffect = pAttack.getAttackEffect(pPlayer, nSkillLevel, pSkill);
        if (pEffect == null) { // Is it neccessary to check this?
            pEffect = pAttack.getAttackEffect(pPlayer, pSkill.getMaxLevel(), pSkill);
            //return;
        } else if (pEffect.getCooldown(pPlayer) > 0) {  // Handle cooldowns
            if (pPlayer.skillisCooling(pAttack.skill)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            pPlayer.addCooldown(pAttack.skill, System.currentTimeMillis(), pEffect.getCooldown(pPlayer));
        }

        int bulletCount = 1;
        switch (pAttack.skill) {
            case Magician.ENERGY_BOLT:
            case Magician.ENERGY_BOLT_1:
            case 140001289: // Psychic Attack
            case 27101100: // Sylvan Lance
            case 27101202: // Pressure Void
            case 27111100: // Spectral Light
            case 27111202: // Moonlight Spear
            case 27121100: // Reflection
            case 27001100:
            case 27121202: // Apocalypse
            case 2121006: // Paralyze
            case 2221003: // 
            case 2221006: // Chain Lightning
            case 2221007: // Blizzard
            case 2221012: // Frozen Orb
            case 2111003: // Poison Mist
            case 12111005: //Flame Gear?
            case 2121003: // Myst Eruption
            case 22181002: // Dark Fog
            case 2321054:
            case 27121303:
            case 27111303:
            case 36121013:
                //   case 36101009:
                //     case 36111010:
                bulletCount = pEffect.getAttackCount();
                DamageParse.OnWeaponAttackRequest(pAttack, pSkill, pPlayer, nSkillLevel, GameConstants.damageCap, pEffect, AttackType.RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.OnMagicAttackRequest(pAttack, pSkill, pPlayer, pEffect);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }
        DamageParse.OnCriticalAttack(pAttack, pPlayer, 3, pEffect);

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

        // Kinesis Psychic Points handling.
        if (GameConstants.isKinesis(pPlayer.getJob())) {
            KinesisHandler.handlePsychicPoint(pPlayer, pAttack.skill);
        }

        // Map attack/movement broadcast, this needs to be broadcasted first before applying
        // otherwise if the monster is killed with a single hit the damage is not shown.
        if (!pPlayer.isHidden()) {
            pPlayer.getMap().broadcastPacket(pPlayer, CField.magicAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSkillLevel, pAttack.display, pAttack.speed, pAttack.allDamage, pAttack.charge, pPlayer.getLevel(), pAttack.attackFlag), pPlayer.getTruePosition());
        } else {
            pPlayer.getMap().broadcastGMMessage(pPlayer, CField.magicAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSkillLevel, pAttack.display, pAttack.speed, pAttack.allDamage, pAttack.charge, pPlayer.getLevel(), pAttack.attackFlag), false);
        }

        // Other unimportant stuff here... highest priority = first
        pPlayer.checkFollow();

        // Cleanup memory refs
        pAttack.cleanupMemory();
    }

    public static void handleLuminousState(User chr, AttackInfo attack) {
        if (chr.getLevel() <= 30 || chr.getLuminousState() > 20040000) {
            return;
        }

        // Handle Luminous state
        switch (attack.skill) {
            case 27001100: // Flash Shower
            case 27101100: //Sylvan Lance
            case 27111100: //Spectral Light
            case 27121100: //Reflection
                chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040216, chr.getLightGauge(), chr.getDarkGauge(), 2000000000));
                chr.setLuminousState(20040216);
                break;
            case 27001201:
            case 27101202:
            case 27111202:
            case 27121202:
                chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040217, chr.getLightGauge(), chr.getDarkGauge(), 2000000000));
                chr.setLuminousState(20040217);
                break;
        }
    }
}
