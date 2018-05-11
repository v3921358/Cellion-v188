package server.commands;

import client.Client;
import constants.ServerConstants;

/**
 *
 * @author
 */
public class DebugCommands {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERNAL_DEVELOPER;
    }

    public static class ToggleCooldown extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Cooldown " + (c.getPlayer().toggleCooldown() ? "removed." : "restored."));
            return 1;
        }
    }

    public static class SetBurningField extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            int fieldLevel = 1;
            if (splitted.length > 1) {
                try {
                    fieldLevel = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException nfe) {
                    c.getPlayer().dropMessage(6, "!setburningfield <optional int - Field Lv amount>");
                }
            }
            c.getPlayer().getMap().setBurningFieldLevel((byte) Math.min(10, fieldLevel));
            return 1;
        }
    }

    public static class RespawnRune extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            c.getPlayer().getMap().respawnRune(true);

            return 1;
        }
    }

    public static class SetCombo extends CommandExecute {

        @Override
        public int execute(Client c, String[] splitted) {
            int comboAmount = 49;
            if (splitted.length > 1) {
                try {
                    comboAmount = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException nfe) {
                    c.getPlayer().dropMessage(6, "!setcombo <optional int - Combo amount>");
                }
            }
            c.getPlayer().setCombo((short) comboAmount);
            return 1;
        }
    }

}
