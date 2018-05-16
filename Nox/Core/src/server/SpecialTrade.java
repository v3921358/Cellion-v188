/*
 * Cellion Development
 */
package server;

import java.util.concurrent.locks.ReentrantLock;
import server.maps.objects.User;
import tools.packet.PlayerShopPacket;

/**
 * Special Trade Features
 * @author Mazen Massoud
 * 
 * @purpose Handle and perform special trade requests such as NX, Vote, Donor, and Event Points.
 */
public class SpecialTrade {
    
    /**
     * OnSpecialTradeRequest
     * 
     * 
     * @param pSender
     * @param pReceiver
     * @param sMessage
     * @return Checks if a special trade command is used, otherwise performs standard chat functions.
     */
    public static boolean OnSpecialTradeRequest(User pSender, User pReceiver, String sMessage) {
        String sSplit[]= sMessage.split(" ", 0);
        String sCommand = sSplit[0].toUpperCase();
        String sTradeType;
        
        /**
         * Any of the following commands can be disabled
         * by simply commenting out the corresponding case below.
         */
        switch (sCommand) {
            case "/OFFERNX":
            case "/OFFERVP":    
            case "/OFFERDP":    
            case "/OFFEREP":
                break;
            default:
                return false;
        }
        
        int nOffer = Integer.parseInt(sSplit[1]);
        
        if (nOffer <= 0) {
            pSender.dropMessage(-5, "Please enter an amount greater than 0.");
            return true;
        } 
        
        switch (sCommand) {
            case "/OFFERNX": 
                sTradeType = "NX";
                if (pSender.getNX() < nOffer) {
                    pSender.dropMessage(-5, "You do not have enough " + sTradeType + ".");
                    return true;
                }
                pSender.nOfferNX = nOffer;
                break; 
            case "/OFFERVP":
                sTradeType = "Vote Points";
                if (pSender.getVPoints() < nOffer) {
                    pSender.dropMessage(-5, "You do not have enough " + sTradeType + ".");
                    return true;
                }
                pSender.nOfferVP = nOffer;
                break;
            case "/OFFERDP":
                sTradeType = "Donor Points";
                if (pSender.getDPoints() < nOffer) {
                    pSender.dropMessage(-5, "You do not have enough " + sTradeType + ".");
                    return true;
                }
                pSender.nOfferDP = nOffer;
                break;
            case "/OFFEREP":
                sTradeType = "Event Points";
                if (pSender.getEPoints() < nOffer) {
                    pSender.dropMessage(-5, "You do not have enough " + sTradeType + ".");
                    return true;
                }
                pSender.nOfferEP = nOffer;
                break;
            default:
                return true;
        }
        
        String sTrade = "(Offer) " + pSender.getName() + " : " + nOffer + " " + sTradeType;
        
        pSender.getClient().SendPacket(PlayerShopPacket.shopChat(sTrade, 0));
        if (pReceiver != null) pReceiver.getClient().SendPacket(PlayerShopPacket.shopChat(sTrade, 0));
        return true;
    }
    
    /**
     * OnSpecialTradeResult
     * Performs the trade operations of any offered points.
     * 
     * @param pPlayerOne
     * @param pPlayerTwo 
     */
    public static void OnSpecialTradeResult(User pPlayerOne, User pPlayerTwo) {
        
        if (pPlayerOne.nOfferNX > 0) { // Handle Player One Offers
            pPlayerOne.gainNX(pPlayerOne.nOfferNX, false);
            pPlayerOne.yellowMessage("[Trade ->] You have sent " + pPlayerTwo.getName() + " " + pPlayerOne.nOfferNX + " NX!");
            pPlayerTwo.gainNX(pPlayerOne.nOfferNX, false);
            pPlayerTwo.yellowMessage("[Trade <-] You have received " + pPlayerOne.nOfferNX + " NX from "+ pPlayerOne.getName() + "!");
        }
        if (pPlayerTwo.nOfferNX > 0) { // Handle Player Two Offers
            pPlayerTwo.gainNX(pPlayerTwo.nOfferNX, false);
            pPlayerTwo.yellowMessage("[Trade ->] You have sent " + pPlayerOne.getName() + " " + pPlayerTwo.nOfferNX + " NX!");
            pPlayerOne.gainNX(pPlayerTwo.nOfferNX, false);
            pPlayerOne.yellowMessage("[Trade <-] You have received " + pPlayerTwo.nOfferNX + " NX from "+ pPlayerTwo.getName() + "!");
        }
        
        if (pPlayerOne.nOfferVP > 0) { // Handle Player One Offers
            pPlayerOne.setVPoints(pPlayerOne.getVPoints() - pPlayerOne.nOfferVP);
            pPlayerOne.yellowMessage("[Trade ->] You have sent " + pPlayerTwo.getName() + " " + pPlayerOne.nOfferVP + " VP!");
            pPlayerTwo.setVPoints(pPlayerTwo.getVPoints() + pPlayerOne.nOfferVP);
            pPlayerTwo.yellowMessage("[Trade <-] You have received " + pPlayerOne.nOfferVP + " VP from "+ pPlayerOne.getName() + "!");
        }
        if (pPlayerTwo.nOfferVP > 0) { // Handle Player Two Offers
            pPlayerTwo.setVPoints(pPlayerTwo.getVPoints() - pPlayerTwo.nOfferVP);
            pPlayerTwo.yellowMessage("[Trade ->] You have sent " + pPlayerOne.getName() + " " + pPlayerTwo.nOfferVP + " VP!");
            pPlayerOne.setVPoints(pPlayerOne.getVPoints() + pPlayerTwo.nOfferVP);
            pPlayerOne.yellowMessage("[Trade <-] You have received " + pPlayerTwo.nOfferVP + " VP from "+ pPlayerTwo.getName() + "!");
        }
        
        if (pPlayerOne.nOfferDP > 0) { // Handle Player One Offers
            pPlayerOne.setDPoints(pPlayerOne.getDPoints() - pPlayerOne.nOfferDP);
            pPlayerOne.yellowMessage("[Trade ->] You have sent " + pPlayerTwo.getName() + " " + pPlayerOne.nOfferDP + " DP!");
            pPlayerTwo.setDPoints(pPlayerTwo.getDPoints() + pPlayerOne.nOfferDP);
            pPlayerTwo.yellowMessage("[Trade <-] You have received " + pPlayerOne.nOfferDP + " DP from "+ pPlayerOne.getName() + "!");
        }
        if (pPlayerTwo.nOfferDP > 0) { // Handle Player Two Offers
            pPlayerTwo.setDPoints(pPlayerTwo.getDPoints() - pPlayerTwo.nOfferDP);
            pPlayerTwo.yellowMessage("[Trade ->] You have sent " + pPlayerOne.getName() + " " + pPlayerTwo.nOfferDP + " DP!");
            pPlayerOne.setDPoints(pPlayerOne.getDPoints() + pPlayerTwo.nOfferDP);
            pPlayerOne.yellowMessage("[Trade <-] You have received " + pPlayerTwo.nOfferDP + " DP from "+ pPlayerTwo.getName() + "!");
        }
        
        if (pPlayerOne.nOfferEP > 0) { // Handle Player One Offers
            pPlayerOne.setEPoints(pPlayerOne.getEPoints() - pPlayerOne.nOfferEP);
            pPlayerOne.yellowMessage("[Trade ->] You have sent " + pPlayerTwo.getName() + " " + pPlayerOne.nOfferEP + " EP!");
            pPlayerTwo.setEPoints(pPlayerTwo.getEPoints() + pPlayerOne.nOfferEP);
            pPlayerTwo.yellowMessage("[Trade <-] You have received " + pPlayerOne.nOfferEP + " EP from "+ pPlayerOne.getName() + "!");
        }
        if (pPlayerTwo.nOfferEP > 0) { // Handle Player Two Offers
            pPlayerTwo.setEPoints(pPlayerTwo.getEPoints() - pPlayerTwo.nOfferEP);
            pPlayerTwo.yellowMessage("[Trade ->] You have sent " + pPlayerOne.getName() + " " + pPlayerTwo.nOfferEP + " EP!");
            pPlayerOne.setEPoints(pPlayerOne.getEPoints() + pPlayerTwo.nOfferEP);
            pPlayerOne.yellowMessage("[Trade <-] You have received " + pPlayerTwo.nOfferEP + " EP from "+ pPlayerTwo.getName() + "!");
        }
        
        OnSpecialTradeReset(pPlayerOne, pPlayerTwo); // Reset Offers
    }
    
    /**
     * OnSpecialTradeReset
     * Reset all special trade offers for both users.
     * 
     * @param pPlayerOne
     * @param pPlayerTwo 
     */
    public static void OnSpecialTradeReset(User pPlayerOne, User pPlayerTwo) {
        
        pPlayerOne.nOfferNX = 0;
        pPlayerOne.nOfferVP = 0;
        pPlayerOne.nOfferDP = 0;
        pPlayerOne.nOfferEP = 0;
        
        pPlayerTwo.nOfferNX = 0;
        pPlayerTwo.nOfferVP = 0;
        pPlayerTwo.nOfferDP = 0;
        pPlayerTwo.nOfferEP = 0;
    }
    
    /**
     * OnSpecialTradeNotification
     * Notify the player of the available special trade commands.
     * 
     * @param pPlayer 
     */
    public static void OnPlayerOneNotification(User pPlayer) {
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" Trade Commands", 1));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offernx <amount> : NX ", 1));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offervp <amount> : Vote Points", 1));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offerdp <amount> : Donor Points", 1));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offerep <amount> : Event Points", 1));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" ", 1));
    }
    
    public static void OnPlayerTwoNotification(User pPlayer) {
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" Trade Commands", 0));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offernx <amount> : NX", 0));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offervp <amount> : Vote Points", 0));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offerdp <amount> : Donor Points", 0));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" /offerep <amount> : Event Points", 0));
        pPlayer.getClient().SendPacket(PlayerShopPacket.shopChat(" ", 0));
    }
}
