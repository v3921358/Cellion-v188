/*
 * Cellion Development
 */
package server.maps;

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
}
