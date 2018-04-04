package server.maps;

import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import constants.NPCConstants;
import provider.MapleData;
import provider.wz.nox.NoxBinaryReader;
import provider.wz.cache.WzDataStorage;
import server.MaplePortalFactory;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.Mob;
import server.life.MapleLifeType;
import server.maps.SharedMapResources.DirectionInfo;
import server.maps.objects.MapleNPC;
import server.maps.objects.MapleReactor;
import server.maps.objects.MapleRuneStone;
import tools.LogHelper;
import tools.Pair;

public class MapleMapFactory {

    private final HashMap<Integer, MapleMap> maps = new HashMap<>();
    private final HashMap<Integer, MapleMap> instanceMap = new HashMap<>();
    private static List<Integer> runeExcludingMap;

    private final ReentrantLock lock = new ReentrantLock();

    private static final Map<Integer, Pair<String, String>> MAP_NAMES = new HashMap(); // Streetname, mapname
    private int channel;

    public static void initialize() {
        loadRuneExcludingMap();
        loadAllMapsFromBinWZ();
    }

    private static void loadAllMapsFromBinWZ() {
        try {
            final NoxBinaryReader data = WzDataStorage.getBinaryMapData();

            // Map String
            final int ContinentSize = data.readInt();
            for (int i = 0; i < ContinentSize; i++) {

                final int MapSize = data.readInt();
                for (int z = 0; z < MapSize; z++) {
                    if (data.readBoolean()) {
                        final int Mapid = data.readInt();

                        final String StreetName = data.readAsciiString();
                        final String MapName = data.readAsciiString();

                        MAP_NAMES.put(Mapid, new Pair(StreetName, MapName));
                    }
                }
                data.readAsciiString(); // Continent name
            }

            // Maps
            final int MapCount = data.readInt();
            for (int i = 0; i < MapCount; i++) {
                final int FieldId = data.readInt();

                final SharedMapResources smr = SharedMapResources.getAndGetSMR(FieldId); // This adds too
                smr.mapid = FieldId;
                smr.mainMapid = data.readInt();
                if (smr.mainMapid == -1) {
                    smr.mainMapid = FieldId;
                }
                smr.setForcedReturnMapId(data.readInt());
                smr.setReturnMapId(data.readInt());

                smr.fieldType = data.readAsciiString();
                smr.fieldTypeEnum = FieldType.getFromVal(smr.fieldType);
                switch (smr.fieldTypeEnum) {
                    case MapleCoconut:
                        smr.mcoconut = new MapleCoconut(data);
                        break;
                    case Snowball:
                        smr.msnowball = new MapleSnowball(data);
                        break;
                }
                smr.fieldLimit = data.readInt();

                smr.monsterRate = data.readFloat();
                smr.defaultBGM = data.readAsciiString();
                smr.recoveryRate = data.readFloat();
                smr.ridingMove = (byte) data.readByte();
                smr.noLanding = data.readBoolean();
                smr.noCancelSkill = data.readBoolean();

                smr.onFirstUserEnter = data.readAsciiString();
                smr.onUserEnter = data.readAsciiString();
                smr.fieldScriptString = data.readAsciiString();

                smr.personalShop = data.readBoolean();
                smr.town = data.readBoolean();
                smr.fly = data.readBoolean();
                smr.swim = data.readBoolean();
                smr.needSkillForFly = data.readBoolean();
                smr.clock = data.readBoolean();
                smr.lvLimit = data.readByte();
                smr.timeLimit = data.readShort();
                smr.partyBonusR = data.readShort();
                smr.standAlone = data.readBoolean();
                smr.partyStandAlone = data.readBoolean();
                smr.quarterView = data.readBoolean();
                smr.hideMinimap = data.readBoolean();
                smr.noChair = data.readBoolean();
                smr.noMapCmd = data.readBoolean();
                smr.everlast = data.readBoolean();
                smr.decHP = data.readShort();
                smr.decHPInterval = data.readInt();
                smr.protectItem = data.readInt();
                smr.consumeItemCoolTime = data.readShort();
                smr.createMobInterval = data.readShort();
                smr.starForceBarrier = data.readInt();
                smr.top = data.readInt();
                smr.bottom = data.readInt();
                smr.left = data.readInt();
                smr.right = data.readInt();
                smr.lvForceMove = data.readInt();
                smr.fixedMobCapacity = data.readShort();
                smr.forcedSpeed = data.readShort();  // Used in maps such as Shinsoo School Road

                Point lBound = new Point();
                Point uBound = new Point();
                final MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
                List<MapleFoothold> allFootholds = new LinkedList();
                MapleFoothold fh;
                final short ExternalCount = data.readShort(); // Writer.Write((short)FootholdTree.WzProperties.Count);
                for (int External = 0; External < ExternalCount; External++) { // <imgdir name="foothold">
                    final int LayerCount = data.readShort(); // Writer.Write((short)Layer.WzProperties.Count);
                    final int FhOrder = data.readShort(); // Writer.Write(short.Parse(Layer.Name));

                    for (int Layers = 0; Layers < LayerCount; Layers++) {
                        final int PlatformCount = data.readShort(); //  Writer.Write((short)Platform.WzProperties.Count);
                        final int PlatformOrder = data.readShort(); // Writer.Write(short.Parse(Platform.Name));

                        for (int Platforms = 0; Platforms < PlatformCount; Platforms++) {
                            final int PlatformId = data.readShort();
                            final int Next = data.readShort();
                            final int Prev = data.readShort();
                            final int X1 = data.readShort();
                            final int X2 = data.readShort();
                            final int Y1 = data.readShort();
                            final int Y2 = data.readShort();

                            fh = new MapleFoothold(new Point(X1, Y1), new Point(X2, Y2), PlatformId);
                            fh.setPrev(Prev);
                            fh.setNext(Next);

                            if (fh.getX1() < lBound.x) {
                                lBound.x = fh.getX1();
                            }
                            if (fh.getX2() > uBound.x) {
                                uBound.x = fh.getX2();
                            }
                            if (fh.getY1() < lBound.y) {
                                lBound.y = fh.getY1();
                            }
                            if (fh.getY2() > uBound.y) {
                                uBound.y = fh.getY2();
                            }
                            allFootholds.add(fh);
                        }
                    }
                }
                for (MapleFoothold foothold : allFootholds) {
                    fTree.insert(foothold);
                }
                fTree.updateFullMapArea();
                smr.footholds = fTree;

                // Node info for monster escort
                /* MapleNodes nodeInfo = new MapleNodes(mapid);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(MapleDataTool.getInt(node, 0));
                        continue;
                    }
                    List<Integer> edges = new ArrayList<>();
                    if (node.getChildByPath("edge") != null) {
                        for (MapleData edge : node.getChildByPath("edge")) {
                            edges.add(MapleDataTool.getInt(edge, -1));
                        }
                    }
                    final MapleNodeInfo mni = new MapleNodeInfo(
                            Integer.parseInt(node.getName()),
                            MapleDataTool.getIntConvert("key", node, 0),
                            MapleDataTool.getIntConvert("x", node, 0),
                            MapleDataTool.getIntConvert("y", node, 0),
                            MapleDataTool.getIntConvert("attr", node, 0), edges);
                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
            nodeInfo.sortNodes();
        }*/
                final int NodeCount = data.readShort();
                final EscortMobFootHold EscortFHData = NodeCount > 0 ? new EscortMobFootHold(FieldId) : null;

                for (int ee = 0; ee < NodeCount; ee++) {
                    final int NodeDataType = data.readByte();

                    switch (NodeDataType) {
                        case 1:
                            // Start info
                            EscortFHData.StartingData = data.readShort();
                            break;
                        case 2:
                            // End info
                            EscortFHData.EndingData = data.readShort();
                            break;
                        // Edge info
                        // nothing for now
                        case 3:
                            break;
                        case 0:
                            final EscortMobFootHoldData EMFHD = new EscortMobFootHoldData();
                            EMFHD.NodeInfo = data.readShort(); // File name
                            final int EdgesCount = data.readByte();
                            for (int edgec = 0; edgec < EdgesCount; edgec++) {
                                EMFHD.edge0.add(data.readShort());
                                EMFHD.edge0.add(data.readShort());
//			    EMFHD.edge1.add(data.readShort());
                            }
                            EMFHD.key = data.readShort();
                            EMFHD.x = data.readShort();
                            EMFHD.y = data.readShort();
                            EMFHD.attr = data.readShort();

                            if (data.readBoolean()) { // stopInfo
                                EMFHD.chatBalloon = data.readInt();
                                EMFHD.stopDuration = (byte) data.readByte();
                                EMFHD.sayTic = (byte) data.readByte();
                                EMFHD.isRandom = data.readBoolean();
                                EMFHD.isRepeat = data.readBoolean();
                                EMFHD.isWeather = data.readBoolean();

                                final int SayInfoCount = data.readByte();
                                for (int sic = 0; sic < SayInfoCount; sic++) {
                                    EMFHD.act.add(data.readAsciiString());
                                    EMFHD.say.add(data.readAsciiString());
                                }
                            }
                            EscortFHData.addInfo(EMFHD);
                            break;
                        default:
                            throw new RuntimeException("Unhandled mob escort data type of " + NodeDataType + "\nMapid:  " + FieldId);
                    }
                }
                smr.footholds.setEscortMobFootHold(EscortFHData);

                // Monster Carnival
                if (data.readBoolean()) {

                    final Map_MCarnival mcarnival = new Map_MCarnival();
                    mcarnival.effectWin = data.readAsciiString();
                    mcarnival.effectLose = data.readAsciiString();
                    mcarnival.soundWin = data.readAsciiString();
                    mcarnival.soundLose = data.readAsciiString();
                    mcarnival.rewardMapWin = data.readInt();
                    mcarnival.rewardMapLose = data.readInt();
                    mcarnival.mobGenMax = data.readByte();
                    mcarnival.mapDivided = data.readBoolean();
                    mcarnival.deathCP = data.readByte();
                    mcarnival.guardianGenMax = data.readShort();
                    mcarnival.reactorRed = data.readInt();
                    mcarnival.reactorBlue = data.readInt();

                    if (data.readBoolean()) {
                        final int mobGenPosSize = data.readShort();
                        for (int mgps = 0; mgps < mobGenPosSize; mgps++) {
                            final Point pos = data.readPoint();
                            final short cy = data.readShort();
                            final byte team = (byte) data.readByte();

                            if (mcarnival.mobGenMax == -1) {
                                if (team == 0) {
                                    mcarnival.posRedInfo.add(pos);
                                } else {
                                    mcarnival.posBlueInfo.add(pos);
                                }
                            } else {
                                mcarnival.posRedInfo.add(pos);
                            }
                            // x, y, fh, cy
                        }
                    }

                    if (data.readBoolean()) {
                        final int mcSize = data.readShort();
                        for (int mcs = 0; mcs < mcSize; mcs++) {
                            final int id = data.readShort();
                            final int SpendCP = data.readInt();

                            mcarnival.mobs.add(id);
                            mcarnival.MobCP.add(SpendCP);
                        }
                    }

                    // Skills
                    if (data.readBoolean()) {
                        final int skillSize = data.readShort();
                        for (int mgps = 0; mgps < skillSize; mgps++) {
                            int key = data.readInt();
                            int skill = data.readInt();

                            mcarnival.Skills.put(key, skill);
                        }
                    }

                    if (data.readBoolean()) {
                        final int guardianGenPosSize = data.readShort();
                        for (int mgps = 0; mgps < guardianGenPosSize; mgps++) {
                            final Map_MCarnival.MCGuardian g = new Map_MCarnival.MCGuardian();

                            final Point pos = data.readPoint();
                            final int f = data.readByte();
                            final byte team = (byte) data.readByte();

                            g.f = f;
                            g.pos = pos;
                            g.team = (byte) team;
                            mcarnival.Guardian.add(g);
                        }
                    }
                    smr.mcarnival = mcarnival;
                }

                if (data.readBoolean()) {
                    int DirectionCount = data.readShort();

                    for (int dir = 0; dir < DirectionCount; dir++) {
                        int directionName = data.readInt();
                        Point xy = data.readPoint();
                        boolean forcedInput = data.readBoolean();

                        DirectionInfo di = new DirectionInfo(directionName, xy, forcedInput);

                        int eventQSize = data.readShort();
                        for (int eq = 0; eq < eventQSize; eq++) {
                            String eventQItem = data.readAsciiString();

                            di.eventQ.add(eventQItem);
                        }
                        smr.directionInfo.add(di);
                    }
                }

                // Lie Detector
                if (data.readBoolean()) {
                    final AutoLieDetector lie = new AutoLieDetector();
                    lie.endHour = (byte) data.readByte();
                    lie.startHour = (byte) data.readByte();
                    lie.interval = data.readInt();
                    lie.prop = (byte) data.readByte();

                    smr.LieDetector = lie;
                }

                // Nett Pyramid [mobMassacre]
                if (data.readBoolean()) {
                    final Map_mobMassacre mass = new Map_mobMassacre();

                    mass.totalbar = data.readInt();
                    mass.decrease_persec = (byte) data.readByte();
                    mass.cool_add = (byte) data.readByte();
                    mass.miss_sub = (byte) data.readByte();
                    mass.hit_add = (byte) data.readByte();

                    final int CountEffectDataSize = data.readByte();
                    for (int bcs = 0; bcs < CountEffectDataSize; bcs++) {
                        final int CountName = data.readInt();
                        final int CountBuff = data.readInt();
                        final boolean SkillUse = data.readInt() > 0;

                        mass.eff.put(CountName,
                                new Pair(CountBuff, SkillUse));
                    }
                    smr.massacre = mass;
                }

                // Capture the flag
                while (data.readBoolean()) {
                    Point pos = data.readPoint();
                    String areaName = data.readAsciiString();
                }

                // Area
                while (data.readBoolean()) {
                    final int x1 = data.readShort();
                    final int x2 = data.readShort();
                    final int y1 = data.readShort();
                    final int y2 = data.readShort();

                    smr.areas.add(new Rectangle(x1, y1, (x2 - x1), (y2 - y1)));
                }

                // Swim Area
                while (data.readBoolean()) {
                    final int x1 = data.readShort();
                    final int x2 = data.readShort();
                    final int y1 = data.readShort();
                    final int y2 = data.readShort();

                    fTree.insertSwimAreaBoundary(x1, x2, y1, y2);
                }

                // Moving Platform
                int MovingPlatformSize = data.readByte();
                for (int mp = 1; mp <= MovingPlatformSize; mp++) {
                    if (data.readBoolean()) { // IF available

                        final int ReadSize = data.readShort();
                        for (int rzs = 0; rzs < ReadSize; rzs++) {
                            final int type = data.readByte();

                            switch (type) {
                                case 1:
                                    final String NodeName = data.readAsciiString();
                                    final int NodeStart = data.readShort();
                                    final int NodeSpeed = data.readShort();
                                    final int x1 = data.readInt(); // changed to int on v188
                                    final int y1 = data.readInt(); // changed to int on v188
                                    final int x2 = data.readInt(); // changed to int on v188
                                    final int y2 = data.readInt(); // changed to int on v188
                                    final int r = data.readInt(); // changed to int on v188
                                    final List<Integer> SN = new ArrayList();
                                    final int SNSize = data.readByte();
                                    for (int sn = 0; sn < SNSize; sn++) {
                                        SN.add(Integer.valueOf(data.readShort()));
                                    }
                                    final Map_MaplePlatform mni = new Map_MaplePlatform(NodeName, NodeStart, NodeSpeed, x1, y1, x2, y2, r, SN);
                                    fTree.addMovingPlatform(mni);
                                    break;
                                case 2:
                                    final String name = data.readAsciiString();

                                    fTree.addMovingPlatformflags(new Pair<>(name, name.endsWith("3") ? 1 : 0));  //idk, no indication in wz
                                    break;
                                case 0:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                // Buff Zone
                while (data.readBoolean()) {
                    final int x1 = data.readShort();
                    final int y1 = data.readShort();
                    final int x2 = data.readShort();
                    final int y2 = data.readShort();
                    final int ItemID = data.readInt();
                    final int Interval = data.readInt();
                    final int Duration = data.readInt();

                    fTree.addBuffZone(new Map_BuffZone(x1, y1, x2, y2, ItemID, Interval, Duration));
                }

                // Life
                smr.Temp_AntiBotBossId = data.readInt();
                smr.Temp_AntiBotMsg = data.readAsciiString();

                while (data.readBoolean()) {
                    final int Id = data.readInt();

                    final int x = data.readShort();
                    final int y = data.readShort();
                    final int fh_ = data.readShort();
                    final int cy = data.readShort();
                    final int rx0 = data.readShort();
                    final int rx1 = data.readShort();
                    final int MobTime = data.readInt();
                    final int F = data.readByte();
                    final int team = data.readByte();
                    final String type = data.readAsciiString();
                    final boolean hide = data.readBoolean();
                    final String LimitedName = data.readAsciiString();

                    smr.LifeStorage.add(new SharedMapResources.TemporaryStorage_Life(Id, x, y, fh_, cy, rx0, rx1, MobTime, F, team, type, LimitedName, hide));
                }

                // Portal
                while (data.readBoolean()) {
                    final int portalId = data.readShort();
                    final int portalType = data.readShort();
                    final String pn = data.readAsciiString();
                    final int toMap = data.readInt();
                    final String toName = data.readAsciiString();
                    final int posX = data.readShort();
                    final int posY = data.readShort();
                    final String script = data.readAsciiString();

//		    smr.PortalStorage.add(new TemporaryStorage_Portal(portalId, portalType, pn, toMap, toName, posX, posY, script));
                    smr.portals.put(portalId, MaplePortalFactory.makePortal(portalId, portalType, pn, toMap, toName, posX, posY, script));
                }

                // Seats
                while (data.readBoolean()) {
                    final int SeatId = data.readByte();
                    final int posX = data.readShort();
                    final int posY = data.readShort();
                    // nothing yet
                }

                // Reactors
                while (data.readBoolean()) {
                    final int Rid = data.readInt();
                    final int posx = data.readShort();
                    final int posy = data.readShort();
                    final int f = data.readByte();
                    final int ReactorTime = data.readInt();
                    final String name = data.readAsciiString();

                    smr.ReactorStorage.add(new SharedMapResources.TemporaryStorage_Reactor(Rid, posx, posy, f, ReactorTime, name));
                }

                // No skills.
                if (data.readBoolean()) {
                    int classSpecific = data.readInt();
                    for (int cs = 0; cs < classSpecific; cs++) {
                        smr.noSkillInfo.classIds.add(data.readInt());
                    }

                    int skillSpecific = data.readInt();
                    for (int ss = 0; ss < skillSpecific; ss++) {
                        smr.noSkillInfo.skillIds.add(data.readInt());
                    }
                }
            }
            System.out.println(String.format("[Info] Loaded %d Maps.", MapCount));
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadRuneExcludingMap() {
        final MapleData runeData = WzDataStorage.getEtcWZ().getData("RuneStone.img");
        List<Integer> ruinsExcludingMap = new ArrayList<>();

        /*MapleData c = runeData.getChildByPath("exceptMap");
        for (MapleData c1 : c) {
            int map = MapleDataTool.getInt(c1, 0);

            ruinsExcludingMap_.add(map);
        }*/
        runeExcludingMap = Collections.unmodifiableList(ruinsExcludingMap); // dont touch :( 
    }

    public MapleMapFactory(int channel) {
        this.channel = channel;
    }

    public final MapleMap getMap(final int mapid) {
        return getMap(mapid, true, true, true);
    }

    //backwards-compatible
    public final MapleMap getMap(final int mapid, final boolean respawns, final boolean npcs) {
        return getMap(mapid, respawns, npcs, true);
    }

    public final MapleMap getMap(final int mapid, final boolean respawns, final boolean npcs, final boolean reactors) {
        MapleMap map = maps.get(mapid);

        if (map == null) {
            synchronized (maps) {
                map = new MapleMap(mapid, channel);

                SharedMapResources smr = SharedMapResources.getSMR(mapid);
                if (smr == null) {
                    return null;
                }
                if (smr.mainMapid != mapid) {
                    smr = SharedMapResources.getAndGetSMR(smr.mainMapid); // Set linking map
                }
                map.setSharedMapResources(smr);

                // Loading part
                if (reactors) {
                    loadReactors(map);
                }
                loadLife(map, npcs, respawns);
            }
            maps.put(mapid, map);
        }
        return map;
    }

    public MapleMap getInstanceMap(final int instanceid) {
        return instanceMap.get(instanceid);
    }

    public void removeInstanceMap(final int instanceid) {
        lock.lock();
        try {
            if (isInstanceMapLoaded(instanceid)) {
                getInstanceMap(instanceid).checkStates("");
                instanceMap.remove(instanceid);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeMap(final int instanceid) {
        lock.lock();
        try {
            if (isMapLoaded(instanceid)) {
                getMap(instanceid).checkStates("");
                maps.remove(instanceid);
            }
        } finally {
            lock.unlock();
        }
    }

    public MapleMap createInstanceMap(int mapid, int returnmap, boolean respawns, boolean npcs, boolean reactors, String Portal_Signature, int instanceid) {
        final MapleMap map = new MapleMap(mapid, channel);

        SharedMapResources smr = SharedMapResources.getAndGetSMR(mapid);
        if (smr == null) {
            return null;
        }
        if (smr.mainMapid != mapid) {
            smr = SharedMapResources.getAndGetSMR(smr.mainMapid); // Set linking map
        }
        map.setSharedMapResources(smr);

        // Loading part
        if (reactors) {
            loadReactors(map);
        }
        map.setdisableEnterScript(true);
        loadLife(map, npcs, respawns);

        instanceMap.put(instanceid, map);

        map.setOverridePortal(map.getId() + "_" + Portal_Signature);
        map.setOverrideReturnMapId(returnmap);

        return map;
    }

    public int getLoadedMaps() {
        return maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return maps.containsKey(mapId);
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return instanceMap.containsKey(instanceid);
    }

    public void clearLoadedMap() {
        lock.lock();
        try {
            maps.clear();
        } finally {
            lock.unlock();
        }
    }

    public List<MapleMap> getAllLoadedMaps() {
        List<MapleMap> ret = new ArrayList<>();
        lock.lock();
        try {
            ret.addAll(maps.values());
            ret.addAll(instanceMap.values());
        } finally {
            lock.unlock();
        }
        return ret;
    }

    public Collection<MapleMap> getAllMaps() {
        return Collections.unmodifiableCollection(maps.values());
    }

    public static String getMapName(int mapid) {
        if (MAP_NAMES.containsKey(mapid)) {
            Pair<String, String> mapName = MAP_NAMES.get(mapid);
            return mapName.left;
        }
        return "";
    }

    public static String getStreetName(int mapid) {
        if (MAP_NAMES.containsKey(mapid)) {
            Pair<String, String> mapName = MAP_NAMES.get(mapid);
            return mapName.right;
        }
        return "";
    }

    // <editor-fold defaultstate="visible" desc="Initialization whenever any map loads"> 
    private static void loadLife(final MapleMap map, boolean loadNPC, boolean respawns) {
        // Variables to keep track of the min and max level of monsters in the map
        int lowestLifeLevel = GameConstants.maxLevel;
        int highestLifeLevel = 0;

        for (SharedMapResources.TemporaryStorage_Life l : map.getSharedMapResources().LifeStorage) {
            if (l.type == MapleLifeType.Unknown) {
                LogHelper.UNCODED.get().info(String.format("[MapleMapFactory] New life type in mapid: %d", map.getId()));
                continue;
            }
            if (loadNPC && l.type == MapleLifeType.Npcs || respawns && l.type == MapleLifeType.Monster) {
                final AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(l.id, l.type);
                if (myLife == null) {
                    continue;
                }

                myLife.setCy(l.cy);
                myLife.setF(l.f);
                myLife.setFh(l.fh);
                myLife.setRx0(l.rx0);
                myLife.setRx1(l.rx1);
                myLife.setMTime(0);
                myLife.setPosition(new Point(l.x, l.y));

                switch (l.type) {
                    case Monster: {
                        final Mob mob = (Mob) myLife;
                        mob.setCarnivalTeam(l.team);

                        map.addMonsterSpawn(mob,
                                l.mobtime,
                                mob.getId() == map.getSharedMapResources().Temp_AntiBotBossId ? map.getSharedMapResources().Temp_AntiBotMsg : null);

                        // Update max/min level
                        if (!mob.getStats().isBoss()) {
                            if (mob.getStats().getLevel() > highestLifeLevel) {
                                highestLifeLevel = mob.getStats().getLevel();
                            }
                            if (mob.getStats().getLevel() < lowestLifeLevel) {
                                highestLifeLevel = mob.getStats().getLevel();
                            }
                        }
                        break;
                    }
                    case Npcs: {
                        if (l.limitedname != null && !l.limitedname.equals("")) {
                            boolean contains = false;
                            for (String s : NPCConstants.LIMITED_NPC) {
                                if (s.equals(l.limitedname)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                continue;
                            }
                        }
                        final MapleNPC npc = (MapleNPC) myLife;
                        final String name = npc.getName();

                        if (name != null && name.contains("Maple TV")) {//thank god lol.
                            continue;
                        }

                        // Doesn't spawn these select NPCs, essentially hiding them.
                        if (GameConstants.isHiddenNpc(npc.getId())) {
                            continue;
                        }

                        map.spawnNpcOnMapLoad((MapleNPC) myLife);
                        break;
                    }
                }
            }
        }
        // Load player NPC, we don't want it to load upon every single map :D
        final List<Integer> playernpcs = NPCConstants.possiblePlayerNPCLocation(map.getId());
        if (playernpcs != null) {
            for (final int i : playernpcs) {
                final MapleNPC npc = MapleLifeFactory.getNPC(i);
                map.spawnNpcOnMapLoad(npc);
            }
        }
        addAreaBossSpawn(map);
        map.loadMonsterRate();

        // Load runes
        if (!runeExcludingMap.contains(map.getId()) && highestLifeLevel >= 30) {
            MapleRuneStone rune = new MapleRuneStone(lowestLifeLevel, highestLifeLevel);
            rune.respawnRuneInMap(map, true);

            map.setRune(rune);
        }
    }

    public static void loadReactors(final MapleMap map) {
        for (SharedMapResources.TemporaryStorage_Reactor r : map.getSharedMapResources().ReactorStorage) {
            final MapleReactorStats stats = MapleReactorFactory.getReactor(r.id);
            final MapleReactor myReactor = new MapleReactor(stats, r.id);

            myReactor.setFacingLeft(r.f == 0);

            myReactor.setPosition(new Point(r.x, r.y));
            myReactor.setDelay(r.ReactorTime * 1000);
            myReactor.setState((byte) 0);
            myReactor.setName(r.name);

            map.spawnReactor(myReactor);
        }
    }
// </editor-fold> 

    private static void addAreaBossSpawn(final MapleMap map) {
        int monsterid = -1;
        int mobtime = -1;
        String msg = null;
        boolean shouldSpawn = true;
        Point pos1 = null, pos2 = null, pos3 = null;

        switch (map.getId()) {
            case 104010200: // Mano
                mobtime = 1200;
                monsterid = 2220000;
                msg = "A cool breeze was felt when Mano appeared.";
                pos1 = new Point(189, 2);
                pos2 = new Point(478, 250);
                pos3 = new Point(611, 489);
                break;
            case 102020500: // Stumpy
                mobtime = 1200;
                monsterid = 3220000;
                msg = "Stumpy has appeared with a stumping sound that rings the Stone Mountain.";
                pos1 = new Point(1121, 2130);
                pos2 = new Point(483, 2171);
                pos3 = new Point(1474, 1706);
                break;
            case 100020101: //Mushmom
                mobtime = 1200;
                monsterid = 6130101;
                msg = "Mushmom has appeared.";
                pos1 = new Point(-311, 201);
                pos2 = new Point(-903, 197);
                pos3 = new Point(-568, 196);
                break;
            case 100020301: //Blue Mushmom
                mobtime = 1200;
                monsterid = 8220007;
                msg = "Blue Mushmom has appeared.";
                pos1 = new Point(-188, -657);
                pos2 = new Point(625, -660);
                pos3 = new Point(508, -648);
                break;
            case 100020401: //Zombie Mushmom
                mobtime = 1200;
                monsterid = 6300005;
                msg = "Zombie Mushmom has appeared.";
                pos1 = new Point(-130, -773);
                pos2 = new Point(504, -760);
                pos3 = new Point(608, -641);
                break;
            case 120030500: // King Clang
                mobtime = 1200;
                monsterid = 5220001;
                msg = "A strange turban shell has appeared on the beach.";
                pos1 = new Point(-355, 179);
                pos2 = new Point(-1283, -113);
                pos3 = new Point(-571, -593);
                break;
            case 250010304: // Tae Roon
                mobtime = 2100;
                monsterid = 7220000;
                msg = "Tae Roon appeared with a loud growl.";
                pos1 = new Point(-210, 33);
                pos2 = new Point(-234, 393);
                pos3 = new Point(-654, 33);
                break;
            case 200010300: // Eliza
                mobtime = 1200;
                monsterid = 8220000;
                msg = "Eliza has appeared with a black whirlwind.";
                pos1 = new Point(665, 83);
                pos2 = new Point(672, -217);
                pos3 = new Point(-123, -217);
                break;
            case 250010503: // Ghost Priest
                mobtime = 1800;
                monsterid = 7220002;
                msg = "The area fills with an unpleasant force of evil.. even the occasional ones of the cats sound disturbing";
                pos1 = new Point(-303, 543);
                pos2 = new Point(227, 543);
                pos3 = new Point(719, 543);
                break;
            case 222010310: // Old Fox
                mobtime = 2700;
                monsterid = 7220001;
                msg = "As the moon light dims, a long fox cry can be heard and the presence of the old fox can be felt.";
                pos1 = new Point(-169, -147);
                pos2 = new Point(-517, 93);
                pos3 = new Point(247, 93);
                break;
            case 103030400: // Dale
                mobtime = 1800;
                monsterid = 6220000;
                msg = "The huge crocodile Dyle has come out from the swamp.";
                pos1 = new Point(-831, 109);
                pos2 = new Point(1525, -75);
                pos3 = new Point(-511, 107);
                break;
            case 101040300: // Faust
                mobtime = 1800;
                monsterid = 5220002;
                msg = "The blue fog became darker when Faust appeared.";
                pos1 = new Point(600, -600);
                pos2 = new Point(600, -800);
                pos3 = new Point(600, -300);
                break;
            case 220050100: // Timer
                mobtime = 1500;
                monsterid = 5220003;
                msg = "Click clock! Timer has appeared with an irregular clock sound.";
                pos1 = new Point(-467, 1032);
                pos2 = new Point(532, 1032);
                pos3 = new Point(-47, 1032);
                break;
            case 221040301: // Zeno
                mobtime = 2400;
                monsterid = 6220001;
                msg = "Zeno has appeared with a heavy sound of machinery.";
                pos1 = new Point(-4134, 416);
                pos2 = new Point(-4283, 776);
                pos3 = new Point(-3292, 776);
                break;
            case 240040401: // Lev
                mobtime = 7200;
                monsterid = 8220003;
                msg = "Leviathan has appeared with a cold wind from over the gorge.";
                pos1 = new Point(-15, 2481);
                pos2 = new Point(127, 1634);
                pos3 = new Point(159, 1142);
                break;
            case 260010201: // Deo
                mobtime = 3600;
                monsterid = 3220001;
                msg = "Deo slowly appeared out of the sand dust.";
                pos1 = new Point(-215, 275);
                pos2 = new Point(298, 275);
                pos3 = new Point(592, 275);
                break;
            case 251010102: // Centipede
                mobtime = 3600;
                monsterid = 5220004;
                msg = "A giant centipede appeared.";
                pos1 = new Point(-41, 124);
                pos2 = new Point(-173, 126);
                pos3 = new Point(79, 118);
                break;
            case 261030000: // Chimera
                mobtime = 2700;
                monsterid = 8220002;
                msg = "Chimera has appeared out of the darkness of the underground with a glitter in her eyes.";
                pos1 = new Point(-1094, -405);
                pos2 = new Point(-772, -116);
                pos3 = new Point(-108, 181);
                break;
            case 230020100: // Sherp
                mobtime = 2700;
                monsterid = 4220000;
                msg = "A strange shell has appeared from a grove of seaweed.";
                pos1 = new Point(-291, -20);
                pos2 = new Point(-272, -500);
                pos3 = new Point(-462, 640);
                break;
            case 103020320: // Shade
                mobtime = 1800;
                monsterid = 5090000;
                msg = "Shade has appeared.";
                pos1 = new Point(79, 174);
                pos2 = new Point(-223, 296);
                pos3 = new Point(80, 275);
                break;
            case 103020420: // Shade
                mobtime = 1800;
                monsterid = 5090000;
                msg = "Shade has appeared.";
                pos1 = new Point(2241, 301);
                pos2 = new Point(1990, 301);
                pos3 = new Point(1684, 307);
                break;
            case 261020300: // Camera
                mobtime = 2700;
                monsterid = 7090000;
                msg = "A camera has appeared.";
                pos1 = new Point(312, 157);
                pos2 = new Point(539, 136);
                pos3 = new Point(760, 141);
                break;
            case 261020401: // Deet and Roi
                mobtime = 2700;
                monsterid = 8090000;
                msg = "Deet and Roi has appeared.";
                pos1 = new Point(-263, 155);
                pos2 = new Point(-436, 122);
                pos3 = new Point(22, 144);
                break;
            case 250020300: // Master Dummy
                mobtime = 2700;
                monsterid = 5090001;
                msg = "Master Dummy has appeared.";
                pos1 = new Point(1208, 27);
                pos2 = new Point(1654, 40);
                pos3 = new Point(927, -502);
                break;
            case 211050000: // Snow Witch
                mobtime = 2700;
                monsterid = 6090001;
                msg = "The witch of snow has appeared.";
                pos1 = new Point(-233, -431);
                pos2 = new Point(-370, -426);
                pos3 = new Point(-526, -420);
                break;
            case 261010003: // Rurumo
                mobtime = 2700;
                monsterid = 6090004;
                msg = "Rurumo has appeared.";
                pos1 = new Point(-861, 301);
                pos2 = new Point(-703, 301);
                pos3 = new Point(-426, 287);
                break;
            case 222010300: // Scholar Ghost
                mobtime = 2700;
                monsterid = 6090003;
                msg = "A wise ghost has appeared.";
                pos1 = new Point(1300, -400);
                pos2 = new Point(1100, -100);
                pos3 = new Point(1100, 100);
                break;
            case 251010101: // Bamboo Warrior
                mobtime = 2700;
                monsterid = 6090002;
                msg = "A warrior with bamboo has appeared.";
                pos1 = new Point(-15, -449);
                pos2 = new Point(-114, -442);
                pos3 = new Point(-255, -446);
                break;
            case 211041400: // Riche
                mobtime = 2700;
                monsterid = 6090000;
                msg = "Riche has appeared.";
                pos1 = new Point(1672, 82);
                pos2 = new Point(2071, 10);
                pos3 = new Point(1417, 57);
                break;
            case 105030500: // Rog
                mobtime = 2700;
                monsterid = 8130100;
                msg = "Jr. Balrog has appeared.";
                pos1 = new Point(1275, -399);
                pos2 = new Point(1254, -412);
                pos3 = new Point(1058, -427);
                break;
            case 105020400: // Snack Bar
                mobtime = 2700;
                monsterid = 8220008;
                msg = "A mysterious shop appeared.";
                pos1 = new Point(-163, 82);
                pos2 = new Point(958, 107);
                pos3 = new Point(706, -206);
                break;
            case 211040101: // Snowman
                mobtime = 3600;
                monsterid = 8220001;
                msg = "A snowman covered in ice has appeared.";
                pos1 = new Point(485, 244);
                pos2 = new Point(-60, 249);
                pos3 = new Point(208, 255);
                break;
            case 910000000: // FM
                /*  if (channel == 7) {
                    mobtime = 3600;
                    monsterid = 9420015;
                    msg = "NooNoo has appeared out of anger.";
                    pos1 = new Point(498, 4);
                    pos2 = new Point(498, 4);
                    pos3 = new Point(498, 4);
                } else if (channel == 8) {
                    mobtime = 3600;
                    monsterid = 9400700;
                    msg = "Giant Tomato has appeared.";
                    pos1 = new Point(498, 4);
                    pos2 = new Point(498, 4);
                    pos3 = new Point(498, 4);
                } else if (channel == 9) {
                    mobtime = 3600;
                    monsterid = 9400734;
                    msg = "Giant Tomato has appeared.";
                    pos1 = new Point(498, 4);
                    pos2 = new Point(498, 4);
                    pos3 = new Point(498, 4);
                }*/
                break;
            case 209000000: // Happyville
                mobtime = 300;
                monsterid = 9500318;
                msg = "Giant Snowman has appeared!";
                pos1 = new Point(-115, 154);
                pos2 = new Point(-115, 154);
                pos3 = new Point(-115, 154);
                break;
            case 677000001:
                mobtime = 60;
                monsterid = 9400612;
                msg = "Marbas has appeared.";
                pos1 = new Point(99, 60);
                pos2 = new Point(99, 60);
                pos3 = new Point(99, 60);
                break;
            case 677000003:
                mobtime = 60;
                monsterid = 9400610;
                msg = "Amdusias has appeared.";
                pos1 = new Point(6, 35);
                pos2 = new Point(6, 35);
                pos3 = new Point(6, 35);
                break;
            case 677000005:
                mobtime = 60;
                monsterid = 9400609;
                msg = "Andras has appeared.";
                pos1 = new Point(-277, 78); //on the spawnpoint
                pos2 = new Point(547, 86); //bottom of right ladder
                pos3 = new Point(-347, 80); //bottom of left ladder
                break;
            case 677000007:
                mobtime = 60;
                monsterid = 9400611;
                msg = "Crocell has appeared.";
                pos1 = new Point(117, 73);
                pos2 = new Point(117, 73);
                pos3 = new Point(117, 73);
                break;
            case 677000009:
                mobtime = 60;
                monsterid = 9400613;
                msg = "Valefor has appeared.";
                pos1 = new Point(85, 66);
                pos2 = new Point(85, 66);
                pos3 = new Point(85, 66);
                break;
            case 931000500:
            case 931000502:
            case 931000504:
            case 931000506:
            case 931000508:
                mobtime = 9 * 3600; //9 hours
                monsterid = 9304005;
                msg = "Jaira has appeared.";
                pos1 = new Point(-872, -332);
                pos2 = new Point(409, -572);
                pos3 = new Point(-131, 0);
                //shouldSpawn = false;
                break;
            case 931000501:
            case 931000503:
            case 931000505:
            case 931000507:
            case 931000509:
                mobtime = 12 * 3600; //12 hours
                monsterid = 9304006;
                msg = "Snow White has appeared.";
                pos1 = new Point(-872, -332);
                pos2 = new Point(409, -572);
                pos3 = new Point(-131, 0);
                //shouldSpawn = false;
                break;
        }
        if (monsterid > 0) {
            map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg, shouldSpawn);
        }
    }
}
