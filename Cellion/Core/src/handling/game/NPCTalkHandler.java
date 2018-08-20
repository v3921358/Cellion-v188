package handling.game;

import client.ClientSocket;
import java.util.concurrent.locks.ReentrantLock;
import scripting.provider.NPCScriptManager;
import server.maps.objects.User;
import server.life.NPCLife;
import net.InPacket;
import net.ProcessPacket;

/**
 * NPCTalkHandler
 * @author Mazen Massoud
 */
public class NPCTalkHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        if (pPlayer == null || pPlayer.getMap() == null || pPlayer.hasBlockedInventory()) return;
        pPlayer.completeDispose();                                              // Meme auto dispose when clicking an NPC.
        
        final NPCLife pNpc = pPlayer.getMap().getNPCByOid(iPacket.DecodeInt());
        if (pNpc == null) return;
        
        if (pPlayer.isIntern()) pPlayer.dropMessage(5, "[NPC Debug] Script ID : " + pNpc.getId());
        
        ReentrantLock safetyLock = new ReentrantLock();                         // Lock to avoid running the script twice and receiving a runtime error.
        if (NPCScriptManager.getInstance().hasScript(c, pNpc.getId(), null)) {  // NPC Script > Before Shops
            safetyLock.lock();
            try {
                NPCScriptManager.getInstance().start(c, pNpc.getId(), null);
                Thread.currentThread().sleep(20);
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
