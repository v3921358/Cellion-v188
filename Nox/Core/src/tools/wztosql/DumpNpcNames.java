package tools.wztosql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.Database;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import tools.StringUtil;

/**
 *
 * @author Itzik
 */
public class DumpNpcNames {

    private static final Map<Integer, String> npcNames = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        System.out.println("Dumping npc name data.");
        DumpNpcNames dump = new DumpNpcNames();
        dump.dumpNpcNameData();
        System.out.println("Dump complete.");
    }

    public void dumpNpcNameData() throws SQLException {
        MapleDataProvider npcData = WzDataStorage.getNPCWz();
        MapleDataProvider stringDataWZ = WzDataStorage.getStringWZ();
        MapleData npcStringData = stringDataWZ.getData("Npc.img");
        try (PreparedStatement ps = Database.GetConnection().prepareStatement("DELETE FROM `wz_npcnamedata`")) {
            ps.execute();
        }
        for (MapleData c : npcStringData) {
            int nid = Integer.parseInt(c.getName());
            String n = StringUtil.getLeftPaddedStr(nid + ".img", '0', 11);
            try {
                if (npcData.getData(n) != null) {//only thing we really have to do is check if it exists. if we wanted to, we could get the script as well :3
                    String name = MapleDataTool.getString("name", c, "MISSINGNO");
                    if (name.contains("Maple TV") || name.contains("Baby Moon Bunny")) {
                        continue;
                    }
                    npcNames.put(nid, name);
                }
            } catch (NullPointerException e) {
            } catch (RuntimeException e) { //swallow, don't add if 
            }
        }
        for (int key : npcNames.keySet()) {
            try {
                try (PreparedStatement ps = Database.GetConnection().prepareStatement(
                        "INSERT INTO `wz_npcnamedata` (`npc`, `name`) VALUES (?, ?)")) {
                    ps.setInt(1, key);
                    ps.setString(2, npcNames.get(key));
                    ps.execute();
                }
                System.out.println("key: " + key + " name: " + npcNames.get(key));
            } catch (Exception ex) {
                System.out.println("Failed to save key " + key);
            }
        }
    }
}
