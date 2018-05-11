package server;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import handling.world.World;
import server.commands.CommandProcessor;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.PlayerShopPacket;

public class MapleTrade {

    private MapleTrade partner = null;
    private List<Item> items = new LinkedList<>();
    private List<Item> exchangeItems;
    private long meso = 0;
    private long exchangeMeso = 0;
    private boolean locked = false;
    private boolean inTrade = false;
    private User chr;
    private byte tradingslot;

    public MapleTrade(byte tradingslot, User chr) {
        this.tradingslot = tradingslot;
        this.chr = chr;
    }

    public void completeTrade() {
        if (exchangeItems != null) {
            List<Item> itemz = new LinkedList<>(exchangeItems);
            for (Item item : itemz) {
                short flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            chr.gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, false);
        }
        exchangeMeso = 0;
        chr.getClient().SendPacket(CField.InteractionPacket.TradeMessage(tradingslot, (byte) 7));
    }

    public void cancel(ClientSocket c, User chr) {
        cancel(c, chr, true);
    }

    public void cancel(ClientSocket c, User chr, boolean unsuccessful) {
        if (items != null) {
            List<Item> itemz = new LinkedList<>(items);
            for (Item item : itemz) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            items.clear();
        }
        if (meso > 0) {
            chr.gainMeso(meso, false, false);
        }
        meso = 0;

        c.SendPacket(CField.InteractionPacket.getTradeCancel(tradingslot));
    }

    public boolean isLocked() {
        return locked;
    }

    public void setMeso(int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (chr.getMeso() >= meso) {
            chr.gainMeso(-meso, false, false);
            this.meso += meso;
            chr.getClient().SendPacket(CField.InteractionPacket.getTradeMesoSet((byte) 0, meso));
            if (partner != null) {
                partner.getCharacter().getClient().SendPacket(CField.InteractionPacket.getTradeMesoSet((byte) 1, meso));
            }
        }
    }

    public void addItem(Item item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        chr.getClient().SendPacket(CField.InteractionPacket.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getCharacter().getClient().SendPacket(CField.InteractionPacket.getTradeItemAdd((byte) 1, item));
        }
    }

    public void chat(String message) throws Exception {
        if (!CommandProcessor.processCommand(chr.getClient(), message, ServerConstants.CommandType.TRADE)) {
            chr.dropMessage(-2, chr.getName() + " : " + message);
            if (partner != null) {
                partner.getCharacter().getClient().SendPacket(PlayerShopPacket.shopChat(chr.getName() + " : " + message, 1));
            }
        }
        if (chr.getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, chr.getName() + " said in trade with " + partner.getCharacter().getName() + ": " + message));
        } else if (partner != null && partner.getCharacter() != null && partner.getCharacter().getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, chr.getName() + " said in trade with " + partner.getCharacter().getName() + ": " + message));
        }
    }

    public void chatAuto(String message) {
        chr.dropMessage(-2, message);
        if (partner != null) {
            partner.getCharacter().getClient().SendPacket(PlayerShopPacket.shopChat(message, 1));
        }
        if (chr.getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, chr.getName() + " said in trade [Automated] with " + partner.getCharacter().getName() + ": " + message));
        } else if (partner != null && partner.getCharacter() != null && partner.getCharacter().getClient().isMonitored()) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, chr.getName() + " said in trade [Automated] with " + partner.getCharacter().getName() + ": " + message));
        }
    }

    public MapleTrade getPartner() {
        return partner;
    }

    public void setPartner(MapleTrade partner) {
        if (locked) {
            return;
        }
        this.partner = partner;
    }

    public User getCharacter() {
        return chr;
    }

    public int getNextTargetSlot() {
        if (items.size() >= 9) {
            return -1;
        }
        int ret = 1;
        for (Item item : items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean inTrade() {
        return inTrade;
    }

    public boolean setItems(ClientSocket c, Item item, byte targetSlot, int quantity) {
        int target = getNextTargetSlot();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (partner == null || target == -1 || InventoryConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP) && (quantity != 1)) {
            return false;
        }
        short flag = item.getFlag();
        if (ItemFlag.UNTRADABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }
        if ((ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId()))
                && !ItemFlag.KARMA_EQ.check(flag) && !ItemFlag.KARMA_USE.check(flag)) {
            c.SendPacket(WvsContext.enableActions());
            return false;
        }

        Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition((short) targetSlot);
        addItem(tradeItem);
        return true;
    }

    private boolean check() {
        if (chr.getMeso() + exchangeMeso < 0) {
            return false;
        }
        Map<MapleInventoryType, Integer> neededSlots = new LinkedHashMap<>();
        for (Item item : exchangeItems) {
            MapleInventoryType type = MapleItemInformationProvider.getInventoryType(item.getItemId());
            if (neededSlots.get(type) == null) {
                neededSlots.put(type, 1);
            } else {
                neededSlots.put(type, neededSlots.get(type) + 1);
            }
        }
        for (Entry<MapleInventoryType, Integer> entry : neededSlots.entrySet()) {
            if (chr.getInventory(entry.getKey()).isFull(entry.getValue() - 1)) {
                return false;
            }
        }
        return true;
    }

    public static void completeTrade(User c) {
        MapleTrade local = c.getTrade();
        MapleTrade partner = local.getPartner();

        if (partner == null || local.locked) {
            return;
        }
        local.locked = true;
        partner.getCharacter().getClient().SendPacket(CField.InteractionPacket.getTradeConfirmation());

        partner.exchangeItems = new LinkedList<>(local.items);
        partner.exchangeMeso = local.meso;

        if (partner.isLocked()) {
            boolean userReady = local.check();
            boolean partnerReady = partner.check();
            if (userReady && partnerReady) {
                local.completeTrade();
                partner.completeTrade();
            } else {
                partner.cancel(partner.getCharacter().getClient(), partner.getCharacter(), userReady ? partnerReady : userReady);
                local.cancel(c.getClient(), c, userReady ? partnerReady : userReady);
            }
            partner.getCharacter().setTrade(null);
            c.setTrade(null);
        }
    }

    public static void cancelTrade(MapleTrade trade, ClientSocket c, User chr) {
        trade.cancel(c, chr);

        MapleTrade partner = trade.getPartner();
        if (partner != null && partner.getCharacter() != null) {
            partner.cancel(partner.getCharacter().getClient(), partner.getCharacter());
            partner.getCharacter().setTrade(null);
        }
        chr.setTrade(null);
    }

    public static void startTrade(User c) {
        if (GameConstants.isZero(c.getJob())) {
            c.getClient().SendPacket(WvsContext.broadcastMsg(5, "Sorry, the trade feature is not available for the Zero class."));
            return;
        }

        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c));
            c.getClient().SendPacket(CField.InteractionPacket.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
        } else {
            c.getClient().SendPacket(WvsContext.broadcastMsg(5, "You are already in a trade window."));
        }
    }

    public static void inviteTrade(User c1, User c2) {
        if (GameConstants.isZero(c2.getJob())) {
            c1.getClient().SendPacket(WvsContext.broadcastMsg(5, "Sorry, the trade feature is not available for the Zero class."));
            c1.getTrade().cancel(c1.getClient(), c1);
            return;
        }

        if (c1 == null || c1.getTrade() == null) {
            return;
        }
        if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().SendPacket(CField.InteractionPacket.getTradeInvite(c1));
        } else {
            c1.getClient().SendPacket(WvsContext.broadcastMsg(5, "The other player is already trading with someone else."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
        }
    }

    public static void visitTrade(User c1, User c2) {
        if (GameConstants.isZero(c2.getJob())) {
            c1.getClient().SendPacket(WvsContext.broadcastMsg(5, "Sorry, the trade feature is not available for the Zero class."));
            return;
        }

        if (c2 != null && c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            c1.getTrade().inTrade = true;
            c2.getClient().SendPacket(PlayerShopPacket.shopVisitorAdd(c1, 1));
            c1.getClient().SendPacket(CField.InteractionPacket.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
        } else {
            c1.getClient().SendPacket(WvsContext.broadcastMsg(5, "The other player has already closed the trade"));
        }
    }

    public static void declineTrade(User c) {
        MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                User other = trade.getPartner().getCharacter();
                if (other != null && other.getTrade() != null) {
                    other.getTrade().cancel(other.getClient(), other);
                    other.setTrade(null);
                    other.dropMessage(5, c.getName() + " has declined your trade request");
                }
            }
            trade.cancel(c.getClient(), c);
            c.setTrade(null);
        }
    }

    public long getExchangeMesos() {
        return exchangeMeso;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Item> getExchangeItems() {
        return exchangeItems;
    }

}
