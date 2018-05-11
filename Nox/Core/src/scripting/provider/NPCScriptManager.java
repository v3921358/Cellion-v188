package scripting.provider;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleClient;
import scripting.AbstractPlayerInteraction;
import scripting.NPCConversationManager;
import scripting.NpcConversationStatelessManager;
import scripting.NpcConversationType;
import server.maps.objects.User.MapleCharacterConversationType;
import server.quest.Quest;
import tools.LogHelper;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> CMS = new WeakHashMap<>();
    private static final NPCScriptManager INSTANCE = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return INSTANCE;
    }

    public boolean hasScript(final MapleClient c, final int npc, String script) {
        Invocable iv = getInvocable("npc/" + npc + ".js", true, ScriptType.NPC);
        if (script != null && !script.isEmpty()) {
            iv = getInvocable("npc/" + script + ".js", true, ScriptType.NPC);
        }
        return iv != null;
    }

    public void start(MapleClient c, int npc, String script) {
        if (!CMS.containsKey(c)) {//Incase an npc is opened without the player being disposed.
            dispose(c);
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!CMS.containsKey(c) && c.canClickNPC()) {
                // Multiple paths, in order to support legacy NPC crap
                final String[] paths = {
                    String.format("npc/%d.js", npc), // by npcID
                    String.format("npc/%s.js", script), // by NPC script
                    String.format("npc_stateless/%d.js", npc), // stateless NPC
                    String.format("npc_stateless/%s.js", script), // stateless NPC
                };

                Invocable iv = null;
                boolean isStatelessNPC = false;

                for (int i = 0; i < paths.length; i++) {
                    final String s = paths[i];
                    iv = getInvocable(s, true, ScriptType.NPC); //safe disposal
                    if (iv != null) {
                        isStatelessNPC = i >= 2;
                        break;
                    }
                }
                if (iv == null) {
                    dispose(c); // script not found
                    return;
                }

                final ScriptEngine scriptengine = (ScriptEngine) iv;

                AbstractPlayerInteraction cm_npc;
                if (!isStatelessNPC) {
                    cm_npc = new NPCConversationManager(c, npc, -1, script, NpcConversationType.Npc, iv);
                } else {
                    cm_npc = new NpcConversationStatelessManager(c, npc, -1, script, NpcConversationType.Npc, iv);
                }
                scriptengine.put("cm", cm_npc);
                CMS.put(c, (NPCConversationManager) cm_npc);

                c.getPlayer().setConversation(MapleCharacterConversationType.NPC_Or_Quest);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    if (!isStatelessNPC) {
                        try {
                            iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                        } catch (NoSuchMethodException nsme_) {
                            dispose(c);
                        }
                    } else {
                        dispose(c);
                    }
                }
            }
        } catch (Exception e) {
            LogHelper.INVOCABLE.get().info("Error executing NPC script, NPC ID : " + npc + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = CMS.get(c);
            if (cm == null || cm.getLastChatType() != NPCChatType.NULL) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
//                    System.err.println("mode " + mode);
//                    System.err.println("type " + type );
//                    System.err.println("sel "+ selection);
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final ScriptException | NoSuchMethodException e) {
                dispose(c);
                LogHelper.INVOCABLE.get().info("Error executing NPC script, NPC ID : " + cm.getNpc() + ":\n{}", e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(MapleClient c, int npc, int quest) {
        if (!Quest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!CMS.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", true, ScriptType.Quest);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, NpcConversationType.StartQuest, iv);
                CMS.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(MapleCharacterConversationType.NPC_Or_Quest);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            LogHelper.INVOCABLE.get().info("Error executing Quest script, QUEST ID : " + quest + "NPC ID" + npc + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void startQuest(MapleClient c, byte mode, byte type, int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = CMS.get(c);
        if (cm == null || cm.getLastChatType() != NPCChatType.NULL) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            LogHelper.INVOCABLE.get().info("Error executing Quest script, QUEST ID : " + cm.getQuest() + "NPC ID" + cm.getNpc() + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void endQuest(MapleClient c, int npc, int quest, boolean customEnd) {
        if (!customEnd && !Quest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!CMS.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", true, ScriptType.Quest);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, null, NpcConversationType.EndQuest, iv);
                CMS.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(MapleCharacterConversationType.NPC_Or_Quest);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            }
        } catch (ScriptException | NoSuchMethodException e) {
            LogHelper.INVOCABLE.get().info("Error executing Quest script, QUEST ID : " + quest + "NPC ID" + npc + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void endQuest(MapleClient c, byte mode, byte type, int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = CMS.get(c);
        if (cm == null || cm.getLastChatType() != NPCChatType.NULL) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (ScriptException | NoSuchMethodException e) {
            LogHelper.INVOCABLE.get().info("Error executing Quest script, QUEST ID : " + cm.getQuest() + "NPC ID" + cm.getNpc() + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void startItemScript(MapleClient c, int npc, String script) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!CMS.containsKey(c) && c.canClickNPC()) {
                final Invocable iv = getInvocable("item/" + script + ".js", true, ScriptType.Item);
                if (iv == null) {
                    LogHelper.GENERAL_EXCEPTION.get().info("New scripted item : " + script);
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, script, NpcConversationType.Npc, iv);
                CMS.put(c, cm);
                scriptengine.put("im", cm);
                c.getPlayer().setConversation(MapleCharacterConversationType.NPC_Or_Quest);
                c.setClickedNPC();
                iv.invokeFunction("use");
            }
        } catch (final ScriptException | NoSuchMethodException e) {
            LogHelper.INVOCABLE.get().info("Error executing Item script, SCRIPT : " + script + ":\n{}", e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void dispose(MapleClient c) {
        final NPCConversationManager npccm = CMS.get(c);
        if (npccm != null) {
            CMS.remove(c);
            if (npccm.getType() == NpcConversationType.Npc) {
                c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                c.removeScriptEngine("scripts/npc/" + npccm.getScript() + ".js");
                c.removeScriptEngine("scripts/npc/notcoded.js");
                c.removeScriptEngine("scripts/npc_stateless/" + npccm.getNpc());
                c.removeScriptEngine("scripts/npc_stateless/" + npccm.getScript());
            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == MapleCharacterConversationType.NPC_Or_Quest) {
            c.getPlayer().setConversation(MapleCharacterConversationType.None);
        }
    }

    public NPCConversationManager getCM(MapleClient c) {
        return CMS.get(c);
    }
}
