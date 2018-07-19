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
import tools.packet.MobPacket;

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
        {682010201, 13}, // Chimney Possessed by the Skeleton
        {682010202, 13}, // Chimney Possessed by the Scarecrow
        {682010203, 13}, // Chimney Possessed by the Clown
        {610040000, 15}, // Alien Base Corridor
        {610040000, 15}, // Alien Base Corridor
        {610040100, 15}, // Alien Base Corridor 2
        {610040200, 15}, // Alien Base Corridor 3
        {610040210, 15}, // Alien Base Corridor 4
        {610040220, 15}, // Alien Base Corridor 5
        {610040230, 15}, // Alien Base Corridor 6
        
        /*Primary Monster Maps*/
        {106030200, 45}, // Mushroom Castle: Castle Corridor 1  
	{106030201, 45}, // Mushroom Castle: Castle Corridor 2 
	{106030700, 45}, // Viking Airship: Galley 
	{211060830, 45}, // Lion King’s Castle: Very Tall Castle Walls 
	{401020000, 45}, // Forbidden Forest: Twisted Forest Border
	{401052102, 45}, // Treglow’s Laboratory: Laboratory B2 Area 3
	{401050001, 45}, // Tyrant’s Castle: Tyrant’s Castle Foyer
	{272000300, 45}, // Leafre of Past: Leafre in Flames 3 
	{272000400, 45}, // Leafre of Past: Leafre in Flames 4
			 // TODO: Find map to fit here in progression.
	{860000032, 45}, // Dangerous Deep Sea 3
	{240093100, 45}, // Inside the Stone Colossus 2
	{541020400, 45}, // Along Ulu City 
			 // TODO: Find map to fit here in progression. 
	{273020200, 45}, // Lonely Rocky Road
	{273040100, 45}, // Forsaken Excavation Site 2
	{272020000, 45}, // Distorted Temple of Time 1
	{272020100, 45}, // Distorted Temple of Time 2 
	{273060300, 45}  // Warrior Grounds
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
        if (pPlayer.getLastAggressionTime() + 8000 > System.currentTimeMillis()) {
            return;
        }
        
        ReentrantLock pLock = new ReentrantLock(true);
        final List<MapleMapObject> aMobsToAggro = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 300000.0D, Arrays.asList(MapleMapObjectType.MONSTER));
        pLock.lock();
        try {
            for (MapleMapObject pObject : aMobsToAggro) {
                Mob pMob = (Mob) pObject;
                if (pMob.getController() != null) {
                    if (pPlayer.getMap().getCharacterById(pMob.getController().getId()) == null) {
                        pMob.switchController(pPlayer, true);
                        pPlayer.getClient().SendPacket(MobPacket.OnMobChangeController(pMob, true));
                        pPlayer.setLastAggressionTime(System.currentTimeMillis());
                    }
                } else  {
                    pMob.switchController(pPlayer, true);
                    pPlayer.getClient().SendPacket(MobPacket.OnMobChangeController(pMob, true));
                    pPlayer.setLastAggressionTime(System.currentTimeMillis());
                }
            }
        } finally {
            pLock.unlock();
        }
    }
}
