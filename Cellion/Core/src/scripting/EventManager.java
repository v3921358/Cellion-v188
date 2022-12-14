/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; w"ithout even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;

import javax.script.Invocable;
import javax.script.ScriptException;

import client.ClientSocket;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.MapleExpedition;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import service.ChannelServer;
import server.MapleSquad;
import server.Randomizer;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleReactorFactory;
import server.maps.objects.User;
import server.maps.objects.Reactor;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.WvsContext;

public class EventManager {

    private static final int[] eventChannel = new int[2];
    private final Invocable iv;
    private final int channel;
    private final Map<String, EventInstanceManager> instances = new WeakHashMap<>();
    private final Properties props = new Properties();
    private final String name;

    public EventManager(ChannelServer cserv, Invocable iv, String name) {
        this.iv = iv;
        this.channel = cserv.getChannel();
        this.name = name;
    }

    public void cancel() {
        try {
            iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay) {
        return EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (NoSuchMethodException | ScriptException ex) {
                    LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay, final EventInstanceManager eim) {
        return EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, eim);
                } catch (NoSuchMethodException | ScriptException ex) {
                    LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
        return EventTimer.getInstance().scheduleAtTimestamp(new Runnable() {

            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException | NoSuchMethodException ex) {
                    System.out.println("Event name : " + name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, timestamp);
    }

    public int getChannel() {
        return channel;
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public EventInstanceManager getInstance(String name) {
        return instances.get(name);
    }

    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public EventInstanceManager newInstance(String name) {
        EventInstanceManager ret = new EventInstanceManager(this, name, channel);
        instances.put(name, ret);
        return ret;
    }

    public void disposeInstance(String name) {
        instances.remove(name);
        if (getProperty("state") != null && instances.isEmpty()) {
            setProperty("state", "0");
        }
        if (getProperty("leader") != null && instances.isEmpty() && getProperty("leader").equals("false")) {
            setProperty("leader", "true");
        }
        if (this.name.equals("CWKPQ")) { //hard code it because i said so
            final MapleSquad squad = ChannelServer.getInstance(channel).getMapleSquad("CWKPQ");//so fkin hacky
            if (squad != null) {
                squad.clear();
                squad.copy();
            }
        }
    }

    public Invocable getIv() {
        return iv;
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public final Properties getProperties() {
        return props;
    }

    public String getName() {
        return name;
    }

    public void startInstance() {
        try {
            iv.invokeFunction("setup", (Object) null);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance_Solo(String mapid, User chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", (Object) mapid);
            eim.registerPlayer(chr);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance(String mapid, User chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", (Object) mapid);
            eim.registerCarnivalParty(chr, chr.getMap(), (byte) 0);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance_Party(String mapid, User chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", (Object) mapid);
            eim.registerParty(chr.getParty(), chr.getMap());
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    //GPQ
    public void startInstance(User character, String leader) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerPlayer(character);
            eim.setProperty("leader", leader);
            eim.setProperty("guildid", String.valueOf(character.getGuildId()));
            setProperty("guildid", String.valueOf(character.getGuildId()));
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance_CharID(User character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", character.getId()));
            eim.registerPlayer(character);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance_CharMapID(User character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", character.getId(), character.getMapId()));
            eim.registerPlayer(character);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance(User character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerPlayer(character);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    //PQ method: starts a PQ
    public void startInstance(MapleParty party, MapleMap map) {
        startInstance(party, map, 255);
    }

    public void startInstance(MapleParty party, MapleMap map, int maxLevel) {
        try {
            int averageLevel = 0, size = 0;
            for (MaplePartyCharacter mpc : party.getMembers()) {
                if (mpc.isOnline() && mpc.getMapid() == map.getId() && mpc.getChannel() == map.getChannel()) {
                    averageLevel += mpc.getLevel();
                    size++;
                }
            }
            if (size <= 0) {
                return;
            }
            averageLevel /= size;
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", Math.min(maxLevel, averageLevel), party.getId()));
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        } catch (Exception ex) {
            //ignore
            startInstance_NoID(party, map, ex);
        }
    }

    public void startInstance(ClientSocket c, MapleExpedition exped, MapleMap map, int maxLevel) {
        try {
            int averageLevel = 0, size = 0;
            for (User mpc : exped.getExpeditionMembers(c)) {
                if (mpc != null && mpc.getMapId() == map.getId() && mpc.getClient().getChannel() == map.getChannel()) {
                    averageLevel += mpc.getLevel();
                    size++;
                }
            }
            if (size <= 0) {
                return;
            }
            averageLevel /= size;
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", Math.min(maxLevel, averageLevel), exped.getId()));
            eim.registerExpedition(c, exped, map);
        } catch (ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        } catch (Exception ex) {
            //ignore
            startInstance_NoID(c, exped, map, ex);
        }
    }

    public void startInstance_NoID(MapleParty party, MapleMap map) {
        startInstance_NoID(party, map, null);
    }

    public void startInstance_NoID(MapleParty party, MapleMap map, final Exception old) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerParty(party, map);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance_NoID(ClientSocket c, MapleExpedition exped, MapleMap map, final Exception old) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerExpedition(c, exped, map);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    //non-PQ method for starting instance
    public void startInstance(EventInstanceManager eim, String leader) {
        try {
            iv.invokeFunction("setup", eim);
            eim.setProperty("leader", leader);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map) {
        startInstance(squad, map, -1);
    }

    public void startInstance(MapleSquad squad, MapleMap map, int questID) {
        if (squad.getStatus() == 0) {
            return; //we dont like cleared squads
        }
        if (!squad.getLeader().isGM()) {
            if (squad.getMembers().size() < squad.getType().i) { //less than 3
                squad.getLeader().dropMessage(5, "The squad has less than " + squad.getType().i + " people participating.");
                return;
            }
            if (name.equals("CWKPQ") && squad.getJobs().size() < 5) {
                squad.getLeader().dropMessage(5, "The squad requires members from every type of job.");
                return;
            }
        }
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", squad.getLeaderName()));
            eim.registerSquad(squad, map, questID);
        } catch (NoSuchMethodException | ScriptException ex) {
            LogHelper.INVOCABLE.get().info("Event name : " + name + ", method Name :\n{}", ex);
        }
    }

    public void warpAllPlayer(int from, int to) {
        final MapleMap tomap = getMapFactory().getMap(to);
        final MapleMap frommap = getMapFactory().getMap(from);
        if (frommap == null || tomap == null) {
            return;
        }
        List<User> list = frommap.getCharacters();

        if (list != null && frommap.getCharactersSize() > 0) {
            for (MapleMapObject mmo : list) {
                ((User) mmo).changeMap(tomap, tomap.getPortal(0));
            }
        }
    }

    public MapleMapFactory getMapFactory() {
        return getChannelServer().getMapFactory();
    }

    public List<User> newCharList() {
        return new ArrayList<>();
    }

    public Mob getMonster(final int id) {
        return LifeFactory.getMonster(id);
    }

    public Reactor getReactor(final int id) {
        return new Reactor(MapleReactorFactory.getReactor(id), id);
    }

    public void broadcastShip(final int mapid, final int effect, final int mode) {
        getMapFactory().getMap(mapid).broadcastPacket(CField.boatPacket(effect, mode));
    }

    public void broadcastYellowMsg(final String msg) {
        getChannelServer().broadcastPacket(WvsContext.yellowChat(msg));
    }

    public void broadcastServerMsg(final int type, final String msg, final boolean weather) {
        if (!weather) {
            getChannelServer().broadcastPacket(WvsContext.broadcastMsg(type, msg));
        } else {
            for (MapleMap load : getMapFactory().getAllMaps()) {
                if (load.getCharactersSize() > 0) {
                    load.startMapEffect(msg, type);
                }
            }
        }
    }

    public boolean scheduleRandomEvent() {
        boolean event = false;
        for (int i = 0; i < eventChannel.length; i++) {
            event |= scheduleRandomEventInChannel(eventChannel[i]);
        }
        return event;
    }

    public boolean scheduleRandomEventInChannel(int chz) {
        final ChannelServer cs = ChannelServer.getInstance(chz);
        if (cs == null || cs.getEvent() > -1) {
            return false;
        }
        MapleEventType t = null;
        while (t == null) {
            for (MapleEventType x : MapleEventType.values()) {
                if (Randomizer.nextInt(MapleEventType.values().length) == 0 && x != MapleEventType.OxQuiz && x != MapleEventType.Survival && x != MapleEventType.CokePlay) {
                    t = x;
                    break;
                }
            }
        }
        System.out.println("[Event] " + t.name() + " in channel " + chz);
        final String msg = MapleEvent.scheduleEvent(t, cs);
        if (msg.length() > 0) {
            broadcastYellowMsg(msg);
            return false;
        }
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (cs.getEvent() >= 0) {
                    MapleEvent.setEvent(cs, true);
                }
            }
        }, 180000);
        return true;
    }

    public void setWorldEvent() {
        for (int i = 0; i < eventChannel.length; i++) {
            eventChannel[i] = Randomizer.nextInt(ChannelServer.getAllInstances().size() - 4) + 2 + i; //2-13
        }
    }

    ///
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    /**
     * Adds and returns an event that executes after a given delay.
     *
     * @param callable The method that should be called
     * @param delay The delay (in ms) after which the call should start
     * @param <V> Return type of the given callable
     * @return The created event (ScheduledFuture)
     */
    public static <V> ScheduledFuture<V> addEvent(Callable<V> callable, long delay) {
        return scheduler.schedule(callable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds and returns an event that executes after a given delay.
     *
     * @param callable The method that should be called
     * @param delay The delay after which the call should start
     * @param timeUnit The time unit of the delay
     * @param <V> The return type of the given callable
     * @return The created event (ScheduledFuture)
     */
    public static <V> ScheduledFuture<V> addEvent(Callable<V> callable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(callable, delay, timeUnit);
    }

    /**
     * Adds and returns an event that executes after a given initial delay, and then after every delay. See
     * https://stackoverflow.com/questions/24649842/scheduleatfixedrate-vs-schedulewithfixeddelay for difference between this method and
     * addFixedDelayEvent.
     *
     * @param runnable The method that should be run
     * @param initialDelay The time that it should take before the first execution should start
     * @param delay The time it should (in ms) take between the start of execution n and execution n+1
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addFixedRateEvent(Runnable runnable, long initialDelay, long delay) {
        return scheduler.scheduleAtFixedRate(runnable, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds and returns an event that executes after a given initial delay, and then after every delay. See
     * https://stackoverflow.com/questions/24649842/scheduleatfixedrate-vs-schedulewithfixeddelay for difference between this method and
     * addFixedDelayEvent.
     *
     * @param runnable The method that should be run
     * @param initialDelay The time that it should take before the first execution should start
     * @param delay The time it should (in ms) take between the start of execution n and execution n+1
     * @param executes The amount of times the
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addFixedRateEvent(Runnable runnable, long initialDelay, long delay, int executes) {
        ScheduledFuture sf = scheduler.scheduleAtFixedRate(runnable, initialDelay, delay, TimeUnit.MILLISECONDS);
        addEvent(() -> sf.cancel(false), 10 + initialDelay + delay * executes);
        return sf;
    }

    /**
     * Adds and returns an event that executes after a given initial delay, and then after every delay. See
     * https://stackoverflow.com/questions/24649842/scheduleatfixedrate-vs-schedulewithfixeddelay for difference between this method and
     * addFixedDelayEvent.
     *
     * @param runnable The method that should be run
     * @param initialDelay The time that it should take before the first execution should start
     * @param delay The time it should take between the start of execution n and execution n+1
     * @param timeUnit The time unit of the delays
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addFixedRateEvent(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(runnable, initialDelay, delay, timeUnit);
    }

    /**
     * Adds and returns an event that executes after a given initial delay, and then after every delay after the task has finished. See
     * https://stackoverflow.com/questions/24649842/scheduleatfixedrate-vs-schedulewithfixeddelay for difference between this method and
     * addFixedDelayEvent.
     *
     * @param runnable The method that should be run
     * @param initialDelay The time that it should take before the first execution should start
     * @param delay The time it should (in ms) take between the start of execution n and execution n+1
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addFixedDelayEvent(Runnable runnable, long initialDelay, long delay) {
        return scheduler.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds and returns an event that executes after a given initial delay, and then after every delay. See
     * https://stackoverflow.com/questions/24649842/scheduleatfixedrate-vs-schedulewithfixeddelay for difference between this method and
     * addFixedDelayEvent.
     *
     * @param runnable The method that should be run
     * @param initialDelay The time that it should take before the first execution should start
     * @param delay The time it should take between the start of execution n and execution n+1
     * @param timeUnit The time unit of the delay
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addFixedDelayEvent(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    /**
     * Adds and returns an event that executes after a given delay.
     *
     * @param runnable The method that should be run
     * @param delay The delay (in ms) after which the call should start
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addEvent(Runnable runnable, long delay) {
        return scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Adds and returns an event that executes after a given delay.
     *
     * @param runnable The method that should be run
     * @param delay The delay after which the call should start
     * @param timeUnit The time unit of the delay
     * @return The created event (ScheduledFuture)
     */
    public static ScheduledFuture addEvent(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(runnable, delay, timeUnit);
    }
}
