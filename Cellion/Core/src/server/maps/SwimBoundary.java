package server.maps;

import java.awt.Point;

/**
 *
 * @author Lloyd Korn
 */
public class SwimBoundary {

    private final int SpecialSwim_x1, SpecialSwim_x2, SpecialSwim_y1, SpecialSwim_y2;

    public SwimBoundary(final int x1, final int x2, final int y1, final int y2) {
        this.SpecialSwim_x1 = x1 != 0 ? x1 - 1000 : 0;
        this.SpecialSwim_x2 = x2 != 0 ? x1 + 1000 : 0;
        this.SpecialSwim_y1 = y1 != 0 ? y1 - 1000 : 0;
        this.SpecialSwim_y2 = y2 != 0 ? y2 + 1000 : 0;
    }

    public final boolean isWithinSwimBoundary(final Point pos) {
        if (SpecialSwim_x1 <= pos.x && SpecialSwim_x2 >= pos.x && SpecialSwim_y1 <= pos.y && SpecialSwim_y2 >= pos.y) {
            return true;
        }
        return false;
    }
}
