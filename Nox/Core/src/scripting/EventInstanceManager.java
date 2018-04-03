package scripting;

import scripting.provider.EventScriptManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.script.ScriptException;

import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import constants.GameConstants;
import client.SkillFactory;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.MapleExpedition;
import handling.world.PartySearch;
import service.ChannelServer;
import net.Packet;
import server.MapleCarnivalParty;
import server.MapleItemInformationProvider;
import server.MapleSquad;
import server.Timer.EventTimer;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.objects.User;
import server.messages.GiveBuffMessage;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class EventInstanceManager {

    private List<User> chars = new LinkedList<>(); //this is messy
    private List<Integer> dced = new LinkedList<>();
    private List<MapleMonster> mobs = new LinkedList<>();
    private Map<Integer, Integer> killCount = new HashMap<>();
    private final EventManager em;
    private final int channel;
    private final String name;
    private Properties props = new Properties();
    private long timeStarted = 0;
    private long eventTime = 0;
    private List<Integer> mapIds = new LinkedList<>();
    private List<Boolean> isInstanced = new LinkedList<>();
    private ScheduledFuture<?> eventTimer;
    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock rL = mutex.readLock(), wL = mutex.writeLock();
    private boolean disposed = false;

    public EventInstanceManager(EventManager em, String name, int channel) {
        this.em = em;
        this.name = name;
        this.channel = channel;
    }

    public void registerPlayer(User chr) {
        if (disposed || chr == null) {
            return;
        }
        try {
            wL.lock();
            try {
                chars.add(chr);
            } finally {
                wL.unlock();
            }
            chr.setEventInstance(this);
            em.getIv().invokeFunction("playerEntry", this, chr);
        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public void changedMap(final User chr, final int mapid) {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("changedMap", this, chr, mapid);
        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public void timeOut(final long delay, final EventInstanceManager eim) {
        if (disposed || eim == null) {
            return;
        }
        eventTimer = EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (disposed || eim == null || em == null) {
                    return;
                }
                try {
                    em.getIv().invokeFunction("scheduledTimeout", eim);
                } catch (Exception ex) {
                    LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
                }
            }
        }, delay);
    }

    public void stopEventTimer() {
        eventTime = 0;
        timeStarted = 0;
        if (eventTimer != null) {
            eventTimer.cancel(false);
        }
    }

    public void restartEventTimer(long time) {
        try {
            if (disposed) {
                return;
            }
            timeStarted = System.currentTimeMillis();
            eventTime = time;
            if (eventTimer != null) {
                eventTimer.cancel(false);
            }
            eventTimer = null;
            final int timesend = (int) time / 1000;

            for (User chr : getPlayers()) {
                if (name.startsWith("PVP")) {
                    chr.getClient().write(CField.getPVPClock(Integer.parseInt(getProperty("type")), timesend));
                } else {
                    chr.getClient().write(CField.getClock(timesend));
                }
            }
            timeOut(time, this);

        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public void startEventTimer(long time) {
        restartEventTimer(time); //just incase
    }

    public boolean isTimerStarted() {
        return eventTime > 0 && timeStarted > 0;
    }

    public long getTimeLeft() {
        return eventTime - (System.currentTimeMillis() - timeStarted);
    }

    public void registerParty(MapleParty party, MapleMap map) {
        if (disposed) {
            return;
        }
        for (MaplePartyCharacter pc : party.getMembers()) {
            registerPlayer(map.getCharacterById(pc.getId()));
        }
        PartySearch ps = World.Party.getSearch(party);
        if (ps != null) {
            World.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
        }
    }

    public void registerExpedition(MapleClient c, MapleExpedition exped, MapleMap map) {
        if (disposed) {
            return;
        }
        for (User pc : exped.getExpeditionMembers(c)) {
            registerPlayer(pc);
        }
        PartySearch ps = World.Party.getSearch(exped);
        if (ps != null) {
            World.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
        }
    }

    public void unregisterPlayerAzwan(User chr) {
        wL.lock();
        try {
            chars.remove(chr);
        } finally {
            wL.unlock();
        }
        chr.setEventInstance(null);
    }

    public void unregisterAll() {
        wL.lock();
        try {
            for (User chr : chars) {
                chr.setEventInstance(null);
            }
            chars.clear();
        } finally {
            wL.unlock();
        }
    }

    public void unregisterPlayer(final User chr) {
        if (disposed) {
            chr.setEventInstance(null);
            return;
        }
        wL.lock();
        try {
            unregisterPlayer_NoLock(chr);
        } finally {
            wL.unlock();
        }
    }

    private boolean unregisterPlayer_NoLock(final User chr) {
        if (name.equals("CWKPQ")) { //hard code it because i said so
            final MapleSquad squad = ChannelServer.getInstance(channel).getMapleSquad("CWKPQ");//so fkin hacky
            if (squad != null) {
                squad.removeMember(chr.getName());
                if (squad.getLeaderName().equals(chr.getName())) {
                    em.setProperty("leader", "false");
                }
            }
        }
        chr.setEventInstance(null);
        if (disposed) {
            return false;
        }
        if (chars.contains(chr)) {
            chars.remove(chr);
            return true;
        }
        return false;
    }

    public final boolean disposeIfPlayerBelow(final byte size, final int towarp) {
        if (disposed) {
            return true;
        }
        MapleMap map = null;
        if (towarp > 0) {
            map = this.getMapFactory().getMap(towarp);
        }

        wL.lock();
        try {
            if (chars != null && chars.size() <= size) {
                final List<User> chrs = new LinkedList<>(chars);
                for (User chr : chrs) {
                    if (chr == null) {
                        continue;
                    }
                    unregisterPlayer_NoLock(chr);
                    if (towarp > 0) {
                        chr.changeMap(map, map.getPortal(0));
                    }
                }
                dispose_NoLock();
                return true;
            }
        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } finally {
            wL.unlock();
        }
        return false;
    }

    public final void saveBossQuest(final int points) {
        if (disposed) {
            return;
        }
        for (User chr : getPlayers()) {
            final MapleQuestStatus record = chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.BOSS_PQ));

            if (record.getCustomData() != null) {
                record.setCustomData(String.valueOf(points + Integer.parseInt(record.getCustomData())));
            } else {
                record.setCustomData(String.valueOf(points)); // First time
            }
            chr.modifyCSPoints(1, points / 5, true);
            chr.getTrait(MapleTraitType.will).addExp(points / 100, chr);
        }
    }

    public final void saveNX(final int points) {
        if (disposed) {
            return;
        }
        for (User chr : getPlayers()) {
            chr.modifyCSPoints(1, points, true);
        }
    }

    public List<User> getPlayers() {
        if (disposed) {
            return Collections.emptyList();
        }
        rL.lock();
        try {
            return new LinkedList<>(chars);
        } finally {
            rL.unlock();
        }
    }

    public List<Integer> getDisconnected() {
        return dced;
    }

    public final int getPlayerCount() {
        if (disposed) {
            return 0;
        }
        return chars.size();
    }

    public void registerMonster(MapleMonster mob) {
        if (disposed) {
            return;
        }
        mobs.add(mob);
        mob.setEventInstance(this);
    }

    public void unregisterMonster(MapleMonster mob) {
        mob.setEventInstance(null);
        if (disposed) {
            return;
        }
        if (mobs.contains(mob)) {
            mobs.remove(mob);
        }
        if (mobs.isEmpty()) {
            try {
                em.getIv().invokeFunction("allMonstersDead", this);
            } catch (Exception ex) {
                LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
            }
        }
    }

    public void playerKilled(User chr) {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("playerDead", this, chr);
        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public boolean revivePlayer(User chr) {
        if (disposed) {
            return false;
        }
        try {
            Object b = em.getIv().invokeFunction("playerRevive", this, chr);
            if (b instanceof Boolean) {
                return (Boolean) b;
            }
        } catch (Exception ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
        return true;
    }

    public void playerDisconnected(final User chr, int idz) {
        if (disposed) {
            return;
        }
        byte ret;
        try {
            ret = ((Double) em.getIv().invokeFunction("playerDisconnected", this, chr)).byteValue();
        } catch (NoSuchMethodException | ScriptException e) {
            ret = 0;
        }

        wL.lock();
        try {
            if (disposed) {
                return;
            }
            if (chr == null || chr.isAlive()) {
                dced.add(idz);
            }
            if (chr != null) {
                unregisterPlayer_NoLock(chr);
            }
            if (ret == 0) {
                if (getPlayerCount() <= 0) {
                    dispose_NoLock();
                }
            } else if ((ret > 0 && getPlayerCount() < ret) || (ret < 0 && (iiPacketder(chr) || getPlayerCount() < (ret * -1)))) {
                final List<User> chrs = new LinkedList<>(chars);
                for (User player : chrs) {
                    if (player.getId() != idz) {
                        removePlayer(player);
                    }
                }
                dispose_NoLock();
            }
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an issue with the event:\n{}", ex);
        } finally {
            wL.unlock();
        }

    }

    /**
     *
     * @param chr
     * @param mob
     */
    public void monsterKilled(final User chr, final MapleMonster mob) {
        if (disposed) {
            return;
        }
        try {
            int inc = ((Double) em.getIv().invokeFunction("monsterValue", this, mob.getId())).intValue();
            if (disposed || chr == null) {
                return;
            }
            Integer kc = killCount.get(chr.getId());
            if (kc == null) {
                kc = inc;
            } else {
                kc += inc;
            }
            killCount.put(chr.getId(), kc);
            if (chr.getCarnivalParty() != null && (mob.getStats().getPoint() > 0 || mob.getStats().getCP() > 0)) {
                em.getIv().invokeFunction("monsterKilled", this, chr, mob.getStats().getCP() > 0 ? mob.getStats().getCP() : mob.getStats().getPoint());
            }
        } catch (ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (NoSuchMethodException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an issue with the event:\n{}", ex);
        }
    }

    public void monsterDamaged(final User chr, final MapleMonster mob, final int damage) {
        if (disposed || mob.getId() != 9700037) { //ghost PQ boss only.
            return;
        }
        try {
            em.getIv().invokeFunction("monsterDamaged", this, chr, mob.getId(), damage);
        } catch (ScriptException | NoSuchMethodException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an issue with the event:\n{}", ex);
        }
    }

    public void addPVPScore(final User chr, final int score) {
        if (disposed) { //ghost PQ boss only.
            return;
        }
        try {
            em.getIv().invokeFunction("addPVPScore", this, chr, score);
        } catch (ScriptException | NoSuchMethodException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an issue with the event:\n{}", ex);
        }
    }

    public int getKillCount(User chr) {
        if (disposed) {
            return 0;
        }
        Integer kc = killCount.get(chr.getId());
        if (kc == null) {
            return 0;
        } else {
            return kc;
        }
    }

    public void dispose_NoLock() {
        if (disposed || em == null) {
            return;
        }
        final String emN = em.getName();
        try {

            disposed = true;
            for (User chr : chars) {
                chr.setEventInstance(null);
            }
            chars.clear();
            chars = null;
            if (mobs.size() >= 1) {
                for (MapleMonster mob : mobs) {
                    if (mob != null) {
                        mob.setEventInstance(null);
                    }
                }
            }
            mobs.clear();
            mobs = null;
            killCount.clear();
            killCount = null;
            dced.clear();
            dced = null;
            timeStarted = 0;
            eventTime = 0;
            props.clear();
            props = null;
            for (int i = 0; i < mapIds.size(); i++) {
                if (isInstanced.get(i)) {
                    this.getMapFactory().removeInstanceMap(mapIds.get(i));
                }
            }
            mapIds.clear();
            mapIds = null;
            isInstanced.clear();
            isInstanced = null;
            em.disposeInstance(name);
        } catch (Exception e) {
            LogHelper.INVOCABLE.get().info("Event name" + emN + ", Instance name : " + name + ":\n{}", e);
        }
    }

    public void dispose() {
        wL.lock();
        try {
            dispose_NoLock();
        } finally {
            wL.unlock();
        }

    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public List<MapleMonster> getMobs() {
        return mobs;
    }

    public final void broadcastPlayerMsg(final int type, final String msg) {
        if (disposed) {
            return;
        }
        for (User chr : getPlayers()) {
            chr.dropMessage(type, msg);
        }
    }

    //PVP
    public final List<Pair<Integer, String>> newPair() {
        return new ArrayList<>();
    }

    public void addToPair(List<Pair<Integer, String>> e, int e1, String e2) {
        e.add(new Pair<>(e1, e2));
    }

    public final List<Pair<Integer, User>> newPair_chr() {
        return new ArrayList<>();
    }

    public void addToPair_chr(List<Pair<Integer, User>> e, int e1, User e2) {
        e.add(new Pair<>(e1, e2));
    }

    public final void broadcastPacket(Packet p) {
        if (disposed) {
            return;
        }
        for (User chr : getPlayers()) {
            chr.getClient().write(p);
        }
    }

    public final void broadcastTeamPacket(Packet p, int team) {
        if (disposed) {
            return;
        }
        for (User chr : getPlayers()) {
            if (chr.getTeam() == team) {
                chr.getClient().write(p);
            }
        }
    }

    public final MapleMap createInstanceMap(final int mapid) {
        if (disposed) {
            return null;
        }
        final int assignedid = EventScriptManager.getNewInstanceMapId();
        mapIds.add(assignedid);
        isInstanced.add(true);
        return this.getMapFactory().createInstanceMap(mapid, 910000000, true, true, true, "EIM", assignedid);
    }

    public final MapleMap createInstanceMapS(final int mapid) {
        if (disposed) {
            return null;
        }
        final int assignedid = EventScriptManager.getNewInstanceMapId();
        mapIds.add(assignedid);
        isInstanced.add(true);

        return this.getMapFactory().createInstanceMap(mapid, 910000000, false, false, false, "EIM", assignedid);
    }

    public final MapleMap setInstanceMap(final int mapid) { //gets instance map from the channelserv
        if (disposed) {
            return this.getMapFactory().getMap(mapid);
        }
        mapIds.add(mapid);
        isInstanced.add(false);
        return this.getMapFactory().getMap(mapid);
    }

    public final MapleMapFactory getMapFactory() {
        return getChannelServer().getMapFactory();
    }

    public final MapleMap getMapInstance(int args) {
        if (disposed) {
            return null;
        }
        try {
            boolean instanced = false;
            int trueMapID = -1;
            if (args >= mapIds.size()) {
                //assume real map
                trueMapID = args;
            } else {
                trueMapID = mapIds.get(args);
                instanced = isInstanced.get(args);
            }
            MapleMap map = null;
            if (!instanced) {
                map = this.getMapFactory().getMap(trueMapID);
                if (map == null) {
                    return null;
                }
                // in case reactors need shuffling and we are actually loading the map
                if (map.getCharactersSize() == 0) {
                    if (em.getProperty("shuffleReactors") != null && em.getProperty("shuffleReactors").equals("true")) {
                        map.shuffleReactors();
                    }
                }
            } else {
                map = this.getMapFactory().getInstanceMap(trueMapID);
                if (map == null) {
                    return null;
                }
                // in case reactors need shuffling and we are actually loading the map
                if (map.getCharactersSize() == 0) {
                    if (em.getProperty("shuffleReactors") != null && em.getProperty("shuffleReactors").equals("true")) {
                        map.shuffleReactors();
                    }
                }
            }
            return map;
        } catch (Exception ex) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an issue with the event:\n{}", ex);
            return null;
        }
    }

    public final void schedule(final String methodName, final long delay) {
        if (disposed) {
            return;
        }
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (disposed || EventInstanceManager.this == null || em == null) {
                    return;
                }
                try {
                    em.getIv().invokeFunction(methodName, EventInstanceManager.this);
                } catch (Exception ex) {
                    LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + "Method Name: " + methodName + ":\n{}", ex);
                }
            }
        }, delay);
    }

    public final String getName() {
        return name;
    }

    public final void setProperty(final String key, final String value) {
        if (disposed) {
            return;
        }
        props.setProperty(key, value);
    }

    public final Object setProperty(final String key, final String value, final boolean prev) {
        if (disposed) {
            return null;
        }
        return props.setProperty(key, value);
    }

    public final String getProperty(final String key) {
        if (disposed) {
            return "";
        }
        return props.getProperty(key);
    }

    public final Properties getProperties() {
        return props;
    }

    public final void leftParty(final User chr) {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("leftParty", this, chr);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public final void disbandParty() {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("disbandParty", this);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    //Separate function to warp players to a "finish" map, if applicable
    public final void finishPQ() {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("clearPQ", this);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public final void removePlayer(final User chr) {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("playerExit", this, chr);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        }
    }

    public final void registerCarnivalParty(final User leader, final MapleMap map, final byte team) {
        if (disposed) {
            return;
        }
        leader.clearCarnivalRequests();
        List<User> characters = new LinkedList<>();
        final MapleParty party = leader.getParty();

        if (party == null) {
            return;
        }
        for (MaplePartyCharacter pc : party.getMembers()) {
            final User c = map.getCharacterById(pc.getId());
            if (c != null) {
                characters.add(c);
                registerPlayer(c);
                c.resetCP();
            }
        }
        PartySearch ps = World.Party.getSearch(party);
        if (ps != null) {
            World.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
        }
        final MapleCarnivalParty carnivalParty = new MapleCarnivalParty(leader, characters, team);
        try {
            em.getIv().invokeFunction("registerCarnivalParty", this, carnivalParty);
        } catch (ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (NoSuchMethodException ex) {
            //ignore
        }
    }

    public void onMapLoad(final User chr) {
        if (disposed) {
            return;
        }
        try {
            em.getIv().invokeFunction("onMapLoad", this, chr);
        } catch (ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name" + em.getName() + ", Instance name : " + name + ":\n{}", ex);
        } catch (NoSuchMethodException ex) {
            // Ignore, we don't want to update this for all events.
        }
    }

    public boolean iiPacketder(final User chr) {
        return (chr != null && chr.getParty() != null && chr.getParty().getLeader().getId() == chr.getId());
    }

    public void registerSquad(MapleSquad squad, MapleMap map, int questID) {
        if (disposed) {
            return;
        }
        final int mapid = map.getId();

        for (String chr : squad.getMembers()) {
            User player = squad.getChar(chr);
            if (player != null && player.getMapId() == mapid) {
                if (questID > 0) {
                    player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData(String.valueOf(System.currentTimeMillis()));
                }
                registerPlayer(player);
                if (player.getParty() != null) {
                    PartySearch ps = World.Party.getSearch(player.getParty());
                    if (ps != null) {
                        World.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest has started.");
                    }
                }
            }
        }
        squad.setStatus((byte) 2);
        squad.getBeginMap().broadcastMessage(CField.stopClock());
    }

    public boolean isDisconnected(final User chr) {
        if (disposed) {
            return false;
        }
        return (dced.contains(chr.getId()));
    }

    public void removeDisconnected(final int id) {
        if (disposed) {
            return;
        }
        dced.remove(id);
    }

    public EventManager getEventManager() {
        return em;
    }

    public void applyBuff(final User chr, final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(chr);
        GiveBuffMessage buff = new GiveBuffMessage(id);
        chr.write(CWvsContext.messagePacket(buff));
    }

    public void applySkill(final User chr, final int id) {
        SkillFactory.getSkill(id).getEffect(1).applyTo(chr);
    }
}
