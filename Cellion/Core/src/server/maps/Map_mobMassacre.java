package server.maps;

import java.util.HashMap;
import java.util.Map;
import tools.Pair;

/**
 *
 * @author Lloyd Korn
 */
public class Map_mobMassacre {

    public int totalbar;
    public byte decrease_persec;
    public byte cool_add;
    public byte miss_sub;
    public byte hit_add;
    public Map<Integer, Pair<Integer, Boolean>> eff = new HashMap();
}
