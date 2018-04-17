package client.inventory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

import database.Database;
import tools.LogHelper;

public class MapleInventoryIdentifier implements Serializable {

    private static final long serialVersionUID = 21830921831301L;
    private final AtomicInteger runningUID = new AtomicInteger(0);
    private static final MapleInventoryIdentifier instance = new MapleInventoryIdentifier();

    public static int getInstance() {
        return instance.getNextUniqueId();
    }

    public int getNextUniqueId() {
        if (runningUID.get() <= 0) {
            runningUID.set(initUID());
        } else {
            runningUID.set(runningUID.get() + 1);
        }
        return runningUID.get();
    }

    public int initUID() {
        int ret = 0;
        try (Connection con = Database.GetConnection()) {
            System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

            int[] ids = new int[5];
            PreparedStatement ps = con.prepareStatement("SELECT MAX(uniqueid) FROM inventoryitems WHERE uniqueid > 0");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ids[0] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(petid) FROM pets");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[1] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(ringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[2] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(partnerringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[3] = rs.getInt(1) + 1; //biggest pl0x. but if this happens -> o_O
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(uniqueid) FROM androids");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[4] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            for (int i = 0; i < ids.length; i++) {
                if (ids[i] > ret) {
                    ret = ids[i];
                }
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info(this.getClass().getName() + "\nThere was an issue with something from the database the database:\n", e);
        }
        System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

        return ret;
    }
}
