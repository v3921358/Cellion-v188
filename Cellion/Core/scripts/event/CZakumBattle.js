function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("CZakumBattle" + leaderid);
    
    eim.setPropertyEx("RecordPlayerTimeSnapshot", 1);

    em.getMapFactory().getMap(280030001).resetMap();

    eim.startEventTimer(5400000); // 1.5 hrs

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(280030001);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 211042300);
}

function changedMap(eim, player, mapid) {
    if (mapid != 280030001) {
	eim.unregisterPlayer(player);
	eim.RemoveWarpbackList(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function scheduledTimeout(eim) {
    end(eim);
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    if (mobId == 8800102) { // Main third body
	if (eim.getPropertyEx("ZakumDead") == 0) {
	    eim.setPropertyEx("ZakumDead",1);
	    
	    eim.schedule("ZakumDead", 5000);
	}	
	eim.RecordPlayerTimeSnapshot("Chaos_Zakum");
    }
    return 1;
}

function ZakumDead(eim) {
    var map = eim.getMapFactory().getMap(280030001);
    map.spawnMonsterOnGroundBelow(em.getMonster(8890000), new java.awt.Point(-90,-210));
    map.spawnMonsterOnGroundBelow(em.getMonster(8890002), new java.awt.Point(90,-210));
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 211042300);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}