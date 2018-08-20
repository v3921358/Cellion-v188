var eventtime = 180000;
var eventtime_first = 120000;

function init() {
    em.setProperty("res0", "0");
    em.setProperty("res1", "0");
    em.setProperty("res2", "0");
    em.setProperty("res3", "0");
    em.setProperty("res4", "0");
}

function setup(eim, leaderid) {
    // Setup the instance when invoked, EG : start PQ
    var instanceName = "npHell" +  leaderid;

    var eim = em.newInstance(instanceName);
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("NettPyramidBuff", 1);
    eim.setPropertyEx("massacre_party", 0);
    eim.setPropertyEx("massacre_hit", 0);
    eim.setPropertyEx("massacre_miss", 0);
    eim.setPropertyEx("massacre_cool", 0);
    eim.setPropertyEx("massacre_skill", 0);
    eim.setPropertyEx("massacre_laststage", 0);

    eim.setPropertyEx("MissMob", 1);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    //926010100
    for (var i = 0; i <= 4; i++) {
	if (em.getProperty("res" + i).equals("0")) {
	    var available = em.getMapFactory().getMap(926011100 + i);

	    eim.setPropertyEx("clocks", -100);
	    eim.setPropertyEx("bar", available.getTotalNPCBar());

	    eim.setPropertyEx("Selected", i);
	    em.setProperty("res" + i, "1");

	    eim.startEventTimer(eventtime_first);
	    eim.schedule("DecreaseTask", 1000);
	    available.toggleDrops(true);
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
	var available = em.getMapFactory().getMap(926011100 + eim.getPropertyEx("Selected"));
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
    if ((mapid >= 926010001 && mapid <= 926023500) && mapid != 926010070 && mapid != 926020001) {
	return;
    }
    eim.unregisterPlayer(player);

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	//	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
    }
// 926010010, 926010070 = tomb
}

function monsterSpawn(map, eim) { // Custom function
    var mob = em.getMonster(9700021);

    eim.registerMonster(mob);

    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(7, 88));
}

function scheduledTimeout(eim) {
    var stage = eim.getPropertyEx("massacre_laststage");
    var map = 926011100 + ((stage + 1) * 100);

    if (stage < 4) { // Fighting map, 0,1,2,3
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

	    available.resetMap();
	    available.toggleDrops(true);

	    eim.warpAllPlayer(map);

	    switch (stage) {
		case 0:
		    break;
		case 1:
		    monsterSpawn(available, eim);
		    break;
		case 2:
		    monsterSpawn(available, eim);
		    break;
		case 3:
		    monsterSpawn(available, eim);
		    monsterSpawn(available, eim);
		    break;
	    }
	}
    } else { // to bonus
	em.setProperty("res" + eim.getPropertyEx("Selected"), "0");

	// 926010001
	var available = em.getMapFactory().getMap(926010001 + eim.getPropertyEx("Selected"));

	if (available == null) {
	    eim.unregisterPlayer(player);

	    eim.disposeIfPlayerBelow(99, 0);
	} else {
	    eim.setProperty("massacre_laststage", stage + 1);
	    eim.UpdateAllPlayerQuest(7761, "49");
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

    var selected = eim.getPropertyEx("Selected");

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setProperty("res" + selected, "0");
    }
    return 0;
}

function monsterValue(eim, mobid, chr) {
    if (mobid >= 9700004 && mobid <= 9700029) {
	eim.updateNPQ(5, chr, false); // Hits
	return 1;
    }
    return 0;
}

function DecreaseTask(eim) {
    eim.updateNPQ(6, null, false); //Hits
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

    eim.disposeIfPlayerBelow(100, 926010000);

    eim.cancelOtherSchedule(false);
    em.setProperty("res" + selected, "0");
}

function clearPQ(eim) {
    // Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script

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
    eim.updateNPQ(8, chr, false); // Hits
}
