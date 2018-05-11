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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import client.Client;
import net.InPacket;
import tools.packet.FarmPacket;
import net.ProcessPacket;

public final class FarmQuestCompletionHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int questId = iPacket.DecodeInt();
        if (questId == 1111) {
            c.SendPacket(FarmPacket.updateQuestInfo(1111, 1, ""));
            SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            sdfGMT.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
            String timeStr = sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", "");
            c.SendPacket(FarmPacket.updateQuestInfo(1111, 2, timeStr));
            c.SendPacket(FarmPacket.alertQuest(1111, 0));
            c.SendPacket(FarmPacket.updateQuestInfo(1112, 0, "A1/"));
            c.SendPacket(FarmPacket.updateQuestInfo(1112, 1, "A1/Z/"));
        }
    }

}
