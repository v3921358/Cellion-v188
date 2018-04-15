/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Maple
 */
import database.Database;
import tools.LogHelper;

public class DojoRankingsData {

    private DojoRankingsData() {
    }
    private static DojoRankingsData instance = new DojoRankingsData();
    private static int limit = 25;

    public static DojoRankingsData getInstance() {
        return instance;
    }

    public String[] names = new String[limit];
    public long[] times = new long[limit];
    public int[] ranks = new int[limit];
    public int totalCharacters = 0;

    public static DojoRankingsData loadLeaderboard() {
        DojoRankingsData ret = new DojoRankingsData();
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps = con.prepareStatement("SELECT `name`, `time` FROM `dojorankings` ORDER BY `time` ASC LIMIT " + limit);
            ResultSet rs = ps.executeQuery();

            int i = 0;
            while (rs.next()) {
                if (rs.getInt("time") != 0) {
                    //long time = (rs.getLong("endtime") - rs.getLong("starttime")) / 1000;
                    ret.ranks[i] = (i + 1);
                    ret.names[i] = rs.getString("name");
                    ret.times[i] = rs.getInt("time");
                    // ret.times[i] = time;
                    ret.totalCharacters++;
                    i++;
                } else {
                    //donothing;
                }
            }
            ps.close();
            rs.close();
            //PreparedStatement pss = con.prepareStatement("INSERT INTO dojodata VALUES(DEFAULT, DEFAULT, DEFAULT, ?, DEFAULT, DEFAULT)");
            //pss.setLong(1, time);
            //ResultSet rss = pss.executeQuery(); 
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
        return ret;
    }
}
