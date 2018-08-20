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
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class AngelicDressup implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        int transformationId = iPacket.DecodeInt();
        boolean isAngelic;
        if (transformationId == 5010094 || transformationId == 5010093) {//5010093 too?
            isAngelic = true;
            chr.getMap().broadcastPacket(chr, CField.showAngelicBusterTransformation(chr.getId(), transformationId), false);
            chr.getMap().broadcastPacket(chr, CField.updateCharLook(chr, isAngelic), false);
            chr.setAngelicDressupState(isAngelic);
            c.SendPacket(WvsContext.enableActions());
        } else {
            //change back?
            isAngelic = false;
            chr.getMap().broadcastPacket(chr, CField.showAngelicBusterTransformation(chr.getId(), transformationId), false);
            chr.getMap().broadcastPacket(chr, CField.updateCharLook(chr, isAngelic), false);
            chr.setAngelicDressupState(isAngelic);
            c.SendPacket(WvsContext.enableActions());
        }
    }
}
