/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.MapleCharacterUtil;
import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.CashItemFactory;
import server.CashItemInfo;
import server.maps.objects.User;
import tools.Triple;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class PurchasePackage {

    public static void BuyGiftPackage(InPacket iPacket, Client c, User chr) {
        iPacket.Skip(1);
        iPacket.DecodeString(); // pic - Has to be CHECKED!
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());
        String partnerName = iPacket.DecodeString();
        String msg = iPacket.DecodeString();
        if (iteminfo == null || c.getPlayer().getCSPoints(1) < iteminfo.getPrice() || msg.length() > 73 || msg.length() < 1) { //dont want packet editors gifting random stuff =P
            c.SendPacket(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        }
        Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
        if (info == null || info.getLeft() <= 0 || info.getLeft() == c.getPlayer().getId() || info.getMid() == c.getAccID()) {
            c.SendPacket(CSPacket.sendCSFail(0xA2)); //9E v75
            playerCashShopInfo(c);
        } else if (!iteminfo.genderEquals(info.getRight())) {
            c.SendPacket(CSPacket.sendCSFail(0xA3));
            playerCashShopInfo(c);
        } else {
            //get the packets for that
            c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), msg, iteminfo.getSN(), MapleInventoryIdentifier.getInstance());
            c.getPlayer().modifyCSPoints(1, -iteminfo.getPrice(), false);
            c.SendPacket(CSPacket.sendGift(iteminfo.getPrice(), iteminfo.getId(), iteminfo.getCount(), partnerName, false));
        }
    }

    public static void Regular(InPacket iPacket, Client c, User chr) {
        iPacket.Skip(1);
        int unk = iPacket.DecodeInt();//is1
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());
        int itemPrice = iPacket.DecodeInt();
        List<Integer> ccc = null;
        if (iteminfo != null) {
            ccc = CashItemFactory.getInstance().getPackageItems(iteminfo.getId());
        }
        if (iteminfo == null || ccc == null || c.getPlayer().getCSPoints(itemPrice) < iteminfo.getPrice()) {
            c.SendPacket(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        } else if (!iteminfo.genderEquals(c.getPlayer().getGender())) {
            c.SendPacket(CSPacket.sendCSFail(0xA6));
            playerCashShopInfo(c);
            return;
        } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
            c.SendPacket(CSPacket.sendCSFail(0xB1));
            playerCashShopInfo(c);
            return;
        }
        Map<Integer, Item> ccz = new HashMap<>();
        for (int i : ccc) {
            final CashItemInfo cii = CashItemFactory.getInstance().getSimpleItem(i);
            if (cii == null) {
                continue;
            }
            Item itemz = c.getPlayer().getCashInventory().toItem(cii);
            if (itemz == null || itemz.getUniqueId() <= 0) {
                continue;
            }
            for (int iz : GameConstants.cashBlock) {
                if (itemz.getItemId() == iz) {
                }
            }
            ccz.put(i, itemz);
            c.getPlayer().getCashInventory().addToInventory(itemz);
        }
        chr.modifyCSPoints(itemPrice, -iteminfo.getPrice(), false);
        c.SendPacket(CSPacket.showBoughtCSPackage(ccz, c.getAccID()));
    }
}
