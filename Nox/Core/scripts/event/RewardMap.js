/**
	Reward map
**/

function init() {
}

function setup(charid, charlvl, fromMap) {
    // Setup the instance when invoked, EG : start PQ
    
    var fieldid = -1;
    if (charlvl < 10) {
	fieldid = 553000000;
    } else if (charlvl < 20) { // Mushroom growing cave
	fieldid = 553000500;
    } else if (charlvl < 24) {
	fieldid = 553000100;
    } else if (charlvl < 35) {
	fieldid = 553000200;
    } else if (charlvl < 54) {
	fieldid = 553000300;
    } else if (charlvl < 63) {
	fieldid = 553000400;
    } else if (charlvl < 70) { // Drake's blue cave'
	fieldid = 553000900;
    } else if (charlvl < 80) { // Pillaging the Treasure Island 
	fieldid = 553001000;
    } else if (charlvl < 100) { // Round table of kentarus
	fieldid = 553000800;
    } else { // Revived memory, newtie protected area
	fieldid = Math.floor(Math.random() * 100) < 50 ? 553000700 : 553000600;
    }
    if (fieldid != -1) {	
	for (var i = 0; i < 10; i++) {
	    var prop = em.getPropertyEx(String.valueOf(i));
	
	    if (prop == null || prop == 0) {
		em.setPropertyEx(String.valueOf(i), 1);
		
		var eim = em.newInstance("treasureIsland_" + charid);
		eim.setProperty("allow_warpback", "false");
		eim.setPropertyEx("Selected", fieldid + i);
		eim.setPropertyEx("FromMap", fromMap);
		
		var map = eim.createInstanceMapS(fieldid + i, fromMap, true, false, false, null);
		
		eim.schedule("beginQuest", 3000);
		eim.startEventTimer(60000); // 1 min
		return eim;
	    }
	}
	return eim;
    }
    return null;
}

function beginQuest(eim) { // Custom function
    eim.setFieldAverageLevel(eim.getMapInstance(0));
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));

    player.dropMessage(6, "[Bonus map!] You have 1 minute to get as many reward as you can before being warped out by the force of earth!");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, eim.setPropertyEx("FromMap"));
}

function changedMap(eim, player, mapid) {
    if (mapid / 10000000 == 553) {
	return;
    }
    //    eim.unregisterPlayer(player);
    eim.unregisterPlayer(player);
    eim.dispose();
}

function playerDisconnected(eim, player) {
    eim.unregisterPlayer(player);
    
    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
    }
    return 0;
}

function clearPQ(eim) {
    // Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
    }
}

function clear(eim) {
    eim.dispose();
}

function cancelSchedule() {}

function playerDead() {}

function monsterValue(eim, mobId) {
    return 1;
}

function allMonstersDead(eim) {}

function disbandParty(eim) {}

function OnHitMiss(eim, chr) {
// Only here if "eim.setPropertyEx("MissMob", 1);" is specified
}