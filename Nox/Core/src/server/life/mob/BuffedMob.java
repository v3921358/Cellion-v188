/*
 * Cellion Development
 */
package server.life.mob;

import constants.ServerConstants;
import server.life.Mob;

/**
 * Buffed Monsters
 * @author Mazen Massoud
 * 
 * @purpose Handles monster strength increases in the buffed channels.
 */
public class BuffedMob {
    
    /**
     * The channel range for Buffed Channels.
     */
    static int BUFFED_CHANNEL_START = 4,
               BUFFED_CHANNEL_END = 10;
    
    /**
     * Allows boss monsters to be Buffed in addition to normal monster.
     */
    public static boolean BUFFED_BOSSES = false;
    
    /**
     * Buffed Mob Stat Multipliers.
     */
    public static double LEVEL_BUFF = 1.75,
                         HP_BUFF = 10,
                         DEFENCE_BUFF = 2,
                         SPEED_BUFF = 2,
                         EXP_BUFF = 2,
                         MESO_BUFF = 2,
                         NX_BUFF = 2;
    
    /**
     * OnBuffedChannel
     * @param nChannel
     * @return Checks if the specified channel is within the Buffed Channel range.
     */
    public static boolean OnBuffedChannel(int nChannel) {
        if (!ServerConstants.BUFFED_CHANNELS) return false;
        return (nChannel >= BUFFED_CHANNEL_START && nChannel <= BUFFED_CHANNEL_END);
    }
    
    /**
     * OnBuffedMobRequest
     * Check if channel is within the Buffed Channel range.
     * 
     * @param pMob
     * @param nChannel
     * @return 
     */
    public static boolean OnBuffedMobRequest(Mob pMob, int nChannel) {
        if (!ServerConstants.BUFFED_CHANNELS) return false;
        if (!BUFFED_BOSSES) return (nChannel >= BUFFED_CHANNEL_START && nChannel <= BUFFED_CHANNEL_END) && !pMob.getStats().isBoss();
        return (nChannel >= BUFFED_CHANNEL_START && nChannel <= BUFFED_CHANNEL_END);
    }
    
    /**
     * OnBuffedMobResult
     * Modifies the mob's stats in order to make them more challenging and rewarding.
     * 
     * @param pMob 
     * @return pMob
     */
    public static Mob OnBuffedMobResult(Mob pMob) {
        
        short nLV = (short) (pMob.getStats().getLevel() * LEVEL_BUFF);
        long nHP = (long) (pMob.getStats().getHp() * HP_BUFF); 
        long nMaxHP = (long) (pMob.getStats().getFinalMaxHp() * HP_BUFF);
        int nPhysDefence = (int) (pMob.getStats().getPhysicalDefense() * DEFENCE_BUFF);
        int nMagicDefence = (int) (pMob.getStats().getMagicDefense() * DEFENCE_BUFF);
        int nSpeed = (int) (pMob.getStats().getSpeed() * SPEED_BUFF);
        
        pMob.changeLevel(nLV);
        pMob.setHp(nHP);
        pMob.getStats().setFinalMaxHp(nMaxHP);
        pMob.getStats().setPhysicalDefense(nPhysDefence);
        pMob.getStats().setMagicDefense(nMagicDefence);
        pMob.getStats().setSpeed(nSpeed);
        return pMob;
    }
}
