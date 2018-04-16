function init() {
    // 0 = Not started, 1 = started, 2 = first head defeated, 3 = second head defeated
    em.setProperty("state", "0");
    em.setProperty("preheadCheck", "0");
// 0 = First head not summoned
// 1 = Pending, to summon first head
// 2 = Second head not summoned
// 3 = Pending, to summon second head
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("preheadCheck", "0");

    var eim = em.newInstance("HorntailBattle");
    
    eim.setPropertyEx("RecordPlayerTimeSnapshot", 1);

    var mf = em.getMapFactory();
    mf.getMap(240060001).resetMap();
    mf.getMap(240060101).resetMap();
    mf.getMap(240060201).resetMap();

    eim.startEventTimer(9000000); // 2.5 hrs
    
    return eim;
}

function playerEntry(eim, player) {
    var map;
    var prop = em.getProperty("MainMap")
    if (prop != null && prop.equals("true")) {
	map = eim.getMapFactory().getMap(240060201);
    } else {
	map = eim.getMapFactory().getMap(240060001);
    }
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 240060001:
	case 240060101:
	case 240060201:
	    return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 240050400);
    em.setProperty("state", "0");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function monsterValue(eim, mobId) {    
    switch (mobId) {
	case 8810102:
	case 8810103:
	case 8810104:
	case 8810105:
	case 8810106:
	case 8810107:
	case 8810108:
	case 8810109:
	    var state2 = eim.getPropertyEx("HorntailBodyState") +1;
	    if (state2 == 8) {
		eim.RecordPlayerTimeSnapshot("Chaos_Horntail");
		
		var map = eim.getMapFactory().getMap(240060201);
		map.spawnMonsterOnGroundBelow(em.getMonster(8890000), new java.awt.Point(-37,-260));
		map.spawnMonsterOnGroundBelow(em.getMonster(8890002), new java.awt.Point(205,-260));
	    }
	    eim.setPropertyEx("HorntailBodyState", state2);
	    break;
	case 8810100: // Pre heads
	case 8810101:
	    var state = em.getProperty("state");

	    if (state.equals("1")) {
		em.setProperty("state", "2");
	    } else if (state.equals("2")) {
		em.setProperty("state", "3");
	    }
	    return 1;
	case 8810122: // CHT drops + sponge
	    break;
    }
    return 0;
}

function allMonstersDead(eim) {
}

function playerRevive(eim, player) {
    return false;
}

function clearPQ(eim) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}