package handling.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.Database;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import server.MaplePackageActions;
import server.maps.objects.User;
import tools.LogHelper;
import tools.Pair;

public class PackageHandler {

    public static boolean addMesoToDB(final int mesos, final String sName, final int recipientID, final boolean isOn) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, recipientID);
                ps.setString(2, sName);
                ps.setInt(3, mesos);
                ps.setLong(4, System.currentTimeMillis());
                ps.setInt(5, isOn ? 0 : 1);
                ps.setInt(6, 3);

                ps.executeUpdate();
            }

            return true;
        } catch (SQLException se) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
            return false;
        }
    }

    public static boolean addItemToDB(final Item item, final int quantity, final int mesos, final String sName, final int recipientID, final boolean isOn) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)", RETURN_GENERATED_KEYS)) {
                ps.setInt(1, recipientID);
                ps.setString(2, sName);
                ps.setInt(3, mesos);
                ps.setLong(4, System.currentTimeMillis());
                ps.setInt(5, isOn ? 0 : 1);

                ps.setInt(6, item.getType().getTypeValue());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ItemLoader.PACKAGE.saveItems(Collections.singletonList(new Pair<>(item, GameConstants.getInventoryType(item.getItemId()))), rs.getInt(1), con);
                    }
                }
            }

            return true;
        } catch (SQLException se) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
            return false;
        }
    }

    public static List<MaplePackageActions> loadItems(final User chr) {
        List<MaplePackageActions> packages = new LinkedList<>();
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM dueypackages WHERE RecieverId = ?")) {
                ps.setInt(1, chr.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        MaplePackageActions pack = getItemByPID(rs.getInt("packageid"));
                        pack.setSender(rs.getString("SenderName"));
                        pack.setMesos(rs.getInt("Mesos"));
                        pack.setSentTime(rs.getLong("TimeStamp"));
                        packages.add(pack);
                    }
                }
            }

            return packages;
        } catch (SQLException se) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
            return null;
        }
    }

    public static MaplePackageActions loadSingleItem(final int packageid, final int charid) {
        List<MaplePackageActions> packages = new LinkedList<>();
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueypackages WHERE PackageId = ? and RecieverId = ?");
            ps.setInt(1, packageid);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MaplePackageActions pack = getItemByPID(packageid);
                pack.setSender(rs.getString("SenderName"));
                pack.setMesos(rs.getInt("Mesos"));
                pack.setSentTime(rs.getLong("TimeStamp"));
                packages.add(pack);
                rs.close();
                ps.close();

                return pack;
            } else {
                rs.close();
                ps.close();

                return null;
            }
        } catch (SQLException se) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
            return null;
        }
    }

    public static void reciveMsg(final ClientSocket c, final int recipientId) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE dueypackages SET Checked = 0 where RecieverId = ?")) {
                ps.setInt(1, recipientId);
                ps.executeUpdate();
            }
        } catch (SQLException se) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
        }

    }

    public static void removeItemFromDB(final int packageid, final int charid) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM dueypackages WHERE PackageId = ? and RecieverId = ?")) {
                ps.setInt(1, packageid);
                ps.setInt(2, charid);
                ps.executeUpdate();
            }
        } catch (SQLException se) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
        }

    }

    public static MaplePackageActions getItemByPID(final int packageid) {
        try (Connection con = Database.GetConnection()) {

            Map<Long, Pair<Item, MapleInventoryType>> iter = ItemLoader.PACKAGE.loadItems(false, packageid, con);
            if (iter != null && iter.size() > 0) {
                for (Pair<Item, MapleInventoryType> i : iter.values()) {
                    return new MaplePackageActions(packageid, i.getLeft());
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
        }

        return new MaplePackageActions(packageid);
    }
}
