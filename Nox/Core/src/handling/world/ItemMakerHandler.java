/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.world;

import java.time.LocalDateTime;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleImp;
import client.inventory.MapleImp.ImpFlag;
import constants.GameConstants;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import net.InPacket;
import tools.packet.WvsContext;

public class ItemMakerHandler {

    public static enum CraftRanking {

        SOSO(19, 30),
        GOOD(20, 40),
        COOL(21, 50);
        public int i, craft;

        private CraftRanking(int i, int craft) {
            this.i = i;
            this.craft = craft;
        }
    }

    public static final void UsePot(final InPacket iPacket, final ClientSocket c) {
        final int itemid = iPacket.DecodeInt();
        final Item slot = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(iPacket.DecodeShort());
        if (slot == null || slot.getQuantity() <= 0 || slot.getItemId() != itemid || itemid / 10000 != 244 || MapleItemInformationProvider.getInstance().getPot(itemid) == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.SendPacket(WvsContext.enableActions());
        for (int i = 0; i < c.getPlayer().getImps().length; i++) {
            if (c.getPlayer().getImps()[i] == null) {
                c.getPlayer().getImps()[i] = new MapleImp(itemid);
                c.SendPacket(WvsContext.updateImp(c.getPlayer().getImps()[i], ImpFlag.SUMMONED.getValue(), i, false));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot.getPosition(), (short) 1, false, false);
                return;
            }
        }

    }

    public static final void ClearPot(final InPacket iPacket, final ClientSocket c) {
        final int index = iPacket.DecodeInt() - 1;
        if (index < 0 || index >= c.getPlayer().getImps().length || c.getPlayer().getImps()[index] == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.SendPacket(WvsContext.updateImp(c.getPlayer().getImps()[index], ImpFlag.REMOVED.getValue(), index, false));
        c.getPlayer().getImps()[index] = null;
    }

    public static final void FeedPot(final InPacket iPacket, final ClientSocket c) {
        final int itemid = iPacket.DecodeInt();
        final Item slot = c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getItem((short) iPacket.DecodeInt());
        if (slot == null || slot.getQuantity() <= 0 || slot.getItemId() != itemid) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final int level = GameConstants.getInventoryType(itemid) == MapleInventoryType.ETC ? MapleItemInformationProvider.getInstance().getItemMakeLevel(itemid) : MapleItemInformationProvider.getInstance().getReqLevel(itemid);
        if (level <= 0 || level < (Math.min(120, c.getPlayer().getLevel()) - 50) || (GameConstants.getInventoryType(itemid) != MapleInventoryType.ETC && GameConstants.getInventoryType(itemid) != MapleInventoryType.EQUIP)) {
            c.getPlayer().dropMessage(1, "The item must be within 50 levels of you.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final int index = iPacket.DecodeInt() - 1;
        if (index < 0 || index >= c.getPlayer().getImps().length || c.getPlayer().getImps()[index] == null || c.getPlayer().getImps()[index].getLevel() >= (MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right - 1) || c.getPlayer().getImps()[index].getState() != 1) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int mask = ImpFlag.FULLNESS.getValue();
        mask |= ImpFlag.FULLNESS_2.getValue();
        mask |= ImpFlag.UPDATE_TIME.getValue();
        mask |= ImpFlag.AWAKE_TIME.getValue();
        //this is where the magic happens
        c.getPlayer().getImps()[index].setFullness(c.getPlayer().getImps()[index].getFullness() + (100 * (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP ? 2 : 1)));
        if (Randomizer.nextBoolean()) {
            mask |= ImpFlag.CLOSENESS.getValue();
            c.getPlayer().getImps()[index].setCloseness(c.getPlayer().getImps()[index].getCloseness() + 1 + (Randomizer.nextInt(5 * (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP ? 2 : 1))));
        } else if (Randomizer.nextInt(5) == 0) { //1/10 chance of sickness
            c.getPlayer().getImps()[index].setState(4); //sick
            mask |= ImpFlag.STATE.getValue();
        }
        if (c.getPlayer().getImps()[index].getFullness() >= 1000) {
            c.getPlayer().getImps()[index].setState(1);
            c.getPlayer().getImps()[index].setFullness(0);
            c.getPlayer().getImps()[index].setLevel(c.getPlayer().getImps()[index].getLevel() + 1);
            mask |= ImpFlag.SUMMONED.getValue();
            if (c.getPlayer().getImps()[index].getLevel() >= (MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right - 1)) {
                c.getPlayer().getImps()[index].setState(5);
            }
        }
        MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemid), slot.getPosition(), (short) 1, false, false);
        c.SendPacket(WvsContext.updateImp(c.getPlayer().getImps()[index], mask, index, false));
    }

    public static final void CurePot(final InPacket iPacket, final ClientSocket c) {
        final int itemid = iPacket.DecodeInt();
        final Item slot = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((short) iPacket.DecodeInt());
        if (slot == null || slot.getQuantity() <= 0 || slot.getItemId() != itemid || itemid / 10000 != 434) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final int index = iPacket.DecodeInt() - 1;
        if (index < 0 || index >= c.getPlayer().getImps().length || c.getPlayer().getImps()[index] == null || c.getPlayer().getImps()[index].getState() != 4) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        c.getPlayer().getImps()[index].setState(1);
        c.SendPacket(WvsContext.updateImp(c.getPlayer().getImps()[index], ImpFlag.STATE.getValue(), index, false));
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot.getPosition(), (short) 1, false, false);
    }

    public static final void RewardPot(final InPacket iPacket, final ClientSocket c) {
        final int index = iPacket.DecodeInt() - 1;
        if (index < 0 || index >= c.getPlayer().getImps().length || c.getPlayer().getImps()[index] == null || c.getPlayer().getImps()[index].getLevel() < (MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right - 1)) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final int itemid = GameConstants.getRewardPot(c.getPlayer().getImps()[index].getItemId(), c.getPlayer().getImps()[index].getCloseness());
        if (itemid <= 0 || !MapleInventoryManipulator.checkSpace(c, itemid, (short) 1, "")) {
            c.getPlayer().dropMessage(1, "Please make some space.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Item Pot from " + c.getPlayer().getImps()[index].getItemId() + " on " + LocalDateTime.now());
        c.SendPacket(WvsContext.updateImp(c.getPlayer().getImps()[index], ImpFlag.REMOVED.getValue(), index, false));
        c.getPlayer().getImps()[index] = null;
    }
}
