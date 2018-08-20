package server.shops;

import java.util.HashMap;
import java.util.Map;

public class ShopFactory {

    private final Map<Integer, Shop> shops = new HashMap<Integer, Shop>();
    private final Map<Integer, Shop> npcShops = new HashMap<Integer, Shop>();
    private static final ShopFactory instance = new ShopFactory();

    public static ShopFactory getInstance() {
        return instance;
    }

    public void clear() {
        this.shops.clear();
        this.npcShops.clear();
    }

    public Shop getShop(int shopId) {
        if (this.shops.containsKey(Integer.valueOf(shopId))) {
            return (Shop) this.shops.get(Integer.valueOf(shopId));
        }
        return loadShop(shopId, true);
    }

    public Shop getShopForNPC(int npcId) {
        if (this.npcShops.containsKey(Integer.valueOf(npcId))) {
            return (Shop) this.npcShops.get(Integer.valueOf(npcId));
        }
        return loadShop(npcId, false);
    }

    private Shop loadShop(int id, boolean isShopId) {
        Shop ret = Shop.createFromDB(id, isShopId);
        if (ret != null) {
            this.shops.put(Integer.valueOf(ret.getId()), ret);
            this.npcShops.put(Integer.valueOf(ret.getNpcId()), ret);
        } else if (isShopId) {
            this.shops.put(Integer.valueOf(id), null);
        } else {
            this.npcShops.put(Integer.valueOf(id), null);
        }
        return ret;
    }
}
