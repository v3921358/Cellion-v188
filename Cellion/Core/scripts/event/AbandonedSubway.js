var eventtime = 180000;
var eventtime_first = 120000;

function init() {
    em.setProperty("status", "0");
}

function setup(eim) {
    // Setup the instance when invoked, EG : start PQ
    var instanceName = "AbondonedSubway";

    var eim = em.newInstance(instanceName);
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("massacre_party", 0);
    eim.setPropertyEx("massacre_hit", 0);
    eim.setPropertyEx("massacre_miss", 0);
    eim.setPropertyEx("massacre_cool", 0);
    eim.setPropertyEx("massacre_skill", 0);
    eim.setPropertyEx("massacre_laststage", 0);
    eim.setPropertyEx("NettPyramidBuff", 1);
    eim.setProperty("IsSubway", "true");
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    //926010100

    if (em.getProperty("status").equals("0")) {
	var available = em.getMapFactory().getMap(910330100);

	eim.setPropertyEx("clocks", -100);
	eim.setPropertyEx("bar", available.getTotalNPCBar());

	em.setProperty("status", "1");

	eim.startEventTimer(eventtime_first);
	eim.schedule("DecreaseTask", 1000);
	available.toggleDrops(true);
	return eim;
    }
    eim.cancelOtherSchedule(false);
    eim.disposeIfPlayerBelow(99, 0);

    return eim;
}

// eim.getPlayers().get(0).dropMessage(5, eim.getProperty("massacre_hit"));
function playerEntry(eim, player) {
    var available = em.getMapFactory().getMap(910330100);

    available.resetMap();
    player.changeMap(available, available.getPortal(0));
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if ((mapid >= 910330100 && mapid <= 910330300) || mapid == 910330001) {
	return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	//	eim.cancelOtherSchedule(false);
	em.setProperty("status", "0");
    }
}

function scheduledTimeout(eim) {
    var stage = eim.getPropertyEx("massacre_laststage");
    var map = 910330100 + ((stage + 1) * 100);

    if (stage < 2) { // Fighting map, 0,1,2,3
	var available = em.getMapFactory().getMap(map + eim.getPropertyEx("Selected"));

	if (available == null) {
	    eim.unregisterPlayer(player);

	    eim.cancelOtherSchedule(false);
	    eim.disposeIfPlayerBelow(99, 0);
	} else {
	    eim.startEventTimer(eventtime);

	    eim.setPropertyEx("massacre_laststage", stage + 1);
	    eim.setPropertyEx("clocks", -100);
	    eim.setPropertyEx("bar", available.getTotalNPCBar());

	    available.toggleDrops(true);
	    available.resetMap();

	    eim.warpAllPlayer(map);
	}
    } else { // to bonus
	em.setProperty("status", "0");

	var available = em.getMapFactory().getMap(910330001);

	if (available == null) {
	    eim.disposeIfPlayerBelow(99, 0);
	} else {
	    eim.setProperty("massacre_laststage", stage + 1);
	    eim.UpdateAllPlayerQuest(7762, "49");
	    eim.stopEventTimer();
	    eim.warpAllPlayer(available.getId());

	    eim.cancelOtherSchedule(false);
	    eim.disposeIfPlayerBelow(99, 0);
	}
    }

// restartEventTimer(long time)
// stopEventTimer()
// startEventTimer(long time)
// isTimerStarted()
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
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("status", "0");
    }
    return 0;
}

function monsterValue(eim, mobid, chr) {
    // Invoked when a monster that's registered has been killed
    // return x amount for this player - "Saved Points"
    eim.updateNPQ(5, chr, true); // Hits
    return 0;
}

function DecreaseTask(eim) {
    eim.updateNPQ(6, null, true); // Hits
}

function leftParty(eim, player) {
    // Happens when a player left the party
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("status", "0");
    }
}

function disbandParty(eim, player) {
    // Happens when the party is disbanded by the leader.

    eim.disposeIfPlayerBelow(100, 910320000);

    eim.cancelOtherSchedule(false);
    em.setProperty("status", "0");
}

function clearPQ(eim) {
    // Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("status", "0");
    }
}

function removePlayer(eim, player) {
    // Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("status", "0");
    }
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function changeMap_Success(eim, player, mapid) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}
