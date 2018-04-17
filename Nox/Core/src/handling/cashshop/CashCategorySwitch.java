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

import client.MapleClient;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.InPacket;
import tools.packet.CSPacket;
import net.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author Novak
 */
public final class CashCategorySwitch implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int cat = iPacket.DecodeByte();
        switch (cat) {
            case 103:
                iPacket.Skip(1);
                int itemSn = iPacket.DecodeInt();
                try (Connection con = Database.GetConnection()) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO `wishlist` VALUES (?, ?)")) {
                        ps.setInt(1, c.getPlayer().getId());
                        ps.setInt(2, itemSn);
                        ps.executeUpdate();
                        ps.close();
                    } catch (SQLException ex) {
                        LogHelper.SQL.get().info("[MapleClient] Failed altering wishlist:\n", ex);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CashCategorySwitch.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

                c.SendPacket(CSPacket.addFavorite(false, itemSn));
                break;
            case 105:
                int item = iPacket.DecodeInt();
                try (Connection con = Database.GetConnection()) {
                    System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

                    try (PreparedStatement ps = con.prepareStatement("UPDATE cashshop_items SET likes = likes+" + 1 + " WHERE sn = ?")) {
                        ps.setInt(1, item);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
                }
                System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

                c.SendPacket(CSPacket.Like(item));
                break;
            case 109:
                c.SendPacket(CSPacket.Favorite(c.getPlayer()));
                break;
            //click on special item TODO
            //int C8 - C9 - CA
            case 112:
                break;
            //buy from cart inventory TODO
            //byte buy = 1 or gift = 0
            //byte amount
            //for each SN
            case 113:
                break;
            default:
                int newcat = iPacket.DecodeInt();
                if (newcat == 4000000) {
                    c.SendPacket(CSPacket.CS_Top_Items());
                    c.SendPacket(CSPacket.SetCashShopBannerPicture());
                } else {
                    c.SendPacket(CSPacket.changeCategory(newcat));
                }
                break;
        }
    }
}
