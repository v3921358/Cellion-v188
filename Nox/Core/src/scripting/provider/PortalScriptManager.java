package scripting.provider;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import scripting.PortalPlayerInteraction;
import scripting.PortalScript;
import server.MaplePortal;
import tools.LogHelper;

public class PortalScriptManager extends AbstractScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    private static PortalScript getPortalScript(final String scriptName) {
        final String portalName = String.format("portal/%s.js", scriptName);

        final Invocable iv = getInvocable(portalName, true, ScriptType.Portal);
        final ScriptEngine scriptengine = (ScriptEngine) iv;
        if (iv == null) {
            return null;
        }
        final PortalScript script = ((Invocable) scriptengine).getInterface(PortalScript.class);

        return script;
    }

    public boolean executePortalScript(final MaplePortal portal, final MapleClient c) {
        int playerMapId = c.getPlayer().getMapId(); // player's map id before portal teleportation

        final PortalScript script = getPortalScript(portal.getScriptName());

        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
            } catch (Exception ex) {
                LogHelper.INVOCABLE.get().info(String.format("Portal name %s, MapId: %d\n{}", portal.getScriptName(), playerMapId), ex);
            }
            return true;
        }
        LogHelper.UNCODED.get().info("Unhandled portal script " + portal.getScriptName() + " on map " + c.getPlayer().getMapId());
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().yellowMessage("Unhandled portal script: " + portal.getScriptName());
        }
        return false;
    }
}
