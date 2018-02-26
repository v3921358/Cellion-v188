/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.export;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import provider.data.HexTool;
import provider.wz.cache.WzDataStorage;
import server.MapleItemInformationProvider;
import service.SendPacketOpcode;
import tools.Tuple;

/**
 *
 * @author kaz_v
 */
public class CashShopParser {

    private ArrayList<Tuple<Integer, Integer, Integer>> parsed = new ArrayList<>();
    private ArrayList<String> sql = new ArrayList<>();
    private InPacket iPacket = new InPacket();
    int linenr = 1;
    int itemCount = 0;

    public static void main(String[] args) {
        System.out.println("Parser will now attempt to generate a cash shop sql."
                + " Please take note that this is an extremely unoptimized and slow process.");
        try {
            (new CashShopParser()).parse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CashShopParser() {
        WzDataStorage.load();
        MapleItemInformationProvider.getInstance().runEtc();
        MapleItemInformationProvider.getInstance().runItems();
    }

    private void parse() throws IOException {
        insertBase();
        Path path = Paths.get(System.getProperty("CashShopParse") + "input.txt");
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            parsePacket(line);
            linenr++;
        }
        dumpSQL();
    }

    private void dumpSQL() throws IOException {
        FileWriter fw = new FileWriter(Paths.get(System.getProperty("CashShopParse") + "output.sql").toString());
        for (String line : sql) {
            fw.write(line + "\r\n");
        }
        fw.close();
    }

    private void parsePacket(String packet) {
        iPacket.Next(HexTool.getByteArrayFromHexString(packet));
        int opcode = iPacket.DecodeShort();
        if (opcode == SendPacketOpcode.CASH_SHOP.getValue()) {
            parseMetaData();
        } else if (opcode == SendPacketOpcode.CS_UPDATE.getValue()) {
            //useless shit
        } else if (opcode == SendPacketOpcode.CASH_SHOP_UPDATE.getValue()) {
            parseCategoryData();
        }
    }

    private void parseMetaData() {
        byte action = iPacket.DecodeByte();
        if (action == 3) {
            parseCategories();
        }
        if (action == 4) {
            parseCategoryFrontpage();
        }
    }

    private void parseCategories() {
        sql.add("DROP TABLE IF EXISTS `cashshop_categories`;");
        sql.add("CREATE TABLE IF NOT EXISTS `cashshop_categories` (");
        sql.add("  `id` int(11) NOT NULL AUTO_INCREMENT,");
        sql.add("  `categoryid` int(11) NOT NULL,");
        sql.add("  `name` varchar(255) NOT NULL,");
        sql.add("  `parent` int(11) NOT NULL,");
        sql.add("  `flag` int(11) NOT NULL,");
        sql.add("  `sold` int(11) NOT NULL,");
        sql.add("  PRIMARY KEY (`id`)");
        sql.add(") ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;");
        sql.add("");
        sql.add("DELETE FROM `cashshop_categories`;");
        sql.add("/*!40000 ALTER TABLE `cashshop_categories` DISABLE KEYS */;");
        sql.add("INSERT INTO `cashshop_categories` (`id`, `categoryid`, `name`, `parent`, `flag`, `sold`) VALUES");

        boolean useless = iPacket.DecodeBoolean();
        byte count = iPacket.DecodeByte();
        for (int i = 0; i < count; ++i) {
            int category = iPacket.DecodeInteger();
            String categoryName = iPacket.DecodeString();
            int parent = iPacket.DecodeInteger();
            int flag = iPacket.DecodeInteger();
            int sold = iPacket.DecodeInteger();
            sql.add("(" + (i + 1) + ", " + category + ", '" + categoryName + "', "
                    + parent + ", " + flag + ", " + sold + (i == (count - 1) ? ");" : "),"));
        }

        sql.add("/*!40000 ALTER TABLE `cashshop_categories` ENABLE KEYS */;");
        sql.add("");
    }

    private void parseCategoryFrontpage() {

    }

    private void parseCategoryData() {
        if (iPacket.DecodeByte() != 0x0B) {
            return;
        }
        boolean useless = iPacket.DecodeBoolean();
        byte size = iPacket.DecodeByte();
        System.out.println("Category " + linenr + " has " + size + " entries.");

        for (int i = 0; i < size; ++i) {
            int category = iPacket.DecodeInteger();
            int subcat = iPacket.DecodeInteger();
            int parent = iPacket.DecodeInteger();
            String url = iPacket.DecodeString();
            int SN = iPacket.DecodeInteger();
            int itemID = iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            int flag = iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            int price = iPacket.DecodeInteger();
            iPacket.DecodeLong();
            iPacket.DecodeLong();
            iPacket.DecodeLong();
            iPacket.DecodeLong();
            int discountPrice = iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            int quantity = iPacket.DecodeInteger();
            int expiration = iPacket.DecodeInteger();
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeInteger();
            iPacket.DecodeShort();
            int gender = iPacket.DecodeInteger();
            int likes = iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            String packName = iPacket.DecodeString();
            iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            iPacket.DecodeInteger();
            int pack = iPacket.DecodeInteger();

            if (pack > 0) {
                for (int j = 0; j < pack; j++) {
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                    iPacket.DecodeInteger();
                }
            }

            iPacket.DecodeShort();

            if (!MapleItemInformationProvider.getInstance().itemExists(itemID)) {
                System.out.println("Skipped item as it doesn't exist in this version: " + itemID);
                return;
            }

            //THIS IS EXTREMELY SLOW, just was too lazy to like, set up nested hashmaps or someshit.
            for (Tuple<Integer, Integer, Integer> processedItem : parsed) {
                if (processedItem.get_1() == itemID
                        && processedItem.get_2() == category
                        && processedItem.get_3() == subcat) {
                    //Item already parsed from another packet entry.
                    return;
                }
            }

            parsed.add(new Tuple(itemID, category, subcat));

            itemCount++;

            sql.add("INSERT INTO `cashshop_items` (`id`, `category`, `subcategory`, `parent`, `image`, `sn`, `itemid`, `flag`, `price`, `discountPrice`, `quantity`, `expire`, `gender`, `likes`) "
                    + "VALUES (" + itemCount + ", " + category + ", " + subcat + ", " + parent + ", '" + url + "', " + SN + ", " + itemID + ", " + flag + ", " + price + ", " + discountPrice + ", " + quantity + ", " + expiration + ", " + gender + ", " + likes + ");");
        }
    }

    private void insertBase() {
        sql.add("DROP TABLE IF EXISTS `cashshop_items`;");
        sql.add("CREATE TABLE IF NOT EXISTS `cashshop_items` (");
        sql.add("  `id` int(11) NOT NULL AUTO_INCREMENT,");
        sql.add("  `category` int(11) NOT NULL,");
        sql.add("  `subcategory` int(11) NOT NULL,");
        sql.add("  `parent` int(11) NOT NULL,");
        sql.add("  `image` varchar(255) NOT NULL,");
        sql.add("  `sn` int(11) NOT NULL,");
        sql.add("  `itemid` int(11) NOT NULL,");
        sql.add("  `flag` int(11) NOT NULL,");
        sql.add("  `price` int(11) NOT NULL,");
        sql.add("  `discountPrice` int(11) NOT NULL,");
        sql.add("  `quantity` int(11) NOT NULL,");
        sql.add("  `expire` int(11) NOT NULL,");
        sql.add("  `gender` tinyint(1) NOT NULL DEFAULT '2',");
        sql.add("  `likes` int(11) NOT NULL,");
        sql.add("  PRIMARY KEY (`id`)");
        sql.add(") ENGINE=InnoDB AUTO_INCREMENT=1228 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;");
        sql.add("");
    }

}
