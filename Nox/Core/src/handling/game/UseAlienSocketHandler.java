package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import java.util.ArrayList;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CSPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseAlienSocketHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item alienSocket = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) iPacket.DecodeShort());
        final int alienSocketId = iPacket.DecodeInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeShort());

        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.SendPacket(WvsContext.inventoryOperation(true, new ArrayList<>()));
            return;
        }
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() < 1) { // Used before
            c.SendPacket(CSPacket.useAlienSocket(true));
            eqq.setSocket1(0);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1, false);
            c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.SendPacket(CSPacket.useAlienSocket(false));
    }
}
