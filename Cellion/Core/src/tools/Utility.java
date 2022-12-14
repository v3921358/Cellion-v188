/*
 * Cellion Development - Utility Tools
 */
package tools;

import client.CharacterTemporaryStat;
import client.QuestStatus;
import enums.InventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import server.MapleInventoryManipulator;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.maps.objects.Summon;
import service.ChannelServer;
import tools.packet.CField;
import tools.packet.WvsContext;

/**
 * Utility Tools/Functions
 * @author Mazen Massoud
 */
public class Utility {

    /**
     * Run SQL Prepared Statement
     * 
     * @param Con
     * @param sSQL 
     * @throws java.sql.SQLException 
     */
    public static void runSQL(Connection Con, String sSQL) throws SQLException {
        try (PreparedStatement pStatement = Con.prepareStatement(sSQL)) {
            pStatement.executeUpdate();
            pStatement.closeOnCompletion();
        }
    }
    
    /**
     * Character Retriever
     *
     * @param dwCharacterID / sCharacterName
     * @return MapleCharacter Object regardless of channel.
     */
    public static User requestCharacter(int dwCharacterID) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            User pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterById(dwCharacterID);
            if (pPlayer != null) {
                return pPlayer;
            }
        }
        return null;
    }

    public static User requestCharacter(String sCharacterName) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            User pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterByName(sCharacterName);
            if (pPlayer != null) {
                return pPlayer;
            }
        }
        return null;
    }

    /**
     * Character Channel Locator
     *
     * @param dwCharacterID / sCharacterName
     * @return Channel of the requested Character.
     */
    public static int requestChannel(int dwCharacterID) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            User pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterById(dwCharacterID);
            if (pPlayer != null) {
                return pPlayer.getClient().getChannel();
            }
        }
        return 0;
    }

    public static int requestChannel(String sCharacterName) {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            User pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterByName(sCharacterName);
            if (pPlayer != null) {
                return pPlayer.getClient().getChannel();
            }
        }
        return 0;
    }

    /**
     * Success Chance Generator
     *
     * @param nChanceToSucceed
     * @return True or false value on whether the chance succeeded.
     */
    public static boolean resultSuccess(double nChanceToSucceed) {
        Random pRandom = new Random();
        return pRandom.nextInt(100) < nChanceToSucceed;
    }

    /**
     * Random Selection from Array
     * 
     * @param nArrayLength
     * @return Random selection within the specified array length.
     */
    public static int getRandomSelection(int nArrayLength) {
        return new Random().nextInt(Math.abs(nArrayLength));
    }
    
    /**
     * appendArrays
     * @param aFirstArray
     * @param aSecondArray
     * @return Combine two arrays together
     */
    public static int[] appendArrays(int[] aFirstArray, int[] aSecondArray) {
        int[] aResult = Arrays.copyOf(aFirstArray, aFirstArray.length + aSecondArray.length);
        System.arraycopy(aSecondArray, 0, aResult, aFirstArray.length, aSecondArray.length);
        return aResult;
    }
    
    public static <T> T[] appendArrays(T[] aFirstArray, T[] aSecondArray) {
        T[] aResult = Arrays.copyOf(aFirstArray, aFirstArray.length + aSecondArray.length);
        System.arraycopy(aSecondArray, 0, aResult, aFirstArray.length, aSecondArray.length);
        return aResult;
    }
    
    public static int[][] appendArrays(int[][] aFirstArray, int[][] aSecondArray) {
        int[][] aResult = new int[aFirstArray.length + aSecondArray.length][];
        System.arraycopy(aFirstArray, 0, aResult, 0, aFirstArray.length);
        System.arraycopy(aSecondArray, 0, aResult, aFirstArray.length, aSecondArray.length);
        return aResult;
    }
    
    /**
     * Check if Number
     *
     * @param sString
     * @return True or false on whether the string value is a number.
     */
    public static boolean isNumber(String sString) {
        return sString.matches("-?\\d+(\\.\\d+)?");
    }
    
    /**
     * Get Last Digit of a Number
     * @param nNumber
     * @return 
     */
    public static int getLastDigit(int nNumber) { 
        return nNumber % 10; 
    }
    
    /**
     * removeAllSummonsForCharacter
     * @author Mazen Massoud
     * @purpose Removes all summons belonging to a player from the map, useful for multiple summons such as Kishin.
     * @param dwCharacterID 
     */
    public static void removeAllSummonsForCharacter(int dwCharacterID) {
        User pPlayer = requestCharacter(dwCharacterID);
        if (pPlayer == null) return;
        
        List<MapleMapObject> pAllSummons = pPlayer.getMap().getAllMapObjects(MapleMapObjectType.SUMMON);
        for (MapleMapObject pObject : pAllSummons) {
            Summon pSummon = (Summon) pObject;
            if (pSummon.getOwnerId() == pPlayer.getId()) {
                pPlayer.getMap().broadcastPacket(CField.SummonPacket.removeSummon(pSummon, true));
                pPlayer.getMap().removeMapObject(pSummon);
                pPlayer.removeVisibleMapObject(pSummon);
                pPlayer.removeSummon(pSummon);
            }
        }
    }
    
    /**
     * Remove Buff from Player's Map
     * @author Mazen Massoud
     * @param pPlayer The function checks all users that are on the same map as this character object.
     * @param pStat The temporary stat that will be removed from this map.
     */
    public static void removeBuffFromMap(User pPlayer, CharacterTemporaryStat pStat) {
        List<User> pMapCharacters = pPlayer.getMap().getCharacters();

        for (User pUser : pMapCharacters) {
            if (pUser.hasBuff(pStat)) {
                pUser.removeCooldown(pUser.getBuffSource(pStat)); // Refund the cooldown of the player's buff.
                pUser.cancelEffectFromTemporaryStat(pStat); // Remove the buff from the player.
            }
        }
    }

    /**
     * Auto Pet Loot
     * @author Mazen Massoud
     * @param pPlayer Character object of which the pet loot is requested from.
     */
    public static void petLootRequest(User pPlayer) {
        ReentrantLock petSafety = new ReentrantLock();
        petSafety.lock();
        
        boolean bHasPet = false;
        for (int i = 0; i < 3; i++) {
            Pet pet = pPlayer.getPet(i);
            if (pet != null) {
                bHasPet = true; // Checks all pet slots to confirm if player has a pet active.
            }
        }
        
        if (!bHasPet) return;
        MapleMapItem pMapLoot;
        final List<MapleMapObject> mItemsInRange = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPet(0).getPos(), 9500, Arrays.asList(MapleMapObjectType.ITEM));
        
        List<MapleMapObject> mItemsToLoot = new ArrayList<>();
        for (MapleMapObject pObject : mItemsInRange) {
            MapleMapItem pLoot = (MapleMapItem) pObject;
            if (pLoot.getOwner() == pPlayer.getId() && !pLoot.isPlayerDrop()) {
                mItemsToLoot.add(pLoot);
            }
        }
        
        List<MapleMapObject> mItems = new ArrayList<>();
        if (mItemsToLoot.size() <= 4) {
            mItems = mItemsToLoot;
        } else {
            for (int i = 1; i <= 4; i++) {
                mItems.add(mItemsToLoot.get(i));
            }
        }

        if (bHasPet) {
            for (MapleMapObject item : mItems) {
                pMapLoot = (MapleMapItem) item;

                if (pMapLoot.getMeso() > 0) { // Meso Drops
                    pPlayer.gainMeso(pMapLoot.getMeso(), true);
                    pMapLoot.setPickedUp(true);
                    pPlayer.getMap().removeMapObject(item);
                    pPlayer.getMap().broadcastPacket(CField.removeItemFromMap(pMapLoot.getObjectId(), 5, pPlayer.getId()), pMapLoot.getPosition());
                } else { // Item Drops
                    if (pMapLoot.isPickedUp()) {
                        pPlayer.getClient().SendPacket(WvsContext.enableActions());
                        continue;
                    }
                    if (pMapLoot.getQuest() > 0 && pPlayer.getQuestStatus(pMapLoot.getQuest()) != QuestStatus.QuestState.Started) {
                        pPlayer.getClient().SendPacket(WvsContext.enableActions());
                        continue;
                    }
                    if (pMapLoot.getOwner() != pPlayer.getId() && ((!pMapLoot.isPlayerDrop() && pMapLoot.getDropType() == 0)
                            || (pMapLoot.isPlayerDrop() && pPlayer.getMap().getSharedMapResources().everlast))) {
                        pPlayer.getClient().SendPacket(WvsContext.enableActions());
                        continue;
                    }
                    if (!pMapLoot.isPlayerDrop() && pMapLoot.getDropType() == 1 && pMapLoot.getOwner() != pPlayer.getId() && (pPlayer.getParty() == null || pPlayer.getParty().getMemberById(pMapLoot.getOwner()) == null)) {
                        pPlayer.getClient().SendPacket(WvsContext.enableActions());
                        continue;
                    }

                    /*if (GameConstants.getInventoryType(pMapLoot.getItemId()) == MapleInventoryType.EQUIP
                            && pMapLoot.getItemId() != 0) {
                        continue;
                    }*/
                    
                    /*if (!pPlayer.haveItem((pMapLoot.getItemId()))) { // Only pick up items the player already has.
                        continue;
                    }*/

                    if (pMapLoot.getItem() == null || !MapleInventoryManipulator.addFromDrop(pPlayer.getClient(), pMapLoot.getItem(), true)) {
                        continue;
                    }
                }

                pMapLoot.setPickedUp(true);
                pPlayer.getMap().broadcastPacket(CField.removeItemFromMap(pMapLoot.getObjectId(), 5, pPlayer.getId()), pMapLoot.getPosition());
                pPlayer.getMap().removeMapObject(item);
            }
            try {
                if (ServerConstants.DEVELOPER_DEBUG_MODE && !ServerConstants.REDUCED_DEBUG_SPAM) {
                    System.out.println("[Debug] Pet Loot Size (" + mItems.size() + ")");
                }
            } finally {
                petSafety.unlock();
            }
        }
    }

    /**
     * Auto Pet Vacuum
     * @author Mazen Massoud
     * @param pPlayer Character object requesting to vacuum all of it's respected item loot on the map.
     */
    public static void petVacuumRequest(User pPlayer) {
        ReentrantLock petSafety = new ReentrantLock();
        petSafety.lock();
        
        try {
            MapleMapItem pMapLoot;
            final List<MapleMapObject> mAllMapItems = pPlayer.getMap().getAllMapObjects(MapleMapObjectType.ITEM);

            boolean bHasPet = false;
            for (int i = 0; i <= 3; i++) {
                Pet pet = pPlayer.getPet(i);
                if (pet != null) {
                    bHasPet = true; // Checks all pet slots to confirm if player has a pet active.
                }
            }

            List<MapleMapObject> mItemsToLoot = new ArrayList<>();
            for (MapleMapObject pObject : mAllMapItems) {
                MapleMapItem pLoot = (MapleMapItem) pObject;
                if (pLoot.getOwner() == pPlayer.getId() && !pLoot.isPlayerDrop()) {
                    mItemsToLoot.add(pLoot);
                }
            }

            List<MapleMapObject> mItems = new ArrayList<>();
            if (mAllMapItems.size() <= 10) {
                mItems = mItemsToLoot;
            } else {
                for (int i = 1; i <= 10; i++) {
                    mItems.add(mItemsToLoot.get(i));
                }
            }

            if (bHasPet) {
                for (MapleMapObject item : mItems) {
                    pMapLoot = (MapleMapItem) item;

                    if (pMapLoot.getMeso() > 0) { // Meso Drops
                        pPlayer.gainMeso(pMapLoot.getMeso(), true);
                        pMapLoot.setPickedUp(true);
                        pPlayer.getMap().removeMapObject(item);
                        pPlayer.getMap().broadcastPacket(CField.removeItemFromMap(pMapLoot.getObjectId(), 5, pPlayer.getId()), pMapLoot.getPosition());
                    } else { // Item Drops
                        if (pMapLoot.isPickedUp()) {
                            pPlayer.getClient().SendPacket(WvsContext.enableActions());
                            continue;
                        }
                        if (pMapLoot.getQuest() > 0 && pPlayer.getQuestStatus(pMapLoot.getQuest()) != QuestStatus.QuestState.Started) {
                            pPlayer.getClient().SendPacket(WvsContext.enableActions());
                            continue;
                        }
                        if (pMapLoot.getOwner() != pPlayer.getId() && ((!pMapLoot.isPlayerDrop() && pMapLoot.getDropType() == 0)
                                || (pMapLoot.isPlayerDrop() && pPlayer.getMap().getSharedMapResources().everlast))) {
                            pPlayer.getClient().SendPacket(WvsContext.enableActions());
                            continue;
                        }
                        if (!pMapLoot.isPlayerDrop() && pMapLoot.getDropType() == 1 && pMapLoot.getOwner() != pPlayer.getId() && (pPlayer.getParty() == null || pPlayer.getParty().getMemberById(pMapLoot.getOwner()) == null)) {
                            pPlayer.getClient().SendPacket(WvsContext.enableActions());
                            continue;
                        }
                        /*if (GameConstants.getInventoryType(pMapLoot.getItemId()) == MapleInventoryType.EQUIP
                                && pMapLoot.getItemId() != 0) {
                            continue;
                        }*/
                        /*if (!pPlayer.haveItem((pMapLoot.getItemId()))) { // Only pick up items the player already has.
                            continue;
                        }*/

                        if (pMapLoot.getItem() == null || !MapleInventoryManipulator.addFromDrop(pPlayer.getClient(), pMapLoot.getItem(), true)) {
                            continue;
                        }
                    }

                    pMapLoot.setPickedUp(true);
                    pPlayer.getMap().broadcastPacket(CField.removeItemFromMap(pMapLoot.getObjectId(), 5, pPlayer.getId()), pMapLoot.getPosition());
                    pPlayer.getMap().removeMapObject(item);
                }
            }
        } finally {
            petSafety.unlock();
        }
    }
}
