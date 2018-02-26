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

import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import scripting.EventInstanceManager;
import scripting.EventManager;
import server.Randomizer;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class EnterPvpHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().getMapId() != 960000000) {
            c.write(CField.pvpBlocked(1));
            c.write(CWvsContext.enableActions());
            return;
        }
        if (c.getPlayer().getParty() != null) {
            c.write(CField.pvpBlocked(9));
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        iPacket.Skip(1);
        int type = iPacket.DecodeByte(), lvl = iPacket.DecodeByte(), playerCount = 0;
        boolean passed = false;
        switch (lvl) {
            case 0:
                passed = c.getPlayer().getLevel() >= 30 && c.getPlayer().getLevel() < 70;
                break;
            case 1:
                passed = c.getPlayer().getLevel() >= 70;
                break;
            case 2:
                passed = c.getPlayer().getLevel() >= 120;
                break;
            case 3:
                passed = c.getPlayer().getLevel() >= 180;
                break;
        }
        final EventManager em = c.getChannelServer().getEventSM().getEventManager("PVP");
        if (!passed || em == null) {
            c.write(CField.pvpBlocked(1));
            c.write(CWvsContext.enableActions());
            return;
        }
        final List<Integer> maps = new ArrayList<>();
        switch (type) {
            case 0:
                maps.add(960010100);
                maps.add(960010101);
                maps.add(960010102);
                break;
            case 1:
                maps.add(960020100);
                maps.add(960020101);
                maps.add(960020102);
                maps.add(960020103);
                break;
            case 2:
                maps.add(960030100);
                break;
            case 3:
                maps.add(689000000);
                maps.add(689000010);
                break;
            default:
                passed = false;
                break;
        }
        if (!passed) {
            c.write(CField.pvpBlocked(1));
            c.write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().getStat().heal(c.getPlayer());
        c.getPlayer().cancelAllBuffs();
        c.getPlayer().dispelDebuffs();
        c.getPlayer().changeRemoval();
        c.getPlayer().clearAllCooldowns();
        c.getPlayer().unequipAllPets();
        final StringBuilder key = new StringBuilder().append(lvl).append(" ").append(type).append(" ");
        //check if any of the maps are available
        for (int i : maps) {
            final EventInstanceManager eim = em.getInstance(new StringBuilder("PVP").append(key.toString()).append(i).toString().replace(" ", "").replace(" ", ""));
            if (eim != null && (eim.getProperty("started").equals("0") || eim.getPlayerCount() < 10)) {
                eim.registerPlayer(c.getPlayer());
                return;
            }
        }
        //make one
        em.startInstance_Solo(key.append(maps.get(Randomizer.nextInt(maps.size()))).toString(), c.getPlayer());
    }

}
