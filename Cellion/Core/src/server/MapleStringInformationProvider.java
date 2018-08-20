package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import provider.wz.nox.NoxBinaryReader;
import provider.wz.cache.WzDataStorage;
import tools.Pair;
import tools.Tuple;

/**
 *
 * @author Lloyd Korn
 */
public class MapleStringInformationProvider {

    private static final Map<Integer, String> CashWeatherMsgCache = new HashMap(); // 'msg'
    private static final Map<Integer, Pair<String, String>> CashStringCache = new HashMap(); //  "name", "desc", "msg"
    private static final Map<Integer, Pair<String, String>> ConsumeStringCache = new HashMap(); // "name", "desc"
    private static final Map<Integer, Pair<String, String>> InsStringCache = new HashMap(); // "name", "desc"
    private static final Map<Integer, Pair<String, String>> EtcStringCache = new HashMap(); // "name", "desc"
    private static final Map<Integer, Pair<String, String>> EquipStringCache = new HashMap(); // "name", "desc"
    private static final Map<Integer, Pair<String, String>> AllitemsStringCache = new HashMap(); // "name", "desc"

    private static final Map<Integer, Pair<String, String>> MapStringCache = new HashMap(); // "streetName", "mapName"
    private static final Map<Integer, String> MobStringCache = new HashMap(); // "name"
    private static final Map<Integer, String> NPCStringCache = new HashMap(); //  "name", "n0", "n1", "f0", "f1", "d0", "d1"
    private static final Map<Integer, Tuple<String, String, String>> PetStringCache = new HashMap(); //  "name", "desc", "descD"
    private static final Map<Integer, Tuple<String, String, String>> SkillStringCache = new HashMap(); //  "bookName", "name", "desc"

    static {
        try {
            final NoxBinaryReader data = WzDataStorage.getBinaryStringData();

            // Cash Item
            int cashItemSize = data.readInt();
            for (int i = 0; i < cashItemSize; i++) {
                int itemId = data.readInt();
                String name = data.readAsciiString();
                String desc = data.readAsciiString();
                String msg = data.readAsciiString();

                final Pair p = new Pair(name, desc);

                CashStringCache.put(itemId, p);
                CashWeatherMsgCache.put(itemId, msg);
                AllitemsStringCache.put(itemId, p);
            }

            // Consume Item
            int consumeItemSize = data.readInt();
            for (int i = 0; i < consumeItemSize; i++) {
                int itemId = data.readInt();
                String name = data.readAsciiString();
                String desc = data.readAsciiString();

                final Pair p = new Pair(name, desc);

                ConsumeStringCache.put(itemId, p);
                AllitemsStringCache.put(itemId, p);
            }

            // Ins Item
            int insItemSize = data.readInt();
            for (int i = 0; i < insItemSize; i++) {
                int itemId = data.readInt();
                String name = data.readAsciiString();
                String desc = data.readAsciiString();

                final Pair p = new Pair(name, desc);

                InsStringCache.put(itemId, p);
                AllitemsStringCache.put(itemId, p);
            }

            // Etc Item
            int etcItemSize = data.readInt();
            for (int i = 0; i < etcItemSize; i++) {
                String categoryName = data.readAsciiString();

                int etcCategorySize = data.readInt();
                for (int z = 0; z < etcCategorySize; z++) {
                    int itemId = data.readInt();
                    String name = data.readAsciiString();
                    String desc = data.readAsciiString();

                    final Pair p = new Pair(name, desc);

                    EtcStringCache.put(itemId, p);
                    AllitemsStringCache.put(itemId, p);
                }
            }

            // Equip Item
            int equipItemSize = data.readInt();
            for (int i = 0; i < equipItemSize; i++) {
                String categoryName = data.readAsciiString();

                int categorySize = data.readInt();
                for (int z = 0; z < categorySize; z++) {
                    String EqpCategoryName = data.readAsciiString();

                    int size = data.readInt();

                    for (int x = 0; x < size; x++) {
                        int itemId = data.readInt();
                        String name = data.readAsciiString();
                        String desc = data.readAsciiString();

                        final Pair p = new Pair(name, desc);

                        EquipStringCache.put(itemId, p);
                        AllitemsStringCache.put(itemId, p);
                    }
                }
            }

            // Map Item
            int mapCategorySize = data.readInt();
            for (int i = 0; i < mapCategorySize; i++) {
                String areaName = data.readAsciiString();

                int mapSize = data.readInt();
                for (int z = 0; z < mapSize; z++) {
                    int itemId = data.readInt();
                    String streetName = data.readAsciiString();
                    String mapName = data.readAsciiString();

                    MapStringCache.put(itemId, new Pair(streetName, mapName));
                }
            }

            // Mob Item
            int mobSize = data.readInt();
            for (int i = 0; i < mobSize; i++) {
                int mobId = data.readInt();
                String name = data.readAsciiString();

                MobStringCache.put(mobId, name);
            }

            // NPC 
            int npcSize = data.readInt();
            for (int i = 0; i < npcSize; i++) {
                int npcId = data.readInt();
                String npcName = data.readAsciiString();
                String n0 = data.readAsciiString();
                String n1 = data.readAsciiString();
                String f0 = data.readAsciiString();
                String f1 = data.readAsciiString();
                String d0 = data.readAsciiString();
                String d1 = data.readAsciiString();

                NPCStringCache.put(npcId, npcName);
            }

            // Monster book [Unused for now]
            int monsterBookSize = data.readInt();
            for (int i = 0; i < monsterBookSize; i++) {
                int mobid = data.readInt();

                // Maps where this monster is in
                int mapSize = data.readInt();
                for (int z = 0; z < mapSize; z++) {
                    int mapId = data.readInt();
                }

                // The rewards dropped by this monster
                int rewardSize = data.readInt();
                for (int z = 0; z < rewardSize; z++) {
                    int rewardItemId = data.readInt();
                }

            }

            // Pet
            int petItemSize = data.readInt();
            for (int i = 0; i < petItemSize; i++) {
                int itemId = data.readInt();
                String name = data.readAsciiString();
                String desc = data.readAsciiString();
                String descD = data.readAsciiString();

                PetStringCache.put(itemId, new Tuple(name, desc, descD));
            }

            // Skill
            int skillAndJobSize = data.readInt();
            for (int i = 0; i < skillAndJobSize; i++) {
                int skillOrJobId = data.readInt();
                String bookName = data.readAsciiString();
                String name = data.readAsciiString();
                String desc = data.readAsciiString();

                SkillStringCache.put(skillOrJobId, new Tuple(bookName, name, desc));
            }

            data.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void initialize() {
        // dummy method
    }

    public static Map<Integer, Pair<String, String>> getCashStringCache() {
        return Collections.unmodifiableMap(CashStringCache);
    }

    public static Map<Integer, String> getCashWeatherMsgStringCache() {
        return Collections.unmodifiableMap(CashWeatherMsgCache);
    }

    public static Map<Integer, Pair<String, String>> getAllitemsStringCache() {
        return Collections.unmodifiableMap(AllitemsStringCache);
    }

    public static Map<Integer, Pair<String, String>> getConsumeStringCache() {
        return Collections.unmodifiableMap(ConsumeStringCache);
    }

    public static Map<Integer, Pair<String, String>> getInsStringCache() {
        return Collections.unmodifiableMap(InsStringCache);
    }

    public static Map<Integer, Pair<String, String>> getEtcStringCache() {
        return Collections.unmodifiableMap(EtcStringCache);
    }

    public static Map<Integer, Pair<String, String>> getEquipStringCache() {
        return Collections.unmodifiableMap(EquipStringCache);
    }

    public static Map<Integer, Pair<String, String>> getMapStringCache() {
        return Collections.unmodifiableMap(MapStringCache);
    }

    public static Map<Integer, String> getMobStringCache() {
        return Collections.unmodifiableMap(MobStringCache);
    }

    public static Map<Integer, String> getNPCStringCache() {
        return Collections.unmodifiableMap(NPCStringCache);
    }

    public static Map<Integer, Tuple<String, String, String>> getPetStringCache() {
        return Collections.unmodifiableMap(PetStringCache);
    }

    public static Map<Integer, Tuple<String, String, String>> getSkillStringCache() {
        return Collections.unmodifiableMap(SkillStringCache);
    }
}
