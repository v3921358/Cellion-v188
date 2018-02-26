package server.maps;

/**
 * Defines the properties of this maple map item
 *
 * @author
 */
public enum MapleMapItemProperties {

    IsBossDrop(1),
    IsEliteBossDrop(2),
    IsPickpocketDrop(3),
    IsPlayerDrop(4),
    IsRandomDrop(5),
    IsCollisionPickUp(6),
    IsNoMove(7),
    IsGlobalDrop(8), // if the item is dropped globally by all monsters 
    ;

    private final int numShifts;

    private MapleMapItemProperties(int numShifts) {
        this.numShifts = numShifts;
    }

    public int getBitvalue() {
        return numShifts;
    }
}
