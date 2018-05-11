/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.maps.objects;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.ItemLoader;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import client.inventory.MapleInventoryIdentifier;
import constants.ServerConstants;
import database.Database;
import java.util.ArrayList;
import server.MapleItemInformationProvider;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.MovementTypeA;
import tools.LogHelper;

public class Pet extends MapleMapObject {

    public static enum PetFlag {

        ITEM_PICKUP(0x01, 5190000, 5191000),
        EXPAND_PICKUP(0x02, 5190002, 5191002), //idk
        AUTO_PICKUP(0x04, 5190003, 5191003), //idk
        UNPICKABLE(0x08, 5190005, -1), //not coded
        LEFTOVER_PICKUP(0x10, 5190004, 5191004), //idk
        HP_CHARGE(0x20, 5190001, 5191001),
        MP_CHARGE(0x40, 5190006, -1),
        PET_BUFF(0x80, -1, -1), //idk
        PET_DRAW(0x100, 5190007, -1), //nfs
        PET_DIALOGUE(0x200, 5190008, -1); //nfs

        private final int i, item, remove;

        private PetFlag(int i, int item, int remove) {
            this.i = i;
            this.item = item;
            this.remove = remove;
        }

        public final int getValue() {
            return i;
        }

        public final boolean check(int flag) {
            return (flag & i) == i;
        }

        public static final PetFlag getByAddId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.item == itemId) {
                    return flag;
                }
            }
            return null;
        }

        public static final PetFlag getByDelId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.remove == itemId) {
                    return flag;
                }
            }
            return null;
        }
    }

    private final Item petItem;
    private String name;
    private int fh = 0, stance = 0, secondsLeft = 0;
    private Point pos;
    private byte fullness = 100, level = 1, summoned = 0;
    private short closeness = 0;
    private boolean changed = false;
    private int color = -1, giant = 100, transform = 0, reinforced = 0;

    public Pet(int id, short position, int uniqueid, short flag) {
        petItem = new Item(id, position, (short) 1, flag, uniqueid);
    }

    public static final Pet loadFromDb(int itemid, int uId, short inventorypos, short flags) {
        try (Connection con = Database.GetConnection()) {

            final Pet ret = new Pet(itemid, inventorypos, uId, flags);
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?")) {
                ps.setInt(1, uId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return null;
                    }

                    ret.setName(rs.getString("name"));
                    ret.setCloseness(rs.getShort("closeness"));
                    ret.setLevel(rs.getByte("level"));
                    ret.setFullness(rs.getByte("fullness"));
                    ret.setSecondsLeft(rs.getInt("seconds"));
                    ret.getItem().setFlag(rs.getShort("flags"));
                    ret.changed = false;
                }
            }

            return ret;
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("There was an issue with loading a pet from the db\n", ex);
            return null;
        }
    }

    public final void saveToDb() {
        if (!changed) {
            return;
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ? WHERE petid = ?")) {
                ps.setInt(1, getItem().getUniqueId());
                ps.setString(2, name);
                ps.setByte(3, level);
                ps.setShort(4, closeness);
                ps.setByte(5, fullness);
                ps.setInt(6, secondsLeft);
                ps.setShort(7, getItem().getFlag());
                ps.executeUpdate();
                changed = false;
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with saving pets to the database:\n", e);
        }

    }

    public static final Pet createPet(final int itemid, final int uniqueid) {
        return createPet(itemid, MapleItemInformationProvider.getInstance().getName(itemid), 1, 0, 100, uniqueid, itemid == 5000054 ? 18000 : 0, (short) 0);
    }

    public static final Pet createPet(int itemid, String name, int level, int closeness, int fullness, int uniqueid, int secondsLeft, short flag) {
        if (uniqueid <= -1) { //wah
            uniqueid = MapleInventoryIdentifier.getInstance();
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, uniqueid); //pse.setInt(1, uniqueid);
                ps.setString(2, name);
                ps.setByte(3, (byte) level);
                ps.setShort(4, (short) closeness);
                ps.setByte(5, (byte) fullness);
                ps.setInt(6, secondsLeft);
                ps.setShort(7, flag);
                ps.executeUpdate();
            }
        } catch (final SQLException ex) {
            LogHelper.SQL.get().info("There was an issue with loading a pet from the db\n", ex);
            return null;
        }

        final Pet pet = new Pet(itemid, (short) -1, uniqueid, flag);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setSecondsLeft(secondsLeft);
        return pet;
    }

    public Item getItem() {
        return petItem;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
        this.changed = true;
    }

    public final boolean getSummoned() {
        return summoned > 0;
    }

    public final byte getSummonedValue() {
        return summoned;
    }

    public final void setSummoned(final int summoned) {
        this.summoned = (byte) summoned;
    }

    public final short getCloseness() {
        return closeness;
    }

    public final void setCloseness(final int closeness) {
        this.closeness = (short) closeness;
        this.changed = true;
    }

    public final byte getLevel() {
        return level;
    }

    public final void setLevel(final int level) {
        this.level = (byte) level;
        this.changed = true;
    }

    public final byte getFullness() {
        return fullness;
    }

    public final void setFullness(final int fullness) {
        this.fullness = (byte) fullness;
        this.changed = true;
    }

    public final int getFh() {
        return fh;
    }

    public final void setFh(final int Fh) {
        this.fh = Fh;
    }

    public final Point getPos() {
        return pos;
    }

    public final void setPos(final Point pos) {
        this.pos = pos;
    }

    public final int getStance() {
        return stance;
    }

    public final void setStance(final int stance) {
        this.stance = stance;
    }

    public final boolean canConsume(final int itemId) {
        final MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        for (final int petId : mii.getItemEffect(itemId).getPetsCanConsume()) {
            if (petId == getItem().getItemId()) {
                return true;
            }
        }
        return false;
    }

    public final void updatePosition(final List<LifeMovementFragment> movement) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof MovementTypeA) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getStance());
            }
        }
    }

    public final int getSecondsLeft() {
        return secondsLeft;
    }

    public final void setSecondsLeft(int sl) {
        this.secondsLeft = sl;
        this.changed = true;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return the giant
     */
    public int getGiant() {
        return giant;
    }

    /**
     * @param giant the giant to set
     */
    public void setGiant(int giant) {
        this.giant = giant;
    }

    /**
     * @return the transform
     */
    public int getTransform() {
        return transform;
    }

    /**
     * @param transform the transform to set
     */
    public void setTransform(int transform) {
        this.transform = transform;
    }

    /**
     * @return the reinforced
     */
    public int getReinforced() {
        return reinforced;
    }

    /**
     * @param reinforced the reinforced to set
     */
    public void setReinforced(int reinforced) {
        this.reinforced = reinforced;
    }

    public static void clearPet() {
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM pets");
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> uids = new ArrayList();
            while (rs.next()) {
                int uid = rs.getInt("petid");
                if (!ItemLoader.isExistsByUniqueid(uid, con)) {
                    if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                        System.err.println("[Debug] Pet (" + rs.getString("name") + ") Pet ID (" + uid + ") doesn't exist, cleaning up.");// It cleans it up if it doesnt exist, OH okay
                    }
                    uids.add(uid);
                }
            }
            ps.close();
            rs.close();
            for (int id : uids) {
                ps = con.prepareStatement("DELETE FROM pets WHERE petid = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PET;
    }

    @Override
    public void sendSpawnData(final ClientSocket client) {
        // does nothing for now, not handled here
    }

    @Override
    public void sendDestroyData(final ClientSocket client) {
        // does nothing for now, not handled here
    }
}
