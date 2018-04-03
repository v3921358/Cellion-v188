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

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Shade;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import handling.world.PlayerHandler;
import server.maps.objects.User;
import net.InPacket;
import netty.ProcessPacket;
import server.MapleStatEffect;
import server.events.MapleEvent;
import server.events.MapleEventType;
import service.ChannelServer;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.CWvsContext;

public final class AtomCollisionHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || chr.hasBlockedInventory() || chr.getMap() == null) {
            return;
        }

        //AttackInfo attack = DamageParse.parseDmgMa(iPacket, chr);
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserForceAtomCollision, iPacket, chr); // Not sure if my parse will work here
        if (attack == null) {
            c.write(CWvsContext.enableActions());
            return;
        }
        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
        if (skill == null || (GameConstants.isAngel(attack.skill) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
            c.write(CWvsContext.enableActions());
            return;
        }
        int skillLevel = chr.getTotalSkillLevel(skill);
        MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            return;
        } else if (effect.getCooldown(chr) > 0) {  // Handle cooldowns
            if (chr.skillisCooling(attack.skill)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr));
        }

        chr.dropMessage(5, "[Debug] Atom ID : " + attack.skill);

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
}
