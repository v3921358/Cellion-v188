/*
 * Rexion Development - Utility Tools
 */
package tools;

import client.CharacterTemporaryStat;
import java.util.List;
import java.util.Random;
import server.maps.objects.MapleCharacter;
import service.ChannelServer;

/**
 *
 * @author Mazen Massoud
 */
public class Utility { 
    
    /**
     * Character Retriever
     * @param dwCharacterID / sCharacterName
     * @return MapleCharacter Object regardless of channel.
     */
    public static MapleCharacter requestCharacter(int dwCharacterID) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            MapleCharacter pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterById(dwCharacterID);
            if (pPlayer != null) {
                return pPlayer;
            }
        }
        return null;
    }
    
    public static MapleCharacter requestCharacter(String sCharacterName) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            MapleCharacter pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterByName(sCharacterName);
            if (pPlayer != null) {
                return pPlayer;
            }
        }
        return null;
    }
    
    /**
     * Character Channel Locator
     * @param dwCharacterID / sCharacterName
     * @return Channel of the requested Character.
     */
    public static int requestChannel(int dwCharacterID) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            MapleCharacter pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterById(dwCharacterID);
            if (pPlayer != null) {
                return pPlayer.getClient().getChannel();
            }
        }
        return 0;
    }
    
    public static int requestChannel(String sCharacterName) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            MapleCharacter pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterByName(sCharacterName);
            if (pPlayer != null) {
                return pPlayer.getClient().getChannel();
            }
        }
        return 0;
    }
    
    /**
     * Success Chance Generator
     * @param nChanceToSucceed
     * @return True or false value on whether the chance succeeded.
     */
    public static boolean resultSuccess(double nChanceToSucceed) {
        Random pRandom = new Random();
        return pRandom.nextInt(100) < nChanceToSucceed;
    }
    
    /**
     * Remove Buff from Player's Map
     * @param pPlayer The function checks all users that are on the same map as this character object.
     * @param pStat The temporary stat that will be removed from this map.
     */
    public static void removeBuffFromMap(MapleCharacter pPlayer, CharacterTemporaryStat pStat) {
        List<MapleCharacter> pMapCharacters = pPlayer.getMap().getCharacters();
        
        for (MapleCharacter pUser : pMapCharacters) {
            if (pUser.hasBuff(pStat)) {
                pUser.removeCooldown(pUser.getBuffSource(pStat)); // Refund the cooldown of the player's buff.
                pUser.cancelEffectFromTemporaryStat(pStat); // Remove the buff from the player.
            }
        }
    }
}
