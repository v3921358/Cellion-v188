package server.maps;

import java.awt.Point;

/**
 *
 * @author Lloyd Korn
 */
public class Map_BuffZone {

    public int x1, y1, x2, y2;
    public int ItemID;
    public int Interval;
    public int Duration;

    public Map_BuffZone(int x1, int y1, int x2, int y2, int Itemid, int Interval, int Duration) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.ItemID = Itemid;
        this.Interval = Interval;
        this.Duration = Duration;
    }

    public boolean isWithinPos(Point chr) {
        return checkPosition(chr);
    }

    private boolean checkPosition(Point chr) {
        return x1 <= chr.x && x2 >= chr.x && y1 <= chr.y && y2 >= chr.y;
    }
}
