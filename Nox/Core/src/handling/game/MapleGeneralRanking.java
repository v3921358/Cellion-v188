/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import tools.LogHelper;

/**
 *
 * @author Itzik
 */
public class MapleGeneralRanking {

    private static final MapleGeneralRanking instance = new MapleGeneralRanking();
    private final List<CandyRankingInfo> candyranks = new LinkedList<>();
    private final int max = Integer.MAX_VALUE;

    public static MapleGeneralRanking getInstance() {
        return instance;
    }

    public void load() {
        if (candyranks.isEmpty()) {
            reload();
        }
    }

    public List<CandyRankingInfo> getCandyRanks() {
        return candyranks;
    }

    public void reload() {
        candyranks.clear();
        PreparedStatement ps;
        ResultSet rs;
        short rank = 1;
        try (Connection con = Database.GetConnection()) {

            ps = con.prepareStatement("SELECT * FROM characters WHERE deletedAt is null ORDER BY `candies` DESC LIMIT " + max);
            rs = ps.executeQuery();
            while (rs.next()) {
                final CandyRankingInfo rankfield = new CandyRankingInfo(rank, rs.getString("name"), rs.getInt("level"));
                candyranks.add(rankfield);
                rank++;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

    }

    public static class CandyRankingInfo {

        private final String name;
        private final short rank;
        private final int candies;

        public CandyRankingInfo(short rank, String name, int candies) {
            this.rank = rank;
            this.name = name;
            this.candies = candies;
        }

        public short getRank() {
            return rank;
        }

        public String getName() {
            return name;
        }

        public int getCandies() {
            return candies;
        }
    }
}
