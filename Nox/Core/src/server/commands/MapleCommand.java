package server.commands;

import client.Client;
import constants.ServerConstants.CommandType;
import server.maps.objects.User;

/**
 * Represents a command given by a user
 *
 * @author Emilyx3
 */
public class MapleCommand {

    /**
     * what {@link User#gm} level is required to use this command
     */
    private final int gmLevelReq;
    /**
     * what gets done when this command is used
     */
    private final CommandExecute exe;

    public MapleCommand(CommandExecute c, int gmLevel) {
        exe = c;
        gmLevelReq = gmLevel;
    }

    /**
     * Call this to apply this command to the specified {@link Client} with the specified arguments.
     *
     * @param c the Client to apply this to
     * @param splitted the arguments
     * @return See {@link CommandExecute#execute}
     */
    public int execute(Client c, String[] splitted) {
        return exe.execute(c, splitted);
    }

    public CommandType getType() {
        return exe.getType();
    }

    /**
     * Returns the GMLevel needed to use this command.
     *
     * @return the required GM Level
     */
    public int getReqGMLevel() {
        return gmLevelReq;
    }
}
