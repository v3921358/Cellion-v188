/**
	Juliet
*/

function init() {
    em.setProperty("started", "false");
}

function setup() {
    em.setProperty("state", "1");

    var eim = em.newInstance("RomeoJuliet_J");
    eim.setProperty("allow_warpback", "false");

    var mf = em.getMapFactory();
    
    var map = mf.getMap(926110000);
    map.resetMap();
    
    map = mf.getMap(926110001);
    map.resetMap();
    
    map = mf.getMap(926110100);
    map.resetMap();
    
    map = mf.getMap(926110200);
    map.resetMap();
    
    map = mf.getMap(926110201);
    map.resetMap();
    
    map = mf.getMap(926110202);
    map.resetMap();
    
    map = mf.getMap(926110203);
    map.resetMap();
    map.spawnNpc(2112010, new java.awt.Point(200, 188)); //urete MADMAN
    
    for (var i = 926110300; i <= 926110304; i++) {
	map = mf.getMap(i);
	map.resetMap();
    }
    
    for (var i = 926110400; i <= 926110401; i++) {
	map = mf.getMap(i);
	map.resetMap();
    }
    
    map = mf.getMap(926110500);
    map.resetMap();
    
    map = mf.getMap(926110600);
    map.resetMap();
    
/*    map = mf.getMap(920010800);
    map.resetMap();
    map.respawn(true);
    map.shuffleReactors();
    
    map = mf.getMap(920010604);
    map.resetMap();
    
    map = mf.getMap(920010602);
    map.resetMap();
    for (var i = 0; i < 10; i++) {
	map.spawnMonsterOnGroundBelow(em.getMonster(9300042), new java.awt.Point(-36,-1845));
    }*/
    
    eim.startEventTimer(2700000); //45

    return eim;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 926110700);

    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    if (mapid < 926110000 || mapid > 926110600) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
    }
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(926110000);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
}

function playerDisconnected(eim, player) {
    return -3;
}

function leftParty(eim, player) {			
    // If only 2 players are left, uncompletable
    if (eim.disposeIfPlayerBelow(3, 926110700)) {
	em.setProperty("state", "0");
    } else {
	playerExit(eim, player);
    }
}

function disbandParty(eim) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, 926110700);

    em.setProperty("state", "0");
}

function playerExit(eim, player) {
    var map = em.getMapFactory().getMap(926110700);

    eim.unregisterPlayer(player);
    player.changeMap(map, map.getPortal(0));
}

function monsterValue(eim, mobId) {
    if (mobId == 9300040) {
	var set = eim.getPropertyEx("stg_storage_ReactorId");
	if (set > 2001015) {
	    return 1;
	}
	em.forceStartReactor(920010300, set);
	eim.setPropertyEx("stg_storage_ReactorId", set + 1);
    } else if (mobId == 9300049) {
	var map = em.getMapFactory().getMap(920010800);
	map.spawnMonsterOnGroundBelow(em.getMonster(9300039), new java.awt.Point(-49,563));
	eim.broadcastPlayerMsg(6, "Here comes Papa Pixie.");
    }
    return 1;
}

// For offline players
function removePlayer(eim, player) {
    eim.unregisterPlayer(player);
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 926110700);

    em.setProperty("state", "0");
}

function finish(eim) {
    eim.disposeIfPlayerBelow(100, 0);

    em.setProperty("state", "0");
}

function timeOut(eim) {
    eim.disposeIfPlayerBelow(100, 926110700);

    em.setProperty("state", "0");
}

function cancelSchedule() {}
function playerDead() {}
function allMonstersDead(eim) {}