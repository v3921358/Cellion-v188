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
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class CharacterInfoRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        int objectid = iPacket.DecodeInteger();
        MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
        c.write(CWvsContext.enableActions());
        if (player != null) {
            c.write(CWvsContext.charInfo(player, c.getPlayer().getId() == objectid));
        }
    }

}
