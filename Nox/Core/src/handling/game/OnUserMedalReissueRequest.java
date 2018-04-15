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
import client.MapleQuestStatus;
import java.time.LocalDateTime;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import server.quest.MapleQuest;
import tools.packet.CField;
import net.ProcessPacket;

public final class OnUserMedalReissueRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleQuest q = MapleQuest.getInstance(iPacket.DecodeShort());
        User chr = c.getPlayer();
        if (q == null) {
            return;
        }

        int itemid = q.getMedalItem();
        if ((itemid != iPacket.DecodeInt()) || (itemid <= 0) || (chr.getQuestStatus(q.getId()) != MapleQuestStatus.MapleQuestState.Completed)) {
            c.SendPacket(CField.UIPacket.reissueMedal(itemid, 4));
            return;
        }
        if (chr.haveItem(itemid, 1, true, true)) {
            c.SendPacket(CField.UIPacket.reissueMedal(itemid, 3));
            return;
        }
        if (!MapleInventoryManipulator.checkSpace(c, itemid, 1, "")) {
            c.SendPacket(CField.UIPacket.reissueMedal(itemid, 2));
            return;
        }
        if (chr.getMeso() < 100) {
            c.SendPacket(CField.UIPacket.reissueMedal(itemid, 1));
            return;
        }
        chr.gainMeso(-100, true, true);
        MapleInventoryManipulator.addById(c, itemid, (byte) 1, new StringBuilder().append("Redeemed item through medal quest ").append(q.getId()).append(" on ").append(LocalDateTime.now()).toString());
        c.SendPacket(CField.UIPacket.reissueMedal(itemid, 0));
    }
}
