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
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class SkillEffectHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        int skillId = iPacket.DecodeInt();
        if (skillId >= 91000000 && skillId < 100000000) {
            chr.getClient().SendPacket(CWvsContext.enableActions());
            return;
        }
        byte level = iPacket.DecodeByte();
        short direction = iPacket.DecodeShort();
        byte unk = iPacket.DecodeByte();

        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(skillId));
        if ((chr == null) || (skill == null) || (chr.getMap() == null)) {
            return;
        }
        int skilllevel_serv = chr.getTotalSkillLevel(skill);

        if ((skilllevel_serv > 0) && (skilllevel_serv == level) && ((skillId == 33101005) || (skill.isChargeSkill()))) {
            chr.setKeyDownSkillTime(System.currentTimeMillis());
            if (skillId == 33101005 || skillId == 27101202) {
                chr.setLinkMid(iPacket.DecodeInt(), 0);
            }
            chr.getMap().broadcastMessage(chr, CField.skillEffect(chr, skillId, level, direction, unk), false);
        }
    }

}
