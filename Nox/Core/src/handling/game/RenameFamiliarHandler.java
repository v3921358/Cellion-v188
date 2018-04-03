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

import client.MapleCharacterUtil;
import client.MapleClient;
import server.maps.objects.User;
import server.maps.objects.MonsterFamiliar;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class RenameFamiliarHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(iPacket.DecodeInteger());
        if (mf == null) {
            c.write(CWvsContext.enableActions());
            return;
        }
        String newName = iPacket.DecodeString();
        if (newName.isEmpty() || MapleCharacterUtil.isEligibleFamiliarName(newName, c.isGm())) {
            mf.setName(newName);
            c.write(CField.renameFamiliar(mf));
        } else {
            chr.dropMessage(1, "This name cannot be used.");
        }
        c.write(CWvsContext.enableActions());
    }

}
