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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.MapleClient;
import client.MapleStat;
import server.maps.objects.MapleCharacter;
import tools.Pair;
import net.InPacket;
import tools.packet.CField;
import netty.ProcessPacket;

public final class RespawnPvpHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final Lock ThreadLock = new ReentrantLock();
        /*if (c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().inPVP() || c.getPlayer().isAlive()) {
         c.write(CWvsContext.enableActions());
         return;
         }*/
        final int type = Integer.parseInt(c.getPlayer().getEventInstance().getProperty("type"));
        byte lvl = 0;
        c.getPlayer().getStat().heal_noUpdate(c.getPlayer());
        c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMp());
        //c.getPlayer().getEventInstance().schedule("broadcastType", 500);   
        ThreadLock.lock();
        try {
            c.getPlayer().getEventInstance().schedule("updateScoreboard", 500);
        } finally {
            ThreadLock.unlock();
        }
        c.getPlayer().changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(type == 0 ? 0 : (type == 3 ? (c.getPlayer().getTeam() == 0 ? 3 : 1) : (c.getPlayer().getTeam() == 0 ? 2 : 3))));
        //c.write(CField.getPVPScore(Integer.parseInt(c.getPlayer().getEventInstance().getProperty(String.valueOf(c.getPlayer().getId()))), false));

        if (c.getPlayer().getLevel() >= 30 && c.getPlayer().getLevel() < 70) {
            lvl = 0;
        } else if (c.getPlayer().getLevel() >= 70 && c.getPlayer().getLevel() < 120) {
            lvl = 1;
        } else if (c.getPlayer().getLevel() >= 120 && c.getPlayer().getLevel() < 180) {
            lvl = 2;
        } else if (c.getPlayer().getLevel() >= 180) {
            lvl = 3;
        }

        List<MapleCharacter> players = c.getPlayer().getEventInstance().getPlayers();
        List<Pair<Integer, String>> players1 = new LinkedList<>();
        for (int xx = 0; xx < players.size(); xx++) {
            players1.add(new Pair<>(players.get(xx).getId(), players.get(xx).getName()));
        }
        //c.write(CField.getPVPType(type, players1, c.getPlayer().getTeam(), true, lvl));
        //c.write(CField.enablePVP(true));
    }

}
