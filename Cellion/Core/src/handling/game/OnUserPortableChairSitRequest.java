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
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import enums.InventoryType;
import constants.GameConstants;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class OnUserPortableChairSitRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int dwField = iPacket.DecodeInt();
        int chairItem = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(InventoryType.SETUP).findById(chairItem);
        if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(chairItem));
            return;
        }
        if (GameConstants.isFishingMap(chr.getMapId()) && chairItem == 3011000) {
            chr.startFishingTask();
        }
        chr.setChair(chairItem);
        chr.getMap().broadcastPacket(chr, CField.showChair(chr.getId(), chairItem), false);
        c.SendPacket(WvsContext.enableActions());
    }
}
