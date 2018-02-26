package server.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lloyd Korn
 */
public class EscortMobFootHold {

    public short StartingData, EndingData;
    public int mapid;
    private Map<Short, EscortMobFootHoldData> Data = new HashMap();

    public EscortMobFootHold(int mapid) {
        this.mapid = mapid;
    }

    public void addInfo(EscortMobFootHoldData data_) {
        Data.put(data_.NodeInfo, data_);
    }

    public Collection<EscortMobFootHoldData> getInfos() {
        return Data.values();
    }

    public EscortMobFootHoldData getNode(final int index) {
        int i = 1;
        for (EscortMobFootHoldData x : getInfos()) {
            if (i == index) {
                return x;
            }
            i++;
        }
        return null;
    }

    public boolean isLastInfo(final int index) {
        return index == Data.size();
    }

    private short getNextNode(final EscortMobFootHoldData mni) {
        if (mni == null) {
            return -1;
        }
        addInfo(mni);
        boolean firstHighest = true;
        // output part
        /*
	 * StringBuilder b = new StringBuilder(mapid + " added key " + mni.key +
	 * ". edges: "); for (int i : mni.edge) { b.append(i + ", "); }
	 * System.out.println(b.toString());
	 * FileoutputUtil.log(FileoutputUtil.PacketEx_Log, b.toString());
         */
        // output part end

        short ret = -1;
        for (short i : mni.edge0) {
            if (!Data.containsKey(i)) {
                if (ret != -1 && (mapid / 100 == 9211204 || mapid / 100 == 9320001 || (mapid / 100 == 9211201 || mapid / 100 == 9211202))) {
                    if (!firstHighest) {
                        ret = (short) Math.min(ret, i);
                    } else {
                        firstHighest = false;
                        ret = (short) Math.max(ret, i);
                        //two ways for stage 5 to get to end, thats highest ->lowest, and lowest -> highest(doesn't work)
                        break;
                    }
                } else {
                    ret = i;
                }
            }
        }
        mni.nextNode = ret;
        return ret;
    }

    public void sortInfo() {
        final Map<Short, EscortMobFootHoldData> unsortedNodes = new HashMap(Data);
        final int nodeSize = unsortedNodes.size();
        Data.clear();

        short nextNode = getNextNode(unsortedNodes.get(StartingData));
        while (Data.size() != nodeSize && nextNode >= 0) {
            nextNode = getNextNode(unsortedNodes.get(nextNode));
        }
    }
}
