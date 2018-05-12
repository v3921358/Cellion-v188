package server.commands;

import client.ClientSocket;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;

/**
 * Donor Commands
 * @author Mazen Massoud
 */
public class DonatorCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.DONOR;
    }

    public static class DonorHelp extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- DONOR COMMANDS -----------------");
            c.getPlayer().yellowMessage("$donorhelp  : Display availible donator commands.");
            c.getPlayer().yellowMessage("$togglesmega : Enable or disable player Super Megaphones.");
            c.getPlayer().completeDispose();
            return 1;
        }
    }

    public static class ToggleSmega extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().setSmega();
            return 1;
        }
    }
}
