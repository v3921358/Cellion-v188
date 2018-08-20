package server.life;

import java.awt.Point;

public class MultiTarget {

    private Point position;

    public MultiTarget(int x, int y) {
        position = new Point(x, y);
    }

    /**
     * @return position
     */
    public Point getPosition() {
        return position;
    }

}
