package tools;

import java.util.Scanner;

import client.inventory.MapleInventoryType;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.MapleStringInformationProvider;

/**
 *
 * @author Itzik
 */
public class ImportItemSet {

    public static void main(String[] args) {
        while (true) {
            System.out.println("Set name: ex. Imperial, Timeless, Abyss");
            Scanner input = new Scanner(System.in);
            String name = input.next();
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                final String itemName = item.getValue().getLeft();

                if (itemName.startsWith(name) && !itemName.equals(name)
                        && (MapleItemInformationProvider.getInventoryType(item.getKey()) == MapleInventoryType.EQUIP || itemName.contains("recipe"))) {
                    sb.append(item.getKey()).append(" - ").append(itemName).append("\r\n");
                }
            }
            System.out.println(sb.toString());
            if (accept(input.next())) {
                main(args);
            }
        }
    }

    public static boolean accept(String toCheck) {
        switch (toCheck.toLowerCase()) {
            case "y":
            case "yes":
            case "true":
                return true;
        }
        return false;
    }
}
