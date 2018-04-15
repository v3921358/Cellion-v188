package server.maps.objects;

import client.MapleClient;
import client.inventory.Item;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import client.inventory.MapleInventoryIdentifier;
import database.Database;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.data.loaders.MapleAndroidBuilder;
import provider.wz.cache.WzDataStorage;
import server.MapleItemInformationProvider;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.MovementTypeA;
import tools.LogHelper;

public class MapleAndroid extends MapleMapObject {

    private final Item androidItem;
    private int stance = 0, fh = 0;
    private int hair;
    private int face;
    private int skin;
    private String name;
    private Point pos = new Point(0, 0);
    private boolean changed = false;

    private MapleAndroid(int itemid, int uniqueid) {
        androidItem = new Item(itemid, (byte) -1, (short) 1);
        androidItem.setUniqueId(uniqueid);
    }

    public Item getItem() {
        return androidItem;
    }

    public static MapleAndroid loadFromDb(int itemid, int uid) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            MapleAndroid ret = new MapleAndroid(itemid, uid);
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?")) {
                ps.setInt(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return null;
                    }
                    ret.setHair(rs.getInt("hair"));
                    ret.setFace(rs.getInt("face"));
                    ret.setName(rs.getString("name"));
                    ret.setSkin(rs.getInt("skin"));
                    ret.changed = false;
                }
            }

            return ret;
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("There were issues load androids\n", ex);
        }
        return null;
    }

    public void saveToDb() {
        if (!this.changed) {
            return;
        }
        try (PreparedStatement ps = Database.GetConnection().prepareStatement("UPDATE androids SET hair = ?, face = ?, name = ?, skin=? WHERE uniqueid = ?")) {
            ps.setInt(1, this.hair);
            ps.setInt(2, this.face);
            ps.setString(3, this.name);
            ps.setInt(4, getItem().getUniqueId());
            ps.setInt(5, this.skin);
            ps.executeUpdate();

            this.changed = false;
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
    }

    public static MapleAndroid createAndroid(int itemid, int uniqueid) {
        int androidId = getAndroidTemplateId(itemid);
        Random r = new Random();
        MapleAndroidBuilder builder = MapleItemInformationProvider.getInstance().getAndroidInfo().stream()
                .filter(android -> android.getId() == androidId).findFirst().get();

        int face = builder.getFace().get(r.nextInt(builder.getFace().size()));
        int hair = builder.getHair().get(r.nextInt(builder.getHair().size()));
        int skin = builder.getSkin().get(r.nextInt(builder.getSkin().size()));
        return saveAndroid(itemid, uniqueid, skin, hair, face);
    }

    public static int getAndroidTemplateId(int itemid) {
        MapleDataProvider chrData = WzDataStorage.getCharacterWZ();
        MapleData data = chrData.getData("Android/" + String.format("%08d", itemid) + ".img");
        return MapleDataTool.getInt(data.getChildByPath("info/android"));
    }

    public static MapleAndroid saveAndroid(int itemid, int uniqueid, int skin, int hair, int face) {
        if (uniqueid < 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try (PreparedStatement pse = Database.GetConnection().prepareStatement("INSERT INTO androids (uniqueid, hair, face, name, skin) VALUES (?, ?, ?, ?, ?)")) {
            pse.setInt(1, uniqueid);
            pse.setInt(2, hair);
            pse.setInt(3, face);
            pse.setString(4, "Android");
            pse.setInt(5, skin);
            pse.executeUpdate();
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("There were issues saving the new android\n", ex);
            return null;
        }
        MapleAndroid pet = new MapleAndroid(itemid, uniqueid);
        pet.setHair(hair);
        pet.setFace(face);
        pet.setSkin(skin);
        pet.setName("Android");

        return pet;
    }

    public void setHair(int closeness) {
        this.hair = closeness;
        this.changed = true;
    }

    public int getHair() {
        return this.hair;
    }

    public void setFace(int closeness) {
        this.face = closeness;
        this.changed = true;
    }

    public int getFace() {
        return this.face;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
        this.changed = true;
    }

    public Point getPos() {
        return this.pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getStance() {
        return this.stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if ((move instanceof LifeMovement)) {
                if ((move instanceof MovementTypeA)) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getStance());
            }
        }
    }

    /**
     * @return the fh
     */
    public int getFh() {
        return fh;
    }

    /**
     * @param fh the fh to set
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     * @return the skin
     */
    public int getSkin() {
        return skin;
    }

    /**
     * @param skin the skin to set
     */
    public void setSkin(int skin) {
        this.skin = skin;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.ANDROID;
    }

    @Override
    public void sendSpawnData(final MapleClient client) {
        // does nothing for now, not handled here
    }

    @Override
    public void sendDestroyData(final MapleClient client) {
        // does nothing for now, not handled here
    }

}
