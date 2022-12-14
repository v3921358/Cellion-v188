function init() {
    // 0 = Not started, 1 = started, 2 = first head defeated, 3 = second head defeated
	em.setProperty("leader", "true");
	em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("preheadCheck", "0");
	em.setProperty("leader", "true");

    var eim = em.newInstance("MagnusBattle");
	var map = eim.setInstanceMap(401060100);

    eim.setInstanceMap(401060100).resetFully();
	var mob0 = em.getMonster(8880002);
		var modified = em.newMonsterStats();
	modified.setOMp(mob0.getMobMaxMp());
	modified.setOHp(mob0.getMobMaxHp() * 750.0);
//	modified.set0Exp(mob0.getMobExp() * 1.0);
	mob0.setOverrideStats(modified);
	map.spawnMonsterOnGroundBelow(mob0, new java.awt.Point(1442, -1347));

    eim.startEventTimer(1 *1800000); //now changed to 1 hour 15 mins
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(401060100);
    player.changeMap(map, map.getPortal(0));
player.setDeathCount(11);
}

function playerRevive(eim, player) {
    player.addHP(1000);
    var map = eim.getMapFactory().getMap(401060100);
    player.changeMap(map, map.getPortal(0));
    return true;
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 401060100:
	    return;
    }
    eim.unregisterPlayer(player);
player.setDeathCount(0);
    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}


function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(150, 400000000);
    em.setProperty("state", "0");
		em.setProperty("leader", "true");
player.setDeathCount(0);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
player.setDeathCount(0);
    if (eim.disposeIfPlayerBelow(150, 401060100)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
    }
}

function monsterValue(eim, mobId) {
    return 1;
}

function allMonstersDead(eim) {
eim.finishPQ();
eim.broadcastPlayerMsg(6, "Magnus is Dead, enter the Portal to your left, to leave.");
player.setDeathCount(0);
}

function playerRevive(eim, player) {
    return false;
}

function clearPQ(eim) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}