function init() {
    em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
   em.setProperty("state", "1");
	em.setProperty("leader", "true");
    var eim = em.newInstance("RanmaruBattle" + leaderid);
    var map = eim.setInstanceMap(807300210);
    map.resetFully();
    var mob0 = em.getMonster(9421583);
	var mob1 = em.getMonster(9421577);
	var mob2 = em.getMonster(9421578);
	var mob3 = em.getMonster(9421579);
	eim.registerMonster(mob0);
	eim.registerMonster(mob1);
	eim.registerMonster(mob2);
	eim.registerMonster(mob3);
	map.spawnMonsterOnGroundBelow(mob0, new java.awt.Point(183, 123));
	map.spawnMonsterOnGroundBelow(mob1, new java.awt.Point(-829, -169));
	map.spawnMonsterOnGroundBelow(mob2, new java.awt.Point(-380, -298));
	map.spawnMonsterOnGroundBelow(mob3, new java.awt.Point(-27, -135));
    eim.startEventTimer(540000); // 1 hr
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(807300210);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    player.addHP(5000);
    var map = eim.getMapFactory().getMap(211000000);
    player.changeMap(map, map.getPortal(0));
    return true;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 211000000);
    em.setProperty("state", "0");
		em.setProperty("leader", "true");
}

function changedMap(eim, player, mapid) {
    if (mapid != 211000000) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
    }
}

function end(eim) {
    if (eim.disposeIfPlayerBelow(100, 211000000)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
    em.setProperty("zakSummoned", "0");
    }
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
eim.broadcastPlayerMsg(5, "Please type @fm to leave this place.");
}

function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}