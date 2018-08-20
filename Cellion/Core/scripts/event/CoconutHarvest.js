function init() {
    // After loading, ChannelServer
    em.setProperty("canenter", "false");
}

function setup() {
    // Setup the instance when invoked, EG : start PQ
    em.setProperty("canenter", "true");

    var eim = em.newInstance("harvest");

    eim.setPropertyEx("redScore", 0);
    eim.setPropertyEx("blueScore", 0);
    eim.setPropertyEx("reduser", 0);
    eim.setPropertyEx("blueuser", 0);
    eim.setPropertyEx("AutoAssignRedBlueTeam", 1); // Property for team handler
    eim.setProperty("allow_warpback", "false");

    eim.schedule("StartCoconut", 300000);//300000); // 5 min
    eim.startEventTimer(1200000); // 20 min

    em.broadcastWorldMsg(6, "[Event] Coconut harvest event has been scheduled to start in 5 minutes, please use @coconut command to enter at Channel 1.");

    return eim;
}

function StartCoconut(eim) {
    em.broadcastWorldMsg(6, "[Event] Coconut Harvest event has started, entry will not be possible now!!!");

    eim.CoconutHarvest_Start();
    eim.broadcastPlayerMsg(6, "The coconut has grown rapidly! Who will be the winner of the Coconut Harvest event? Red or Blue!")
}

function playerEntry(eim, player) {
    // Warp player in etc..
    var map = em.getMapFactory().getMap(109080000);
    player.changeMap(map, map.getPortal(0));

    player.dropMessage(6, "The coconut harvest will start in 5 minutes [15.00] on the timer.");
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if (mapid != 109080000 && mapid != 109080001 && mapid != 109080002) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, mapid)) {
	    em.setProperty("canenter", "false");
	}
    }
}

function scheduledTimeout(eim) {
    // When event timeout..

    // restartEventTimer(long time)
    // stopEventTimer()
    // startEventTimer(long time)
    // isTimerStarted()

    var red = parseInt(eim.getPropertyEx("redScore"));
    var blue = parseInt(eim.getPropertyEx("blueScore"));

    if (blue > red) {
	em.warpAllPlayer_Team(109080000, 109050000, 109050001, 0);
    } else {
	em.warpAllPlayer_Team(109080000, 109050000, 109050001, 1);
    }

    eim.disposeIfPlayerBelow(100, 109050001);
    em.setProperty("canenter", "false");
}

function allMonstersDead(eim) {
    // When invoking unregisterMonster(MapleMonster mob) OR killed
    // Happens only when size = 0
}

function playerDead(eim, player) {
    // Happens when player dies
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