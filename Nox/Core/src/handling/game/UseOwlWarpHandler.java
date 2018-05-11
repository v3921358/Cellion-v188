package handling.game;

import client.Client;
import java.util.List;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import net.InPacket;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseOwlWarpHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    @Override
    public void Process(Client c, InPacket iPacket) {
        if (!c.getPlayer().isAlive()) {
            c.SendPacket(CWvsContext.getOwlMessage(4));
            return;
        } else if (c.getPlayer().getTrade() != null) {
            c.SendPacket(CWvsContext.getOwlMessage(7));
            return;
        }
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022 && !c.getPlayer().hasBlockedInventory()) {
            final int id = iPacket.DecodeInt();
            final int map = iPacket.DecodeInt();
            if (map >= 910000001 && map <= 910000022) {
                c.SendPacket(CWvsContext.getOwlMessage(0));
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0:
                        objects = mapp.getAllMapObjects(MapleMapObjectType.HIRED_MERCHANT);
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        objects = mapp.getAllMapObjects(MapleMapObjectType.HIRED_MERCHANT);
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop) {
                            final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                            if (ips instanceof HiredMerchant) {
                                merchant = (HiredMerchant) ips;
                            }
                        }
                        break;
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.SendPacket(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    } else if (!merchant.isOpen() || !merchant.isAvailable()) {
                        c.getPlayer().dropMessage(1, "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                    } else if (merchant.getFreeSlot() == -1) {
                        c.getPlayer().dropMessage(1, "You can't enter the room due to full capacity.");
                    } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                        c.getPlayer().dropMessage(1, "You may not enter this store.");
                    } else {
                        c.getPlayer().setPlayerShop(merchant);
                        merchant.addVisitor(c.getPlayer());
                        c.SendPacket(PlayerShopPacket.getHiredMerch(c.getPlayer(), merchant, false));
                    }
                } else {
                    c.getPlayer().dropMessage(1, "The room is already closed.");
                }
            } else {
                c.SendPacket(CWvsContext.getOwlMessage(23));
            }
        } else {
            c.SendPacket(CWvsContext.getOwlMessage(23));
        }
    }

}
