/*
 * Quest 21739 - Aran - To the seal garden
 */

function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("lionking_1");

    eim.startEventTimer(600000);

    var map = eim.getMapFactory().getMap(921140100);
    map.resetMap();
    map.respawn(true);

    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(921140100);
    player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 211060200);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    if (mapid != 921140100) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable:
    playerExit(eim, player);
}

function disbandParty(eim) {
    eim.disposeIfPlayerBelow(100, 211060200);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(211060200);
    player.changeMap(map, map.getPortal(0));
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 211060200);
}

function allMonstersDead(eim) {
//has nothing to do with monster killing
}

function monsterValue(eim, mobId) {
    return 1;
}

function cancelSchedule() {
}