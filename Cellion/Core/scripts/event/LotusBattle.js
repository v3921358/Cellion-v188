function init() {
    // 0 = Not started, 1 = started, 2 = first head defeated, 3 = second head defeated
	em.setProperty("leader", "true");
	em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("preheadCheck", "0");
	em.setProperty("leader", "true");

    var eim = em.newInstance("LotusBattle");
var map1 = eim.setInstanceMap(350060600);
		var map = eim.setInstanceMap(350060400);
    map.resetFully();
map1.resetFully();
	var mob0 = em.getMonster(8240103);
		var modified = em.newMonsterStats();
	modified.setOMp(mob0.getMobMaxMp());
	modified.setOHp(mob0.getMobMaxHp() * 1123200.0);
//	modified.set0Exp(mob0.getMobExp() * 1123200.0);
	mob0.setOverrideStats(modified);

	// var mob1 = em.getMonster(8240105);
	// var modified = em.newMonsterStats();
	 //modified.setOMp(mob1.getMobMaxMp());
	 //modified.setOHp(mob1.getMobMaxHp() * 12373333.0);
//	modified.set0Exp(mob1.getMobExp() * 12373333.0);
	 //mob1.setOverrideStats(modified);

	map.spawnMonsterOnGroundBelow(mob0, new java.awt.Point(12, -16));
 //map1.spawnMonsterOnGroundBelow(mob1, new java.awt.Point(469, -16));
    eim.startEventTimer(2* 1800000); //now changed to 1 hour 15 mins
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(350060400);
    player.changeMap(map, map.getPortal(0));
player.setDeathCount(16);
}

function playerRevive(eim, player) {
    player.addHP(1000);
eim.dispose();
    var map = eim.getMapFactory().getMap(350060400);
    player.changeMap(map, map.getPortal(0));
    return true;
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 350060400:
        case 350060500:
        case 350060600:
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

function monsterValue(eim, mobId) {	
		return 1;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 350060400);
    em.setProperty("state", "0");
		em.setProperty("leader", "true");
player.setDeathCount(0);
}

function end(eim) {
    if (eim.disposeIfPlayerBelow(100, 350060400)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
player.setDeathCount(0);
   // eim.broadcastPlayerMsg(5, "Enter the Portal to your left, to leave.");
    }
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
eim.changeMusic("Bgm13/CokeTown");
}

function playerRevive(eim, player) {
    return false;
}

function clearPQ(eim) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}