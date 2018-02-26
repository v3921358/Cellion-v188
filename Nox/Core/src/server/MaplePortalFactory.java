package server;

import java.awt.Point;

/**
 *
 * @author Lloyd Korn
 */
public class MaplePortalFactory {

    private static int nextDoorPortal = 0x80;

    public static MaplePortal makePortal(int portalid, int type, String pn, int toMap, String toName, int posX, int posY, String script) {
        final MaplePortal ret = new MaplePortal(type);

        ret.setName(pn);
        ret.setTarget(toName);
        ret.setTargetMapId(toMap);
        ret.setPosition(new Point(posX, posY));
        if (script != null && script.equals("")) {
            script = null;
        }
        ret.setScriptName(script);

        if (ret.getType() == MaplePortal.DOOR_PORTAL) {
            ret.setId(nextDoorPortal);
            nextDoorPortal++;
        } else {
            ret.setId(portalid);
        }
        return ret;
    }
}
