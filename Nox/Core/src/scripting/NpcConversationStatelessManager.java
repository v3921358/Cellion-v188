package scripting;

import client.Client;
import javax.script.Invocable;

/**
 *
 * @author Lloyd Korn
 */
public class NpcConversationStatelessManager extends AbstractPlayerInteraction {

    private String getText;
    private final NpcConversationType type; // -1 = NPC, 0 = start quest, 1 = end quest
    private final Invocable iv;

    public NpcConversationStatelessManager(Client c, int npc, int questid, String npcscript, NpcConversationType type, Invocable iv) {
        super(c, npc, questid, npcscript);
        this.type = type;
        this.iv = iv;
    }
}
