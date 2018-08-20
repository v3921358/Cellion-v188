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
import handling.world.MaplePartyCharacter;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class EnterAzwanHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().getMapId() != 262000300) {
            //c.SendPacket(CField.pvpBlocked(1));
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (c.getPlayer().getLevel() < 40) {
            //c.SendPacket(CField.pvpBlocked(1));
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        byte mode = iPacket.DecodeByte();
        byte difficult = iPacket.DecodeByte();
        byte party = iPacket.DecodeByte();
        int mapid = 262020000 + (mode * 1000) + difficult; //Supply doesn't have difficult but it's always 0 so idc
        if (party == 1 && c.getPlayer().getParty() == null) {
            //c.SendPacket(CField.pvpBlocked(9));
            c.SendPacket(WvsContext.enableActions());
        }
        if (party == 1 && c.getPlayer().getParty() != null) {
            for (MaplePartyCharacter partymembers : c.getPlayer().getParty().getMembers()) {
                if (c.getChannelServer().getPlayerStorage().getCharacterById(partymembers.getId()).getMapId() != 262000300) {
                    c.getPlayer().dropMessage(1, "Please make sure all of your party members are in the same map.");
                    c.SendPacket(WvsContext.enableActions());
                }
            }
        }
        if (party == 1 && c.getPlayer().getParty() != null) {
            for (MaplePartyCharacter partymember : c.getPlayer().getParty().getMembers()) {
                c.getChannelServer().getPlayerStorage().getCharacterById(partymember.getId()).changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
            }
        } else {
            //party = 0;
            c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
        }
        //EventManager em = c.getChannelServer().getEventSM().getEventManager("Azwan");
        //EventInstanceManager eim = em.newInstance("Azwan");
        //eim.setProperty("Global_StartMap", mapid + "");
        //eim.setProperty("Global_ExitMap", (party == 1 ? 262000100 : 262000200) + "");
        //eim.setProperty("Global_MinPerson", 1 + "");
        //eim.setProperty("Global_RewardMap", (party == 1 ? 262000100 : 262000200) + "");
        //eim.setProperty("CurrentStage", "1");
    }

}
