package scripting;

import enums.InventoryType;
import client.inventory.*;
import scripting.provider.NPCScriptManager;
import java.awt.Point;
import java.time.LocalDateTime;
import java.util.List;
import client.ClientSocket;
import client.QuestStatus;
import client.Trait.MapleTraitType;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import handling.world.MapleExpedition;
import handling.world.MapleGuild;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import java.util.Map;
import service.ChannelServer;
import enums.NPCInterfaceType;
import enums.NPCChatType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStringInformationProvider;
import server.Randomizer;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.Event_DojoAgent;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import enums.SavedLocationType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.maps.objects.Reactor;
import server.messages.GiveBuffMessage;
import server.quest.Quest;
import tools.Pair;
import tools.Utility;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.UIPacket;
import tools.packet.WvsContext;
import tools.packet.WvsContext.InfoPacket;
import tools.packet.PetPacket;

public abstract class AbstractPlayerInteraction {

    protected ClientSocket c;
    protected int id, id2;
    protected String script;

    public AbstractPlayerInteraction(final ClientSocket c, final int id, final int id2, final String script) {
        this.c = c;
        this.id = id;
        this.id2 = id2;
        this.script = script;
    }

    public final ClientSocket getClient() {
        return c;
    }

    public final ClientSocket getC() {
        return c;
    }

    public User getChar() {
        return c.getPlayer();
    }

    public final ChannelServer getChannelServer() {
        return c.getChannelServer();
    }

    public final User getPlayer() {
        return c.getPlayer();
    }

    public final boolean isInventoryFull(User pPlayer, int nInventoryType) {
        InventoryType pInventory = InventoryType.getByType((byte) nInventoryType);
        if (pPlayer.getInventory(pInventory).isFull()) {
            return true;
        }
        return false;
    }

    public final EventManager getEventManager(final String event) {
        return c.getChannelServer().getEventSM().getEventManager(event);
    }

    public final EventInstanceManager getEventInstance() {
        return c.getPlayer().getEventInstance();
    }

    public final void openNpc(int npc, String filename) {
        NPCScriptManager.getInstance().start(c, npc, filename);
    }

    public final void openNpc(ClientSocket client, int npc, String filename) {
        NPCScriptManager.getInstance().start(client, npc, filename);
    }

    public final void warp(final int map) {
        final MapleMap mapz = getWarpMap(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp_Instanced(final int map) {
        final MapleMap mapz = getMap_Instanced(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        if (portal != 0 && map == c.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0) { //estimation
                c.SendPacket(CField.instantMapWarp((byte) portal)); //until we get packet for far movement, this will do
                c.getPlayer().checkFollow();
                c.getPlayer().getMap().movePlayer(c.getPlayer(), portalPos);
            } else {
                c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warp(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == c.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0) { //estimation
                c.getPlayer().checkFollow();
                c.SendPacket(CField.instantMapWarp((byte) c.getPlayer().getMap().getPortal(portal).getId()));
                c.getPlayer().getMap().movePlayer(c.getPlayer(), new Point(c.getPlayer().getMap().getPortal(portal).getPosition()));
            } else {
                c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warpMap(final int mapid, final int portal) {
        final MapleMap map = getMap(mapid);
        if (map == null) {
            return;
        }
        for (User chr : c.getPlayer().getMap().getCharacters()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }

    public final void warpByName(final int mapid, final String chrname) {
        User chr = c.getChannelServer().getPlayerStorage().getCharacterByName(chrname);
        if (chr == null) {
            c.getPlayer().dropMessage(1, "Could not find the character.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final MapleMap mapz = getWarpMap(mapid);
        try {
            chr.changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
            chr.getClient().removeClickedNPC();
            NPCScriptManager.getInstance().dispose(chr.getClient());
            chr.getClient().SendPacket(WvsContext.enableActions());
        } catch (Exception e) {
            chr.changeMap(mapz, mapz.getPortal(0));
            chr.getClient().removeClickedNPC();
            NPCScriptManager.getInstance().dispose(chr.getClient());
            chr.getClient().SendPacket(WvsContext.enableActions());
        }
    }

    public final void mapChangeTimer(final int map, final int nextmap, final int time, final boolean notice) {
        final List<User> current = c.getChannelServer().getMapFactory().getMap(map).getCharacters();
        c.getChannelServer().getMapFactory().getMap(map).broadcastPacket(CField.getClock(time));
        if (notice) {
            c.getChannelServer().getMapFactory().getMap(map).startMapEffect("You will be moved out of the map when the timer ends.", 5120041);
        }
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (current != null) {
                    for (User chrs : current) {
                        chrs.changeMap(nextmap, 0);
                    }
                }
            }
        }, time * 1000); // seconds
    }

    public final void startSelfTimer(final int time_sec, final int tomap) {
        c.getPlayer().startMapTimeLimitTask(time_sec, this.getMap(tomap));
    }

    public final void playPortalSE() {
        c.SendPacket(EffectPacket.showForeignEffect(-1, EffectPacket.PlayPortalSE));
    }

    private MapleMap getWarpMap(final int map) {
        return ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(map);
    }

    public final MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    public final MapleMap getMap(final int map) {
        return getWarpMap(map);
    }

    public final MapleMap getMap_Instanced(final int map) {
        return c.getPlayer().getEventInstance() == null ? getMap(map) : c.getPlayer().getEventInstance().getMapInstance(map);
    }

    public void spawnMonster(final int id, final int qty) {
        spawnMob(id, qty, c.getPlayer().getTruePosition());
    }

    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(LifeFactory.getMonster(id), new Point(x, y));
        }
    }

    public final void spawnMob(final int id, final int qty, final int x, final int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    public final void spawnMob(final int id, final int x, final int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    private void spawnMob(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; i++) {
            c.getPlayer().getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(id), pos);
        }
    }

    public final void killMob(int ids) {
        c.getPlayer().getMap().killMonster(ids);
    }

    public final void killAllMob() {
        c.getPlayer().getMap().killAllMonsters(true);
    }

    public final void addHP(final int delta) {
        c.getPlayer().addHP(delta);
    }

    public final int getPlayerStat(final String type) {
        switch (type) {
            case "LVL":
                return c.getPlayer().getLevel();
            case "STR":
                return c.getPlayer().getStat().getStr();
            case "DEX":
                return c.getPlayer().getStat().getDex();
            case "INT":
                return c.getPlayer().getStat().getInt();
            case "LUK":
                return c.getPlayer().getStat().getLuk();
            case "HP":
                return c.getPlayer().getStat().getHp();
            case "MP":
                return c.getPlayer().getStat().getMp();
            case "MAXHP":
                return c.getPlayer().getStat().getMaxHp();
            case "IndieMMP":
                return c.getPlayer().getStat().getMaxMp();
            case "RAP":
                return c.getPlayer().getRemainingAp();
            case "RSP":
                return c.getPlayer().getRemainingSp();
            case "GID":
                return c.getPlayer().getGuildId();
            case "GRANK":
                return c.getPlayer().getGuildRank();
            case "ARANK":
                return c.getPlayer().getAllianceRank();
            case "GM":
                return c.getPlayer().isGM() ? 1 : 0;
            case "ADMIN":
                return c.getPlayer().isAdmin() ? 1 : 0;
            case "GENDER":
                return c.getPlayer().getGender();
            case "FACE":
                return c.getPlayer().getFace();
            case "HAIR":
                return c.getPlayer().getHair();
        }
        return -1;
    }

    public final String getName() {
        return c.getPlayer().getName();
    }

    public final boolean haveItem(final int itemid) {
        return haveItem(itemid, 1);
    }

    public final boolean haveItem(final int itemid, final int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public final boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(InventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public final boolean canHoldSlots(final int slot) {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(InventoryType.getByType((byte) i)).isFull(slot)) {
                return false;
            }
        }
        return true;
    }

    public final boolean canHold(final int itemid) {
        return c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final boolean canHold(final int itemid, final int quantity) {
        return MapleInventoryManipulator.checkSpace(c, itemid, quantity, "");
    }

    public final QuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(Quest.getInstance(id));
    }

    public final QuestStatus getQuestNoRecord(final int id) {
        return c.getPlayer().getQuestNoAdd(Quest.getInstance(id));
    }

    public final byte getQuestStatus(final int id) {
        return c.getPlayer().getQuestStatus(id).getValue();
    }

    public final boolean isQuestActive(final int id) {
        return getQuestStatus(id) == 1;
    }

    public final boolean isQuestFinished(final int id) {
        return getQuestStatus(id) == 2;
    }

    public final void showQuestMsg(final String msg) {
        c.SendPacket(WvsContext.showQuestMsg(msg));
    }

    public final void forceStartQuest(final int id, final String data) {
        Quest.getInstance(id).forceStart(c.getPlayer(), 0, data);
    }

    public final void forceStartQuest(final int id, final int data, final boolean filler) {
        Quest.getInstance(id).forceStart(c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(final int id) {
        Quest.getInstance(id).forceStart(c.getPlayer(), 0, null);
    }

    public void forceCompleteQuest(final int id) {
        Quest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    public void spawnNpc(final int npcId) {
        c.getPlayer().getMap().spawnNpc(npcId, c.getPlayer().getPosition());
    }

    public final void spawnNpc(final int npcId, final int x, final int y) {
        c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public final void spawnNpc(final int npcId, final Point pos) {
        c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public final void spawnNpcForPlayer(final int npcId, final int x, final int y) {
        c.getPlayer().getMap().spawnNpcForPlayer(c, npcId, new Point(x, y));
    }

    public final void removeNpc(final int mapid, final int npcId) {
        c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public final void removeNpc(final int npcId) {
        c.getPlayer().getMap().removeNpc(npcId);
    }

    public final void hideNpc(final int npcId) {
        c.getPlayer().getMap().hideNpc(npcId);
    }

    public final void respawn(final boolean force) {
        c.getPlayer().getMap().OnRespawn(force, System.currentTimeMillis());
    }

    public final void forceStartReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllMapObjects(MapleMapObjectType.REACTOR)) {
            Reactor react = (Reactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(c);
                break;
            }
        }
    }

    public void destroyReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllMapObjects(MapleMapObjectType.REACTOR)) {
            Reactor react = (Reactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public void hitReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllMapObjects(MapleMapObjectType.REACTOR)) {
            final Reactor react = (Reactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    public String getInventoryItems(String type) {
        MapleInventory inv = null;
        switch (type) {
            case "EQUIP":
                inv = c.getPlayer().getInventory(InventoryType.EQUIP);
                break;
            case "USE":
                inv = c.getPlayer().getInventory(InventoryType.USE);
                break;
            case "ETC":
                inv = c.getPlayer().getInventory(InventoryType.ETC);
                break;
            case "SETUP":
                inv = c.getPlayer().getInventory(InventoryType.SETUP);
                break;
            case "CASH":
                inv = c.getPlayer().getInventory(InventoryType.CASH);
                break;
        }
        StringBuilder sb = new StringBuilder();
        for (Item item : inv.list()) {
            sb.append("#L" + item.getPosition() + "##v" + item.getItemId() + "##t" + item.getItemId() + "##l \r\n");
        }
        String items = sb.toString();
        return items;
    }

    /**
     * Returns a string containing all items for selected invType
     * @param sCharacter
     * @param invType
     * @return
     */
    public String accessInventory(String sCharacter, byte invType){
        User pPlayer = Utility.requestCharacter(sCharacter);
        if (pPlayer == null) {
            c.getPlayer().dropMessage(5,"There was a problem accessing the inventory of Character (" + sCharacter + ")");
            return "";
        }
        MapleInventory pInv = null;
        pInv = pPlayer.getInventory(InventoryType.getByType(invType));
        StringBuilder sb = new StringBuilder();
        for (Item pItem : pInv.list()) {
            sb.append("#L" + pItem.getPosition() + "##v" + pItem.getItemId() + "##t" + pItem.getItemId() + "##l \r\n");
        }
        String sItem = sb.toString();
        return sItem;
    }

    /**
     * Delete item by slot ID
     * @param sCharacter
     * @param invType
     * @param slot
     * @param quantity
     */
    public void deleteItemBySlot(String sCharacter, byte invType, short slot, short quantity) {
        User pPlayer = Utility.requestCharacter(sCharacter);
        InventoryType type = InventoryType.getByType(invType);
        ClientSocket targetSocket = pPlayer.getClient();
        if (pPlayer == null) {
            c.getPlayer().dropMessage(5,"There was a problem accessing the inventory of Character (" + sCharacter + ")");
            return;
        }
        MapleInventoryManipulator.removeFromSlot(targetSocket, type, slot, quantity, false); //fromDrop? change this if needed
    }

    /**
     * Delete item by item ID
     * @param sCharacter
     * @param invType
     * @param itemId
     * @param quantity
     */
    public void deleteItemById(String sCharacter, byte invType, int itemId, int quantity) {
        User pPlayer = Utility.requestCharacter(sCharacter);
        InventoryType type = InventoryType.getByType(invType);
        ClientSocket targetSocket = pPlayer.getClient();
        if (pPlayer == null) {
            c.getPlayer().dropMessage(5,"There was a problem accessing the inventory of Character (" + sCharacter + ")");
            return;
        }
        MapleInventoryManipulator.removeById(targetSocket, type, itemId, quantity, false, false); //fromDrop? change this if needed
    }

    /**
     * Return Item ID from Inventory Slot
     * @param nInventoryType
     * @param nSlot
     * @return
     */
    public int getItemFromSlot(byte nInventoryType, short nSlot) {
        InventoryType pType = InventoryType.getByType(nInventoryType);
        Item pItem = c.getPlayer().getInventory(pType).getItem(nSlot);
        return pItem.getItemId();
    }

    public void gainCube(int itemId, short nQuantity) {
            Item item;
            item = new Item(itemId, (byte) 0, nQuantity, (byte) 0);
            MapleInventoryManipulator.addbyItem(c, item);
    }

    public final int getJob() {
        return c.getPlayer().getJob();
    }

    public final void gainNX(final int nAmount) {
        c.getPlayer().gainNX(nAmount, true);
    }

    public final void gainItemPeriod(final int id, final short quantity, final int period) { //period is in days
        gainItem(id, quantity, false, period, false, -1, "");
    }

    public final void gainItemPeriod(final int id, final short quantity, final long period, final String owner) { //period is in days
        gainItem(id, quantity, false, period, false, -1, owner);
    }

    public final void gainItemPeriod(final int id, final short quantity, final int period, boolean hours) { //period is in days
        gainItem(id, quantity, false, period, hours, -1, "");
    }

    public final void gainItemPeriod(final int id, final short quantity, final long period, boolean hours, final String owner) { //period is in days
        gainItem(id, quantity, false, period, hours, -1, owner);
    }

    public final void gainItem(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, false, -1, "");
    }

    public final void gainItemSilent(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, false, -1, "", c, false);
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats) {
        gainItem(id, quantity, randomStats, 0, false, -1, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final int slots) {
        gainItem(id, quantity, randomStats, 0, false, slots, "");
    }

    public final void gainItem(final int id, final short quantity, final long period) {
        gainItem(id, quantity, false, period, false, -1, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots) {
        gainItem(id, quantity, randomStats, period, false, slots, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, boolean hours, final int slots, final String owner) {
        gainItem(id, quantity, randomStats, period, hours, slots, owner, c);
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, boolean hours, final int slots, final String owner, final ClientSocket cg) {
        gainItem(id, quantity, randomStats, period, hours, slots, owner, cg, true);
    }

//    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, boolean hours, final int slots, boolean potential, final String owner) {
//        gainItem(id, quantity, randomStats, period, hours, slots, potential, owner, c);
//    }
    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, boolean hours, final int slots, final String owner, final ClientSocket cg, final boolean show) {
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final InventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if (type.equals(InventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (randomStats ? ii.randomizeStats((Equip) ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0) {
                    item.setExpiration(System.currentTimeMillis() + (period * (hours ? 1 : 24) * 60 * 60 * 1000));
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                item.setGMLog("Received from interaction " + this.id + " (" + id2 + ") on " + LocalDateTime.now());
                final String name = ii.getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                    final String msg = "< " + name + " > has been rewarded.";
                    cg.getPlayer().dropMessage(-1, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period, hours, "Received from interaction " + this.id + " (" + id2 + ") on " + LocalDateTime.now());
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        if (show) {
            cg.SendPacket(InfoPacket.getShowItemGain(id, quantity, true));
        }
    }

    public final boolean removeItem(final int id) { //quantity 1
        if (MapleInventoryManipulator.removeById_Lock(c, GameConstants.getInventoryType(id), id)) {
            c.SendPacket(InfoPacket.getShowItemGain(id, (short) -1, true));
            return true;
        }
        return false;
    }

    public final void changeMusic(final String songName) {
        getPlayer().getMap().broadcastPacket(CField.musicChange(songName));
    }

    public final void worldMessage(final int type, final String message) {
        World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(type, message));
    }

    // default playerMessage and mapMessage to use type 5
    public final void playerMessage(final String message) {
        playerMessage(5, message);
    }

    public final void mapMessage(final String message) {
        mapMessage(5, message);
    }

    public final void guildMessage(final String message) {
        guildMessage(5, message);
    }

    public final void playerMessage(final int type, final String message) {
        c.getPlayer().dropMessage(type, message);
    }

    public final void mapMessage(final int type, final String message) {
        c.getPlayer().getMap().broadcastPacket(WvsContext.broadcastMsg(type, message));
    }

    public final void guildMessage(final int type, final String message) {
        if (getPlayer().getGuildId() > 0) {
            World.Guild.guildPacket(getPlayer().getGuildId(), WvsContext.broadcastMsg(type, message));
        }
    }

    public final MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    public final MapleGuild getGuild(int guildid) {
        return World.Guild.getGuild(guildid);
    }

    public final MapleParty getParty() {
        return c.getPlayer().getParty();
    }

    public final int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public final boolean iiPacketder() {
        if (getPlayer().getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == c.getPlayer().getId();
    }

    public final boolean isAllPartyMembersAllowedJob(final int job) {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    public final boolean allMembersHere() {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            final User chr = c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    public final void warpParty(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, 0);
            return;
        }
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public final void warpParty(final int mapId, final int portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        final boolean rand = portal < 0;
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                } else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }

    public final void warpParty_Instanced(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId);
            return;
        }
        final MapleMap target = getMap_Instanced(mapId);

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public void gainMeso(int gain) {
        c.getPlayer().gainMeso(gain, true, true);
    }

    public void gainExp(int gain) {
        c.getPlayer().gainExp(gain, true, true, true);
    }

    public void gainExpR(int gain) {
        double exp = gain * c.getChannelServer().getExpRate(c.getPlayer() != null ? c.getPlayer().getWorld() : 0);

        c.getPlayer().gainExp((int) exp, true, true, true);
    }

    public void gainSp(final int amount) {
        c.getPlayer().gainSP((short) amount);
    }

    public final void givePartyItems(final int id, final short quantity, final List<User> party) {
        for (User chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity, "Received from party interaction " + id + " (" + id2 + ")");
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().SendPacket(InfoPacket.getShowItemGain(id, quantity, true));
        }
    }

    public void addPartyTrait(String t, int e, final List<User> party) {
        for (final User chr : party) {
            chr.getTrait(MapleTraitType.valueOf(t)).addExp(e, chr);
        }
    }

    public void addPartyTrait(String t, int e) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            addTrait(t, e);
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.getTrait(MapleTraitType.valueOf(t)).addExp(e, curChar);
            }
        }
    }

    public void addTrait(String t, int e) {
        getPlayer().getTrait(MapleTraitType.valueOf(t)).addExp(e, getPlayer());
    }

    public final void givePartyItems(final int id, final short quantity) {
        givePartyItems(id, quantity, false);
    }

    public final void givePartyItems(final int id, final short quantity, final boolean removeAll) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                gainItem(id, (short) (removeAll ? -curChar.itemQuantity(id) : quantity), false, 0, false, 0, "", curChar.getClient());
            }
        }
    }

    public final void givePartyExp_PQ(final int maxLevel, final double mod, final List<User> party) {
        for (final User chr : party) {
            final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(chr.getLevel() > maxLevel ? (maxLevel + ((maxLevel - chr.getLevel()) / 10)) : chr.getLevel()) / (Math.min(chr.getLevel(), maxLevel) / 5.0) / (mod * 2.0));
            chr.gainExp((int) (amount * c.getChannelServer().getExpRate(chr.getWorld())), true, true, true);
        }
    }

    public final void gainExp_PQ(final int maxLevel, final double mod) {
        final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? (maxLevel + (getPlayer().getLevel() / 10)) : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0) / mod);
        gainExp((int) (amount * c.getChannelServer().getExpRate(c.getPlayer() != null ? c.getPlayer().getWorld() : 0)));
    }

    public final void givePartyExp_PQ(final int maxLevel, final double mod) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? (maxLevel + (getPlayer().getLevel() / 10)) : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0) / mod);
            gainExp((int) (amount * c.getChannelServer().getExpRate(c.getPlayer() != null ? c.getPlayer().getWorld() : 0)));
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                final int amount = (int) Math.round(GameConstants.getExpNeededForLevel(curChar.getLevel() > maxLevel ? (maxLevel + (curChar.getLevel() / 10)) : curChar.getLevel()) / (Math.min(curChar.getLevel(), maxLevel) / 10.0) / mod);
                curChar.gainExp((int) (amount * c.getChannelServer().getExpRate(curChar.getWorld())), true, true, true);
            }
        }
    }

    public final void givePartyExp(final int amount, final List<User> party) {
        for (final User chr : party) {
            chr.gainExp((int) (amount * c.getChannelServer().getExpRate(chr.getWorld())), true, true, true);
        }
    }

    public final void givePartyExp(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainExp((int) (amount * c.getChannelServer().getExpRate(c.getPlayer() != null ? c.getPlayer().getWorld() : 0)));
        } else {
            final int cMap = getPlayer().getMapId();
            for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
                final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
                if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                    curChar.gainExp((int) (amount * c.getChannelServer().getExpRate(curChar.getWorld())), true, true, true);
                }
            }
        }
    }

    public final void endPartyQuest(final int amount, final List<User> party) {
        for (final User chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public final void endPartyQuest(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.endPartyQuest(amount);
            }
        }
    }

    public final void removeFromParty(final int id, final List<User> party) {
        for (final User chr : party) {
            final int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().SendPacket(InfoPacket.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    public final void removeFromParty(final int id) {
        givePartyItems(id, (short) 0, true);
    }

    public final void useSkill(final int skill, final int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(c.getPlayer());
    }

    public final void useItem(final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        GiveBuffMessage buff = new GiveBuffMessage(id);
        c.getPlayer().write(WvsContext.messagePacket(buff));
    }

    public final void cancelItem(final int id) {
        c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

    public final int getMorphState() {
        return c.getPlayer().getMorphState();
    }

    public final void removeAll(final int id) {
        c.getPlayer().removeAll(id);
    }

    public final void gainCloseness(final int closeness, final int index) {
        final Pet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness((int) (pet.getCloseness() + (closeness * getChannelServer().getTraitRate())));
            //  getClient().getPlayer().forceUpdateItem(pet.getItem());
            getClient().SendPacket(PetPacket.updatePet(pet, getPlayer().getInventory(InventoryType.CASH).getItem((byte) pet.getItem().getPosition()), true));
        }
    }

    public final void gainClosenessAll(final int closeness) {
        for (final Pet pet : getPlayer().getPets()) {
            if (pet != null && pet.getSummoned()) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getPlayer().forceUpdateItem(pet.getItem());
                getClient().SendPacket(PetPacket.updatePet(pet, getPlayer().getInventory(InventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
            }
        }
    }

    public final void givePartyNX(final int amount, final List<User> party) {
        for (final User chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    public final void givePartyNX(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainNX(amount);
            return;
        }
        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final User curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.modifyCSPoints(1, amount, true);
            }
        }
    }

    public final void resetMap(final int mapid) {
        getMap(mapid).resetFully();
    }

    public final void openNpc(final int id) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(getClient(), id, null);
    }

    public final void openNpc(final ClientSocket cg, final int id) {
        cg.removeClickedNPC();
        NPCScriptManager.getInstance().start(cg, id, null);
    }

    public int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    public boolean haveMonster(final int mobid) {
        for (MapleMapObject obj : c.getPlayer().getMap().getAllMapObjects(MapleMapObjectType.MONSTER)) {
            final Mob mob = (Mob) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    public int getChannelNumber() {
        return c.getChannel();
    }

    public int getMonsterCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getAllMapObjectSize(MapleMapObjectType.MONSTER);
    }

    public void teachSkill(final int id, final int level, final byte masterlevel) {
        getPlayer().changeSingleSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public void teachSkill(final int id, int level) {
        final Skill skil = SkillFactory.getSkill(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSingleSkillLevel(skil, level, (byte) skil.getMaxLevel());
    }

    public int getPlayerCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    /*  
    *   spawnMonsterInMap
    *   @author Mazen Massoud
    *
    *   @purpose Spawns a monster to a set x/y coordinate on a specific map.
    *   @param (Map ID, Mob ID, X-Coordinate, Y-Coordinate).
    **/
    public void spawnMonsterInMap(final int nMapId, final int nMobId, int posX, int posY) {
        Mob pMob = LifeFactory.getMonster(nMobId);
        c.getChannelServer().getMapFactory().getMap(nMapId).spawnMonsterOnGroundBelow(pMob, new Point(posX, posY));
    }

    public void spawnModifiedMonsterInMap(final int nMapId, final int nMobId, int posX, int posY, long nNewHp) {
        Mob pMob = LifeFactory.getMonster(nMobId);
        pMob.setHp(nNewHp);
        pMob.getStats().setHp(nNewHp);
        c.getChannelServer().getMapFactory().getMap(nMapId).spawnMonsterOnGroundBelow(pMob, new Point(posX, posY));
    }

    public final void dojo_getUp() {
        //int sec = 12;//getCurrentTime
//        long curtime = getCurrentTime();
//        System.err.println(curtime);
        c.getPlayer().updateInfoQuest(7215, "stage=6;type=1;token=3");
        c.getPlayer().updateInfoQuest(7218, "1");
        for (int i = 0; i < 3; i++) {
            c.getPlayer().updateInfoQuest(7281, "item=0;chk=0;cNum=0;sec=2;stage=0;lBonus=0"); //last stage
        }
        for (int i = 0; i < 2; i++) {
            c.getPlayer().updateInfoQuest(7281, "item=0;chk=0;cNum=0;sec=2;stage=0;lBonus=0");
        }
        c.getPlayer().updateInfoQuest(7216, "3");
        c.getPlayer().updateInfoQuest(7214, "5");
        c.getPlayer().updateInfoQuest(7215, "0");
        //c.write(InfoPacket.updateInfoQuest(1207, "min=1;tuto=1")); //old - 1207, "pt=1;min=4;belt=1;tuto=1")); //todo
        //c.write(InfoPacket.updateInfoQuest(7281, "item=0;chk=0;cNum=0;sec=" + sec + ";stage=0;lBonus=0"));
        c.SendPacket(EffectPacket.Mulung_DojoUp2());
        c.SendPacket(CField.instantMapWarp((byte) 6));
    }

    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(c.getPlayer(), fromresting, c.getPlayer().getMap());
        }
        return Event_DojoAgent.warpNextMap_Agent(c.getPlayer(), fromresting);
    }

    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting, final int mapid) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(c.getPlayer(), fromresting, getMap(mapid));
        }
        return Event_DojoAgent.warpNextMap_Agent(c.getPlayer(), fromresting);
    }

    public final int dojo_getPts() {
        return c.getPlayer().getIntNoRecord(GameConstants.DOJO);
    }

    public final MapleEvent getEvent(final String loc) {
        return c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }

    public final int getSavedLocation(final String loc) {
        final Integer ret = c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 950000100;
        }
        return ret;
    }

    public final void saveLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public final void saveReturnLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc), c.getPlayer().getMap().getReturnMap().getId());
    }

    public final void clearSavedLocation(final String loc) {
        c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public final void summonMsg(final String msg) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.SendPacket(UIPacket.UserTutorMessage(msg));
    }

    public final void summonMsg(final int type) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.SendPacket(UIPacket.UserTutorMessage(type));
    }

    public final void showInstruction(final String msg, final int width, final int height) {
        c.SendPacket(CField.sendHint(msg, width, height));
    }

    public final void playerSummonHint(final boolean summon) {
        c.getPlayer().setHasSummon(summon);
        c.SendPacket(UIPacket.UserHireTutor(summon));
    }

    public final String getInfoQuest(final int id) {
        return c.getPlayer().getInfoQuest(id);
    }

    public final void updateInfoQuest(final int id, final String data) {
        c.getPlayer().updateInfoQuest(id, data);
    }

    public final boolean getEvanIntroState(final String data) {
        return getInfoQuest(22013).equals(data);
    }

    public final void updateEvanIntroState(final String data) {
        updateInfoQuest(22013, data);
    }

    public final void Aran_Start() {
        c.SendPacket(CField.Aran_Start());
    }

    public final void evanTutorial(final String data, final int v1) {
        c.SendPacket(NPCPacket.getEvanTutorial(data));
    }

    public final void showWZUOLEffect(final String data) {
        c.SendPacket(EffectPacket.showWZUOLEffect(data, false));
    }

    public final void showReservedEffect_CutScene(final String data) {
        c.SendPacket(EffectPacket.showReservedEffect_CutScene(data));
    }

    public final void EarnTitleMsg(final String data) {
        c.SendPacket(WvsContext.getTopMsg(data));
    }

    public final void topMsg(final String data) {
        c.SendPacket(WvsContext.getTopMsg(data));
    }

    public final void EnableUI(final short i) {
        c.SendPacket(UIPacket.IntroEnableUI(i > 0));
    }

    public final void DisableUI(final boolean enabled) {
        c.SendPacket(UIPacket.IntroEnableUI(enabled));
    }

    public final void MovieClipIntroUI(final boolean enabled) {
        c.SendPacket(UIPacket.IntroEnableUI(enabled));
        c.SendPacket(UIPacket.IntroLock(enabled));
    }

    public InventoryType getInvType(int i) {
        return InventoryType.getByType((byte) i);
    }

    public String getItemName(final int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flags) {
        if (id >= 5001000 || id < 5000000) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(c, id, (short) 1, "", Pet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags), 45, false, "Pet from interaction " + id + " (" + id2 + ")" + " on " + LocalDateTime.now());
        } catch (NullPointerException ex) {
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(c, getInvType(invType), slot, quantity, true);
    }

    public void gainGP(final int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.gainGP(getPlayer().getGuildId(), gp); //1 for
    }

    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return World.Guild.getGP(getPlayer().getGuildId()); //1 for
    }

    public void showMapEffect(String path) {
        getClient().SendPacket(CField.MapEff(path));
    }

    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    public EventInstanceManager getDisconnected(String event) {
        EventManager em = getEventManager(event);
        if (em == null) {
            return null;
        }
        for (EventInstanceManager eim : em.getInstances()) {
            if (eim.isDisconnected(c.getPlayer()) && eim.getPlayerCount() > 0) {
                return eim;
            }
        }
        return null;
    }

    public boolean isAllReactorState(final int reactorId, final int state) {
        boolean ret = false;
        for (MapleMapObject o : getMap().getAllMapObjects(MapleMapObjectType.REACTOR)) {
            final Reactor r = (Reactor) o;

            if (r.getReactorId() == reactorId) {
                ret = r.getState() == state;
            }
        }
        return ret;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPlayer().getTruePosition());
    }

    // summon one monster, remote location
    public void spawnMonster(int id, int x, int y) {
        spawnMonster(id, 1, new Point(x, y));
    }

    // multiple monsters, remote location
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    // handler for all spawnMonster
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(id), pos);
        }
    }

    public void sendNPCText(final String text, final int npc) {
        getMap().broadcastPacket(NPCPacket.getNPCTalk(npc, NPCChatType.OK, text, NPCInterfaceType.NPC_Cancellable, npc));
    }

    public boolean getTempFlag(final int flag) {
        return (c.getChannelServer().getTempFlag() & flag) == flag;
    }

    public void sendUIWindow(final int type, final int npc) {
        c.SendPacket(CField.UIPacket.openUIOption(type, npc));
    }

    public void logPQ(String text) {
//	FileoutputUtil.log(FileoutputUtil.PQ_Log, text);
    }

    public void trembleEffect(int type, int delay) {
        c.SendPacket(CField.trembleEffect(type, delay));
    }

    public int nextInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public Quest getQuest(int arg0) {
        return Quest.getInstance(arg0);
    }

    public void achievement(int a) {
        //c.getPlayer().getMap().broadcastMessage(CField.achievementRatio(a));
    }

    public final MapleInventory getInventory(int type) {
        return c.getPlayer().getInventory(InventoryType.getByType((byte) type));
    }

    public final void prepareAswanMob(int mapid, EventManager eim) {
        MapleMap map = eim.getMapFactory().getMap(mapid);
        if (c.getPlayer().getParty() != null) {
            map.setChangeableMobOrigin(ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getCharacterById(c.getPlayer().getParty().getLeader().getId()));
        } else {
            map.setChangeableMobOrigin(c.getPlayer());
        }
//        map.setChangeableMobUsing(true);
        map.killAllMonsters(false);
        map.OnRespawn(true, System.currentTimeMillis());
    }

    public final void startAswanOffSeason(final User leader) {
        final List<User> check1 = c.getChannelServer().getMapFactory().getMap(955000100).getCharacters();
        final List<User> check2 = c.getChannelServer().getMapFactory().getMap(955000200).getCharacters();
        final List<User> check3 = c.getChannelServer().getMapFactory().getMap(955000300).getCharacters();
        c.getChannelServer().getMapFactory().getMap(955000100).broadcastPacket(CField.getClock(20 * 60));
        c.getChannelServer().getMapFactory().getMap(955000200).broadcastPacket(CField.getClock(20 * 60));
        c.getChannelServer().getMapFactory().getMap(955000300).broadcastPacket(CField.getClock(20 * 60));
        EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (check1 != null && check2 != null && check3 != null && (leader.getMapId() == 955000100 || leader.getMapId() == 955000200 || leader.getMapId() == 955000300)) {
                    for (User chrs : check1) {
                        chrs.changeMap(262010000, 0);
                    }
                    for (User chrs : check2) {
                        chrs.changeMap(262010000, 0);
                    }
                    for (User chrs : check3) {
                        chrs.changeMap(262010000, 0);
                    }
                } else {
                    EventTimer.getInstance().stop();
                }
            }
        }, 20 * 60 * 1000);
    }

    public int randInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public void sendDirectionStatus(int key, int value) {
        c.SendPacket(UIPacket.UserInGameDirectionEvent(key, value));
        c.SendPacket(UIPacket.getDirectionStatus(true));
    }

    public void sendDirectionStatus(int key, int value, boolean direction) {
        c.SendPacket(UIPacket.UserInGameDirectionEvent(key, value));
        c.SendPacket(UIPacket.getDirectionStatus(direction));
    }

    public void sendDirectionInfo(String data) {
        c.SendPacket(UIPacket.UserInGameDirectionEvent(data, 2000, 0, -100, 0, 0));
        c.SendPacket(UIPacket.UserInGameDirectionEvent(1, 2000));
    }

    public void getDirectionInfo(String data, int value, int x, int y, int a, int b) {
        c.SendPacket(CField.UIPacket.UserInGameDirectionEvent(data, value, x, y, a, b));
    }

    public void getDirectionInfo(byte type, int value) {
        c.SendPacket(CField.UIPacket.UserInGameDirectionEvent(type, value));
    }

    public void introEnableUI(int wtf) {
        c.SendPacket(CField.UIPacket.IntroEnableUI(wtf > 0));
    }

    public void sendDirectionFacialExpression(int expression, int duration) {
        c.SendPacket(CField.directionFacialExpression(expression, duration));
    }

    public void introDisableUI(boolean enable) {
        c.SendPacket(CField.UIPacket.IntroEnableUI(enable));
    }

    public void getDirectionStatus(boolean enable) {
        c.SendPacket(CField.UIPacket.getDirectionStatus(enable));
    }

    public void playMovie(String data, boolean show) {
        c.SendPacket(UIPacket.playMovie(data, show));
    }

    public String getCharacterName(int characterid) {
        return c.getChannelServer().getPlayerStorage().getCharacterById(characterid).getName();
    }

    public final MapleExpedition getExpedition() {
        return World.Party.getExped(c.getPlayer().getParty().getId());
    }

    public int getExpeditionMembers(int id) {
        return World.Party.getExped(c.getPlayer().getParty().getId()).getAllMembers();
    }

    public void warpExpedition(int mapid, int portal) {
        for (User chr : World.Party.getExped(c.getPlayer().getParty().getId()).getExpeditionMembers(c)) {
            chr.changeMap(mapid, portal);
        }
    }

    public String getMasteryBooksByJob(String job) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
            if (item.getKey() >= 2280000 && item.getKey() < 2300000) {

                final String skilldesc = item.getValue().getRight();
                if (skilldesc.contains(job)) {
                    sb.append("~").append(item.getKey());
                }
            }
        }
        return sb.toString();
    }

    public String format(String format, Object... toFormat) {
        return String.format(format, toFormat);
    }

    public void addReward(int type, int item, int mp, int meso, int exp, String desc) {
        getPlayer().addReward(type, item, mp, meso, exp, desc);
    }

    public void addReward(long start, long end, int type, int item, int mp, int meso, int exp, String desc) {
        getPlayer().addReward(start, end, type, item, mp, meso, exp, desc);
    }

    public int getPQLog(String pqid) {
        return getPlayer().getPQLog(pqid);
    }

//    public int getGiftLog(String pqid) {
//        return getPlayer().getGiftLog(pqid);
//    }
    //@Override
    public void setPQLog(String pqid) {
        getPlayer().setPQLog(pqid);
    }
}
