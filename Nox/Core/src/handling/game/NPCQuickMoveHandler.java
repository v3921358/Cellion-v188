package handling.game;

import client.ClientSocket;
import constants.QuickMove;
import constants.ServerConstants;
import java.util.LinkedList;
import java.util.List;
import scripting.provider.NPCScriptManager;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;

/**
 * Quick Move
 * @author 
 */
public class NPCQuickMoveHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        final int nNPC = iPacket.DecodeInt();
        
        if (pPlayer.hasBlockedInventory() || pPlayer.isInBlockedMap() || pPlayer.getLevel() < 10) {
            return;
        }
        
        if (pPlayer.getMapId() == ServerConstants.JAIL_MAP) {
            pPlayer.dropMessage(5, "Sorry, you may not do that here.");
            pPlayer.completeDispose();
            return;
        }
        
        for (QuickMove pQM : QuickMove.values()) {
            if (pQM.getMap() != pPlayer.getMapId()) {
                List<QuickMove.QuickMoveNPC> aQM = new LinkedList();
                int nFlag = pQM.getNPCFlag();
                for (QuickMove.QuickMoveNPC pNPC : QuickMove.QuickMoveNPC.values()) {
                    if ((nFlag & pNPC.getValue()) != 0 && pNPC.getId() == nNPC) {
                        NPCScriptManager.getInstance().start(c, nNPC, null);
                        break;
                    }
                }
            }
        }
    }
}
