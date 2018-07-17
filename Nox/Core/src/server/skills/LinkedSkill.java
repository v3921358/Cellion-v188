/*
 * Cellion Development
 */
package server.skills;

import static client.SkillFactory.getSkill;
import constants.GameConstants;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.maps.objects.User;
import tools.Utility;

/**
 * Global Linked Skill Handler
 * @author Mazen Massoud
 */
public class LinkedSkill {
    
    /**
     * Linked Skill IDs
     */
    public final static int ZERO = 80000110,
                            LUMINOUS = 80000005,
                            KANNA = 80000004,
                            DEMON_AVENGER = 80000050,
                            DEMON_SLAYER = 80000001,
                            ANGELIC_BUSTER = 80001155,
                            HAYATO = 80000003,
                            CANNONEER = 80000000,
                            XENON = 80000047,
                            PHANTOM = 80000002,
                            BEAST_TAMER = 80010006,
                            MERCEDES = 80001040,
                            ARAN = 80000370,
                            EVAN = 80000369,
                            SHADE = 80000169,
                            KINESIS = 80000188,
                            JETT = 80001151,
                            KAISER = 80000006,
                            MIHILE = 80001140,
                            CYGNUS = 80000055,
                            RESISTANCE = 80000329;
    
    /**
     * OnLinkedSkillRequest
     * @param pPlayer 
     */
    public static void OnLinkedSkillRequest(User pPlayer) {
        
        int dwAccountID = pPlayer.getAccountID();
        int nCygnusCount = 0;
        int nResistanceCount = 0;
        
        try (Connection con = Database.GetConnection()) {
                
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + dwAccountID)) {
                try (ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        int nJobID = rs.getInt(18);
                        int nLevel = rs.getInt(6);
                        boolean bDeleted = (rs.getString(4) != null);
                        
                        if (GameConstants.isCygnusKnight(nJobID) && !bDeleted) {
                            if (nLevel >= 120) nCygnusCount += 2;
                            else nCygnusCount += 1;
                        }
                        if (GameConstants.isResistance(nJobID) && !bDeleted) {
                            nResistanceCount += 1;
                        }
                    }
                }
            } catch (SQLException e) {}
            
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = " + dwAccountID)) {
                try (ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        int nJobID = rs.getInt(18);
                        int nLevel = rs.getInt(6);
                        boolean bDeleted = (rs.getString(4) != null);
                        
                        if (!bDeleted) OnLinkedSkillResult(pPlayer, nJobID, nLevel, nCygnusCount, nResistanceCount);
                    }
                }
            } catch (SQLException e) {}
        } catch (SQLException ex) {}
    }
    
    /**
     * OnLinkedSkillResult
     * @param pPlayer
     * @param nLinkedJobID
     * @param nLinkedLevel
     * @param nCygnusCount
     * @param nResistanceCount 
     */
    public static void OnLinkedSkillResult(User pPlayer, int nLinkedJobID, int nLinkedLevel, int nCygnusCount, int nResistanceCount) {
        int nLinkedSkillID = 0;
        byte nSLV = 0;
        byte nMasterSLV = 0;
        
        if (GameConstants.isCannoneer(nLinkedJobID)) {
            
            nLinkedSkillID = CANNONEER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isMercedes(nLinkedJobID)) {
            
            nLinkedSkillID = MERCEDES;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isDemonSlayer(nLinkedJobID) && nLinkedJobID != 3001) {
            
            nLinkedSkillID = DEMON_SLAYER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isPhantom(nLinkedJobID)) {
            
            nLinkedSkillID = PHANTOM;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isMihile(nLinkedJobID)) {
            
            nLinkedSkillID = MIHILE;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isJett(nLinkedJobID)) {
            
            nLinkedSkillID = JETT;
            nMasterSLV = 1;
            if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isLuminous(nLinkedJobID)) {
            
            nLinkedSkillID = LUMINOUS;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isKaiser(nLinkedJobID)) {
            
            nLinkedSkillID = KAISER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isAngelicBuster(nLinkedJobID)) {
            
            nLinkedSkillID = ANGELIC_BUSTER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isDemonAvenger(nLinkedJobID) && nLinkedJobID != 3001) {
            
            nLinkedSkillID = DEMON_AVENGER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isXenon(nLinkedJobID)) {
            
            nLinkedSkillID = XENON;
            nMasterSLV = 2;
            if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isHayato(nLinkedJobID)) {
            
            nLinkedSkillID = HAYATO;
            nMasterSLV = 2;
            if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isKanna(nLinkedJobID)) {
            
            nLinkedSkillID = KANNA;
            nMasterSLV = 2;
            if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isCygnusKnight(nLinkedJobID)) {
            
            nLinkedSkillID = CYGNUS;
            nMasterSLV = 10;
            nSLV = (byte) nCygnusCount;
        } else if (GameConstants.isBeastTamer(nLinkedJobID)) {
            
            nLinkedSkillID = BEAST_TAMER;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isZero(nLinkedJobID)) {
            
            nLinkedSkillID = ZERO;
            nMasterSLV = 5;
            if (nLinkedLevel >= 178) nSLV = 5;
            else if (nLinkedLevel >= 138) nSLV = 3;
            else if (nLinkedLevel >= 128) nSLV = 2;
            else if (nLinkedLevel >= 118) nSLV = 1;
            else return;
        } /*else if (GameConstants.isShade(nLinkedJobID)) { // Disabled for now due to an issue where the client doesnt detect the ReviveOnce buffstat.
            
            nLinkedSkillID = SHADE;
            nMasterSLV = 2;
            if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        }*/ else if (GameConstants.isResistance(nLinkedJobID)) {
            
            nLinkedSkillID = RESISTANCE;
            nMasterSLV = 8;
            if (nLinkedLevel >= 70) nSLV = (byte) nResistanceCount;
            else return;
        } else if (GameConstants.isKinesis(nLinkedJobID)) {
            
            nLinkedSkillID = KINESIS;
            nMasterSLV = 2;
            if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isAran(nLinkedJobID)) {
            
            nLinkedSkillID = ARAN;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        } else if (GameConstants.isEvan(nLinkedJobID)) {
            
            nLinkedSkillID = EVAN;
            nMasterSLV = 3;
            if (nLinkedLevel >= 210) nSLV = 3;
            else if (nLinkedLevel >= 120) nSLV = 2;
            else if (nLinkedLevel >= 70) nSLV = 1;
            else return;
        }
        
        if (nSLV > nMasterSLV) nSLV = nMasterSLV;
        pPlayer.changeSingleSkillLevel(getSkill(nLinkedSkillID), nSLV, nMasterSLV);
        //OnGiveLinkedSkill(pPlayer.getId(), nLinkedSkillID, nSLV, nMasterSLV);
    }
    
    /**
     * OnGiveLinkedSkill
     * @param dwCharacterID
     * @param nSkillID
     * @param nSLV
     * @param nMasterSLV 
     */
    public static void OnGiveLinkedSkill(int dwCharacterID, int nSkillID, byte nSLV, byte nMasterSLV)  {
        User pPlayer = Utility.requestCharacter(dwCharacterID);
        
        try (Connection con = Database.GetConnection()) {

            Utility.runSQL(con, "DELETE FROM skills WHERE skillid = " + nSkillID + " AND characterid = " + dwCharacterID);
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO skills (skillid, characterid, skilllevel, masterlevel, expiration, victimid) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, nSkillID);
                ps.setInt(2, dwCharacterID);
                ps.setInt(3, nSLV);
                ps.setByte(4, nMasterSLV);
                ps.setLong(5, -1);
                ps.setInt(6, 0);
                ps.execute();
            }
        } catch (SQLException ex) {}
    }
}
