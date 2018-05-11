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
import constants.GameConstants;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;
import net.ProcessPacket;

public final class AngelicTransformation implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();
        byte type = iPacket.DecodeByte();
        if (type == 1) {
            if (GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
                c.SendPacket(JobPacket.AngelicPacket.DressUpTime(type));
                c.SendPacket(JobPacket.AngelicPacket.updateDress(chr.getAngelicDressupSuit(), c.getPlayer()));//default = 5010094
                chr.getMap().broadcastMessage(chr, CField.updateCharLook(chr, true), false);//PLZ TEST ANGELIC CHANGE! <-- tells me these handlers might be fucked
            } else {
                c.SendPacket(CWvsContext.enableActions());
            }
        }
    }
}
