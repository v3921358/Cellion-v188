/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.packet;

import client.ClientSocket;
import net.OutPacket;
import service.SendPacketOpcode;

/**
 *
 * @author William
 */
public class AuctionPacket {
    
    public static OutPacket enterAuction(ClientSocket c) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetAuctionField.getValue());
        long time = System.currentTimeMillis();
        PacketHelper.addCharacterInfo(oPacket, c.getPlayer());
        oPacket.EncodeLong(PacketHelper.getTime(time));

        return oPacket;
    }
}
