package server.life;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import database.Database;
import handling.world.MapleCharacterLook;
import handling.world.World;
import service.ChannelServer;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.LogHelper;
import tools.packet.CField.NPCPacket;
import tools.packet.WvsContext;

public class PlayerNPC extends NPCLife implements MapleCharacterLook {

    public static final boolean Auto_Update = false;
    private String name;
    private Map<Short, Integer> equips = new HashMap<>();
    private Map<Short, Integer> secondEquips = new HashMap<>();
    private int mapid, face, hair, charId, elf, faceMarking, ears, tail, zeroBetaFace, zeroBetaHair, angelicDressupFace, angelicDressupHair, angelicDressupSuit;
    private short job;
    private byte skin, gender, secondGender;
    private final int[] pets = new int[3];

    public PlayerNPC(ResultSet rs) throws Exception {
        super(rs.getInt("ScriptId"));
        this.name = rs.getString("name");
        hair = rs.getInt("hair");
        face = rs.getInt("face");
        zeroBetaHair = rs.getInt("zeroBetaHair");
        zeroBetaFace = rs.getInt("zeroBetaFace");
        angelicDressupHair = rs.getInt("angelicDressupHair");
        angelicDressupFace = rs.getInt("angelicDressupFace");
        mapid = rs.getInt("map");
        skin = rs.getByte("skin");
        charId = rs.getInt("charid");
        gender = rs.getByte("gender");
        secondGender = gender;
        job = rs.getShort("job");
        elf = rs.getInt("elf");
        faceMarking = rs.getInt("faceMarking");
        ears = rs.getInt("ears");
        tail = rs.getInt("tail");
        setCoords(rs.getInt("x"), rs.getInt("y"), rs.getInt("dir"), rs.getInt("Foothold"));
        String[] pet = rs.getString("pets").split(",");
        for (int i = 0; i < 3; i++) {
            if (pet[i] != null) {
                pets[i] = Integer.parseInt(pet[i]);
            } else {
                pets[i] = 0;
            }
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs_equip WHERE NpcId = ?")) {
                ps.setInt(1, getId());
                try (ResultSet rs2 = ps.executeQuery()) {
                    while (rs2.next()) {
                        equips.put((short) rs2.getByte("equippos"), rs2.getInt("equipid"));
                        secondEquips.put((short) rs2.getByte("equippos"), rs2.getInt("equipid"));
                    }
                }
                ps.close();
            }
        }

    }

    public PlayerNPC(User cid, int npc, MapleMap map, User base) {
        super(npc);
        this.name = cid.getName();
        this.charId = cid.getId();
        this.mapid = map.getId();
        setCoords(base.getTruePosition().x, base.getTruePosition().y, 0, base.getFh()); //0 = facing dir? no idea, but 1 dosnt work
        update(cid);
    }

    @Override
    public String getName() {
        return name;
    }

    private void setCoords(int x, int y, int f, int fh) {
        setPosition(new Point(x, y));
        setCy(y);
        setRx0(x - 50);
        setRx1(x + 50);
        setF(f);
        setFh(fh);
    }

    public static void loadAll() {
        List<PlayerNPC> toAdd = new ArrayList<>();
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM playernpcs"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    toAdd.add(new PlayerNPC(rs));
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
        }

        for (PlayerNPC npc : toAdd) {
            npc.addToServer();
        }
    }

    public static void updateByCharId(User chr) {
        if (World.Find.findChannel(chr.getId()) > 0) { //if character is in cserv
            for (PlayerNPC npc : ChannelServer.getInstance(World.Find.findChannel(chr.getId())).getAllPlayerNPC()) {
                npc.update(chr);
            }
        }
    }

    public void addToServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.addPlayerNPC(this);
        }
    }

    public void removeFromServer() {
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            cserv.removePlayerNPC(this);
        }
    }

    private void update(User chr) {
        if (chr == null || charId != chr.getId()) {
            return; //cant use name as it mightve been change actually..
        }
        this.name = chr.getName();
        setHair(chr.getHair());
        setZeroBetaHair(chr.getZeroBetaHair());
        setAngelicDressupFace(chr.getAngelicDressupFace());
        setFace(chr.getFace());
        setZeroBetaFace(chr.getZeroBetaFace());
        setAngelicDressupHair(chr.getAngelicDressupHair());
        setSkin((chr.getSkinColor()));
        setGender(chr.getGender());
        setSecondGender(chr.getGender());//is always female but w/e not gonna rewrite. so dis shit makes it male/female gg, can't tell the difference with npc's anyway.
        setPets(chr.getPets());
        setJob(chr.getJob());
        setElf(chr.getElf());
        setFaceMarking(chr.getFaceMarking());
        setEars(chr.getEars());
        setTail(chr.getTail());
        setAngelicDressupSuit(chr.getAngelicDressupSuit());
        equips = new HashMap<>();
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).newList()) {
            if (item.getPosition() < -127) {
                continue;
            }
            equips.put((Short) item.getPosition(), item.getItemId());
        }

        secondEquips = new HashMap<>();
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).newList()) {
            if (item.getPosition() < -127) {
                continue;
            }
            secondEquips.put((Short) item.getPosition(), item.getItemId());
        }
        saveToDB();
    }

    public void destroy() {
        destroy(false); //just sql
    }

    public void destroy(boolean remove) {
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("DELETE FROM playernpcs WHERE scriptid = ?");
            ps.setInt(1, getId());
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("DELETE FROM playernpcs_equip WHERE npcid = ?");
            ps.setInt(1, getId());
            ps.executeUpdate();
            ps.close();
            if (remove) {
                removeFromServer();
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

    }

    public void saveToDB() {
        try (Connection con = Database.GetConnection()) {

            if (getNPCFromWZ() == null) {
                destroy(true);
                return;
            }
            destroy();
            PreparedStatement ps = con.prepareStatement("INSERT INTO playernpcs(name, hair, face, skin, x, y, map, charid, scriptid, foothold, dir, gender, pets, job, elf, faceMarking, ears, tail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int k = 0;
            ps.setString(++k, getName());
            ps.setInt(++k, getHair());
            ps.setInt(++k, getFace());
            ps.setInt(++k, getSkinColor());
            ps.setInt(++k, getTruePosition().x);
            ps.setInt(++k, getTruePosition().y);
            ps.setInt(++k, getMapId());
            ps.setInt(++k, getCharId());
            ps.setInt(++k, getId());
            ps.setInt(++k, getFh());
            ps.setInt(++k, getF());
            ps.setInt(++k, getGender());
            String[] pet = {"0", "0", "0"};
            for (int i = 0; i < 3; i++) {
                if (pets[i] > 0) {
                    pet[i] = String.valueOf(pets[i]);
                }
            }
            ps.setString(++k, pet[0] + "," + pet[1] + "," + pet[2]);
            ps.setShort(++k, getJob());
            ps.setInt(++k, getElf());
            ps.setInt(++k, getFaceMarking());
            ps.setInt(++k, getEars());
            ps.setInt(++k, getTail());
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("INSERT INTO playernpcs_equip(npcid, charid, equipid, equippos) VALUES (?, ?, ?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, getCharId());
            for (Entry<Short, Integer> equip : equips.entrySet()) {
                ps.setInt(3, equip.getValue());
                ps.setInt(4, equip.getKey());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException se) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
        }

    }

    @Override
    public short getJob() {
        return job;
    }

    @Override
    public int getFaceMarking() {
        return faceMarking;
    }

    @Override
    public int getEars() {
        return ears;
    }

    @Override
    public int getTail() {
        return tail;
    }

    @Override
    public int getElf() {
        return elf;
    }

    @Override
    public Map<Short, Integer> getEquips(boolean fusionAnvil) {
        return equips;
    }

    @Override
    public Map<Short, Integer> getSecondaryEquips(boolean fusionAnvil) {
        return secondEquips;
    }

    @Override
    public Map<Short, Integer> getTotems() {
        return new HashMap<>();
    }

    @Override
    public byte getSkinColor() {
        return skin;
    }

    @Override
    public byte getGender() {
        return gender;
    }

    @Override
    public int getFace() {
        return face;
    }

    @Override
    public int getHair() {
        return hair;
    }

    @Override
    public byte getSecondaryGender() {
        return secondGender;
    }

    @Override
    public int getZeroBetaFace() {
        return zeroBetaFace;
    }

    @Override
    public int getZeroBetaHair() {
        return zeroBetaHair;
    }

    public int getCharId() {
        return charId;
    }

    public int getMapId() {
        return mapid;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public void setFaceMarking(int faceMarking) {
        this.faceMarking = faceMarking;
    }

    public void setEars(int ears) {
        this.ears = ears;
    }

    public void setTail(int tail) {
        this.tail = tail;
    }

    public void setElf(int elf) {
        this.elf = elf;
    }

    public void setSkin(byte s) {
        this.skin = s;
    }

    public void setFace(int f) {
        this.face = f;
    }

    public void setHair(int h) {
        this.hair = h;
    }

    public void setGender(int g) {
        this.gender = (byte) g;
    }

    public void setZeroBetaFace(int f) {
        this.zeroBetaFace = f;
    }

    public void setAngelicDressupSuit(int s) {
        this.angelicDressupSuit = s;
    }

    public void setZeroBetaHair(int h) {
        this.zeroBetaHair = h;
    }

    public void setAngelicDressupFace(int f) {
        this.angelicDressupFace = f;
    }

    public void setAngelicDressupHair(int h) {
        this.angelicDressupHair = h;
    }

    public void setSecondGender(int g) {
        this.secondGender = (byte) g;
    }

    public int getPet(int i) {
        return pets[i] > 0 ? pets[i] : 0;
    }

    public void setPets(List<Pet> p) {
        for (int i = 0; i < 3; i++) {
            if (p != null && p.size() > i && p.get(i) != null) {
                this.pets[i] = p.get(i).getItem().getItemId();
            } else {
                this.pets[i] = 0;
            }
        }
    }

    @Override
    public void sendSpawnData(ClientSocket client) {
        client.SendPacket(NPCPacket.spawnNPC(this, true));
        client.SendPacket(WvsContext.spawnPlayerNPC(this));
        client.SendPacket(NPCPacket.spawnNPCRequestController(this, true));
    }

    public NPCLife getNPCFromWZ() {
        NPCLife npc = LifeFactory.getNPC(getId());
        if (npc != null) {
            this.name = getName();
        }
        return npc;
    }

    @Override
    public int getAngelicDressupHair() {
        return angelicDressupHair;
    }

    @Override
    public int getAngelicDressupFace() {
        return angelicDressupFace;
    }

    @Override
    public int getAngelicDressupSuit() {
        return angelicDressupSuit;
    }

    /* (non-Javadoc)
	 * @see handling.world.MapleCharacterLook#getPets()
     */
    @Override
    public List<Pet> getPets() {
        return null; //for now
    }
}
