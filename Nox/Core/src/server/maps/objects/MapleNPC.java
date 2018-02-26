package server.maps.objects;

import client.MapleClient;
import server.MapleStringInformationProvider;
import server.life.AbstractLoadedMapleLife;
import server.maps.MapleMapObjectType;
import server.shops.MapleShopFactory;
import tools.packet.CField.NPCPacket;

public class MapleNPC extends AbstractLoadedMapleLife {

    private String overrideName = null;
    private boolean custom = false;

    public MapleNPC(final int id) {
        super(id);
    }

    public void setOverrideName(String overrideName) {
        this.overrideName = overrideName;
    }

    public final boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    public final void sendShop(final MapleClient c) {
        MapleShopFactory.getInstance().getShopForNPC(getId()).sendShop(c);
    }

    // Handling for custom NPCs.
    public final boolean isCustom() {
        return custom;
    }

    public final void setCustom(final boolean custom) {
        this.custom = custom;
    }

    @Override
    public void sendSpawnData(final MapleClient client) {
        if (getId() >= 9901000) {
        } else {
            client.write(NPCPacket.spawnNPC(this, true));
            client.write(NPCPacket.spawnNPCRequestController(this, true));
        }
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
        client.write(NPCPacket.removeNPCController(getObjectId()));
        client.write(NPCPacket.removeNPC(getObjectId()));
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.NPC;
    }

    public String getName() {
        if (overrideName != null) {
            return overrideName;
        }

        String name = MapleStringInformationProvider.getNPCStringCache().get(this.getId());
        return name;
    }
}
