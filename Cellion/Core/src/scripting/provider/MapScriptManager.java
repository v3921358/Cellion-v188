package scripting.provider;

import client.ClientSocket;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import scripting.MapEnterInteraction;
import scripting.MapScript;
import server.maps.MapScriptMethods;
import tools.LogHelper;

/**
 *
 * @author
 */
public class MapScriptManager extends AbstractScriptManager {

    private static MapScript getMapScript(final String scriptName, boolean isFirstUserEnter, boolean isFieldSet) {
        final String portalName;
        if (isFieldSet) {
            portalName = String.format("map/fieldSet/%s.js", scriptName);
        } else if (isFirstUserEnter) {
            portalName = String.format("map/onFirstUserEnter/%s.js", scriptName);
        } else {
            portalName = String.format("map/onUserEnter/%s.js", scriptName);
        }

        final Invocable iv = getInvocable(portalName, true, ScriptType.Maps);
        final ScriptEngine scriptengine = (ScriptEngine) iv;
        if (iv == null) {
            return null;
        }
        final MapScript script = ((Invocable) scriptengine).getInterface(MapScript.class);

        return script;
    }

    public static boolean executeMapScript(final ClientSocket c, String scriptName, boolean isFirstUserEnter, int LastMapId) {
        final MapScript script = getMapScript(scriptName, isFirstUserEnter, false);

        if (script != null) {
            script.enter(new MapEnterInteraction(c, LastMapId));
            return true;
        }
        // try to execute hard coded crap now, this will be temporary until we fixed it
        boolean ishardcodedScriptExecuted;
        if (isFirstUserEnter) {
            ishardcodedScriptExecuted = MapScriptMethods.startScript_FirstUser(c, scriptName);
        } else {
            ishardcodedScriptExecuted = MapScriptMethods.startScript_User(c, scriptName);
        }
        if (!ishardcodedScriptExecuted) {
            LogHelper.UNCODED.get().info("Unhandled (" + (isFirstUserEnter ? "onFirstUserEnter" : "onUserEnter") + ") map script " + scriptName + " on map " + c.getPlayer().getMapId());
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().yellowMessage("Unhandled (" + (isFirstUserEnter ? "onFirstUserEnter" : "onUserEnter") + ") map script " + scriptName + " on map " + c.getPlayer().getMapId());
            }
        }
        return false;
    }

    public static boolean executeMapScript_FieldSet(final ClientSocket c, String scriptName, int LastMapId) {
        final MapScript script = getMapScript(scriptName, false, true);

        if (script != null) {
            script.enter(new MapEnterInteraction(c, LastMapId));
            return true;
        }
        LogHelper.UNCODED.get().info("Unhandled fieldSet map script " + scriptName + " on map " + c.getPlayer().getMapId());
        return false;
    }
}
