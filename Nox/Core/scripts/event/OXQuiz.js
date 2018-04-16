function init() {
    // After loading, ChannelServer
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");

    var eim = em.newInstance("OXQuizdsdsdsd");
    eim.setPropertyEx("EventState", 0);

    //  var map = eim.getMapFactory().getMap(109020001);

    eim.startEventTimer(180000);

    em.broadcastWorldMsg(6, "[Event] OXQuiz event has been scheduled to start in 3 minutes, please use @oxquiz command to enter at Channel 1.");

    return eim;
}

function playerEntry(eim, player) {
    // Warp player in etc..
    var map = eim.getMapFactory().getMap(109020001);
    player.changeMap(map, map.getPortal(0));

    eim.broadcast_OXQuiz_JobString(player, 0);
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if (mapid != 109020001) {
	eim.unregisterPlayer(player);
	eim.RemoveWarpbackList(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    eim.cancelOtherSchedule(false);
	}
    }
}

function scheduledTimeout(eim) {
    // When event timeout..

    // restartEventTimer(long time)
    // stopEventTimer()
    // startEventTimer(long time)
    // isTimerStarted()
    var state = eim.getPropertyEx("EventState");
    if (state == 0) {
	em.setPropertyEx("state", "0"); // Close up
	eim.setPropertyEx("EventState", 1);
	eim.broadcastPlayerMsg(5, "[OXQuiz] Get ready for the questions! Head to your Right if you think it's wrong, and the left if it is correct.");
	
	eim.startEventTimer(10000);
    } else if (state < 12) {
	eim.setPropertyEx("EventState", ++state);
    } else { // End
}
}

function allMonstersDead(eim) {
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
    // Happens when player dies
    eim.broadcast_OXQuiz_JobString(player, 1);
}

function playerRevive(eim, player) {
    // Happens when player's revived.
    // @Param : returns true/false
    return true;
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