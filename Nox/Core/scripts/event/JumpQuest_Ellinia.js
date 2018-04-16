function init() {
    // After loading, ChannelServer
    em.setProperty("canenter", "false");
}

function setup() {
    // Setup the instance when invoked, EG : start PQ
    em.setProperty("canenter", "true");

    var eim = em.newInstance("ellin_jq");

    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("WinOrder", 1);

    var map = eim.createInstanceMapS(101000102, 910000000, "ellin_jq");
    var map2 = eim.createInstanceMapS(101000103, 910000000, "ellin_jq");
    var map3 = eim.createInstanceMapS(101000104, 910000000, "ellin_jq");

    eim.schedule("CloseWarp", 1100000);
    eim.startEventTimer(2200000);

    em.broadcastWorldMsg(6, "[Event] Ellinia jump quest event has started! Please use @ellinia to get into the event at channel 1!");

    return eim;
}

function CloseWarp() {
    em.setProperty("canenter", "false");
}

function playerEntry(eim, player) {
    // Warp player in etc..
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));

    eim.broadcastPlayerMsg(6, player.getName()+"  has joined the quest.");

    player.dropMessage(6, "Go go go!!!");
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    switch (mapid) {
	case 101000102:
	case 101000103:
	case 101000104:
	    return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, mapid)) {
	em.setProperty("canenter", "false");
    }
}

function scheduledTimeout(eim) {
    // When event timeout..

    // restartEventTimer(long time)
    // stopEventTimer()
    // startEventTimer(long time)
    // isTimerStarted()

    eim.broadcastPlayerMsg(6, "The event has ended, please try again some other time.");

    eim.disposeIfPlayerBelow(100, 910000000);
    em.setProperty("canenter", "false");
}

function allMonstersDead(eim) {
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
// Happens when player dies
eim.broadcastPlayerMsg(5, player.getName() + " has died!");
}

function playerRevive(eim, player) {
// Happens when player's revived.
// @Param : returns true/false
}

function playerDisconnected(eim, player) {
    // return 0 - Deregister player normally + Dispose instance if there are zero player left
    // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
    // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
    return 0;
}

function monsterValue(eim, mobid) {
    // Invoked when a monster that's registered has been killed
    // return x amount for this player - "Saved Points"
    return 1;
}

function leftParty(eim, player) {
// Happens when a player left the party
}

function disbandParty(eim, player) {
// Happens when the party is disbanded by the leader.
}

function clearPQ(eim) {
// Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
}

function removePlayer(eim, player) {
// Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function changeMap_Success(eim, player, mapid) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}