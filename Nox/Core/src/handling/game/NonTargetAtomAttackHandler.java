package handling.game;

import java.lang.ref.WeakReference;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.SkillConstants;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import handling.world.AttackType;
import handling.world.DamageParse;
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
import server.life.MobAttackInfo;
import service.RecvPacketOpcode;

public final class NonTargetAtomAttackHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || chr.hasBlockedInventory() || chr.getMap() == null) {
            return;
        }

        //AttackInfo attack = DamageParse.parseDmgMa(iPacket, chr);
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserNonTargetForceAtomAttack, iPacket, chr); // Not sure if parse will work on this
        if (attack == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
        if (skill == null || (GameConstants.isAngel(attack.skill) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int skillLevel = chr.getTotalSkillLevel(skill);
        StatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            return;
        } else if (effect.getCooldown(chr) > 0) {  // Handle cooldowns
            if (chr.skillisCooling(attack.skill)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr));
        }

        int bulletCount = 1;
        switch (attack.skill) {
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
                bulletCount = effect.getAttackCount();
                DamageParse.applyAttack(attack, skill, chr, skillLevel, GameConstants.damageCap, effect, AttackType.RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.applyAttackMagic(attack, skill, chr, effect);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }
        DamageParse.modifyCriticalAttack(attack, chr, 3, effect);

        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!chr.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "You may not use that here.");
                            return;
                        }
                    }
                }
            }
        }

        // Map attack/movement broadcast, this needs to be broadcasted first before applying
        // otherwise if the monster is killed with a single hit the damage is not shown.
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.attackFlag), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.attackFlag), false);
        }

        // Other unimportant stuff here... highest priority = first
        chr.checkFollow();

        // Cleanup memory refs
        attack.cleanupMemory();
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
