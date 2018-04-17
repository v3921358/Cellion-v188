package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import database.Database;
import tools.LogHelper;
import tools.Triple;

public class MapleCharacterUtil {

    private static final Pattern petPattern = Pattern.compile("[a-zA-Z0-9]{4,12}");
    private static final Pattern familiarPattern = Pattern.compile("[a-zA-Z0-9 ]{4,12}");

    public static boolean isEligibleFamiliarName(String name, boolean isGM) {
        if (name.length() > 30) {
            return false;
        }
        if (isGM) {
            return true;
        }
        if (name.length() < 3 || !familiarPattern.matcher(name).matches()) {
            return false;
        }
        return true;
    }

    public static boolean canChangePetName(final String name) {
        if (petPattern.matcher(name).matches()) {
            return true;
        }
        return false;
    }

    public static String makeMapleReadable(final String in) {
        String wui = in.replace('I', 'i');
        wui = wui.replace('l', 'L');
        wui = wui.replace("rn", "Rn");
        wui = wui.replace("vv", "Vv");
        wui = wui.replace("VV", "Vv");
        return wui;
    }

    public static int getIdByName(final String name) {
        final int id;
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ? AND deletedAt is null LIMIT 1")) {
                ps.setString(1, name);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return -1;
                    }
                    id = rs.getInt("id");
                }
            } catch (SQLException exp) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", exp);
                return -1;
            }
        } catch (SQLException exp) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", exp);
            return -1;
        }

        return id;
    }

    // -2 = An unknown error occured
    // -1 = Account not found on database
    // 0 = You do not have a second password set currently.
    // 1 = The password you have input is wrong
    // 2 = Password Changed successfully
    public static int Change_SecondPassword(final int accid, final String password, final String newpassword) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * from accounts where id = ?")) {
                ps.setInt(1, accid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return -1;
                    }
                    String secondPassword = rs.getString("2ndpassword");
                    if (secondPassword == null) {
                        rs.close();
                        ps.close();
                        return 0;
                    }
                    if (!crypto.BCrypt.checkpw(password, secondPassword)) {
                        rs.close();
                        ps.close();
                        return 1;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts set 2ndpassword = ? where id = ?")) {
                try {
                    String BCryptedPass = crypto.BCrypt.hashpw(newpassword, crypto.BCrypt.gensalt());
                    ps.setString(1, BCryptedPass);
                    ps.setInt(2, accid);

                    if (!ps.execute()) {
                        ps.close();
                        return 2;
                    }
                } catch (Exception e) {
                    LogHelper.GENERAL_EXCEPTION.get().info(e);
                    return -2;
                }
            } catch (Exception e) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
                return -2;
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
            return -2;
        }

        return -2;
    }

//id accountid gender
    public static Triple<Integer, Integer, Integer> getInfoByName(String name, int world) {

        Triple<Integer, Integer, Integer> id = null;
        try (Connection con = Database.GetConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name = ? AND world = ? AND deletedAt is null")) {
                ps.setString(1, name);
                ps.setInt(2, world);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return null;
                    }
                    id = new Triple<>(rs.getInt("id"), rs.getInt("accountid"), rs.getInt("gender"));
                }
            } catch (Exception e) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }

        return id;
    }

    public static void setNXCodeUsed(String name, String code) throws SQLException {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE nxcode SET `user` = ?, `valid` = 0 WHERE code = ?")) {
                ps.setString(1, name);
                ps.setString(2, code);
                ps.execute();
            } catch (Exception e) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }

    }

    public static void sendNote(String to, String name, String msg, int fame) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, to);
                ps.setString(2, name);
                ps.setString(3, msg);
                ps.setLong(4, System.currentTimeMillis());
                ps.setInt(5, fame);
                ps.executeUpdate();
            } catch (Exception e) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }

    }

    public static Triple<Boolean, Integer, Integer> getNXCodeInfo(String code) throws SQLException {
        Triple<Boolean, Integer, Integer> ret = null;
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT `valid`, `type`, `item` FROM nxcode WHERE code LIKE ?")) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = new Triple<>(rs.getInt("valid") > 0, rs.getInt("type"), rs.getInt("item"));
                    }
                }
            } catch (Exception e) {
                LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }

        return ret;
    }
}
