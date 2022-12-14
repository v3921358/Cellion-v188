/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.CharacterUtil;
import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.ServerConstants;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import server.CashItem;
import server.CashItemFactory;
import server.CashItemInfo;
import server.maps.objects.User;
import tools.Triple;
import net.InPacket;
import server.CashShop;
import tools.packet.CSPacket;
import tools.packet.WvsContext;

/**
 *
 * @author Novak
 */
public class ItemPurchase {

    public static void BuyNormalItem(InPacket iPacket, ClientSocket c, User chr) {
        iPacket.DecodeByte(); // bByMaplePoint
        int type = iPacket.DecodeInt(); // nPurchaseOption
        iPacket.DecodeByte(); // Unknown
        int sn = iPacket.DecodeInt(); // nCommoditySN
        CashItem item = CashItemFactory.getInstance().getAllItem(sn);
        int itemPrice = iPacket.DecodeInt(); // nPrice
        int nQuantity = iPacket.DecodeInt(); // Quantity of items bought, used for cart
        iPacket.DecodeInt(); // Unknown
        iPacket.DecodeInt(); // Unknown
        iPacket.DecodeInt(); // Unknown

        if (item == null) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        if (itemPrice != item.getDiscountPrice()) {
            c.SendPacket(CSPacket.sendCSFail(0));
            return;
        }

        boolean blockedCS = (item.getItemId() == 5211048 || item.getItemId() == 5211046 || item.getItemId() == 5710000 || item.getItemId() == 5360042 || item.getItemId() == 5360000);

        chr.modifyCSPoints(type, -itemPrice, true);
        Item changedItem = chr.getCashInventory().toItem(item);
        if ((changedItem != null)) {
            if (blockedCS && !ServerConstants.CS_COUPONS) {//check if it's an exp/drop rate coupon and if they're enabled.
                c.SendPacket(CSPacket.sendCSFail(0)); //can't buy lol.
                c.getPlayer().modifyCSPoints(type, itemPrice); //instant refund basically.
            } else {
                chr.getCashInventory().addToInventory(changedItem);
                c.SendPacket(CSPacket.showBoughtCSItem(changedItem, item.getSN(), c.getAccID()));
            }
        } else {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
    }

    public static void BuyEquipItem(InPacket iPacket, ClientSocket c, User chr) {
        iPacket.Skip(1);
        int itemPrice = iPacket.DecodeInt();
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());

        if (iteminfo != null && chr.getCSPoints(itemPrice) >= iteminfo.getPrice()) {
            if (!iteminfo.genderEquals(c.getPlayer().getGender())/* && c.getPlayer().getAndroid() == null*/) {
                c.SendPacket(CSPacket.sendCSFail(0xA7));
                playerCashShopInfo(c);
                return;
            } else if (iteminfo.getId() == 5211046 || iteminfo.getId() == 5211047 || iteminfo.getId() == 5211048 || iteminfo.getId() == 5050100 || iteminfo.getId() == 5051001) {
                c.SendPacket(WvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                c.SendPacket(WvsContext.enableActions());
                playerCashShopInfo(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                c.SendPacket(CSPacket.sendCSFail(0xB2));
                playerCashShopInfo(c);
                return;
            }
            for (int id : GameConstants.cashBlock) {
                if (iteminfo.getId() == id) {
                    c.SendPacket(WvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                    c.SendPacket(WvsContext.enableActions());
                    playerCashShopInfo(c);
                    return;
                }
            }
            chr.modifyCSPoints(itemPrice, -iteminfo.getPrice(), false);
            Item changedItem = chr.getCashInventory().toItem(iteminfo);
            if (changedItem != null && changedItem.getUniqueId() > 0 && changedItem.getItemId() == iteminfo.getId() && changedItem.getQuantity() == iteminfo.getCount()) {
                chr.getCashInventory().addToInventory(changedItem);
                c.SendPacket(CSPacket.showBoughtCSItem(changedItem, iteminfo.getSN(), c.getAccID()));
            } else {
                c.SendPacket(CSPacket.sendCSFail(0));
            }
        } else {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
    }

    public static void BuyRings(InPacket iPacket, ClientSocket c, User chr, int type) {
        iPacket.DecodeString();
        int itemPrice = iPacket.DecodeInt();
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());
        String partnerName = iPacket.DecodeString();
        String msg = iPacket.DecodeString();
        if ((iteminfo == null) || (!GameConstants.isEffectRing(iteminfo.getId())) || (c.getPlayer().getCSPoints(itemPrice) < iteminfo.getPrice()) || (msg.length() > 73) || (msg.length() < 1)) {
            c.SendPacket(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        }
        if (!iteminfo.genderEquals(c.getPlayer().getGender())) {
            playerCashShopInfo(c);
            return;
        }
        if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
            playerCashShopInfo(c);
            return;
        }
        Triple CaseInfo = CharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
        if ((CaseInfo == null) || (((Integer) CaseInfo.getLeft()) <= 0) || (((Integer) CaseInfo.getLeft()) == c.getPlayer().getId())) {
            playerCashShopInfo(c);
            return;
        }
        if (((Integer) CaseInfo.getMid()) == c.getAccID()) {
            playerCashShopInfo(c);
            return;
        }
        if ((((Integer) CaseInfo.getRight()) == c.getPlayer().getGender()) && (type == 30)) {
            playerCashShopInfo(c);
            return;
        }
        int err = MapleRing.createRing(iteminfo.getId(), c.getPlayer(), partnerName, msg, ((Integer) CaseInfo.getLeft()), iteminfo.getSN());
        if (err != 1) {
            playerCashShopInfo(c);
            return;
        }
        c.getPlayer().dropMessage(1, "Purchase successful.");
    }
}
