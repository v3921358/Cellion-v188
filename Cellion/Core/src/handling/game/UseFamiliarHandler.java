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
import client.inventory.Item;
import enums.InventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleFamiliar;
import server.life.LifeFactory;
import server.maps.objects.User;
import server.maps.objects.MonsterFamiliar;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class UseFamiliarHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(iPacket.DecodeInt());
        short slot = iPacket.DecodeShort();
        int itemId = iPacket.DecodeInt();
        Item toUse = chr.getInventory(InventoryType.USE).getItem(slot);
        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 287)) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        MapleFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(itemId);
        if (LifeFactory.getMonsterStats(f.getMob()).getLevel() <= c.getPlayer().getLevel()) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(f.getFamiliar());
            if (mf != null) {
                if (mf.getVitality() >= 3) {
                    mf.setExpiry(Math.min(System.currentTimeMillis() + 7776000000L, mf.getExpiry() + 2592000000L));
                } else {
                    mf.setVitality(mf.getVitality() + 1);
                    mf.setExpiry(mf.getExpiry() + 2592000000L);
                }
            } else {
                mf = new MonsterFamiliar(c.getPlayer().getId(), f.getFamiliar(), System.currentTimeMillis() + 2592000000L);
                c.getPlayer().getFamiliars().put(f.getFamiliar(), mf);
            }
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false, false);
            c.SendPacket(CField.registerFamiliar(mf));
        }
    }

}
