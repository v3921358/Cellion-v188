package server.maps.objects;

import java.awt.Point;

import client.Client;
import net.OutPacket;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterStats;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import tools.packet.PacketHelper;

public final class MonsterFamiliar extends AnimatedMapleMapObject {

    private final int id;
    private final int familiar;
    private int fatigue;
    private final int characterid;
    private String name;
    private long expiry;
    private short fh = 0;
    private byte vitality;

    public MonsterFamiliar(int characterid, int id, int familiar, long expiry, String name, int fatigue, byte vitality) {
        this.familiar = familiar;
        this.characterid = characterid;
        this.expiry = expiry;
        this.vitality = vitality;
        this.id = id;
        this.name = name;
        this.fatigue = fatigue;
        setStance(0);
        setPosition(new Point(0, 0));
    }

    public MonsterFamiliar(int characterid, int familiar, long expiry) {
        this.familiar = familiar;
        this.characterid = characterid;
        this.expiry = expiry;
        fatigue = 0;
        vitality = 1;
        name = getOriginalName();
        id = Randomizer.nextInt();
    }

    public String getOriginalName() {
        return getStats().getName();
    }

    public MapleMonsterStats getStats() {
        return MapleLifeFactory.getMonsterStats(MapleItemInformationProvider.getInstance().getFamiliar(familiar).getMob());
    }

    public void addFatigue(User owner) {
        addFatigue(owner, 1);
    }

    public void addFatigue(User owner, int f) {
        fatigue = Math.min(vitality * 300, Math.max(0, fatigue + f));
        owner.getClient().SendPacket(CField.updateFamiliar(this));
        if (fatigue >= vitality * 300) {
            owner.removeFamiliar();
        }
    }

    public int getFamiliar() {
        return familiar;
    }

    public int getId() {
        return id;
    }

    public int getFatigue() {
        return fatigue;
    }

    public int getCharacterId() {
        return characterid;
    }

    public final String getName() {
        return name;
    }

    public long getExpiry() {
        return expiry;
    }

    public byte getVitality() {
        return vitality;
    }

    public void setFatigue(int f) {
        fatigue = f;
    }

    public void setName(String n) {
        name = n;
    }

    public void setExpiry(long e) {
        expiry = e;
    }

    public void setVitality(int v) {
        vitality = ((byte) v);
    }

    public void setFh(int f) {
        fh = ((short) f);
    }

    public short getFh() {
        return fh;
    }

    @Override
    public void sendSpawnData(Client client) {
        client.SendPacket(CField.spawnFamiliar(this, true, false));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.SendPacket(CField.spawnFamiliar(this, false, false));
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.FAMILIAR;
    }

    public void writeRegisterPacket(OutPacket oPacket, boolean chr) {
        oPacket.EncodeInt(getCharacterId());
        oPacket.EncodeInt(getFamiliar());
        oPacket.Fill(0, 13);
        oPacket.EncodeByte(chr ? 1 : 0);
        oPacket.EncodeShort(getVitality());
        oPacket.EncodeInt(getFatigue());
        oPacket.EncodeLong(PacketHelper.getTime(getVitality() >= 3 ? System.currentTimeMillis() : -2L));
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeLong(PacketHelper.getTime(getExpiry()));
        oPacket.EncodeShort(getVitality());
    }
}
