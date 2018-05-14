package server.commands;

import client.ClientSocket;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import database.Database;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import server.maps.objects.User;
import tools.LogHelper;
import tools.StringUtil;
import tools.Utility;
import tools.packet.WvsContext;

/**
 * Player Commands
 * @author Mazen Massoud
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- PLAYER COMMANDS -----------------");
            c.getPlayer().yellowMessage("@support <message> : Send a message to availible staff members.");
            c.getPlayer().yellowMessage("@wallet : Displays account currency information.");
            c.getPlayer().yellowMessage("@dispose : Enables your character's actions when stuck.");
            c.getPlayer().yellowMessage("@event : Quick travel to the current event, if available.");
            c.getPlayer().yellowMessage("@fm : Quick travel to the Free Market.");
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class Wallet extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- ACCOUNT WALLET -----------------");
            c.getPlayer().yellowMessage("NX Cash : " + c.getPlayer().getNX());
            c.getPlayer().yellowMessage("Donor Points : " + c.getPlayer().getDPoints());
            c.getPlayer().yellowMessage("Vote Points : " + c.getPlayer().getVPoints());
            c.getPlayer().yellowMessage("Event Points : " + c.getPlayer().getEPoints());
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class FM extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pPlayer = c.getPlayer();
            for (int i : GameConstants.blockedMaps) {
                if (pPlayer.getMapId() == i) {
                    pPlayer.dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (pPlayer.getMapId() == ServerConstants.UNIVERSAL_START_MAP || pPlayer.getMapId() == ServerConstants.JAIL_MAP || pPlayer.getMapId() == 910000000) {
                pPlayer.dropMessage(5, "You may not use this command here.");
                return 0;
            }

            pPlayer.saveLocation(SavedLocationType.FREE_MARKET, pPlayer.getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            pPlayer.changeMap(map, map.getPortal(0));
            return 1;
        }
    }

    public static class Support extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, "[Player Support] " + c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1)));
            c.getPlayer().dropMessage(5, "Your message has been sent successfully.");
            return 1;
        }
    }
    
    public static class Event extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (c.getPlayer().getClient().getChannelServer().eventOn) {
                c.getPlayer().changeMap(c.getPlayer().getClient().getChannelServer().eventMap, 0);
                c.getPlayer().dropMessage(5, "Welcome to the " + ServerConstants.SERVER_NAME + " event, have fun!");
            } else {
                c.getPlayer().dropMessage(5, "Sorry, there is currently no event being hosted.");
            }
            return 1;
        }
    }

    public static class EA extends Dispose {
    }
    
    public static class Dispose extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(5, "Your characters actions have been enabled.");
            c.getPlayer().completeDispose();
            
            c.getPlayer().dropMessage(5, "tServer  : " + System.currentTimeMillis());
            c.getPlayer().dropMessage(5, "tHorntail : " + c.getPlayer().tHorntail);
            return 1;
        }
    }
    
    public static class Job extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().OnJobAdvanceRequest();
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    /*
    *   Point Claim Method from Database
    *   @purpose Allow players to claim points from the database without needing to log out.
    *
    *   @author Mazen
    *   @author Poppy
     */
    public static class Vote extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (ServerConstants.USE_API) {
                // Voting will then be handled straight through the API.
                return 0;
            }
            
            int nAmount = 0;
            boolean bSuccess = false;

            try (Connection con = Database.GetConnection()) {
                
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cms_votes WHERE accountid = " + c.getPlayer().getAccountID() + " AND collected = 0")) {

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            nAmount += rs.getInt(5);
                            PreparedStatement ps_2 = (PreparedStatement) con.prepareStatement("UPDATE cms_votes SET collected = 1 WHERE id = " + rs.getInt(1));
                            ps_2.executeUpdate();
                            ps_2.close();

                            bSuccess = true;
                        }
                        c.getPlayer().setVPoints(c.getPlayer().getVPoints() + nAmount);
                    }

                    if (bSuccess) {
                        c.getPlayer().dropMessage(6, "You have claimed " + nAmount + " vote points, and now have a total of " + c.getPlayer().getVPoints() + ".");
                    } else {
                        c.getPlayer().dropMessage(5, "Sorry, looks like you don't have any unclaimed vote points.");
                    }
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[Vote Claim] Error retrieving last character creation time\n", e);
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(PlayerCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 1;
        }
    }
}
