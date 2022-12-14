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
import enums.Stat;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class OnUserGivePopularityRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        final int who = iPacket.DecodeInt();
        final int mode = iPacket.DecodeByte();

        final int famechange = mode == 0 ? -1 : 1;
        final User target = chr.getMap().getCharacterById(who);

        if (target == null || target == chr) { // faming self
            c.SendPacket(WvsContext.giveFameErrorResponse(1));
            return;
        } else if (chr.getLevel() < 15) {
            c.SendPacket(WvsContext.giveFameErrorResponse(2));
            return;
        }
        switch (chr.canGiveFame(target)) {
            case OK:
                if (Math.abs(target.getFame() + famechange) <= 99999) {
                    target.addFame(famechange);
                    target.updateSingleStat(Stat.Fame, target.getFame());
                }
                if (!chr.isGM()) {
                    chr.hasGivenFame(target);
                }
                c.SendPacket(WvsContext.OnFameResult(0, target.getName(), famechange == 1, target.getFame()));
                target.getClient().SendPacket(WvsContext.OnFameResult(5, chr.getName(), famechange == 1, 0));
                break;
            case NOT_TODAY:
                c.SendPacket(WvsContext.giveFameErrorResponse(3));
                break;
            case NOT_THIS_MONTH:
                c.SendPacket(WvsContext.giveFameErrorResponse(4));
                break;
        }
    }
}
