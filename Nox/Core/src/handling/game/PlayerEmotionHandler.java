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

import java.lang.ref.WeakReference;

import client.ClientSocket;
import client.anticheat.CheatingOffense;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.Timer;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class PlayerEmotionHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        int emote = iPacket.DecodeInt();
        if (emote > 7) {
            final int emoteid = 5159992 + emote;
            final MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
        }
        if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden()) { //O_o
            chr.getMap().broadcastMessage(chr, CField.facialExpression(chr, emote), false);

        }
    }

}
