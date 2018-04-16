/*
 * Quest 21302 - Aran - 
 */

function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("q_21302");

    eim.startEventTimer(1200000);

    var map = eim.getMapFactory().getMap(914022200);
    map.resetMap();
    map.spawnMonsterOnGroundBelow(em.getMonster(9001013), new java.awt.Point(-124,454));

    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(914022100);
    player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 140030000);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    if (mapid != 914022100 && mapid != 914022200) {
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
    eim.disposeIfPlayerBelow(100, 140030000);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(140030000);
    player.changeMap(map, map.getPortal(0));
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 140030000);
}

function allMonstersDead(eim) {
//has nothing to do with monster killing
}

function monsterValue(eim, mobId) {
    return 1;
}

function cancelSchedule() {
}