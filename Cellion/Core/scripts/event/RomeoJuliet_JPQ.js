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
    eim.setPropertyEx("Stage1Pass", 0);
    eim.setPropertyEx("Stage1Letter", 0);
    eim.setPropertyEx("Stage2Pass", 0);
    eim.setPropertyEx("Stage3Pass", 0);
    eim.setPropertyEx("Stage4Pass", 0);
    eim.setPropertyEx("Stage5Pass", 0);
    eim.setPropertyEx("Stage6_Portal1", 0);
    eim.setPropertyEx("Stage6_Portal2", 0);
    eim.setPropertyEx("Stage6_Portal3", 0);
    eim.setPropertyEx("Stage6_Portal4", 0);
    eim.setPropertyEx("StageCenter", 0);
    eim.setPropertyEx("Stage_GiveLetter", 0);

    var mf = em.getMapFactory();
    
    var map = mf.getMap(926110000);
    map.resetMap();
    map.spawnNpc(2112013, new java.awt.Point(316,225), true);
    
    map = mf.getMap(926110001);
    map.resetMap();
    for (var i = 0; i < 20; i++) {
	map.spawnMonsterOnGroundBelow(em.getMonster(9300142), new java.awt.Point(1494,211));
	map.spawnMonsterOnGroundBelow(em.getMonster(9300141), new java.awt.Point(1494,211));
    }
    
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
    map.spawnNpc(2112010, new java.awt.Point(200, 188), true); //urete
    
    for (var i = 926110300; i <= 926110304; i++) {
	map = mf.getMap(i);
	map.resetMap();
    }
    
    for (var i = 926110400; i <= 926110401; i++) {
	map = mf.getMap(i);
	map.resetMap();
	if (i == 926110401) {
	    map.spawnNpc(2112010, new java.awt.Point(248, 150), true); //urete
	}
    }
    
    map = mf.getMap(926110500);
    map.resetMap();
    map.spawnNpc(2112015, new java.awt.Point(369, 150), true); //urete
    
    map = mf.getMap(926110600);
    map.resetMap();
    map.spawnNpc(2112006, new java.awt.Point(287,128), true); //Romeo
    map.spawnNpc(2112005, new java.awt.Point(221,128), true); //Juliet
    
    var y;
    for (y = 0; y < 4; y++) { //stage number
	eim.setProperty("stage6_" + y, "0");
	for (var b = 0; b < 10; b++) {
	    for (var c = 0; c < 4; c++) {
		//em.broadcastYellowMsg("stage6_" + y + "_" + b + "_" + c + " = 0");
		eim.setProperty("stage6_ " + y + "_" + b + "_" + c + "", "0");
	    }
	}
    }
    var i;
    for (y = 0; y < 4; y++) { //stage number
	for (i = 0; i < 10; i++) {		
	    var found = false;
	    while (!found) {
		for (var x = 0; x < 4; x++) {
		    if (!found) {
			var founded = false;
			for (var z = 0; z < 4; z++) { //check if any other stages have this value set already.
			    //em.broadcastYellowMsg("stage6_" + z + "_" + i + "_" + x + " check");
			    if (eim.getProperty("stage6_" + z + "_" + i + "_" + x + "") == null) {
				eim.setProperty("stage6_" + z + "_" + i + "_" + x + "", "0");
			    } else if (eim.getProperty("stage6_" + z + "_" + i + "_" + x + "").equals("1")) {
				founded = true;
				break;
			    }
			}
			if (!founded && Math.random() < 0.25) {
			    //em.broadcastYellowMsg("stage6_" + z + "_" + i + "_" + x + " = 1");
			    eim.setProperty("stage6_" + y + "_" + i + "_" + x + "", "1");
			    found = true;
			    break;
			}
		    }
		}
	    }
	//BUT, stage6_0_0_0 set, then stage6_1_0_0 also not set!
	}
    }
    
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
    if (mobId == 9300137) {
	eim.broadcastPlayerMsg(6, "Juliet has died :(.");
	eim.disposeIfPlayerBelow(100, 926110700);
	em.setProperty("state", "0");
    } else if (mobId == 9300152) {
	var mf = em.getMapFactory();
	var map = mf.getMap(926110401);
	map.spawnNpc(2112006, new java.awt.Point(61,150), true); //Romeo
	map.spawnNpc(2112005, new java.awt.Point(10,150), true); //Juliet
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