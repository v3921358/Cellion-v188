package tools;

import java.util.ArrayList;
import java.util.List;

import server.ItemInformation;
import server.MapleItemInformationProvider;
import server.MapleStringInformationProvider;
import server.quest.MapleQuest;
import tools.Pair;
import tools.Tuple;

/**
 *
 * @author Itzik
 */
public class SearchGenerator {

    public static String searchData(int type, String search) {
        String result = "";

        List<String> ret = new ArrayList<>();

        switch (type) {
            case 1:
                for (ItemInformation pair : MapleItemInformationProvider.getInstance().getAllItems()) {
                    if (pair != null && pair.name != null && pair.name.toLowerCase().contains(search.toLowerCase())) {
                        ret.add("\r\n#L" + pair.itemId + "##b" + pair.itemId + " " + " #k- " + " #r#z" + pair.itemId + "##k");
                    }
                }
                break;
            case 2:
                for (java.util.Map.Entry<Integer, String> npcPair : MapleStringInformationProvider.getNPCStringCache().entrySet()) {
                    if (npcPair.getValue().toLowerCase().contains(search.toLowerCase())
                            || search.toLowerCase().contains(npcPair.getValue().toLowerCase())) {
                        ret.add("\r\n#L" + npcPair.getKey() + "#" + npcPair.getKey() + " - " + npcPair.getValue());
                    }
                }
                break;
            case 3:
                for (java.util.Map.Entry<Integer, Pair<String, String>> mapPair : MapleStringInformationProvider.getMapStringCache().entrySet()) {
                    if (mapPair.getValue().getRight().toLowerCase().contains(search.toLowerCase())
                            || search.toLowerCase().contains(mapPair.getValue().getRight())) {
                        final String fullMapName = mapPair.getValue().getLeft() + " - " + mapPair.getValue().getRight();
                        ret.add("\r\n" + fullMapName);

                        ret.add("\r\n#L" + mapPair.getKey() + "#" + mapPair.getKey() + " - " + fullMapName);
                    }
                }
                break;
            case 4: // Mobs
                for (java.util.Map.Entry<Integer, String> mobPair : MapleStringInformationProvider.getMobStringCache().entrySet()) {
                    if (mobPair.getValue().toLowerCase().contains(search.toLowerCase())) {
                        ret.add("\r\n#L" + mobPair.getKey() + "#" + mobPair.getKey() + " - " + mobPair.getValue());
                    }
                }
                break;
            case 5:
                for (MapleQuest pair : MapleQuest.getAllInstances()) {
                    if (pair.getName().length() > 0 && pair.getName().toLowerCase().contains(search.toLowerCase())) {
                        ret.add("\r\n#L" + pair.getId() + "#" + pair.getId() + " - " + pair.getName());
                    }
                }
                break;
            case 6:

                for (java.util.Map.Entry<Integer, Tuple<String, String, String>> itemValue : MapleStringInformationProvider.getSkillStringCache().entrySet()) {
                    final String skillName = itemValue.getValue().get_2();

                    if (skillName.toLowerCase().contains(search.toLowerCase()) || search.toLowerCase().contains(skillName)) {
                        ret.add("\r\n#L" + itemValue.getKey() + "#" + itemValue.getKey() + " - " + skillName);
                    }
                }
                break;
            default:
                break;
        }
        if (ret.size() > 0) {
            for (String singleRet : ret) {
                if (result.length() < 10000) {
                    result += singleRet;
                } else {
                    result += "\r\n#bCouldn't load all data, there are too many results.#k";
                    return result;
                }
            }
        } else {
            result = "";
        }
        return result;
    }

    public static boolean foundData(int type, String search) {
        return !searchData(type, search).isEmpty();
    }
}
