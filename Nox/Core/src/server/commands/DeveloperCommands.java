package server.commands;

import client.ClientSocket;
import constants.ServerConstants;
import net.OutPacket;
import server.maps.objects.User;
import tools.StringUtil;
import tools.packet.CField;

/**
 * Developer Commands
 * @author Mazen Massoud
 */
public class DeveloperCommands {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERNAL_DEVELOPER;
    }

    /**
     * Enables access to the commands in the CommandVault class.
     */
    public static class CommandVault extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pPlayer = c.getPlayer();
            pPlayer.setGM((pPlayer.getGMLevel() < 10) ? (byte) 10 : (byte) 5);
            pPlayer.dropMessage(6, "You" + ((pPlayer.getGMLevel() < 10) ? " now " : " no longer ") + "have access to additional commands located in the Command Vault.");
            return 1;
        }
    }
    
    public static class ToggleCooldown extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(6, "Cooldowns " + (c.getPlayer().toggleCooldown() ? "removed." : "restored."));
            return 1;
        }
    }
    
    public static class GMPerson extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]).setGM(Byte.parseByte(splitted[2]));
            return 1;
        }
    }
    
    public static class SetBurningField extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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
    
    public static class CancelBuffs extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().cancelAllBuffs();
            return 1;
        }
    }

    public static class RespawnRune extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().respawnRune(true);

            return 1;
        }
    }

    public static class SetCombo extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
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

    public static class TestDirection extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.SendPacket(CField.UIPacket.UserInGameDirectionEvent(StringUtil.joinStringFrom(splitted, 5), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5])));
            return 1;
        }
    }

    public static class MakePacket extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            byte[] aData = StringUtil.joinStringFrom(splitted, 1).getBytes();
            byte[] aDataTrim = new byte[aData.length - 2];
            System.arraycopy(aData, 2, aDataTrim, 0, aDataTrim.length);
            c.SendPacket(new OutPacket((short) ((aData[0] & 0xFF) + (aData[1] >>> 8))).Encode(aDataTrim));
            return 1;
        }
    }
}
