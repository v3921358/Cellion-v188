function init() {
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    return 1;
}

function setup() {
    em.setProperty("state", "1");

    var eim = em.newInstance("SheepRanch");

    var mf = em.getMapFactory();
    
    var map = mf.getMap(910040100);
    map.killAllMonsters(false);
    map.respawn(true);

    eim.startEventTimer(60 * 1000 * 5);

    return eim;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 910040000);

    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 910040100:
	case 910040200:
	case 910040300:
	case 910040400:
	    return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(910040100);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
}

function playerDisconnected(eim, player) {
    return -3;
}

function leftParty(eim, player) {			
}

function disbandParty(eim) {
}

function playerExit(eim, player) {
    var map = em.getMapFactory().getMap(910040000);

    eim.unregisterPlayer(player);
    player.changeMap(map, map.getPortal(0));
}

// For offline players
function removePlayer(eim, player) {
    eim.unregisterPlayer(player);
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 910040000);

    em.setProperty("state", "0");
}

function finish(eim) {
    eim.disposeIfPlayerBelow(100, 910040000);

    em.setProperty("state", "0");
}

function timeOut(eim) {
    eim.disposeIfPlayerBelow(100, 910040000);

    em.setProperty("state", "0");
}

function cancelSchedule() {}
function playerDead() {}
function allMonstersDead(eim) {}