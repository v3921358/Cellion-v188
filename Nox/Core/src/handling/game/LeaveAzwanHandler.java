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
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class LeaveAzwanHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().inAzwan()) {
            //c.SendPacket(CField.pvpBlocked(6));
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        //c.getPlayer().cancelAllBuffs();
        //c.getPlayer().changeRemoval();
        //c.getPlayer().dispelDebuffs();
        //c.getPlayer().clearAllCooldowns();
        //c.write(CWvsContext.clearMidMsg());
        //c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(262000200));
        c.SendPacket(CField.showEffect("hillah/fail"));
        c.SendPacket(CField.UIPacket.sendAzwanResult());
        //c.getPlayer().getStats().recalcLocalStats(c.getPlayer());
        //c.getPlayer().getStats().heal(c.getPlayer());
    }

}
