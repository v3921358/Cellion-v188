/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.export;

import enums.EquipSlotType;
import enums.ItemType;
import database.Database;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import provider.wz.cache.WzDataStorage;
import server.CashItemFactory;
import server.MapleItemInformationProvider;

/**
 *
 * @author Kaz Voeten
 */
public class CashShopFiller {

    private class CashCategoryItem {

        public int itemid, category, subcategory, parent, expire, quantity, flag, discount, price, likes, sn;
        public byte gender;
    }

    private static int getCategoryByType(EquipSlotType type) {
        switch (type) {
            case Weapon:
            case Weapon_TakingBothSlot_Shield:
                return 1060100;
            case Shoes:
                return 1061000;
            case Shoulder:
                return 1060211;
            case Shield_OrDualBlade:
                return 1060101;
            case Ring:
                return 1061200;
            case Pants:
                return 1060902;
            case Longcoat:
                return 1060700;
            case Coat:
                return 1060800;
            case Glove:
                return 1061100;
            case Cape:
                return 1061300;
            case Cap:
            case CashCap:
                return 1060200;
            case Accessary_Face:
                return 1060300;
            case Accessary_Eye:
                return 1060400;
            case Pendant:
            case Belt:
                return 1060211;
            case Earring:
                return 1060600;
            default:
                return -1;

        }
    }

    private ArrayList<String> sql = new ArrayList<>();
    private HashMap<Integer, CashCategoryItem> mItems = new HashMap<>();
    int sn = 1000000;

    public static void main(String[] args) {
        System.out.println("And attempt will be made to add all cash equip items known to man to the cash shop.");
        try {
            (new CashShopFiller()).parse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CashShopFiller() {
        Database.Initialize();
        WzDataStorage.load();
        MapleItemInformationProvider.getInstance().runEtc();
        MapleItemInformationProvider.getInstance().runItems();
    }

    private void parse() throws IOException {
        insertBase();

        try (Connection con = Database.GetConnection(); ResultSet rs = con.prepareStatement("SELECT * FROM cashshop_items").executeQuery()) {
            while (rs.next()) {
                CashCategoryItem cItem = new CashCategoryItem();
                cItem.category = rs.getInt("category");
                cItem.subcategory = rs.getInt("subcategory");
                cItem.discount = rs.getInt("discountPrice");
                cItem.expire = rs.getInt("expire");
                cItem.flag = rs.getInt("flag");
                cItem.gender = rs.getByte("gender");
                cItem.itemid = rs.getInt("itemid");
                cItem.parent = rs.getInt("parent") - (rs.getInt("parent") % 10); //fixes wrong parents!
                cItem.price = rs.getInt("price");
                cItem.quantity = rs.getInt("quantity");
                cItem.sn = rs.getInt("sn");
                cItem.likes = rs.getInt("likes");
                mItems.put(cItem.sn, cItem);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /* Factory gives us too many broken items it seems..?
        MapleItemInformationProvider.getInstance().getAllItems().forEach((item)
                -> {
            if (MapleItemInformationProvider.getInstance().isCash(item.itemId)) {
                if (item.eq != null && item.eq.getType() == ItemType.Equipment && !isBlockedByMazen(item.itemId) && !mItems.containsKey(item.itemId)) {
                    CashCategoryItem cItem = new CashCategoryItem();
                    EquipSlotType pType = MapleItemInformationProvider.getInstance().getSlotType(item.itemId);
                    if (pType != null) {
                        cItem.category = getCategoryByType(pType);
                        if (cItem.category > 0) {
                            cItem.itemid = item.itemId;
                            cItem.gender = (byte) ((item.itemId / 1000) % 10);
                            cItem.parent = 1060000;
                            cItem.sn = sn++;
                            cItem.expire = 1;
                            cItem.flag = 0;
                            cItem.quantity = 1;
                            cItem.discount = 5000;
                            cItem.price = 5000;
                            mItems.put(cItem.itemid, cItem);
                        }
                    }
                }
            }
        });
         */
        dumpSQL();
    }

    private void dumpSQL() throws IOException {
        mItems.forEach((key, cItem) -> {
            sql.add("(" + cItem.category + ", " + cItem.subcategory+ ", " + cItem.parent + ", '', " + cItem.sn + ", " + cItem.itemid + ", " + cItem.flag + ", " + cItem.price + ", " + cItem.discount + ", " + cItem.quantity + ", " + cItem.expire + ", " + cItem.gender + ", " + cItem.likes + "),");

        });
        FileWriter fw = new FileWriter("output.sql");
        for (String line : sql) {
            fw.write(line + "\r\n");
        }
        fw.close();
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
        sql.add("INSERT INTO `cashshop_items` (`category`, `subcategory`, `parent`, `image`, `sn`, `itemid`, `flag`, `price`, `discountPrice`, `quantity`, `expire`, `gender`, `likes`) VALUES");
        sql.add("");
    }

    private boolean isBlockedByMazen(int Equip) {
        switch (Equip) {
            case 1042367:
            case 1042362:
            case 1042363:
            case 1042364:
            case 1042357:
            case 1048002:
            case 1042350:
            case 1042332:
            case 1042319:
            case 1042314:
            case 1042311:
            case 1042276:
            case 1042313:
            case 1042199:
            case 1042200:
            case 1042206:
            case 1042216:
            case 1042217:
            case 1042218:
            case 1042219:
            case 1042232:
            case 1048001:
            case 1042267:
            case 1042251:
            case 1042278:
            case 1042275:
            case 1042279:
            case 1042280:
            case 1042281:
            case 1042282:
            case 1042290:
            case 1042315:
            case 1042349:
            case 1042329:
            case 1042330:
            case 1042338:
            case 1042337:
            case 1042334:
            case 1042336:
            case 1042335:
            case 1042343:
            case 1042312:
            case 1053116:
            case 1053127:
            case 1053097:
            case 1053098:
            case 1053093:
            case 1053094:
            case 1053099:
            case 1053082:
            case 1052940:
            case 1053035:
            case 1050423:
            case 1050424:
            case 1050428:
            case 1050414:
            case 1050419:
            case 1051435:
            case 1051488:
            case 1052674:
            case 1052675:
            case 1052676:
            case 1052677:
            case 1052684:
            case 1052902:
            case 1052901:
            case 1053049:
            case 1053109:
            case 1053110:
            case 1050153:
            case 1053059:
            case 1050293:
            case 1050291:
            case 1050301:
            case 1050300:
            case 1050305:
            case 1050365:
            case 1050371:
            case 1050359:
            case 1050384:
            case 1050385:
            case 1050417:
            case 1051373:
            case 1052595:
            case 1052587:
            case 1052671:
            case 1052948:
            case 1052923:
            case 1052954:
            case 1052996:
            case 1052211:
            case 1052213:
            case 1053040:
            case 1053061:
            case 1052594:
            case 1053091:
            case 1050422:
            case 1052231:
            case 1052442:
            case 1052586:
            case 1052661:
            case 1051190:
            case 1053033:
            case 1051368:
            case 1051391:
            case 1050351:
            case 1082704:
            case 1082705:
            case 1082702:
            case 1082713:
            case 1082714:
            case 1082558:
            case 1082685:
            case 1082694:
            case 1082689:
            case 1082692:
            case 1082631:
            case 1082620:
            case 1082581:
            case 1082550:
            case 1082592:
            case 1082684:
            case 1082588:
            case 1082519:
            case 1082505:
            case 1082312:
            case 1082634:
            case 1082500:
            case 1082548:
            case 1073181:
            case 1073182:
            case 1073155:
            case 1073046:
            case 1073047:
            case 1072934:
            case 1072897:
            case 1072873:
            case 1072840:
            case 1072829:
            case 1073144:
            case 1073040:
            case 1072803:
            case 1073019:
            case 1072951:
            case 1004878:
            case 1004862:
            case 1004863:
            case 1004854:
            case 1004848:
            case 1004849:
            case 1004576:
            case 1004544:
            case 1004545:
            case 1004546:
            case 1004547:
            case 1004548:
            case 1004540:
            case 1004110:
            case 1004111:
            case 1003367:
            case 1003538:
            case 1003597:
            case 1003596:
            case 1003714:
            case 1002999:
            case 1003214:
            case 1003215:
            case 1003268:
            case 1003777:
            case 1003778:
            case 1003803:
            case 1003867:
            case 1003890:
            case 1003910:
            case 1003914:
            case 1004003:
            case 1004004:
            case 1004026:
            case 1004027:
            case 1004028:
            case 1004029:
            case 1004166:
            case 1004180:
            case 1004384:
            case 1004472:
            case 1004470:
            case 1004503:
            case 1004589:
            case 1004723:
            case 1004688:
            case 1004204:
            case 1004205:
            case 1004203:
            case 1004455:
            case 1004480:
            case 1004471:
            case 1004490:
            case 1004633:
            case 1004618:
            case 1004619:
            case 1004620:
            case 1004659:
            case 1003882:
            case 1003829:
            case 1003518:
            case 1062234:
            case 1062233:
            case 1062225:
            case 1062231:
            case 1062220:
            case 1062103:
            case 1062124:
            case 1062126:
            case 1062136:
            case 1062156:
            case 1062175:
            case 1062153:
            case 1062183:
            case 1062172:
            case 1062155:
            case 1062232:
            case 1102991:
            case 1102982:
            case 1102981:
            case 1102972:
            case 1102968:
            case 1102959:
            case 1102960:
            case 1102950:
            case 1102946:
            case 1102947:
            case 1102935:
            case 1102936:
            case 1102912:
            case 1102939:
            case 1102917:
            case 1102884:
            case 1102870:
            case 1102841:
            case 1102842:
            case 1102827:
            case 1102820:
            case 1102787:
            case 1102767:
            case 1102729:
            case 1102723:
            case 1102724:
            case 1102292:
            case 1102261:
            case 1102267:
            case 1102310:
            case 1102451:
            case 1102450:
            case 1102452:
            case 1102511:
            case 1102632:
            case 1102630:
            case 1102644:
            case 1102699:
            case 1102700:
            case 1102748:
            case 1102766:
            case 1102789:
            case 1102869:
            case 1102906:
            case 1102336:
            case 1102835:
            case 1102301:
            case 1102957:
            case 1102811:
            case 1102381:
            case 1102396:
            case 1102708:
            case 1102779:
            case 1102956:
            case 1102798:
            case 1102508:
            case 1702696:
            case 1702716:
            case 1702709:
            case 1702701:
            case 1702475:
            case 1702469:
            case 1702375:
            case 1702687:
            case 1702680:
            case 1702682:
            case 1702649:
            case 1702651:
            case 1702645:
            case 1702643:
            case 1702632:
            case 1702634:
            case 1702395:
            case 1702399:
            case 1702457:
            case 1702467:
            case 1702436:
            case 1702480:
            case 1702565:
            case 1702613:
            case 1702586:
            case 1702624:
            case 1702653:
            case 1702224:
            case 1702235:
            case 1702374:
            case 1702409:
            case 1702556:
            case 1702585:
            case 1702526:
            case 1702710:
            case 1115121:
            case 1115032:
            case 1115123:
            case 1115108:
            case 1115109:
            case 1115110:
            case 1115034:
            case 1115028:
            case 1115117:
            case 1115019:
            case 1115020:
            case 1115021:
            case 1112137:
            case 1112141:
            case 1112142:
            case 1112143:
            case 1112144:
            case 1112145:
            case 1112146:
            case 1112148:
            case 1112149:
            case 1112150:
            case 1112151:
            case 1112152:
            case 1112153:
            case 1112154:
            case 1112155:
            case 1112156:
            case 1112157:
            case 1112159:
            case 1112162:
            case 1112163:
            case 1112164:
            case 1112165:
            case 1112166:
            case 1112170:
            case 1112171:
            case 1112172:
            case 1112173:
            case 1112176:
            case 1112177:
            case 1112178:
            case 1112179:
            case 1112180:
            case 1112181:
            case 1112182:
            case 1112183:
            case 1112184:
            case 1112190:
            case 1112191:
            case 1112192:
            case 1112193:
            case 1112194:
            case 1112195:
            case 1112196:
            case 1112197:
            case 1112198:
            case 1112199:
            case 1115004:
            case 1115005:
            case 1115006:
            case 1115007:
            case 1115008:
            case 1115009:
            case 1115010:
            case 1115011:
            case 1115104:
            case 1115103:
            case 1012412:
            case 1012462:
            case 1012485:
            case 1012495:
            case 1012517:
            case 1012518:
            case 1012501:
            case 1012510:
            case 1012534:
            case 1012562:
            case 1010006:
            case 1022223:
            case 1022243:
            case 1022065:
            case 1022044:
            case 1022207:
            case 1022109:
            case 1022108:
            case 1022074:
            case 1022249:
            case 1022259:
            case 1022173:
            case 1022270:
            case 1022247:
            case 1022050:
                return true;
            default:
                return false;
        }
    }
}
