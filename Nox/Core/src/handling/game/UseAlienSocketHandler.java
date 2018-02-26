package handling.game;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import java.util.ArrayList;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class UseAlienSocketHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item alienSocket = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) iPacket.DecodeShort());
        final int alienSocketId = iPacket.DecodeInteger();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeShort());

        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.write(CWvsContext.inventoryOperation(true, new ArrayList<>()));
            return;
        }
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() < 1) { // Used before
            c.write(CSPacket.useAlienSocket(true));
            eqq.setSocket1(0);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1, false);
            c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.write(CSPacket.useAlienSocket(false));
    }
}
