package server.maps;

import java.util.List;

/**
 *
 * @author Lloyd Korn
 */
public class Map_MaplePlatform {

    public String name;
    public int start, speed, x1, y1, x2, y2, r;
    public List<Integer> SN;

    public Map_MaplePlatform(String name, int start, int speed, int x1, int y1, int x2, int y2, int r, List<Integer> SN) {
        this.name = name;
        this.start = start;
        this.speed = speed;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.r = r;
        this.SN = SN;
    }
}
