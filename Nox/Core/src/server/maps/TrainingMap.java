/*
 * Cellion Development
 */
package server.maps;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import server.life.Mob;
import server.maps.objects.User;

/**
 * Training Map Handler
 * @author Mazen Massoud
 * @purpose Modifies the training maps used in the server, controls the spawn rates and more.
 */
public class TrainingMap {
    
    /**
     * aTrainingMaps
     * @syntax {nMapID, nSpawnRateMultiplier}
     */
    public static float[][] aTrainingMaps = new float[][]{
        {272000300, 5}, // Leafre in Flames 3 
        {860000032, 5}, // Dangerous Deep Sea 3 
        {240093100, 5}, // Inside the Stone Colossus 2
        {273040100, 5}, // Forsaken Excavation Site 2
        {211060830, 5}, // Very Tall Castle Walls
        {106031504, 5}, // Galley    
        {120040300, 5}, // Beachgrass Dunes 3 
        {551030000, 5}, // Fantasy Theme Park 3
        {105200900, 5}, // Neglected Garden
    };
    
    /**
     * bFullMapAggression
     * @purpose All monsters on the map will be aggressive towards attackers, rather than just nearby monsters.
     */
    public static boolean bFullMapAggression = true;
    
    /**
     * OnBalanceSpawnRate
     * @param nMapID
     * @return 
     */
    public static float OnBalanceSpawnRate(int nMapID) {
        float nMultiplier = 1;
        for (int i = 0; i < aTrainingMaps.length; i++) {
            for (int j = 0; j < aTrainingMaps[i].length; j++) {
                if(Float.valueOf(aTrainingMaps[i][j]).equals(nMapID)) nMultiplier = aTrainingMaps[i][1];
            }
        }
        return nMultiplier;
    }
    
    /**
     * OnAggrovateNearby
     * @purpose Aggro nearby monsters surrounding the player.
     * @param pPlayer
     */
    public static void OnMonsterAggressionRequest(User pPlayer) {
        ReentrantLock pLock = new ReentrantLock(true);
        pLock.lock();
        try {
            final List<MapleMapObject> aMobsToAggro = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 6000.0D, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject pObject : aMobsToAggro) {
                Mob pMob = (Mob) pObject;
                if (pMob != null && pPlayer.getTruePosition().distanceSq(pMob.getTruePosition()) < 6000.0D && pMob.getLinkCID() <= 0) {
                    if (pMob.getController() != null) {
                        if (pPlayer.getMap().getCharacterById(pMob.getController().getId()) == null) {
                            pMob.switchController(pPlayer, true);
                            pPlayer.controlMonster(pMob, true);
                        } else {
                            pMob.switchController(pMob.getController(), true);
                        }
                    } else {
                        pMob.switchController(pPlayer, true);
                    }
                }
            }
        } finally {
            pLock.unlock();
        }
    }
}
