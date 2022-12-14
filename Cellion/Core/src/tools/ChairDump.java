package tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import server.MapleItemInformationProvider;
import server.MapleStringInformationProvider;

/**
 *
 * @author Itzik
 */
public class ChairDump {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileOutputStream out = new FileOutputStream("ChairDump.txt", false);
        StringBuilder sb = new StringBuilder();
        int shopId = 145274;
        int npcId = 9010000;
        sb.append("INSERT INTO shops (`shopid`, `npcid`) VALUES(").append(shopId).append(", ").append(npcId).append(");\r\n");
        int price = 1;

        for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
            if (item.getKey() >= 3010000 && item.getKey() < 3020000) {
                sb.append("INSERT INTO shopitems (`shopid`, `itemid`, `price`, `position`) VALUES(");
                sb.append(shopId).append(", ").append(item.getKey()).append(", ").append(price).append(", 0);\r\n");
            }
        }
        out.write(sb.toString().getBytes());
    }
}
