/*
 * Cellion Development
 */
package server;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import server.maps.objects.User;
import tools.Utility;

/**
* Boss Attempt Timer System
* @author Mazen Massoud
* 
* @purpose This system handles the boss timers to control how often a player may attempt a specific boss.
*/
public class BossTimer {
    
    /**
     * Boss Attempt Cooldowns (in Hours)
     * These constants control how many hours a player must wait before attempting the specified boss again.
     * 1 Week = 168 Hours
     */
    public static final int HILA_COOLDOWN = 2,              // TODO: Handle, Script, Make Available
            
                            ZAKUM_COOLDOWN = 2,             // Complete
                            HORNTAIL_COOLDOWN = 2,          // Complete
            
                            RANMARU_COOLDOWN = 24,          // TODO: Item Drops
            
                            CRIMSONQUEEN_COOLDOWN = 24,     // Complete // TODO: Update Script
                            PIERRE_COOLDOWN = 24,           // Complete // TODO: Update Script
                            VONBON_COOLDOWN = 24,           // Complete // TODO: Update Script
                            VELLUM_COOLDOWN = 24,           // Complete // TODO: Update Script
            
                            URSUS_COOLDOWN = 24,            // TODO: Handle, Script, Make Available
                            LOTUS_COOLDOWN = 24,            // TODO: Handle, Script, Make Available
                            ARKARIUM_COOLDOWN = 24,         // TODO: Handle, Script, Make Available
                            CYGNUS_COOLDOWN = 24,           // TODO: Handle, Script, Make Available
            
                            MAGNUS_COOLDOWN = 24,           // Complete
            
                            LUCID_COOLDOWN = 168;           // TODO: Handle & Script, Make Available
    
    /**
     * OnSetBossAttempt
     * @purpose Set the next available boss attempt time.
     * 
     * @param pPlayer
     * @param sBossName 
     */
    public static void OnSetBossAttempt(User pPlayer, String sBossName) {
        switch (sBossName) {
            case "HILA":
                pPlayer.tHila = System.currentTimeMillis() + (HILA_COOLDOWN * 60 * 60 * 1000);
            case "ZAKUM":                                                                   
                pPlayer.tZakum = System.currentTimeMillis() + (ZAKUM_COOLDOWN * 60 * 60 * 1000);
                break;
            case "HORNTAIL":
                pPlayer.tHorntail = System.currentTimeMillis() + (HORNTAIL_COOLDOWN * 60 * 60 * 1000);
                break;
            case "RANMARU":                                           
                pPlayer.tRanmaru = System.currentTimeMillis() + (RANMARU_COOLDOWN * 60 * 60 * 1000);        
                break;
            case "CRIMSONQUEEN":                                                           
                pPlayer.tCrimsonQueen = System.currentTimeMillis() + (CRIMSONQUEEN_COOLDOWN * 60 * 60 * 1000); 
                break;
            case "PIERRE":                                                            
                pPlayer.tPierre = System.currentTimeMillis() + (PIERRE_COOLDOWN * 60 * 60 * 1000);          
                break;
            case "VONBON":                                                             
                pPlayer.tVonBon = System.currentTimeMillis() + (VONBON_COOLDOWN * 60 * 60 * 1000);          
                break;
            case "VELLUM":
                pPlayer.tVellum = System.currentTimeMillis() + (VELLUM_COOLDOWN * 60 * 60 * 1000);  
                break;
            case "LOTUS":
                pPlayer.tLotus = System.currentTimeMillis() + (ARKARIUM_COOLDOWN * 60 * 60 * 1000); 
                break;
            case "URSUS":
                pPlayer.tUrsus = System.currentTimeMillis() + (ARKARIUM_COOLDOWN * 60 * 60 * 1000); 
                break;
            case "ARKARIUM":
                pPlayer.tArkarium = System.currentTimeMillis() + (ARKARIUM_COOLDOWN * 60 * 60 * 1000); 
                break;
            case "CYGNUS":
                pPlayer.tCygnus = System.currentTimeMillis() + (CYGNUS_COOLDOWN * 60 * 60 * 1000); 
                break;
            case "MAGNUS":
                pPlayer.tMagnus = System.currentTimeMillis() + (MAGNUS_COOLDOWN * 60 * 60 * 1000);        
                break;
            case "LUCID":
                pPlayer.tLucid = System.currentTimeMillis() + (LUCID_COOLDOWN * 60 * 60 * 1000); 
                break;
            default: 
                pPlayer.dropMessage(5, "[Warning] Unsupported Parameter");
                break;
        }
    }
    
    /**
     * UserBossAttemptRequest
     * @purpose Check if the user is eligible to attempt the specified boss.
     * 
     * @param pPlayer
     * @param sBossName 
     * @return bCanAttempt
     */
    public static boolean UserBossAttemptRequest(User pPlayer, String sBossName) {
        long tRemaining = 0;
        
        switch (sBossName) {
            case "HILA":
                tRemaining = pPlayer.tHila - System.currentTimeMillis();
                break;
            case "ZAKUM":
                tRemaining = pPlayer.tZakum - System.currentTimeMillis();
                break;
            case "HORNTAIL":
                tRemaining = pPlayer.tHorntail - System.currentTimeMillis();
                break;
            case "RANMARU":
                tRemaining = pPlayer.tRanmaru - System.currentTimeMillis();    
                break;
            case "CRIMSONQUEEN":
                tRemaining = pPlayer.tCrimsonQueen - System.currentTimeMillis();
                break;
            case "PIERRE":
                tRemaining = pPlayer.tPierre - System.currentTimeMillis();     
                break;
            case "VONBON":
                tRemaining = pPlayer.tVonBon - System.currentTimeMillis();     
                break;
            case "VELLUM":
                tRemaining = pPlayer.tVellum - System.currentTimeMillis();
                break;
            case "LOTUS":
                tRemaining = pPlayer.tLotus - System.currentTimeMillis();
                break;
            case "URSUS":
                tRemaining = pPlayer.tUrsus - System.currentTimeMillis();
                break;
            case "ARKARIUM":
                tRemaining = pPlayer.tArkarium - System.currentTimeMillis();
                break;
            case "CYGNUS":
                tRemaining = pPlayer.tCygnus - System.currentTimeMillis();
                break;
            case "MAGNUS":
                tRemaining = pPlayer.tMagnus - System.currentTimeMillis();     
                break;
            case "LUCID":
                tRemaining = pPlayer.tLucid - System.currentTimeMillis();     
                break;
            default: 
                pPlayer.dropMessage(5, "[Warning] Unsupported Parameter");
                break;
        }
        
        int tMinutes = (int) (TimeUnit.MILLISECONDS.toMinutes(tRemaining));
        
        if (tMinutes > 1440) {
            if(tRemaining > 0) pPlayer.dropMessage(5, "Sorry, you must wait another " + TimeUnit.MILLISECONDS.toDays(tRemaining) + " day(s) before fighting this boss.");
        } else if (tMinutes > 180) {
            if(tRemaining > 0) pPlayer.dropMessage(5, "Sorry, you must wait another " + TimeUnit.MILLISECONDS.toHours(tRemaining) + " hour(s) before fighting this boss.");
        } else {
            if(tRemaining > 0) pPlayer.dropMessage(5, "Sorry, you must wait another " + tMinutes + " minute(s) before fighting this boss.");
        }
        
        return (tRemaining < 0);
    }
    
    /**
     * UserSaveBossTime
     * @purpose Save all the next available boss attempt times for the player's account.
     * 
     * @param pPlayer
     */
    public static void UserSaveBossTime(User pPlayer) {
        int dwAccID = pPlayer.getClient().getAccID();
        try (Connection con = Database.GetConnection()) {
            Utility.runSQL(con, "DELETE FROM bosstime WHERE dwAccountID = " + dwAccID);
            
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bosstime (dwAccountID, `tHila`, `tZakum`, `tHorntail`, `tRanmaru`, `tCrimsonQueen`, `tPierre`, `tVonBon`, `tVellum`, `tLotus`, `tUrsus`, `tArkarium`, `tCygnus`, `tMagnus`, `tLucid`) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                
                ps.setInt(1, dwAccID);
                ps.setLong(2, pPlayer.tHila);
                ps.setLong(3, pPlayer.tZakum);
                ps.setLong(4, pPlayer.tHorntail);
                ps.setLong(5, pPlayer.tRanmaru);
                ps.setLong(6, pPlayer.tCrimsonQueen);
                ps.setLong(7, pPlayer.tPierre);
                ps.setLong(8, pPlayer.tVonBon);
                ps.setLong(9, pPlayer.tVellum);
                ps.setLong(10, pPlayer.tLotus);
                ps.setLong(11, pPlayer.tUrsus);
                ps.setLong(12, pPlayer.tArkarium);
                ps.setLong(13, pPlayer.tCygnus);
                ps.setLong(14, pPlayer.tMagnus);
                ps.setLong(15, pPlayer.tLucid);
                ps.execute();
                ps.close();
            } catch (SQLException e) {
            }
        } catch (SQLException e) {
        }
    }
    
    /**
     * UserLoadBossTime
     * @purpose Load all the next available boss attempt times for the player's account.
     * 
     * @param pPlayer
     */
    public static void UserLoadBossTime(User pPlayer) {
        int dwAccID = pPlayer.getClient().getAccID();
        try (Connection Connection = Database.GetConnection()) {
            try (PreparedStatement runStatement = Connection.prepareStatement("SELECT * from bosstime WHERE dwAccountID = ?")) {
                runStatement.setInt(1, dwAccID);
                ResultSet Result = runStatement.executeQuery();
                while (Result.next()) {
                    pPlayer.tHila = Result.getLong("tHila");
                    pPlayer.tZakum = Result.getLong("tZakum");
                    pPlayer.tHorntail = Result.getLong("tHorntail");
                    pPlayer.tRanmaru = Result.getLong("tRanmaru");
                    pPlayer.tCrimsonQueen = Result.getLong("tCrimsonQueen");
                    pPlayer.tPierre = Result.getLong("tPierre");
                    pPlayer.tVonBon = Result.getLong("tVonBon");
                    pPlayer.tVellum = Result.getLong("tVellum");
                    pPlayer.tLotus = Result.getLong("tLotus");
                    pPlayer.tUrsus = Result.getLong("tArkarium");
                    pPlayer.tArkarium = Result.getLong("tArkarium");
                    pPlayer.tCygnus = Result.getLong("tCygnus");
                    pPlayer.tMagnus = Result.getLong("tMagnus");
                    pPlayer.tLucid = Result.getLong("tLucid");
                }
                Result.close();
            } catch (SQLException e) {
            }
        } catch (SQLException e) {
        }
    }
}
