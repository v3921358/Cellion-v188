function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("english_e");
    
    eim.setProperty("allow_warpback", "false");
    eim.setProperty("Pass", "false");
    eim.setPropertyEx("GivePass", 4);

    eim.startEventTimer(300000);

    var map = eim.getMapFactory().getMap(702090301);
    map.resetMap();
    
    eim.schedule("beginQuest", 2000);

    return eim;
}

function beginQuest(eim) { // Custom function
    eim.broadcastMapEffect(eim.getMapFactory().getMap(702090301), 2)
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(702090301);
    player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 702090400);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 702090301:
	case 702090302:
	case 702090303:
	    return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
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
    eim.disposeIfPlayerBelow(100, 702090400);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(702090400);
    player.changeMap(map, map.getPortal(0));
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 702090400);
}

function allMonstersDead(eim) {
//has nothing to do with monster killing
}

function monsterValue(eim, mobId) {
    return 1;
}

function cancelSchedule() {
}