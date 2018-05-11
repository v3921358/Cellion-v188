package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.InPacket;
import server.maps.objects.User;
import server.maps.objects.Extractor;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class ExtractorHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        final int itemId = iPacket.DecodeInt();
        final int fee = iPacket.DecodeInt();
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);

        if (toUse == null || toUse.getQuantity() < 1
                || itemId / 10000 != 304
                || fee <= 0 || fee > 1_000_000
                || chr.getExtractor() != null || !chr.getMap().getSharedMapResources().town) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        chr.setExtractor(new Extractor(chr, itemId, fee, chr.getFh())); //no clue about time left
        chr.getMap().spawnExtractor(chr.getExtractor());

        //expiry date ..
        //MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, toUse.getPosition(), (short) 1, false);
    }

}
