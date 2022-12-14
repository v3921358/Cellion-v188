function init() {
    // 0 = Not started, 1 = started, 2 = first head defeated, 3 = second head defeated
	em.setProperty("leader", "true");
	em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("preheadCheck", "0");
	em.setProperty("leader", "true");

    var eim = em.newInstance("GTemple");
		var map = eim.setInstanceMap(809060100);
    map.resetFully();
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(809060100);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    player.addHP(1000);
eim.dispose();
    var map = eim.getMapFactory().getMap(809060100);
    player.changeMap(map, map.getPortal(0));
    return true;
}

//function revivePlayer(eim, player) {
//     player.addHP(1000);
//	 eim.dispose();
//	 }

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 809060100:
	    return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {	
   //  var mobId == 8930000;
		return 0;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 809060100);
    em.setProperty("state", "0");
		em.setProperty("leader", "true");
}

function end(eim) {
    if (eim.disposeIfPlayerBelow(100, 809060100)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
   // eim.broadcastPlayerMsg(5, "Enter the Portal to your left, to leave.");
    }
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
em.broadcastServerMsg(6, "The SAO Event has not spawned!", false);
}

//function playerRevive(eim, player) {
 //   return false;
//}

function clearPQ(eim) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}