function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("Astaroth" + leaderid);

    var map = em.getMapFactory().getMap(677000012);
    map.resetMap();
    eim.broadcastMapEffect(map, 5120025, "Astaroth : You foolish humans! How dare you come here!");

    var mob = em.getMonster(9400633);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(779, 45));

//	var map = em.getMapFactory().getMap(280030000);
//	var mob = em.getMonster(9300216);
//	map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-10, -215));
//
    eim.startEventTimer(600000); // 10 min

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(677000012);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 677000011);
}

function changedMap(eim, player, mapid) {
    if (mapid != 677000012) {
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
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 677000011);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function disbandParty (eim) {
    end();
}

function leftParty (eim, player) {
    end();
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function playerDead(eim, player) {}
function cancelSchedule() {}