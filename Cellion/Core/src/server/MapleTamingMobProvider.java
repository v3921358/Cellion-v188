package server;

import java.util.HashMap;
import java.util.Map;
import provider.wz.cache.WzDataStorage;
import provider.wz.nox.NoxBinaryReader;

/**
 *
 * @author Lloyd Korn
 */
public class MapleTamingMobProvider {

    private static final Map<Integer, MapleTamingMobStats> TAMINGMOB_CACHE = new HashMap<>();

    public static void initialize() {
        loadTamingMob();
    }

    private static void loadTamingMob() {
        try {
            NoxBinaryReader data = WzDataStorage.getBinaryTamingMobData();

            int size = data.readInt();
            for (int i = 0; i < size; i++) {
                int tamingMobId = data.readInt();
                int speed = data.readInt();
                int jump = data.readInt();
                float fs = data.readFloat();
                float swim = data.readFloat();
                int fatigue = data.readInt();
                boolean userSpeed = data.readBoolean();
                boolean userJump = data.readBoolean();
                boolean continentMove = data.readBoolean();

                MapleTamingMobStats stats = new MapleTamingMobStats(tamingMobId);
                stats.setSpeed(speed);
                stats.setJump(jump);
                stats.setFs(fs);
                stats.setSwim(swim);
                stats.setFatigue(fatigue);
                stats.setUserJump(userJump);
                stats.setUserSpeed(userSpeed);
                stats.setContinentMove(continentMove);

                TAMINGMOB_CACHE.put(tamingMobId, stats);
            }

            System.out.println(String.format("[Info] Loaded %d Taming Mobs.", TAMINGMOB_CACHE.size()));
            data.close();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static MapleTamingMobStats getTamingMobStats(int id) {
        if (TAMINGMOB_CACHE.containsKey(id)) {
            return TAMINGMOB_CACHE.get(id);
        }
        return null;
    }

}
