function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("HobKing" + leaderid);

    eim.setPropertyEx("Arrive", 0);
    eim.setPropertyEx("Move", 1);
    
    eim.setProperty("allow_warpback", "false");
    eim.setProperty("LockedTime", "0");
    eim.setProperty("LastLateMember", "");
    
    var fact = em.getMapFactory();
    fact.getMap(921120005).resetMap(); // First map
    for (var i = 921120100; i <= 921120500; i+=100) {
	fact.getMap(i).resetMap();
    }
    eim.schedule("beginQuest", 3000);
    eim.startEventTimer(1800000);

    return eim;
}

function beginQuest(eim) { // Custom function
    eim.setPropertyEx("Spawned", 1); // prevent map rush
    
    var fact = em.getMapFactory();
    
    eim.setFieldAverageLevel(921120005);
    var map = fact.getMap(921120005);
    map.respawn(true);
    
    for (var i = 921120100; i <= 921120500; i+=100) {
	eim.setFieldAverageLevel(i);
	
	var map = fact.getMap(i);
	map.respawn(true);
	switch (i) {
	    case 921120100:
		var mob = em.getMonster(9300275);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-487, 154));
		break;
	    case 921120200:
		var mob = em.getMonster(9300275);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-455, 154));
		break;
	    case 921120300:
		var mob = em.getMonster(9300275);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(415, -266));
		break;
	    case 921120400:
		var mob = em.getMonster(9300275);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(71, -746));
		break;
	    case 921120500:
		var mob = em.getMonster(9300275);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-538, 174));
		break;
	}
    }
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(921120005);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 921120000);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 921120005:
	case 921120100:
	case 921120200:
	case 921120300:
	case 921120400:
	case 921120500:
	    return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    if (mobId == 9300275) {
	eim.broadcastPlayerMsg(6, "Failure of protecting Shammos, the event has ended.");
	end(eim);
    }
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 921120000);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function disbandParty (eim) {
    end(eim);
}

function leftParty (eim, player) {
    end(eim);
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function playerDead(eim, player) {}
function cancelSchedule() {}