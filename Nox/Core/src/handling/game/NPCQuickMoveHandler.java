package handling.game;

import client.MapleClient;
import constants.QuickMove;
import java.util.LinkedList;
import java.util.List;
import scripting.provider.NPCScriptManager;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class NPCQuickMoveHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final int npcid = iPacket.DecodeInt();
        if (c.getPlayer().hasBlockedInventory() || c.getPlayer().isInBlockedMap() || c.getPlayer().getLevel() < 10) {
            return;
        }
        for (QuickMove qm : QuickMove.values()) {
            if (qm.getMap() != c.getPlayer().getMapId()) {
                List<QuickMove.QuickMoveNPC> qmn = new LinkedList();
                int npcs = qm.getNPCFlag();
                for (QuickMove.QuickMoveNPC npc : QuickMove.QuickMoveNPC.values()) {
                    if ((npcs & npc.getValue()) != 0 && npc.getId() == npcid) {
                        NPCScriptManager.getInstance().start(c, npcid, null);
                        break;
                    }
                }
            }
        }
    }

}
