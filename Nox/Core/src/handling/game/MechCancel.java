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
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class MechCancel implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int sourceid = iPacket.DecodeInt();
        if ((sourceid % 10000 < 1000) && (SkillFactory.getSkill(sourceid) == null)) {
            sourceid += 1000;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill == null) {
            return;
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkillTime(0L);
            chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
        } else {
            chr.cancelEffect(skill.getEffect(iPacket.DecodeByte()), false, -1L);
        }
    }
}
