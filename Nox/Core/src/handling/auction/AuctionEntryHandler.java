package handling.auction;

import client.ClientSocket;
import handling.world.InterServerHandler;
import net.InPacket;
import net.ProcessPacket;
/**
 *
 * @author
 */
public class AuctionEntryHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        InterServerHandler.enterAuctionHouse(c, c.getPlayer());
    }

}
