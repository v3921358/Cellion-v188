/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.auction;

import client.ClientSocket;
import handling.cashshop.CashShopPurchase;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.LogHelper;

/**
 *
 * @author William
 */
public class AuctionRequestHandler implements ProcessPacket<ClientSocket> {
    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        final int action = iPacket.DecodeInt();
        chr.dropMessage(5, "[AuctionRequestHandler] action: "+action);
        switch (action) {
            case 0: // Initial load (list items)
                chr.dropMessage(1, "[AuctionRequestHandler] INIT");
                break;
            case 1: // Exit
                AuctionOperation.LeaveAuction(iPacket, c, c.getPlayer());
                chr.dropMessage(1, "[AuctionRequestHandler] LEAVE");
                break;
            case 10: // Sell item
                chr.dropMessage(1, "[AuctionRequestHandler] SELL ITEM");
                break;
            case 40: // Search action
                int searchCategory = iPacket.DecodeInt(); // 0 = armor, 1 = weapons, 2 = use, 3 = cash, 4 = etc
                short searchItemName1 = iPacket.DecodeShort();
                String searchItemName = "";
                if (searchItemName1 >= 1) {
                    searchItemName = iPacket.DecodeString();
                }
                int itemType1 = iPacket.DecodeInt();
                int itemType2 = iPacket.DecodeInt();
                int minLevel = iPacket.DecodeInt();
                int maxLevel = iPacket.DecodeInt();
                long minMeso = iPacket.DecodeLong();
                long maxMeso = iPacket.DecodeLong();
                int potentialType = iPacket.DecodeInt();
                byte andor = iPacket.DecodeByte();
                chr.dropMessage(1, "[AuctionRequestHandler] SEARCH | Category: "+searchCategory+" / itemName: "+searchItemName);
                break;
            case 50: // Sell tab
            case 51: // Transactions tab
                chr.dropMessage(1, "[AuctionRequestHandler] "+action+" Tab");
                break;
            default:
                System.out.println("[AuctionRequestHandler] New Operation Found (" + action + ")");
                LogHelper.GENERAL_EXCEPTION.get().info("[AuctionRequestHandler] Unknown action type: " + action);
                break;
        }
    }
}
