package server.stores;

import java.util.List;

import client.MapleClient;
import net.Packet;
import server.maps.objects.User;
import server.stores.AbstractPlayerStore.BoughtItem;
import tools.Pair;

public interface IMaplePlayerShop {

    public final static byte HIRED_MERCHANT = 1;
    public final static byte PLAYER_SHOP = 2;
    public final static byte OMOK = 3;
    public final static byte MATCH_CARD = 4;

    public String getOwnerName();

    public String getDescription();

    public List<Pair<Byte, User>> getVisitors();

    public List<MaplePlayerShopItem> getItems();

    public boolean isOpen();

    public boolean removeItem(int item);

    public boolean isOwner(User chr);

    public byte getShopType();

    public byte getVisitorSlot(User visitor);

    public byte getFreeSlot();

    public int getItemId();

    public int getMeso();

    public int getOwnerId();

    public int getOwnerAccId();

    public void setOpen(boolean open);

    public void setMeso(int meso);

    public void addItem(MaplePlayerShopItem item);

    public void removeFromSlot(int slot);

    public void broadcastToVisitors(Packet packet);

    public void addVisitor(User visitor);

    public void removeVisitor(User visitor);

    public void removeAllVisitors(int error, int type);

    public void buy(MapleClient c, int item, short quantity);

    public void closeShop(boolean saveItems, boolean remove);

    public String getPassword();

    public int getMaxSize();

    public int getSize();

    public int getGameType();

    public void update();

    public void setAvailable(boolean b);

    public boolean isAvailable();

    public List<BoughtItem> getBoughtItems();
}
