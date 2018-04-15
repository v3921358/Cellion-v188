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
package handling.farm;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;

public final class FarmBuildingPlacementHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int position = iPacket.DecodeInt();
        int itemId = iPacket.DecodeInt();
        iPacket.DecodeByte(); //idk
        if (itemId / 10000 < 112 || itemId / 10000 > 114) {
            return;
        }
        if (position > (25 * 25) - 1) { //biggest farm 25x25
            return;
        }
        int size = (itemId / 10000) % 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!c.getFarm().checkSpace(size, position - j - i)) {
                    return;
                }
            }
        }
        //c.getFarm().getFarmInventory().updateItemQuantity(itemId, -1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
    }

}
