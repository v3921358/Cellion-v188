package server.maps;

import enums.SummonMovementType;
import enums.MapItemType;
import enums.FieldLimitType;
import enums.MonsterHPType;
import server.life.NPCLife;
import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.QuestStatus.QuestState;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.anticheat.AntiCheat;
import client.inventory.Equip;
import client.inventory.Item;
import enums.InventoryType;
import constants.GameConstants;
import constants.QuickMove;
import constants.QuickMove.QuickMoveNPC;
import constants.ServerConstants;
import database.Database;
import handling.world.ExpeditionType;
import handling.world.PartyOperation;
import handling.world.World;

import scripting.EventManager;
import server.*;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleSquad.MapleSquadType;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.events.MapleEvent;
import server.life.*;
import server.maps.Map_MCarnival.MCGuardian;
import server.maps.objects.*;
import server.quest.Quest;
import service.ChannelServer;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.SummonPacket;
import tools.packet.WvsContext;
import tools.packet.WvsContext.PartyPacket;
import tools.packet.JobPacket.PhantomPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.OutPacket;
import scripting.provider.MapScriptManager;
import server.life.mob.BuffedMob;
import tools.LogHelper;

public final class MapleMap {

    /*
     * Holds mappings of OID -> MapleMapObject separated by MapleMapObjectType.
     * Please acquire the appropriate lock when reading and writing to the LinkedHashMaps.
     * The MapObjectType Maps themselves do not need to synchronized in any way since they should never be modified.
    
     */
    protected ClientSocket c;
    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<User> characters = new ArrayList<>();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    private int runningOid = 500000;
    private final Lock runningOidLock = new ReentrantLock();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private MapleMapEffect mapEffect;
    private final byte channel;
    private int instanceid = -1, permanentWeather = 0;
    private boolean dropsDisabled = false, gDropsDisabled = false,
            squadTimer = false, isSpawns = true, checkStates = true;
    private String speedRunLeader = "";
    private final List<Integer> dced = new ArrayList<>();
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart = 0, lastHurtTime = 0;
    private MapleSquadType squad;
    private RuneStone rune;
    private final Map<String, Integer> environment = new LinkedHashMap<>();
    private WeakReference<User> changeMobOrigin = null;

    // Monster spawns
    private long lastRespawnTime = 0;
    private final AtomicInteger maxRegularSpawn = new AtomicInteger(0);
    private final List<Spawns> monsterSpawn = new ArrayList();
    private final List<Spawns> bossSpawn = new ArrayList();

    // Individual map data
    private boolean disableEnterScript;

    // Burning field
    private long burningField_mapVacancyTime;
    private long burningField_LastUpdateTime;
    private byte burningFieldLevel;

    // Elite boss
    private byte eliteBossCount; // 0 ~ 20. Once it reaches 20, spawn an elite boss.
    private final AtomicInteger eliteBoss_KillCount; // for every 1,000 kills, increase eliteBossCount by 1

    // Shared map resources
    private SharedMapResources smr;
    private int overrideReturnMapId = -1; // Override return map id for event instance
    private String overridePortal = null; // Override portal script for custom map

    public MapleMap(final int mapid, final int channel) {
        this.disableEnterScript = false;
        this.channel = (byte) channel;

        EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<>(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap(objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);

        // Respawns & burning field
        this.lastRespawnTime = 0;
        this.burningField_mapVacancyTime = 0;
        this.burningFieldLevel = 0;
        this.burningField_LastUpdateTime = System.currentTimeMillis();

        // Elite boss
        eliteBossCount = 0; // 0 ~ 20. Once it reaches 20, spawn an elite boss.
        eliteBoss_KillCount = new AtomicInteger(0); // for every 1,000 kills, increase eliteBossCount by 1
    }

    public final void Client(ClientSocket c) {
        this.c = c;
    }

    public final int getMaximumSpawnCount() {
        return maxRegularSpawn.get();
    }
    
    public final void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }

    public final boolean getSpawns() {
        return isSpawns;
    }

    public final void toggleDrops() {
        this.dropsDisabled = !dropsDisabled;
    }

    public final void setDrops(final boolean b) {
        this.dropsDisabled = b;
    }

    public final void toggleGDrops() {
        this.gDropsDisabled = !gDropsDisabled;
    }

    public final int getId() {
        return smr.mapid;
    }

    public final MapleMap getReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(smr.getReturnMapId(this));
    }

    public final MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(channel).getMapFactory().getMap(smr.getForcedReturnMapId(this));
    }

    /**
     * Sets the common map data shared aceoss multiple maps with the same ID.
     *
     * @param smr
     */
    public void setSharedMapResources(SharedMapResources smr) {
        this.smr = smr;
    }

    public SharedMapResources getSharedMapResources() {
        return smr;
    }

    public void setdisableEnterScript(boolean disableEnterScript) {
        this.disableEnterScript = disableEnterScript;
    }

    public void setOverrideReturnMapId(int overrideReturnMapId) {
        this.overrideReturnMapId = overrideReturnMapId;
    }

    public int getOverrideReturnMapId() {
        return overrideReturnMapId;
    }

    public void setOverridePortal(String overridePortal) {
        this.overridePortal = overridePortal;
    }

    public String getOverridePortal() {
        return overridePortal;
    }

    public final String getMapName() {
        return MapleMapFactory.getMapName(smr.mapid);
    }

    public final String getStreetName() {
        return MapleMapFactory.getStreetName(smr.mapid);
    }

    public final int getCurrentPartyId() {
        charactersLock.readLock().lock();
        try {
            final Iterator<User> ltr = characters.iterator();
            User chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (chr.getParty() != null) {
                    return chr.getParty().getId();
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return -1;
    }

    public final void addMapObject(final MapleMapObject mapobject) {
        runningOidLock.lock();
        int newOid;
        try {
            newOid = ++runningOid;
        } finally {
            runningOidLock.unlock();
        }

        mapobject.setObjectId(newOid);

        final MapleMapObjectType type = mapobject.getType();
        mapobjectlocks.get(type).writeLock().lock();
        try {
            mapobjects.get(type).put(newOid, mapobject);
        } finally {
            mapobjectlocks.get(type).writeLock().unlock();
        }
    }

    private void spawnAndAddRangedMapObject(final MapleMapObject mapobject, final DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);

        charactersLock.readLock().lock();
        try {
            final Iterator<User> itr = characters.iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if ((mapobject.getType() == MapleMapObjectType.MIST || chr.getTruePosition().distanceSq(mapobject.getTruePosition()) <= GameConstants.maxViewRangeSq())) {
                    packetbakery.sendPackets(chr.getClient());
                    chr.addVisibleMapObject(mapobject);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public final void removeMapObject(final MapleMapObject obj) {
        final MapleMapObjectType type = obj.getType();

        mapobjectlocks.get(type).writeLock().lock();
        try {
            mapobjects.get(type).remove(obj.getObjectId());
        } finally {
            mapobjectlocks.get(type).writeLock().unlock();
        }
    }

    public final Point calcPointBelow(final Point initial) {
        final MapleFoothold fh = smr.footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            final double s1 = Math.abs(fh.getY2() - fh.getY1());
            final double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
                dropY = fh.getY1() + (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }

    public final Point calcDropPos(final Point initial, final Point fallback) {
        Point ret;
        if (this.getSharedMapResources().quarterView) { // if the map is like 3D
            ret = new Point(initial.x, initial.y + 70);
        } else {
            ret = calcPointBelow(new Point(initial.x, initial.y - 50));
            if (ret == null) {
                return fallback;
            }
        }
        return ret;
    }
 
    private void dropFromMonster(final User pPlayer, final Mob pMob, final boolean bInstanced) {
        if (pMob == null || pPlayer == null || ChannelServer.getInstance(channel) == null || dropsDisabled || pMob.dropsDisabled() || pPlayer.getPyramidSubway() != null) { //no drops in pyramid ok? no cash either
            return;
        }

        // Global Meso Drop
        pMob.OnMesoDropRequest(pPlayer); 
        
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte) (pMob.getStats().isExplosiveReward() ? 3 : pMob.getStats().isFfaLoot() ? 2 : pPlayer.getParty() != null ? 1 : 0);
        final int mobpos = pMob.getTruePosition().x;
        final float cmServerrate = ChannelServer.getInstance(channel).getMesoRate(pPlayer.getWorld());
        final float chServerrate = ChannelServer.getInstance(channel).getDropRate(pPlayer.getWorld());
        final float caServerrate = ChannelServer.getInstance(channel).getCashRate();
        final int cashz = (int) ((pMob.getStats().isBoss() && pMob.getStats().getHPDisplayType() == MonsterHPType.BossHP ? 20 : 1) * caServerrate);
        final int cashModifier = (int) ((pMob.getStats().isBoss() ? (pMob.getStats().isPartyBonus() ? (pMob.getMobExp() / 1000) : 0) : (pMob.getMobExp() / 1000 + pMob.getMobMaxHp() / 20000))); //no rate
        
        Item pItemDrop;
        byte nD = 1;
        Point nPOS = new Point(0, pMob.getTruePosition().y);
        double nShowdown = 100.0;
        final MonsterStatusEffect pMobEffect = pMob.getBuff(MonsterStatus.SHOWDOWN);
        if (pMobEffect != null) {
            nShowdown += pMobEffect.getX();
        }

        final MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> drops = mi.retrieveDrop(pMob.getId());
        if (drops == null) { //if no drops, no global drops either
            return;
        }
        final List<MonsterDropEntry> dropEntry = new ArrayList<>(drops);
        Collections.shuffle(dropEntry);

        boolean mesoDropped = false;
        for (MonsterDropEntry de : dropEntry) {
            if (de.itemId == pMob.getStolen()) {
                continue;
            }
            if (de.itemId != 0 && !ii.itemExists(de.itemId)) {
                continue;
            }
            if (Randomizer.nextInt(999999) < (int) (de.chance * chServerrate * pPlayer.getDropMod() * pPlayer.getStat().dropBuff / 100.0 * (nShowdown / 100.0))) {
                if (mesoDropped && droptype != 3 && de.itemId == 0) { //not more than 1 sack of meso
                    continue;
                }
                if (de.questid > 0 && pPlayer.getQuestStatus(de.questid) != QuestState.Started) {
                    continue;
                }
                if (de.itemId / 10000 == 238 && !pMob.getStats().isBoss() && pPlayer.getMonsterBook().getLevelByCard(ii.getCardMobId(de.itemId)) >= 2) {
                    continue;
                }
                if (droptype == 3) {
                    nPOS.x = (mobpos + (nD % 2 == 0 ? (40 * (nD + 1) / 2) : -(40 * (nD / 2))));
                } else {
                    nPOS.x = (mobpos + ((nD % 2 == 0) ? (25 * (nD + 1) / 2) : -(25 * (nD / 2))));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(1 + Math.abs(de.Maximum - de.Minimum)) + de.Minimum;
                    if (mesos > 0) {
                        int calcMesoAmount = (int) (mesos * (pPlayer.getStat().mesoBuff / 100.0) * pPlayer.getDropMod() * cmServerrate);
                        spawnMesoDrop(calcMesoAmount, calcDropPos(nPOS, pMob.getTruePosition()), pMob, pPlayer, false, droptype);
                        mesoDropped = true;
                    }
                } else {
                    if (GameConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        pItemDrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        final int range = Math.abs(de.Maximum - de.Minimum);
                        pItemDrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte) 0);
                    }
                    pItemDrop.setGMLog("Dropped from monster " + pMob.getId() + " on " + smr.mapid);
                    spawnMobDrop(pItemDrop, calcDropPos(nPOS, pMob.getTruePosition()), pMob, pPlayer, droptype, de.questid, false);
                }
                nD++;
            }
        }
        final List<MonsterGlobalDropEntry> globalEntry = new ArrayList<>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        // Global Drops
        for (MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance && (de.continent < 0 || (de.continent < 10 && smr.mapid / 100000000 == de.continent) || (de.continent < 100 && smr.mapid / 10000000 == de.continent) || (de.continent < 1000 && smr.mapid / 1000000 == de.continent))) {
                if (de.questid > 0 && pPlayer.getQuestStatus(de.questid) != QuestState.Started) {
                    continue;
                }
                if (de.itemId == 0) {
                    //int calcNxAmount = (int) ((Randomizer.nextInt(cashz) + cashz + cashModifier) * (pPlayer.getStat().cashBuff / 100.0) * pPlayer.getCashMod());
                    //pPlayer.modifyCSPoints(2, calcNxAmount, true); // Global NX Drop
                } else if (!gDropsDisabled) {
                    if (droptype == 3) {
                        nPOS.x = (mobpos + (nD % 2 == 0 ? (40 * (nD + 1) / 2) : -(40 * (nD / 2))));
                    } else {
                        nPOS.x = (mobpos + ((nD % 2 == 0) ? (25 * (nD + 1) / 2) : -(25 * (nD / 2))));
                    }
                    if (GameConstants.getInventoryType(de.itemId) == InventoryType.EQUIP) {
                        pItemDrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        pItemDrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1), (byte) 0);
                    }
                    pItemDrop.setGMLog("Dropped from monster " + pMob.getId() + " on " + smr.mapid + " (Global)");
                    spawnMobDrop(pItemDrop, calcDropPos(nPOS, pMob.getTruePosition()), pMob, pPlayer, de.onlySelf ? 0 : droptype, de.questid, true);
                    nD++;
                }
            }
        }
    }

    public void removeMonster(Mob monster) {
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();

        broadcastPacket(MobPacket.killMonster(monster.getObjectId(), 0, GameConstants.isAzwanMap(smr.mapid)));

        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(Mob monster) { // For mobs with removeAfter
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this);
        }
        broadcastPacket(MobPacket.killMonster(monster.getObjectId(), monster.getStats().getSelfD() < 0 ? 1 : monster.getStats().getSelfD(), false));
        removeMapObject(monster);
        monster.killed();
    }

    public final void killMonster(Mob monster, User chr, boolean withDrops, boolean second, short animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public final void ReplaceMobDelayed(User chr, Mob pOldQueen, Mob pNewMob) {
        final Point pPos = new Point(pOldQueen.getPosition());
        spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(pOldQueen);
        pOldQueen.killed();
        broadcastPacket(MobPacket.killMonster(pOldQueen.getObjectId(), 2, false));
        MapTimer.getInstance().schedule(() -> {
            spawnMonsterOnGroudBelow(pNewMob, pPos);
        }, 1000);
    }

    public final void killMonster(Mob monster, User chr, boolean withDrops, boolean second, short animation, int lastSkill) {
        if (monster.getId() == 9300471) {
            spawnNpcForPlayer(chr.getClient(), 9073000, new Point(-595, 215));
        }
        if (monster.getId() == 9001045) {
            chr.setQuestAdd(Quest.getInstance(25103), QuestState.Started, "1");
        }
        if (monster.getId() == 9001050) {
            if (chr.getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) < 2) { //should be 1 left
                Quest.getInstance(20035).forceComplete(chr, 1106000);
            }
        }
        if (monster.getId() == 9400902) {
            chr.getMap().resetFully();
            chr.getMap().killAllMonsters(true);
            chr.getMap().startMapEffect("So what do you think? Just as though as the original, right?! Move by going to Resercher H. Your first reward is this soundtrack", 5120039);
            chr.getMap().broadcastPacket(CField.musicChange("Bgm02.img/MissingYou"));
        }
        if ((monster.getId() == 8810122 || monster.getId() == 8810018) && !second) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    killMonster(monster, chr, true, true, (byte) 1);
                    killAllMonsters(true);
                }
            }, 3000);
            return;
        }
        if (monster.getId() == 8820014) { //pb sponge, kills pb(w) first before dying
            killMonster(8820000);
        } else if (monster.getId() == 9300166) { //ariant pq bomb
            animation = 2;
        }
        spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(monster);
        monster.killed();
        final MapleSquad sqd = getSquadByMap();
        final boolean instanced = sqd != null || monster.getEventInstance() != null || getEMByMap() != null;

        if (animation >= 0) {
            if (GameConstants.isAzwanMap(getId())) {
                broadcastPacket(MobPacket.killMonster(monster.getObjectId(), animation, true));
            } else {
                broadcastPacket(MobPacket.killMonster(monster.getObjectId(), animation, false));
            }
        }

        // Handles the EXP split among players
        int dropOwner = monster.killBy(chr, lastSkill);

        // Handle buffs given to map when the monster is dead
        if (monster.getBuffToGive() > -1) {
            final int buffid = monster.getBuffToGive();
            final StatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);

            charactersLock.readLock().lock();
            try {
                for (final User mc : characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);

                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001:
                                mc.getClient().SendPacket(EffectPacket.showOwnBuffEffect(buffid, EffectPacket.BuffItemEffect, mc.getLevel(), 1)); // HT nine spirit
                                broadcastPacket(mc, EffectPacket.showBuffEffect(mc.getId(), buffid, EffectPacket.BuffItemEffect, mc.getLevel(), 1), false); // HT nine spirit
                                break;
                        }
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }

        final int mobid = monster.getId();
        ExpeditionType type = null;
        if (mobid == 8810018 && smr.mapid == 240060200) { // Horntail
            //World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, "To the crew that have finally conquered Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!"));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    //c.finishAchievement(16);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            //FileoutputUtil.log(FileoutputUtil.Horntail_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.Horntail;
            }
            doShrine(true);
        } else if (mobid == 8810122 && smr.mapid == 240060201) { // Horntail
            //World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, "To the crew that have finally conquered Chaos Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!"));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    /// c.finishAchievement(24);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
//            FileoutputUtil.log(FileoutputUtil.Horntail_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.ChaosHT;
            }
            doShrine(true);
        } else if ((mobid == 9400266 && smr.mapid == 802000111)
                || (mobid == 9400265 && smr.mapid == 802000211)
                || (mobid == 9400270 && smr.mapid == 802000411)
                || (mobid == 9400273 && smr.mapid == 802000611)
                || (mobid == 9400294 && smr.mapid == 802000711)
                || (mobid == 9400296 && smr.mapid == 802000803)
                || (mobid == 9400289 && smr.mapid == 802000821)) {
            doShrine(true);
            //INSERT HERE: 2095_tokyo
        } else if (mobid == 8920100 && smr.mapid == 105200310) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052006), 1052006);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(56, 135));
        } else if (mobid == 8830000 && smr.mapid == 105100300) {
            if (speedRunStart > 0) {
                type = ExpeditionType.Balrog;
            }
        } else if ((mobid == 9420544 || mobid == 9420549) && smr.mapid == 551030200 && monster.getEventInstance() != null && monster.getEventInstance().getName().contains(getEMByMap().getName())) {
            doShrine(this.getAllMapObjectSize(MapleMapObjectType.REACTOR) == 0);
        } else if (mobid == 8820001 && smr.mapid == 270050100) {
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "Oh, the exploration team who has defeated Pink Bean with undying fervor! You are the true victors of time!"));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(17);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Pink_Bean;
            }
            doShrine(true);
        } else if (mobid == 8850011 && smr.mapid == 274040200) {
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "To you whom have defeated Empress Cygnus in the future, you are the heroes of time!"));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Cygnus;
            }
            doShrine(true);
        } else if (mobid == 8870000 && smr.mapid == 262030300) { //hilla
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "Hilla is dead."));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Hilla;
            }
            doShrine(true);
        } else if (mobid == 8920000 || mobid == 8920001 || mobid == 8920002 || mobid == 8920003) {  // Root Abyss Reactors
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052009), 1052009);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(124, 135));
        } else if (mobid == 8900000 || mobid == 8900001 || mobid == 8900002 || mobid == 8900003) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052009), 1052009);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(497, 551));

        } else if (mobid == 8910000 || mobid == 8910001 || mobid == 8910002 || mobid == 8910003) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052009), 1052009);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(-195, 455));

        } else if (mobid == 8910100 || mobid == 8910101 || mobid == 8910102 || mobid == 8910103) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052006), 1052006);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(-195, 455));

        } else if (mobid == 8930100 || mobid == 8930101 || mobid == 8930102 || mobid == 8930103) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(1052006), 1052006);
            my.setState((byte) 1);
            spawnReactorOnGroundBelow(my, new java.awt.Point(-78, 442));

        } else if (mobid == 8870100) { //hilla
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "Dark Hilla is dead."));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Hilla;
            }
            doShrine(true);
        } else if (mobid == 8860000 && smr.mapid == 272030400) {
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "To you whom have defeated Arkarium in the Darkness, you are the heroes of time!"));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Arkarium;
            }
            doShrine(true);
        } else if (mobid == 8920100 || mobid == 8920101 || mobid == 8920102 || mobid == 8920103 && smr.mapid == 105200310) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Easy_Queen;
            }
            doShrine(true);
        } else if (mobid == 9500392 && smr.mapid == 950101010) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Golden_Ravana;
            }
            doShrine(true);
        } else if (mobid == 8910100 && smr.mapid == 105200110) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Easy_VonBon;
            }
            doShrine(true);
        } else if (mobid == 8910000 && smr.mapid == 105200510) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_VonBon;
            }
            doShrine(true);
        } else if (mobid == 8880000 && smr.mapid == 401060200) { // Magnus
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "The Brave heroes have finally killed Chaos Magnus, Cheers Maplers.."));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Magnus;
            }
            doShrine(true);
        } else if (mobid == 8880002 && smr.mapid == 401060100) { // Normal Magnus
            //  World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, "The Brave heroes have finally killed Magnus, Cheers Maplers.."));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.MagnusN;
            }
            doShrine(true);
        } else if (mobid == 8900100 || mobid == 8900101 || mobid == 8900102 && smr.mapid == 105200610) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Easy_Pierre;
            }
            doShrine(true);
        } else if (mobid == 8900000 || mobid == 8900001 || mobid == 8900002 && smr.mapid == 105200210) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_Pierre;
            }
            doShrine(true);
        } else if (mobid == 8920000 || mobid == 8920001 ||  mobid == 8920002 || mobid == 8920003 && smr.mapid == 105200710) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_Queen;
            }
            doShrine(true);
        } else if (mobid == 8930100 && smr.mapid == 105200410) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Easy_Vellum;
            }
            doShrine(true);
        } else if (mobid == 8930000 && smr.mapid == 105200810) {

            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(39);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_Vellum;
            }
            doShrine(true);
        } else if (mobid == 9421586 && smr.mapid == 807300110) { // Ranmaru
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "The brave warriors of Ereve destroyed Ranmaru and now peace sorrounds the vail."));
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Ranmaru;
            }
            doShrine(true);
        } else if (mobid == 8840000 && smr.mapid == 211070100) {
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(38);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Von_Leon;
            }
            doShrine(true);
        } else if (mobid == 8800002 && smr.mapid == 280030000) {
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    // c.finishAchievement(15);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
//            FileoutputUtil.log(FileoutputUtil.Zakum_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.Zakum;
            }
            doShrine(true);
        } else if (mobid == 8800102 && smr.mapid == 280030001) {
            charactersLock.readLock().lock();
            try {
                for (User c : characters) {
                    //c/.finishAchievement(23);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            //FileoutputUtil.log(FileoutputUtil.Zakum_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_Zakum;
            }

            doShrine(true);
            /*} else if (mobid >= 9400903 && mobid <= 9400910) {
             boolean makeZakReal = true;
             final Collection<MapleMonster> monsters = getAllMonsters();

             for (final MapleMonster mons : monsters) {
             if (mons.getId() >= 9400903 && mons.getId() <= 9400910) {
             makeZakReal = false;
             break;
             }
             }
             if (makeZakReal) {
             for (final MapleMapObject object : monsters) {
             final MapleMonster mons = ((MapleMonster) object);
             if (mons.getId() == 9400900) {
             final Point pos = mons.getTruePosition();
             this.killAllMonsters(true);
             spawnMonsterOnGroundBelow(LifeFactory.getMonster(9400900), pos);
             break;
             }
             }
             }*/
        } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);

            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final Mob mons = ((Mob) object);
                    if (mons.getId() == 8800000) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(LifeFactory.getMonster(8800000), pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);

            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getId() >= 8800103 && mons.getId() <= 8800110) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (MapleMapObject o : monsters) {
                    final Mob mons = (Mob) o;

                    if (mons.getId() == 8800100) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(LifeFactory.getMonster(8800100), pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 8800023 && mobid <= 8800030) {
            boolean makeZakReal = true;
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);

            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getId() >= 8800023 && mons.getId() <= 8800030) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (MapleMapObject object : monsters) {

                    final Mob mons = ((Mob) object);
                    if (mons.getId() == 8800020) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(LifeFactory.getMonster(8800020), pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 9400903 && mobid <= 9400910) {
            boolean makeZakReal = true;
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);

            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getId() >= 9400903 && mons.getId() <= 9400910) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final Mob mons = ((Mob) object);
                    if (mons.getId() == 9400900) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(LifeFactory.getMonster(9400900), pos);
                        break;
                    }
                }
            }
        } else if (mobid == 8820008) { //wipe out statues and respawn
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);
            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            final List<MapleMapObject> monsters = this.getAllMapObjects(MapleMapObjectType.MONSTER);
            for (MapleMapObject o : monsters) {
                final Mob mons = (Mob) o;

                if (mons.getId() != 8820000 && mons.getId() != 8820001 && mons.getObjectId() != monster.getObjectId() && mons.isAlive() && mons.getLinkOid() == monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid / 100000 == 98 && chr.getMapId() / 10000000 == 95 && this.getAllMapObjectSize(MapleMapObjectType.MONSTER) == 0) {
            switch ((chr.getMapId() % 1000) / 100) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    chr.getClient().SendPacket(CField.MapEff("monsterPark/clear"));
                    break;
                case 5:
                    if (chr.getMapId() / 1000000 == 952) {
                        chr.getClient().SendPacket(CField.MapEff("monsterPark/clearF"));
                    } else {
                        chr.getClient().SendPacket(CField.MapEff("monsterPark/clear"));
                    }
                    break;
                case 6:
                    chr.getClient().SendPacket(CField.MapEff("monsterPark/clearF"));
                    break;
            }
        }
        if (type != null) {
            if (speedRunStart > 0 && speedRunLeader.length() > 0) {
                long endTime = System.currentTimeMillis();
                String time = StringUtil.getReadableMillis(speedRunStart, endTime);
                broadcastPacket(WvsContext.broadcastMsg(5, speedRunLeader + "'s squad has taken " + time + " to defeat " + type.name() + "!"));
                getRankAndAdd(speedRunLeader, time, type, (endTime - speedRunStart), (sqd == null ? null : sqd.getMembers()));
                endSpeedRun();
            }

        }

        if (withDrops && dropOwner != 1) {
            User drop;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            dropFromMonster(drop, monster, instanced);

            if (drop != null) {
                int mobCharLvDifference = chr.getLevel() - monster.getStats().getLevel();
                if (mobCharLvDifference >= -10 && mobCharLvDifference <= 20) { // level difference must be within 20
                    // Elite boss
                    int currentEliteCount = eliteBoss_KillCount.addAndGet(1);
                    if (currentEliteCount % 1000 == 0) { // for every 1000 mobs, spawn a boss
                        eliteBossCount++;

                        if (eliteBossCount == 20) { // prepare spawn elite boss
                            eliteBossCount = 0;
                        } else {

                        }
                    }
                }
            }
        }
    }

    public List<RuneStone> getAllRune() {
        return getAllRuneThreadsafe();
    }

    public List<RuneStone> getAllRuneThreadsafe() {
        ArrayList<RuneStone> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.RUNE).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.RUNE).values()) {
                ret.add((RuneStone) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.RUNE).readLock().unlock();
        }
        return ret;
    }

    public void killAllMonsters(final boolean animate) {
        for (MapleMapObject monstermo : this.getAllMapObjects(MapleMapObjectType.MONSTER)) {
            final Mob monster = (Mob) monstermo;

            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            if (GameConstants.isAzwanMap(smr.mapid)) {
                broadcastPacket(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0, true));
            } else {
                broadcastPacket(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0, false));
            }
            removeMapObject(monster);
            monster.killed();
        }
    }

    public void killMonster(final int monsId) {
        for (MapleMapObject monstermo : this.getAllMapObjects(MapleMapObjectType.MONSTER)) {
            final Mob monster = (Mob) monstermo;

            if (monster.getId() == monsId) {
                spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(monster);
                if (GameConstants.isAzwanMap(smr.mapid)) {
                    broadcastPacket(MobPacket.killMonster(monster.getObjectId(), 1, true));
                } else {
                    broadcastPacket(MobPacket.killMonster(monster.getObjectId(), 1, false));
                }
                monster.killed();
                break;
            }
        }
    }

    private String mapDebug_Log() {
        final StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(LocalDateTime.now().toString());

        sb.append(" | Mapid : ").append(smr.mapid);

        charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(characters.size()).append("] | ");
            for (User mc : characters) {
                sb.append(mc.getName()).append(", ");
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return sb.toString();
    }

    public final void limitReactor(final int rid, final int num) {
        List<Reactor> toDestroy = new ArrayList<>();
        Map<Integer, Integer> contained = new LinkedHashMap<>();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.REACTOR).values().stream().map((obj) -> (Reactor) obj).forEach((mr) -> {
                if (contained.containsKey(mr.getReactorId())) {
                    if (contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                    } else {
                        contained.put(mr.getReactorId(), contained.get(mr.getReactorId()) + 1);
                    }
                } else {
                    contained.put(mr.getReactorId(), 1);
                }
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (Reactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactors(final int first, final int last) {
        for (MapleMapObject o : this.getAllMapObjects(MapleMapObjectType.REACTOR)) {
            Reactor mr = (Reactor) o;

            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactor(final int oid) {
        final Reactor reactor = getReactorByOid(oid);
        if (reactor == null) {
            return;
        }
        broadcastPacket(CField.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public final void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public final void reloadReactors() {
        List<Reactor> toSpawn = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final Reactor reactor = (Reactor) obj;
                broadcastPacket(CField.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (Reactor r : toSpawn) {
            removeMapObject(r);
            if (!r.isCustom()) { //guardians cpq
                respawnReactor(r);
            }
        }
    }

    /*
     * command to reset all item-reactors in a map to state 0 for GM/NPC use - not tested (broken reactors get removed
     * from mapobjects when destroyed) Should create instances for multiple copies of non-respawning reactors...
     */
    public final void resetReactors() {
        setReactorState((byte) 0);
    }

    public final void setReactorState() {
        setReactorState((byte) 1);
    }

    public final void setReactorState(final byte state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.REACTOR).values().stream().forEach((obj) -> {
                ((Reactor) obj).forceHitReactor((byte) state);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void setReactorDelay(final int state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.REACTOR).values().stream().forEach((obj) -> {
                ((Reactor) obj).setDelay(state);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map for PQ purposes (such as ZPQ/LMPQ)
     */
    public final void shuffleReactors() {
        shuffleReactors(0, 9999999); //all
    }

    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.REACTOR).values().stream().map((obj) -> (Reactor) obj).filter((mr) -> (mr.getReactorId() >= first && mr.getReactorId() <= last)).forEach((mr) -> {
                points.add(mr.getPosition());
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.REACTOR).values().stream().map((obj) -> (Reactor) obj).filter((mr) -> (mr.getReactorId() >= first && mr.getReactorId() <= last)).forEach((mr) -> {
                mr.setPosition(points.remove(points.size() - 1));
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * Automatically finds a new controller for the given monster from the chars on the map...
     *
     * @param monster
     */
    public final void updateMonsterController(final Mob monster) {
        if (!monster.isAlive() || monster.getLinkCID() > 0 || monster.getStats().isEscort()) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this || monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > monster.getRange()) {
                monster.getController().stopControllingMonster(monster);
            } else { // Everything is fine :)
                return;
            }
        }
        int mincontrolled = -1;
        User newController = null;

        charactersLock.readLock().lock();
        try {
            final Iterator<User> ltr = characters.iterator();
            User chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isHidden() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1) && chr.getTruePosition().distanceSq(monster.getTruePosition()) <= monster.getRange()) {
                    mincontrolled = chr.getControlledSize();
                    newController = chr;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public void spawnNpc(final int id, final Point pos) {
        final NPCLife npc = LifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(smr.footholds.findBelow(pos).getId());
        addMapObject(npc);
        npc.setCustom(true);
        broadcastPacket(NPCPacket.spawnNPC(npc, true));
    }

    public void spawnNpcForPlayer(ClientSocket c, final int id, final Point pos) {
        final NPCLife npc = LifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(smr.footholds.findBelow(pos).getId());
        addMapObject(npc);
        npc.setCustom(true);
        c.SendPacket(NPCPacket.spawnNPC(npc, true));
    }

    public void spawnNpcOnMapLoad(NPCLife life) {
        spawnAndAddRangedMapObject(life, (ClientSocket c1) -> {
            life.sendSpawnData(c1);
        });
    }

    public final void removeNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                NPCLife npc = (NPCLife) itr.next();
                if (npc.isCustom() && (npcid == -1 || npc.getId() == npcid)) {
                    broadcastPacket(NPCPacket.removeNPCController(npc.getObjectId()));
                    broadcastPacket(NPCPacket.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    // Original Methods
    /*public void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = LifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(smr.footholds.findBelow(pos).getId());
        addMapObject(npc);
        broadcastMessage(NPCPacket.spawnNPC(npc, true));
    }

    public void spawnNpcForPlayer(ClientSocket c, final int id, final Point pos) {
        final MapleNPC npc = LifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(smr.footholds.findBelow(pos).getId());
        addMapObject(npc);
        c.write(NPCPacket.spawnNPC(npc, true));
    }

    public void spawnNpcOnMapLoad(MapleNPC life) {
        spawnAndAddRangedMapObject(life, (Client c1) -> {
            life.sendSpawnData(c1);
        });
    }

    public void removeNpc(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npcid == -1 || npc.getId() == npcid) {
                    broadcastMessage(NPCPacket.removeNPCController(npc.getObjectId()));
                    broadcastMessage(NPCPacket.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }*/
    public void hideNpc(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                NPCLife npc = (NPCLife) itr.next();
                if (npcid == -1 || npc.getId() == npcid) {
                    broadcastPacket(NPCPacket.removeNPCController(npc.getObjectId()));
                    broadcastPacket(NPCPacket.removeNPC(npc.getObjectId()));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public final void hideNpc(ClientSocket c, final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                NPCLife npc = (NPCLife) itr.next();
                if (npcid == -1 || npc.getId() == npcid) {
                    c.SendPacket(NPCPacket.removeNPCController(npc.getObjectId()));
                    c.SendPacket(NPCPacket.removeNPC(npc.getObjectId()));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public void spawnReactorOnGroundBelow(final Reactor mob, final Point pos) {
        mob.setPosition(pos); //reactors dont need FH lol
        mob.setCustom(true);
        spawnReactor(mob);
    }

    public void spawnMonster_sSack(final Mob mob, final Point pos, final int spawnType) {
        mob.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
        mob.setFh(smr.footholds.findBelow(pos).getId());
        OnSpawnMonster(mob, spawnType, 100);
    }
    
    public void spawnMonster_sSack(final Mob mob, final Point pos, final int spawnType, final int nMobScale) {
        mob.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
        mob.setFh(smr.footholds.findBelow(pos).getId());
        OnSpawnMonster(mob, spawnType, nMobScale);
    }

    public void spawnObtacleAtom() {
        CField.spawnObtacleAtomBomb();
    }

    public void spawnMonsterOnGroundBelow(final Mob mob, final Point pos) {
        spawnMonster_sSack(mob, pos, -2, 100);
    }
    
    public void spawnMonsterOnGroundBelow(final Mob mob, final Point pos, final int nMobScale) {
        spawnMonster_sSack(mob, pos, -2, nMobScale);
    }

    public int spawnMonsterWithEffectBelow(final Mob mob, final Point pos, final int effect) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos);
    }

    public void spawnClockMist(final Mist clock) {
        spawnAndAddRangedMapObject(clock, new DelayedPacketCreation() {
            @Override
            public void sendPackets(ClientSocket c) {
                c.SendPacket(CField.spawnClockMist(clock));
            }
        });
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastPacket(CField.removeMist(clock.getObjectId(), false));
                removeMapObject(clock);
            }
        }, 22000);
    }

    public void spawnZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final Mob mainb = LifeFactory.getMonster(8800000);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007,
            8800008, 8800009, 8800010};

        for (final int i : zakpart) {
            final Mob part = LifeFactory.getMonster(i);
            part.setPosition(spos);

            OnSpawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnPinkZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final Mob mainb = LifeFactory.getMonster(9400900);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {9400903, 9400904, 9400905, 9400906, 9400907,
            9400908, 9400909, 9400910};

        for (final int i : zakpart) {
            final Mob part = LifeFactory.getMonster(i);
            part.setPosition(spos);

            OnSpawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnEasyZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final Mob mainb = LifeFactory.getMonster(8800020);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800023, 8800024, 8800025, 8800026, 8800027,
            8800028, 8800029, 8800030};

        for (final int i : zakpart) {
            final Mob part = LifeFactory.getMonster(i);
            part.setPosition(spos);

            OnSpawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnChaosZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final Mob mainb = LifeFactory.getMonster(8800100);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800103, 8800104, 8800105, 8800106, 8800107,
            8800108, 8800109, 8800110};

        for (final int i : zakpart) {
            final Mob part = LifeFactory.getMonster(i);
            part.setPosition(spos);

            OnSpawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public void spawnFakeMonsterOnGroundBelow(final Mob mob, final Point pos) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    private void checkRemoveAfter(final Mob monster) {
        final int ra = monster.getStats().getRemoveAfter();

        if (ra > 0 && monster.getLinkCID() <= 0) {
            monster.registerKill(ra * 1000);
        }
    }

    /**
     * Mob Respawning
     * @param monster
     * @param oid 
     */
    public void spawnRevives(final Mob monster, final int oid) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                
                if (GameConstants.isAzwanMap(c.getPlayer().getMapId())) {
                    c.SendPacket(MobPacket.spawnMonster(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid, true)); // TODO effect
                } else {
                    c.SendPacket(MobPacket.spawnMonster(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid, false)); // TODO effect
                }
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final void OnSpawnEliteMonster(Mob pMob, int nSpawnType) {
        OnSpawnMonster(pMob, nSpawnType, true, null, 250);
    }
    
    public final void OnSpawnMonster(Mob pMob, int nSpawnType) {
        OnSpawnMonster(pMob, nSpawnType, true, null, 100);
    }
    
    public final void OnSpawnMonster(Mob pMob, int nSpawnType, int nMobScale) {
        OnSpawnMonster(pMob, nSpawnType, true, null, nMobScale);
    }

    /**
     * OnSpawnMonster
     * 
     * @param pMob
     * @param nSpawnType
     * @param bOverwrite
     * @param pPlayer 
     * @param nScale
     */
    public final void OnSpawnMonster(final Mob pMob, final int nSpawnType, final boolean bOverwrite, final User pPlayer, int nScale) {
        
        boolean bBuffedMonster = BuffedMob.OnBuffedMobRequest(pMob, this.channel);
        final Mob pMonster = bBuffedMonster ? BuffedMob.OnBuffedMobResult(pMob) : pMob;
        
        if (this.getId() == 109010100 && pMonster.getId() != 9300166) return;

        pMonster.setMap(this);
        checkRemoveAfter(pMonster);

        if (pMonster.getId() == 9300166) {
            MapTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    broadcastPacket(MobPacket.killMonster(pMonster.getObjectId(), 2, false));
                    removeMapObject(pMonster);
                }
            }, this.getId() == 109010100 ? 1500 /*Bomberman*/ : this.getId() == 910025200 ? 200 /*The Dragon's Shout*/ : 3000);
        }
        
        spawnAndAddRangedMapObject(pMonster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                if (GameConstants.isAzwanMap(c.getPlayer().getMapId())) {
                    c.SendPacket(MobPacket.spawnMonster(pMonster, pMonster.getStats().getSummonType() <= 1 || pMonster.getStats().getSummonType() == 27 || bOverwrite ? nSpawnType : pMonster.getStats().getSummonType(), 0, true, nScale));
                } else if (GameConstants.isChangeable(pMonster.getId())) {
                    c.SendPacket(MobPacket.spawnMonster(pMonster, pMonster.getStats().getSummonType() <= 1 || pMonster.getStats().getSummonType() == 27 || true ? nSpawnType : pMonster.getStats().getSummonType(), 0, false, nScale));
                } else {
                    c.SendPacket(MobPacket.spawnMonster(pMonster, pMonster.getStats().getSummonType() <= 1 || pMonster.getStats().getSummonType() == 27 || bOverwrite ? nSpawnType : pMonster.getStats().getSummonType(), 0, false, nScale));
                }
            }
        });
        updateMonsterController(pMonster);

        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public final int spawnMonsterWithEffect(final Mob monster, final int effect, Point pos) {
        try {
            monster.setMap(this);
            monster.setPosition(pos);

            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
                @Override
                public final void sendPackets(ClientSocket c) {
                    if (GameConstants.isAzwanMap(c.getPlayer().getMapId())) {
                        c.SendPacket(MobPacket.spawnMonster(monster, effect, 0, true));
                    } else {
                        c.SendPacket(MobPacket.spawnMonster(monster, effect, 0, false));
                    }
                }
            });
            updateMonsterController(monster);

            spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        } catch (Exception e) {
            return -1;
        }
    }

    public final void spawnFakeMonster(final Mob monster) {
        monster.setMap(this);
        monster.setFake(true);

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                if (GameConstants.isAzwanMap(c.getPlayer().getMapId())) {
                    c.SendPacket(MobPacket.spawnMonster(monster, -4, 0, true));
                } else {
                    c.SendPacket(MobPacket.spawnMonster(monster, -4, 0, false));
                }
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnRandomHerbOrVien(final Point pos) {
        final int Id = smr.getRandomherbsAndViens();

        final MapleReactorStats stats = MapleReactorFactory.getReactor(Id);
        final Reactor myReactor = new Reactor(stats, Id);

        myReactor.setFacingLeft(Randomizer.nextInt(1) == 0);
        myReactor.setisProfessions(true);
        myReactor.setDelay(0); // never respawn
        myReactor.setName("");
        myReactor.setState((byte) 0);
        myReactor.setPosition(pos);

        spawnReactor(myReactor);
    }

    public final void spawnReactor(final Reactor reactor) {
        reactor.setMap(this);

        spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                c.SendPacket(CField.spawnReactor(reactor));
            }
        });
    }

    private void respawnReactor(final Reactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public final void spawnDoor(final Door door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                door.sendSpawnData(c);
                c.SendPacket(WvsContext.enableActions());
            }
        });
    }

    public final void spawnMechDoor(final MechDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {
            @Override
            public final void sendPackets(ClientSocket c) {
                c.SendPacket(CField.spawnMechDoor(door, true));
                c.SendPacket(WvsContext.enableActions());
            }
        });
    }

    public final void spawnSummon(final Summon pSummon) {
        spawnSummon(pSummon, false);
    }
    
    public final void spawnSummon(final Summon pSummon, boolean bSpecialSummon) {
        pSummon.updateMap(this);
        spawnAndAddRangedMapObject(pSummon, new DelayedPacketCreation() {
            @Override
            public void sendPackets(ClientSocket c) {
                if (pSummon != null && c.getPlayer() != null && (!pSummon.isChangedMap() || pSummon.getOwnerId() == c.getPlayer().getId())) {
                    c.SendPacket(SummonPacket.spawnSummon(pSummon, true, bSpecialSummon));
                }
            }
        });
    }

    public final void spawnFamiliar(final MonsterFamiliar familiar, final boolean respawn) {
        spawnAndAddRangedMapObject(familiar, (ClientSocket c1) -> {
            if (familiar != null && c1.getPlayer() != null) {
                c1.SendPacket(CField.spawnFamiliar(familiar, true, respawn));
            }
        });
    }

    public final void spawnExtractor(final Extractor ex) {
        spawnAndAddRangedMapObject(ex, (ClientSocket c1) -> {
            ex.sendSpawnData(c1);
        });
    }

    public final void spawnMapleKite(int ItemID, String PlayerName, MapleMap Map, String Message, byte KiteType, Point position) {
        final Kite kite = new Kite(ItemID, this.getId(), PlayerName, this, Message, KiteType, position);

        spawnAndAddRangedMapObject(kite, (ClientSocket c1) -> {
            kite.sendSpawnData(c1);
        });
    }

    public final void spawnMist(final Mist mist, final int duration, boolean fake) {
        spawnAndAddRangedMapObject(mist, (ClientSocket c1) -> {
            mist.sendSpawnData(c1);
        });

        final MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        switch (mist.isPoisonMist()) {
            case 1:
                //poison: 0 = none, 1 = poisonous, 2 = recovery
                final User owner = getCharacterById(mist.getOwnerId());
                final boolean pvp = owner.inPVP();
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER))) {
                            if (pvp && mist.makeChanceResult() && !((User) mo).hasDOT() && ((User) mo).getId() != mist.getOwnerId()) {
                                ((User) mo).setDOT(mist.getSource().getDOT(), mist.getSourceSkill().getId(), mist.getSkillLevel());
                            } else if (!pvp && mist.makeChanceResult() && !((Mob) mo).isBuffed(MonsterStatus.POISON)) {
                                ((Mob) mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, duration, true, mist.getSource());
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 4:
                poisonSchedule = tMan.register(new Runnable() {
                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final User chr = ((User) mo);
                                chr.addMP((int) (mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0)));
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            default:
                poisonSchedule = null;
                break;
        }
        mist.setPoisonSchedule(poisonSchedule);
        mist.setSchedule(tMan.schedule(new Runnable() {
            @Override
            public void run() {
                broadcastPacket(CField.removeMist(mist.getObjectId(), false));
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, duration));
    }

    /**
     * Spawns an item that disappears immediately upon dropped -- eg: quest item
     *
     * @param dropper
     * @param owner
     * @param item
     * @param pos
     */
    public final void disappearingItemDrop(MapleMapObject dropper, User owner, Item item, Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, owner, (byte) 1, -1);

        broadcastPacket(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 3), drop.getTruePosition());
    }

    /**
     * Spawns meso from a monster drop to the map.
     *
     * @param meso
     * @param position
     * @param dropper
     * @param owner
     * @param playerDrop
     * @param droptype
     */
    public final void spawnMesoDrop(int meso, Point position, MapleMapObject dropper, User owner, boolean playerDrop, byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, owner, droptype);
        if (playerDrop) {
            mdrop.setProperties(MapItemType.IsPlayerDrop);
        }

        if (dropper != null) {
            if (dropper instanceof Mob) {
                mdrop.setProperties(MapItemType.IsPickpocketDrop);
            }
        }
        spawnAndAddRangedMapObject(mdrop, (ClientSocket c1) -> {
            c1.SendPacket(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), droppos, (byte) 1));
        });
        if (!smr.everlast) {
            mdrop.registerExpire(GameConstants.mapItemExpiration_ms);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    /**
     * Spawns a monster drop to the map
     *
     * @param idrop
     * @param dropPos
     * @param mob
     * @param chr
     * @param droptype
     * @param questid
     * @param global_drop
     */
    public final void spawnMobDrop(Item idrop, Point dropPos, Mob mob, User chr, byte droptype, int questid, boolean global_drop) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, chr, droptype, questid);
        if (mob.getStats().isBoss()) {
            mdrop.setProperties(MapItemType.IsBossDrop);
        }
        if (global_drop) {
            mdrop.setProperties(MapItemType.IsGlobalDrop);
        }
        // if mob is elite boss
        // mdrop.setProperties(MapleMapItemProperties.IsEliteBossDrop);

        spawnAndAddRangedMapObject(mdrop, (ClientSocket c1) -> {
            if (c1 != null && c1.getPlayer() != null && (questid <= 0 || c1.getPlayer().getQuestStatus(questid) == QuestState.Started) && (idrop.getItemId() / 10000 != 238 || c1.getPlayer().getMonsterBook().getLevelByCard(idrop.getItemId()) >= 2) && mob != null && dropPos != null) {
                c1.SendPacket(CField.dropItemFromMapObject(mdrop, mob, mob.getTruePosition(), dropPos, (byte) 1));
            }
        });
//	broadcastMessage(CField.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 0));

        mdrop.registerExpire(global_drop ? GameConstants.mapItemExpirationGlobalDrops_ms : GameConstants.mapItemExpiration_ms);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
        activateItemReactors(mdrop, chr.getClient());
    }

    /**
     * Spawns an item drop to the map -- dropped by the player
     *
     * @param dropper
     * @param owner
     * @param item
     * @param pos
     * @param ffaDrop
     * @param playerDrop
     * @param isCollisionPickUp
     */
    public void spawnItemDrop(MapleMapObject dropper, User owner, Item item, Point pos, boolean ffaDrop, boolean playerDrop, boolean isCollisionPickUp) {
        spawnItemDropInternal(dropper, owner, false, item, pos, ffaDrop, playerDrop, isCollisionPickUp);
    }

    private void spawnItemDropInternal(MapleMapObject dropper, User owner, boolean showToOwnerOnly, Item item, Point pos,
            boolean ffaDrop, boolean playerDrop, boolean isCollisionPickUp) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, owner, (byte) 2, -1);
        if (playerDrop) {
            drop.setProperties(MapItemType.IsPlayerDrop);
        }
        if (isCollisionPickUp) {
            drop.setProperties(MapItemType.IsCollisionPickUp);
        }

        spawnAndAddRangedMapObject(drop, (ClientSocket c1) -> {
            if ((showToOwnerOnly && owner != null && owner.getId() == c1.getPlayer().getId())
                    || !showToOwnerOnly) {
                c1.SendPacket(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        if (showToOwnerOnly) {
            broadcastPacket(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));
        }

        if (!smr.everlast) {
            drop.registerExpire(GameConstants.mapItemExpiration_ms);
            activateItemReactors(drop, owner.getClient());
        }
    }

    public final void spawnRandDrop() {
        if (smr.mapid != 910000000 || channel != 1) {
            return; //fm, ch1
        }

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem) o).isRandomDrop()) {
                    return;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                final Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
                final int theItem = Randomizer.nextInt(1000);
                int itemid = 0;
                if (theItem < 950) { //0-949 = normal, 950-989 = rare, 990-999 = super
                    itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
                } else if (theItem < 990) {
                    itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
                } else {
                    itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
                }
                spawnAutoDrop(itemid, pos);
            }
        }, 20000);
    }

    public final void spawnAutoDrop(final int itemid, final Point pos) {
        Item idrop = null;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == InventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        idrop.setGMLog("Dropped from auto " + " on " + smr.mapid);

        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        spawnAndAddRangedMapObject(mdrop, (ClientSocket c1) -> {
            c1.SendPacket(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 1));
        });
        broadcastPacket(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 0));
        if (itemid / 10000 != 291) {
            mdrop.registerExpire(GameConstants.mapItemExpiration_ms);
        }
    }

    private void activateItemReactors(final MapleMapItem drop, final ClientSocket c) {
        final Item item = drop.getItem();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final Reactor react = (Reactor) o;

                if (react.getReactorType() == 100) {
                    if (item.getItemId() == react.getReactItem().getLeft() && react.getReactItem().getRight() == item.getQuantity()) {
                        if (react.getArea().contains(drop.getTruePosition())) {
                            if (!react.isTimerActive()) {
                                MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                                react.setTimerActive(true);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public List<MapleMapItem> getAllItems() {
        ArrayList<MapleMapItem> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.ITEM).values().stream().forEach((mmo) -> {
                ret.add((MapleMapItem) mmo);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public Point getPointOfItem(int itemid) {
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                MapleMapItem mm = ((MapleMapItem) mmo);
                if (mm.getItem() != null && mm.getItem().getItemId() == itemid) {
                    return mm.getPosition();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return null;
    }

    public List<Mist> getAllMists() {
        ArrayList<Mist> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.MIST).values().stream().forEach((mmo) -> {
                ret.add((Mist) mmo);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }

    public final void returnEverLastItem(final User chr) {
        for (final MapleMapObject o : getAllItems()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);
                broadcastPacket(CField.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getTruePosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
        spawnRandDrop();
    }

    public final void talkMonster(final String msg, final int itemId, final int objectid) {
        if (itemId > 0) {
            startMapEffect(msg, itemId, false);
        }
        broadcastPacket(MobPacket.talkMonster(objectid, itemId, msg)); //5120035
        broadcastPacket(MobPacket.removeTalkMonster(objectid));
    }

    public final void startMapEffect(final String msg, final int itemId) {
        startMapEffect(msg, itemId, false);
    }

    public final void startMapEffect(final String msg, final int itemId, final boolean jukebox) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        mapEffect.setJukebox(jukebox);
        broadcastPacket(mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (mapEffect != null) {
                    broadcastPacket(mapEffect.makeDestroyData());
                    mapEffect = null;
                }
            }
        }, jukebox ? 100000 : 10000);
    }

    public final void startExtendedMapEffect(final String msg, final int itemId) {
        broadcastPacket(CField.startMapEffect(msg, itemId, true));
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastPacket(CField.removeMapEffect());
                broadcastPacket(CField.startMapEffect(msg, itemId, false));
                //dont remove mapeffect.
            }
        }, 30000);
    }

    public final void startSimpleMapEffect(final String msg, final int itemId) {
        broadcastPacket(CField.startMapEffect(msg, itemId, true));
    }

    public final void startJukebox(final String msg, final int itemId) {
        startMapEffect(msg, itemId, true);
    }

    public final void addPlayer(final User chr) {
        addPlayer(chr, -1); // cross compatible, in case some moogra script still calling it x_X
    }

    /**
     * Adds the player to the map
     *
     * @param pPlayer
     * @param lastmapid last map where the player came [This variable is handy for map scripts, in case the player reloads the map by
     * jumping down the platform]
     */
    public final void addPlayer(final User pPlayer, int lastmapid) {
        TrainingMap.OnMonsterAggressionRequest(pPlayer); // Aggro all mobs to player.
        
        /*if (ServerConstants.ANTI_CHEAT) {
            pPlayer.write(AntiCheat.bannedProccessRequest()); // Check for hack programs.
        }*/
        
        // Update burning field state first, before adding player
        // as the respawn task of the map is not activated if no player is currently in it [optimize for resource usage]
        updateBurningField(System.currentTimeMillis());

        // Add the player object to mapping
        mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.PLAYER).put(pPlayer.getObjectId(), pPlayer);
        } finally {
            mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().unlock();
        }

        charactersLock.writeLock().lock();
        try {
            characters.add(pPlayer);
        } finally {
            charactersLock.writeLock().unlock();
        }

        //chr.dropMessage(5, "mob rate: "+ smr.monsterRate + " Size: " + monsterSpawn.size());
        pPlayer.setChangeTime();
        if (GameConstants.isTeamMap(smr.mapid) && !pPlayer.inPVP()) {
            pPlayer.setTeam(getAndSwitchTeam() ? 0 : 1);
        }

        if (!pPlayer.isHidden()) {
            broadcastPacket(pPlayer, CField.spawnPlayerMapObject(pPlayer), false);
            if (pPlayer.isIntern() && speedRunStart > 0) {
                endSpeedRun();
                broadcastPacket(WvsContext.broadcastMsg(5, "The speed run has ended."));
            }
        } else {
            broadcastGMMessage(pPlayer, CField.spawnPlayerMapObject(pPlayer), false);
        }

        // Map Scripts Handling
        if (!pPlayer.isClone()) {
            switch (pPlayer.getMapId()) {
                case 552000010: // Tutorial Part 1
                case 552000021: // Tutorial Part 2
                case 552000020: // Tutorial Part 3
                case 552000030: // Tutorial Part 4
                case 552000040: // Tutorial Part 5
                    break; // Maps that we don't want using map scripts.
                default:
                    if (!smr.onFirstUserEnter.equals("")) {
                        if (getCharactersSize() == 1) {
                            MapScriptManager.executeMapScript(pPlayer.getClient(), smr.onFirstUserEnter, true, lastmapid);
                        }
                    }
                    if (!smr.onUserEnter.equals("")) {
                        MapScriptManager.executeMapScript(pPlayer.getClient(), smr.onUserEnter, false, lastmapid);
                    }
                    break;
            }
        }

        for (final Pet pet : pPlayer.getPets()) {
            if (pet.getSummoned()) {
                broadcastPacket(pPlayer, PetPacket.OnActivated(pPlayer.id, pPlayer.getPetIndex(pet), true, pet, 0), false);
            }
        }
        if (pPlayer.getSummonedFamiliar() != null) {
            pPlayer.spawnFamiliar(pPlayer.getSummonedFamiliar(), true);
        }
        if (pPlayer.getAndroid() != null) {
            pPlayer.getAndroid().setPos(pPlayer.getPosition());
            broadcastPacket(CField.spawnAndroid(pPlayer, pPlayer.getAndroid()));
        }
        if (pPlayer.getParty() != null) {
            pPlayer.silentPartyUpdate();
            pPlayer.getClient().SendPacket(PartyPacket.updateParty(pPlayer.getClient().getChannel(), pPlayer.getParty(), PartyOperation.SILENT_UPDATE, null));
            pPlayer.updatePartyMemberHP();
            pPlayer.receivePartyMemberHP();
        }
        if (!pPlayer.isInBlockedMap() && pPlayer.getLevel() >= 10) {
            for (QuickMove qm : QuickMove.values()) {
                if (qm.getMap() == pPlayer.getMapId() || ServerConstants.QUICKMOVE_EVERYWHERE) {
                    List<QuickMoveNPC> qmn = new LinkedList<>();
                    int npcs = qm.getNPCFlag();
                    for (QuickMoveNPC npc : QuickMoveNPC.values()) {
                        if ((npcs & npc.getValue()) != 0 && npc.show()) {
                            qmn.add(npc);
                        }
                    }
                    pPlayer.getClient().SendPacket(CField.getQuickMoveInfo(true, qmn));
                    break;
                }
            }
        } else {
            pPlayer.getClient().SendPacket(CField.getQuickMoveInfo(false, new LinkedList<>()));
        }

        /*for (int npc : GameConstants.unusedNpcs) {
            if (getNPCById(npc) != null && getId() != 910000000) {
                chr.getClient().write(CField.NPCPacket.toggleNPCShow(getNPCById(npc).getObjectId(), true));
                //hide unused npcs
            }
        }*/
        if (getNPCById(9073000) != null && getId() == 931050410) {
            pPlayer.getClient().SendPacket(CField.NPCPacket.toggleNPCShow(getNPCById(9073000).getObjectId(), true));
        }
        if (GameConstants.isPhantom(pPlayer.getJob())) {
            pPlayer.getClient().SendPacket(PhantomPacket.updateCardStack(pPlayer.getCardStack()));
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(pPlayer.getClient());
        }
        if (smr.timeLimit > 0 && getForcedReturnMap() != null) {
            pPlayer.startMapTimeLimitTask(smr.timeLimit, getForcedReturnMap());
        }
        if (pPlayer.getBuffedValue(CharacterTemporaryStat.RideVehicle) != null && !GameConstants.isResistance(pPlayer.getJob())) {
            if (FieldLimitType.UnableToUseTamingMob.check(smr.fieldLimit)) {
                pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
            }
        }
        if (pPlayer.getEventInstance() != null && pPlayer.getEventInstance().isTimerStarted()) {
            if (pPlayer.inPVP()) {
                pPlayer.getClient().SendPacket(CField.getPVPClock(Integer.parseInt(pPlayer.getEventInstance().getProperty("type")), (int) (pPlayer.getEventInstance().getTimeLeft() / 1000)));
            } else {
                pPlayer.getClient().SendPacket(CField.getClock((int) (pPlayer.getEventInstance().getTimeLeft() / 1000)));
            }
        }
        if (smr.clock) {
            final Calendar cal = Calendar.getInstance();
            pPlayer.getClient().SendPacket((CField.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
        }
        if (pPlayer.getCarnivalParty() != null && pPlayer.getEventInstance() != null) {
            pPlayer.getEventInstance().onMapLoad(pPlayer);
        }
        MapleEvent.mapLoad(pPlayer, channel);
        if (getSquadBegin() != null && getSquadBegin().getTimeLeft() > 0 && getSquadBegin().getStatus() == 1) {
            pPlayer.getClient().SendPacket(CField.getClock((int) (getSquadBegin().getTimeLeft() / 1000)));
        }
        if (smr.mapid / 1000 != 105100 && smr.mapid / 100 != 8020003 && smr.mapid / 100 != 8020008 && smr.mapid != 271040100) { //no boss_balrog/2095/coreblaze/auf/cygnus. but coreblaze/auf/cygnus does AFTER
            final MapleSquad sqd = getSquadByMap(); //for all squads
            final EventManager em = getEMByMap();
            if (!squadTimer && sqd != null && pPlayer.getName().equals(sqd.getLeaderName()) && em != null && em.getProperty("leader") != null && em.getProperty("leader").equals("true") && checkStates) {
                //leader? display
                doShrine(false);
                squadTimer = true;
            }
        }
        if (getAllMapObjectSize(MapleMapObjectType.MONSTER) > 0 && (smr.mapid == 280030001 || smr.mapid == 240060201 || smr.mapid == 280030000 || smr.mapid == 240060200 || smr.mapid == 220080001 || smr.mapid == 541020800 || smr.mapid == 541010100)) {
            String music = "Bgm09/TimeAttack";
            switch (smr.mapid) {
                case 240060200:
                case 240060201:
                    music = "Bgm14/HonTale";
                    break;
                case 280030000:
                case 280030001:
                    music = "Bgm06/FinalFight";
                    break;
            }
            pPlayer.getClient().SendPacket(CField.musicChange(music));
            //maybe timer too for zak/ht
        }
        if (smr.mapid == 914000000 || smr.mapid == 927000000) {
            pPlayer.getClient().SendPacket(WvsContext.temporaryStats_Aran());
        } else if (smr.mapid == 105100300 && pPlayer.getLevel() >= 91) {
            pPlayer.getClient().SendPacket(WvsContext.temporaryStats_Balrog(pPlayer));
        } else if (smr.mapid == 140090000 || smr.mapid == 105100301 || smr.mapid == 105100401 || smr.mapid == 105100100) {
            pPlayer.getClient().SendPacket(WvsContext.temporaryStats_Reset());
        }
        if (GameConstants.isEvan(pPlayer.getJob()) && pPlayer.getJob() >= 2200) {
            if (pPlayer.getDragon() == null) {
                pPlayer.makeDragon();
            } else {
                pPlayer.getDragon().setPosition(pPlayer.getPosition());
            }
            if (pPlayer.getDragon() != null) {
                broadcastPacket(CField.spawnDragon(pPlayer.getDragon()));
            }
        }
        /*if (GameConstants.isKanna(chr.getJob())) {
            if (chr.getHaku() == null && chr.getBuffedValue(CharacterTemporaryStat.HAKU_REBORN) == null) {
                chr.makeHaku();
            } else {
                chr.getHaku().setPosition(chr.getPosition());
            }
            if (chr.getHaku() != null) {
                if (chr.getBuffSource(CharacterTemporaryStat.HAKU_REBORN) > 0) {
                    chr.getHaku().sendStats();
                    //chr.getMap().broadcastMessage(chr, CField.spawnHaku_change0(chr.getId()), true);
                    //chr.getMap().broadcastMessage(chr, CField.spawnHaku_change1(chr.getHaku()), true);
                    chr.getMap().broadcastMessage(chr, CField.transformHaku(chr.getId(), true), true);
                } else {
                    broadcastMessage(CField.spawnHaku(chr.getHaku(), false));
                }
            }
        }*/

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.out.printf("OnFirstUserEnter (%s), OnUserEnter (%s) \r\n", smr.onFirstUserEnter, smr.onUserEnter);
        }

        if (smr.mapid == 103050900) {
            pPlayer.getClient().SendPacket(CField.UIPacket.IntroEnableUI(true));
            pPlayer.getClient().SendPacket(CField.UIPacket.UserInGameDirectionEvent(1, 8000));
            pPlayer.getClient().SendPacket(CField.UIPacket.UserInGameDirectionEvent(1, 8000));
            pPlayer.getClient().SendPacket(CField.UIPacket.UserInGameDirectionEvent(3, 2));
            pPlayer.dropMessage(-1, "On A Rainy Day");
            pPlayer.dropMessage(-1, "The Secret Garden Depths");
            pPlayer.getClient().removeClickedNPC();
            pPlayer.getClient().SendPacket(CField.UIPacket.getDirectionStatus(false));
            pPlayer.getClient().SendPacket(CField.UIPacket.IntroEnableUI(false));
            pPlayer.getClient().removeClickedNPC();
            pPlayer.dropMessage(-1, "Click on ryden to get your first quest");
        }

        if (permanentWeather > 0) {
            pPlayer.getClient().SendPacket(CField.startMapEffect("", permanentWeather, false)); //snow, no msg
        }
        if (smr.footholds.getMovingPlatforms().size() > 0) {
            pPlayer.getClient().SendPacket(CField.getMovingPlatforms(this));
        }
        if (environment.size() > 0) {
            pPlayer.getClient().SendPacket(CField.getUpdateEnvironment(this));
        }
        //if (partyBonusRate > 0) {
        //    chr.dropMessage(-1, partyBonusRate + "% additional EXP will be applied per each party member here.");
        //    chr.dropMessage(-1, "You've entered the party play zone.");
        //}
        if (!smr.needSkillForFly) {
            pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.Flying);
        }
        if (burningFieldLevel > 0) {
            pPlayer.getClient().SendPacket(CField.playSound("Sound/FarmSE.img/boxResult", 100));
            pPlayer.getClient().SendPacket(CField.EffectPacket.showBurningFieldTextEffect(getBurningFieldMessage()));
        }
        if (pPlayer.getJob() < 3200 || pPlayer.getJob() > 3212) {
            //chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.AURA);
        }
        if (pPlayer.getCustomBGState() == 1) {
            pPlayer.removeBGLayers();
        }
    }

    public void doShrine(final boolean spawned) { //false = entering map, true = defeated
        if (squadSchedule != null) {
            cancelSquadSchedule(true);
        }
        final MapleSquad sqd = getSquadByMap();
        if (sqd == null) {
            return;
        }
        final int mode = (smr.mapid == 280030000 ? 1 : (smr.mapid == 280030001 ? 2 : (smr.mapid == 240060200 || smr.mapid == 240060201 ? 3 : 0)));
        //chaos_horntail message for horntail too because it looks nicer
        final EventManager em = getEMByMap();
        if (em != null && getCharactersSize() > 0) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            final Runnable run;
            MapleMap returnMapa = getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == smr.mapid) {
                returnMapa = getReturnMap();
            }
            switch (mode) {
                case 1:
                case 2:
                    //chaoszakum
                    broadcastPacket(CField.showChaosZakumShrine(spawned, 5));
                    break;
                case 3:
                    //ht/chaosht
                    broadcastPacket(CField.showChaosHorntailShrine(spawned, 5));
                    break;
                default:
                    broadcastPacket(CField.showHorntailShrine(spawned, 5));
                    break;
            }
            if (spawned) { //both of these together dont go well
                broadcastPacket(CField.getClock(300)); //5 min
            }
            final MapleMap returnMapz = returnMapa;
            if (!spawned) { //no monsters yet; inforce timer to spawn it quickly
                final List<MapleMapObject> monsterz = getAllMapObjects(MapleMapObjectType.MONSTER);
                final List<Integer> monsteridz = new ArrayList<>();
                for (MapleMapObject m : monsterz) {
                    monsteridz.add(m.getObjectId());
                }
                run = new Runnable() {
                    @Override
                    public void run() {
                        final MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && MapleMap.this.getAllMapObjectSize(MapleMapObjectType.MONSTER) == monsterz.size() && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            boolean passed = monsterz.isEmpty();
                            for (MapleMapObject m : MapleMap.this.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                                for (int i : monsteridz) {
                                    if (m.getObjectId() == i) {
                                        passed = true;
                                        break;
                                    }
                                }
                                if (passed) {
                                    break;
                                } //even one of the monsters is the same
                            }
                            if (passed) {
                                //are we still the same squad? are monsters still == 0?
                                OutPacket oPacket;
                                if (mode == 1 || mode == 2) { //chaoszakum
                                    oPacket = CField.showChaosZakumShrine(spawned, 0);
                                } else {
                                    oPacket = CField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                                }
                                for (User chr : getCharacters()) { //warp all in map
                                    chr.getClient().SendPacket(oPacket);
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                                }
                                checkStates("");
                                resetFully();
                            }
                        }

                    }
                };
            } else { //inforce timer to gtfo
                run = new Runnable() {
                    @Override
                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        //we dont need to stop clock here because they're getting warped out anyway
                        if (MapleMap.this.getCharactersSize() > 0 && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            //are we still the same squad? monsters however don't count
                            OutPacket oPacket;
                            if (mode == 1 || mode == 2) { //chaoszakum
                                oPacket = CField.showChaosZakumShrine(spawned, 0);
                            } else {
                                oPacket = CField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                            }
                            for (User chr : getCharacters()) { //warp all in map
                                chr.getClient().SendPacket(oPacket);
                                chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                            }
                            checkStates("");
                            resetFully();
                        }
                    }
                };
            }
            squadSchedule = MapTimer.getInstance().schedule(run, 300000); //5 mins
        }
    }

    public final MapleSquad getSquadByMap() {
        MapleSquadType zz = null;
        switch (smr.mapid) {
            case 105100400:
            case 105100300:
                zz = MapleSquadType.bossbalrog;
                break;
            case 280030000:
                zz = MapleSquadType.zak;
                break;
            case 280030001:
                zz = MapleSquadType.chaoszak;
                break;
            case 240060200:
                zz = MapleSquadType.horntail;
                break;
            case 240060201:
                zz = MapleSquadType.chaosht;
                break;
            case 270050100:
                zz = MapleSquadType.pinkbean;
                break;
            case 802000111:
                zz = MapleSquadType.nmm_squad;
                break;
            case 802000211:
                zz = MapleSquadType.vergamot;
                break;
            case 802000311:
                zz = MapleSquadType.tokyo_2095;
                break;
            case 802000411:
                zz = MapleSquadType.dunas;
                break;
            case 802000611:
                zz = MapleSquadType.nibergen_squad;
                break;
            case 802000711:
                zz = MapleSquadType.dunas2;
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                zz = MapleSquadType.core_blaze;
                break;
            case 802000821:
            case 802000823:
                zz = MapleSquadType.aufheben;
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                zz = MapleSquadType.vonleon;
                break;
            case 551030200:
                zz = MapleSquadType.scartar;
                break;
            case 271040100:
                zz = MapleSquadType.cygnus;
                break;
            case 262030300:
                zz = MapleSquadType.hilla;
                break;
            case 262031300:
                zz = MapleSquadType.darkhilla;
                break;
            case 272030400:
                zz = MapleSquadType.arkarium;
                break;
            default:
                return null;
        }
        return ChannelServer.getInstance(channel).getMapleSquad(zz);
    }

    public final MapleSquad getSquadBegin() {
        if (squad != null) {
            return ChannelServer.getInstance(channel).getMapleSquad(squad);
        }
        return null;
    }

    public final EventManager getEMByMap() {
        String em;
        switch (smr.mapid) {
            case 105100400:
                em = "BossBalrog_EASY";
                break;
            case 105100300:
                em = "BossBalrog_NORMAL";
                break;
            case 280030000:
                em = "ZakumBattle";
                break;
            case 240060200:
                em = "HorntailBattle";
                break;
            case 280030001:
                em = "ChaosZakum";
                break;
            case 240060201:
                em = "ChaosHorntail";
                break;
            case 270050100:
                em = "PinkBeanBattle";
                break;
            case 802000111:
                em = "NamelessMagicMonster";
                break;
            case 802000211:
                em = "Vergamot";
                break;
            case 802000311:
                em = "2095_tokyo";
                break;
            case 802000411:
                em = "Dunas";
                break;
            case 802000611:
                em = "Nibergen";
                break;
            case 802000711:
                em = "Dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                em = "CoreBlaze";
                break;
            case 802000821:
            case 802000823:
                em = "Aufhaven";
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                em = "VonLeonBattle";
                break;
            case 551030200:
                em = "ScarTarBattle";
                break;
            case 271040100:
                em = "CygnusBattle";
                break;
            case 262030300:
                em = "HillaBattle";
                break;
            case 262031300:
                em = "DarkHillaBattle";
                break;
            case 272020110:
            case 272030400:
                em = "ArkariumBattle";
                break;
            case 955000100:
            case 955000200:
            case 955000300:
                em = "AswanOffSeason";
                break;
            case 689010000:
                em = "PinkZakumEntrance";
                break;
            case 689013000:
                em = "PinkZakumFight";
                break;
            default:
                if (smr.mapid >= 262020000 && smr.mapid < 262023000) {
                    em = "Azwan";
                    break;
                }
                return null;
        }
        return ChannelServer.getInstance(channel).getEventSM().getEventManager(em);
    }

    public final void removePlayer(final User chr) {
        //log.warn("[dc] [level2] Player {} leaves map {}", new Object[] { chr.getName(), smr.mapid });

        if (smr.everlast) {
            returnEverLastItem(chr);
        }

        charactersLock.writeLock().lock();
        try {
            characters.remove(chr);
        } finally {
            charactersLock.writeLock().unlock();
        }
        removeMapObject(chr);
        chr.checkFollow();
        chr.removeExtractor();
        broadcastPacket(CField.removePlayerFromMap(chr.getId()));

        if (chr.getSummonedFamiliar() != null) {
            chr.removeVisibleFamiliar();
        }
        List<Summon> toCancel = new ArrayList<>();
        final List<Summon> ss = chr.getSummonsReadLock();
        try {
            for (final Summon summon : ss) {
                broadcastPacket(SummonPacket.removeSummon(summon, true));
                removeMapObject(summon);
                if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || summon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                    toCancel.add(summon);
                } else {
                    summon.setChangedMap(true);
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        for (Summon summon : toCancel) {
            chr.removeSummon(summon);
            chr.dispelSkill(summon.getSkill()); //remove the buff
        }
        checkStates(chr.getName());
        if (smr.mapid == 109020001) {
            chr.canTalk(true);
        }
        chr.leaveMap(this);
    }

    public final void broadcastPacket(final OutPacket packet) {
        broadcastPacket(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public final void broadcastPacket(final User source, final OutPacket packet, final boolean repeatToSource) {
        broadcastPacket(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getTruePosition());
    }

    public final void broadcastPacket(final OutPacket packet, final Point rangedFrom) {
        broadcastPacket(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public final void broadcastPacket(final User source, final OutPacket packet, final Point rangedFrom) {
        broadcastPacket(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public void broadcastPacket(final User source, final OutPacket oPacket, final double rangeSq, final Point rangedFrom) {
        charactersLock.readLock().lock();
        try {
            for (User chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getTruePosition()) <= rangeSq) {
                            if (ServerConstants.DEVELOPER_DEBUG_MODE && !ServerConstants.REDUCED_DEBUG_SPAM) System.err.println("[Debug] Sending Ranged Broadcast Packet (" + chr.getName() + ")");
                            chr.getClient().SendPacket(oPacket);
                        }
                    } else {
                        if (ServerConstants.DEVELOPER_DEBUG_MODE && !ServerConstants.REDUCED_DEBUG_SPAM) System.err.println("[Debug] Sending Broadcast Packet (" + chr.getName() + ")");
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void broadcastGMMessage(User source, OutPacket packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastGMMessage(User source, OutPacket oPacket) {
        charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (User chr : characters) {
                    if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                        System.err.println("[Debug] Sending GM Broadcast Packet to (" + chr.getName() + ")");
                    }
                    if (chr.isGM()) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            } else {
                for (User chr : characters) {
                    if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                        System.err.println("[Debug] Sending GM Broadcast Packet to (" + chr.getName() + ")");
                    }
                    if (chr != source && (chr.isGM())) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void broadcastNONGMMessage(User source, OutPacket packet, boolean repeatToSource) {
        broadcastNONGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastNONGMMessage(User source, OutPacket oPacket) {
        charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (User chr : characters) {
                    if (!chr.isGM()) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            } else {
                for (User chr : characters) {
                    if (chr != source && (!chr.isGM())) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    /*
     *  Custom Whisper Method
     *  
     *  Purpose: Handle player through broadcast method rather than a seperate chat server.
     *  Method: Only broadcasts the message to the player with the specified name.
     */
    public void broadcastWhisper(User source, OutPacket packet, boolean repeatToSource, String msgDestination) {
        broadcastWhisper(repeatToSource ? null : source, packet, msgDestination);
    }

    private void broadcastWhisper(User source, OutPacket oPacket, String msgDestination) {
        charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (User chr : characters) {
                    if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                        System.err.println("[Debug] Sending Whisper Broadcast Packet to (" + chr.getName() + ")");
                    }
                    if (chr == c.getChannelServer().getPlayerStorage().getCharacterByName(msgDestination)) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            } else {
                for (User chr : characters) {
                    if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                        System.err.println("[Debug] Sending Whisper Broadcast Packet to (" + chr.getName() + ")");
                    }
                    if (chr != source && chr == c.getChannelServer().getPlayerStorage().getCharacterByName(msgDestination)) {
                        chr.getClient().SendPacket(oPacket);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    private void sendObjectPlacement(final User c) {
        if (c == null) {
            return;
        }
        for (final MapleMapObject o : getMapObjectsInRange(c.getTruePosition(), c.getRange(), GameConstants.rangedMapobjectTypes)) {
            if (o.getType() == MapleMapObjectType.REACTOR) {
                if (!((Reactor) o).isAlive()) {
                    continue;
                }
            }
            o.sendSpawnData(c.getClient());
            c.addVisibleMapObject(o);
        }
    }

    public final void updateMapObjectVisibility(final User chr, final MapleMapObject mo) {
        if (chr == null) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range
            if (mo.getType() == MapleMapObjectType.MIST || mo.getType() == MapleMapObjectType.EXTRACTOR || mo.getType() == MapleMapObjectType.SUMMON || mo.getType() == MapleMapObjectType.FAMILIAR || mo instanceof MechDoor || mo.getTruePosition().distanceSq(chr.getTruePosition()) <= mo.getRange()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else // monster left view range
        if (!(mo instanceof MechDoor) && mo.getType() != MapleMapObjectType.MIST && mo.getType() != MapleMapObjectType.EXTRACTOR && mo.getType() != MapleMapObjectType.SUMMON && mo.getType() != MapleMapObjectType.FAMILIAR && mo.getTruePosition().distanceSq(chr.getTruePosition()) > mo.getRange()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        } else if (mo.getType() == MapleMapObjectType.MONSTER) { //monster didn't leave view range, and is visible
            if (chr.getTruePosition().distanceSq(mo.getTruePosition()) <= GameConstants.maxViewRangeSq_Half()) {
                updateMonsterController((Mob) mo);
            }
        }
    }

    public void moveMonster(Mob monster, Point reportedPos) {
        monster.setPosition(reportedPos);

        charactersLock.readLock().lock();
        try {
            characters.stream().forEach((mc) -> {
                updateMapObjectVisibility(mc, monster);
            });
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void movePlayer(final User player, final Point newPosition) {
        player.setPosition(newPosition);
        try {
            Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
            ArrayList<MapleMapObject> copy = new ArrayList<>(visibleObjects);
            Iterator<MapleMapObject> itr = copy.iterator();
            while (itr.hasNext()) {
                MapleMapObject mo = itr.next();
                if (mo != null) {
                    if (getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else {
                        visibleObjects.remove(mo);
                    }
                }
            }
            for (MapleMapObject mo : getMapObjectsInRange(player.getTruePosition(), player.getRange())) {
                if (mo != null && !visibleObjects.contains(mo)) {
                    mo.sendSpawnData(player.getClient());
                    visibleObjects.add(mo);
                }
            }
        } finally {
            player.unlockWriteVisibleMapObjects();
        }
    }

    public String spawnDebug() {
        StringBuilder sb = new StringBuilder("Mobs in map : ");
        sb.append(getAllMapObjectSize(MapleMapObjectType.MONSTER));
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(maxRegularSpawn);
        sb.append(" monster rate: ");
        sb.append(smr.monsterRate);

        return sb.toString();
    }

    public void spawnMonsterOnGroudBelow(Mob mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);
    }

    private class ActivateItemReactor implements Runnable {

        private final MapleMapItem mapitem;
        private final Reactor reactor;
        private final ClientSocket c;

        public ActivateItemReactor(MapleMapItem mapitem, Reactor reactor, ClientSocket c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), mapitem.getType()) && !mapitem.isPickedUp()) {
                mapitem.expire(MapleMap.this);
                reactor.hitReactor(c);
                reactor.setTimerActive(false);

                if (reactor.getDelay() > 0) {
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            reactor.forceHitReactor((byte) 0);
                        }
                    }, reactor.getDelay());
                }
            } else {
                reactor.setTimerActive(false);
            }
        }
    }

    private static interface DelayedPacketCreation {

        void sendPackets(ClientSocket c);
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        charactersLock.readLock().lock();
        try {
            for (User chr : characters) {
                if (chr.getTruePosition().y > -80) {
                    teamss[0]++;
                } else {
                    teamss[1]++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        } else {
            return "st00";
        }
    }

    public boolean isDisconnected(int id) {
        return dced.contains(id);
    }

    public void addDisconnected(int id) {
        dced.add(id);
    }

    public void resetDisconnected() {
        dced.clear();
    }

    public void startSpeedRun() {
        final MapleSquad squad = getSquadByMap();
        if (squad != null) {
            charactersLock.readLock().lock();
            try {
                for (User chr : characters) {
                    if (chr.getName().equals(squad.getLeaderName()) && !chr.isIntern()) {
                        startSpeedRun(chr.getName());
                        return;
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
    }

    public void startSpeedRun(String leader) {
        speedRunStart = System.currentTimeMillis();
        speedRunLeader = leader;
    }

    public void endSpeedRun() {
        speedRunStart = 0;
        speedRunLeader = "";
    }

    public void getRankAndAdd(String leader, String time, ExpeditionType type, long timz, Collection<String> squad) {
        try (Connection con = Database.GetConnection()) {

            long lastTime = SpeedRunner.getSpeedRunData(type) == null ? 0 : SpeedRunner.getSpeedRunData(type).right;
            //if(timz > lastTime && lastTime > 0) {
            //return;
            //}
            //Pair<String, Map<Integer, String>>
            StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)")) {
                ps.setString(1, type.name());
                ps.setString(2, leader);
                ps.setString(3, time);
                ps.setLong(4, timz);
                ps.setString(5, z);
                ps.executeUpdate();
            }

            if (lastTime == 0) { //great, we just add it
                SpeedRunner.addSpeedRunData(type, SpeedRunner.addSpeedRunData(new StringBuilder(SpeedRunner.getPreamble(type)), new HashMap<Integer, String>(), z, leader, 1, time), timz);
            } else {
                //i wish we had a way to get the rank
                //TODO revamp
                SpeedRunner.removeSpeedRunData(type);
                SpeedRunner.loadSpeedRunData(type);
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

    }

    public long getSpeedRunStart() {
        return speedRunStart;
    }

    public final void disconnectAll() {
        for (User chr : getCharacters()) {
            if (!chr.isGM()) {
                chr.getClient().disconnect(true, false);
                chr.getClient().Close();
            }
        }
    }

    public void resetNPCs() {
        removeNpc(-1);
    }

    public void resetPQ(int level) {
        resetFully();
        for (MapleMapObject o : this.getAllMapObjects(MapleMapObjectType.MONSTER)) {
            final Mob mons = (Mob) o;

            mons.changeLevel(level, true);
        }
        resetSpawnLevel(level);
    }

    public void resetSpawnLevel(int level) {
        for (Spawns spawn : monsterSpawn) {
            if (spawn instanceof SpawnPoint) {
                ((SpawnPoint) spawn).setLevel(level);
            }
        }
    }

    public void resetFully() {
        resetFully(true);
    }

    public void resetFully(final boolean respawn) {
        killAllMonsters(false);
        reloadReactors();
        removeDrops();
        resetNPCs();

        resetDisconnected();
        endSpeedRun();
        cancelSquadSchedule(true);
        environment.clear();
        if (respawn) {
            OnRespawn(true, System.currentTimeMillis());
        }
    }

    public final void cancelSquadSchedule(boolean interrupt) {
        squadTimer = false;
        checkStates = true;
        if (squadSchedule != null) {
            squadSchedule.cancel(interrupt);
            squadSchedule = null;
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = this.getAllItems();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final boolean makeCarnivalSpawn(final int team, final Mob newMons, final int num) {
        /*   MonsterPoint ret = null;
        for (MonsterPoint mp : nodes.getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                final Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                for (Spawns s : monsterSpawn) {
                    if (s.getCarnivalId() > -1 && (mp.team == -1 || s.getCarnivalTeam() == mp.team) && s.getPosition().x == newpos.x && s.getPosition().y == newpos.y) {
                        found = true;
                        break; //this point has already been used.
                    }
                }
                if (!found) {
                    ret = mp; //this point is safe for use.
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0); //always.
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50); //does this matter
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            final SpawnPoint sp = addMonsterSpawn(newMons, 1, null);
            sp.setCarnival(num);
        }
        return ret != null;*/
        return false;
    }

    public final boolean makeCarnivalReactor(final int team, final int num) {
        final Reactor old = getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5 || smr.mcarnival == null) { //already exists
            return false;
        }
        Point guardz = null;
        final List<MapleMapObject> react = this.getAllMapObjects(MapleMapObjectType.REACTOR);

        for (MCGuardian guard : smr.mcarnival.Guardian) {
            if (guard.team == team || guard.team == -1) {
                boolean found = false;
                for (MapleMapObject o : react) {
                    final Reactor r = (Reactor) o;

                    if (r.getTruePosition().x == guard.pos.x && r.getTruePosition().y == guard.pos.y && r.getState() < 5) {
                        found = true;
                        break; //already used
                    }
                }
                if (!found) {
                    guardz = guard.pos; //this point is safe for use.
                    break;
                }
            }
        }
        if (guardz != null) {
            final Reactor my = new Reactor(MapleReactorFactory.getReactor(9980000 + team), 9980000 + team);
            my.setState((byte) 1);
            my.setName(team + "" + num); //lol
            //with num. -> guardians in factory
            spawnReactorOnGroundBelow(my, guardz);
            final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);

            for (MapleMapObject o : this.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                final Mob mons = (Mob) o;

                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false);
                }
            }
        }
        return guardz != null;
    }

    // <editor-fold defaultstate="collapsed" desc="Burning field">
    /**
     * Updates the burning field state of this map. NOTE: This function must be synchronized, in an event that multiple access were called
     * at the same time if many players access the map at once
     *
     * @param currentTime
     */
    public synchronized void updateBurningField(final long currentTime) {
        if (burningFieldLevel == -1) {
            return; // this map can't have 'burning field'. -1 flag is set by MapleMap.loadMonsterRate();
        }
        long timeDifference = (currentTime - burningField_LastUpdateTime);
        if (this.getCharactersSize() == 0) { // thread safe, only dealing with hashmap.size()
            this.burningField_mapVacancyTime += timeDifference;
        } else {
            this.burningField_mapVacancyTime -= timeDifference * 6l; // every 10 minutes a character is in, it needs 1 hour to restore

            if (this.burningField_mapVacancyTime < 0) {
                this.burningField_mapVacancyTime = 0;
            }
        }
        burningField_LastUpdateTime = currentTime;

        // Update state
        int currentBurningFieldLevel = Math.min((int) (this.burningField_mapVacancyTime / (1000L * 60L * 60L)), 10); // max burning field of Lv 10
        if (burningFieldLevel != currentBurningFieldLevel) {
            this.burningFieldLevel = (byte) currentBurningFieldLevel;

            // Broadcast to everyone on the updated state
            this.broadcastPacket(CField.playSound("Sound/FarmSE.img/boxResult", 100));
            this.broadcastPacket(CField.EffectPacket.showBurningFieldTextEffect(getBurningFieldMessage()));
        }
        // DEBUG ONLY
        //   this.broadcastMessage(CWvsContext.broadcastMsg(5, "Burning field Level: " + burningFieldLevel + ", Update Time: " + 
        //           new Date(burningField_LastUpdateTime).toString() + ", Vacancy: " + burningField_mapVacancyTime / 1000 + " seconds."));
    }

    public int getBurningFieldBonusEXP() {
        return this.burningFieldLevel * 10;
    }

    private String getBurningFieldMessage() {
        if (burningFieldLevel > 0) {
            return String.format("Burning Stage %d: %d%s Bonus EXP!",
                    burningFieldLevel,
                    getBurningFieldBonusEXP(),
                    "%");
        }
        return "Burning Field destroyed.";
    }

    /**
     * Sets the current burning vacancy time by [burning field level]
     *
     * @param level
     */
    public void setBurningFieldLevel(byte level) {
        burningField_mapVacancyTime = level * (1000l * 60l * 60l) + 5l;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Respawns">
    public final boolean canSpawn(long now) {
        return lastRespawnTime + smr.createMobInterval < now;
    }

    /**
     * Respawns the monsters of this field. This function also updates
     *
     * @param bForce
     * @param tCurrent Current time.
     */
    public void OnRespawn(boolean bForce, final long tCurrent) {
        final int nSpawnedSize = spawnedMonstersOnMap.get();
        
        int nMaximumSpawnCount = maxRegularSpawn.get();
        if(TrainingMap.bEnabled) nMaximumSpawnCount = TrainingMap.OnBalanceSpawnCount(this);
        
        if (bForce) {
            int nShouldSpawn = nMaximumSpawnCount - nSpawnedSize;

            if (nShouldSpawn > 0) {
                for (final Spawns pSpawnPoint : bossSpawn) {
                    pSpawnPoint.spawnMonster(this);
                    nShouldSpawn--;

                    if (nShouldSpawn <= 0) {
                        return;
                    }
                }
                for (final Spawns pSpawnPoint : monsterSpawn) {
                    pSpawnPoint.spawnMonster(this);
                    nShouldSpawn--;

                    if (nShouldSpawn <= 0) {
                        return;
                    }
                }
            }
        } else {
            int nMax = nMaximumSpawnCount;
            if (smr.partyBonusR > 0) {                                                                          // LHC/Cygnus Stronghold, More players = higher spawn rate.
                nMax *= 1.0f + (0.07f * (float) (this.getCharactersSize() - 1));                                // 2 player = 7% more spawn, 3 = 14% and so on..
            }

            float nSpawnRate = 1f;                                                                              // Base spawn rate.
            
            if (ServerConstants.MODIFY_GLOBAL_SPAWN_RATE) {
                nSpawnRate *= ServerConstants.SPAWN_RATE_MULTIPLIER;                                            // Adjust global spawn rate.
            }
            
            List<User> pMapCharacters = getCharacters();
            for (User pUser : pMapCharacters) {
                if (pUser.hasBuff(CharacterTemporaryStat.IncMobRateDummy)) {
                    nSpawnRate *= 1.5;                                                                           // Adjust spawn rate while Kanna's Kishin is active.
                }
            }
            
            //int nShouldSpawn = (int) (Math.min(smr.maxRegularSpawnAtOnce, Math.max(0, nMax - nSpawnedSize)) * nSpawnRate); // Original
            int nShouldSpawn = nMax - nSpawnedSize; 
            
            if (ServerConstants.DEVELOPER_DEBUG_MODE && !ServerConstants.REDUCED_DEBUG_SPAM) {
                System.out.printf("[Debug] SpawnedMonsterOnMap (%s), MaxRegularSpawn (%s), MaxRegularSpawnAtOnce (%s)\r\n", spawnedMonstersOnMap.get(), maxRegularSpawn.get(), smr.maxRegularSpawnAtOnce);
            }
            
            if (nShouldSpawn > 0) {
                for (final Spawns spawnPoint : bossSpawn) {
                    if (spawnPoint.shouldSpawn(tCurrent, 1f)) {
                        spawnPoint.spawnMonster(this);
                        nShouldSpawn--;

                        if (nShouldSpawn <= 0) {
                            return;
                        }
                    }
                }
                Collections.shuffle(monsterSpawn);
                for (final Spawns spawnPoint : monsterSpawn) {
                    if (spawnPoint.shouldSpawn(tCurrent, nSpawnRate)) {
                        spawnPoint.spawnMonster(this);
                        nShouldSpawn--;

                        if (nShouldSpawn <= 0) {
                            return;
                        }
                        /*} else if (!spawnPoint.getLastSpawnedMonster_IsRareMonster()
                            && channel.getRareMonstertracking().canSpawnRareMonster(getId(), cTime, spawnPoint.getSpawnedTime())) {
                        //make it rare here
                        spawnPoint.getLastSpawnedRealMonster().setRareMonster(true, MobConstant.RareMonsterAppearPeriod);*/
                    }
                }
            }
            lastRespawnTime = tCurrent;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Maple Ruin operation"> 
    /**
     * Sets the rune of the map, upon map initialization
     *
     * @param m
     */
    public void setRune(RuneStone m) {
        if (this.rune != null) {
            return;
        }
        this.rune = m;

        spawnAndAddRangedMapObject(m, (ClientSocket c1) -> {
            c1.SendPacket(CField.RunePacket.spawnRune(rune));
        });
    }

    /**
     * Attempts to respawn the rune. This is only to be executed by the map monster respawn thread.
     *
     * @param force
     */
    public void respawnRune(boolean force) {
        if (rune == null || (!rune.getAbleToRespawn() && !force)) {
            return;
        }
        rune.respawnRuneInMap(this, false);
    }

    public RuneStone getRuneStone() {
        return rune;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Get map objects"> 
    /**
     * Gets all map object in the map by the following type. Execution: O(n)
     *
     * @param type
     * @return
     */
    public List<MapleMapObject> getAllMapObjects(MapleMapObjectType type) {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(type).readLock().lock();
        try {
            mapobjects.get(type).values().stream().forEach((mmo) -> {
                ret.add(mmo);
            });
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
        return ret;
    }

    public int getAllMapObjectSize(MapleMapObjectType type) {
        return mapobjects.get(type).size();
    }

    public List<Summon> getAllSummons() {
        ArrayList<Summon> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.SUMMON).values().stream().filter((mmo) -> (mmo instanceof Summon)).forEach((mmo) -> {
                ret.add((Summon) mmo);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllDoors() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.DOOR).values().stream().filter((mmo) -> (mmo instanceof Door)).forEach((mmo) -> {
                ret.add(mmo);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMechDoors() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.DOOR).values().stream().filter((mmo) -> (mmo instanceof MechDoor)).forEach((mmo) -> {
                ret.add(mmo);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<Integer> getAllUniqueMonsters() {
        ArrayList<Integer> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.MONSTER).values().stream().map((mmo) -> ((Mob) mmo).getId()).filter((theId) -> (!ret.contains(theId))).forEach((theId) -> {
                ret.add(theId);
            });
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public MapleMapObject getClosestMapObjectInRange(final Point from, final double rangeSq, final List<MapleMapObjectType> MapObject_types) {
        MapleMapObject ret = null;
        for (MapleMapObjectType type : MapObject_types) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq && (ret == null || from.distanceSq(ret.getTruePosition()) > from.distanceSq(mmo.getTruePosition()))) {
                        ret = mmo;
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        return mapobjects.get(type).get(oid);
    }

    public final boolean containsNPC(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            return mapobjects.get(MapleMapObjectType.NPC).values().stream().filter(obj -> ((NPCLife) obj).getId() == npcid).count() > 0;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public NPCLife getNPCById(int id) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Optional<MapleMapObject> foundNPC = mapobjects.get(MapleMapObjectType.NPC).values().stream().filter(obj -> ((NPCLife) obj).getId() == id).findFirst();
            if (foundNPC.isPresent()) {
                return (NPCLife) foundNPC.get();
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
        return null;
    }

    public Mob getMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            Optional<MapleMapObject> foundNPC = mapobjects.get(MapleMapObjectType.MONSTER).values().stream().filter(obj -> ((Mob) obj).getId() == id).findFirst();
            if (foundNPC.isPresent()) {
                return (Mob) foundNPC.get();
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return null;
    }

    public int countMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            return (int) mapobjects.get(MapleMapObjectType.MONSTER).values().stream().filter(obj -> ((Mob) obj).getId() == id).count();
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public Reactor getReactorById(int id) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            Optional<MapleMapObject> foundReactor = mapobjects.get(MapleMapObjectType.REACTOR).values().stream().filter(obj -> ((Reactor) obj).getReactorId() == id).findFirst();
            if (foundReactor.isPresent()) {
                return (Reactor) foundReactor.get();
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return null;
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns null
     *
     * @param oid
     * @return
     */
    public Mob getMonsterByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (Mob) mmo;
    }

    public NPCLife getNPCByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (NPCLife) mmo;
    }

    public Reactor getReactorByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (Reactor) mmo;
    }

    public MonsterFamiliar getFamiliarByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.FAMILIAR);
        if (mmo == null) {
            return null;
        }
        return (MonsterFamiliar) mmo;
    }

    public Reactor getReactorByName(final String name) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            Optional<MapleMapObject> foundReactor = mapobjects.get(MapleMapObjectType.REACTOR).values().stream().filter(obj -> ((Reactor) obj).getName().equalsIgnoreCase(name)).findFirst();
            if (foundReactor.isPresent()) {
                return (Reactor) foundReactor.get();
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return null;
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : smr.portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : smr.portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public List<User> getCharacters() {
        final List<User> chars = new ArrayList<>();

        charactersLock.readLock().lock();
        try {
            characters.stream().forEach((mc) -> {
                chars.add(mc);
            });
        } finally {
            charactersLock.readLock().unlock();
        }
        return chars;
    }

    public User getCharacterByName(final String id) {
        charactersLock.readLock().lock();
        try {
            for (User mc : characters) {
                if (mc.getName().equalsIgnoreCase(id)) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    public User getCharacterById(final int id) {
        charactersLock.readLock().lock();
        try {
            for (User mc : characters) {
                if (mc.getId() == id) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    /**
     * Gets the lowest level monster [non boss] from the spawnpoints of this map
     *
     * @return MonsterStats
     */
    public MonsterStats getLowestLevelMonster() {
        MonsterStats lowestStats = null;
        for (Spawns sp : monsterSpawn) {
            if (!sp.isBossSpawnPoint()) {
                final MonsterStats stats = sp.getMonster();

                if (lowestStats == null || stats.getLevel() < lowestStats.getLevel()) {
                    lowestStats = stats;
                }
            }
        }
        return lowestStats;
    }

    public final List<MaplePortal> getPortalsInRange(final Point from, final double rangeSq) {
        final List<MaplePortal> ret = new ArrayList<>();
        for (MaplePortal type : smr.portals.values()) {
            if (from.distanceSq(type.getPosition()) <= rangeSq && type.getTargetMapId() != smr.mapid && type.getTargetMapId() != 999999999) {
                ret.add(type);
            }
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        return getMapObjectsInRange(from, rangeSq, Arrays.asList(MapleMapObjectType.ITEM));
    }

    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRect(final Rectangle box, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (box.contains(mmo.getTruePosition())) {
                        ret.add(mmo);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public final List<User> getCharactersIntersect(final Rectangle box) {
        final List<User> ret = new ArrayList<>();
        charactersLock.readLock().lock();
        try {
            for (User chr : characters) {
                if (chr.getBounds().intersects(box)) {
                    ret.add(chr);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final List<User> getPlayersInRectAndInList(final Rectangle box, final List<User> chrList) {
        final List<User> character = new LinkedList<>();

        charactersLock.readLock().lock();
        try {
            final Iterator<User> ltr = characters.iterator();
            User a;
            while (ltr.hasNext()) {
                a = ltr.next();
                if (chrList.contains(a) && box.contains(a.getTruePosition())) {
                    character.add(a);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return character;
    }

    public final MaplePortal getPortal(final String portalname) {
        for (final MaplePortal port : smr.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public final MaplePortal getPortal(final int portalid) {
        final MaplePortal portal = smr.portals.get(portalid);
        if (portal == null) {
            return smr.portals.get(0);
        }
        return smr.portals.get(portalid);
    }

    /**
     * Gets the size of all map objects in the map excluding character count.
     *
     * @return
     */
    public final int getMapObjectSize() {
        return mapobjects.size() + getCharactersSize() - characters.size();
    }

    /**
     * Gets the size of all characters in the map.
     *
     * @return
     */
    public final int getCharactersSize() {
        return characters.size();
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(smr.portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="Respawn Task"> 
    public int getNumSpawnPoints() {
        return monsterSpawn.size();
    }

    public void loadMonsterRate() {
        final int spawnSize = monsterSpawn.size();

        if (spawnSize != 0) {
            /*
	     * int rate = Math.round(spawnSize * smr.monsterRate); if (rate < 2)
	     * { rate = 2; } else if (rate > spawnSize) { rate = spawnSize -
	     * (spawnSize / 12); }
             */
            // final Rectangle MapDimension = smr.footholds.getFullMapArea();
            //  final int rate = (int) Math.min(spawnSize * 1.15f, Math.sqrt(MapDimension.height) + Math.sqrt(MapDimension.width) / 7);

            int spawnRate = (int) (spawnSize / smr.monsterRate);
            if (spawnRate < 6) {
                spawnRate = 6;
            }

            smr.maxRegularSpawnAtOnce = spawnRate;

            final List<Spawns> newBossSpawn = new ArrayList();
            final List<Spawns> newSpawn = new ArrayList();

            int lvlmin = -1, lvlmax = 30;
            for (Spawns s : monsterSpawn) {
                final int lvl = s.getMonster().getLevel();//s.getReferenceMonster().getStats().getLevel();
                if (lvl > lvlmax) {
                    lvlmax = lvl;
                }
                if (lvlmin == -1 || lvlmin > lvl) {
                    lvlmin = lvl;
                }
                if (s.getMonster().isBoss()) {
                    newBossSpawn.add(s);
                } else {
                    newSpawn.add(s);
                }
            }
// Herbs and Viens
            final List<Integer> HerbsViens = SharedMapResources.setRandomherbsAndViens(smr, spawnSize, getId(), lvlmin, lvlmax);

            int skip = 0, total = 0;
            if (smr.maxHerbSpawn > 0 && !HerbsViens.isEmpty()) {
                for (Spawns s : monsterSpawn) {
                    if (s.getSpawnPoint() != null) { // random spawn point based bosses, skipping
                        if (skip % 2 == 0) {
                            spawnRandomHerbOrVien(s.getSpawnPoint());
                            total++;
                            if (total == smr.maxHerbSpawn) {
                                break;
                            }
                        }
                        skip++;
                    }
                }
            }
            monsterSpawn.clear();
            monsterSpawn.addAll(newSpawn);
            bossSpawn.addAll(newBossSpawn);

            maxRegularSpawn.set(/*rate*/smr.maxRegularSpawnAtOnce);

            // Maps with monster Lv below 100 cant have burning field
            if (lvlmin < 100) {
                burningFieldLevel = -1;
            }
        } else {
            // This map isn't a burning field, since there's no monsters 
            burningFieldLevel = -1;
        }
    }

    public final SpawnPoint addMonsterSpawn(final Mob monster, final int mobTime, final String msg) {
        final Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        final SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, monster.getCarnivalTeam(), msg);
        if (monster.getCarnivalTeam() > -1) {
            monsterSpawn.add(0, sp); //at the beginning
        } else {
            monsterSpawn.add(sp);
        }
        return sp;
    }

    public final void addAreaMonsterSpawn(final Mob monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg, final boolean shouldSpawn) {
        pos1 = calcPointBelow(pos1);
        pos2 = calcPointBelow(pos2);
        pos3 = calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: smr.mapid " + smr.mapid + ", monster " + monster.getId() + " could not be spawned.");

            return;
        } else if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn));
    }
    // </editor-fold> 

    public boolean getAndSwitchTeam() {
        return getCharactersSize() % 2 != 0;
    }

    public void setSquad(MapleSquadType s) {
        this.squad = s;
    }

    public int getChannel() {
        return channel;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public int getPermanentWeather() {
        return permanentWeather;
    }

    public void checkStates(final String chr) {
        if (!checkStates) {
            return;
        }
        final MapleSquad sqd = getSquadByMap();
        final EventManager em = getEMByMap();
        final int size = getCharactersSize();
        if (sqd != null && sqd.getStatus() == 2) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equalsIgnoreCase(chr)) {
                    em.setProperty("leader", "false");
                }
                if (chr.equals("") || size == 0) {
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    cancelSquadSchedule(!chr.equals(""));
                    sqd.clear();
                    sqd.copy();
                }
            }
        }
        if (em != null && em.getProperty("state") != null && (sqd == null || sqd.getStatus() == 2) && size == 0) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
                em.setProperty("leader", "true");
            }
        }
        if (speedRunStart > 0 && size == 0) {
            endSpeedRun();
        }
        //if (squad != null) {
        //    final MapleSquad sqdd = ChannelServer.getInstance(channel).getMapleSquad(squad);
        //    if (sqdd != null && chr != null && chr.length() > 0 && sqdd.getAllNextPlayer().contains(chr)) {
        //	sqdd.getAllNextPlayer().remove(chr);
        //	broadcastMessage(CWvsContext.broadcastMsg(5, "The queued player " + chr + " has left the map."));
        //    }
        //}
    }

    public void setCheckStates(boolean b) {
        this.checkStates = b;
    }

    public final void changeEnvironment(final String ms, final int type) {
        broadcastPacket(CField.environmentChange(ms, type, 0));
    }

    public final void toggleEnvironment(final String ms) {
        if (environment.containsKey(ms)) {
            moveEnvironment(ms, environment.get(ms) == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    public final void moveEnvironment(final String ms, final int type) {
        broadcastPacket(CField.environmentMove(ms, type));
        environment.put(ms, type);
    }

    public final Map<String, Integer> getEnvironment() {
        return environment;
    }

    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(smr.areas.get(index));
    }

    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;

        charactersLock.readLock().lock();
        try {
            final Iterator<User> ltr = characters.iterator();
            User a;
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(smr.areas.get(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int ret = getNumPlayersInRect(rect);

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            ret = mapobjects.get(MapleMapObjectType.ITEM).values().stream().filter((mmo) -> (rect.contains(mmo.getTruePosition()))).map((_item) -> 1).reduce(ret, Integer::sum);
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public final boolean canHurt(long now) {
        if (lastHurtTime > 0 && lastHurtTime + smr.decHPInterval < now) {
            lastHurtTime = now;
            return true;
        }
        return false;
    }

    public final void resetShammos(final ClientSocket c) {
        killAllMonsters(true);
        broadcastPacket(WvsContext.broadcastMsg(5, "A player has moved too far from Shammos. Shammos is going back to the start."));
        EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (c.getPlayer() != null) {
                    c.getPlayer().changeMap(MapleMap.this, getPortal(0));
                    if (getCharactersSize() > 1) {
                        MapScriptMethods.startScript_FirstUser(c, "shammos_Fenter");
                    }
                }
            }
        }, 500); //avoid dl
    }

    public int getInstanceId() {
        return instanceid;
    }

    public void setInstanceId(int ii) {
        this.instanceid = ii;
    }

    public final void setChangeableMobOrigin(User d) {
        this.changeMobOrigin = new WeakReference<>(d);
    }

    public final User getChangeableMobOrigin() {
        if (changeMobOrigin == null) {
            return null;
        }
        return changeMobOrigin.get();
    }

    public final void mapMessage(final int type, final String message) {
        broadcastPacket(WvsContext.broadcastMsg(type, message));
    }
}
