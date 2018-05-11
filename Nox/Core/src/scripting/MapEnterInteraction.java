package scripting;

import client.Client;

/**
 *
 * @author
 */
public class MapEnterInteraction extends AbstractPlayerInteraction {

    private final int LastMapId;

    public MapEnterInteraction(final Client c, int LastMapId) {
        super(c, LastMapId, 0, null);
        this.LastMapId = LastMapId;
    }

    /**
     * The last map the character is in before executing this map script
     *
     * @return LastMapId
     */
    public int getLastMapId() {
        return LastMapId;
    }

    /**
     * Starts a map effect
     *
     * @param msg The message to show
     * @param itemId The effect itemid
     * @param bypass Should it override existing effects?
     */
    public final void startMapEffect(final String msg, final int itemId, boolean bypass) {
        c.getPlayer().getMap().startMapEffect(msg, itemId, bypass);
    }
}
