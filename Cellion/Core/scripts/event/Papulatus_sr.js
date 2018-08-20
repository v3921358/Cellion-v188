function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    var eim = em.newInstance("Papulatus_sr");

    em.setProperty("state", "1");

    eim.startEventTimer(3600000, 220080000); // 1 hr
    eim.setProperty("time", java.lang.System.currentTimeMillis());
    eim.createInstanceMap(220080001);

    return eim;    
}

function playerEntry(eim, player) {
    //var map = em.getMapFactory().getMap(220080001);
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 220080000);

    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    if (mapid != 220080001) {
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

function monsterValue(eim, mobId) {
    if (mobId == 8500002) {
	var time = java.lang.System.currentTimeMillis() - parseInt(eim.getProperty("time"));
	java.lang.System.out.println("time = " + time);
    }
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    if (eim.disposeIfPlayerBelow(100, 220080000)) {
	em.setProperty("state", "0");
    }
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
}

function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}