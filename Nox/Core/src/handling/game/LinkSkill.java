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
import net.ProcessPacket;

public final class LinkSkill implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        int sourceSkill = iPacket.DecodeInt();
        int linkSkill = 0;
        int charID = iPacket.DecodeInt();
        int accID = c.getPlayer().getAccountID();

        switch (sourceSkill) {
            case 110:
                linkSkill = 80000000;
                break;
            case 20021110:
                linkSkill = 80001040;
                break;
            case 20030204:
                linkSkill = 80000002;
                break;
            case 20040218:
                linkSkill = 80000005;
                break;
            case 30010112:
                linkSkill = 80000001;
                break;
            case 50001214:
                linkSkill = 80001140;
                break;
            case 60000222:
                linkSkill = 80000006;
                break;
            case 60011219:
                linkSkill = 80001155;
                break;
        }
        chr.giveLinkSkill(chr, linkSkill, accID, charID, chr.getSkillLevel(sourceSkill), chr.getMasterLevel(sourceSkill));
    }

}
