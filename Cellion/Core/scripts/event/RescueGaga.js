var eventtime = 180000;

function init() {
    em.setProperty("res0", "0");
    em.setProperty("res1", "0");
    em.setProperty("res2", "0");
    em.setProperty("res3", "0");
    em.setProperty("res4", "0");
}

function setup(eim, leaderid) {
    // Setup the instance when invoked, EG : start PQ
    var instanceName = "resgaga" +  leaderid;

    var eim = em.newInstance(instanceName);
    eim.setProperty("allow_warpback", "false");

    // If there are more than 1 map for this, you'll need to do mapid + instancename
    //926010100

    for (var i = 0; i <= 4; i++) {
	if (em.getProperty("res" + i).equals("0")) {
	    var available = em.getMapFactory().getMap(922240000 + i);
	    	    
	    eim.setPropertyEx("Selected", i);
	    em.setProperty("res" + i, "1");
	    
	    eim.startEventTimer(eventtime);
	    
	    available.resetMap();
	    return eim;
	}
    }
    eim.setPropertyEx("Selected", -1);
    
    eim.cancelOtherSchedule(false);
    eim.disposeIfPlayerBelow(99, 0);

    return null;
}

// eim.getPlayers().get(0).dropMessage(5, eim.getProperty("massacre_hit"));
function playerEntry(eim, player) {
    if (eim.getPropertyEx("Selected") == -1) {
	eim.unregisterPlayer(player);

	eim.disposeIfPlayerBelow(0, 0);
    }
    else {
	var available = em.getMapFactory().getMap(922240000 + eim.getPropertyEx("Selected"));
	if (available == null) {
	    eim.unregisterPlayer(player);

	    eim.cancelOtherSchedule(false);
	    eim.disposeIfPlayerBelow(99, 0);
	} else {
	    player.changeMap(available, available.getPortal(0));
	}
    }
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if (mapid >= 922240000 && mapid <= 922240200) {
	return;
    }
    var selected = eim.getPropertyEx("Selected");
    
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("res" + selected, "0");
    }
// 926010010, 926010070 = tomb
}

function scheduledTimeout(eim) {
    var selected = eim.getPropertyEx("Selected");

    eim.disposeIfPlayerBelow(100, 922240200);

    eim.cancelOtherSchedule(false);
    em.setProperty("res" + selected, "0");
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

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
    }
    return 0;
}

function monsterValue(eim, mobid, chr) {
    // Invoked when a monster that's registered has been killed
    // return x amount for this player - "Saved Points"
    return 0;
}

function leftParty(eim, player) {
    // Happens when a player left the party
    eim.unregisterPlayer(player);

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
    }
}

function disbandParty(eim, player) {
    // Happens when the party is disbanded by the leader.

    var selected = eim.getPropertyEx("Selected");

    eim.disposeIfPlayerBelow(100, 922240200);

    eim.cancelOtherSchedule(false);
    em.setProperty("res" + selected, "0");
}

function clearPQ(eim) {
    // Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
    eim.unregisterPlayer(player);

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
    }
}

function removePlayer(eim, player) {
    // Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
    eim.unregisterPlayer(player);

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
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

function OnHitMiss(eim, chr) {
    // Only here if "eim.setPropertyEx("MissMob", 1);" is specified
}