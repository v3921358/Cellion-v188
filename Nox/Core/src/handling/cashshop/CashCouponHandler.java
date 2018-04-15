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

import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.Item;
import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import server.MapleInventoryManipulator;
import tools.Triple;
import net.InPacket;
import tools.packet.CSPacket;
import net.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author Novak
 */
public final class CashCouponHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static boolean CouponCodeAttempt(final MapleClient c) {
        c.couponAttempt++;
        return c.couponAttempt > 5;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        useCoupon(iPacket, c);
    }

    public static void useCoupon(InPacket iPacket, MapleClient c) {
        iPacket.Skip(2);
        String code = iPacket.DecodeString();

        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            c.SendPacket(CSPacket.sendCSFail(49));
            return;
        }
        if (info != null && info.left) {
            if (!CouponCodeAttempt(c)) {
                int type = info.mid, item = info.right;
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                    c.SendPacket(CSPacket.sendCSFail(49));
                    return;
                }
                /*
                 * Explanation of type!
                 * Basically, this makes coupon codes do
                 * different things!
                 *
                 * Type 1: A-Cash,
                 * Type 2: Maple Points
                 * Type 3: Item.. use SN
                 * Type 4: Mesos
                 */
                Map<Integer, Item> itemz = new HashMap<>();
                int maplePoints = 0, mesos = 0;
                switch (type) {
                    case 1:
                    case 2: //Confirmed MaplePoints
                        c.getPlayer().modifyCSPoints(type, item, false);
                        maplePoints = item;
                        break;
                    case 3:
                        c.getPlayer().gainMeso(item, false);
                        mesos = item;
                    case 4:
                        short quantity = 1;
                        MapleInventoryManipulator.addById(c, item, (short) quantity, null);
                        break;
                    case 5:
                        MapleInventoryManipulator.addById(c, item, (short) 100, null);
                        break;
                }
                c.SendPacket(CSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
                playerCashShopInfo(c);
            }
        } else if (CouponCodeAttempt(c) == true) {
            c.SendPacket(CSPacket.sendCSFail(48)); //A1, 9F
        } else {
            c.SendPacket(CSPacket.sendCSFail(info == null ? 14 : 17)); //A1, 9F
        }
    }
}
