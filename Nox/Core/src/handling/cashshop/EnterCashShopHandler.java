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
package handling.cashshop;

import client.ClientSocket;
import client.ClientSocket.MapleClientLoginState;
import handling.world.CharacterTransfer;
import handling.world.MapleMessengerCharacter;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import service.CashShopServer;
import service.ChannelServer;
import service.FarmServer;
import service.LoginServer;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.FarmPacket;
import net.ProcessPacket;

public final class EnterCashShopHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr.hasBlockedInventory() || chr.getEventInstance() != null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(chr.getId());
        if (farmtransfer != null) {
            c.SendPacket(FarmPacket.farmMessage("You cannot move into Cash Shop while visiting your farm."));
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.changeChannelData(new CharacterTransfer(chr), chr.getId(), -10);
        ch.removePlayer(chr);
        String s = c.getSessionIPAddress();
        c.updateLoginState(MapleClientLoginState.ChangeChannel, s);

        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        c.SendPacket(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);

        c.setPlayer(null);
        c.setReceiving(false);
    }
}
