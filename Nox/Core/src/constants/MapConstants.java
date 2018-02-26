package constants;

import java.util.ArrayList;
import java.util.List;
import server.maps.SharedMapResources;

/**
 *
 * @author Lloyd Korn
 */
public class MapConstants {

    public static final int SPINEL_FOREST = 950000100;

    public static List<Integer> getVienOrHerbsSpawnFromMap(SharedMapResources smr, int spawnSize, int id, int lvlmin, int lvlmax) {
        final List<Integer> array = new ArrayList();
        if (lvlmin < 30
                || id / 1000 == 610030
                || // Crimson wood keep PQ
                id / 100000000 == 9
                || // Hidden street
                id / 100 == 2110419
                || // The Passage
                id / 1000 == 211042
                || // Cave of Trial
                id / 100000 == 1050 // Sleepywood
                ) {
            return array;
        }
//	 System.out.println("Size : "+spawnSize+" Min : " + lvlmin + ", max " + lvlmax);
        if (spawnSize < 8) {
            if (id / 1000 != 211060) { // LHC castle
                return array;
            }
            smr.maxHerbSpawn = 5;
        } else {
            smr.maxHerbSpawn = spawnSize < 14 ? 3 : spawnSize < 18 ? 4 : spawnSize < 30 ? 5 : 6;
        }
        if (lvlmin < 100) {
            for (int i = lvlmin % 10; i <= lvlmax % 10; i++) {
                array.add(ReactorConstants.Herbs.get(i));
                array.add(ReactorConstants.Viens.get(i));
            }
        } else {
            array.add(ReactorConstants.Emerald_Vein);
            array.add(ReactorConstants.Gold_Vein);
            array.add(ReactorConstants.Aquamarine_Vein);
            array.add(ReactorConstants.Red_Vein);
            array.add(ReactorConstants.Black_Vein);
            array.add(ReactorConstants.Purple_Vein);

            array.add(ReactorConstants.Emerald_Herb);
            array.add(ReactorConstants.Gold_Herb);
            array.add(ReactorConstants.Aquamarine_Herb);
            array.add(ReactorConstants.Red_Herb);
            array.add(ReactorConstants.Black_Herb);
            array.add(ReactorConstants.Purple_Herb);
        }
        return array;
    }
}
