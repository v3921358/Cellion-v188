function init() {
	em.setProperty("leader", "true");
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
   em.setProperty("state", "1");
	em.setProperty("leader", "true");
    var eim = em.newInstance("RanmaruNorm" + leaderid);
	
    var map = eim.setInstanceMap(807300110);
    map.resetFully();
	map.killAllMonsters(false);
    var mob0 = em.getMonster(9421581);
	var mob1 = em.getMonster(9421575);
	var mob2 = em.getMonster(9421576);
	eim.registerMonster(mob0);
	eim.registerMonster(mob1);
	eim.registerMonster(mob2);
	map.spawnMonsterOnGroundBelow(mob0, new java.awt.Point(183, 123));
	map.spawnMonsterOnGroundBelow(mob1, new java.awt.Point(-838, -169));
	map.spawnMonsterOnGroundBelow(mob2, new java.awt.Point(-11, -135));

    em.setProperty("state", "1");

    eim.startEventTimer(3600000); // 1 hr
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(807300110);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    player.addHP(50);
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