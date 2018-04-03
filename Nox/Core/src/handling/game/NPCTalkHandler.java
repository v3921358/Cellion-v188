package handling.game;

import client.MapleClient;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import scripting.provider.NPCScriptManager;
import server.maps.objects.User;
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
        User pPlayer = c.getPlayer();
        final MapleNPC pNpc = pPlayer.getMap().getNPCByOid(iPacket.DecodeInteger());

        if (pPlayer == null || pPlayer.getMap() == null || pNpc == null || pPlayer.hasBlockedInventory()) {
            return;
        }

        if (pPlayer.isIntern()) { // Useful debug!
            pPlayer.dropMessage(5, "[NPC Debug] Script ID : " + pNpc.getId());
        }

        if (NPCScriptManager.getInstance().getCM(c) != null) { // Meme auto dispose when clicking an NPC.
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.write(CWvsContext.enableActions());
        }

        ReentrantLock safetyLock = new ReentrantLock(); // Lock to avoid running the script twice and receiving a runtime error.
        if (NPCScriptManager.getInstance().hasScript(c, pNpc.getId(), null)) { // NPC Script > Before Shops
            safetyLock.lock();
            try {
                NPCScriptManager.getInstance().start(c, pNpc.getId(), null);
                Thread.currentThread().sleep(15);
            } catch (InterruptedException ex) {
                System.err.println("[Debug] Interrupted Exception at NPC Script Runtime.");
            } finally {
                safetyLock.unlock();
            }
        } else if (pNpc.hasShop()) {
            pPlayer.setConversation(User.MapleCharacterConversationType.NPC_Or_Quest);
            pNpc.sendShop(c);
        }
    }
}
