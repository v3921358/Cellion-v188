package server.commands;

import client.ClientSocket;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.World;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.StringUtil;
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
            c.getPlayer().yellowMessage("@dispose : Enables your character's actions when stuck.");
            c.getPlayer().yellowMessage("@event : Quick travel to the current event, if available.");
            c.getPlayer().yellowMessage("@online : Display the current online players.");
            c.getPlayer().yellowMessage("@fm : Quick travel to the Free Market.");
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class FM extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (c.getPlayer().getMapId() == ServerConstants.UNIVERSAL_START_MAP || c.getPlayer().getMapId() == ServerConstants.JAIL_MAP || c.getPlayer().getMapId() == 910000000) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }

            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(map, map.getPortal(0));
            return 1;
        }
    }

    public static class Support extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, "[" + ServerConstants.SERVER_NAME + " Support] " + c.getPlayer().getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
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
            return 1;
        }
    }
}
