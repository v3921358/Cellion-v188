
var exitMap = 401060000;
var startMap = 401060200;
var bossMap = 401060200;
var bossId = 8880010;//8900101 and 8900102 as well I think //TODO
var eventName = "ChaosMagnus";
var time = 3600000; //15 mins

function init() {
    cakes = 11;
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function setup(level, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    var eim = em.newInstance(eventName + leaderid);
    em.setProperty("stage", "0");
    var map = eim.setInstanceMap(bossMap);
    map.resetFully(true);
    map.setSpawns(false);
    //map.resetSpawnLevel(level);
    eim.startEventTimer(time);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    player.addHP(500000);
    var map = eim.getMapFactory().getMap(401060200);
    player.changeMap(map, map.getPortal(0));
    return true;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, exitMap);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    //end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 401060000) {
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
    if (mobId == bossId) {//TODO
        eim.broadcastPlayerMsg(5, "Pierre has been killed");
//        end(eim);
    }
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
    eim.disposeIfPlayerBelow(100, exitMap);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
}

function leftParty(eim, player) {
    end(eim);
}
function disbandParty(eim) {
    end(eim);
}
function playerDead(eim, player) {
}
function cancelSchedule() {
}