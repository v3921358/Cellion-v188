function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("Bodyguard" + leaderid);

    em.getMapFactory().getMap(801040100).resetMap();

    eim.startEventTimer(5400000); // 1.5 hrs

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(801040100);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 801040000);
}

function changedMap(eim, player, mapid) {
    if (mapid != 801040100) {
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
    eim.disposeIfPlayerBelow(100, 801040000);

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