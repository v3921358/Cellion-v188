package handling.game;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleExtractor;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class ExtractorHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final MapleCharacter chr = c.getPlayer();
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.write(CWvsContext.enableActions());
            return;
        }

        final int itemId = iPacket.DecodeInteger();
        final int fee = iPacket.DecodeInteger();
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);

        if (toUse == null || toUse.getQuantity() < 1
                || itemId / 10000 != 304
                || fee <= 0 || fee > 1_000_000
                || chr.getExtractor() != null || !chr.getMap().getSharedMapResources().town) {
            c.write(CWvsContext.enableActions());
            return;
        }
        chr.setExtractor(new MapleExtractor(chr, itemId, fee, chr.getFh())); //no clue about time left
        chr.getMap().spawnExtractor(chr.getExtractor());

        //expiry date ..
        //MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, toUse.getPosition(), (short) 1, false);
    }

}
