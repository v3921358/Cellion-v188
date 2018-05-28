/*
 * Cellion Development
 */
package server.maps;

import constants.ServerConstants;
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
     * bEnabled
     * @purpose Enables or disables the training map system.
     */
    public static boolean bEnabled = true;
    
    /**
     * bFullMapAggression
     * @purpose All monsters on the map will be aggressive towards attackers, rather than just nearby monsters.
     */
    public static boolean bFullMapAggression = true;
    
    /**
     * bGlobalSpawnIncrease & bGlobalSpawnMultiplier 
     * @purpose Increases the spawn rate globally for all maps that are *not* a part of the aTrainingMaps array below.
     */
    public static boolean bGlobalSpawnIncrease = true;
    public static float bGlobalSpawnMultiplier = 2.25F;
    
    /**
     * aTrainingMaps
     * @syntax {nMapID, nMaximumSpawnCount}
     */
    public static int[][] aTrainingMaps = new int[][]{
        
        /*Nerfed Maps*/
        {682010201, 8}, // Chimney Possessed by the Skeleton
        {682010202, 8}, // Chimney Possessed by the Scarecrow
        {682010203, 8}, // Chimney Possessed by the Clown
        
        {610040000, 10}, // Alien Base Corridor
        {610040000, 10}, // Alien Base Corridor
        {610040100, 10}, // Alien Base Corridor 2
        {610040200, 10}, // Alien Base Corridor 3
        {610040210, 10}, // Alien Base Corridor 4
        {610040220, 10}, // Alien Base Corridor 5
        {610040230, 10}, // Alien Base Corridor 6
        
        /*Buffed Mobs*/
        {272000300, 22}, // Leafre in Flames 3 
        {860000032, 22}, // Dangerous Deep Sea 3 
        {240093100, 22}, // Inside the Stone Colossus 2
        {273040100, 22}, // Forsaken Excavation Site 2
        {211060830, 22}, // Very Tall Castle Walls
        {106031504, 22}, // Galley    
        {120040300, 22}, // Beachgrass Dunes 3 
        {551030000, 22}, // Fantasy Theme Park 3
        {105200900, 22}, // Neglected Garden
    };
    
    /**
     * OnBalanceSpawnCount
     * @param pMap
     * @return 
     */
    public static int OnBalanceSpawnCount(MapleMap pMap) {
        for (int i = 0; i < aTrainingMaps.length; i++) {
            for (int j = 0; j < aTrainingMaps[i].length; j++) {
                if(Integer.valueOf(aTrainingMaps[i][j]).equals(pMap.getId())) {
                    return aTrainingMaps[i][1];
                }
            }
        }
        if (bGlobalSpawnIncrease) {
            return (int) (pMap.getMaximumSpawnCount() * bGlobalSpawnMultiplier);
        }
        return pMap.getMaximumSpawnCount();
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
