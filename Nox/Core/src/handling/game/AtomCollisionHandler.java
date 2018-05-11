/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.BlazeWizard;
import constants.skills.Shade;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import handling.world.PlayerHandler;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;
import server.MapleStatEffect;
import server.events.MapleEvent;
import server.events.MapleEventType;
import service.ChannelServer;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.WvsContext;

public final class AtomCollisionHandler implements ProcessPacket<ClientSocket> {

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
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserForceAtomCollision, iPacket, pPlayer); // Not sure if my parse will work here
        if (attack == null) {
            if (pPlayer.isDeveloper()) {
                pPlayer.dropMessage(5, "[AtomCollision Debug] Atom ID : " + attack.skill);
            }
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
        if (skill == null || (GameConstants.isAngel(attack.skill) && (pPlayer.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
            if (pPlayer.isDeveloper()) {
                pPlayer.dropMessage(5, "[AtomCollision Debug] Returning Early 1 / Atom ID : " + attack.skill);
            }
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int skillLevel = pPlayer.getTotalSkillLevel(skill);
        MapleStatEffect effect = attack.getAttackEffect(pPlayer, skillLevel, skill);
        if (effect == null) {
            if (pPlayer.isDeveloper()) {
                pPlayer.dropMessage(5, "[AtomCollision Debug] Returning Early 2 / Atom ID : " + attack.skill);
            }
            return;
        } else if (effect.getCooldown(pPlayer) > 0) {  // Handle cooldowns
            if (pPlayer.skillisCooling(attack.skill)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            pPlayer.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(pPlayer));
        }

        pPlayer.dropMessage(5, "[Debug] Atom ID : " + attack.skill);

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
            case 14000028: // Night Walker Shadow Bat
            case Shade.FOX_SPIRITS:
            case Shade.FOX_SPIRIT_MASTERY:
            case BlazeWizard.ORBITAL_FLAME:
            case BlazeWizard.ORBITAL_FLAME_1:
            case BlazeWizard.GREATER_ORBITAL_FLAME:
            case BlazeWizard.GREATER_ORBITAL_FLAME_1:
            case BlazeWizard.GRAND_ORBITAL_FLAME:
            case BlazeWizard.GRAND_ORBITAL_FLAME_1:
            case BlazeWizard.FINAL_ORBITAL_FLAME:
            case BlazeWizard.FINAL_ORBITAL_FLAME_1:
                //   case 36101009:
                //     case 36111010:
                bulletCount = effect.getAttackCount();
                DamageParse.applyAttack(attack, skill, pPlayer, skillLevel, GameConstants.damageCap, effect, AttackType.RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.applyAttackMagic(attack, skill, pPlayer, effect);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }
        DamageParse.modifyCriticalAttack(attack, pPlayer, 3, effect);

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

        // Map attack/movement broadcast, this needs to be broadcasted first before applying
        // otherwise if the monster is killed with a single hit the damage is not shown.
        if (!pPlayer.isHidden()) {
            pPlayer.getMap().broadcastMessage(pPlayer, CField.magicAttack(pPlayer.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, pPlayer.getLevel(), attack.attackFlag), pPlayer.getTruePosition());
        } else {
            pPlayer.getMap().broadcastGMMessage(pPlayer, CField.magicAttack(pPlayer.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, pPlayer.getLevel(), attack.attackFlag), false);
        }

        // Other unimportant stuff here... highest priority = first
        pPlayer.checkFollow();

        // Cleanup memory refs
        attack.cleanupMemory();
    }
}
