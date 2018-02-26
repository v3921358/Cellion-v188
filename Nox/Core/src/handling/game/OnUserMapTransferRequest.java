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
import net.InPacket;
import server.maps.FieldLimitType;
import server.maps.objects.MapleCharacter;
import tools.packet.CSPacket;
import netty.ProcessPacket;

public final class OnUserMapTransferRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        byte addrem = iPacket.DecodeByte();
        byte vip = iPacket.DecodeByte();

        switch (vip) {
            case 1:
                if (addrem == 0) {
                    chr.deleteFromRegRocks(iPacket.DecodeInteger());
                } else if (addrem == 1) {
                    if (!FieldLimitType.VipRock.checkFlag(chr.getMap())) {
                        chr.addRegRockMap();
                    } else {
                        chr.dropMessage(1, "This map is not available to enter for the list.");
                    }
                }
                break;
            case 2:
                if (addrem == 0) {
                    chr.deleteFromRocks(iPacket.DecodeInteger());
                } else if (addrem == 1) {
                    if (!FieldLimitType.VipRock.checkFlag(chr.getMap())) {
                        chr.addRockMap();
                    } else {
                        chr.dropMessage(1, "This map is not available to enter for the list.");
                    }
                }
                break;
            case 3:
            case 5:
                if (addrem == 0) {
                    chr.deleteFromHyperRocks(iPacket.DecodeInteger());
                } else if (addrem == 1) {
                    if (!FieldLimitType.VipRock.checkFlag(chr.getMap())) {
                        chr.addHyperRockMap();
                    } else {
                        chr.dropMessage(1, "This map is not available to enter for the list.");
                    }
                }
                break;
            default:
                break;
        }
        c.write(CSPacket.OnMapTransferResult(chr, vip, addrem == 0));
    }
}
