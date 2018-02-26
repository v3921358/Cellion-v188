package handling.game;

import client.MapleClient;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import scripting.provider.NPCScriptManager;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleNPC;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Mazen Massoud
 */
public class NPCTalkHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter oPlayer = c.getPlayer();
        final MapleNPC oNpc = oPlayer.getMap().getNPCByOid(iPacket.DecodeInteger());
        
        if (oPlayer == null || oPlayer.getMap() == null || oNpc == null || oPlayer.hasBlockedInventory()) {
            return;
        }
        
        if (oPlayer.isIntern()) { // Useful debug!
            oPlayer.dropMessage(5, "[NPC Debug] Script ID : " + oNpc.getId());
        }
        
        if (NPCScriptManager.getInstance().getCM(c) != null) { // Meme auto dispose when clicking an NPC.
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());
        }

        ReentrantLock safetyLock = new ReentrantLock(); // Lock to avoid running the script twice and receiving a runtime error.
        if (NPCScriptManager.getInstance().hasScript(c, oNpc.getId(), null)) { // NPC Script > Before Shops
            safetyLock.lock();
            try {
                NPCScriptManager.getInstance().start(c, oNpc.getId(), null);
                Thread.currentThread().sleep(15);
            } catch (InterruptedException ex) {
                System.err.println("[Debug] Interrupted Exception at NPC Script Runtime.");
            } finally {
                safetyLock.unlock();
            }
        } else if (oNpc.hasShop()) {
            oPlayer.setConversation(MapleCharacter.MapleCharacterConversationType.NPC_Or_Quest);
            oNpc.sendShop(c);
        }
    }
}
