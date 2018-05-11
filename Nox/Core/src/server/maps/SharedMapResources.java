package server.maps;

import constants.JobConstants;
import constants.MapConstants;
import constants.ReactorConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.MaplePortal;
import server.Randomizer;
import server.life.LifeType;

/**
 *
 * @author Lloyd Korn
 */
public class SharedMapResources {

    private static final ReadWriteLock SMR_MUTEX = new ReentrantReadWriteLock();
    private static final Map<Integer, SharedMapResources> SMR_CACHE = new HashMap();

    // Map Data
    private List<Integer> RandomherbsAndViens;
    public int maxHerbSpawn = 0, maxRegularSpawnAtOnce = 0;
    public String onUserEnter;
    public String onFirstUserEnter;
    public String fieldScript;
    public String fieldScriptString, defaultBGM;
    public AutoLieDetector LieDetector = null;
    public Map_mobMassacre massacre = null;
    public Map_MCarnival mcarnival = null;
    public MapleCoconut mcoconut = null;
    public MapleSnowball msnowball = null;
    public final Map<Integer, MaplePortal> portals = new HashMap();
    public final List<Rectangle> areas = new ArrayList();
    public MapleFootholdTree footholds = null;
    public byte ridingMove;
    public boolean noLanding, noCancelSkill, needSkillForFly, fly, town, swim, clock, personalShop, everlast = false, noMapCmd,
            standAlone, partyStandAlone, quarterView, hideMinimap, noChair;
    public int lvLimit = 0, partyBonusR = 0, protectItem = 0, mapid, mainMapid,
            timeLimit, fieldLimit, consumeItemCoolTime, starForceBarrier, decHPInterval, lvForceMove;
    private int returnMapId, forcedReturnMap = 999999999;
    public int top, bottom, left, right;
    public FieldType fieldTypeEnum; // regular, snowball, etc
    public String fieldType;
    public short decHP = 0, createMobInterval = 9000, fixedMobCapacity;
    public short forcedSpeed = 0; // The fixed speed which the character can move in this map. Used in maps such as Shinsoo School Road
    public float monsterRate, recoveryRate;

    // Temp data for all channels 20 because they need a customized object and it cant be shared
    public List<TemporaryStorage_Life> LifeStorage = new ArrayList();
    public List<TemporaryStorage_Reactor> ReactorStorage = new ArrayList();
    public List<TemporaryStorage_Portal> PortalStorage = new ArrayList();

    // Nodes
    private int nodeStart = -1;
    private boolean firstHighest = true;
    public Map<Integer, MapleNodeInfo> nodes = new HashMap(); //used for HOB pq.

    // Direction info
    public final List<DirectionInfo> directionInfo = new ArrayList();

    // No skill info
    public final NoSkillInfo noSkillInfo = new NoSkillInfo();

    /*
     * Returns the return map for this; however if there is an existing override
     * value for event instance it will return the event set fieldid instead
     */
    public int getReturnMapId(final MapleMap map) {
        if (map.getOverrideReturnMapId() != -1) {
            return map.getOverrideReturnMapId();
        }
        return returnMapId;
    }

    public int getForcedReturnMapId(final MapleMap map) {
        if (map.getOverrideReturnMapId() != -1) {
            return map.getOverrideReturnMapId();
        }
        return this.forcedReturnMap;
    }

    public void setForcedReturnMapId(int returnMapId) {
        this.forcedReturnMap = returnMapId;
    }

    public void setReturnMapId(int returnMapId) {
        this.returnMapId = returnMapId;
    }

    public int getRandomherbsAndViens() {
        if (Randomizer.nextInt(99) < 5) {
            return Randomizer.nextBoolean() ? ReactorConstants.Heartstone : ReactorConstants.Gold_Flower;
        }
        return RandomherbsAndViens.get(Randomizer.nextInt(RandomherbsAndViens.size()));
    }

    public static final List<Integer> setRandomherbsAndViens(SharedMapResources smr, int spawnSize, int id, int min, int max) {
        if (smr.RandomherbsAndViens == null) {
            smr.RandomherbsAndViens = MapConstants.getVienOrHerbsSpawnFromMap(smr, spawnSize, id, min, max);
        }
        return smr.RandomherbsAndViens;
    }

    public static final boolean containsSMR(final int mapid) {
        return SMR_CACHE.containsKey(mapid);
    }

    public static final SharedMapResources getSMR(final int mapid) {
        if (SMR_CACHE.containsKey(mapid)) {
            return SMR_CACHE.get(mapid);
        }
        return null;
    }

    public static final SharedMapResources getAndGetSMR(final int mapid) {
        SMR_MUTEX.writeLock().lock();
        try {
            if (SMR_CACHE.containsKey(mapid)) {
                return SMR_CACHE.get(mapid);
            }
            final SharedMapResources s = new SharedMapResources();

            SMR_CACHE.put(mapid, s);
            return s;
        } finally {
            SMR_MUTEX.writeLock().unlock();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="No skills">
    /**
     * Describes the skills which are restricted from use in this map
     */
    public static class NoSkillInfo {

        public final List<Integer> skillIds = new ArrayList<>();
        public final List<Integer> classIds = new ArrayList<>();

        public NoSkillInfo() {

        }

        public boolean isSkillUsable(int job, int skillid) {
            if (skillIds.contains(skillid)) {
                return false;
            }
            for (Integer classId : classIds) {
                if (JobConstants.isJobClass(job, classId)) {
                    return false;
                }
            }
            return true;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Direction Info">
    public static class DirectionInfo {

        public Point pos;
        public int key;
        public boolean forcedInput;
        public List<String> eventQ = new ArrayList<>();

        public DirectionInfo(int key, Point pos, boolean forcedInput) {
            this.key = key;
            this.pos = pos;
            this.forcedInput = forcedInput;
        }
    }

    public void addDirection(int key, DirectionInfo d) {
        this.directionInfo.add(key, d);
    }

    public DirectionInfo getDirection(int key) {
        if (key >= directionInfo.size()) {
            return null;
        }
        return directionInfo.get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nodes">
    public static class MapleNodeInfo {

        public int node, key, x, y, attr, nextNode = -1;
        public List<Integer> edge;

        public MapleNodeInfo(int node, int key, int x, int y, int attr, List<Integer> edge) {
            this.node = node;
            this.key = key;
            this.x = x;
            this.y = y;
            this.attr = attr;
            this.edge = edge;
        }
    }

    public void addNode(final MapleNodeInfo mni) {
        this.nodes.put(mni.key, mni);
    }

    public Collection<MapleNodeInfo> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    public void setNodeStart(final int ns) {
        this.nodeStart = ns;
    }

    public MapleNodeInfo getNode(final int index) {
        int i = 1;
        for (MapleNodeInfo x : getNodes()) {
            if (i == index) {
                return x;
            }
            i++;
        }
        return null;
    }

    public boolean isLastNode(final int index) {
        return index == nodes.size();
    }

    private int getNextNode(final MapleNodeInfo mni) {
        if (mni == null) {
            return -1;
        }
        addNode(mni);
        // output part
        /*StringBuilder b = new StringBuilder(mapid + " added key " + mni.key + ". edges: ");
         for (int i : mni.edge) {
         b.append(i + ", ");
         }
         System.out.println(b.toString());
         FileoutputUtil.log(FileoutputUtil.PacketEx_Log, b.toString());*/
        // output part end

        int ret = -1;
        for (int i : mni.edge) {
            if (!nodes.containsKey(Integer.valueOf(i))) {
                if (ret != -1 && (mapid / 100 == 9211204 || mapid / 100 == 9320001 || (mapid / 100 == 9211201 || mapid / 100 == 9211202))) {
                    if (!firstHighest) {
                        ret = Math.min(ret, i);
                    } else {
                        firstHighest = false;
                        ret = Math.max(ret, i);
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

    public void sortNodes() {
        if (nodes.size() <= 0 || nodeStart < 0) {
            return;
        }
        Map<Integer, MapleNodeInfo> unsortedNodes = new HashMap<>(nodes);
        final int nodeSize = unsortedNodes.size();
        nodes.clear();
        int nextNode = getNextNode(unsortedNodes.get(nodeStart));
        while (nodes.size() != nodeSize && nextNode >= 0) {
            nextNode = getNextNode(unsortedNodes.get(nextNode));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Life">
    public int Temp_AntiBotBossId;
    public String Temp_AntiBotMsg;

    public static class TemporaryStorage_Life {

        public int id, x, y, fh, cy, rx0, rx1, mobtime;
        public byte f, team;
        public String limitedname;
        public boolean hide;
        public LifeType type;

        public TemporaryStorage_Life(int id, int x, int y, int fh, int cy, int rx0, int rx1, int mobtime, int f, int team, String type, String limitedname, boolean hide) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.fh = fh;
            this.cy = cy;
            this.rx0 = rx0;
            this.rx1 = rx1;
            this.mobtime = mobtime;
            this.f = (byte) f;
            this.team = (byte) team;
            this.type = LifeType.getLifeByData(type);
            this.limitedname = limitedname;
            this.hide = hide;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Portal">
    public static class TemporaryStorage_Portal {

        public int portalId, portalType, toMap, posX, posY;
        public String pn, toName, script;

        public TemporaryStorage_Portal(int portalId, int portalType, String pn, int toMap, String toName, int posX, int posY, String script) {
            this.portalId = portalId;
            this.portalType = portalType;
            this.pn = pn;
            this.toMap = toMap;
            this.toName = toName;
            this.posX = posX;
            this.posY = posY;
            this.script = script;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reactor">
    public static class TemporaryStorage_Reactor {

        public int id, x, y, ReactorTime;
        public byte f;
        public String name;

        public TemporaryStorage_Reactor(int id, int x, int y, int f, int ReactorTime, String name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.f = (byte) f;
            this.ReactorTime = ReactorTime;
            this.name = name;
        }
    }
    // </editor-fold>
}
