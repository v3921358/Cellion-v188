/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.cashshop;

import client.MapleClient;
import client.inventory.Item;
import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CSPacket;
import netty.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author Novak
 */
public final class CashShopPurchase implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final MapleCharacter chr = c.getPlayer();
        final int action = iPacket.DecodeByte();
        purchaseType actionType = purchaseType.UNK;
        for (final purchaseType availableType : purchaseType.values()) {
            if (availableType.getType() == action) {
                actionType = availableType;
            }
        }
        switch (actionType) {
            case COUPON:
                CashCouponHandler.useCoupon(iPacket, c);
                break;
            case ITEM_PURCHASE:
                ItemPurchase.BuyNormalItem(iPacket, c, chr);
                break;
            case EQUIP_PURCHASE:
                ItemPurchase.BuyEquipItem(iPacket, c, chr);
                break;
            case GIFT_PACKAGE:
                PurchasePackage.BuyGiftPackage(iPacket, c, chr);
                break;
            case WISHLIST_INCREASE: //one of these should be wishlist related.
            case INVENTORY_INCREASE:
                SlotIncrease.inventory(iPacket, c, chr);
                break;
            case STORAGE_INCREASE:
                SlotIncrease.storage(iPacket, c, chr);
                break;
            case CHARSLOT_INCREASE:
                SlotIncrease.charSlots(iPacket, c, chr);
                break;
            case PENDANTSLOT_INCREASE:
                SlotIncrease.pendantSlots(iPacket, c, chr);
                break;
            case INVENTORY_RETRIEVE:
                CSInventoryAction.RetrieveItem(iPacket, c, chr);
                break;
            case INVENTORY_ADD:
                CSInventoryAction.AddItem(iPacket, c, chr);
                break;
            case RING_PURCHASE_FRIEND:
            case RING_PURCHASE_COUPLE:
                ItemPurchase.BuyRings(iPacket, c, chr, actionType.getType());
                break;
            case PACKAGE:
                PurchasePackage.Regular(iPacket, c, chr);
                break;
            case QUEST_ITEM_1:
            case QUEST_ITEM_2:
                QuestPurchase.buyItem(iPacket, c, chr);
                break;
            case PURCHASE_LOG_UPDATE:
                c.write(CSPacket.updatePurchaseRecord());
                break;
            case RANDOM_BOX:
                c.write(CSPacket.sendRandomBox((int) iPacket.DecodeLong(), new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
                break;
            case FARM_GEM_UPDATE://UNSURE!
                FarmItemPurchase.gems(iPacket, c, chr);
                break;
            default:
                System.out.println("[Cash Shop Debug] New Operation Found (" + actionType + ")");
                LogHelper.GENERAL_EXCEPTION.get().info("[CashShopPurchase] Unknow action type: " + actionType);
                c.write(CSPacket.sendCSFail(0));
                break;
        }
        try {
            c.getPlayer().getCashInventory().save();
        } catch (SQLException ex) {
            Logger.getLogger(CashShopPurchase.class.getName()).log(Level.SEVERE, null, ex);
        }
        playerCashShopInfo(c);
    }

    private enum purchaseType {
        COUPON(0),
        ITEM_PURCHASE(2),
        EQUIP_PURCHASE(3),
        GIFT_PACKAGE(4),
        WISHLIST_INCREASE(5),
        INVENTORY_INCREASE(6),
        STORAGE_INCREASE(7),
        CHARSLOT_INCREASE(8),
        PENDANTSLOT_INCREASE(10),
        INVENTORY_RETRIEVE(15),
        INVENTORY_ADD(16),
        RING_PURCHASE_FRIEND(33),
        RING_PURCHASE_COUPLE(39),
        PACKAGE(34),
        QUEST_ITEM_1(35),
        QUEST_ITEM_2(99),
        PURCHASE_LOG_UPDATE(48),
        RANDOM_BOX(91),
        FARM_GEM_UPDATE(101),
        UNK(1337);
        private final int type;

        private purchaseType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
