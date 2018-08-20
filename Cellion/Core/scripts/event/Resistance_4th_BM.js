function init() {
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    return 1;
}

function setup() {
    em.setProperty("state", "1");
    
    var eim = em.newInstance("Resistance_4th_BM");

    var map = eim.getMapFactory().getMap(931000300);
    map.respawn(true);
    map.resetReactors();
    eim.startEventTimer(1200000); // 20 minutes

    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(931000300);
    player.changeMap(map, map.getPortal(0));
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 310060220);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 931000300:
	case 931000310:
	case 931000320:
	    return;
    }
    eim.unregisterPlayer(player);

    eim.disposeIfPlayerBelow(0, 0);
    em.setProperty("state", "0");
}

function playerDisconnected(eim, player) {
    return 0;
}

function leftParty(eim, player) {
    playerExit(eim, player);
}

function disbandParty(eim) {
    //boot whole party and end
    eim.disposeIfPlayerBelow(100, 310060220);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(310060220);
    player.changeMap(map, map.getPortal(0));
    em.setProperty("state", "0");
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 310060220);
    em.setProperty("state", "0");
}

function allMonstersDead(eim) {
//has nothing to do with monster killing
}

function cancelSchedule() {
}