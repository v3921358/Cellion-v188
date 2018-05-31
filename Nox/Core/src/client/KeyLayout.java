package client;

import constants.GameConstants;
import database.Database;
import net.OutPacket;
import tools.Pair;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import tools.LogHelper;

public class KeyLayout implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private boolean changed = false;
    private final Map<Integer, Pair<Byte, Integer>> keymap;

    public KeyLayout() {
        keymap = new HashMap<>();
    }

    public KeyLayout(Map<Integer, Pair<Byte, Integer>> keys) {
        keymap = keys;
    }

    /**
     * Gets the unmodifiable Map of the key layout of the character
     *
     * @return Map<Integer, Pair<Byte, Integer>> layout.
     */
    public Map<Integer, Pair<Byte, Integer>> getLayout() {
        return keymap;
    }

    /**
     * Adds the keymap to the Map
     *
     * @param key
     * @param type
     * @param action
     * @param changed
     */
    public void addKeyLayout(int key, byte type, int action, boolean changed) {
        keymap.put(key, new Pair<Byte, Integer>(type, action));
        if (changed) {
            this.changed = true;
        }
    }

    /**
     * Remove a key layout from the Map
     *
     * @param key
     * @param changed
     */
    public void removeKeyLayout(int key, boolean changed) {
        keymap.remove(key);

        if (changed) {
            this.changed = true;
        }
    }

    public final void writeData(final OutPacket oPacket, int jobid) {
        oPacket.EncodeByte(keymap.isEmpty() ? 1 : 0);
        if (keymap.isEmpty()) {
            return;
        }
        Pair<Byte, Integer> binding;
        if (GameConstants.isBeastTamer(jobid)) { // TODO: Fix beast tamer skill lulz
            for (int x = 0; x < 89; x++) { // Animal 1
                binding = keymap.get(x);
                if (binding != null) {
                    oPacket.EncodeByte(binding.getLeft());
                    oPacket.EncodeInt(binding.getRight());
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }
            for (int x = 0; x < 89; x++) { // Animal 2
                binding = keymap.get(x);
                if (binding != null) {
                    oPacket.EncodeByte(binding.getLeft());
                    oPacket.EncodeInt(binding.getRight());
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }
            for (int x = 0; x < 89; x++) { // Animal 3
                binding = keymap.get(x);
                if (binding != null) {
                    oPacket.EncodeByte(binding.getLeft());
                    oPacket.EncodeInt(binding.getRight());
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }
            for (int x = 0; x < 89; x++) { // Animal 4
                binding = keymap.get(x);
                if (binding != null) {
                    oPacket.EncodeByte(binding.getLeft());
                    oPacket.EncodeInt(binding.getRight());
                } else {
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                }
            }
        }
        for (int x = 0; x < 89; x++) { // Normal
            binding = keymap.get(x);
            if (binding != null) {
                oPacket.EncodeByte(binding.getLeft());
                oPacket.EncodeInt(binding.getRight());
            } else {
                oPacket.EncodeByte(0);
                oPacket.EncodeInt(0);
            }
        }
    }

    public final void saveKeys(final int charid) throws SQLException {
        if (!changed) {
            return;
        }
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM keymap WHERE characterid = ?")) {
                ps.setInt(1, charid);
                ps.execute();
            }
            if (keymap.isEmpty()) {
                return;
            }
            boolean first = true;
            StringBuilder query = new StringBuilder();

            for (Entry<Integer, Pair<Byte, Integer>> keybinding : keymap.entrySet()) {
                if (first) {
                    first = false;
                    query.append("INSERT INTO keymap VALUES (");
                } else {
                    query.append(",(");
                }
                query.append("DEFAULT,");
                query.append(charid).append(",");
                query.append(keybinding.getKey().intValue()).append(",");
                query.append(keybinding.getValue().getLeft().byteValue()).append(",");
                query.append(keybinding.getValue().getRight().intValue()).append(")");
            }
            try (PreparedStatement ps2 = con.prepareStatement(query.toString())) {
                ps2.execute();
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", ex);
        }

    }
}
