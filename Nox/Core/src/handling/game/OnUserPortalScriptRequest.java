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

import client.Client;
import server.MaplePortal;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class OnUserPortalScriptRequest implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        iPacket.DecodeByte();
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        String portal_name = iPacket.DecodeString();
        if (chr.isAdmin()) {
            chr.yellowMessage("[OnUserPortalScriptRequest] " + portal_name);
        }
        MaplePortal portal = chr.getMap().getPortal(portal_name);
        if ((portal != null) && (!chr.hasBlockedInventory())) {
            if (chr.isAdmin()) {
            }
            portal.enterPortal(c);
        } else {
            c.SendPacket(CWvsContext.enableActions());
        }
    }
}
