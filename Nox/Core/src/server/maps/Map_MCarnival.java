package server.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lloyd Korn
 */
public class Map_MCarnival {

    public String effectWin, effectLose, soundWin, soundLose;
    public int rewardMapWin, rewardMapLose, mobGenMax, deathCP, guardianGenMax, reactorRed, reactorBlue;
    public boolean mapDivided;
    public List<Point> posRedInfo = new ArrayList();
    public List<Point> posBlueInfo = new ArrayList();
    public List<Integer> mobs = new ArrayList();
    public List<Integer> MobCP = new ArrayList();
    public Map<Integer, Integer> Skills = new HashMap();
    public List<MCGuardian> Guardian = new ArrayList();

    public static class MCGuardian {

        public Point pos;
        public int f;
        public byte team;
    }

    public Point getPosForSummon(int team, int summoned) {
        if (team == 0) {
            if (summoned >= posBlueInfo.size()) {
                return null;
            }
            return posBlueInfo.get(summoned);
        } else {
            if (summoned >= posRedInfo.size()) {
                return null;
            }
            return posRedInfo.get(summoned);
        }
    }
}
