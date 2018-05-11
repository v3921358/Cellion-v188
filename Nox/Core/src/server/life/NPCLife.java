package server.life;

import client.ClientSocket;
import server.MapleStringInformationProvider;
import server.maps.MapleMapObjectType;
import server.shops.MapleShopFactory;
import tools.packet.CField.NPCPacket;

public class NPCLife extends AbstractLoadedMapleLife {

    private String overrideName = null;
    private boolean custom = false;

    public NPCLife(final int id) {
        super(id);
    }

    public void setOverrideName(String overrideName) {
        this.overrideName = overrideName;
    }

    public final boolean hasShop() {
        return MapleShopFactory.getInstance().getShopForNPC(getId()) != null;
    }

    public final void sendShop(final ClientSocket c) {
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
    public void sendSpawnData(final ClientSocket client) {
        if (getId() >= 9901000) {
        } else {
            client.SendPacket(NPCPacket.spawnNPC(this, true));
            client.SendPacket(NPCPacket.spawnNPCRequestController(this, true));
        }
    }

    @Override
    public final void sendDestroyData(final ClientSocket client) {
        client.SendPacket(NPCPacket.removeNPCController(getObjectId()));
        client.SendPacket(NPCPacket.removeNPC(getObjectId()));
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
