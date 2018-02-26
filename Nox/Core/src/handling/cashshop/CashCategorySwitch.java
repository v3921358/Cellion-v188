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
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.InPacket;
import tools.packet.CSPacket;
import netty.ProcessPacket;
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
                int itemSn = iPacket.DecodeInteger();
                try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `wishlist` VALUES (?, ?)")) {
                    ps.setInt(1, c.getPlayer().getId());
                    ps.setInt(2, itemSn);
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    LogHelper.SQL.get().info("[MapleClient] Failed altering wishlist:\n", ex);
                }
                c.write(CSPacket.addFavorite(false, itemSn));
                break;
            case 105:
                int item = iPacket.DecodeInteger();
                try {
                    Connection con = DatabaseConnection.getConnection();
                    try (PreparedStatement ps = con.prepareStatement("UPDATE cashshop_items SET likes = likes+" + 1 + " WHERE sn = ?")) {
                        ps.setInt(1, item);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                }
                c.write(CSPacket.Like(item));
                break;
            case 109:
                c.write(CSPacket.Favorite(c.getPlayer()));
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
                int newcat = iPacket.DecodeInteger();
                if (newcat == 4000000) {
                    c.write(CSPacket.CS_Top_Items());
                    c.write(CSPacket.SetCashShopBannerPicture());
                } else {
                    c.write(CSPacket.changeCategory(newcat));
                }
                break;
        }
    }
}
