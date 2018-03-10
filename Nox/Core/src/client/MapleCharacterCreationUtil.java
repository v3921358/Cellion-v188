package client;

import static client.MapleCharacterUtil.getIdByName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import constants.GameConstants;
import database.DatabaseConnection;
import tools.LogHelper;

/**
 * Static class for the utils required to check/create character creation.
 *
 * @author
 */
public class MapleCharacterCreationUtil {

    private static final Pattern namePattern = Pattern.compile("[a-zA-Z0-9]{4,12}");

    public static boolean canCreateChar(final String name, final boolean gm) {
        return getIdByName(name) == -1 && isEligibleCharName(name, gm);
    }

    public static boolean isEligibleCharName(final String name, final boolean gm) {
        if (name.length() > 12) {
            return false;
        }
        if (gm) {
            return true;
        }
        if ((name.length() < 3 && !gm) || !namePattern.matcher(name).matches()) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the account is able to make a character. The following must be fulfilled: 1) Character size is below the amount of slots 2)
     * Last creation date is below 20 seconds. If below 20 seconds it is logged, as an early warning if someone tries to spam
     *
     * @param worldId
     * @param accountId
     * @return
     */
    public static boolean canMakeCharacter(int worldId, int accountId) {
        final boolean canMake = loadCharactersSize(worldId, accountId) < getCharacterSlots(accountId, worldId);

        if (canMake) {
            Connection con = DatabaseConnection.getConnection();
            //lastCharacterCreationTime
            try (PreparedStatement ps = con.prepareStatement("SELECT lastCharacterCreationTime FROM accounts WHERE id = ? LIMIT 1")) {
                ps.setInt(1, accountId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp lastCreationDate = rs.getTimestamp("lastCharacterCreationTime");
                        long currentTime = new Date().getTime();
                        if (lastCreationDate != null) {
                            long difference = currentTime - lastCreationDate.getTime();

                            if (difference < 20000) { // too fast!
                                LogHelper.PACKET_EDIT_HACK.get().warn(String.format("[CharacterCreationUtil] Account id %d has tried to create a character within %d millis.", accountId, difference));
                                return false;
                            }
                        }
                    }
                }
            } catch (SQLException exp) {
                LogHelper.SQL.get().info("[CharacterCreationUtil] Error retrieving last character creation time\n", exp);
            }

            // everything's fine, try to update last creation time
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET lastCharacterCreationTime = NOW() WHERE id = ? LIMIT 1")) {
                ps.setInt(1, accountId);

                ps.execute();

            } catch (SQLException exp) {
                LogHelper.SQL.get().info("[CharacterCreationUtil] Error updating last character creation time\n", exp);
            }
        }

        return canMake;
    }

    private static int loadCharactersSize(int serverId, int accountId) {
        int chars = 0;

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ? AND deletedAt is null")) {
            ps.setInt(1, accountId);
            ps.setInt(2, serverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    chars = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[CharacterCreationUtil] Error loading character size.\n", e);
        }
        return chars;
    }

    /**
     * Gets the amount of character slot the account in the world is allowed to create.
     *
     * @param accountId
     * @param worldId
     * @return The amount of slots. Returns 0 if database query fails.
     */
    public static int getCharacterSlots(int accountId, int worldId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM character_slots WHERE accid = ? AND worldid = ? LIMIT 1;")) {
                ps.setInt(1, accountId);
                ps.setInt(2, worldId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("charslots");
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO character_slots (accid, worldid, charslots) VALUES (?, ?, ?)")) {
                            psu.setInt(1, accountId);
                            psu.setInt(2, worldId);
                            psu.setInt(3, GameConstants.characterSlot);
                            psu.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException sqlE) {
            LogHelper.SQL.get().info("[CharacterCreationUtil] Error getting the amount of character slots.\n", sqlE);
        }

        return 0;
    }

    public static boolean gainCharacterSlot(int accountId, int worldId, int currentSlotAmount) {
        if (currentSlotAmount >= GameConstants.characterSlotMax) {
            return false;
        }
        currentSlotAmount++;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?")) {
                ps.setInt(1, Math.min(GameConstants.characterSlotMax, currentSlotAmount));
                ps.setInt(2, worldId);
                ps.setInt(3, accountId);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException sqlE) {
            LogHelper.SQL.get().info("[CharacterCreationUtil] Error increasing character slots in SQL.\n", sqlE);
            return false;
        }
        return true;
    }
}
