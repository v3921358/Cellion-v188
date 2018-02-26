package server.maps;

import java.util.LinkedList;
import java.util.List;
import server.Randomizer;

/**
 *
 * @author Lloyd Korn
 */
public class EscortMobFootHoldData {

    public short NodeInfo, nextNode = -1;
    // Basic info
    public short key, x, y, attr;
    // Edge
    public List<Short> edge0 = new LinkedList();
    //   public List<Short> edge1 = new LinkedList();
    // stopInfo
    public byte stopDuration, sayTic;
    public int chatBalloon;
    public boolean isRepeat, isWeather, isRandom;
    public List<String> act = new LinkedList();
    public List<String> say = new LinkedList();

    public String getRandomSay() {
        if (say == null) {
            return null;
        }
        return say.get(Randomizer.nextInt(say.size()));
    }
}
